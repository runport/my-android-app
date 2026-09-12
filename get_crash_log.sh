#!/bin/bash
# get_crash_log.sh - دریافت لاگ کرش برنامه

PACKAGE="com.example"  # مطمئن شوید نام پکیج درست است
LOG_FILE="crash_log.txt"

echo "در حال پاک کردن لاگ‌های قبلی..."
adb logcat -c 2>/dev/null || { echo "خطا: adb پیدا نشد یا دستگاه وصل نیست"; exit 1; }

echo "لطفاً حالا در برنامه، روی بخش 'تنظیمات' -> 'کارهای آماده' کلیک کنید تا کرش کند..."
echo "منتظر می‌مانم..."

# منتظر می‌مانیم تا فرآیند برنامه متوقف شود
adb wait-for-device
while adb shell pidof "$PACKAGE" > /dev/null 2>&1; do
    sleep 0.5
done

echo "برنامه متوقف شد. در حال جمع‌آوری لاگ..."
adb logcat -d > "$LOG_FILE"

echo "لاگ در فایل '$LOG_FILE' ذخیره شد."
echo "--------------------------------------------------"
echo "خطوط مربوط به خطا (FATAL EXCEPTION):"
grep -A 30 "FATAL EXCEPTION" "$LOG_FILE" | head -60
echo "--------------------------------------------------"
echo "لطفاً خروجی بالا را کپی کنید و برای من بفرستید."
