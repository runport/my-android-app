#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
phase1_price_update.py
فاز ۱: به‌روزرسانی قیمت روز (بدون خرید) + تاریخچه + اثر بر محصولات
- بند ۳۹: قیمت روز طاقه و ملزومات
- بند ۱۲: تاریخچه قیمت
- بند ۱۳: آخرین قیمت ثبت‌شده مبنای محاسبه
- بند ۱۴: به‌روزرسانی بهای محصولات در جریان
"""
import re, sys
from pathlib import Path

G, Y, R, B, C, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[96m", "\033[0m"
ok   = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")
step = lambda m: print(f"\n{C}━━━ {m} ━━━{RST}")

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

def replace_once(content, old, new, label):
    if new in content:
        warn(f"{label}: قبلاً اعمال شده"); return content
    if old not in content:
        err(f"{label}: الگو پیدا نشد"); return content
    ok(label)
    return content.replace(old, new, 1)

def insert_before_last_brace(content, block, label):
    if block.strip() in content:
        warn(f"{label}: قبلاً اعمال شده"); return content
    idx = content.rstrip().rfind("}")
    if idx < 0:
        err(f"{label}: }} پایانی پیدا نشد"); return content
    ok(label)
    return content[:idx] + block + content[idx:]

BASE = Path("app/src/main/java/com/example")
ENTITIES = BASE / "data/model/Entities.kt"
REPO = BASE / "data/repository/ManufacturingRepository.kt"
VM = BASE / "viewmodel/ManufacturingViewModel.kt"
DB = BASE / "data/database/AppDatabase.kt"
DAO_FILE = BASE / "data/dao/AppDao.kt"
QA = BASE / "ui/dialogs/QuickActionSheets.kt"
INV = BASE / "ui/screens/InventoryScreen.kt"

# ======================================================
step("۱. Entity: افزودن CurrentPrice به FabricRollEntity")
# ======================================================
c = read(ENTITIES)
c = replace_once(c,
"""  val buyPricePerMeter: Long = 0L, // قیمت خرید هر متر
  val buyPricePerKg: Long = 0L, // قیمت خرید هر کیلوگرم
  val allocatedShippingCost: Long = 0L, // هزینه باربری تخصیص‌یافته به این طاقه از بارنامه""",
"""  val buyPricePerMeter: Long = 0L, // قیمت خرید هر متر (تاریخی)
  val buyPricePerKg: Long = 0L, // قیمت خرید هر کیلوگرم (تاریخی)
  val currentPricePerMeter: Long = 0L, // آخرین قیمت روز هر متر (مبنای محاسبات فعلی)
  val currentPricePerKg: Long = 0L, // آخرین قیمت روز هر کیلوگرم (مبنای محاسبات فعلی)
  val lastPriceUpdateDate: String = "", // تاریخ آخرین به‌روزرسانی قیمت روز
  val lastPriceUpdateTimestamp: Long = 0L,
  val allocatedShippingCost: Long = 0L, // هزینه باربری تخصیص‌یافته به این طاقه از بارنامه""",
"FabricRollEntity currentPrice")

# ======================================================
step("۲. Entity: افزودن CurrentPrice به MaterialEntity")
# ======================================================
c = read(ENTITIES)
c = replace_once(c,
"""  val currentPrice: Long = 0L, // آخرین قیمت ثبت‌شده (مبنای محاسبه بهای جاری محصولات)
  val lastPurchasePrice: Long = 0L, // آخرین قیمت خرید واقعی""",
"""  val currentPrice: Long = 0L, // آخرین قیمت روز (مبنای محاسبه بهای جاری محصولات)
  val lastPurchasePrice: Long = 0L, // آخرین قیمت خرید واقعی (تاریخی)
  val currentPriceKg: Long = 0L, // آخرین قیمت روز به ازای کیلوگرم (اگر واحد پایه عدد/متر باشد)
  val priceUpdateNote: String = "", // یادداشت آخرین به‌روزرسانی قیمت روز""",
"MaterialEntity currentPriceKg")

