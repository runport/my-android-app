package com.example.data.model

import com.example.util.PersianDateHelper

enum class TimeRangeMode(
  val title: String,
  val salesTitle: String
) {
  TODAY_24H("۲۴ ساعت اخیر", "فروش — ۲۴ ساعت اخیر"),
  LAST_MONTH("ماه اخیر", "فروش — ماه اخیر"),
  LAST_YEAR("سال اخیر", "فروش — سال اخیر"),
  CUSTOM("بازه سفارشی", "فروش — بازه سفارشی");

  companion object {
    fun fromPeriodFilter(filter: PeriodFilter): TimeRangeMode = when (filter) {
      PeriodFilter.TODAY -> TODAY_24H
      PeriodFilter.MONTH -> LAST_MONTH
      PeriodFilter.YEAR -> LAST_YEAR
      PeriodFilter.CUSTOM -> CUSTOM
    }
  }
}

data class AppTimeRangeState(
  val mode: TimeRangeMode = TimeRangeMode.TODAY_24H,
  val startTimestamp: Long = System.currentTimeMillis() - 24 * 3600 * 1000L,
  val endTimestamp: Long = System.currentTimeMillis(),
  val customFromPersian: String = "",
  val customToPersian: String = ""
) {
  val title: String
    get() = when (mode) {
      TimeRangeMode.TODAY_24H -> "۲۴ ساعت اخیر"
      TimeRangeMode.LAST_MONTH -> "ماه اخیر"
      TimeRangeMode.LAST_YEAR -> "سال اخیر"
      TimeRangeMode.CUSTOM -> {
        if (customFromPersian.isNotEmpty() && customToPersian.isNotEmpty()) {
          "از تاریخ $customFromPersian تا تاریخ $customToPersian"
        } else {
          "بازه سفارشی"
        }
      }
    }

  val salesTitle: String
    get() = when (mode) {
      TimeRangeMode.TODAY_24H -> "فروش — ۲۴ ساعت اخیر"
      TimeRangeMode.LAST_MONTH -> "فروش — ماه اخیر"
      TimeRangeMode.LAST_YEAR -> "فروش — سال اخیر"
      TimeRangeMode.CUSTOM -> {
        if (customFromPersian.isNotEmpty() && customToPersian.isNotEmpty()) {
          "فروش — از تاریخ $customFromPersian تا تاریخ $customToPersian"
        } else {
          "فروش — بازه سفارشی"
        }
      }
    }

  fun getChartTitle(metricTitle: String = "فروش و سود خالص"): String {
    return when (mode) {
      TimeRangeMode.TODAY_24H -> "$metricTitle — ۲۴ ساعت اخیر"
      TimeRangeMode.LAST_MONTH -> "$metricTitle — ماه اخیر"
      TimeRangeMode.LAST_YEAR -> "$metricTitle — سال اخیر"
      TimeRangeMode.CUSTOM -> {
        if (customFromPersian.isNotEmpty() && customToPersian.isNotEmpty()) {
          "$metricTitle — از تاریخ $customFromPersian تا تاریخ $customToPersian"
        } else {
          "$metricTitle — بازه سفارشی"
        }
      }
    }
  }

  fun isInRange(timestamp: Long): Boolean {
    return timestamp in startTimestamp..endTimestamp
  }

  companion object {
    fun createToday24Hours(): AppTimeRangeState {
      val now = System.currentTimeMillis()
      return AppTimeRangeState(
        mode = TimeRangeMode.TODAY_24H,
        startTimestamp = now - 24 * 3600 * 1000L,
        endTimestamp = now
      )
    }

    fun createLastMonth(): AppTimeRangeState {
      val now = System.currentTimeMillis()
      return AppTimeRangeState(
        mode = TimeRangeMode.LAST_MONTH,
        startTimestamp = now - 30L * 24 * 3600 * 1000L,
        endTimestamp = now
      )
    }

    fun createLastYear(): AppTimeRangeState {
      val now = System.currentTimeMillis()
      return AppTimeRangeState(
        mode = TimeRangeMode.LAST_YEAR,
        startTimestamp = now - 365L * 24 * 3600 * 1000L,
        endTimestamp = now
      )
    }

    fun createCustom(fromPersian: String, toPersian: String, fromTime: Long, toTime: Long): AppTimeRangeState {
      return AppTimeRangeState(
        mode = TimeRangeMode.CUSTOM,
        startTimestamp = fromTime.coerceAtMost(toTime),
        endTimestamp = toTime.coerceAtLeast(fromTime),
        customFromPersian = fromPersian,
        customToPersian = toPersian
      )
    }
  }
}

data class CombinedChartPoint(
  val label: String,
  val sales: Long,
  val profit: Long,
  val formattedSales: String,
  val formattedProfit: String,
  val profitMarginPercent: Int = if (sales > 0) ((profit.toDouble() / sales) * 100).toInt() else 0,
  val timestamp: Long = 0L
)

enum class ChartDisplayMetric(val title: String) {
  COMBINED("هر دو (فروش و سود خالص)"),
  SALES_ONLY("فقط فروش"),
  PROFIT_ONLY("فقط سود خالص")
}
