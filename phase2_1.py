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
VM = BASE / "viewmodel/ManufacturingViewModel.kt"
QA = BASE / "ui/dialogs/QuickActionSheets.kt"
NEW_FORM = BASE / "ui/dialogs/WaybillMultiItemForm.kt"
REPO = BASE / "data/repository/ManufacturingRepository.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# ============================================
# ۱. WaybillMultiItemForm.kt — فایل جدید
# ============================================
info("۱. ساخت WaybillMultiItemForm.kt")

form_code = '''package com.example.ui.dialogs

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShippingAllocationMethod
import com.example.data.service.FinancialCalculationService
import com.example.ui.components.CurrencyHelper
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.util.PersianDateHelper
import com.example.viewmodel.ManufacturingViewModel

/**
 * یک ردیف کالا در بارنامه
 */
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

/**
 * فرم بارنامه چندقلمی — فاز ۲
 * یک بارنامه با چند قلم از تامین‌کنندگان مختلف
 * تخصیص هزینه باربری بر اساس مبنای انتخابی هر ردیف
 */
@Composable
fun WaybillMultiItemForm(
    viewModel: ManufacturingViewModel,
    onBack: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val shippingCompanies by viewModel.shippingCompanies.collectAsState()
    val fabricRolls by viewModel.fabricRolls.collectAsState()
    val materials by viewModel.materials.collectAsState()
    val suppliers by viewModel.suppliers.collectAsState()

    var trackingNumber by remember { mutableStateOf("BL-${(10000..99999).random()}") }
    var title by remember { mutableStateOf("بارنامه ورودی چندقلمی") }
    var carrierName by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf(PersianDateHelper.getTodayPersianDate()) }
    var totalAmountText by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }
    var showCompanyPicker by remember { mutableStateOf(false) }

    val items = remember { mutableStateListOf(
        WaybillItemDraft(allocationMethod = ShippingAllocationMethod.BY_WEIGHT)
    ) }

    var pickerItemIndex by remember { mutableStateOf<Int?>(null) }
    var pickerType by remember { mutableStateOf("FABRIC") }

    val totalAmount = totalAmountText.toLongOrNull() ?: 0L

    // محاسبه سهم هر قلم
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
            IconButton(onClick = onBack) { Icon(Icons.Default.Close, null, tint = customColors.textMuted) }
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
                Icon(Icons.Default.LocalShipping, null, tint = AccentCyan, modifier = Modifier.size(18.dp))
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
                    shippingCompanies.take(5).forEach { c ->
                        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .background(customColors.cardElevated)
                            .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
                            .clickable { carrierName = c.name; showCompanyPicker = false }
                            .padding(10.dp)) {
                            Text("${c.name} ${if (c.phone.isNotBlank()) "• ${c.phone}" else ""}",
                                 color = customColors.textPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // مبلغ کل باربری
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
                onClick = {
                    items.add(WaybillItemDraft())
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp))
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
                                Icon(Icons.Default.Delete, null, tint = StatusDanger, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // انتخاب نوع کالا
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("FABRIC_ROLL" to "طاقه", "MATERIAL" to "ملزومات").forEach { (type, label) ->
                            val selected = item.itemType == type
                            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp))
                                .background(if (selected) AccentIndigo.copy(alpha = 0.2f) else customColors.secondaryBg)
                                .border(1.dp, if (selected) AccentIndigo else customColors.border, RoundedCornerShape(6.dp))
                                .clickable {
                                    items[idx] = item.copy(itemType = type, itemId = 0L, itemName = "", itemCode = "")
                                }
                                .padding(8.dp), contentAlignment = Alignment.Center) {
                                Text(label, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                     color = if (selected) AccentIndigo else customColors.textSecondary)
                            }
                        }
                    }

                    // انتخاب کالا
                    Button(
                        onClick = { pickerItemIndex = idx; pickerType = item.itemType },
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
                        ).forEach { (m, label) ->
                            val selected = item.allocationMethod == m
                            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp))
                                .background(if (selected) AccentCyan.copy(alpha = 0.15f) else customColors.secondaryBg)
                                .border(1.dp, if (selected) AccentCyan else customColors.border, RoundedCornerShape(6.dp))
                                .clickable { items[idx] = item.copy(allocationMethod = m) }
                                .padding(6.dp), contentAlignment = Alignment.Center) {
                                Text(label, fontSize = 10.sp,
                                     color = if (selected) AccentCyan else customColors.textSecondary,
                                     fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }

                    // نمایش سهم محاسبه‌شده
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
                AlertDialog(
                    onDismissRequest = { pickerItemIndex = null },
                    title = { Text("انتخاب کالا", fontWeight = FontWeight.Bold) },
                    text = {
                        val list = if (currentItem.itemType == "FABRIC_ROLL") fabricRolls else materials
                        if (list.isEmpty()) {
                            Text("کالایی در این دسته موجود نیست", color = StatusWarning)
                        } else {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())
                                .heightIn(max = 400.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                list.take(20).forEach { any ->
                                    val name = when (any) {
                                        is com.example.data.model.FabricRollEntity -> "${any.rollCode} - ${any.fabricType}"
                                        is com.example.data.model.MaterialEntity -> "${any.code} - ${any.name}"
                                        else -> ""
                                    }
                                    val code = when (any) {
                                        is com.example.data.model.FabricRollEntity -> any.rollCode
                                        is com.example.data.model.MaterialEntity -> any.code
                                        else -> ""
                                    }
                                    val id = when (any) {
                                        is com.example.data.model.FabricRollEntity -> any.id
                                        is com.example.data.model.MaterialEntity -> any.id
                                        else -> 0L
                                    }
                                    val defaultUnit = when (any) {
                                        is com.example.data.model.FabricRollEntity -> "کیلوگرم"
                                        is com.example.data.model.MaterialEntity -> any.unit
                                        else -> "کیلوگرم"
                                    }
                                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                                        .background(customColors.cardElevated)
                                        .clickable {
                                            items[idx] = currentItem.copy(itemId = id, itemCode = code, itemName = name, unit = defaultUnit)
                                            pickerItemIndex = null
                                        }
                                        .padding(8.dp)) {
                                        Text(name, fontSize = 11.sp, color = customColors.textPrimary)
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

        // توضیحات
        ExecutiveTextField(label = "توضیحات", value = notes, onValueChange = { notes = it })

        // دکمه ثبت
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

/**
 * محاسبه تخصیص عادلانه کرایه بین چند قلم
 * جمع تمام سهم‌ها دقیقاً برابر totalAmount
 */
fun calculateAllocations(items: List<WaybillItemDraft>, totalAmount: Long): List<Long> {
    if (items.isEmpty() || totalAmount <= 0L) return items.map { 0L }

    // گروه‌بندی بر اساس مبنای تخصیص
    val groups = items.groupBy { it.allocationMethod }
    val result = MutableList(items.size) { 0L }

    groups.forEach { (method, groupItems) ->
        // جمع کل مبنای این گروه
        val totalBasis = groupItems.sumOf { item ->
            when (method) {
                ShippingAllocationMethod.BY_WEIGHT -> item.weightKgText.toDoubleOrNull() ?: item.quantityText.toDoubleOrNull() ?: 0.0
                ShippingAllocationMethod.PER_QUANTITY -> item.quantityText.toDoubleOrNull() ?: 0.0
                else -> item.quantityText.toDoubleOrNull() ?: 0.0
            }
        }

        if (totalBasis <= 0.0) return@forEach

        // سهم این گروه از مبلغ کل = نسبت مبنای گروه به کل
        val allBasis = items.sumOf { item ->
            when (it.allocationMethod) {
                ShippingAllocationMethod.BY_WEIGHT -> it.weightKgText.toDoubleOrNull() ?: it.quantityText.toDoubleOrNull() ?: 0.0
                ShippingAllocationMethod.PER_QUANTITY -> it.quantityText.toDoubleOrNull() ?: 0.0
                else -> it.quantityText.toDoubleOrNull() ?: 0.0
            }
        }

        val groupShare = if (allBasis > 0.0) (totalAmount * (totalBasis / allBasis)).toLong() else 0L

        // توزیع groupShare بین اقلام این گروه
        groupItems.forEach { item ->
            val idx = items.indexOf(item)
            if (idx >= 0) {
                val basis = when (method) {
                    ShippingAllocationMethod.BY_WEIGHT -> item.weightKgText.toDoubleOrNull() ?: item.quantityText.toDoubleOrNull() ?: 0.0
                    ShippingAllocationMethod.PER_QUANTITY -> item.quantityText.toDoubleOrNull() ?: 0.0
                    else -> item.quantityText.toDoubleOrNull() ?: 0.0
                }
                val itemShare = if (totalBasis > 0.0) (groupShare * (basis / totalBasis)).toLong() else 0L
                result[idx] = itemShare
            }
        }
    }

    // تعدیل باقیمانده (خطای رُند) روی آخرین قلم
    val diff = totalAmount - result.sum()
    if (diff != 0L && result.isNotEmpty()) {
        result[result.lastIndex] += diff
    }
    return result
}
'''
write(NEW_FORM, form_code)
log("WaybillMultiItemForm.kt ساخته شد")

