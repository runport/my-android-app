#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""fix_backhandler.py — fix invalid MoreSubSection.NONE reference."""
from pathlib import Path
import sys

P = Path("app/src/main/java/com/example/ui/screens/MoreHubScreen.kt")
if not P.exists():
    print(f"ERROR: {P} not found"); sys.exit(1)

c = P.read_text(encoding="utf-8")
orig_len = len(c)

# Fix the two invalid references
fixes = [
    ("selectedSubSection != MoreSubSection.NONE",
     "selectedSubSection == MoreSubSection.READY_GOODS"),
    ("viewModel.setSubSection(MoreSubSection.NONE)",
     "viewModel.setSubSection(MoreSubSection.ORDERS)"),
]

for old, new in fixes:
    if old in c:
        c = c.replace(old, new)
        print(f"  replaced: {old!r}")
    else:
        print(f"  WARNING: not found: {old!r}")

P.write_text(c, encoding="utf-8")
print(f"\nWritten: {P}  ({orig_len} -> {len(c)} bytes)")

# Verify
c2 = P.read_text(encoding="utf-8")
ok1 = "MoreSubSection.NONE" not in c2
ok2 = "selectedSubSection == MoreSubSection.READY_GOODS" in c2
ok3 = "viewModel.setSubSection(MoreSubSection.ORDERS)" in c2
ok4 = c2.count("{") == c2.count("}")

print()
print("=== VERIFY ===")
print(f"  NONE refs removed          : {ok1}")
print(f"  BackHandler enabled cond   : {ok2}")
print(f"  BackHandler action valid   : {ok3}")
print(f"  braces balanced            : {ok4}  ({c2.count('{')} vs {c2.count('}')})")

if not (ok1 and ok2 and ok3 and ok4):
    print("\nFAILED. Restore with: git checkout -- app/src/main/java/com/example/ui/screens/MoreHubScreen.kt")
    sys.exit(1)
print("\nOK.")
