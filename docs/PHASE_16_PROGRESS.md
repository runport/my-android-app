# Phase 16 — Progress & Handoff

**Last updated:** after commit `4ef738f`
**Branch:** `feature/cutting-parts-workflow`
**DB version:** 15
**Status:** waiting for user demo test

---

## What is done in Phase 16

| # | Title | Commit |
|---|---|---|
| 16.1a | QuickSaleForm cleanup — remove pricing toggles | `7a6ceda` |
| 16.1b-prep | add missing Dialog imports | `bffbca0` |
| 16.1b | QuickSaleForm inline QuickCustomerForm | `7852afb` |
| 16.2a | insertSaleOrder — paid/unpaid + ledger | `e40d5e1` |
| 16.2b | createSaleOrderWithReservation fix + paid branch | `88c8c22` |
| 16.3a | multi-line sale backend | `6b3b072` |
| 16.3b-prep | SaleLineDraft + imports | `f8af9e2` |
| 16.3b | multi-line QuickSaleForm UI | `1de27ce` |
| 16.4 | wire ReserveOrderDialog in MainActivity | `28c6e5d` |
| 16.5 | unify profit source | `29551ab` |
| 16.6 | VALUE + MANUAL freight allocation UI | `431271e` |
| 16.7 | accessory freight tracking (DB v14) | `2f8267c` |
| 16.8 | ProductionEntity final-price fields (DB v15) | `7b3f1a1` |
| 16.9a | calculateFinalUnitPrice() engine | `155fce7` |
| 16.9b1 | QuickRollConsumeForm uses engine | `e488a05` |
| 16.9b3a | snapshot shipping/overhead/salePrice | `7468e99` |
| 16.9b3b | QuickReadyGoodsForm default via engine | `6451f2f` |
| Demo1 | DemoSeeder.kt + Repository wrappers | `b361a99` |
| Demo3 | UI buttons to seed/clear demo data | `4ef738f` |

---

## Current state

- Demo seeder (`DemoSeeder.kt`, 356 lines) inserts the user's real data:
  - 3 suppliers (Yazdani Nassaji, Sibil, Hooshiyar Souri)
  - 7 fabric rolls (`DEMO-ROL-1..7`)
  - 6 materials (`DEMO-MAT-*`): kesh 2.2, navar 1, navar konafi, kesh 4, band shimel, zip
  - 2 customers (Babak: 3 settled sales; Heydari: 1 reservation)
  - 3 products: six-pocket (465k), konafi bag (520k), navar keshi (485k)
  - 3 waybills: 1.45M + 800k + 350k
- UI buttons in `MoreHubScreen` under "مدیریت داده‌ها":
  - Seed demo (green) with confirmation AlertDialog
  - Clear demo (outlined red) with confirmation AlertDialog
- `clearDemo()` deletes only rows with `DEMO-*` / `PROD-*` prefixes.

**Waiting on:** user installing the latest APK and running 6 manual tests.
Results not yet reported.

---

## What is left in Phase 16

| # | Title | Size | Priority |
|---|---|---|---|
| 16.10 | Reservation duration in FactorySettings (DB v16) | ~30 lines | required |
| 16.11 | Reservation lifecycle + WorkManager auto-return | ~150 lines | required |
| 16.12 | Navigation redesign | large | optional |
| 16.5b | remove hardcoded *1.55 markup in ProductionEntity | ~10 lines | polish |

---

## Working conventions (do not break)

1. One topic per patch — split large patches into a/b/c.
2. `--dry-run` first, then `--apply`.
3. Python scripts for edits, never `sed` (RTL hazard).
4. Brace-balance check before commit.
5. Wait for green GitHub Actions before the next patch.
6. Scripts live under `scripts/` and are gitignored. Only source files are committed.
7. Assistant has no shell/GitHub access — user runs every command and pastes raw output.

---

## Key files (approx. sizes)

| File | Lines | Role |
|---|---|---|
| Entities.kt | ~1400 | all data classes |
| AppDatabase.kt | ~1000 | Room + migrations |
| AppDao.kt | ~980 | all DAOs |
| ManufacturingRepository.kt | ~4948 | business logic |
| ManufacturingViewModel.kt | ~2800 | state |
| QuickActionSheets.kt | ~6551 | all forms |
| FinancialCalculationService.kt | ~1349 | calculations |
| MoreHubScreen.kt | ~2585 | sub-navigation + data mgmt |
| DemoSeeder.kt | 356 | demo data |

**Migration chain:** MIGRATION_4_5 ... MIGRATION_14_15

**New functions added in Phase 16:**
- FinancialCalculationService.calculateFinalUnitPrice(...)
- ManufacturingRepository.insertMultiLineSaleOrder(...)
- ManufacturingRepository.insertSaleOrder(...) — paid/unpaid branch
- ManufacturingRepository.seedDemo() / clearDemo()
- ManufacturingViewModel.submitMultiLineSale(...) / seedDemo() / clearDemo()

---

## Known caveats

1. ProductionEntity.estimatedSalePricePerItem still has hardcoded *1.55 markup.
   16.9b3b bypasses it in QuickReadyGoodsForm. Getter cleanup deferred to 16.5b.
2. updateSaleOrderStatus in ManufacturingRepository has no caller (dead code).
   16.11 will revive it.
3. createSaleOrderWithReservation lookup bug fixed in 16.2b.
4. MoreHubScreen.saveFactorySettings signature:
   (Long, Long, Double, Long, Long, String, String, String, Int, Double, Int, Double) -> Unit
   Adding a new FactorySettings field requires updating this signature.
5. seedDemo refuses if DEMO-ROL-1 exists. clearDemo only deletes demo rows.

---

## How to start the next chat

Paste at the top of the next chat:

    سلام! ادامه پروژه Android.

    مخزن: github.com/runport/my-android-app
    شاخه: feature/cutting-parts-workflow
    آخرین commit: 4ef738f
    سند فاز: docs/PHASE_16_PROGRESS.md (قبلاً خونده شده)

    [وضعیت تست دمو / باگ‌ها اینجا]

    قواعد: dry-run first، Python نه sed، brace check، صبر تا Actions سبز.

Then:
- "تست OK بود" → go to 16.10 (Reservation duration, DB v16).
- "این باگ‌ها رو دیدم" → fix each bug first, then 16.10.

---

## Quick commands for the next chat

    git log --oneline -5
    git status
    gh run list --branch feature/cutting-parts-workflow --limit 3
