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

# ==========================================
# ۱. پیدا و حذف بلوک «نقش کاربری فعال (RBAC)»
# ==========================================
info("۱. حذف بلوک نقش کاربری از تنظیمات")

# پیدا کردن بلوک کامل «نقش کاربری فعال»
# با الگوی منظم
pattern = re.compile(
    r'(\s*// 0\. User Role \(RBAC\) Switcher\s*\n\s*item \{[\s\S]*?\n\s*\}\s*\n\s*)(// 1\. Theme)',
    re.MULTILINE
)

match = pattern.search(c)
if match:
    # حذف بلوک (فقط گروه اول)
    c = c[:match.start()] + "\n        " + match.group(2) + c[match.end():]
    log("بلوک RBAC حذف شد")
else:
    # روش جایگزین: پیدا کردن با anchor
    anchor_start = "// 0. User Role (RBAC) Switcher"
    anchor_end = "// 1. Theme Configuration Setting"
    
    if anchor_start in c and anchor_end in c:
        start_idx = c.find(anchor_start)
        end_idx = c.find(anchor_end)
        if start_idx < end_idx:
            # از ابتدای خط شروع تا ابتدای خط end
            line_start = c.rfind("\n", 0, start_idx) + 1
            line_end = c.rfind("\n", 0, end_idx) + 1
            c = c[:line_start] + c[line_end:]
            log("بلوک RBAC حذف شد (روش جایگزین)")
        else:
            warn("ترتیب anchor ها اشتباه است")
    else:
        warn("anchor بلوک RBAC پیدا نشد — ممکن است از قبل حذف شده باشد")

# ==========================================
# ۲. حذف importهای مربوط به UserRole (اگر استفاده دیگری ندارند)
# ==========================================
info("۲. بررسی importها")

# UserRole مدل است، در UI نباید باقی بماند
if "UserRole.values()" in c or "currentUserRole" in c:
    warn("هنوز ارجاع به UserRole وجود دارد")
else:
    log("ارجاع UserRole در UI پاک شد")

# ==========================================
# ۳. اعتبارسنجی
# ==========================================
print()
info("۳. بررسی آکولاد:")
ob, cb = c.count("{"), c.count("}")
if ob == cb:
    log(f"MoreHubScreen: {ob} متوازن")
else:
    err(f"MoreHubScreen: {ob} vs {cb} نامتوازن!")

write(MORE, c)

print()
log("فاز ۵.۴ اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase5.4): remove user role selector from settings' && git push origin feature/cutting-parts-workflow{RST}")
