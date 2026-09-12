from pathlib import Path
ROOT = Path("app/src/main/java/com/example")
targets = [
    "ui/screens/InventoryScreen.kt",
    "ui/screens/MoreHubScreen.kt",
    "ui/dialogs/ManagementDialogs.kt",
    "ui/dialogs/QuickActionSheets.kt",
]
for t in targets:
    p = ROOT / t
    if not p.exists(): continue
    lines = p.read_text(encoding="utf-8").split("\n")
    print("=" * 72)
    print(f"{t}")
    print("=" * 72)
    for i, ln in enumerate(lines, 1):
        s = ln.strip()
        if any(k in s for k in ['"حذف"', '"افزودن"', '"اضافه"', '"ویرایش"',
                                 '"ثبت"', '"ذخیره"', "Icons.Default.Delete",
                                 "Icons.Default.Add", "Icons.Default.Edit"]):
            start = max(0, i-4)
            end = min(len(lines), i+4)
            print(f"\n--- match at L{i} ---")
            for j in range(start, end):
                m = ">>>" if j+1 == i else "   "
                print(f"{m} {j+1:5d}  {lines[j]}")
    print()
