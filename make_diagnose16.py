from pathlib import Path
#!/usr/bin/env python3
"""Generate diagnose_phase16.sh"""

SCRIPT = r"""#!/usr/bin/env bash
set -u
ROOT="app/src/main/java/com/example"

echo "═══════════════════════════════════════════════════════════"
echo "  PHASE 16 — DIAGNOSTIC"
echo "  Date: $(date -u +%Y-%m-%dT%H:%M:%SZ)"
echo "  Branch: $(git rev-parse --abbrev-ref HEAD 2>/dev/null)"
echo "  Commit: $(git rev-parse --short HEAD 2>/dev/null)"
echo "═══════════════════════════════════════════════════════════"

section() {
  echo ""
  echo "───────────────────────────────────────────────────────────"
  echo "  $1"
  echo "───────────────────────────────────────────────────────────"
}

dump() {
  local file="$1"; local symbol="$2"; local ctx="${3:-0}"
  if [ ! -f "$file" ]; then echo "MISSING: $file"; return; fi
  local start
  start=$(grep -n "$symbol" "$file" | head -1 | cut -d: -f1)
  if [ -z "$start" ]; then echo "NOT FOUND: $symbol in $file"; return; fi
  local from=$((start - ctx))
  local to=$((start + 80))
  echo "--- $file — $symbol (lines $from-$to) ---"
  sed -n "${from},${to}p" "$file"
  echo ""
}

section "1. QuickSaleForm (full)"
dump "$ROOT/ui/dialogs/QuickActionSheets.kt" "fun QuickSaleForm"

section "2. QuickCustomerForm (full)"
dump "$ROOT/ui/dialogs/QuickActionSheets.kt" "fun QuickCustomerForm"

section "3. ItemSelectionPopupDialog"
IF=$(find "$ROOT" -name "*.kt" -exec grep -l "fun ItemSelectionPopupDialog" {} \; | head -1)
dump "$IF" "fun ItemSelectionPopupDialog" 3

section "4. QuickSaleForm call-site in QuickActionsModalBottomSheet"
sed -n '215,240p' "$ROOT/ui/dialogs/QuickActionSheets.kt"

section "5. submitSale in ViewModel"
dump "$ROOT/viewmodel/ManufacturingViewModel.kt" "fun submitSale" 3

section "6. insertSaleOrder in Repository"
dump "$ROOT/data/repository/ManufacturingRepository.kt" "suspend fun insertSaleOrder" 3

section "7. createSaleOrderWithReservation in Repository"
dump "$ROOT/data/repository/ManufacturingRepository.kt" "suspend fun createSaleOrderWithReservation" 3

section "8. ReserveOrderDialog"
dump "$ROOT/ui/dialogs/ReserveOrderDialog.kt" "fun ReserveOrderDialog" 3

section "9. Where ReserveOrderDialog is invoked"
grep -rn "ReserveOrderDialog" "$ROOT" | head -20

section "10. Where رزرو جدید / reserve button is wired"
grep -rn "رزرو جدید\|رزرو سفارش\|RESERVE_ORDER" "$ROOT" | head -20

section "11. SaleOrderEntity model"
dump "$ROOT/data/model/Entities.kt" "data class SaleOrderEntity" 2

section "12. InventoryEntity model"
dump "$ROOT/data/model/Entities.kt" "data class InventoryEntity" 2

section "13. FactorySettingsEntity model"
dump "$ROOT/data/model/Entities.kt" "data class FactorySettingsEntity" 2

section "14. AccessoryPurchaseEntity model"
dump "$ROOT/data/model/Entities.kt" "data class AccessoryPurchaseEntity" 2

section "15. ProductionConsumableEntity model"
dump "$ROOT/data/model/Entities.kt" "data class ProductionConsumableEntity" 2

section "16. ShippingExpenseEntity model"
dump "$ROOT/data/model/Entities.kt" "data class ShippingExpenseEntity" 2

section "17. WaybillItemEntity model"
dump "$ROOT/data/model/Entities.kt" "data class WaybillItemEntity" 2

section "18. allocateWaybillShipping in Repository"
dump "$ROOT/data/repository/ManufacturingRepository.kt" "suspend fun allocateWaybillShipping" 3

section "19. submitMultiItemWaybill in Repository"
dump "$ROOT/data/repository/ManufacturingRepository.kt" "suspend fun submitMultiItemWaybill" 3

section "20. Current DB version and migrations"
grep -n "version = \|MIGRATION_" "$ROOT/data/database/AppDatabase.kt" | head -40

section "21. Where 'قیمت بر اساس' appears"
grep -rn "قیمت بر اساس" "$ROOT" | head -20

section "22. Dashboard / MainActivity entry points"
sed -n '90,140p' "$ROOT/MainActivity.kt"

echo ""
echo "═══════════════════════════════════════════════════════════"
echo "  END OF DIAGNOSTIC"
echo "═══════════════════════════════════════════════════════════"
"""

out = Path("diagnose_phase16.sh")
out.write_text(SCRIPT, encoding="utf-8")
print("WROTE:", out)
print("Now run: chmod +x diagnose_phase16.sh")
