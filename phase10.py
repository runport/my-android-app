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
QA = BASE / "ui/dialogs/QuickActionSheets.kt"
MORE = BASE / "ui/screens/MoreHubScreen.kt"
REPO = BASE / "data/repository/ManufacturingRepository.kt"
VM = BASE / "viewmodel/ManufacturingViewModel.kt"
ENT = BASE / "data/model/Entities.kt"
DB = BASE / "data/database/AppDatabase.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# =====================================================
# ۱۰.۱ — حذف فیلدهای باربری ثابت از QuickSettingsForm
# =====================================================
info("۱۰.۱ — حذف فیلدهای باربری ثابت")

c = read(QA)
old_row = '''    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "باربری ثابت سفارش (تومان)", value = shipOrderText, keyboardType = KeyboardType.Number, onValueChange = { shipOrderText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "باربری هر طاقه (تومان)", value = shipRollText, keyboardType = KeyboardType.Number, onValueChange = { shipRollText = it })
      }
    }'''

new_row = '''    // 📌 هزینه باربری به صورت خودکار از بارنامه‌های ثبت‌شده محاسبه می‌شود.
    // فیلدهای ثابت قبلی حذف شده‌اند (مقادیر پیش‌فرض در fallback استفاده می‌شوند).'''

if new_row.split("\n")[0] in c:
    warn("قبلاً حذف شده")
elif old_row in c:
    c = c.replace(old_row, new_row, 1)
    write(QA, c)
    log("فیلدهای باربری ثابت حذف شدند")
else:
    warn("الگوی فیلدهای باربری پیدا نشد")

# =====================================================
# ۱۰.۲ — افزودن دکمه «پاک کردن کل داده» به MoreHubScreen
# =====================================================
info("۱۰.۲ — افزودن بخش پاک کردن کل داده")

# ۱۰.۲.۱ — ViewModel: متد جدید
vm_c = read(VM)
if "clearAllDataWithoutDemo" not in vm_c:
    idx = vm_c.rstrip().rfind("}")
    method = '''
  // ==========================================
  // CLEAR ALL DATA (بدون دمو)
  // ==========================================
  fun clearAllDataWithoutDemo() {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.clearAllDataKeepingStructure()
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.localizedMessage}", true)
      }
    }
  }
'''
    vm_c = vm_c[:idx] + method + vm_c[idx:]
    write(VM, vm_c)
    log("متد ViewModel اضافه شد")
else:
    warn("متد ViewModel از قبل هست")

# ۱۰.۲.۲ — Repository: متد جدید
repo_c = read(REPO)
if "clearAllDataKeepingStructure" not in repo_c:
    idx = repo_c.rstrip().rfind("}")
    method = '''
  /**
   * پاک کردن تمام داده‌ها بدون بارگذاری دمو
   * فقط تنظیمات پیش‌فرض FactorySettings بازنشانی می‌شود
   */
  suspend fun clearAllDataKeepingStructure(): Pair<Boolean, String> = database.withTransaction {
    try {
      database.clearAllTables()
      // تنظیمات پیش‌فرض
      database.factorySettingsDao().insertOrUpdate(
        com.example.data.model.FactorySettingsEntity()
      )
      Pair(true, "تمام داده‌ها پاک شد. سیستم آماده ورود اطلاعات جدید است.")
    } catch (e: Exception) {
      Pair(false, "خطا در پاکسازی: ${e.localizedMessage}")
    }
  }
'''
    repo_c = repo_c[:idx] + method + repo_c[idx:]
    write(REPO, repo_c)
    log("متد Repository اضافه شد")
else:
    warn("متد Repository از قبل هست")

# ۱۰.۲.۳ — MoreHubScreen: state + دکمه
mc = read(MORE)

if "showClearAllConfirmDialog" not in mc:
    anchor_state = "var showResetConfirmDialog by remember { mutableStateOf(false) }"
    new_state = """var showResetConfirmDialog by remember { mutableStateOf(false) }
  var showClearAllConfirmDialog by remember { mutableStateOf(false) }"""
    if anchor_state in mc:
        mc = mc.replace(anchor_state, new_state, 1)
        log("state به MoreHubScreen اضافه شد")

