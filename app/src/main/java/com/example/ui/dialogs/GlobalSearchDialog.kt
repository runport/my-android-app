package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.components.CurrencyHelper
import com.example.viewmodel.ManufacturingViewModel

private enum class SearchCategory(val title: String, val icon: ImageVector) {
  ALL("همه نتایج", Icons.Default.FilterList),
  PRODUCTS("کالاها و مدل‌ها", Icons.Default.Inventory2),
  ROLLS("طاقه‌های پارچه", Icons.Default.Layers),
  CUSTOMERS("مشتریان", Icons.Default.Person),
  SUPPLIERS("تأمین‌کنندگان", Icons.Default.Store),
  ORDERS("سفارش‌های فروش", Icons.Default.ReceiptLong),
  MATERIALS("مواد و ملزومات", Icons.Default.Widgets)
}

private data class SearchResultItem(
  val id: Long,
  val category: SearchCategory,
  val title: String,
  val subtitle: String,
  val badge: String,
  val detail: String,
  val badgeColor: Color
)

@Composable
fun GlobalSearchDialog(
  viewModel: ManufacturingViewModel,
  onDismiss: () -> Unit
) {
  val customColors = LocalCustomColors.current
  var query by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(SearchCategory.ALL) }

  val products by viewModel.products.collectAsState()
  val rolls by viewModel.fabricRolls.collectAsState()
  val customers by viewModel.customers.collectAsState()
  val suppliers by viewModel.suppliers.collectAsState()
  val orders by viewModel.salesOrders.collectAsState()
  val materials by viewModel.materials.collectAsState()

  // Search execution
  val allResults = remember(query, products, rolls, customers, suppliers, orders, materials) {
    val q = query.trim().lowercase()
    val list = mutableListOf<SearchResultItem>()

    if (q.isNotEmpty()) {
      // 1. Products
      products.filter {
        it.name.lowercase().contains(q) || it.code.lowercase().contains(q) || it.categoryName.lowercase().contains(q)
      }.forEach { p ->
        list.add(
          SearchResultItem(
            id = p.id,
            category = SearchCategory.PRODUCTS,
            title = p.name,
            subtitle = "کد: ${p.code} • دسته: ${p.categoryName}",
            badge = "مدل تولیدی",
            detail = "فروش: ${CurrencyHelper.formatNumber(p.effectiveSellingPrice)} تومان | هزینه: ${CurrencyHelper.formatNumber(p.currentCostPrice)}",
            badgeColor = AccentIndigo
          )
        )
      }

      // 2. Rolls
      rolls.filter {
        it.rollCode.lowercase().contains(q) || it.fabricType.lowercase().contains(q) || it.color.lowercase().contains(q)
      }.forEach { r ->
        list.add(
          SearchResultItem(
            id = r.id,
            category = SearchCategory.ROLLS,
            title = "طاقه ${r.rollCode} - ${r.fabricType}",
            subtitle = "رنگ: ${r.color} • وزن اولیه: ${r.weightKg} کیلوگرم",
            badge = "طاقه پارچه",
            detail = "باقی‌مانده: ${r.remainingMeters} از ${r.initialMeters} متر • وضعیت: ${r.status}",
            badgeColor = AccentCyan
          )
        )
      }

      // 3. Customers
      customers.filter {
        it.name.lowercase().contains(q) || it.company.lowercase().contains(q) || it.phone.lowercase().contains(q)
      }.forEach { c ->
        list.add(
          SearchResultItem(
            id = c.id,
            category = SearchCategory.CUSTOMERS,
            title = c.name,
            subtitle = "فروشگاه/شرکت: ${c.company} • تلفن: ${c.phone}",
            badge = "مشتری",
            detail = "مجموع خریدها: ${CurrencyHelper.formatNumber(c.totalPurchases)} تومان • مانده بدهی: ${CurrencyHelper.formatNumber(c.currentDebt)}",
            badgeColor = StatusSuccess
          )
        )
      }

      // 4. Suppliers
      suppliers.filter {
        it.name.lowercase().contains(q) || it.storeName.lowercase().contains(q) || it.phone.lowercase().contains(q)
      }.forEach { s ->
        list.add(
          SearchResultItem(
            id = s.id,
            category = SearchCategory.SUPPLIERS,
            title = s.name,
            subtitle = "فروشگاه: ${s.storeName} • رسته: ${s.distributionCategory}",
            badge = "تأمین‌کننده",
            detail = "تلفن: ${s.phone} • مانده بدهی: ${CurrencyHelper.formatNumber(s.currentDebt)} تومان",
            badgeColor = AccentPurple
          )
        )
      }

      // 5. Orders
      orders.filter {
        it.orderNumber.lowercase().contains(q) || it.customerName.lowercase().contains(q) || it.modelName.lowercase().contains(q)
      }.forEach { o ->
        list.add(
          SearchResultItem(
            id = o.id,
            category = SearchCategory.ORDERS,
            title = "سفارش ${o.orderNumber} - ${o.customerName}",
            subtitle = "مدل: ${o.modelName} • تعداد: ${o.quantity} عدد",
            badge = "سفارش فروش",
            detail = "مبلغ کل: ${CurrencyHelper.formatNumber(o.netTotal)} تومان • وضعیت: ${o.deliveryStatus}",
            badgeColor = StatusWarning
          )
        )
      }

      // 6. Materials
      materials.filter {
        it.name.lowercase().contains(q) || it.code.lowercase().contains(q) || it.category.lowercase().contains(q)
      }.forEach { m ->
        list.add(
          SearchResultItem(
            id = m.id,
            category = SearchCategory.MATERIALS,
            title = m.name,
            subtitle = "کد: ${m.code} • واحد: ${m.unit}",
            badge = "ماده اولیه / ملزومات",
            detail = "نرخ روز: ${CurrencyHelper.formatNumber(m.currentPrice)} تومان • نرخ خرید: ${CurrencyHelper.formatNumber(m.lastPurchasePrice)}",
            badgeColor = AccentBlue
          )
        )
      }
    }

    list
  }

  val filteredResults = remember(allResults, selectedCategory) {
    if (selectedCategory == SearchCategory.ALL) allResults
    else allResults.filter { it.category == selectedCategory }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.88f)
        .clip(RoundedCornerShape(16.dp)),
      color = customColors.card,
      border = BorderStroke(1.dp, customColors.border)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AccentBlue.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Search, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
            }
            Column {
              Text(
                text = "جستجوی جامع کارگاه",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = customColors.textPrimary
              )
              Text(
                text = "مدل، طاقه، مشتری، سفارش، تأمین‌کننده، مواد اولیه",
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textMuted
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "بستن", tint = customColors.textMuted)
          }
        }

        // Search Input TextField
        OutlinedTextField(
          value = query,
          onValueChange = { query = it },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("global_search_input"),
          placeholder = {
            Text("عبارت جستجو را بنویسید (مثلاً اسلش، ROL-101، علی، کتان...)", color = customColors.textMuted, fontSize = 12.sp)
          },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = AccentBlue)
          },
          trailingIcon = {
            if (query.isNotEmpty()) {
              IconButton(onClick = { query = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "پاک کردن", tint = customColors.textMuted)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBlue,
            unfocusedBorderColor = customColors.border,
            focusedContainerColor = customColors.bg,
            unfocusedContainerColor = customColors.bg,
            focusedTextColor = customColors.textPrimary,
            unfocusedTextColor = customColors.textPrimary
          )
        )

        // Category Filter Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(SearchCategory.values()) { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = {
                Text(
                  text = cat.title,
                  fontSize = 11.sp,
                  fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal
                )
              },
              leadingIcon = {
                Icon(cat.icon, contentDescription = null, modifier = Modifier.size(14.dp))
              },
              shape = RoundedCornerShape(20.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = AccentBlue.copy(alpha = 0.2f),
                selectedLabelColor = AccentBlue,
                selectedLeadingIconColor = AccentBlue
              )
            )
          }
        }

        // Quick Tag Suggestions when empty
        if (query.isBlank()) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "واژه‌های پرتکرار و پیشنهادی کارگاه:",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.textMuted
            )
            val suggestedTags = listOf("اسلش", "M201", "کتان بنگال", "طاقه", "کش", "تهران", "تکمیل شده", "الگانس")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              suggestedTags.take(4).forEach { tag ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(customColors.bg)
                    .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
                    .clickable { query = tag }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(tag, style = MaterialTheme.typography.labelSmall, color = customColors.textPrimary)
                }
              }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              suggestedTags.drop(4).forEach { tag ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(customColors.bg)
                    .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
                    .clickable { query = tag }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(tag, style = MaterialTheme.typography.labelSmall, color = customColors.textPrimary)
                }
              }
            }
          }
        }

        // Results List
        if (query.isNotBlank() && filteredResults.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.Search, contentDescription = null, tint = customColors.textMuted, modifier = Modifier.size(36.dp))
              Text("موردی مطابق با «$query» یافت نشد.", color = customColors.textMuted, fontSize = 13.sp)
              Text("کلمات کلیدی دیگری را جستجو کنید.", color = customColors.textMuted, fontSize = 11.sp)
            }
          }
        } else if (query.isNotBlank()) {
          Text(
            text = "${filteredResults.size} نتیجه یافت شد:",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textMuted
          )
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(filteredResults) { item ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("search_result_${item.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = customColors.bg),
                border = BorderStroke(1.dp, customColors.border)
              ) {
                Column(
                  modifier = Modifier.padding(12.dp),
                  verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = item.title,
                      style = MaterialTheme.typography.titleSmall,
                      fontWeight = FontWeight.Bold,
                      color = customColors.textPrimary
                    )
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(item.badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = item.badge,
                        color = item.badgeColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }

                  Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                  )

                  Text(
                    text = item.detail,
                    style = MaterialTheme.typography.labelSmall,
                    color = customColors.textPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }
        } else {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }
  }
}
