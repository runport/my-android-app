package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FabricRollEntity
import com.example.data.model.ShippingAllocationMethod
import com.example.data.model.ShippingExpenseEntity
import com.example.ui.components.CurrencyHelper
import com.example.ui.theme.*

/**
 * Unit mode for fabric consumption (Meters vs Kilograms)
 */
enum class FabricConsumptionUnit(val label: String, val shortUnit: String) {
  METERS("بر حسب متراژ", "متر"),
  KILOGRAMS("بر حسب وزن", "کیلو")
}

/**
 * Modular fabric consumption input selector supporting Meters and Kilograms
 */
@Composable
fun FabricConsumptionInput(
  selectedRoll: FabricRollEntity?,
  consumptionUnit: FabricConsumptionUnit,
  onUnitChange: (FabricConsumptionUnit) -> Unit,
  amountText: String,
  onAmountChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  label: String = "میزان مصرف پارچه"
) {
  val customColors = LocalCustomColors.current
  val amount = amountText.toDoubleOrNull() ?: 0.0
  val metersPerKg = selectedRoll?.metersPerKg?.takeIf { it > 0.0 } ?: 3.0

  val effectiveMeters = if (consumptionUnit == FabricConsumptionUnit.METERS) {
    amount
  } else {
    amount * metersPerKg
  }

  val equivalentOtherUnit = if (consumptionUnit == FabricConsumptionUnit.METERS) {
    if (metersPerKg > 0.0) amount / metersPerKg else 0.0
  } else {
    amount * metersPerKg
  }

  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
    // Unit Mode Switcher Toggle
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(customColors.cardElevated)
        .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
        .padding(3.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      FabricConsumptionUnit.values().forEach { unit ->
        val isSelected = consumptionUnit == unit
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) AccentIndigo else Color.Transparent)
            .clickable { onUnitChange(unit) }
            .padding(vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = unit.label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else customColors.textSecondary
          )
        }
      }
    }

    // Input Text Field
    ExecutiveTextField(
      label = "$label (${consumptionUnit.shortUnit})",
      value = amountText,
      keyboardType = KeyboardType.Decimal,
      onValueChange = onAmountChange
    )

    // Dynamic Conversion Helper Badge
    if (amount > 0.0 && selectedRoll != null) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(AccentCyan.copy(alpha = 0.1f))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (consumptionUnit == FabricConsumptionUnit.METERS) {
              "معادل وزنی: ${String.format(java.util.Locale.US, "%.2f", equivalentOtherUnit)} کیلوگرم"
            } else {
              "معادل متراژ: ${String.format(java.util.Locale.US, "%.2f", equivalentOtherUnit)} متر"
            },
            fontSize = 10.sp,
            color = AccentCyan,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "نرخ طاقه: ${String.format(java.util.Locale.US, "%.1f", metersPerKg)} متر/کیلو",
            fontSize = 9.sp,
            color = customColors.textMuted
          )
        }
      }
    }
  }
}

/**
 * Form for editing and deleting an existing Fabric Roll (طاقه پارچه)
 */
