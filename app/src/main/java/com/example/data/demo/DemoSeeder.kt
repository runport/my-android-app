package com.example.data.demo

import androidx.room.withTransaction
import com.example.data.database.AppDatabase
import com.example.data.model.AccessoryPurchaseEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.FabricCategoryEntity
import com.example.data.model.FabricRollEntity
import com.example.data.model.FactorySettingsEntity
import com.example.data.model.InventoryEntity
import com.example.data.model.MaterialEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SaleOrderEntity
import com.example.data.model.ShippingExpenseEntity
import com.example.data.model.SupplierEntity

/**
 * Phase 16.Demo — Seeds the app with the user's real business data
 * so the full pipeline can be tested end-to-end.
 *
 * Seeds:
 *   3 suppliers, 7 fabric rolls (2nd batch), 6 materials,
 *   2 customers (Babak + Heydari), 3 products, 3 inventory rows,
 *   3 waybills, Babak's 3 sale orders, Heydari's reservation.
 *
 * Idempotency: seed() refuses if DEMO-ROL-1 already exists.
 * clear() deletes only rows whose code/name matches the demo set.
 */
object DemoSeeder {

    private const val M_PER_KG = 3.4
    private const val DEMO_FABRIC_CAT = "دورس ۳ نخ پنبه"

    // ---------- Seed ----------

