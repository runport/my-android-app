package com.example

import com.example.data.model.FixedCostEntity
import com.example.data.model.FixedCostScope
import com.example.data.service.FinancialCalculationService
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests verifying financial calculations and business constraints
 * Specifically testing Requirement 14:
 * - 100m fabric roll with multi-product allocation
 * - Strict capacity check (<= 100m valid, > 100m invalid)
 * - Cost aggregation (Fabric, Tailor, Consumables, Fixed costs) and Profit
 * - KG/Meter dynamic pricing calculation
 */
class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `test 100m roll capacity validation - 60m and 40m succeeds`() {
    val totalAvailable = 100.0
    val requestedMeters = 60.0 + 40.0

    val isValid = FinancialCalculationService.validateRollCapacity(
      rollRemainingMeters = totalAvailable,
      totalRequestedMeters = requestedMeters
    )

    assertTrue("100m allocated from 100m roll must be valid", isValid)
    val remaining = FinancialCalculationService.calculateRemainingRollMeters(totalAvailable, requestedMeters)
    assertEquals(0.0, remaining, 0.001)
  }

  @Test
  fun `test 100m roll capacity validation - 60m and 41m exceeds capacity with error`() {
    val totalAvailable = 100.0
    val requestedMeters = 60.0 + 41.0 // 101.0m

    val isValid = FinancialCalculationService.validateRollCapacity(
      rollRemainingMeters = totalAvailable,
      totalRequestedMeters = requestedMeters
    )

    assertFalse("101m allocated from 100m roll must be rejected as invalid", isValid)
    val remaining = FinancialCalculationService.calculateRemainingRollMeters(totalAvailable, requestedMeters)
    assertEquals(0.0, remaining, 0.001)
  }

  @Test
  fun `test cost breakdown and profit calculation for multi-product production`() {
    // 50 Hoodies
    val quantity = 50
    val fabricCost = 60L * 150_000L // 9,000,000 Toman (60 meters of fabric)
    val shippingCost = 300_000L // 300,000 Toman allocated shipping
    val accessoriesCost = (50 * 25_000L) + (50 * 5_000L) // 1,500,000 Toman (Zippers + Labels)
    val tailorCost = 50 * 45_000L // 2,250,000 Toman (هزینه خیاط‌کار)

    val fixedCosts = listOf(
      FixedCostEntity(
        id = 1L,
        title = "اجاره کارگاه تولیدی",
        amount = 15_000_000L,
        scope = FixedCostScope.ALL_PRODUCTS.name,
        date = "1403/12/20"
      )
    )

    // Allocate fixed cost for 50 units (estimating 1000 units monthly -> 15,000 per unit -> 750,000 for 50 units)
    val fixedCostPerUnit = FinancialCalculationService.allocateFixedCostsForProduct(
      fixedCosts = fixedCosts,
      productCode = "HD-204",
      category = "هودی و سویشرت",
      estimatedMonthlyUnits = 1000
    )
    val allocatedFixedCostTotal = fixedCostPerUnit * quantity

    assertEquals(15_000L, fixedCostPerUnit)
    assertEquals(750_000L, allocatedFixedCostTotal)

    // Total Cost = 9,000,000 + 300,000 + 1,500,000 + 2,250,000 + 750,000 = 13,800,000
    val totalCost = fabricCost + shippingCost + accessoriesCost + tailorCost + allocatedFixedCostTotal
    val unitCost = totalCost / quantity

    assertEquals(13_800_000L, totalCost)
    assertEquals(276_000L, unitCost) // 276,000 Toman per hoodie

    // Selling at 420,000 Toman each
    val unitSalePrice = 420_000L
    val grossProfitPerUnit = FinancialCalculationService.calculateGrossProfit(unitSalePrice, unitCost)
    val grossProfitTotal = FinancialCalculationService.calculateGrossProfitTotal(unitSalePrice, unitCost, quantity)

    assertEquals(144_000L, grossProfitPerUnit) // 420,000 - 276,000 = 144,000
    assertEquals(7_200_000L, grossProfitTotal) // 144,000 * 50 = 7,200,000

    val netProfit = FinancialCalculationService.calculateNetProfit(
      grossRevenue = unitSalePrice * quantity,
      discountAmount = 0L,
      totalProductCost = totalCost
    )
    assertEquals(7_200_000L, netProfit)
  }

  @Test
  fun `test kg to meter pricing calculation`() {
    // 450,000 Toman per kg, with 3 meters per kg => 150,000 Toman per meter
    val pricePerKg = 450_000L
    val metersPerKg = 3.0
    val calculatedPricePerMeter = FinancialCalculationService.calculatePricePerMeter(pricePerKg, metersPerKg)

    assertEquals(150_000L, calculatedPricePerMeter)

    // Reverse: 150,000 per meter * 3 meters/kg = 450,000 per kg
    val calculatedPricePerKg = FinancialCalculationService.calculatePricePerKg(calculatedPricePerMeter, metersPerKg)
    assertEquals(450_000L, calculatedPricePerKg)
  }
}


