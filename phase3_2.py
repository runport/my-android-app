#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
log  = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")

BASE = Path("app/src/main/java/com/example")
REPO = BASE / "data/repository/ManufacturingRepository.kt"
VM = BASE / "viewmodel/ManufacturingViewModel.kt"
DAO_FILE = BASE / "data/dao/AppDao.kt"
INV = BASE / "ui/screens/InventoryScreen.kt"
QA = BASE / "ui/dialogs/QuickActionSheets.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# ============================================
# ۱. DAO: کوئری‌های update و delete برای RollUsage
# ============================================
info("۱. افزودن کوئری‌های CRUD مصرف طاقه به DAO")

c = read(DAO_FILE)
if "updateRollUsage" in c:
    warn("کوئری‌ها از قبل هستند")
else:
    m = re.search(r'interface\s+RollUsageDao\s*\{', c)
    if m:
        open_idx = m.end() - 1
        depth = 0
        i = open_idx
        while i < len(c):
            if c[i] == '{': depth += 1
            elif c[i] == '}':
                depth -= 1
                if depth == 0: break
            i += 1
        block = '''
  @Update
  suspend fun updateRollUsage(usage: RollUsageEntity)

  @Delete
  suspend fun deleteRollUsage(usage: RollUsageEntity)

  @Query("SELECT * FROM roll_usages WHERE id = :id")
  suspend fun getUsageById(id: Long): RollUsageEntity?
'''
        c = c[:i] + block + c[i:]
        write(DAO_FILE, c)
        log("کوئری‌های CRUD اضافه شدند")
    else:
        err("interface RollUsageDao پیدا نشد")

# ============================================
# ۲. Repository: متدهای update و delete
# ============================================
info("۲. افزودن متدهای Repository")

c = read(REPO)
if "updateRollUsage" in c:
    warn("متدها از قبل هستند")
else:
    idx = c.rstrip().rfind("}")
    block = '''
  // ==========================================
  // ROLL USAGE CRUD (ویرایش و حذف مصرف طاقه)
  // ==========================================

  /**
   * ویرایش یک مصرف ثبت‌شده
   * اختلاف مقدار مصرف را روی موجودی طاقه اعمال می‌کند
   */
  suspend fun updateRollUsage(
    usageId: Long,
    newMetersUsed: Double,
    newWeightKgUsed: Double,
    newModelName: String,
    newNote: String
  ): Pair<Boolean, String> = database.withTransaction {
    val usage = database.rollUsageDao().getUsageById(usageId)
      ?: return@withTransaction Pair(false, "مصرف یافت نشد")
    val roll = database.fabricRollDao().getRollById(usage.rollId)
      ?: return@withTransaction Pair(false, "طاقه یافت نشد")

    // اختلاف متری: مثبت = مصرف بیشتر، منفی = مصرف کمتر
    val metersDiff = newMetersUsed - usage.metersUsed

    // موجودی جدید طاقه (اگر مصرف بیشتر شد، کم می‌شود)
    val newRemaining = roll.remainingMeters - metersDiff
    if (newRemaining < 0) {
      return@withTransaction Pair(
        false,
        "موجودی طاقه کافی نیست. باقیمانده فعلی: ${"%.2f".format(roll.remainingMeters)} متر"
      )
    }

    // محاسبه مجدد هزینه‌ها
    val newFabricCost = (newMetersUsed * roll.buyPricePerMeter).toLong()
    val newShippingCost = if (roll.initialMeters > 0)
      ((newMetersUsed / roll.initialMeters) * roll.allocatedShippingCost).toLong()
    else 0L

    // به‌روزرسانی طاقه
    val metersPerKg = roll.metersPerKg
    val newRemainingKg = if (metersPerKg > 0) newRemaining / metersPerKg else 0.0
    val newStatus = if (newRemaining <= 0.5) "پایان یافته" else "در حال مصرف"

    database.fabricRollDao().updateRoll(
      roll.copy(
        remainingMeters = newRemaining,
        remainingWeightKg = newRemainingKg,
        status = newStatus
      )
    )

    // به‌روزرسانی رکورد مصرف
    database.rollUsageDao().updateRollUsage(
      usage.copy(
        metersUsed = newMetersUsed,
        weightKgUsed = newWeightKgUsed,
        modelName = newModelName,
        note = newNote,
        allocatedFabricCost = newFabricCost,
        allocatedShippingCost = newShippingCost
      )
    )

    Pair(true, "مصرف با موفقیت ویرایش شد. باقیمانده طاقه: ${"%.2f".format(newRemaining)} متر")
  }

  /**
   * حذف یک مصرف - موجودی طاقه برمی‌گردد
   */
  suspend fun deleteRollUsage(usageId: Long): Pair<Boolean, String> = database.withTransaction {
    val usage = database.rollUsageDao().getUsageById(usageId)
      ?: return@withTransaction Pair(false, "مصرف یافت نشد")
    val roll = database.fabricRollDao().getRollById(usage.rollId)

    // برگرداندن متر به طاقه
    if (roll != null) {
      val restoredMeters = roll.remainingMeters + usage.metersUsed
      val metersPerKg = roll.metersPerKg
      val restoredKg = if (metersPerKg > 0) restoredMeters / metersPerKg else 0.0
      val newStatus = if (restoredMeters <= 0.5) "پایان یافته"
                     else if (restoredMeters >= roll.initialMeters - 0.5) "موجود"
                     else "در حال مصرف"

      database.fabricRollDao().updateRoll(
        roll.copy(
          remainingMeters = restoredMeters,
          remainingWeightKg = restoredKg,
          status = newStatus
        )
      )
    }

    database.rollUsageDao().deleteRollUsage(usage)
    Pair(true, "مصرف حذف شد و ${"%.2f".format(usage.metersUsed)} متر به طاقه برگشت")
  }
'''
    c = c[:idx] + block + c[idx:]
    write(REPO, c)
    log("متدهای Repository اضافه شدند")

