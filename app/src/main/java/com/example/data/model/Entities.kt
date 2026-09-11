package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Fabric roll and raw material storage (with both Meters and Kilograms)
 */
@Entity(tableName = "fabrics")
data class FabricEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val code: String,
  val color: String,
  val batchNumber: String,
  val supplierName: String,
  val rollCount: Int,
  val totalMeters: Double,
  val totalWeightKg: Double = 0.0,
  val buyPricePerMeter: Long,
  val buyPricePerKg: Long = 0L,
  val currentMarketPrice: Long,
  val isLowStock: Boolean = false,
  val previousBuyPricePerMeter: Long = 0L,
  val previousBuyPricePerKg: Long = 0L,
  val previousPurchaseDate: String = "",
  val purchaseDate: String = "",
) {
  val totalStockValue: Long get() = (totalMeters * currentMarketPrice).toLong()
  val metersPerKg: Double get() = if (totalWeightKg > 0) totalMeters / totalWeightKg else 0.0
  val kgPerRoll: Double get() = if (rollCount > 0 && totalWeightKg > 0) totalWeightKg / rollCount else 0.0
  val hasPriceChanged: Boolean get() = previousBuyPricePerMeter > 0L && previousBuyPricePerMeter != buyPricePerMeter
}

/**
 * Cutting operation tracking & Multi-part Cutting Batches (پارت‌های برش)
 * Supports Cutting Part 1, 2, 3... with product linking, customer orders, fabric and accessory costs,
 * auto-calculated product weight, and unified status workflow (برش خورده -> در حال دوخت -> کار آماده -> تحویل شده).
 */
@Entity(tableName = "cutting_orders")
data class CuttingEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val modelCode: String,
  val modelName: String,
  val fabricCode: String,
  val targetQuantity: Int,
  val cutQuantity: Int,
  val standardMetersPerItem: Double,
  val actualMetersPerItem: Double,
  val standardWeightKgPerItem: Double = 0.0,
  val actualWeightKgPerItem: Double = 0.0, // averageProductWeight = weightKgUsed / cutQuantity
  val status: String, // "برش خورده" (CUT), "در حال دوخت" (IN_SEWING), "کار آماده" (READY), "تحویل شده" (DELIVERED)
  val date: String,
  val partNumber: Int = 1, // پارت برش ۱, ۲, ۳...
  val partTitle: String = "پارت برش ۱",
  val rollId: Long? = null,
  val rollCode: String = "",
  val productId: Long? = null,
  val productCode: String = "",
  val productName: String = "",
  val size: String = "",
  val color: String = "",
  val workType: String = "تولید برای انبار", // "تولید برای انبار", "سفارش مشتری"
  val customerId: Long? = null,
  val customerName: String = "",
  val orderId: Long? = null,
  val orderNumber: String = "",
  val metersUsed: Double = 0.0,
  val weightKgUsed: Double = 0.0,
  val sellingPrice: Long = 0L,
  val targetProfit: Long = 0L,
  val targetMargin: Double = 0.0,
  val timestamp: Long = 0L,
  val notes: String = "",
  val fabricCost: Long = 0L,
  val allocatedShippingCost: Long = 0L,
  val accessoriesCost: Long = 0L,
  val tailorCost: Long = 0L,
  val overheadCost: Long = 0L,
  val otherDirectCost: Long = 0L,
  val totalCost: Long = 0L,
  val isStockAdded: Boolean = false,
) {
  val shortageQuantity: Int get() = (targetQuantity - cutQuantity).coerceAtLeast(0)
  val progressPercent: Int get() = if (targetQuantity > 0) ((cutQuantity.toDouble() / targetQuantity) * 100).toInt().coerceIn(0, 100) else 100
  val consumptionDeviationPercent: Double get() = if (standardMetersPerItem > 0) ((actualMetersPerItem - standardMetersPerItem) / standardMetersPerItem) * 100 else 0.0
  val isAbnormalConsumption: Boolean get() = consumptionDeviationPercent > 10.0
  val calculatedAverageWeightKg: Double get() = if (cutQuantity > 0 && weightKgUsed > 0.0) weightKgUsed / cutQuantity else (if (actualWeightKgPerItem > 0.0) actualWeightKgPerItem else 0.0)
  val calculatedAverageWeightGrams: Double get() = calculatedAverageWeightKg * 1000.0
  val calculatedAverageMeters: Double get() = if (cutQuantity > 0 && metersUsed > 0.0) metersUsed / cutQuantity else (if (actualMetersPerItem > 0.0) actualMetersPerItem else 0.0)
  val unitCost: Long get() = if (cutQuantity > 0) totalCost / cutQuantity else 0L
  val unitSellingPrice: Long get() = if (cutQuantity > 0 && sellingPrice > 0L) sellingPrice / cutQuantity else (unitCost + if (cutQuantity > 0) targetProfit / cutQuantity else 0L)


  companion object {
    const val STATUS_CUT = "برش خورده"
    const val STATUS_SEWING = "در حال دوخت"
    const val STATUS_READY = "کار آماده"
    const val STATUS_WAREHOUSE = "تحویل انبار"
    const val STATUS_DELIVERED = "تحویل شده"
    const val WORK_TYPE_STOCK = "تولید برای انبار"
    const val WORK_TYPE_CUSTOMER = "سفارش مشتری"
  }
}

/**
 * Production batches with auto-calculated unit metrics and cost-profit analysis
 */
@Entity(tableName = "production_records")
data class ProductionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val modelCode: String,
  val modelName: String,
  val quantity: Int,
  val fabricRollsUsed: Int,
  val fabricMetersUsed: Double,
  val totalWeightKg: Double,
  val sewingWagePerItem: Long, // دستمزد دوخت هر کار (هزینه خیاط‌کار)
  val fabricPricePerMeter: Long,
  val accessoriesCostPerItem: Long,
  val status: String, // "سفارش داده شده", "در حال دوخت", "آماده ارسال / تکمیل موجودی"
  val date: String,
  val rollId: Long? = null, // شناسه طاقه مصرف‌شده در صورت وجود
  val rollCode: String = "", // کد طاقه مثلا "ROL-101"
  val consumablesSummary: String = "", // خلاصه ملزومات مصرفی
  val orderId: Long? = null, // سفارش مرتبط در صورت وجود
) {
  val weightPerItemGrams: Double get() = if (quantity > 0) (totalWeightKg * 1000.0) / quantity else 0.0
  val fabricMetersPerItem: Double get() = if (quantity > 0) fabricMetersUsed / quantity else 0.0
  val fabricCostPerItem: Long get() = (fabricMetersPerItem * fabricPricePerMeter).toLong()
  val tailorCostPerItem: Long get() = sewingWagePerItem // هزینه خیاط‌کار
  val tailorTotalCost: Long get() = tailorCostPerItem * quantity
  val unitCostPrice: Long get() = fabricCostPerItem + tailorCostPerItem + accessoriesCostPerItem
  val totalCost: Long get() = unitCostPrice * quantity
  val estimatedSalePricePerItem: Long get() = (unitCostPrice * 1.55).toLong()
  val unitProfit: Long get() = estimatedSalePricePerItem - unitCostPrice
  val totalProfit: Long get() = unitProfit * quantity
}

