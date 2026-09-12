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
ENT = BASE / "data/model/Entities.kt"
DB = BASE / "data/database/AppDatabase.kt"
REPO = BASE / "data/repository/ManufacturingRepository.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")


def find_class_constructor_bounds(content, class_name):
    """
    پیدا کردن محدوده constructor یک data class با شمارش پرانتز دقیق.
    برمی‌گرداند: (start_of_params, index_of_closing_paren)
    """
    m = re.search(r'data class\s+' + re.escape(class_name) + r'\s*\(', content)
    if not m:
        return None
    open_paren_idx = m.end() - 1
    depth = 0
    i = open_paren_idx
    while i < len(content):
        ch = content[i]
        if ch == '(':
            depth += 1
        elif ch == ')':
            depth -= 1
            if depth == 0:
                return (open_paren_idx + 1, i)
        i += 1
    return None


def has_field_in_class(content, class_name, field_name):
    """چک می‌کند آیا فیلد در constructor کلاس هست"""
    bounds = find_class_constructor_bounds(content, class_name)
    if not bounds:
        return False
    start, end = bounds
    body = content[start:end]
    return re.search(r'\bval\s+' + re.escape(field_name) + r'\b', body) is not None


# =========================================================
# ۱. افزودن فیلد به MaterialEntity با روش دقیق
# =========================================================
info("۱. افزودن فیلد allocatedShippingCost به MaterialEntity")

c = read(ENT)

if has_field_in_class(c, "MaterialEntity", "allocatedShippingCost"):
    warn("فیلد از قبل در MaterialEntity هست")
else:
    bounds = find_class_constructor_bounds(c, "MaterialEntity")
    if not bounds:
        err("MaterialEntity پیدا نشد")
        exit(1)
    
    start, end = bounds
    info(f"constructor MaterialEntity: خط {c[:start].count(chr(10)) + 1} تا {c[:end].count(chr(10)) + 1}")
    
    # پیدا کردن آخرین فیلد برای درج بعد از آن
    body = c[start:end]
    # پیدا کردن آخرین `,` که بعدش newline هست
    last_comma = body.rfind(",")
    
    if last_comma < 0:
        err("هیچ کامایی در constructor پیدا نشد")
        exit(1)
    
    # درج فیلد جدید بعد از آخرین فیلد
    field_def = "\n  val allocatedShippingCost: Long = 0L, // سهم کرایه باربری از بارنامه‌ها"
    insert_pos = start + last_comma + 1  # بعد از کاما
    new_content = c[:insert_pos] + field_def + c[insert_pos:]
    
    write(ENT, new_content)
    log("فیلد اضافه شد")
    
    # تأیید
    verify = read(ENT)
    if has_field_in_class(verify, "MaterialEntity", "allocatedShippingCost"):
        log("✓ تأیید: فیلد در MaterialEntity هست")
    else:
        err("✗ تأیید نشد!")


# =========================================================
# ۲. نسخه دیتابیس 11 → 12
# =========================================================
info("۲. نسخه دیتابیس 11 → 12")

c = read(DB)
if "version = 12," in c:
    warn("نسخه از قبل 12 است")
elif "version = 11," in c:
    c = c.replace("version = 11,", "version = 12,", 1)
    write(DB, c)
    log("نسخه: 11 → 12")
else:
    err("نسخه 11 پیدا نشد")


# =========================================================
# ۳. افزودن MIGRATION_11_12
# =========================================================
info("۳. افزودن MIGRATION_11_12")

c = read(DB)
if "MIGRATION_11_12" in c:
    warn("MIGRATION_11_12 از قبل هست")
else:
    mig = '''    val MIGRATION_11_12 = object : Migration(11, 12) {
      override fun migrate(db: SupportSQLiteDatabase) {
        try {
          db.execSQL("ALTER TABLE `materials` ADD COLUMN `allocatedShippingCost` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
      }
    }

'''
    marker = "    fun getDatabase("
    if marker in c:
        c = c.replace(marker, mig + marker, 1)
        write(DB, c)
        log("MIGRATION_11_12 اضافه شد")
    else:
        err("fun getDatabase پیدا نشد")

    # افزودن به addMigrations
    c = read(DB)
    old_add = ".addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11)"
    new_add = ".addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12)"
    if old_add in c:
        c = c.replace(old_add, new_add, 1)
        write(DB, c)
        log("addMigrations به‌روز شد")
    elif "MIGRATION_10_11, MIGRATION_11_12" in c:
        warn("addMigrations از قبل به‌روز")
    else:
        err("خط addMigrations پیدا نشد")


# =========================================================
# ۴. افزودن تخصیص به Repository
# =========================================================
info("۴. افزودن تخصیص به Repository")

c = read(REPO)
if 'allocatedShippingCost = mat.allocatedShippingCost + alloc' in c:
    warn("کد تخصیص از قبل هست")
else:
    # پیدا کردن anchor دقیق: بلوک تخصیص به طاقه
    anchor = '''      // تخصیص به طاقه در صورت وجود
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
    
    if anchor in c:
        new_block = anchor + '''
      // تخصیص به ملزومات در صورت وجود (فاز ۱۱)
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
        c = c.replace(anchor, new_block, 1)
        write(REPO, c)
        log("تخصیص به ملزومات اضافه شد")
    else:
        err("anchor تخصیص پیدا نشد — بررسی دستی")


# =========================================================
# ۵. چک نهایی
# =========================================================
print()
info("۵. بررسی نهایی:")

# آکولاد
for path, label in [(ENT, "Entities"), (DB, "AppDatabase"), (REPO, "Repository")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

# فیلد در MaterialEntity
cc = read(ENT)
if has_field_in_class(cc, "MaterialEntity", "allocatedShippingCost"):
    log("MaterialEntity.allocatedShippingCost ✓")
else:
    err("MaterialEntity.allocatedShippingCost ✗")

# Migration
cc = read(DB)
if "MIGRATION_11_12" in cc:
    log("MIGRATION_11_12 ✓")
else:
    err("MIGRATION_11_12 ✗")

if "version = 12," in cc:
    log("نسخه 12 ✓")
else:
    err("نسخه 12 ✗")

# Repository
cc = read(REPO)
if 'allocatedShippingCost = mat.allocatedShippingCost + alloc' in cc:
    log("تخصیص به ملزومات ✓")
else:
    err("تخصیص به ملزومات ✗")

print()
log("فاز ۱۱ اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase11): add material shipping allocation (proper)' && git push origin feature/cutting-parts-workflow{RST}")