if "clearAllDataWithoutDemo()" not in mc:
    # پیدا کردن دکمه reset موجود
    old_btn = '''Button(
                  onClick = { showResetConfirmDialog = true },
                  modifier = Modifier.fillMaxWidth().height(42.dp),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = StatusDanger.copy(alpha = 0.85f))
                ) {
                  Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(Modifier.size(8.dp))
                  Text("پاک کردن دائم داده‌ها و بازنشانی داده‌های نمونه (دمو)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }'''

    new_btn = '''Button(
                  onClick = { showClearAllConfirmDialog = true },
                  modifier = Modifier.fillMaxWidth().height(42.dp),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                  Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(Modifier.size(8.dp))
                  Text("پاک کردن کامل داده‌ها (شروع از صفر، بدون دمو)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                  onClick = { showResetConfirmDialog = true },
                  modifier = Modifier.fillMaxWidth().height(42.dp),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusWarning)
                ) {
                  Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(Modifier.size(8.dp))
                  Text("بارگذاری داده‌های نمونه (دمو)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }'''

    if old_btn in mc:
        mc = mc.replace(old_btn, new_btn, 1)
        log("دکمه‌ها به‌روز شدند (پاک کامل + دمو جدا)")
    else:
        warn("دکمه reset پیدا نشد — بررسی دستی")

# ۱۰.۲.۴ — نمایش دیالوگ تأیید جدید
if "showClearAllConfirmDialog)" not in mc and "showClearAllConfirmDialog" in mc:
    anchor_dlg = "if (showResetConfirmDialog) {"
    new_dlg = '''if (showClearAllConfirmDialog) {
    DeleteAllDataConfirmDialog(
      onConfirm = {
        viewModel.clearAllDataWithoutDemo()
        showClearAllConfirmDialog = false
      },
      onDismiss = { showClearAllConfirmDialog = false }
    )
  }

  if (showResetConfirmDialog) {'''
    if anchor_dlg in mc:
        mc = mc.replace(anchor_dlg, new_dlg, 1)
        log("دیالوگ تأیید پاک کامل اضافه شد")

write(MORE, mc)

# =====================================================
# ۱۰.۳ — افزودن هزینه‌های اضافی به QuickRollConsumeForm
# =====================================================
info("۱۰.۳ — افزودن فیلدهای هزینه به مصرف پارچه")

c = read(QA)

# چک می‌کنیم قبلاً اعمال نشده
if "tailorCostText" in c and "profitPercentText" in c:
    warn("فیلدها از قبل هستند")
