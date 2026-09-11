#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
log  = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")

ANALYTICS = Path("app/src/main/java/com/example/ui/screens/AnalyticsScreen.kt")
DASH = Path("app/src/main/java/com/example/ui/screens/DashboardScreen.kt")

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# ============================================
# ۱. Analytics: بهبود اعداد با ChartFormatter
# ============================================
info("۱. بهبود AnalyticsScreen")

c = read(ANALYTICS)
if not c:
    err("AnalyticsScreen پیدا نشد")
    exit(1)

# چک import
if "import com.example.util.ChartFormatter" not in c:
    idx = c.rfind("\nimport ")
    end = c.find("\n", idx + 1)
    c = c[:end] + "\nimport com.example.util.ChartFormatter" + c[end:]
    log("import ChartFormatter اضافه شد")

# جایگزینی اعداد بزرگ در متن‌ها با فرمت مختصر
replacements = [
    # اعداد در SummaryMetricPill ها
    (
        'Text(\n        text = label,\n        style = MaterialTheme.typography.labelSmall,\n        color = customColors.textMuted,\n        fontSize = 10.sp\n      )\n      Text(\n        text = value,',
        'Text(\n        text = label,\n        style = MaterialTheme.typography.labelSmall,\n        color = customColors.textMuted,\n        fontSize = 10.sp\n      )\n      Text(\n        text = value,'
    ),
]

# روش ساده‌تر: تنها درصدها را بهبود دهیم
if "profitMarginPercent.toInt()" in c:
    c = c.replace(
        "profitMarginPercent.toInt()}٪",
        "ChartFormatter.formatPercent(profitMarginPercent)}",
    )
    log("درصد سود در Profit Report بهبود یافت")

write(ANALYTICS, c)

# ============================================
# ۲. Dashboard: بررسی و بهبود
# ============================================
info("۲. بررسی DashboardScreen")

dc = read(DASH)
if "ChartFormatter" not in dc:
    if "import com.example.util.ChartFormatter" not in dc:
        idx = dc.rfind("\nimport ")
        end = dc.find("\n", idx + 1)
        dc = dc[:end] + "\nimport com.example.util.ChartFormatter" + dc[end:]
        write(DASH, dc)
        log("import ChartFormatter به Dashboard اضافه شد")
else:
    warn("Dashboard قبلاً ChartFormatter دارد")

# ============================================
# ۳. اعتبارسنجی
# ============================================
print()
info("۳. بررسی آکولاد:")
for path, label in [(ANALYTICS, "Analytics"), (DASH, "Dashboard")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

print()
log("فاز ۷.۲ اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase7.2): apply ChartFormatter in analytics' && git push origin feature/cutting-parts-workflow{RST}")
