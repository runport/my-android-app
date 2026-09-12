#!/bin/bash
# get_crash_final.sh - capture crash with CORRECT package name

PACKAGE="com.aistudio.manufacturing.vrtqxk"
LOGFILE="crash_final.txt"

# ۱) بزرگ کردن بافر logcat
adb logcat -G 16M 2>/dev/null

# ۲) پاک کردن لاگ قدیمی
adb logcat -c 2>/dev/null

# ۳) چک کردن اینکه پکیج نصب است
if ! adb shell pm list packages | grep -q "$PACKAGE"; then
    echo "✗ Package '$PACKAGE' not installed!"
    echo "Installed com.aistudio packages:"
    adb shell pm list packages | grep aistudio
    exit 1
fi

echo "=========================================="
echo "  ۱. برنامه را باز کنید"
echo "  ۲. به بخش تنظیمات → کارهای آماده بروید"
echo "  ۳. روی گزینه‌ای که کرش می‌کند بزنید"
echo "  ۴. بعد از کرش، اینجا Enter بزنید"
echo "=========================================="
read -p "بعد از کرش Enter بزنید..."

# ۴) ذخیره‌ی کل لاگ
adb logcat -d > "$LOGFILE"

echo
echo "=== FATAL EXCEPTION ==="
grep -A 50 "FATAL EXCEPTION" "$LOGFILE" | head -100

echo
echo "=== AndroidRuntime ==="
grep -B2 -A 50 "AndroidRuntime" "$LOGFILE" | head -100

echo
echo "=== هر خط حاوی نام پکیج شما ==="
grep "$PACKAGE" "$LOGFILE" | tail -80

echo
echo "=== خطوط آخر ==="
tail -40 "$LOGFILE"

echo
echo "لاگ کامل: $LOGFILE"
