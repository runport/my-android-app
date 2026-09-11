#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
fix_build.py — اصلاح خطاهای کامپایل پس از اعمال فاز ۱
"""
import re
from pathlib import Path

BASE = Path("app/src/main/java/com/example")
ENTITIES = BASE / "data/model/Entities.kt"
REPO = BASE / "data/repository/ManufacturingRepository.kt"
VM = BASE / "viewmodel/ManufacturingViewModel.kt"
DB = BASE / "data/database/AppDatabase.kt"
DAO_FILE = BASE / "data/dao/AppDao.kt"
QA = BASE / "ui/dialogs/QuickActionSheets.kt"
INV = BASE / "ui/screens/InventoryScreen.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

def log(msg, icon="✓"): print(f"{icon} {msg}")

# ---------- ۱. رفع تکرار بلوک MARKET_PRICE_UPDATE در QuickActionSheets ----------
def fix_duplicate_branch():
    c = read(QA)
    block = '''        QuickActionType.MARKET_PRICE_UPDATE -> {
          QuickMarketPriceUpdateForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }'''
    count = c.count(block)
    if count > 1:
        # حذف آخرین occurrence
        idx = c.rfind(block)
        c = c[:idx] + c[idx+len(block):]
        write(QA, c)
        log(f"بلوک تکراری MARKET_PRICE_UPDATE حذف شد (تعداد {count})")
    elif count == 1:
        log("بلوک MARKET_PRICE_UPDATE فقط یک بار وجود دارد")
    else:
        log("بلوک MARKET_PRICE_UPDATE پیدا نشد! بررسی دستی لازم است", "⚠")

# ---------- ۲. بررسی متدهای DAO ----------
def fix_dao_methods():
    c = read(DAO_FILE)
    # getBOMsUsingMaterial
    if "getBOMsUsingMaterial" not in c:
        # اضافه کردن به interface ProductBOMDao
        m = re.search(r'interface\s+ProductBOMDao\s*\{', c)
        if m:
            open_idx = m.end() - 1
            depth = 0
            for i in range(open_idx, len(c)):
                if c[i] == '{': depth += 1
                elif c[i] == '}':
                    depth -= 1
                    if depth == 0:
                        close_idx = i
                        break
            block = """
  @Query("SELECT * FROM product_boms WHERE materialId = :materialId")
  suspend fun getBOMsUsingMaterial(materialId: Long): List<ProductBOMEntity>
"""
            c = c[:close_idx] + block + c[close_idx:]
            write(DAO_FILE, c)
            log("متد getBOMsUsingMaterial به DAO اضافه شد")
        else:
            log("interface ProductBOMDao پیدا نشد", "⚠")
    else:
        log("متد getBOMsUsingMaterial از قبل وجود دارد")

    # getRollByIdOnce
    if "getRollByIdOnce" not in c:
        m = re.search(r'interface\s+FabricRollDao\s*\{', c)
        if m:
            open_idx = m.end() - 1
            depth = 0
            for i in range(open_idx, len(c)):
                if c[i] == '{': depth += 1
                elif c[i] == '}':
                    depth -= 1
                    if depth == 0:
                        close_idx = i
                        break
            block = """
  @Query("SELECT * FROM fabric_rolls WHERE id = :rollId")
  suspend fun getRollByIdOnce(rollId: Long): FabricRollEntity?
"""
            c = c[:close_idx] + block + c[close_idx:]
            write(DAO_FILE, c)
            log("متد getRollByIdOnce به DAO اضافه شد")
        else:
            log("interface FabricRollDao پیدا نشد", "⚠")
    else:
        log("متد getRollByIdOnce از قبل وجود دارد")

    # getAllCuttings
    if "getAllCuttings" not in c:
        m = re.search(r'interface\s+CuttingDao\s*\{', c)
        if m:
            open_idx = m.end() - 1
            depth = 0
            for i in range(open_idx, len(c)):
                if c[i] == '{': depth += 1
                elif c[i] == '}':
                    depth -= 1
                    if depth == 0:
                        close_idx = i
                        break
            block = """
  @Query("SELECT * FROM cutting_orders ORDER BY timestamp DESC")
  fun getAllCuttings(): Flow<List<CuttingEntity>>
"""
            c = c[:close_idx] + block + c[close_idx:]
            write(DAO_FILE, c)
            log("متد getAllCuttings به DAO اضافه شد")
        else:
            log("interface CuttingDao پیدا نشد", "⚠")
    else:
        log("متد getAllCuttings از قبل وجود دارد")

# ---------- ۳. بررسی ایمپورت‌ها ----------
def fix_imports():
    # InventoryScreen: Refresh
    c = read(INV)
    if "import androidx.compose.material.icons.filled.Refresh" not in c:
        c = c.replace(
            "import androidx.compose.material.icons.filled.Inventory2",
            "import androidx.compose.material.icons.filled.Inventory2\nimport androidx.compose.material.icons.filled.Refresh"
        )
        write(INV, c)
        log("import Refresh به InventoryScreen اضافه شد")
    else:
        log("import Refresh از قبل وجود دارد")

    # Repository: ProductBOMEntity و firstOrNull
    c = read(REPO)
    if "import com.example.data.model.ProductBOMEntity" not in c:
        c = c.replace(
            "import com.example.data.model.ProductEntity",
            "import com.example.data.model.ProductBOMEntity\nimport com.example.data.model.ProductEntity"
        )
        write(REPO, c)
        log("import ProductBOMEntity به Repository اضافه شد")
    else:
        log("import ProductBOMEntity از قبل وجود دارد")

    if "import kotlinx.coroutines.flow.firstOrNull" not in c:
        c = c.replace(
            "import kotlinx.coroutines.flow.first",
            "import kotlinx.coroutines.flow.first\nimport kotlinx.coroutines.flow.firstOrNull"
        )
        write(REPO, c)
        log("import firstOrNull به Repository اضافه شد")
    else:
        log("import firstOrNull از قبل وجود دارد")

# ---------- ۴. بررسی فیلدهای جدید در Entities ----------
def check_entity_fields():
    c = read(ENTITIES)
    if "currentPricePerMeter" not in c:
        log("فیلد currentPricePerMeter در FabricRollEntity وجود ندارد!", "✗")
    else:
        log("فیلد currentPricePerMeter در FabricRollEntity موجود است")
    if "currentPriceKg" not in c:
        log("فیلد currentPriceKg در MaterialEntity وجود ندارد!", "✗")
    else:
        log("فیلد currentPriceKg در MaterialEntity موجود است")

# ---------- ۵. اعتبارسنجی نهایی آکولادها ----------
def validate_braces():
    for path, label in [(ENTITIES, "Entities"), (REPO, "Repository"),
                        (VM, "ViewModel"), (DB, "Database"),
                        (DAO_FILE, "AppDao"), (QA, "QuickActionSheets"),
                        (INV, "InventoryScreen")]:
        c = read(path)
        ob, cb = c.count("{"), c.count("}")
        status = "✓" if ob == cb else "✗"
        print(f"{status} {label}: {{={ob} }}={cb}")

if __name__ == "__main__":
    print("=" * 60)
    print("🔧 اسکریپت اصلاح خطاهای کامپایل فاز ۱")
    print("=" * 60)
    fix_duplicate_branch()
    fix_dao_methods()
    fix_imports()
    check_entity_fields()
    print("\n📐 اعتبارسنجی نهایی آکولادها:")
    validate_braces()
    print("\n✅ اصلاحات انجام شد. حالا:")
    print("   git add .")
    print("   git commit -m 'fix: resolve build errors after phase 1'")
    print("   git push origin feature/cutting-parts-workflow")