@Composable
fun EditFabricRollForm(
  roll: FabricRollEntity,
  allExpenses: List<ShippingExpenseEntity> = emptyList(),
  onUpdate: (FabricRollEntity) -> Unit,
  onDelete: (Long) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  var showDeleteConfirm by remember { mutableStateOf(false) }

  var rollCode by remember { mutableStateOf(roll.rollCode) }
  var fabricType by remember { mutableStateOf(roll.fabricType) }
  var fabricCode by remember { mutableStateOf(roll.fabricCode) }
  var color by remember { mutableStateOf(roll.color) }
  var supplierName by remember { mutableStateOf(roll.supplierName) }
  var batchNumber by remember { mutableStateOf(roll.batchNumber) }
  var status by remember { mutableStateOf(roll.status) }

  var initialMetersText by remember { mutableStateOf(roll.initialMeters.toString()) }
  var remainingMetersText by remember { mutableStateOf(roll.remainingMeters.toString()) }
  var weightKgText by remember { mutableStateOf(roll.weightKg.toString()) }
  var buyPricePerMeterText by remember { mutableStateOf(roll.buyPricePerMeter.toString()) }
  var buyPricePerKgText by remember { mutableStateOf(roll.buyPricePerKg.toString()) }
  var allocatedShippingCostText by remember { mutableStateOf(roll.allocatedShippingCost.toString()) }

  val initialMeters = initialMetersText.toDoubleOrNull() ?: 0.0
  val remainingMeters = remainingMetersText.toDoubleOrNull() ?: 0.0
  val weightKg = weightKgText.toDoubleOrNull() ?: 0.0
  val buyPricePerMeter = buyPricePerMeterText.toLongOrNull() ?: 0L
  val buyPricePerKg = buyPricePerKgText.toLongOrNull() ?: 0L
  val allocatedShippingCost = allocatedShippingCostText.toLongOrNull() ?: 0L

  val totalBaseCost = (initialMeters * buyPricePerMeter).toLong()
  val totalCostWithShipping = totalBaseCost + allocatedShippingCost
  val effectiveCostPerMeter = if (initialMeters > 0.0) (totalCostWithShipping / initialMeters).toLong() else buyPricePerMeter

  val assignedExpense = allExpenses.find { it.id == roll.shippingExpenseId }

  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = { Text("حذف طاقه پارچه از انبار", fontWeight = FontWeight.Bold) },
      text = { Text("آیا از حذف طاقه ${roll.rollCode} (${roll.fabricType}) اطمینان دارید؟ این عملیات غیرقابل بازگشت است.") },
      confirmButton = {
        Button(
          onClick = {
            showDeleteConfirm = false
            onDelete(roll.id)
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
        ) {
          Text("بله، حذف شود", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirm = false }) {
          Text("انصراف")
        }
      }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("ویرایش مشخصات طاقه پارچه", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        Text("شناسه: ${roll.rollCode} | تاریخ ثبت: ${roll.inboundDate}", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted, fontSize = 11.sp)
      }
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    // Identifiers
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "شناسه / کد طاقه", value = rollCode, onValueChange = { rollCode = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "کد پارچه", value = fabricCode, onValueChange = { fabricCode = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.3f)) {
        ExecutiveTextField(label = "جنس / نوع پارچه", value = fabricType, onValueChange = { fabricType = it })
      }
      Box(modifier = Modifier.weight(0.7f)) {
        ExecutiveTextField(label = "رنگ", value = color, onValueChange = { color = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "نام تأمین‌کننده / بافنده", value = supplierName, onValueChange = { supplierName = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "شماره بچ / پارت", value = batchNumber, onValueChange = { batchNumber = it })
      }
    }

    // Status Selector
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf("موجود", "در حال مصرف", "پایان یافته").forEach { s ->
        val isSelected = status == s
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AccentIndigo.copy(alpha = 0.2f) else customColors.cardElevated)
            .border(1.dp, if (isSelected) AccentIndigo else customColors.border, RoundedCornerShape(8.dp))
            .clickable { status = s }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(s, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) AccentIndigo else customColors.textSecondary)
        }
      }
    }

    // Quantitative metrics
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "متراژ اولیه (متر)", value = initialMetersText, keyboardType = KeyboardType.Decimal, onValueChange = { initialMetersText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "متراژ باقیمانده (متر)", value = remainingMetersText, keyboardType = KeyboardType.Decimal, onValueChange = { remainingMetersText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "وزن طاقه (کیلو)", value = weightKgText, keyboardType = KeyboardType.Decimal, onValueChange = { weightKgText = it })
      }
    }

    // Pricing
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت خرید هر متر (تومان)", value = buyPricePerMeterText, keyboardType = KeyboardType.Number, onValueChange = { buyPricePerMeterText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت خرید هر کیلو (تومان)", value = buyPricePerKgText, keyboardType = KeyboardType.Number, onValueChange = { buyPricePerKgText = it })
      }
    }

    // Shipping Cost Section
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, AccentBlue.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
            Text("هزینه باربری این طاقه (بر اساس بارنامه)", style = MaterialTheme.typography.labelMedium, color = AccentBlue, fontWeight = FontWeight.Bold)
          }
          if (assignedExpense != null) {
            Text("بارنامه: ${assignedExpense.trackingNumber}", fontSize = 10.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
          }
        }

        ExecutiveTextField(
          label = "کرایه باربری سرانه اختصاص‌یافته (تومان)",
          value = allocatedShippingCostText,
          keyboardType = KeyboardType.Number,
          onValueChange = { allocatedShippingCostText = it }
        )

        Text(
          "نکته: نرخ باربری بر اساس بارنامه اختصاص یافته تعیین می‌شود؛ در صورت نیاز به تعدیل دستی می‌توانید این رقم را ویرایش کنید.",
          fontSize = 10.sp,
          color = customColors.textMuted
        )
      }
    }

    // Financial Calculation Summary Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.cardElevated)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("خلاصه بهای تمام شده طاقه", style = MaterialTheme.typography.labelMedium, color = AccentCyan, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("بهای خرید اولیه پارچه:", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(totalBaseCost), fontSize = 11.sp, color = customColors.textPrimary)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("سهم کرایه باربری:", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(allocatedShippingCost), fontSize = 11.sp, color = AccentBlue)
        }
        HorizontalDivider(color = customColors.border)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("بهای تمام شده کل در انبار:", fontSize = 11.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text(CurrencyHelper.formatToman(totalCostWithShipping), fontSize = 12.sp, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("بهای تمام شده واقعی هر متر با باربری:", fontSize = 11.sp, color = customColors.textMuted)
          Text("${CurrencyHelper.formatToman(effectiveCostPerMeter)} / متر", fontSize = 11.sp, color = AccentIndigo, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Action Buttons: Save & Delete
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
      Button(
        onClick = {
          val updated = roll.copy(
            rollCode = rollCode.trim(),
            fabricType = fabricType.trim(),
            fabricCode = fabricCode.trim(),
            color = color.trim(),
            supplierName = supplierName.trim(),
            batchNumber = batchNumber.trim(),
            status = status,
            initialMeters = initialMeters,
            remainingMeters = remainingMeters,
            weightKg = weightKg,
            buyPricePerMeter = buyPricePerMeter,
            buyPricePerKg = buyPricePerKg,
            allocatedShippingCost = allocatedShippingCost
          )
          onUpdate(updated)
        },
        modifier = Modifier
          .weight(1.3f)
          .height(48.dp)
          .testTag("save_fabric_roll_edit_action"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
          Text("ذخیره تغییرات طاقه", fontWeight = FontWeight.Bold, color = Color.Black)
        }
      }

      OutlinedButton(
        onClick = { showDeleteConfirm = true },
        modifier = Modifier
          .weight(0.7f)
          .height(48.dp)
          .testTag("delete_fabric_roll_action"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDanger),
        border = androidx.compose.foundation.BorderStroke(1.dp, StatusDanger)
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Delete, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(16.dp))
          Text("حذف طاقه", fontWeight = FontWeight.Bold, color = StatusDanger, fontSize = 11.sp)
        }
      }
    }
  }
}