/**
 * Warehouse inventory products (ready, reserved, available, raw materials & accessories)
 */
@Entity(tableName = "inventory_items")
data class InventoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val code: String,
  val category: String, // "محصولات آماده", "مواد اولیه", "ملزومات"
  val readyForShipment: Int,
  val reservedQuantity: Int,
  val availableForSale: Int,
  val unitSalePrice: Long,
  val unitCostPrice: Long,
  val unitWeightGrams: Double,
  val totalWeightKg: Double = 0.0,
  val unitType: String = "عدد", // "عدد", "کیلوگرم", "متر", "دوک و قرقره", "توپی", "بسته", ...
  val lastUpdated: String,
  val previousCostPrice: Long = 0L,
  val previousCostDate: String = "",
  val supplierName: String = "",
  val metersPerKg: Double = 0.0, // برای ملزومات بر پایه کیلو: متراژ در کیلو
  val pricePerMeter: Long = 0L, // قیمت محاسبه‌شده هر متر
) {
  val totalStock: Int get() = readyForShipment + reservedQuantity + availableForSale
  val totalStockValue: Long get() = totalStock * unitSalePrice
  val hasPriceChanged: Boolean get() = previousCostPrice > 0L && previousCostPrice != unitCostPrice
}

/**
 * Sales and client orders with timeline status and stock fulfillment check
 */
@Entity(tableName = "sales_orders")
data class SaleOrderEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val orderNumber: String,
  val customerName: String,
  val customerPhone: String,
  val modelCode: String,
  val modelName: String,
  val quantity: Int,
  val unitPrice: Long,
  val unitCost: Long,
  val discountAmount: Long = 0L,
  val paidAmount: Long = 0L,
  val orderDate: String,
  val deliveryStatus: String, // "ثبت شده", "در تولید", "در حال تکمیل", "آماده ارسال", "ارسال شده", "تحویل شده", "لغو شده"
  val isDelayed: Boolean = false,
  val delayDays: Int = 0,
  val channel: String = "فروش حضوری",
  val customerId: Long? = null,
  val color: String = "",
  val size: String = "",
  val variantId: Long? = null,
  val shippingCost: Long = 0L,
  val costSnapshot: Long = 0L,
  val salePriceSnapshot: Long = 0L,
) {
  val effectiveUnitCost: Long get() = if (costSnapshot > 0L) costSnapshot else unitCost
  val effectiveUnitPrice: Long get() = if (salePriceSnapshot > 0L) salePriceSnapshot else unitPrice
  val grossTotal: Long get() = quantity * effectiveUnitPrice
  val netTotal: Long get() = (grossTotal - discountAmount + shippingCost).coerceAtLeast(0L)
  val remainingDebt: Long get() = (netTotal - paidAmount).coerceAtLeast(0L)
  val totalCost: Long get() = (quantity * effectiveUnitCost) + shippingCost
  val totalProfit: Long get() = (netTotal - totalCost).coerceAtLeast(0L)
}

/**
 * Customer profile with mini-analytics and purchase history tiers
 */
@Entity(tableName = "customers")
data class CustomerEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val company: String,
  val phone: String,
  val address: String,
  val category: String, // "عمده‌فروش", "فروشگاه زنجیره‌ای", "بوتیک و آنلاین"
  val totalPurchases: Long,
  val orderCount: Int,
  val currentDebt: Long,
  val tier: String, // "خرید اول", "خرید دوم", "خرید سوم+"
  val popularModels: String,
  val lastOrderDate: String,
  val totalPaid: Long = 0L,
)

/**
 * Supplier entity for fabrics and accessories with unified comprehensive profile
 */
@Entity(tableName = "suppliers")
data class SupplierEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String, // نام و نام خانوادگی
  val storeName: String = "", // اسم فروشگاه
  val mobile: String = "", // شماره موبایل
  val phone: String = "", // شماره ثابت
  val address: String = "", // آدرس
  val distributionCategory: String = "", // توزیع‌کننده چی هست (پارچه، زیپ، دکمه، نخ، ملزومات)
  val description: String = "", // توضیحات
  val supplyType: String = "پارچه و ملزومات",
  val totalPurchases: Long = 0L,
  val paidAmount: Long = 0L,
  val currentDebt: Long = 0L,
  val lastPurchaseDate: String = "",
  val priceHistoryNote: String = "",
)

/**
 * System settings and production model benchmarks
 */
@Entity(tableName = "model_standards")
data class ModelStandardEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val modelCode: String,
  val modelName: String,
  val standardFabricConsumptionMeters: Double,
  val standardWeightGrams: Double,
  val sewingWage: Long,
  val suggestedSalePrice: Long,
  val baseFabricCostPerMeter: Long,
)

enum class PeriodFilter(val title: String) {
  TODAY("۲۴ ساعت اخیر"),
  MONTH("ماه اخیر"),
  YEAR("سال اخیر"),
  CUSTOM("بازه سفارشی")
}

data class AlertItem(
  val id: String,
  val title: String,
  val description: String,
  val type: AlertType,
  val tag: String,
)

enum class AlertType {
  DANGER,
  WARNING,
  INFO
}

data class ChartPoint(
  val label: String,
  val value: Long,
  val formattedValue: String,
)

/**
 * Chart Type option for first page dashboard
 */
enum class DashboardChartType(val title: String, val desc: String) {
  BAR_LINE("ترکیبی (میله و خط)", "ستون برای فروش و خط پیوسته برای سود خالص"),
  LINE("خطی پیوسته (Line)", "روند هموار فروش و سود با نقاط عطف تعاملی"),
  BAR("میله‌ای آماری (Bar)", "ستون‌های مقایسه‌ای فروش و سود خالص"),
  AREA_LINE("مساحتی گرادیانت (Area)", "منحنی هموار با گرادیانت سایه برای فروش و سود"),
  DONUT_PRIMARY("چارت گرد اختصاصی (دونات)", "تمرکز روی سهم پرتفوی انبار و ارزش موجودی")
}

/**
 * Layout arrangement option for first page dashboard
 */
