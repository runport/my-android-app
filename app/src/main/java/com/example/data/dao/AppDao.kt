package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import com.example.data.model.FabricPriceHistoryEntity
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
import kotlinx.coroutines.flow.Flow

@Dao
interface FabricDao {
  @Query("SELECT * FROM fabrics ORDER BY id DESC")
  fun getAllFabrics(): Flow<List<FabricEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFabric(fabric: FabricEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(fabrics: List<FabricEntity>)

  @Update
  suspend fun updateFabric(fabric: FabricEntity)

  @Delete
  suspend fun deleteFabric(fabric: FabricEntity)

  @Query("DELETE FROM fabrics WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT * FROM fabrics WHERE code = :code LIMIT 1")
  suspend fun getByCode(code: String): FabricEntity?

  @Query("SELECT * FROM fabrics WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): FabricEntity?
}

@Dao
interface CuttingDao {
  @Query("SELECT * FROM cutting_orders ORDER BY id DESC")
  fun getAllCuttings(): Flow<List<CuttingEntity>>

  @Query("SELECT * FROM cutting_orders WHERE id = :id LIMIT 1")
  suspend fun getCuttingById(id: Long): CuttingEntity?

  @Query("SELECT * FROM cutting_orders WHERE rollId = :rollId ORDER BY partNumber ASC")
  fun getCuttingsForRoll(rollId: Long): Flow<List<CuttingEntity>>

  @Query("SELECT * FROM cutting_orders WHERE rollId = :rollId ORDER BY partNumber ASC")
  suspend fun getCuttingsForRollOnce(rollId: Long): List<CuttingEntity>

  @Query("SELECT * FROM cutting_orders WHERE status != 'کار آماده' AND status != 'تحویل شده' ORDER BY id DESC")
  fun getInProgressCuttings(): Flow<List<CuttingEntity>>

  @Query("SELECT * FROM cutting_orders WHERE status = 'کار آماده' OR status = 'تحویل شده' ORDER BY id DESC")
  fun getCompletedCuttings(): Flow<List<CuttingEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCutting(cutting: CuttingEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(cuttings: List<CuttingEntity>)

  @Update
  suspend fun updateCutting(cutting: CuttingEntity)

  @Delete
  suspend fun deleteCutting(cutting: CuttingEntity)

  @Query("DELETE FROM cutting_orders WHERE id = :id")
  suspend fun deleteById(id: Long)
  // ===== Cutting Parts Workflow Queries =====

  @Query("SELECT * FROM cutting_orders WHERE rollId = :rollId ORDER BY partNumber ASC")
  fun getCuttingPartsByRoll(rollId: Long): Flow<List<CuttingEntity>>

  @Query("SELECT * FROM cutting_orders WHERE status = :status ORDER BY timestamp DESC")
  fun getCuttingPartsByStatus(status: String): Flow<List<CuttingEntity>>

  @Query("SELECT COALESCE(MAX(partNumber), 0) FROM cutting_orders WHERE rollId = :rollId")
  suspend fun getMaxPartNumber(rollId: Long): Int

  @Query("SELECT * FROM cutting_orders WHERE status IN ('برش خورده', 'در حال دوخت', 'کار آماده') ORDER BY timestamp DESC")
  fun getActiveParts(): Flow<List<CuttingEntity>>
}

@Dao
interface ProductionDao {
  @Query("SELECT * FROM production_records ORDER BY id DESC")
  fun getAllProductions(): Flow<List<ProductionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduction(production: ProductionEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(productions: List<ProductionEntity>)

  @Update
  suspend fun updateProduction(production: ProductionEntity)

  @Delete
  suspend fun deleteProduction(production: ProductionEntity)
}

@Dao
interface InventoryDao {
  @Query("SELECT * FROM inventory_items ORDER BY id DESC")
  fun getAllInventory(): Flow<List<InventoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertItem(item: InventoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<InventoryEntity>)

  @Update
  suspend fun updateItem(item: InventoryEntity)

  @Delete
  suspend fun deleteItem(item: InventoryEntity)

  @Query("DELETE FROM inventory_items WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT * FROM inventory_items WHERE code = :code LIMIT 1")
  suspend fun getByCode(code: String): InventoryEntity?

  @Query("SELECT * FROM inventory_items WHERE code = :code LIMIT 1")
  suspend fun getItemByCode(code: String): InventoryEntity?

  @Query("SELECT * FROM inventory_items WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): InventoryEntity?
}

@Dao
interface SaleOrderDao {
  @Query("SELECT * FROM sales_orders ORDER BY id DESC")
  fun getAllSalesOrders(): Flow<List<SaleOrderEntity>>

  @Query("SELECT * FROM sales_orders WHERE id = :id LIMIT 1")
  suspend fun getOrderById(id: Long): SaleOrderEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: SaleOrderEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(orders: List<SaleOrderEntity>)

  @Update
  suspend fun updateOrder(order: SaleOrderEntity)

  @Delete
  suspend fun deleteOrder(order: SaleOrderEntity)
}

@Dao
interface CustomerDao {
  @Query("SELECT * FROM customers ORDER BY totalPurchases DESC")
  fun getAllCustomers(): Flow<List<CustomerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomer(customer: CustomerEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(customers: List<CustomerEntity>)

  @Update
  suspend fun updateCustomer(customer: CustomerEntity)

  @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
  suspend fun getCustomerById(id: Long): CustomerEntity?

  @Query("SELECT * FROM customers WHERE name = :name LIMIT 1")
  suspend fun getCustomerByName(name: String): CustomerEntity?

  @Delete
  suspend fun deleteCustomer(customer: CustomerEntity)

  @Query("DELETE FROM customers WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface SupplierDao {
  @Query("SELECT * FROM suppliers ORDER BY totalPurchases DESC")
  fun getAllSuppliers(): Flow<List<SupplierEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSupplier(supplier: SupplierEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(suppliers: List<SupplierEntity>)

  @Update
  suspend fun updateSupplier(supplier: SupplierEntity)

  @Delete
  suspend fun deleteSupplier(supplier: SupplierEntity)

  @Query("DELETE FROM suppliers WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT * FROM suppliers WHERE id = :id LIMIT 1")
  suspend fun getSupplierById(id: Long): SupplierEntity?

  @Query("SELECT * FROM suppliers WHERE name = :name LIMIT 1")
  suspend fun getSupplierByName(name: String): SupplierEntity?
}

@Dao
interface ModelStandardDao {
  @Query("SELECT * FROM model_standards ORDER BY id ASC")
  fun getAllStandards(): Flow<List<ModelStandardEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStandard(standard: ModelStandardEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(standards: List<ModelStandardEntity>)

  @Update
  suspend fun updateStandard(standard: ModelStandardEntity)
}

@Dao
interface FactorySettingsDao {
  @Query("SELECT * FROM factory_settings WHERE id = 1 LIMIT 1")
  fun getSettings(): Flow<FactorySettingsEntity?>

  @Query("SELECT * FROM factory_settings WHERE id = 1 LIMIT 1")
  suspend fun getSettingsOnce(): FactorySettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(settings: FactorySettingsEntity)

  @Query("DELETE FROM factory_settings")
  suspend fun deleteAll()
}

@Dao
interface FabricRollDao {
  @Query("SELECT * FROM fabric_rolls ORDER BY id DESC")
  fun getAllRolls(): Flow<List<FabricRollEntity>>

  @Query("SELECT * FROM fabric_rolls WHERE remainingMeters > 0.5 ORDER BY id DESC")
  fun getAvailableRolls(): Flow<List<FabricRollEntity>>

  @Query("SELECT * FROM fabric_rolls WHERE id = :id LIMIT 1")
  suspend fun getRollById(id: Long): FabricRollEntity?

  @Query("SELECT * FROM fabric_rolls WHERE rollCode = :rollCode LIMIT 1")
  suspend fun getRollByCode(rollCode: String): FabricRollEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRoll(roll: FabricRollEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(rolls: List<FabricRollEntity>)

  @Update
  suspend fun updateRoll(roll: FabricRollEntity)

  @Query("SELECT * FROM fabric_rolls ORDER BY id DESC")
  suspend fun getAllRollsOnce(): List<FabricRollEntity>

  @Query("SELECT * FROM fabric_rolls WHERE shippingExpenseId = :expenseId")
  suspend fun getRollsForShippingExpense(expenseId: Long): List<FabricRollEntity>

  @Query("DELETE FROM fabric_rolls WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface RollUsageDao {
  @Query("SELECT * FROM roll_usages ORDER BY id DESC")
  fun getAllUsages(): Flow<List<RollUsageEntity>>

  @Query("SELECT * FROM roll_usages WHERE rollId = :rollId ORDER BY id DESC")
  fun getUsagesForRoll(rollId: Long): Flow<List<RollUsageEntity>>

  @Query("SELECT * FROM roll_usages WHERE productionId = :productionId ORDER BY id DESC")
  fun getUsagesForProduction(productionId: Long): Flow<List<RollUsageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUsage(usage: RollUsageEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(usages: List<RollUsageEntity>)

  @Update
  suspend fun updateRollUsage(usage: RollUsageEntity)

  @Delete
  suspend fun deleteRollUsage(usage: RollUsageEntity)

  @Query("SELECT * FROM roll_usages WHERE id = :id")
  suspend fun getUsageById(id: Long): RollUsageEntity?
}

@Dao
interface AccessoryPurchaseDao {
  @Query("SELECT * FROM accessory_purchases ORDER BY id DESC")
  fun getAllPurchases(): Flow<List<AccessoryPurchaseEntity>>

  @Query("SELECT * FROM accessory_purchases WHERE accessoryId = :accessoryId ORDER BY id DESC")
  fun getPurchasesForAccessory(accessoryId: Long): Flow<List<AccessoryPurchaseEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPurchase(purchase: AccessoryPurchaseEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(purchases: List<AccessoryPurchaseEntity>)
}

@Dao
interface ShippingExpenseDao {
  @Query("SELECT * FROM shipping_expenses ORDER BY id DESC")
  fun getAllExpenses(): Flow<List<ShippingExpenseEntity>>

  @Query("SELECT * FROM shipping_expenses WHERE id = :id LIMIT 1")
  suspend fun getExpenseById(id: Long): ShippingExpenseEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExpense(expense: ShippingExpenseEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(expenses: List<ShippingExpenseEntity>)

  @Update
  suspend fun updateExpense(expense: ShippingExpenseEntity)

  @Query("DELETE FROM shipping_expenses WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface OrderStatusHistoryDao {
  @Query("SELECT * FROM order_status_history ORDER BY id DESC")
  fun getAllHistory(): Flow<List<OrderStatusHistoryEntity>>

  @Query("SELECT * FROM order_status_history WHERE orderId = :orderId ORDER BY id DESC")
  fun getHistoryForOrder(orderId: Long): Flow<List<OrderStatusHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHistory(history: OrderStatusHistoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(historyList: List<OrderStatusHistoryEntity>)
}

@Dao
interface FixedCostDao {
  @Query("SELECT * FROM fixed_costs ORDER BY id DESC")
  fun getAllFixedCosts(): Flow<List<FixedCostEntity>>

  @Query("SELECT * FROM fixed_costs ORDER BY id DESC")
  suspend fun getAllFixedCostsList(): List<FixedCostEntity>

  @Query("SELECT * FROM fixed_costs WHERE scope = :scope ORDER BY id DESC")
  fun getFixedCostsByScope(scope: String): Flow<List<FixedCostEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFixedCost(cost: FixedCostEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(costs: List<FixedCostEntity>)

  @Update
  suspend fun updateFixedCost(cost: FixedCostEntity)

  @Query("DELETE FROM fixed_costs WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface ProductionConsumableDao {
  @Query("SELECT * FROM production_consumables ORDER BY id DESC")
  fun getAllConsumables(): Flow<List<ProductionConsumableEntity>>

  @Query("SELECT * FROM production_consumables WHERE productionId = :productionId ORDER BY id DESC")
  fun getConsumablesForProduction(productionId: Long): Flow<List<ProductionConsumableEntity>>

  @Query("SELECT * FROM production_consumables WHERE cuttingId = :cuttingId ORDER BY id ASC")
  fun getConsumablesForCutting(cuttingId: Long): Flow<List<ProductionConsumableEntity>>

  @Query("SELECT * FROM production_consumables WHERE cuttingId = :cuttingId ORDER BY id ASC")
  suspend fun getConsumablesForCuttingList(cuttingId: Long): List<ProductionConsumableEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertConsumable(item: ProductionConsumableEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<ProductionConsumableEntity>)
}

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categories ORDER BY name ASC")
  fun getAllCategories(): Flow<List<CategoryEntity>>

  @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY name ASC")
  fun getActiveCategories(): Flow<List<CategoryEntity>>

  @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): CategoryEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(category: CategoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(categories: List<CategoryEntity>)

  @Update
  suspend fun update(category: CategoryEntity)

  @Delete
  suspend fun delete(category: CategoryEntity)

  @Query("DELETE FROM categories WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface ProductDao {
  @Query("SELECT * FROM products ORDER BY id DESC")
  fun getAllProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name ASC")
  fun getActiveProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE categoryId = :catId ORDER BY name ASC")
  fun getProductsByCategory(catId: Long): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
  suspend fun getProductById(id: Long): ProductEntity?

  @Query("SELECT * FROM products WHERE code = :code LIMIT 1")
  suspend fun getProductByCode(code: String): ProductEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(product: ProductEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(products: List<ProductEntity>)

  @Update
  suspend fun update(product: ProductEntity)

  @Delete
  suspend fun delete(product: ProductEntity)

  @Query("DELETE FROM products WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface ColorDao {
  @Query("SELECT * FROM colors ORDER BY name ASC")
  fun getAllColors(): Flow<List<ColorEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(color: ColorEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(colors: List<ColorEntity>)

  @Update
  suspend fun update(color: ColorEntity)

  @Delete
  suspend fun delete(color: ColorEntity)

  @Query("DELETE FROM colors WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface SizeDao {
  @Query("SELECT * FROM sizes ORDER BY sortOrder ASC")
  fun getAllSizes(): Flow<List<SizeEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(size: SizeEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(sizes: List<SizeEntity>)

  @Update
  suspend fun update(size: SizeEntity)

  @Delete
  suspend fun delete(size: SizeEntity)

  @Query("DELETE FROM sizes WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface ProductVariantDao {
  @Query("SELECT * FROM product_variants ORDER BY productId ASC, sizeId ASC")
  fun getAllVariants(): Flow<List<ProductVariantEntity>>

  @Query("SELECT * FROM product_variants WHERE productId = :productId")
  fun getVariantsForProduct(productId: Long): Flow<List<ProductVariantEntity>>

  @Query("SELECT * FROM product_variants WHERE productId = :productId")
  suspend fun getVariantsListForProduct(productId: Long): List<ProductVariantEntity>

  @Query("SELECT * FROM product_variants WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): ProductVariantEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(variant: ProductVariantEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(variants: List<ProductVariantEntity>)

  @Update
  suspend fun update(variant: ProductVariantEntity)

  @Query("DELETE FROM product_variants WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface MaterialDao {
  @Query("SELECT * FROM materials ORDER BY name ASC")
  fun getAllMaterials(): Flow<List<MaterialEntity>>

  @Query("SELECT * FROM materials WHERE category = :category ORDER BY name ASC")
  fun getMaterialsByCategory(category: String): Flow<List<MaterialEntity>>

  @Query("SELECT * FROM materials WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): MaterialEntity?

  @Query("SELECT * FROM materials WHERE code = :code LIMIT 1")
  suspend fun getByCode(code: String): MaterialEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(material: MaterialEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(materials: List<MaterialEntity>)

  @Update
  suspend fun update(material: MaterialEntity)

  @Delete
  suspend fun delete(material: MaterialEntity)

  @Query("DELETE FROM materials WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface MaterialUnitDao {
  @Query("SELECT * FROM material_units ORDER BY name ASC")
  fun getAllUnits(): Flow<List<MaterialUnitEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(unit: MaterialUnitEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(units: List<MaterialUnitEntity>)

  @Update
  suspend fun update(unit: MaterialUnitEntity)

  @Query("DELETE FROM material_units WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface ProductBOMDao {
  @Query("SELECT * FROM product_boms ORDER BY id ASC")
  fun getAllBOMs(): Flow<List<ProductBOMEntity>>

  @Query("SELECT * FROM product_boms WHERE productId = :productId")
  fun getBOMForProduct(productId: Long): Flow<List<ProductBOMEntity>>

  @Query("SELECT * FROM product_boms WHERE productId = :productId")
  suspend fun getBOMListForProduct(productId: Long): List<ProductBOMEntity>

  @Query("SELECT * FROM product_boms WHERE materialId = :materialId")
  suspend fun getBOMsUsingMaterial(materialId: Long): List<ProductBOMEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(bom: ProductBOMEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(boms: List<ProductBOMEntity>)

  @Update
  suspend fun update(bom: ProductBOMEntity)

  @Query("DELETE FROM product_boms WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("DELETE FROM product_boms WHERE productId = :productId")
  suspend fun deleteByProductId(productId: Long)
}

@Dao
interface MaterialPriceHistoryDao {
  @Query("SELECT * FROM material_price_history ORDER BY id DESC")
  fun getAllHistory(): Flow<List<MaterialPriceHistoryEntity>>

  @Query("SELECT * FROM material_price_history WHERE materialId = :materialId ORDER BY id DESC")
  fun getHistoryForMaterial(materialId: Long): Flow<List<MaterialPriceHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(history: MaterialPriceHistoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<MaterialPriceHistoryEntity>)
}

@Dao
interface FabricPriceHistoryDao {
  @Query("SELECT * FROM fabric_price_history ORDER BY id DESC")
  fun getAllHistory(): Flow<List<FabricPriceHistoryEntity>>

  @Query("SELECT * FROM fabric_price_history WHERE fabricCategoryId = :categoryId ORDER BY id DESC")
  fun getHistoryForCategory(categoryId: Long): Flow<List<FabricPriceHistoryEntity>>

  @Query("SELECT * FROM fabric_price_history WHERE triggeringRollId = :rollId ORDER BY id DESC")
  fun getHistoryForRoll(rollId: Long): Flow<List<FabricPriceHistoryEntity>>

  @Query("SELECT * FROM fabric_price_history ORDER BY id DESC LIMIT :limit")
  fun getRecentHistory(limit: Int): Flow<List<FabricPriceHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(history: FabricPriceHistoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<FabricPriceHistoryEntity>)
}

@Dao
interface ProductPriceHistoryDao {
  @Query("SELECT * FROM product_price_history ORDER BY id DESC")
  fun getAllHistory(): Flow<List<ProductPriceHistoryEntity>>

  @Query("SELECT * FROM product_price_history WHERE productId = :productId ORDER BY id DESC")
  fun getHistoryForProduct(productId: Long): Flow<List<ProductPriceHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(history: ProductPriceHistoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<ProductPriceHistoryEntity>)
}

@Dao
interface PriceChangeReasonDao {
  @Query("SELECT * FROM price_change_reasons ORDER BY id ASC")
  fun getAllReasons(): Flow<List<PriceChangeReasonEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(reason: PriceChangeReasonEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(reasons: List<PriceChangeReasonEntity>)

  @Update
  suspend fun update(reason: PriceChangeReasonEntity)

  @Query("DELETE FROM price_change_reasons WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface InventoryLedgerDao {
  @Query("SELECT * FROM inventory_ledger ORDER BY id DESC")
  fun getAllTransactions(): Flow<List<InventoryLedgerEntity>>

  @Query("SELECT * FROM inventory_ledger WHERE itemType = :itemType AND itemId = :itemId ORDER BY id DESC")
  fun getTransactionsForItem(itemType: String, itemId: Long): Flow<List<InventoryLedgerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(entry: InventoryLedgerEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(entries: List<InventoryLedgerEntity>)
}

@Dao
interface PurchaseOrderDao {
  @Query("SELECT * FROM purchase_orders ORDER BY id DESC")
  fun getAllOrders(): Flow<List<PurchaseOrderEntity>>

  @Query("SELECT * FROM purchase_orders WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): PurchaseOrderEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(order: PurchaseOrderEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(orders: List<PurchaseOrderEntity>)

  @Update
  suspend fun update(order: PurchaseOrderEntity)

  @Query("DELETE FROM purchase_orders WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface PurchaseItemDao {
  @Query("SELECT * FROM purchase_items WHERE purchaseOrderId = :orderId")
  fun getItemsForOrder(orderId: Long): Flow<List<PurchaseItemEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(item: PurchaseItemEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<PurchaseItemEntity>)
}

@Dao
interface SalesChannelDao {
  @Query("SELECT * FROM sales_channels ORDER BY id ASC")
  fun getAllChannels(): Flow<List<SalesChannelEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(channel: SalesChannelEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(channels: List<SalesChannelEntity>)

  @Update
  suspend fun update(channel: SalesChannelEntity)

  @Query("DELETE FROM sales_channels WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface CustomerPaymentDao {
  @Query("SELECT * FROM customer_payments ORDER BY id DESC")
  fun getAllPayments(): Flow<List<CustomerPaymentEntity>>

  @Query("SELECT * FROM customer_payments WHERE customerId = :customerId ORDER BY id DESC")
  fun getPaymentsForCustomer(customerId: Long): Flow<List<CustomerPaymentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(payment: CustomerPaymentEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(payments: List<CustomerPaymentEntity>)
}

@Dao
interface SupplierPaymentDao {
  @Query("SELECT * FROM supplier_payments ORDER BY id DESC")
  fun getAllPayments(): Flow<List<SupplierPaymentEntity>>

  @Query("SELECT * FROM supplier_payments WHERE supplierId = :supplierId ORDER BY id DESC")
  fun getPaymentsForSupplier(supplierId: Long): Flow<List<SupplierPaymentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(payment: SupplierPaymentEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(payments: List<SupplierPaymentEntity>)
}

@Dao
interface ShippingRateHistoryDao {
  @Query("SELECT * FROM shipping_rate_history ORDER BY id DESC")
  fun getAllHistory(): Flow<List<ShippingRateHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(history: ShippingRateHistoryEntity): Long
}

@Dao
interface AuditLogDao {
  @Query("SELECT * FROM audit_logs ORDER BY id DESC")
  fun getAllLogs(): Flow<List<AuditLogEntity>>

  @Query("SELECT * FROM audit_logs WHERE entityName = :entityName AND entityId = :entityId ORDER BY id DESC")
  fun getLogsForEntity(entityName: String, entityId: Long): Flow<List<AuditLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(log: AuditLogEntity): Long
}

@Dao
interface InventoryAdjustmentDao {
  @Query("SELECT * FROM inventory_adjustments ORDER BY id DESC")
  fun getAllAdjustments(): Flow<List<InventoryAdjustmentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(adjustment: InventoryAdjustmentEntity): Long
}

@Dao
interface FabricCategoryDao {
  @Query("SELECT * FROM fabric_categories ORDER BY name ASC")
  fun getAllCategories(): Flow<List<FabricCategoryEntity>>

  @Query("SELECT * FROM fabric_categories WHERE isActive = 1 ORDER BY name ASC")
  fun getActiveCategories(): Flow<List<FabricCategoryEntity>>

  @Query("SELECT * FROM fabric_categories WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): FabricCategoryEntity?

  @Query("SELECT * FROM fabric_categories WHERE name = :name LIMIT 1")
  suspend fun getByName(name: String): FabricCategoryEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(category: FabricCategoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(categories: List<FabricCategoryEntity>)

  @Update
  suspend fun update(category: FabricCategoryEntity)

  @Delete
  suspend fun delete(category: FabricCategoryEntity)

  @Query("DELETE FROM fabric_categories WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface ShippingCompanyDao {
  @Query("SELECT * FROM shipping_companies ORDER BY name ASC")
  fun getAllCompanies(): Flow<List<ShippingCompanyEntity>>

  @Query("SELECT * FROM shipping_companies WHERE isActive = 1 ORDER BY name ASC")
  fun getActiveCompanies(): Flow<List<ShippingCompanyEntity>>

  @Query("SELECT * FROM shipping_companies WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): ShippingCompanyEntity?

  @Query("SELECT * FROM shipping_companies WHERE name = :name LIMIT 1")
  suspend fun getByName(name: String): ShippingCompanyEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(company: ShippingCompanyEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(companies: List<ShippingCompanyEntity>)

  @Update
  suspend fun update(company: ShippingCompanyEntity)

  @Delete
  suspend fun delete(company: ShippingCompanyEntity)

  @Query("DELETE FROM shipping_companies WHERE id = :id")
  suspend fun deleteById(id: Long)
}

@Dao
interface WaybillItemDao {
  @Query("SELECT * FROM waybill_items WHERE waybillId = :waybillId ORDER BY id ASC")
  fun getItemsForWaybill(waybillId: Long): Flow<List<WaybillItemEntity>>

  @Query("SELECT * FROM waybill_items WHERE waybillId = :waybillId ORDER BY id ASC")
  suspend fun getItemsForWaybillList(waybillId: Long): List<WaybillItemEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(item: WaybillItemEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<WaybillItemEntity>)

  @Update
  suspend fun update(item: WaybillItemEntity)

  @Query("DELETE FROM waybill_items WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("DELETE FROM waybill_items WHERE waybillId = :waybillId")
  suspend fun deleteByWaybillId(waybillId: Long)

  @Query("SELECT * FROM waybill_items WHERE itemType = :itemType AND itemId = :itemId")
  suspend fun getItemsForItem(itemType: String, itemId: Long): List<WaybillItemEntity>
}

@Dao
interface BaseCostConfigDao {
  @Query("SELECT * FROM base_cost_configs ORDER BY id ASC")
  fun getAllConfigs(): Flow<List<BaseCostConfigEntity>>

  @Query("SELECT * FROM base_cost_configs WHERE isActive = 1 ORDER BY id ASC")
  fun getActiveConfigs(): Flow<List<BaseCostConfigEntity>>

  @Query("SELECT * FROM base_cost_configs WHERE isActive = 1 ORDER BY id ASC")
  suspend fun getActiveConfigsSync(): List<BaseCostConfigEntity>

  @Query("SELECT * FROM base_cost_configs ORDER BY id ASC")
  suspend fun getAllConfigsSync(): List<BaseCostConfigEntity>

  @Query("SELECT * FROM base_cost_configs WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): BaseCostConfigEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(config: BaseCostConfigEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(configs: List<BaseCostConfigEntity>)

  @Update
  suspend fun update(config: BaseCostConfigEntity)

  @Delete
  suspend fun delete(config: BaseCostConfigEntity)

  @Query("DELETE FROM base_cost_configs WHERE id = :id")
  suspend fun deleteById(id: Long)

  // ===== Price Update Queries (روزآمدسازی قیمت بدون خرید) =====

  @Query("UPDATE fabric_rolls SET currentPricePerMeter = :pricePerMeter, currentPricePerKg = :pricePerKg, lastPriceUpdateDate = :date, lastPriceUpdateTimestamp = :timestamp WHERE id = :rollId")
  suspend fun updateRollCurrentPrice(rollId: Long, pricePerMeter: Long, pricePerKg: Long, date: String, timestamp: Long)

  @Query("UPDATE materials SET currentPrice = :newPrice, currentPriceKg = :priceKg, lastPriceChangeDate = :date, lastPriceChangeTimestamp = :timestamp, priceUpdateNote = :note WHERE id = :materialId")
  suspend fun updateMaterialCurrentPrice(materialId: Long, newPrice: Long, priceKg: Long, date: String, timestamp: Long, note: String)

  @Query("SELECT * FROM fabric_rolls WHERE id = :rollId")
  suspend fun getRollByIdOnce(rollId: Long): FabricRollEntity?
}