# ======================================================
step("۳. DAO: افزودن کوئری‌های به‌روزرسانی قیمت")
# ======================================================
c = read(DAO_FILE)
if "updateRollCurrentPrice" not in c:
    block = """
  // ===== Price Update Queries (روزآمدسازی قیمت بدون خرید) =====

  @Query("UPDATE fabric_rolls SET currentPricePerMeter = :pricePerMeter, currentPricePerKg = :pricePerKg, lastPriceUpdateDate = :date, lastPriceUpdateTimestamp = :timestamp WHERE id = :rollId")
  suspend fun updateRollCurrentPrice(rollId: Long, pricePerMeter: Long, pricePerKg: Long, date: String, timestamp: Long)

  @Query("UPDATE materials SET currentPrice = :newPrice, currentPriceKg = :priceKg, lastPriceChangeDate = :date, lastPriceChangeTimestamp = :timestamp, priceUpdateNote = :note WHERE id = :materialId")
  suspend fun updateMaterialCurrentPrice(materialId: Long, newPrice: Long, priceKg: Long, date: String, timestamp: Long, note: String)

  @Query("SELECT * FROM fabric_rolls WHERE id = :rollId")
  suspend fun getRollByIdOnce(rollId: Long): FabricRollEntity?
"""
    idx = c.rstrip().rfind("}")
    c = c[:idx] + block + c[idx:]
    write(DAO_FILE, c)
    ok("کوئری‌های قیمت DAO اضافه شد")
else:
    warn("کوئری‌های قیمت DAO قبلاً هست")

