package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AccessoryPurchaseDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.ColorDao
import com.example.data.dao.CustomerDao
import com.example.data.dao.CuttingDao
import com.example.data.dao.FabricDao
import com.example.data.dao.FabricRollDao
import com.example.data.dao.FactorySettingsDao
import com.example.data.dao.FixedCostDao
import com.example.data.dao.InventoryDao
import com.example.data.dao.InventoryLedgerDao
import com.example.data.dao.MaterialDao
import com.example.data.dao.MaterialPriceHistoryDao
import com.example.data.dao.MaterialUnitDao
import com.example.data.dao.ModelStandardDao
import com.example.data.dao.OrderStatusHistoryDao
import com.example.data.dao.PriceChangeReasonDao
import com.example.data.dao.ProductBOMDao
import com.example.data.dao.ProductDao
import com.example.data.dao.ProductPriceHistoryDao
import com.example.data.dao.ProductVariantDao
import com.example.data.dao.ProductionConsumableDao
import com.example.data.dao.ProductionDao
import com.example.data.dao.PurchaseItemDao
import com.example.data.dao.PurchaseOrderDao
import com.example.data.dao.RollUsageDao
import com.example.data.dao.SaleOrderDao
import com.example.data.dao.SalesChannelDao
import com.example.data.dao.ShippingExpenseDao
import com.example.data.dao.SizeDao
import com.example.data.dao.SupplierDao
import com.example.data.dao.CustomerPaymentDao
import com.example.data.dao.SupplierPaymentDao
import com.example.data.dao.ShippingRateHistoryDao
import com.example.data.dao.AuditLogDao
import com.example.data.dao.InventoryAdjustmentDao
import com.example.data.model.AccessoryPurchaseEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ColorEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.CuttingEntity
import com.example.data.model.FabricEntity
import com.example.data.model.FabricRollEntity
import com.example.data.model.FactorySettingsEntity
import com.example.data.model.FixedCostEntity
import com.example.data.model.InventoryEntity
import com.example.data.model.InventoryLedgerEntity
import com.example.data.model.MaterialEntity
import com.example.data.model.MaterialPriceHistoryEntity
import com.example.data.model.MaterialUnitEntity
import com.example.data.model.ModelStandardEntity
import com.example.data.model.OrderStatusHistoryEntity
import com.example.data.model.PriceChangeReasonEntity
import com.example.data.model.ProductBOMEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ProductPriceHistoryEntity
import com.example.data.model.ProductVariantEntity
import com.example.data.model.ProductionConsumableEntity
import com.example.data.model.ProductionEntity
import com.example.data.model.PurchaseItemEntity
import com.example.data.model.PurchaseOrderEntity
import com.example.data.model.RollUsageEntity
import com.example.data.model.SaleOrderEntity
import com.example.data.model.SalesChannelEntity
import com.example.data.model.ShippingExpenseEntity
import com.example.data.model.SizeEntity
import com.example.data.model.SupplierEntity
import com.example.data.model.CustomerPaymentEntity
import com.example.data.model.SupplierPaymentEntity
import com.example.data.model.ShippingRateHistoryEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.InventoryAdjustmentEntity
import com.example.data.model.FabricCategoryEntity
import com.example.data.model.ShippingCompanyEntity
import com.example.data.model.WaybillItemEntity
import com.example.data.model.BaseCostConfigEntity
import com.example.data.dao.FabricCategoryDao
import com.example.data.dao.ShippingCompanyDao
import com.example.data.dao.WaybillItemDao
import com.example.data.dao.BaseCostConfigDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
  entities = [
    FabricEntity::class,
    CuttingEntity::class,
    ProductionEntity::class,
    InventoryEntity::class,
    SaleOrderEntity::class,
    CustomerEntity::class,
    SupplierEntity::class,
    ModelStandardEntity::class,
    FactorySettingsEntity::class,
    FabricRollEntity::class,
    RollUsageEntity::class,
    AccessoryPurchaseEntity::class,
    ShippingExpenseEntity::class,
    OrderStatusHistoryEntity::class,
    FixedCostEntity::class,
    ProductionConsumableEntity::class,
    CategoryEntity::class,
    ProductEntity::class,
    ColorEntity::class,
    SizeEntity::class,
    ProductVariantEntity::class,
    MaterialEntity::class,
    MaterialUnitEntity::class,
    ProductBOMEntity::class,
    MaterialPriceHistoryEntity::class,
    ProductPriceHistoryEntity::class,
    PriceChangeReasonEntity::class,
    InventoryLedgerEntity::class,
    PurchaseOrderEntity::class,
    PurchaseItemEntity::class,
    SalesChannelEntity::class,
    CustomerPaymentEntity::class,
    SupplierPaymentEntity::class,
    ShippingRateHistoryEntity::class,
    AuditLogEntity::class,
    InventoryAdjustmentEntity::class,
    FabricCategoryEntity::class,
    ShippingCompanyEntity::class,
    WaybillItemEntity::class,
    BaseCostConfigEntity::class,
  ],
  version = 9,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun fabricDao(): FabricDao
  abstract fun cuttingDao(): CuttingDao
  abstract fun productionDao(): ProductionDao
  abstract fun inventoryDao(): InventoryDao
  abstract fun saleOrderDao(): SaleOrderDao
  abstract fun customerDao(): CustomerDao
  abstract fun supplierDao(): SupplierDao
  abstract fun modelStandardDao(): ModelStandardDao
  abstract fun factorySettingsDao(): FactorySettingsDao
  abstract fun fabricRollDao(): FabricRollDao
  abstract fun rollUsageDao(): RollUsageDao
  abstract fun accessoryPurchaseDao(): AccessoryPurchaseDao
  abstract fun shippingExpenseDao(): ShippingExpenseDao
  abstract fun orderStatusHistoryDao(): OrderStatusHistoryDao
  abstract fun fixedCostDao(): FixedCostDao
  abstract fun productionConsumableDao(): ProductionConsumableDao

  abstract fun categoryDao(): CategoryDao
  abstract fun productDao(): ProductDao
  abstract fun colorDao(): ColorDao
  abstract fun sizeDao(): SizeDao
  abstract fun productVariantDao(): ProductVariantDao
  abstract fun materialDao(): MaterialDao
  abstract fun materialUnitDao(): MaterialUnitDao
  abstract fun productBOMDao(): ProductBOMDao
  abstract fun materialPriceHistoryDao(): MaterialPriceHistoryDao
  abstract fun productPriceHistoryDao(): ProductPriceHistoryDao
  abstract fun priceChangeReasonDao(): PriceChangeReasonDao
  abstract fun inventoryLedgerDao(): InventoryLedgerDao
  abstract fun purchaseOrderDao(): PurchaseOrderDao
  abstract fun purchaseItemDao(): PurchaseItemDao
  abstract fun salesChannelDao(): SalesChannelDao
  abstract fun customerPaymentDao(): CustomerPaymentDao
  abstract fun supplierPaymentDao(): SupplierPaymentDao
  abstract fun shippingRateHistoryDao(): ShippingRateHistoryDao
  abstract fun auditLogDao(): AuditLogDao
  abstract fun inventoryAdjustmentDao(): InventoryAdjustmentDao

  abstract fun fabricCategoryDao(): FabricCategoryDao
  abstract fun shippingCompanyDao(): ShippingCompanyDao
  abstract fun waybillItemDao(): WaybillItemDao
  abstract fun baseCostConfigDao(): BaseCostConfigDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    val MIGRATION_4_5 = object : Migration(4, 5) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `fabric_rolls` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `rollCode` TEXT NOT NULL,
            `inboundDate` TEXT NOT NULL,
            `inboundTimestamp` INTEGER NOT NULL,
            `fabricType` TEXT NOT NULL,
            `fabricCode` TEXT NOT NULL,
            `color` TEXT NOT NULL,
            `initialMeters` REAL NOT NULL,
            `remainingMeters` REAL NOT NULL,
            `weightKg` REAL NOT NULL,
            `buyPricePerMeter` INTEGER NOT NULL,
            `buyPricePerKg` INTEGER NOT NULL,
            `allocatedShippingCost` INTEGER NOT NULL,
            `status` TEXT NOT NULL,
            `supplierName` TEXT NOT NULL,
            `batchNumber` TEXT NOT NULL,
            `shippingExpenseId` INTEGER
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `roll_usages` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `rollId` INTEGER NOT NULL,
            `rollCode` TEXT NOT NULL,
            `productionId` INTEGER NOT NULL,
            `cuttingId` INTEGER NOT NULL,
            `modelCode` TEXT NOT NULL,
            `modelName` TEXT NOT NULL,
            `metersUsed` REAL NOT NULL,
            `weightKgUsed` REAL NOT NULL,
            `usageDate` TEXT NOT NULL,
            `usageTimestamp` INTEGER NOT NULL,
            `allocatedFabricCost` INTEGER NOT NULL,
            `allocatedShippingCost` INTEGER NOT NULL,
            `note` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `accessory_purchases` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `accessoryId` INTEGER NOT NULL,
            `accessoryCode` TEXT NOT NULL,
            `accessoryName` TEXT NOT NULL,
            `purchaseDate` TEXT NOT NULL,
            `purchaseTimestamp` INTEGER NOT NULL,
            `quantity` REAL NOT NULL,
            `unit` TEXT NOT NULL,
            `unitCostPrice` INTEGER NOT NULL,
            `totalCostPrice` INTEGER NOT NULL,
            `supplierName` TEXT NOT NULL,
            `allocatedShippingCost` INTEGER NOT NULL,
            `metersPerKg` REAL NOT NULL,
            `pricePerMeter` INTEGER NOT NULL,
            `shippingExpenseId` INTEGER,
            `note` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `shipping_expenses` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `trackingNumber` TEXT NOT NULL,
            `title` TEXT NOT NULL,
            `date` TEXT NOT NULL,
            `timestamp` INTEGER NOT NULL,
            `totalAmount` INTEGER NOT NULL,
            `inboundType` TEXT NOT NULL,
            `itemCount` INTEGER NOT NULL,
            `totalWeightKg` REAL NOT NULL,
            `totalQuantity` REAL NOT NULL,
            `unit` TEXT NOT NULL,
            `allocationMethod` TEXT NOT NULL,
            `costPerUnit` INTEGER NOT NULL,
            `carrierName` TEXT NOT NULL,
            `notes` TEXT NOT NULL
          )"""
        )
      }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `order_status_history` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `orderId` INTEGER NOT NULL,
            `orderNumber` TEXT NOT NULL,
            `oldStatus` TEXT NOT NULL,
            `newStatus` TEXT NOT NULL,
            `date` TEXT NOT NULL,
            `time` TEXT NOT NULL,
            `timestamp` INTEGER NOT NULL,
            `note` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `fixed_costs` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `title` TEXT NOT NULL,
            `amount` INTEGER NOT NULL,
            `scope` TEXT NOT NULL,
            `targetCategory` TEXT NOT NULL,
            `targetProductCodes` TEXT NOT NULL,
            `targetProductionId` INTEGER NOT NULL,
            `date` TEXT NOT NULL,
            `timestamp` INTEGER NOT NULL,
            `notes` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `production_consumables` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `productionId` INTEGER NOT NULL,
            `accessoryCode` TEXT NOT NULL,
            `accessoryName` TEXT NOT NULL,
            `quantityUsed` REAL NOT NULL,
            `unit` TEXT NOT NULL,
            `unitCostPrice` INTEGER NOT NULL,
            `totalCost` INTEGER NOT NULL,
            `date` TEXT NOT NULL
          )"""
        )
        try {
          db.execSQL("ALTER TABLE `production_records` ADD COLUMN `rollId` INTEGER")
          db.execSQL("ALTER TABLE `production_records` ADD COLUMN `rollCode` TEXT NOT NULL DEFAULT ''")
          db.execSQL("ALTER TABLE `production_records` ADD COLUMN `consumablesSummary` TEXT NOT NULL DEFAULT ''")
          db.execSQL("ALTER TABLE `production_records` ADD COLUMN `orderId` INTEGER")
        } catch (_: Exception) {
          // If already added
        }
      }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `categories` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `code` TEXT NOT NULL,
            `name` TEXT NOT NULL,
            `isActive` INTEGER NOT NULL,
            `createdDate` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `products` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `code` TEXT NOT NULL,
            `name` TEXT NOT NULL,
            `categoryId` INTEGER NOT NULL,
            `categoryName` TEXT NOT NULL,
            `description` TEXT NOT NULL,
            `isActive` INTEGER NOT NULL,
            `suggestedSellingPrice` INTEGER NOT NULL,
            `manualOverridePrice` INTEGER,
            `isManualPrice` INTEGER NOT NULL,
            `currentCostPrice` INTEGER NOT NULL,
            `sewingWage` INTEGER NOT NULL,
            `allocatedFreightCost` INTEGER NOT NULL,
            `overheadCost` INTEGER NOT NULL,
            `targetProfitPercent` REAL NOT NULL,
            `profitCalculationType` TEXT NOT NULL,
            `lastPriceUpdateTimestamp` INTEGER NOT NULL,
            `lastPriceUpdateDate` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `colors` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `name` TEXT NOT NULL,
            `colorHex` TEXT NOT NULL,
            `isActive` INTEGER NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `sizes` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `name` TEXT NOT NULL,
            `sortOrder` INTEGER NOT NULL,
            `isActive` INTEGER NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `product_variants` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `productId` INTEGER NOT NULL,
            `productCode` TEXT NOT NULL,
            `productName` TEXT NOT NULL,
            `colorId` INTEGER NOT NULL,
            `colorName` TEXT NOT NULL,
            `sizeId` INTEGER NOT NULL,
            `sizeName` TEXT NOT NULL,
            `onHandQuantity` INTEGER NOT NULL,
            `reservedQuantity` INTEGER NOT NULL,
            `currentUnitCost` INTEGER NOT NULL,
            `currentUnitSalePrice` INTEGER NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `materials` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `code` TEXT NOT NULL,
            `name` TEXT NOT NULL,
            `category` TEXT NOT NULL,
            `unit` TEXT NOT NULL,
            `currentPrice` INTEGER NOT NULL,
            `lastPurchasePrice` INTEGER NOT NULL,
            `lastPriceSource` TEXT NOT NULL,
            `lastPriceChangeDate` TEXT NOT NULL,
            `lastPriceChangeTimestamp` INTEGER NOT NULL,
            `stockQuantity` REAL NOT NULL,
            `minStockThreshold` REAL NOT NULL,
            `metersPerKg` REAL NOT NULL,
            `supplierName` TEXT NOT NULL,
            `isActive` INTEGER NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `material_units` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `code` TEXT NOT NULL,
            `name` TEXT NOT NULL,
            `isBaseUnit` INTEGER NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `product_boms` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `productId` INTEGER NOT NULL,
            `productCode` TEXT NOT NULL,
            `materialId` INTEGER NOT NULL,
            `materialName` TEXT NOT NULL,
            `materialCategory` TEXT NOT NULL,
            `unit` TEXT NOT NULL,
            `standardQuantity` REAL NOT NULL,
            `unitRate` INTEGER NOT NULL,
            `note` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `material_price_history` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `materialId` INTEGER NOT NULL,
            `materialName` TEXT NOT NULL,
            `oldPrice` INTEGER NOT NULL,
            `newPrice` INTEGER NOT NULL,
            `date` TEXT NOT NULL,
            `timestamp` INTEGER NOT NULL,
            `changeAmount` INTEGER NOT NULL,
            `changePercent` REAL NOT NULL,
            `reason` TEXT NOT NULL,
            `source` TEXT NOT NULL,
            `supplierName` TEXT NOT NULL,
            `notes` TEXT NOT NULL,
            `recordedBy` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `product_price_history` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `productId` INTEGER NOT NULL,
            `productCode` TEXT NOT NULL,
            `productName` TEXT NOT NULL,
            `oldCostPrice` INTEGER NOT NULL,
            `newCostPrice` INTEGER NOT NULL,
            `oldSalePrice` INTEGER NOT NULL,
            `newSalePrice` INTEGER NOT NULL,
            `date` TEXT NOT NULL,
            `timestamp` INTEGER NOT NULL,
            `reason` TEXT NOT NULL,
            `triggeringMaterialId` INTEGER,
            `triggeringMaterialName` TEXT NOT NULL,
            `costChangeAmount` INTEGER NOT NULL,
            `costChangePercent` REAL NOT NULL,
            `notes` TEXT NOT NULL,
            `breakdownJson` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `price_change_reasons` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `title` TEXT NOT NULL,
            `isDefault` INTEGER NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `inventory_ledger` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `timestamp` INTEGER NOT NULL,
            `date` TEXT NOT NULL,
            `itemType` TEXT NOT NULL,
            `itemId` INTEGER NOT NULL,
            `itemCode` TEXT NOT NULL,
            `itemName` TEXT NOT NULL,
            `color` TEXT NOT NULL,
            `size` TEXT NOT NULL,
            `transactionType` TEXT NOT NULL,
            `quantityChange` REAL NOT NULL,
            `balanceAfter` REAL NOT NULL,
            `unit` TEXT NOT NULL,
            `unitPriceAtTime` INTEGER NOT NULL,
            `relatedDocumentNumber` TEXT NOT NULL,
            `notes` TEXT NOT NULL,
            `operator` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `purchase_orders` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `orderNumber` TEXT NOT NULL,
            `supplierId` INTEGER NOT NULL,
            `supplierName` TEXT NOT NULL,
            `orderDate` TEXT NOT NULL,
            `expectedDeliveryDate` TEXT NOT NULL,
            `totalAmount` INTEGER NOT NULL,
            `shippingAmount` INTEGER NOT NULL,
            `paidAmount` INTEGER NOT NULL,
            `status` TEXT NOT NULL,
            `notes` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `purchase_items` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `purchaseOrderId` INTEGER NOT NULL,
            `materialId` INTEGER NOT NULL,
            `materialName` TEXT NOT NULL,
            `orderedQuantity` REAL NOT NULL,
            `deliveredQuantity` REAL NOT NULL,
            `unit` TEXT NOT NULL,
            `unitPrice` INTEGER NOT NULL,
            `totalPrice` INTEGER NOT NULL,
            `allocatedShipping` INTEGER NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `sales_channels` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `name` TEXT NOT NULL,
            `code` TEXT NOT NULL,
            `isActive` INTEGER NOT NULL
          )"""
        )
      }
    }

    val MIGRATION_7_8 = object : Migration(7, 8) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `customer_payments` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `customerId` INTEGER NOT NULL,
            `customerName` TEXT NOT NULL,
            `orderId` INTEGER,
            `orderNumber` TEXT NOT NULL,
            `amount` INTEGER NOT NULL,
            `date` TEXT NOT NULL,
            `timestamp` INTEGER NOT NULL,
            `paymentMethod` TEXT NOT NULL,
            `referenceNumber` TEXT NOT NULL,
            `notes` TEXT NOT NULL,
            `recordedBy` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `supplier_payments` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `supplierId` INTEGER NOT NULL,
            `supplierName` TEXT NOT NULL,
            `purchaseOrderId` INTEGER,
            `orderNumber` TEXT NOT NULL,
            `amount` INTEGER NOT NULL,
            `date` TEXT NOT NULL,
            `timestamp` INTEGER NOT NULL,
            `paymentMethod` TEXT NOT NULL,
            `referenceNumber` TEXT NOT NULL,
            `notes` TEXT NOT NULL,
            `recordedBy` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `shipping_rate_history` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `oldRate` INTEGER NOT NULL,
            `newRate` INTEGER NOT NULL,
            `changeAmount` INTEGER NOT NULL,
            `changePercent` REAL NOT NULL,
            `date` TEXT NOT NULL,
            `timestamp` INTEGER NOT NULL,
            `reason` TEXT NOT NULL,
            `source` TEXT NOT NULL,
            `notes` TEXT NOT NULL,
            `recordedBy` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `audit_logs` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `timestamp` INTEGER NOT NULL,
            `date` TEXT NOT NULL,
            `entityName` TEXT NOT NULL,
            `entityId` INTEGER NOT NULL,
            `action` TEXT NOT NULL,
            `oldValue` TEXT NOT NULL,
            `newValue` TEXT NOT NULL,
            `reason` TEXT NOT NULL,
            `recordedBy` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `inventory_adjustments` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `timestamp` INTEGER NOT NULL,
            `date` TEXT NOT NULL,
            `itemType` TEXT NOT NULL,
            `itemId` INTEGER NOT NULL,
            `itemCode` TEXT NOT NULL,
            `itemName` TEXT NOT NULL,
            `color` TEXT NOT NULL,
            `size` TEXT NOT NULL,
            `adjustmentType` TEXT NOT NULL,
            `quantity` REAL NOT NULL,
            `reason` TEXT NOT NULL,
            `notes` TEXT NOT NULL,
            `operator` TEXT NOT NULL
          )"""
        )
        try {
          db.execSQL("ALTER TABLE `sales_orders` ADD COLUMN `channel` TEXT NOT NULL DEFAULT 'فروش حضوری'")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `sales_orders` ADD COLUMN `customerId` INTEGER")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `sales_orders` ADD COLUMN `color` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `sales_orders` ADD COLUMN `size` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `sales_orders` ADD COLUMN `variantId` INTEGER")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `sales_orders` ADD COLUMN `shippingCost` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `sales_orders` ADD COLUMN `costSnapshot` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `sales_orders` ADD COLUMN `salePriceSnapshot` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `customers` ADD COLUMN `totalPaid` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `suppliers` ADD COLUMN `paidAmount` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `suppliers` ADD COLUMN `currentDebt` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
      }
    }

    val MIGRATION_8_9 = object : Migration(8, 9) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `fabric_categories` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `code` TEXT NOT NULL,
            `name` TEXT NOT NULL,
            `description` TEXT NOT NULL,
            `isActive` INTEGER NOT NULL,
            `createdDate` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `shipping_companies` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `name` TEXT NOT NULL,
            `phone` TEXT NOT NULL,
            `address` TEXT NOT NULL,
            `notes` TEXT NOT NULL,
            `isActive` INTEGER NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `waybill_items` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `waybillId` INTEGER NOT NULL,
            `supplierId` INTEGER,
            `supplierName` TEXT NOT NULL,
            `itemType` TEXT NOT NULL,
            `itemId` INTEGER NOT NULL,
            `itemCode` TEXT NOT NULL,
            `itemName` TEXT NOT NULL,
            `quantity` REAL NOT NULL,
            `unit` TEXT NOT NULL,
            `purchaseValue` INTEGER NOT NULL,
            `weightKg` REAL NOT NULL,
            `volumeM3` REAL NOT NULL,
            `shippingAllocation` INTEGER NOT NULL,
            `notes` TEXT NOT NULL
          )"""
        )
        db.execSQL(
          """CREATE TABLE IF NOT EXISTS `base_cost_configs` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `title` TEXT NOT NULL,
            `costType` TEXT NOT NULL,
            `amount` INTEGER NOT NULL,
            `unit` TEXT NOT NULL,
            `isPerGarment` INTEGER NOT NULL,
            `isActive` INTEGER NOT NULL,
            `notes` TEXT NOT NULL
          )"""
        )
        try {
          db.execSQL("ALTER TABLE `fabric_rolls` ADD COLUMN `remainingWeightKg` REAL NOT NULL DEFAULT 0.0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `fabric_rolls` ADD COLUMN `otherDirectCost` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `fabric_rolls` ADD COLUMN `fabricCategoryId` INTEGER")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `fabric_rolls` ADD COLUMN `fabricCategoryName` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `shipping_expenses` ADD COLUMN `deliveryDate` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `shipping_expenses` ADD COLUMN `shippingCompanyId` INTEGER")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `shipping_expenses` ADD COLUMN `shippingCompanyContact` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `shipping_expenses` ADD COLUMN `status` TEXT NOT NULL DEFAULT 'ثبت شده'")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `partNumber` INTEGER NOT NULL DEFAULT 1")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `partTitle` TEXT NOT NULL DEFAULT 'پارت برش ۱'")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `rollId` INTEGER")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `rollCode` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `productId` INTEGER")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `productCode` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `productName` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `size` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `color` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `workType` TEXT NOT NULL DEFAULT 'تولید برای انبار'")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `customerId` INTEGER")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `customerName` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `orderId` INTEGER")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `orderNumber` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `metersUsed` REAL NOT NULL DEFAULT 0.0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `weightKgUsed` REAL NOT NULL DEFAULT 0.0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `sellingPrice` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `targetProfit` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `targetMargin` REAL NOT NULL DEFAULT 0.0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `notes` TEXT NOT NULL DEFAULT ''")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `timestamp` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `fabricCost` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `allocatedShippingCost` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `accessoriesCost` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `tailorCost` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `overheadCost` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `otherDirectCost` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `totalCost` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `cutting_orders` ADD COLUMN `isStockAdded` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `production_consumables` ADD COLUMN `cuttingId` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
          db.execSQL("ALTER TABLE `production_consumables` ADD COLUMN `materialId` INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
      }
    }

    fun getDatabase(
      context: Context,
      scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    ): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "manufacturing_executive.db"
        )
          .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
          .addCallback(DatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    suspend fun resetDatabaseToDemo(db: AppDatabase) {
      db.clearAllTables()
      populateDatabase(db)
    }

    suspend fun populateDatabase(db: AppDatabase) {
      // 1. Model Standards
      val standards = listOf(
        ModelStandardEntity(
          modelCode = "M204",
          modelName = "هودی کلاه‌دار اورسایز M204",
          standardFabricConsumptionMeters = 1.40,
          standardWeightGrams = 370.0,
          sewingWage = 75000L,
          suggestedSalePrice = 850000L,
          baseFabricCostPerMeter = 210000L
        ),
        ModelStandardEntity(
          modelCode = "M201",
          modelName = "شلوار اسلش کژوال M201",
          standardFabricConsumptionMeters = 1.15,
          standardWeightGrams = 290.0,
          sewingWage = 55000L,
          suggestedSalePrice = 620000L,
          baseFabricCostPerMeter = 195000L
        ),
        ModelStandardEntity(
          modelCode = "M108",
          modelName = "تی‌شرت بیسیک پنبه M108",
          standardFabricConsumptionMeters = 0.85,
          standardWeightGrams = 180.0,
          sewingWage = 35000L,
          suggestedSalePrice = 390000L,
          baseFabricCostPerMeter = 160000L
        )
      )
      db.modelStandardDao().insertAll(standards)

      // 2. Fabrics (Raw materials with Meters and Kilograms)
      val fabrics = listOf(
        FabricEntity(
          name = "پنبه دورس ۳ نخ خارخورده",
          code = "M204",
          color = "مشکی زغالی",
          batchNumber = "PRT-982",
          supplierName = "نساجی تابان کاشان",
          rollCount = 8,
          totalMeters = 420.0,
          totalWeightKg = 155.0,
          buyPricePerMeter = 210000L,
          buyPricePerKg = 569000L,
          currentMarketPrice = 240000L,
          isLowStock = true, // Triggers alert as requested
          previousBuyPricePerMeter = 190000L,
          previousBuyPricePerKg = 515000L,
          previousPurchaseDate = "۱۲ بهمن",
          purchaseDate = "۱۴ اسفند"
        ),
        FabricEntity(
          name = "کتان بنگالین کشی",
          code = "M201",
          color = "سبز ارتشی",
          batchNumber = "PRT-975",
          supplierName = "نساجی جهان بافت",
          rollCount = 19,
          totalMeters = 1350.0,
          totalWeightKg = 390.0,
          buyPricePerMeter = 195000L,
          buyPricePerKg = 675000L,
          currentMarketPrice = 215000L,
          isLowStock = false,
          previousBuyPricePerMeter = 182000L,
          previousBuyPricePerKg = 630000L,
          previousPurchaseDate = "۲۵ بهمن",
          purchaseDate = "۱۳ اسفند"
        ),
        FabricEntity(
          name = "سوپر پنبه ۱۰۰٪ شانه شده",
          code = "M108",
          color = "سفید اپتیک",
          batchNumber = "PRT-960",
          supplierName = "شرکت بافندگی مهر",
          rollCount = 26,
          totalMeters = 2100.0,
          totalWeightKg = 378.0,
          buyPricePerMeter = 160000L,
          buyPricePerKg = 888000L,
          currentMarketPrice = 175000L,
          isLowStock = false,
          previousBuyPricePerMeter = 0L,
          previousBuyPricePerKg = 0L,
          previousPurchaseDate = "",
          purchaseDate = "۱۱ اسفند"
        ),
        FabricEntity(
          name = "گلکسی پنبه لاکرا دار",
          code = "M310",
          color = "طوسی ملانژ",
          batchNumber = "PRT-988",
          supplierName = "نساجی تابان کاشان",
          rollCount = 14,
          totalMeters = 980.0,
          totalWeightKg = 245.0,
          buyPricePerMeter = 225000L,
          buyPricePerKg = 900000L,
          currentMarketPrice = 250000L,
          isLowStock = false,
          previousBuyPricePerMeter = 205000L,
          previousBuyPricePerKg = 820000L,
          previousPurchaseDate = "۱۸ بهمن",
          purchaseDate = "۱۲ اسفند"
        )
      )
      db.fabricDao().insertAll(fabrics)

      // 3. Cutting Orders (with progress and audit)
      val cuttings = listOf(
        CuttingEntity(
          modelCode = "M204",
          modelName = "هودی کلاه‌دار اورسایز M204",
          fabricCode = "M204",
          targetQuantity = 1000,
          cutQuantity = 850,
          standardMetersPerItem = 1.40,
          actualMetersPerItem = 1.42,
          status = "در حال برش",
          date = "۱۴ اسفند"
        ),
        CuttingEntity(
          modelCode = "M201",
          modelName = "شلوار اسلش کژوال M201",
          fabricCode = "M201",
          targetQuantity = 600,
          cutQuantity = 480,
          standardMetersPerItem = 1.15,
          actualMetersPerItem = 1.36, // Abnormal consumption: 18% more!
          status = "در حال برش",
          date = "۱۳ اسفند"
        ),
        CuttingEntity(
          modelCode = "M108",
          modelName = "تی‌شرت بیسیک پنبه M108",
          fabricCode = "M108",
          targetQuantity = 1500,
          cutQuantity = 1500,
          standardMetersPerItem = 0.85,
          actualMetersPerItem = 0.84,
          status = "تکمیل شده",
          date = "۱۱ اسفند"
        )
      )
      db.cuttingDao().insertAll(cuttings)

      // 4. Production Records
      val productions = listOf(
        ProductionEntity(
          modelCode = "M204",
          modelName = "هودی کلاه‌دار اورسایز M204",
          quantity = 500,
          fabricRollsUsed = 10,
          fabricMetersUsed = 700.0,
          totalWeightKg = 185.0, // 370g per item
          sewingWagePerItem = 75000L,
          fabricPricePerMeter = 210000L,
          accessoriesCostPerItem = 32000L,
          status = "تکمیل شده",
          date = "۱۴ اسفند"
        ),
        ProductionEntity(
          modelCode = "M201",
          modelName = "شلوار اسلش کژوال M201",
          quantity = 400,
          fabricRollsUsed = 7,
          fabricMetersUsed = 544.0,
          totalWeightKg = 116.0,
          sewingWagePerItem = 55000L,
          fabricPricePerMeter = 195000L,
          accessoriesCostPerItem = 28000L,
          status = "در حال دوخت",
          date = "۱۳ اسفند"
        ),
        ProductionEntity(
          modelCode = "M108",
          modelName = "تی‌شرت بیسیک پنبه M108",
          quantity = 1200,
          fabricRollsUsed = 14,
          fabricMetersUsed = 1020.0,
          totalWeightKg = 216.0,
          sewingWagePerItem = 35000L,
          fabricPricePerMeter = 160000L,
          accessoriesCostPerItem = 12000L,
          status = "بسته‌بندی",
          date = "۱۲ اسفند"
        )
      )
      db.productionDao().insertAll(productions)

      // 5. Inventory Items (Warehouse)
      val inventory = listOf(
        InventoryEntity(
          name = "هودی اورسایز M204",
          code = "PRD-204",
          category = "محصولات آماده",
          readyForShipment = 320,
          reservedQuantity = 120,
          availableForSale = 200,
          unitSalePrice = 850000L,
          unitCostPrice = 478000L,
          unitWeightGrams = 370.0,
          lastUpdated = "امروز، ساعت ۱۱:۳۰"
        ),
        InventoryEntity(
          name = "شلوار اسلش کژوال M201",
          code = "PRD-201",
          category = "محصولات آماده",
          readyForShipment = 180,
          reservedQuantity = 90,
          availableForSale = 150,
          unitSalePrice = 620000L,
          unitCostPrice = 348000L,
          unitWeightGrams = 290.0,
          lastUpdated = "دیروز"
        ),
        InventoryEntity(
          name = "تی‌شرت پنبه بیسیک M108",
          code = "PRD-108",
          category = "محصولات آماده",
          readyForShipment = 450,
          reservedQuantity = 300,
          availableForSale = 620,
          unitSalePrice = 390000L,
          unitCostPrice = 183000L,
          unitWeightGrams = 180.0,
          lastUpdated = "امروز، ساعت ۰۹:۱۵"
        ),
        InventoryEntity(
          name = "زیپ دنده‌فلزی ۸۰ سانت YKK",
          code = "ACC-012",
          category = "ملزومات",
          readyForShipment = 0,
          reservedQuantity = 400,
          availableForSale = 1800,
          unitSalePrice = 28000L,
          unitCostPrice = 24000L,
          unitWeightGrams = 25.0,
          unitType = "عدد",
          lastUpdated = "امروز",
          previousCostPrice = 21500L,
          previousCostDate = "۲۵ بهمن",
          supplierName = "صنایع زیپ و یراق پارس (YKK)"
        ),
        InventoryEntity(
          name = "بند کلاه گرد بافت اعلا",
          code = "ACC-045",
          category = "ملزومات",
          readyForShipment = 0,
          reservedQuantity = 300,
          availableForSale = 2200,
          unitSalePrice = 9500L,
          unitCostPrice = 7200L,
          unitWeightGrams = 12.0,
          unitType = "متر",
          lastUpdated = "۱۳ اسفند",
          previousCostPrice = 6400L,
          previousCostDate = "۱۵ بهمن",
          supplierName = "تولیدی بند و کش الماس"
        )
      )
      db.inventoryDao().insertAll(inventory)

      // 6. Sales Orders (with timeline and delays)
      val orders = listOf(
        SaleOrderEntity(
          orderNumber = "#2048",
          customerName = "پوشاک سپهر تهران",
          customerPhone = "۰۹۱۲۳۴۵۶۷۸۹",
          modelCode = "M204",
          modelName = "هودی کلاه‌دار اورسایز M204",
          quantity = 350,
          unitPrice = 850000L,
          unitCost = 478000L,
          discountAmount = 3500000L,
          paidAmount = 180000000L,
          orderDate = "۹ اسفند",
          deliveryStatus = "در تولید",
          isDelayed = true,
          delayDays = 2 // Alert requirement: #2048 عقب افتاده ۲ روز تأخیر
        ),
        SaleOrderEntity(
          orderNumber = "#2049",
          customerName = "بوتیک زنجیره‌ای الگانس",
          customerPhone = "۰۹۱۲۹۸۷۶۵۴۳",
          modelCode = "M201",
          modelName = "شلوار اسلش کژوال M201",
          quantity = 240,
          unitPrice = 620000L,
          unitCost = 348000L,
          discountAmount = 1800000L,
          paidAmount = 147000000L,
          orderDate = "۱۲ اسفند",
          deliveryStatus = "آماده ارسال",
          isDelayed = false
        ),
        SaleOrderEntity(
          orderNumber = "#2050",
          customerName = "پخش عمده آوا شیراز",
          customerPhone = "۰۹۱۷۱۱۱۴۴۵۵",
          modelCode = "M108",
          modelName = "تی‌شرت بیسیک پنبه M108",
          quantity = 800,
          unitPrice = 390000L,
          unitCost = 183000L,
          discountAmount = 5000000L,
          paidAmount = 250000000L,
          orderDate = "۱۳ اسفند",
          deliveryStatus = "در حال تکمیل",
          isDelayed = false
        ),
        SaleOrderEntity(
          orderNumber = "#2051",
          customerName = "فروشگاه مد امروز اصفهان",
          customerPhone = "۰۹۱۳۲۲۲۶۶۷۷",
          modelCode = "M204",
          modelName = "هودی کلاه‌دار اورسایز M204",
          quantity = 180,
          unitPrice = 850000L,
          unitCost = 478000L,
          discountAmount = 0L,
          paidAmount = 153000000L,
          orderDate = "۱۴ اسفند",
          deliveryStatus = "ثبت شده",
          isDelayed = false
        ),
        SaleOrderEntity(
          orderNumber = "#2045",
          customerName = "مرکز خرید رویال تبریز",
          customerPhone = "۰۹۱۴۳۳۳۸۸۹۹",
          modelCode = "M201",
          modelName = "شلوار اسلش کژوال M201",
          quantity = 300,
          unitPrice = 620000L,
          unitCost = 348000L,
          discountAmount = 2000000L,
          paidAmount = 184000000L,
          orderDate = "۶ اسفند",
          deliveryStatus = "تحویل شده",
          isDelayed = false
        )
      )
      db.saleOrderDao().insertAll(orders)

      // 7. Customers (Mini-dashboard profiles)
      val customers = listOf(
        CustomerEntity(
          name = "پوشاک سپهر تهران (حاج احمد سپهری)",
          company = "بازرگانی سپهر پارس",
          phone = "۰۹۱۲۳۴۵۶۷۸۹",
          address = "تهران، بازار بزرگ، سرای حاج حسن، پلاک ۴۲",
          category = "عمده‌فروش",
          totalPurchases = 4850000000L,
          orderCount = 14,
          currentDebt = 114000000L,
          tier = "خرید سوم+",
          popularModels = "M204, M201",
          lastOrderDate = "۹ اسفند"
        ),
        CustomerEntity(
          name = "بوتیک زنجیره‌ای الگانس",
          company = "مجموعه فروشگاه‌های الگانس",
          phone = "۰۹۱۲۹۸۷۶۵۴۳",
          address = "تهران، شهرک غرب، مرکز خرید گلستان، طبقه اول",
          category = "فروشگاه زنجیره‌ای",
          totalPurchases = 2150000000L,
          orderCount = 8,
          currentDebt = 0L,
          tier = "خرید سوم+",
          popularModels = "M201, M108",
          lastOrderDate = "۱۲ اسفند"
        ),
        CustomerEntity(
          name = "پخش عمده آوا شیراز",
          company = "پخش پوشاک آوا",
          phone = "۰۹۱۷۱۱۱۴۴۵۵",
          address = "شیراز، بلوار مدرس، مجتمع تجاری بهار",
          category = "عمده‌فروش",
          totalPurchases = 980000000L,
          orderCount = 3,
          currentDebt = 57000000L,
          tier = "خرید دوم",
          popularModels = "M108",
          lastOrderDate = "۱۳ اسفند"
        ),
        CustomerEntity(
          name = "فروشگاه مد امروز اصفهان",
          company = "مد امروز ایران",
          phone = "۰۹۱۳۲۲۲۶۶۷۷",
          address = "اصفهان، خیابان چهارباغ بالا",
          category = "بوتیک و آنلاین",
          totalPurchases = 153000000L,
          orderCount = 1,
          currentDebt = 0L,
          tier = "خرید اول",
          popularModels = "M204",
          lastOrderDate = "۱۴ اسفند"
        )
      )
      db.customerDao().insertAll(customers)

      // 8. Suppliers
      val suppliers = listOf(
        SupplierEntity(
          name = "حاج رضا تابان",
          storeName = "نساجی تابان کاشان",
          mobile = "۰۹۱۲۱۱۱۹۹۸۸",
          phone = "۰۳۱۵۵۲۲۹۹۰۰",
          address = "کاشان، شهرک صنعتی راوند، بلوار نساجی، پلاک ۱۲",
          distributionCategory = "پارچه دورس، پنبه و ملانژ",
          description = "تأمین‌کننده اصلی پارچه‌های پاییزه و زمستانه با شرایط چکی ۶۰ روزه",
          supplyType = "پارچه دورس و پنبه",
          totalPurchases = 6200000000L,
          lastPurchaseDate = "۱۲ اسفند",
          priceHistoryNote = "افزایش ۸ درصدی در پارت جدید به علت نوسان نخ پنبه"
        ),
        SupplierEntity(
          name = "مهندس علیرضا جهان‌بخش",
          storeName = "بازرگانی نساجی جهان بافت",
          mobile = "۰۹۱۲۳۳۳۴۴۵۵",
          phone = "۰۲۱۸۸۳۳۵۵۴۴",
          address = "تهران، خیابان خیام، کوچه بازار پاچنار، پلاک ۴۸",
          distributionCategory = "پارچه کتان، بنگالین و لی کاغذی",
          description = "تأمین پارچه‌های شلواری با ضمانت ثبات رنگ و عدم آبرفت",
          supplyType = "پارچه کتان و بنگالین",
          totalPurchases = 3800000000L,
          lastPurchaseDate = "۱۰ اسفند",
          priceHistoryNote = "قیمت ثابت با تخفیف ۵ درصدی خرید تناژ"
        ),
        SupplierEntity(
          name = "محمدحسین پارسا",
          storeName = "صنایع زیپ و یراق پارس (YKK)",
          mobile = "۰۹۱۲۵۵۵۶۶۷۷",
          phone = "۰۲۱۵۵۶۶۷۷۸۸",
          address = "تهران، بازار بزرگ، سرای مشیر خلوت، طبقه همکف، پلاک ۱۰",
          distributionCategory = "زیپ، سرزیپ فلزی، دکمه و مارک",
          description = "واردات و توزیع مستقیم یراق‌آلات درجه یک پوشاک اسپرت",
          supplyType = "ملزومات (زیپ، دکمه، کش)",
          totalPurchases = 920000000L,
          lastPurchaseDate = "۷ اسفند",
          priceHistoryNote = "تحویل سریع ۲۴ ساعته با ثبات نرخ تا پایان ماه"
        ),
        SupplierEntity(
          name = "کریم اسماعیلی",
          storeName = "تولیدی بند و کش الماس",
          mobile = "۰۹۳۵۶۶۶۷۷۸۸",
          phone = "۰۲۱۶۶۷۷۸۸۹۹",
          address = "تهران، خیابان فلسطین جنوبی، کوچه مهرداد، بن‌بست اول",
          distributionCategory = "بند کلاه هودی، کش شلوار، مارک ژلاتینی",
          description = "تولید سفارشی بند گرد بافت و کش پهن با تراکم بالا",
          supplyType = "ملزومات (بند کلاه و کش شلوار)",
          totalPurchases = 410000000L,
          lastPurchaseDate = "۲ اسفند",
          priceHistoryNote = "کیفیت سوپر بدون رنگ‌دهی در شستشو"
        )
      )
      db.supplierDao().insertAll(suppliers)

      // 9. Factory Settings (Fixed Costs, Margins, Overheads & Preferences)
      db.factorySettingsDao().insertOrUpdate(
        FactorySettingsEntity(
          id = 1L,
          fixedShippingCostPerOrder = 180000L,
          fixedShippingCostPerRoll = 85000L,
          targetProfitMarginPercent = 35.0,
          overheadCostPerItem = 45000L,
          defaultAccessoriesCost = 32000L,
          isDarkTheme = true,
          companyName = "تولیدی برتر پوشاک"
        )
      )

      // 10. Shipping Freight Expenses (باربری مستقل و روش‌های تخصیص)
      val shippingExpenses = listOf(
        ShippingExpenseEntity(
          trackingNumber = "BR-9821",
          title = "باربری طاقه‌های دورس از کاشان (۱۰۰ طاقه)",
          date = "۱۲ اسفند",
          timestamp = System.currentTimeMillis() - 86400000L * 2,
          totalAmount = 10000000L, // ۱۰ میلیون تومان
          inboundType = "طاقه پارچه",
          itemCount = 100,
          totalWeightKg = 1950.0,
          totalQuantity = 5200.0,
          unit = "طاقه",
          allocationMethod = "PER_ITEM", // به ازای هر قلم/طاقه: 10,000,000 / 100 = 100,000 تومان
          costPerUnit = 100000L,
          carrierName = "باربری میهن تورج",
          notes = "تخصیص ۱۰۰,۰۰۰ تومان به ازای هر طاقه برای ۱۰۰ طاقه ورودی"
        ),
        ShippingExpenseEntity(
          trackingNumber = "BR-9844",
          title = "باربری ملزومات، نخ و زیپ از بازار تهران (۱۰۰ کیلو)",
          date = "۱۳ اسفند",
          timestamp = System.currentTimeMillis() - 86400000L,
          totalAmount = 5000000L, // ۵ میلیون تومان
          inboundType = "ملزومات",
          itemCount = 12,
          totalWeightKg = 100.0,
          totalQuantity = 100.0,
          unit = "کیلوگرم",
          allocationMethod = "PER_WEIGHT", // بر اساس وزن: 5,000,000 / 100 = 50,000 تومان هر کیلو
          costPerUnit = 50000L,
          carrierName = "پیک و باربری پیشتاز بازار",
          notes = "هزینه باربری هر کیلوگرم ۵۰,۰۰۰ تومان"
        ),
        ShippingExpenseEntity(
          trackingNumber = "BR-9860",
          title = "باربری ترکیبی پارچه کتان و کش شلوار",
          date = "۱۴ اسفند",
          timestamp = System.currentTimeMillis(),
          totalAmount = 4500000L,
          inboundType = "ترکیبی",
          itemCount = 45,
          totalWeightKg = 620.0,
          totalQuantity = 1800.0,
          unit = "کیلوگرم",
          allocationMethod = "WEIGHTED",
          costPerUnit = 7258L,
          carrierName = "باربری سریع ترابر",
          notes = "تخصیص وزنی نسبی"
        )
      )
      db.shippingExpenseDao().insertAll(shippingExpenses)

      // 11. Fabric Rolls (مدیریت طاقه‌ها با اطلاعات ورود، متراژ، وزن و باربری)
      val rolls = listOf(
        FabricRollEntity(
          rollCode = "ROL-101",
          inboundDate = "۱۲ اسفند",
          inboundTimestamp = System.currentTimeMillis() - 86400000L * 2,
          fabricType = "پنبه دورس ۳ نخ خارخورده",
          fabricCode = "M204",
          color = "مشکی زغالی",
          initialMeters = 120.0,
          remainingMeters = 95.0, // ۲۵ متر برای هودی مصرف شده
          weightKg = 44.0,
          buyPricePerMeter = 210000L,
          buyPricePerKg = 572000L,
          allocatedShippingCost = 100000L, // ۱۰۰ هزار تومان باربری این طاقه
          status = "در حال مصرف",
          supplierName = "نساجی تابان کاشان",
          batchNumber = "PRT-982",
          shippingExpenseId = 1L
        ),
        FabricRollEntity(
          rollCode = "ROL-102",
          inboundDate = "۱۲ اسفند",
          inboundTimestamp = System.currentTimeMillis() - 86400000L * 2,
          fabricType = "پنبه دورس ۳ نخ خارخورده",
          fabricCode = "M204",
          color = "مشکی زغالی",
          initialMeters = 110.0,
          remainingMeters = 110.0,
          weightKg = 40.5,
          buyPricePerMeter = 210000L,
          buyPricePerKg = 570000L,
          allocatedShippingCost = 100000L,
          status = "موجود",
          supplierName = "نساجی تابان کاشان",
          batchNumber = "PRT-982",
          shippingExpenseId = 1L
        ),
        FabricRollEntity(
          rollCode = "ROL-103",
          inboundDate = "۱۳ اسفند",
          inboundTimestamp = System.currentTimeMillis() - 86400000L,
          fabricType = "کتان بنگالین کشی",
          fabricCode = "M201",
          color = "سبز ارتشی",
          initialMeters = 130.0,
          remainingMeters = 85.0, // ۴۵ متر برای شلوار اسلش مصرف شده
          weightKg = 38.0,
          buyPricePerMeter = 195000L,
          buyPricePerKg = 667000L,
          allocatedShippingCost = 95000L,
          status = "در حال مصرف",
          supplierName = "نساجی جهان بافت",
          batchNumber = "PRT-975",
          shippingExpenseId = 1L
        ),
        FabricRollEntity(
          rollCode = "ROL-104",
          inboundDate = "۱۳ اسفند",
          inboundTimestamp = System.currentTimeMillis() - 86400000L,
          fabricType = "کتان بنگالین کشی",
          fabricCode = "M201",
          color = "سبز ارتشی",
          initialMeters = 140.0,
          remainingMeters = 140.0,
          weightKg = 41.0,
          buyPricePerMeter = 195000L,
          buyPricePerKg = 665000L,
          allocatedShippingCost = 95000L,
          status = "موجود",
          supplierName = "نساجی جهان بافت",
          batchNumber = "PRT-975",
          shippingExpenseId = 1L
        ),
        FabricRollEntity(
          rollCode = "ROL-105",
          inboundDate = "۱۱ اسفند",
          inboundTimestamp = System.currentTimeMillis() - 86400000L * 3,
          fabricType = "سوپر پنبه ۱۰۰٪ شانه شده",
          fabricCode = "M108",
          color = "سفید اپتیک",
          initialMeters = 150.0,
          remainingMeters = 150.0,
          weightKg = 27.0,
          buyPricePerMeter = 160000L,
          buyPricePerKg = 888000L,
          allocatedShippingCost = 80000L,
          status = "موجود",
          supplierName = "شرکت بافندگی مهر",
          batchNumber = "PRT-960",
          shippingExpenseId = 1L
        ),
        FabricRollEntity(
          rollCode = "ROL-106",
          inboundDate = "۱۰ اسفند",
          inboundTimestamp = System.currentTimeMillis() - 86400000L * 4,
          fabricType = "گلکسی پنبه لاکرا دار",
          fabricCode = "M310",
          color = "طوسی ملانژ",
          initialMeters = 100.0,
          remainingMeters = 0.0, // تمام شده
          weightKg = 25.0,
          buyPricePerMeter = 225000L,
          buyPricePerKg = 900000L,
          allocatedShippingCost = 85000L,
          status = "پایان یافته",
          supplierName = "نساجی تابان کاشان",
          batchNumber = "PRT-988",
          shippingExpenseId = 1L
        )
      )
      db.fabricRollDao().insertAll(rolls)

      // 12. Roll Usages (ثبت مصرف‌های چندگانه از یک طاقه)
      val usages = listOf(
        RollUsageEntity(
          rollId = 1L,
          rollCode = "ROL-101",
          productionId = 1L,
          cuttingId = 1L,
          modelCode = "M204",
          modelName = "هودی کلاه‌دار اورسایز M204",
          metersUsed = 25.0,
          weightKgUsed = 9.2,
          usageDate = "۱۴ اسفند",
          usageTimestamp = System.currentTimeMillis(),
          allocatedFabricCost = (25.0 * 210000L).toLong(),
          allocatedShippingCost = ((25.0 / 120.0) * 100000L).toLong(),
          note = "مصرف اولین پارت برش برای هودی زمستانه"
        ),
        RollUsageEntity(
          rollId = 3L,
          rollCode = "ROL-103",
          productionId = 2L,
          cuttingId = 2L,
          modelCode = "M201",
          modelName = "شلوار اسلش کژوال M201",
          metersUsed = 45.0,
          weightKgUsed = 13.1,
          usageDate = "۱۳ اسفند",
          usageTimestamp = System.currentTimeMillis() - 86400000L,
          allocatedFabricCost = (45.0 * 195000L).toLong(),
          allocatedShippingCost = ((45.0 / 130.0) * 95000L).toLong(),
          note = "برش پارت اول اسلش مردانه"
        )
      )
      db.rollUsageDao().insertAll(usages)

      // 13. Accessory Purchases (تاریخچه خرید ملزومات با واحدها و قیمت قدیم/جدید)
      val accessoryPurchases = listOf(
        AccessoryPurchaseEntity(
          accessoryId = 4L,
          accessoryCode = "ACC-01",
          accessoryName = "زیپ استخوانی دنده‌پلاستیک ۵۰ سانت",
          purchaseDate = "۱۳ اسفند",
          purchaseTimestamp = System.currentTimeMillis() - 86400000L,
          quantity = 500.0,
          unit = "عدد",
          unitCostPrice = 14500L, // قیمت جدید (سبز)
          totalCostPrice = 7250000L,
          supplierName = "صنایع زیپ و یراق پارس (YKK)",
          allocatedShippingCost = 350000L,
          metersPerKg = 0.0,
          pricePerMeter = 0L,
          shippingExpenseId = 2L,
          note = "قیمت قبلی ۱۲,۰۰۰ تومان بود"
        ),
        AccessoryPurchaseEntity(
          accessoryId = 6L,
          accessoryCode = "ACC-03",
          accessoryName = "نخ پنبه‌ای ۴۰/۲ پلی‌پلی (سوپر)",
          purchaseDate = "۱۲ اسفند",
          purchaseTimestamp = System.currentTimeMillis() - 86400000L * 2,
          quantity = 40.0,
          unit = "کیلوگرم",
          unitCostPrice = 380000L, // هر کیلوگرم ۳۸۰,۰۰۰ تومان
          totalCostPrice = 15200000L,
          supplierName = "شرکت بافندگی مهر",
          allocatedShippingCost = 50000L * 40L, // ۵۰,۰۰۰ تومان باربری هر کیلو
          metersPerKg = 4500.0, // ۴۵۰۰ متر در هر کیلو
          pricePerMeter = 84L, // Price Per Meter = 380,000 / 4500 = 84 Toman
          shippingExpenseId = 2L,
          note = "محاسبه خودکار متری ۸۴ تومان بر اساس ۴۵۰۰ متر در کیلو"
        )
      )
      db.accessoryPurchaseDao().insertAll(accessoryPurchases)

      // 14. Fixed Costs with distinct Scopes (هزینه‌های ثابت با دامنه‌های مختلف تخصیص)
      val fixedCosts = listOf(
        FixedCostEntity(
          title = "اجاره ماهانه کارگاه و سالن دوخت",
          amount = 35000000L,
          scope = "ALL_PRODUCTS",
          targetCategory = "",
          targetProductCodes = "",
          targetProductionId = 0L,
          date = "۰۱ اسفند",
          timestamp = System.currentTimeMillis() - 86400000L * 15,
          notes = "تسهیم عمومی روی کل تیراژ ماهانه کارگاه"
        ),
        FixedCostEntity(
          title = "طراحی الگو، شابلون و چیدمان سایزبندی هودی",
          amount = 4500000L,
          scope = "PRODUCT_CATEGORY",
          targetCategory = "محصولات آماده",
          targetProductCodes = "HD-204",
          targetProductionId = 0L,
          date = "۰۵ اسفند",
          timestamp = System.currentTimeMillis() - 86400000L * 10,
          notes = "تخصیص اختصاصی به دسته هودی و سویشرت"
        ),
        FixedCostEntity(
          title = "سرویس، روغن‌کاری و استهلاک چرخ‌های راسته و میان‌دوز",
          amount = 6200000L,
          scope = "SELECTED_PRODUCTS",
          targetCategory = "",
          targetProductCodes = "HD-204,M201,M108",
          targetProductionId = 0L,
          date = "۱۰ اسفند",
          timestamp = System.currentTimeMillis() - 86400000L * 5,
          notes = "تخصیص به مدل‌های پرتیراژ کارگاه"
        )
      )
      db.fixedCostDao().insertAll(fixedCosts)

      // 15. Order Status History (تاریخچه تغییر وضعیت سفارشات)
      val orderHistory = listOf(
        OrderStatusHistoryEntity(
          orderId = 1L,
          orderNumber = "ORD-1403-101",
          oldStatus = "سفارش داده شده",
          newStatus = "در حال دوخت",
          date = "۱۴ اسفند",
          time = "۱۰:۳۰",
          timestamp = System.currentTimeMillis() - 86400000L * 2,
          note = "تخصیص پارچه دورس و آغاز برشکاری"
        ),
        OrderStatusHistoryEntity(
          orderId = 1L,
          orderNumber = "ORD-1403-101",
          oldStatus = "در حال دوخت",
          newStatus = "آماده ارسال / تکمیل موجودی",
          date = "۱۵ اسفند",
          time = "۱۶:۴۵",
          timestamp = System.currentTimeMillis() - 86400000L,
          note = "تکمیل دوخت و انتقال به انبار بسته‌بندی"
        ),
        OrderStatusHistoryEntity(
          orderId = 2L,
          orderNumber = "ORD-1403-102",
          oldStatus = "سفارش داده شده",
          newStatus = "در حال دوخت",
          date = "۱۵ اسفند",
          time = "۱۱:۱۵",
          timestamp = System.currentTimeMillis() - 86400000L,
          note = "آغاز دوخت پارت اسلش کژوال"
        )
      )
      db.orderStatusHistoryDao().insertAll(orderHistory)

      // 16. Product Categories
      val categories = listOf(
        CategoryEntity(code = "CAT-HOODIE", name = "هودی و دورس", isActive = true, createdDate = "۰۱ اسفند"),
        CategoryEntity(code = "CAT-SLASH", name = "شلوار اسلش کتان و اسپرت", isActive = true, createdDate = "۰۱ اسفند"),
        CategoryEntity(code = "CAT-TSHIRT", name = "تیشرت و پلوشرت پنبه", isActive = true, createdDate = "۰۱ اسفند")
      )
      db.categoryDao().insertAll(categories)

      // 17. Colors & Sizes
      val colors = listOf(
        ColorEntity(name = "مشکی زغالی", colorHex = "#1E1E1E"),
        ColorEntity(name = "سبز ارتشی", colorHex = "#4B5320"),
        ColorEntity(name = "سفید اپتیک", colorHex = "#FFFFFF"),
        ColorEntity(name = "طوسی ملانژ", colorHex = "#8E8E93"),
        ColorEntity(name = "سرمه‌ای سیر", colorHex = "#001F3F")
      )
      db.colorDao().insertAll(colors)

      val sizes = listOf(
        SizeEntity(name = "M", sortOrder = 1),
        SizeEntity(name = "L", sortOrder = 2),
        SizeEntity(name = "XL", sortOrder = 3),
        SizeEntity(name = "2XL", sortOrder = 4),
        SizeEntity(name = "3XL", sortOrder = 5)
      )
      db.sizeDao().insertAll(sizes)

      // 18. Raw Materials & Trims
      val materials = listOf(
        MaterialEntity(
          code = "MAT-DORES-3N",
          name = "پارچه دورس ۳ نخ خارخورده",
          category = "FABRIC",
          unit = "کیلوگرم",
          currentPrice = 569000L,
          lastPurchasePrice = 569000L,
          lastPriceSource = "PURCHASE_INVOICE",
          lastPriceChangeDate = "۱۴ اسفند",
          lastPriceChangeTimestamp = System.currentTimeMillis() - 86400000L * 2,
          stockQuantity = 155.0,
          minStockThreshold = 50.0,
          metersPerKg = 2.7,
          supplierName = "نساجی تابان کاشان"
        ),
        MaterialEntity(
          code = "MAT-BENGAL-SP",
          name = "پارچه کتان بنگالین کشی",
          category = "FABRIC",
          unit = "کیلوگرم",
          currentPrice = 675000L,
          lastPurchasePrice = 675000L,
          lastPriceSource = "PURCHASE_INVOICE",
          lastPriceChangeDate = "۱۳ اسفند",
          lastPriceChangeTimestamp = System.currentTimeMillis() - 86400000L * 3,
          stockQuantity = 390.0,
          minStockThreshold = 100.0,
          metersPerKg = 3.46,
          supplierName = "نساجی جهان بافت"
        ),
        MaterialEntity(
          code = "MAT-ELASTIC-4CM",
          name = "کش پهن ۴ سانت بافت ترک",
          category = "ACCESSORY",
          unit = "متر",
          currentPrice = 14500L,
          lastPurchasePrice = 14500L,
          lastPriceSource = "PURCHASE_INVOICE",
          lastPriceChangeDate = "۱۴ اسفند",
          lastPriceChangeTimestamp = System.currentTimeMillis() - 86400000L * 2,
          stockQuantity = 850.0,
          minStockThreshold = 200.0,
          metersPerKg = 0.0,
          supplierName = "بازرگانی یراق و ملزومات تهران"
        ),
        MaterialEntity(
          code = "MAT-ZIPPER-80CM",
          name = "زیپ استخوانی ۸۰ سانت کاپشنی",
          category = "ACCESSORY",
          unit = "عدد",
          currentPrice = 28000L,
          lastPurchasePrice = 28000L,
          lastPriceSource = "PURCHASE_INVOICE",
          lastPriceChangeDate = "۱۳ اسفند",
          lastPriceChangeTimestamp = System.currentTimeMillis() - 86400000L * 3,
          stockQuantity = 420.0,
          minStockThreshold = 100.0,
          metersPerKg = 0.0,
          supplierName = "شرکت ملزومات سراج"
        ),
        MaterialEntity(
          code = "MAT-LABEL-SATIN",
          name = "مارک و لیبل ساتن بافته پشت یقه",
          category = "ACCESSORY",
          unit = "عدد",
          currentPrice = 4500L,
          lastPurchasePrice = 4500L,
          lastPriceSource = "PURCHASE_INVOICE",
          lastPriceChangeDate = "۱۰ اسفند",
          lastPriceChangeTimestamp = System.currentTimeMillis() - 86400000L * 5,
          stockQuantity = 1200.0,
          minStockThreshold = 300.0,
          metersPerKg = 0.0,
          supplierName = "چاپ و لیبل نوین"
        )
      )
      db.materialDao().insertAll(materials)

      // 19. Products with Enterprise BOM & Dynamic Costing
      // Product 1: M204 Hoodie
      // BOM: 0.52 kg دورس 3نخ (approx 1.4m @ 569,000/kg = 295,880) + 1 عدد زیپ (28,000) + 1 لیبل (4,500) + اجرت دوخت (75,000) + باربری (15,000) + سربار (20,000) = ~438,380
      val hoodieProduct = ProductEntity(
        code = "HD-204",
        name = "هودی کلاه‌دار اورسایز M204",
        categoryId = 1L,
        categoryName = "هودی و دورس",
        description = "هودی کلاه‌دار پاییزه قواره آزاد با جیب کانگورویی و کشبافت درجه یک",
        isActive = true,
        suggestedSellingPrice = 850000L,
        manualOverridePrice = null,
        isManualPrice = false,
        currentCostPrice = 438000L,
        sewingWage = 75000L,
        allocatedFreightCost = 15000L,
        overheadCost = 20000L,
        targetProfitPercent = 94.0,
        profitCalculationType = "MARKUP",
        lastPriceUpdateTimestamp = System.currentTimeMillis(),
        lastPriceUpdateDate = "۱۵ اسفند"
      )
      val hoodieId = db.productDao().insert(hoodieProduct)

      val hoodieBOMs = listOf(
        ProductBOMEntity(
          productId = hoodieId,
          productCode = "HD-204",
          materialId = 1L,
          materialName = "پارچه دورس ۳ نخ خارخورده",
          materialCategory = "FABRIC",
          unit = "کیلوگرم",
          standardQuantity = 0.52,
          unitRate = 569000L,
          note = "مصرف ۱.۴۰ متر پارچه دورس بر اساس گرماژ ۳۷۰ گرم"
        ),
        ProductBOMEntity(
          productId = hoodieId,
          productCode = "HD-204",
          materialId = 4L,
          materialName = "زیپ استخوانی ۸۰ سانت کاپشنی",
          materialCategory = "ACCESSORY",
          unit = "عدد",
          standardQuantity = 1.0,
          unitRate = 28000L,
          note = "زیپ دنده‌درشت با کیفیت"
        ),
        ProductBOMEntity(
          productId = hoodieId,
          productCode = "HD-204",
          materialId = 5L,
          materialName = "مارک و لیبل ساتن بافته پشت یقه",
          materialCategory = "ACCESSORY",
          unit = "عدد",
          standardQuantity = 1.0,
          unitRate = 4500L,
          note = "شامل لیبل سایز و شستشو"
        )
      )
      db.productBOMDao().insertAll(hoodieBOMs)

      // Product 2: M201 Slash Pants
      // BOM: 0.33 kg کتان بنگال (approx 1.15m @ 675,000/kg = 222,750) + 1.1m کش ۴ سانت (15,950) + 1 لیبل (4,500) + اجرت (55,000) + باربری (12,000) + سربار (15,000) = ~325,200
      val slashProduct = ProductEntity(
        code = "M201",
        name = "شلوار اسلش کژوال M201",
        categoryId = 2L,
        categoryName = "شلوار اسلش کتان و اسپرت",
        description = "اسلش راحت کتان بنگال کشی دو جیب بغل زیپ‌دار با دمپا گت",
        isActive = true,
        suggestedSellingPrice = 620000L,
        manualOverridePrice = null,
        isManualPrice = false,
        currentCostPrice = 325000L,
        sewingWage = 55000L,
        allocatedFreightCost = 12000L,
        overheadCost = 15000L,
        targetProfitPercent = 90.0,
        profitCalculationType = "MARKUP",
        lastPriceUpdateTimestamp = System.currentTimeMillis(),
        lastPriceUpdateDate = "۱۵ اسفند"
      )
      val slashId = db.productDao().insert(slashProduct)

      val slashBOMs = listOf(
        ProductBOMEntity(
          productId = slashId,
          productCode = "M201",
          materialId = 2L,
          materialName = "پارچه کتان بنگالین کشی",
          materialCategory = "FABRIC",
          unit = "کیلوگرم",
          standardQuantity = 0.33,
          unitRate = 675000L,
          note = "مصرف ۱.۱۵ متر کتان بر اساس گرماژ ۲۹۰ گرم"
        ),
        ProductBOMEntity(
          productId = slashId,
          productCode = "M201",
          materialId = 3L,
          materialName = "کش پهن ۴ سانت بافت ترک",
          materialCategory = "ACCESSORY",
          unit = "متر",
          standardQuantity = 1.1,
          unitRate = 14500L,
          note = "کمر کش مقاوم با دوخت ۴ سوزنه"
        ),
        ProductBOMEntity(
          productId = slashId,
          productCode = "M201",
          materialId = 5L,
          materialName = "مارک و لیبل ساتن بافته پشت یقه",
          materialCategory = "ACCESSORY",
          unit = "عدد",
          standardQuantity = 1.0,
          unitRate = 4500L,
          note = "مارک پرچمی کمر شلوار"
        )
      )
      db.productBOMDao().insertAll(slashBOMs)

      // 20. Product Variants with OnHand & Reserved Quantities
      val hoodieVariants = listOf(
        ProductVariantEntity(
          productId = hoodieId,
          productCode = "HD-204",
          productName = "هودی کلاه‌دار اورسایز M204",
          colorId = 1L,
          colorName = "مشکی زغالی",
          sizeId = 2L,
          sizeName = "L",
          onHandQuantity = 120,
          reservedQuantity = 40,
          currentUnitCost = 438000L,
          currentUnitSalePrice = 850000L
        ),
        ProductVariantEntity(
          productId = hoodieId,
          productCode = "HD-204",
          productName = "هودی کلاه‌دار اورسایز M204",
          colorId = 1L,
          colorName = "مشکی زغالی",
          sizeId = 3L,
          sizeName = "XL",
          onHandQuantity = 150,
          reservedQuantity = 50,
          currentUnitCost = 438000L,
          currentUnitSalePrice = 850000L
        )
      )
      db.productVariantDao().insertAll(hoodieVariants)

      // 21. Price Change Reasons
      val reasons = listOf(
        PriceChangeReasonEntity(title = "تغییر قیمت بازار بدون فاکتور خرید", isDefault = true),
        PriceChangeReasonEntity(title = "افزایش قیمت خرید فاکتور تامین‌کننده", isDefault = false),
        PriceChangeReasonEntity(title = "افزایش دستمزد و اجرت دوخت خیاطی", isDefault = false),
        PriceChangeReasonEntity(title = "تغییر نرخ کرایه باربری و حمل", isDefault = false),
        PriceChangeReasonEntity(title = "اصلاح فرمول مصرف BOM", isDefault = false)
      )
      db.priceChangeReasonDao().insertAll(reasons)

      // 22. Sales Channels
      val channels = listOf(
        SalesChannelEntity(name = "فروش حضوری / بنکداری", code = "WHOLESALE"),
        SalesChannelEntity(name = "پیج اینستاگرام کارگاه", code = "INSTAGRAM"),
        SalesChannelEntity(name = "کانال روبیکا", code = "RUBIKA"),
        SalesChannelEntity(name = "کانال تلگرام و واتساپ", code = "TELEGRAM")
      )
      db.salesChannelDao().insertAll(channels)

      // 23. Fabric Categories (دسته‌بندی‌های مستقل پارچه)
      val fabricCategories = listOf(
        FabricCategoryEntity(code = "FC-01", name = "دورس و زمستانه", description = "دورس ۳ نخ، ۲ نخ خارخورده و بدون خار", isActive = true),
        FabricCategoryEntity(code = "FC-02", name = "کتان و شلواری", description = "کتان بنگالین، لی کاغذی و پنبه کتان", isActive = true),
        FabricCategoryEntity(code = "FC-03", name = "پنبه و تریکو", description = "سوپر پنبه یکرو، ملانژ و بیسیک", isActive = true),
        FabricCategoryEntity(code = "FC-04", name = "آستری و متفرقه", description = "آستر تافته، جیب و پشم شیشه", isActive = true)
      )
      db.fabricCategoryDao().insertAll(fabricCategories)

      // 24. Shipping Companies (شرکت‌های باربری و حمل)
      val shippingCompanies = listOf(
        ShippingCompanyEntity(name = "باربری میهن تورج", phone = "۰۲۱۵۵۳۳۲۲۱۱", address = "تهران، پایانه باربری جنوب، غرفه ۲۴", notes = "باربری تخصصی از کاشان و اصفهان"),
        ShippingCompanyEntity(name = "پیک و باربری پیشتاز بازار", phone = "۰۲۱۵۵۶۶۴۴۲۲", address = "تهران، بازار بزرگ، پاچنار", notes = "حمل خرده بار ملزومات و زیپ"),
        ShippingCompanyEntity(name = "باربری سریع ترابر", phone = "۰۲۱۶۶۵۵۴۴۳۳", address = "تهران، بزرگراه فتح، خیابان ۱۷ شهریور", notes = "محموله‌های سنگین و تناژ")
      )
      db.shippingCompanyDao().insertAll(shippingCompanies)

      // 25. Base Cost Configurations (تنظیمات بهای پایه کارگاه)
      val baseCostConfigs = listOf(
        BaseCostConfigEntity(title = "اجاره سالن دوخت و کارگاه", costType = "OVERHEAD", amount = 15000L, unit = "تومان / قطعه", isPerGarment = true, isActive = true),
        BaseCostConfigEntity(title = "استهلاک چرخ خیاطی و تیغ برش", costType = "OVERHEAD", amount = 8000L, unit = "تومان / قطعه", isPerGarment = true, isActive = true),
        BaseCostConfigEntity(title = "قبوض برق و آب صنعتی", costType = "OVERHEAD", amount = 5000L, unit = "تومان / قطعه", isPerGarment = true, isActive = true),
        BaseCostConfigEntity(title = "بسته‌بندی، سلفون و کارتن", costType = "PACKAGING", amount = 7000L, unit = "تومان / قطعه", isPerGarment = true, isActive = true)
      )
      db.baseCostConfigDao().insertAll(baseCostConfigs)
    }
  }

  private class DatabaseCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          populateDatabase(database)
        }
      }
    }

    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
      super.onDestructiveMigration(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          populateDatabase(database)
        }
      }
    }
  }
}
