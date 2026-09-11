package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AccessoryPurchaseEntity
import com.example.data.model.AlertItem
import com.example.data.model.AppTimeRangeState
import com.example.data.model.ChartDisplayMetric
import com.example.data.model.ChartPoint
import com.example.data.model.CombinedChartPoint
import com.example.data.model.CustomerEntity
import com.example.data.model.CuttingEntity
import com.example.data.model.DashboardChartType
import com.example.data.model.FabricEntity
import com.example.data.model.FabricRollEntity
import com.example.data.model.FactorySettingsEntity
import com.example.data.model.FixedCostEntity
import com.example.data.model.InventoryEntity
import com.example.data.model.ModelStandardEntity
import com.example.data.model.MultiProductReadyItem
import com.example.data.model.OrderStatusHistoryEntity
import com.example.data.model.PeriodFilter
import com.example.data.model.ProductionConsumableEntity
import com.example.data.model.ProductionConsumableInputItem
import com.example.data.model.ProductionEntity
import com.example.data.model.RollUsageEntity
import com.example.data.model.SaleOrderEntity
import com.example.data.model.ShippingAllocationMethod
import com.example.data.model.ShippingExpenseEntity
import com.example.data.model.SupplierEntity
import com.example.data.model.TimeRangeMode
import com.example.data.model.*
import com.example.data.repository.ManufacturingRepository
import com.example.data.service.FinancialCalculationService
import com.example.util.AppFontManager
import com.example.util.PersianDateHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val title: String) {
  DASHBOARD("خانه"),
  ANALYTICS("گزارش"),
  INVENTORY("انبار"),
  MORE("بیشتر")
}

enum class ReportCategory(val title: String, val icon: String) {
  OVERVIEW("نمای کلی", "📊"),
  SALES("فروش", "💰"),
  PRODUCTION("تولید", "⚙️"),
  INVENTORY("موجودی", "📦"),
  ORDERS("سفارشات", "📋"),
  COSTS("هزینه‌ها", "🏷️"),
  PROFIT("سود", "📈"),
  CONSUMABLES("ملزومات", "🧵"),
  FABRIC("پارچه و طاقه", "🧶"),
  FREIGHT("باربری", "🚚"),
  TAILOR_COST("خیاط‌کار", "✂️")
}

enum class MoreSubSection(val title: String) {
  ORDERS("سفارشات"),
  PRODUCTION("تولید"),
  MASTER_DATA("اطلاعات پایه و BOM"),
  PURCHASES("خرید و تدارکات"),
  LEDGER("دفتر کل و اسناد"),
  CUTTING("برش"),
  CUSTOMERS("مشتریان"),
  SUPPLIERS("تأمین‌کنندگان"),
  SETTINGS("تنظیمات استاندارد")
}

enum class QuickActionType {
  NONE,
  WAREHOUSE_HUB,   // مرکز عملیات انبارداری و ثبت سریع
  FABRIC_IN,       // افزودن طاقه پارچه (متراژ و کیلوگرم)
  READY_GOODS_IN,  // ثبت تعداد کار آماده به انبار کالا
  ACCESSORY_IN,    // ثبت ملزومات و خرج‌کار
  CUSTOMER,        // افزودن مشتری جدید
  SALE,            // ثبت فاکتور فروش و سفارش
  SALE_RETURN,     // ثبت مرجوعی فروش و بازگشت به انبار
  PURCHASE_IN,     // ثبت خرید مواد با امکان تحویل ناقص و باربری
  PRODUCTION,      // ثبت کارگاه تولید
  ATOMIC_BOM_PROD, // تولید اتمیک بر اساس فرمول BOM
  MARKET_PRICE_UPDATE, // تغییر نرخ بازار بدون خرید
  CUTTING,         // ثبت پارت برشکاری
  MULTI_CUT,       // پاپ‌آپ بازشونده برش چند مدلی از یک طاقه
  SETTINGS_EDIT,   // ویرایش هزینه‌های ثابت و سربار
  EDIT_FABRIC,     // ویرایش مشخصات طاقه پارچه
  EDIT_INVENTORY,  // ویرایش کالا و موجودی انبار
  EDIT_ORDER,      // ویرایش سفارش فروش
  EDIT_CUSTOMER,   // ویرایش مشخصات مشتری
  EDIT_FABRIC_ROLL,// ویرایش مشخصات طاقه پارچه
  EDIT_SHIPPING_EXPENSE, // ویرایش بارنامه و هزینه باربری
  ROLL_IN,         // ثبت طاقه جدید پارچه با اطلاعات کامل و باربری
  ROLL_CONSUME,    // ثبت مصرف از طاقه برای خط تولید
  SHIPPING_IN,
  SHIPPING_MULTI,
  SHIPPING_COMPANY_MANAGER,
  CATEGORY_MANAGER,     // ثبت رکورد مستقل هزینه باربری
  ROLL_HISTORY,    // سوابق مصرف‌های یک طاقه
  FIXED_COST_IN,   // ثبت هزینه ثابت با تعیین Scope
  CUSTOMER_PAYMENT,// دریافت وجه از مشتری
  SUPPLIER_PAYMENT,// پرداخت به تأمین‌کننده
  INVENTORY_AUDIT  // انبارگردانی و تعدیل موجودی
}

enum class ChartMetric(val title: String) {
  DAILY_SALES("فروش روزانه"),
  MONTHLY_SALES("فروش ماهانه"),
  MONTHLY_PROFIT("سود ماهانه"),
  MONTHLY_PRODUCTION("تولید ماهانه")
}

data class DashboardKpiState(
  val salesAmount: Long = 0L,
  val salesGrowthPercent: Double = 0.0,
  val netProfitAmount: Long = 0L,
  val profitGrowthPercent: Double = 0.0,
  val totalCostAmount: Long = 0L,
  val salesCount: Int = 0,
  val productionCount: Int = 0,
  val readyForShipmentCount: Int = 0,
  val totalFabricRolls: Int = 0,
  val cuttingCount: Int = 0,
  val newCustomersCount: Int = 0,
  val repeatCustomersCount: Int = 0,
)

data class UiNotification(
  val message: String,
  val isError: Boolean = false
)

data class PriceUpdateTarget(
  val type: PriceUpdateType,
  val id: Long,
  val title: String,
  val currentPricePerMeter: Long = 0L,
  val currentPricePerKg: Long = 0L,
  val metersPerKg: Double = 0.0,
  val unit: String = "متر"
)

enum class PriceUpdateType { FABRIC_ROLL, MATERIAL }