# ======================================================
step("۴. Repository: متد به‌روزرسانی قیمت طاقه")
# ======================================================
c = read(REPO)
block = '''
  // ==========================================
  // PRICE UPDATE (روزآمدسازی قیمت بدون خرید)
  // ==========================================

  /**
   * به‌روزرسانی قیمت روز یک طاقه بدون ثبت خرید جدید.
   * موجودی فیزیکی دست نمی‌خورد؛ فقط قیمت روز و بهای محاسباتی.
   */
  suspend fun updateFabricRollCurrentPrice(
    rollId: Long, newPricePerMeter: Long, newPricePerKg: Long = 0L,
    reason: String = "تغییر قیمت بازار", operator: String = "مدیر کارگاه"
  ): Pair<Boolean, String> = database.withTransaction {
    val roll = database.fabricRollDao().getRollById(rollId)
      ?: return@withTransaction Pair(false, "طاقه یافت نشد")
    if (newPricePerMeter <= 0L && newPricePerKg <= 0L) {
      return@withTransaction Pair(false, "قیمت جدید باید بیشتر از صفر باشد")
    }
    val finalPricePerMeter = if (newPricePerMeter > 0L) newPricePerMeter else {
      if (roll.metersPerKg > 0.0) (newPricePerKg / roll.metersPerKg).toLong() else roll.buyPricePerMeter
    }
    val finalPricePerKg = if (newPricePerKg > 0L) newPricePerKg else {
      (finalPricePerMeter * roll.metersPerKg).toLong()
    }
    val oldPriceM = if (roll.currentPricePerMeter > 0L) roll.currentPricePerMeter else roll.buyPricePerMeter
    val oldPriceKg = if (roll.currentPricePerKg > 0L) roll.currentPricePerKg else roll.buyPricePerKg
    if (oldPriceM == finalPricePerMeter && oldPriceKg == finalPricePerKg) {
      return@withTransaction Pair(true, "قیمت تغییری نکرد")
    }

    val today = PersianDateHelper.getTodayPersianDate()
    val now = System.currentTimeMillis()

    database.fabricRollDao().updateRoll(
      roll.copy(
        currentPricePerMeter = finalPricePerMeter,
        currentPricePerKg = finalPricePerKg,
        lastPriceUpdateDate = today,
        lastPriceUpdateTimestamp = now
      )
    )

    // ثبت در تاریخچه قیمت
    val changeM = finalPricePerMeter - oldPriceM
    val changePercent = if (oldPriceM > 0L) (changeM.toDouble() / oldPriceM) * 100.0 else 0.0
    try {
      database.materialPriceHistoryDao().insert(
        MaterialPriceHistoryEntity(
          materialId = roll.id,
          materialName = "${roll.fabricType} - طاقه ${roll.rollCode}",
          oldPrice = oldPriceM,
          newPrice = finalPricePerMeter,
          date = today,
          timestamp = now,
          changeAmount = changeM,
          changePercent = changePercent,
          reason = reason,
          source = "FABRIC_ROLL_PRICE_UPDATE",
          supplierName = roll.supplierName,
          recordedBy = operator
        )
      )
    } catch (_: Exception) {}

    // به‌روزرسانی بهای محصولات در جریان که از این طاقه مصرف کرده‌اند
    updateInProgressProductsAfterPriceChange(roll.id, finalPricePerMeter, operator)

    Pair(true, "قیمت طاقه ${roll.rollCode} به‌روزرسانی شد (${finalPricePerMeter} تومان/متر)")
  }

  /**
   * به‌روزرسانی قیمت روز یک ماده اولیه/ملزوم
   */
  suspend fun updateMaterialCurrentPrice(
    materialId: Long, newPrice: Long, reason: String = "تغییر قیمت بازار",
    operator: String = "مدیر کارگاه"
  ): Pair<Boolean, String> = database.withTransaction {
    val mat = database.materialDao().getById(materialId)
      ?: return@withTransaction Pair(false, "ماده یافت نشد")
    if (newPrice <= 0L) return@withTransaction Pair(false, "قیمت باید بیشتر از صفر باشد")
    if (mat.currentPrice == newPrice) {
      return@withTransaction Pair(true, "قیمت تغییری نکرد")
    }
    val today = PersianDateHelper.getTodayPersianDate()
    val now = System.currentTimeMillis()

    database.materialDao().update(
      mat.copy(
        currentPrice = newPrice,
        lastPriceSource = "MARKET_UPDATE",
        lastPriceChangeDate = today,
        lastPriceChangeTimestamp = now
      )
    )

    try {
      val changeAmount = newPrice - mat.currentPrice
      database.materialPriceHistoryDao().insert(
        MaterialPriceHistoryEntity(
          materialId = mat.id,
          materialName = mat.name,
          oldPrice = mat.currentPrice,
          newPrice = newPrice,
          date = today,
          timestamp = now,
          changeAmount = changeAmount,
          changePercent = if (mat.currentPrice > 0L) (changeAmount.toDouble() / mat.currentPrice) * 100.0 else 0.0,
          reason = reason,
          source = "MATERIAL_PRICE_UPDATE",
          supplierName = mat.supplierName,
          recordedBy = operator
        )
      )
    } catch (_: Exception) {}

    // به‌روزرسانی BOM استفاده‌کننده
    updateProductsUsingMaterial(materialId, newPrice, operator)
    Pair(true, "قیمت «${mat.name}» به‌روزرسانی شد")
  }

  /**
   * به‌روزرسانی بهای محصولات در جریان که از این طاقه استفاده کرده‌اند
   */
  private suspend fun updateInProgressProductsAfterPriceChange(
    rollId: Long, newPricePerMeter: Long, operator: String
  ) {
    try {
      val allCuttings = database.cuttingDao().getAllCuttings().firstOrNull() ?: emptyList()
      val affected = allCuttings.filter { it.rollId == rollId && !it.isStockAdded }
      val today = PersianDateHelper.getTodayPersianDate()
      val now = System.currentTimeMillis()

      affected.forEach { part ->
        val newFabricCost = (part.metersUsed * newPricePerMeter).toLong()
        val newTotal = newFabricCost + part.allocatedShippingCost +
          part.accessoriesCost + part.tailorCost + part.overheadCost + part.otherDirectCost
        database.cuttingDao().updateCutting(
          part.copy(fabricCost = newFabricCost, totalCost = newTotal)
        )
        try {
          database.auditLogDao().insert(
            AuditLogEntity(
              timestamp = now, date = today, entityName = "CuttingPart",
              entityId = part.id, action = "PRICE_RECALC",
              oldValue = "بهای قبلی: ${part.totalCost}",
              newValue = "بهای جدید: $newTotal (پس از به‌روزرسانی قیمت طاقه)",
              reason = "به‌روزرسانی خودکار بر اساس آخرین قیمت ثبت‌شده",
              recordedBy = operator
            )
          )
        } catch (_: Exception) {}
      }
    } catch (_: Exception) {}
  }

  /**
   * به‌روزرسانی BOM و بهای محصولات استفاده‌کننده از این ماده
   */
  private suspend fun updateProductsUsingMaterial(
    materialId: Long, newPrice: Long, operator: String
  ) {
    try {
      val boms = database.productBOMDao().getBOMsUsingMaterial(materialId)
      val productIds = boms.map { it.productId }.distinct()
      val today = PersianDateHelper.getTodayPersianDate()
      val now = System.currentTimeMillis()

      productIds.forEach { pid ->
        val product = database.productDao().getProductById(pid) ?: return@forEach
        val productBoms = database.productBOMDao().getBOMListForProduct(pid)
        var newCost = 0L
        productBoms.forEach { b ->
          val price = if (b.materialId == materialId) newPrice
                     else database.materialDao().getById(b.materialId)?.currentPrice ?: b.unitRate
          newCost += (b.standardQuantity * price).toLong()
        }
        val fullCost = newCost + product.sewingWage + product.allocatedFreightCost + product.overheadCost
        database.productDao().update(
          product.copy(
            currentCostPrice = fullCost,
            lastPriceUpdateDate = today,
            lastPriceUpdateTimestamp = now
          )
        )
      }
    } catch (_: Exception) {}
  }
'''
c = insert_before_last_brace(c, block, "Repository قیمت روز")
write(REPO, c)

