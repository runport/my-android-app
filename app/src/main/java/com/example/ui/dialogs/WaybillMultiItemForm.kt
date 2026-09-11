package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShippingAllocationMethod
import com.example.ui.components.CurrencyHelper
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.util.PersianDateHelper
import com.example.viewmodel.ManufacturingViewModel

data class WaybillItemDraft(
    val id: String = java.util.UUID.randomUUID().toString(),
    var itemType: String = "FABRIC_ROLL",
    var itemId: Long = 0L,
    var itemCode: String = "",
    var itemName: String = "",
    var supplierId: Long? = null,
    var supplierName: String = "",
    var quantityText: String = "0",
    var unit: String = "کیلوگرم",
    var purchaseValueText: String = "0",
    var weightKgText: String = "0",
    var allocationMethod: ShippingAllocationMethod = ShippingAllocationMethod.BY_WEIGHT
)

/** ساختار داخلی برای آیتم لیست انتخاب */
private data class PickerOption(
    val id: Long,
    val code: String,
    val name: String,
    val defaultUnit: String
)

@Composable
fun WaybillMultiItemForm(
    viewModel: ManufacturingViewModel,
    onBack: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val shippingCompanies by viewModel.shippingCompanies.collectAsState()
    val fabricRolls by viewModel.fabricRolls.collectAsState()
    val materials by viewModel.materials.collectAsState()

    var trackingNumber by remember { mutableStateOf("BL-${(10000..99999).random()}") }
    var title by remember { mutableStateOf("بارنامه ورودی چندقلمی") }
    var carrierName by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf(PersianDateHelper.getTodayPersianDate()) }
    var totalAmountText by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }
    var showCompanyPicker by remember { mutableStateOf(false) }

    val items = remember {
        mutableStateListOf(WaybillItemDraft())
    }

    var pickerItemIndex by remember { mutableStateOf<Int?>(null) }

    val totalAmount = totalAmountText.toLongOrNull() ?: 0L
    val allocations = remember(items.toList(), totalAmount) {
        calculateAllocations(items.toList(), totalAmount)
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
                Text("ثبت بارنامه چندقلمی", style = MaterialTheme.typography.titleMedium,
                    color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                Text("تخصیص صحیح هزینه باربری به هر قلم بر اساس مبنای انتخابی",
                    style = MaterialTheme.typography.bodySmall, color = customColors.textMuted, fontSize = 11.sp)
            }
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = null, tint = customColors.textMuted)
            }
        }

        // اطلاعات بارنامه
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                ExecutiveTextField(label = "شماره بارنامه", value = trackingNumber, onValueChange = { trackingNumber = it })
            }
            Box(modifier = Modifier.weight(1f)) {
                ExecutiveTextField(label = "تاریخ تحویل", value = deliveryDate, onValueChange = { deliveryDate = it })
            }
        }

        ExecutiveTextField(label = "عنوان بارنامه", value = title, onValueChange = { title = it })

        // انتخاب شرکت باربری
        Button(
            onClick = { showCompanyPicker = !showCompanyPicker },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan.copy(alpha = 0.2f))
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                Text(
                    text = if (carrierName.isNotBlank()) "باربری: $carrierName - برای تغییر کلیک کنید"
                    else "انتخاب شرکت باربری",
                    color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold
                )
            }
        }

        if (showCompanyPicker) {
            if (shippingCompanies.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                    .background(StatusWarning.copy(alpha = 0.15f)).padding(10.dp)) {
                    Text("هنوز شرکت باربری ثبت نشده. از بخش «تنظیمات پایه» اضافه کنید.",
                        color = StatusWarning, fontSize = 11.sp)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    shippingCompanies.take(5).forEach { company ->
                        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .background(customColors.cardElevated)
                            .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
                            .clickable { carrierName = company.name; showCompanyPicker = false }
                            .padding(10.dp)) {
                            Text("${company.name}${if (company.phone.isNotBlank()) " • ${company.phone}" else ""}",
                                color = customColors.textPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        ExecutiveTextField(
            label = "مبلغ کل باربری (تومان)",
            value = totalAmountText,
            keyboardType = KeyboardType.Number,
            onValueChange = { totalAmountText = it }
        )

        HorizontalDivider(color = customColors.border)

        // اقلام بارنامه
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("اقلام این بارنامه (${items.size} قلم)", style = MaterialTheme.typography.titleSmall,
                color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            Button(
                onClick = { items.add(WaybillItemDraft()) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.size(4.dp))
                Text("افزودن قلم", fontSize = 11.sp)
            }
        }

        // ردیف‌های اقلام
        items.forEachIndexed { idx, item ->
            val alloc = allocations.getOrNull(idx) ?: 0L
            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(customColors.cardElevated)
                    .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("قلم ${idx + 1}: ${item.itemName.ifBlank { "انتخاب کنید" }}",
                            color = AccentIndigo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        if (items.size > 1) {
                            IconButton(onClick = { items.removeAt(idx) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // نوع کالا
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("FABRIC_ROLL" to "طاقه", "MATERIAL" to "ملزومات").forEach { pair ->
                            val type = pair.first
                            val label = pair.second
                            val selected = item.itemType == type
                            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp))
                                .background(if (selected) AccentIndigo.copy(alpha = 0.2f) else customColors.secondaryBg)
                                .border(1.dp, if (selected) AccentIndigo else customColors.border, RoundedCornerShape(6.dp))
                                .clickable {
                                    items[idx] = item.copy(itemType = type, itemId = 0L, itemName = "", itemCode = "")
                                }
                                .padding(8.dp), contentAlignment = Alignment.Center) {
                                Text(label, fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) AccentIndigo else customColors.textSecondary)
                            }
                        }
                    }

                    // انتخاب کالا
                    Button(
                        onClick = { pickerItemIndex = idx },
                        modifier = Modifier.fillMaxWidth().height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = customColors.secondaryBg)
                    ) {
                        Text(
                            text = if (item.itemName.isNotBlank()) "کالا: ${item.itemName}" else "انتخاب کالا از انبار",
                            color = customColors.textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold
                        )
                    }

                    // مقدار و واحد
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            ExecutiveTextField(label = "مقدار", value = item.quantityText,
                                keyboardType = KeyboardType.Decimal,
                                onValueChange = { items[idx] = item.copy(quantityText = it) })
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ExecutiveTextField(label = "واحد", value = item.unit,
                                onValueChange = { items[idx] = item.copy(unit = it) })
                        }
                    }

                    // مبنای تخصیص
                    Text("مبنای تخصیص کرایه:", fontSize = 11.sp, color = customColors.textMuted)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            ShippingAllocationMethod.BY_WEIGHT to "وزن",
                            ShippingAllocationMethod.PER_QUANTITY to "متر/عدد"
                        ).forEach { pair ->
                            val method = pair.first
                            val label = pair.second
                            val selected = item.allocationMethod == method
                            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp))
                                .background(if (selected) AccentCyan.copy(alpha = 0.15f) else customColors.secondaryBg)
                                .border(1.dp, if (selected) AccentCyan else customColors.border, RoundedCornerShape(6.dp))
                                .clickable { items[idx] = item.copy(allocationMethod = method) }
                                .padding(6.dp), contentAlignment = Alignment.Center) {
                                Text(label, fontSize = 10.sp,
                                    color = if (selected) AccentCyan else customColors.textSecondary,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }

                    // نمایش سهم
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                        .background(StatusSuccess.copy(alpha = 0.1f)).padding(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("سهم این قلم از باربری:", fontSize = 11.sp, color = customColors.textSecondary)
                            Text(CurrencyHelper.formatToman(alloc), fontSize = 12.sp,
                                color = StatusSuccess, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // دیالوگ انتخاب کالا
        pickerItemIndex?.let { idx ->
            val currentItem = items.getOrNull(idx)
            if (currentItem != null) {
                val options: List<PickerOption> = remember(currentItem.itemType, fabricRolls, materials) {
                    if (currentItem.itemType == "FABRIC_ROLL") {
                        fabricRolls.map { roll ->
                            PickerOption(
                                id = roll.id,
                                code = roll.rollCode,
                                name = "${roll.rollCode} - ${roll.fabricType} (${roll.color})",
                                defaultUnit = "کیلوگرم"
                            )
                        }
                    } else {
                        materials.map { mat ->
                            PickerOption(
                                id = mat.id,
                                code = mat.code,
                                name = "${mat.code} - ${mat.name}",
                                defaultUnit = mat.unit
                            )
                        }
                    }
                }

                AlertDialog(
                    onDismissRequest = { pickerItemIndex = null },
                    title = { Text("انتخاب کالا", fontWeight = FontWeight.Bold) },
                    text = {
                        if (options.isEmpty()) {
                            Text("کالایی در این دسته موجود نیست", color = StatusWarning)
                        } else {
                            Column(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                                    .heightIn(max = 400.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                options.take(30).forEach { option ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(customColors.cardElevated)
                                            .clickable {
                                                items[idx] = currentItem.copy(
                                                    itemId = option.id,
                                                    itemCode = option.code,
                                                    itemName = option.name,
                                                    unit = option.defaultUnit
                                                )
                                                pickerItemIndex = null
                                            }
                                            .padding(10.dp)
                                    ) {
                                        Text(option.name, fontSize = 11.sp, color = customColors.textPrimary)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { pickerItemIndex = null }) { Text("بستن") }
                    },
                    containerColor = customColors.cardElevated
                )
            }
        }

        // جمع‌بندی
        if (items.isNotEmpty() && totalAmount > 0L) {
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(customColors.cardElevated)
                .border(1.dp, AccentCyan, RoundedCornerShape(12.dp))
                .padding(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("جمع‌بندی تخصیص باربری", color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("مبلغ کل باربری:", fontSize = 11.sp, color = customColors.textSecondary)
                        Text(CurrencyHelper.formatToman(totalAmount), fontSize = 12.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("مجموع تخصیص‌یافته:", fontSize = 11.sp, color = customColors.textSecondary)
                        Text(CurrencyHelper.formatToman(allocations.sum()), fontSize = 12.sp, color = StatusSuccess, fontWeight = FontWeight.Bold)
                    }
                    val diff = totalAmount - allocations.sum()
                    if (diff != 0L) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("اختلاف (تعدیل):", fontSize = 11.sp, color = customColors.textSecondary)
                            Text(CurrencyHelper.formatToman(diff), fontSize = 11.sp, color = StatusWarning)
                        }
                    }
                }
            }
        }

        ExecutiveTextField(label = "توضیحات", value = notes, onValueChange = { notes = it })

        Button(
            onClick = {
                viewModel.submitMultiItemWaybill(
                    trackingNumber = trackingNumber,
                    title = title,
                    carrierName = carrierName,
                    deliveryDate = deliveryDate,
                    totalAmount = totalAmount,
                    items = items.toList(),
                    allocations = allocations,
                    notes = notes
                )
            },
            enabled = totalAmount > 0L && items.all { it.itemId > 0L && (it.quantityText.toDoubleOrNull() ?: 0.0) > 0.0 },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
        ) {
            Text("ثبت بارنامه و تخصیص کرایه به اقلام", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ExecutiveTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = customColors.textPrimary,
            unfocusedTextColor = customColors.textPrimary,
            focusedContainerColor = customColors.secondaryBg,
            unfocusedContainerColor = customColors.secondaryBg,
            focusedBorderColor = AccentIndigo,
            unfocusedBorderColor = customColors.border,
            focusedLabelColor = AccentIndigo,
            unfocusedLabelColor = customColors.textMuted
        )
    )
}

fun calculateAllocations(items: List<WaybillItemDraft>, totalAmount: Long): List<Long> {
    if (items.isEmpty() || totalAmount <= 0L) return items.map { 0L }

    // مبنا برای هر آیتم
    fun basisOf(item: WaybillItemDraft): Double {
        return when (item.allocationMethod) {
            ShippingAllocationMethod.BY_WEIGHT -> item.weightKgText.toDoubleOrNull()
                ?: item.quantityText.toDoubleOrNull() ?: 0.0
            ShippingAllocationMethod.PER_QUANTITY -> item.quantityText.toDoubleOrNull() ?: 0.0
            else -> item.quantityText.toDoubleOrNull() ?: 0.0
        }
    }

    val bases = items.map { basisOf(it) }
    val totalBasis = bases.sum()

    if (totalBasis <= 0.0) return items.map { 0L }

    val result = MutableList(items.size) { 0L }
    var accumulated = 0L
    for (i in items.indices) {
        if (i == items.lastIndex) {
            result[i] = (totalAmount - accumulated).coerceAtLeast(0L)
        } else {
            val share = (totalAmount * (bases[i] / totalBasis)).toLong()
            result[i] = share
            accumulated += share
        }
    }
    return result
}
