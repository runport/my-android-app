#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
fix_v3_step1.py — fixes:
  1. Back button in MoreHubScreen doesn't return from sub-sections
  2. Remove '(فاز ۲)' from InventoryScreen labels
"""
from pathlib import Path
import sys

ROOT = Path("app/src/main/java/com/example")

# =========================================================================
# FIX 1 — BackHandler in MoreHubScreen
# =========================================================================
print("=" * 72)
print("FIX 1 — BackHandler in MoreHubScreen")
print("=" * 72)

P = ROOT / "ui/screens/MoreHubScreen.kt"
if not P.exists():
    print(f"ERROR: {P} not found"); sys.exit(1)

lines = P.read_text(encoding="utf-8").split("\n")

# --- 1a) check imports ---
has_backhandler_import = any(
    "androidx.activity.compose.BackHandler" in ln for ln in lines
)
has_none_import = any("import androidx.compose.ui.unit.dp" in ln for ln in lines)

# find a good import anchor — after the last "import androidx.compose."
last_compose_import_idx = None
for i, ln in enumerate(lines):
    if ln.startswith("import androidx.compose."):
        last_compose_import_idx = i

if not has_backhandler_import and last_compose_import_idx is not None:
    lines.insert(
        last_compose_import_idx + 1,
        "import androidx.activity.compose.BackHandler"
    )
    print(f"  import added after line {last_compose_import_idx+1}")
elif has_backhandler_import:
    print("  import already present")
else:
    print("  ERROR: cannot find import anchor"); sys.exit(1)

# --- 1b) find insertion point for BackHandler body ---
# anchor: the line with "val selectedSubSection by viewModel.selectedSubSection.collectAsState()"
anchor_idx = None
for i, ln in enumerate(lines):
    if "val selectedSubSection by" in ln and "collectAsState" in ln:
        anchor_idx = i
        break

if anchor_idx is None:
    print("  ERROR: 'val selectedSubSection by' not found"); sys.exit(1)

raw = lines[anchor_idx]
indent = raw[:len(raw) - len(raw.lstrip())]
print(f"  anchor at line {anchor_idx+1}: {raw.strip()!r}")
print(f"  detected indent: {len(indent)} spaces")

# check if BackHandler already present
already = any("BackHandler(" in ln for ln in lines)
if already:
    print("  BackHandler already present, skipping body insert")
else:
    block = [
        "",
        f"{indent}// FIX v3: handle system back — return to hub root if in a sub-section",
        f"{indent}BackHandler(",
        f"{indent}  enabled = selectedSubSection != MoreSubSection.NONE",
        f"{indent}) {{",
        f"{indent}  viewModel.setSubSection(MoreSubSection.NONE)",
        f"{indent}}}",
    ]
    lines = lines[:anchor_idx+1] + block + lines[anchor_idx+1:]
    print(f"  BackHandler inserted after line {anchor_idx+1}")

P.write_text("\n".join(lines), encoding="utf-8")
print(f"  written: {P}")

# =========================================================================
# FIX 2 — Remove '(فاز ۲)' from InventoryScreen
# =========================================================================
print()
print("=" * 72)
print("FIX 2 — Remove '(فاز ۲)' from InventoryScreen")
print("=" * 72)

P2 = ROOT / "ui/screens/InventoryScreen.kt"
if not P2.exists():
    print(f"ERROR: {P2} not found"); sys.exit(1)

c = P2.read_text(encoding="utf-8")
original_len = len(c)

replacements = [
    ('FABRIC_ROLLS("طاقه‌ها (فاز ۲)")',      'FABRIC_ROLLS("طاقه‌ها")'),
    ('"+ طاقه جدید (فاز ۲)"',                '"+ طاقه جدید"'),
    ('طاقه‌ها (فاز ۲)',                       'طاقه‌ها'),
    (' (فاز ۲)',                              ''),           # fallback
    (' (فاز 2)',                              ''),
]

total_replaced = 0
for old, new in replacements:
    n = c.count(old)
    if n > 0:
        c = c.replace(old, new)
        total_replaced += n
        print(f"  replaced {n}x: {old!r}  ->  {new!r}")

if total_replaced == 0:
    print("  nothing to replace (already clean)")
else:
    P2.write_text(c, encoding="utf-8")
    print(f"  written: {P2}  ({original_len} -> {len(c)} bytes)")

# =========================================================================
# VERIFY
# =========================================================================
print()
print("=" * 72)
print("VERIFY")
print("=" * 72)

c1 = P.read_text(encoding="utf-8")
print(f"  MoreHubScreen BackHandler      : {'BackHandler(' in c1}")
print(f"  MoreHubScreen BackHandler imp  : {'import androidx.activity.compose.BackHandler' in c1}")
print(f"  MoreHubScreen braces           : {c1.count('{')} vs {c1.count('}')}")

c2 = P2.read_text(encoding="utf-8")
print(f"  Inventory فاز ۲ remaining       : {c2.count('فاز ۲')}")
print(f"  Inventory braces               : {c2.count('{')} vs {c2.count('}')}")

ok = (
    "BackHandler(" in c1
    and "import androidx.activity.compose.BackHandler" in c1
    and c1.count("{") == c1.count("}")
    and c2.count("فاز ۲") == 0
    and c2.count("{") == c2.count("}")
)

if ok:
    print()
    print("✓ All checks passed.")
    print()
    print("Next steps:")
    print("  1. Inspect:  git --no-pager diff app/src/main/java/com/example/ui/screens/")
    print("  2. Commit + push, then check GitHub Actions")
else:
    print()
    print("✗ Some checks failed. Restore with:")
    print("  git checkout -- app/src/main/java/com/example/ui/screens/MoreHubScreen.kt")
    print("  git checkout -- app/src/main/java/com/example/ui/screens/InventoryScreen.kt")
    sys.exit(1)
