package com.example.util

import java.util.Locale

/**
 * Centralized formatting helper for inventory, fabric rolls, cutting parts, and weights/measures.
 * Strictly prevents unreadable floating-point displays (e.g. 2.7999999997)
 * and always pairs numeric values with their exact physical measurement units.
 */
object NumberFormatHelper {

  fun formatKg(value: Double): String {
    return String.format(Locale.US, "%.2f KG", value)
  }

  fun formatMeters(value: Double): String {
    return String.format(Locale.US, "%.2f M", value)
  }

  fun formatGrams(value: Double): String {
    return String.format(Locale.US, "%.1f G", value)
  }

  fun formatPcs(value: Int): String {
    return "$value PCS"
  }

  fun formatDecimal(value: Double, decimals: Int = 2): String {
    return String.format(Locale.US, "%.${decimals}f", value)
  }

  fun formatDualStock(weightKg: Double, meters: Double): String {
    return "${formatDecimal(weightKg)} KG / ${formatDecimal(meters)} M"
  }

  fun formatPercent(value: Double): String {
    return "${String.format(Locale.US, "%.1f", value)}٪"
  }

  fun formatAverageWeight(weightKgUsed: Double, quantity: Int): String {
    if (quantity <= 0) return "0.000 KG/PCS"
    val avgKg = weightKgUsed / quantity
    val avgGrams = avgKg * 1000.0
    return if (avgKg < 1.0) {
      String.format(Locale.US, "%.0f G/PCS", avgGrams)
    } else {
      String.format(Locale.US, "%.3f KG/PCS", avgKg)
    }
  }
}