enum class DashboardLayoutArrangement(val title: String, val desc: String) {
  CLASSIC("کلاسیک (Classic)", "چیدمان استاندارد با کارت‌های شاخص، نمودارها و سفارشات"),
  COMPACT("فشرده (Compact)", "کارت‌های متراکم و صرفه‌جویی در فضا برای نمایش سریع اطلاعات"),
  MANAGEMENT("مدیریتی (Management)", "تمرکز بر سود خالص، حاشیه سود، درآمد و ساختار هزینه‌ها"),
  LARGE_CARDS("کارت‌های بزرگ (Large Cards)", "کارت‌های درشت با فونت بزرگ، نوار پیشرفت و جزئیات کامل"),
  TWO_COLUMN("دو ستونه (Two Column)", "چیدمان منظم گرید دو ستونی در کلیه بخش‌های داشبورد"),
  STATISTICS_FOCUSED("تمرکز بر آمار (Statistics)", "اولویت با ارقام تولید، برش، سفارشات، طاقه‌ها و مشتریان"),
  CHARTS_FOCUSED("تمرکز بر نمودار (Charts)", "قرارگیری نمودار ترکیبی و چارت گرد در صدر صفحه"),
  MINIMAL("مینیمال (Minimal)", "طراحی بسیار خلوت و مدرن، تنها با ارقام کلیدی بدون شلوغی"),

  // Backward compatibility aliases
  STANDARD("استاندارد اجرایی", "کارت‌های شاخص -> نمودارها -> هشدارها -> سفارشات"),
  CHARTS_FIRST("نمودار محور و آماری", "نمودار تحلیلی و چارت گرد در ابتدا -> سپس آمار و ارقام"),
  ALERTS_FIRST("عملیاتی و هشدار محور", "مرکز هشدارهای کمبود در بالا -> وضعیت سفارشات -> نمودارها");

  companion object {
    fun fromName(name: String): DashboardLayoutArrangement {
      return try {
        valueOf(name)
      } catch (_: Exception) {
        CLASSIC
      }
    }

    /**
     * The 8 primary user-selectable layouts
     */
    val primaryLayouts = listOf(
      CLASSIC,
      COMPACT,
      MANAGEMENT,
      LARGE_CARDS,
      TWO_COLUMN,
      STATISTICS_FOCUSED,
      CHARTS_FOCUSED,
      MINIMAL
    )
  }
}

/**
 * Fixed cost benchmarks, overheads, alert thresholds & factory default settings
 */
@Entity(tableName = "factory_settings")
data class FactorySettingsEntity(
  @PrimaryKey val id: Long = 1L,
  val fixedShippingCostPerOrder: Long = 180000L, // هزینه باربری ثابت
  val fixedShippingCostPerRoll: Long = 85000L,   // هزینه باربری به ازای هر طاقه
  val targetProfitMarginPercent: Double = 35.0,  // درصد حاشیه سود ثابت هدف
  val overheadCostPerItem: Long = 45000L,        // هزینه سربار و اجاره کارگاه به ازای هر کار
  val defaultAccessoriesCost: Long = 32000L,     // هزینه ملزومات پایه
  val isDarkTheme: Boolean = true,               // تم انتخابی (تاریک/روشن)
  val companyName: String = "تولیدی برتر پوشاک",
  // New user requested features:
  val dashboardChartType: String = DashboardChartType.AREA_LINE.name, // انتخاب نوع چارت آمارگیر
  val dashboardLayout: String = DashboardLayoutArrangement.STANDARD.name, // انتخاب نوع چیدمان بخش‌ها
  // Stock alert thresholds (حد هشدار کمبود موجودی جهت ایجاد نوتیفیکیشن)
  val minFabricRollsThreshold: Int = 10,       // حداقل تعداد طاقه پارچه
  val minFabricWeightKgThreshold: Double = 200.0, // حداقل وزن پارچه (کیلوگرم)
  val minReadyGoodsCountThreshold: Int = 300,  // حداقل تعداد کار آماده (تعداد)
  val minAccessoriesWeightKgThreshold: Double = 15.0, // حداقل وزن ملزومات خیاطی (کیلوگرم)
  val selectedFontCode: String = "yekan", // فونت پیش‌فرض یکان و قابل تغییر به وزیرمتن، شبنم و سیستم
  val systemNotificationsEnabled: Boolean = true, // ارسال هشدارهای فوری به نوتیفیکیشن بالای گوشی
  val autoSyncRollPrices: Boolean = true, // اعمال تغییرات قیمت طاقه/ملزومات بر موجودی انبار با ثبت قیمت قبلی قرمز
  val customUnitTypes: String = "عدد,کیلوگرم,متر,دوک و قرقره,توپی" // واحدهای پیش‌فرض و افزوده شده
)

/**
 * Donut / Pie slice data structure for Circular Chart
 */
data class DonutSlice(
  val label: String,
  val value: Double,
  val count: Int,
  val unit: String,
  val color: androidx.compose.ui.graphics.Color,
  val formattedValue: String,
)

/**
 * Item specification for multi-model roll cutting operation
 */
data class MultiCutModelItem(
  val id: String = java.util.UUID.randomUUID().toString(),
  val modelName: String,
  val modelCode: String,
  val cutQuantity: Int,
  val metersPerItem: Double, // قد کار یا متراژ مصرفی هر عدد (متر)
  val heightCm: Int = 0,     // قد کار به سانتی‌متر
  val trimsUsedNote: String = "",
) {
  val totalMetersUsed: Double get() = cutQuantity * metersPerItem
}

/**
 * Trims and accessories bill-of-materials entry
 */
data class TrimsConsumptionItem(
  val accessoryCode: String,
  val accessoryName: String,
  val unit: String,
  val quantityPerGarment: Double,
  val unitCostPrice: Long,
) {
  val totalCostPerGarment: Long get() = (quantityPerGarment * unitCostPrice).toLong()
}

/**
 * Standard units for garment accessories & trims
 */
enum class AccessoryUnit(val title: String, val code: String) {
  PIECE("عدد", "PIECE"),
  METER("متر", "METER"),
  YARD("یارد", "YARD"),
  KILOGRAM("کیلوگرم", "KILOGRAM"),
  GRAM("گرم", "GRAM");

  companion object {
    fun fromTitle(title: String): AccessoryUnit {
      return values().firstOrNull { it.title == title || it.name.equals(title, ignoreCase = true) } ?: PIECE
    }
  }
}

/**
 * Shipping freight cost allocation methods
 * Supports By Purchase Value (Default), By Weight, By Volume, Equal, and Manual Allocation.
 */
