#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Find the file with LazyColumn nested inside verticalScroll."""
import re
from pathlib import Path

ROOT = Path("app/src/main/java/com/example")
files = list(ROOT.rglob("*.kt"))
print(f"Scanning {len(files)} .kt files...")
print()

suspects = []

for f in files:
    try:
        content = f.read_text(encoding="utf-8")
    except Exception:
        continue

    has_vscroll = "verticalScroll" in content
    has_lazy = "LazyColumn" in content or "LazyRow" in content

    if has_vscroll and has_lazy:
        suspects.append(f)
        print(f"[SUSPECT] {f.relative_to(ROOT)}")
        print(f"  verticalScroll count: {content.count('verticalScroll')}")
        print(f"  LazyColumn count:     {content.count('LazyColumn')}")
        print()

        lines = content.split("\n")
        for i, ln in enumerate(lines, 1):
            if "verticalScroll" in ln:
                print(f"  L{i}  vscroll:  {ln.strip()[:130]}")
            if "LazyColumn" in ln:
                print(f"  L{i}  lazycol:  {ln.strip()[:130]}")
        print("  " + "-" * 60)
        print()

if not suspects:
    print("No file with both verticalScroll + LazyColumn found.")
    print()
    print("Alternative check: listing all files with LazyColumn:")
    for f in files:
        try:
            c = f.read_text(encoding="utf-8")
        except Exception:
            continue
        if "LazyColumn" in c:
            print(f"  {f.relative_to(ROOT)}  ({c.count('LazyColumn')} occurrences)")
else:
    print(f"\nFound {len(suspects)} suspect file(s).")
    print("Send me the FULL output above.")