class ManufacturingViewModel(
  private val repository: ManufacturingRepository
) : ViewModel() {

  private val _selectedTab = MutableStateFlow(MainTab.DASHBOARD)
  val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

  private val _selectedSubSection = MutableStateFlow(MoreSubSection.ORDERS)
  val selectedSubSection: StateFlow<MoreSubSection> = _selectedSubSection.asStateFlow()

  private val _periodFilter = MutableStateFlow(PeriodFilter.TODAY)
  val periodFilter: StateFlow<PeriodFilter> = _periodFilter.asStateFlow()

  private val _timeRangeState = MutableStateFlow(AppTimeRangeState.createToday24Hours())
  val timeRangeState: StateFlow<AppTimeRangeState> = _timeRangeState.asStateFlow()

  private val _chartDisplayMetric = MutableStateFlow(ChartDisplayMetric.COMBINED)
  val chartDisplayMetric: StateFlow<ChartDisplayMetric> = _chartDisplayMetric.asStateFlow()

  private val _showChartValues = MutableStateFlow(false)
  val showChartValues: StateFlow<Boolean> = _showChartValues.asStateFlow()

  private val _customPeriodDays = MutableStateFlow(7)
  val customPeriodDays: StateFlow<Int> = _customPeriodDays.asStateFlow()

  fun setCustomPeriodDays(days: Int) {
    _customPeriodDays.value = days
  }

  fun setChartDisplayMetric(metric: ChartDisplayMetric) {
    _chartDisplayMetric.value = metric
  }

  fun toggleShowChartValues() {
    _showChartValues.value = !_showChartValues.value
  }

  fun setShowChartValues(show: Boolean) {
    _showChartValues.value = show
  }

  fun setTimeRangeMode(mode: TimeRangeMode) {
    when (mode) {
      TimeRangeMode.TODAY_24H -> {
        _timeRangeState.value = AppTimeRangeState.createToday24Hours()
        _periodFilter.value = PeriodFilter.TODAY
      }
      TimeRangeMode.LAST_MONTH -> {
        _timeRangeState.value = AppTimeRangeState.createLastMonth()
        _periodFilter.value = PeriodFilter.MONTH
      }
      TimeRangeMode.LAST_YEAR -> {
        _timeRangeState.value = AppTimeRangeState.createLastYear()
        _periodFilter.value = PeriodFilter.YEAR
      }
      TimeRangeMode.CUSTOM -> {
        val (curY, curM, curD) = PersianDateHelper.getCurrentJalaliDate()
        val mName = PersianDateHelper.persianMonths.getOrElse(curM - 1) { "اسفند" }
        val fromP = "${PersianDateHelper.toPersianDigits(1)} $mName"
        val toP = "${PersianDateHelper.toPersianDigits(curD)} $mName"
        val now = System.currentTimeMillis()
        val start = now - 7 * 24 * 3600 * 1000L
        _timeRangeState.value = AppTimeRangeState.createCustom(fromP, toP, start, now)
        _periodFilter.value = PeriodFilter.CUSTOM
      }
    }
  }

  fun setCustomDateRange(fromPersian: String, toPersian: String, fromTime: Long, toTime: Long) {
    _timeRangeState.value = AppTimeRangeState.createCustom(fromPersian, toPersian, fromTime, toTime)
    _periodFilter.value = PeriodFilter.CUSTOM
  }

  private val _selectedChartMetric = MutableStateFlow(ChartMetric.DAILY_SALES)
  val selectedChartMetric: StateFlow<ChartMetric> = _selectedChartMetric.asStateFlow()

  private val _activeQuickAction = MutableStateFlow(QuickActionType.NONE)
  val activeQuickAction: StateFlow<QuickActionType> = _activeQuickAction.asStateFlow()

  private val _isDarkTheme = MutableStateFlow(true)
  val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

  private val _selectedFont = MutableStateFlow(com.example.ui.theme.PersianFont.YEKAN)
  val selectedFont: StateFlow<com.example.ui.theme.PersianFont> = _selectedFont.asStateFlow()

  init {
    viewModelScope.launch {
      repository.factorySettings.collect { settings ->
        settings?.let {
          _isDarkTheme.value = it.isDarkTheme
          _selectedFont.value = com.example.ui.theme.PersianFont.fromCode(it.selectedFontCode)
        }
      }
    }
  }

  val editingFabric = MutableStateFlow<FabricEntity?>(null)
  val editingInventory = MutableStateFlow<InventoryEntity?>(null)
  val editingOrder = MutableStateFlow<SaleOrderEntity?>(null)
  val editingCustomer = MutableStateFlow<CustomerEntity?>(null)
  val editingFabricRoll = MutableStateFlow<FabricRollEntity?>(null)
  val editingShippingExpense = MutableStateFlow<ShippingExpenseEntity?>(null)

  private val _notification = MutableStateFlow<UiNotification?>(null)
  val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

  val fabrics: StateFlow<List<FabricEntity>> = repository.allFabrics
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val fabricRolls: StateFlow<List<FabricRollEntity>> = repository.allFabricRolls
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val availableFabricRolls: StateFlow<List<FabricRollEntity>> = repository.availableFabricRolls
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val rollUsages: StateFlow<List<RollUsageEntity>> = repository.allRollUsages
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val accessoryPurchases: StateFlow<List<AccessoryPurchaseEntity>> = repository.allAccessoryPurchases
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val shippingExpenses: StateFlow<List<ShippingExpenseEntity>> = repository.allShippingExpenses
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val selectedRollForHistory = MutableStateFlow<FabricRollEntity?>(null)

  // Dynamic Shipping Averages calculation tied to current central timeRangeState!
  val shippingAverages: StateFlow<FinancialCalculationService.ShippingAverages> = combine(
    shippingExpenses, timeRangeState
  ) { expenses, timeRange ->
    FinancialCalculationService.calculateShippingAverages(expenses, timeRange)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialCalculationService.ShippingAverages(0L, 0, 0L, 0L, 0L, 0L, 0, 0.0, 0.0))

  val cuttings: StateFlow<List<CuttingEntity>> = repository.allCuttings
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val productions: StateFlow<List<ProductionEntity>> = repository.allProductions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val inventory: StateFlow<List<InventoryEntity>> = repository.allInventory
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val salesOrders: StateFlow<List<SaleOrderEntity>> = repository.allSalesOrders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val customers: StateFlow<List<CustomerEntity>> = repository.allCustomers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val suppliers: StateFlow<List<SupplierEntity>> = repository.allSuppliers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val standards: StateFlow<List<ModelStandardEntity>> = repository.allStandards
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val factorySettings: StateFlow<com.example.data.model.FactorySettingsEntity> = repository.factorySettings
    .map { it ?: com.example.data.model.FactorySettingsEntity() }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.data.model.FactorySettingsEntity())

  val alerts: StateFlow<List<AlertItem>> = repository.alertsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val orderStatusHistory: StateFlow<List<OrderStatusHistoryEntity>> = repository.allOrderStatusHistory
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val fixedCosts: StateFlow<List<FixedCostEntity>> = repository.allFixedCosts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val productionConsumables: StateFlow<List<ProductionConsumableEntity>> = repository.allProductionConsumables
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Master Data & Enterprise Operations StateFlows
  val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val products: StateFlow<List<ProductEntity>> = repository.allProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val colors: StateFlow<List<ColorEntity>> = repository.allColors
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val sizes: StateFlow<List<SizeEntity>> = repository.allSizes
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val materials: StateFlow<List<MaterialEntity>> = repository.allMaterials
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val materialUnits: StateFlow<List<MaterialUnitEntity>> = repository.allMaterialUnits
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val boms: StateFlow<List<ProductBOMEntity>> = repository.allBOMs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val materialPriceHistory: StateFlow<List<MaterialPriceHistoryEntity>> = repository.allMaterialPriceHistory
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val productPriceHistory: StateFlow<List<ProductPriceHistoryEntity>> = repository.allProductPriceHistory
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val priceChangeReasons: StateFlow<List<PriceChangeReasonEntity>> = repository.allPriceChangeReasons
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val purchaseOrders: StateFlow<List<PurchaseOrderEntity>> = repository.allPurchaseOrders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val salesChannels: StateFlow<List<SalesChannelEntity>> = repository.allSalesChannels
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val customerPayments: StateFlow<List<CustomerPaymentEntity>> = repository.allCustomerPayments
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val supplierPayments: StateFlow<List<SupplierPaymentEntity>> = repository.allSupplierPayments
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val shippingRateHistory: StateFlow<List<ShippingRateHistoryEntity>> = repository.allShippingRateHistory
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val auditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val inventoryLedger: StateFlow<List<InventoryLedgerEntity>> = repository.allInventoryLedger
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val fabricCategories: StateFlow<List<FabricCategoryEntity>> = repository.allFabricCategories
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeFabricCategories: StateFlow<List<FabricCategoryEntity>> = repository.activeFabricCategories
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val shippingCompanies: StateFlow<List<ShippingCompanyEntity>> = repository.allShippingCompanies
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeShippingCompanies: StateFlow<List<ShippingCompanyEntity>> = repository.activeShippingCompanies
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val baseCostConfigs: StateFlow<List<BaseCostConfigEntity>> = repository.allBaseCostConfigs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeBaseCostConfigs: StateFlow<List<BaseCostConfigEntity>> = repository.activeBaseCostConfigs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val inProgressCuttings: StateFlow<List<CuttingEntity>> = repository.inProgressCuttings
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val completedCuttings: StateFlow<List<CuttingEntity>> = repository.completedCuttings
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val cutButNotSewnParts: StateFlow<List<CuttingEntity>> =
    repository.cuttingPartsByStatus(CuttingEntity.STATUS_CUT)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val sewingParts: StateFlow<List<CuttingEntity>> =
    repository.cuttingPartsByStatus(CuttingEntity.STATUS_SEWING)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val readyParts: StateFlow<List<CuttingEntity>> =
    repository.cuttingPartsByStatus(CuttingEntity.STATUS_READY)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeParts: StateFlow<List<CuttingEntity>> =
    repository.activeCuttingParts()
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun getCuttingPartsForRoll(rollId: Long): StateFlow<List<CuttingEntity>> =
    repository.cuttingPartsByRoll(rollId)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun updateCuttingPartStatus(partId: Long, newStatus: String, note: String = "") {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.updateCuttingPartStatus(partId, newStatus, note)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.localizedMessage}", true)
      }
    }
  }

  fun buildAutoPartTitle(partNumber: Int): String = when (partNumber) {
    1 -> "برش و تولید اول"
    2 -> "برش و تولید دوم"
    3 -> "برش و تولید سوم"
    4 -> "برش و تولید چهارم"
    5 -> "برش و تولید پنجم"
    else -> "برش و تولید شماره $partNumber"
  }

  fun transferAllReadyParts() {
    viewModelScope.launch {
      try {
        val ready = readyParts.value
        if (ready.isEmpty()) {
          _notification.value = UiNotification("پارت آماده‌ای نیست", true)
          return@launch
        }
        var okCount = 0
        ready.forEach { part ->
          val (ok, _) = repository.updateCuttingPartStatus(part.id, CuttingEntity.STATUS_READY, "انتقال گروهی")
          if (ok) okCount++
        }
        _notification.value = UiNotification("$okCount پارت منتقل شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.localizedMessage}", true)
      }
    }
  }

  val currentUserRole = MutableStateFlow(UserRole.ADMIN)

  fun setCurrentUserRole(role: UserRole) {
    currentUserRole.value = role
    _notification.value = UiNotification("نقش کاربری به «${role.title}» تغییر یافت", false)
  }

  // Dynamic Donut / Pie chart slices for Circular Portfolio Chart
  val inventoryDonutSlices: StateFlow<List<com.example.data.model.DonutSlice>> = combine(
    inventory, fabrics, salesOrders
  ) { inv, fabs, orders ->
    val readyVal = inv.filter { it.category == "محصولات آماده" }.sumOf { it.totalStockValue }.toDouble()
    val readyCount = inv.filter { it.category == "محصولات آماده" }.sumOf { it.totalStock }

    val fabricVal = fabs.sumOf { it.totalStockValue }.toDouble()
    val fabricMeters = fabs.sumOf { it.totalMeters }.toInt()

    val accVal = inv.filter { it.category == "ملزومات" }.sumOf { it.totalStockValue }.toDouble()
    val accCount = inv.filter { it.category == "ملزومات" }.sumOf { it.totalStock }

    val ordersVal = orders.filter { it.deliveryStatus != "تحویل شده" }.sumOf { it.netTotal }.toDouble()
    val ordersCount = orders.count { it.deliveryStatus != "تحویل شده" }

    listOf(
      com.example.data.model.DonutSlice(
        label = "محصولات آماده",
        value = readyVal.coerceAtLeast(1.0),
        count = readyCount,
        unit = "عدد",
        color = com.example.ui.theme.AccentIndigo,
        formattedValue = "${(readyVal / 1000000).toInt()} م"
      ),
      com.example.data.model.DonutSlice(
        label = "طاقه‌های پارچه",
        value = fabricVal.coerceAtLeast(1.0),
        count = fabricMeters,
        unit = "متر",
        color = com.example.ui.theme.AccentCyan,
        formattedValue = "${(fabricVal / 1000000).toInt()} م"
      ),
      com.example.data.model.DonutSlice(
        label = "ملزومات و خرج‌کار",
        value = accVal.coerceAtLeast(1.0),
        count = accCount,
        unit = "عدد",
        color = com.example.ui.theme.AccentAmber,
        formattedValue = "${(accVal / 1000000).toInt()} م"
      ),
      com.example.data.model.DonutSlice(
        label = "سفارشات در جریان",
        value = ordersVal.coerceAtLeast(1.0),
        count = ordersCount,
        unit = "سفارش",
        color = com.example.ui.theme.AccentPurple,
        formattedValue = "${(ordersVal / 1000000).toInt()} م"
      )
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dynamic KPI calculations derived from centralized FinancialCalculationService and timeRangeState
  val kpiState: StateFlow<DashboardKpiState> = combine(
    salesOrders, productions, inventory, fabrics, cuttings, factorySettings, _timeRangeState
  ) { args ->
    @Suppress("UNCHECKED_CAST")
    val orders = args[0] as List<SaleOrderEntity>
    @Suppress("UNCHECKED_CAST")
    val prods = args[1] as List<ProductionEntity>
    @Suppress("UNCHECKED_CAST")
    val inv = args[2] as List<InventoryEntity>
    @Suppress("UNCHECKED_CAST")
    val fabs = args[3] as List<FabricEntity>
    @Suppress("UNCHECKED_CAST")
    val cuts = args[4] as List<CuttingEntity>
    val settings = args[5] as FactorySettingsEntity
    val timeRange = args[6] as AppTimeRangeState

    FinancialCalculationService.calculateDashboardKpis(
      orders = orders,
      productions = prods,
      inventory = inv,
      fabrics = fabs,
      cuttings = cuts,
      settings = settings,
      timeRange = timeRange
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardKpiState())

  // Centralized Combined Chart Points for Sales + Net Profit
  val combinedChartPoints: StateFlow<List<CombinedChartPoint>> = combine(
    kpiState, _timeRangeState, salesOrders
  ) { kpi, timeRange, orders ->
    FinancialCalculationService.generateCombinedChartPoints(kpi, timeRange, orders)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Mapped single metric points for backward compatibility with legacy views
  val dashboardChartPoints: StateFlow<List<ChartPoint>> = combine(
    combinedChartPoints, _selectedChartMetric
  ) { points, metric ->
    points.map { pt ->
      val v = when (metric) {
        ChartMetric.DAILY_SALES, ChartMetric.MONTHLY_SALES -> pt.sales
        ChartMetric.MONTHLY_PROFIT -> pt.profit
        ChartMetric.MONTHLY_PRODUCTION -> (pt.sales / 500_000L).coerceAtLeast(50L)
      }
      ChartPoint(
        label = pt.label,
        value = v,
        formattedValue = if (metric == ChartMetric.MONTHLY_PROFIT) pt.formattedProfit else pt.formattedSales
      )
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private fun formatChartVal(metric: ChartMetric, value: Long): String {
    return if (metric == ChartMetric.MONTHLY_PRODUCTION) {
      "$value عدد"
    } else {
      FinancialCalculationService.formatCurrency(value)
    }
  }

  // 10 Comprehensive Reports (Single Source of Truth from FinancialCalculationService)
  val salesReport: StateFlow<SalesReportData> = combine(
    salesOrders, _timeRangeState
  ) { orders, timeRange ->
    FinancialCalculationService.generateSalesReport(orders, timeRange)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SalesReportData())

  val productionReport: StateFlow<ProductionReportData> = combine(
    productions, fabricRolls, _timeRangeState
  ) { prods, rolls, timeRange ->
    FinancialCalculationService.generateProductionReport(prods, rolls, timeRange)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductionReportData())

  val inventoryReport: StateFlow<InventoryReportData> = combine(
    inventory, fabrics, fabricRolls, factorySettings
  ) { inv, fabs, rolls, settings ->
    FinancialCalculationService.generateInventoryReport(inv, fabs, rolls, settings)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InventoryReportData())

  val ordersReport: StateFlow<OrdersReportData> = combine(
    salesOrders, _timeRangeState
  ) { orders, timeRange ->
    FinancialCalculationService.generateOrdersReport(orders, timeRange)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OrdersReportData())

  val costReport: StateFlow<CostReportData> = combine(
    productions, shippingExpenses, fixedCosts, factorySettings, _timeRangeState
  ) { prods, shipping, fixed, settings, timeRange ->
    FinancialCalculationService.generateCostReport(prods, shipping, fixed, settings, timeRange)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CostReportData())

  val profitReport: StateFlow<ProfitReportData> = combine(
    salesReport, costReport
  ) { sales, costs ->
    FinancialCalculationService.generateProfitReport(sales, costs)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfitReportData())

  val consumablesReport: StateFlow<ConsumablesReportData> = combine(
    productionConsumables, accessoryPurchases, inventory
  ) { cons, purchases, inv ->
    FinancialCalculationService.generateConsumablesReport(cons, purchases, inv)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConsumablesReportData())

  val fabricReport: StateFlow<FabricReportData> = combine(
    fabrics, fabricRolls
  ) { fabs, rolls ->
    FinancialCalculationService.generateFabricReport(fabs, rolls)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FabricReportData())

  val freightReport: StateFlow<FreightReportData> = combine(
    shippingExpenses, fabricRolls
  ) { shipping, rolls ->
    FinancialCalculationService.generateFreightReport(shipping, rolls)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FreightReportData())

  val tailorCostReport: StateFlow<TailorCostReportData> = productions.map { prods ->
    FinancialCalculationService.generateTailorCostReport(prods)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TailorCostReportData())

  // Drill Down State & Actions
  private val _selectedDrillDown = MutableStateFlow<DrillDownData?>(null)
  val selectedDrillDown: StateFlow<DrillDownData?> = _selectedDrillDown.asStateFlow()

  fun openDrillDown(metricKey: String) {
    val drill = FinancialCalculationService.generateDrillDown(
      metricKey = metricKey,
      sales = salesReport.value,
      costs = costReport.value,
      profit = profitReport.value,
      production = productionReport.value,
      inventory = inventoryReport.value,
      freight = freightReport.value,
      tailor = tailorCostReport.value,
      orders = salesOrders.value,
      productions = productions.value,
      shippingList = shippingExpenses.value
    )
    _selectedDrillDown.value = drill
  }

  fun dismissDrillDown() {
    _selectedDrillDown.value = null
  }

  // Active Report Category Navigation
  private val _activeReportCategory = MutableStateFlow(ReportCategory.OVERVIEW)
  val activeReportCategory: StateFlow<ReportCategory> = _activeReportCategory.asStateFlow()

  fun setReportCategory(cat: ReportCategory) {
    _activeReportCategory.value = cat
  }

  // Section Settings Target Dialog
  private val _sectionSettingsDialog = MutableStateFlow<SectionSettingsTarget?>(null)
  val sectionSettingsDialog: StateFlow<SectionSettingsTarget?> = _sectionSettingsDialog.asStateFlow()

  fun openSectionSettings(target: SectionSettingsTarget) {
    _sectionSettingsDialog.value = target
  }

  fun closeSectionSettings() {
    _sectionSettingsDialog.value = null
  }

  // Custom Font Family (File Picker support)
  private val _customFontFamily = MutableStateFlow<androidx.compose.ui.text.font.FontFamily?>(null)
  val customFontFamily: StateFlow<androidx.compose.ui.text.font.FontFamily?> = _customFontFamily.asStateFlow()

  fun initCustomFont(context: android.content.Context) {
    if (AppFontManager.hasCustomFont(context)) {
      _customFontFamily.value = AppFontManager.getCustomFontFamily(context)
    }
  }

  fun selectCustomFontFile(context: android.content.Context, uri: android.net.Uri) {
    viewModelScope.launch {
      val result = AppFontManager.validateAndSaveCustomFont(context, uri)
      if (result.isSuccess) {
        _customFontFamily.value = AppFontManager.getCustomFontFamily(context)
        setPersianFont(com.example.ui.theme.PersianFont.CUSTOM)
        _notification.value = UiNotification("قلم «${result.getOrNull() ?: "سفارشی"}» با موفقیت بارگذاری و فعال شد")
      } else {
        _notification.value = UiNotification("خطا در بارگذاری قلم: ${result.exceptionOrNull()?.message}", true)
      }
    }
  }

  fun setTab(tab: MainTab) {
    _selectedTab.value = tab
  }

  fun setSubSection(section: MoreSubSection) {
    _selectedSubSection.value = section
  }

  fun setPeriodFilter(filter: PeriodFilter) {
    _periodFilter.value = filter
    when (filter) {
      PeriodFilter.TODAY -> _timeRangeState.value = AppTimeRangeState.createToday24Hours()
      PeriodFilter.MONTH -> _timeRangeState.value = AppTimeRangeState.createLastMonth()
      PeriodFilter.YEAR -> _timeRangeState.value = AppTimeRangeState.createLastYear()
      PeriodFilter.CUSTOM -> {
        if (_timeRangeState.value.mode != TimeRangeMode.CUSTOM) {
          setTimeRangeMode(TimeRangeMode.CUSTOM)
        }
      }
    }
  }

  fun setChartMetric(metric: ChartMetric) {
    _selectedChartMetric.value = metric
  }

  fun openQuickAction(action: QuickActionType) {
    _activeQuickAction.value = action
  }

  fun closeQuickAction() {
    _activeQuickAction.value = QuickActionType.NONE
  }

  fun dismissNotification() {
    _notification.value = null
  }

  fun submitSale(
    customerName: String,
    customerPhone: String,
    modelCode: String,
    modelName: String,
    quantity: Int,
    unitPrice: Long,
    discountAmount: Long,
    paidAmount: Long,
    unitCost: Long
  ) {
    viewModelScope.launch {
      try {
        repository.insertSaleOrder(
          customerName = customerName,
          customerPhone = customerPhone,
          modelCode = modelCode,
          modelName = modelName,
          quantity = quantity,
          unitPrice = unitPrice,
          discountAmount = discountAmount,
          paidAmount = paidAmount,
          unitCost = unitCost
        )
        closeQuickAction()
        _notification.value = UiNotification("سفارش فروش با موفقیت ثبت و از موجودی انبار کسر شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت فروش: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitProduction(
    modelCode: String,
    modelName: String,
    quantity: Int,
    fabricRollsUsed: Int,
    fabricMetersUsed: Double,
    totalWeightKg: Double,
    sewingWagePerItem: Long,
    fabricPricePerMeter: Long,
    accessoriesCostPerItem: Long
  ) {
    viewModelScope.launch {
      try {
        repository.insertProductionRecord(
          modelCode = modelCode,
          modelName = modelName,
          quantity = quantity,
          fabricRollsUsed = fabricRollsUsed,
          fabricMetersUsed = fabricMetersUsed,
          totalWeightKg = totalWeightKg,
          sewingWagePerItem = sewingWagePerItem,
          fabricPricePerMeter = fabricPricePerMeter,
          accessoriesCostPerItem = accessoriesCostPerItem
        )
        closeQuickAction()
        _notification.value = UiNotification("تولید ثبت شد و محصول به صورت خودکار وارد انبار گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت تولید: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitCutting(
    modelCode: String,
    modelName: String,
    fabricCode: String,
    targetQuantity: Int,
    cutQuantity: Int,
    standardMetersPerItem: Double,
    actualMetersPerItem: Double
  ) {
    viewModelScope.launch {
      try {
        repository.insertCuttingOrder(
          modelCode = modelCode,
          modelName = modelName,
          fabricCode = fabricCode,
          targetQuantity = targetQuantity,
          cutQuantity = cutQuantity,
          standardMetersPerItem = standardMetersPerItem,
          actualMetersPerItem = actualMetersPerItem
        )
        closeQuickAction()
        _notification.value = UiNotification("عملیات برش با موفقیت در سیستم ثبت گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت برش: ${e.localizedMessage}", true)
      }
    }
  }

  fun toggleTheme() {
    val current = _isDarkTheme.value
    _isDarkTheme.value = !current
    viewModelScope.launch {
      repository.saveFactorySettings(factorySettings.value.copy(isDarkTheme = !current))
    }
  }

  fun setTheme(isDark: Boolean) {
    _isDarkTheme.value = isDark
    viewModelScope.launch {
      repository.saveFactorySettings(factorySettings.value.copy(isDarkTheme = isDark))
    }
  }

  fun setPersianFont(font: com.example.ui.theme.PersianFont) {
    _selectedFont.value = font
    viewModelScope.launch {
      val updated = factorySettings.value.copy(selectedFontCode = font.code)
      repository.saveFactorySettings(updated)
      _notification.value = UiNotification("قلم برنامه به «${font.displayName}» تغییر یافت")
    }
  }

  fun toggleSystemNotifications(enabled: Boolean) {
    viewModelScope.launch {
      val updated = factorySettings.value.copy(systemNotificationsEnabled = enabled)
      repository.saveFactorySettings(updated)
      _notification.value = UiNotification(if (enabled) "اعلان‌های بالای گوشی فعال شدند" else "اعلان‌های بالای گوشی غیرفعال شدند")
    }
  }

  fun sendTestNotification(context: android.content.Context) {
    com.example.util.AppNotificationManager.showNotification(
      context = context,
      title = "آزمایش اعلان سیستم کارخانه",
      message = "سیستم اعلان‌ها و هشدارهای فوری با موفقیت در نوار بالای گوشی فعال شد."
    )
    _notification.value = UiNotification("یک اعلان آزمایشی به بالای صفحه گوشی ارسال شد")
  }

  fun submitMultiModelCutting(
    fabricId: Long,
    modelCuts: List<com.example.data.model.MultiCutModelItem>,
    keepRemainingInStock: Boolean,
    context: android.content.Context? = null
  ) {
    viewModelScope.launch {
      try {
        val (success, message) = repository.executeMultiModelCutting(fabricId, modelCuts, keepRemainingInStock)
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(message)
          context?.let { ctx ->
            if (factorySettings.value.systemNotificationsEnabled) {
              com.example.util.AppNotificationManager.showNotification(
                context = ctx,
                title = "برش چند مدلی طاقه پارچه",
                message = message
              )
            }
          }
        } else {
          _notification.value = UiNotification(message, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت برش چند مدلی: ${e.localizedMessage}", true)
      }
    }
  }

  fun saveFactorySettings(
    fixedShippingPerOrder: Long,
    fixedShippingPerRoll: Long,
    targetMargin: Double,
    overheadCost: Long,
    defaultAccCost: Long,
    companyName: String,
    dashboardChartType: String = factorySettings.value.dashboardChartType,
    dashboardLayout: String = factorySettings.value.dashboardLayout,
    minFabricRolls: Int = factorySettings.value.minFabricRollsThreshold,
    minFabricWeightKg: Double = factorySettings.value.minFabricWeightKgThreshold,
    minReadyGoodsCount: Int = factorySettings.value.minReadyGoodsCountThreshold,
    minAccessoriesWeightKg: Double = factorySettings.value.minAccessoriesWeightKgThreshold
  ) {
    viewModelScope.launch {
      try {
        val updated = factorySettings.value.copy(
          fixedShippingCostPerOrder = fixedShippingPerOrder,
          fixedShippingCostPerRoll = fixedShippingPerRoll,
          targetProfitMarginPercent = targetMargin,
          overheadCostPerItem = overheadCost,
          defaultAccessoriesCost = defaultAccCost,
          companyName = companyName,
          dashboardChartType = dashboardChartType,
          dashboardLayout = dashboardLayout,
          minFabricRollsThreshold = minFabricRolls,
          minFabricWeightKgThreshold = minFabricWeightKg,
          minReadyGoodsCountThreshold = minReadyGoodsCount,
          minAccessoriesWeightKgThreshold = minAccessoriesWeightKg
        )
        repository.saveFactorySettings(updated)
        closeQuickAction()
        _notification.value = UiNotification("تنظیمات کارگاه، نمودارها و هشدارهای موجودی به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ذخیره تنظیمات: ${e.localizedMessage}", true)
      }
    }
  }

  fun updateDashboardChartType(chartType: com.example.data.model.DashboardChartType) {
    viewModelScope.launch {
      try {
        val updated = factorySettings.value.copy(dashboardChartType = chartType.name)
        repository.saveFactorySettings(updated)
        _notification.value = UiNotification("نوع چارت آمارگیر صفحه اول به «${chartType.title}» تغییر یافت")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در تغییر چارت: ${e.localizedMessage}", true)
      }
    }
  }

  fun updateDashboardLayout(layout: com.example.data.model.DashboardLayoutArrangement) {
    viewModelScope.launch {
      try {
        val updated = factorySettings.value.copy(dashboardLayout = layout.name)
        repository.saveFactorySettings(updated)
        _notification.value = UiNotification("چیدمان صفحه اول به «${layout.title}» تغییر یافت")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در تغییر چیدمان: ${e.localizedMessage}", true)
      }
    }
  }

  fun clearTemporaryCache(context: android.content.Context) {
    viewModelScope.launch {
      try {
        repository.clearTemporaryCache(context)
        _notification.value = UiNotification("حافظه کش و فایل‌های موقت با موفقیت پاکسازی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در پاکسازی کش: ${e.localizedMessage}", true)
      }
    }
  }

  fun resetToDemoData() {
    viewModelScope.launch {
      try {
        repository.resetDatabaseToDemo()
        _notification.value = UiNotification("کلیه داده‌ها بازنشانی و اطلاعات نمونه و دمو مجدداً بارگذاری شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در بازنشانی داده‌ها: ${e.localizedMessage}", true)
      }
    }
  }

  fun exportAllDataJson(context: android.content.Context) {
    viewModelScope.launch {
      try {
        val allFabs = fabrics.value
        val allCuts = cuttings.value
        val allProds = productions.value
        val allInv = inventory.value
        val allOrders = salesOrders.value
        val allCusts = customers.value
        val allSupps = suppliers.value
        val settings = factorySettings.value

        val exportBuilder = StringBuilder()
        exportBuilder.append("{\n")
        exportBuilder.append("  \"app\": \"Executive Garment Factory Management\",\n")
        exportBuilder.append("  \"export_timestamp\": \"${System.currentTimeMillis()}\",\n")
        exportBuilder.append("  \"company_name\": \"${settings.companyName}\",\n")
        exportBuilder.append("  \"counts\": {\n")
        exportBuilder.append("    \"fabrics\": ${allFabs.size},\n")
        exportBuilder.append("    \"cuttings\": ${allCuts.size},\n")
        exportBuilder.append("    \"productions\": ${allProds.size},\n")
        exportBuilder.append("    \"inventory\": ${allInv.size},\n")
        exportBuilder.append("    \"orders\": ${allOrders.size},\n")
        exportBuilder.append("    \"customers\": ${allCusts.size},\n")
        exportBuilder.append("    \"suppliers\": ${allSupps.size}\n")
        exportBuilder.append("  },\n")
        exportBuilder.append("  \"settings\": {\n")
        exportBuilder.append("    \"fixed_shipping_order\": ${settings.fixedShippingCostPerOrder},\n")
        exportBuilder.append("    \"fixed_shipping_roll\": ${settings.fixedShippingCostPerRoll},\n")
        exportBuilder.append("    \"target_margin_percent\": ${settings.targetProfitMarginPercent},\n")
        exportBuilder.append("    \"overhead_cost_item\": ${settings.overheadCostPerItem},\n")
        exportBuilder.append("    \"chart_type\": \"${settings.dashboardChartType}\",\n")
        exportBuilder.append("    \"layout_arrangement\": \"${settings.dashboardLayout}\",\n")
        exportBuilder.append("    \"threshold_fabric_rolls\": ${settings.minFabricRollsThreshold},\n")
        exportBuilder.append("    \"threshold_fabric_weight_kg\": ${settings.minFabricWeightKgThreshold},\n")
        exportBuilder.append("    \"threshold_ready_goods\": ${settings.minReadyGoodsCountThreshold},\n")
        exportBuilder.append("    \"threshold_accessories_weight_kg\": ${settings.minAccessoriesWeightKgThreshold}\n")
        exportBuilder.append("  },\n")

        // Fabrics summary
        exportBuilder.append("  \"fabrics\": [\n")
        allFabs.forEachIndexed { idx, f ->
          val comma = if (idx < allFabs.size - 1) "," else ""
          exportBuilder.append("    {\"id\": ${f.id}, \"name\": \"${f.name}\", \"code\": \"${f.code}\", \"rolls\": ${f.rollCount}, \"meters\": ${f.totalMeters}, \"weightKg\": ${f.totalWeightKg}, \"buyPriceMeter\": ${f.buyPricePerMeter}}$comma\n")
        }
        exportBuilder.append("  ],\n")

        // Inventory summary
        exportBuilder.append("  \"inventory\": [\n")
        allInv.forEachIndexed { idx, i ->
          val comma = if (idx < allInv.size - 1) "," else ""
          exportBuilder.append("    {\"id\": ${i.id}, \"name\": \"${i.name}\", \"code\": \"${i.code}\", \"category\": \"${i.category}\", \"ready\": ${i.readyForShipment}, \"available\": ${i.availableForSale}, \"price\": ${i.unitSalePrice}, \"weightGrams\": ${i.unitWeightGrams}}$comma\n")
        }
        exportBuilder.append("  ],\n")

        // Orders summary
        exportBuilder.append("  \"sales_orders\": [\n")
        allOrders.forEachIndexed { idx, o ->
          val comma = if (idx < allOrders.size - 1) "," else ""
          exportBuilder.append("    {\"orderNumber\": \"${o.orderNumber}\", \"customer\": \"${o.customerName}\", \"model\": \"${o.modelName}\", \"quantity\": ${o.quantity}, \"total\": ${o.netTotal}, \"status\": \"${o.deliveryStatus}\"}$comma\n")
        }
        exportBuilder.append("  ]\n")
        exportBuilder.append("}")

        val jsonString = exportBuilder.toString()

        // 1. Save to external files dir
        val exportFile = java.io.File(context.getExternalFilesDir(null), "factory_data_backup_${System.currentTimeMillis()}.json")
        exportFile.writeText(jsonString)

        // 2. Open Android system share sheet so the user can send to telegram/whatsapp, save to Drive, copy, etc.
        val sendIntent = android.content.Intent().apply {
          action = android.content.Intent.ACTION_SEND
          putExtra(android.content.Intent.EXTRA_TEXT, jsonString)
          putExtra(android.content.Intent.EXTRA_SUBJECT, "پشتیبان داده‌های کارگاه تولیدی")
          type = "text/plain"
          addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(android.content.Intent.createChooser(sendIntent, "ذخیره و ارسال داده‌های کارگاه").apply {
          addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        })

        _notification.value = UiNotification("فایل پشتیبان داده‌ها آماده و منوی اشتراک‌گذاری باز شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در پشتیبان‌گیری: ${e.localizedMessage}", true)
      }
    }
  }

  fun duplicateProduct(productId: Long) {
    viewModelScope.launch {
      val result = repository.duplicateProduct(productId)
      _notification.value = UiNotification(result.second, !result.first)
    }
  }

  fun restoreBackupData(jsonContent: String) {
    viewModelScope.launch {
      val result = repository.restoreDataFromJson(jsonContent)
      _notification.value = UiNotification(result.second, !result.first)
    }
  }

  fun submitFabric(
    name: String,
    code: String,
    color: String,
    batchNumber: String,
    supplierName: String,
    rollCount: Int,
    totalMeters: Double,
    totalWeightKg: Double = 0.0,
    buyPricePerMeter: Long,
    buyPricePerKg: Long = 0L,
    syncWarehousePrices: Boolean = true
  ) {
    viewModelScope.launch {
      try {
        repository.insertFabric(
          name = name,
          code = code,
          color = color,
          batchNumber = batchNumber,
          supplierName = supplierName,
          rollCount = rollCount,
          totalMeters = totalMeters,
          totalWeightKg = totalWeightKg,
          buyPricePerMeter = buyPricePerMeter,
          buyPricePerKg = buyPricePerKg,
          syncWarehousePrices = syncWarehousePrices
        )
        closeQuickAction()
        _notification.value = UiNotification("پارت جدید پارچه ($totalMeters متر / $totalWeightKg کیلو) با موفقیت در انبار ثبت شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ورود پارچه: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitReadyGoods(
    name: String,
    code: String,
    readyCount: Int,
    availableCount: Int,
    salePrice: Long,
    costPrice: Long,
    unitWeightGrams: Double
  ) {
    viewModelScope.launch {
      try {
        repository.insertInventoryItem(
          name = name,
          code = code,
          category = "محصولات آماده",
          readyCount = readyCount,
          availableCount = availableCount,
          unitSalePrice = salePrice,
          unitCostPrice = costPrice,
          unitWeightGrams = unitWeightGrams,
          unitType = "عدد",
          syncPrices = true
        )
        closeQuickAction()
        _notification.value = UiNotification("محصول آماده دوخت با موفقیت به موجودی انبار اضافه شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت محصول: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitAccessory(
    name: String,
    code: String,
    quantity: Int,
    unitSalePrice: Long,
    unitCostPrice: Long,
    unitType: String,
    supplierName: String = "",
    metersPerKg: Double = 0.0,
    pricePerMeter: Long = 0L
  ) {
    viewModelScope.launch {
      try {
        repository.insertInventoryItem(
          name = name,
          code = code,
          category = "ملزومات",
          readyCount = 0,
          availableCount = quantity,
          unitSalePrice = unitSalePrice,
          unitCostPrice = unitCostPrice,
          unitType = unitType,
          supplierName = supplierName,
          metersPerKg = metersPerKg,
          pricePerMeter = pricePerMeter,
          syncPrices = true
        )
        closeQuickAction()
        _notification.value = UiNotification("قلم ملزومات و خرج‌کار ($name) با موفقیت در انبار ثبت شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت ملزومات: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitSupplier(
    name: String,
    storeName: String,
    mobile: String,
    phone: String,
    address: String,
    distributionCategory: String,
    description: String
  ) {
    viewModelScope.launch {
      try {
        repository.insertSupplier(
          name = name,
          storeName = storeName,
          mobile = mobile,
          phone = phone,
          address = address,
          distributionCategory = distributionCategory,
          description = description
        )
        _notification.value = UiNotification("تأمین‌کننده «$name - $storeName» با موفقیت در سیستم ثبت گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت تأمین‌کننده: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteSupplier(supplier: SupplierEntity) {
    viewModelScope.launch {
      try {
        repository.deleteSupplier(supplier)
        _notification.value = UiNotification("تأمین‌کننده «${supplier.name}» با موفقیت حذف گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف تأمین‌کننده: ${e.localizedMessage}", true)
      }
    }
  }

  fun addCustomUnitType(newUnit: String) {
    viewModelScope.launch {
      try {
        val currentUnits = factorySettings.value.customUnitTypes
        if (!currentUnits.split(",").map { it.trim() }.contains(newUnit.trim())) {
          val updated = "$currentUnits,${newUnit.trim()}"
          repository.saveFactorySettings(factorySettings.value.copy(customUnitTypes = updated))
          _notification.value = UiNotification("واحد سنجش جدید «$newUnit» به گزینه‌ها اضافه شد")
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در افزودن واحد: ${e.localizedMessage}", true)
      }
    }
  }

  fun startEditFabric(fabric: FabricEntity) {
    editingFabric.value = fabric
    openQuickAction(QuickActionType.EDIT_FABRIC)
  }

  fun updateFabric(fabric: FabricEntity) {
    viewModelScope.launch {
      try {
        repository.updateFabric(fabric)
        closeQuickAction()
        editingFabric.value = null
        _notification.value = UiNotification("اطلاعات طاقه پارچه به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش طاقه: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteFabric(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteFabric(id)
        closeQuickAction()
        editingFabric.value = null
        _notification.value = UiNotification("طاقه پارچه با موفقیت از انبار حذف گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف طاقه: ${e.localizedMessage}", true)
      }
    }
  }

  fun startEditInventory(item: InventoryEntity) {
    editingInventory.value = item
    openQuickAction(QuickActionType.EDIT_INVENTORY)
  }

  fun updateInventoryItem(item: InventoryEntity) {
    viewModelScope.launch {
      try {
        repository.updateInventoryItem(item)
        closeQuickAction()
        editingInventory.value = null
        _notification.value = UiNotification("اطلاعات قلم انبار با موفقیت به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش انبار: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteInventoryItem(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteInventoryItem(id)
        closeQuickAction()
        editingInventory.value = null
        _notification.value = UiNotification("قلم کالا از انبار حذف گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف کالا: ${e.localizedMessage}", true)
      }
    }
  }

  fun startEditOrder(order: SaleOrderEntity) {
    editingOrder.value = order
    openQuickAction(QuickActionType.EDIT_ORDER)
  }

  fun updateSaleOrder(order: SaleOrderEntity) {
    viewModelScope.launch {
      try {
        repository.updateSaleOrder(order)
        closeQuickAction()
        editingOrder.value = null
        _notification.value = UiNotification("فاکتور سفارش فروش با موفقیت اصلاح گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش سفارش: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteSaleOrder(order: SaleOrderEntity) {
    viewModelScope.launch {
      try {
        repository.deleteSaleOrder(order)
        closeQuickAction()
        editingOrder.value = null
        _notification.value = UiNotification("سفارش فروش حذف گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف سفارش: ${e.localizedMessage}", true)
      }
    }
  }

  fun startEditCustomer(customer: CustomerEntity) {
    editingCustomer.value = customer
    openQuickAction(QuickActionType.EDIT_CUSTOMER)
  }

  fun updateCustomer(customer: CustomerEntity) {
    viewModelScope.launch {
      try {
        repository.updateCustomer(customer)
        closeQuickAction()
        editingCustomer.value = null
        _notification.value = UiNotification("مشخصات مشتری به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش مشتری: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteCustomer(customer: CustomerEntity) {
    viewModelScope.launch {
      try {
        repository.deleteCustomer(customer)
        closeQuickAction()
        editingCustomer.value = null
        _notification.value = UiNotification("پرونده مشتری حذف گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف مشتری: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitCustomer(
    name: String,
    company: String,
    phone: String,
    address: String,
    category: String
  ) {
    viewModelScope.launch {
      try {
        repository.insertCustomer(
          name = name,
          company = company,
          phone = phone,
          address = address,
          category = category
        )
        closeQuickAction()
        _notification.value = UiNotification("پروفایل مشتری جدید با موفقیت فعال شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت مشتری: ${e.localizedMessage}", true)
      }
    }
  }

  // ==========================================
  // PHASE 2: FABRIC ROLLS & SHIPPING ACTIONS
  // ==========================================

  fun showRollHistory(roll: FabricRollEntity) {
    selectedRollForHistory.value = roll
    openQuickAction(QuickActionType.ROLL_HISTORY)
  }

  fun submitFabricRoll(
    rollCode: String,
    inboundDate: String = com.example.util.PersianDateHelper.getTodayPersianDate(),
    fabricType: String,
    fabricCode: String,
    color: String,
    initialMeters: Double,
    weightKg: Double = 0.0,
    buyPricePerMeter: Long = 0L,
    buyPricePerKg: Long = 0L,
    allocatedShippingCost: Long = 0L,
    supplierName: String = "",
    batchNumber: String = ""
  ) {
    viewModelScope.launch {
      try {
        repository.insertFabricRoll(
          rollCode = rollCode,
          inboundDate = inboundDate,
          fabricType = fabricType,
          fabricCode = fabricCode,
          color = color,
          initialMeters = initialMeters,
          weightKg = weightKg,
          buyPricePerMeter = buyPricePerMeter,
          buyPricePerKg = buyPricePerKg,
          allocatedShippingCost = allocatedShippingCost,
          supplierName = supplierName,
          batchNumber = batchNumber
        )
        closeQuickAction()
        _notification.value = UiNotification("طاقه $rollCode با موفقیت به انبار افزوده شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت طاقه: ${e.localizedMessage}", true)
      }
    }
  }

  fun consumeFabricRoll(
    rollId: Long,
    modelCode: String,
    modelName: String,
    metersUsed: Double,
    productionId: Long = 0L,
    cuttingId: Long = 0L,
    note: String = "",
    garmentCount: Int = 0,
    metersPerGarment: Double = 0.0,
    createProductionOrder: Boolean = true
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.consumeFabricRoll(
          rollId = rollId,
          modelCode = modelCode,
          modelName = modelName,
          metersUsed = metersUsed,
          productionId = productionId,
          cuttingId = cuttingId,
          note = note,
          garmentCount = garmentCount,
          metersPerGarment = metersPerGarment,
          createProductionOrder = createProductionOrder
        )
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت مصرف طاقه: ${e.localizedMessage}", true)
      }
    }
  }

  fun completeProductionToReadyGoods(
    production: ProductionEntity,
    salePrice: Long = 0L
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.completeProductionToReadyGoods(production, salePrice)
        if (success) {
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در تحویل کار آماده: ${e.localizedMessage}", true)
      }
    }
  }

  fun startEditFabricRoll(roll: FabricRollEntity) {
    editingFabricRoll.value = roll
    openQuickAction(QuickActionType.EDIT_FABRIC_ROLL)
  }

  fun updateFabricRoll(roll: FabricRollEntity) {
    viewModelScope.launch {
      try {
        repository.updateFabricRoll(roll)
        closeQuickAction()
        editingFabricRoll.value = null
        _notification.value = UiNotification("اطلاعات طاقه ${roll.rollCode} به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش طاقه: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteFabricRoll(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteFabricRoll(id)
        closeQuickAction()
        editingFabricRoll.value = null
        _notification.value = UiNotification("طاقه از انبار حذف گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف طاقه: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitAccessoryPurchase(
    accessoryCode: String,
    accessoryName: String,
    quantity: Double,
    unit: String,
    unitCostPrice: Long,
    supplierName: String = "",
    allocatedShippingCost: Long = 0L,
    metersPerKg: Double = 0.0,
    pricePerMeter: Long = 0L,
    shippingExpenseId: Long? = null,
    note: String = ""
  ) {
    viewModelScope.launch {
      try {
        repository.insertAccessoryPurchase(
          accessoryCode = accessoryCode,
          accessoryName = accessoryName,
          quantity = quantity,
          unit = unit,
          unitCostPrice = unitCostPrice,
          supplierName = supplierName,
          allocatedShippingCost = allocatedShippingCost,
          metersPerKg = metersPerKg,
          pricePerMeter = pricePerMeter,
          shippingExpenseId = shippingExpenseId,
          note = note
        )
        closeQuickAction()
        _notification.value = UiNotification("خرید ملزوم $accessoryName با واحد $unit ثبت گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت خرید ملزومات: ${e.localizedMessage}", true)
      }
    }
  }

  fun submitShippingExpense(
    trackingNumber: String,
    title: String,
    totalAmount: Long,
    inboundType: String,
    itemCount: Int,
    totalWeightKg: Double,
    totalQuantity: Double,
    unit: String,
    allocationMethod: ShippingAllocationMethod,
    carrierName: String = "",
    notes: String = "",
    allocateToRollIds: List<Long> = emptyList()
  ) {
    viewModelScope.launch {
      try {
        repository.insertShippingExpense(
          trackingNumber = trackingNumber,
          title = title,
          totalAmount = totalAmount,
          inboundType = inboundType,
          itemCount = itemCount,
          totalWeightKg = totalWeightKg,
          totalQuantity = totalQuantity,
          unit = unit,
          allocationMethod = allocationMethod,
          carrierName = carrierName,
          notes = notes,
          allocateToRollIds = allocateToRollIds
        )
        closeQuickAction()
        _notification.value = UiNotification("بارنامه و هزینه باربری با موفقیت ثبت و تخصیص داده شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت باربری: ${e.localizedMessage}", true)
      }
    }
  }

  fun startEditShippingExpense(expense: ShippingExpenseEntity) {
    editingShippingExpense.value = expense
    openQuickAction(QuickActionType.EDIT_SHIPPING_EXPENSE)
  }

  fun updateShippingExpense(
    expense: ShippingExpenseEntity,
    allocatedRollIds: List<Long>
  ) {
    viewModelScope.launch {
      try {
        repository.updateShippingExpense(expense, allocatedRollIds)
        closeQuickAction()
        editingShippingExpense.value = null
        _notification.value = UiNotification("بارنامه و تسهیم باربری به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش باربری: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteShippingExpense(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteShippingExpense(id)
        closeQuickAction()
        editingShippingExpense.value = null
        _notification.value = UiNotification("رکورد باربری حذف و سهم طاقه‌ها آزاد شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف باربری: ${e.localizedMessage}", true)
      }
    }
  }

  // ==========================================
  // Order Status Workflow & History
  // ==========================================

  fun updateOrderStatus(orderId: Long, newStatus: String, note: String = "") {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.updateOrderStatusWithHistory(orderId, newStatus, note)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در تغییر وضعیت سفارش: ${e.message}", true)
      }
    }
  }

  // ==========================================
  // Fixed Costs Management (هزینه‌های ثابت با Scope)
  // ==========================================

  fun submitFixedCost(
    title: String,
    amount: Long,
    scope: String,
    targetCategory: String = "",
    targetProductCodes: String = "",
    targetProductionId: Long = 0L,
    notes: String = ""
  ) {
    viewModelScope.launch {
      try {
        repository.insertFixedCost(
          FixedCostEntity(
            title = title,
            amount = amount,
            scope = scope,
            targetCategory = targetCategory,
            targetProductCodes = targetProductCodes,
            targetProductionId = targetProductionId,
            date = PersianDateHelper.getCurrentPersianDate(),
            timestamp = System.currentTimeMillis(),
            notes = notes
          )
        )
        closeQuickAction()
        _notification.value = UiNotification("هزینه ثابت «$title» با دامنه تخصیص ثبت گردید")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت هزینه ثابت: ${e.message}", true)
      }
    }
  }

  fun deleteFixedCost(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteFixedCost(id)
        _notification.value = UiNotification("هزینه ثابت حذف شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف هزینه: ${e.message}", true)
      }
    }
  }

  // ==========================================
  // FABRIC CATEGORIES
  // ==========================================
  fun submitFabricCategory(name: String, code: String, description: String = "") {
    viewModelScope.launch {
      try {
        repository.insertFabricCategory(
          FabricCategoryEntity(name = name, code = code, description = description, isActive = true)
        )
        _notification.value = UiNotification("دسته‌بندی پارچه «$name» ثبت شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت دسته‌بندی: ${e.localizedMessage}", true)
      }
    }
  }

  fun updateFabricCategory(category: FabricCategoryEntity) {
    viewModelScope.launch {
      try {
        repository.updateFabricCategory(category)
        _notification.value = UiNotification("دسته‌بندی پارچه «${category.name}» به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش دسته‌بندی: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteFabricCategory(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteFabricCategory(id)
        _notification.value = UiNotification("دسته‌بندی پارچه حذف شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف دسته‌بندی: ${e.localizedMessage}", true)
      }
    }
  }

  // ==========================================
  // SHIPPING COMPANIES
  // ==========================================
  fun submitShippingCompany(name: String, phone: String = "", address: String = "", notes: String = "") {
    viewModelScope.launch {
      try {
        repository.insertShippingCompany(
          ShippingCompanyEntity(name = name, phone = phone, address = address, notes = notes)
        )
        _notification.value = UiNotification("شرکت باربری «$name» با موفقیت اضافه شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت باربری: ${e.localizedMessage}", true)
      }
    }
  }

  fun updateShippingCompany(company: ShippingCompanyEntity) {
    viewModelScope.launch {
      try {
        repository.updateShippingCompany(company)
        _notification.value = UiNotification("اطلاعات باربری «${company.name}» به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش باربری: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteShippingCompany(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteShippingCompany(id)
        _notification.value = UiNotification("شرکت باربری حذف شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف باربری: ${e.localizedMessage}", true)
      }
    }
  }

  // ==========================================
  // BASE COST CONFIGURATIONS
  // ==========================================
  fun submitBaseCostConfig(
    title: String,
    costType: String = "OVERHEAD",
    amount: Long,
    unit: String = "تومان / قطعه",
    isPerGarment: Boolean = true
  ) {
    viewModelScope.launch {
      try {
        repository.insertBaseCostConfig(
          BaseCostConfigEntity(
            title = title,
            costType = costType,
            amount = amount,
            unit = unit,
            isPerGarment = isPerGarment,
            isActive = true
          )
        )
        _notification.value = UiNotification("پیکربندی بهای پایه «$title» ذخیره شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت بهای پایه: ${e.localizedMessage}", true)
      }
    }
  }

  fun updateBaseCostConfig(config: BaseCostConfigEntity) {
    viewModelScope.launch {
      try {
        repository.updateBaseCostConfig(config)
        _notification.value = UiNotification("پیکربندی هزینه «${config.title}» به‌روزرسانی شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش هزینه: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteBaseCostConfig(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteBaseCostConfig(id)
        _notification.value = UiNotification("پیکربندی هزینه حذف شد")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف هزینه: ${e.localizedMessage}", true)
      }
    }
  }

  // ==========================================
  // ENHANCED CUTTING PART & WORKFLOW
  // ==========================================
  fun submitCuttingPart(
    rollId: Long,
    partTitle: String,
    productId: Long?,
    productCode: String,
    productName: String,
    size: String,
    color: String,
    workType: String,
    customerId: Long?,
    customerName: String,
    orderId: Long?,
    orderNumber: String,
    cutQuantity: Int,
    metersUsed: Double,
    weightKgUsed: Double = 0.0,
    sewingWagePerItem: Long = 0L,
    accessoriesCostPerItem: Long = 0L,
    sellingPrice: Long = 0L,
    notes: String = "",
    consumablesList: List<ProductionConsumableInputItem> = emptyList()
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.addCuttingPart(
          rollId = rollId,
          partTitle = partTitle,
          productId = productId,
          productCode = productCode,
          productName = productName,
          size = size,
          color = color,
          workType = workType,
          customerId = customerId,
          customerName = customerName,
          orderId = orderId,
          orderNumber = orderNumber,
          cutQuantity = cutQuantity,
          metersUsed = metersUsed,
          weightKgUsed = weightKgUsed,
          sewingWagePerItem = sewingWagePerItem,
          accessoriesCostPerItem = accessoriesCostPerItem,
          sellingPrice = sellingPrice,
          notes = notes,
          consumablesList = consumablesList
        )
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت پارت برش: ${e.localizedMessage}", true)
      }
    }
  }

  fun completeCuttingToReadyGoods(
    cuttingId: Long,
    destination: String = "انبار محصولات آماده",
    note: String = ""
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.completeCuttingToReadyGoods(cuttingId, destination, note)
        if (success) {
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در تکمیل برش به انبار: ${e.localizedMessage}", true)
      }
    }
  }

  fun allocateShippingExpenseAdvanced(
    expenseId: Long,
    method: ShippingAllocationMethod,
    targetRollIds: List<Long>
  ) {
    viewModelScope.launch {
      try {
        val success = repository.allocateShippingToRollsAdvanced(expenseId, method, targetRollIds)
        if (success) {
          _notification.value = UiNotification("تسهیم پیشرفته هزینه باربری بر اساس ${method.title} اعمال شد")
        } else {
          _notification.value = UiNotification("خطا در انجام تسهیم باربری", true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در تسهیم باربری: ${e.localizedMessage}", true)
      }
    }
  }

  // ==========================================
  // 1, 2, 3: Multi-Product Ready Goods from Roll + Inventory Control
  // ==========================================

  fun submitMultiProductReadyGoods(
    rollId: Long,
    products: List<MultiProductReadyItem>,
    note: String = ""
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.submitMultiProductReadyGoods(rollId, products, note)
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت کارهای آماده: ${e.message}", true)
      }
    }
  }

  // ==========================================
  // 4, 8: Production with Consumables & Tailor Cost
  // ==========================================

  fun submitProductionWithConsumables(
    modelCode: String,
    modelName: String,
    quantity: Int,
    rollId: Long?,
    rollCode: String,
    fabricMetersUsed: Double,
    sewingWagePerItem: Long,
    consumables: List<ProductionConsumableInputItem>,
    note: String = ""
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.submitProductionWithConsumables(
          modelCode = modelCode,
          modelName = modelName,
          quantity = quantity,
          rollId = rollId,
          rollCode = rollCode,
          fabricMetersUsed = fabricMetersUsed,
          sewingWagePerItem = sewingWagePerItem,
          consumables = consumables,
          note = note
        )
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت تولید: ${e.message}", true)
      }
    }
  }

  // ==========================================
  // Sale Return (مرجوعی فروش و بازگشت به انبار)
  // ==========================================

  fun submitSaleReturn(
    orderId: Long,
    returnQuantity: Int,
    reason: String,
    returnToStock: Boolean = true,
    refundAmount: Long = 0L,
    operator: String = "مدیر سیستم"
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.recordSaleReturn(
          orderId = orderId,
          returnQuantity = returnQuantity,
          reason = reason,
          returnToStock = returnToStock,
          refundAmount = refundAmount,
          operator = operator
        )
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت مرجوعی: ${e.message}", true)
      }
    }
  }

  // ==========================================
  // Purchase Order with Partial Delivery & Shipping
  // ==========================================

  fun submitPurchaseOrderWithDelivery(
    supplierId: Long,
    supplierName: String,
    materialId: Long,
    materialName: String,
    orderedQuantity: Double,
    deliveredQuantity: Double,
    unitPrice: Long,
    shippingCost: Long = 0L,
    discountAmount: Long = 0L,
    paidAmount: Long = 0L,
    notes: String = "",
    operator: String = "مدیر سیستم"
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.recordPurchaseOrderWithDelivery(
          supplierId = supplierId,
          supplierName = supplierName,
          materialId = materialId,
          materialName = materialName,
          orderedQuantity = orderedQuantity,
          deliveredQuantity = deliveredQuantity,
          unitPrice = unitPrice,
          shippingCost = shippingCost,
          discountAmount = discountAmount,
          paidAmount = paidAmount,
          notes = notes,
          operator = operator
        )
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت سفارش خرید: ${e.message}", true)
      }
    }
  }

  // ==========================================
  // Atomic Production from BOM (کسر اتمیک مواد و ثبت محصول)
  // ==========================================

  fun submitAtomicProduction(
    productId: Long,
    color: String,
    size: String,
    quantity: Int,
    sewingWagePerItem: Long = 0L,
    operator: String = "مدیر سیستم"
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.produceAtomicFromBOM(
          productId = productId,
          color = color,
          size = size,
          quantity = quantity,
          tailorWagePerItem = sewingWagePerItem,
          operator = operator
        )
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت تولید: ${e.message}", true)
      }
    }
  }

  // ==========================================
  // Market Price Update (بروزرسانی نرخ بازار و محاسبه مجدد محصولات)
  // ==========================================

  fun submitMarketPriceUpdate(
    materialId: Long,
    newPrice: Long,
    reason: String = "نوسان نرخ بازار",
    operator: String = "مدیر سیستم"
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.updateMaterialMarketPrice(
          materialId = materialId,
          newPrice = newPrice,
          reason = reason,
          operator = operator
        )
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در تغییر نرخ بازار: ${e.message}", true)
      }
    }
  }

  // ==========================================
  // Customer & Supplier Payments
  // ==========================================

  fun submitCustomerPayment(
    customerId: Long,
    amount: Long,
    orderId: Long? = null,
    orderNumber: String = "",
    trackingCode: String = "",
    notes: String = ""
  ) {
    viewModelScope.launch {
      try {
        val cust = customers.value.find { it.id == customerId }
        if (cust != null) {
          val (success, msg) = repository.recordCustomerPayment(
            customerId = customerId,
            customerName = cust.name,
            orderId = orderId,
            orderNumber = orderNumber,
            amount = amount,
            paymentMethod = "کارت به کارت / نقدی",
            referenceNumber = trackingCode,
            notes = notes
          )
          if (success) {
            closeQuickAction()
            _notification.value = UiNotification(msg)
          } else {
            _notification.value = UiNotification(msg, true)
          }
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت دریافت وجه: ${e.message}", true)
      }
    }
  }

  fun submitSupplierPayment(
    supplierId: Long,
    amount: Long,
    purchaseOrderId: Long? = null,
    orderNumber: String = "",
    trackingCode: String = "",
    notes: String = ""
  ) {
    viewModelScope.launch {
      try {
        val sup = suppliers.value.find { it.id == supplierId }
        if (sup != null) {
          val (success, msg) = repository.recordSupplierPayment(
            supplierId = supplierId,
            supplierName = sup.name,
            purchaseOrderId = purchaseOrderId,
            orderNumber = orderNumber,
            amount = amount,
            paymentMethod = "حواله پایا / ساتنا",
            referenceNumber = trackingCode,
            notes = notes
          )
          if (success) {
            closeQuickAction()
            _notification.value = UiNotification(msg)
          } else {
            _notification.value = UiNotification(msg, true)
          }
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت پرداخت: ${e.message}", true)
      }
    }
  }

  // ==========================================
  // Smart Deletes (حذف هوشمند با حفظ تاریخچه مالی)
  // ==========================================

  fun submitSmartDeleteProduct(productId: Long) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.smartDeleteProduct(productId)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در غیرفعال‌سازی کالا: ${e.message}", true)
      }
    }
  }

  fun submitSmartDeleteMaterial(materialId: Long) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.smartDeleteMaterial(materialId)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف ماده: ${e.message}", true)
      }
    }
  }

  fun submitSmartDeleteCustomer(customerId: Long) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.smartDeleteCustomer(customerId)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در مدیریت مشتری: ${e.message}", true)
      }
    }
  }

  fun submitSmartDeleteSupplier(supplierId: Long) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.smartDeleteSupplier(supplierId)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در مدیریت تأمین‌کننده: ${e.message}", true)
      }
    }
  }

  // ==========================================
  // Inventory Physical Adjustment (انبارگردانی)
  // ==========================================

  fun submitInventoryAdjustment(
    itemType: String,
    itemId: Long,
    itemCode: String,
    itemName: String,
    color: String = "",
    size: String = "",
    adjustmentType: String,
    quantity: Double,
    reason: String,
    notes: String = "",
    operator: String = "مدیر انبار"
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.recordInventoryAdjustment(
          itemType = itemType,
          itemId = itemId,
          itemCode = itemCode,
          itemName = itemName,
          color = color,
          size = size,
          adjustmentType = adjustmentType,
          quantity = quantity,
          reason = reason,
          notes = notes,
          operator = operator
        )
        if (success) {
          closeQuickAction()
          _notification.value = UiNotification(msg)
        } else {
          _notification.value = UiNotification(msg, true)
        }
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت انبارگردانی: ${e.message}", true)
      }
    }
  }

  // ==========================================
  // Master Data CRUD Helpers
  // ==========================================

  fun saveCategory(code: String, name: String) {
    viewModelScope.launch {
      try {
        repository.saveCategory(
          CategoryEntity(code = code, name = name, createdDate = PersianDateHelper.getCurrentPersianDate())
        )
        _notification.value = UiNotification("دسته‌بندی «$name» با موفقیت ذخیره شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ذخیره دسته‌بندی: ${e.message}", true)
      }
    }
  }

  fun deleteCategory(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteCategory(id)
        _notification.value = UiNotification("دسته‌بندی حذف شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف دسته‌بندی: ${e.message}", true)
      }
    }
  }

  fun saveColor(name: String, hexCode: String) {
    viewModelScope.launch {
      try {
        repository.saveColor(ColorEntity(name = name, colorHex = hexCode))
        _notification.value = UiNotification("رنگ «$name» ذخیره شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ذخیره رنگ: ${e.message}", true)
      }
    }
  }

  fun deleteColor(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteColor(id)
        _notification.value = UiNotification("رنگ حذف شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.message}", true)
      }
    }
  }

  fun saveSize(name: String) {
    viewModelScope.launch {
      try {
        repository.saveSize(SizeEntity(name = name, sortOrder = 1))
        _notification.value = UiNotification("سایز «$name» ذخیره شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ذخیره سایز: ${e.message}", true)
      }
    }
  }

  fun deleteSize(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteSize(id)
        _notification.value = UiNotification("سایز حذف شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.message}", true)
      }
    }
  }

  fun saveMaterial(code: String, name: String, category: String, unit: String, currentPrice: Long, minStock: Double) {
    viewModelScope.launch {
      try {
        repository.saveMaterial(
          MaterialEntity(
            code = code,
            name = name,
            category = category,
            unit = unit,
            currentPrice = currentPrice,
            lastPurchasePrice = currentPrice,
            lastPriceChangeDate = PersianDateHelper.getCurrentPersianDate(),
            lastPriceChangeTimestamp = System.currentTimeMillis(),
            minStockThreshold = minStock,
            stockQuantity = 0.0
          )
        )
        _notification.value = UiNotification("ماده «$name» با نرخ پایه $currentPrice ذخیره شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ذخیره ماده: ${e.message}", true)
      }
    }
  }

  fun saveProduct(
    code: String,
    name: String,
    category: String,
    targetMarginPercent: Double,
    overheadCost: Long,
    allocatedFreightCost: Long,
    sellingPriceOverride: Long = 0L
  ) {
    viewModelScope.launch {
      try {
        repository.saveProduct(
          ProductEntity(
            code = code,
            name = name,
            categoryName = category,
            targetProfitPercent = targetMarginPercent,
            overheadCost = overheadCost,
            allocatedFreightCost = allocatedFreightCost,
            suggestedSellingPrice = sellingPriceOverride,
            lastPriceUpdateDate = PersianDateHelper.getCurrentPersianDate(),
            lastPriceUpdateTimestamp = System.currentTimeMillis()
          )
        )
        _notification.value = UiNotification("کالای «$name» تعریف شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در تعریف کالا: ${e.message}", true)
      }
    }
  }

  fun saveBOM(productId: Long, productCode: String, materialId: Long, materialName: String, unit: String, qty: Double, rate: Long) {
    viewModelScope.launch {
      try {
        repository.saveBOM(
          ProductBOMEntity(
            productId = productId,
            productCode = productCode,
            materialId = materialId,
            materialName = materialName,
            unit = unit,
            standardQuantity = qty,
            unitRate = rate
          )
        )
        repository.recalculateProductDynamicCost(productId)
        _notification.value = UiNotification("ماده مصرفی «$materialName» به فرمول تولید اضافه گردید.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ثبت فرمول BOM: ${e.message}", true)
      }
    }
  }

  fun deleteBOM(bomId: Long, productId: Long) {
    viewModelScope.launch {
      try {
        repository.deleteBOM(bomId)
        repository.recalculateProductDynamicCost(productId)
        _notification.value = UiNotification("ردیف فرمول BOM حذف و بهای تمام‌شده بروزرسانی شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف فرمول: ${e.message}", true)
      }
    }
  }

  fun saveMaterialUnit(code: String, name: String) {
    viewModelScope.launch {
      try {
        repository.saveMaterialUnit(MaterialUnitEntity(code = code, name = name))
        _notification.value = UiNotification("واحد «$name» ثبت گردید.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.message}", true)
      }
    }
  }

  fun deleteMaterialUnit(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteMaterialUnit(id)
        _notification.value = UiNotification("واحد حذف شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.message}", true)
      }
    }
  }

  fun savePriceChangeReason(title: String) {
    viewModelScope.launch {
      try {
        repository.savePriceChangeReason(PriceChangeReasonEntity(title = title))
        _notification.value = UiNotification("علت تغییر قیمت «$title» ثبت شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.message}", true)
      }
    }
  }

  fun deletePriceChangeReason(id: Long) {
    viewModelScope.launch {
      try {
        repository.deletePriceChangeReason(id)
        _notification.value = UiNotification("علت تغییر حذف شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.message}", true)
      }
    }
  }

  fun saveSalesChannel(title: String) {
    viewModelScope.launch {
      try {
        val chCode = "CH-${System.currentTimeMillis().toString().takeLast(4)}"
        repository.saveSalesChannel(SalesChannelEntity(name = title, code = chCode))
        _notification.value = UiNotification("کانال فروش «$title» ثبت شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.message}", true)
      }
    }
  }

  fun deleteSalesChannel(id: Long) {
    viewModelScope.launch {
      try {
        repository.deleteSalesChannel(id)
        _notification.value = UiNotification("کانال فروش حذف شد.")
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا: ${e.message}", true)
      }
    }
  }

  // ==========================================
  // PRICE UPDATE ACTIONS
  // ==========================================

  fun updateRollPrice(rollId: Long, newPricePerMeter: Long, newPricePerKg: Long = 0L, reason: String = "تغییر قیمت بازار") {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.updateFabricRollCurrentPrice(rollId, newPricePerMeter, newPricePerKg, reason)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در به‌روزرسانی قیمت: ${e.localizedMessage}", true)
      }
    }
  }

  fun updateMaterialPrice(materialId: Long, newPrice: Long, reason: String = "تغییر قیمت بازار") {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.updateMaterialCurrentPrice(materialId, newPrice, reason)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در به‌روزرسانی قیمت: ${e.localizedMessage}", true)
      }
    }
  }

  // state برای دیالوگ قیمت
  private val _priceUpdateTarget = MutableStateFlow<PriceUpdateTarget?>(null)
  val priceUpdateTarget: StateFlow<PriceUpdateTarget?> = _priceUpdateTarget.asStateFlow()

  fun openPriceUpdateDialog(target: PriceUpdateTarget) { _priceUpdateTarget.value = target }
  fun closePriceUpdateDialog() { _priceUpdateTarget.value = null }

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

  // ==========================================
  // WAYBILL DETAILS
  // ==========================================

  private val _waybillDetailsTarget = MutableStateFlow<com.example.data.model.FabricRollEntity?>(null)
  val waybillDetailsTarget: StateFlow<com.example.data.model.FabricRollEntity?> = _waybillDetailsTarget.asStateFlow()

  fun openWaybillDetails(roll: com.example.data.model.FabricRollEntity) {
    _waybillDetailsTarget.value = roll
  }

  fun closeWaybillDetails() {
    _waybillDetailsTarget.value = null
  }

  fun getRepository(): com.example.data.repository.ManufacturingRepository = repository

  // ==========================================
  // ROLL USAGE ACTIONS
  // ==========================================

  fun updateRollUsageAction(
    usageId: Long,
    newMetersUsed: Double,
    newWeightKgUsed: Double,
    newModelName: String,
    newNote: String
  ) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.updateRollUsage(
          usageId, newMetersUsed, newWeightKgUsed, newModelName, newNote
        )
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در ویرایش: ${e.localizedMessage}", true)
      }
    }
  }

  fun deleteRollUsageAction(usageId: Long) {
    viewModelScope.launch {
      try {
        val (success, msg) = repository.deleteRollUsage(usageId)
        _notification.value = UiNotification(msg, !success)
      } catch (e: Exception) {
        _notification.value = UiNotification("خطا در حذف: ${e.localizedMessage}", true)
      }
    }
  }

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
          channel = "رزرو"
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
}

