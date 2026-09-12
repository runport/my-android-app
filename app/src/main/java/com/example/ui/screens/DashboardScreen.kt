package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertItem
import com.example.data.model.ChartPoint
import com.example.data.model.DashboardChartType
import com.example.viewmodel.DashboardKpiState
import com.example.data.model.DashboardLayoutArrangement
import com.example.data.model.DonutSlice
import com.example.data.model.FactorySettingsEntity
import com.example.data.model.PeriodFilter
import com.example.data.model.SaleOrderEntity
import com.example.data.model.SectionSettingsTarget
import com.example.data.model.TimeRangeMode
import com.example.data.service.FinancialCalculationService
import com.example.ui.components.AlertCenterCard
import com.example.ui.components.ChartSettingsDialog
import com.example.ui.components.CurrencyHelper
import com.example.ui.components.CustomDateRangeDialog
import com.example.ui.components.DarkMinimalChart
import com.example.ui.components.ExecutiveCombinedChart
import com.example.ui.components.ExecutiveDonutChart
import com.example.ui.components.FixedCostBenchmarkCard
import com.example.ui.components.HeroKpiCard
import com.example.ui.components.PeriodSelectorPill
import com.example.ui.components.SecondaryKpiCard
import com.example.ui.components.StatusChip
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.MainTab
import com.example.viewmodel.ManufacturingViewModel
import com.example.viewmodel.MoreSubSection
import com.example.viewmodel.QuickActionType
import com.example.util.ChartFormatter
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DashboardScreen(
  viewModel: ManufacturingViewModel,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  val kpis by viewModel.kpiState.collectAsStateWithLifecycle()
  val periodFilter by viewModel.periodFilter.collectAsStateWithLifecycle()
  val alerts by viewModel.alerts.collectAsStateWithLifecycle()
  val orders by viewModel.salesOrders.collectAsStateWithLifecycle()
  val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
  val donutSlices by viewModel.inventoryDonutSlices.collectAsStateWithLifecycle()
  val factorySettings by viewModel.factorySettings.collectAsStateWithLifecycle()
  val timeRange by viewModel.timeRangeState.collectAsStateWithLifecycle()
  val combinedChartPoints by viewModel.combinedChartPoints.collectAsStateWithLifecycle()
  val chartDisplayMetric by viewModel.chartDisplayMetric.collectAsStateWithLifecycle()
  val showChartValues by viewModel.showChartValues.collectAsStateWithLifecycle()
  val customPeriodDays by viewModel.customPeriodDays.collectAsStateWithLifecycle()

  var showChartSettingsDialog by remember { mutableStateOf(false) }
  var showCustomDateRangeDialog by remember { mutableStateOf(false) }
  var showGlobalSearchDialog by remember { mutableStateOf(false) }

  val currentChartType = remember(factorySettings.dashboardChartType) {
    try {
      DashboardChartType.valueOf(factorySettings.dashboardChartType)
    } catch (_: Exception) {
      DashboardChartType.BAR_LINE
    }
  }

  val activeLayout = remember(factorySettings.dashboardLayout) {
    try {
      DashboardLayoutArrangement.valueOf(factorySettings.dashboardLayout)
    } catch (_: Exception) {
      DashboardLayoutArrangement.CLASSIC
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header with Section Pencil Icon ✏️
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          verticalArrangement = Arrangement.spacedBy(4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = factorySettings.companyName.ifEmpty { "تولیدی برتر" },
              style = MaterialTheme.typography.headlineSmall,
              color = customColors.textPrimary,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 0.3.sp
            )
            SectionEditIcon(
              onClick = { viewModel.openSectionSettings(SectionSettingsTarget.HEADER) }
            )
          }
          Text(
            text = "سیستم مدیریت اجرایی کارخانه پوشاک",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.textMuted,
            fontWeight = FontWeight.Medium
          )
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Layout Selector Pill (✏️)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(20.dp))
              .clickable { viewModel.openSectionSettings(SectionSettingsTarget.LAYOUT) }
              .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = activeLayout.title.substringBefore(" ("),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                color = AccentIndigo,
                fontWeight = FontWeight.Bold
              )
              Text(text = "✏️", fontSize = 10.sp)
            }
          }

          // Global Search Button 🔍
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(20.dp))
              .clickable { showGlobalSearchDialog = true }
              .padding(horizontal = 8.dp, vertical = 6.dp)
              .testTag("global_search_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "جستجوی سراسری",
              tint = AccentBlue,
              modifier = Modifier.size(16.dp)
            )
          }

          // Theme Switcher Button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(20.dp))
              .clickable { viewModel.toggleTheme() }
              .padding(horizontal = 8.dp, vertical = 6.dp)
              .testTag("toggle_theme_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = "تغییر تم",
              tint = if (isDarkTheme) Color(0xFFFBBF24) else AccentIndigo,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }

    // 2. Period Filter Selector
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PeriodSelectorPill(
          selectedFilter = periodFilter,
          onFilterSelected = { filter ->
            viewModel.setPeriodFilter(filter)
            if (filter == PeriodFilter.CUSTOM) {
              showCustomDateRangeDialog = true
            }
          },
          modifier = Modifier.fillMaxWidth()
        )

        if (periodFilter == PeriodFilter.CUSTOM) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(customColors.secondaryBg)
                .border(1.dp, AccentCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .clickable { showCustomDateRangeDialog = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.DateRange,
                  contentDescription = null,
                  tint = AccentCyan,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = if (timeRange.customFromPersian.isNotEmpty()) {
                    "بازه: از ${timeRange.customFromPersian} تا ${timeRange.customToPersian}"
                  } else {
                    "انتخاب تاریخ شروع و پایان بازه"
                  },
                  style = MaterialTheme.typography.labelSmall,
                  color = customColors.textPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
              Text(
                text = "تغییر تاریخ ✎",
                style = MaterialTheme.typography.labelSmall,
                color = AccentCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              listOf(7 to "۷ روز", 15 to "۱۵ روز", 30 to "۳۰ روز", 90 to "فصلی (۹۰ روز)").forEach { (days, label) ->
                val isSelected = customPeriodDays == days
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) AccentCyan.copy(alpha = 0.2f) else customColors.card)
                    .border(1.dp, if (isSelected) AccentCyan else customColors.border, RoundedCornerShape(8.dp))
                    .clickable { viewModel.setCustomPeriodDays(days) }
                    .padding(vertical = 5.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = if (isSelected) AccentCyan else customColors.textSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                }
              }
            }
          }
        }
      }
    }

    // ================= 8 DISTINCT DASHBOARD LAYOUTS =================

    when (activeLayout) {
      DashboardLayoutArrangement.CLASSIC -> {
        // Classic Layout: Header -> Period -> KPIs -> Chart -> Donut -> Alerts -> Orders
        item {
          KpisSectionHeader(onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.KPIS) })
          Spacer(modifier = Modifier.height(8.dp))
          HeroKpisBlock(kpis = kpis, timeRangeTitle = timeRange.title, onDrillDown = { viewModel.openDrillDown(it) })
        }

        item {
          SecondaryKpisGrid(kpis = kpis, onDrillDown = { viewModel.openDrillDown(it) })
        }

        item {
          ChartSectionHeader(onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) })
          Spacer(modifier = Modifier.height(8.dp))
          ExecutiveCombinedChart(
            points = combinedChartPoints,
            timeRange = timeRange,
            chartType = currentChartType,
            displayMetric = chartDisplayMetric,
            showValues = showChartValues,
            onOpenSettings = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) },
            onToggleShowValues = { viewModel.toggleShowChartValues() }
          )
        }

        item {
          ExecutiveDonutChart(
            title = "تحلیل پورتفوی و چارت گرد انبار",
            subtitle = "سهم ریالی محصولات آماده، طاقه‌های پارچه، ملزومات و سفارشات",
            slices = donutSlices,
            centerTitle = "کل دارایی انبار"
          )
        }

        item {
          AlertsSectionBlock(alerts = alerts, onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ALERTS) })
        }

        item {
          OrdersSectionBlock(
            orders = orders,
            onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ORDERS) },
            onSeeAll = {
              viewModel.setTab(MainTab.MORE)
              viewModel.setSubSection(MoreSubSection.ORDERS)
            },
            onOrderClick = { viewModel.startEditOrder(it) }
          )
        }
      }

      DashboardLayoutArrangement.COMPACT -> {
        // Compact Layout: High density 4-KPI Grid -> Mini Chart -> Alerts -> Orders
        item {
          KpisSectionHeader(title = "خلاصه فشرده شاخص‌ها", onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.KPIS) })
          Spacer(modifier = Modifier.height(8.dp))
          CompactKpiGrid(kpis = kpis, onDrillDown = { viewModel.openDrillDown(it) })
        }

        item {
          ChartSectionHeader(title = "روند فشرده زمانی", onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) })
          Spacer(modifier = Modifier.height(8.dp))
          ExecutiveCombinedChart(
            points = combinedChartPoints,
            timeRange = timeRange,
            chartType = currentChartType,
            displayMetric = chartDisplayMetric,
            showValues = showChartValues,
            onOpenSettings = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) },
            onToggleShowValues = { viewModel.toggleShowChartValues() }
          )
        }

        item {
          AlertsSectionBlock(alerts = alerts, onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ALERTS) })
        }

        item {
          OrdersSectionBlock(
            orders = orders,
            onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ORDERS) },
            onSeeAll = {
              viewModel.setTab(MainTab.MORE)
              viewModel.setSubSection(MoreSubSection.ORDERS)
            },
            onOrderClick = { viewModel.startEditOrder(it) }
          )
        }
      }

      DashboardLayoutArrangement.MANAGEMENT -> {
        // Management Layout: Profit First -> Benchmarks -> Combined Chart -> Alerts -> Orders
        item {
          KpisSectionHeader(title = "سود و بنچمارک‌های مدیریتی", onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.KPIS) })
          Spacer(modifier = Modifier.height(8.dp))
          // Large Net Profit Hero Card
          HeroKpiCard(
            title = "سود خالص دوره (${timeRange.title}) - برای کاوش کلیک کنید 🔍",
            valueText = CurrencyHelper.formatToman(kpis.netProfitAmount),
            growthText = "↑ ${kpis.profitGrowthPercent}٪",
            isPositive = true,
            icon = Icons.Default.CheckCircle,
            accentColor = StatusSuccess,
            modifier = Modifier.clickable { viewModel.openDrillDown("NET_PROFIT") }
          )
        }

        item {
          FixedCostBenchmarkCard(
            settings = factorySettings,
            onEditClick = { viewModel.openSectionSettings(SectionSettingsTarget.COSTS) }
          )
        }

        item {
          ChartSectionHeader(title = "تحلیل همزمان سود و فروش", onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) })
          Spacer(modifier = Modifier.height(8.dp))
          ExecutiveCombinedChart(
            points = combinedChartPoints,
            timeRange = timeRange,
            chartType = currentChartType,
            displayMetric = chartDisplayMetric,
            showValues = showChartValues,
            onOpenSettings = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) },
            onToggleShowValues = { viewModel.toggleShowChartValues() }
          )
        }

        item {
          AlertsSectionBlock(alerts = alerts, onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ALERTS) })
        }

        item {
          OrdersSectionBlock(
            orders = orders,
            onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ORDERS) },
            onSeeAll = {
              viewModel.setTab(MainTab.MORE)
              viewModel.setSubSection(MoreSubSection.ORDERS)
            },
            onOrderClick = { viewModel.startEditOrder(it) }
          )
        }
      }

      DashboardLayoutArrangement.LARGE_CARDS -> {
        // Large Cards Layout: 4 Prominent Cards for Sales, Profit, Production, Inventory
        item {
          KpisSectionHeader(title = "کارت‌های کلیدی عریض", onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.KPIS) })
          Spacer(modifier = Modifier.height(8.dp))
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            HeroKpiCard(
              title = "کل فروش دوره (${timeRange.title}) - کاوش 🔍",
              valueText = CurrencyHelper.formatToman(kpis.salesAmount),
              growthText = "↑ ${kpis.salesGrowthPercent}٪",
              isPositive = true,
              icon = Icons.Default.TrendingUp,
              accentColor = AccentIndigo,
              modifier = Modifier.clickable { viewModel.openDrillDown("TOTAL_SALES") }
            )
            HeroKpiCard(
              title = "سود خالص دوره (${timeRange.title}) - کاوش 🔍",
              valueText = CurrencyHelper.formatToman(kpis.netProfitAmount),
              growthText = "↑ ${kpis.profitGrowthPercent}٪",
              isPositive = true,
              icon = Icons.Default.CheckCircle,
              accentColor = StatusSuccess,
              modifier = Modifier.clickable { viewModel.openDrillDown("NET_PROFIT") }
            )
            HeroKpiCard(
              title = "تیراژ کل تولید کارگاه - کاوش 🔍",
              valueText = "${CurrencyHelper.formatNumber(kpis.productionCount)} عدد",
              growthText = "${kpis.readyForShipmentCount} آماده ارسال",
              isPositive = true,
              icon = Icons.Default.PrecisionManufacturing,
              accentColor = AccentCyan,
              modifier = Modifier.clickable { viewModel.openDrillDown("TOTAL_PRODUCTION") }
            )
            HeroKpiCard(
              title = "طاقه‌های موجود پارچه - کاوش 🔍",
              valueText = "${kpis.totalFabricRolls} طاقه",
              growthText = "انبار مرکزی",
              isPositive = true,
              icon = Icons.Default.Inventory,
              accentColor = AccentPurple,
              modifier = Modifier.clickable { viewModel.openDrillDown("INVENTORY_VALUE") }
            )
          }
        }

        item {
          ChartSectionHeader(onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) })
          Spacer(modifier = Modifier.height(8.dp))
          ExecutiveCombinedChart(
            points = combinedChartPoints,
            timeRange = timeRange,
            chartType = currentChartType,
            displayMetric = chartDisplayMetric,
            showValues = showChartValues,
            onOpenSettings = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) },
            onToggleShowValues = { viewModel.toggleShowChartValues() }
          )
        }

        item {
          OrdersSectionBlock(
            orders = orders,
            onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ORDERS) },
            onSeeAll = {
              viewModel.setTab(MainTab.MORE)
              viewModel.setSubSection(MoreSubSection.ORDERS)
            },
            onOrderClick = { viewModel.startEditOrder(it) }
          )
        }
      }

      DashboardLayoutArrangement.TWO_COLUMN -> {
        // Two-Column Layout: Grid-based layout for tablets and spacious phones
        item {
          KpisSectionHeader(title = "شاخص‌ها در ساختار دو ستونه", onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.KPIS) })
          Spacer(modifier = Modifier.height(8.dp))
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(modifier = Modifier.weight(1f)) {
                SecondaryKpiCard(
                  title = "کل فروش",
                  valueText = CurrencyHelper.formatToman(kpis.salesAmount),
                  subtitle = "${kpis.salesCount} سفارش",
                  icon = Icons.Default.TrendingUp,
                  modifier = Modifier.clickable { viewModel.openDrillDown("TOTAL_SALES") }
                )
              }
              Box(modifier = Modifier.weight(1f)) {
                SecondaryKpiCard(
                  title = "سود خالص",
                  valueText = CurrencyHelper.formatToman(kpis.netProfitAmount),
                  subtitle = "حاشیه هدف محقق شده",
                  icon = Icons.Default.CheckCircle,
                  modifier = Modifier.clickable { viewModel.openDrillDown("NET_PROFIT") }
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(modifier = Modifier.weight(1f)) {
                SecondaryKpiCard(
                  title = "هزینه کل",
                  valueText = CurrencyHelper.formatToman(kpis.totalCostAmount),
                  subtitle = "مواد، خیاط و باربری",
                  icon = Icons.Default.AttachMoney,
                  modifier = Modifier.clickable { viewModel.openDrillDown("TOTAL_COST") }
                )
              }
              Box(modifier = Modifier.weight(1f)) {
                SecondaryKpiCard(
                  title = "تیراژ تولید",
                  valueText = "${CurrencyHelper.formatNumber(kpis.productionCount)} عدد",
                  subtitle = "${kpis.readyForShipmentCount} آماده تحویل",
                  icon = Icons.Default.PrecisionManufacturing,
                  modifier = Modifier.clickable { viewModel.openDrillDown("TOTAL_PRODUCTION") }
                )
              }
            }
          }
        }

        item {
          ChartSectionHeader(onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) })
          Spacer(modifier = Modifier.height(8.dp))
          ExecutiveCombinedChart(
            points = combinedChartPoints,
            timeRange = timeRange,
            chartType = currentChartType,
            displayMetric = chartDisplayMetric,
            showValues = showChartValues,
            onOpenSettings = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) },
            onToggleShowValues = { viewModel.toggleShowChartValues() }
          )
        }

        item {
          AlertsSectionBlock(alerts = alerts, onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ALERTS) })
        }

        item {
          OrdersSectionBlock(
            orders = orders,
            onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ORDERS) },
            onSeeAll = {
              viewModel.setTab(MainTab.MORE)
              viewModel.setSubSection(MoreSubSection.ORDERS)
            },
            onOrderClick = { viewModel.startEditOrder(it) }
          )
        }
      }

      DashboardLayoutArrangement.STATISTICS_FOCUSED -> {
        // Statistics Focused: Dense quantitative benchmarks and analytical cards
        item {
          KpisSectionHeader(title = "میز فرمان آماری و شاخص‌های کمی", onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.KPIS) })
          Spacer(modifier = Modifier.height(8.dp))
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              StatCard(modifier = Modifier.weight(1f), label = "کل فروش", value = CurrencyHelper.formatToman(kpis.salesAmount), color = AccentIndigo, onClick = { viewModel.openDrillDown("TOTAL_SALES") })
              StatCard(modifier = Modifier.weight(1f), label = "سود خالص", value = CurrencyHelper.formatToman(kpis.netProfitAmount), color = StatusSuccess, onClick = { viewModel.openDrillDown("NET_PROFIT") })
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              StatCard(modifier = Modifier.weight(1f), label = "کل هزینه‌ها", value = CurrencyHelper.formatToman(kpis.totalCostAmount), color = StatusWarning, onClick = { viewModel.openDrillDown("TOTAL_COST") })
              StatCard(modifier = Modifier.weight(1f), label = "تیراژ کل", value = "${kpis.productionCount} کار", color = AccentCyan, onClick = { viewModel.openDrillDown("TOTAL_PRODUCTION") })
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              StatCard(modifier = Modifier.weight(1f), label = "طاقه‌های پارچه", value = "${kpis.totalFabricRolls} طاقه", color = AccentPurple, onClick = { viewModel.openDrillDown("INVENTORY_VALUE") })
              StatCard(modifier = Modifier.weight(1f), label = "آماده ارسال", value = "${kpis.readyForShipmentCount} عدد", color = AccentBlue, onClick = { viewModel.openDrillDown("TOTAL_PRODUCTION") })
            }
          }
        }

        item {
          ChartSectionHeader(title = "توزیع آماری دوره‌ای", onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) })
          Spacer(modifier = Modifier.height(8.dp))
          ExecutiveCombinedChart(
            points = combinedChartPoints,
            timeRange = timeRange,
            chartType = currentChartType,
            displayMetric = chartDisplayMetric,
            showValues = true,
            onOpenSettings = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) },
            onToggleShowValues = { viewModel.toggleShowChartValues() }
          )
        }

        item {
          OrdersSectionBlock(
            orders = orders,
            onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ORDERS) },
            onSeeAll = {
              viewModel.setTab(MainTab.MORE)
              viewModel.setSubSection(MoreSubSection.ORDERS)
            },
            onOrderClick = { viewModel.startEditOrder(it) }
          )
        }
      }

      DashboardLayoutArrangement.CHARTS_FOCUSED, DashboardLayoutArrangement.CHARTS_FIRST -> {
        // Charts Focused: Expanded visual analytics at the top
        item {
          ChartSectionHeader(title = "نمودار تحلیلی اصلی کارخانه", onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) })
          Spacer(modifier = Modifier.height(8.dp))
          ExecutiveCombinedChart(
            points = combinedChartPoints,
            timeRange = timeRange,
            chartType = currentChartType,
            displayMetric = chartDisplayMetric,
            showValues = showChartValues,
            onOpenSettings = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) },
            onToggleShowValues = { viewModel.toggleShowChartValues() }
          )
        }

        item {
          ExecutiveDonutChart(
            title = "تحلیل پورتفوی و چارت گرد انبار",
            subtitle = "سهم ریالی محصولات آماده، طاقه‌های پارچه، ملزومات و سفارشات",
            slices = donutSlices,
            centerTitle = "کل دارایی انبار"
          )
        }

        item {
          KpisSectionHeader(onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.KPIS) })
          Spacer(modifier = Modifier.height(8.dp))
          HeroKpisBlock(kpis = kpis, timeRangeTitle = timeRange.title, onDrillDown = { viewModel.openDrillDown(it) })
        }

        item {
          AlertsSectionBlock(alerts = alerts, onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ALERTS) })
        }
      }

      DashboardLayoutArrangement.MINIMAL -> {
        // Minimal Layout: Quick, distraction-free view with essentials
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "دیده‌بان مینیمال کارخانه",
              style = MaterialTheme.typography.titleMedium,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold
            )
            SectionEditIcon(onClick = { viewModel.openSectionSettings(SectionSettingsTarget.KPIS) })
          }
          Spacer(modifier = Modifier.height(8.dp))
          HeroKpiCard(
            title = "سود خالص - برای کاوش کلیک کنید 🔍",
            valueText = CurrencyHelper.formatToman(kpis.netProfitAmount),
            growthText = "فروش: ${CurrencyHelper.formatToman(kpis.salesAmount)}",
            isPositive = true,
            icon = Icons.Default.CheckCircle,
            accentColor = StatusSuccess,
            modifier = Modifier.clickable { viewModel.openDrillDown("NET_PROFIT") }
          )
        }

        item {
          OrdersSectionBlock(
            orders = orders,
            onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ORDERS) },
            onSeeAll = {
              viewModel.setTab(MainTab.MORE)
              viewModel.setSubSection(MoreSubSection.ORDERS)
            },
            onOrderClick = { viewModel.startEditOrder(it) }
          )
        }
      }

      else -> {
        // Fallback to Classic
        item {
          KpisSectionHeader(onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.KPIS) })
          Spacer(modifier = Modifier.height(8.dp))
          HeroKpisBlock(kpis = kpis, timeRangeTitle = timeRange.title, onDrillDown = { viewModel.openDrillDown(it) })
        }

        item {
          SecondaryKpisGrid(kpis = kpis, onDrillDown = { viewModel.openDrillDown(it) })
        }

        item {
          ChartSectionHeader(onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) })
          Spacer(modifier = Modifier.height(8.dp))
          ExecutiveCombinedChart(
            points = combinedChartPoints,
            timeRange = timeRange,
            chartType = currentChartType,
            displayMetric = chartDisplayMetric,
            showValues = showChartValues,
            onOpenSettings = { viewModel.openSectionSettings(SectionSettingsTarget.CHART) },
            onToggleShowValues = { viewModel.toggleShowChartValues() }
          )
        }

        item {
          AlertsSectionBlock(alerts = alerts, onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ALERTS) })
        }

        item {
          OrdersSectionBlock(
            orders = orders,
            onEdit = { viewModel.openSectionSettings(SectionSettingsTarget.ORDERS) },
            onSeeAll = {
              viewModel.setTab(MainTab.MORE)
              viewModel.setSubSection(MoreSubSection.ORDERS)
            },
            onOrderClick = { viewModel.startEditOrder(it) }
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Custom Date Range Dialog
  if (showCustomDateRangeDialog) {
    CustomDateRangeDialog(
      initialFromPersian = timeRange.customFromPersian,
      initialToPersian = timeRange.customToPersian,
      onDismiss = { showCustomDateRangeDialog = false },
      onConfirm = { fromP, toP, fromTime, toTime ->
        viewModel.setCustomDateRange(fromP, toP, fromTime, toTime)
        showCustomDateRangeDialog = false
      }
    )
  }

  if (showGlobalSearchDialog) {
    com.example.ui.dialogs.GlobalSearchDialog(
      viewModel = viewModel,
      onDismiss = { showGlobalSearchDialog = false }
    )
  }
}

// ======================= REUSABLE DASHBOARD BLOCKS =======================

@Composable
private fun SectionEditIcon(onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .size(26.dp)
      .clip(CircleShape)
      .background(AccentIndigo.copy(alpha = 0.12f))
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Text(text = "✏️", fontSize = 12.sp)
  }
}

