#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re
import subprocess
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
log  = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")

ROOT = Path(".")

# ============================================
# ۱. حذف اسکریپت‌های موقت
# ============================================
info("۱. حذف اسکریپت‌های پایتون موقت")

py_files = list(ROOT.glob("*.py"))
py_files = [f for f in py_files if f.name not in ["phase6.py"]]

deleted = 0
for f in py_files:
    try:
        f.unlink()
        deleted += 1
    except Exception as e:
        warn(f"حذف نشد: {f.name} — {e}")

log(f"{deleted} اسکریپت موقت حذف شد")

# ============================================
# ۲. حذف بکاپ برنچ (اختیاری)
# ============================================
info("۲. بررسی برنچ‌های بکاپ")

r = subprocess.run("git branch --list backup-before-cutting-parts", 
                   shell=True, capture_output=True, text=True)
if r.stdout.strip():
    warn("برنچ بکاپ وجود دارد — دستی می‌توانی حذف کنی")
    print("   git branch -D backup-before-cutting-parts")
else:
    log("برنچ بکاپ وجود ندارد")

# ============================================
# ۳. به‌روزرسانی PROJECT_STATUS.md
# ============================================
info("۳. به‌روزرسانی PROJECT_STATUS.md")

status_file = ROOT / "PROJECT_STATUS.md"
if status_file.exists():
    old_content = status_file.read_text(encoding="utf-8")
    
    new_section = """

---

## 📋 فازهای پیاده‌سازی‌شده (نسخه v10 → v11)

### ✅ فاز ۱: به‌روزرسانی قیمت بدون خرید
- دکمه 🔄 روی کارت طاقه → دیالوگ قیمت روز
- تاریخچه قیمت در `material_price_history`
- به‌روزرسانی خودکار بهای محصولات در جریان
- Migration 9 → 10

### ✅ فاز ۲: بارنامه و CRUD
- **۲.۱**: فرم بارنامه چندقلمی با تخصیص به هر ردیف
- **۲.۲**: CRUD کامل باربری + دسته‌بندی پارچه/محصول
- **۲.۳**: نمایش سهم کرایه روی کارت طاقه

### ✅ فاز ۳: مصرف طاقه و واحدها
- **۳.۱**: `UnitFormatter` برای نمایش خوانا (رفع ۲.۷۹۹۹۹۹۹۷)
- **۳.۲**: CRUD backend برای مصرف طاقه
- **۳.۳**: UI ویرایش/حذف مصرف + تبدیل خودکار kg ↔ m

### ✅ فاز ۴: رزرو و کارهای آماده
- **۴.۱**: سیستم رزرو سفارش با پرداخت نهایی و لغو
- **۴.۲**: صفحه «کارهای آماده» با ۳ تب (CUT/SEWING/READY)
- **۴.۳**: اتصال ناوبری + دکمه رزرو جدید

### ✅ فاز ۵: بهبود UI و مدیریت داده
- **۵.۱**: حذف «سلام مدیر محترم» + بزرگ‌تر کردن نام برند
- **۵.۲**: دیالوگ‌های پیشرفته مدیریت داده (حذف ۲ مرحله‌ای)
- **۵.۳**: اتصال دیالوگ‌ها به تنظیمات
- **۵.۴**: حذف بخش «نقش کاربری» (RBAC) از UI

### 🔒 Migrationها
- `MIGRATION_9_10`: به‌روزرسانی قیمت
- `MIGRATION_10_11`: فیلدهای فعلی قیمت

### 📁 فایل‌های جدید کلیدی
- `util/UnitFormatter.kt`
- `ui/dialogs/WaybillMultiItemForm.kt`
- `ui/dialogs/WaybillDetailsDialog.kt`
- `ui/dialogs/ManagementDialogs.kt`
- `ui/dialogs/RollUsageEditDialog.kt`
- `ui/dialogs/ReserveOrderDialog.kt`
- `ui/dialogs/DataManagementDialogs.kt`
- `ui/screens/ReadyGoodsScreen.kt`
"""
    
    if "## 📋 فازهای پیاده‌سازی‌شده" not in old_content:
        status_file.write_text(old_content + new_section, encoding="utf-8")
        log("PROJECT_STATUS.md به‌روز شد")
    else:
        warn("PROJECT_STATUS.md قبلاً به‌روز شده")
else:
    warn("PROJECT_STATUS.md پیدا نشد")

# ============================================
# ۴. چک نهایی فایل‌های کلیدی
# ============================================
info("۴. بررسی فایل‌های کلیدی")

BASE = Path("app/src/main/java/com/example")
key_files = [
    (BASE / "util/UnitFormatter.kt", "UnitFormatter"),
    (BASE / "ui/dialogs/WaybillMultiItemForm.kt", "Waybill Multi"),
    (BASE / "ui/dialogs/WaybillDetailsDialog.kt", "Waybill Details"),
    (BASE / "ui/dialogs/ManagementDialogs.kt", "Management"),
    (BASE / "ui/dialogs/RollUsageEditDialog.kt", "RollUsage Edit"),
    (BASE / "ui/dialogs/ReserveOrderDialog.kt", "Reserve Order"),
    (BASE / "ui/dialogs/DataManagementDialogs.kt", "Data Management"),
    (BASE / "ui/screens/ReadyGoodsScreen.kt", "Ready Goods"),
]

all_ok = True
for path, label in key_files:
    if path.exists():
        size = path.stat().st_size
        log(f"{label}: {size} bytes")
    else:
        err(f"{label}: پیدا نشد")
        all_ok = False

# ============================================
# ۵. بررسی Migrationهای AppDatabase
# ============================================
info("۵. بررسی Migrationها")

db_file = BASE / "data/database/AppDatabase.kt"
if db_file.exists():
    db_content = db_file.read_text(encoding="utf-8")
    
    # چک version
    v_match = re.search(r'version\s*=\s*(\d+)', db_content)
    if v_match:
        version = v_match.group(1)
        log(f"نسخه دیتابیس: {version}")
    else:
        warn("نسخه دیتابیس پیدا نشد")
    
    # چک migrationها
    migrations = ["MIGRATION_4_5", "MIGRATION_5_6", "MIGRATION_6_7", 
                  "MIGRATION_7_8", "MIGRATION_8_9", "MIGRATION_9_10",
                  "MIGRATION_10_11"]
    for m in migrations:
        if m in db_content:
            log(f"{m} ✓")
        else:
            warn(f"{m} ✗")

# ============================================
# ۶. Git status
# ============================================
print()
info("۶. Git status:")
r = subprocess.run("git status --short --untracked-files=no", 
                   shell=True, capture_output=True, text=True)
if r.stdout.strip():
    print(r.stdout)
else:
    log("Working tree تمیز است")

print()
log("فاز ۶ اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'chore(phase6): cleanup temp scripts and update docs' && git push origin feature/cutting-parts-workflow{RST}")
