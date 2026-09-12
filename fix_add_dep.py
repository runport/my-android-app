#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Detect build system and add lifecycle-runtime-compose dependency."""
import re
from pathlib import Path

print("=" * 72)
print("STEP 1 — locate build files")
print("=" * 72)

candidates = []
for p in Path(".").rglob("*.gradle*"):
    s = str(p)
    if "/build/" in s or "/.gradle/" in s:
        continue
    if p.name in ("build.gradle", "build.gradle.kts"):
        candidates.append(p)
        print(f"  FOUND: {p}  ({p.stat().st_size} bytes)")

# version catalog
catalog = None
for p in Path(".").rglob("libs.versions.toml"):
    if "/build/" in str(p):
        continue
    catalog = p
    print(f"  FOUND catalog: {p}  ({p.stat().st_size} bytes)")
    break

if not candidates:
    print("\nERROR: no build.gradle files found")
    raise SystemExit(1)

print()
print("=" * 72)
print("STEP 2 — check current lifecycle deps")
print("=" * 72)

for p in candidates:
    c = p.read_text(encoding="utf-8")
    matches = re.findall(r'.*lifecycle.*', c)
    if matches:
        print(f"\n  {p}:")
        for m in matches[:20]:
            print(f"    {m.strip()[:120]}")

print()
print("=" * 72)
print("STEP 3 — check version catalog")
print("=" * 72)
if catalog:
    c = catalog.read_text(encoding="utf-8")
    for ln in c.split("\n"):
        if "lifecycle" in ln.lower():
            print(f"  {ln.strip()}")
else:
    print("  no version catalog")

print()
print("=" * 72)
print("Send the FULL output above before running any fix.")
print("=" * 72)
