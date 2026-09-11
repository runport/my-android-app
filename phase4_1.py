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
NEW_DLG = BASE / "ui/dialogs/ReserveOrderDialog.kt"
MORE = BASE / "ui/screens/MoreHubScreen.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# ============================================
# ۱. ساخت ReserveOrderDialog.kt
# ============================================
info("۱. ساخت ReserveOrderDialog.kt")

code = '''package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.model.CustomerEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.CurrencyHelper
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.ManufacturingViewModel

/**
 * دیالوگ رزرو سفارش مشتری - فاز ۴.۱
 * سفارش ابتدا رزرو می‌شود، با پرداخت نهایی به فروش تبدیل می‌شود
 */
@Composable
fun ReserveOrderDialog(
    viewModel: ManufacturingViewModel,
    onDismiss: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val customers by viewModel.customers.collectAsState()
    val products by viewModel.products.collectAsState()
    val inventory by viewModel.inventory.collectAsState()

    var selectedCustomer by remember { mutableStateOf<CustomerEntity?>(null) }
    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var quantityText by remember { mutableStateOf("10") }
    var unitPriceText by remember { mutableStateOf("") }
    var paidAmountText by remember { mutableStateOf("0") }
    var discountText by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }

    var showCustomerPicker by remember { mutableStateOf(false) }
    var showProductPicker by remember { mutableStateOf(false) }

    val qty = quantityText.toIntOrNull() ?: 0
    val unitPrice = unitPriceText.toLongOrNull() ?: selectedProduct?.effectiveSellingPrice ?: 0L
    val paid = paidAmountText.toLongOrNull() ?: 0L
    val discount = discountText.toLongOrNull() ?: 0L

    val grossTotal = qty * unitPrice
    val netTotal = (grossTotal - discount).coerceAtLeast(0L)
    val remaining = (netTotal - paid).coerceAtLeast(0L)

    // چک موجودی برای رزرو
    val availableStock = if (selectedProduct != null) {
        inventory.find { it.code == selectedProduct!!.code }?.availableForSale ?: 0
    } else 0
    val stockStatus = when {
        selectedProduct == null -> "برای بررسی موجودی، محصول را انتخاب کنید"
        availableStock >= qty -> "موجودی کافی: $availableStock عدد"
        availableStock > 0 -> "موجودی جزئی: $availableStock عدد (باقی نیاز به تولید)"
        else -> "بدون موجودی — تمام در نوبت تولید"
    }

    // انتخاب مشتری
    if (showCustomerPicker) {
        AlertDialog(
            onDismissRequest = { showCustomerPicker = false },
            title = { Text("انتخاب مشتری", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.heightIn(max = 400.dp)) {
                    if (customers.isEmpty()) {
                        Text("مشتری ثبت نشده", color = StatusWarning)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(customers) { cust ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(customColors.card)
                                        .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedCustomer = cust
                                            showCustomerPicker = false
                                        }
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(cust.name, color = customColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            "بدهی: ${CurrencyHelper.formatToman(cust.currentDebt)} • ${cust.phone}",
                                            color = customColors.textMuted, fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCustomerPicker = false }) { Text("بستن") }
            },
            containerColor = customColors.cardElevated
        )
    }

    // انتخاب محصول
    if (showProductPicker) {
        AlertDialog(
            onDismissRequest = { showProductPicker = false },
            title = { Text("انتخاب محصول", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.heightIn(max = 400.dp)) {
                    if (products.isEmpty()) {
                        Text("محصولی ثبت نشده", color = StatusWarning)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(products) { prod ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(customColors.card)
                                        .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedProduct = prod
                                            unitPriceText = prod.effectiveSellingPrice.toString()
                                            showProductPicker = false
                                        }
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(prod.name, color = customColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            "کد: ${prod.code} • قیمت: ${CurrencyHelper.formatToman(prod.effectiveSellingPrice)}",
                                            color = customColors.textMuted, fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProductPicker = false }) { Text("بستن") }
            },
            containerColor = customColors.cardElevated
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("رزرو سفارش مشتری", fontWeight = FontWeight.Bold)
                    Text(
                        "سفارش ابتدا رزرو می‌شود، با پرداخت نهایی فروش می‌شود",
                        fontSize = 10.sp, color = customColors.textMuted
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = customColors.textMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // انتخاب مشتری
                Button(
                    onClick = { showCustomerPicker = true },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCustomer == null) AccentCyan.copy(alpha = 0.2f)
                                        else AccentCyan.copy(alpha = 0.15f)
                    )
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = selectedCustomer?.let { "مشتری: ${it.name}" } ?: "انتخاب مشتری از لیست",
                        color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold
                    )
                }

                // انتخاب محصول
                Button(
                    onClick = { showProductPicker = true },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = selectedProduct?.let { "محصول: ${it.name}" } ?: "انتخاب محصول",
                        color = AccentIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold
                    )
                }

                // وضعیت موجودی
                if (selectedProduct != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (availableStock >= qty) StatusSuccess.copy(alpha = 0.12f)
                                else StatusWarning.copy(alpha = 0.12f)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            stockStatus,
                            color = if (availableStock >= qty) StatusSuccess else StatusWarning,
                            fontSize = 10.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }

                // تعداد و قیمت
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ReserveTextField(
                            label = "تعداد",
                            value = quantityText,
                            keyboardType = KeyboardType.Number,
                            onValueChange = { quantityText = it }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ReserveTextField(
                            label = "قیمت واحد (تومان)",
                            value = unitPriceText,
                            keyboardType = KeyboardType.Number,
                            onValueChange = { unitPriceText = it }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ReserveTextField(
                            label = "تخفیف (تومان)",
                            value = discountText,
                            keyboardType = KeyboardType.Number,
                            onValueChange = { discountText = it }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ReserveTextField(
                            label = "پیش‌پرداخت (تومان)",
                            value = paidAmountText,
                            keyboardType = KeyboardType.Number,
                            onValueChange = { paidAmountText = it }
                        )
                    }
                }

                ReserveTextField(
                    label = "یادداشت",
                    value = notes,
                    onValueChange = { notes = it }
                )

                // جمع‌بندی
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(customColors.secondaryBg)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("مبلغ کل:", fontSize = 11.sp, color = customColors.textMuted)
                            Text(CurrencyHelper.formatToman(netTotal), fontSize = 11.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("مانده بدهی پس از پیش‌پرداخت:", fontSize = 11.sp, color = customColors.textMuted)
                            Text(CurrencyHelper.formatToman(remaining), fontSize = 11.sp,
                                color = if (remaining > 0) StatusWarning else StatusSuccess, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedCustomer != null && selectedProduct != null && qty > 0 && unitPrice > 0) {
                        viewModel.submitReservedSale(
                            customerId = selectedCustomer!!.id,
                            customerName = selectedCustomer!!.name,
                            customerPhone = selectedCustomer!!.phone,
                            modelCode = selectedProduct!!.code,
                            modelName = selectedProduct!!.name,
                            quantity = qty,
                            unitPrice = unitPrice,
                            discountAmount = discount,
                            paidAmount = paid,
                            notes = notes
                        )
                        onDismiss()
                    }
                },
                enabled = selectedCustomer != null && selectedProduct != null && qty > 0 && unitPrice > 0,
                colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(4.dp))
                Text("ثبت رزرو سفارش")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف", color = customColors.textMuted) }
        },
        containerColor = customColors.cardElevated
    )
}

@Composable
private fun ReserveTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val customColors = LocalCustomColors.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
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
'''
write(NEW_DLG, code)
log("ReserveOrderDialog.kt ساخته شد")

