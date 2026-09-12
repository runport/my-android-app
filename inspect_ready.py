#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Inspect ReadyGoodsScreen.kt structure and how it is called from MoreHub."""
from pathlib import Path

ROOT = Path("app/src/main/java/com/example")

def show(path, from_line=1, to_line=None, label=""):
    p = ROOT / path
    if not p.exists():
        print(f"NOT FOUND: {path}")
        return
    lines = p.read_text(encoding="utf-8").split("\n")
    end = to_line if to_line else len(lines)
    print("=" * 72)
    print(f"FILE: {path}   ({len(lines)} lines)   {label}")
    print("=" * 72)
    for i in range(from_line - 1, min(end, len(lines))):
        ln = lines[i]
        mark = ">>>" if ("LazyColumn" in ln or "verticalScroll" in ln or
                         "Column(" in ln or "rememberScrollState" in ln or
                         "ReadyGoodsScreen" in ln) else "   "
        print(f"{mark} {i+1:5d}  {ln}")
    print()

# Show the entire ReadyGoodsScreen structure
p = ROOT / "ui/screens/ReadyGoodsScreen.kt"
if p.exists():
    lines = p.read_text(encoding="utf-8").split("\n")
    print(f"Total lines: {len(lines)}")
    print()
    # Find all fun definitions
    print("Function declarations:")
    for i, ln in enumerate(lines, 1):
        if ln.strip().startswith("fun ") or "@Composable" in ln:
            print(f"  L{i}: {ln.strip()[:100]}")
    print()

    # Show regions around each LazyColumn
    for i, ln in enumerate(lines, 1):
        if "LazyColumn" in ln and not ln.strip().startswith("import"):
            start = max(1, i - 10)
            end = min(len(lines), i + 60)
            print(f"--- LazyColumn at L{i}  (showing {start}-{end}) ---")
            for j in range(start - 1, end):
                m = ">>>" if (j+1) == i else "   "
                print(f"{m} {j+1:5d}  {lines[j]}")
            print()

    # Show top area (imports + structure)
    print("--- First 60 lines (structure/imports) ---")
    for i in range(min(60, len(lines))):
        print(f"{'   '} {i+1:5d}  {lines[i]}")
    print()
else:
    print("ReadyGoodsScreen.kt NOT FOUND")

# Show how ReadyGoodsScreen is called in MoreHubScreen
p2 = ROOT / "ui/screens/MoreHubScreen.kt"
if p2.exists():
    lines = p2.read_text(encoding="utf-8").split("\n")
    print("=" * 72)
    print(f"MoreHubScreen.kt  ({len(lines)} lines) — call to ReadyGoodsScreen")
    print("=" * 72)
    for i, ln in enumerate(lines, 1):
        if "ReadyGoods" in ln or "ReadyGood" in ln:
            start = max(1, i - 5)
            end = min(len(lines), i + 10)
            print(f"--- Match at L{i} ---")
            for j in range(start - 1, end):
                m = ">>>" if (j+1) == i else "   "
                print(f"{m} {j+1:5d}  {lines[j]}")
            print()

print("=" * 72)
print("Send the FULL output above.")
