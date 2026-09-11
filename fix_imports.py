#!/usr/bin/env python3
from pathlib import Path

QA = Path("app/src/main/java/com/example/ui/dialogs/QuickActionSheets.kt")
G, R, Y, RST = "\033[92m", "\033[91m", "\033[93m", "\033[0m"

c = QA.read_text(encoding="utf-8")

# imports مورد نیاز
needed = [
    ("androidx.compose.material3.AlertDialog",
     "import androidx.compose.material3.AlertDialog"),
    ("androidx.compose.material3.TextButton",
     "import androidx.compose.material3.TextButton"),
]

# نقطه درج: بعد از آخرین import موجود
lines = c.split("\n")
last_import_idx = -1
for i, line in enumerate(lines):
    if line.startswith("import "):
        last_import_idx = i

if last_import_idx < 0:
    print(f"{R}✗ هیچ import پیدا نشد{RST}")
    exit(1)

added = []
for name, imp in needed:
    if imp in c:
        print(f"{Y}⚠{RST} {name} از قبل موجود")
    else:
        # بین importها درج کن (ترتیب الفبایی مهم نیست)
        lines.insert(last_import_idx + 1, imp)
        last_import_idx += 1
        added.append(name)
        print(f"{G}✓{RST} {name} اضافه شد")

if added:
    QA.write_text("\n".join(lines), encoding="utf-8")

# چک نهایی
final = QA.read_text(encoding="utf-8")
ob, cb = final.count("{"), final.count("}")
print()
if ob == cb:
    print(f"{G}✓{RST} آکولاد متوازن: {ob}")
else:
    print(f"{R}✗{RST} آکولاد نامتوازن: {ob} vs {cb}")

print()
print(f"{G}تمام. حالا:{RST}")
print("  git add .")
print("  git commit -m 'fix: add missing AlertDialog and TextButton imports'")
print("  git push origin feature/cutting-parts-workflow")
