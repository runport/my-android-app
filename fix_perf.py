#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
fix_perf.py — replace collectAsState() with collectAsStateWithLifecycle()
in heavy files. This defers flow collection when the composable leaves
the resumed lifecycle state, reducing recompositions and CPU usage.
"""
from pathlib import Path
import sys

ROOT = Path("app/src/main/java/com/example")
IMPORT_NEW = "import androidx.lifecycle.compose.collectAsStateWithLifecycle"

TARGETS = [
    "MainActivity.kt",
    "ui/screens/MoreHubScreen.kt",
    "ui/screens/DashboardScreen.kt",
    "ui/screens/AnalyticsScreen.kt",
    "ui/screens/InventoryScreen.kt",
    "ui/screens/ReadyGoodsScreen.kt",
    "ui/dialogs/QuickActionSheets.kt",
    "ui/dialogs/ReserveOrderDialog.kt",
]

print("=" * 72)
print("collectAsState -> collectAsStateWithLifecycle")
print("=" * 72)

total_files = 0
total_calls = 0
failures = []

for t in TARGETS:
    p = ROOT / t
    if not p.exists():
        print(f"  SKIP (not found): {t}")
        continue

    c = p.read_text(encoding="utf-8")
    n = c.count(".collectAsState()")
    if n == 0:
        print(f"  {t}: no calls")
        continue

    c2 = c.replace(".collectAsState()", ".collectAsStateWithLifecycle()")

    if IMPORT_NEW not in c2:
        lines = c2.split("\n")
        last_imp = -1
        for i, ln in enumerate(lines):
            if ln.startswith("import "):
                last_imp = i
        if last_imp >= 0:
            lines.insert(last_imp + 1, IMPORT_NEW)
            c2 = "\n".join(lines)

    # sanity check: braces unchanged
    if c2.count("{") != c.count("{"):
        failures.append(t)
        print(f"  FAILED (braces): {t}")
        continue

    p.write_text(c2, encoding="utf-8")
    total_files += 1
    total_calls += n
    print(f"  {t}: replaced {n} call(s)")

print()
print("=" * 72)
print(f"  files changed : {total_files}")
print(f"  calls changed : {total_calls}")
if failures:
    print(f"  failures      : {failures}")
print("=" * 72)

if failures:
    print("\nSome files failed. Restore them with:")
    for f in failures:
        print(f"  git checkout -- app/src/main/java/com/example/{f}")
    sys.exit(1)

print("\nOK. Now verify with:")
print("  git --no-pager diff --stat")
