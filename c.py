#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
fix_v2.py — رفع خطاهای کامپایل فاز ۱ (به‌روزرسانی قیمت)
"""
import re
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
def log(m): print(f"{G}✓{RST} {m}")
def warn(m): print(f"{Y}⚠{RST} {m}")
def err(m): print(f"{R}✗{RST} {m}")
def info(m): print(f"{B}ℹ{RST} {m}")

BASE = Path("app/src/main/java/com/example")
REPO = BASE / "data/repository/ManufacturingRepository.kt"
DAO_FILE = BASE / "data/dao/AppDao.kt"
QA = BASE / "ui/dialogs/QuickActionSheets.kt"
INV = BASE / "ui/screens/InventoryScreen.kt"
VM = BASE / "viewmodel/ManufacturingViewModel.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# ============ ۱. import های گمشده در Repository ============
print()
info("۱. بررسی importها در Repository")
c = read(REPO)

# ProductBOMEntity
if "import com.example.data.model.ProductBOMEntity" not in c:
    # قبل از ProductEntity اضافه کن
    if "import com.example.data.model.ProductEntity" in c:
        c = c.replace(
            "import com.example.data.model.ProductEntity",
            "import com.example.data.model.ProductBOMEntity\nimport com.example.data.model.ProductEntity",
            1
        )
        log("import ProductBOMEntity اضافه شد")
    else:
        warn("ProductEntity import پیدا نشد — دستی چک کن")
else:
    log("import ProductBOMEntity از قبل هست")

# firstOrNull
if "import kotlinx.coroutines.flow.firstOrNull" not in c:
    if "import kotlinx.coroutines.flow.first" in c:
        c = c.replace(
            "import kotlinx.coroutines.flow.first\n",
            "import kotlinx.coroutines.flow.first\nimport kotlinx.coroutines.flow.firstOrNull\n",
            1
        )
        log("import firstOrNull اضافه شد")
    elif "import kotlinx.coroutines.flow.Flow" in c:
        c = c.replace(
            "import kotlinx.coroutines.flow.Flow",
            "import kotlinx.coroutines.flow.Flow\nimport kotlinx.coroutines.flow.firstOrNull",
            1
        )
        log("import firstOrNull اضافه شد (کنار Flow)")
    else:
        warn("import مربوطه پیدا نشد — دستی چک کن")
else:
    log("import firstOrNull از قبل هست")

write(REPO, c)

# ============ ۲. متد getBOMsUsingMaterial در DAO ============
print()
info("۲. بررسی متد getBOMsUsingMaterial در DAO")
c = read(DAO_FILE)
if "getBOMsUsingMaterial" in c:
    log("getBOMsUsingMaterial از قبل هست")
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
        close_idx = i
        block = """
  @Query("SELECT * FROM product_boms WHERE materialId = :materialId")
  suspend fun getBOMsUsingMaterial(materialId: Long): List<ProductBOMEntity>
"""
        c = c[:close_idx] + block + c[close_idx:]
        write(DAO_FILE, c)
        log("getBOMsUsingMaterial اضافه شد")
    else:
        err("interface ProductBOMDao پیدا نشد")

# ============ ۳. رفع PriceUpdateDialog — return داخل Composable ============
print()
info("۳. رفع PriceUpdateDialog در QuickActionSheets")
c = read(QA)

# مشکل: `val t = target ?: return` در Composable
old_pattern = """  val customColors = LocalCustomColors.current
  val target by viewModel.priceUpdateTarget.collectAsState()
  val t = target ?: return

  var pricePerMeterText by remember { mutableStateOf(t.currentPricePerMeter.toString()) }"""

new_pattern = """  val customColors = LocalCustomColors.current
  val target by viewModel.priceUpdateTarget.collectAsState()

  if (target == null) return
  val t = target!!

  var pricePerMeterText by remember(t.id) { mutableStateOf(t.currentPricePerMeter.toString()) }"""

if old_pattern in c:
    c = c.replace(old_pattern, new_pattern, 1)
    log("PriceUpdateDialog اصلاح شد (return → if null)")
elif new_pattern in c:
    log("PriceUpdateDialog از قبل اصلاح شده")
else:
    warn("الگوی PriceUpdateDialog پیدا نشد — دستی چک کن")

# رفع remember dependency برای pricePerKgText و reasonText
c = c.replace(
    """  var pricePerKgText by remember { mutableStateOf(t.currentPricePerKg.toString()) }
  var reasonText by remember { mutableStateOf("تغییر قیمت بازار") }""",
    """  var pricePerKgText by remember(t.id) { mutableStateOf(t.currentPricePerKg.toString()) }
  var reasonText by remember(t.id) { mutableStateOf("تغییر قیمت بازار") }""",
    1
)

write(QA, c)

# ============ ۴. بررسی تکرار MARKET_PRICE_UPDATE ============
print()
info("۴. بررسی تکرار بلوک MARKET_PRICE_UPDATE")
c = read(QA)
block = """        QuickActionType.MARKET_PRICE_UPDATE -> {
          QuickMarketPriceUpdateForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }"""
count = c.count(block)
if count > 1:
    idx = c.rfind(block)
    c = c[:idx] + c[idx+len(block):]
    write(QA, c)
    log(f"بلوک تکراری حذف شد (تعداد {count})")
elif count == 1:
    log("MARKET_PRICE_UPDATE فقط یک بار هست")
else:
    # شاید قبلاً حذف شده، ببینیم QuickMarketPriceUpdateForm تعریف شده
    if "fun QuickMarketPriceUpdateForm" in c:
        log("QuickMarketPriceUpdateForm تعریف شده ولی branch نیست")
    else:
        warn("بلوک MARKET_PRICE_UPDATE پیدا نشد")

# ============ ۵. بررسی تکراری بودن QuickMarketPriceUpdateForm ============
print()
info("۵. بررسی تکراری بودن تابع QuickMarketPriceUpdateForm")
c = read(QA)
matches = re.findall(r'fun\s+QuickMarketPriceUpdateForm\s*\(', c)
if len(matches) > 1:
    err(f"تابع QuickMarketPriceUpdateForm {len(matches)} بار تعریف شده! رفع:")
    # نگه‌داشتن اولین، حذف بقیه (باید دستی انجام شود)
    warn("— این مورد نیاز به بررسی دستی دارد —")
else:
    log(f"تابع QuickMarketPriceUpdateForm {len(matches)} بار تعریف شده")

# ============ ۶. import Refresh در InventoryScreen ============
print()
info("۶. بررسی import Refresh در InventoryScreen")
c = read(INV)
if "import androidx.compose.material.icons.filled.Refresh" not in c:
    c = c.replace(
        "import androidx.compose.material.icons.filled.Inventory2",
        "import androidx.compose.material.icons.filled.Inventory2\nimport androidx.compose.material.icons.filled.Refresh",
        1
    )
    write(INV, c)
    log("import Refresh اضافه شد")
else:
    log("import Refresh از قبل هست")

# ============ ۷. آکولاد نهایی ============
print()
info("۷. بررسی نهایی آکولادها:")
for path, label in [(REPO, "Repository"), (DAO_FILE, "AppDao"),
                    (QA, "QuickActionSheets"), (INV, "InventoryScreen"),
                    (VM, "ViewModel")]:
    c = read(path)
    ob, cb = c.count("{"), c.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} — نامتوازن!")

print()
print("=" * 60)
log("فاز اصلاح اجرا شد")
print("=" * 60)
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'fix: resolve phase 1 build errors' && git push{RST}")
