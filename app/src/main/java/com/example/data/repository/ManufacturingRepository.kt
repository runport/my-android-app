package com.example.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.first
import com.example.data.database.AppDatabase
import com.example.data.model.AccessoryPurchaseEntity
import com.example.data.model.AlertItem
import com.example.data.model.AlertType
import com.example.data.model.CustomerEntity
import com.example.data.model.CuttingEntity
import com.example.data.model.FabricEntity
import com.example.data.model.FabricRollEntity
import com.example.data.model.FixedCostEntity
import com.example.data.model.InventoryEntity
import com.example.data.model.InventoryLedgerEntity
import com.example.data.model.ModelStandardEntity
import com.example.data.model.MultiProductReadyItem
import com.example.data.model.OrderStatusHistoryEntity
import com.example.data.model.ProductionConsumableEntity
import com.example.data.model.ProductionConsumableInputItem
import com.example.data.model.ProductionEntity
import com.example.data.model.RollUsageEntity
import com.example.data.model.SaleOrderEntity
import com.example.data.model.SaleOrderStatus
import com.example.data.model.ShippingAllocationMethod
import com.example.data.model.ShippingExpenseEntity
import com.example.data.model.SupplierEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ColorEntity
import com.example.data.model.SizeEntity
import com.example.data.model.ProductVariantEntity
import com.example.data.model.MaterialEntity
import com.example.data.model.MaterialUnitEntity
import com.example.data.model.ProductBOMEntity
import com.example.data.model.MaterialPriceHistoryEntity
import com.example.data.model.FabricPriceHistoryEntity
import com.example.data.model.ProductPriceHistoryEntity
import com.example.data.model.PriceChangeReasonEntity
import com.example.data.model.PurchaseOrderEntity
import com.example.data.model.PurchaseItemEntity
import com.example.data.model.SalesChannelEntity
import com.example.data.model.CustomerPaymentEntity
import com.example.data.model.SupplierPaymentEntity
import com.example.data.model.ShippingRateHistoryEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.InventoryAdjustmentEntity
import com.example.data.model.FabricCategoryEntity
import com.example.data.model.ShippingCompanyEntity
import com.example.data.model.WaybillItemEntity
import com.example.data.model.BaseCostConfigEntity
import com.example.data.service.FinancialCalculationService
import com.example.util.PersianDateHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class ManufacturingRepository(private val database: AppDatabase) {

  val allFabrics: Flow<List<FabricEntity>> = database.fabricDao().getAllFabrics()
  val allFabricRolls: Flow<List<FabricRollEntity>> = database.fabricRollDao().getAllRolls()
  val availableFabricRolls: Flow<List<FabricRollEntity>> = database.fabricRollDao().getAvailableRolls()
  val allRollUsages: Flow<List<RollUsageEntity>> = database.rollUsageDao().getAllUsages()
  val allAccessoryPurchases: Flow<List<AccessoryPurchaseEntity>> = database.accessoryPurchaseDao().getAllPurchases()
  val allShippingExpenses: Flow<List<ShippingExpenseEntity>> = database.shippingExpenseDao().getAllExpenses()
  val allOrderStatusHistory: Flow<List<OrderStatusHistoryEntity>> = database.orderStatusHistoryDao().getAllHistory()
  val allFixedCosts: Flow<List<FixedCostEntity>> = database.fixedCostDao().getAllFixedCosts()
  val allProductionConsumables: Flow<List<ProductionConsumableEntity>> = database.productionConsumableDao().getAllConsumables()
  val allCuttings: Flow<List<CuttingEntity>> = database.cuttingDao().getAllCuttings()
  val inProgressCuttings: Flow<List<CuttingEntity>> = database.cuttingDao().getInProgressCuttings()
  val completedCuttings: Flow<List<CuttingEntity>> = database.cuttingDao().getCompletedCuttings()
  val allProductions: Flow<List<ProductionEntity>> = database.productionDao().getAllProductions()
  val allInventory: Flow<List<InventoryEntity>> = database.inventoryDao().getAllInventory()
  val allSalesOrders: Flow<List<SaleOrderEntity>> = database.saleOrderDao().getAllSalesOrders()
  val allCustomers: Flow<List<CustomerEntity>> = database.customerDao().getAllCustomers()
  val allSuppliers: Flow<List<SupplierEntity>> = database.supplierDao().getAllSuppliers()
  val allStandards: Flow<List<ModelStandardEntity>> = database.modelStandardDao().getAllStandards()
  val factorySettings: Flow<com.example.data.model.FactorySettingsEntity?> = database.factorySettingsDao().getSettings()

  // Master Data & Extended Enterprise Flows
  val allCategories: Flow<List<CategoryEntity>> = database.categoryDao().getAllCategories()
  val allProducts: Flow<List<ProductEntity>> = database.productDao().getAllProducts()
  val allColors: Flow<List<ColorEntity>> = database.colorDao().getAllColors()
  val allSizes: Flow<List<SizeEntity>> = database.sizeDao().getAllSizes()
  val allVariants: Flow<List<ProductVariantEntity>> = database.productVariantDao().getAllVariants()
  val allMaterials: Flow<List<MaterialEntity>> = database.materialDao().getAllMaterials()
  val allMaterialUnits: Flow<List<MaterialUnitEntity>> = database.materialUnitDao().getAllUnits()
  val allBOMs: Flow<List<ProductBOMEntity>> = database.productBOMDao().getAllBOMs()
  val allMaterialPriceHistory: Flow<List<MaterialPriceHistoryEntity>> = database.materialPriceHistoryDao().getAllHistory()
  val allProductPriceHistory: Flow<List<ProductPriceHistoryEntity>> = database.productPriceHistoryDao().getAllHistory()
  val allPriceChangeReasons: Flow<List<PriceChangeReasonEntity>> = database.priceChangeReasonDao().getAllReasons()
  val allInventoryLedger: Flow<List<InventoryLedgerEntity>> = database.inventoryLedgerDao().getAllTransactions()
  val allPurchaseOrders: Flow<List<PurchaseOrderEntity>> = database.purchaseOrderDao().getAllOrders()
  val allSalesChannels: Flow<List<SalesChannelEntity>> = database.salesChannelDao().getAllChannels()
  val allCustomerPayments: Flow<List<CustomerPaymentEntity>> = database.customerPaymentDao().getAllPayments()
  val allSupplierPayments: Flow<List<SupplierPaymentEntity>> = database.supplierPaymentDao().getAllPayments()
  val allShippingRateHistory: Flow<List<ShippingRateHistoryEntity>> = database.shippingRateHistoryDao().getAllHistory()
  val allAuditLogs: Flow<List<AuditLogEntity>> = database.auditLogDao().getAllLogs()
  val allInventoryAdjustments: Flow<List<InventoryAdjustmentEntity>> = database.inventoryAdjustmentDao().getAllAdjustments()
  val allFabricCategories: Flow<List<FabricCategoryEntity>> = database.fabricCategoryDao().getAllCategories()
  val activeFabricCategories: Flow<List<FabricCategoryEntity>> = database.fabricCategoryDao().getActiveCategories()
  val allShippingCompanies: Flow<List<ShippingCompanyEntity>> = database.shippingCompanyDao().getAllCompanies()
  val activeShippingCompanies: Flow<List<ShippingCompanyEntity>> = database.shippingCompanyDao().getActiveCompanies()
  val allBaseCostConfigs: Flow<List<BaseCostConfigEntity>> = database.baseCostConfigDao().getAllConfigs()
  val activeBaseCostConfigs: Flow<List<BaseCostConfigEntity>> = database.baseCostConfigDao().getActiveConfigs()

  // Dynamic Executive Alerts using custom configurable thresholds
  val alertsFlow: Flow<List<AlertItem>> = combine(
    database.fabricDao().getAllFabrics(),
    database.inventoryDao().getAllInventory(),
    database.saleOrderDao().getAllSalesOrders(),
    database.cuttingDao().getAllCuttings(),
    database.factorySettingsDao().getSettings()
  ) { fabrics, inventory, orders, cuttings, settingsOrNull ->
    val settings = settingsOrNull ?: com.example.data.model.FactorySettingsEntity()
    val alertList = mutableListOf<AlertItem>()

    // 1. Fabric low stock check (بررسی طاقه پارچه بر اساس تعداد طاقه یا وزن)
    val totalRolls = fabrics.sumOf { it.rollCount }
    val totalFabricWeight = fabrics.sumOf { it.totalWeightKg }
    if (totalRolls < settings.minFabricRollsThreshold) {
      alertList.add(
        AlertItem(
          id = "fabric_rolls_low",
          title = "هشدار کمبود طاقه پارچه",
          description = "موجودی: $totalRolls طاقه (کمتر از حد مجاز ${settings.minFabricRollsThreshold} طاقه)",
          type = AlertType.DANGER,
          tag = "انبار پارچه"
        )
      )
    }
    if (totalFabricWeight < settings.minFabricWeightKgThreshold) {
      alertList.add(
        AlertItem(
          id = "fabric_weight_low",
          title = "کسری وزن کل پارچه",
          description = "وزن موجودی: ${totalFabricWeight.toInt()} کیلوگرم (حد هشدار: ${settings.minFabricWeightKgThreshold.toInt()} کیلوگرم)",
          type = AlertType.WARNING,
          tag = "انبار پارچه"
        )
      )
    }

    // Individual fabric roll alert
    fabrics.filter { it.rollCount <= 8 || it.totalMeters < 500.0 }.forEach { fabric ->
      alertList.add(
        AlertItem(
          id = "fabric_${fabric.id}",
          title = "کمبود طاقه پارچه ${fabric.code}",
          description = "${fabric.name}: ${fabric.rollCount} طاقه (${fabric.totalWeightKg.toInt()} کیلوگرم)",
          type = AlertType.WARNING,
          tag = "انبار پارچه"
        )
      )
    }

    // 2. Ready goods check (میزان تعداد کار آماده بر اساس تعداد)
    val readyGoods = inventory.filter { it.category == "محصولات آماده" }
    val totalReadyCount = readyGoods.sumOf { it.readyForShipment }
    if (totalReadyCount < settings.minReadyGoodsCountThreshold) {
      alertList.add(
        AlertItem(
          id = "ready_goods_shortage",
          title = "هشدار کاهش کار آماده تحویل",
          description = "موجودی آماده: $totalReadyCount عدد (کمتر از حد مجاز ${settings.minReadyGoodsCountThreshold} عدد)",
          type = AlertType.DANGER,
          tag = "محصولات نهایی"
        )
      )
    }

    // 3. Sewing accessories check (میزان ملزومات خیاطی بر اساس وزن کیلوگرم)
    val accessories = inventory.filter { it.category == "ملزومات" }
    val totalAccWeightKg = accessories.sumOf { (it.totalStock * it.unitWeightGrams) / 1000.0 }
    if (totalAccWeightKg < settings.minAccessoriesWeightKgThreshold) {
      alertList.add(
        AlertItem(
          id = "accessories_shortage",
          title = "هشدار کسری ملزومات و خرج‌کار",
          description = "وزن کل ملزومات: ${"%.1f".format(totalAccWeightKg)} کیلوگرم (کمتر از حد مجاز ${settings.minAccessoriesWeightKgThreshold.toInt()} کیلوگرم)",
          type = AlertType.WARNING,
          tag = "انبار ملزومات"
        )
      )
    }

    // 4. Delayed sales orders
    orders.filter { it.isDelayed }.forEach { order ->
      alertList.add(
        AlertItem(
          id = "order_${order.id}",
          title = "سفارش ${order.orderNumber} عقب افتاده",
          description = "${order.delayDays} روز تأخیر در آماده‌سازی مشتری ${order.customerName}",
          type = AlertType.DANGER,
          tag = "واحد فروش"
        )
      )
    }

    // 5. Abnormal fabric cutting consumption (کاملاً داینامیک از رکوردهای میز برش)
    cuttings.filter { it.isAbnormalConsumption }.forEach { cutting ->
      alertList.add(
        AlertItem(
          id = "abnormal_cut_${cutting.id}",
          title = "انحراف مصرف پارچه ${cutting.modelCode}",
          description = "${String.format(java.util.Locale.US, "%.1f", cutting.consumptionDeviationPercent)}٪ بیشتر از استاندارد مجاز در مدل ${cutting.modelName}",
          type = AlertType.WARNING,
          tag = "خط برش"
        )
      )
    }

    alertList
  }

  suspend fun insertSaleOrder(
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
    val orderNumber = "#${(2052..2999).random()}"
    val order = SaleOrderEntity(
      orderNumber = orderNumber,
      customerName = customerName,
      customerPhone = customerPhone,
      modelCode = modelCode,
      modelName = modelName,
      quantity = quantity,
      unitPrice = unitPrice,
      unitCost = unitCost,
      discountAmount = discountAmount,
      paidAmount = paidAmount,
      orderDate = "امروز",
      deliveryStatus = "ثبت شده"
    )
    database.saleOrderDao().insertOrder(order)

    // Check inventory and reserve stock or trigger shortage requirement automatically
    val inventoryItem = database.inventoryDao().getByCode("PRD-${modelCode.removePrefix("M")}")
    if (inventoryItem != null) {
      val newAvailable = (inventoryItem.availableForSale - quantity).coerceAtLeast(0)
      val newReserved = inventoryItem.reservedQuantity + quantity.coerceAtMost(inventoryItem.availableForSale)
      database.inventoryDao().updateItem(
        inventoryItem.copy(
          availableForSale = newAvailable,
          reservedQuantity = newReserved
        )
      )
    }
  }

  suspend fun insertProductionRecord(
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
    val record = ProductionEntity(
      modelCode = modelCode,
      modelName = modelName,
      quantity = quantity,
      fabricRollsUsed = fabricRollsUsed,
      fabricMetersUsed = fabricMetersUsed,
      totalWeightKg = totalWeightKg,
      sewingWagePerItem = sewingWagePerItem,
      fabricPricePerMeter = fabricPricePerMeter,
      accessoriesCostPerItem = accessoriesCostPerItem,
      status = "تکمیل شده",
      date = "امروز"
    )
    database.productionDao().insertProduction(record)

    // AUTOMATION PRINCIPLE: Product flows from Production directly into Inventory!
    val prodCode = "PRD-${modelCode.removePrefix("M")}"
    val existing = database.inventoryDao().getByCode(prodCode)
    if (existing != null) {
      database.inventoryDao().updateItem(
        existing.copy(
          readyForShipment = existing.readyForShipment + quantity,
          availableForSale = existing.availableForSale + quantity,
          unitWeightGrams = record.weightPerItemGrams,
          unitCostPrice = record.unitCostPrice,
          lastUpdated = "امروز، تحویل از تولید"
        )
      )
    } else {
      database.inventoryDao().insertItem(
        InventoryEntity(
          name = modelName,
          code = prodCode,
          category = "محصولات آماده",
          readyForShipment = quantity,
          reservedQuantity = 0,
          availableForSale = quantity,
          unitSalePrice = record.estimatedSalePricePerItem,
          unitCostPrice = record.unitCostPrice,
          unitWeightGrams = record.weightPerItemGrams,
          lastUpdated = "امروز، تحویل از خط تولید"
        )
      )
    }
  }

  suspend fun insertCuttingOrder(
    modelCode: String,
    modelName: String,
    fabricCode: String,
    targetQuantity: Int,
    cutQuantity: Int,
    standardMetersPerItem: Double,
    actualMetersPerItem: Double
  ) {
    val cutting = CuttingEntity(
      modelCode = modelCode,
      modelName = modelName,
      fabricCode = fabricCode,
      targetQuantity = targetQuantity,
      cutQuantity = cutQuantity,
      standardMetersPerItem = standardMetersPerItem,
      actualMetersPerItem = actualMetersPerItem,
      status = if (cutQuantity >= targetQuantity) "تکمیل شده" else "در حال برش",
      date = "امروز"
    )
    database.cuttingDao().insertCutting(cutting)
  }

  suspend fun executeMultiModelCutting(
    fabricId: Long,
    modelCuts: List<com.example.data.model.MultiCutModelItem>,
    keepRemainingInStock: Boolean
  ): Pair<Boolean, String> = database.withTransaction {
    val fabric = database.fabricDao().getById(fabricId)
      ?: return@withTransaction Pair(false, "طاقه پارچه مورد نظر در انبار یافت نشد")

    val totalConsumedMeters = modelCuts.sumOf { it.totalMetersUsed }
    if (totalConsumedMeters > fabric.totalMeters + 0.1) {
      return@withTransaction Pair(false, "مجموع مصرف مدل‌ها (${"%.1f".format(totalConsumedMeters)} متر) از موجودی کل طاقه (${"%.1f".format(fabric.totalMeters)} متر) بیشتر است")
    }

    val remainingMeters = (fabric.totalMeters - totalConsumedMeters).coerceAtLeast(0.0)
    val metersPerKg = if (fabric.totalMeters > 0 && fabric.totalWeightKg > 0) fabric.totalMeters / fabric.totalWeightKg else 3.0
    val remainingWeightKg = if (metersPerKg > 0) remainingMeters / metersPerKg else 0.0
    val remainingRolls = if (remainingMeters <= 0.5) 0 else if (fabric.rollCount > 1) {
      val avgMeterPerRoll = fabric.totalMeters / fabric.rollCount
      (remainingMeters / avgMeterPerRoll).toInt().coerceAtLeast(1)
    } else if (remainingMeters > 0) 1 else 0

    // Update fabric stock in database
    database.fabricDao().updateFabric(
      fabric.copy(
        totalMeters = remainingMeters,
        totalWeightKg = remainingWeightKg,
        rollCount = remainingRolls,
        isLowStock = remainingMeters < 25.0
      )
    )

    // Insert individual cutting records for each model
    modelCuts.forEach { item ->
      val cutEntity = CuttingEntity(
        modelCode = item.modelCode,
        modelName = item.modelName,
        fabricCode = "${fabric.code} (${fabric.name})",
        targetQuantity = item.cutQuantity,
        cutQuantity = item.cutQuantity,
        standardMetersPerItem = item.metersPerItem,
        actualMetersPerItem = item.metersPerItem,
        standardWeightKgPerItem = if (metersPerKg > 0) item.metersPerItem / metersPerKg else 0.0,
        actualWeightKgPerItem = if (metersPerKg > 0) item.metersPerItem / metersPerKg else 0.0,
        status = "برش‌خورده - آماده دوخت",
        date = "امروز"
      )
      database.cuttingDao().insertCutting(cutEntity)
    }

    val modelSummary = modelCuts.joinToString("، ") { "${it.cutQuantity} عدد ${it.modelName} (${"%.1f".format(it.totalMetersUsed)}متر)" }
    val msg = "برش چند مدلی طاقه ${fabric.code} اعمال شد: $modelSummary | باقی‌مانده طاقه: ${"%.1f".format(remainingMeters)} متر (${"%.1f".format(remainingWeightKg)} کیلو)"
    Pair(true, msg)
  }


  suspend fun insertFabric(
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
    syncWarehousePrices: Boolean = true,
    purchaseDate: String = com.example.util.PersianDateHelper.getTodayPersianDate()
  ) {
    val effectivePriceKg = if (buyPricePerKg > 0) buyPricePerKg else if (totalWeightKg > 0) ((totalMeters * buyPricePerMeter) / totalWeightKg).toLong() else 0L

    val existing = database.fabricDao().getByCode(code)
    var prevMeterPrice = 0L
    var prevKgPrice = 0L
    var prevDate = ""

    if (existing != null) {
      prevMeterPrice = existing.buyPricePerMeter
      prevKgPrice = existing.buyPricePerKg
      prevDate = if (existing.purchaseDate.isNotEmpty()) existing.purchaseDate else "پارت قبلی"

      if (syncWarehousePrices && prevMeterPrice != buyPricePerMeter) {
        // Sync price to existing warehouse fabric rolls of same code, recording previous price in red
        database.fabricDao().updateFabric(
          existing.copy(
            previousBuyPricePerMeter = prevMeterPrice,
            previousBuyPricePerKg = prevKgPrice,
            previousPurchaseDate = prevDate,
            buyPricePerMeter = buyPricePerMeter,
            buyPricePerKg = effectivePriceKg,
            purchaseDate = purchaseDate,
            currentMarketPrice = (buyPricePerMeter * 1.12).toLong()
          )
        )
      }
    }

    val fabric = FabricEntity(
      name = name,
      code = code,
      color = color,
      batchNumber = batchNumber,
      supplierName = supplierName,
      rollCount = rollCount,
      totalMeters = totalMeters,
      totalWeightKg = totalWeightKg,
      buyPricePerMeter = buyPricePerMeter,
      buyPricePerKg = effectivePriceKg,
      currentMarketPrice = (buyPricePerMeter * 1.12).toLong(),
      isLowStock = totalMeters < 500,
      previousBuyPricePerMeter = if (prevMeterPrice > 0 && prevMeterPrice != buyPricePerMeter) prevMeterPrice else 0L,
      previousBuyPricePerKg = if (prevKgPrice > 0 && prevKgPrice != effectivePriceKg) prevKgPrice else 0L,
      previousPurchaseDate = prevDate,
      purchaseDate = purchaseDate
    )
    database.fabricDao().insertFabric(fabric)
  }

  suspend fun updateFabric(fabric: FabricEntity) {
    database.fabricDao().updateFabric(fabric)
  }

  suspend fun deleteFabric(id: Long) {
    database.fabricDao().deleteById(id)
  }

  suspend fun insertInventoryItem(
    name: String,
    code: String,
    category: String,
    readyCount: Int,
    availableCount: Int,
    unitSalePrice: Long,
    unitCostPrice: Long,
    unitWeightGrams: Double = 0.0,
    totalWeightKg: Double = 0.0,
    unitType: String = "عدد",
    supplierName: String = "",
    metersPerKg: Double = 0.0,
    pricePerMeter: Long = 0L,
    syncPrices: Boolean = true
  ) {
    val existing = database.inventoryDao().getByCode(code)
    var prevCostPrice = 0L
    var prevCostDate = ""
    val todayDate = com.example.util.PersianDateHelper.getTodayPersianDate()

    if (existing != null) {
      prevCostPrice = existing.unitCostPrice
      prevCostDate = existing.lastUpdated

      if (syncPrices) {
        // Update existing item with new price and accumulate stock
        val updatedReady = if (category == "محصولات آماده") existing.readyForShipment + readyCount else existing.readyForShipment
        val updatedAvail = existing.availableForSale + availableCount
        val finalUnitWeight = if (unitWeightGrams > 0) unitWeightGrams else existing.unitWeightGrams
        val finalPricePerMeter = if (pricePerMeter > 0) pricePerMeter else existing.pricePerMeter

        database.inventoryDao().updateItem(
          existing.copy(
            name = name,
            readyForShipment = updatedReady,
            availableForSale = updatedAvail,
            unitSalePrice = if (unitSalePrice > 0) unitSalePrice else existing.unitSalePrice,
            unitCostPrice = unitCostPrice,
            unitWeightGrams = finalUnitWeight,
            totalWeightKg = totalWeightKg.coerceAtLeast(existing.totalWeightKg),
            unitType = unitType,
            lastUpdated = todayDate,
            previousCostPrice = if (prevCostPrice != unitCostPrice) prevCostPrice else existing.previousCostPrice,
            previousCostDate = if (prevCostPrice != unitCostPrice) prevCostDate else existing.previousCostDate,
            supplierName = supplierName.ifEmpty { existing.supplierName },
            metersPerKg = if (metersPerKg > 0) metersPerKg else existing.metersPerKg,
            pricePerMeter = finalPricePerMeter
          )
        )
        return
      }
    }

    val item = InventoryEntity(
      name = name,
      code = code,
      category = category,
      readyForShipment = readyCount,
      reservedQuantity = 0,
      availableForSale = availableCount,
      unitSalePrice = unitSalePrice,
      unitCostPrice = unitCostPrice,
      unitWeightGrams = unitWeightGrams,
      totalWeightKg = totalWeightKg,
      unitType = unitType,
      lastUpdated = todayDate,
      previousCostPrice = if (prevCostPrice > 0 && prevCostPrice != unitCostPrice) prevCostPrice else 0L,
      previousCostDate = prevCostDate,
      supplierName = supplierName,
      metersPerKg = metersPerKg,
      pricePerMeter = pricePerMeter
    )
    database.inventoryDao().insertItem(item)
  }

  suspend fun insertSupplier(
    name: String,
    storeName: String,
    mobile: String,
    phone: String,
    address: String,
    distributionCategory: String,
    description: String
  ) {
    val supplier = SupplierEntity(
      name = name,
      storeName = storeName,
      mobile = mobile,
      phone = phone,
      address = address,
      distributionCategory = distributionCategory,
      description = description,
      supplyType = distributionCategory.ifEmpty { "پارچه و ملزومات" },
      totalPurchases = 0L,
      lastPurchaseDate = com.example.util.PersianDateHelper.getTodayPersianDate(),
      priceHistoryNote = "ثبت مستقیم در سامانه کارخانه"
    )
    database.supplierDao().insertSupplier(supplier)
  }

  suspend fun deleteSupplier(supplier: SupplierEntity) {
    database.supplierDao().deleteSupplier(supplier)
  }

  suspend fun deleteSupplierById(id: Long) {
    database.supplierDao().deleteById(id)
  }

  suspend fun updateInventoryItem(item: InventoryEntity) {
    database.inventoryDao().updateItem(item)
  }

  suspend fun deleteInventoryItem(id: Long) {
    database.inventoryDao().deleteById(id)
  }

  suspend fun updateSaleOrder(order: SaleOrderEntity) {
    database.saleOrderDao().updateOrder(order)
  }

  suspend fun deleteSaleOrder(order: SaleOrderEntity) {
    database.saleOrderDao().deleteOrder(order)
  }

  suspend fun insertCustomer(
    name: String,
    company: String,
    phone: String,
    address: String,
    category: String
  ) {
    val customer = CustomerEntity(
      name = name,
      company = company,
      phone = phone,
      address = address,
      category = category,
      totalPurchases = 0L,
      orderCount = 0,
      currentDebt = 0L,
      tier = "خرید اول",
      popularModels = "-",
      lastOrderDate = "امروز"
    )
    database.customerDao().insertCustomer(customer)
  }

  suspend fun updateCustomer(customer: CustomerEntity) {
    database.customerDao().updateCustomer(customer)
  }

  suspend fun deleteCustomer(customer: CustomerEntity) {
    database.customerDao().deleteCustomer(customer)
  }

  suspend fun saveFactorySettings(settings: com.example.data.model.FactorySettingsEntity) {
    database.factorySettingsDao().insertOrUpdate(settings)
  }

  suspend fun resetDatabaseToDemo() {
    AppDatabase.resetDatabaseToDemo(database)
  }

  suspend fun clearTemporaryCache(context: android.content.Context) {
    try {
      context.cacheDir?.deleteRecursively()
      context.codeCacheDir?.deleteRecursively()
    } catch (_: Exception) {}
  }

  suspend fun updateOrderStatus(orderId: Long, newStatus: String) {
    // Helper to update specific order status
  }

  // ==========================================
  // PHASE 2: FABRIC ROLL & INBOUND WAREHOUSE
  // ==========================================

  suspend fun insertFabricRoll(
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
    batchNumber: String = "",
    shippingExpenseId: Long? = null,
    fabricCategoryId: Long? = null,
    fabricCategoryName: String = "",
    otherDirectCost: Long = 0L
  ): Long {
    val computedPriceMeter = if (buyPricePerMeter > 0L) {
      buyPricePerMeter
    } else if (buyPricePerKg > 0L && initialMeters > 0.0 && weightKg > 0.0) {
      val metersPerKg = initialMeters / weightKg
      FinancialCalculationService.calculatePricePerMeter(buyPricePerKg, metersPerKg)
    } else 0L

    val computedPriceKg = if (buyPricePerKg > 0L) {
      buyPricePerKg
    } else if (computedPriceMeter > 0L && initialMeters > 0.0 && weightKg > 0.0) {
      val metersPerKg = initialMeters / weightKg
      FinancialCalculationService.calculatePricePerKg(computedPriceMeter, metersPerKg)
    } else 0L

    val roll = FabricRollEntity(
      rollCode = rollCode,
      inboundDate = inboundDate,
      inboundTimestamp = System.currentTimeMillis(),
      fabricType = fabricType,
      fabricCode = fabricCode,
      color = color,
      initialMeters = initialMeters,
      remainingMeters = initialMeters,
      weightKg = weightKg,
      remainingWeightKg = weightKg,
      buyPricePerMeter = computedPriceMeter,
      buyPricePerKg = computedPriceKg,
      allocatedShippingCost = allocatedShippingCost,
      otherDirectCost = otherDirectCost,
      status = "موجود",
      supplierName = supplierName,
      batchNumber = batchNumber,
      shippingExpenseId = shippingExpenseId,
      fabricCategoryId = fabricCategoryId,
      fabricCategoryName = fabricCategoryName
    )
    val rollId = database.fabricRollDao().insertRoll(roll)

    // Synchronize or create fabric aggregate record
    val existingFabric = database.fabricDao().getByCode(fabricCode)
    if (existingFabric != null) {
      database.fabricDao().updateFabric(
        existingFabric.copy(
          rollCount = existingFabric.rollCount + 1,
          totalMeters = existingFabric.totalMeters + initialMeters,
          totalWeightKg = existingFabric.totalWeightKg + weightKg,
          buyPricePerMeter = computedPriceMeter,
          buyPricePerKg = computedPriceKg,
          isLowStock = false
        )
      )
    } else {
      database.fabricDao().insertFabric(
        FabricEntity(
          name = fabricType,
          code = fabricCode,
          color = color,
          batchNumber = batchNumber,
          supplierName = supplierName,
          rollCount = 1,
          totalMeters = initialMeters,
          totalWeightKg = weightKg,
          buyPricePerMeter = computedPriceMeter,
          buyPricePerKg = computedPriceKg,
          currentMarketPrice = (computedPriceMeter * 1.12).toLong(),
          isLowStock = false,
          purchaseDate = inboundDate
        )
      )
    }

    return rollId
  }

  suspend fun updateFabricRoll(roll: FabricRollEntity) {
    database.fabricRollDao().updateRoll(roll)
  }

  suspend fun deleteFabricRoll(id: Long) {
    database.fabricRollDao().deleteById(id)
  }

  suspend fun consumeFabricRoll(
    rollId: Long,
    modelCode: String,
    modelName: String,
    metersUsed: Double,
    productionId: Long = 0L,
    cuttingId: Long = 0L,
    note: String = "",
    garmentCount: Int = 0,
    metersPerGarment: Double = 0.0,
    createProductionOrder: Boolean = true,
    tailorCostPerItem: Long = 0L,
    accessoriesCostPerItem: Long = 0L,
    otherDirectCost: Long = 0L,
    initialStatus: String = "در حال دوخت"
  ): Pair<Boolean, String> = database.withTransaction {
    val roll = database.fabricRollDao().getRollById(rollId)
      ?: return@withTransaction Pair(false, "طاقه مورد نظر با شناسه $rollId یافت نشد")

    if (metersUsed <= 0.0) {
      return@withTransaction Pair(false, "متراژ مصرفی باید بیشتر از صفر باشد")
    }

    if (metersUsed > roll.remainingMeters + 0.05) {
      return@withTransaction Pair(false, "متراژ درخواستی (${"%.1f".format(metersUsed)}متر) از موجودی باقیمانده طاقه (${"%.1f".format(roll.remainingMeters)}متر) بیشتر است")
    }

    val newRemaining = (roll.remainingMeters - metersUsed).coerceAtLeast(0.0)
    val newStatus = if (newRemaining <= 0.5) "پایان یافته" else "در حال مصرف"

    val metersPerKg = roll.metersPerKg
    val weightKgUsed = if (metersPerKg > 0.0) metersUsed / metersPerKg else 0.0
    val newRemainingWeight = if (roll.initialMeters > 0.0) {
      ((newRemaining / roll.initialMeters) * roll.weightKg).coerceAtLeast(0.0)
    } else 0.0

    val fabricCostUsed = (metersUsed * roll.buyPricePerMeter).toLong()
    val shippingCostUsed = if (roll.initialMeters > 0.0) ((metersUsed / roll.initialMeters) * roll.allocatedShippingCost).toLong() else 0L

    database.fabricRollDao().updateRoll(
      roll.copy(
        remainingMeters = newRemaining,
        remainingWeightKg = newRemainingWeight,
        status = newStatus
      )
    )

    // Deduct from general fabrics table as well if exists
    if (roll.fabricCode.isNotEmpty()) {
      val fab = database.fabricDao().getByCode(roll.fabricCode)
      if (fab != null) {
        val updatedMeters = (fab.totalMeters - metersUsed).coerceAtLeast(0.0)
        database.fabricDao().updateFabric(
          fab.copy(
            totalMeters = updatedMeters,
            isLowStock = updatedMeters < 50.0
          )
        )
      }
    }

    var effectiveProductionId = productionId
    if (createProductionOrder && garmentCount > 0 && productionId == 0L) {
      val prod = ProductionEntity(
        modelCode = modelCode,
        modelName = modelName,
        quantity = garmentCount,
        fabricRollsUsed = 1,
        fabricMetersUsed = metersUsed,
        totalWeightKg = weightKgUsed,
        sewingWagePerItem = tailorCostPerItem,
        fabricPricePerMeter = roll.buyPricePerMeter,
        accessoriesCostPerItem = accessoriesCostPerItem,
        status = initialStatus,
        date = com.example.util.PersianDateHelper.getTodayPersianDate(),
        rollId = roll.id,
        rollCode = roll.rollCode,
        consumablesSummary = "مصرف ${"%.1f".format(metersUsed)} متر (${"%.2f".format(weightKgUsed)} کیلو) از طاقه ${roll.rollCode}"
      )
      effectiveProductionId = database.productionDao().insertProduction(prod)
    }

    val consumptionNote = if (note.isNotBlank()) note else if (garmentCount > 0) "برش $garmentCount کار (هر کار ${"%.2f".format(metersPerGarment)} متر)" else ""

    val usage = RollUsageEntity(
      rollId = roll.id,
      rollCode = roll.rollCode,
      productionId = effectiveProductionId,
      cuttingId = cuttingId,
      modelCode = modelCode,
      modelName = modelName,
      metersUsed = metersUsed,
      weightKgUsed = weightKgUsed,
      usageDate = com.example.util.PersianDateHelper.getTodayPersianDate(),
      usageTimestamp = System.currentTimeMillis(),
      allocatedFabricCost = fabricCostUsed,
      allocatedShippingCost = shippingCostUsed,
      note = consumptionNote
    )
    database.rollUsageDao().insertUsage(usage)

    Pair(true, "مصرف ${"%.1f".format(metersUsed)} متر (${"%.2f".format(weightKgUsed)} کیلو) از طاقه ${roll.rollCode} ثبت شد. باقیمانده طاقه: ${"%.1f".format(newRemaining)} متر")
  }

  suspend fun completeProductionToReadyGoods(
    production: ProductionEntity,
    salePrice: Long = 0L
  ): Pair<Boolean, String> {
    val count = production.quantity
    val updatedProd = production.copy(
      status = "آماده ارسال / تکمیل موجودی"
    )
    database.productionDao().updateProduction(updatedProd)

    // Find or create in InventoryEntity
    val existing = database.inventoryDao().getByCode(production.modelCode)
    val unitCost = if (count > 0) production.totalCost / count else 0L
    val effectiveSalePrice = if (salePrice > 0L) salePrice else (if (existing != null && existing.unitSalePrice > 0L) existing.unitSalePrice else (unitCost * 1.5).toLong())

    if (existing != null) {
      database.inventoryDao().updateItem(
        existing.copy(
          readyForShipment = existing.readyForShipment + count,
          availableForSale = existing.availableForSale + count,
          unitCostPrice = if (unitCost > 0L) unitCost else existing.unitCostPrice,
          unitSalePrice = effectiveSalePrice
        )
      )
    } else {
      val newItem = InventoryEntity(
        name = production.modelName,
        code = production.modelCode,
        category = "محصولات آماده",
        readyForShipment = count,
        reservedQuantity = 0,
        availableForSale = count,
        unitCostPrice = unitCost,
        unitSalePrice = effectiveSalePrice,
        unitWeightGrams = production.weightPerItemGrams,
        totalWeightKg = production.totalWeightKg,
        unitType = "عدد",
        lastUpdated = com.example.util.PersianDateHelper.getTodayPersianDate()
      )
      database.inventoryDao().insertItem(newItem)
    }

    try {
      database.inventoryLedgerDao().insert(
        InventoryLedgerEntity(
          timestamp = System.currentTimeMillis(),
          date = com.example.util.PersianDateHelper.getTodayPersianDate(),
          itemType = "FINISHED_GOOD",
          itemId = production.id,
          itemCode = production.modelCode,
          itemName = production.modelName,
          color = "",
          size = "",
          transactionType = "PRODUCTION_FINISH_INBOUND",
          quantityChange = count.toDouble(),
          balanceAfter = (existing?.readyForShipment ?: 0) + count.toDouble(),
          unit = "عدد",
          unitPriceAtTime = unitCost,
          relatedDocumentNumber = "PROD-${production.id}",
          notes = "ثبت کار آماده حاصل از مصرف طاقه ${production.rollCode}",
          operator = "مدیر کارگاه"
        )
      )
    } catch (_: Exception) {}

    return Pair(true, "$count عدد «${production.modelName}» با موفقیت به عنوان کار آماده در انبار ثبت گردید")
  }

  // ==========================================
  // PHASE 2: ACCESSORIES & PRICE HISTORY
  // ==========================================

  suspend fun insertAccessoryPurchase(
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
  ): Long {
    val computedPriceMeter = if (pricePerMeter > 0L) {
      pricePerMeter
    } else if (unit == "کیلوگرم" && metersPerKg > 0.0 && unitCostPrice > 0L) {
      FinancialCalculationService.calculatePricePerMeter(unitCostPrice, metersPerKg)
    } else 0L

    val totalCost = (quantity * unitCostPrice).toLong()
    val todayDate = com.example.util.PersianDateHelper.getTodayPersianDate()

    val purchase = AccessoryPurchaseEntity(
      accessoryCode = accessoryCode,
      accessoryName = accessoryName,
      purchaseDate = todayDate,
      purchaseTimestamp = System.currentTimeMillis(),
      quantity = quantity,
      unit = unit,
      unitCostPrice = unitCostPrice,
      totalCostPrice = totalCost,
      supplierName = supplierName,
      allocatedShippingCost = allocatedShippingCost,
      metersPerKg = metersPerKg,
      pricePerMeter = computedPriceMeter,
      shippingExpenseId = shippingExpenseId,
      note = note
    )
    val purchaseId = database.accessoryPurchaseDao().insertPurchase(purchase)

    // Update inventory item: accessories have NO sale price (cost only)
    val existing = database.inventoryDao().getByCode(accessoryCode)
    if (existing != null) {
      val prevCost = existing.unitCostPrice
      val prevDate = existing.lastUpdated
      database.inventoryDao().updateItem(
        existing.copy(
          name = accessoryName,
          availableForSale = existing.availableForSale + quantity.toInt(),
          unitCostPrice = unitCostPrice,
          unitSalePrice = 0L, // ملزومات قیمت فروش مستقل ندارند - هزینه تولید هستند
          unitType = unit,
          lastUpdated = todayDate,
          previousCostPrice = if (prevCost > 0L && prevCost != unitCostPrice) prevCost else existing.previousCostPrice,
          previousCostDate = if (prevCost > 0L && prevCost != unitCostPrice) prevDate else existing.previousCostDate,
          supplierName = supplierName.ifEmpty { existing.supplierName },
          metersPerKg = if (metersPerKg > 0.0) metersPerKg else existing.metersPerKg,
          pricePerMeter = computedPriceMeter
        )
      )
    } else {
      database.inventoryDao().insertItem(
        InventoryEntity(
          name = accessoryName,
          code = accessoryCode,
          category = "ملزومات",
          readyForShipment = 0,
          reservedQuantity = 0,
          availableForSale = quantity.toInt(),
          unitSalePrice = 0L, // ملزومات قیمت فروش مستقل ندارند - هزینه تولید هستند
          unitCostPrice = unitCostPrice,
          unitWeightGrams = 0.0,
          unitType = unit,
          lastUpdated = todayDate,
          previousCostPrice = 0L,
          previousCostDate = "",
          supplierName = supplierName,
          metersPerKg = metersPerKg,
          pricePerMeter = computedPriceMeter
        )
      )
    }

    return purchaseId
  }

  // ==========================================
  // PHASE 2: SHIPPING EXPENSES & ALLOCATION
  // ==========================================

  suspend fun insertShippingExpense(
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
  ): Long {
    val costPerUnit = FinancialCalculationService.calculateUnitShippingCost(
      totalAmount = totalAmount,
      method = allocationMethod,
      itemCount = itemCount,
      totalWeightKg = totalWeightKg,
      totalQuantity = totalQuantity
    )

    val expense = ShippingExpenseEntity(
      trackingNumber = trackingNumber,
      title = title,
      date = com.example.util.PersianDateHelper.getTodayPersianDate(),
      timestamp = System.currentTimeMillis(),
      totalAmount = totalAmount,
      inboundType = inboundType,
      itemCount = itemCount,
      totalWeightKg = totalWeightKg,
      totalQuantity = totalQuantity,
      unit = unit,
      allocationMethod = allocationMethod.name,
      costPerUnit = costPerUnit,
      carrierName = carrierName,
      notes = notes
    )
    val expenseId = database.shippingExpenseDao().insertExpense(expense)

    // If specific rolls are provided to allocate this shipping cost to:
    if (allocateToRollIds.isNotEmpty()) {
      val rollCount = allocateToRollIds.size
      val perRollShipping = totalAmount / rollCount
      allocateToRollIds.forEach { rId ->
        val r = database.fabricRollDao().getRollById(rId)
        if (r != null) {
          database.fabricRollDao().updateRoll(
            r.copy(
              allocatedShippingCost = perRollShipping,
              shippingExpenseId = expenseId
            )
          )
        }
      }
    }

    return expenseId
  }

  suspend fun updateShippingExpense(
    expense: ShippingExpenseEntity,
    allocatedRollIds: List<Long>
  ) {
    val costPerUnit = FinancialCalculationService.calculateUnitShippingCost(
      totalAmount = expense.totalAmount,
      method = try { ShippingAllocationMethod.valueOf(expense.allocationMethod) } catch (_: Exception) { ShippingAllocationMethod.PER_ITEM },
      itemCount = if (allocatedRollIds.isNotEmpty()) allocatedRollIds.size else expense.itemCount,
      totalWeightKg = expense.totalWeightKg,
      totalQuantity = expense.totalQuantity
    )

    val updatedExpense = expense.copy(
      costPerUnit = costPerUnit,
      itemCount = if (allocatedRollIds.isNotEmpty()) allocatedRollIds.size else expense.itemCount
    )
    database.shippingExpenseDao().updateExpense(updatedExpense)

    // Clear previous rolls that were unselected
    val currentRolls = database.fabricRollDao().getRollsForShippingExpense(expense.id)
    currentRolls.filter { !allocatedRollIds.contains(it.id) }.forEach { roll ->
      database.fabricRollDao().updateRoll(
        roll.copy(
          allocatedShippingCost = 0L,
          shippingExpenseId = null
        )
      )
    }

    // Allocate to selected rolls
    if (allocatedRollIds.isNotEmpty()) {
      val perRollShipping = expense.totalAmount / allocatedRollIds.size
      allocatedRollIds.forEach { rId ->
        val r = database.fabricRollDao().getRollById(rId)
        if (r != null) {
          database.fabricRollDao().updateRoll(
            r.copy(
              allocatedShippingCost = perRollShipping,
              shippingExpenseId = expense.id
            )
          )
        }
      }
    }
  }

  suspend fun deleteShippingExpense(id: Long) {
    // Clear allocated shipping from rolls linked to this expense
    val linkedRolls = database.fabricRollDao().getRollsForShippingExpense(id)
    linkedRolls.forEach { roll ->
      database.fabricRollDao().updateRoll(
        roll.copy(
          allocatedShippingCost = 0L,
          shippingExpenseId = null
        )
      )
    }
    database.shippingExpenseDao().deleteById(id)
  }

  // ====================================================
  // FABRIC CATEGORIES
  // ====================================================
  suspend fun insertFabricCategory(category: FabricCategoryEntity): Long {
    return database.fabricCategoryDao().insert(category)
  }

  suspend fun updateFabricCategory(category: FabricCategoryEntity) {
    database.fabricCategoryDao().update(category)
  }

  suspend fun deleteFabricCategory(id: Long) {
    database.fabricCategoryDao().deleteById(id)
  }

  // ====================================================
  // SHIPPING COMPANIES
  // ====================================================
  suspend fun insertShippingCompany(company: ShippingCompanyEntity): Long {
    return database.shippingCompanyDao().insert(company)
  }

  suspend fun updateShippingCompany(company: ShippingCompanyEntity) {
    database.shippingCompanyDao().update(company)
  }

  suspend fun deleteShippingCompany(id: Long) {
    database.shippingCompanyDao().deleteById(id)
  }

  // ====================================================
  // BASE COST CONFIGURATIONS
  // ====================================================
  suspend fun insertBaseCostConfig(config: BaseCostConfigEntity): Long {
    return database.baseCostConfigDao().insert(config)
  }

  suspend fun updateBaseCostConfig(config: BaseCostConfigEntity) {
    database.baseCostConfigDao().update(config)
  }

  suspend fun deleteBaseCostConfig(id: Long) {
    database.baseCostConfigDao().deleteById(id)
  }

  // ====================================================
  // WAYBILL ITEMS & ADVANCED SHIPPING ALLOCATION
  // ====================================================
  fun getWaybillItems(waybillId: Long): Flow<List<WaybillItemEntity>> {
    return database.waybillItemDao().getItemsForWaybill(waybillId)
  }

  suspend fun insertWaybillItem(item: WaybillItemEntity): Long {
    return database.waybillItemDao().insert(item)
  }

  suspend fun updateWaybillItem(item: WaybillItemEntity) {
    database.waybillItemDao().update(item)
  }

  suspend fun deleteWaybillItem(id: Long) {
    database.waybillItemDao().deleteById(id)
  }

  suspend fun allocateWaybillShipping(
    waybillId: Long,
    totalFreight: Long,
    method: ShippingAllocationMethod
  ): Boolean = database.withTransaction {
    val items = database.waybillItemDao().getItemsForWaybillList(waybillId)
    if (items.isEmpty()) return@withTransaction false

    val allocations = FinancialCalculationService.allocateShippingToItems(totalFreight, method, items)
    items.forEachIndexed { index, item ->
      val share = if (index < allocations.size) allocations[index] else 0L
      database.waybillItemDao().update(item.copy(shippingAllocation = share))
    }
    true
  }

  suspend fun allocateShippingToRollsAdvanced(
    expenseId: Long,
    method: ShippingAllocationMethod,
    targetRollIds: List<Long>
  ): Boolean = database.withTransaction {
    val expense = database.shippingExpenseDao().getExpenseById(expenseId) ?: return@withTransaction false
    if (targetRollIds.isEmpty()) return@withTransaction false

    val rolls = targetRollIds.mapNotNull { database.fabricRollDao().getRollById(it) }
    if (rolls.isEmpty()) return@withTransaction false

    val totalFreight = expense.totalAmount
    when (method) {
      ShippingAllocationMethod.BY_WEIGHT -> {
        val totalWeight = rolls.sumOf { it.weightKg }
        rolls.forEach { r ->
          val share = if (totalWeight > 0.0) ((r.weightKg / totalWeight) * totalFreight).toLong() else totalFreight / rolls.size
          database.fabricRollDao().updateRoll(r.copy(allocatedShippingCost = share, shippingExpenseId = expenseId))
        }
      }
      ShippingAllocationMethod.BY_PURCHASE_VALUE -> {
        val totalVal = rolls.sumOf { (it.initialMeters * it.buyPricePerMeter).toLong() }
        rolls.forEach { r ->
          val rollVal = (r.initialMeters * r.buyPricePerMeter).toLong()
          val share = if (totalVal > 0L) ((rollVal.toDouble() / totalVal.toDouble()) * totalFreight).toLong() else totalFreight / rolls.size
          database.fabricRollDao().updateRoll(r.copy(allocatedShippingCost = share, shippingExpenseId = expenseId))
        }
      }
      else -> {
        val equal = totalFreight / rolls.size
        rolls.forEach { r ->
          database.fabricRollDao().updateRoll(r.copy(allocatedShippingCost = equal, shippingExpenseId = expenseId))
        }
      }
    }
    database.shippingExpenseDao().updateExpense(
      expense.copy(
        itemCount = rolls.size,
        allocationMethod = method.name,
        costPerUnit = totalFreight / rolls.size
      )
    )
    true
  }

  // ====================================================
  // ENHANCED CUTTING & MULTI-PART WORKFLOW
  // ====================================================
  suspend fun addCuttingPart(
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
  ): Pair<Boolean, String> = database.withTransaction {
    val roll = database.fabricRollDao().getRollById(rollId)
      ?: return@withTransaction Pair(false, "طاقه مورد نظر با شناسه $rollId یافت نشد")

    if (cutQuantity <= 0) {
      return@withTransaction Pair(false, "تعداد برش باید بزرگتر از صفر باشد")
    }

    if (metersUsed <= 0.0) {
      return@withTransaction Pair(false, "متراژ مصرفی باید بیشتر از صفر باشد")
    }

    if (metersUsed > roll.remainingMeters + 0.05) {
      return@withTransaction Pair(false, "متراژ درخواستی (${"%.1f".format(metersUsed)}متر) از موجودی باقیمانده طاقه (${"%.1f".format(roll.remainingMeters)}متر) بیشتر است")
    }

    // 1. Deduct from roll
    val newRemainingMeters = (roll.remainingMeters - metersUsed).coerceAtLeast(0.0)
    val effectiveWeightUsed = if (weightKgUsed > 0.0) weightKgUsed else if (roll.metersPerKg > 0.0) metersUsed / roll.metersPerKg else 0.0
    val newRemainingWeight = if (roll.initialMeters > 0.0) ((newRemainingMeters / roll.initialMeters) * roll.weightKg).coerceAtLeast(0.0) else 0.0
    val rollStatus = if (newRemainingMeters <= 0.5) "پایان یافته" else "در حال مصرف"

    database.fabricRollDao().updateRoll(
      roll.copy(
        remainingMeters = newRemainingMeters,
        remainingWeightKg = newRemainingWeight,
        status = rollStatus
      )
    )

    // Also deduct from generic fabric aggregate if matched
    if (roll.fabricCode.isNotEmpty()) {
      val fab = database.fabricDao().getByCode(roll.fabricCode)
      if (fab != null) {
        val updatedM = (fab.totalMeters - metersUsed).coerceAtLeast(0.0)
        database.fabricDao().updateFabric(fab.copy(totalMeters = updatedM, isLowStock = updatedM < 50.0))
      }
    }

    // 2. Fetch base cost configs to calculate overhead
    val baseConfigs = database.baseCostConfigDao().getAllConfigsSync()
    val (overheadPerItem, totalOverhead) = FinancialCalculationService.calculateOverheadFromBaseConfigs(baseConfigs, cutQuantity)

    // 3. Calculate fabric cost and allocated shipping share
    val fabricCostPerMeter = roll.buyPricePerMeter
    val allocatedShippingForThisCut = if (roll.initialMeters > 0.0) {
      ((metersUsed / roll.initialMeters) * roll.allocatedShippingCost).toLong()
    } else 0L

    val actualMetersPerItem = if (cutQuantity > 0) metersUsed / cutQuantity else 0.0
    val actualWeightKgPerItem = if (cutQuantity > 0) effectiveWeightUsed / cutQuantity else 0.0

    val todayDate = PersianDateHelper.getTodayPersianDate()

    val totalCostForBatch = (metersUsed * fabricCostPerMeter).toLong() +
      allocatedShippingForThisCut +
      (accessoriesCostPerItem * cutQuantity) +
      (sewingWagePerItem * cutQuantity) +
      (overheadPerItem * cutQuantity)

    // 4. Create Cutting Order
    val cutting = CuttingEntity(
      modelCode = productCode,
      modelName = productName,
      fabricCode = roll.fabricCode,
      targetQuantity = cutQuantity,
      cutQuantity = cutQuantity,
      standardMetersPerItem = actualMetersPerItem,
      actualMetersPerItem = actualMetersPerItem,
      standardWeightKgPerItem = actualWeightKgPerItem,
      actualWeightKgPerItem = actualWeightKgPerItem,
      status = "برش خورده",
      date = todayDate,
      partNumber = 1,
      partTitle = partTitle.ifEmpty { "پارت برش ${productName}" },
      rollId = roll.id,
      rollCode = roll.rollCode,
      productId = productId,
      productCode = productCode,
      productName = productName,
      size = size,
      color = color.ifEmpty { roll.color },
      workType = workType,
      customerId = customerId,
      customerName = customerName,
      orderId = orderId,
      orderNumber = orderNumber,
      metersUsed = metersUsed,
      weightKgUsed = effectiveWeightUsed,
      sellingPrice = sellingPrice * cutQuantity,
      targetProfit = ((sellingPrice * cutQuantity) - totalCostForBatch).coerceAtLeast(0L),
      targetMargin = if (sellingPrice > 0L) (((sellingPrice * cutQuantity) - totalCostForBatch).toDouble() / (sellingPrice * cutQuantity).toDouble()) * 100.0 else 0.0,
      timestamp = System.currentTimeMillis(),
      notes = notes,
      fabricCost = (metersUsed * fabricCostPerMeter).toLong(),
      allocatedShippingCost = allocatedShippingForThisCut,
      accessoriesCost = accessoriesCostPerItem * cutQuantity,
      tailorCost = sewingWagePerItem * cutQuantity,
      overheadCost = overheadPerItem * cutQuantity,
      otherDirectCost = 0L,
      totalCost = totalCostForBatch,
      isStockAdded = false
    )
    val cuttingId = database.cuttingDao().insertCutting(cutting)

    // 5. Record Roll Usage
    database.rollUsageDao().insertUsage(
      RollUsageEntity(
        rollId = roll.id,
        rollCode = roll.rollCode,
        cuttingId = cuttingId,
        productionId = 0L,
        modelCode = productCode,
        modelName = productName,
        metersUsed = metersUsed,
        weightKgUsed = effectiveWeightUsed,
        usageDate = todayDate,
        usageTimestamp = System.currentTimeMillis(),
        allocatedFabricCost = (metersUsed * fabricCostPerMeter).toLong(),
        allocatedShippingCost = allocatedShippingForThisCut,
        note = "برش پارت: $partTitle"
      )
    )

    // 6. Record Consumables
    consumablesList.forEach { c ->
      database.productionConsumableDao().insertConsumable(
        ProductionConsumableEntity(
          productionId = 0L,
          cuttingId = cuttingId,
          materialId = 0L,
          accessoryCode = c.accessoryCode,
          accessoryName = c.accessoryName,
          quantityUsed = c.quantityUsed,
          unit = c.unit,
          unitCostPrice = c.unitCostPrice,
          totalCost = c.totalCost,
          date = todayDate
        )
      )
    }

    Pair(true, "پارت برش «$partTitle» با تعداد $cutQuantity عدد با موفقیت ثبت شد و از طاقه ${roll.rollCode} کسر گردید")
  }

  suspend fun completeCuttingToReadyGoods(
    cuttingId: Long,
    destination: String = "انبار محصولات آماده",
    note: String = ""
  ): Pair<Boolean, String> = database.withTransaction {
    val cutting = database.cuttingDao().getCuttingById(cuttingId)
      ?: return@withTransaction Pair(false, "رکورد برش با شناسه $cuttingId یافت نشد")

    // Update cutting status
    database.cuttingDao().updateCutting(
      cutting.copy(
        status = "تکمیل شده - تحویل انبار"
      )
    )

    // Calculate total unit cost
    val unitCost = cutting.unitCost
    val count = cutting.cutQuantity
    val prodCode = if (cutting.productCode.isNotEmpty()) cutting.productCode else "PRD-${cutting.id}"
    val prodName = if (cutting.productName.isNotEmpty()) cutting.productName else cutting.partTitle

    val existing = database.inventoryDao().getByCode(prodCode)
    val todayDate = PersianDateHelper.getTodayPersianDate()

    if (existing != null) {
      database.inventoryDao().updateItem(
        existing.copy(
          readyForShipment = existing.readyForShipment + count,
          availableForSale = existing.availableForSale + count,
          unitCostPrice = if (unitCost > 0L) unitCost else existing.unitCostPrice,
          lastUpdated = todayDate
        )
      )
    } else {
      database.inventoryDao().insertItem(
        InventoryEntity(
          name = prodName,
          code = prodCode,
          category = "محصولات آماده",
          readyForShipment = count,
          reservedQuantity = 0,
          availableForSale = count,
          unitCostPrice = unitCost,
          unitSalePrice = cutting.unitSellingPrice,
          unitWeightGrams = (cutting.actualWeightKgPerItem * 1000.0),
          totalWeightKg = cutting.actualWeightKgPerItem * count,
          unitType = "عدد",
          lastUpdated = todayDate
        )
      )
    }

    try {
      database.inventoryLedgerDao().insert(
        InventoryLedgerEntity(
          timestamp = System.currentTimeMillis(),
          date = todayDate,
          itemType = "FINISHED_GOOD",
          itemId = cutting.productId ?: cutting.id,
          itemCode = prodCode,
          itemName = prodName,
          color = cutting.color,
          size = cutting.size,
          transactionType = "CUTTING_FINISH_INBOUND",
          quantityChange = count.toDouble(),
          balanceAfter = (existing?.readyForShipment ?: 0) + count.toDouble(),
          unit = "عدد",
          unitPriceAtTime = unitCost,
          relatedDocumentNumber = "CUT-${cutting.id}",
          notes = "تحویل کار آماده حاصل از پارت برش ${cutting.partTitle} به $destination",
          operator = "مدیر کارگاه"
        )
      )
    } catch (_: Exception) {}

    // If linked to an order, update order status if needed
    if (cutting.orderId != null && cutting.orderId > 0L) {
      val order = database.saleOrderDao().getOrderById(cutting.orderId)
      if (order != null) {
        database.saleOrderDao().updateOrder(order.copy(deliveryStatus = "آماده تحویل"))
      }
    }

    Pair(true, "$count عدد «$prodName» با موفقیت به انبار $destination اضافه گردید")
  }

  // ====================================================
  // Fixed Costs Management (هزینه‌های ثابت با تفکیک Scope)
  // ====================================================

  suspend fun insertFixedCost(cost: FixedCostEntity): Long {
    return database.fixedCostDao().insertFixedCost(cost)
  }

  suspend fun deleteFixedCost(id: Long) {
    database.fixedCostDao().deleteById(id)
  }

  // ====================================================
  // Order Status Workflow & History Tracking (وضعیت سفارش و تاریخچه)
  // ====================================================

  suspend fun updateOrderStatusWithHistory(
    orderId: Long,
    newStatus: String,
    note: String = ""
  ): Pair<Boolean, String> {
    val order = database.saleOrderDao().getOrderById(orderId)
      ?: return Pair(false, "سفارش مورد نظر یافت نشد")

    val oldStatus = order.deliveryStatus
    if (oldStatus == newStatus) {
      return Pair(true, "وضعیت سفارش تغییری نکرد")
    }

    // 1. Update Order Delivery Status
    database.saleOrderDao().updateOrder(order.copy(deliveryStatus = newStatus))

    // 2. Track History
    val history = OrderStatusHistoryEntity(
      orderId = order.id,
      orderNumber = order.orderNumber,
      oldStatus = oldStatus,
      newStatus = newStatus,
      date = PersianDateHelper.getCurrentPersianDate(),
      time = PersianDateHelper.getCurrentTime(),
      timestamp = System.currentTimeMillis(),
      note = note
    )
    database.orderStatusHistoryDao().insertHistory(history)

    // 3. Synchronize with related Production records if any
    try {
      val productions = database.productionDao().getAllProductions().firstOrNull() ?: emptyList()
      productions.filter { it.orderId == order.id || (it.modelCode == order.modelCode && it.date == order.orderDate) }.forEach { p ->
        database.productionDao().updateProduction(p.copy(status = newStatus))
      }
    } catch (_: Exception) {
      // Best-effort sync
    }

    return Pair(true, "وضعیت سفارش از «$oldStatus» به «$newStatus» تغییر یافت و تاریخچه ثبت گردید.")
  }

  // ====================================================
  // 1, 2, 3: Multi-Product Ready Goods from Roll + Inventory Control
  // ====================================================

  suspend fun submitMultiProductReadyGoods(
    rollId: Long,
    products: List<MultiProductReadyItem>,
    note: String = ""
  ): Pair<Boolean, String> {
    if (products.isEmpty()) {
      return Pair(false, "هیچ محصولی برای ثبت اضافه نشده است")
    }

    val roll = database.fabricRollDao().getRollById(rollId)
      ?: return Pair(false, "طاقه مورد نظر با شناسه $rollId یافت نشد")

    // Total meters validation (کنترل دقیق موجودی طاقه)
    val totalRequestedMeters = products.sumOf { it.metersUsed }
    if (!FinancialCalculationService.validateRollCapacity(roll.remainingMeters, totalRequestedMeters)) {
      val deficit = String.format(java.util.Locale.US, "%.1f", totalRequestedMeters - roll.remainingMeters)
      return Pair(
        false,
        "خطای کنترل موجودی طاقه ${roll.rollCode}: مجموع متراژ درخواستی ($totalRequestedMeters متر) از موجودی باقیمانده طاقه (${roll.remainingMeters} متر) بیشتر است! کسری: $deficit متر"
      )
    }

    val currentDate = PersianDateHelper.getCurrentPersianDate()
    val currentTime = PersianDateHelper.getCurrentTime()
    val timestamp = System.currentTimeMillis()

    // 1. Process each product line
    for (item in products) {
      val allocatedFabricCost = (item.metersUsed * roll.buyPricePerMeter).toLong()
      val allocatedShipping = if (roll.initialMeters > 0.0) {
        ((item.metersUsed / roll.initialMeters) * roll.allocatedShippingCost).toLong()
      } else {
        0L
      }
      val weightUsedKg = (item.unitWeightGrams * item.readyQuantity) / 1000.0

      // A. Create Roll Usage record
      val usage = RollUsageEntity(
        rollId = roll.id,
        rollCode = roll.rollCode,
        productionId = 0L,
        cuttingId = 0L,
        modelCode = item.modelCode,
        modelName = item.modelName,
        metersUsed = item.metersUsed,
        weightKgUsed = weightUsedKg,
        usageDate = currentDate,
        usageTimestamp = timestamp,
        allocatedFabricCost = allocatedFabricCost,
        allocatedShippingCost = allocatedShipping,
        note = "ثبت کار آماده از طاقه ${roll.rollCode} ($note)"
      )
      val usageId = database.rollUsageDao().insertUsage(usage)

      // B. Create Production Record (Status: آماده ارسال / تکمیل موجودی)
      val prodEntity = ProductionEntity(
        modelCode = item.modelCode,
        modelName = item.modelName,
        quantity = item.readyQuantity,
        fabricRollsUsed = 1,
        fabricMetersUsed = item.metersUsed,
        totalWeightKg = weightUsedKg,
        sewingWagePerItem = item.sewingWagePerItem,
        fabricPricePerMeter = roll.buyPricePerMeter,
        accessoriesCostPerItem = item.accessoriesCostPerItem,
        status = SaleOrderStatus.READY_FOR_SHIPPING,
        date = currentDate,
        rollId = roll.id,
        rollCode = roll.rollCode,
        consumablesSummary = "تولید مستقیم از طاقه ${roll.rollCode} (${item.metersUsed} متر)",
        orderId = null
      )
      val prodId = database.productionDao().insertProduction(prodEntity)

      // C. Allocate Fixed Costs according to Scope
      val allFixedCosts = database.fixedCostDao().getAllFixedCostsList()
      val allocatedFixed = FinancialCalculationService.allocateFixedCostsForProduct(
        fixedCosts = allFixedCosts,
        productCode = item.modelCode,
        category = "محصولات آماده",
        productionId = prodId,
        totalProductionUnitsInBatch = item.readyQuantity
      )

      // D. Update Inventory (محصولات آماده) with Complete Cost Breakdown:
      // Material + Consumables + Allocated Freight + Tailor Cost + Allocated Fixed Costs
      val existingInv = database.inventoryDao().getByCode(item.modelCode)
      val fabricCostPerUnit = if (item.readyQuantity > 0) allocatedFabricCost / item.readyQuantity else 0L
      val shippingPerUnit = if (item.readyQuantity > 0) allocatedShipping / item.readyQuantity else 0L
      val unitCost = FinancialCalculationService.calculateDetailedProductCost(
        materialCost = fabricCostPerUnit,
        consumablesCost = item.accessoriesCostPerItem,
        allocatedFreightCost = shippingPerUnit,
        tailorCost = item.sewingWagePerItem,
        allocatedFixedCosts = allocatedFixed
      )

      if (existingInv != null) {
        database.inventoryDao().updateItem(
          existingInv.copy(
            readyForShipment = existingInv.readyForShipment + item.readyQuantity,
            availableForSale = existingInv.availableForSale + item.readyQuantity,
            unitCostPrice = unitCost,
            unitSalePrice = item.salePricePerItem,
            lastUpdated = currentDate
          )
        )
      } else {
        database.inventoryDao().insertItem(
          InventoryEntity(
            name = item.modelName,
            code = item.modelCode,
            category = "محصولات آماده",
            readyForShipment = item.readyQuantity,
            reservedQuantity = 0,
            availableForSale = item.readyQuantity,
            unitCostPrice = unitCost,
            unitSalePrice = item.salePricePerItem,
            unitWeightGrams = item.unitWeightGrams,
            totalWeightKg = weightUsedKg,
            unitType = "عدد",
            lastUpdated = currentDate
          )
        )
      }
    }

    // 2. Deduct from Roll and update status
    val newRemaining = FinancialCalculationService.calculateRemainingRollMeters(roll.remainingMeters, totalRequestedMeters)
    val newStatus = if (newRemaining <= 0.5) "پایان یافته" else "در حال مصرف"
    database.fabricRollDao().updateRoll(
      roll.copy(
        remainingMeters = newRemaining,
        status = newStatus
      )
    )

    return Pair(
      true,
      "ثبت موفق: ${products.size} محصول به انبار کار آماده اضافه شد. باقیمانده طاقه ${roll.rollCode}: $newRemaining متر."
    )
  }

  // ====================================================
  // 4, 8: Production Registration with Consumables & Tailor Cost
  // ====================================================

  suspend fun submitProductionWithConsumables(
    modelCode: String,
    modelName: String,
    quantity: Int,
    rollId: Long?,
    rollCode: String,
    fabricMetersUsed: Double,
    sewingWagePerItem: Long, // هزینه خیاط‌کار
    consumables: List<ProductionConsumableInputItem>,
    note: String = ""
  ): Pair<Boolean, String> {
    if (quantity <= 0) {
      return Pair(false, "تعداد تولید باید بیشتر از صفر باشد")
    }

    val currentDate = PersianDateHelper.getCurrentPersianDate()
    val timestamp = System.currentTimeMillis()

    // 1. Fabric calculations
    var fabricPricePerMeter = 210000L
    var roll: FabricRollEntity? = null
    if (rollId != null && rollId > 0L) {
      roll = database.fabricRollDao().getRollById(rollId)
      if (roll != null) {
        if (!FinancialCalculationService.validateRollCapacity(roll.remainingMeters, fabricMetersUsed)) {
          return Pair(false, "متراژ درخواستی ($fabricMetersUsed متر) از باقیمانده طاقه (${roll.remainingMeters} متر) بیشتر است!")
        }
        fabricPricePerMeter = roll.buyPricePerMeter
      }
    }

    // 2. Calculate consumables total and per item
    val totalConsumablesCost = consumables.sumOf { it.totalCost }
    val consumablesPerItem = if (quantity > 0) totalConsumablesCost / quantity else 0L
    val consumablesSummary = consumables.joinToString("، ") { "${it.accessoryName}: ${it.quantityUsed} ${it.unit}" }

    // 3. Insert Production Record
    val production = ProductionEntity(
      modelCode = modelCode,
      modelName = modelName,
      quantity = quantity,
      fabricRollsUsed = if (roll != null) 1 else 0,
      fabricMetersUsed = fabricMetersUsed,
      totalWeightKg = (fabricMetersUsed * 0.28), // Approximation based on density
      sewingWagePerItem = sewingWagePerItem,
      fabricPricePerMeter = fabricPricePerMeter,
      accessoriesCostPerItem = consumablesPerItem,
      status = SaleOrderStatus.IN_SEWING,
      date = currentDate,
      rollId = roll?.id,
      rollCode = roll?.rollCode ?: rollCode,
      consumablesSummary = consumablesSummary,
      orderId = null
    )
    val prodId = database.productionDao().insertProduction(production)

    // 4. Record individual consumables
    for (item in consumables) {
      val entity = ProductionConsumableEntity(
        productionId = prodId,
        accessoryCode = item.accessoryCode,
        accessoryName = item.accessoryName,
        quantityUsed = item.quantityUsed,
        unit = item.unit,
        unitCostPrice = item.unitCostPrice,
        totalCost = item.totalCost,
        date = currentDate
      )
      database.productionConsumableDao().insertConsumable(entity)
    }

    // 5. If roll was used, deduct from roll
    if (roll != null && fabricMetersUsed > 0.0) {
      val newRemaining = FinancialCalculationService.calculateRemainingRollMeters(roll.remainingMeters, fabricMetersUsed)
      val newStatus = if (newRemaining <= 0.5) "پایان یافته" else "در حال مصرف"
      database.fabricRollDao().updateRoll(roll.copy(remainingMeters = newRemaining, status = newStatus))

      val usage = RollUsageEntity(
        rollId = roll.id,
        rollCode = roll.rollCode,
        productionId = prodId,
        cuttingId = 0L,
        modelCode = modelCode,
        modelName = modelName,
        metersUsed = fabricMetersUsed,
        weightKgUsed = fabricMetersUsed * 0.28,
        usageDate = currentDate,
        usageTimestamp = timestamp,
        allocatedFabricCost = (fabricMetersUsed * roll.buyPricePerMeter).toLong(),
        allocatedShippingCost = if (roll.initialMeters > 0.0) ((fabricMetersUsed / roll.initialMeters) * roll.allocatedShippingCost).toLong() else 0L,
        note = "ثبت تولید $modelName ($note)"
      )
      database.rollUsageDao().insertUsage(usage)
    }

    // 6. Update inventory timestamp
    val existingInv = database.inventoryDao().getByCode(modelCode)
    if (existingInv != null) {
      database.inventoryDao().updateItem(
        existingInv.copy(
          lastUpdated = currentDate
        )
      )
    }

    return Pair(true, "تولید تیراژ $quantity عدد $modelName با ثبت ملزومات و هزینه خیاط‌کار ثبت شد.")
  }

  // ====================================================
  // ENTERPRISE WORKFLOW: SALES & STOCK RESERVATION
  // ====================================================

  suspend fun createSaleOrderWithReservation(
    customerId: Long?,
    customerName: String,
    customerPhone: String,
    modelCode: String,
    modelName: String,
    quantity: Int,
    unitPrice: Long,
    discountAmount: Long = 0L,
    paidAmount: Long = 0L,
    channel: String = "فروش حضوری",
    color: String = "",
    size: String = "",
    variantId: Long? = null,
    shippingCost: Long = 0L
  ): Pair<Boolean, String> = database.withTransaction {
    if (quantity <= 0) return@withTransaction Pair(false, "تعداد سفارش نامعتبر است")
    val currentDate = PersianDateHelper.getCurrentPersianDate()
    val orderNumber = "ORD-${System.currentTimeMillis().toString().takeLast(6)}"

    // Snapshot of current product cost and sale price
    val product = database.productDao().getProductByCode(modelCode)
    val costSnapshot = product?.currentCostPrice ?: 0L
    val salePriceSnapshot = product?.suggestedSellingPrice ?: unitPrice

    // Inventory reservation check (Scenario 9: reserve available stock, surplus requires production)
    val invItem = database.inventoryDao().getByCode(modelCode)
    val available = invItem?.availableForSale ?: 0
    val reserveQty = minOf(quantity, maxOf(0, available))
    val deficitToProduce = quantity - reserveQty
    val isFullyReserved = reserveQty == quantity

    if (invItem != null && reserveQty > 0) {
      val updatedInv = invItem.copy(
        availableForSale = invItem.availableForSale - reserveQty,
        reservedQuantity = invItem.reservedQuantity + reserveQty,
        lastUpdated = currentDate
      )
      database.inventoryDao().updateItem(updatedInv)

      // Record Reservation in ledger
      database.inventoryLedgerDao().insert(
        InventoryLedgerEntity(
          timestamp = System.currentTimeMillis(),
          date = currentDate,
          itemType = "FINISHED_GOOD",
          itemId = invItem.id,
          itemCode = modelCode,
          itemName = modelName,
          color = color,
          size = size,
          transactionType = "SALE_RESERVATION",
          quantityChange = -reserveQty.toDouble(),
          balanceAfter = updatedInv.availableForSale.toDouble(),
          unit = "عدد",
          unitPriceAtTime = costSnapshot,
          relatedDocumentNumber = orderNumber,
          notes = if (deficitToProduce > 0) "رزرو $reserveQty عدد (کسری نیازمند تولید: $deficitToProduce عدد) برای سفارش $orderNumber ($customerName)" else "رزرو کامل $reserveQty عدد برای سفارش $orderNumber ($customerName)",
          operator = "مدیر سیستم"
        )
      )
    }

    val initialStatus = when {
      isFullyReserved -> "آماده ارسال"
      reserveQty > 0 -> "رزرو بخشی ($reserveQty از $quantity عدد) - نیازمند تولید $deficitToProduce عدد"
      else -> "در انتظار تولید ($quantity عدد)"
    }

    val order = SaleOrderEntity(
      orderNumber = orderNumber,
      customerName = customerName,
      customerPhone = customerPhone,
      modelCode = modelCode,
      modelName = modelName,
      quantity = quantity,
      unitPrice = unitPrice,
      unitCost = costSnapshot,
      discountAmount = discountAmount,
      paidAmount = paidAmount,
      orderDate = currentDate,
      deliveryStatus = initialStatus,
      channel = channel,
      customerId = customerId,
      color = color,
      size = size,
      variantId = variantId,
      shippingCost = shippingCost,
      costSnapshot = costSnapshot,
      salePriceSnapshot = salePriceSnapshot
    )
    val orderId = database.saleOrderDao().insertOrder(order)

    // Update Customer debt & purchases
    if (customerId != null) {
      val customer = database.customerDao().getCustomerById(customerId)
      if (customer != null) {
        val updatedCust = customer.copy(
          totalPurchases = customer.totalPurchases + order.netTotal,
          totalPaid = customer.totalPaid + paidAmount,
          currentDebt = customer.currentDebt + order.remainingDebt,
          orderCount = customer.orderCount + 1,
          lastOrderDate = currentDate
        )
        database.customerDao().updateCustomer(updatedCust)
      }
    } else {
      val customer = database.customerDao().getCustomerByName(customerName)
      if (customer != null) {
        val updatedCust = customer.copy(
          totalPurchases = customer.totalPurchases + order.netTotal,
          totalPaid = customer.totalPaid + paidAmount,
          currentDebt = customer.currentDebt + order.remainingDebt,
          orderCount = customer.orderCount + 1,
          lastOrderDate = currentDate
        )
        database.customerDao().updateCustomer(updatedCust)
      }
    }

    // Record payment if paidAmount > 0
    if (paidAmount > 0L) {
      database.customerPaymentDao().insert(
        CustomerPaymentEntity(
          customerId = customerId ?: 0L,
          customerName = customerName,
          orderId = orderId,
          orderNumber = orderNumber,
          amount = paidAmount,
          date = currentDate,
          timestamp = System.currentTimeMillis(),
          paymentMethod = "پیش‌پرداخت سفارش",
          referenceNumber = "ADV-$orderNumber",
          notes = "پیش‌پرداخت سفارش $orderNumber",
          recordedBy = "مدیر سیستم"
        )
      )
    }

    // Audit log
    database.auditLogDao().insert(
      AuditLogEntity(
        timestamp = System.currentTimeMillis(),
        date = currentDate,
        entityName = "SaleOrder",
        entityId = orderId,
        action = "CREATE_ORDER",
        oldValue = "",
        newValue = "سفارش $orderNumber با مبلغ ${order.netTotal} تومان ثبت شد. رزرو: $reserveQty عدد، کسری: $deficitToProduce عدد",
        reason = "ثبت سفارش جدید از کانال $channel",
        recordedBy = "مدیر سیستم"
      )
    )

    val resMsg = when {
      isFullyReserved -> "موجودی کالا کامل رزرو شد ($quantity عدد)."
      reserveQty > 0 -> "$reserveQty عدد از انبار رزرو شد و $deficitToProduce عدد در انتظار تولید قرار گرفت."
      else -> "موجودی انبار صفر است؛ تمام $quantity عدد در نوبت تولید قرار گرفت."
    }
    Pair(true, "سفارش $orderNumber با موفقیت ثبت شد. $resMsg")
  }

  suspend fun updateSaleOrderStatus(
    orderId: Long,
    newStatus: String,
    notes: String = "",
    operator: String = "مدیر سیستم"
  ): Pair<Boolean, String> = database.withTransaction {
    val order = database.saleOrderDao().getOrderById(orderId)
      ?: return@withTransaction Pair(false, "سفارش با شناسه $orderId یافت نشد")
    val oldStatus = order.deliveryStatus
    if (oldStatus == newStatus) return@withTransaction Pair(true, "وضعیت تغییر نکرد")

    val currentDate = PersianDateHelper.getCurrentPersianDate()
    val currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
    val now = System.currentTimeMillis()

    // Status history
    database.orderStatusHistoryDao().insertHistory(
      OrderStatusHistoryEntity(
        orderId = order.id,
        orderNumber = order.orderNumber,
        oldStatus = oldStatus,
        newStatus = newStatus,
        date = currentDate,
        time = currentTime,
        timestamp = now,
        note = notes
      )
    )

    // Handle inventory transitions
    val inv = database.inventoryDao().getByCode(order.modelCode)
    if (inv != null) {
      if (newStatus == "ارسال شده" || newStatus == "تحویل شده") {
        val newReady = (inv.readyForShipment - order.quantity).coerceAtLeast(0)
        val newReserved = (inv.reservedQuantity - order.quantity).coerceAtLeast(0)
        database.inventoryDao().updateItem(
          inv.copy(
            readyForShipment = newReady,
            reservedQuantity = newReserved,
            lastUpdated = currentDate
          )
        )
        database.inventoryLedgerDao().insert(
          InventoryLedgerEntity(
            timestamp = now,
            date = currentDate,
            itemType = "FINISHED_GOOD",
            itemId = inv.id,
            itemCode = order.modelCode,
            itemName = order.modelName,
            color = order.color,
            size = order.size,
            transactionType = "SALE_SHIPMENT",
            quantityChange = -order.quantity.toDouble(),
            balanceAfter = (newReady + newReserved + inv.availableForSale).toDouble(),
            unit = "عدد",
            unitPriceAtTime = order.costSnapshot,
            relatedDocumentNumber = order.orderNumber,
            notes = "خروج سفارش ${order.orderNumber} به مقصد ${order.customerName}",
            operator = operator
          )
        )
      } else if (newStatus == "لغو شده") {
        val newReserved = (inv.reservedQuantity - order.quantity).coerceAtLeast(0)
        val newAvailable = inv.availableForSale + order.quantity
        database.inventoryDao().updateItem(
          inv.copy(
            reservedQuantity = newReserved,
            availableForSale = newAvailable,
            lastUpdated = currentDate
          )
        )
        val cust = if (order.customerId != null) database.customerDao().getCustomerById(order.customerId) else database.customerDao().getCustomerByName(order.customerName)
        if (cust != null) {
          database.customerDao().updateCustomer(
            cust.copy(
              totalPurchases = (cust.totalPurchases - order.netTotal).coerceAtLeast(0L),
              currentDebt = (cust.currentDebt - order.remainingDebt).coerceAtLeast(0L)
            )
          )
        }
        database.inventoryLedgerDao().insert(
          InventoryLedgerEntity(
            timestamp = now,
            date = currentDate,
            itemType = "FINISHED_GOOD",
            itemId = inv.id,
            itemCode = order.modelCode,
            itemName = order.modelName,
            color = order.color,
            size = order.size,
            transactionType = "CANCEL_RESERVATION",
            quantityChange = order.quantity.toDouble(),
            balanceAfter = (inv.readyForShipment + newReserved + newAvailable).toDouble(),
            unit = "عدد",
            unitPriceAtTime = order.costSnapshot,
            relatedDocumentNumber = order.orderNumber,
            notes = "لغو سفارش ${order.orderNumber} و آزادسازی ${order.quantity} عدد کالای رزرو شده",
            operator = operator
          )
        )
      }
    }

    database.saleOrderDao().updateOrder(order.copy(deliveryStatus = newStatus))
    database.auditLogDao().insert(
      AuditLogEntity(
        timestamp = now,
        date = currentDate,
        entityName = "SaleOrder",
        entityId = orderId,
        action = "STATUS_CHANGE",
        oldValue = oldStatus,
        newValue = newStatus,
        reason = notes.ifBlank { "تغییر وضعیت سفارش" },
        recordedBy = operator
      )
    )

    Pair(true, "وضعیت سفارش به $newStatus تغییر یافت")
  }

  // ====================================================
  // FINANCIAL ACCOUNTING: PAYMENTS & DEBT
  // ====================================================

  suspend fun recordCustomerPayment(
    customerId: Long,
    customerName: String,
    orderId: Long?,
    orderNumber: String,
    amount: Long,
    paymentMethod: String = "کارت به کارت",
    referenceNumber: String = "",
    notes: String = "",
    recordedBy: String = "حسابدار"
  ): Pair<Boolean, String> = database.withTransaction {
    if (amount <= 0L) return@withTransaction Pair(false, "مبلغ پرداختی باید بیشتر از صفر باشد")
    val currentDate = PersianDateHelper.getCurrentPersianDate()
    val now = System.currentTimeMillis()

    // 1. Insert payment record
    val payment = CustomerPaymentEntity(
      customerId = customerId,
      customerName = customerName,
      orderId = orderId,
      orderNumber = orderNumber,
      amount = amount,
      date = currentDate,
      timestamp = now,
      paymentMethod = paymentMethod,
      referenceNumber = referenceNumber,
      notes = notes,
      recordedBy = recordedBy
    )
    database.customerPaymentDao().insert(payment)

    // 2. Update Customer balance
    val customer = database.customerDao().getCustomerById(customerId)
      ?: database.customerDao().getCustomerByName(customerName)
    if (customer != null) {
      val newTotalPaid = customer.totalPaid + amount
      val newDebt = (customer.currentDebt - amount).coerceAtLeast(0L)
      database.customerDao().updateCustomer(
        customer.copy(
          totalPaid = newTotalPaid,
          currentDebt = newDebt
        )
      )
    }

    // 3. Update Order if linked
    if (orderId != null && orderId > 0L) {
      val order = database.saleOrderDao().getOrderById(orderId)
      if (order != null) {
        val newOrderPaid = order.paidAmount + amount
        database.saleOrderDao().updateOrder(
          order.copy(paidAmount = newOrderPaid)
        )
      }
    }

    // 4. Audit Log
    database.auditLogDao().insert(
      AuditLogEntity(
        timestamp = now,
        date = currentDate,
        entityName = "CustomerPayment",
        entityId = customerId,
        action = "RECEIVE_PAYMENT",
        oldValue = "",
        newValue = "دریافت مبلغ $amount تومان از $customerName (روش: $paymentMethod)",
        reason = notes.ifBlank { "ثبت واریزی مشتری" },
        recordedBy = recordedBy
      )
    )

    Pair(true, "پرداخت مبلغ ${FinancialCalculationService.formatCurrency(amount)} ثبت و بدهی مشتری بروزرسانی شد.")
  }

  suspend fun recordSupplierPayment(
    supplierId: Long,
    supplierName: String,
    purchaseOrderId: Long?,
    orderNumber: String,
    amount: Long,
    paymentMethod: String = "حواله پایا/ساتنا",
    referenceNumber: String = "",
    notes: String = "",
    recordedBy: String = "مدیر مالی"
  ): Pair<Boolean, String> = database.withTransaction {
    if (amount <= 0L) return@withTransaction Pair(false, "مبلغ پرداختی باید بیشتر از صفر باشد")
    val currentDate = PersianDateHelper.getCurrentPersianDate()
    val now = System.currentTimeMillis()

    database.supplierPaymentDao().insert(
      SupplierPaymentEntity(
        supplierId = supplierId,
        supplierName = supplierName,
        purchaseOrderId = purchaseOrderId,
        orderNumber = orderNumber,
        amount = amount,
        date = currentDate,
        timestamp = now,
        paymentMethod = paymentMethod,
        referenceNumber = referenceNumber,
        notes = notes,
        recordedBy = recordedBy
      )
    )

    val supplier = database.supplierDao().getSupplierById(supplierId)
      ?: database.supplierDao().getSupplierByName(supplierName)
    if (supplier != null) {
      val newPaid = supplier.paidAmount + amount
      val newDebt = (supplier.currentDebt - amount).coerceAtLeast(0L)
      database.supplierDao().updateSupplier(
        supplier.copy(
          paidAmount = newPaid,
          currentDebt = newDebt
        )
      )
    }

    if (purchaseOrderId != null && purchaseOrderId > 0L) {
      val po = database.purchaseOrderDao().getById(purchaseOrderId)
      if (po != null) {
        val newPoPaid = po.paidAmount + amount
        database.purchaseOrderDao().update(po.copy(paidAmount = newPoPaid))
      }
    }

    database.auditLogDao().insert(
      AuditLogEntity(
        timestamp = now,
        date = currentDate,
        entityName = "SupplierPayment",
        entityId = supplierId,
        action = "PAY_SUPPLIER",
        oldValue = "",
        newValue = "پرداخت مبلغ $amount تومان به تأمین‌کننده $supplierName",
        reason = notes.ifBlank { "تسویه حساب تأمین‌کننده" },
        recordedBy = recordedBy
      )
    )

    Pair(true, "پرداخت به تأمین‌کننده ثبت و مانده حساب بروزرسانی شد.")
  }

  // ====================================================
  // INVENTORY ADJUSTMENT & AUDIT RECONCILIATION
  // ====================================================

  suspend fun recordInventoryAdjustment(
    itemType: String, // "MATERIAL", "FABRIC_ROLL", "FINISHED_GOOD"
    itemId: Long,
    itemCode: String,
    itemName: String,
    color: String = "",
    size: String = "",
    adjustmentType: String, // "INCREASE", "DECREASE"
    quantity: Double,
    reason: String,
    notes: String = "",
    operator: String = "مدیر انبار"
  ): Pair<Boolean, String> = database.withTransaction {
    if (quantity <= 0.0) return@withTransaction Pair(false, "مقدار تعدیل باید بیشتر از صفر باشد")
    val currentDate = PersianDateHelper.getCurrentPersianDate()
    val now = System.currentTimeMillis()
    var currentBalance = 0.0

    when (itemType) {
      "MATERIAL" -> {
        val mat = database.materialDao().getById(itemId)
          ?: return@withTransaction Pair(false, "ماده اولیه با شناسه $itemId یافت نشد")
        val newQty = if (adjustmentType == "INCREASE") mat.stockQuantity + quantity else mat.stockQuantity - quantity
        if (newQty < 0.0) return@withTransaction Pair(false, "موجودی ماده اولیه نمی‌تواند منفی شود (موجودی فعلی: ${mat.stockQuantity})")
        database.materialDao().update(mat.copy(stockQuantity = newQty))
        currentBalance = newQty
      }
      "FABRIC_ROLL" -> {
        val roll = database.fabricRollDao().getRollById(itemId)
          ?: return@withTransaction Pair(false, "طاقه با شناسه $itemId یافت نشد")
        val newMeters = if (adjustmentType == "INCREASE") roll.remainingMeters + quantity else roll.remainingMeters - quantity
        if (newMeters < 0.0) return@withTransaction Pair(false, "متراژ طاقه نمی‌تواند منفی شود (باقیمانده فعلی: ${roll.remainingMeters})")
        val newStatus = if (newMeters <= 0.5) "پایان یافته" else "در حال مصرف"
        database.fabricRollDao().updateRoll(roll.copy(remainingMeters = newMeters, status = newStatus))
        currentBalance = newMeters
      }
      "FINISHED_GOOD" -> {
        val inv = database.inventoryDao().getByCode(itemCode)
          ?: return@withTransaction Pair(false, "کالای نهایی با کد $itemCode یافت نشد")
        val intQty = quantity.toInt()
        val newAvailable = if (adjustmentType == "INCREASE") inv.availableForSale + intQty else inv.availableForSale - intQty
        val newReady = if (adjustmentType == "INCREASE") inv.readyForShipment + intQty else inv.readyForShipment - intQty
        if (newAvailable < 0) {
          return@withTransaction Pair(false, "موجودی کالای نهایی نمی‌تواند منفی شود (موجودی آزاد: ${inv.availableForSale})")
        }
        database.inventoryDao().updateItem(
          inv.copy(
            availableForSale = newAvailable,
            readyForShipment = newReady.coerceAtLeast(0),
            lastUpdated = currentDate
          )
        )
        currentBalance = (inv.readyForShipment + inv.reservedQuantity + newAvailable).toDouble()
      }
      else -> return@withTransaction Pair(false, "نوع کالای نامعتبر")
    }

    // Record adjustment
    database.inventoryAdjustmentDao().insert(
      InventoryAdjustmentEntity(
        timestamp = now,
        date = currentDate,
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
    )

    // Ledger & Audit
    val signedQty = if (adjustmentType == "INCREASE") quantity else -quantity
    database.inventoryLedgerDao().insert(
      InventoryLedgerEntity(
        timestamp = now,
        date = currentDate,
        itemType = itemType,
        itemId = itemId,
        itemCode = itemCode,
        itemName = itemName,
        color = color,
        size = size,
        transactionType = "ADJUSTMENT_$adjustmentType",
        quantityChange = signedQty,
        balanceAfter = currentBalance,
        unit = if (itemType == "FABRIC_ROLL") "متر" else if (itemType == "MATERIAL") "واحد" else "عدد",
        unitPriceAtTime = 0L,
        relatedDocumentNumber = reason,
        notes = "$reason (${if (adjustmentType == "INCREASE") "+" else "-"}$quantity)",
        operator = operator
      )
    )

    database.auditLogDao().insert(
      AuditLogEntity(
        timestamp = now,
        date = currentDate,
        entityName = "InventoryAdjustment",
        entityId = itemId,
        action = "ADJUST_${itemType}_$adjustmentType",
        oldValue = "",
        newValue = "$adjustmentType $quantity برای $itemName (دلیل: $reason)",
        reason = reason,
        recordedBy = operator
      )
    )

    Pair(true, "انبارگردانی و تعدیل موجودی با موفقیت ثبت شد.")
  }

  // ====================================================
  // SHIPPING RATE UPDATE WITH AUDIT
  // ====================================================

  suspend fun updateShippingRate(
    newRate: Long,
    reason: String = "تغییر تعرفه پایه باربری",
    source: String = "پایانه باربری",
    notes: String = "",
    recordedBy: String = "مدیر کارگاه"
  ): Pair<Boolean, String> {
    val settings = database.factorySettingsDao().getSettingsOnce() ?: com.example.data.model.FactorySettingsEntity()
    val oldRate = settings.fixedShippingCostPerOrder
    val changeAmount = newRate - oldRate
    val changePercent = if (oldRate > 0L) ((changeAmount.toDouble() / oldRate.toDouble()) * 100.0) else 0.0
    val currentDate = PersianDateHelper.getCurrentPersianDate()
    val now = System.currentTimeMillis()

    database.shippingRateHistoryDao().insert(
      ShippingRateHistoryEntity(
        oldRate = oldRate,
        newRate = newRate,
        changeAmount = changeAmount,
        changePercent = changePercent,
        date = currentDate,
        timestamp = now,
        reason = reason,
        source = source,
        notes = notes,
        recordedBy = recordedBy
      )
    )

    database.factorySettingsDao().insertOrUpdate(settings.copy(fixedShippingCostPerOrder = newRate))

    database.auditLogDao().insert(
      AuditLogEntity(
        timestamp = now,
        date = currentDate,
        entityName = "FactorySettings",
        entityId = settings.id,
        action = "UPDATE_SHIPPING_RATE",
        oldValue = "$oldRate تومان",
        newValue = "$newRate تومان",
        reason = reason,
        recordedBy = recordedBy
      )
    )

    return Pair(true, "نرخ پایه باربری با موفقیت بروزرسانی و در تاریخچه ثبت شد.")
  }

  // ==========================================
  // PHASE 3 & 4: SALES RETURN & ATOMIC STOCK RESTORE
  // ==========================================
  suspend fun recordSaleReturn(
    orderId: Long,
    returnQuantity: Int,
    reason: String,
    returnToStock: Boolean = true,
    refundAmount: Long = 0L,
    operator: String = "مدیر سیستم"
  ): Pair<Boolean, String> = database.withTransaction {
    val order = database.saleOrderDao().getOrderById(orderId)
      ?: return@withTransaction Pair(false, "سفارش با شناسه $orderId یافت نشد")
    if (returnQuantity <= 0 || returnQuantity > order.quantity) {
      return@withTransaction Pair(false, "تعداد مرجوعی نامعتبر است (حداکثر ${order.quantity} عدد)")
    }
    val currentDate = PersianDateHelper.getCurrentPersianDate()
    val now = System.currentTimeMillis()

    // 1. If returnToStock is true, add back to available inventory
    val inv = database.inventoryDao().getByCode(order.modelCode)
    if (inv != null && returnToStock) {
      val newAvailable = inv.availableForSale + returnQuantity
      database.inventoryDao().updateItem(
        inv.copy(
          availableForSale = newAvailable,
          lastUpdated = currentDate
        )
      )
      // Inventory Ledger
      database.inventoryLedgerDao().insert(
        InventoryLedgerEntity(
          timestamp = now,
          date = currentDate,
          itemType = "FINISHED_GOOD",
          itemId = inv.id,
          itemCode = order.modelCode,
          itemName = order.modelName,
          color = order.color,
          size = order.size,
          transactionType = "SALE_RETURN",
          quantityChange = returnQuantity.toDouble(),
          balanceAfter = (inv.readyForShipment + inv.reservedQuantity + newAvailable).toDouble(),
          unit = "عدد",
          unitPriceAtTime = order.costSnapshot,
          relatedDocumentNumber = order.orderNumber,
          notes = "مرجوعی $returnQuantity عدد از سفارش ${order.orderNumber} (دلیل: $reason)",
          operator = operator
        )
      )
    }

    // 2. Adjust Customer Account (Debt and Total Purchases)
    val returnAmount = returnQuantity * order.effectiveUnitPrice
    val cust = if (order.customerId != null) database.customerDao().getCustomerById(order.customerId) else database.customerDao().getCustomerByName(order.customerName)
    if (cust != null) {
      val newPurchases = (cust.totalPurchases - returnAmount).coerceAtLeast(0L)
      val newDebt = (cust.currentDebt - returnAmount + refundAmount).coerceAtLeast(0L)
      database.customerDao().updateCustomer(
        cust.copy(
          totalPurchases = newPurchases,
          currentDebt = newDebt
        )
      )
    }

    // 3. Update Order status
    val newOrderStatus = if (returnQuantity == order.quantity) "مرجوع شده کامل" else "مرجوعی بخشی (${returnQuantity} عدد)"
    database.saleOrderDao().updateOrder(order.copy(deliveryStatus = newOrderStatus))

    // 4. Audit Log
    database.auditLogDao().insert(
      AuditLogEntity(
        timestamp = now,
        date = currentDate,
        entityName = "SaleOrder",
        entityId = orderId,
        action = "SALE_RETURN",
        oldValue = order.deliveryStatus,
        newValue = newOrderStatus,
        reason = reason,
        recordedBy = operator
      )
    )

    Pair(true, "مرجوعی با موفقیت ثبت شد و حساب مشتری و موجودی کالا بروزرسانی گردید.")
  }

  // ==========================================
  // PHASE 3 & 4: PURCHASE ORDERS & PARTIAL DELIVERY
  // ==========================================
  suspend fun recordPurchaseOrderWithDelivery(
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
    operator: String = "مدیر تدارکات"
  ): Pair<Boolean, String> = database.withTransaction {
    if (orderedQuantity <= 0.0 || deliveredQuantity < 0.0 || unitPrice <= 0L) {
      return@withTransaction Pair(false, "مقادیر و قیمت وارد شده نامعتبر است")
    }
    val currentDate = PersianDateHelper.getCurrentPersianDate()
    val now = System.currentTimeMillis()
    val poNumber = "PO-${now.toString().takeLast(6)}"

    // Delivered inventory goods total
    val deliveredGross = (deliveredQuantity * unitPrice).toLong()
    val totalBillAmount = (deliveredGross + shippingCost - discountAmount).coerceAtLeast(0L)
    val debtAmount = (totalBillAmount - paidAmount).coerceAtLeast(0L)
    val pendingQty = (orderedQuantity - deliveredQuantity).coerceAtLeast(0.0)

    val purchaseOrder = PurchaseOrderEntity(
      orderNumber = poNumber,
      supplierId = supplierId,
      supplierName = supplierName,
      orderDate = currentDate,
      totalAmount = totalBillAmount,
      shippingAmount = shippingCost,
      paidAmount = paidAmount,
      status = if (pendingQty <= 0.0) "تحویل شده" else "ثبت شده (کسری ${pendingQty.toInt()})",
      notes = "$notes | سفارش: $orderedQuantity | تحویل: $deliveredQuantity"
    )
    val poId = database.purchaseOrderDao().insert(purchaseOrder)

    // Insert item
    database.purchaseItemDao().insert(
      PurchaseItemEntity(
        purchaseOrderId = poId,
        materialId = materialId,
        materialName = materialName,
        orderedQuantity = orderedQuantity,
        deliveredQuantity = deliveredQuantity,
        unitPrice = unitPrice,
        unit = "کیلو/واحد",
        totalPrice = deliveredGross
      )
    )

    // Update Material Stock & Last Purchase Price
    val mat = database.materialDao().getById(materialId)
    if (mat != null) {
      val newStock = mat.stockQuantity + deliveredQuantity
      database.materialDao().update(
        mat.copy(
          stockQuantity = newStock,
          lastPurchasePrice = unitPrice,
          lastPriceSource = "PURCHASE",
          lastPriceChangeDate = currentDate,
          lastPriceChangeTimestamp = now
        )
      )

      // Ledger
      if (deliveredQuantity > 0.0) {
        database.inventoryLedgerDao().insert(
          InventoryLedgerEntity(
            timestamp = now,
            date = currentDate,
            itemType = "MATERIAL",
            itemId = materialId,
            itemCode = mat.code,
            itemName = materialName,
            transactionType = "PURCHASE_RECEIPT",
            quantityChange = deliveredQuantity,
            balanceAfter = newStock,
            unit = mat.unit,
            unitPriceAtTime = unitPrice,
            relatedDocumentNumber = poNumber,
            notes = "ورود خرید از تأمین‌کننده $supplierName ($deliveredQuantity از $orderedQuantity)",
            operator = operator
          )
        )
      }
    }

    // Update Supplier debt & purchases
    val sup = database.supplierDao().getSupplierById(supplierId)
      ?: database.supplierDao().getSupplierByName(supplierName)
    if (sup != null) {
      database.supplierDao().updateSupplier(
        sup.copy(
          totalPurchases = sup.totalPurchases + totalBillAmount,
          paidAmount = sup.paidAmount + paidAmount,
          currentDebt = sup.currentDebt + debtAmount,
          lastPurchaseDate = currentDate
        )
      )
    }

    if (paidAmount > 0L) {
      database.supplierPaymentDao().insert(
        SupplierPaymentEntity(
          supplierId = supplierId,
          supplierName = supplierName,
          purchaseOrderId = poId,
          orderNumber = poNumber,
          amount = paidAmount,
          date = currentDate,
          timestamp = now,
          paymentMethod = "پرداخت فاکتور خرید",
          referenceNumber = "PAY-$poNumber",
          notes = "پرداخت همزمان با فاکتور خرید $poNumber",
          recordedBy = operator
        )
      )
    }

    // Audit log
    database.auditLogDao().insert(
      AuditLogEntity(
        timestamp = now,
        date = currentDate,
        entityName = "PurchaseOrder",
        entityId = poId,
        action = "CREATE_PURCHASE",
        oldValue = "",
        newValue = "خرید $poNumber به مبلغ $totalBillAmount از $supplierName (تحویل: $deliveredQuantity, مانده: $pendingQty)",
        reason = "خرید مواد اولیه و ملزومات",
        recordedBy = operator
      )
    )

    Pair(true, "فاکتور خرید $poNumber با موفقیت ثبت و موجودی مواد اولیه و حساب تأمین‌کننده بروزرسانی گردید.")
  }

  // ==========================================
  // PHASE 3 & 4: BOM ATOMIC PRODUCTION WITH SHORTAGE CHECK
  // ==========================================
  suspend fun produceAtomicFromBOM(
    productId: Long,
    color: String = "مشکی",
    size: String = "L",
    quantity: Int,
    tailorWagePerItem: Long = 0L,
    operator: String = "مدیر تولید"
  ): Pair<Boolean, String> = database.withTransaction {
    if (quantity <= 0) return@withTransaction Pair(false, "تعداد تولید باید بیشتر از صفر باشد")
    val product = database.productDao().getProductById(productId)
      ?: return@withTransaction Pair(false, "محصول با شناسه $productId یافت نشد")

    val boms = database.productBOMDao().getBOMListForProduct(productId)
    val currentDate = PersianDateHelper.getCurrentPersianDate()
    val now = System.currentTimeMillis()

    // 1. Validation phase: check if all BOM materials exist with sufficient quantity
    val shortages = mutableListOf<String>()
    val materialsToDeduct = mutableListOf<Pair<MaterialEntity, Double>>()
    var totalBomMaterialCostPerUnit = 0L

    for (bom in boms) {
      val needed = bom.standardQuantity * quantity
      val mat = database.materialDao().getById(bom.materialId)
      if (mat == null) {
        shortages.add("ماده اولیه «${bom.materialName}» در سیستم یافت نشد.")
      } else if (mat.stockQuantity < needed) {
        val shortage = needed - mat.stockQuantity
        val unitLabel = mat.unit.ifBlank { bom.unit }
        shortages.add("«${mat.name}»: نیاز: ${String.format(java.util.Locale.US, "%.2f", needed)} $unitLabel | موجودی فعلی: ${String.format(java.util.Locale.US, "%.2f", mat.stockQuantity)} $unitLabel | کسری: ${String.format(java.util.Locale.US, "%.2f", shortage)} $unitLabel")
      } else {
        materialsToDeduct.add(Pair(mat, needed))
        totalBomMaterialCostPerUnit += (bom.standardQuantity * mat.currentPrice).toLong()
      }
    }

    // ATOMIC FAIL: If any material has shortage, STOP! No materials deducted, no production created!
    if (shortages.isNotEmpty()) {
      val errorMsg = "امکان تولید وجود ندارد! کسری مواد اولیه:\n" + shortages.joinToString("\n")
      return@withTransaction Pair(false, errorMsg)
    }

    // 2. Execution phase: All materials sufficient -> Deduct materials and log ledger
    for ((mat, needed) in materialsToDeduct) {
      val newStock = mat.stockQuantity - needed
      database.materialDao().update(mat.copy(stockQuantity = newStock))

      database.inventoryLedgerDao().insert(
        InventoryLedgerEntity(
          timestamp = now,
          date = currentDate,
          itemType = "MATERIAL",
          itemId = mat.id,
          itemCode = mat.code,
          itemName = mat.name,
          color = color,
          size = size,
          transactionType = "PRODUCTION_CONSUMPTION",
          quantityChange = -needed,
          balanceAfter = newStock,
          unit = mat.unit,
          unitPriceAtTime = mat.currentPrice,
          relatedDocumentNumber = "PROD-${product.code}",
          notes = "مصرف برای تولید $quantity عدد ${product.name}",
          operator = operator
        )
      )
    }

    // 3. Snapshot costing
    val effectiveSewingWage = if (tailorWagePerItem > 0L) tailorWagePerItem else product.sewingWage
    val unitCostSnapshot = totalBomMaterialCostPerUnit + effectiveSewingWage + product.allocatedFreightCost + product.overheadCost

    // 4. Record Production Entity
    val prodBatch = "PRD-${now.toString().takeLast(6)}"
    database.productionDao().insertProduction(
      ProductionEntity(
        modelCode = product.code,
        modelName = product.name,
        quantity = quantity,
        fabricRollsUsed = 1,
        fabricMetersUsed = totalBomMaterialCostPerUnit.toDouble(),
        totalWeightKg = (quantity * 400.0) / 1000.0,
        sewingWagePerItem = effectiveSewingWage,
        fabricPricePerMeter = totalBomMaterialCostPerUnit,
        accessoriesCostPerItem = product.allocatedFreightCost + product.overheadCost,
        status = "آماده ارسال / تکمیل موجودی",
        date = currentDate,
        consumablesSummary = "تولید اتمیک از فرمول BOM ($quantity عدد)"
      )
    )

    // 5. Add Finished Goods to Warehouse Inventory
    val invItem = database.inventoryDao().getByCode(product.code)
    if (invItem != null) {
      val newAvailable = invItem.availableForSale + quantity
      val newReady = invItem.readyForShipment + quantity
      database.inventoryDao().updateItem(
        invItem.copy(
          availableForSale = newAvailable,
          readyForShipment = newReady,
          unitCostPrice = unitCostSnapshot,
          unitSalePrice = product.effectiveSellingPrice,
          lastUpdated = currentDate
        )
      )
    } else {
      database.inventoryDao().insertItem(
        InventoryEntity(
          name = product.name,
          code = product.code,
          category = "محصولات آماده",
          readyForShipment = quantity,
          reservedQuantity = 0,
          availableForSale = quantity,
          unitSalePrice = product.effectiveSellingPrice,
          unitCostPrice = unitCostSnapshot,
          unitWeightGrams = 400.0,
          lastUpdated = currentDate
        )
      )
    }

    // Ledger for finished goods
    database.inventoryLedgerDao().insert(
      InventoryLedgerEntity(
        timestamp = now,
        date = currentDate,
        itemType = "FINISHED_GOOD",
        itemId = productId,
        itemCode = product.code,
        itemName = product.name,
        color = color,
        size = size,
        transactionType = "PRODUCTION_RECEIPT",
        quantityChange = quantity.toDouble(),
        balanceAfter = ((invItem?.readyForShipment ?: 0) + quantity).toDouble(),
        unit = "عدد",
        unitPriceAtTime = unitCostSnapshot,
        relatedDocumentNumber = prodBatch,
        notes = "تولید اتمیک $quantity عدد ${product.name} (بهای تمام‌شده هر عدد: ${FinancialCalculationService.formatCurrency(unitCostSnapshot)})",
        operator = operator
      )
    )

    // Audit log
    database.auditLogDao().insert(
      AuditLogEntity(
        timestamp = now,
        date = currentDate,
        entityName = "Production",
        entityId = productId,
        action = "ATOMIC_PRODUCTION",
        oldValue = "",
        newValue = "تولید $quantity عدد ${product.name} (بچ $prodBatch)",
        reason = "تولید اتمیک از روی BOM با بهای تمام‌شده اسنپ‌شات $unitCostSnapshot",
        recordedBy = operator
      )
    )

    Pair(true, "تولید $quantity عدد «${product.name}» با موفقیت انجام شد. تمام مواد اولیه طبق فرمول BOM کسر و به انبار کالای آماده اضافه گردید.")
  }

  // ==========================================
  // PHASE 3 & 4: MARKET PRICE UPDATE WITHOUT PURCHASE & FREE STOCK REVALUATION
  // ==========================================
  suspend fun updateMaterialMarketPrice(
    materialId: Long,
    newPrice: Long,
    reason: String = "افزایش قیمت بازار",
    source: String = "MARKET_UPDATE",
    operator: String = "مدیر کارگاه"
  ): Pair<Boolean, String> {
    if (newPrice <= 0L) return Pair(false, "قیمت جدید باید بیشتر از صفر باشد")
    val mat = database.materialDao().getById(materialId)
      ?: return Pair(false, "ماده اولیه با شناسه $materialId یافت نشد")
    val oldPrice = mat.currentPrice
    val changeAmount = newPrice - oldPrice
    val changePercent = if (oldPrice > 0L) ((changeAmount.toDouble() / oldPrice.toDouble()) * 100.0) else 0.0
    val currentDate = PersianDateHelper.getCurrentPersianDate()
    val now = System.currentTimeMillis()

    // 1. Record Material Price History
    database.materialPriceHistoryDao().insert(
      MaterialPriceHistoryEntity(
        materialId = materialId,
        materialName = mat.name,
        oldPrice = oldPrice,
        newPrice = newPrice,
        date = currentDate,
        timestamp = now,
        changeAmount = changeAmount,
        changePercent = changePercent,
        reason = reason,
        source = source,
        supplierName = mat.supplierName,
        recordedBy = operator
      )
    )

    // 2. Update Material Current Price (keeps lastPurchasePrice untouched!)
    database.materialDao().update(
      mat.copy(
        currentPrice = newPrice,
        lastPriceSource = source,
        lastPriceChangeDate = currentDate,
        lastPriceChangeTimestamp = now
      )
    )

    // 3. Find all products using this material in their BOM
    val affectedBoms = database.productBOMDao().getBOMsUsingMaterial(materialId)
    val affectedProductIds = affectedBoms.map { it.productId }.distinct()
    val affectedProductSummaries = mutableListOf<String>()

    for (prodId in affectedProductIds) {
      val product = database.productDao().getProductById(prodId) ?: continue
      val productBoms = database.productBOMDao().getBOMListForProduct(prodId)

      // Recalculate total BOM materials cost
      var newBomMaterialsCost = 0L
      for (b in productBoms) {
        val m = if (b.materialId == materialId) mat.copy(currentPrice = newPrice) else database.materialDao().getById(b.materialId)
        val rate = m?.currentPrice ?: b.unitRate
        newBomMaterialsCost += (b.standardQuantity * rate).toLong()
      }

      val oldCost = product.currentCostPrice
      val newCost = newBomMaterialsCost + product.sewingWage + product.allocatedFreightCost + product.overheadCost
      val costChange = newCost - oldCost
      val costChangePercent = if (oldCost > 0L) ((costChange.toDouble() / oldCost.toDouble()) * 100.0) else 0.0

      // Calculate new suggested selling price based on target profit
      val newSuggestedPrice = if (product.profitCalculationType == "MARGIN") {
        val denom = (1.0 - (product.targetProfitPercent / 100.0)).coerceAtLeast(0.1)
        (newCost.toDouble() / denom).toLong()
      } else {
        (newCost * (1.0 + (product.targetProfitPercent / 100.0))).toLong()
      }

      // Record Product Price History
      database.productPriceHistoryDao().insert(
        ProductPriceHistoryEntity(
          productId = prodId,
          productCode = product.code,
          productName = product.name,
          oldCostPrice = oldCost,
          newCostPrice = newCost,
          oldSalePrice = product.effectiveSellingPrice,
          newSalePrice = newSuggestedPrice,
          date = currentDate,
          timestamp = now,
          reason = "تغییر نرخ بازار ماده «${mat.name}» ($reason)",
          triggeringMaterialId = materialId,
          triggeringMaterialName = mat.name,
          costChangeAmount = costChange,
          costChangePercent = costChangePercent,
          notes = "ثبت شده توسط $operator"
        )
      )

      // Update product cost & suggested selling price
      database.productDao().update(
        product.copy(
          currentCostPrice = newCost,
          suggestedSellingPrice = newSuggestedPrice,
          lastPriceUpdateTimestamp = now,
          lastPriceUpdateDate = currentDate
        )
      )

      // Revalue Free Inventory (availableForSale) while preserving reserved items!
      val inv = database.inventoryDao().getByCode(product.code)
      if (inv != null) {
        database.inventoryDao().updateItem(
          inv.copy(
            unitCostPrice = newCost,
            unitSalePrice = if (!product.isManualPrice) newSuggestedPrice else inv.unitSalePrice,
            previousCostPrice = oldCost,
            previousCostDate = currentDate,
            lastUpdated = currentDate
          )
        )
      }

      affectedProductSummaries.add("«${product.name}»: بهای تمام‌شده از ${FinancialCalculationService.formatCurrency(oldCost)} به ${FinancialCalculationService.formatCurrency(newCost)} (${if (costChange >= 0) "+" else ""}${String.format(java.util.Locale.US, "%.1f", costChangePercent)}%) تغییر یافت.")
    }

    // 4. Audit Log
    database.auditLogDao().insert(
      AuditLogEntity(
        timestamp = now,
        date = currentDate,
        entityName = "Material",
        entityId = materialId,
        action = "MARKET_PRICE_UPDATE",
        oldValue = "$oldPrice تومان",
        newValue = "$newPrice تومان",
        reason = reason,
        recordedBy = operator
      )
    )

    val summaryText = if (affectedProductSummaries.isNotEmpty()) {
      "\nمحصولات متأثر و بازسنجی بهای تمام‌شده انبار آزاد:\n" + affectedProductSummaries.joinToString("\n")
    } else {
      "\n(این ماده در فرمول BOM محصول فعالی تعریف نشده است)"
    }

    return Pair(true, "قیمت بازار «${mat.name}» به ${FinancialCalculationService.formatCurrency(newPrice)} بروزرسانی شد.$summaryText")
  }

  // ==========================================
  // SMART DELETES (PREVENT HARD DELETION IF FINANCIAL/INVENTORY HISTORY EXISTS)
  // ==========================================
  suspend fun smartDeleteProduct(productId: Long, operator: String = "مدیر سیستم"): Pair<Boolean, String> {
    val prod = database.productDao().getProductById(productId)
      ?: return Pair(false, "محصول یافت نشد")

    // Check if product has sales history or active inventory
    val sales = database.saleOrderDao().getAllSalesOrders().firstOrNull()?.filter { it.modelCode == prod.code } ?: emptyList()
    val inv = database.inventoryDao().getByCode(prod.code)

    if (sales.isNotEmpty() || (inv != null && (inv.readyForShipment > 0 || inv.reservedQuantity > 0))) {
      // Deactivate instead of hard delete!
      database.productDao().update(prod.copy(isActive = false))
      database.auditLogDao().insert(
        AuditLogEntity(
          timestamp = System.currentTimeMillis(),
          date = PersianDateHelper.getCurrentPersianDate(),
          entityName = "Product",
          entityId = productId,
          action = "SMART_DEACTIVATE",
          oldValue = "Active",
          newValue = "Inactive",
          reason = "غیرفعال‌سازی هوشمند به علت وجود سابقه فروش یا موجودی انبار (عدم حذف فیزیکی جهت حفظ یکپارچگی مالی)",
          recordedBy = operator
        )
      )
      return Pair(true, "محصول «${prod.name}» به دلیل داشتن سابقه مالی و فروش به جای حذف دائم، غیرفعال شد تا اطلاعات مالی حفظ شود.")
    } else {
      database.productBOMDao().deleteByProductId(productId)
      database.productDao().deleteById(productId)
      return Pair(true, "محصول «${prod.name}» با موفقیت حذف گردید.")
    }
  }

  suspend fun smartDeleteMaterial(materialId: Long, operator: String = "مدیر سیستم"): Pair<Boolean, String> {
    val mat = database.materialDao().getById(materialId)
      ?: return Pair(false, "ماده اولیه یافت نشد")

    val usedInBoms = database.productBOMDao().getBOMsUsingMaterial(materialId)
    if (usedInBoms.isNotEmpty() || mat.stockQuantity > 0) {
      database.materialDao().update(mat.copy(isActive = false))
      database.auditLogDao().insert(
        AuditLogEntity(
          timestamp = System.currentTimeMillis(),
          date = PersianDateHelper.getCurrentPersianDate(),
          entityName = "Material",
          entityId = materialId,
          action = "SMART_DEACTIVATE",
          oldValue = "Active",
          newValue = "Inactive",
          reason = "غیرفعال‌سازی هوشمند به علت استفاده در فرمول BOM یا داشتن موجودی در انبار",
          recordedBy = operator
        )
      )
      return Pair(true, "ماده «${mat.name}» به دلیل مصرف در فرمول تولید یا موجودی انبار غیرفعال گردید.")
    } else {
      database.materialDao().deleteById(materialId)
      return Pair(true, "ماده اولیه «${mat.name}» با موفقیت حذف شد.")
    }
  }

  suspend fun smartDeleteSupplier(supplierId: Long, operator: String = "مدیر سیستم"): Pair<Boolean, String> {
    val sup = database.supplierDao().getSupplierById(supplierId)
      ?: return Pair(false, "تأمین‌کننده یافت نشد")

    if (sup.totalPurchases > 0 || sup.currentDebt > 0) {
      val updatedDesc = if (sup.description.startsWith("[غیرفعال]")) sup.description else "[غیرفعال] ${sup.description}"
      database.supplierDao().updateSupplier(sup.copy(description = updatedDesc))
      database.auditLogDao().insert(
        AuditLogEntity(
          timestamp = System.currentTimeMillis(),
          date = PersianDateHelper.getCurrentPersianDate(),
          entityName = "Supplier",
          entityId = supplierId,
          action = "SMART_DEACTIVATE",
          oldValue = "Active",
          newValue = "Inactive",
          reason = "غیرفعال‌سازی تأمین‌کننده به علت وجود بدهی یا سابقه خرید",
          recordedBy = operator
        )
      )
      return Pair(true, "تأمین‌کننده «${sup.name}» به دلیل داشتن گردش مالی به جای حذف دائم، غیرفعال گردید.")
    } else {
      database.supplierDao().deleteById(supplierId)
      return Pair(true, "تأمین‌کننده «${sup.name}» با موفقیت حذف شد.")
    }
  }

  suspend fun smartDeleteCustomer(customerId: Long, operator: String = "مدیر سیستم"): Pair<Boolean, String> {
    val cust = database.customerDao().getCustomerById(customerId)
      ?: return Pair(false, "مشتری یافت نشد")

    if (cust.totalPurchases > 0 || cust.currentDebt > 0) {
      val updatedCat = if (cust.category.startsWith("[غیرفعال]")) cust.category else "[غیرفعال] ${cust.category}"
      database.customerDao().updateCustomer(cust.copy(category = updatedCat))
      database.auditLogDao().insert(
        AuditLogEntity(
          timestamp = System.currentTimeMillis(),
          date = PersianDateHelper.getCurrentPersianDate(),
          entityName = "Customer",
          entityId = customerId,
          action = "SMART_DEACTIVATE",
          oldValue = "Active",
          newValue = "Inactive",
          reason = "غیرفعال‌سازی مشتری به علت وجود مانده حساب یا تاریخچه خرید",
          recordedBy = operator
        )
      )
      return Pair(true, "مشتری «${cust.name}» به دلیل داشتن گردش حساب مالی به جای حذف دائم، غیرفعال گردید.")
    } else {
      database.customerDao().deleteById(customerId)
      return Pair(true, "مشتری «${cust.name}» با موفقیت حذف شد.")
    }
  }

  suspend fun recalculateProductDynamicCost(productId: Long) {
    val product = database.productDao().getProductById(productId) ?: return
    val boms = database.productBOMDao().getBOMListForProduct(productId)
    var totalBomCost = 0L
    for (b in boms) {
      val mat = database.materialDao().getById(b.materialId)
      val rate = mat?.currentPrice ?: b.unitRate
      totalBomCost += (b.standardQuantity * rate).toLong()
    }
    val newCost = totalBomCost + product.sewingWage + product.allocatedFreightCost + product.overheadCost
    val targetProfitAmount = (newCost * (product.targetProfitPercent / 100.0)).toLong()
    val suggestedPrice = newCost + targetProfitAmount
    database.productDao().update(
      product.copy(
        currentCostPrice = newCost,
        suggestedSellingPrice = suggestedPrice,
        lastPriceUpdateDate = PersianDateHelper.getCurrentPersianDate(),
        lastPriceUpdateTimestamp = System.currentTimeMillis()
      )
    )
  }

  // ==========================================
  // MASTER DATA CRUD OPERATIONS
  // ==========================================
  suspend fun saveCategory(category: CategoryEntity) {
    if (category.id == 0L) {
      database.categoryDao().insert(category)
    } else {
      database.categoryDao().update(category)
    }
  }

  suspend fun deleteCategory(id: Long) {
    database.categoryDao().deleteById(id)
  }

  suspend fun saveColor(color: ColorEntity) {
    if (color.id == 0L) {
      database.colorDao().insert(color)
    } else {
      database.colorDao().update(color)
    }
  }

  suspend fun deleteColor(id: Long) {
    database.colorDao().deleteById(id)
  }

  suspend fun saveSize(size: SizeEntity) {
    if (size.id == 0L) {
      database.sizeDao().insert(size)
    } else {
      database.sizeDao().update(size)
    }
  }

  suspend fun deleteSize(id: Long) {
    database.sizeDao().deleteById(id)
  }

  suspend fun saveMaterial(material: MaterialEntity): Long {
    return if (material.id == 0L) {
      database.materialDao().insert(material)
    } else {
      database.materialDao().update(material)
      material.id
    }
  }

  suspend fun saveProduct(product: ProductEntity): Long {
    return if (product.id == 0L) {
      database.productDao().insert(product)
    } else {
      database.productDao().update(product)
      product.id
    }
  }

  suspend fun saveBOM(bom: ProductBOMEntity): Long {
    return if (bom.id == 0L) {
      database.productBOMDao().insert(bom)
    } else {
      database.productBOMDao().update(bom)
      bom.id
    }
  }

  suspend fun deleteBOM(id: Long) {
    database.productBOMDao().deleteById(id)
  }

  suspend fun saveMaterialUnit(unit: MaterialUnitEntity) {
    if (unit.id == 0L) {
      database.materialUnitDao().insert(unit)
    } else {
      database.materialUnitDao().update(unit)
    }
  }

  suspend fun deleteMaterialUnit(id: Long) {
    database.materialUnitDao().deleteById(id)
  }

  suspend fun saveSalesChannel(channel: SalesChannelEntity) {
    if (channel.id == 0L) {
      database.salesChannelDao().insert(channel)
    } else {
      database.salesChannelDao().update(channel)
    }
  }

  suspend fun deleteSalesChannel(id: Long) {
    database.salesChannelDao().deleteById(id)
  }

  suspend fun savePriceChangeReason(reason: PriceChangeReasonEntity) {
    if (reason.id == 0L) {
      database.priceChangeReasonDao().insert(reason)
    } else {
      database.priceChangeReasonDao().update(reason)
    }
  }

  suspend fun deletePriceChangeReason(id: Long) {
    database.priceChangeReasonDao().deleteById(id)
  }

  suspend fun duplicateProduct(productId: Long, operator: String = "مدیر کارگاه"): Pair<Boolean, String> {
    val original = database.productDao().getProductById(productId)
      ?: return Pair(false, "محصول مورد نظر یافت نشد.")

    val randomSuffix = (100..999).random()
    val newCode = "${original.code}-C$randomSuffix"
    val newName = "${original.name} (کپی)"

    val duplicatedProduct = original.copy(
      id = 0L,
      code = newCode,
      name = newName,
      lastPriceUpdateDate = PersianDateHelper.getCurrentPersianDate(),
      lastPriceUpdateTimestamp = System.currentTimeMillis()
    )
    val newProductId = database.productDao().insert(duplicatedProduct)

    // Duplicate BOM items
    val boms = database.productBOMDao().getBOMListForProduct(productId)
    if (boms.isNotEmpty()) {
      val copiedBoms = boms.map { bom ->
        bom.copy(id = 0L, productId = newProductId)
      }
      database.productBOMDao().insertAll(copiedBoms)
    }

    // Duplicate Product Variants
    val variants = database.productVariantDao().getVariantsListForProduct(productId)
    if (variants.isNotEmpty()) {
      val copiedVariants = variants.map { v ->
        v.copy(
          id = 0L,
          productId = newProductId,
          productCode = newCode,
          productName = newName
        )
      }
      database.productVariantDao().insertAll(copiedVariants)
    }

    // Record Audit Log
    database.auditLogDao().insert(
      AuditLogEntity(
        timestamp = System.currentTimeMillis(),
        date = PersianDateHelper.getCurrentPersianDate(),
        entityName = "Product",
        entityId = newProductId,
        action = "DUPLICATE",
        oldValue = "کد اصلی: ${original.code}",
        newValue = "کد جدید: $newCode",
        reason = "تکثیر مدل و کپی فرمول ساخت BOM و رنگ/سایز از ${original.name}",
        recordedBy = operator
      )
    )

    return Pair(true, "مدل «${original.name}» با کد جدید «$newCode» و فرمول BOM با موفقیت تکثیر شد.")
  }

  suspend fun exportFullBackupJson(): String {
    val jsonObj = org.json.JSONObject()

    // 1. Settings
    val settings = database.factorySettingsDao().getSettingsOnce()
    if (settings != null) {
      val sObj = org.json.JSONObject()
      sObj.put("fixed_shipping_order", settings.fixedShippingCostPerOrder)
      sObj.put("fixed_shipping_roll", settings.fixedShippingCostPerRoll)
      sObj.put("target_margin_percent", settings.targetProfitMarginPercent)
      sObj.put("overhead_cost_item", settings.overheadCostPerItem)
      jsonObj.put("settings", sObj)
    }

    // 2. Products
    val products = database.productDao().getAllProducts().first()
    val prodArr = org.json.JSONArray()
    products.forEach { p ->
      val pObj = org.json.JSONObject()
      pObj.put("id", p.id)
      pObj.put("code", p.code)
      pObj.put("name", p.name)
      pObj.put("categoryId", p.categoryId)
      pObj.put("currentCostPrice", p.currentCostPrice)
      pObj.put("suggestedSellingPrice", p.suggestedSellingPrice)
      pObj.put("isActive", p.isActive)
      prodArr.put(pObj)
    }
    jsonObj.put("products", prodArr)

    // 3. Materials
    val materials = database.materialDao().getAllMaterials().first()
    val matArr = org.json.JSONArray()
    materials.forEach { m ->
      val mObj = org.json.JSONObject()
      mObj.put("id", m.id)
      mObj.put("code", m.code)
      mObj.put("name", m.name)
      mObj.put("category", m.category)
      mObj.put("unit", m.unit)
      mObj.put("currentPrice", m.currentPrice)
      mObj.put("lastPurchasePrice", m.lastPurchasePrice)
      mObj.put("stockQuantity", m.stockQuantity)
      mObj.put("minStockThreshold", m.minStockThreshold)
      mObj.put("metersPerKg", m.metersPerKg)
      mObj.put("supplierName", m.supplierName)
      mObj.put("isActive", m.isActive)
      matArr.put(mObj)
    }
    jsonObj.put("materials", matArr)

    // 4. Rolls
    val rolls = database.fabricRollDao().getAllRolls().first()
    val rollArr = org.json.JSONArray()
    rolls.forEach { r ->
      val rObj = org.json.JSONObject()
      rObj.put("id", r.id)
      rObj.put("rollCode", r.rollCode)
      rObj.put("inboundDate", r.inboundDate)
      rObj.put("inboundTimestamp", r.inboundTimestamp)
      rObj.put("fabricType", r.fabricType)
      rObj.put("fabricCode", r.fabricCode)
      rObj.put("color", r.color)
      rObj.put("initialMeters", r.initialMeters)
      rObj.put("remainingMeters", r.remainingMeters)
      rObj.put("weightKg", r.weightKg)
      rObj.put("buyPricePerMeter", r.buyPricePerMeter)
      rObj.put("buyPricePerKg", r.buyPricePerKg)
      rObj.put("allocatedShippingCost", r.allocatedShippingCost)
      rObj.put("status", r.status)
      rObj.put("supplierName", r.supplierName)
      rObj.put("batchNumber", r.batchNumber)
      rollArr.put(rObj)
    }
    jsonObj.put("rolls", rollArr)

    // 5. Inventory
    val inventory = database.inventoryDao().getAllInventory().first()
    val invArr = org.json.JSONArray()
    inventory.forEach { i ->
      val iObj = org.json.JSONObject()
      iObj.put("id", i.id)
      iObj.put("code", i.code)
      iObj.put("name", i.name)
      iObj.put("category", i.category)
      iObj.put("readyForShipment", i.readyForShipment)
      iObj.put("reservedQuantity", i.reservedQuantity)
      iObj.put("availableForSale", i.availableForSale)
      iObj.put("unitCostPrice", i.unitCostPrice)
      iObj.put("unitSalePrice", i.unitSalePrice)
      iObj.put("unitWeightGrams", i.unitWeightGrams)
      iObj.put("totalWeightKg", i.totalWeightKg)
      iObj.put("unitType", i.unitType)
      iObj.put("lastUpdated", i.lastUpdated)
      invArr.put(iObj)
    }
    jsonObj.put("inventory", invArr)

    // 6. Purchases
    val purchases = database.purchaseOrderDao().getAllOrders().first()
    val poArr = org.json.JSONArray()
    purchases.forEach { po ->
      val pObj = org.json.JSONObject()
      pObj.put("id", po.id)
      pObj.put("orderNumber", po.orderNumber)
      pObj.put("supplierId", po.supplierId)
      pObj.put("supplierName", po.supplierName)
      pObj.put("orderDate", po.orderDate)
      pObj.put("expectedDeliveryDate", po.expectedDeliveryDate)
      pObj.put("totalAmount", po.totalAmount)
      pObj.put("shippingAmount", po.shippingAmount)
      pObj.put("paidAmount", po.paidAmount)
      pObj.put("status", po.status)
      pObj.put("notes", po.notes)
      poArr.put(pObj)
    }
    jsonObj.put("purchases", poArr)

    // 7. Production
    val productions = database.productionDao().getAllProductions().first()
    val prodRecordsArr = org.json.JSONArray()
    productions.forEach { pr ->
      val prObj = org.json.JSONObject()
      prObj.put("id", pr.id)
      prObj.put("modelCode", pr.modelCode)
      prObj.put("modelName", pr.modelName)
      prObj.put("quantity", pr.quantity)
      prObj.put("fabricRollsUsed", pr.fabricRollsUsed)
      prObj.put("fabricMetersUsed", pr.fabricMetersUsed)
      prObj.put("totalWeightKg", pr.totalWeightKg)
      prObj.put("sewingWagePerItem", pr.sewingWagePerItem)
      prObj.put("fabricPricePerMeter", pr.fabricPricePerMeter)
      prObj.put("accessoriesCostPerItem", pr.accessoriesCostPerItem)
      prObj.put("status", pr.status)
      prObj.put("date", pr.date)
      prObj.put("rollId", pr.rollId)
      prObj.put("rollCode", pr.rollCode)
      prObj.put("consumablesSummary", pr.consumablesSummary)
      prodRecordsArr.put(prObj)
    }
    jsonObj.put("production", prodRecordsArr)

    // 8. Orders
    val orders = database.saleOrderDao().getAllSalesOrders().first()
    val ordArr = org.json.JSONArray()
    orders.forEach { o ->
      val oObj = org.json.JSONObject()
      oObj.put("id", o.id)
      oObj.put("orderNumber", o.orderNumber)
      oObj.put("customerName", o.customerName)
      oObj.put("customerPhone", o.customerPhone)
      oObj.put("modelCode", o.modelCode)
      oObj.put("modelName", o.modelName)
      oObj.put("quantity", o.quantity)
      oObj.put("unitPrice", o.unitPrice)
      oObj.put("unitCost", o.unitCost)
      oObj.put("discountAmount", o.discountAmount)
      oObj.put("paidAmount", o.paidAmount)
      oObj.put("orderDate", o.orderDate)
      oObj.put("deliveryStatus", o.deliveryStatus)
      oObj.put("channel", o.channel)
      oObj.put("customerId", o.customerId ?: 0L)
      oObj.put("color", o.color)
      oObj.put("size", o.size)
      oObj.put("shippingCost", o.shippingCost)
      oObj.put("costSnapshot", o.costSnapshot)
      oObj.put("salePriceSnapshot", o.salePriceSnapshot)
      ordArr.put(oObj)
    }
    jsonObj.put("orders", ordArr)

    // 9. Customers
    val customers = database.customerDao().getAllCustomers().first()
    val custArr = org.json.JSONArray()
    customers.forEach { c ->
      val cObj = org.json.JSONObject()
      cObj.put("id", c.id)
      cObj.put("name", c.name)
      cObj.put("phone", c.phone)
      cObj.put("totalPurchases", c.totalPurchases)
      cObj.put("totalPaid", c.totalPaid)
      cObj.put("currentDebt", c.currentDebt)
      cObj.put("orderCount", c.orderCount)
      cObj.put("lastOrderDate", c.lastOrderDate)
      custArr.put(cObj)
    }
    jsonObj.put("customers", custArr)

    // 10. Suppliers
    val suppliers = database.supplierDao().getAllSuppliers().first()
    val supArr = org.json.JSONArray()
    suppliers.forEach { s ->
      val sObj = org.json.JSONObject()
      sObj.put("id", s.id)
      sObj.put("name", s.name)
      sObj.put("phone", s.phone)
      sObj.put("totalPurchases", s.totalPurchases)
      sObj.put("paidAmount", s.paidAmount)
      sObj.put("currentDebt", s.currentDebt)
      sObj.put("lastPurchaseDate", s.lastPurchaseDate)
      supArr.put(sObj)
    }
    jsonObj.put("suppliers", supArr)

    // 11. Payments (Customer Payments)
    val payments = database.customerPaymentDao().getAllPayments().first()
    val payArr = org.json.JSONArray()
    payments.forEach { p ->
      val pObj = org.json.JSONObject()
      pObj.put("id", p.id)
      pObj.put("customerId", p.customerId)
      pObj.put("customerName", p.customerName)
      pObj.put("orderId", p.orderId ?: 0L)
      pObj.put("orderNumber", p.orderNumber)
      pObj.put("amount", p.amount)
      pObj.put("date", p.date)
      pObj.put("timestamp", p.timestamp)
      pObj.put("paymentMethod", p.paymentMethod)
      pObj.put("referenceNumber", p.referenceNumber)
      pObj.put("notes", p.notes)
      pObj.put("recordedBy", p.recordedBy)
      payArr.put(pObj)
    }
    jsonObj.put("payments", payArr)

    // 12. Expenses (Fixed Costs)
    val expenses = database.fixedCostDao().getAllFixedCostsList()
    val expArr = org.json.JSONArray()
    expenses.forEach { e ->
      val eObj = org.json.JSONObject()
      eObj.put("id", e.id)
      eObj.put("title", e.title)
      eObj.put("amount", e.amount)
      eObj.put("scope", e.scope)
      eObj.put("date", e.date)
      eObj.put("notes", e.notes)
      expArr.put(eObj)
    }
    jsonObj.put("expenses", expArr)

    return jsonObj.toString(2)
  }

  suspend fun restoreDataFromJson(jsonContent: String, operator: String = "مدیر سیستم"): Pair<Boolean, String> = database.withTransaction {
    try {
      val jsonObj = org.json.JSONObject(jsonContent)

      // Restore settings if present
      if (jsonObj.has("settings")) {
        val s = jsonObj.getJSONObject("settings")
        val current = database.factorySettingsDao().getSettingsOnce() ?: com.example.data.model.FactorySettingsEntity()
        val updated = current.copy(
          fixedShippingCostPerOrder = s.optLong("fixed_shipping_order", current.fixedShippingCostPerOrder),
          fixedShippingCostPerRoll = s.optLong("fixed_shipping_roll", current.fixedShippingCostPerRoll),
          targetProfitMarginPercent = s.optDouble("target_margin_percent", current.targetProfitMarginPercent),
          overheadCostPerItem = s.optLong("overhead_cost_item", current.overheadCostPerItem)
        )
        database.factorySettingsDao().insertOrUpdate(updated)
      }

      // Restore Products
      if (jsonObj.has("products")) {
        val arr = jsonObj.getJSONArray("products")
        val list = mutableListOf<ProductEntity>()
        for (i in 0 until arr.length()) {
          val o = arr.getJSONObject(i)
          list.add(
            ProductEntity(
              id = o.optLong("id", 0L),
              code = o.optString("code", "P-$i"),
              name = o.optString("name", "Product $i"),
              categoryId = o.optLong("categoryId", 1L),
              currentCostPrice = o.optLong("currentCostPrice", 0L),
              suggestedSellingPrice = o.optLong("suggestedSellingPrice", 0L),
              isActive = o.optBoolean("isActive", true)
            )
          )
        }
        database.productDao().insertAll(list)
      }

      // Restore Materials
      if (jsonObj.has("materials")) {
        val arr = jsonObj.getJSONArray("materials")
        val list = mutableListOf<MaterialEntity>()
        for (i in 0 until arr.length()) {
          val o = arr.getJSONObject(i)
          list.add(
            MaterialEntity(
              id = o.optLong("id", 0L),
              code = o.optString("code", "M-$i"),
              name = o.optString("name", "Material $i"),
              category = o.optString("category", "پارچه"),
              unit = o.optString("unit", "کیلوگرم"),
              currentPrice = o.optLong("currentPrice", 0L),
              lastPurchasePrice = o.optLong("lastPurchasePrice", 0L),
              stockQuantity = o.optDouble("stockQuantity", 0.0),
              minStockThreshold = o.optDouble("minStockThreshold", 10.0),
              metersPerKg = o.optDouble("metersPerKg", 3.0),
              supplierName = o.optString("supplierName", ""),
              isActive = o.optBoolean("isActive", true)
            )
          )
        }
        database.materialDao().insertAll(list)
      }

      // Restore Rolls
      if (jsonObj.has("rolls")) {
        val arr = jsonObj.getJSONArray("rolls")
        val list = mutableListOf<FabricRollEntity>()
        for (i in 0 until arr.length()) {
          val o = arr.getJSONObject(i)
          list.add(
            FabricRollEntity(
              id = o.optLong("id", 0L),
              rollCode = o.optString("rollCode", "R-$i"),
              inboundDate = o.optString("inboundDate", PersianDateHelper.getCurrentPersianDate()),
              inboundTimestamp = o.optLong("inboundTimestamp", System.currentTimeMillis()),
              fabricType = o.optString("fabricType", "پارچه"),
              fabricCode = o.optString("fabricCode", "F-$i"),
              color = o.optString("color", "مشکی"),
              initialMeters = o.optDouble("initialMeters", 100.0),
              remainingMeters = o.optDouble("remainingMeters", 100.0),
              weightKg = o.optDouble("weightKg", 30.0),
              buyPricePerMeter = o.optLong("buyPricePerMeter", 0L),
              buyPricePerKg = o.optLong("buyPricePerKg", 0L),
              allocatedShippingCost = o.optLong("allocatedShippingCost", 0L),
              status = o.optString("status", "موجود"),
              supplierName = o.optString("supplierName", ""),
              batchNumber = o.optString("batchNumber", "")
            )
          )
        }
        database.fabricRollDao().insertAll(list)
      }

      // Restore Inventory
      if (jsonObj.has("inventory")) {
        val arr = jsonObj.getJSONArray("inventory")
        val list = mutableListOf<InventoryEntity>()
        for (i in 0 until arr.length()) {
          val o = arr.getJSONObject(i)
          list.add(
            InventoryEntity(
              id = o.optLong("id", 0L),
              name = o.optString("name", "Item $i"),
              code = o.optString("code", "I-$i"),
              category = o.optString("category", "محصولات آماده"),
              readyForShipment = o.optInt("readyForShipment", 0),
              reservedQuantity = o.optInt("reservedQuantity", 0),
              availableForSale = o.optInt("availableForSale", 0),
              unitCostPrice = o.optLong("unitCostPrice", 0L),
              unitSalePrice = o.optLong("unitSalePrice", 0L),
              unitWeightGrams = o.optDouble("unitWeightGrams", 400.0),
              totalWeightKg = o.optDouble("totalWeightKg", 0.0),
              unitType = o.optString("unitType", "عدد"),
              lastUpdated = o.optString("lastUpdated", PersianDateHelper.getCurrentPersianDate())
            )
          )
        }
        database.inventoryDao().insertAll(list)
      }

      // Restore Purchases
      if (jsonObj.has("purchases")) {
        val arr = jsonObj.getJSONArray("purchases")
        val list = mutableListOf<PurchaseOrderEntity>()
        for (i in 0 until arr.length()) {
          val o = arr.getJSONObject(i)
          list.add(
            PurchaseOrderEntity(
              id = o.optLong("id", 0L),
              orderNumber = o.optString("orderNumber", "PO-$i"),
              supplierId = o.optLong("supplierId", 1L),
              supplierName = o.optString("supplierName", "تأمین‌کننده"),
              orderDate = o.optString("orderDate", PersianDateHelper.getCurrentPersianDate()),
              expectedDeliveryDate = o.optString("expectedDeliveryDate", ""),
              totalAmount = o.optLong("totalAmount", 0L),
              shippingAmount = o.optLong("shippingAmount", 0L),
              paidAmount = o.optLong("paidAmount", 0L),
              status = o.optString("status", "تحویل شده"),
              notes = o.optString("notes", "")
            )
          )
        }
        database.purchaseOrderDao().insertAll(list)
      }

      // Restore Production
      if (jsonObj.has("production")) {
        val arr = jsonObj.getJSONArray("production")
        val list = mutableListOf<ProductionEntity>()
        for (i in 0 until arr.length()) {
          val o = arr.getJSONObject(i)
          list.add(
            ProductionEntity(
              id = o.optLong("id", 0L),
              modelCode = o.optString("modelCode", "M-$i"),
              modelName = o.optString("modelName", "Model $i"),
              quantity = o.optInt("quantity", 0),
              fabricRollsUsed = o.optInt("fabricRollsUsed", 1),
              fabricMetersUsed = o.optDouble("fabricMetersUsed", 0.0),
              totalWeightKg = o.optDouble("totalWeightKg", 0.0),
              sewingWagePerItem = o.optLong("sewingWagePerItem", 0L),
              fabricPricePerMeter = o.optLong("fabricPricePerMeter", 0L),
              accessoriesCostPerItem = o.optLong("accessoriesCostPerItem", 0L),
              status = o.optString("status", "آماده ارسال / تکمیل موجودی"),
              date = o.optString("date", PersianDateHelper.getCurrentPersianDate()),
              rollId = o.optLong("rollId", 0L),
              rollCode = o.optString("rollCode", ""),
              consumablesSummary = o.optString("consumablesSummary", "")
            )
          )
        }
        database.productionDao().insertAll(list)
      }

      // Restore Orders
      if (jsonObj.has("orders")) {
        val arr = jsonObj.getJSONArray("orders")
        val list = mutableListOf<SaleOrderEntity>()
        for (i in 0 until arr.length()) {
          val o = arr.getJSONObject(i)
          val custId = o.optLong("customerId", 0L)
          list.add(
            SaleOrderEntity(
              id = o.optLong("id", 0L),
              orderNumber = o.optString("orderNumber", "ORD-$i"),
              customerName = o.optString("customerName", "مشتری"),
              customerPhone = o.optString("customerPhone", ""),
              modelCode = o.optString("modelCode", "M-$i"),
              modelName = o.optString("modelName", "Model $i"),
              quantity = o.optInt("quantity", 1),
              unitPrice = o.optLong("unitPrice", 0L),
              unitCost = o.optLong("unitCost", 0L),
              discountAmount = o.optLong("discountAmount", 0L),
              paidAmount = o.optLong("paidAmount", 0L),
              orderDate = o.optString("orderDate", PersianDateHelper.getCurrentPersianDate()),
              deliveryStatus = o.optString("deliveryStatus", "آماده ارسال"),
              channel = o.optString("channel", "فروش حضوری"),
              customerId = if (custId > 0L) custId else null,
              color = o.optString("color", ""),
              size = o.optString("size", ""),
              shippingCost = o.optLong("shippingCost", 0L),
              costSnapshot = o.optLong("costSnapshot", 0L),
              salePriceSnapshot = o.optLong("salePriceSnapshot", 0L)
            )
          )
        }
        database.saleOrderDao().insertAll(list)
      }

      // Restore Customers
      if (jsonObj.has("customers")) {
        val arr = jsonObj.getJSONArray("customers")
        val list = mutableListOf<CustomerEntity>()
        for (i in 0 until arr.length()) {
          val o = arr.getJSONObject(i)
          list.add(
            CustomerEntity(
              id = o.optLong("id", 0L),
              name = o.optString("name", "Customer $i"),
              company = o.optString("company", ""),
              phone = o.optString("phone", ""),
              address = o.optString("address", ""),
              category = o.optString("category", "بوتیک و آنلاین"),
              totalPurchases = o.optLong("totalPurchases", 0L),
              totalPaid = o.optLong("totalPaid", 0L),
              currentDebt = o.optLong("currentDebt", 0L),
              orderCount = o.optInt("orderCount", 0),
              tier = o.optString("tier", "خرید اول"),
              popularModels = o.optString("popularModels", ""),
              lastOrderDate = o.optString("lastOrderDate", "")
            )
          )
        }
        database.customerDao().insertAll(list)
      }

      // Restore Suppliers
      if (jsonObj.has("suppliers")) {
        val arr = jsonObj.getJSONArray("suppliers")
        val list = mutableListOf<SupplierEntity>()
        for (i in 0 until arr.length()) {
          val o = arr.getJSONObject(i)
          list.add(
            SupplierEntity(
              id = o.optLong("id", 0L),
              name = o.optString("name", "Supplier $i"),
              phone = o.optString("phone", ""),
              totalPurchases = o.optLong("totalPurchases", 0L),
              paidAmount = o.optLong("paidAmount", 0L),
              currentDebt = o.optLong("currentDebt", 0L),
              lastPurchaseDate = o.optString("lastPurchaseDate", "")
            )
          )
        }
        database.supplierDao().insertAll(list)
      }

      // Restore Payments
      if (jsonObj.has("payments")) {
        val arr = jsonObj.getJSONArray("payments")
        val list = mutableListOf<CustomerPaymentEntity>()
        for (i in 0 until arr.length()) {
          val o = arr.getJSONObject(i)
          list.add(
            CustomerPaymentEntity(
              id = o.optLong("id", 0L),
              customerId = o.optLong("customerId", 0L),
              customerName = o.optString("customerName", ""),
              orderId = o.optLong("orderId", 0L),
              orderNumber = o.optString("orderNumber", ""),
              amount = o.optLong("amount", 0L),
              date = o.optString("date", PersianDateHelper.getCurrentPersianDate()),
              timestamp = o.optLong("timestamp", System.currentTimeMillis()),
              paymentMethod = o.optString("paymentMethod", "کارت به کارت"),
              referenceNumber = o.optString("referenceNumber", ""),
              notes = o.optString("notes", ""),
              recordedBy = o.optString("recordedBy", operator)
            )
          )
        }
        database.customerPaymentDao().insertAll(list)
      }

      // Restore Expenses (Fixed Costs)
      if (jsonObj.has("expenses")) {
        val arr = jsonObj.getJSONArray("expenses")
        val list = mutableListOf<FixedCostEntity>()
        for (i in 0 until arr.length()) {
          val o = arr.getJSONObject(i)
          list.add(
            FixedCostEntity(
              id = o.optLong("id", 0L),
              title = o.optString("title", "هزینه $i"),
              amount = o.optLong("amount", 0L),
              scope = o.optString("scope", "ALL_PRODUCTS"),
              date = o.optString("date", PersianDateHelper.getCurrentPersianDate()),
              notes = o.optString("notes", "")
            )
          )
        }
        database.fixedCostDao().insertAll(list)
      }

      database.auditLogDao().insert(
        AuditLogEntity(
          timestamp = System.currentTimeMillis(),
          date = PersianDateHelper.getCurrentPersianDate(),
          entityName = "SYSTEM",
          entityId = 0L,
          action = "RESTORE_FULL_BACKUP",
          oldValue = "",
          newValue = "Restored all manufacturing data",
          reason = "بازیابی کامل اطلاعات تمام ماژول‌ها از فایل پشتیبان JSON",
          recordedBy = operator
        )
      )

      Pair(true, "اطلاعات تمام بخش‌ها (کالاها، مواد اولیه، طاقه‌ها، موجودی، سفارش‌ها، خریدها، مشتریان و هزینه‌ها) با موفقیت بازیابی شد.")
    } catch (e: Exception) {
      Pair(false, "خطا در پردازش فایل پشتیبان: ${e.localizedMessage}")
    }
  }

  // ==========================================
  // CUTTING PARTS WORKFLOW
  // ==========================================

  fun cuttingPartsByRoll(rollId: Long): Flow<List<CuttingEntity>> =
    database.cuttingDao().getCuttingPartsByRoll(rollId)

  fun cuttingPartsByStatus(status: String): Flow<List<CuttingEntity>> =
    database.cuttingDao().getCuttingPartsByStatus(status)

  fun activeCuttingParts(): Flow<List<CuttingEntity>> =
    database.cuttingDao().getActiveParts()

  suspend fun getNextPartNumber(rollId: Long): Int =
    database.cuttingDao().getMaxPartNumber(rollId) + 1

  suspend fun updateCuttingPartStatus(
    partId: Long, newStatus: String, note: String = ""
  ): Pair<Boolean, String> = database.withTransaction {
    val part = database.cuttingDao().getCuttingById(partId)
      ?: return@withTransaction Pair(false, "پارت با شناسه $partId یافت نشد")
    if (part.status == newStatus) return@withTransaction Pair(true, "وضعیت تغییری نکرد")
    val updatedNote = if (note.isNotBlank()) {
      if (part.notes.isBlank()) note else "${part.notes} | $note"
    } else part.notes
    database.cuttingDao().updateCutting(part.copy(status = newStatus, notes = updatedNote))
    if (newStatus == CuttingEntity.STATUS_READY && !part.isStockAdded) {
      addPartToWarehouseInternal(part)
    }
    if (part.orderId != null && part.orderId > 0L) {
      val newOrderStatus = when (newStatus) {
        CuttingEntity.STATUS_SEWING -> SaleOrderStatus.IN_SEWING
        CuttingEntity.STATUS_READY -> SaleOrderStatus.READY_FOR_SHIPPING
        else -> null
      }
      if (newOrderStatus != null) {
        try {
          database.saleOrderDao().getOrderById(part.orderId)?.let { order ->
            database.saleOrderDao().updateOrder(order.copy(deliveryStatus = newOrderStatus))
            database.orderStatusHistoryDao().insertHistory(
              OrderStatusHistoryEntity(
                orderId = order.id, orderNumber = order.orderNumber,
                oldStatus = order.deliveryStatus, newStatus = newOrderStatus,
                date = PersianDateHelper.getCurrentPersianDate(),
                time = PersianDateHelper.getCurrentTime(),
                timestamp = System.currentTimeMillis(),
                note = "به‌روزرسانی خودکار از پارت"
              )
            )
          }
        } catch (_: Exception) {}
      }
    }
    Pair(true, "وضعیت پارت به «$newStatus» تغییر یافت")
  }

  private suspend fun addPartToWarehouseInternal(part: CuttingEntity) {
    val prodCode = if (part.productCode.isNotBlank()) part.productCode else "PRD-${part.id}"
    val prodName = if (part.productName.isNotBlank()) part.productName else part.partTitle
    val count = part.cutQuantity
    val unitCost = part.unitCost
    val todayDate = PersianDateHelper.getTodayPersianDate()
    val existing = database.inventoryDao().getByCode(prodCode)
    if (existing != null) {
      database.inventoryDao().updateItem(
        existing.copy(
          readyForShipment = existing.readyForShipment + count,
          availableForSale = existing.availableForSale + count,
          unitCostPrice = if (unitCost > 0L) unitCost else existing.unitCostPrice,
          lastUpdated = todayDate
        )
      )
    } else {
      database.inventoryDao().insertItem(
        InventoryEntity(
          name = prodName, code = prodCode, category = "محصولات آماده",
          readyForShipment = count, reservedQuantity = 0, availableForSale = count,
          unitCostPrice = unitCost, unitSalePrice = part.unitSellingPrice,
          unitWeightGrams = part.actualWeightKgPerItem * 1000.0,
          totalWeightKg = part.weightKgUsed, unitType = "عدد", lastUpdated = todayDate
        )
      )
    }
    try {
      database.inventoryLedgerDao().insert(
        InventoryLedgerEntity(
          timestamp = System.currentTimeMillis(), date = todayDate,
          itemType = "FINISHED_GOOD", itemId = part.id,
          itemCode = prodCode, itemName = prodName,
          color = part.color, size = part.size,
          transactionType = "CUTTING_PART_READY",
          quantityChange = count.toDouble(),
          balanceAfter = (existing?.readyForShipment ?: 0) + count.toDouble(),
          unit = "عدد", unitPriceAtTime = unitCost,
          relatedDocumentNumber = "PART-${part.id}",
          notes = "کار آماده از پارت", operator = "مدیر کارگاه"
        )
      )
    } catch (_: Exception) {}
    database.cuttingDao().updateCutting(part.copy(isStockAdded = true))
  }

  suspend fun getBOMAsConsumableInputs(
    productId: Long, quantity: Int
  ): List<ProductionConsumableInputItem> {
    val boms = database.productBOMDao().getBOMListForProduct(productId)
    return boms.mapNotNull { bom ->
      val m = database.materialDao().getById(bom.materialId) ?: return@mapNotNull null
      ProductionConsumableInputItem(
        accessoryCode = m.code, accessoryName = m.name,
        quantityUsed = bom.standardQuantity * quantity,
        unit = m.unit.ifBlank { bom.unit },
        unitCostPrice = m.currentPrice
      )
    }
  }

  // ==========================================
  // PRICE UPDATE (روزآمدسازی قیمت بدون خرید)
  // ==========================================

  /**
   * Phase 15 Patch 2: register a new fabric roll purchase and propagate its price
   * to all rolls of the same fabricCategoryId.
   *
   * Interpretation B: buyPricePerMeter/Kg (historical) is preserved untouched.
   * currentPricePerMeter/Kg (market) is updated on all same-category rolls.
   */
  suspend fun recordFabricPurchaseAndPropagate(
    rollId: Long,
    newPricePerMeter: Long,
    newPricePerKg: Long = 0L,
    supplierName: String = "",
    reason: String = "ثبت خرید جدید",
    operator: String = "مدیر کارگاه"
  ): Pair<Boolean, String> = database.withTransaction {
    val roll = database.fabricRollDao().getRollById(rollId)
      ?: return@withTransaction Pair(false, "طاقه یافت نشد")
    if (newPricePerMeter <= 0L && newPricePerKg <= 0L) {
      return@withTransaction Pair(false, "قیمت جدید باید بیشتر از صفر باشد")
    }

    val today = com.example.util.PersianDateHelper.getTodayPersianDate()
    val now = System.currentTimeMillis()

    val metersPerKg = if (roll.metersPerKg > 0.0) roll.metersPerKg else 0.0
    val finalPricePerMeter = if (newPricePerMeter > 0L) newPricePerMeter
      else if (newPricePerKg > 0L && metersPerKg > 0.0) (newPricePerKg / metersPerKg).toLong()
      else 0L
    val finalPricePerKg = if (newPricePerKg > 0L) newPricePerKg
      else if (finalPricePerMeter > 0L && metersPerKg > 0.0) (finalPricePerMeter * metersPerKg).toLong()
      else 0L

    if (finalPricePerMeter <= 0L) {
      return@withTransaction Pair(false, "قیمت معتبر نیست")
    }

    val oldCurrentPerMeter = if (roll.currentPricePerMeter > 0L) roll.currentPricePerMeter else roll.buyPricePerMeter
    val oldCurrentPerKg = if (roll.currentPricePerKg > 0L) roll.currentPricePerKg else roll.buyPricePerKg

    // 1. Update triggering roll
    database.fabricRollDao().updateRoll(
      roll.copy(
        currentPricePerMeter = finalPricePerMeter,
        currentPricePerKg = finalPricePerKg,
        lastPriceUpdateDate = today,
        lastPriceUpdateTimestamp = now
      )
    )

    // 2. Propagate to same-category rolls
    var affectedCount = 1
    val categoryId = roll.fabricCategoryId

    if (categoryId != null) {
      val sameCategoryRolls = database.fabricRollDao().getRollsByCategory(categoryId)
      sameCategoryRolls.forEach { other ->
        if (other.id != roll.id) {
          val otherMetersPerKg = if (other.metersPerKg > 0.0) other.metersPerKg else 0.0
          val otherFinalPerKg = if (finalPricePerMeter > 0L && otherMetersPerKg > 0.0)
            (finalPricePerMeter * otherMetersPerKg).toLong() else finalPricePerKg
          database.fabricRollDao().updateRoll(
            other.copy(
              currentPricePerMeter = finalPricePerMeter,
              currentPricePerKg = otherFinalPerKg,
              lastPriceUpdateDate = today,
              lastPriceUpdateTimestamp = now
            )
          )
          affectedCount++
          updateInProgressProductsAfterPriceChange(other.id, finalPricePerMeter, operator)
        }
      }
    }

    // 3. Record history
    val changeM = finalPricePerMeter - oldCurrentPerMeter
    val changePercent = if (oldCurrentPerMeter > 0L)
      (changeM.toDouble() / oldCurrentPerMeter) * 100.0 else 0.0
    try {
      database.fabricPriceHistoryDao().insert(
        FabricPriceHistoryEntity(
          fabricCategoryId = categoryId,
          fabricCategoryName = roll.fabricCategoryName,
          triggeringRollId = roll.id,
          triggeringRollCode = roll.rollCode,
          oldPricePerMeter = oldCurrentPerMeter,
          newPricePerMeter = finalPricePerMeter,
          oldPricePerKg = oldCurrentPerKg,
          newPricePerKg = finalPricePerKg,
          date = today,
          timestamp = now,
          changeAmountPerMeter = changeM,
          changePercentPerMeter = changePercent,
          affectedRollCount = affectedCount,
          reason = reason,
          source = "PURCHASE",
          supplierName = if (supplierName.isNotBlank()) supplierName else roll.supplierName,
          recordedBy = operator
        )
      )
    } catch (_: Exception) {}

    val msg = if (affectedCount > 1)
      "قیمت $affectedCount طاقه از دسته ${roll.fabricCategoryName} به‌روزرسانی شد ($finalPricePerMeter تومان/متر)"
    else
      "قیمت طاقه ${roll.rollCode} به‌روزرسانی شد ($finalPricePerMeter تومان/متر)"

    Pair(true, msg)
  }

  /**
   * به‌روزرسانی قیمت روز یک طاقه بدون ثبت خرید جدید.
   * موجودی فیزیکی دست نمی‌خورد؛ فقط قیمت روز و بهای محاسباتی.
   */
  suspend fun updateFabricRollCurrentPrice(
    rollId: Long, newPricePerMeter: Long, newPricePerKg: Long = 0L,
    reason: String = "تغییر قیمت بازار", operator: String = "مدیر کارگاه"
  ): Pair<Boolean, String> = database.withTransaction {
    val roll = database.fabricRollDao().getRollById(rollId)
      ?: return@withTransaction Pair(false, "طاقه یافت نشد")
    if (newPricePerMeter <= 0L && newPricePerKg <= 0L) {
      return@withTransaction Pair(false, "قیمت جدید باید بیشتر از صفر باشد")
    }
    val finalPricePerMeter = if (newPricePerMeter > 0L) newPricePerMeter else {
      if (roll.metersPerKg > 0.0) (newPricePerKg / roll.metersPerKg).toLong() else roll.buyPricePerMeter
    }
    val finalPricePerKg = if (newPricePerKg > 0L) newPricePerKg else {
      (finalPricePerMeter * roll.metersPerKg).toLong()
    }
    val oldPriceM = if (roll.currentPricePerMeter > 0L) roll.currentPricePerMeter else roll.buyPricePerMeter
    val oldPriceKg = if (roll.currentPricePerKg > 0L) roll.currentPricePerKg else roll.buyPricePerKg
    if (oldPriceM == finalPricePerMeter && oldPriceKg == finalPricePerKg) {
      return@withTransaction Pair(true, "قیمت تغییری نکرد")
    }

    val today = PersianDateHelper.getTodayPersianDate()
    val now = System.currentTimeMillis()

    database.fabricRollDao().updateRoll(
      roll.copy(
        currentPricePerMeter = finalPricePerMeter,
        currentPricePerKg = finalPricePerKg,
        lastPriceUpdateDate = today,
        lastPriceUpdateTimestamp = now
      )
    )

    // ثبت در تاریخچه قیمت
    val changeM = finalPricePerMeter - oldPriceM
    val changePercent = if (oldPriceM > 0L) (changeM.toDouble() / oldPriceM) * 100.0 else 0.0
    try {
      database.materialPriceHistoryDao().insert(
        MaterialPriceHistoryEntity(
          materialId = roll.id,
          materialName = "${roll.fabricType} - طاقه ${roll.rollCode}",
          oldPrice = oldPriceM,
          newPrice = finalPricePerMeter,
          date = today,
          timestamp = now,
          changeAmount = changeM,
          changePercent = changePercent,
          reason = reason,
          source = "FABRIC_ROLL_PRICE_UPDATE",
          supplierName = roll.supplierName,
          recordedBy = operator
        )
      )
    } catch (_: Exception) {}

    // به‌روزرسانی بهای محصولات در جریان که از این طاقه مصرف کرده‌اند
    updateInProgressProductsAfterPriceChange(roll.id, finalPricePerMeter, operator)

    Pair(true, "قیمت طاقه ${roll.rollCode} به‌روزرسانی شد (${finalPricePerMeter} تومان/متر)")
  }

  /**
   * به‌روزرسانی قیمت روز یک ماده اولیه/ملزوم
   */
  suspend fun updateMaterialCurrentPrice(
    materialId: Long, newPrice: Long, reason: String = "تغییر قیمت بازار",
    operator: String = "مدیر کارگاه"
  ): Pair<Boolean, String> = database.withTransaction {
    val mat = database.materialDao().getById(materialId)
      ?: return@withTransaction Pair(false, "ماده یافت نشد")
    if (newPrice <= 0L) return@withTransaction Pair(false, "قیمت باید بیشتر از صفر باشد")
    if (mat.currentPrice == newPrice) {
      return@withTransaction Pair(true, "قیمت تغییری نکرد")
    }
    val today = PersianDateHelper.getTodayPersianDate()
    val now = System.currentTimeMillis()

    database.materialDao().update(
      mat.copy(
        currentPrice = newPrice,
        lastPriceSource = "MARKET_UPDATE",
        lastPriceChangeDate = today,
        lastPriceChangeTimestamp = now
      )
    )

    try {
      val changeAmount = newPrice - mat.currentPrice
      database.materialPriceHistoryDao().insert(
        MaterialPriceHistoryEntity(
          materialId = mat.id,
          materialName = mat.name,
          oldPrice = mat.currentPrice,
          newPrice = newPrice,
          date = today,
          timestamp = now,
          changeAmount = changeAmount,
          changePercent = if (mat.currentPrice > 0L) (changeAmount.toDouble() / mat.currentPrice) * 100.0 else 0.0,
          reason = reason,
          source = "MATERIAL_PRICE_UPDATE",
          supplierName = mat.supplierName,
          recordedBy = operator
        )
      )
    } catch (_: Exception) {}

    // به‌روزرسانی BOM استفاده‌کننده
    updateProductsUsingMaterial(materialId, newPrice, operator)
    Pair(true, "قیمت «${mat.name}» به‌روزرسانی شد")
  }

  /**
   * به‌روزرسانی بهای محصولات در جریان که از این طاقه استفاده کرده‌اند
   */
  private suspend fun updateInProgressProductsAfterPriceChange(
    rollId: Long, newPricePerMeter: Long, operator: String
  ) {
    try {
      val allCuttings = database.cuttingDao().getAllCuttings().firstOrNull() ?: emptyList()
      val affected = allCuttings.filter { it.rollId == rollId && !it.isStockAdded }
      val today = PersianDateHelper.getTodayPersianDate()
      val now = System.currentTimeMillis()

      affected.forEach { part ->
        val newFabricCost = (part.metersUsed * newPricePerMeter).toLong()
        val newTotal = newFabricCost + part.allocatedShippingCost +
          part.accessoriesCost + part.tailorCost + part.overheadCost + part.otherDirectCost
        database.cuttingDao().updateCutting(
          part.copy(fabricCost = newFabricCost, totalCost = newTotal)
        )
        try {
          database.auditLogDao().insert(
            AuditLogEntity(
              timestamp = now, date = today, entityName = "CuttingPart",
              entityId = part.id, action = "PRICE_RECALC",
              oldValue = "بهای قبلی: ${part.totalCost}",
              newValue = "بهای جدید: $newTotal (پس از به‌روزرسانی قیمت طاقه)",
              reason = "به‌روزرسانی خودکار بر اساس آخرین قیمت ثبت‌شده",
              recordedBy = operator
            )
          )
        } catch (_: Exception) {}
      }
    } catch (_: Exception) {}
  }

  /**
   * به‌روزرسانی BOM و بهای محصولات استفاده‌کننده از این ماده
   */
  private suspend fun updateProductsUsingMaterial(
    materialId: Long, newPrice: Long, operator: String
  ) {
    try {
      val boms = database.productBOMDao().getBOMsUsingMaterial(materialId)
      val productIds = boms.map { it.productId }.distinct()
      val today = PersianDateHelper.getTodayPersianDate()
      val now = System.currentTimeMillis()

      productIds.forEach { pid ->
        val product = database.productDao().getProductById(pid) ?: return@forEach
        val productBoms = database.productBOMDao().getBOMListForProduct(pid)
        var newCost = 0L
        productBoms.forEach { b ->
          val price = if (b.materialId == materialId) newPrice
                     else database.materialDao().getById(b.materialId)?.currentPrice ?: b.unitRate
          newCost += (b.standardQuantity * price).toLong()
        }
        val fullCost = newCost + product.sewingWage + product.allocatedFreightCost + product.overheadCost
        database.productDao().update(
          product.copy(
            currentCostPrice = fullCost,
            lastPriceUpdateDate = today,
            lastPriceUpdateTimestamp = now
          )
        )
      }
    } catch (_: Exception) {}
  }

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
      // تخصیص به ملزومات در صورت وجود (فاز 11)
      else if (item.itemType == "MATERIAL") {
        try {
          val mat = database.materialDao().getById(item.itemId)
          if (mat != null) {
            database.materialDao().update(
              mat.copy(allocatedShippingCost = mat.allocatedShippingCost + alloc)
            )
          }
        } catch (_: Exception) {}
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

  /**
   * دریافت همه ردیف‌های بارنامه مرتبط با یک قلم خاص (طاقه یا ملزومات)
   */
  suspend fun getAllWaybillItemsForItem(itemType: String, itemId: Long): List<WaybillItemEntity> {
    return try {
      database.waybillItemDao().getItemsForItem(itemType, itemId)
    } catch (_: Exception) {
      emptyList()
    }
  }

  // ==========================================
  // ROLL USAGE CRUD (ویرایش و حذف مصرف طاقه)
  // ==========================================

  /**
   * ویرایش یک مصرف ثبت‌شده
   * اختلاف مقدار مصرف را روی موجودی طاقه اعمال می‌کند
   */
  suspend fun updateRollUsage(
    usageId: Long,
    newMetersUsed: Double,
    newWeightKgUsed: Double,
    newModelName: String,
    newNote: String
  ): Pair<Boolean, String> = database.withTransaction {
    val usage = database.rollUsageDao().getUsageById(usageId)
      ?: return@withTransaction Pair(false, "مصرف یافت نشد")
    val roll = database.fabricRollDao().getRollById(usage.rollId)
      ?: return@withTransaction Pair(false, "طاقه یافت نشد")

    // اختلاف متری: مثبت = مصرف بیشتر، منفی = مصرف کمتر
    val metersDiff = newMetersUsed - usage.metersUsed

    // موجودی جدید طاقه (اگر مصرف بیشتر شد، کم می‌شود)
    val newRemaining = roll.remainingMeters - metersDiff
    if (newRemaining < 0) {
      return@withTransaction Pair(
        false,
        "موجودی طاقه کافی نیست. باقیمانده فعلی: ${"%.2f".format(roll.remainingMeters)} متر"
      )
    }

    // محاسبه مجدد هزینه‌ها
    val newFabricCost = (newMetersUsed * roll.buyPricePerMeter).toLong()
    val newShippingCost = if (roll.initialMeters > 0)
      ((newMetersUsed / roll.initialMeters) * roll.allocatedShippingCost).toLong()
    else 0L

    // به‌روزرسانی طاقه
    val metersPerKg = roll.metersPerKg
    val newRemainingKg = if (metersPerKg > 0) newRemaining / metersPerKg else 0.0
    val newStatus = if (newRemaining <= 0.5) "پایان یافته" else "در حال مصرف"

    database.fabricRollDao().updateRoll(
      roll.copy(
        remainingMeters = newRemaining,
        remainingWeightKg = newRemainingKg,
        status = newStatus
      )
    )

    // به‌روزرسانی رکورد مصرف
    database.rollUsageDao().updateRollUsage(
      usage.copy(
        metersUsed = newMetersUsed,
        weightKgUsed = newWeightKgUsed,
        modelName = newModelName,
        note = newNote,
        allocatedFabricCost = newFabricCost,
        allocatedShippingCost = newShippingCost
      )
    )

    Pair(true, "مصرف با موفقیت ویرایش شد. باقیمانده طاقه: ${"%.2f".format(newRemaining)} متر")
  }

  /**
   * حذف یک مصرف - موجودی طاقه برمی‌گردد
   */
  suspend fun deleteRollUsage(usageId: Long): Pair<Boolean, String> = database.withTransaction {
    val usage = database.rollUsageDao().getUsageById(usageId)
      ?: return@withTransaction Pair(false, "مصرف یافت نشد")
    val roll = database.fabricRollDao().getRollById(usage.rollId)

    // برگرداندن متر به طاقه
    if (roll != null) {
      val restoredMeters = roll.remainingMeters + usage.metersUsed
      val metersPerKg = roll.metersPerKg
      val restoredKg = if (metersPerKg > 0) restoredMeters / metersPerKg else 0.0
      val newStatus = if (restoredMeters <= 0.5) "پایان یافته"
                     else if (restoredMeters >= roll.initialMeters - 0.5) "موجود"
                     else "در حال مصرف"

      database.fabricRollDao().updateRoll(
        roll.copy(
          remainingMeters = restoredMeters,
          remainingWeightKg = restoredKg,
          status = newStatus
        )
      )
    }

    database.rollUsageDao().deleteRollUsage(usage)
    Pair(true, "مصرف حذف شد و ${"%.2f".format(usage.metersUsed)} متر به طاقه برگشت")
  }

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

  /**
   * پاک کردن تمام داده‌ها بدون بارگذاری دمو
   * فقط تنظیمات پیش‌فرض FactorySettings بازنشانی می‌شود
   */
  suspend fun clearAllDataKeepingStructure(): Pair<Boolean, String> = database.withTransaction {
    try {
      database.clearAllTables()
      // تنظیمات پیش‌فرض
      database.factorySettingsDao().insertOrUpdate(
        com.example.data.model.FactorySettingsEntity()
      )
      Pair(true, "تمام داده‌ها پاک شد. سیستم آماده ورود اطلاعات جدید است.")
    } catch (e: Exception) {
      Pair(false, "خطا در پاکسازی: ${e.localizedMessage}")
    }
  }
}


