#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Trace where 'Ready Tasks' screen is and what LazyColumn nesting exists."""
import re
from pathlib import Path

ROOT = Path("app/src/main/java/com/example")
files = list(ROOT.rglob("*.kt"))

# 1) Find Persian/Arabic string mentioning ready tasks / quick actions
print("=" * 70)
print("STEP 1 — search for 'کارهای آماده' / 'کارهای روزانه' / 'QuickAction'")
print("=" * 70)
for f in files:
    try:
        c = f.read_text(encoding="utf-8")
    except Exception:
        continue
    for kw in ["کارهای آماده", "کارهای روزانه", "Ready Tasks", "ReadyTasks", "quick_actions", "QuickActions"]:
        if kw in c:
            print(f"  {f.relative_to(ROOT)}  ->  {kw}")
            for i, ln in enumerate(c.split("\n"), 1):
                if kw in ln:
                    print(f"      L{i}: {ln.strip()[:120]}")
            print()

# 2) Detect nested LazyColumn — heuristic scan
print("=" * 70)
print("STEP 2 — files with nested LazyColumn (LazyColumn inside items{})")
print("=" * 70)

def analyze(f):
    try:
        lines = f.read_text(encoding="utf-8").split("\n")
    except Exception:
        return
    stack = []  # stack of (lineno, kind)
    for i, ln in enumerate(lines, 1):
        stripped = ln.strip()
        # opening LazyColumn/LazyRow
        if re.search(r'\bLazyColumn\s*\(', ln) or re.search(r'\bLazyRow\s*\(', ln):
            stack.append((i, "lazy"))
        # items() marks inner block
        if re.search(r'\bitems\s*\(', ln):
            stack.append((i, "items"))
        # closing brace count
        opens = ln.count("{")
        closes = ln.count("}")
        # pop for each closing brace
        for _ in range(closes):
            if stack:
                stack.pop()

        # is this LazyColumn nested inside items+another LazyColumn?
        if re.search(r'\bLazyColumn\s*\(', ln) or re.search(r'\bLazyRow\s*\(', ln):
            has_items = any(k == "items" for _, k in stack)
            lazy_count = sum(1 for _, k in stack if k == "lazy")
            if has_items and lazy_count >= 1:
                print(f"  {f.relative_to(ROOT)}  L{i}: NESTED LazyColumn inside items")
                print(f"      {ln.strip()[:120]}")

for f in files:
    analyze(f)

print()
print("=" * 70)
print("STEP 3 — all files with LazyColumn, sorted by count")
print("=" * 70)
counts = []
for f in files:
    try:
        c = f.read_text(encoding="utf-8")
    except Exception:
        continue
    n = c.count("LazyColumn") + c.count("LazyRow")
    if n > 0:
        counts.append((n, f))
for n, f in sorted(counts, reverse=True):
    print(f"  {n:3d}  {f.relative_to(ROOT)}")
