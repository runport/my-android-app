package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FabricEntity
import com.example.data.model.FabricRollEntity
import com.example.data.model.InventoryEntity
import com.example.data.model.ShippingExpenseEntity
import com.example.ui.components.CurrencyHelper
import com.example.ui.components.SecondaryKpiCard
import com.example.ui.components.StatusChip
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.ManufacturingViewModel
import com.example.viewmodel.QuickActionType

enum class InventoryCategory(val title: String) {
  FINISHED_GOODS("محصولات آماده"),
  FABRIC_ROLLS("طاقه‌ها (فاز ۲)"),
  RAW_FABRICS("دسته‌های پارچه"),
  ACCESSORIES("ملزومات"),
  SHIPPING_RECORDS("باربری")
}

@Composable
fun InventoryScreen(
  viewModel: ManufacturingViewModel,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  var selectedCategory by remember { mutableStateOf(InventoryCategory.FINISHED_GOODS) }
  val inventoryItems by viewModel.inventory.collectAsState()
  val fabrics by viewModel.fabrics.collectAsState()
  val fabricRolls by viewModel.fabricRolls.collectAsState()
  val shippingExpenses by viewModel.shippingExpenses.collectAsState()
  val shippingAverages by viewModel.shippingAverages.collectAsState()
  val productions by viewModel.productions.collectAsState()

  val totalReady = inventoryItems.sumOf { it.readyForShipment }
  val totalReserved = inventoryItems.sumOf { it.reservedQuantity }
  val totalAvailable = inventoryItems.sumOf { it.availableForSale }
  val totalFabricMeters = fabrics.sumOf { it.totalMeters }
  val totalFabricKg = fabrics.sumOf { it.totalWeightKg }
  val totalValuation = inventoryItems.sumOf { it.totalStockValue } + fabrics.sumOf { it.totalStockValue }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
              text = "داشبورد جامع انبارداری",
              style = MaterialTheme.typography.titleLarge,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "پایش زنده طاقه‌ها، محصولات آماده، ملزومات و اوزان",
              style = MaterialTheme.typography.bodySmall,
              color = customColors.textMuted
            )
          }

          Button(
            onClick = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) },
            colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.size(4.dp))
            Text("عملیات انبار", style = MaterialTheme.typography.labelSmall)
          }
        }

        // Quick Action Buttons Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = { viewModel.openQuickAction(QuickActionType.ROLL_IN) },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
          ) {
            Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
            Spacer(Modifier.size(4.dp))
            Text("+ طاقه جدید (فاز ۲)", style = MaterialTheme.typography.labelSmall, color = Color.Black, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.openQuickAction(QuickActionType.ROLL_CONSUME) },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(Modifier.size(4.dp))
            Text("مصرف از طاقه", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.openQuickAction(QuickActionType.SHIPPING_IN) },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
          ) {
            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(Modifier.size(4.dp))
            Text("+ ثبت بارنامه", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = { viewModel.openQuickAction(QuickActionType.READY_GOODS_IN) },
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(14.dp))
            Spacer(Modifier.size(4.dp))
            Text("+ ثبت کار آماده", style = MaterialTheme.typography.labelSmall, color = customColors.textPrimary)
          }

          OutlinedButton(
            onClick = { viewModel.openQuickAction(QuickActionType.ACCESSORY_IN) },
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.Category, contentDescription = null, tint = com.example.ui.theme.AccentAmber, modifier = Modifier.size(14.dp))
            Spacer(Modifier.size(4.dp))
            Text("+ ورود ملزومات", style = MaterialTheme.typography.labelSmall, color = customColors.textPrimary)
          }
        }
      }
    }

    // 2. Modern Warehouse Overview Cards
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(modifier = Modifier.weight(1f)) {
            SecondaryKpiCard(
              title = "آماده ارسال",
              valueText = "${CurrencyHelper.formatNumber(totalReady)} عدد",
              subtitle = "بسته‌بندی نهایی",
              icon = Icons.Default.CheckCircle
            )
          }
          Box(modifier = Modifier.weight(1f)) {
            SecondaryKpiCard(
              title = "رزرو سفارشات",
              valueText = "${CurrencyHelper.formatNumber(totalReserved)} عدد",
              subtitle = "در انتظار تسویه و باربری",
              icon = Icons.Default.Inventory2
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(modifier = Modifier.weight(1f)) {
            SecondaryKpiCard(
              title = "موجودی پارچه (متر و کیلو)",
              valueText = "${totalFabricMeters.toInt()} متر",
              subtitle = "معادل ${totalFabricKg.toInt()} کیلوگرم",
              icon = Icons.Default.Scale
            )
          }
          Box(modifier = Modifier.weight(1f)) {
            SecondaryKpiCard(
              title = "ارزش کل انبارداری",
              valueText = CurrencyHelper.formatToman(totalValuation),
              subtitle = "کالای آماده + پارچه + ملزومات",
              icon = Icons.Default.Inventory2
            )
          }
        }
      }
    }

    // 3. Category Filter Selector
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.secondaryBg)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        InventoryCategory.values().forEach { category ->
          val isSelected = category == selectedCategory
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) customColors.cardElevated else Color.Transparent)
              .clickable { selectedCategory = category }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = category.title,
              style = MaterialTheme.typography.labelSmall,
              color = if (isSelected) customColors.textPrimary else customColors.textMuted,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 11.sp
            )
          }
        }
      }
    }

    // 4. Listing Items Based on Category with Edit Capabilities
    when (selectedCategory) {
      InventoryCategory.FINISHED_GOODS -> {
        val inProgressRollProductions = productions.filter { it.rollId != null && it.status != "تکمیل شده" && it.status != "آماده ارسال / تکمیل موجودی" }
        if (inProgressRollProductions.isNotEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(customColors.cardElevated)
                .border(1.dp, AccentIndigo.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(14.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    "کارهای برش‌خورده از طاقه‌ها (در حال دوخت - انتقال به انبار):",
                    style = MaterialTheme.typography.titleSmall,
                    color = AccentIndigo,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    "${inProgressRollProductions.size} پارت",
                    style = MaterialTheme.typography.labelSmall,
                    color = customColors.textMuted
                  )
                }
                inProgressRollProductions.forEach { prod ->
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(customColors.secondaryBg)
                      .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
                      .padding(10.dp)
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(prod.modelName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
                        Text("${prod.quantity} عدد (${String.format(java.util.Locale.US, "%.1f", prod.fabricMetersUsed)} متر پارچه)", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                      }
                      Button(
                        onClick = { viewModel.completeProductionToReadyGoods(prod) },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                      ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("ثبت در انبار کالا", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        val finished = inventoryItems.filter { it.category == "محصولات آماده" }
        items(finished) { item ->
          InventoryProductCard(
            item = item,
            onEdit = { viewModel.startEditInventory(item) }
          )
        }
      }
      InventoryCategory.FABRIC_ROLLS -> {
        if (fabricRolls.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(customColors.card)
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Inventory2, contentDescription = null, tint = customColors.textMuted, modifier = Modifier.size(36.dp))
                Text("هنوز طاقه‌ای ثبت نشده است", style = MaterialTheme.typography.bodyMedium, color = customColors.textMuted)
                Button(
                  onClick = { viewModel.openQuickAction(QuickActionType.ROLL_IN) },
                  colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("+ ثبت اولین طاقه پارچه", color = Color.Black, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        } else {
          items(fabricRolls) { roll ->
            FabricRollInventoryCard(
              roll = roll,
              onConsume = {
                viewModel.openQuickAction(QuickActionType.ROLL_CONSUME)
              },
              onHistory = {
                viewModel.showRollHistory(roll)
              },
              onEdit = {
                viewModel.startEditFabricRoll(roll)
              }
            )
          }
        }
      }
      InventoryCategory.RAW_FABRICS -> {
        items(fabrics) { fabric ->
          FabricInventoryCard(
            fabric = fabric,
            onEdit = { viewModel.startEditFabric(fabric) }
          )
        }
      }
      InventoryCategory.ACCESSORIES -> {
        val accessories = inventoryItems.filter { it.category == "ملزومات" }
        items(accessories) { item ->
          InventoryProductCard(
            item = item,
            onEdit = { viewModel.startEditInventory(item) }
          )
        }
      }
      InventoryCategory.SHIPPING_RECORDS -> {
        item {
          // Shipping Averages Summary Card
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.cardElevated)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(14.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text("خلاصه شاخص‌های کرایه باربری (تخصیص یافته)", style = MaterialTheme.typography.titleSmall, color = AccentBlue, fontWeight = FontWeight.Bold)
              Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("مجموع کل هزینه‌های باربری:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                Text(CurrencyHelper.formatToman(shippingAverages.totalAmount), style = MaterialTheme.typography.bodyMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
              }
              HorizontalDivider(color = customColors.border)
              Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("میانگین هزینه هر طاقه:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                Text(CurrencyHelper.formatToman(shippingAverages.averagePerRoll), style = MaterialTheme.typography.bodySmall, color = AccentCyan, fontWeight = FontWeight.Bold)
              }
              Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("میانگین هزینه هر کیلوگرم:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                Text(CurrencyHelper.formatToman(shippingAverages.averagePerKg), style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
              }
              Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("میانگین هزینه هر واحد/متر:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                Text(CurrencyHelper.formatToman(shippingAverages.averagePerUnit), style = MaterialTheme.typography.bodySmall, color = customColors.textSecondary)
              }
            }
          }
        }

        if (shippingExpenses.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(customColors.card)
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = customColors.textMuted, modifier = Modifier.size(36.dp))
                Text("هنوز بارنامه‌ای ثبت نشده است", style = MaterialTheme.typography.bodyMedium, color = customColors.textMuted)
                Button(
                  onClick = { viewModel.openQuickAction(QuickActionType.SHIPPING_IN) },
                  colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("+ ثبت بارنامه جدید", color = Color.White, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        } else {
          items(shippingExpenses) { expense ->
            ShippingExpenseInventoryCard(expense = expense)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
fun InventoryProductCard(
  item: InventoryEntity,
  onEdit: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val totalWeightKg = (item.readyForShipment * item.unitWeightGrams) / 1000.0

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .clickable(onClick = onEdit)
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = item.name,
            style = MaterialTheme.typography.titleSmall,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "کد: ${item.code} • وزن تک: ${item.unitWeightGrams.toInt()} گرم • کل: ${String.format("%.1f", totalWeightKg)} کیلو",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textMuted
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          StatusChip(status = if (item.availableForSale > 50) "موجودی مطلوب" else "موجودی محدود")
          IconButton(
            onClick = onEdit,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = customColors.textMuted, modifier = Modifier.size(16.dp))
          }
        }
      }

      // 3 Stock Blocks: آماده ارسال | رزرو | قابل فروش
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(customColors.secondaryBg)
          .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("آماده ارسال", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${item.readyForShipment} عدد", style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("رزرو شده", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${item.reservedQuantity} عدد", style = MaterialTheme.typography.bodySmall, color = StatusWarning, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("قابل فروش", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${item.availableForSale} عدد", style = MaterialTheme.typography.bodySmall, color = AccentBlue, fontWeight = FontWeight.Bold)
        }
      }

      // Red Price Change Alert (if cost price updated)
      if (item.hasPriceChanged) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(StatusDanger.copy(alpha = 0.12f))
            .border(1.dp, StatusDanger.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.History, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(14.dp))
              Text(
                text = "تغییر بهای تمام‌شده: ${CurrencyHelper.formatToman(item.previousCostPrice)}",
                style = MaterialTheme.typography.labelSmall,
                color = StatusDanger,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
            Text(
              text = "تاریخ: ${item.previousCostDate.ifBlank { "ثبت گذشته" }}",
              style = MaterialTheme.typography.labelSmall,
              color = StatusDanger,
              fontSize = 10.sp
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "ارزش موجودی: ${CurrencyHelper.formatToman(item.totalStockValue)}",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textSecondary
        )
        Text(
          text = "کلیک جهت ویرایش",
          style = MaterialTheme.typography.labelSmall,
          color = AccentIndigo,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

@Composable
fun FabricInventoryCard(
  fabric: FabricEntity,
  onEdit: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, if (fabric.isLowStock) StatusWarning.copy(alpha = 0.4f) else customColors.border, RoundedCornerShape(14.dp))
      .clickable(onClick = onEdit)
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = fabric.name,
            style = MaterialTheme.typography.titleSmall,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "کد: ${fabric.code} • رنگ: ${fabric.color} • پارت: ${fabric.batchNumber}",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textMuted
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          if (fabric.isLowStock) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.WarningAmber, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(16.dp))
              Text("کمبود", style = MaterialTheme.typography.labelSmall, color = StatusWarning)
            }
          } else {
            StatusChip(status = "موجودی کافی")
          }

          IconButton(
            onClick = onEdit,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = customColors.textMuted, modifier = Modifier.size(16.dp))
          }
        }
      }

      // Meter & Kilograms & Rolls & Prices
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(customColors.secondaryBg)
          .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("متراژ کل", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${fabric.totalMeters.toInt()} متر", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("وزن کل (کیلو)", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${fabric.totalWeightKg.toInt()} کیلو", style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("تعداد طاقه", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${fabric.rollCount} طاقه", style = MaterialTheme.typography.bodySmall, color = AccentCyan, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("قیمت کیلو", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(fabric.buyPricePerKg), style = MaterialTheme.typography.bodySmall, color = customColors.textSecondary)
        }
      }

      // Red Price Change Alert for Fabric (if price updated)
      if (fabric.hasPriceChanged) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(StatusDanger.copy(alpha = 0.12f))
            .border(1.dp, StatusDanger.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.History, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(14.dp))
              Text(
                text = "تغییر قیمت متری: ${CurrencyHelper.formatToman(fabric.previousBuyPricePerMeter)}",
                style = MaterialTheme.typography.labelSmall,
                color = StatusDanger,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
            Text(
              text = "تاریخ: ${fabric.previousPurchaseDate.ifBlank { "ثبت گذشته" }}",
              style = MaterialTheme.typography.labelSmall,
              color = StatusDanger,
              fontSize = 10.sp
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "ارزش کل موجودی: ${CurrencyHelper.formatToman(fabric.totalStockValue)}",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textSecondary,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "تأمین‌کننده: ${fabric.supplierName}",
          style = MaterialTheme.typography.labelSmall,
          color = customColors.textMuted
        )
      }
    }
  }
}

