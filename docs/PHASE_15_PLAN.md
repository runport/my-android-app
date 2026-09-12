# Phase 15 - Fabric Price Propagation & Reports

Branch: feature/cutting-parts-workflow
Database: v12 -> v13

## Decisions
- Interpretation B: historical buyPricePerMeter kept; currentPricePerMeter holds market price
- Same-category detection: fabricCategoryId on FabricRollEntity
- Execution order: infrastructure -> repository -> forms -> reports

---

## Patch 1 - Infrastructure (DB v13)

Changes:
1. Entities.kt: add FabricPriceHistoryEntity (table fabric_price_history)
2. AppDao.kt: add FabricPriceHistoryDao
3. AppDatabase.kt:
   - add entity to list
   - version = 12 -> version = 13
   - add abstract fun fabricPriceHistoryDao()
   - add MIGRATION_12_13
   - register in addMigrations

No changes to FabricRollEntity - all required fields already exist.

---

## Patch 2 - Repository Price Propagation

New function:
    suspend fun recordFabricPurchaseAndPropagate(
        rollId: Long,
        newPricePerMeter: Long,
        newPricePerKg: Long,
        supplierName: String,
        operator: String = "manager"
    ): Pair<Boolean, String>

Logic:
1. Fetch triggering roll
2. If it has fabricCategoryId, fetch all rolls in that category
3. Update currentPricePerMeter/Kg on all of them to the new price
4. Do NOT touch buyPricePerMeter/Kg (historical)
5. Insert one row into fabric_price_history with affectedRollCount
6. Call updateInProgressProductsAfterPriceChange

UI: confirmation dialog "Update N other rolls in this category?"

---

## Patch 3 - Redesign QuickRollConsumeForm (Step 1)

Roll header:
- Stock in kg + meters
- Cutting parts summary (dates, counts)
- Purchase price + shipping cost

Fields:
- Model select from ModelStandardEntity or manual
- Work quantity
- Consumption: automatic kg <-> m conversion
- Sewing wage from FactorySettingsEntity
- Profit: auto convert percent <-> amount
- Consumables: auto from BOM
- Extra costs
- Suggested sale price = total cost + fabric cost (editable)
- Status: Cut / Sewing / Ready to ship

Fabric price basis: highest purchase price in fabricCategoryId or most recent

---

## Patch 4 - Redesign QuickReadyGoodsForm (Step 2)

Read automatically from data captured in Patch 3:
- Roll header (summary)
- Model + quantity (default from Patch 3)
- Consumption, wage, consumables, sale price - DISPLAY ONLY
- Allow editing only sale price
- Final commit + history

---

## Patch 5 - Reports

1. Price history chart per category (from fabric_price_history)
2. Actual vs replacement profit report:
   - Actual: actualCostPerMeter (historical buyPricePerMeter)
   - Replacement: currentPricePerMeter (market)
3. Timeline stats

---

## Patching Rules
- one topic per patch
- dry-run first
- brace balance before commit
- no sed (RTL issues)
- wait for green GitHub Actions before next patch
