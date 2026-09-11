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
REPO = BASE / "data/repository/ManufacturingRepository.kt"
DAO_FILE = BASE / "data/dao/AppDao.kt"
QA = BASE / "ui/dialogs/QuickActionSheets.kt"
INV = BASE / "ui/screens/InventoryScreen.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# ۱. importهای Repository
info("۱. importهای Repository")
c = read(REPO)
if "import com.example.data.model.ProductBOMEntity" not in c:
    if "import com.example.data.model.ProductEntity" in c:
        c = c.replace("import com.example.data.model.ProductEntity",
                      "import com.example.data.model.ProductBOMEntity\nimport com.example.data.model.ProductEntity", 1)
        log("ProductBOMEntity import اضافه شد")
    else:
        warn("ProductEntity import پیدا نشد")
else:
    log("ProductBOMEntity از قبل هست")

if "import kotlinx.coroutines.flow.firstOrNull" not in c:
    if "import kotlinx.coroutines.flow.Flow" in c:
        c = c.replace("import kotlinx.coroutines.flow.Flow",
                      "import kotlinx.coroutines.flow.Flow\nimport kotlinx.coroutines.flow.firstOrNull", 1)
        log("firstOrNull import اضافه شد")
    else:
        warn("Flow import پیدا نشد")
else:
    log("firstOrNull از قبل هست")
write(REPO, c)

# ۲. getBOMsUsingMaterial در DAO
info("۲. getBOMsUsingMaterial در DAO")
c = read(DAO_FILE)
if "getBOMsUsingMaterial" in c:
    log("از قبل هست")
else:
    m = re.search(r'interface\s+ProductBOMDao\s*\{', c)
    if m:
        open_idx = m.end() - 1
        depth = 0
        i = open_idx
        while i < len(c):
            if c[i] == '{': depth += 1
            elif c[i] == '}':
                depth -= 1
                if depth == 0: break
            i += 1
        block = "\n  @Query(\"SELECT * FROM product_boms WHERE materialId = :materialId\")\n  suspend fun getBOMsUsingMaterial(materialId: Long): List<ProductBOMEntity>\n"
        c = c[:i] + block + c[i:]
        write(DAO_FILE, c)
        log("getBOMsUsingMaterial اضافه شد")
    else:
        err("ProductBOMDao interface پیدا نشد")

# ۳. PriceUpdateDialog — رفع return در Composable
info("۳. PriceUpdateDialog")
c = read(QA)
old = """  val customColors = LocalCustomColors.current
  val target by viewModel.priceUpdateTarget.collectAsState()
  val t = target ?: return

  var pricePerMeterText by remember { mutableStateOf(t.currentPricePerMeter.toString()) }
  var pricePerKgText by remember { mutableStateOf(t.currentPricePerKg.toString()) }
  var reasonText by remember { mutableStateOf("تغییر قیمت بازار") }"""
new = """  val customColors = LocalCustomColors.current
  val target by viewModel.priceUpdateTarget.collectAsState()

  if (target == null) return
  val t = target!!

  var pricePerMeterText by remember(t.id) { mutableStateOf(t.currentPricePerMeter.toString()) }
  var pricePerKgText by remember(t.id) { mutableStateOf(t.currentPricePerKg.toString()) }
  var reasonText by remember(t.id) { mutableStateOf("تغییر قیمت بازار") }"""

if old in c:
    c = c.replace(old, new, 1)
    log("PriceUpdateDialog اصلاح شد")
elif new in c:
    log("از قبل اصلاح شده")
else:
    warn("الگوی PriceUpdateDialog پیدا نشد")
write(QA, c)

# ۴. import Refresh
info("۴. import Refresh در InventoryScreen")
c = read(INV)
if "import androidx.compose.material.icons.filled.Refresh" not in c:
    if "import androidx.compose.material.icons.filled.Inventory2" in c:
        c = c.replace("import androidx.compose.material.icons.filled.Inventory2",
                      "import androidx.compose.material.icons.filled.Inventory2\nimport androidx.compose.material.icons.filled.Refresh", 1)
        write(INV, c)
        log("اضافه شد")
    else:
        warn("Inventory2 import پیدا نشد")
else:
    log("از قبل هست")

# ۵. بررسی نهایی
info("۵. بررسی آکولادها")
for path, label in [(REPO, "Repository"), (DAO_FILE, "AppDao"),
                    (QA, "QuickActionSheets"), (INV, "InventoryScreen")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

print()
log("تمام. حالا:")
print("  git add .")
print("  git commit -m 'fix: resolve phase 1 build errors'")
print("  git push origin feature/cutting-parts-workflow")