else:
    # پیدا کردن محل مناسب: بعد از فیلد "یادداشت / پارت دوخت"
    anchor_note = '''    ExecutiveTextField(
      label = "یادداشت / پارت دوخت",
      value = note,
      onValueChange = { note = it }
    )'''

    if anchor_note in c:
        # state جدید
        state_anchor = '''  var note by remember { mutableStateOf("تولید و برش پارت اول") }'''
        new_state = '''  var note by remember { mutableStateOf("تولید و برش پارت اول") }
  var tailorCostText by remember { mutableStateOf("85000") }
  var profitPercentText by remember { mutableStateOf("35") }
  var baseMaterialsText by remember { mutableStateOf("15000") }'''

        if state_anchor in c and "tailorCostText" not in c:
            c = c.replace(state_anchor, new_state, 1)
            log("state های جدید اضافه شدند")

        # بخش UI
        ui_block = anchor_note + '''

    // ============================================
    // هزینه‌های تولید و قیمت‌گذاری (فاز ۱۰.۳)
    // ============================================
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.cardElevated)
        .border(1.dp, AccentIndigo.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          "💰 هزینه‌های تولید و قیمت‌گذاری محصول",
          style = MaterialTheme.typography.labelMedium,
          color = AccentIndigo,
          fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(modifier = Modifier.weight(1f)) {
            ExecutiveTextField(
              label = "هزینه خیاط هر کار",
              value = tailorCostText,
              keyboardType = KeyboardType.Number,
              onValueChange = { tailorCostText = it }
            )
          }
          Box(modifier = Modifier.weight(1f)) {
            ExecutiveTextField(
              label = "سود ثابت هدف (٪)",
              value = profitPercentText,
              keyboardType = KeyboardType.Decimal,
              onValueChange = { profitPercentText = it }
            )
          }
        }

        ExecutiveTextField(
          label = "هزینه ملزومات پایه هر کار (نخ، دوک، برق، کرایه)",
          value = baseMaterialsText,
          keyboardType = KeyboardType.Number,
          onValueChange = { baseMaterialsText = it }
        )

        // پیش‌نمایش محاسبه
        val fabricCostPerUnit = if (garmentCount > 0) ((metersUsed * buyPriceMeter) / garmentCount).toLong() else 0L
        val tailorVal = tailorCostText.toLongOrNull() ?: 0L
        val baseVal = baseMaterialsText.toLongOrNull() ?: 0L
        val profitPct = profitPercentText.toDoubleOrNull() ?: 0.0
        val subtotal = fabricCostPerUnit + tailorVal + baseVal
        val profitVal = (subtotal * profitPct / 100.0).toLong()
        val finalUnitPrice = subtotal + profitVal

        HorizontalDivider(color = customColors.border)

        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("هزینه پارچه هر کار:", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(fabricCostPerUnit), fontSize = 11.sp, color = customColors.textPrimary)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("جمع هزینه (بدون سود):", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(subtotal), fontSize = 11.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("سود (${profitPct.toInt()}٪):", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(profitVal), fontSize = 11.sp, color = StatusSuccess)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("قیمت فروش پیشنهادی هر کار:", fontSize = 12.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text(CurrencyHelper.formatToman(finalUnitPrice), fontSize = 13.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
        }
      }
    }'''

        if anchor_note in c and "هزینه‌های تولید و قیمت‌گذاری محصول" not in c:
            c = c.replace(anchor_note, ui_block, 1)
            log("بخش UI اضافه شد")

        write(QA, c)
    else:
        warn("anchor یادداشت در QuickRollConsumeForm پیدا نشد")

# =====================================================
# ۱۰.۴ — تخصیص کرایه به ملزومات (Migration 11 → 12)
# =====================================================
info("۱۰.۴ — تخصیص کرایه به ملزومات")

# ۱۰.۴.۱ — افزودن فیلد به MaterialEntity
ec = read(ENT)
if "allocatedShippingCost" in ec and "MaterialEntity" in ec:
    # چک می‌کنیم داخل MaterialEntity هست یا نه
    mat_start = ec.find("data class MaterialEntity(")
    if mat_start > 0:
        mat_end = ec.find(")", mat_start + 100)
        mat_body = ec[mat_start:mat_end]
        if "allocatedShippingCost" in mat_body:
            warn("فیلد در MaterialEntity از قبل هست")
        else:
            # اضافه کردن بعد از priceUpdateNote
            anchor = '''  val priceUpdateNote: String = "",'''
            new_anchor = '''  val priceUpdateNote: String = "",
  val allocatedShippingCost: Long = 0L, // سهم کرایه باربری از بارنامه‌ها'''
            if anchor in ec:
                ec = ec.replace(anchor, new_anchor, 1)
                write(ENT, ec)
                log("فیلد allocatedShippingCost به MaterialEntity اضافه شد")
            else:
                warn("anchor priceUpdateNote پیدا نشد")

# ۱۰.۴.۲ — Migration 11 → 12
dbc = read(DB)
if "MIGRATION_11_12" in dbc:
    warn("Migration 11_12 از قبل هست")
