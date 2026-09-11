#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
log  = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")

BASE = Path("app/src/main/java/com/example")
ENTITIES = BASE / "data/model/Entities.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")


def find_class_range(content, class_name):
    """محدوده constructor data class را پیدا می‌کند"""
    m = re.search(rf'data class {re.escape(class_name)}\(', content)
    if not m:
        return None
    start = m.end()
    depth = 1
    i = start
    while i < len(content):
        if content[i] == '(':
            depth += 1
        elif content[i] == ')':
            depth -= 1
            if depth == 0:
                return (start, i)
        i += 1
    return None


def ensure_field(content, class_name, field_name, field_def):
    rng = find_class_range(content, class_name)
    if not rng:
        return content, False, f"class {class_name} not found"
    start, end = rng
    body = content[start:end]
    if re.search(rf'\bval\s+{re.escape(field_name)}\b', body):
        return content, False, "already exists"
    return content[:end] + "\n" + field_def + content[end:], True, "added"


# ============ DIAGNOSTIC ============
print("=" * 70)
print("DIAGNOSTIC — وضعیت فعلی")
print("=" * 70)

ec = read(ENTITIES)
print(f"\nEntities.kt:")
print(f"  currentPricePerMeter: {ec.count('currentPricePerMeter')} بار")
print(f"  currentPricePerKg   : {ec.count('currentPricePerKg')} بار")
print(f"  currentPriceKg      : {ec.count('currentPriceKg')} بار")
print(f"  lastPriceUpdateDate : {ec.count('lastPriceUpdateDate')} بار")

rng = find_class_range(ec, "FabricRollEntity")
if rng:
    print(f"\n=== FabricRollEntity fields ===")
    print(ec[rng[0]:rng[1]].strip()[:1000])

rng = find_class_range(ec, "MaterialEntity")
if rng:
    print(f"\n=== MaterialEntity fields ===")
    print(ec[rng[0]:rng[1]].strip()[:1000])

# ============ FIX ============
print()
print("=" * 70)
print("APPLYING FIX")
print("=" * 70)

content = ec
changed = False

for fname, fdef in [
    ("currentPricePerMeter",      "  val currentPricePerMeter: Long = 0L,"),
    ("currentPricePerKg",         "  val currentPricePerKg: Long = 0L,"),
    ("lastPriceUpdateDate",       '  val lastPriceUpdateDate: String = "",'),
    ("lastPriceUpdateTimestamp",  "  val lastPriceUpdateTimestamp: Long = 0L,"),
]:
    content, was_changed, msg = ensure_field(content, "FabricRollEntity", fname, fdef)
    if was_changed:
        log(f"FabricRollEntity.{fname} → اضافه شد")
        changed = True
    else:
        info(f"FabricRollEntity.{fname} → {msg}")

for fname, fdef in [
    ("currentPriceKg",    "  val currentPriceKg: Long = 0L,"),
    ("priceUpdateNote",   '  val priceUpdateNote: String = "",'),
]:
    content, was_changed, msg = ensure_field(content, "MaterialEntity", fname, fdef)
    if was_changed:
        log(f"MaterialEntity.{fname} → اضافه شد")
        changed = True
    else:
        info(f"MaterialEntity.{fname} → {msg}")

if changed:
    write(ENTITIES, content)
    log("Entities.kt ذخیره شد ✓")
else:
    warn("Entities.kt تغییری نکرد")

# ============ FINAL ============
print()
print("=" * 70)
print("FINAL CHECK")
print("=" * 70)

ec2 = read(ENTITIES)
for f in ["currentPricePerMeter", "currentPricePerKg", "currentPriceKg",
          "lastPriceUpdateDate", "priceUpdateNote"]:
    cnt = ec2.count(f)
    if cnt > 0:
        log(f"{f}: {cnt}")
    else:
        err(f"{f}: نیست!")

c = read(ENTITIES)
ob, cb = c.count("{"), c.count("}")
if ob == cb:
    log(f"آکولاد: {ob} متوازن")
else:
    err(f"آکولاد: {ob} vs {cb} نامتوازن")

print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'fix: add missing price columns to entities' && git push origin feature/cutting-parts-workflow{RST}")
