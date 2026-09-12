#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Diagnose multiple issues: perf, back, inventory cards, management buttons."""
from pathlib import Path
import re

ROOT = Path("app/src/main/java/com/example")

# ---------- 1. Ready Goods back button ----------
print("=" * 72)
print("1) ReadyGoods / MoreHub — back handling")
print("=" * 72)

for name in ["ui/screens/MoreHubScreen.kt", "ui/screens/ReadyGoodsScreen.kt",
             "MainActivity.kt"]:
    p = ROOT / name
    if not p.exists():
        print(f"  NOT FOUND: {name}")
        continue
    lines = p.read_text(encoding="utf-8").split("\n")
    print(f"\n  FILE: {name}  ({len(lines)} lines)")
    for i, ln in enumerate(lines, 1):
        if any(k in ln for k in [
            "BackHandler", "onBackPressed", "BackPressedDispatcher",
            "popBackStack", "navigateUp", "selectedSubSection",
            "setSubSection", "ReadyGoodsScreen"
        ]):
            print(f"    L{i}: {ln.strip()[:120]}")
    print()

# ---------- 2. Inventory cards / chart ----------
print("=" * 72)
print("2) Inventory cards with Persian labels")
print("=" * 72)

for name in ["ui/screens/InventoryScreen.kt", "ui/screens/DashboardScreen.kt"]:
    p = ROOT / name
    if not p.exists():
        print(f"  NOT FOUND: {name}")
        continue
    content = p.read_text(encoding="utf-8")
    for kw in ["محصولات آماده", "طاقه", "فاز ۲", "فاز 2", "دسته های پارچه",
               "دسته‌های پارچه", "ملزومات", "باربری", "باربرى"]:
        if kw in content:
            lines = content.split("\n")
            for i, ln in enumerate(lines, 1):
                if kw in ln:
                    print(f"  {name}  L{i}: {ln.strip()[:120]}")

print()

# ---------- 3. Management buttons (add/edit/delete) ----------
print("=" * 72)
print("3) Management button patterns")
print("=" * 72)

patterns = [
    ("حذف", 0), ("افزودن", 0), ("اضافه", 0), ("ویرایش", 0),
    ("Delete", 0), ("Add", 0), ("Edit", 0),
]
for name in ["ui/screens/InventoryScreen.kt", "ui/screens/MoreHubScreen.kt",
             "ui/dialogs/ManagementDialogs.kt", "ui/dialogs/QuickActionSheets.kt"]:
    p = ROOT / name
    if not p.exists():
        continue
    content = p.read_text(encoding="utf-8")
    total = 0
    for kw, _ in patterns:
        total += content.count(f'"{kw}"')
    print(f"  {name}: {total} button literals")

# ---------- 4. Performance — huge recompositions, LazyColumn problems ----------
print()
print("=" * 72)
print("4) Performance hotspots")
print("=" * 72)

for f in ROOT.rglob("*.kt"):
    try:
        c = f.read_text(encoding="utf-8")
    except Exception:
        continue
    issues = []
    if c.count("LazyColumn") >= 3 and "items(" in c:
        issues.append(f"many LazyColumn ({c.count('LazyColumn')})")
    if c.count("verticalScroll") >= 5:
        issues.append(f"many verticalScroll ({c.count('verticalScroll')})")
    if "collectAsState()" in c and c.count("collectAsState()") >= 8:
        issues.append(f"many collectAsState ({c.count('collectAsState()')})")
    if issues:
        print(f"  {f.relative_to(ROOT)}: {', '.join(issues)}")

print()
print("=" * 72)
print("Send the FULL output above.")
