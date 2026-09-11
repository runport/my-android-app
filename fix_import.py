from pathlib import Path

INV = Path("app/src/main/java/com/example/ui/screens/InventoryScreen.kt")
c = INV.read_text(encoding="utf-8")

# پیدا کردن آخرین import
idx = c.rfind("\nimport ")
end_of_imports = c.find("\n", idx + 1)

# لیست importهای لازم
needed = [
    "import com.example.ui.dialogs.WaybillDetailsDialog",
]

added = []
for imp in needed:
    if imp not in c:
        c = c[:end_of_imports] + "\n" + imp + c[end_of_imports:]
        end_of_imports += len("\n" + imp)
        added.append(imp)

if added:
    INV.write_text(c, encoding="utf-8")
    print(f"✓ {len(added)} import اضافه شد")
    for imp in added:
        print(f"  + {imp}")
else:
    print("✓ imports از قبل موجودند")

# چک آکولاد
final = INV.read_text(encoding="utf-8")
ob, cb = final.count("{"), final.count("}")
print(f"\nآکولاد: {{={ob} }}={cb} {'✓' if ob == cb else '✗'}")
print(f"WaybillDetailsDialog در فایل: {final.count('WaybillDetailsDialog')} بار")

print()
print("حالا:")
print("  git add .")
print('  git commit -m "fix: add WaybillDetailsDialog import"')
print("  git push origin feature/cutting-parts-workflow")
