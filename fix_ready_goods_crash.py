#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
fix_ready_goods_crash.py

Fixes IllegalStateException crash caused by nesting ReadyGoodsScreen's
LazyColumn inside MoreHubScreen's outer LazyColumn.

Solution:
  1. In MoreHubScreen, short-circuit before the outer LazyColumn when
     selectedSubSection == READY_GOODS, rendering ReadyGoodsScreen directly.
  2. Replace the READY_GOODS -> { item { ReadyGoodsScreen(...) } } branch
     with an empty branch (it will never execute anyway).
"""
from pathlib import Path

p = Path("app/src/main/java/com/example/ui/screens/MoreHubScreen.kt")
if not p.exists():
    print(f"ERROR: {p} not found")
    raise SystemExit(1)

c = p.read_text(encoding="utf-8")
original = c

# ---------- 1. Insert early-return before the outer LazyColumn ----------

early_return = (
    "  // Phase 11.x fix: READY_GOODS has its own LazyColumn; do not nest it.\n"
    "  if (selectedSubSection == MoreSubSection.READY_GOODS) {\n"
    "    ReadyGoodsScreen(viewModel = viewModel, modifier = modifier)\n"
    "    return\n"
    "  }\n"
    "\n"
)

anchor_outer = (
    "    LazyColumn(\n"
    "      modifier = modifier\n"
    "        .fillMaxSize()\n"
    "        .padding(horizontal = 16.dp),\n"
    "      verticalArrangement = Arrangement.spacedBy(16.dp)\n"
    "    ) {"
)

if early_return.strip() in c:
    print("  [1] early-return already present, skipping")
else:
    idx = c.find(anchor_outer)
    if idx < 0:
        print("  [1] ERROR: outer LazyColumn anchor not found")
        raise SystemExit(1)
    c = c[:idx] + early_return + c[idx:]
    print("  [1] early-return inserted before outer LazyColumn")

# ---------- 2. Empty the READY_GOODS branch ----------

old_branch = (
    "        MoreSubSection.READY_GOODS -> {\n"
    "          item {\n"
    "            ReadyGoodsScreen(viewModel = viewModel)\n"
    "          }\n"
    "        }"
)

new_branch = (
    "        MoreSubSection.READY_GOODS -> {}  "
    "// handled above the LazyColumn (avoids nested-LazyColumn crash)"
)

if new_branch.split("\n")[0] in c:
    print("  [2] READY_GOODS branch already emptied, skipping")
elif old_branch in c:
    c = c.replace(old_branch, new_branch, 1)
    print("  [2] READY_GOODS branch emptied")
else:
    print("  [2] ERROR: READY_GOODS branch anchor not found")
    raise SystemExit(1)

# ---------- 3. Write & verify ----------
if c == original:
    print("\nNothing changed.")
    raise SystemExit(0)

p.write_text(c, encoding="utf-8")
print(f"\nWritten: {p}")

c2 = p.read_text(encoding="utf-8")
print()
print("=== VERIFY ===")
ok1 = "// Phase 11.x fix" in c2
ok2 = "MoreSubSection.READY_GOODS -> {}" in c2
ok3 = "item {\n            ReadyGoodsScreen" not in c2
ok4 = c2.count("{") == c2.count("}")

print(f"  early-return inserted     : {ok1}")
print(f"  READY_GOODS branch empty  : {ok2}")
print(f"  old nested call removed   : {ok3}")
print(f"  braces balanced           : {ok4} "
      f"({c2.count('{')} vs {c2.count('}')})")
print(f"\nLines: {original.count(chr(10))+1} -> {c2.count(chr(10))+1}")

if not (ok1 and ok2 and ok3 and ok4):
    print("\nVERIFICATION FAILED — run: git checkout -- "
          "app/src/main/java/com/example/ui/screens/MoreHubScreen.kt")
    raise SystemExit(1)

print("\nDone. Now build:")
print("  ./gradlew :app:assembleDebug")
