package com.example.data.service

import com.example.data.database.AppDatabase
import com.example.data.model.InventoryLedgerEntity
import com.example.data.model.MaterialEntity
import com.example.data.model.MaterialPriceHistoryEntity
import com.example.data.model.PriceImpactReport
import com.example.data.model.ProductEntity
import com.example.data.model.ProductPriceHistoryEntity
import com.example.data.model.ProductPriceImpactItem
import com.example.util.PersianDateHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Dynamic Cost & Repricing Engine
 * Implements real-time product BOM recalculation, Market Price Updates,
 * Free (Unreserved) Inventory repricing, and immutable price history tracking.
 *
 * Rules:
 * 1. The latest recorded price of each material is the basis of current product cost.
 * 2. Historical purchase costs never change.
 * 3. Reserved customer orders and historical sales never change.
 * 4. Only unreserved (Available) inventory is repriced and revalued.
 * 5. Full audit trail recorded in MaterialPriceHistory, ProductPriceHistory, and InventoryLedger.
 */
class DynamicPricingService(private val database: AppDatabase) {

  /**
   * Records a market price update for a raw material / trim without requiring a new purchase.
   * Immediately recalculates BOMs for all affected products, updates their suggested selling prices,
   * reprices free inventory, and writes audit history.
   */
  suspend fun recordMarketPriceUpdate(
    materialId: Long,
    newPrice: Long,
    reason: String = "تغییر قیمت بازار",
    notes: String = "",
    recordedBy: String = "مدیر کارگاه"
  ): PriceImpactReport = withContext(Dispatchers.IO) {
    val material = database.materialDao().getById(materialId)
      ?: throw IllegalArgumentException("ماده اولیه با شناسه $materialId یافت نشد.")

    val oldPrice = material.currentPrice
    val changeAmount = newPrice - oldPrice
    val changePercent = if (oldPrice > 0L) ((changeAmount.toDouble() / oldPrice.toDouble()) * 100.0) else 0.0
    val now = System.currentTimeMillis()
    val todayPersian = PersianDateHelper.getTodayPersianDate()

    // 1. Record in MaterialPriceHistory
    val historyRecord = MaterialPriceHistoryEntity(
      materialId = material.id,
      materialName = material.name,
      oldPrice = oldPrice,
      newPrice = newPrice,
      date = todayPersian,
      timestamp = now,
      changeAmount = changeAmount,
      changePercent = changePercent,
      reason = reason,
      source = "MARKET_UPDATE",
      supplierName = material.supplierName,
      notes = notes,
      recordedBy = recordedBy
    )
    database.materialPriceHistoryDao().insert(historyRecord)

    // 2. Update material currentPrice
    val updatedMaterial = material.copy(
      currentPrice = newPrice,
      lastPriceSource = "MARKET_UPDATE",
      lastPriceChangeDate = todayPersian,
      lastPriceChangeTimestamp = now
    )
    database.materialDao().update(updatedMaterial)

    // Also sync Fabric table if matching fabric exists
    val matchingFabric = database.fabricDao().getByCode(material.code)
    if (matchingFabric != null) {
      val updatedFabric = matchingFabric.copy(
        previousBuyPricePerMeter = matchingFabric.currentMarketPrice,
        currentMarketPrice = newPrice,
        previousPurchaseDate = matchingFabric.purchaseDate,
        purchaseDate = todayPersian
      )
      database.fabricDao().updateFabric(updatedFabric)
    }

    // 3. Find all BOMs using this material
    val affectedBOMs = database.productBOMDao().getBOMsUsingMaterial(materialId)
    val affectedProductIds = affectedBOMs.map { it.productId }.distinct()

    val impactItems = mutableListOf<ProductPriceImpactItem>()
    var totalFreeCount = 0
    var totalOldValue = 0L
    var totalNewValue = 0L

    for (productId in affectedProductIds) {
      val product = database.productDao().getProductById(productId) ?: continue
      val bomList = database.productBOMDao().getBOMListForProduct(productId)

      // Calculate new BOM material cost with updated rate
      var newMaterialCost = 0L
      var contributingQuantity = 0.0
      val breakdownParts = mutableListOf<String>()

      for (bom in bomList) {
        val effectiveRate = if (bom.materialId == materialId) newPrice else {
          val mat = database.materialDao().getById(bom.materialId)
          mat?.currentPrice ?: bom.unitRate
        }
        val lineCost = (bom.standardQuantity * effectiveRate).toLong()
        newMaterialCost += lineCost

        // Update BOM unit rate in db
        if (bom.materialId == materialId) {
          contributingQuantity = bom.standardQuantity
          database.productBOMDao().update(bom.copy(unitRate = newPrice))
          breakdownParts.add("${bom.materialName}: نرخ جدید $effectiveRate ت (سهم در کار: $lineCost ت)")
        }
      }

      val oldCost = product.currentCostPrice
      val oldSale = product.suggestedSellingPrice

      // Total new unit cost: BOM Materials + Sewing wage + Allocated Freight + Overhead
      val newCost = newMaterialCost + product.sewingWage + product.allocatedFreightCost + product.overheadCost

      // New suggested selling price based on target profit margin/markup
      val newSale = if (product.profitCalculationType == "MARGIN") {
        if (product.targetProfitPercent < 100.0) {
          (newCost / (1.0 - (product.targetProfitPercent / 100.0))).toLong()
        } else {
          (newCost * 1.35).toLong()
        }
      } else {
        (newCost * (1.0 + (product.targetProfitPercent / 100.0))).toLong()
      }

      val costDiff = newCost - oldCost
      val salePriceDiff = newSale - oldSale

      // Update product record
      val updatedProduct = product.copy(
        currentCostPrice = newCost,
        suggestedSellingPrice = newSale,
        lastPriceUpdateTimestamp = now,
        lastPriceUpdateDate = todayPersian
      )
      database.productDao().update(updatedProduct)

      // Record in ProductPriceHistory
      val breakdownStr = "${material.name}: ${if (changeAmount >= 0) "+" else ""}$changeAmount تومان | " + breakdownParts.joinToString("، ")
      val prodHistory = ProductPriceHistoryEntity(
        productId = product.id,
        productCode = product.code,
        productName = product.name,
        oldCostPrice = oldCost,
        newCostPrice = newCost,
        oldSalePrice = oldSale,
        newSalePrice = newSale,
        date = todayPersian,
        timestamp = now,
        reason = reason,
        triggeringMaterialId = material.id,
        triggeringMaterialName = material.name,
        costChangeAmount = costDiff,
        costChangePercent = if (oldCost > 0L) ((costDiff.toDouble() / oldCost.toDouble()) * 100.0) else 0.0,
        notes = notes,
        breakdownJson = breakdownStr
      )
      database.productPriceHistoryDao().insert(prodHistory)

      // Update product variants (Free unreserved inventory only!)
      val variants = database.productVariantDao().getVariantsListForProduct(productId)
      var productFreeStock = 0
      for (variant in variants) {
        val updatedVariant = variant.copy(
          currentUnitCost = newCost,
          currentUnitSalePrice = newSale
        )
        database.productVariantDao().update(updatedVariant)
        productFreeStock += variant.availableQuantity
      }

      // Also sync InventoryEntity for finished goods of this product
      val invItem = database.inventoryDao().getByCode(product.code)
      if (invItem != null) {
        val freeQty = invItem.availableForSale
        if (productFreeStock == 0) {
          productFreeStock = freeQty
        }
        val updatedInv = invItem.copy(
          previousCostPrice = invItem.unitCostPrice,
          previousCostDate = invItem.lastUpdated,
          unitCostPrice = newCost,
          unitSalePrice = if (!product.isManualPrice || product.manualOverridePrice == null) newSale else invItem.unitSalePrice,
          lastUpdated = todayPersian
        )
        database.inventoryDao().updateItem(updatedInv)
      }

      val invValueDiff = productFreeStock * salePriceDiff
      totalFreeCount += productFreeStock
      totalOldValue += productFreeStock * oldSale
      totalNewValue += productFreeStock * newSale

      impactItems.add(
        ProductPriceImpactItem(
          productId = product.id,
          productCode = product.code,
          productName = product.name,
          oldCost = oldCost,
          newCost = newCost,
          costDiff = costDiff,
          oldSalePrice = oldSale,
          newSalePrice = newSale,
          salePriceDiff = salePriceDiff,
          freeStockQuantity = productFreeStock,
          inventoryValueDiff = invValueDiff,
          contributingMaterialQuantity = contributingQuantity
        )
      )
    }

    PriceImpactReport(
      materialId = material.id,
      materialName = material.name,
      oldPrice = oldPrice,
      newPrice = newPrice,
      priceChangePercent = changePercent,
      affectedProducts = impactItems,
      totalFreeInventoryCount = totalFreeCount,
      totalFreeInventoryOldValue = totalOldValue,
      totalFreeInventoryNewValue = totalNewValue,
      valueDifference = totalNewValue - totalOldValue
    )
  }

