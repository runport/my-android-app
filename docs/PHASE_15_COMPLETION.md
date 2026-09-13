# Phase 15 — Completion Notes

## Repository
- Repo: github.com/runport/my-android-app
- Branch: feature/cutting-parts-workflow
- Build: GitHub Actions (Gradle 9.3.1, Java 17)
- DB version: 13

## Commits (in order)
1. `81e5d1d` Patch 1: FabricPriceHistoryEntity + DAO + DB v13 + MIGRATION_12_13
2. `ba73743` Patch 2: Repository propagation + ViewModel
3. `d206604` Patch 2f: comment fix
4. `231c105` Patch 3: consumeFabricRoll +4 params + status selector + extra costs
5. `47f06dc` Patch 3B: auto-fill from factorySettings + bidirectional profit
6. `0fa5841` Patch 3C: cutting parts summary + tailor auto-fill from standards
7. `b6aef62` Patch 3D: editable sale price carried to Ready Goods
8. (hash)   Patch 3E: BOM auto-fill for accessories cost
9. (hash)   Patch 4A: remove Add Product button from QuickReadyGoodsForm
10. `76ab98f` Patch 4B: auto-populate lines + rename to "کارهای آماده"
11. `8259865` Patch 4C: edit toggle + ProductionEntity sync
12. `e42582d` Patch 4D: fix double-deduction + status filter + convert button
13. `5a2562f` Patch 4Df: newRemaining scope fix

## Key Decisions
- **Interpretation B**: `buyPricePerMeter` historical preserved; `currentPricePerMeter` holds market price
- **Same-category detection**: `FabricRollEntity.fabricCategoryId`
- **Auto-populate in Step 2**: from `viewModel.productions` filtered by `rollId` and `statusFilter`
- **Avoid double-deduction**: in `submitMultiProductReadyGoods`, only items with `productionId == 0` deduct from roll
- **statusFilter**: default "در حال دوخت"; chips "برش خورده" / "در حال دوخت"

## Key Files
- `app/src/main/java/com/example/data/model/Entities.kt`
  - `FabricPriceHistoryEntity` (~ line 930)
  - `MultiProductReadyItem.productionId` (~ line 719)
- `app/src/main/java/com/example/data/dao/AppDao.kt`
  - `FabricPriceHistoryDao` (~ line 666)
  - `FabricRollDao.getRollsByCategory`
  - `ProductionDao.getProductionById`
- `app/src/main/java/com/example/data/database/AppDatabase.kt`
  - `MIGRATION_12_13` (~ line 858); `version = 13`
- `app/src/main/java/com/example/data/repository/ManufacturingRepository.kt`
  - `recordFabricPurchaseAndPropagate` (~ line 3970)
  - `getBomCostPerItem` (~ line 3968)
  - `submitMultiProductReadyGoods` (~ line 1572) — with `totalNewMeters` fix
- `app/src/main/java/com/example/viewmodel/ManufacturingViewModel.kt`
  - `recordFabricPurchaseAndPropagate`
  - `loadBomCostPerItem(productId, onResult)`
- `app/src/main/java/com/example/ui/dialogs/QuickActionSheets.kt`
  - `QuickRollConsumeForm` (~ line 4053)
  - `QuickReadyGoodsForm` (~ line 1083)
  - `QuickActionsModalBottomSheet` (~ line 122)
- `app/src/main/java/com/example/ui/screens/AnalyticsScreen.kt`
  - `FabricReportSection` (~ line 1308)
- `app/src/main/java/com/example/data/model/ReportModels.kt`
  - `FabricReportData` (~ line 156)

## Not Yet Manually Tested
- Patch 4D (double-deduction fix) on device
- Auto-populate from ProductionEntity in Step 2 (only dry-run)
- Convert-to-ready button in both forms

## Remaining Work
- Patch 5B: fabric price history chart (Canvas)
- Manual APK test + fix bugs if any

## Technical Notes
- `FinancialCalculationService.generateFabricReport(fabs, rolls)` — generates the fabric report
- `FabricReportData` in `ReportModels.kt:156`
- `ReportCategory.FABRIC` in `ManufacturingViewModel.kt:62`
- `FabricReportSection` in `AnalyticsScreen.kt:1308`
- `FabricPriceHistoryDao` already exists in `AppDao.kt` but not yet used in ViewModel

## Patching Rules (repeated from CONVENTIONS)
- One topic per patch
- Dry-run first
- Brace balance check
- No `sed` on RTL files
- Wait for green GitHub Actions before next patch