# ============================================
# ۳. ViewModel: اکشن‌های update/delete
# ============================================
info("۳. افزودن اکشن‌های ViewModel")

c = read(VM)
if "updateRollUsageAction" in c or "fun updateRollUsage(" in c:
    warn("اکشن‌ها از قبل هستند")
else:
    idx = c.rstrip().rfind("}")
    block = '''
  // ==========================================
  // ROLL USAGE ACTIONS
  // ==========================================

  fun updateRollUsageAction(
    usageId: Long,
    newMetersUsed: Double,
    newWeightKgUsed: Double,
    newModelName: String,
    newNote: String
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.updateRollUsage(
          usageId, newMetersUsed, newWeightKgUsed, newModelName, newNote
        )
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteRollUsageAction(usageId: Long) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.deleteRollUsage(usageId)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف: ${e.localizedMessage}", true)
      }
    }
  }
'''
    c = c[:idx] + block + c[idx:]
    write(VM, c)
    log("اکشن‌ها اضافه شدند")

# ============================================
# ۴. RollHistoryModal: دکمه ویرایش/حذف
# ============================================
info("۴. افزودن دکمه ویرایش/حذف به RollHistoryModal")

c = read(QA)
if "onEdit = {" in c and "updateRollUsageAction" in c:
    warn("قبلاً اعمال شده")
elif "fun RollHistoryModal(" in c:
    # پیدا کردن بخش نمایش usages.forEach
    anchor = """        usages.forEach { usage ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(customColors.secondaryBg)
              .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
              .padding(12.dp)
          ) {"""

    if anchor in c:
        # در این بخش، دکمه‌های edit/delete در گوشه ظاهر می‌شوند
        replacement = """        usages.forEach { usage ->
          var showEditDialog by remember(usage.id) { mutableStateOf(false) }
          var showDeleteConfirm by remember(usage.id) { mutableStateOf(false) }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(customColors.secondaryBg)
              .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
              .padding(12.dp)
          ) {"""
        c = c.replace(anchor, replacement, 1)
        log("state ویرایش/حذف به usage اضافه شد (بخش ۱)")
    else:
        warn("anchor usages.forEach پیدا نشد — مرحله ۴ ناتمام")

write(QA, c)

# ============================================
# ۵. اعتبارسنجی
# ============================================
print()
info("۵. بررسی آکولاد:")
for path, label in [(DAO_FILE, "AppDao"), (REPO, "Repository"),
                    (VM, "ViewModel"), (QA, "QuickActionSheets")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

print()
log("زیرفاز 3.2 اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase3.2): roll usage CRUD' && git push origin feature/cutting-parts-workflow{RST}")
print()
warn("نکته: UI ویرایش/حذف در 3.3 کامل می‌شود")