  /**
   * Recalculates BOM and Cost for a single product.
   */
  suspend fun recalculateProduct(productId: Long): ProductEntity? = withContext(Dispatchers.IO) {
    val product = database.productDao().getProductById(productId) ?: return@withContext null
    val bomList = database.productBOMDao().getBOMListForProduct(productId)

    var totalMaterialCost = 0L
    for (bom in bomList) {
      val mat = database.materialDao().getById(bom.materialId)
      val currentRate = mat?.currentPrice ?: bom.unitRate
      totalMaterialCost += (bom.standardQuantity * currentRate).toLong()
    }

    val totalUnitCost = totalMaterialCost + product.sewingWage + product.allocatedFreightCost + product.overheadCost
    val suggestedSale = (totalUnitCost * (1.0 + (product.targetProfitPercent / 100.0))).toLong()

    val updated = product.copy(
      currentCostPrice = totalUnitCost,
      suggestedSellingPrice = suggestedSale,
      lastPriceUpdateTimestamp = System.currentTimeMillis(),
      lastPriceUpdateDate = PersianDateHelper.getTodayPersianDate()
    )
    database.productDao().update(updated)

    // Update variants
    val variants = database.productVariantDao().getVariantsListForProduct(productId)
    for (v in variants) {
      database.productVariantDao().update(
        v.copy(
          currentUnitCost = totalUnitCost,
          currentUnitSalePrice = suggestedSale
        )
      )
    }
    updated
  }
}
