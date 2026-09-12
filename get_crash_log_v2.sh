#!/bin/bash
# get_crash_log_v2.sh — capture crash with larger buffer

PACKAGE="com.example"           # اگر نام پکیج فرق دارد، اصلاح کنید
LOGFILE="crash_log_v2.txt"

# 1) بزرگ کردن بافر logcat برای نگه‌داشتن لاگ‌های بیشتر
adb logcat -G 16M 2>/dev/null

# 2) پاک کردن لاگ قدیمی
adb logcat -c 2>/dev/null

echo "=========================================="
echo "  ۱. برنامه را باز کنید"
echo "  ۲. به بخش تنظیمات → کارهای آماده بروید"
echo "  ۳. روی گزینه‌ای که کرش می‌کند بزنید"
echo "  ۴. بعد از کرش، اینجا Enter بزنید"
echo "=========================================="
read -p "بعد از کرش Enter بزنید..."

# 3) ذخیره‌ی کل لاگ
adb logcat -d > "$LOGFILE"

# 4) فیلترهای مختلف
echo
echo "=== FATAL EXCEPTION ==="
grep -A 40 "FATAL EXCEPTION" "$LOGFILE" | head -80

echo
echo "=== AndroidRuntime ==="
grep -A 40 "AndroidRuntime" "$LOGFILE" | head -80

echo
echo "=== خطوط حاوی $PACKAGE ==="
grep "$PACKAGE" "$LOGFILE" | tail -60

echo
echo "=== ANR ==="
grep -A 20 "ANR in" "$LOGFILE" | head -40

echo
echo "=== خطوط آخر (۲۰ خط) ==="
tail -20 "$LOGFILE"

echo
echo "لاگ کامل در فایل: $LOGFILE"
echo "لطفاً کل خروجی بالا را کپی کنید و بفرستید."