enum class ShippingAllocationMethod(val title: String, val desc: String) {
  BY_PURCHASE_VALUE("بر اساس ارزش ریالی خرید (پیش‌فرض)", "تسهیم متناسب با مبلغ خرید هر قلم کالا"),
  BY_WEIGHT("بر اساس وزن (کیلوگرم)", "تسهیم متناسب با وزن ناخالص هر قلم یا طاقه"),
  BY_VOLUME("بر اساس حجم (متر مکعب)", "تسهیم متناسب با حجم اشغال‌شده بار"),
  EQUAL("تخصیص مساوی", "تقسیم مساوی مبلغ کل باربری بین تمام اقلام"),
  MANUAL("تخصیص دستی", "ورود دستی سهم هر کالا مشروط بر برابری دقیق جمع سهم‌ها با کرایه کل"),
  // Backward compatibility keys
  PER_ITEM("به ازای هر قلم / طاقه", "تقسیم مساوی مبلغ باربری بر تعداد طاقه‌ها یا اقلام محموله"),
  PER_WEIGHT("بر اساس وزن / کیلوگرم", "تخصیص باربری متناسب با وزن ناخالص هر طاقه یا بسته (کیلوگرم)"),
  PER_QUANTITY("بر اساس تعداد / متراژ", "تقسیم متناسب با متراژ پارچه یا تعداد عددی ملزومات"),
  WEIGHTED("تخصیص وزنی نسبی", "تسهیم وزنی متناسب با سهم ارزش ریالی یا وزن نسبی هر قلم")
}

/**
 * Fabric Category Entity (دسته‌بندی مستقل پارچه)
 * نخ، غواصی، بنگال، بنگال اسپندکس، پنبه دورس، کرپ و ...
 */
@Entity(tableName = "fabric_categories")
data class FabricCategoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val code: String = "",
  val name: String, // e.g. "نخ", "غواصی", "بنگال", "بنگال اسپندکس"
  val description: String = "",
  val isActive: Boolean = true,
  val createdDate: String = "",
)

/**
 * Shipping Company Entity (شرکت‌های باربری و رانندگان)
 * مدیریت مستقل شرکت‌های باربری، شماره تماس، آدرس و وضعیت فعالیت
 */
@Entity(tableName = "shipping_companies")
data class ShippingCompanyEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String, // نام باربری مثلا "باربری پیشتاز", "کالارسان شیراز"
  val phone: String = "",
  val address: String = "",
  val notes: String = "",
  val isActive: Boolean = true,
)

/**
 * Waybill Items (اقلام بارنامه و باربری)
 * پشتیبانی از چند تأمین‌کننده در یک بارنامه و نگهداری سهم باربری هر قلم
 */
@Entity(tableName = "waybill_items")
data class WaybillItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val waybillId: Long, // شناسه در shipping_expenses
  val supplierId: Long? = null,
  val supplierName: String = "",
  val itemType: String, // "FABRIC_ROLL", "ACCESSORY"
  val itemId: Long = 0L,
  val itemCode: String = "",
  val itemName: String = "",
  val quantity: Double = 0.0,
  val unit: String = "عدد",
  val purchaseValue: Long = 0L,
  val weightKg: Double = 0.0,
  val volumeM3: Double = 0.0,
  val shippingAllocation: Long = 0L,
  val notes: String = "",
)

/**
 * Base Cost / Overhead / Pricing Configuration (هزینه‌های پایه کارگاه و قیمت‌گذاری)
 * هزینه خیاط، ملزومات پایه، هزینه نخ، دوک، برق، کرایه، سایر سربار، حاشیه سود هدف
 */
@Entity(tableName = "base_cost_configs")
data class BaseCostConfigEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String, // مثلا "هزینه خیاط", "هزینه ملزومات پایه", "هزینه نخ", "هزینه دوک", "هزینه برق", "هزینه کرایه", "سایر سربار", "حاشیه سود هدف"
  val costType: String = "OVERHEAD", // "TAILOR", "BASE_ACCESSORY", "THREAD", "SPOOL", "ELECTRICITY", "RENT", "OVERHEAD", "PROFIT_MARGIN", "OTHER"
  val amount: Long = 0L,
  val unit: String = "تومان به ازای هر کار",
  val isPerGarment: Boolean = true,
  val isActive: Boolean = true,
  val notes: String = "",
)

/**
 * 1. Fabric Roll Entity (مدیریت طاقه)
 * Includes ID, rollCode, inboundDate, fabricType, initial/remaining meters, weight, buy price, allocated shipping, status
 * Maintains dual inventory in KG and Meter simultaneously.
 */
@Entity(tableName = "fabric_rolls")
data class FabricRollEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val rollCode: String, // شناسه/کد طاقه مثلا "ROL-101"
  val inboundDate: String, // تاریخ ورود شمسی
  val inboundTimestamp: Long = 0L,
  val fabricType: String, // نوع پارچه مثلا "پنبه دورس ۳ نخ"
  val fabricCode: String = "", // کد پارچه مثلا "M204"
  val color: String = "", // رنگ
  val initialMeters: Double, // مقدار اولیه به متر
  val remainingMeters: Double, // مقدار باقی‌مانده به متر
  val weightKg: Double = 0.0, // وزن اولیه به کیلوگرم
  val remainingWeightKg: Double = 0.0, // وزن باقیمانده واقعی به کیلوگرم
  val buyPricePerMeter: Long = 0L, // قیمت خرید هر متر
  val buyPricePerKg: Long = 0L, // قیمت خرید هر کیلوگرم
  val allocatedShippingCost: Long = 0L, // هزینه باربری تخصیص‌یافته به این طاقه از بارنامه
  val otherDirectCost: Long = 0L, // سایر هزینه‌های مستقیم
  val fabricCategoryId: Long? = null, // پیوند به دسته‌بندی پارچه
  val fabricCategoryName: String = "",
  val status: String = "موجود", // "موجود", "در حال مصرف", "پایان یافته"
  val supplierName: String = "",
  val batchNumber: String = "",
  val shippingExpenseId: Long? = null,
) {
  val metersPerKg: Double get() = if (weightKg > 0.0) initialMeters / weightKg else 0.0
  val currentWeightKg: Double get() = if (remainingWeightKg > 0.0) remainingWeightKg else (if (metersPerKg > 0.0) remainingMeters / metersPerKg else 0.0)
  val consumedMeters: Double get() = (initialMeters - remainingMeters).coerceAtLeast(0.0)
  val consumedWeightKg: Double get() = (weightKg - currentWeightKg).coerceAtLeast(0.0)
  val totalBaseCost: Long get() = if (buyPricePerKg > 0L && weightKg > 0.0) (weightKg * buyPricePerKg).toLong() else (initialMeters * buyPricePerMeter).toLong()
  val actualCost: Long get() = totalBaseCost + allocatedShippingCost + otherDirectCost
  val actualCostPerMeter: Long get() = if (initialMeters > 0.0) (actualCost / initialMeters).toLong() else buyPricePerMeter
  val actualCostPerKg: Long get() = if (weightKg > 0.0) (actualCost / weightKg).toLong() else buyPricePerKg
  val totalCostWithShipping: Long get() = actualCost
  val effectiveCostPerMeter: Long get() = actualCostPerMeter
  val consumptionPercent: Int get() = if (initialMeters > 0.0) (((initialMeters - remainingMeters) / initialMeters) * 100).toInt().coerceIn(0, 100) else 0
  val isFinished: Boolean get() = remainingMeters <= 0.5
  val dualStockDisplay: String get() = "${String.format(java.util.Locale.US, "%.2f", currentWeightKg)} KG / ${String.format(java.util.Locale.US, "%.2f", remainingMeters)} M"
}