@Composable
private fun KpisSectionHeader(
  title: String = "شاخص‌های کلیدی عملکرد (KPI)",
  onEdit: () -> Unit
) {
  val customColors = LocalCustomColors.current
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      color = customColors.textPrimary,
      fontWeight = FontWeight.Bold
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = "تنظیمات KPI",
        style = MaterialTheme.typography.labelSmall,
        color = customColors.textMuted,
        fontSize = 11.sp
      )
      SectionEditIcon(onClick = onEdit)
    }
  }
}

@Composable
private fun ChartSectionHeader(
  title: String = "نمودار تحلیلی و روند زمانی",
  onEdit: () -> Unit
) {
  val customColors = LocalCustomColors.current
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      color = customColors.textPrimary,
      fontWeight = FontWeight.Bold
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = "تنظیمات چارت",
        style = MaterialTheme.typography.labelSmall,
        color = customColors.textMuted,
        fontSize = 11.sp
      )
      SectionEditIcon(onClick = onEdit)
    }
  }
}

@Composable
private fun HeroKpisBlock(
  kpis: DashboardKpiState,
  timeRangeTitle: String,
  onDrillDown: (String) -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    HeroKpiCard(
      title = "مجموع فروش ($timeRangeTitle) - برای کاوش کلیک کنید 🔍",
      valueText = CurrencyHelper.formatToman(kpis.salesAmount),
      growthText = "↑ ${kpis.salesGrowthPercent}٪",
      isPositive = true,
      icon = Icons.Default.TrendingUp,
      accentColor = AccentIndigo,
      modifier = Modifier.clickable { onDrillDown("TOTAL_SALES") }
    )

    HeroKpiCard(
      title = "سود خالص دوره ($timeRangeTitle) - برای کاوش کلیک کنید 🔍",
      valueText = CurrencyHelper.formatToman(kpis.netProfitAmount),
      growthText = "↑ ${kpis.profitGrowthPercent}٪",
      isPositive = true,
      icon = Icons.Default.CheckCircle,
      accentColor = StatusSuccess,
      modifier = Modifier.clickable { onDrillDown("NET_PROFIT") }
    )
  }
}