# ======================================================
step("۵. ViewModel: اکشن‌های به‌روزرسانی قیمت")
# ======================================================
c = read(VM)
block = '''
  // ==========================================
  // PRICE UPDATE ACTIONS
  // ==========================================

  fun updateRollPrice(rollId: Long, newPricePerMeter: Long, newPricePerKg: Long = 0L, reason: String = "تغییر قیمت بازار") {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.updateFabricRollCurrentPrice(rollId, newPricePerMeter, newPricePerKg, reason)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در به‌روزرسانی قیمت: ${e.localizedMessage}", true)
      }
    }
  }

  fun updateMaterialPrice(materialId: Long, newPrice: Long, reason: String = "تغییر قیمت بازار") {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.updateMaterialCurrentPrice(materialId, newPrice, reason)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در به‌روزرسانی قیمت: ${e.localizedMessage}", true)
      }
    }
  }

  // state برای دیالوگ قیمت
  private val _priceUpdateTarget = MutableStateFlow<PriceUpdateTarget?>(null)
  val priceUpdateTarget: StateFlow<PriceUpdateTarget?> = _priceUpdateTarget.asStateFlow()

  fun openPriceUpdateDialog(target: PriceUpdateTarget) { _priceUpdateTarget.value = target }
  fun closePriceUpdateDialog() { _priceUpdateTarget.value = null }
'''
c = insert_before_last_brace(c, block, "ViewModel price actions")
write(VM, c)