/**
 * 2. Multi-consumption tracking for fabric rolls (مصرف‌های چندگانه از یک طاقه)
 * Allows a single roll to be consumed across multiple products/models with inventory protection
 */
@Entity(tableName = "roll_usages")
data class RollUsageEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val rollId: Long,
  val rollCode: String,
  val productionId: Long = 0L,
  val cuttingId: Long = 0L,
  val modelCode: String,
  val modelName: String,
  val metersUsed: Double,
  val weightKgUsed: Double = 0.0,
  val usageDate: String,
  val usageTimestamp: Long = 0L,
  val allocatedFabricCost: Long = 0L, // هزینه پارچه منتقل‌شده به بهای تمام‌شده محصول
  val allocatedShippingCost: Long = 0L, // هزینه باربری منتقل‌شده به بهای تمام‌شده محصول
  val note: String = "",
)

/**
 * 3. Accessory purchase history with unit, price history, and meters-per-kg conversions
 */
@Entity(tableName = "accessory_purchases")
data class AccessoryPurchaseEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val accessoryId: Long = 0L,
  val accessoryCode: String,
  val accessoryName: String,
  val purchaseDate: String,
  val purchaseTimestamp: Long = 0L,
  val quantity: Double,
  val unit: String, // "عدد", "متر", "یارد", "کیلوگرم", "گرم"
  val unitCostPrice: Long, // قیمت هر واحد (Cost)
  val totalCostPrice: Long, // مبلغ کل
  val supplierName: String = "",
  val allocatedShippingCost: Long = 0L,
  val metersPerKg: Double = 0.0, // اگر خرید به کیلوگرم باشد و متراژ در کیلو معلوم باشد
  val pricePerMeter: Long = 0L, // Price Per Meter = Price Per KG / Meter Per KG
  val shippingExpenseId: Long? = null,
  val note: String = "",
)

/**
 * 4. Shipping freight expense entity (بارنامه و هزینه باربری)
 * پشتیبانی از شماره بارنامه، تاریخ ثبت و تحویل، شرکت باربری و روش‌های متنوع تخصیص هزینه
 */
@Entity(tableName = "shipping_expenses")
data class ShippingExpenseEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val trackingNumber: String = "", // شماره بارنامه / کد رهگیری
  val title: String, // عنوان محموله مثلا "باربری طاقه‌های دورس از نساجی تابان کاشان"
  val date: String, // تاریخ ثبت
  val deliveryDate: String = "", // تاریخ تحویل باربری
  val timestamp: Long = 0L,
  val totalAmount: Long, // مبلغ کل باربری (تومان)
  val inboundType: String = "طاقه پارچه", // "طاقه پارچه", "ملزومات", "ترکیبی"
  val itemCount: Int = 0, // تعداد طاقه یا قلم
  val totalWeightKg: Double = 0.0, // وزن کل به کیلوگرم
  val totalQuantity: Double = 0.0, // تعداد یا متراژ کل
  val unit: String = "طاقه", // "طاقه", "کیلوگرم", "متر", "عدد", "یارد", "گرم"
  val allocationMethod: String = "BY_PURCHASE_VALUE", // "BY_PURCHASE_VALUE", "BY_WEIGHT", "BY_VOLUME", "EQUAL", "MANUAL"
  val costPerUnit: Long = 0L, // هزینه سرشکن شده هر واحد
  val carrierName: String = "", // شرکت باربری یا راننده
  val shippingCompanyId: Long? = null, // پیوند به شرکت باربری
  val shippingCompanyContact: String = "",
  val status: String = "ثبت شده",
  val notes: String = "",
)

/**
 * Coordinated 3 status states for Orders and Productions
 * 1. سفارش داده شده
 * 2. در حال دوخت
 * 3. آماده ارسال / تکمیل موجودی
 */
object SaleOrderStatus {
  const val ORDER_PLACED = "سفارش داده شده"
  const val IN_SEWING = "در حال دوخت"
  const val READY_FOR_SHIPPING = "آماده ارسال / تکمیل موجودی"

  val ALL = listOf(ORDER_PLACED, IN_SEWING, READY_FOR_SHIPPING)
}

/**
 * 5. Order status history entity (تاریخچه تغییر وضعیت سفارش)
 * Tracks Old Status, New Status, Date, Time, Timestamp
 */
@Entity(tableName = "order_status_history")
data class OrderStatusHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val orderId: Long,
  val orderNumber: String,
  val oldStatus: String,
  val newStatus: String,
  val date: String,
  val time: String,
  val timestamp: Long = 0L,
  val note: String = "",
)

/**
 * Scope for Fixed Costs
 * ALL_PRODUCTS, SELECTED_PRODUCTS, PRODUCT_CATEGORY, SPECIFIC_PRODUCTION
 */
enum class FixedCostScope(val title: String, val code: String) {
  ALL_PRODUCTS("همه محصولات", "ALL_PRODUCTS"),
  SELECTED_PRODUCTS("محصولات انتخابی", "SELECTED_PRODUCTS"),
  PRODUCT_CATEGORY("دسته‌بندی محصول", "PRODUCT_CATEGORY"),
  SPECIFIC_PRODUCTION("تولید خاص", "SPECIFIC_PRODUCTION");

  companion object {
    fun fromCode(code: String): FixedCostScope {
      return values().firstOrNull { it.code.equals(code, ignoreCase = true) || it.name.equals(code, ignoreCase = true) } ?: ALL_PRODUCTS
    }
  }
}

/**
 * 6. Fixed Costs entity (هزینه‌های ثابت کارگاه با تفکیک دقیق Scope)
 */
@Entity(tableName = "fixed_costs")
data class FixedCostEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String, // e.g. "اجاره سالن دوخت", "استهلاک ماشین‌آلات", "طراحی الگو و شابلون"
  val amount: Long, // مبلغ کل هزینه (تومان)
  val scope: String = FixedCostScope.ALL_PRODUCTS.code, // ALL_PRODUCTS, SELECTED_PRODUCTS, PRODUCT_CATEGORY, SPECIFIC_PRODUCTION
  val targetCategory: String = "", // e.g. "محصولات آماده", "هودی و سویشرت"
  val targetProductCodes: String = "", // e.g. "HD-204,SW-102"
  val targetProductionId: Long = 0L, // شناسه رکورد تولید
  val date: String,
  val timestamp: Long = 0L,
  val notes: String = "",
)

/**
 * 7. Production Consumable entity (مصرف ملزومات در ثبت تولید و برش با محاسبه هزینه واقعی از سابقه خرید)
 */
