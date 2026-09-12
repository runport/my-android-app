#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
log  = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")

INV = Path("app/src/main/java/com/example/ui/screens/InventoryScreen.kt")

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

if not INV.exists():
    err("InventoryScreen.kt پیدا نشد")
    exit(1)

c = read(INV)

# ============================================
# ۱. اضافه کردن import های لازم
# ============================================
info("۱. اضافه کردن imports")

imports_needed = [
    "import com.example.ui.dialogs.CategoryManagerDialog",
]

for imp in imports_needed:
    if imp not in c:
        idx = c.rfind("\nimport ")
        end = c.find("\n", idx + 1)
        c = c[:end] + "\n" + imp + c[end:]
        log(f"✓ {imp.split('.')[-1]} اضافه شد")
    else:
        warn(f"{imp.split('.')[-1]} از قبل هست")

# ============================================
# ۲. اضافه کردن state برای نمایش دیالوگ
# ============================================
info("۲. اضافه کردن state")

state_anchor = "var selectedCategory by remember { mutableStateOf(InventoryCategory.FINISHED_GOODS) }"
state_new = """var selectedCategory by remember { mutableStateOf(InventoryCategory.FINISHED_GOODS) }
  var showCategoryManager by remember { mutableStateOf(false) }"""

if "showCategoryManager" in c:
    warn("state از قبل هست")
elif state_anchor in c:
    c = c.replace(state_anchor, state_new, 1)
    log("state showCategoryManager اضافه شد")
else:
    err("anchor state پیدا نشد")

# ============================================
# ۳. اضافه کردن دکمه در بخش RAW_FABRICS
# ============================================
info("۳. اضافه کردن دکمه در RAW_FABRICS")

btn_block_fabrics = """      InventoryCategory.RAW_FABRICS -> {
        item {
          Button(
            onClick = { showCategoryManager = true },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(6.dp))
            Text("مدیریت دسته‌بندی پارچه (افزودن / ویرایش / حذف)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
        items(fabrics) { fabric ->"""

old_anchor_fabrics = """      InventoryCategory.RAW_FABRICS -> {
        items(fabrics) { fabric ->"""

if "مدیریت دسته‌بندی پارچه" in c:
    warn("دکمه پارچه از قبل هست")
elif old_anchor_fabrics in c:
    c = c.replace(old_anchor_fabrics, btn_block_fabrics, 1)
    log("دکمه مدیریت پارچه اضافه شد")
else:
    err("anchor RAW_FABRICS پیدا نشد")

# ============================================
# ۴. اضافه کردن دکمه در بخش ACCESSORIES
# ============================================
info("۴. اضافه کردن دکمه در ACCESSORIES")

btn_block_acc = """      InventoryCategory.ACCESSORIES -> {
        val accessories = inventoryItems.filter { it.category == "ملزومات" }
        item {
          Button(
            onClick = { showCategoryManager = true },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentAmber)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
            Spacer(Modifier.size(6.dp))
            Text("مدیریت دسته‌بندی ملزومات (افزودن / ویرایش / حذف)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
          }
        }
        items(accessories) { item ->"""

old_anchor_acc = """      InventoryCategory.ACCESSORIES -> {
        val accessories = inventoryItems.filter { it.category == "ملزومات" }
        items(accessories) { item ->"""

if "مدیریت دسته‌بندی ملزومات" in c:
    warn("دکمه ملزومات از قبل هست")
elif old_anchor_acc in c:
    c = c.replace(old_anchor_acc, btn_block_acc, 1)
    log("دکمه مدیریت ملزومات اضافه شد")
else:
    warn("anchor ACCESSORIES با ساختار پیش‌بینی‌شده پیدا نشد — بررسی دستی")

# ============================================
# ۵. اضافه کردن نمایش دیالوگ در انتهای InventoryScreen
# ============================================
info("۵. اضافه کردن نمایش دیالوگ")

# پیدا کردن آخرین } تابع InventoryScreen
# معمولاً بعد از Spacer(Modifier.height(80.dp)) هست

dialog_block = """

  // دیالوگ مدیریت دسته‌بندی‌ها
  if (showCategoryManager) {
    CategoryManagerDialog(
      viewModel = viewModel,
      onDismiss = { showCategoryManager = false }
    )
  }
"""

# پیدا کردن محل مناسب: قبل از آخرین } تابع
# راه: پیدا کردن آخرین "}\n}" بعد از LazyColumn closing
if "CategoryManagerDialog(" in c:
    warn("نمایش دیالوگ از قبل هست")
else:
    # پیدا کردن انتهای LazyColumn در InventoryScreen
    # معمولاً با این الگو: "    item {\n      Spacer(modifier = Modifier.height(80.dp))\n    }\n  }"
    anchor_end = """    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }"""
    
    if anchor_end in c:
        replacement = """    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
""" + dialog_block
        c = c.replace(anchor_end, replacement, 1)
        log("نمایش دیالوگ اضافه شد")
    else:
        warn("anchor انتهای LazyColumn پیدا نشد — بررسی دستی")

# ============================================
# ۶. اعتبارسنجی
# ============================================
print()
info("۶. بررسی نهایی:")

checks = [
    ("showCategoryManager", "state"),
    ("مدیریت دسته‌بندی پارچه", "دکمه پارچه"),
    ("CategoryManagerDialog(", "نمایش دیالوگ"),
]

for marker, label in checks:
    if marker in c:
        log(f"{label} ✓")
    else:
        err(f"{label} ✗")

# چک آکولاد
ob, cb = c.count("{"), c.count("}")
if ob == cb:
    log(f"آکولاد: {ob} متوازن")
else:
    err(f"آکولاد: {ob} vs {cb} نامتوازن!")

write(INV, c)

print()
log("فاز ۹ اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase9): add category manager button in inventory' && git push origin feature/cutting-parts-workflow{RST}")