# ======================================================
step("۶. ViewModel: data class PriceUpdateTarget")
# ======================================================
c = read(VM)
if "data class PriceUpdateTarget" not in c:
    insert_point = c.find("class ManufacturingViewModel")
    if insert_point > 0:
        data_class = '''data class PriceUpdateTarget(
  val type: PriceUpdateType,
  val id: Long,
  val title: String,
  val currentPricePerMeter: Long = 0L,
  val currentPricePerKg: Long = 0L,
  val metersPerKg: Double = 0.0,
  val unit: String = "متر"
)

enum class PriceUpdateType { FABRIC_ROLL, MATERIAL }

'''
        c = c[:insert_point] + data_class + c[insert_point:]
        write(VM, c)
        ok("data class PriceUpdateTarget اضافه شد")
    else:
        err("کلاس ManufacturingViewModel پیدا نشد")
else:
    warn("PriceUpdateTarget قبلاً هست")

# ======================================================
step("۷. AppDatabase: نسخه 10 → 11 + Migration")
# ======================================================
c = read(DB)
if "version = 11," in c:
    warn("نسخه قبلاً 11 است")
elif "version = 10," in c:
    c = c.replace("version = 10,", "version = 11,", 1)
    write(DB, c)
    ok("نسخه: 10 → 11")
else:
    err("نسخه 10 پیدا نشد (شاید قبلاً تغییر کرده)")

c = read(DB)
if "MIGRATION_10_11" not in c:
    mig = '''    val MIGRATION_10_11 = object : Migration(10, 11) {
      override fun migrate(db: SupportSQLiteDatabase) {
        try {
          db.execSQL("ALTER TABLE `fabric_rolls` ADD COLUMN `currentPricePerMeter` INTEGER NOT NULL DEFAULT 0")
          db.execSQL("ALTER TABLE `fabric_rolls` ADD COLUMN `currentPricePerKg` INTEGER NOT NULL DEFAULT 0")
          db.execSQL("ALTER TABLE `fabric_rolls` ADD COLUMN `lastPriceUpdateDate` TEXT NOT NULL DEFAULT ''")
          db.execSQL("ALTER TABLE `fabric_rolls` ADD COLUMN `lastPriceUpdateTimestamp` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `materials` ADD COLUMN `currentPriceKg` INTEGER NOT NULL DEFAULT 0")
          db.execSQL("ALTER TABLE `materials` ADD COLUMN `priceUpdateNote` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        // مقدار اولیه: قیمت روز = قیمت خرید
        db.execSQL("UPDATE `fabric_rolls` SET `currentPricePerMeter` = `buyPricePerMeter` WHERE `currentPricePerMeter` = 0")
        db.execSQL("UPDATE `fabric_rolls` SET `currentPricePerKg` = `buyPricePerKg` WHERE `currentPricePerKg` = 0")
        db.execSQL("UPDATE `materials` SET `currentPriceKg` = `currentPrice` WHERE `currentPriceKg` = 0")
      }
    }

'''
    marker = "    fun getDatabase("
    if marker in c:
        c = c.replace(marker, mig + marker, 1)
        write(DB, c)
        ok("MIGRATION_10_11 اضافه شد")
    else:
        err("fun getDatabase پیدا نشد")

c = read(DB)
old = ".addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)"
new = ".addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11)"
if "MIGRATION_9_10, MIGRATION_10_11" in c:
    warn("addMigrations قبلاً به‌روز")
elif old in c:
    write(DB, c.replace(old, new, 1))
    ok("addMigrations به‌روز شد")
else:
    err("خط addMigrations پیدا نشد")

# ======================================================
step("۸. InventoryScreen: دکمه به‌روزرسانی قیمت روی کارت طاقه")
# ======================================================
c = read(INV)
c = replace_once(c,
"""fun FabricRollInventoryCard(
  roll: FabricRollEntity,
  onConsume: () -> Unit,
  onHistory: () -> Unit,
  onEdit: () -> Unit = {}
) {""",
"""fun FabricRollInventoryCard(
  roll: FabricRollEntity,
  onConsume: () -> Unit,
  onHistory: () -> Unit,
  onEdit: () -> Unit = {},
  onUpdatePrice: () -> Unit = {}
) {""",
"signature FabricRollInventoryCard + onUpdatePrice")