# ============================================
# ۲. افزودن اکشن‌های ViewModel
# ============================================
info("۲. افزودن اکشن‌های ViewModel")

c = read(VM)
if "submitReservedSale" in c:
    warn("اکشن‌ها از قبل هستند")
else:
    idx = c.rstrip().rfind("}")
    block = '''
  // ==========================================
  // RESERVE ORDER (فاز ۴.۱)
  // ==========================================

  /**
   * ثبت سفارش مشتری با رزرو موجودی
   */
  fun submitReservedSale(
    customerId: Long,
    customerName: String,
    customerPhone: String,
    modelCode: String,
    modelName: String,
    quantity: Int,
    unitPrice: Long,
    discountAmount: Long,
    paidAmount: Long,
    notes: String = ""
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.createSaleOrderWithReservation(
          customerId = customerId,
          customerName = customerName,
          customerPhone = customerPhone,
          modelCode = modelCode,
          modelName = modelName,
          quantity = quantity,
          unitPrice = unitPrice,
          discountAmount = discountAmount,
          paidAmount = paidAmount,
          channel = "رزرو",
          notes = notes
        )
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت رزرو: ${e.localizedMessage}", true)
      }
    }
  }

  /**
   * پرداخت نهایی سفارش - رزرو را به فروش تبدیل می‌کند
   */
  fun finalizeOrderPayment(orderId: Long, additionalPayment: Long) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.finalizeOrderWithPayment(orderId, additionalPayment)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در پرداخت نهایی: ${e.localizedMessage}", true)
      }
    }
  }

  /**
   * لغو سفارش - رزرو را آزاد می‌کند
   */
  fun cancelOrderWithRelease(orderId: Long, reason: String = "لغو توسط کاربر") {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.cancelOrderWithRelease(orderId, reason)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در لغو سفارش: ${e.localizedMessage}", true)
      }
    }
  }

  // دیالوگ رزرو
  private val _showReserveOrderDialog = MutableStateFlow(false)
  val showReserveOrderDialog: StateFlow<Boolean> = _showReserveOrderDialog.asStateFlow()

  fun openReserveOrderDialog() { _showReserveOrderDialog.value = true }
  fun closeReserveOrderDialog() { _showReserveOrderDialog.value = false }
'''
    c = c[:idx] + block + c[idx:]
    write(VM, c)
    log("اکشن‌ها اضافه شدند")