@Entity(tableName = "production_consumables")
data class ProductionConsumableEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productionId: Long = 0L,
  val cuttingId: Long = 0L, // پیوند به پارت برشکاری در صورت وجود
  val materialId: Long = 0L, // پیوند به کالای ملزومات در انبار
  val accessoryCode: String,
  val accessoryName: String,
  val quantityUsed: Double,
  val unit: String = "عدد", // عدد, متر, یارد, کیلوگرم, گرم
  val unitCostPrice: Long, // قیمت واقعی محاسبه‌شده از سابقه خرید
  val totalCost: Long, // quantityUsed * unitCostPrice
  val date: String,
)

/**
 * Item specification for multi-product registration from a single roll
 */
data class MultiProductReadyItem(
  val id: String = java.util.UUID.randomUUID().toString(),
  val modelName: String,
  val modelCode: String,
  val readyQuantity: Int,
  val metersUsed: Double,
  val sewingWagePerItem: Long = 85000L, // دستمزد خیاط‌کار
  val salePricePerItem: Long = 680000L,
  val unitWeightGrams: Double = 500.0,
  val accessoriesCostPerItem: Long = 32000L,
) {
  val metersPerItem: Double get() = if (readyQuantity > 0) metersUsed / readyQuantity else 0.0
}

/**
 * Item specification for consumable entered in production
 */
data class ProductionConsumableInputItem(
  val id: String = java.util.UUID.randomUUID().toString(),
  val accessoryCode: String,
  val accessoryName: String,
  val quantityUsed: Double,
  val unit: String,
  val unitCostPrice: Long,
) {
  val totalCost: Long get() = (quantityUsed * unitCostPrice).toLong()
}

/**
 * Detailed Product Cost Breakdown
 * Material + Consumables + Allocated Freight + Tailor Cost + Allocated Fixed Costs + Other
 */
data class DetailedProductCostBreakdown(
  val materialCost: Long,          // هزینه پارچه مصرفی (متراژ × نرخ هر متر)
  val consumablesCost: Long,       // هزینه ملزومات مصرفی (دکمه، زیپ، نخ، ...)
  val allocatedFreightCost: Long,  // سهم کرایه باربری طاقه
  val tailorCost: Long,            // دستمزد خیاط‌کار
  val allocatedFixedCosts: Long,   // هزینه‌های ثابت تخصیص یافته طبق Scope
  val otherCosts: Long = 0L,       // سایر هزینه‌های مستقیم
) {
  val totalUnitCost: Long get() = materialCost + consumablesCost + allocatedFreightCost + tailorCost + allocatedFixedCosts + otherCosts

  fun calculateGrossProfit(unitSalePrice: Long): Long = (unitSalePrice - totalUnitCost).coerceAtLeast(0L)
  fun calculateGrossProfitMargin(unitSalePrice: Long): Double =
    if (unitSalePrice > 0L) (calculateGrossProfit(unitSalePrice).toDouble() / unitSalePrice.toDouble()) * 100.0 else 0.0
}

/**
 * 8. Product Category Master Data
 */
@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val code: String,
  val name: String,
  val isActive: Boolean = true,
  val createdDate: String = "",
)

/**
 * 9. Product Master Data (کالای تولیدی با بهای تمام‌شده داینامیک)
 */
@Entity(tableName = "products")
data class ProductEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val code: String, // مثلا SL101
  val name: String, // مثلا شلوار اسلش پنبه پاییزه
  val categoryId: Long = 0L,
  val categoryName: String = "",
  val description: String = "",
  val isActive: Boolean = true,
  val suggestedSellingPrice: Long = 0L, // قیمت فروش محاسبه‌شده داینامیک
  val manualOverridePrice: Long? = null, // قیمت فروش دستی در صورت نیاز
  val isManualPrice: Boolean = false,
  val currentCostPrice: Long = 0L, // بهای تمام‌شده جاری محصول (از BOM + اجرت + سرشکن)
  val sewingWage: Long = 85000L, // اجرت دوخت خیاط
  val allocatedFreightCost: Long = 15000L, // سهم کرایه حمل باربری به ازای هر عدد
  val overheadCost: Long = 20000L, // هزینه سربار ثابت کارگاه به ازای هر عدد
  val targetProfitPercent: Double = 35.0, // حاشیه سود هدف
  val profitCalculationType: String = "MARKUP", // "MARKUP" (درصد روی بهای تمام شده) یا "MARGIN"
  val lastPriceUpdateTimestamp: Long = 0L,
  val lastPriceUpdateDate: String = "",
) {
  val effectiveSellingPrice: Long get() = if (isManualPrice && manualOverridePrice != null && manualOverridePrice > 0L) manualOverridePrice else suggestedSellingPrice
  val grossProfit: Long get() = (effectiveSellingPrice - currentCostPrice).coerceAtLeast(0L)
  val grossProfitMarginPercent: Double get() = if (effectiveSellingPrice > 0L) (grossProfit.toDouble() / effectiveSellingPrice.toDouble()) * 100.0 else 0.0
}

/**
 * 10. Color Master Data
 */
@Entity(tableName = "colors")
data class ColorEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String, // مشکی، سرمه‌ای، طوسی ملانژ، زیتونی، سفید
  val colorHex: String = "#1E293B",
  val isActive: Boolean = true,
)

/**
 * 11. Size Master Data
 */
@Entity(tableName = "sizes")
data class SizeEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String, // M, L, XL, 2XL, 3XL, فری سایز
  val sortOrder: Int = 1,
  val isActive: Boolean = true,
)

/**
 * 12. Product Variant Stock (ترکیب کالا + رنگ + سایز با تفکیک کل، رزرو و آزاد)
 */
@Entity(tableName = "product_variants")
data class ProductVariantEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productId: Long,
  val productCode: String,
  val productName: String,
  val colorId: Long = 0L,
  val colorName: String = "مشکی",
  val sizeId: Long = 0L,
  val sizeName: String = "L",
  val onHandQuantity: Int = 0, // موجودی کل فیزیکی انبار
  val reservedQuantity: Int = 0, // موجودی رزرو شده برای سفارشات جاری
  val currentUnitCost: Long = 0L, // بهای تمام‌شده واحد
  val currentUnitSalePrice: Long = 0L, // قیمت فروش واحد
) {
  val availableQuantity: Int get() = (onHandQuantity - reservedQuantity).coerceAtLeast(0) // موجودی آزاد و قابل فروش
  val totalOnHandValue: Long get() = onHandQuantity * currentUnitSalePrice
  val freeInventoryValue: Long get() = availableQuantity * currentUnitSalePrice
  val freeInventoryCostValue: Long get() = availableQuantity * currentUnitCost
}

