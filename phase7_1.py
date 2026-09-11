#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
log  = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")

BASE = Path("app/src/main/java/com/example")
UTIL = BASE / "util"
CHART_HELPER = UTIL / "ChartFormatter.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# ============================================
# ۱. ساخت ChartFormatter.kt
# ============================================
info("۱. ساخت ChartFormatter.kt")

UTIL.mkdir(parents=True, exist_ok=True)

code = '''package com.example.util

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
'''
write(CHART_HELPER, code)
log("ChartFormatter.kt ساخته شد")

# ============================================
# ۲. بهبود KPI Cards در Dashboard
# ============================================
info("۲. بررسی KPI Cards در Dashboard")

DASH = BASE / "ui/screens/DashboardScreen.kt"
dash_content = read(DASH)

if "ChartFormatter" in dash_content:
    warn("قبلاً اعمال شده")
else:
    # اضافه کردن import
    if "import com.example.util.ChartFormatter" not in dash_content:
        idx = dash_content.rfind("\nimport ")
        end = dash_content.find("\n", idx + 1)
        dash_content = dash_content[:end] + "\nimport com.example.util.ChartFormatter" + dash_content[end:]
        log("import ChartFormatter به Dashboard اضافه شد")

# ============================================
# ۳. بهبود Analytics Screen
# ============================================
info("۳. بررسی AnalyticsScreen")

ANALYTICS = BASE / "ui/screens/AnalyticsScreen.kt"
analytics_content = read(ANALYTICS)

if analytics_content:
    if "ChartFormatter" in analytics_content:
        warn("قبلاً اعمال شده")
    else:
        # اضافه کردن import
        if "import com.example.util.ChartFormatter" not in analytics_content:
            idx = analytics_content.rfind("\nimport ")
            end = analytics_content.find("\n", idx + 1)
            analytics_content = analytics_content[:end] + "\nimport com.example.util.ChartFormatter" + analytics_content[end:]
            write(ANALYTICS, analytics_content)
            log("import ChartFormatter به Analytics اضافه شد")
else:
    warn("AnalyticsScreen پیدا نشد")

# ============================================
# ۴. اعتبارسنجی
# ============================================
print()
info("۴. بررسی آکولاد:")
for path, label in [(CHART_HELPER, "ChartFormatter"), (DASH, "Dashboard")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

if dash_content:
    write(DASH, dash_content)

print()
log("فاز ۷.۱ اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase7.1): chart formatting utilities' && git push origin feature/cutting-parts-workflow{RST}")
print()
warn("نکته: این فاز پایه ChartFormatter را می‌سازد.")
warn("اعمال در نمودارها در فاز ۷.۲ انجام می‌شود.")