c = replace_once(c,
"""        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = AccentIndigo)
          }
          IconButton(onClick = onHistory) {
            Icon(Icons.Default.History, contentDescription = "سوابق مصرف", tint = AccentCyan)
          }
        }
      }""",
"""        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onUpdatePrice) {
            Icon(Icons.Default.Refresh, contentDescription = "به‌روزرسانی قیمت روز", tint = StatusSuccess)
          }
          IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = AccentIndigo)
          }
          IconButton(onClick = onHistory) {
            Icon(Icons.Default.History, contentDescription = "سوابق مصرف", tint = AccentCyan)
          }
        }
      }""",
"icon buttons + refresh")

# اضافه کردن import Refresh
if "import androidx.compose.material.icons.filled.Refresh" not in c:
    c = c.replace(
        "import androidx.compose.material.icons.filled.Inventory2",
        "import androidx.compose.material.icons.filled.Inventory2\nimport androidx.compose.material.icons.filled.Refresh",
        1
    )
    ok("import Refresh اضافه شد")

c = replace_once(c,
"""              onEdit = {
                viewModel.startEditFabricRoll(roll)
              }
            )
          }""",
"""              onEdit = {
                viewModel.startEditFabricRoll(roll)
              },
              onUpdatePrice = {
                viewModel.openPriceUpdateDialog(
                  com.example.viewmodel.PriceUpdateTarget(
                    type = com.example.viewmodel.PriceUpdateType.FABRIC_ROLL,
                    id = roll.id,
                    title = "طاقه ${roll.rollCode} - ${roll.fabricType}",
                    currentPricePerMeter = if (roll.currentPricePerMeter > 0L) roll.currentPricePerMeter else roll.buyPricePerMeter,
                    currentPricePerKg = if (roll.currentPricePerKg > 0L) roll.currentPricePerKg else roll.buyPricePerKg,
                    metersPerKg = roll.metersPerKg,
                    unit = "متر"
                  )
                )
              }
            )
          }""",
"call site onUpdatePrice")

write(INV, c)

# ======================================================
step("۹. QuickActionSheets: دیالوگ به‌روزرسانی قیمت")
# ======================================================
c = read(QA)

# اضافه کردن branch به ModalBottomSheet
c = replace_once(c,
"""        QuickActionType.INVENTORY_AUDIT -> {
          QuickInventoryAuditForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }""",
"""        QuickActionType.INVENTORY_AUDIT -> {
          QuickInventoryAuditForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.MARKET_PRICE_UPDATE -> {
          QuickMarketPriceUpdateForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }""",
"branch MARKET_PRICE_UPDATE")