# ============================================
# ۲. افزودن QuickActionType.SHIPPING_MULTI
# ============================================
info("۲. افزودن SHIPPING_MULTI به QuickActionType")
c = read(VM)
if "SHIPPING_MULTI" in c:
    warn("SHIPPING_MULTI از قبل هست")
else:
    if "SHIPPING_IN," in c:
        c = c.replace("  SHIPPING_IN,", "  SHIPPING_IN,\n  SHIPPING_MULTI,", 1)
        write(VM, c)
        log("SHIPPING_MULTI اضافه شد")
    else:
        err("SHIPPING_IN در enum پیدا نشد")

# ============================================
# ۳. افزودن متد submitMultiItemWaybill به ViewModel
# ============================================
info("۳. افزودن متد به ViewModel")
c = read(VM)
if "submitMultiItemWaybill" in c:
    warn("متد از قبل هست")
else:
    # قبل از آخرین آکولاد
    idx = c.rstrip().rfind("}")
    block = '''
  /**
   * ثبت بارنامه چندقلمی با تخصیص صحیح کرایه به هر قلم (فاز ۲)
   */
  fun submitMultiItemWaybill(
    trackingNumber: String,
    title: String,
    carrierName: String,
    deliveryDate: String,
    totalAmount: Long,
    items: List<com.example.ui.dialogs.WaybillItemDraft>,
    allocations: List<Long>,
    notes: String = ""
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.submitMultiItemWaybill(
          trackingNumber, title, carrierName, deliveryDate, totalAmount, items, allocations, notes
        )
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.localizedMessage}", true)
      }
    }
  }
'''
    c = c[:idx] + block + c[idx:]
    write(VM, c)
    log("متد ViewModel اضافه شد")