    suspend fun seed(db: AppDatabase): Pair<Boolean, String> {
        if (db.fabricRollDao().getRollByCode("DEMO-ROL-1") != null) {
            return Pair(false, "داده‌های دمو قبلاً بارگذاری شده‌اند. اول آن‌ها را پاک کنید.")
        }

        var nRolls = 0
        var nMats = 0
        var nProds = 0

        db.withTransaction {

            // 1. Fabric category
            val catId = db.fabricCategoryDao().getByName(DEMO_FABRIC_CAT)?.id
                ?: db.fabricCategoryDao().insert(
                    FabricCategoryEntity(name = DEMO_FABRIC_CAT, isActive = true)
                )

            // 2. Suppliers
            listOf(
                SupplierEntity(
                    name = "یزدانی نساجی", storeName = "دفتر فروش آنیل",
                    mobile = "09120000001", address = "تهران، بازار بزرگ، پاساژ ملت",
                    distributionCategory = "پارچه", supplyType = "پارچه",
                    lastPurchaseDate = "1404/09/18"
                ),
                SupplierEntity(
                    name = "سبیل", storeName = "فروشگاه سبیل",
                    mobile = "09120000002", address = "تهران، بازار بزرگ، پامنار",
                    distributionCategory = "ملزومات", supplyType = "ملزومات",
                    lastPurchaseDate = "1404/09/18"
                ),
                SupplierEntity(
                    name = "هوشیار سوری", storeName = "",
                    mobile = "09120000003", address = "تهران، بازار بزرگ، پاساژ ملت",
                    distributionCategory = "پارچه", supplyType = "پارچه",
                    lastPurchaseDate = "1405/05/20"
                )
            ).forEach { s ->
                if (db.supplierDao().getSupplierByName(s.name) == null) {
                    db.supplierDao().insertSupplier(s)
                }
            }

            // 3. Fabric rolls — 7 total
            data class R(
                val code: String, val color: String, val kg: Double,
                val usedM: Double, val date: String, val supplier: String,
                val batch: Int  // 1 = first purchase (freight 1.45M), 2 = second
            )
            val rolls = listOf(
                R("DEMO-ROL-1", "مشکی", 15.0,   0.0, "1404/09/18", "یزدانی نساجی", 1),
                R("DEMO-ROL-2", "مشکی", 21.0,  63.0, "1404/09/18", "یزدانی نساجی", 1),
                R("DEMO-ROL-3", "مشکی", 25.0,  42.0, "1404/09/18", "یزدانی نساجی", 1),
                R("DEMO-ROL-4", "مشکی", 19.0,   0.0, "1404/09/18", "یزدانی نساجی", 1),
                R("DEMO-ROL-5", "مشکی", 19.9,   0.0, "1404/09/18", "یزدانی نساجی", 1),
                R("DEMO-ROL-6", "مشکی", 20.5,  34.0, "1405/05/20", "هوشیار سوری", 2),
                R("DEMO-ROL-7", "سفید", 17.5,  10.2, "1405/05/20", "هوشیار سوری", 2)
            )
            val batch1 = rolls.filter { it.batch == 1 }
            val batch2 = rolls.filter { it.batch == 2 }
            val batch1Kg = batch1.sumOf { it.kg }
            val batch2Kg = batch2.sumOf { it.kg }
            val freightBatch1 = 1_450_000L
            val freightBatch2 =   350_000L

            rolls.forEach { r ->
                val pool = if (r.batch == 1) batch1Kg else batch2Kg
                val poolFreight = if (r.batch == 1) freightBatch1 else freightBatch2
                val allocated = ((r.kg / pool) * poolFreight).toLong()
                val initialM = r.kg * M_PER_KG
                val remainingM = (initialM - r.usedM).coerceAtLeast(0.0)
                db.fabricRollDao().insertRoll(
                    FabricRollEntity(
                        rollCode = r.code,
                        inboundDate = r.date,
                        fabricType = "پنبه دورس ۳ نخ",
                        fabricCode = "DEMO-FC",
                        color = r.color,
                        initialMeters = initialM,
                        remainingMeters = remainingM,
                        weightKg = r.kg,
                        remainingWeightKg = remainingM / M_PER_KG,
                        buyPricePerMeter = 650_000L,
                        buyPricePerKg = 650_000L,
                        allocatedShippingCost = allocated,
                        fabricCategoryId = catId,
                        fabricCategoryName = DEMO_FABRIC_CAT,
                        status = if (remainingM <= 0.5) "پایان یافته" else "موجود",
                        supplierName = r.supplier,
                        currentPricePerMeter = 1_000_000L,
                        currentPricePerKg = 1_000_000L,
                        lastPriceUpdateDate = "امروز",
                        lastPriceUpdateTimestamp = System.currentTimeMillis()
                    )
                )
                nRolls += 1
            }

            // 4. Materials — 6 items
            data class M(
                val code: String, val name: String, val unit: String,
                val bought: Double, val remaining: Double,
                val buyPrice: Long, val currentPrice: Long, val category: String
            )
            val mats = listOf(
                M("DEMO-MAT-KESH22", "کش ۲.۲ سانت",       "کیلوگرم", 15.0,  7.0, 370_000L, 850_000L, "کش"),
                M("DEMO-MAT-NAVAR1", "نوار ۱ سانت",       "کیلوگرم", 15.0, 10.0, 650_000L, 900_000L, "نوار"),
                M("DEMO-MAT-KONAFI", "نوار کنفی ۲ سانت",  "کیلوگرم", 15.0, 15.0, 850_000L, 850_000L, "نوار"),
                M("DEMO-MAT-KESH4",  "کش ۴ سانت",         "کیلوگرم", 15.0, 12.0, 850_000L, 850_000L, "کش"),
                M("DEMO-MAT-SHIMEL", "بند شیمل",          "کیلوگرم", 14.0,  6.0, 450_000L, 900_000L, "بند"),
                M("DEMO-MAT-ZIP",    "زیپ دنده‌فلزی",     "عدد",   1000.0, 1000.0, 3_000L, 3_000L, "زیپ")
            )
            val matFreight = 800_000L
            val matTotalValue = mats.sumOf { (it.bought * it.buyPrice).toLong() }
            mats.forEach { m ->
                val share = if (matTotalValue > 0L) {
                    ((m.bought * m.buyPrice) * matFreight / matTotalValue).toLong()
                } else 0L
                db.materialDao().insert(
                    MaterialEntity(
                        code = m.code,
                        name = m.name,
                        category = m.category,
                        unit = m.unit,
                        currentPrice = m.currentPrice,
                        lastPurchasePrice = m.buyPrice,
                        lastPriceSource = "PURCHASE",
                        lastPriceChangeDate = "1404/09/18",
                        stockQuantity = m.remaining,
                        minStockThreshold = 5.0,
                        supplierName = "سبیل",
                        allocatedShippingCost = share
                    )
                )
                nMats += 1
            }

            // 5. Customers
            val babakId = db.customerDao().getCustomerByName("بابک")?.id
                ?: db.customerDao().insertCustomer(
                    CustomerEntity(
                        name = "بابک", company = "", phone = "09130000001",
                        address = "نهاوند", category = "بوتیک و آنلاین",
                        totalPurchases = 0L, orderCount = 0, currentDebt = 0L,
                        tier = "خرید اول", popularModels = "شش جیب",
                        lastOrderDate = "1405/05/20"
                    )
                )
            val heydariId = db.customerDao().getCustomerByName("خانم حیدری")?.id
                ?: db.customerDao().insertCustomer(
                    CustomerEntity(
                        name = "خانم حیدری", company = "", phone = "09160000002",
                        address = "خرم‌آباد", category = "بوتیک و آنلاین",
                        totalPurchases = 0L, orderCount = 0, currentDebt = 0L,
                        tier = "خرید اول", popularModels = "شش جیب",
                        lastOrderDate = "1405/05/20"
                    )
                )

            // 6. Products — 3 models
            data class P(val code: String, val name: String, val price: Long, val cost: Long)
            val prods = listOf(
                P("PROD-SIXPOCKET",  "شش جیب دمپا (ساده/کش)", 465_000L, 320_000L),
                P("PROD-KONAFI-BAG", "بگ نوار کنفی",          520_000L, 360_000L),
                P("PROD-NAVAR-KESH", "نواردار کشی",           485_000L, 330_000L)
            )
            prods.forEach { p ->
                if (db.productDao().getProductByCode(p.code) == null) {
                    db.productDao().insert(
                        ProductEntity(
                            code = p.code,
                            name = p.name,
                            categoryId = 0L,
                            categoryName = "محصولات آماده",
                            isActive = true,
                            suggestedSellingPrice = p.price,
                            currentCostPrice = p.cost,
                            sewingWage = 85_000L
                        )
                    )
                    nProds += 1
                }
            }

            // 7. Inventory rows
            data class Inv(
                val code: String, val name: String, val ready: Int,
                val reserved: Int, val cost: Long, val sale: Long, val weightG: Double
            )
            listOf(
                Inv("PROD-SIXPOCKET",  "شش جیب دمپا (ساده/کش)", 18, 6, 320_000L, 465_000L, 500.0),
                Inv("PROD-KONAFI-BAG", "بگ نوار کنفی",          24, 0, 360_000L, 520_000L, 450.0),
                Inv("PROD-NAVAR-KESH", "نواردار کشی",           15, 0, 330_000L, 485_000L, 480.0)
            ).forEach { inv ->
                if (db.inventoryDao().getByCode(inv.code) == null) {
                    db.inventoryDao().insertItem(
                        InventoryEntity(
                            name = inv.name,
                            code = inv.code,
                            category = "محصولات آماده",
                            readyForShipment = inv.ready,
                            reservedQuantity = inv.reserved,
                            availableForSale = inv.ready - inv.reserved,
                            unitSalePrice = inv.sale,
                            unitCostPrice = inv.cost,
                            unitWeightGrams = inv.weightG,
                            totalWeightKg = inv.ready * inv.weightG / 1000.0,
                            lastUpdated = "1405/05/20"
                        )
                    )
                }
            }

            // 8. Waybills — 3 rows
            data class W(
                val title: String, val date: String, val amount: Long,
                val inboundType: String, val count: Int, val kg: Double
            )
            listOf(
                W("باربری ۵ طاقه از یزدانی نساجی", "1404/09/18", freightBatch1, "طاقه پارچه", 5, batch1Kg),
                W("باربری ملزومات از سبیل",         "1404/09/18", matFreight,     "ملزومات",    6, 74.0),
                W("باربری ۲ طاقه از هوشیار سوری",  "1405/05/20", freightBatch2, "طاقه پارچه", 2, batch2Kg)
            ).forEach { w ->
                db.shippingExpenseDao().insertExpense(
                    ShippingExpenseEntity(
                        trackingNumber = "DEMO-WB-${w.date.replace("/", "")}",
                        title = w.title,
                        date = w.date,
                        timestamp = System.currentTimeMillis(),
                        totalAmount = w.amount,
                        inboundType = w.inboundType,
                        itemCount = w.count,
                        totalWeightKg = w.kg,
                        unit = "طاقه",
                        allocationMethod = "BY_WEIGHT",
                        carrierName = "باربری بازار",
                        status = "ثبت شده"
                    )
                )
            }

            // 9. Babak's sales — 3 orders settled
            repeat(3) { idx ->
                val subtotal = 6 * 465_000L
                db.saleOrderDao().insertOrder(
                    SaleOrderEntity(
                        orderNumber = "#DEMO-SALE-${idx + 1}",
                        customerName = "بابک",
                        customerPhone = "09130000001",
                        modelCode = "PROD-SIXPOCKET",
                        modelName = "شش جیب دمپا (ساده/کش)",
                        quantity = 6,
                        unitPrice = 465_000L,
                        unitCost = 320_000L,
                        discountAmount = 0L,
                        paidAmount = subtotal,
                        orderDate = "1405/05/20",
                        deliveryStatus = "تحویل شده",
                        channel = "فروش حضوری",
                        customerId = babakId
                    )
                )
            }

            // 10. Heydari's reservation
            db.saleOrderDao().insertOrder(
                SaleOrderEntity(
                    orderNumber = "#DEMO-RES-1",
                    customerName = "خانم حیدری",
                    customerPhone = "09160000002",
                    modelCode = "PROD-SIXPOCKET",
                    modelName = "شش جیب دمپا (ساده/کش)",
                    quantity = 6,
                    unitPrice = 465_000L,
                    unitCost = 320_000L,
                    discountAmount = 0L,
                    paidAmount = 0L,
                    orderDate = "1405/05/20",
                    deliveryStatus = "رزرو مشتری",
                    channel = "پیش‌خرید / رزرو",
                    customerId = heydariId
                )
            )

            // 11. Ensure factorySettings row exists
            if (db.factorySettingsDao().getSettingsOnce() == null) {
                db.factorySettingsDao().insertOrUpdate(FactorySettingsEntity())
            }
        }

        return Pair(
            true,
            "داده‌های دمو ثبت شد: $nRolls طاقه • $nMats ملزومات • $nProds محصول"
        )
    }

    // ---------- Clear ----------

    suspend fun clear(db: AppDatabase): Pair<Boolean, String> {
        val dbRaw = db.openHelper.writableDatabase
        var deleted = 0
        db.withTransaction {
            val tables = listOf(
                "fabric_rolls WHERE rollCode LIKE 'DEMO-ROL-%'",
                "materials WHERE code LIKE 'DEMO-MAT-%'",
                "products WHERE code LIKE 'PROD-%'",
                "inventory_items WHERE code LIKE 'PROD-%'",
                "sales_orders WHERE orderNumber LIKE '#DEMO-%'",
                "shipping_expenses WHERE trackingNumber LIKE 'DEMO-WB-%'",
                "suppliers WHERE name IN ('یزدانی نساجی','سبیل','هوشیار سوری')",
                "customers WHERE name IN ('بابک','خانم حیدری')",
                "fabric_categories WHERE name = '" + DEMO_FABRIC_CAT + "'"
            )
            tables.forEach { t ->
                dbRaw.execSQL("DELETE FROM $t")
                deleted += 1
            }
        }
        return Pair(true, "داده‌های دمو پاک شد ($deleted مجموعه).")
    }
}
