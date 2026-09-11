#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
log  = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")

DASH = Path("app/src/main/java/com/example/ui/screens/DashboardScreen.kt")

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

if not DASH.exists():
    err("DashboardScreen.kt پیدا نشد")
    exit(1)

c = read(DASH)

# ============================================
# ۱. حذف «سلام، مدیر محترم» و بازطراحی Header
# ============================================
info("۱. بازطراحی header داشبورد")

old_header = '''        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = factorySettings.companyName.ifEmpty { "مدیریت اجرایی کارخانه پوشاک" },
              style = MaterialTheme.typography.labelSmall,
              color = customColors.textMuted,
              fontWeight = FontWeight.Medium,
              letterSpacing = 0.5.sp
            )
            // ✏️ Edit Header Settings
            SectionEditIcon(
              onClick = { viewModel.openSectionSettings(SectionSettingsTarget.HEADER) }
            )
          }
          Text(
            text = "سلام، مدیر محترم",
            style = MaterialTheme.typography.headlineSmall,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
        }'''

new_header = '''        Column(
          verticalArrangement = Arrangement.spacedBy(4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = factorySettings.companyName.ifEmpty { "تولیدی برتر" },
              style = MaterialTheme.typography.headlineSmall,
              color = customColors.textPrimary,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 0.3.sp
            )
            SectionEditIcon(
              onClick = { viewModel.openSectionSettings(SectionSettingsTarget.HEADER) }
            )
          }
          Text(
            text = "سیستم مدیریت اجرایی کارخانه پوشاک",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.textMuted,
            fontWeight = FontWeight.Medium
          )
        }'''

if old_header in c:
    c = c.replace(old_header, new_header, 1)
    log("header داشبورد بازطراحی شد")
elif '"سلام، مدیر محترم"' in c:
    # جایگزینی خط ساده
    c = c.replace(
        '''          Text(
            text = "سلام، مدیر محترم",
            style = MaterialTheme.typography.headlineSmall,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )''',
        '''          Text(
            text = "سیستم مدیریت اجرایی کارخانه پوشاک",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.textMuted,
            fontWeight = FontWeight.Medium
          )''',
        1
    )
    log("«سلام، مدیر محترم» حذف شد")
else:
    warn("الگوی «سلام مدیر» پیدا نشد — بررسی دستی")

# ============================================
# ۲. بزرگ‌تر کردن نام شرکت در fallback
# ============================================
info("۲. بهبود fallback نام شرکت")

if '"مدیریت اجرایی کارخانه پوشاک"' in c:
    c = c.replace(
        '"مدیریت اجرایی کارخانه پوشاک"',
        '"تولیدی برتر"',
        1
    )
    log("fallback به «تولیدی برتر» تغییر کرد")

# ============================================
# ۳. تغییر چیدمان header به وسط‌چین
# ============================================
info("۳. بررسی چیدمان header")

# این بخش را در مرحله ۱ انجام دادیم، پس فقط چک می‌کنیم
if "horizontalAlignment = Alignment.CenterHorizontally" in c:
    log("وسط‌چین شدن header اعمال شد")
else:
    warn("وسط‌چین شدن نیاز به بررسی دستی دارد")

# ============================================
# ۴. اعتبارسنجی
# ============================================
print()
info("۴. بررسی آکولاد:")
ob, cb = c.count("{"), c.count("}")
if ob == cb:
    log(f"DashboardScreen: {ob} متوازن")
else:
    err(f"DashboardScreen: {ob} vs {cb} نامتوازن!")

write(DASH, c)

print()
log("فاز ۵.۱ اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase5.1): dashboard header cleanup' && git push origin feature/cutting-parts-workflow{RST}")
