#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import os, re, sys, subprocess
from pathlib import Path

BRANCH_NAME   = "feature/cutting-parts-workflow"
BACKUP_BRANCH = "backup-before-cutting-parts"
COMMIT_MSG    = "feat(cutting): part-based workflow with status transitions"

BASE_PATH     = Path("app/src/main/java/com/example")
ENTITIES_FILE = BASE_PATH / "data/model/Entities.kt"
REPO_FILE     = BASE_PATH / "data/repository/ManufacturingRepository.kt"
VM_FILE       = BASE_PATH / "viewmodel/ManufacturingViewModel.kt"
DB_FILE       = BASE_PATH / "data/database/AppDatabase.kt"

G, Y, R, B, C, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[96m", "\033[0m"
ok   = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")
step = lambda m: print(f"\n{C}━━━ {m} ━━━{RST}")

def run(cmd, check=False):
    r = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    if check and r.returncode != 0:
        err(f"دستور شکست خورد: {cmd}")
        if r.stdout: print(r.stdout[:2000])
        if r.stderr: print(r.stderr[:2000])
        sys.exit(1)
    return r

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

def find_app_dao_file():
    for p in BASE_PATH.rglob("AppDao.kt"):
        return p
    for p in BASE_PATH.rglob("*.kt"):
        try:
            if "interface CuttingDao" in read(p): return p
        except Exception: pass
    return None

def find_interface_bounds(content, name):
    m = re.search(r'interface\s+' + re.escape(name) + r'\s*\{', content)
    if not m: return None
    open_idx = m.end() - 1
    depth = 0; i = open_idx
    while i < len(content):
        if content[i] == '{': depth += 1
        elif content[i] == '}':
            depth -= 1
            if depth == 0: return (m.start(), open_idx, i)
        i += 1
    return None

def check_environment():
    step("۱. بررسی محیط پروژه")
    if not (Path("settings.gradle.kts").exists() or Path("settings.gradle").exists()):
        err("settings.gradle پیدا نشد"); sys.exit(1)
    ok("settings.gradle موجود است")
    if not Path("app").is_dir():
        err("پوشه app/ پیدا نشد"); sys.exit(1)
    ok("پوشه app/ موجود است")
    for name, p in {"Entities": ENTITIES_FILE, "Repository": REPO_FILE,
                    "ViewModel": VM_FILE, "AppDatabase": DB_FILE}.items():
        if not p.exists():
            err(f"فایل پیدا نشد: {p}"); sys.exit(1)
        ok(f"{name}: ✓")
    if run("git rev-parse --is-inside-work-tree").returncode != 0:
        err("مخزن Git نیست"); sys.exit(1)
    ok("مخزن Git معتبر")
    gradlew = Path("gradlew") if os.name != "nt" else Path("gradlew.bat")
    if gradlew.exists(): ok("gradlew موجود")
    else:
        warn("gradlew نیست → کامپایل محلی رد می‌شود")

def check_structure():
    step("۲. بررسی ساختار کد")
    errors = []
    if "data class CuttingEntity(" not in read(ENTITIES_FILE): errors.append("CuttingEntity نیست")
    else: ok("CuttingEntity موجود")
    if not read(REPO_FILE).rstrip().endswith("}"): errors.append("Repository با } تمام نمی‌شود")
    else: ok("Repository استاندارد")
    if "completedCuttings" not in read(VM_FILE): errors.append("completedCuttings نیست")
    else: ok("completedCuttings موجود")
    db = read(DB_FILE)
    if "version = 9," not in db and "version = 10," not in db: errors.append("نسخه DB نامعتبر")
    else: ok("نسخه DB معتبر")
    dao = find_app_dao_file()
    if not dao: errors.append("فایل DAO پیدا نشد")
    else:
        ok(f"فایل DAO: {dao.name}")
        dc = read(dao)
        if "interface CuttingDao" in dc: ok("interface CuttingDao موجود")
        else: errors.append("interface CuttingDao نیست")
        if "fun getCuttingById" in dc: ok("getCuttingById موجود")
        else: errors.append("getCuttingById نیست")
    if errors:
        print(); err("ایرادات:")
        for e in errors: print(f"   • {e}")
        sys.exit(1)
    return dao

def create_backup():
    step("۳. بکاپ")
    current_branch = run("git branch --show-current", check=True).stdout.strip()
    info(f"برنچ فعلی: {current_branch}")
    status = run("git status --porcelain --untracked-files=no").stdout.strip()
    if status:
        warn("تغییرات uncommitted:")
        print(status)
        err("اول git stash یا commit کن")
        sys.exit(1)
    ok("Working tree تمیز")
    if run(f"git rev-parse --verify {BACKUP_BRANCH}").returncode == 0:
        run(f"git branch -D {BACKUP_BRANCH}")
    run(f"git branch {BACKUP_BRANCH}", check=True)
    ok(f"بکاپ: {BACKUP_BRANCH}")
    if run(f"git rev-parse --verify {BRANCH_NAME}").returncode == 0:
        run(f"git checkout {BRANCH_NAME}", check=True)
    else:
        run(f"git checkout -b {BRANCH_NAME}", check=True)
    ok(f"روی برنچ: {BRANCH_NAME}")
    return current_branch

def apply_entities():
    c = read(ENTITIES_FILE)
    if "const val STATUS_CUT" in c: warn("companion object قبلاً هست"); return
    m = re.search(r'data class CuttingEntity\([\s\S]*?\n\)\s*\{', c)
    if not m: raise RuntimeError("data class CuttingEntity پیدا نشد")
    open_idx = c.find('{', m.end() - 50)
    if open_idx < 0: raise RuntimeError("آکولاد باز CuttingEntity پیدا نشد")
    depth = 0; close_idx = -1; i = open_idx
    while i < len(c):
        if c[i] == '{': depth += 1
        elif c[i] == '}':
            depth -= 1
            if depth == 0: close_idx = i; break
        i += 1
    if close_idx < 0: raise RuntimeError("} پایانی CuttingEntity پیدا نشد")
    companion = '''

  companion object {
    const val STATUS_CUT = "برش خورده"
    const val STATUS_SEWING = "در حال دوخت"
    const val STATUS_READY = "کار آماده"
    const val STATUS_WAREHOUSE = "تحویل انبار"
    const val STATUS_DELIVERED = "تحویل شده"
    const val WORK_TYPE_STOCK = "تولید برای انبار"
    const val WORK_TYPE_CUSTOMER = "سفارش مشتری"
  }'''
    write(ENTITIES_FILE, c[:close_idx] + companion + "\n" + c[close_idx:])
    ok("companion object اضافه شد")

def apply_dao(dao_path):
    c = read(dao_path)
    if "getCuttingPartsByRoll" in c: warn("کوئری DAO قبلاً هست"); return
    bounds = find_interface_bounds(c, "CuttingDao")
    if not bounds: raise RuntimeError("interface CuttingDao پیدا نشد")
    start, open_idx, close_idx = bounds
    info(f"interface CuttingDao در خط {c[:start].count(chr(10)) + 1} پیدا شد")
    block = """  // ===== Cutting Parts Workflow Queries =====

  @Query("SELECT * FROM cutting_orders WHERE rollId = :rollId ORDER BY partNumber ASC")
  fun getCuttingPartsByRoll(rollId: Long): Flow<List<CuttingEntity>>

  @Query("SELECT * FROM cutting_orders WHERE status = :status ORDER BY timestamp DESC")
  fun getCuttingPartsByStatus(status: String): Flow<List<CuttingEntity>>

  @Query("SELECT COALESCE(MAX(partNumber), 0) FROM cutting_orders WHERE rollId = :rollId")
  suspend fun getMaxPartNumber(rollId: Long): Int

  @Query("SELECT * FROM cutting_orders WHERE status IN ('برش خورده', 'در حال دوخت', 'کار آماده') ORDER BY timestamp DESC")
  fun getActiveParts(): Flow<List<CuttingEntity>>
"""
    write(dao_path, c[:close_idx] + block + c[close_idx:])
    ok(f"کوئری‌های DAO به {dao_path.name} اضافه شد")

def apply_repository():
    c = read(REPO_FILE)
    if "fun cuttingPartsByRoll" in c: warn("متدهای Repository قبلاً هست"); return
    idx = c.rstrip().rfind("}")
    if idx < 0: raise RuntimeError("} پایانی نیست")
    block = '''
  // ==========================================
  // CUTTING PARTS WORKFLOW
  // ==========================================

  fun cuttingPartsByRoll(rollId: Long): Flow<List<CuttingEntity>> =
    database.cuttingDao().getCuttingPartsByRoll(rollId)

  fun cuttingPartsByStatus(status: String): Flow<List<CuttingEntity>> =
    database.cuttingDao().getCuttingPartsByStatus(status)

  fun activeCuttingParts(): Flow<List<CuttingEntity>> =
    database.cuttingDao().getActiveParts()

  suspend fun getNextPartNumber(rollId: Long): Int =
    database.cuttingDao().getMaxPartNumber(rollId) + 1

  suspend fun updateCuttingPartStatus(
    partId: Long, newStatus: String, note: String = ""
  ): Pair<Boolean, String> = database.withTransaction {
    val part = database.cuttingDao().getCuttingById(partId)
      ?: return@withTransaction Pair(false, "پارت با شناسه $partId یافت نشد")
    if (part.status == newStatus) return@withTransaction Pair(true, "وضعیت تغییری نکرد")
    val updatedNote = if (note.isNotBlank()) {
      if (part.notes.isBlank()) note else "${part.notes} | $note"
    } else part.notes
    database.cuttingDao().updateCutting(part.copy(status = newStatus, notes = updatedNote))
    if (newStatus == CuttingEntity.STATUS_READY && !part.isStockAdded) {
      addPartToWarehouseInternal(part)
    }
    if (part.orderId != null && part.orderId > 0L) {
      val newOrderStatus = when (newStatus) {
        CuttingEntity.STATUS_SEWING -> SaleOrderStatus.IN_SEWING
        CuttingEntity.STATUS_READY -> SaleOrderStatus.READY_FOR_SHIPPING
        else -> null
      }
      if (newOrderStatus != null) {
        try {
          database.saleOrderDao().getOrderById(part.orderId)?.let { order ->
            database.saleOrderDao().updateOrder(order.copy(deliveryStatus = newOrderStatus))
            database.orderStatusHistoryDao().insertHistory(
              OrderStatusHistoryEntity(
                orderId = order.id, orderNumber = order.orderNumber,
                oldStatus = order.deliveryStatus, newStatus = newOrderStatus,
                date = PersianDateHelper.getCurrentPersianDate(),
                time = PersianDateHelper.getCurrentTime(),
                timestamp = System.currentTimeMillis(),
                note = "به‌روزرسانی خودکار از پارت"
              )
            )
          }
        } catch (_: Exception) {}
      }
    }
    Pair(true, "وضعیت پارت به «$newStatus» تغییر یافت")
  }

  private suspend fun addPartToWarehouseInternal(part: CuttingEntity) {
    val prodCode = if (part.productCode.isNotBlank()) part.productCode else "PRD-${part.id}"
    val prodName = if (part.productName.isNotBlank()) part.productName else part.partTitle
    val count = part.cutQuantity
    val unitCost = part.unitCost
    val todayDate = PersianDateHelper.getTodayPersianDate()
    val existing = database.inventoryDao().getByCode(prodCode)
    if (existing != null) {
      database.inventoryDao().updateItem(
        existing.copy(
          readyForShipment = existing.readyForShipment + count,
          availableForSale = existing.availableForSale + count,
          unitCostPrice = if (unitCost > 0L) unitCost else existing.unitCostPrice,
          lastUpdated = todayDate
        )
      )
    } else {
      database.inventoryDao().insertItem(
        InventoryEntity(
          name = prodName, code = prodCode, category = "محصولات آماده",
          readyForShipment = count, reservedQuantity = 0, availableForSale = count,
          unitCostPrice = unitCost, unitSalePrice = part.unitSellingPrice,
          unitWeightGrams = part.actualWeightKgPerItem * 1000.0,
          totalWeightKg = part.weightKgUsed, unitType = "عدد", lastUpdated = todayDate
        )
      )
    }
    try {
      database.inventoryLedgerDao().insert(
        InventoryLedgerEntity(
          timestamp = System.currentTimeMillis(), date = todayDate,
          itemType = "FINISHED_GOOD", itemId = part.id,
          itemCode = prodCode, itemName = prodName,
          color = part.color, size = part.size,
          transactionType = "CUTTING_PART_READY",
          quantityChange = count.toDouble(),
          balanceAfter = (existing?.readyForShipment ?: 0) + count.toDouble(),
          unit = "عدد", unitPriceAtTime = unitCost,
          relatedDocumentNumber = "PART-${part.id}",
          notes = "کار آماده از پارت", operator = "مدیر کارگاه"
        )
      )
    } catch (_: Exception) {}
    database.cuttingDao().updateCutting(part.copy(isStockAdded = true))
  }

  suspend fun getBOMAsConsumableInputs(
    productId: Long, quantity: Int
  ): List<ProductionConsumableInputItem> {
    val boms = database.productBOMDao().getBOMListForProduct(productId)
    return boms.mapNotNull { bom ->
      val m = database.materialDao().getById(bom.materialId) ?: return@mapNotNull null
      ProductionConsumableInputItem(
        accessoryCode = m.code, accessoryName = m.name,
        quantityUsed = bom.standardQuantity * quantity,
        unit = m.unit.ifBlank { bom.unit },
        unitCostPrice = m.currentPrice
      )
    }
  }
'''
    write(REPO_FILE, c[:idx] + block + c[idx:])
    ok("متدهای Repository اضافه شد")

def apply_viewmodel():
    c = read(VM_FILE)
    if "cutButNotSewnParts" in c: warn("StateFlowها قبلاً هست"); return
    marker = """  val completedCuttings: StateFlow<List<CuttingEntity>> = repository.completedCuttings
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())"""
    if marker not in c: raise RuntimeError("الگوی completedCuttings پیدا نشد")
    block = marker + '''

  val cutButNotSewnParts: StateFlow<List<CuttingEntity>> =
    repository.cuttingPartsByStatus(CuttingEntity.STATUS_CUT)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val sewingParts: StateFlow<List<CuttingEntity>> =
    repository.cuttingPartsByStatus(CuttingEntity.STATUS_SEWING)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val readyParts: StateFlow<List<CuttingEntity>> =
    repository.cuttingPartsByStatus(CuttingEntity.STATUS_READY)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeParts: StateFlow<List<CuttingEntity>> =
    repository.activeCuttingParts()
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun getCuttingPartsForRoll(rollId: Long): StateFlow<List<CuttingEntity>> =
    repository.cuttingPartsByRoll(rollId)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun updateCuttingPartStatus(partId: Long, newStatus: String, note: String = "") {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.updateCuttingPartStatus(partId, newStatus, note)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.localizedMessage}", true)
      }
    }
  }

  fun buildAutoPartTitle(partNumber: Int): String = when (partNumber) {
    1 -> "برش و تولید اول"
    2 -> "برش و تولید دوم"
    3 -> "برش و تولید سوم"
    4 -> "برش و تولید چهارم"
    5 -> "برش و تولید پنجم"
    else -> "برش و تولید شماره $partNumber"
  }

  fun transferAllReadyParts() {
    viewModelScope.launch {
      try {
        val ready = readyParts.value
        if (ready.isEmpty()) {
          _notification.value = UiNotification("پارت آماده‌ای نیست", true)
          return@launch
        }
        var okCount = 0
        ready.forEach { part ->
          val (ok, _) = repository.updateCuttingPartStatus(part.id, CuttingEntity.STATUS_READY, "انتقال گروهی")
          if (ok) okCount++
        }
        _notification.value = UiNotification("$okCount پارت منتقل شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.localizedMessage}", true)
      }
    }
  }'''
    write(VM_FILE, c.replace(marker, block, 1))
    ok("StateFlowها اضافه شد")

def apply_database():
    c = read(DB_FILE)
    if "version = 10," in c: warn("نسخه قبلاً 10")
    elif "version = 9," in c:
        c = c.replace("version = 9,", "version = 10,", 1)
        write(DB_FILE, c); ok("نسخه: 9 → 10")
    else: raise RuntimeError("version = 9, پیدا نشد")
    c = read(DB_FILE)
    if "MIGRATION_9_10" not in c:
        mig = '''    val MIGRATION_9_10 = object : Migration(9, 10) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("UPDATE cutting_orders SET status = 'برش خورده' WHERE status IN ('برش‌خورده', 'برش‌خورده - آماده دوخت', 'در حال برش')")
        db.execSQL("UPDATE cutting_orders SET status = 'کار آماده' WHERE status IN ('تکمیل شده', 'آماده دوخت')")
        db.execSQL("UPDATE cutting_orders SET partNumber = 1 WHERE partNumber = 0")
      }
    }

'''
        marker = "    fun getDatabase("
        if marker not in c: raise RuntimeError("fun getDatabase پیدا نشد")
        c = c.replace(marker, mig + marker, 1)
        write(DB_FILE, c); ok("MIGRATION_9_10 اضافه شد")
    c = read(DB_FILE)
    old = ".addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)"
    new = ".addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)"
    if "MIGRATION_8_9, MIGRATION_9_10" in c: warn("addMigrations قبلاً به‌روز")
    elif old in c:
        write(DB_FILE, c.replace(old, new, 1)); ok("addMigrations به‌روز شد")
    else: raise RuntimeError("خط addMigrations پیدا نشد")

def validate_syntax():
    step("۵. اعتبارسنجی")
    all_ok = True
    for path, marker, label in [
        (ENTITIES_FILE, "const val STATUS_CUT", "Entity"),
        (REPO_FILE, "fun cuttingPartsByRoll", "Repository"),
        (VM_FILE, "cutButNotSewnParts", "ViewModel"),
        (DB_FILE, "MIGRATION_9_10", "Migration")]:
        if path.exists() and marker in read(path): ok(f"{label}: ✓")
        else: err(f"{label}: ✗"); all_ok = False
    dao = find_app_dao_file()
    if dao and "getCuttingPartsByRoll" in read(dao): ok("DAO: ✓")
    else: err("DAO: ✗"); all_ok = False
    for path, label in [(ENTITIES_FILE, "Entity"), (REPO_FILE, "Repository"),
                        (VM_FILE, "ViewModel"), (DB_FILE, "Database")]:
        c = read(path)
        if c.count("{") != c.count("}"):
            err(f"آکولاد {label}: {{={c.count('{')} }}={c.count('}')}")
            all_ok = False
        else: ok(f"آکولاد {label} متوازن")
    if dao:
        dc = read(dao)
        if dc.count("{") != dc.count("}"):
            err(f"آکولاد {dao.name} نامتوازن"); all_ok = False
        else: ok(f"آکولاد {dao.name} متوازن")
    return all_ok

def rollback(current_branch):
    step("رول‌بک")
    run("git checkout -- .")
    run(f"git checkout {current_branch}")
    warn("تغییرات برگردانده شدند")

def commit():
    step("۶. کامیت")
    if not run("git status --porcelain --untracked-files=no").stdout.strip():
        warn("چیزی برای کامیت نیست"); return False
    run("git add -A", check=True)
    r = run(f'git commit -m "{COMMIT_MSG}"')
    if r.returncode == 0:
        ok("کامیت شد")
        print(run("git show --stat HEAD").stdout)
        return True
    warn("کامیت نشد"); return False

def main():
    print()
    print("=" * 70)
    print(f"{C}🛡  اعمال ایمن تغییرات پارت‌های برش (v3){RST}")
    print("=" * 70)
    check_environment()
    dao_path = check_structure()
    current_branch = create_backup()
    step("۴. اعمال تغییرات")
    try:
        apply_entities()
        apply_dao(dao_path)
        apply_repository()
        apply_viewmodel()
        apply_database()
    except Exception as e:
        err(f"خطا: {e}")
        import traceback; traceback.print_exc()
        rollback(current_branch); sys.exit(1)
    if not validate_syntax():
        err("اعتبارسنجی شکست خورد")
        rollback(current_branch); sys.exit(1)
    if not commit(): sys.exit(1)
    print()
    print("=" * 70)
    print(f"{G}✅ تغییرات اعمال و کامیت شد!{RST}")
    print("=" * 70)
    print()
    print(f"برنچ: {B}{BRANCH_NAME}{RST}")
    print(f"بکاپ: {B}{BACKUP_BRANCH}{RST}")
    print()
    print(f"📤 پوش: {B}git push -u origin {BRANCH_NAME}{RST}")
    print()
    print(f"🔙 برگشت: {B}git checkout {current_branch} && git branch -D {BRANCH_NAME}{RST}")
    print()

if __name__ == "__main__":
    try: main()
    except KeyboardInterrupt:
        print(f"\n{Y}متوقف شد{RST}"); sys.exit(1)
