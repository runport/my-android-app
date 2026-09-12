# نقشه کار پروژه

**آخرین به‌روزرسانی:** 2026-09-12

این فایل تاریخچه کامل فازها و تغییرات اعمال‌شده است.
هر پچ جدید در انتها اضافه می‌شود.

---

## ✅ فازهای تکمیل‌شده

### فاز ۱۱ — تخصیص هزینه باربری به ملزومات
- افزودن `allocatedShippingCost` به `MaterialEntity`
- نسخه دیتابیس ۱۱ → ۱۲
- `MIGRATION_11_12`
- تخصیص در `ManufacturingRepository`

### فاز ۱۲ — بازطراحی تب‌های انبار
- ۲ ردیف (۳+۲) با آیکون و رنگ accent
- یکسان‌سازی border همه تب‌ها (2dp solid)

### فاز ۱۳ — یکسان‌سازی دکمه‌ها
- ساخت `ui/components/ManagementButtons.kt`
- جایگزینی در `InventoryScreen`, `ManagementDialogs`, `MoreHubScreen`
- `key = { it.id }` در ۱۶ `items()`
- `collectAsStateWithLifecycle` سراسری

### فاز ۱۴ — سیستم حافظه پروژه (همین حالا)
- ساخت `docs/ROADMAP.md`, `docs/CHANGELOG.md`, `docs/PROJECT_STATE.md`
- ساخت `.chat-bootstrap.md` برای چت‌های بعدی
- ساخت `scripts/update_state.sh`

---

## ⏳ فازهای در انتظار

### فاز ۱۵ — بازسازی فرم «ثبت کار آماده»
**هدف:** ساده‌سازی فرم — فقط طاقه، تعداد، قیمت

**فایل‌های درگیر:**
- `ui/dialogs/QuickActionSheets.kt` (QuickReadyGoodsForm، خطوط ۱۰۸۳-۱۶۸۹)
- `viewmodel/ManufacturingViewModel.kt` (submitMultiProductReadyGoods)
- `data/repository/ManufacturingRepository.kt`

**تغییرات:**
- حذف از UI: `میزان مصرف`, `واحد مصرف`, `هزینه خیاط‌کار`, `هزینه ملزومات`, `وزن هر عدد`
- حفظ در UI: `نام محصول`, `کد مدل`, `تعداد کار آماده`, `قیمت فروش`
- انتقال فیلدهای حذف‌شده به `QuickRollConsumeForm`

**وضعیت:** ⏳ در انتظار اجرای پچ ۲

### فاز ۱۶ — بهبود عملکرد (بخش ۲)
- `derivedStateOf` برای فیلترها
- جدا کردن `MoreSubSection` به composable مستقل
- `remember` در `DashboardScreen` (۱۲ collect) و `AnalyticsScreen` (۱۱ collect)

### فاز ۱۷ — گزارش‌ها
- P&L ماهانه
- گزارش تولید
- Export CSV

### فاز ۱۸ — Backup / Restore
- ذخیره JSON
- بازیابی از فایل
- WorkManager

### فاز ۱۹ — UX Polish
- Empty states
- Loading indicators
- Undo toast

---

## 📋 قواعد این فایل

- هر پچ جدید → یک بخش جدید در «فازهای تکمیل‌شده»
- وضعیت هر فاز: `✅` یا `⏳`
- هر بخش شامل: هدف، فایل‌ها، تغییرات، کامیت مرتبط