# ============================================
# ۴. افزودن case به QuickActionsModalBottomSheet
# ============================================
info("۴. افزودن case به Modal")
c = read(QA)
if "WaybillMultiItemForm(" in c:
    warn("case از قبل هست")
else:
    anchor = """        QuickActionType.SHIPPING_IN -> {
          QuickShippingExpenseForm("""
    if anchor in c:
        new_case = """        QuickActionType.SHIPPING_MULTI -> {
          WaybillMultiItemForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.SHIPPING_IN -> {
          QuickShippingExpenseForm("""
        c = c.replace(anchor, new_case, 1)
        write(QA, c)
        log("case SHIPPING_MULTI اضافه شد")
    else:
        err("anchor QuickActionType.SHIPPING_IN پیدا نشد")

# ============================================
# ۵. افزودن متد به Repository
# ============================================
info("۵. افزودن متد به Repository")
c = read(REPO)
if "submitMultiItemWaybill" in c:
    warn("متد از قبل هست")
else:
    idx = c.rstrip().rfind("}")
    block = '''
  /**
   * ثبت بارنامه چندقلمی با تخصیص صحیح کرایه (فاز ۲)
   */
  suspend fun submitMultiItemWaybill(
    trackingNumber: String,
    title: String,
    carrierName: String,
    deliveryDate: String,
    totalAmount: Long,
    items: List<com.example.ui.dialogs.WaybillItemDraft>,
    allocations: List<Long>,
    notes: String = ""
  ): Pair<Boolean, String> = database.withTransaction {
    if (items.isEmpty()) return@withTransaction Pair(false, "هیچ قلمی اضافه نشده")
    if (totalAmount <= 0L) return@withTransaction Pair(false, "مبلغ کل باربری نامعتبر")

    val todayDate = PersianDateHelper.getTodayPersianDate()
    val now = System.currentTimeMillis()

    // ۱. درج بارنامه اصلی
    val primaryMethod = items.firstOrNull()?.allocationMethod?.name ?: "BY_WEIGHT"
    val expense = ShippingExpenseEntity(
      trackingNumber = trackingNumber,
      title = title,
      date = todayDate,
      deliveryDate = deliveryDate,
      timestamp = now,
      totalAmount = totalAmount,
      inboundType = "ترکیبی چندقلمی",
      itemCount = items.size,
      totalWeightKg = items.sumOf { it.weightKgText.toDoubleOrNull() ?: 0.0 },
      totalQuantity = items.sumOf { it.quantityText.toDoubleOrNull() ?: 0.0 },
      unit = "قلم",
      allocationMethod = primaryMethod,
      costPerUnit = totalAmount / items.size.coerceAtLeast(1),
      carrierName = carrierName,
      status = "ثبت شده",
      notes = notes
    )
    val expenseId = database.shippingExpenseDao().insertExpense(expense)

    // ۲. درج اقلام بارنامه + تخصیص به کالاها
    items.forEachIndexed { idx, item ->
      val alloc = allocations.getOrNull(idx) ?: 0L
      val waybillItem = WaybillItemEntity(
        waybillId = expenseId,
        supplierId = item.supplierId,
        supplierName = item.supplierName,
        itemType = item.itemType,
        itemId = item.itemId,
        itemCode = item.itemCode,
        itemName = item.itemName,
        quantity = item.quantityText.toDoubleOrNull() ?: 0.0,
        unit = item.unit,
        purchaseValue = item.purchaseValueText.toLongOrNull() ?: 0L,
        weightKg = item.weightKgText.toDoubleOrNull() ?: 0.0,
        volumeM3 = 0.0,
        shippingAllocation = alloc,
        notes = ""
      )
      database.waybillItemDao().insert(waybillItem)

      // تخصیص به طاقه در صورت وجود
      if (item.itemType == "FABRIC_ROLL") {
        val roll = database.fabricRollDao().getRollById(item.itemId)
        if (roll != null) {
          database.fabricRollDao().updateRoll(
            roll.copy(
              allocatedShippingCost = alloc,
              shippingExpenseId = expenseId
            )
          )
        }
      }
    }

    // ۳. آدیت لاگ
    try {
      database.auditLogDao().insert(
        AuditLogEntity(
          timestamp = now, date = todayDate,
          entityName = "Waybill", entityId = expenseId,
          action = "CREATE_MULTI_ITEM_WAYBILL",
          oldValue = "",
          newValue = "بارنامه $trackingNumber با ${items.size} قلم و مبلغ ${totalAmount}",
          reason = "ثبت بارنامه چندقلمی با تخصیص به هر قلم",
          recordedBy = "مدیر کارگاه"
        )
      )
    } catch (_: Exception) {}

    Pair(true, "بارنامه $trackingNumber با ${items.size} قلم ثبت شد و کرایه بین اقلام تخصیص یافت")
  }
'''
    c = c[:idx] + block + c[idx:]
    write(REPO, c)
    log("متد Repository اضافه شد")

# ============================================
# ۶. اعتبارسنجی
# ============================================
print()
info("۶. بررسی نهایی:")
for path, label in [(VM, "ViewModel"), (QA, "QuickActionSheets"),
                    (REPO, "Repository"), (NEW_FORM, "WaybillMultiItemForm")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

print()
log("زیرفاز 2.1 اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase2.1): multi-item waybill' && git push origin feature/cutting-parts-workflow{RST}")
