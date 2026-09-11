package com.example.ui.dialogs

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
