#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
فاز ۳.۱: نمایش واضح مصرف طاقه (kg/m) + تبدیل واحد
"""
import re
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
log  = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")

BASE = Path("app/src/main/java/com/example")
UTIL = BASE / "util"
QA = BASE / "ui/dialogs/QuickActionSheets.kt"
INV = BASE / "ui/screens/InventoryScreen.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# ============================================
# ۱. ساخت UnitFormatter.kt
# ============================================
info("۱. ساخت UnitFormatter.kt در util/")

UTIL.mkdir(parents=True, exist_ok=True)
unit_file = UTIL / "UnitFormatter.kt"

code = '''package com.example.util

import java.util.Locale

/**
 * فرمت‌بندی واحدها با نمایش خوانا و تبدیل خودکار
 * رفع مشکل «۲.۷۹۹۹۹۹۹۹۹۷ متر» → «۲.۸۰ متر»
 */
object UnitFormatter {

    /** نمایش عدد کیلوگرم با ۲ رقم اعشار */
    fun formatKg(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "۰.۰۰ کیلوگرم"
        return String.format(Locale.US, "%.2f کیلوگرم", value)
    }

    /** نمایش عدد متر با ۲ رقم اعشار */
    fun formatMeters(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "۰.۰۰ متر"
        return String.format(Locale.US, "%.2f متر", value)
    }

    /** نمایش همزمان kg و متر */
    fun formatDual(kg: Double, meters: Double): String {
        return "${formatKg(kg)} • ${formatMeters(meters)}"
    }

    /** نمایش گرم */
    fun formatGrams(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "۰ گرم"
        return String.format(Locale.US, "%.0f گرم", value)
    }

    /** تبدیل کیلو به متر با factor */
    fun kgToMeters(kg: Double, metersPerKg: Double): Double {
        if (metersPerKg <= 0.0) return 0.0
        return kg * metersPerKg
    }

    /** تبدیل متر به کیلو با factor */
    fun metersToKg(meters: Double, metersPerKg: Double): Double {
        if (metersPerKg <= 0.0) return 0.0
        return meters / metersPerKg
    }

    /** نمایش اختصار وزن */
    fun shortKg(value: Double): String {
        return String.format(Locale.US, "%.2f", value)
    }

    /** نمایش اختصار متر */
    fun shortMeters(value: Double): String {
        return String.format(Locale.US, "%.2f", value)
    }

    /** نمایش درصد */
    fun percent(value: Double): String {
        return String.format(Locale.US, "%.1f٪", value)
    }

    /** نمایش قیمت مختصر */
    fun price(value: Long): String {
        return String.format(Locale.US, "%,d تومان", value)
    }
}
'''
write(unit_file, code)
log("UnitFormatter.kt ساخته شد")

# ============================================
# ۲. رفع نمایش در QuickRollConsumeForm
# ============================================
info("۲. رفع نمایش اعداد در QuickRollConsumeForm")

c = read(QA)
modified = False

# جایگزینی String.format های طولانی با UnitFormatter
patterns = [
    # موجودی
    (
        '''String.format(java.util.Locale.US, "%.1f", roll.remainingMeters)''',
        '''UnitFormatter.shortMeters(roll.remainingMeters)'''
    ),
    (
        '''String.format(java.util.Locale.US, "%.1f", selectedRoll?.remainingMeters ?: 0.0)''',
        '''UnitFormatter.shortMeters(selectedRoll?.remainingMeters ?: 0.0)'''
    ),
    (
        '''String.format(java.util.Locale.US, "%.2f", metersUsed)''',
        '''UnitFormatter.shortMeters(metersUsed)'''
    ),
    (
        '''String.format(java.util.Locale.US, "%.2f", kgUsed)''',
        '''UnitFormatter.shortKg(kgUsed)'''
    ),
    (
        '''String.format(java.util.Locale.US, "%.2f", newRemainingMeters)''',
        '''UnitFormatter.shortMeters(newRemainingMeters)'''
    ),
    (
        '''String.format(java.util.Locale.US, "%.2f", newRemainingKg)''',
        '''UnitFormatter.shortKg(newRemainingKg)'''
    ),
    (
        '''String.format(java.util.Locale.US, "%.2f", metersPerGarment)''',
        '''UnitFormatter.shortMeters(metersPerGarment)'''
    ),
    (
        '''String.format(java.util.Locale.US, "%.1f", remainingMeters)''',
        '''UnitFormatter.shortMeters(remainingMeters)'''
    ),
]

for old, new in patterns:
    if old in c and new not in c:
        c = c.replace(old, new)
        modified = True

if modified:
    log("فرمت‌ها بهبود یافتند")
else:
    warn("هیچ الگویی جایگزین نشد")

# اضافه کردن import
if "import com.example.util.UnitFormatter" not in c:
    if "import com.example.util.PersianDateHelper" in c:
        c = c.replace(
            "import com.example.util.PersianDateHelper",
            "import com.example.util.PersianDateHelper\nimport com.example.util.UnitFormatter",
            1
        )
        log("import UnitFormatter اضافه شد")
    else:
        # جایگزین
        c = c.replace(
            "import com.example.viewmodel.ManufacturingViewModel",
            "import com.example.util.UnitFormatter\nimport com.example.viewmodel.ManufacturingViewModel",
            1
        )
        log("import UnitFormatter اضافه شد (جایگزین)")

write(QA, c)

# ============================================
# ۳. رفع نمایش در InventoryScreen (کارت طاقه)
# ============================================
info("۳. رفع نمایش اعداد در کارت طاقه")

c = read(INV)
modified = False

# اضافه کردن import
if "import com.example.util.UnitFormatter" not in c:
    if "import com.example.util.PersianDateHelper" in c:
        c = c.replace(
            "import com.example.util.PersianDateHelper",
            "import com.example.util.PersianDateHelper\nimport com.example.util.UnitFormatter",
            1
        )
        log("import UnitFormatter اضافه شد")
    else:
        # بعد از آخرین import
        idx = c.rfind("import ")
        end_of_line = c.find("\n", idx)
        if end_of_line > 0:
            c = c[:end_of_line] + "\nimport com.example.util.UnitFormatter" + c[end_of_line:]
            log("import UnitFormatter اضافه شد (آخر imports)")

# جایگزینی String.format ها
inv_patterns = [
    # نمایش موجودی
    (
        '''String.format(java.util.Locale.US, "%.1f", roll.remainingMeters)''',
        '''UnitFormatter.shortMeters(roll.remainingMeters)'''
    ),
    (
        '''String.format(java.util.Locale.US, "%.1f", remainingWeightKg)''',
        '''UnitFormatter.shortKg(remainingWeightKg)'''
    ),
    (
        '''String.format("%.1f", remainingWeightKg)''',
        '''UnitFormatter.shortKg(remainingWeightKg)'''
    ),
    (
        '''String.format("%.1f", totalWeightKg)''',
        '''UnitFormatter.shortKg(totalWeightKg)'''
    ),
]

for old, new in inv_patterns:
    if old in c and new not in c:
        c = c.replace(old, new)
        modified = True

if modified:
    log("فرمت‌ها در InventoryScreen بهبود یافتند")
else:
    warn("الگوهای InventoryScreen پیدا نشدند")

write(INV, c)

# ============================================
# ۴. رفع نمایش در MaterialEntity و FabricRollEntity (computed)
# ============================================
info("۴. بررسی Entity - نمایش اعشار")
entities = BASE / "data/model/Entities.kt"
ec = read(entities)

# FabricRollEntity.dualStockDisplay
old_dual = '''  val dualStockDisplay: String get() = "${String.format(java.util.Locale.US, "%.2f", currentWeightKg)} KG / ${String.format(java.util.Locale.US, "%.2f", remainingMeters)} M"'''
new_dual = '''  val dualStockDisplay: String get() = "⚖️ ${String.format(java.util.Locale.US, "%.2f", currentWeightKg)} کیلو • 📏 ${String.format(java.util.Locale.US, "%.2f", remainingMeters)} متر"'''

if old_dual in ec:
    ec = ec.replace(old_dual, new_dual, 1)
    write(entities, ec)
    log("dualStockDisplay بهبود یافت")
else:
    warn("dualStockDisplay پیدا نشد (شاید قبلاً تغییر کرده)")

# ============================================
# ۵. اعتبارسنجی
# ============================================
print()
info("۵. بررسی آکولاد:")
for path, label in [(QA, "QuickActionSheets"), (INV, "InventoryScreen"),
                    (unit_file, "UnitFormatter"), (entities, "Entities")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

print()
log("زیرفاز 3.1 اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase3.1): clear kg/m display with UnitFormatter' && git push origin feature/cutting-parts-workflow{RST}")