/**
 * 13. Material Master Data (مواد اولیه و ملزومات با آخرین نرخ بازار)
 */
@Entity(tableName = "materials")
data class MaterialEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val code: String, // MAT-101
  val name: String, // پارچه کتان بنگال، کش ۴ سانت، زیپ دنده‌فلزی، نخ پنبه
  val category: String, // پارچه، کش، زیپ، دکمه، نخ، لیبل، پلاستیک، چاپ، خرج‌کار
  val unit: String = "کیلوگرم", // کیلوگرم، متر، عدد، دوک، یارد
  val currentPrice: Long = 0L, // آخرین قیمت ثبت‌شده (مبنای محاسبه بهای جاری محصولات)
  val lastPurchasePrice: Long = 0L, // آخرین قیمت خرید واقعی
  val lastPriceSource: String = "MARKET_UPDATE", // MARKET_UPDATE, PURCHASE, MANUAL
  val lastPriceChangeDate: String = "",
  val lastPriceChangeTimestamp: Long = 0L,
  val stockQuantity: Double = 0.0,
  val minStockThreshold: Double = 10.0,
  val metersPerKg: Double = 0.0, // اگر به کیلو خریده شود و به متر مصرف شود
  val supplierName: String = "",
  val isActive: Boolean = true,
) {
  val isLowStock: Boolean get() = stockQuantity <= minStockThreshold
}

/**
 * 14. Unit Definition Master Data
 */
@Entity(tableName = "material_units")
data class MaterialUnitEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val code: String,
  val name: String,
  val isBaseUnit: Boolean = true,
)

/**
 * 15. Product BOM / Production Formula Entry (فرمول تولید محصول)
 */
@Entity(tableName = "product_boms")
data class ProductBOMEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productId: Long,
  val productCode: String,
  val materialId: Long,
  val materialName: String,
  val materialCategory: String = "پارچه",
  val unit: String = "متر",
  val standardQuantity: Double, // متراژ یا تعداد استاندارد مصرف برای یک عدد محصول
  val unitRate: Long = 0L, // آخرین نرخ واحد ثبت‌شده این ماده
  val note: String = "",
) {
  val totalLineCost: Long get() = (standardQuantity * unitRate).toLong()
}

/**
 * 16. Material Price History (تاریخچه تغییرات قیمت مواد و تغییر قیمت بازار)
 */
@Entity(tableName = "material_price_history")
data class MaterialPriceHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val materialId: Long,
  val materialName: String,
  val oldPrice: Long,
  val newPrice: Long,
  val date: String,
  val timestamp: Long = 0L,
  val changeAmount: Long = 0L, // newPrice - oldPrice
  val changePercent: Double = 0.0,
  val reason: String = "تغییر قیمت بازار",
  val source: String = "MARKET_UPDATE", // "MARKET_UPDATE", "PURCHASE", "MANUAL"
  val supplierName: String = "",
  val notes: String = "",
  val recordedBy: String = "مدیر کارگاه",
)

/**
 * 17. Product Price & Cost History (تاریخچه بهای تمام‌شده و قیمت فروش محصول)
 */
@Entity(tableName = "product_price_history")
data class ProductPriceHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productId: Long,
  val productCode: String,
  val productName: String,
  val oldCostPrice: Long,
  val newCostPrice: Long,
  val oldSalePrice: Long,
  val newSalePrice: Long,
  val date: String,
  val timestamp: Long = 0L,
  val reason: String = "",
  val triggeringMaterialId: Long? = null,
  val triggeringMaterialName: String = "",
  val costChangeAmount: Long = 0L,
  val costChangePercent: Double = 0.0,
  val notes: String = "",
  val breakdownJson: String = "", // خلاصه مؤلفه‌ها: مثلا پارچه +80,000، کش +15,000
)

/**
 * 18. Predefined & Custom Reasons for Price Changes
 */
@Entity(tableName = "price_change_reasons")
data class PriceChangeReasonEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val isDefault: Boolean = false,
)

/**
 * 19. Inventory Ledger (دفتر کل انبار و تراکنش‌های موجودی فیزیکی)
 */
@Entity(tableName = "inventory_ledger")
data class InventoryLedgerEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: Long = 0L,
  val date: String,
  val itemType: String, // "MATERIAL", "FABRIC_ROLL", "FINISHED_GOOD"
  val itemId: Long,
  val itemCode: String,
  val itemName: String,
  val color: String = "",
  val size: String = "",
  val transactionType: String, // "INITIAL_ENTRY", "PURCHASE_INBOUND", "PRODUCTION_CONSUME", "PRODUCTION_FINISH_INBOUND", "SALE_RESERVATION", "SALE_SHIPMENT", "RETURN", "ADJUSTMENT"
  val quantityChange: Double, // مثبت برای ورود، منفی برای خروج
  val balanceAfter: Double, // مانده پس از تراکنش
  val unit: String = "عدد",
  val unitPriceAtTime: Long = 0L, // بهای واحد در زمان انجام تراکنش (Snapshot)
  val relatedDocumentNumber: String = "", // کد سفارش، کد طاقه، شماره فاکتور
  val notes: String = "",
  val operator: String = "مدیر کارگاه",
)

/**
 * 20. Purchase Orders (سفارشات خرید از تأمین‌کننده)
 */
@Entity(tableName = "purchase_orders")
data class PurchaseOrderEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val orderNumber: String,
  val supplierId: Long,
  val supplierName: String,
  val orderDate: String,
  val expectedDeliveryDate: String = "",
  val totalAmount: Long = 0L,
  val shippingAmount: Long = 0L,
  val paidAmount: Long = 0L,
  val status: String = "ثبت شده", // "ثبت شده", "تحویل شده", "تسویه شده", "لغو شده"
  val notes: String = "",
)

/**
 * 21. Purchase Order Items
 */
@Entity(tableName = "purchase_items")
data class PurchaseItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val purchaseOrderId: Long,
  val materialId: Long,
  val materialName: String,
  val orderedQuantity: Double,
  val deliveredQuantity: Double = 0.0,
  val unit: String = "کیلوگرم",
  val unitPrice: Long = 0L,
  val totalPrice: Long = 0L,
  val allocatedShipping: Long = 0L,
)

/**
 * 22. Sales Channels (کانال‌های فروش)
 */
@Entity(tableName = "sales_channels")
data class SalesChannelEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String, // "حضوری", "اینستاگرام", "روبیکا", "تلگرام", "واتساپ", "سایر"
  val code: String,
  val isActive: Boolean = true,
)

/**
 * Summary impact report returned after recording a Market Price Update
 */
data class PriceImpactReport(
  val materialId: Long,
  val materialName: String,
  val oldPrice: Long,
  val newPrice: Long,
  val priceChangePercent: Double,
  val affectedProducts: List<ProductPriceImpactItem>,
  val totalFreeInventoryCount: Int,
  val totalFreeInventoryOldValue: Long,
  val totalFreeInventoryNewValue: Long,
  val valueDifference: Long,
)

