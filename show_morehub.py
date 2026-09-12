#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Show MoreHubScreen structure around the outer LazyColumn and READY_GOODS branch."""
from pathlib import Path

p = Path("app/src/main/java/com/example/ui/screens/MoreHubScreen.kt")
lines = p.read_text(encoding="utf-8").split("\n")

print(f"Total lines: {len(lines)}")
print()
print("=" * 72)
print("All @Composable / fun / LazyColumn / when() / MoreSubSection branches")
print("=" * 72)
for i, ln in enumerate(lines, 1):
    s = ln.strip()
    if (s.startswith("fun ") or s.startswith("@Composable")
            or "LazyColumn" in s
            or "MoreSubSection." in s and "->" in s
            or s.startswith("when (")
            or "column(" in s.lower() and "verticalScroll" in s.lower()):
        print(f"  L{i:5d}  {ln[:120]}")

print()
print("=" * 72)
print("Structure around the outer LazyColumn: showing L640-L820")
print("=" * 72)
for i in range(639, min(820, len(lines))):
    ln = lines[i]
    m = ">>>" if ("LazyColumn" in ln or "when (" in ln or "MoreSubSection" in ln or
                   "ReadyGoodsScreen" in ln or "column(" in ln) else "   "
    print(f"{m} {i+1:5d}  {ln}")
print()