# ============================================
# ۳. افزودن متدهای Repository
# ============================================
info("۳. بررسی متدهای Repository")

REPO = BASE / "data/repository/ManufacturingRepository.kt"
rc = read(REPO)

# createSaleOrderWithReservation باید باشد
if "createSaleOrderWithReservation" not in rc:
    err("createSaleOrderWithReservation در Repository پیدا نشد!")
    err("این متد باید از قبل وجود داشته باشد — بررسی دستی لازم است")
else:
    log("createSaleOrderWithReservation از قبل هست")

# finalizeOrderWithPayment
if "finalizeOrderWithPayment" in rc:
    warn("finalizeOrderWithPayment از قبل هست")
else:
    idx = rc.rstrip().rfind("}")
    block = '''
  /**
   * پرداخت نهایی سفارش رزرو شده - تبدیل به فروش
   */
  suspend fun finalizeOrderWithPayment(
    orderId: Long,
    additionalPayment: Long
  ): Pair<Boolean, String> = database.withTransaction {
    val order = database.saleOrderDao().getOrderById(orderId)
      ?: return@withTransaction Pair(false, "سفارش یافت نشد")

    if (order.deliveryStatus.contains("فروش نهایی") || order.deliveryStatus.contains("تحویل شده")) {
      return@withTransaction Pair(false, "این سفارش قبلاً نهایی شده")
    }

    val today = PersianDateHelper.getCurrentPersianDate()
    val now = System.currentTimeMillis()
    val newPaid = order.paidAmount + additionalPayment
    val totalPrice = order.netTotal

    if (newPaid < totalPrice) {
      return@withTransaction Pair(
        false,
        "مبلغ پرداختی کافی نیست. مانده: ${totalPrice - newPaid}"
      )
    }

    // ۱. تغییر وضعیت سفارش
    val finalStatus = "فروش نهایی - تحویل شده"
    database.saleOrderDao().updateOrder(
      order.copy(
        paidAmount = newPaid,
        deliveryStatus = finalStatus
      )
    )

    // ۲. کاهش موجودی فیزیکی
    val inv = database.inventoryDao().getByCode(order.modelCode)
    if (inv != null) {
      val newReady = (inv.readyForShipment - order.quantity).coerceAtLeast(0)
      val newReserved = (inv.reservedQuantity - order.quantity).coerceAtLeast(0)
      database.inventoryDao().updateItem(
        inv.copy(
          readyForShipment = newReady,
          reservedQuantity = newReserved,
          lastUpdated = today
        )
      )
    }

    // ۳. ثبت پرداخت
    if (additionalPayment > 0L) {
      database.customerPaymentDao().insert(
        CustomerPaymentEntity(
          customerId = order.customerId ?: 0L,
          customerName = order.customerName,
          orderId = order.id,
          orderNumber = order.orderNumber,
          amount = additionalPayment,
          date = today,
          timestamp = now,
          paymentMethod = "تسویه نهایی سفارش",
          referenceNumber = "FINAL-${order.orderNumber}",
          notes = "پرداخت نهایی و تبدیل رزرو به فروش",
          recordedBy = "مدیر فروش"
        )
      )
    }

    // ۴. لاگ
    try {
      database.auditLogDao().insert(
        AuditLogEntity(
          timestamp = now, date = today,
          entityName = "SaleOrder", entityId = orderId,
          action = "FINALIZE_ORDER",
          oldValue = order.deliveryStatus,
          newValue = finalStatus,
          reason = "پرداخت نهایی و تبدیل رزرو به فروش",
          recordedBy = "مدیر فروش"
        )
      )
    } catch (_: Exception) {}

    Pair(true, "سفارش ${order.orderNumber} نهایی شد و فروش قطعی ثبت گردید")
  }

  /**
   * لغو سفارش رزرو شده - آزادسازی موجودی رزرو
   */
  suspend fun cancelOrderWithRelease(
    orderId: Long,
    reason: String
  ): Pair<Boolean, String> = database.withTransaction {
    val order = database.saleOrderDao().getOrderById(orderId)
      ?: return@withTransaction Pair(false, "سفارش یافت نشد")

    val today = PersianDateHelper.getCurrentPersianDate()
    val now = System.currentTimeMillis()

    // ۱. آزادسازی رزرو
    val inv = database.inventoryDao().getByCode(order.modelCode)
    if (inv != null) {
      val newReserved = (inv.reservedQuantity - order.quantity).coerceAtLeast(0)
      val newAvailable = inv.availableForSale + order.quantity
      database.inventoryDao().updateItem(
        inv.copy(
          reservedQuantity = newReserved,
          availableForSale = newAvailable,
          lastUpdated = today
        )
      )
    }

    // ۲. تغییر وضعیت
    database.saleOrderDao().updateOrder(
      order.copy(deliveryStatus = "لغو شده")
    )

    // ۳. تعدیل بدهی مشتری
    val cust = if (order.customerId != null) {
      database.customerDao().getCustomerById(order.customerId)
    } else {
      database.customerDao().getCustomerByName(order.customerName)
    }
    if (cust != null) {
      database.customerDao().updateCustomer(
        cust.copy(
          totalPurchases = (cust.totalPurchases - order.netTotal).coerceAtLeast(0L),
          currentDebt = (cust.currentDebt - order.remainingDebt).coerceAtLeast(0L)
        )
      )
    }

    // ۴. لاگ
    try {
      database.auditLogDao().insert(
        AuditLogEntity(
          timestamp = now, date = today,
          entityName = "SaleOrder", entityId = orderId,
          action = "CANCEL_ORDER",
          oldValue = order.deliveryStatus,
          newValue = "لغو شده",
          reason = reason,
          recordedBy = "مدیر فروش"
        )
      )
    } catch (_: Exception) {}

    Pair(true, "سفارش ${order.orderNumber} لغو شد و ${order.quantity} عدد به موجودی آزاد برگشت")
  }
'''
    rc = rc[:idx] + block + rc[idx:]
    write(REPO, rc)
    log("متدهای Repository اضافه شدند")

# ============================================
# ۴. افزودن دکمه به MoreHubScreen (بخش سفارشات)
# ============================================
info("۴. بررسی MoreHubScreen")

# فقط چک می‌کنیم فایل هست
if MORE.exists():
    log("MoreHubScreen موجود است (اتصال UI در ۴.۲)")
else:
    warn("MoreHubScreen پیدا نشد")

# ============================================
# ۵. اعتبارسنجی
# ============================================
print()
info("۵. بررسی آکولاد:")
for path, label in [(VM, "ViewModel"), (NEW_DLG, "ReserveOrderDialog"), (REPO, "Repository")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

print()
log("فاز ۴.۱ اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase4.1): order reservation system' && git push origin feature/cutting-parts-workflow{RST}")
