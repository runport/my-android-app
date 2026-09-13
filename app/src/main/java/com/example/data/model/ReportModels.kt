package com.example.data.model

import androidx.compose.ui.graphics.Color

/**
 * Report Models for the comprehensive Professional Reports module
 * All models derive strictly from the database and FinancialCalculationService
 */

data class ProductSalesPerformance(
  val modelCode: String,
  val modelName: String,
  val unitsSold: Int,
  val totalSalesAmount: Long,
  val totalProfit: Long,
  val marginPercent: Double
)

data class DateSalesPoint(
  val datePersian: String,
  val timestamp: Long,
  val amount: Long,
  val count: Int
)

data class SalesReportData(
  val totalSalesAmount: Long = 0L,
  val salesCount: Int = 0,
  val dailySalesAmount: Long = 0L,
  val monthlySalesAmount: Long = 0L,
  val yearlySalesAmount: Long = 0L,
  val bestProducts: List<ProductSalesPerformance> = emptyList(),
  val worstProducts: List<ProductSalesPerformance> = emptyList(),
  val dailyBreakdown: List<DateSalesPoint> = emptyList()
)

data class ProductProductionSummary(
  val modelCode: String,
  val modelName: String,
  val totalQuantity: Int,
  val totalCost: Long,
  val unitCost: Long
)

data class RollProductionSummary(
  val rollNumber: String,
  val fabricType: String,
  val totalItemsProduced: Int,
  val modelsProduced: List<String>
)

data class DateProductionSummary(
  val datePersian: String,
  val quantity: Int,
  val batchesCount: Int
)

data class ProductionReportData(
  val totalProductionCount: Int = 0,
  val byProduct: List<ProductProductionSummary> = emptyList(),
  val byFabricRoll: List<RollProductionSummary> = emptyList(),
  val byDate: List<DateProductionSummary> = emptyList(),
  val completedCount: Int = 0,
  val inProgressCount: Int = 0
)

data class StockAlertSummary(
  val code: String,
  val name: String,
  val category: String,
  val currentStock: Double,
  val unit: String,
  val minThreshold: Double,
  val isCritical: Boolean
)

data class InventoryReportData(
  val totalInventoryValue: Long = 0L,
  val fabricValue: Long = 0L,
  val fabricTotalMeters: Double = 0.0,
  val consumablesValue: Long = 0L,
  val consumablesItemCount: Int = 0,
  val remainingRollsCount: Int = 0,
  val remainingRollsMeters: Double = 0.0,
  val lowStockItems: List<StockAlertSummary> = emptyList(),
  val criticalStockItems: List<StockAlertSummary> = emptyList()
)

data class OrdersReportData(
  val totalOrdersCount: Int = 0,
  val totalOrdersAmount: Long = 0L,
  val orderedCount: Int = 0,
  val inProductionCount: Int = 0,
  val readyToShipCount: Int = 0,
  val deliveredCount: Int = 0,
  val averageOrderValue: Long = 0L,
  val recentOrders: List<SaleOrderEntity> = emptyList()
)

data class CostReportData(
  val materialCost: Long = 0L,
  val consumablesCost: Long = 0L,
  val freightCost: Long = 0L,
  val tailorCost: Long = 0L,
  val fixedCosts: Long = 0L,
  val otherCosts: Long = 0L,
  val totalCost: Long = 0L
) {
  fun getPercentages(): Map<String, Double> {
    val total = totalCost.toDouble().coerceAtLeast(1.0)
    return mapOf(
      "پارچه" to (materialCost / total) * 100.0,
      "ملزومات" to (consumablesCost / total) * 100.0,
      "باربری" to (freightCost / total) * 100.0,
      "دستمزد دوخت" to (tailorCost / total) * 100.0,
      "هزینه‌های ثابت" to (fixedCosts / total) * 100.0,
      "متفرقه" to (otherCosts / total) * 100.0
    )
  }
}

data class ProfitReportData(
  val revenue: Long = 0L,
  val cogs: Long = 0L,
  val grossProfit: Long = 0L,
  val expenses: Long = 0L,
  val netProfit: Long = 0L,
  val profitMarginPercent: Double = 0.0
)

data class ConsumableItemSummary(
  val name: String,
  val category: String,
  val purchasedValue: Long,
  val usedValue: Long,
  val stockQuantity: Double,
  val unit: String
)

data class ConsumablesReportData(
  val totalPurchasedValue: Long = 0L,
  val totalUsedValue: Long = 0L,
  val remainingStockValue: Long = 0L,
  val items: List<ConsumableItemSummary> = emptyList()
)

data class FabricRollSummary(
  val rollNumber: String,
  val fabricType: String,
  val initialMeters: Double,
  val remainingMeters: Double,
  val totalCost: Long,
  val freightAllocated: Long,
  // Phase 15 Patch 5A: actual vs economic price tracking
  val buyPricePerMeter: Long = 0L,
  val currentPricePerMeter: Long = 0L
)

data class FabricReportData(
  val totalRolls: Int = 0,
  val activeRollsCount: Int = 0,
  val inboundMeters: Double = 0.0,
  val consumedMeters: Double = 0.0,
  val remainingMeters: Double = 0.0,
  val totalFabricCost: Long = 0L,
  val totalFreightAllocated: Long = 0L,
  val rolls: List<FabricRollSummary> = emptyList(),
  // Phase 15 Patch 5A: actual (historical) vs economic (market) value
  val totalActualValue: Long = 0L,
  val totalEconomicValue: Long = 0L,
  val opportunityDelta: Long = 0L
)

data class FreightReportData(
  val totalFreightExpense: Long = 0L,
  val avgPerRoll: Long = 0L,
  val avgPerKg: Long = 0L,
  val avgPerUnit: Long = 0L,
  val totalBillsCount: Int = 0,
  val bills: List<ShippingExpenseEntity> = emptyList()
)

data class TailorBatchCostSummary(
  val batchNumber: String,
  val modelName: String,
  val quantity: Int,
  val tailorWagePerItem: Long,
  val totalBatchWage: Long
)

data class TailorCostReportData(
  val totalTailorCost: Long = 0L,
  val avgWagePerModel: Long = 0L,
  val batches: List<TailorBatchCostSummary> = emptyList()
)

/**
 * Drill-down data models:
 * Every important figure in reports/dashboard can be inspected hierarchically
 */
data class DrillDownComponent(
  val label: String,
  val amount: Long,
  val isPositive: Boolean, // true: revenue/addition (+), false: cost/deduction (-)
  val percentage: Double = 0.0,
  val keyId: String = ""
)

data class DrillDownItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val amount: Long,
  val date: String,
  val badgeText: String? = null
)

data class DrillDownData(
  val metricKey: String,
  val title: String,
  val totalAmount: Long,
  val formattedTotal: String,
  val subtitle: String,
  val components: List<DrillDownComponent> = emptyList(),
  val items: List<DrillDownItem> = emptyList()
)

/**
 * Target section for dedicated settings dialog (✏️)
 */
enum class SectionSettingsTarget(val title: String) {
  HEADER("تنظیمات نام و تم کارخانه"),
  LAYOUT("تنظیمات چیدمان داشبورد"),
  KPIS("تنظیمات شاخص‌ها و حاشیه سود هدف"),
  CHART("تنظیمات نمودارها و شاخص آماری"),
  ALERTS("تنظیمات آستانه هشدارهای کمبود انبار"),
  ORDERS("تنظیمات سفارشات و باربری"),
  FONT("تنظیمات قلم و فونت نرم‌افزار"),
  COSTS("تنظیمات ساختار هزینه‌ها و سربار")
}
