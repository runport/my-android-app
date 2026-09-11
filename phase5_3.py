#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
log  = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")

MORE = Path("app/src/main/java/com/example/ui/screens/MoreHubScreen.kt")

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

if not MORE.exists():
    err("MoreHubScreen.kt پیدا نشد")
    exit(1)

c = read(MORE)

# ============================================
# ۱. اضافه کردن import های لازم
# ============================================
info("۱. اضافه کردن imports")

imports_needed = [
    "import com.example.ui.dialogs.DeleteAllDataConfirmDialog",
    "import com.example.ui.dialogs.RestoreFromFileDialog",
]

for imp in imports_needed:
    if imp not in c:
        idx = c.rfind("\nimport ")
        end = c.find("\n", idx + 1)
        c = c[:end] + "\n" + imp + c[end:]
        log(f"✓ {imp.split('.')[-1]} اضافه شد")
    else:
        warn(f"{imp.split('.')[-1]} از قبل هست")

# ============================================
# ۲. اضافه کردن state در showResetConfirmDialog area
# ============================================
info("۲. بررسی state های موجود")

# چک کنیم showResetConfirmDialog هست
if "showResetConfirmDialog" not in c:
    err("showResetConfirmDialog در MoreHubScreen پیدا نشد")
    exit(1)
else:
    log("showResetConfirmDialog موجود است")

# ============================================
# ۳. جایگزینی AlertDialog حذف داده با دیالوگ جدید
# ============================================
info("۳. جایگزینی دیالوگ حذف داده")

# پیدا کردن AlertDialog مربوط به reset
old_reset_pattern = re.compile(
    r'if \(showResetConfirmDialog\) \{[\s\S]*?\n  \}',
    re.MULTILINE
)

# جستجو
matches = old_reset_pattern.findall(c)
if not matches:
    warn("الگوی AlertDialog reset پیدا نشد — بررسی دستی")
else:
    # جایگزینی اولین match
    old_block = matches[0]
    new_block = '''if (showResetConfirmDialog) {
    DeleteAllDataConfirmDialog(
      onConfirm = {
        viewModel.resetToDemoData()
        showResetConfirmDialog = false
      },
      onDismiss = { showResetConfirmDialog = false }
    )
  }'''
    c = c.replace(old_block, new_block, 1)
    log("دیالوگ حذف داده جایگزین شد")

# ============================================
# ۴. جایگزینی AlertDialog بازیابی با دیالوگ جدید
# ============================================
info("۴. جایگزینی دیالوگ بازیابی")

old_restore_pattern = re.compile(
    r'if \(showRestoreBackupDialog\) \{[\s\S]*?\n  \}',
    re.MULTILINE
)

matches = old_restore_pattern.findall(c)
if not matches:
    warn("الگوی AlertDialog restore پیدا نشد — بررسی دستی")
else:
    old_block = matches[0]
    new_block = '''if (showRestoreBackupDialog) {
    RestoreFromFileDialog(
      onConfirm = { jsonText ->
        viewModel.restoreBackupData(jsonText)
        showRestoreBackupDialog = false
      },
      onDismiss = { showRestoreBackupDialog = false }
    )
  }'''
    c = c.replace(old_block, new_block, 1)
    log("دیالوگ بازیابی جایگزین شد")

# ============================================
# ۵. اعتبارسنجی
# ============================================
print()
info("۵. بررسی آکولاد:")
ob, cb = c.count("{"), c.count("}")
if ob == cb:
    log(f"MoreHubScreen: {ob} متوازن")
else:
    err(f"MoreHubScreen: {ob} vs {cb} نامتوازن!")

write(MORE, c)

print()
log("فاز ۵.۳ اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase5.3): connect data dialogs to settings' && git push origin feature/cutting-parts-workflow{RST}")