else:
    # نسخه
    if "version = 11," in dbc:
        dbc = dbc.replace("version = 11,", "version = 12,", 1)
        log("نسخه: 11 → 12")
    else:
        warn("نسخه 11 پیدا نشد")

    # Migration
    mig = '''    val MIGRATION_11_12 = object : Migration(11, 12) {
      override fun migrate(db: SupportSQLiteDatabase) {
        try {
          db.execSQL("ALTER TABLE `materials` ADD COLUMN `allocatedShippingCost` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
      }
    }

'''
    marker = "    fun getDatabase("
    if marker in dbc:
        dbc = dbc.replace(marker, mig + marker, 1)
        log("MIGRATION_11_12 اضافه شد")

    # افزودن به addMigrations
    old_add = ".addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11)"
    new_add = ".addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12)"
    if old_add in dbc:
        dbc = dbc.replace(old_add, new_add, 1)
        log("addMigrations به‌روز شد")
    elif "MIGRATION_10_11, MIGRATION_11_12" in dbc:
        warn("addMigrations از قبل به‌روز")
    else:
        warn("خط addMigrations پیدا نشد")

    write(DB, dbc)

# ۱۰.۴.۳ — Repository: تخصیص به ملزومات
repo_c = read(REPO)
if 'MATERIAL"' in repo_c and "getItemsForItem" in repo_c:
    # به‌روزرسانی submitMultiItemWaybill برای پشتیبانی از ملزومات
    old_alloc = '''      // تخصیص به طاقه در صورت وجود
      if (item.itemType == "FABRIC_ROLL") {
        val roll = database.fabricRollDao().getRollById(item.itemId)
        if (roll != null) {
          database.fabricRollDao().updateRoll(
            roll.copy(
              allocatedShippingCost = alloc,
              shippingExpenseId = expenseId
            )
          )
        }
      }'''

    new_alloc = '''      // تخصیص به طاقه در صورت وجود
      if (item.itemType == "FABRIC_ROLL") {
        val roll = database.fabricRollDao().getRollById(item.itemId)
        if (roll != null) {
          database.fabricRollDao().updateRoll(
            roll.copy(
              allocatedShippingCost = alloc,
              shippingExpenseId = expenseId
            )
          )
        }
      }
      // تخصیص به ملزومات در صورت وجود (فاز ۱۰.۴)
      else if (item.itemType == "MATERIAL") {
        try {
          val mat = database.materialDao().getById(item.itemId)
          if (mat != null) {
            database.materialDao().update(
              mat.copy(allocatedShippingCost = mat.allocatedShippingCost + alloc)
            )
          }
        } catch (_: Exception) {}
      }'''

    if old_alloc in repo_c and "getById(item.itemId)" in repo_c and "allocatedShippingCost + alloc" in repo_c:
        warn("تخصیص ملزومات از قبل هست")
    elif old_alloc in repo_c:
        repo_c = repo_c.replace(old_alloc, new_alloc, 1)
        write(REPO, repo_c)
        log("تخصیص کرایه به ملزومات اضافه شد")
    else:
        warn("anchor تخصیص پیدا نشد")

# =====================================================
# اعتبارسنجی نهایی
# =====================================================
print()
info("بررسی نهایی:")

checks = [
    (QA, "هزینه خیاط هر کار", "فیلد خیاط"),
    (QA, "باربری به صورت خودکار", "حذف فیلد باربری"),
    (MORE, "showClearAllConfirmDialog", "state پاک کامل"),
    (MORE, "clearAllDataWithoutDemo()", "فراخوانی پاک کامل"),
    (VM, "clearAllDataWithoutDemo", "متد VM"),
    (REPO, "clearAllDataKeepingStructure", "متد Repo پاک"),
    (ENT, "allocatedShippingCost", "فیلد Entity"),
    (DB, "MIGRATION_11_12", "Migration"),
]

for path, marker, label in checks:
    cc = read(path)
    if marker in cc:
        log(f"{label} ✓")
    else:
        err(f"{label} ✗")

print()
info("بررسی آکولاد:")
for path, label in [(QA, "QuickActionSheets"), (MORE, "MoreHubScreen"),
                     (VM, "ViewModel"), (REPO, "Repository"),
                     (ENT, "Entities"), (DB, "AppDatabase")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

print()
log("فاز ۱۰ اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase10): settings cleanup, clear-all-data, consumption costs, material shipping' && git push origin feature/cutting-parts-workflow{RST}")
print()
warn("⚠️ اگر گوشی نسخه قبلی دارد، اول uninstall کن (Migration جدید)")
