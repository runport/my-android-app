package com.example.util

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
