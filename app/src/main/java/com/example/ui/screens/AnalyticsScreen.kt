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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConsumablesReportData
import com.example.data.model.CostReportData
import com.example.data.model.FabricReportData
import com.example.data.model.FreightReportData
import com.example.data.model.InventoryReportData
import com.example.data.model.OrdersReportData
import com.example.data.model.ProductionReportData
import com.example.data.model.ProfitReportData
import com.example.data.model.SalesReportData
import com.example.data.model.TailorCostReportData
import com.example.data.service.FinancialCalculationService
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.ManufacturingViewModel
import com.example.viewmodel.ReportCategory
import com.example.util.ChartFormatter

@Composable
fun AnalyticsScreen(
  viewModel: ManufacturingViewModel,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  val activeCategory by viewModel.activeReportCategory.collectAsState()

  val salesReport by viewModel.salesReport.collectAsState()
  val productionReport by viewModel.productionReport.collectAsState()
  val inventoryReport by viewModel.inventoryReport.collectAsState()
  val ordersReport by viewModel.ordersReport.collectAsState()
  val costReport by viewModel.costReport.collectAsState()
  val profitReport by viewModel.profitReport.collectAsState()
  val consumablesReport by viewModel.consumablesReport.collectAsState()
  val fabricReport by viewModel.fabricReport.collectAsState()
  val freightReport by viewModel.freightReport.collectAsState()
  val tailorReport by viewModel.tailorCostReport.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header & Title
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = "مرکز گزارشات و هوش تجاری کارخانه",
          style = MaterialTheme.typography.titleLarge,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "تحلیل جامع فروش، تولید، موجودی، هزینه‌ها، سود، ملزومات، پارچه، باربری و خیاط‌کاران",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textMuted
        )
      }
    }

    // 2. Horizontal Scrollable Report Categories
    item {
      val scrollState = rememberScrollState()
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ReportCategory.entries.forEach { cat ->
          val isSelected = activeCategory == cat
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(if (isSelected) AccentIndigo else customColors.card)
              .border(
                1.dp,
                if (isSelected) AccentIndigo else customColors.border,
                RoundedCornerShape(20.dp)
              )
              .clickable { viewModel.setReportCategory(cat) }
              .padding(horizontal = 14.dp, vertical = 8.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(text = cat.icon, fontSize = 13.sp)
              Text(
                text = cat.title,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Color.White else customColors.textPrimary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }
    }

    // 3. Dynamic Section Content
    when (activeCategory) {
      ReportCategory.OVERVIEW -> {
        item {
          ReportsOverviewSection(
            sales = salesReport,
            production = productionReport,
            inventory = inventoryReport,
            profit = profitReport,
            costs = costReport,
            onDrillDown = { key -> viewModel.openDrillDown(key) },
            onSelectCategory = { cat -> viewModel.setReportCategory(cat) }
          )
        }
      }

      ReportCategory.SALES -> {
        item {
          SalesReportSection(
            report = salesReport,
            onDrillDown = { viewModel.openDrillDown("TOTAL_SALES") }
          )
        }
      }

      ReportCategory.PRODUCTION -> {
        item {
          ProductionReportSection(
            report = productionReport,
            onDrillDown = { viewModel.openDrillDown("TOTAL_PRODUCTION") }
          )
        }
      }

      ReportCategory.INVENTORY -> {
        item {
          InventoryReportSection(
            report = inventoryReport,
            onDrillDown = { viewModel.openDrillDown("INVENTORY_VALUE") }
          )
        }
      }

      ReportCategory.ORDERS -> {
        item {
          OrdersReportSection(
            report = ordersReport,
            onDrillDown = { viewModel.openDrillDown("TOTAL_SALES") }
          )
        }
      }

      ReportCategory.COSTS -> {
        item {
          CostReportSection(
            report = costReport,
            onDrillDown = { viewModel.openDrillDown("TOTAL_COST") }
          )
        }
      }

      ReportCategory.PROFIT -> {
        item {
          ProfitReportSection(
            report = profitReport,
            costs = costReport,
            onDrillDown = { viewModel.openDrillDown("NET_PROFIT") }
          )
        }
      }

      ReportCategory.CONSUMABLES -> {
        item {
          ConsumablesReportSection(
            report = consumablesReport
          )
        }
      }

      ReportCategory.FABRIC -> {
        item {
          FabricReportSection(
            report = fabricReport
          )
        }
      }

      ReportCategory.FREIGHT -> {
        item {
          FreightReportSection(
            report = freightReport,
            onDrillDown = { viewModel.openDrillDown("FREIGHT_COST") }
          )
        }
      }

      ReportCategory.TAILOR_COST -> {
        item {
          TailorCostReportSection(
            report = tailorReport,
            onDrillDown = { viewModel.openDrillDown("TAILOR_COST") }
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

// ======================= 1. OVERVIEW SECTION =======================

@Composable
private fun ReportsOverviewSection(
  sales: SalesReportData,
  production: ProductionReportData,
  inventory: InventoryReportData,
  profit: ProfitReportData,
  costs: CostReportData,
  onDrillDown: (String) -> Unit,
  onSelectCategory: (ReportCategory) -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // Top Hero Card: Net Profit
    ClickableMetricHeroCard(
      title = "سود خالص دوره (Net Profit)",
      amount = FinancialCalculationService.formatCurrency(profit.netProfit),
      subtitle = "حاشیه سود واقعی: ${profit.profitMarginPercent.toInt()}٪ • برای کاوش اجزا کلیک کنید",
      badge = "کاوش تفصیلی 🔍",
      accentColor = StatusSuccess,
      onClick = { onDrillDown("NET_PROFIT") }
    )

    // Key Metric Grid (2x2)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      ClickableKpiBox(
        modifier = Modifier.weight(1f),
        title = "کل فروش",
        value = FinancialCalculationService.formatCurrency(sales.totalSalesAmount),
        caption = "${sales.salesCount} سفارش ثبت‌شده",
        accentColor = AccentIndigo,
        onClick = { onDrillDown("TOTAL_SALES") }
      )
      ClickableKpiBox(
        modifier = Modifier.weight(1f),
        title = "تیراژ کل تولید",
        value = "${production.totalProductionCount} عدد",
        caption = "${production.completedCount} کار آماده",
        accentColor = AccentCyan,
        onClick = { onDrillDown("TOTAL_PRODUCTION") }
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      ClickableKpiBox(
        modifier = Modifier.weight(1f),
        title = "ارزش موجودی انبار",
        value = FinancialCalculationService.formatCurrency(inventory.totalInventoryValue),
        caption = "${inventory.remainingRollsCount} طاقه در دسترس",
        accentColor = AccentPurple,
        onClick = { onDrillDown("INVENTORY_VALUE") }
      )
      ClickableKpiBox(
        modifier = Modifier.weight(1f),
        title = "کل هزینه‌های دوره",
        value = FinancialCalculationService.formatCurrency(costs.totalCost),
        caption = "مواد، دوخت، باربری، سربار",
        accentColor = StatusWarning,
        onClick = { onDrillDown("TOTAL_COST") }
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Direct Quick Links to All 10 Detailed Reports
    Text(
      text = "دسترسی سریع به ۱۰ گزارش تفکیکی",
      style = MaterialTheme.typography.labelLarge,
      color = customColors.textPrimary,
      fontWeight = FontWeight.Bold
    )

    val quickReports = listOf(
      ReportCategory.SALES to "فروش روزانه، ماهانه و پرفروش‌ها",
      ReportCategory.PRODUCTION to "تیراژ به تفکیک مدل، طاقه و تاریخ",
      ReportCategory.INVENTORY to "موجودی طاقه‌ها، ملزومات و هشدارهای کسری",
      ReportCategory.ORDERS to "وضعیت تحویل و میانگین سفارشات",
      ReportCategory.COSTS to "تفکیک ۶ گانه هزینه‌های تمام‌شده",
      ReportCategory.PROFIT to "درآمد، بهای تمام‌شده و سود ناخالص",
      ReportCategory.CONSUMABLES to "خرید، مصرف و موجودی خرج‌کار",
      ReportCategory.FABRIC to "متراژ ورودی، مصرفی و باقیمانده طاقه‌ها",
      ReportCategory.FREIGHT to "میانگین کرایه هر طاقه، کیلو و فاکتورها",
      ReportCategory.TAILOR_COST to "دستمزد دوخت خطوط تولید و خیاط‌کاران"
    )

    quickReports.forEach { (cat, desc) ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.card)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .clickable { onSelectCategory(cat) }
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(text = cat.icon, fontSize = 16.sp)
            Column {
              Text(
                text = "گزارش ${cat.title}",
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
          }
          Text(
            text = "مشاهده ←",
            style = MaterialTheme.typography.labelSmall,
            color = AccentIndigo,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

// ======================= 2. SALES REPORT =======================

@Composable
private fun SalesReportSection(
  report: SalesReportData,
  onDrillDown: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    ClickableMetricHeroCard(
      title = "کل فروش دوره (Total Sales)",
      amount = FinancialCalculationService.formatCurrency(report.totalSalesAmount),
      subtitle = "تعداد کل سفارشات ثبت‌شده: ${report.salesCount} عدد",
      badge = "کاوش تفصیلی فروش",
      accentColor = AccentIndigo,
      onClick = onDrillDown
    )

    // Daily / Monthly / Yearly breakdown
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "فروش امروز",
        value = FinancialCalculationService.formatCurrency(report.dailySalesAmount)
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "فروش ۳۰ روزه",
        value = FinancialCalculationService.formatCurrency(report.monthlySalesAmount)
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "فروش سالانه",
        value = FinancialCalculationService.formatCurrency(report.yearlySalesAmount)
      )
    }

    // Best Products
    if (report.bestProducts.isNotEmpty()) {
      Text(
        text = "بهترین و پرفروش‌ترین محصولات (Best Products)",
        style = MaterialTheme.typography.labelLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      report.bestProducts.forEachIndexed { idx, p ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(customColors.card)
            .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
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
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(StatusSuccess.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${idx + 1}",
                  style = MaterialTheme.typography.labelSmall,
                  color = StatusSuccess,
                  fontWeight = FontWeight.Bold
                )
              }
              Column {
                Text(
                  text = p.modelName,
                  style = MaterialTheme.typography.bodyMedium,
                  color = customColors.textPrimary,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "کد: ${p.modelCode} • تیراژ فروش: ${p.unitsSold} عدد",
                  style = MaterialTheme.typography.bodySmall,
                  color = customColors.textMuted,
                  fontSize = 11.sp
                )
              }
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = FinancialCalculationService.formatCurrency(p.totalSalesAmount),
                style = MaterialTheme.typography.labelLarge,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "حاشیه سود: ${p.marginPercent.toInt()}٪",
                style = MaterialTheme.typography.labelSmall,
                color = StatusSuccess,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }

    // Worst Products
    if (report.worstProducts.isNotEmpty()) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "محصولات با فروش پایین یا نیازمند توجه (Worst Products)",
        style = MaterialTheme.typography.labelLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      report.worstProducts.forEach { p ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(customColors.card)
            .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = p.modelName,
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "فروش ثبت شده: ${p.unitsSold} عدد",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
            Text(
              text = FinancialCalculationService.formatCurrency(p.totalSalesAmount),
              style = MaterialTheme.typography.bodyMedium,
              color = customColors.textSecondary,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

// ======================= 3. PRODUCTION REPORT =======================

@Composable
private fun ProductionReportSection(
  report: ProductionReportData,
  onDrillDown: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    ClickableMetricHeroCard(
      title = "کل تیراژ تولید کارگاه (Total Production)",
      amount = "${report.totalProductionCount} عدد",
      subtitle = "تکمیل‌شده: ${report.completedCount} عدد • در جریان دوخت: ${report.inProgressCount} عدد",
      badge = "کاوش تفصیلی تولید",
      accentColor = AccentCyan,
      onClick = onDrillDown
    )

    // Completion Status Progress Bar
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
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "نسبت پیشرفت تولید و آماده‌سازی",
            style = MaterialTheme.typography.bodyMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
          val pct = if (report.totalProductionCount > 0) {
            ((report.completedCount.toDouble() / report.totalProductionCount) * 100).toInt()
          } else 0
          Text(
            text = "$pct٪ تکمیل",
            style = MaterialTheme.typography.labelMedium,
            color = StatusSuccess,
            fontWeight = FontWeight.Bold
          )
        }

        LinearProgressIndicator(
          progress = {
            if (report.totalProductionCount > 0) {
              (report.completedCount.toFloat() / report.totalProductionCount).coerceIn(0f, 1f)
            } else 0f
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = StatusSuccess,
          trackColor = customColors.border
        )
      }
    }

    // By Product
    if (report.byProduct.isNotEmpty()) {
      Text(
        text = "تیراژ و بهای تمام‌شده به تفکیک مدل (By Product)",
        style = MaterialTheme.typography.labelLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      report.byProduct.forEach { prod ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(customColors.card)
            .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = prod.modelName,
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "کد: ${prod.modelCode} • تیراژ: ${prod.totalQuantity} عدد",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = FinancialCalculationService.formatCurrency(prod.totalCost),
                style = MaterialTheme.typography.labelMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "بهای هر کار: ${FinancialCalculationService.formatCurrency(prod.unitCost)}",
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textMuted,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }

    // By Fabric Roll
    if (report.byFabricRoll.isNotEmpty()) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "تفکیک تولید به ازای طاقه‌های پارچه (By Fabric Roll)",
        style = MaterialTheme.typography.labelLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      report.byFabricRoll.forEach { r ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(customColors.card)
            .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "طاقه شماره: ${r.rollNumber}",
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "جنس: ${r.fabricType} • مدل‌های برش‌خورده: ${r.modelsProduced.joinToString("، ")}",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
            Text(
              text = "${r.totalItemsProduced} عدد",
              style = MaterialTheme.typography.labelLarge,
              color = AccentCyan,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

// ======================= 4. INVENTORY REPORT =======================

@Composable
private fun InventoryReportSection(
  report: InventoryReportData,
  onDrillDown: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    ClickableMetricHeroCard(
      title = "ارزش کل موجودی انبار (Inventory Value)",
      amount = FinancialCalculationService.formatCurrency(report.totalInventoryValue),
      subtitle = "ارزش پارچه‌ها: ${FinancialCalculationService.formatCurrency(report.fabricValue)} • ملزومات: ${FinancialCalculationService.formatCurrency(report.consumablesValue)}",
      badge = "کاوش موجودی",
      accentColor = AccentPurple,
      onClick = onDrillDown
    )

    // Summary Metric Cards
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "طاقه‌های موجود",
        value = "${report.remainingRollsCount} طاقه"
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "متراژ کل پارچه",
        value = "${report.fabricTotalMeters.toInt()} متر"
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "اقلام ملزومات",
        value = "${report.consumablesItemCount} عدد"
      )
    }

    // Critical Stock (کسری موجودی بحرانی)
    if (report.criticalStockItems.isNotEmpty()) {
      Text(
        text = "اقلام با کسری بحرانی موجودی (Critical Stock - نیازمند تأمین فوری)",
        style = MaterialTheme.typography.labelLarge,
        color = StatusDanger,
        fontWeight = FontWeight.Bold
      )

      report.criticalStockItems.forEach { item ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(StatusDanger.copy(alpha = 0.08f))
            .border(1.dp, StatusDanger.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "دسته: ${item.category} • حداقل استاندارد: ${item.minThreshold.toInt()} ${item.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(StatusDanger)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "${item.currentStock.toInt()} ${item.unit}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // Low Stock (اقلام در آستانه کمبود)
    if (report.lowStockItems.isNotEmpty()) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "اقلام در آستانه کمبود (Low Stock)",
        style = MaterialTheme.typography.labelLarge,
        color = StatusWarning,
        fontWeight = FontWeight.Bold
      )

      report.lowStockItems.forEach { item ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(StatusWarning.copy(alpha = 0.08f))
            .border(1.dp, StatusWarning.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "دسته: ${item.category} • حداقل: ${item.minThreshold.toInt()} ${item.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
            Text(
              text = "${item.currentStock.toInt()} ${item.unit}",
              style = MaterialTheme.typography.labelMedium,
              color = StatusWarning,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

// ======================= 5. ORDERS REPORT =======================

@Composable
private fun OrdersReportSection(
  report: OrdersReportData,
  onDrillDown: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    ClickableMetricHeroCard(
      title = "کل سفارشات فروش (Total Orders)",
      amount = FinancialCalculationService.formatCurrency(report.totalOrdersAmount),
      subtitle = "تعداد کل: ${report.totalOrdersCount} سفارش • میانگین ارزش هر سفارش: ${FinancialCalculationService.formatCurrency(report.averageOrderValue)}",
      badge = "کاوش سفارشات",
      accentColor = AccentIndigo,
      onClick = onDrillDown
    )

    // Pipeline breakdown
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "ثبت اولیه",
        value = "${report.orderedCount} عدد"
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "در خط تولید",
        value = "${report.inProductionCount} عدد"
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "آماده ارسال",
        value = "${report.readyToShipCount} عدد"
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "تحویل مشتری",
        value = "${report.deliveredCount} عدد"
      )
    }

    // Recent orders list
    if (report.recentOrders.isNotEmpty()) {
      Text(
        text = "آخرین سفارشات ثبتی کارخانه",
        style = MaterialTheme.typography.labelLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      report.recentOrders.forEach { ord ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(customColors.card)
            .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${ord.customerName} • ${ord.modelName}",
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${ord.quantity} عدد • ثبت: ${ord.orderDate}",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = FinancialCalculationService.formatCurrency(FinancialCalculationService.calculateOrderNetTotal(ord)),
                style = MaterialTheme.typography.labelMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = ord.deliveryStatus,
                style = MaterialTheme.typography.labelSmall,
                color = AccentCyan,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }
  }
}

// ======================= 6. COSTS REPORT =======================

@Composable
private fun CostReportSection(
  report: CostReportData,
  onDrillDown: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    ClickableMetricHeroCard(
      title = "ساختار کل هزینه‌ها (Total Cost)",
      amount = FinancialCalculationService.formatCurrency(report.totalCost),
      subtitle = "تفکیک ۶ گانه هزینه‌های تمام‌شده مواد، خیاط، ملزومات، باربری و سربار",
      badge = "کاوش هزینه‌ها",
      accentColor = StatusWarning,
      onClick = onDrillDown
    )

    // Cost Breakdown Items
    val items = listOf(
      Triple("هزینه پارچه و مواد اصلی (Material)", report.materialCost, AccentIndigo),
      Triple("دستمزد دوخت و خیاط‌کاران (Tailor)", report.tailorCost, AccentCyan),
      Triple("ملزومات و خرج‌کار خیاطی (Consumables)", report.consumablesCost, AccentPurple),
      Triple("کرایه حمل و باربری بارنامه (Freight)", report.freightCost, AccentBlue),
      Triple("هزینه‌های ثابت و اجاره کارگاه (Fixed)", report.fixedCosts, StatusWarning),
      Triple("سربار و ضایعات تولید (Other)", report.otherCosts, customColors.textMuted)
    )

    items.forEach { (label, amount, color) ->
      val total = report.totalCost.toDouble().coerceAtLeast(1.0)
      val pct = ((amount.toDouble() / total) * 100).toInt()

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.card)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .padding(12.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = label,
              style = MaterialTheme.typography.bodyMedium,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = FinancialCalculationService.formatCurrency(amount),
              style = MaterialTheme.typography.labelLarge,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            LinearProgressIndicator(
              progress = { (pct / 100f).coerceIn(0f, 1f) },
              modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = color,
              trackColor = customColors.border
            )
            Text(
              text = "$pct٪",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.textMuted,
              fontSize = 11.sp
            )
          }
        }
      }
    }
  }
}

// ======================= 7. PROFIT REPORT =======================

@Composable
private fun ProfitReportSection(
  report: ProfitReportData,
  costs: CostReportData,
  onDrillDown: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    ClickableMetricHeroCard(
      title = "سود خالص نهایی کارگاه (Net Profit)",
      amount = FinancialCalculationService.formatCurrency(report.netProfit),
      subtitle = "درآمد منهای کل بهای تمام‌شده و هزینه‌های جاری کارگاه",
      badge = "کاوش تفصیلی سود",
      accentColor = StatusSuccess,
      onClick = onDrillDown
    )

    // Financial Flow Cards: Revenue -> COGS -> Gross Profit -> Expenses -> Net Profit
    val flowItems = listOf(
      Triple("درآمد کل فروش (Revenue)", report.revenue, true),
      Triple("بهای تمام‌شده کالا (COGS)", report.cogs, false),
      Triple("سود ناخالص (Gross Profit)", report.grossProfit, true),
      Triple("هزینه‌های جاری و ثابت (Expenses)", report.expenses, false),
      Triple("سود خالص (Net Profit)", report.netProfit, true)
    )

    flowItems.forEach { (label, amt, isPos) ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.card)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "${if (isPos) "+" else "-"}${FinancialCalculationService.formatCurrency(amt)}",
            style = MaterialTheme.typography.labelLarge,
            color = if (isPos) StatusSuccess else customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Profit Margin Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(StatusSuccess.copy(alpha = 0.08f))
        .border(1.dp, StatusSuccess.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "درصد حاشیه سود نهایی (Profit Margin)",
            style = MaterialTheme.typography.bodyMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "نسبت سود خالص به کل درآمد ناخالص فروش",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.textMuted
          )
        }
        Text(
          text = "${report.profitMarginPercent.toInt()}٪",
          style = MaterialTheme.typography.headlineSmall,
          color = StatusSuccess,
          fontWeight = FontWeight.ExtraBold
        )
      }
    }
  }
}

// ======================= 8. CONSUMABLES REPORT =======================

@Composable
private fun ConsumablesReportSection(
  report: ConsumablesReportData
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // Top Hero Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(18.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "گزارش ملزومات و خرج‌کار خیاطی (Consumables)",
          style = MaterialTheme.typography.labelSmall,
          color = customColors.textMuted
        )
        Text(
          text = FinancialCalculationService.formatCurrency(report.totalPurchasedValue),
          style = MaterialTheme.typography.headlineMedium,
          color = AccentPurple,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "ارزش کل خرید ملزومات دوره • مصرف در تولید: ${FinancialCalculationService.formatCurrency(report.totalUsedValue)}",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textSecondary
        )
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "کل خرید ملزومات",
        value = FinancialCalculationService.formatCurrency(report.totalPurchasedValue)
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "مصرف در خط دوخت",
        value = FinancialCalculationService.formatCurrency(report.totalUsedValue)
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "ارزش مانده در انبار",
        value = FinancialCalculationService.formatCurrency(report.remainingStockValue)
      )
    }

    // Per Item Details
    if (report.items.isNotEmpty()) {
      Text(
        text = "تفکیک وضعیت به ازای هر قلم ملزومات",
        style = MaterialTheme.typography.labelLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      report.items.forEach { item ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(customColors.card)
            .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "موجودی فعلی: ${item.stockQuantity.toInt()} ${item.unit} • مصرف: ${FinancialCalculationService.formatCurrency(item.usedValue)}",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = FinancialCalculationService.formatCurrency(item.purchasedValue),
                style = MaterialTheme.typography.labelMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "خرید ثبت‌شده",
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textMuted,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }
  }
}

// ======================= 9. FABRIC REPORT =======================

@Composable
private fun FabricReportSection(
  report: FabricReportData
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(18.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "گزارش جامع پارچه و طاقه‌ها (Fabric & Rolls)",
          style = MaterialTheme.typography.labelSmall,
          color = customColors.textMuted
        )
        Text(
          text = "${report.remainingMeters.toInt()} متر پارچه موجود",
          style = MaterialTheme.typography.headlineMedium,
          color = AccentIndigo,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${report.activeRollsCount} طاقه فعال از مجموع ${report.totalRolls} طاقه • ارزش کل: ${FinancialCalculationService.formatCurrency(report.totalFabricCost)}",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textSecondary
        )
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "متراژ ورودی (Inbound)",
        value = "${report.inboundMeters.toInt()} متر"
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "متراژ مصرفی (Consumed)",
        value = "${report.consumedMeters.toInt()} متر"
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "متراژ مانده (Remaining)",
        value = "${report.remainingMeters.toInt()} متر"
      )
    }

    // Roll-by-roll list
    if (report.rolls.isNotEmpty()) {
      Text(
        text = "فهرست طاقه‌های ثبت‌شده و باربری تخصیصی",
        style = MaterialTheme.typography.labelLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      report.rolls.forEach { r ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(customColors.card)
            .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "طاقه کد: ${r.rollNumber} • ${r.fabricType}",
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "متراژ اولیه: ${r.initialMeters.toInt()} م • باقیمانده: ${r.remainingMeters.toInt()} م • باربری: ${FinancialCalculationService.formatCurrency(r.freightAllocated)}",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
            Text(
              text = FinancialCalculationService.formatCurrency(r.totalCost),
              style = MaterialTheme.typography.labelMedium,
              color = AccentIndigo,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

// ======================= 10. FREIGHT REPORT =======================

@Composable
private fun FreightReportSection(
  report: FreightReportData,
  onDrillDown: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    ClickableMetricHeroCard(
      title = "کل هزینه باربری و حمل (Total Freight)",
      amount = FinancialCalculationService.formatCurrency(report.totalFreightExpense),
      subtitle = "ثبت‌شده در ${report.totalBillsCount} فقره بارنامه باربری",
      badge = "کاوش باربری",
      accentColor = AccentBlue,
      onClick = onDrillDown
    )

    // Averages breakdown
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "میانگین هر طاقه",
        value = FinancialCalculationService.formatCurrency(report.avgPerRoll)
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "میانگین هر کیلو",
        value = FinancialCalculationService.formatCurrency(report.avgPerKg)
      )
      SummaryMetricPill(
        modifier = Modifier.weight(1f),
        label = "میانگین هر واحد",
        value = FinancialCalculationService.formatCurrency(report.avgPerUnit)
      )
    }

    // Bills List
    if (report.bills.isNotEmpty()) {
      Text(
        text = "بارنامه‌ها و هزینه‌های حمل ورودی کارگاه",
        style = MaterialTheme.typography.labelLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      report.bills.forEach { b ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(customColors.card)
            .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${b.title} • بارنامه ${b.trackingNumber.ifEmpty { b.id.toString() }}",
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "باربری: ${b.carrierName} • وزن: ${b.totalWeightKg} ک‌گ • ${b.itemCount} قلم • تاریخ: ${b.date}",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
            Text(
              text = FinancialCalculationService.formatCurrency(b.totalAmount),
              style = MaterialTheme.typography.labelMedium,
              color = AccentBlue,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

// ======================= 11. TAILOR COST REPORT =======================

@Composable
private fun TailorCostReportSection(
  report: TailorCostReportData,
  onDrillDown: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    ClickableMetricHeroCard(
      title = "کل هزینه و دستمزد خیاط‌کاران (Tailor Cost)",
      amount = FinancialCalculationService.formatCurrency(report.totalTailorCost),
      subtitle = "میانگین دستمزد دوخت هر عدد کار: ${FinancialCalculationService.formatCurrency(report.avgWagePerModel)}",
      badge = "کاوش خیاط‌کار",
      accentColor = AccentCyan,
      onClick = onDrillDown
    )

    // Batch details
    if (report.batches.isNotEmpty()) {
      Text(
        text = "تفکیک دستمزد به ازای بچ‌ها و خطوط تولید",
        style = MaterialTheme.typography.labelLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      report.batches.forEach { b ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(customColors.card)
            .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${b.modelName} (بچ ${b.batchNumber})",
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "تیراژ: ${b.quantity} عدد • دستمزد هر کار: ${FinancialCalculationService.formatCurrency(b.tailorWagePerItem)}",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
            Text(
              text = FinancialCalculationService.formatCurrency(b.totalBatchWage),
              style = MaterialTheme.typography.labelMedium,
              color = AccentCyan,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

// ======================= REUSABLE COMPONENT HELPERS =======================

@Composable
private fun ClickableMetricHeroCard(
  title: String,
  amount: String,
  subtitle: String,
  badge: String,
  accentColor: Color,
  onClick: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(18.dp))
      .clickable(onClick = onClick)
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall,
          color = customColors.textMuted
        )
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = badge,
            style = MaterialTheme.typography.labelSmall,
            color = accentColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Text(
        text = amount,
        style = MaterialTheme.typography.headlineMedium,
        color = accentColor,
        fontWeight = FontWeight.ExtraBold
      )

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = customColors.textSecondary
      )
    }
  }
}

@Composable
private fun ClickableKpiBox(
  modifier: Modifier = Modifier,
  title: String,
  value: String,
  caption: String,
  accentColor: Color,
  onClick: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(12.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = customColors.textMuted,
        fontSize = 11.sp
      )
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        color = accentColor,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = caption,
        style = MaterialTheme.typography.bodySmall,
        color = customColors.textSecondary,
        fontSize = 10.sp
      )
    }
  }
}

@Composable
private fun SummaryMetricPill(
  modifier: Modifier = Modifier,
  label: String,
  value: String
) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
      .padding(10.dp)
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(2.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = customColors.textMuted,
        fontSize = 10.sp
      )
      Text(
        text = value,
        style = MaterialTheme.typography.labelMedium,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
    }
  }
}