@Composable
private fun SecondaryKpisGrid(
  kpis: DashboardKpiState,
  onDrillDown: (String) -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(modifier = Modifier.weight(1f)) {
        SecondaryKpiCard(
          title = "هزینه کل تمام‌شده",
          valueText = CurrencyHelper.formatToman(kpis.totalCostAmount),
          subtitle = "پارچه، دوخت و باربری",
          icon = Icons.Default.AttachMoney,
          modifier = Modifier.clickable { onDrillDown("TOTAL_COST") }
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        SecondaryKpiCard(
          title = "تعداد فاکتور فروش",
          valueText = "${kpis.salesCount} سفارش",
          subtitle = "ثبت نهایی",
          icon = Icons.Default.Receipt,
          modifier = Modifier.clickable { onDrillDown("TOTAL_SALES") }
        )
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(modifier = Modifier.weight(1f)) {
        SecondaryKpiCard(
          title = "تعداد تولید کارگاه",
          valueText = "${CurrencyHelper.formatNumber(kpis.productionCount)} عدد",
          subtitle = "۳ خط فعال دوخت",
          icon = Icons.Default.PrecisionManufacturing,
          modifier = Modifier.clickable { onDrillDown("TOTAL_PRODUCTION") }
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        SecondaryKpiCard(
          title = "طاقه‌های موجود پارچه",
          valueText = "${kpis.totalFabricRolls} طاقه",
          subtitle = "انبار مرکزی",
          icon = Icons.Default.Inventory,
          modifier = Modifier.clickable { onDrillDown("INVENTORY_VALUE") }
        )
      }
    }
  }
}

@Composable
private fun CompactKpiGrid(
  kpis: DashboardKpiState,
  onDrillDown: (String) -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.card)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .clickable { onDrillDown("TOTAL_SALES") }
          .padding(12.dp)
      ) {
        Column {
          Text("فروش دوره", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(kpis.salesAmount), style = MaterialTheme.typography.bodyMedium, color = AccentIndigo, fontWeight = FontWeight.Bold)
        }
      }
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.card)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .clickable { onDrillDown("NET_PROFIT") }
          .padding(12.dp)
      ) {
        Column {
          Text("سود خالص", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(kpis.netProfitAmount), style = MaterialTheme.typography.bodyMedium, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.card)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .clickable { onDrillDown("TOTAL_COST") }
          .padding(12.dp)
      ) {
        Column {
          Text("هزینه‌ها", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(kpis.totalCostAmount), style = MaterialTheme.typography.bodyMedium, color = StatusWarning, fontWeight = FontWeight.Bold)
        }
      }
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.card)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .clickable { onDrillDown("TOTAL_PRODUCTION") }
          .padding(12.dp)
      ) {
        Column {
          Text("تیراژ کل", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${kpis.productionCount} عدد", style = MaterialTheme.typography.bodyMedium, color = AccentCyan, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun StatCard(
  modifier: Modifier = Modifier,
  label: String,
  value: String,
  color: Color,
  onClick: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(12.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
      Text(text = label, style = MaterialTheme.typography.labelSmall, color = customColors.textMuted, fontSize = 11.sp)
      Text(text = value, style = MaterialTheme.typography.bodyLarge, color = color, fontWeight = FontWeight.ExtraBold)
    }
  }
}

@Composable
private fun AlertsSectionBlock(
  alerts: List<AlertItem>,
  onEdit: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = "مرکز هشدارهای مهم انبار و تولید",
          style = MaterialTheme.typography.titleMedium,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )
        SectionEditIcon(onClick = onEdit)
      }
      Text(
        text = "${alerts.size} مورد نیازمند توجه",
        style = MaterialTheme.typography.labelSmall,
        color = customColors.textMuted
      )
    }

    alerts.forEach { alert ->
      AlertCenterCard(alert = alert)
    }
  }
}

@Composable
private fun OrdersSectionBlock(
  orders: List<SaleOrderEntity>,
  onEdit: () -> Unit,
  onSeeAll: () -> Unit,
  onOrderClick: (SaleOrderEntity) -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = "آخرین وضعیت سفارشات",
          style = MaterialTheme.typography.titleMedium,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )
        SectionEditIcon(onClick = onEdit)
      }
      Text(
        text = "مشاهده همه",
        style = MaterialTheme.typography.labelSmall,
        color = AccentBlue,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clickable(onClick = onSeeAll)
      )
    }

    orders.take(3).forEach { order ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.card)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .clickable { onOrderClick(order) }
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = order.orderNumber,
                style = MaterialTheme.typography.titleSmall,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              StatusChip(status = order.deliveryStatus)
            }
            Text(
              text = "${order.customerName} • ${order.quantity} عدد ${order.modelName}",
              style = MaterialTheme.typography.bodySmall,
              color = customColors.textSecondary
            )
          }

          Text(
            text = CurrencyHelper.formatToman(order.netTotal),
            style = MaterialTheme.typography.titleSmall,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
