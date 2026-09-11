package com.example.util

import java.util.Locale
import kotlin.math.abs

/**
 * فرمت‌بندی اعداد برای نمودارها و KPI
 * رفع مشکل نمایش اعداد بزرگ روی نمودار
 */
object ChartFormatter {

    /**
     * فرمت مبلغ به صورت مختصر برای نمایش روی نمودار
     * مثال: ۱۲,۵۰۰,۰۰۰ → "۱۲.۵ م"
     */
    fun formatCompactCurrency(amount: Long): String {
        if (amount == 0L) return "۰"
        val absAmount = abs(amount)
        return when {
            absAmount >= 1_000_000_000L -> {
                val v = amount / 1_000_000_000.0
                String.format(Locale.US, "%.1f میلیارد", v)
            }
            absAmount >= 1_000_000L -> {
                val v = amount / 1_000_000.0
                String.format(Locale.US, "%.1f م", v)
            }
            absAmount >= 1_000L -> {
                val v = amount / 1_000.0
                String.format(Locale.US, "%.0f هزار", v)
            }
            else -> amount.toString()
        }
    }

    /**
     * فرمت عدد بزرگ به صورت مختصر
     */
    fun formatCompactNumber(value: Long): String {
        if (value == 0L) return "۰"
        val absValue = abs(value)
        return when {
            absValue >= 1_000_000_000L -> String.format(Locale.US, "%.1fB", value / 1_000_000_000.0)
            absValue >= 1_000_000L -> String.format(Locale.US, "%.1fM", value / 1_000_000.0)
            absValue >= 1_000L -> String.format(Locale.US, "%.0fK", value / 1_000.0)
            else -> value.toString()
        }
    }

    /**
     * فرمت محور Y نمودار برای نمایش
     */
    fun formatAxisLabel(value: Float): String {
        val v = value.toLong()
        return when {
            abs(v) >= 1_000_000L -> String.format(Locale.US, "%.0fM", value / 1_000_000f)
            abs(v) >= 1_000L -> String.format(Locale.US, "%.0fK", value / 1_000f)
            else -> v.toString()
        }
    }

    /**
     * محاسبه ماکسیمم مناسب برای محور Y
     * گرد کردن به نزدیک‌ترین عدد قابل خواندن
     */
    fun roundMaxValue(rawMax: Float): Float {
        if (rawMax <= 0f) return 100f
        val magnitude = when {
            rawMax >= 1_000_000f -> 100_000f
            rawMax >= 100_000f -> 10_000f
            rawMax >= 10_000f -> 1_000f
            rawMax >= 1_000f -> 100f
            rawMax >= 100f -> 10f
            else -> 1f
        }
        return ((rawMax / magnitude).toInt() + 1) * magnitude
    }

    /**
     * درصد نمایش
     */
    fun formatPercent(value: Double): String {
        return String.format(Locale.US, "%.1f٪", value)
    }

    /**
     * نمایش درصد با علامت +/- برای رشد
     */
    fun formatGrowthPercent(value: Double): String {
        val sign = if (value >= 0) "+" else ""
        return "$sign${String.format(Locale.US, "%.1f", value)}٪"
    }
}