data class ProductPriceImpactItem(
  val productId: Long,
  val productCode: String,
  val productName: String,
  val oldCost: Long,
  val newCost: Long,
  val costDiff: Long,
  val oldSalePrice: Long,
  val newSalePrice: Long,
  val salePriceDiff: Long,
  val freeStockQuantity: Int,
  val inventoryValueDiff: Long,
  val contributingMaterialQuantity: Double,
)

/**
 * 23. Customer Payment Transaction (تراکنش دریافت وجه از مشتری)
 */
@Entity(tableName = "customer_payments")
data class CustomerPaymentEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val customerId: Long,
  val customerName: String,
  val orderId: Long? = null,
  val orderNumber: String = "",
  val amount: Long,
  val date: String,
  val timestamp: Long = 0L,
  val paymentMethod: String = "کارت به کارت", // "کارت به کارت", "نقدی", "حواله پایا/ساتنا", "چک صیادی", "درگاه آنلاین", "سایر"
  val referenceNumber: String = "",
  val notes: String = "",
  val recordedBy: String = "مدیر مالی",
)

/**
 * 24. Supplier Payment Transaction (تراکنش پرداخت وجه به تأمین‌کننده)
 */
@Entity(tableName = "supplier_payments")
data class SupplierPaymentEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val supplierId: Long,
  val supplierName: String,
  val purchaseOrderId: Long? = null,
  val orderNumber: String = "",
  val amount: Long,
  val date: String,
  val timestamp: Long = 0L,
  val paymentMethod: String = "حواله بانکی",
  val referenceNumber: String = "",
  val notes: String = "",
  val recordedBy: String = "مدیر خرید",
)

/**
 * 25. Shipping Rate History (تاریخچه نرخ پایه باربری)
 */
@Entity(tableName = "shipping_rate_history")
data class ShippingRateHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val oldRate: Long,
  val newRate: Long,
  val changeAmount: Long,
  val changePercent: Double,
  val date: String,
  val timestamp: Long = 0L,
  val reason: String = "افزایش نرخ باربری سراسری",
  val source: String = "SETTINGS_UPDATE",
  val notes: String = "",
  val recordedBy: String = "مدیر کارگاه",
)

/**
 * 26. Audit Log (لاگ سیستمی و مالی کلیه عملیات‌های کلیدی)
 */
@Entity(tableName = "audit_logs")
data class AuditLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: Long = 0L,
  val date: String,
  val entityName: String, // "MATERIAL", "PRODUCT", "SALE_ORDER", "PURCHASE_ORDER", "PAYMENT", "INVENTORY"
  val entityId: Long = 0L,
  val action: String, // "CREATE", "UPDATE", "PRICE_CHANGE", "PURCHASE", "PRODUCTION", "RESERVATION", "SHIPMENT", "SALE", "RETURN", "PAYMENT", "ADJUSTMENT"
  val oldValue: String = "",
  val newValue: String = "",
  val reason: String = "",
  val recordedBy: String = "کاربر سیستم",
)

/**
 * 27. Inventory Physical Adjustment (تعدیل دستی موجودی فیزیکی)
 */
@Entity(tableName = "inventory_adjustments")
data class InventoryAdjustmentEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: Long = 0L,
  val date: String,
  val itemType: String, // "MATERIAL", "FABRIC_ROLL", "FINISHED_GOOD"
  val itemId: Long,
  val itemCode: String,
  val itemName: String,
  val color: String = "",
  val size: String = "",
  val adjustmentType: String, // "INCREASE", "DECREASE"
  val quantity: Double,
  val reason: String = "شمارش انبارگردانی",
  val notes: String = "",
  val operator: String = "انباردار",
)

/**
 * Preview model for Market Price Update before commit
 */
data class PriceUpdatePreviewData(
  val materialId: Long,
  val materialName: String,
  val currentPrice: Long,
  val newPrice: Long,
  val priceDiff: Long,
  val priceDiffPercent: Double,
  val affectedProductCount: Int,
  val affectedFreeStockTotal: Int,
  val reservedStockCountExcluded: Int,
  val estimatedFreeStockValueImpact: Long,
  val affectedProductsSummary: List<ProductPriceImpactItem>
)

/**
 * 28. System User Role & Permissions (نقش‌های کاربری و سطوح دسترسی سازمانی)
 */
enum class UserRole(
  val title: String,
  val canEditPricing: Boolean,
  val canAccessAccounting: Boolean,
  val canPerformStockAdjustment: Boolean,
  val canModifyBOM: Boolean,
  val canManageSystemSettings: Boolean
) {
  ADMIN(
    title = "مدیر ارشد کارخانه",
    canEditPricing = true,
    canAccessAccounting = true,
    canPerformStockAdjustment = true,
    canModifyBOM = true,
    canManageSystemSettings = true
  ),
  ACCOUNTANT(
    title = "حسابدار مالی",
    canEditPricing = true,
    canAccessAccounting = true,
    canPerformStockAdjustment = false,
    canModifyBOM = false,
    canManageSystemSettings = false
  ),
  WAREHOUSE_KEEPER(
    title = "انباردار",
    canEditPricing = false,
    canAccessAccounting = false,
    canPerformStockAdjustment = true,
    canModifyBOM = false,
    canManageSystemSettings = false
  ),
  PRODUCTION_MANAGER(
    title = "مدیر تولید و برش",
    canEditPricing = false,
    canAccessAccounting = false,
    canPerformStockAdjustment = false,
    canModifyBOM = true,
    canManageSystemSettings = false
  );

  fun validateAccess(action: String): Pair<Boolean, String> {
    return when (action) {
      "EDIT_PRICING" -> if (canEditPricing) Pair(true, "مجاز") else Pair(false, "کاربر با نقش $title دسترسی به تغییر قیمت یا سود ندارد.")
      "ACCESS_ACCOUNTING" -> if (canAccessAccounting) Pair(true, "مجاز") else Pair(false, "کاربر با نقش $title دسترسی به بخش حسابداری ندارد.")
      "STOCK_ADJUSTMENT" -> if (canPerformStockAdjustment) Pair(true, "مجاز") else Pair(false, "کاربر با نقش $title دسترسی به تعدیل انبارگردانی ندارد.")
      "MODIFY_BOM" -> if (canModifyBOM) Pair(true, "مجاز") else Pair(false, "کاربر با نقش $title دسترسی به ویرایش فرمول ساخت ندارد.")
      "SYSTEM_SETTINGS" -> if (canManageSystemSettings) Pair(true, "مجاز") else Pair(false, "تنها مدیر ارشد مجاز به تغییر تنظیمات پایه سیستم است.")
      else -> Pair(true, "مجاز")
    }
  }
}




