#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Show code around LazyColumn and verticalScroll in suspect files."""
from pathlib import Path

ROOT = Path("app/src/main/java/com/example")

def show(path, ranges):
    p = ROOT / path
    if not p.exists():
        print(f"NOT FOUND: {path}")
        return
    lines = p.read_text(encoding="utf-8").split("\n")
    print("=" * 70)
    print(f"FILE: {path}   ({len(lines)} lines)")
    print("=" * 70)
    for (start, end, label) in ranges:
        print(f"\n--- {label}: lines {start}-{end} ---")
        for i in range(max(0, start-1), min(len(lines), end)):
            marker = ">>>" if (i+1) in [
                ln for ln, txt in enumerate(lines, 1)
                if "verticalScroll" in txt or "LazyColumn" in txt
            ] else "   "
            print(f"{marker} {i+1:5d}  {lines[i]}")
        print()

# ReserveOrderDialog.kt
show("ui/dialogs/ReserveOrderDialog.kt", [
    (100, 175, "LazyColumn regions"),
    (200, 240, "verticalScroll region"),
])

# ExecutiveFormComponents.kt
show("ui/dialogs/ExecutiveFormComponents.kt", [
    (150, 190, "LazyColumn #1"),
    (340, 390, "LazyColumn #2"),
    (470, 510, "verticalScroll"),
])

print("=" * 70)
print("Send the FULL output above.")