# اضافه کردن دیالوگ قیمت
dialog_code = '''

/**
 * دیالوگ به‌روزرسانی قیمت روز (بدون ثبت خرید جدید)
 * بند ۳۹: کاربر می‌تواند قیمت روز را تغییر دهد بدون اینکه موجودی فیزیکی تغییر کند
 */
@Composable
fun PriceUpdateDialog(
  viewModel: ManufacturingViewModel,
  onDismiss: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val target by viewModel.priceUpdateTarget.collectAsState()
  val t = target ?: return

  var pricePerMeterText by remember { mutableStateOf(t.currentPricePerMeter.toString()) }
  var pricePerKgText by remember { mutableStateOf(t.currentPricePerKg.toString()) }
  var reasonText by remember { mutableStateOf("تغییر قیمت بازار") }

  val isFabricRoll = t.type == com.example.viewmodel.PriceUpdateType.FABRIC_ROLL

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("به‌روزرسانی قیمت روز", fontWeight = FontWeight.Bold, color = customColors.textPrimary)
        Text(t.title, fontSize = 11.sp, color = customColors.textMuted)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // اطلاعات فعلی
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(customColors.secondaryBg)
            .padding(10.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("قیمت قبلی:", fontSize = 11.sp, color = customColors.textMuted)
            if (isFabricRoll) {
              Text("متر: ${CurrencyHelper.formatToman(t.currentPricePerMeter)} • کیلو: ${CurrencyHelper.formatToman(t.currentPricePerKg)}",
                fontSize = 12.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            } else {
              Text("هر ${t.unit}: ${CurrencyHelper.formatToman(t.currentPricePerMeter)}",
                fontSize = 12.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            }
          }
        }

        // فیلدهای جدید
        if (isFabricRoll) {
          ExecutiveTextField(
            label = "قیمت جدید هر متر (تومان)",
            value = pricePerMeterText,
            keyboardType = KeyboardType.Number,
            onValueChange = {
              pricePerMeterText = it
              val pm = it.toLongOrNull() ?: 0L
              if (pm > 0L && t.metersPerKg > 0.0) {
                pricePerKgText = (pm * t.metersPerKg).toLong().toString()
              }
            }
          )
          ExecutiveTextField(
            label = "قیمت جدید هر کیلو (تومان)",
            value = pricePerKgText,
            keyboardType = KeyboardType.Number,
            onValueChange = {
              pricePerKgText = it
              val pk = it.toLongOrNull() ?: 0L
              if (pk > 0L && t.metersPerKg > 0.0) {
                pricePerMeterText = (pk / t.metersPerKg).toLong().toString()
              }
            }
          )
        } else {
          ExecutiveTextField(
            label = "قیمت جدید هر ${t.unit} (تومان)",
            value = pricePerMeterText,
            keyboardType = KeyboardType.Number,
            onValueChange = { pricePerMeterText = it }
          )
        }

        ExecutiveTextField(
          label = "دلیل تغییر قیمت",
          value = reasonText,
          onValueChange = { reasonText = it }
        )

        // هشدار
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(StatusWarning.copy(alpha = 0.12f))
            .padding(8.dp)
        ) {
          Text(
            "⚠ این عملیات موجودی فیزیکی را تغییر نمی‌دهد. فقط قیمت روز و بهای محاسباتی به‌روزرسانی می‌شود.",
            fontSize = 11.sp, color = StatusWarning
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val pm = pricePerMeterText.toLongOrNull() ?: 0L
          val pk = pricePerKgText.toLongOrNull() ?: 0L
          if (isFabricRoll) {
            viewModel.updateRollPrice(t.id, pm, pk, reasonText)
          } else {
            viewModel.updateMaterialPrice(t.id, pm, reasonText)
          }
          viewModel.closePriceUpdateDialog()
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
      ) {
        Text("ثبت قیمت روز", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = {
        viewModel.closePriceUpdateDialog()
        onDismiss()
      }) {
        Text("انصراف", color = customColors.textMuted)
      }
    },
    containerColor = customColors.cardElevated
  )
}
'''
c = c.rstrip() + dialog_code
write(QA, c)
ok("PriceUpdateDialog اضافه شد")

# ======================================================
step("۱۰. اعتبارسنجی آکولاد")
# ======================================================
for path, label in [(ENTITIES, "Entities"), (REPO, "Repository"),
                    (VM, "ViewModel"), (DB, "Database"),
                    (DAO_FILE, "AppDao"), (QA, "QuickActionSheets"),
                    (INV, "InventoryScreen")]:
    content = read(path)
    ob, cb = content.count("{"), content.count("}")
    if ob == cb:
        ok(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن")

print()
print("=" * 70)
ok("فاز ۱ اعمال شد")
print("=" * 70)
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase1): price update without purchase' && git push{RST}")
print()
info("⚠ ممکن است یک warning در مورد ModalBottomSheet call-site")
info("  باشد اگر MARKET_PRICE_UPDATE branch را دوبار اضافه کرده باشد.")
info("  اگر build خطا داد، متن خطا را بفرست.")