/**
 * Form for editing and deleting an existing Shipping Expense (بارنامه و هزینه باربری)
 * and managing which fabric rolls it is allocated to.
 */
@Composable
fun EditShippingExpenseForm(
  expense: ShippingExpenseEntity,
  allRolls: List<FabricRollEntity>,
  onUpdate: (ShippingExpenseEntity, List<Long>) -> Unit,
  onDelete: (Long) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  var showDeleteConfirm by remember { mutableStateOf(false) }

  var trackingNumber by remember { mutableStateOf(expense.trackingNumber) }
  var title by remember { mutableStateOf(expense.title) }
  var carrierName by remember { mutableStateOf(expense.carrierName) }
  var totalAmountText by remember { mutableStateOf(expense.totalAmount.toString()) }
  var inboundType by remember { mutableStateOf(expense.inboundType) }
  var allocationMethod by remember {
    mutableStateOf(
      try { ShippingAllocationMethod.valueOf(expense.allocationMethod) } catch (_: Exception) { ShippingAllocationMethod.PER_ITEM }
    )
  }

  var itemCountText by remember { mutableStateOf(expense.itemCount.toString()) }
  var totalWeightKgText by remember { mutableStateOf(expense.totalWeightKg.toString()) }
  var totalQuantityText by remember { mutableStateOf(expense.totalQuantity.toString()) }
  var notes by remember { mutableStateOf(expense.notes) }

  // Which rolls are allocated to this expense
  val initialSelectedRollIds = remember {
    allRolls.filter { it.shippingExpenseId == expense.id }.map { it.id }.toMutableStateList()
  }

  val totalAmount = totalAmountText.toLongOrNull() ?: 0L
  val itemCount = if (initialSelectedRollIds.isNotEmpty()) initialSelectedRollIds.size else (itemCountText.toIntOrNull() ?: 0)
  val totalWeightKg = totalWeightKgText.toDoubleOrNull() ?: 0.0
  val totalQuantity = totalQuantityText.toDoubleOrNull() ?: 0.0

  val costPerUnit = if (initialSelectedRollIds.isNotEmpty()) {
    totalAmount / initialSelectedRollIds.size
  } else {
    when (allocationMethod) {
      ShippingAllocationMethod.PER_ITEM, ShippingAllocationMethod.EQUAL -> if (itemCount > 0) totalAmount / itemCount else totalAmount
      ShippingAllocationMethod.PER_WEIGHT, ShippingAllocationMethod.BY_WEIGHT -> if (totalWeightKg > 0.0) (totalAmount / totalWeightKg).toLong() else 0L
      ShippingAllocationMethod.PER_QUANTITY, ShippingAllocationMethod.BY_VOLUME -> if (totalQuantity > 0.0) (totalAmount / totalQuantity).toLong() else 0L
      ShippingAllocationMethod.WEIGHTED, ShippingAllocationMethod.BY_PURCHASE_VALUE, ShippingAllocationMethod.MANUAL -> totalAmount
    }
  }

  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = { Text("حذف بارنامه و رکورد باربری", fontWeight = FontWeight.Bold) },
      text = { Text("آیا از حذف این بارنامه (${expense.trackingNumber}) اطمینان دارید؟ با حذف، سهم باربری طاقه‌های اختصاص‌یافته صفر خواهد شد.") },
      confirmButton = {
        Button(
          onClick = {
            showDeleteConfirm = false
            onDelete(expense.id)
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
        ) {
          Text("بله، حذف بارنامه", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirm = false }) {
          Text("انصراف")
        }
      }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("ویرایش بارنامه و تخصیص اقلام", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        Text("رهگیری: ${expense.trackingNumber} | تاریخ: ${expense.date}", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted, fontSize = 11.sp)
      }
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "شماره بارنامه/رهگیری", value = trackingNumber, onValueChange = { trackingNumber = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "نوع محموله", value = inboundType, onValueChange = { inboundType = it })
      }
    }

    ExecutiveTextField(label = "شرح محموله / بارنامه", value = title, onValueChange = { title = it })
    ExecutiveTextField(label = "نام باربری / شرکت حمل", value = carrierName, onValueChange = { carrierName = it })

    ExecutiveTextField(
      label = "مبلغ کل هزینه باربری (تومان)",
      value = totalAmountText,
      keyboardType = KeyboardType.Number,
      onValueChange = { totalAmountText = it }
    )

    // Allocation Method
    Text("روش تخصیص هزینه باربری:", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf(
        ShippingAllocationMethod.PER_ITEM to "بر اساس تعداد طاقه",
        ShippingAllocationMethod.PER_WEIGHT to "بر اساس وزن (کیلو)",
        ShippingAllocationMethod.PER_QUANTITY to "بر اساس متراژ"
      ).forEach { (method, label) ->
        val isSel = allocationMethod == method
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSel) AccentBlue.copy(alpha = 0.2f) else customColors.cardElevated)
            .border(1.dp, if (isSel) AccentBlue else customColors.border, RoundedCornerShape(8.dp))
            .clickable { allocationMethod = method }
            .padding(vertical = 8.dp, horizontal = 4.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(label, fontSize = 10.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = if (isSel) AccentBlue else customColors.textSecondary)
        }
      }
    }

    // Allocation to Fabric Rolls
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("اقلام و طاقه‌های این بارنامه:", style = MaterialTheme.typography.labelMedium, color = AccentCyan, fontWeight = FontWeight.Bold)
          Text("${initialSelectedRollIds.size} طاقه انتخاب شده", fontSize = 11.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
        }

        if (allRolls.isEmpty()) {
          Text("طاقه‌ای در انبار ثبت نشده است.", fontSize = 11.sp, color = customColors.textMuted)
        } else {
          allRolls.forEach { roll ->
            val isChecked = initialSelectedRollIds.contains(roll.id)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isChecked) AccentBlue.copy(alpha = 0.12f) else customColors.cardElevated)
                .clickable {
                  if (isChecked) initialSelectedRollIds.remove(roll.id) else initialSelectedRollIds.add(roll.id)
                }
                .padding(horizontal = 8.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                  checked = isChecked,
                  onCheckedChange = { chk ->
                    if (chk) initialSelectedRollIds.add(roll.id) else initialSelectedRollIds.remove(roll.id)
                  },
                  colors = CheckboxDefaults.colors(checkedColor = AccentBlue)
                )
                Column {
                  Text("${roll.rollCode} - ${roll.fabricType}", fontSize = 11.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  Text("رنگ: ${roll.color} | متراژ: ${roll.remainingMeters}متر", fontSize = 10.sp, color = customColors.textMuted)
                }
              }

              if (isChecked && initialSelectedRollIds.isNotEmpty()) {
                val perRollCost = totalAmount / initialSelectedRollIds.size
                Text("+${CurrencyHelper.formatToman(perRollCost)}", fontSize = 10.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // Calculated Unit Cost Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.cardElevated)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("مبلغ کل کرایه حمل باربری:", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(totalAmount), fontSize = 12.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("سهم باربری هر طاقه منتسب‌شده:", fontSize = 11.sp, color = customColors.textMuted)
          Text(
            text = "${CurrencyHelper.formatToman(costPerUnit)} / هر طاقه",
            fontSize = 12.sp,
            color = AccentBlue,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    ExecutiveTextField(label = "توضیحات، راننده و شماره تلفن", value = notes, onValueChange = { notes = it })

    // Action buttons
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
      Button(
        onClick = {
          val updated = expense.copy(
            trackingNumber = trackingNumber.trim(),
            title = title.trim(),
            carrierName = carrierName.trim(),
            totalAmount = totalAmount,
            inboundType = inboundType.trim(),
            allocationMethod = allocationMethod.name,
            itemCount = if (initialSelectedRollIds.isNotEmpty()) initialSelectedRollIds.size else itemCount,
            totalWeightKg = totalWeightKg,
            totalQuantity = totalQuantity,
            costPerUnit = costPerUnit,
            notes = notes.trim()
          )
          onUpdate(updated, initialSelectedRollIds.toList())
        },
        modifier = Modifier
          .weight(1.3f)
          .height(48.dp)
          .testTag("save_shipping_expense_action"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
          Text("ذخیره و تخصیص مجدد", fontWeight = FontWeight.Bold, color = Color.White)
        }
      }

      OutlinedButton(
        onClick = { showDeleteConfirm = true },
        modifier = Modifier
          .weight(0.7f)
          .height(48.dp)
          .testTag("delete_shipping_expense_action"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDanger),
        border = androidx.compose.foundation.BorderStroke(1.dp, StatusDanger)
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Delete, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(16.dp))
          Text("حذف بارنامه", fontWeight = FontWeight.Bold, color = StatusDanger, fontSize = 11.sp)
        }
      }
    }
  }
}
