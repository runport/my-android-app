#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
fix_ready_v2.py — line-based fix (whitespace-tolerant)

Fixes: IllegalStateException from nesting ReadyGoodsScreen's LazyColumn
inside MoreHubScreen's outer LazyColumn.
"""
from pathlib import Path
import sys

P = Path("app/src/main/java/com/example/ui/screens/MoreHubScreen.kt")
if not P.exists():
    print(f"ERROR: {P} not found"); sys.exit(1)

text = P.read_text(encoding="utf-8")
lines = text.split("\n")
n = len(lines)

# ---- locate outer LazyColumn by finding the "values().forEach" anchor ----
target_anchor = None
for i, ln in enumerate(lines):
    if "MoreSubSection.values().forEach" in ln:
        target_anchor = i
        break

if target_anchor is None:
    print("ERROR: 'MoreSubSection.values().forEach' not found"); sys.exit(1)
print(f"  anchor 'MoreSubSection.values().forEach' at line {target_anchor+1}")

# search backward for LazyColumn(
lazy_idx = None
for j in range(target_anchor - 1, max(0, target_anchor - 60), -1):
    if "LazyColumn(" in lines[j]:
        lazy_idx = j
        break

if lazy_idx is None:
    print("ERROR: outer LazyColumn not found backward"); sys.exit(1)
print(f"  outer LazyColumn at line {lazy_idx+1}: {lines[lazy_idx]!r}")

# detect indentation of LazyColumn line
raw = lines[lazy_idx]
indent = raw[:len(raw) - len(raw.lstrip())]
print(f"  detected indent: {len(indent)} spaces")

# build early-return block with matching indent
er = [
    f"{indent}// Phase 11.x fix: READY_GOODS has its own LazyColumn; do not nest it.",
    f"{indent}if (selectedSubSection == MoreSubSection.READY_GOODS) {{",
    f"{indent}  ReadyGoodsScreen(viewModel = viewModel, modifier = modifier)",
    f"{indent}  return",
    f"{indent}}}",
    "",
]

# insert if not already present
if any("Phase 11.x fix: READY_GOODS" in l for l in lines):
    print("  [1] early-return already present, skipping")
else:
    lines = lines[:lazy_idx] + er + lines[lazy_idx:]
    print(f"  [1] early-return inserted before line {lazy_idx+1}")

# ---- empty the READY_GOODS branch inside `when` ----
# find the branch line
branch_start = None
for i, ln in enumerate(lines):
    s = ln.strip()
    if s.startswith("MoreSubSection.READY_GOODS") and "->" in s and "{}" not in s:
        branch_start = i
        break

if branch_start is None:
    print("  [2] READY_GOODS branch already empty, skipping")
else:
    print(f"  [2] READY_GOODS branch at line {branch_start+1}: {lines[branch_start]!r}")
    raw = lines[branch_start]
    branch_indent = raw[:len(raw) - len(raw.lstrip())]

    # find opening brace and match closing at same indent
    depth = 0
    end_idx = None
    for j in range(branch_start, min(branch_start + 40, len(lines))):
        depth += lines[j].count("{") - lines[j].count("}")
        if depth == 0 and j > branch_start:
            end_idx = j
            break

    if end_idx is None:
        print("  [2] ERROR: could not find branch end"); sys.exit(1)

    print(f"  [2] branch spans lines {branch_start+1}..{end_idx+1}")
    new_branch = f"{branch_indent}MoreSubSection.READY_GOODS -> {{}}  // handled above the LazyColumn (avoids nested-LazyColumn crash)"
    lines = lines[:branch_start] + [new_branch] + lines[end_idx+1:]
    print("  [2] READY_GOODS branch emptied")

new_text = "\n".join(lines)
P.write_text(new_text, encoding="utf-8")
print(f"\nWritten: {P}")

# ---- verify ----
c2 = P.read_text(encoding="utf-8")
ok1 = "Phase 11.x fix: READY_GOODS" in c2
ok2 = "MoreSubSection.READY_GOODS -> {}" in c2
ok3 = "item {\n            ReadyGoodsScreen" not in c2
ok4 = c2.count("{") == c2.count("}")
ok5 = "return" in c2.split("Phase 11.x fix")[1][:300]

print()
print("=== VERIFY ===")
print(f"  early-return inserted       : {ok1}")
print(f"  READY_GOODS branch empty    : {ok2}")
print(f"  old nested call removed     : {ok3}")
print(f"  braces balanced             : {ok4}  ({c2.count('{')} vs {c2.count('}')})")
print(f"  early-return has return     : {ok5}")
print(f"  lines: {text.count(chr(10))+1} -> {c2.count(chr(10))+1}")

if not (ok1 and ok2 and ok3 and ok4 and ok5):
    print("\nVERIFICATION FAILED — restore with:")
    print("  git checkout -- app/src/main/java/com/example/ui/screens/MoreHubScreen.kt")
    sys.exit(1)

print("\nDONE. Build next:")
print("  ./gradlew :app:assembleDebug")
