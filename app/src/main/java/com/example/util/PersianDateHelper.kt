package com.example.util

import java.util.Calendar

/**
 * Persian (Jalali) date helper for automated date generation and period filtering
 */
object PersianDateHelper {

  val persianMonths = listOf(
    "فروردین", "اردیبهشت", "خرداد",
    "تیر", "مرداد", "شهریور",
    "مهر", "آبان", "آذر",
    "دی", "بهمن", "اسفند"
  )

  /**
   * Returns current Persian date formatted like "۱۵ اسفند ۱۴۰۴"
   */
  fun getTodayPersianDate(): String {
    return getPersianDate(System.currentTimeMillis())
  }

  fun getCurrentPersianDate(): String {
    return getTodayPersianDate()
  }

  fun getCurrentTime(): String {
    val cal = Calendar.getInstance()
    val h = cal.get(Calendar.HOUR_OF_DAY)
    val m = cal.get(Calendar.MINUTE)
    return String.format(java.util.Locale.US, "%02d:%02d", h, m)
  }

  /**
   * Returns a short Persian date like "۱۵ اسفند"
   */
  fun getTodayShortPersianDate(): String {
    return getShortPersianDate(System.currentTimeMillis())
  }

  fun getCurrentJalaliDate(): Triple<Int, Int, Int> {
    val cal = Calendar.getInstance()
    val gy = cal.get(Calendar.YEAR)
    val gm = cal.get(Calendar.MONTH) + 1
    val gd = cal.get(Calendar.DAY_OF_MONTH)
    return gregorianToJalali(gy, gm, gd)
  }

  fun getPersianDate(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val gy = cal.get(Calendar.YEAR)
    val gm = cal.get(Calendar.MONTH) + 1
    val gd = cal.get(Calendar.DAY_OF_MONTH)

    val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
    val monthName = persianMonths.getOrElse(jm - 1) { "اسفند" }
    return "${toPersianDigits(jd)} $monthName ${toPersianDigits(jy)}"
  }

  fun getShortPersianDate(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val gy = cal.get(Calendar.YEAR)
    val gm = cal.get(Calendar.MONTH) + 1
    val gd = cal.get(Calendar.DAY_OF_MONTH)

    val (_, jm, jd) = gregorianToJalali(gy, gm, gd)
    val monthName = persianMonths.getOrElse(jm - 1) { "اسفند" }
    return "${toPersianDigits(jd)} $monthName"
  }

  fun toEnglishDigits(str: String): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val sb = StringBuilder()
    for (ch in str) {
      val pIdx = persianDigits.indexOf(ch)
      val aIdx = arabicDigits.indexOf(ch)
      when {
        pIdx != -1 -> sb.append(pIdx)
        aIdx != -1 -> sb.append(aIdx)
        else -> sb.append(ch)
      }
    }
    return sb.toString()
  }

  fun jalaliToTimestamp(jy: Int, jm: Int, jd: Int): Long {
    val (gy, gm, gd) = jalaliToGregorian(jy, jm, jd)
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, gy)
    cal.set(Calendar.MONTH, gm - 1)
    cal.set(Calendar.DAY_OF_MONTH, gd)
    cal.set(Calendar.HOUR_OF_DAY, 12)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
  }

  fun parseDateToTimestamp(dateStr: String): Long {
    val clean = toEnglishDigits(dateStr.trim())
    val now = System.currentTimeMillis()
    if (clean.contains("امروز")) return now
    if (clean.contains("دیروز")) return now - 24 * 3600 * 1000L

    // Check if format like "1403/12/14" or "1404-12-14"
    val slashParts = clean.split("/", "-")
    if (slashParts.size == 3) {
      val y = slashParts[0].filter { it.isDigit() }.toIntOrNull() ?: 1404
      val m = slashParts[1].filter { it.isDigit() }.toIntOrNull() ?: 1
      val d = slashParts[2].filter { it.isDigit() }.toIntOrNull() ?: 1
      return jalaliToTimestamp(y, m, d)
    }

    // Check if contains month name, e.g. "۱۴ اسفند" or "۱۴ اسفند ۱۴۰۴"
    val (curJy, curJm, curJd) = getCurrentJalaliDate()
    for (mIdx in persianMonths.indices) {
      val mName = persianMonths[mIdx]
      if (clean.contains(mName)) {
        val words = clean.split(" ")
        val digits = words.mapNotNull { it.filter { c -> c.isDigit() }.toIntOrNull() }
        val day = digits.firstOrNull() ?: curJd
        val year = if (digits.size >= 2) digits[1] else curJy
        return jalaliToTimestamp(year, mIdx + 1, day)
      }
    }

    // Default fallback
    return now
  }

  fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
    val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
    val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    val jy2 = jy - 979
    val jm2 = jm - 1
    val jd2 = jd - 1

    var jDayNo = 365 * jy2 + (jy2 / 33) * 8 + (jy2 % 33 + 3) / 4
    for (i in 0 until jm2) {
      jDayNo += jDaysInMonth[i]
    }
    jDayNo += jd2

    var gDayNo = jDayNo + 79

    var gyCalculated = 1600 + 400 * (gDayNo / 146097)
    gDayNo %= 146097

    var leap = true
    if (gDayNo >= 36525) {
      gDayNo--
      gyCalculated += 100 * (gDayNo / 36524)
      gDayNo %= 36524
      if (gDayNo >= 365) {
        gDayNo++
      } else {
        leap = false
      }
    }

    gyCalculated += 4 * (gDayNo / 1461)
    gDayNo %= 1461

    if (gDayNo >= 366) {
      leap = false
      gDayNo--
      gyCalculated += gDayNo / 365
      gDayNo %= 365
    }

    var gm = 0
    while (gm < 12) {
      val dim = if (gm == 1 && ((gyCalculated % 4 == 0 && gyCalculated % 100 != 0) || (gyCalculated % 400 == 0))) 29 else gDaysInMonth[gm]
      if (gDayNo < dim) break
      gDayNo -= dim
      gm++
    }
    val gd = gDayNo + 1
    return Triple(gyCalculated, gm + 1, gd)
  }

  fun toPersianDigits(number: Int): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val str = number.toString()
    val sb = StringBuilder()
    for (ch in str) {
      if (ch in '0'..'9') {
        sb.append(persianDigits[ch - '0'])
      } else {
        sb.append(ch)
      }
    }
    return sb.toString()
  }

  fun toPersianDigits(str: String): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val sb = StringBuilder()
    for (ch in str) {
      if (ch in '0'..'9') {
        sb.append(persianDigits[ch - '0'])
      } else {
        sb.append(ch)
      }
    }
    return sb.toString()
  }

  private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
    val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

    var gy2 = gy - 1600
    var gm2 = gm - 1
    var gd2 = gd - 1

    var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400

    for (i in 0 until gm2) {
      gDayNo += gDaysInMonth[i]
    }
    if (gm2 > 1 && ((gy2 % 4 == 0 && gy2 % 100 != 0) || (gy2 % 400 == 0))) {
      gDayNo++
    }
    gDayNo += gd2

    var jDayNo = gDayNo - 79

    val jNp = jDayNo / 12053
    jDayNo %= 12053

    var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
    jDayNo %= 1461

    if (jDayNo >= 366) {
      jy += (jDayNo - 1) / 365
      jDayNo = (jDayNo - 1) % 365
    }

    var jm = 0
    for (i in 0..11) {
      if (jDayNo < jDaysInMonth[i]) {
        jm = i + 1
        break
      }
      jDayNo -= jDaysInMonth[i]
    }
    val jd = jDayNo + 1

    return Triple(jy, jm, jd)
  }
}