@Composable
fun FabricRollInventoryCard(
  roll: FabricRollEntity,
  onConsume: () -> Unit,
  onHistory: () -> Unit,
  onEdit: () -> Unit = {}
) {
  val customColors = LocalCustomColors.current
  val remainingPercent = if (roll.initialMeters > 0) (roll.remainingMeters / roll.initialMeters).toFloat() else 0f
  val isFinished = roll.status == "FINISHED" || roll.remainingMeters <= 0.01

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "طاقه: ${roll.rollCode}",
              style = MaterialTheme.typography.titleSmall,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold
            )
            val chipText = when {
              isFinished -> "پایان یافته"
              roll.remainingMeters < roll.initialMeters -> "در حال مصرف"
              else -> "طاقه سالم / نو"
            }
            StatusChip(status = chipText)
          }
          Text(
            text = "${roll.fabricType} • رنگ: ${roll.color} • پارت: ${roll.batchNumber.ifBlank { "عمومی" }}",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textMuted
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = AccentIndigo)
          }
          IconButton(onClick = onHistory) {
            Icon(Icons.Default.History, contentDescription = "سوابق مصرف", tint = AccentCyan)
          }
        }
      }

      // Progress bar for roll meters
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text(
            text = "موجودی فعلی: ${String.format("%.1f", roll.remainingMeters)} از ${roll.initialMeters} متر",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textPrimary,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "${(remainingPercent * 100).toInt()}٪ باقی‌مانده",
            style = MaterialTheme.typography.labelSmall,
            color = if (remainingPercent > 0.3f) StatusSuccess else StatusDanger,
            fontWeight = FontWeight.Bold
          )
        }
        LinearProgressIndicator(
          progress = { remainingPercent.coerceIn(0f, 1f) },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = if (remainingPercent > 0.3f) AccentCyan else StatusDanger,
          trackColor = customColors.secondaryBg
        )
      }

      HorizontalDivider(color = customColors.border)

      // Metrics & Cost
      val remainingWeightKg = if (roll.initialMeters > 0.0) (roll.remainingMeters / roll.initialMeters) * roll.weightKg else 0.0
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text("قیمت خرید متری", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(roll.buyPricePerMeter), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Column {
          Text("وزن باقی‌مانده", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${String.format("%.1f", remainingWeightKg)} کیلو", style = MaterialTheme.typography.bodySmall, color = customColors.textSecondary)
        }
        Column {
          Text("سهم باربری تخصیص یافته", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(roll.allocatedShippingCost), style = MaterialTheme.typography.bodySmall, color = AccentBlue, fontWeight = FontWeight.SemiBold)
        }
      }

      // Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onHistory,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(Modifier.size(4.dp))
          Text("سوابق برش", style = MaterialTheme.typography.labelSmall)
        }

        if (!isFinished) {
          Button(
            onClick = onConsume,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("برش / مصرف", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun ShippingExpenseInventoryCard(
  expense: ShippingExpenseEntity
) {
  val customColors = LocalCustomColors.current
  val allocationLabel = when (expense.allocationMethod) {
    "PER_ITEM", "PER_ROLL" -> "تسهیم بر اساس تعداد طاقه"
    "PER_WEIGHT", "WEIGHTED" -> "تسهیم بر اساس وزن (کیلو)"
    "PER_QUANTITY" -> "تسهیم بر اساس تعداد قطعه"
    else -> "تسهیم (${expense.allocationMethod})"
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = expense.title,
            style = MaterialTheme.typography.titleSmall,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "باربری: ${expense.carrierName} • بارنامه: ${expense.trackingNumber.ifBlank { "ندارد" }}",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textMuted
          )
        }

        Text(
          text = CurrencyHelper.formatToman(expense.totalAmount),
          style = MaterialTheme.typography.titleSmall,
          color = AccentBlue,
          fontWeight = FontWeight.Bold
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("روش تسهیم: $allocationLabel", style = MaterialTheme.typography.labelSmall, color = AccentCyan)
        Text("تاریخ: ${expense.date}", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
      }

      if (expense.notes.isNotBlank()) {
        Text(
          text = "یادداشت: ${expense.notes}",
          style = MaterialTheme.typography.labelSmall,
          color = customColors.textSecondary
        )
      }
    }
  }
}
