#!/usr/bin/env python3
"""Generate docs/PHASE_16_PLAN.md — full spec for Phase 16."""

from pathlib import Path

PLAN = """# Phase 16 — Master Plan

## Goal
Bring the calculation engine and navigation in line with the user's real
business model (fabric rolls, accessories, combined freight bills,
reservation cycle). Fix broken buttons in `QuickSaleForm` and unify profit
calculation.

## User's Business Model (canonical)
1. Buy fabric rolls (kg-based); pay freight; freight is allocated per roll
   and per kg.
2. Buy accessories (kg, count, meter, yard — mixed units); pay freight;
   freight is allocated *either* by value (default), by weight, or
   manually per line.
3. Freight bills can mix fabric rolls and accessories. Total freight must
   be split across all lines by chosen method.
4. Cut work -> "برش خورده" -> "در حال دوخت" -> into warehouse.
5. Customer orders -> deduct from warehouse.
6. If unpaid -> reservation for configurable period (default 7 days).
   Auto-return to warehouse after that. Can be released early by user.
7. Settled order -> goes to "sold & settled" for that customer.

## Final Price Formula
    price = fabricCost
          + fabricFreightShare
          + accessoriesCost (from BOM)
          + accessoriesFreightShare
          + tailorWage
          + overheadCost
          + profit (percent OR amount, toggle)
    -> editable by user before saving

## Decisions (confirmed with user)
- Profit: single source of truth `FactorySettings.targetProfitMarginPercent`,
  editable as percent OR amount with live conversion.
- Reservation: configurable in `FactorySettings` (default 7 days).
- Freight allocation: three modes selectable per bill:
  * value-based (default)
  * weight-based
  * manual (per line percent)
- Sale form: no meter/kg price toggles. Flat unit price only.

---

## Patch List (12 patches, 4 sessions)

### Session 1 — Broken buttons + unification

**Patch 16.1 — QuickSaleForm cleanup**
Files: `QuickActionSheets.kt`
- Remove "قیمت بر اساس متر / عدد" toggle
- Remove "قیمت بر اساس کیلوگرم" toggle
- Customer field -> `ItemSelectionPopupDialog` with `viewModel.customers`
- Add "+ افزودن مشتری جدید" -> opens `QuickCustomerForm`
- Product field -> `ItemSelectionPopupDialog` filtered to
  `inventory.category == "محصولات آماده"`
- Add "+ افزودن محصول جدید" if useful

**Patch 16.2 — Sale deducts stock**
Files: `ManufacturingRepository.kt`
- In `insertSaleOrder` / `createSaleOrderWithReservation`:
  - check `availableForSale >= qty`
  - decrement `availableForSale` and `readyForShipment`
  - insert `InventoryLedgerEntity` with type SALE_OUT
  - if not fully paid -> increment `reservedQuantity` instead

**Patch 16.3 — Multi-item sale**
Files: `QuickActionSheets.kt`
- Convert `QuickSaleForm` to multi-line (list of `SaleLineDraft`)
- `+` button to add a line
- Cart summary with subtotal, discount, total
- Confirm button submits all lines in one order

**Patch 16.4 — Fix "رزرو جدید" button**
Files: `MoreHubScreen.kt`, `QuickActionSheets.kt`
- Find the reserve button and wire it to `ReserveOrderDialog`
  or to `QuickSaleForm` with `isPreOrder = true`

**Patch 16.5 — Unify profit source**
Files: `FinancialCalculationService.kt`, `QuickActionSheets.kt`,
       `ManufacturingViewModel.kt`
- All forms read `FactorySettings.targetProfitMarginPercent`
- Percent <-> amount toggle in UI

### Session 2 — Freight allocation

**Patch 16.6 — Freight allocation with 3 modes**
Files: `Entities.kt` (`ShippingExpenseEntity`, `WaybillItemEntity`),
       `ManufacturingRepository.kt` (`allocateWaybillShipping`)
- Add `allocationMethod: String = "VALUE"` to bill
- Implement value/weight/manual
- Store per-line allocated amount

**Patch 16.7 — Accessory freight tracking**
Files: `AccessoryPurchaseEntity`, `ManufacturingRepository.kt`
- Add `allocatedShippingCost: Long = 0L` to `AccessoryPurchaseEntity`
- DB v14 migration
- Propagate into `ProductionConsumableEntity`

### Session 3 — Final price engine

**Patch 16.8 — DB v14 schema**
Files: `Entities.kt`, `AppDatabase.kt`
- `ProductionEntity` add:
  - `fabricShippingShare: Long = 0L`
  - `accessoriesShippingShare: Long = 0L`
  - `sellingPrice: Long = 0L`
  - `overheadCostPerItem: Long = 0L`
- `ProductionConsumableEntity` add:
  - `allocatedShippingCost: Long = 0L`
- MIGRATION_13_14

**Patch 16.9 — Final price engine + UI**
Files: `FinancialCalculationService.kt`, `QuickActionSheets.kt`
- New function `calculateFinalUnitPrice(...)`
- Display in `QuickRollConsumeForm` and `QuickReadyGoodsForm`
- Editable field

### Session 4 — Reservation + polish

**Patch 16.10 — Reservation duration setting**
Files: `FactorySettingsEntity`, `QuickSettingsForm`
- Add `reservationDays: Int = 7`
- DB v15 migration (or combine into v14)

**Patch 16.11 — Reservation lifecycle**
Files: `ManufacturingRepository.kt`, `MainActivity.kt`
- `ReservationEntity` if needed
- Auto-return via `WorkManager` daily check
- Manual release button
- UI badge showing reserved vs available

**Patch 16.12 — Navigation redesign (optional)**
- Merge duplicate entry points
- One place per task

---

## Key Files & Anchors

| File | Purpose | Notes |
|---|---|---|
| `Entities.kt` (1262 lines) | All data classes | `FabricRollEntity` L518, `ProductionEntity` L110, `AccessoryPurchaseEntity` L586, `ProductionConsumableEntity` L701 |
| `AppDao.kt` (979) | Room DAOs | 30 interfaces — `SaleOrderDao` L180, `InventoryDao` L150, `CustomerDao` L201 |
| `AppDatabase.kt` (2030) | Room DB | `version = 13` L140, `MIGRATION_12_13` L858 |
| `ManufacturingRepository.kt` (4693) | Business logic | `insertSaleOrder` L216, `createSaleOrderWithReservation` L1826, `consumeFabricRoll` L743, `submitMultiProductReadyGoods` L1572, `allocateWaybillShipping` L1189 |
| `ManufacturingViewModel.kt` (2782) | State | `MainTab` L46, `ReportCategory` L53, `QuickActionType` L80 |
| `QuickActionSheets.kt` (6490) | All forms | `QuickSaleForm` ~L2151, `QuickRollConsumeForm` ~L4053, `QuickReadyGoodsForm` ~L1083, `QuickWarehouseHub` ~L417, `QuickCustomerForm` ~L2966 |
| `FinancialCalculationService.kt` (1312) | Math | `generateFabricReport` L1007, `calculateDetailedProductCost` |
| `ReportModels.kt` (240) | Report data classes | `FabricReportData` L159 |
| `MoreHubScreen.kt` (2523) | Sub-navigation | |
| `AnalyticsScreen.kt` (1862) | Reports screen | `FabricReportSection` L1308 |

## Repository
- `github.com/runport/my-android-app`
- Branch: `feature/cutting-parts-workflow`
- Package: `com.aistudio.manufacturing.vrtqxk`
- Activity: `com.example.MainActivity`
- Build: GitHub Actions (Gradle 9.3.1, Java 17)

## Conventions (from CONVENTIONS.md)
- One topic per patch
- Dry-run first, then apply
- Brace balance check
- No `sed` on RTL files (use Python)
- Wait for green GitHub Actions before next patch

## Reference: Phase 15 Notes
See `docs/PHASE_15_COMPLETION.md` for prior decisions and commits.

## How to work (for the next chat)
1. Run `diagnose_phase16.sh` first to capture current state
2. Send output to assistant
3. Assistant writes `scripts/phase16_patchN.py`
4. Run dry-run, then apply
5. Diff, commit, push
6. Wait for green
7. Next patch
"""

out = Path("docs")
out.mkdir(exist_ok=True)
(out / "PHASE_16_PLAN.md").write_text(PLAN, encoding="utf-8")
print("WROTE:", out / "PHASE_16_PLAN.md")
