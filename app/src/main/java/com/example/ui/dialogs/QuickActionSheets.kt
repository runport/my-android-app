package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerEntity
import com.example.data.model.DashboardChartType
import com.example.data.model.DashboardLayoutArrangement
import com.example.data.model.FabricEntity
import com.example.data.model.FabricRollEntity
import com.example.data.model.FactorySettingsEntity
import com.example.data.model.FixedCostEntity
import com.example.data.model.FixedCostScope
import com.example.data.model.InventoryEntity
import com.example.data.model.MultiProductReadyItem
import com.example.data.model.OrderStatusHistoryEntity
import com.example.data.model.ProductionConsumableInputItem
import com.example.data.model.RollUsageEntity
import com.example.data.model.SaleOrderEntity
import com.example.data.model.SaleOrderStatus
import com.example.data.model.ShippingAllocationMethod
import com.example.data.model.ShippingExpenseEntity
import com.example.data.service.FinancialCalculationService
import com.example.ui.components.CurrencyHelper
import com.example.ui.components.OldNewPriceIndicator
import com.example.util.PersianDateHelper
import com.example.util.UnitFormatter
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.ManufacturingViewModel
import com.example.viewmodel.QuickActionType
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ManagementEditButton
import com.example.ui.components.ManagementDeleteButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionsModalBottomSheet(
  viewModel: ManufacturingViewModel,
  activeAction: QuickActionType,
  sheetState: SheetState,
  onDismiss: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val editingFabric by viewModel.editingFabric.collectAsStateWithLifecycle()
  val editingInventory by viewModel.editingInventory.collectAsStateWithLifecycle()
  val editingOrder by viewModel.editingOrder.collectAsStateWithLifecycle()
  val editingCustomer by viewModel.editingCustomer.collectAsStateWithLifecycle()
  val editingFabricRoll by viewModel.editingFabricRoll.collectAsStateWithLifecycle()
  val editingShippingExpense by viewModel.editingShippingExpense.collectAsStateWithLifecycle()
  val allShippingExpenses by viewModel.shippingExpenses.collectAsStateWithLifecycle()
  val allFabricRolls by viewModel.fabricRolls.collectAsStateWithLifecycle()
  val factorySettings by viewModel.factorySettings.collectAsStateWithLifecycle()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = customColors.card,
    scrimColor = Color.Black.copy(alpha = 0.65f),
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(vertical = 10.dp)
          .width(40.dp)
          .height(4.dp)
          .clip(CircleShape)
          .background(customColors.border)
      )
    }
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      when (activeAction) {
        QuickActionType.NONE -> {
          QuickWarehouseHub(
            onSelectAction = { action -> viewModel.openQuickAction(action) },
            onClose = onDismiss
          )
        }
        QuickActionType.WAREHOUSE_HUB -> {
          QuickWarehouseHub(
            onSelectAction = { action -> viewModel.openQuickAction(action) },
            onClose = onDismiss
          )
        }
        QuickActionType.FABRIC_IN -> {
          QuickFabricForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.READY_GOODS_IN -> {
          QuickReadyGoodsForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.ACCESSORY_IN -> {
          QuickAccessoryForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.CUSTOMER -> {
          QuickCustomerForm(
            onSubmit = { name, company, phone, addr, cat ->
              viewModel.submitCustomer(name, company, phone, addr, cat)
            },
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.SETTINGS_EDIT -> {
          QuickSettingsForm(
            initialSettings = factorySettings,
            onSubmit = { shipOrder, shipRoll, margin, overhead, accCost, compName, chartType, layout, mRolls, mWeight, mReady, mAccWeight ->
              viewModel.saveFactorySettings(
                fixedShippingPerOrder = shipOrder,
                fixedShippingPerRoll = shipRoll,
                targetMargin = margin,
                overheadCost = overhead,
                defaultAccCost = accCost,
                companyName = compName,
                dashboardChartType = chartType,
                dashboardLayout = layout,
                minFabricRolls = mRolls,
                minFabricWeightKg = mWeight,
                minReadyGoodsCount = mReady,
                minAccessoriesWeightKg = mAccWeight
              )
            },
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.SALE -> {
          QuickSaleForm(
            viewModel = viewModel,
            isPreOrder = false,
            onSubmit = { customer, phone, modelCode, modelName, qty, price, discount, paid, cost ->
              viewModel.submitSale(customer, phone, modelCode, modelName, qty, price, discount, paid, cost)
            },
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.PRODUCTION -> {
          QuickProductionForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.CUTTING -> {
          QuickCuttingForm(
            onSubmit = { code, name, fabCode, target, cut, std, act ->
              viewModel.submitCutting(code, name, fabCode, target, cut, std, act)
            },
            onOpenMultiCut = { viewModel.openQuickAction(QuickActionType.MULTI_CUT) },
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.MULTI_CUT -> {
          MultiModelCuttingSheet(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) },
            onClose = onDismiss
          )
        }
        QuickActionType.EDIT_FABRIC -> {
          editingFabric?.let { fabric ->
            EditFabricForm(
              fabric = fabric,
              onUpdate = { updated -> viewModel.updateFabric(updated) },
              onDelete = { id -> viewModel.deleteFabric(id) },
              onBack = onDismiss
            )
          }
        }
        QuickActionType.EDIT_INVENTORY -> {
          editingInventory?.let { item ->
            EditInventoryForm(
              item = item,
              onUpdate = { updated -> viewModel.updateInventoryItem(updated) },
              onDelete = { id -> viewModel.deleteInventoryItem(id) },
              onBack = onDismiss
            )
          }
        }
        QuickActionType.EDIT_ORDER -> {
          editingOrder?.let { order ->
            EditOrderForm(
              order = order,
              onUpdate = { updated -> viewModel.updateSaleOrder(updated) },
              onDelete = { ord -> viewModel.deleteSaleOrder(ord) },
              onBack = onDismiss
            )
          }
        }
        QuickActionType.EDIT_CUSTOMER -> {
          editingCustomer?.let { customer ->
            EditCustomerForm(
              customer = customer,
              onUpdate = { updated -> viewModel.updateCustomer(updated) },
              onDelete = { cust -> viewModel.deleteCustomer(cust) },
              onBack = onDismiss
            )
          }
        }
        QuickActionType.EDIT_FABRIC_ROLL -> {
          editingFabricRoll?.let { roll ->
            EditFabricRollForm(
              roll = roll,
              allExpenses = allShippingExpenses,
              onUpdate = { updated -> viewModel.updateFabricRoll(updated) },
              onDelete = { id -> viewModel.deleteFabricRoll(id) },
              onBack = onDismiss
            )
          }
        }
        QuickActionType.EDIT_SHIPPING_EXPENSE -> {
          editingShippingExpense?.let { expense ->
            EditShippingExpenseForm(
              expense = expense,
              allRolls = allFabricRolls,
              onUpdate = { updated, rollIds -> viewModel.updateShippingExpense(updated, rollIds) },
              onDelete = { id -> viewModel.deleteShippingExpense(id) },
              onBack = onDismiss
            )
          }
        }
        QuickActionType.ROLL_IN -> {
          QuickRollForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.ROLL_CONSUME -> {
          QuickRollConsumeForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.SHIPPING_COMPANY_MANAGER -> {
          ShippingCompanyManagerDialog(
            viewModel = viewModel,
            onDismiss = onDismiss
          )
        }
        QuickActionType.CATEGORY_MANAGER -> {
          CategoryManagerDialog(
            viewModel = viewModel,
            onDismiss = onDismiss
          )
        }
        QuickActionType.SHIPPING_MULTI -> {
          WaybillMultiItemForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.SHIPPING_IN -> {
          QuickShippingExpenseForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.ROLL_HISTORY -> {
          RollHistoryModal(
            viewModel = viewModel,
            onClose = onDismiss
          )
        }
        QuickActionType.FIXED_COST_IN -> {
          QuickFixedCostForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.SALE_RETURN -> {
          QuickSaleReturnForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.PURCHASE_IN -> {
          QuickPurchaseOrderForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.ATOMIC_BOM_PROD -> {
          QuickAtomicBOMProductionForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.MARKET_PRICE_UPDATE -> {
          QuickMarketPriceUpdateForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.CUSTOMER_PAYMENT -> {
          QuickCustomerPaymentForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.SUPPLIER_PAYMENT -> {
          QuickSupplierPaymentForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }
        QuickActionType.INVENTORY_AUDIT -> {
          QuickInventoryAuditForm(
            viewModel = viewModel,
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }

      }
    }
    Spacer(modifier = Modifier.height(28.dp))
  }
}

/**
 * Warehouse & Operations Hub (Triggered by the Prominent Center Button)
 */
@Composable
fun QuickWarehouseHub(
  onSelectAction: (QuickActionType) -> Unit,
  onClose: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = "مرکز عملیات انبارداری و تولید",
          style = MaterialTheme.typography.titleMedium,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
        Text(
          text = "انبار پارچه، کار آماده، ملزومات، مشتریان و هزینه‌ها",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textMuted,
          fontSize = 11.sp
        )
      }
      IconButton(onClick = onClose) {
        Icon(Icons.Default.Close, contentDescription = "بستن", tint = customColors.textMuted)
      }
    }

    // SECTION 1: Warehouse & Inventory Core Actions
    Text(
      text = "مدیریت انبار و موجودی کالا (فاز ۲)",
      style = MaterialTheme.typography.labelMedium,
      color = AccentIndigo,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )

    // New Phase 2: Add Independent Fabric Roll with barcode/code & freight allocation
    QuickActionTile(
      title = "ثبت طاقه جدید پارچه (با باربری و وزن)",
      description = "ثبت شناسه طاقه، متراژ اولیه، وزن، قیمت خرید و تخصیص هزینه باربری",
      icon = Icons.Default.Inventory2,
      color = AccentCyan,
      onClick = { onSelectAction(QuickActionType.ROLL_IN) },
      tag = "action_roll_in"
    )

    // New Phase 2: Add Shipping Expense Record with Allocation
    QuickActionTile(
      title = "ثبت بارنامه و هزینه باربری (تخصیص کرایه)",
      description = "ثبت هزینه باربری با روش‌های تخصیص (تعداد، وزن، مقدار) و محاسبه سرانه",
      icon = Icons.Default.LocalShipping,
      color = AccentBlue,
      onClick = { onSelectAction(QuickActionType.SHIPPING_IN) },
      tag = "action_shipping_in"
    )

    // 1. Add Fabric Roll (Meters + Kilograms)
    QuickActionTile(
      title = "افزودن طاقه پارچه (متراژ و کیلوگرم)",
      description = "ثبت طاقه‌ها، متراژ، وزن به کیلوگرم و قیمت متری/کیلویی با تبدیل خودکار",
      icon = Icons.Default.Inventory2,
      color = AccentCyan,
      onClick = { onSelectAction(QuickActionType.FABRIC_IN) },
      tag = "action_fabric_in"
    )

    // 2. Add Ready Products (تعداد کار آماده)
    QuickActionTile(
      title = "ثبت تعداد کار آماده به انبار",
      description = "ورود محصولات تکمیل‌شده، وزن به گرم و کیلوگرم، قیمت فروش و تمام‌شده",
      icon = Icons.Default.CheckCircle,
      color = StatusSuccess,
      onClick = { onSelectAction(QuickActionType.READY_GOODS_IN) },
      tag = "action_ready_goods_in"
    )

    // 3. Add Trims & Accessories (ملزومات و خرج‌کار)
    QuickActionTile(
      title = "ورود ملزومات و خرج‌کار (واحد و تاریخچه قیمت)",
      description = "ثبت زیپ، دکمه، کش، نخ و لیبل با واحدهای متر، یارد، عدد، کیلو، گرم",
      icon = Icons.Default.Category,
      color = com.example.ui.theme.AccentAmber,
      onClick = { onSelectAction(QuickActionType.ACCESSORY_IN) },
      tag = "action_accessories_in"
    )

    Spacer(modifier = Modifier.height(6.dp))

    // SECTION 2: Commerce & Customers
    Text(
      text = "امور تجاری و مشتریان",
      style = MaterialTheme.typography.labelMedium,
      color = AccentIndigo,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )

    // 4. Add Customer
    QuickActionTile(
      title = "افزودن مشتری جدید",
      description = "ثبت پرونده مشتری، مشخصات فروشگاه، تلفن و رده خریدار",
      icon = Icons.Default.PersonAdd,
      color = com.example.ui.theme.AccentPurple,
      onClick = { onSelectAction(QuickActionType.CUSTOMER) },
      tag = "action_new_customer"
    )

    // 5. Fixed Cost Allocation
    QuickActionTile(
      title = "ثبت هزینه‌های ثابت (تخصیص هوشمند)",
      description = "ثبت اجاره، استهلاک و طراحی با تعیین دامنه تخصیص (همه، دسته، محصول، تولید)",
      icon = Icons.Default.AccountBalance,
      color = com.example.ui.theme.AccentAmber,
      onClick = { onSelectAction(QuickActionType.FIXED_COST_IN) },
      tag = "action_fixed_cost_in"
    )

    // 6. Fixed Costs & Settings
    QuickActionTile(
      title = "تنظیم مبالغ ثابت و سود (باربری و هزینه خیاط‌کار)",
      description = "تعیین هزینه ثابت باربری هر سفارش/طاقه، حاشیه سود ثابت و هزینه خیاط‌کار",
      icon = Icons.Default.Tune,
      color = AccentIndigo,
      onClick = { onSelectAction(QuickActionType.SETTINGS_EDIT) },
      tag = "action_fixed_costs"
    )

    // 7. Fast Sale
    QuickActionTile(
      title = "ثبت فاکتور فروش فوری",
      description = "محاسبه خودکار مبلغ، تخفیف، بدهی و سود سفارش",
      icon = Icons.Default.ShoppingCart,
      color = AccentBlue,
      onClick = { onSelectAction(QuickActionType.SALE) },
      tag = "action_quick_sale"
    )

    Spacer(modifier = Modifier.height(6.dp))

    // SECTION 3: Manufacturing Operations
    Text(
      text = "عملیات تولید و برش",
      style = MaterialTheme.typography.labelMedium,
      color = AccentIndigo,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )

    // New Phase 2: Consume from Fabric Roll for models with inventory protection
    QuickActionTile(
      title = "مصرف طاقه پارچه (تخصیص به مدل‌ها و کنترل باقیمانده)",
      description = "تخصیص متراژ طاقه به یک یا چند مدل، کسر از موجودی طاقه و بهای تمام‌شده",
      icon = Icons.Default.PrecisionManufacturing,
      color = StatusSuccess,
      onClick = { onSelectAction(QuickActionType.ROLL_CONSUME) },
      tag = "action_roll_consume"
    )

    QuickActionTile(
      title = "ثبت عملیات برشکاری ساده",
      description = "ثبت تعداد برش و پایش انحراف مصرف پارچه نسبت به استاندارد",
      icon = Icons.Default.ContentCut,
      color = StatusWarning,
      onClick = { onSelectAction(QuickActionType.CUTTING) },
      tag = "action_quick_cutting"
    )

    QuickActionTile(
      title = "برش چند مدلی از یک طاقه (پاپ‌آپ تفکیک مدل‌ها)",
      description = "تقسیم یک طاقه پارچه بین چندین مدل با مشخص کردن قد کار و تعداد هر مدل",
      icon = Icons.Default.ContentCut,
      color = AccentIndigo,
      onClick = { onSelectAction(QuickActionType.MULTI_CUT) },
      tag = "action_multi_cut"
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = "مدیریت پایه (CRUD کامل)",
      style = MaterialTheme.typography.labelMedium,
      color = AccentIndigo,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )

    QuickActionTile(
      title = "مدیریت شرکت‌های باربری",
      description = "افزودن، ویرایش و حذف شرکت‌های باربری و رانندگان",
      icon = Icons.Default.LocalShipping,
      color = AccentBlue,
      onClick = { onSelectAction(QuickActionType.SHIPPING_COMPANY_MANAGER) },
      tag = "action_company_manager"
    )

    QuickActionTile(
      title = "مدیریت دسته‌بندی‌ها",
      description = "دسته‌های پارچه، محصول و ملزومات - افزودن، ویرایش، حذف",
      icon = Icons.Default.Category,
      color = AccentIndigo,
      onClick = { onSelectAction(QuickActionType.CATEGORY_MANAGER) },
      tag = "action_category_manager"
    )

    QuickActionTile(
      title = "ثبت بچ تولید و کارگاه",
      description = "ورود متراژ و طاقه، انتقال خودکار محصول تکمیل‌شده به انبار",
      icon = Icons.Default.PrecisionManufacturing,
      color = StatusSuccess,
      onClick = { onSelectAction(QuickActionType.PRODUCTION) },
      tag = "action_quick_production"
    )
  }
}

@Composable
fun QuickActionTile(
  title: String,
  description: String,
  icon: ImageVector,
  color: Color,
  onClick: () -> Unit,
  tag: String
) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.cardElevated)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(14.dp)
      .testTag(tag)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
      }

      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textMuted,
          fontSize = 11.sp
        )
      }
    }
  }
}

/**
 * 1. Fabric Form (With Kilograms & Meters + Auto-Popups & Trend Chart)
 */
@Composable
fun QuickFabricForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val existingFabrics by viewModel.fabrics.collectAsStateWithLifecycle()
  val existingSuppliers by viewModel.suppliers.collectAsStateWithLifecycle()
  val factorySettings by viewModel.factorySettings.collectAsStateWithLifecycle()

  var showItemPicker by remember { mutableStateOf(false) }
  var showSupplierPicker by remember { mutableStateOf(false) }
  var showSupplierRegisterDialog by remember { mutableStateOf(false) }

  var name by remember { mutableStateOf("دورس پنبه ۳ نخ") }
  var code by remember { mutableStateOf("M204") }
  var color by remember { mutableStateOf("سرمه‌ای تیره") }
  var batchNumber by remember { mutableStateOf("PRT-995") }
  var supplierName by remember { mutableStateOf("نساجی تابان کاشان") }
  var rollsText by remember { mutableStateOf("8") }
  var weightKgText by remember { mutableStateOf("150") }
  var metersPerKgText by remember { mutableStateOf("3.0") }
  var metersText by remember { mutableStateOf("450") }
  var buyPricePerMeterText by remember { mutableStateOf("210000") }
  var buyPricePerKgText by remember { mutableStateOf("630000") }
  var previousBuyPricePerMeter by remember { mutableStateOf(0L) }
  var previousPurchaseDate by remember { mutableStateOf("") }
  var syncWarehousePrices by remember { mutableStateOf(true) }

  // Item selection popup with "+ ثبت محصول جدید" at top
  if (showItemPicker) {
    ItemSelectionPopupDialog(
      title = "انتخاب پارچه قبلی یا ثبت جدید",
      items = existingFabrics,
      onDismiss = { showItemPicker = false },
      onAddNew = {
        name = ""
        code = ""
        color = ""
        rollsText = ""
        weightKgText = ""
        metersPerKgText = "3.0"
        metersText = ""
        buyPricePerMeterText = ""
        buyPricePerKgText = ""
        previousBuyPricePerMeter = 0L
        previousPurchaseDate = ""
      },
      onItemSelected = { selected ->
        name = selected.name
        code = selected.code
        color = selected.color
        supplierName = selected.supplierName
        metersPerKgText = if (selected.metersPerKg > 0.0) selected.metersPerKg.toString() else "3.0"
        buyPricePerMeterText = selected.buyPricePerMeter.toString()
        buyPricePerKgText = selected.buyPricePerKg.toString()
        previousBuyPricePerMeter = if (selected.previousBuyPricePerMeter > 0L) selected.previousBuyPricePerMeter else selected.buyPricePerMeter
        previousPurchaseDate = selected.previousPurchaseDate.ifBlank { selected.purchaseDate }
      },
      itemLabel = { "${it.name} - ${it.color}" },
      itemCode = { it.code },
      itemSecondary = { "موجودی: ${it.rollCount} طاقه (${it.totalMeters.toInt()} متر) | تأمین‌کننده: ${it.supplierName}" },
      itemPrice = { it.buyPricePerMeter }
    )
  }

  // Supplier selection popup with "+ ثبت تامین‌کننده جدید" at top
  if (showSupplierPicker) {
    SupplierSelectionPopupDialog(
      suppliers = existingSuppliers,
      onDismiss = { showSupplierPicker = false },
      onAddNewSupplier = { showSupplierRegisterDialog = true },
      onSupplierSelected = { sup ->
        supplierName = "${sup.name} (${sup.storeName.ifBlank { "دفتر مرکزی" }})"
      }
    )
  }

  // 7-Field Supplier Registration Dialog
  if (showSupplierRegisterDialog) {
    SupplierRegistrationDialog(
      onDismiss = { showSupplierRegisterDialog = false },
      onSubmit = { sName, sStore, sMobile, sPhone, sAddr, sCat, sDesc ->
        viewModel.submitSupplier(sName, sStore, sMobile, sPhone, sAddr, sCat, sDesc)
        supplierName = "$sName ($sStore)"
      }
    )
  }

  val rolls = rollsText.toIntOrNull() ?: 0
  val meters = metersText.toDoubleOrNull() ?: 0.0
  val weightKg = weightKgText.toDoubleOrNull() ?: 0.0
  val metersPerKg = metersPerKgText.toDoubleOrNull() ?: (if (weightKg > 0.0) meters / weightKg else 3.0)
  val priceMeter = buyPricePerMeterText.toLongOrNull() ?: 0L
  val priceKg = buyPricePerKgText.toLongOrNull() ?: 0L

  val totalVal = if (priceMeter > 0 && meters > 0) (meters * priceMeter).toLong() else (weightKg * priceKg).toLong()

  // Dynamic cost calculation based on pricing formula:
  // (Fabric Cost + Accessories + Shipping + Printing + Sewing + Fixed Profit)
  val estimatedUnitMeter = 1.4
  val fabricCostPerUnit = (priceMeter * estimatedUnitMeter).toLong()
  val accCost = factorySettings.defaultAccessoriesCost
  val shipCostPerRoll = factorySettings.fixedShippingCostPerRoll
  val estimatedPiecesPerRoll = (metersPerKg * 25 / estimatedUnitMeter).coerceAtLeast(1.0)
  val shippingPerPiece = (shipCostPerRoll / estimatedPiecesPerRoll).toLong()
  val sewingCost = 85000L
  val printingCost = 45000L
  val subTotalCost = fabricCostPerUnit + accCost + shippingPerPiece + sewingCost + printingCost
  val targetProfit = (subTotalCost * (factorySettings.targetProfitMarginPercent / 100.0)).toLong()
  val estimatedSalePrice = subTotalCost + targetProfit

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("افزودن طاقه پارچه (متراژ و کیلوگرم)", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    // Top action buttons for quick selection
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = { showItemPicker = true },
        modifier = Modifier.weight(1f).height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan.copy(alpha = 0.2f))
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Search, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
          Text("انتخاب پارچه / جدید", color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Button(
        onClick = { showSupplierPicker = true },
        modifier = Modifier.weight(1f).height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo.copy(alpha = 0.2f))
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.PersonAdd, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(16.dp))
          Text("تأمین‌کننده / ۷ گانه", color = AccentIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.4f)) {
        ExecutiveTextField(label = "نام/نوع پارچه", value = name, onValueChange = { name = it })
      }
      Box(modifier = Modifier.weight(0.8f)) {
        ExecutiveTextField(label = "کد پارچه", value = code, onValueChange = { code = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "رنگ", value = color, onValueChange = { color = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "شماره پارت", value = batchNumber, onValueChange = { batchNumber = it })
      }
    }

    ExecutiveTextField(
      label = "تأمین‌کننده پارچه (کلیک برای انتخاب یا ثبت ۷ گانه)",
      value = supplierName,
      onValueChange = { supplierName = it }
    )

    // Quantitative metrics: Rolls, Weight, Meters per Kg -> Automatic Total Meters
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد طاقه", value = rollsText, keyboardType = KeyboardType.Number, onValueChange = { rollsText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "وزن کل طاقه‌ها (کیلو)",
          value = weightKgText,
          keyboardType = KeyboardType.Decimal,
          onValueChange = {
            weightKgText = it
            val w = it.toDoubleOrNull() ?: 0.0
            val mpk = metersPerKgText.toDoubleOrNull() ?: 0.0
            if (w > 0.0 && mpk > 0.0) {
              metersText = String.format(java.util.Locale.US, "%.1f", w * mpk)
            }
          }
        )
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "متراژ پارچه در کیلو (متر/کیلو)",
          value = metersPerKgText,
          keyboardType = KeyboardType.Decimal,
          onValueChange = {
            metersPerKgText = it
            val mpk = it.toDoubleOrNull() ?: 0.0
            val w = weightKgText.toDoubleOrNull() ?: 0.0
            if (w > 0.0 && mpk > 0.0) {
              metersText = String.format(java.util.Locale.US, "%.1f", w * mpk)
            }
          }
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "متراژ کل طاقه (محاسبه خودکار)",
          value = metersText,
          keyboardType = KeyboardType.Decimal,
          onValueChange = {
            metersText = it
            val m = it.toDoubleOrNull() ?: 0.0
            val w = weightKgText.toDoubleOrNull() ?: 0.0
            if (m > 0.0 && w > 0.0) {
              metersPerKgText = String.format(java.util.Locale.US, "%.2f", m / w)
            }
          }
        )
      }
    }

    // Pricing: Meter and Kilogram
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "قیمت خرید هر متر (تومان)",
          value = buyPricePerMeterText,
          keyboardType = KeyboardType.Number,
          onValueChange = {
            buyPricePerMeterText = it
            val pm = it.toLongOrNull() ?: 0L
            val mpk = metersPerKgText.toDoubleOrNull() ?: 3.0
            if (pm > 0L && mpk > 0.0) {
              buyPricePerKgText = (pm * mpk).toLong().toString()
            }
          }
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "قیمت هر کیلو (تومان)",
          value = buyPricePerKgText,
          keyboardType = KeyboardType.Number,
          onValueChange = {
            buyPricePerKgText = it
            val pkg = it.toLongOrNull() ?: 0L
            val mpk = metersPerKgText.toDoubleOrNull() ?: 3.0
            if (pkg > 0L && mpk > 0.0) {
              buyPricePerMeterText = (pkg / mpk).toLong().toString()
            }
          }
        )
      }
    }

    // Price Update in RED if price has changed from previous purchase
    PriceUpdateBadge(
      currentPrice = priceMeter,
      previousPrice = previousBuyPricePerMeter,
      previousDate = previousPurchaseDate,
      unitLabel = "هر متر"
    )

    // Line Chart showing Stock and Sales/Usage over dates with 3-way toggle
    MiniTrendLineChart(
      title = "روند موجودی و مصرف طاقه کد $code در تاریخ‌های مختلف",
      dates = listOf("۱۵ اسفند", "۱۸ اسفند", "۲۲ اسفند", "۲۸ اسفند", "امروز"),
      stockValues = listOf(120.0, 180.0, 150.0, 240.0, (rolls * 25.0).coerceAtLeast(60.0)),
      salesValues = listOf(60.0, 95.0, 110.0, 130.0, 85.0)
    )

    // Formula pricing preview & Sync Checkbox
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("ارزش کل خرید پارچه این پارت:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(totalVal), style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("بهای تمام‌شده هر عدد (فرمول کامل کارخانه):", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(subTotalCost), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("قیمت فروش پیشنهادی (با سود ${factorySettings.targetProfitMarginPercent.toInt()}٪):", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(estimatedSalePrice), style = MaterialTheme.typography.bodySmall, color = AccentCyan, fontWeight = FontWeight.Bold)
        }

        HorizontalDivider(color = customColors.border)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { syncWarehousePrices = !syncWarehousePrices },
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Checkbox(
            checked = syncWarehousePrices,
            onCheckedChange = { syncWarehousePrices = it },
            colors = CheckboxDefaults.colors(checkedColor = AccentCyan)
          )
          Text(
            text = "همگام‌سازی خودکار بهای تمام‌شده و قیمت فروش در انبار محصولات آماده",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textPrimary,
            fontSize = 11.sp
          )
        }
      }
    }

    Button(
      onClick = {
        viewModel.submitFabric(
          name = name,
          code = code,
          color = color,
          batchNumber = batchNumber,
          supplierName = supplierName,
          rollCount = rolls,
          totalMeters = meters,
          totalWeightKg = weightKg,
          buyPricePerMeter = priceMeter,
          buyPricePerKg = priceKg,
          syncWarehousePrices = syncWarehousePrices
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_fabric_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
    ) {
      Text("تأیید و ثبت در انبار مواد اولیه", fontWeight = FontWeight.Bold, color = Color.Black)
    }
  }
}

/**
 * Helper draft class for multi-product lines from a single fabric roll
 */
data class ReadyProductLineDraft(
  val id: String = java.util.UUID.randomUUID().toString(),
  var name: String = "هودی بیسیک پاییزه",
  var code: String = "HD-204",
  var readyQuantityText: String = "50",
  var metersUsedText: String = "25.0",
  var consumptionUnit: FabricConsumptionUnit = FabricConsumptionUnit.METERS,
  var sewingWageText: String = "85000",
  var accessoriesCostText: String = "32000",
  var salePriceText: String = "680000",
  var unitWeightGramsText: String = "500",
  var previousCostPrice: Long = 0L,
  var previousCostDate: String = ""
)

/**
 * 2. Ready Goods Form (ثبت کار آماده - چند محصول از یک طاقه + تاریخ خودکار + کنترل موجودی)
 */
@Composable
fun QuickReadyGoodsForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val existingItems by viewModel.inventory.collectAsStateWithLifecycle()
  val availableRolls by viewModel.availableFabricRolls.collectAsStateWithLifecycle()
  val readyGoodsList = remember(existingItems) {
    existingItems.filter { it.category == "محصولات آماده" || it.readyForShipment > 0 || it.availableForSale > 0 }
  }

  // Automatic Persian Date registration
  var registrationDate by remember { mutableStateOf(PersianDateHelper.getCurrentPersianDate()) }

  // Roll selection state (Only available rolls shown)
  var selectedRollId by remember {
    mutableStateOf(availableRolls.firstOrNull()?.id ?: 0L)
  }
  val selectedRoll = availableRolls.find { it.id == selectedRollId } ?: availableRolls.firstOrNull()

  // Multi-product lines list from this roll
  val productLines = remember {
    mutableStateListOf(
      ReadyProductLineDraft(
        name = "هودی کلاه‌دار پاییزه",
        code = "HD-204",
        readyQuantityText = "40",
        metersUsedText = "30.0",
        sewingWageText = "85000",
        accessoriesCostText = "32000",
        salePriceText = "680000",
        unitWeightGramsText = "550"
      )
    )
  }

  var pickerLineIndex by remember { mutableStateOf<Int?>(null) }
  var rollDropdownExpanded by remember { mutableStateOf(false) }

  // Item selection popup
  if (pickerLineIndex != null) {
    val targetIndex = pickerLineIndex!!
    ItemSelectionPopupDialog(
      title = "انتخاب محصول از انبار کالا",
      items = readyGoodsList,
      onDismiss = { pickerLineIndex = null },
      onAddNew = {
        if (targetIndex in productLines.indices) {
          productLines[targetIndex] = productLines[targetIndex].copy(
            name = "",
            code = "",
            readyQuantityText = "1",
            metersUsedText = "1.0",
            salePriceText = "0",
            previousCostPrice = 0L,
            previousCostDate = ""
          )
        }
        pickerLineIndex = null
      },
      onItemSelected = { selected ->
        if (targetIndex in productLines.indices) {
          productLines[targetIndex] = productLines[targetIndex].copy(
            name = selected.name,
            code = selected.code,
            readyQuantityText = if (selected.readyForShipment > 0) selected.readyForShipment.toString() else "30",
            salePriceText = selected.unitSalePrice.toString(),
            sewingWageText = "85000",
            accessoriesCostText = "32000",
            unitWeightGramsText = if (selected.unitWeightGrams > 0.0) selected.unitWeightGrams.toString() else "500",
            previousCostPrice = if (selected.previousCostPrice > 0L) selected.previousCostPrice else selected.unitCostPrice,
            previousCostDate = selected.previousCostDate
          )
        }
        pickerLineIndex = null
      },
      itemLabel = { it.name },
      itemCode = { it.code },
      itemSecondary = { "موجودی: ${it.readyForShipment} عدد | قیمت فروش: ${CurrencyHelper.formatToman(it.unitSalePrice)}" },
      itemPrice = { it.unitCostPrice }
    )
  }

  // Inventory & Capacity calculations
  val rollMetersPerKg = if ((selectedRoll?.weightKg ?: 0.0) > 0.0 && (selectedRoll?.initialMeters ?: 0.0) > 0.0)
    (selectedRoll!!.initialMeters / selectedRoll.weightKg) else 3.0

  val totalRequestedMeters = productLines.sumOf { draft ->
    val raw = draft.metersUsedText.toDoubleOrNull() ?: 0.0
    if (draft.consumptionUnit == FabricConsumptionUnit.METERS) raw else raw * rollMetersPerKg
  }
  val rollRemainingMeters = selectedRoll?.remainingMeters ?: 0.0
  val isCapacityValid = selectedRoll != null && FinancialCalculationService.validateRollCapacity(rollRemainingMeters, totalRequestedMeters)
  val remainingAfterSubmission = (rollRemainingMeters - totalRequestedMeters).coerceAtLeast(0.0)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = "ثبت کار آماده (چند محصول از یک طاقه)",
          style = MaterialTheme.typography.titleMedium,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Icon(Icons.Default.CalendarToday, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(13.dp))
          Text(
            text = "تاریخ ثبت: $registrationDate",
            style = MaterialTheme.typography.labelSmall,
            color = AccentBlue,
            fontWeight = FontWeight.Medium
          )
        }
      }
      IconButton(onClick = onBack) {
        Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted)
      }
    }

    ExecutiveTextField(
      label = "تاریخ ثبت کار آماده در انبار (شمسی - خودکار و قابل ویرایش)",
      value = registrationDate,
      onValueChange = { registrationDate = it },
      leadingIcon = Icons.Default.CalendarToday
    )

    // 1. SELECT FABRIC ROLL ("از طاقه" - فقط طاقه‌های موجود)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, if (selectedRoll == null) StatusDanger else customColors.border, RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "فیلد «از طاقه» (انتخاب طاقه موجود)",
            style = MaterialTheme.typography.labelMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${availableRolls.size} طاقه موجود",
            style = MaterialTheme.typography.labelSmall,
            color = AccentCyan
          )
        }

        if (availableRolls.isEmpty()) {
          Text(
            text = "هیچ طاقه فعالی با موجودی مثبت در انبار یافت نشد!",
            style = MaterialTheme.typography.bodySmall,
            color = StatusDanger
          )
        } else {
          // Dropdown / selector button for rolls
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
              .clickable { rollDropdownExpanded = !rollDropdownExpanded }
              .padding(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                  text = "طاقه انتخابی: ${selectedRoll?.rollCode ?: "انتخاب کنید"} (${selectedRoll?.fabricType ?: ""})",
                  style = MaterialTheme.typography.bodyMedium,
                  color = customColors.textPrimary,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "موجودی باقیمانده: ${selectedRoll?.remainingMeters ?: 0.0} متر | قیمت خرید: ${CurrencyHelper.formatToman(selectedRoll?.buyPricePerMeter ?: 0L)} /متر",
                  style = MaterialTheme.typography.bodySmall,
                  color = customColors.textMuted
                )
              }
              Icon(Icons.Default.ArrowDropDown, contentDescription = "انتخاب طاقه", tint = customColors.textPrimary)
            }
          }

          if (rollDropdownExpanded) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(customColors.card)
                .border(1.dp, AccentIndigo, RoundedCornerShape(8.dp))
                .padding(6.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              availableRolls.forEach { roll ->
                val isSelected = roll.id == selectedRoll?.id
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) AccentIndigo.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable {
                      selectedRollId = roll.id
                      rollDropdownExpanded = false
                    }
                    .padding(8.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "${roll.rollCode} • ${roll.fabricType}",
                      style = MaterialTheme.typography.bodySmall,
                      color = if (isSelected) AccentIndigo else customColors.textPrimary,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                      text = "${roll.remainingMeters} متر باقیمانده",
                      style = MaterialTheme.typography.labelSmall,
                      color = StatusSuccess,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 2. LIVE INVENTORY CONTROL CARD (کنترل موجودی طاقه)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(if (isCapacityValid) StatusSuccess.copy(alpha = 0.08f) else StatusDanger.copy(alpha = 0.12f))
        .border(1.5.dp, if (isCapacityValid) StatusSuccess else StatusDanger, RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isCapacityValid) "کنترل ظرفیت طاقه: مجاز" else "خطای کنترل موجودی طاقه!",
            style = MaterialTheme.typography.titleSmall,
            color = if (isCapacityValid) StatusSuccess else StatusDanger,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "موجودی طاقه: $rollRemainingMeters متر",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textPrimary
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "مجموع متراژ درخواستی محصولات: $totalRequestedMeters متر",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.textSecondary
          )
          Text(
            text = if (isCapacityValid) "باقیمانده پس از ثبت: $remainingAfterSubmission متر" else "کسری: ${String.format(java.util.Locale.US, "%.1f", totalRequestedMeters - rollRemainingMeters)} متر",
            style = MaterialTheme.typography.bodySmall,
            color = if (isCapacityValid) StatusSuccess else StatusDanger,
            fontWeight = FontWeight.Bold
          )
        }

        if (!isCapacityValid) {
          Text(
            text = "⚠️ مجموع متراژ درخواستی ($totalRequestedMeters متر) بیشتر از متراژ طاقه ($rollRemainingMeters متر) است و سیستم مانع از ثبت می‌شود.",
            style = MaterialTheme.typography.labelSmall,
            color = StatusDanger
          )
        }
      }
    }

    // 3. MULTI-PRODUCT LIST (چند محصول از یک طاقه + و -)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "محصولات تولیدی از این طاقه (${productLines.size} قلم)",
        style = MaterialTheme.typography.labelLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      Button(
        onClick = {
          productLines.add(
            ReadyProductLineDraft(
              name = "محصول جدید ${productLines.size + 1}",
              code = "PR-${100 + productLines.size + 1}",
              readyQuantityText = "20",
              metersUsedText = "15.0",
              sewingWageText = "85000",
              accessoriesCostText = "30000",
              salePriceText = "650000",
              unitWeightGramsText = "450"
            )
          )
        },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
        modifier = Modifier.height(34.dp)
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Add, contentDescription = "افزودن محصول", modifier = Modifier.size(16.dp))
          Text("افزودن محصول (+)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Product Line Cards
    productLines.forEachIndexed { index, line ->
      val qty = line.readyQuantityText.toIntOrNull() ?: 0
      val enteredRaw = line.metersUsedText.toDoubleOrNull() ?: 0.0
      val meters = if (line.consumptionUnit == FabricConsumptionUnit.METERS) enteredRaw else enteredRaw * rollMetersPerKg
      val wage = line.sewingWageText.toLongOrNull() ?: 0L
      val accCost = line.accessoriesCostText.toLongOrNull() ?: 0L
      val rollPrice = selectedRoll?.buyPricePerMeter ?: 0L
      val fabricCostTotal = (meters * rollPrice).toLong()
      val fabricCostPerUnit = if (qty > 0) fabricCostTotal / qty else 0L
      val calculatedUnitCost = fabricCostPerUnit + wage + accCost

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.card)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .padding(12.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          // Line Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Box(
                modifier = Modifier
                  .size(22.dp)
                  .clip(CircleShape)
                  .background(AccentIndigo),
                contentAlignment = Alignment.Center
              ) {
                Text("${index + 1}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
              Text(
                text = if (line.name.isNotBlank()) line.name else "محصول جدید ${index + 1}",
                style = MaterialTheme.typography.titleSmall,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
              // Select from inventory or Add new
              Button(
                onClick = { pickerLineIndex = index },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue.copy(alpha = 0.15f)),
                modifier = Modifier.height(28.dp)
              ) {
                Text("انتخاب از انبار / مدل جدید", color = AccentBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }

              // Remove button (-)
              if (productLines.size > 1) {
                IconButton(
                  onClick = { productLines.removeAt(index) },
                  modifier = Modifier.size(28.dp)
                ) {
                  Icon(Icons.Default.Remove, contentDescription = "حذف محصول", tint = StatusDanger, modifier = Modifier.size(16.dp))
                }
              }
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1.4f)) {
              ExecutiveTextField(
                label = "نام محصول",
                value = line.name,
                onValueChange = { productLines[index] = line.copy(name = it) }
              )
            }
            Box(modifier = Modifier.weight(0.7f)) {
              ExecutiveTextField(
                label = "کد مدل",
                value = line.code,
                onValueChange = { productLines[index] = line.copy(code = it) }
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
              ExecutiveTextField(
                label = "تعداد کار آماده (عدد)",
                value = line.readyQuantityText,
                keyboardType = KeyboardType.Number,
                onValueChange = { productLines[index] = line.copy(readyQuantityText = it) }
              )
            }
            Box(modifier = Modifier.weight(1f)) {
              Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(customColors.secondaryBg)
                    .padding(2.dp),
                  horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                  listOf(
                    FabricConsumptionUnit.METERS to "📏 متر",
                    FabricConsumptionUnit.KILOGRAMS to "⚖️ کیلو"
                  ).forEach { (u, lbl) ->
                    val isSel = line.consumptionUnit == u
                    Box(
                      modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSel) AccentIndigo else Color.Transparent)
                        .clickable { productLines[index] = line.copy(consumptionUnit = u) }
                        .padding(vertical = 3.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(lbl, fontSize = 10.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = if (isSel) Color.White else customColors.textSecondary)
                    }
                  }
                }
                ExecutiveTextField(
                  label = "میزان مصرف (${line.consumptionUnit.shortUnit})",
                  value = line.metersUsedText,
                  keyboardType = KeyboardType.Decimal,
                  onValueChange = { productLines[index] = line.copy(metersUsedText = it) }
                )
                if (enteredRaw > 0.0) {
                  val eqv = if (line.consumptionUnit == FabricConsumptionUnit.METERS) {
                    if (rollMetersPerKg > 0) enteredRaw / rollMetersPerKg else 0.0
                  } else enteredRaw * rollMetersPerKg
                  val eqvUnit = if (line.consumptionUnit == FabricConsumptionUnit.METERS) "کیلوگرم" else "متر"
                  Text("≈ ${String.format(java.util.Locale.US, "%.1f", eqv)} $eqvUnit", fontSize = 10.sp, color = AccentCyan)
                }
              }
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
              ExecutiveTextField(
                label = "هزینه خیاط‌کار هر کار (تومان)",
                value = line.sewingWageText,
                keyboardType = KeyboardType.Number,
                onValueChange = { productLines[index] = line.copy(sewingWageText = it) }
              )
            }
            Box(modifier = Modifier.weight(1f)) {
              ExecutiveTextField(
                label = "هزینه ملزومات هر کار (تومان)",
                value = line.accessoriesCostText,
                keyboardType = KeyboardType.Number,
                onValueChange = { productLines[index] = line.copy(accessoriesCostText = it) }
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
              ExecutiveTextField(
                label = "قیمت فروش هر عدد (تومان)",
                value = line.salePriceText,
                keyboardType = KeyboardType.Number,
                onValueChange = { productLines[index] = line.copy(salePriceText = it) }
              )
            }
            Box(modifier = Modifier.weight(1f)) {
              ExecutiveTextField(
                label = "وزن هر عدد (گرم)",
                value = line.unitWeightGramsText,
                keyboardType = KeyboardType.Decimal,
                onValueChange = { productLines[index] = line.copy(unitWeightGramsText = it) }
              )
            }
          }

          // Price Comparison & Cost Breakdown for this product
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(customColors.secondaryBg)
              .padding(8.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("بهای تمام‌شده محاسبه‌شده هر عدد:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                // Display Old vs New price indicator (Red/Small vs Green)
                OldNewPriceIndicator(
                  currentPrice = calculatedUnitCost,
                  previousPrice = line.previousCostPrice
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "پارچه: ${CurrencyHelper.formatToman(fabricCostPerUnit)} • خیاط‌کار: ${CurrencyHelper.formatToman(wage)} • ملزومات: ${CurrencyHelper.formatToman(accCost)}",
                  style = MaterialTheme.typography.labelSmall,
                  color = customColors.textMuted
                )
              }
            }
          }
        }
      }
    }

    // Submit Multi-Product Button
    Button(
      onClick = {
        if (selectedRoll != null && isCapacityValid) {
          val items = productLines.map { draft ->
            val entered = draft.metersUsedText.toDoubleOrNull() ?: 0.0
            val effMeters = if (draft.consumptionUnit == FabricConsumptionUnit.METERS) entered else entered * rollMetersPerKg
            MultiProductReadyItem(
              modelName = draft.name,
              modelCode = draft.code,
              readyQuantity = draft.readyQuantityText.toIntOrNull() ?: 0,
              metersUsed = effMeters,
              sewingWagePerItem = draft.sewingWageText.toLongOrNull() ?: 85000L,
              salePricePerItem = draft.salePriceText.toLongOrNull() ?: 680000L,
              unitWeightGrams = draft.unitWeightGramsText.toDoubleOrNull() ?: 500.0,
              accessoriesCostPerItem = draft.accessoriesCostText.toLongOrNull() ?: 32000L
            )
          }
          viewModel.submitMultiProductReadyGoods(
            rollId = selectedRoll.id,
            products = items,
            note = "ثبت همزمان ${items.size} محصول از طاقه ${selectedRoll.rollCode}"
          )
        }
      },
      enabled = isCapacityValid && totalRequestedMeters > 0.0 && selectedRoll != null,
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_multi_ready_goods_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = if (isCapacityValid) StatusSuccess else StatusDanger
      )
    ) {
      Text(
        text = if (isCapacityValid) "تأیید و ثبت ${productLines.size} محصول در انبار کالا" else "خطای کسری متراژ طاقه - ثبت مسدود است",
        fontWeight = FontWeight.Bold
      )
    }
  }
}

/**
 * 3. Accessories Form (ملزومات و خرج‌کار + پاپ‌آپ و تأمین‌کننده و واحد)
 */
@Composable
fun QuickAccessoryForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val existingItems by viewModel.inventory.collectAsStateWithLifecycle()
  val existingSuppliers by viewModel.suppliers.collectAsStateWithLifecycle()
  val factorySettings by viewModel.factorySettings.collectAsStateWithLifecycle()

  val accessoriesList = remember(existingItems) {
    existingItems.filter { it.category == "ملزومات" || it.code.startsWith("ACC") }
  }

  var showItemPicker by remember { mutableStateOf(false) }
  var showSupplierPicker by remember { mutableStateOf(false) }
  var showSupplierRegisterDialog by remember { mutableStateOf(false) }
  var showAddUnitDialog by remember { mutableStateOf(false) }

  var name by remember { mutableStateOf("زیپ استخوانی فلزی ۵۰ سانت") }
  var code by remember { mutableStateOf("ACC-ZIP-50") }
  var supplierName by remember { mutableStateOf("بازرگانی یراق البرز") }
  var qtyText by remember { mutableStateOf("1500") }
  var unitSalePriceText by remember { mutableStateOf("38000") }
  var unitCostPriceText by remember { mutableStateOf("26000") }
  var unitType by remember { mutableStateOf("عدد") }
  var metersPerKgText by remember { mutableStateOf("0.0") }
  var pricePerMeterText by remember { mutableStateOf("0") }
  var previousCostPrice by remember { mutableStateOf(0L) }
  var previousCostDate by remember { mutableStateOf("") }

  // Item selection popup with "+ ثبت ملزومات جدید" at top
  if (showItemPicker) {
    ItemSelectionPopupDialog(
      title = "انتخاب ملزومات قبلی یا ثبت جدید",
      items = accessoriesList,
      onDismiss = { showItemPicker = false },
      onAddNew = {
        name = ""
        code = ""
        qtyText = ""
        unitSalePriceText = ""
        unitCostPriceText = ""
        previousCostPrice = 0L
        previousCostDate = ""
      },
      onItemSelected = { selected ->
        name = selected.name
        code = selected.code
        supplierName = selected.supplierName
        unitType = selected.unitType
        qtyText = selected.availableForSale.toString()
        unitSalePriceText = selected.unitSalePrice.toString()
        unitCostPriceText = selected.unitCostPrice.toString()
        previousCostPrice = if (selected.previousCostPrice > 0L) selected.previousCostPrice else selected.unitCostPrice
        previousCostDate = selected.previousCostDate
      },
      itemLabel = { it.name },
      itemCode = { it.code },
      itemSecondary = { "موجودی: ${it.availableForSale} ${it.unitType} | قیمت خرید: ${CurrencyHelper.formatToman(it.unitCostPrice)}" },
      itemPrice = { it.unitCostPrice }
    )
  }

  // Supplier selection popup with "+ ثبت تامین‌کننده جدید" at top
  if (showSupplierPicker) {
    SupplierSelectionPopupDialog(
      suppliers = existingSuppliers,
      onDismiss = { showSupplierPicker = false },
      onAddNewSupplier = { showSupplierRegisterDialog = true },
      onSupplierSelected = { sup ->
        supplierName = "${sup.name} (${sup.storeName.ifBlank { "دفتر مرکزی" }})"
      }
    )
  }

  // 7-Field Supplier Registration Dialog
  if (showSupplierRegisterDialog) {
    SupplierRegistrationDialog(
      onDismiss = { showSupplierRegisterDialog = false },
      onSubmit = { sName, sStore, sMobile, sPhone, sAddr, sCat, sDesc ->
        viewModel.submitSupplier(sName, sStore, sMobile, sPhone, sAddr, sCat, sDesc)
        supplierName = "$sName ($sStore)"
      }
    )
  }

  // Custom Unit Type Dialog
  if (showAddUnitDialog) {
    CustomUnitTypeDialog(
      onDismiss = { showAddUnitDialog = false },
      onAddUnit = { newUnit ->
        viewModel.addCustomUnitType(newUnit)
        unitType = newUnit
      }
    )
  }

  val qty = qtyText.toIntOrNull() ?: 0
  val sale = unitSalePriceText.toLongOrNull() ?: 0L
  val cost = unitCostPriceText.toLongOrNull() ?: 0L
  val metersPerKg = metersPerKgText.toDoubleOrNull() ?: 0.0
  val pricePerMeter = pricePerMeterText.toLongOrNull() ?: 0L

  val availableUnits = remember(factorySettings.customUnitTypes) {
    factorySettings.customUnitTypes.split(",").map { it.trim() }.filter { it.isNotBlank() }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ورود ملزومات و خرج‌کار به انبار", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    // Top action buttons for quick selection
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = { showItemPicker = true },
        modifier = Modifier.weight(1f).height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentAmber.copy(alpha = 0.2f))
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Search, contentDescription = null, tint = com.example.ui.theme.AccentAmber, modifier = Modifier.size(16.dp))
          Text("انتخاب ملزومات / جدید", color = com.example.ui.theme.AccentAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Button(
        onClick = { showSupplierPicker = true },
        modifier = Modifier.weight(1f).height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo.copy(alpha = 0.2f))
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.PersonAdd, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(16.dp))
          Text("تأمین‌کننده / ۷ گانه", color = AccentIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.5f)) {
        ExecutiveTextField(label = "نام قلم ملزومات (زیپ، دکمه، کش، نخ)", value = name, onValueChange = { name = it })
      }
      Box(modifier = Modifier.weight(0.8f)) {
        ExecutiveTextField(label = "کد قلم", value = code, onValueChange = { code = it })
      }
    }

    ExecutiveTextField(
      label = "تأمین‌کننده خرج‌کار (کلیک برای انتخاب یا ثبت ۷ گانه)",
      value = supplierName,
      onValueChange = { supplierName = it }
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد / موجودی", value = qtyText, keyboardType = KeyboardType.Number, onValueChange = { qtyText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("واحد سنجش:", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
            Text(
              text = "+ افزودن واحد",
              style = MaterialTheme.typography.labelSmall,
              color = AccentCyan,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.clickable { showAddUnitDialog = true }
            )
          }
          ExecutiveTextField(
            label = "واحد ($unitType)",
            value = unitType,
            onValueChange = { unitType = it }
          )
        }
      }
    }

    // Quick Unit Selector Pills
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      availableUnits.take(6).forEach { u ->
        val isSelected = unitType == u
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) com.example.ui.theme.AccentAmber.copy(alpha = 0.2f) else customColors.cardElevated)
            .border(1.dp, if (isSelected) com.example.ui.theme.AccentAmber else customColors.border, RoundedCornerShape(6.dp))
            .clickable { unitType = u }
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = u,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = if (isSelected) com.example.ui.theme.AccentAmber else customColors.textSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )
        }
      }
    }

    // Pricing
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت خرید واحد (تومان)", value = unitCostPriceText, keyboardType = KeyboardType.Number, onValueChange = { unitCostPriceText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت فروش/ارزیابی (تومان)", value = unitSalePriceText, keyboardType = KeyboardType.Number, onValueChange = { unitSalePriceText = it })
      }
    }

    // Price Update badge in RED if cost changed
    PriceUpdateBadge(
      currentPrice = cost,
      previousPrice = previousCostPrice,
      previousDate = previousCostDate,
      unitLabel = "خرید هر $unitType"
    )

    // Line Chart showing Stock and Usage over dates with 3 view toggles
    MiniTrendLineChart(
      title = "روند موجودی و مصرف $name در تاریخ‌های مختلف",
      dates = listOf("۱۵ اسفند", "۱۸ اسفند", "۲۲ اسفند", "۲۸ اسفند", "امروز"),
      stockValues = listOf(500.0, 1200.0, 900.0, 1800.0, qty.toDouble().coerceAtLeast(100.0)),
      salesValues = listOf(150.0, 300.0, 450.0, 200.0, 350.0)
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text("ارزش کل خرید ملزومات:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        Text(CurrencyHelper.formatToman(qty * cost), style = MaterialTheme.typography.bodySmall, color = com.example.ui.theme.AccentAmber, fontWeight = FontWeight.Bold)
      }
    }

    Button(
      onClick = {
        viewModel.submitAccessory(
          name = name,
          code = code,
          quantity = qty,
          unitSalePrice = sale,
          unitCostPrice = cost,
          unitType = unitType,
          supplierName = supplierName,
          metersPerKg = metersPerKg,
          pricePerMeter = pricePerMeter
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_accessory_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentAmber)
    ) {
      Text("تأیید و ثبت در انبار ملزومات", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 4. Fixed Costs & Margins Settings Form (باربری، حاشیه سود ثابت، سربار)
 */
@Composable
fun QuickSettingsForm(
  initialSettings: FactorySettingsEntity,
  onSubmit: (Long, Long, Double, Long, Long, String, String, String, Int, Double, Int, Double) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var shipOrderText by remember { mutableStateOf(initialSettings.fixedShippingCostPerOrder.toString()) }
  var shipRollText by remember { mutableStateOf(initialSettings.fixedShippingCostPerRoll.toString()) }
  var marginText by remember { mutableStateOf(initialSettings.targetProfitMarginPercent.toString()) }
  var overheadText by remember { mutableStateOf(initialSettings.overheadCostPerItem.toString()) }
  var defaultAccText by remember { mutableStateOf(initialSettings.defaultAccessoriesCost.toString()) }
  var companyName by remember { mutableStateOf(initialSettings.companyName) }

  // New chart and layout settings
  var selectedChartType by remember { mutableStateOf(initialSettings.dashboardChartType) }
  var selectedLayout by remember { mutableStateOf(initialSettings.dashboardLayout) }

  // New threshold settings
  var minFabricRollsText by remember { mutableStateOf(initialSettings.minFabricRollsThreshold.toString()) }
  var minFabricWeightText by remember { mutableStateOf(initialSettings.minFabricWeightKgThreshold.toInt().toString()) }
  var minReadyGoodsText by remember { mutableStateOf(initialSettings.minReadyGoodsCountThreshold.toString()) }
  var minAccessoriesWeightText by remember { mutableStateOf(initialSettings.minAccessoriesWeightKgThreshold.toInt().toString()) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("تنظیمات کارگاه، چیدمان و هشدارهای موجودی", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    ExecutiveTextField(label = "نام برند / مجموعه تولیدی", value = companyName, onValueChange = { companyName = it })

    // 1. Chart Type Selector (انتخاب نوع چارت آمارگیر صفحه اول)
    Text("نوع نمودار و چارت آمارگیر صفحه اول", style = MaterialTheme.typography.labelMedium, color = customColors.textSecondary, fontWeight = FontWeight.Bold)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      DashboardChartType.values().forEach { cType ->
        val isSelected = selectedChartType == cType.name
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AccentIndigo.copy(alpha = 0.2f) else customColors.card)
            .border(1.dp, if (isSelected) AccentIndigo else customColors.border, RoundedCornerShape(10.dp))
            .clickable { selectedChartType = cType.name }
            .padding(vertical = 10.dp, horizontal = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = cType.title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) AccentIndigo else customColors.textMuted,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
          )
        }
      }
    }

    // 2. Layout Arrangement Selector (انتخاب نوع چیدمان بخش‌ها در صفحات اول)
    Text("نوع چیدمان بخش‌ها در صفحه اصلی", style = MaterialTheme.typography.labelMedium, color = customColors.textSecondary, fontWeight = FontWeight.Bold)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      DashboardLayoutArrangement.values().forEach { lType ->
        val isSelected = selectedLayout == lType.name
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AccentBlue.copy(alpha = 0.2f) else customColors.card)
            .border(1.dp, if (isSelected) AccentBlue else customColors.border, RoundedCornerShape(10.dp))
            .clickable { selectedLayout = lType.name }
            .padding(vertical = 10.dp, horizontal = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = lType.title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) AccentBlue else customColors.textMuted,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
          )
        }
      }
    }

    HorizontalDivider(color = customColors.borderSubtle, thickness = 1.dp)

    // 3. Stock Alert Thresholds (حد آستانه هشدار کسری موجودی)
    Text("حد آستانه هشدار کسری و نوتیفیکیشن انبار", style = MaterialTheme.typography.labelMedium, color = customColors.textSecondary, fontWeight = FontWeight.Bold)

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "هشدار طاقه پارچه (تعداد طاقه)", value = minFabricRollsText, keyboardType = KeyboardType.Number, onValueChange = { minFabricRollsText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "هشدار وزن پارچه (کیلوگرم)", value = minFabricWeightText, keyboardType = KeyboardType.Number, onValueChange = { minFabricWeightText = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "هشدار کار آماده (تعداد)", value = minReadyGoodsText, keyboardType = KeyboardType.Number, onValueChange = { minReadyGoodsText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "هشدار ملزومات (وزن به کیلو)", value = minAccessoriesWeightText, keyboardType = KeyboardType.Number, onValueChange = { minAccessoriesWeightText = it })
      }
    }

    HorizontalDivider(color = customColors.borderSubtle, thickness = 1.dp)

    // 4. Fixed Costs (مبالغ ثابت باربری، سود و سربار)
    Text("مبالغ ثابت باربری و سربار", style = MaterialTheme.typography.labelMedium, color = customColors.textSecondary, fontWeight = FontWeight.Bold)

    // 📌 هزینه باربری به صورت خودکار از بارنامه‌های ثبت‌شده محاسبه می‌شود.
    // فیلدهای ثابت قبلی حذف شده‌اند (مقادیر پیش‌فرض در fallback استفاده می‌شوند).

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "حاشیه سود ثابت هدف (٪)", value = marginText, keyboardType = KeyboardType.Decimal, onValueChange = { marginText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "سربار هر کار (تومان)", value = overheadText, keyboardType = KeyboardType.Number, onValueChange = { overheadText = it })
      }
    }

    ExecutiveTextField(
      label = "هزینه پیش‌فرض ملزومات هر کار (تومان)",
      value = defaultAccText,
      keyboardType = KeyboardType.Number,
      onValueChange = { defaultAccText = it }
    )

    Button(
      onClick = {
        val sOrder = shipOrderText.toLongOrNull() ?: initialSettings.fixedShippingCostPerOrder
        val sRoll = shipRollText.toLongOrNull() ?: initialSettings.fixedShippingCostPerRoll
        val mPercent = marginText.toDoubleOrNull() ?: initialSettings.targetProfitMarginPercent
        val ovh = overheadText.toLongOrNull() ?: initialSettings.overheadCostPerItem
        val acc = defaultAccText.toLongOrNull() ?: initialSettings.defaultAccessoriesCost
        val mRolls = minFabricRollsText.toIntOrNull() ?: initialSettings.minFabricRollsThreshold
        val mWeight = minFabricWeightText.toDoubleOrNull() ?: initialSettings.minFabricWeightKgThreshold
        val mReady = minReadyGoodsText.toIntOrNull() ?: initialSettings.minReadyGoodsCountThreshold
        val mAccWeight = minAccessoriesWeightText.toDoubleOrNull() ?: initialSettings.minAccessoriesWeightKgThreshold

        onSubmit(sOrder, sRoll, mPercent, ovh, acc, companyName, selectedChartType, selectedLayout, mRolls, mWeight, mReady, mAccWeight)
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("save_settings_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
    ) {
      Text("ذخیره کلیه تنظیمات کارگاه", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 5. Quick Sale Form
 */
@Composable
fun QuickSaleForm(
  viewModel: ManufacturingViewModel,
  isPreOrder: Boolean,
  onSubmit: (String, String, String, String, Int, Long, Long, Long, Long) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val customers by viewModel.customers.collectAsStateWithLifecycle()
  val products by viewModel.products.collectAsStateWithLifecycle()
  var showCustomerPicker by remember { mutableStateOf(false) }
  var showProductPicker by remember { mutableStateOf(false) }

  var customerName by remember { mutableStateOf("بوتیک آریا (احمدی)") }
  var customerPhone by remember { mutableStateOf("09121234567") }
  var modelCode by remember { mutableStateOf("HD-204") }
  var modelName by remember { mutableStateOf("هودی دورس ۳ نخ خارخورده") }
  var quantityText by remember { mutableStateOf("50") }
  var unitPriceText by remember { mutableStateOf("680000") }
  var discountText by remember { mutableStateOf("500000") }
  var prePaymentText by remember { mutableStateOf("15000000") }
  var pricingMode by remember { mutableStateOf("UNIT") }
  var pricePerKgText by remember { mutableStateOf("450000") }
  var metersPerKgText by remember { mutableStateOf("3.0") }

  val qty = quantityText.toIntOrNull() ?: 0
  val effectiveUnitPrice = unitPriceText.toLongOrNull() ?: 0L
  val unitPrice = effectiveUnitPrice
  val discount = discountText.toLongOrNull() ?: 0L
  val prePaid = prePaymentText.toLongOrNull() ?: 0L

  val subtotal = qty * unitPrice
  val netTotal = (subtotal - discount).coerceAtLeast(0L)
  val remainingDebt = (netTotal - prePaid).coerceAtLeast(0L)
  val unitCost = 425000L
  val totalCost = qty * unitCost
  val profit = (netTotal - totalCost).coerceAtLeast(0L)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (isPreOrder) "ثبت سفارش جدید مشتری" else "ثبت فاکتور فروش فوری",
        style = MaterialTheme.typography.titleMedium,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )
      IconButton(onClick = onBack) {
        Icon(Icons.Default.Close, contentDescription = "بازگشت", tint = customColors.textMuted)
      }
    }

    // Customer Picker
    Button(
      onClick = { showCustomerPicker = true },
      modifier = Modifier.fillMaxWidth().height(44.dp),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentPurple.copy(alpha = 0.18f))
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = com.example.ui.theme.AccentPurple, modifier = Modifier.size(18.dp))
        Text(
          text = if (customerName.isNotBlank())
            "مشتری: $customerName - برای تغییر کلیک کنید"
          else
            "انتخاب مشتری از لیست یا ثبت مشتری جدید",
          color = com.example.ui.theme.AccentPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold
        )
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.2f)) {
        ExecutiveTextField(label = "نام خریدار / فروشگاه", value = customerName, onValueChange = { customerName = it })
      }
      Box(modifier = Modifier.weight(0.8f)) {
        ExecutiveTextField(label = "تلفن", value = customerPhone, keyboardType = KeyboardType.Phone, onValueChange = { customerPhone = it })
      }
    }

    // Product Picker for Sale
    Button(
      onClick = { showProductPicker = true },
      modifier = Modifier.fillMaxWidth().height(44.dp),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo.copy(alpha = 0.18f))
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Search, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(18.dp))
        Text(
          text = if (modelName.isNotBlank())
            "محصول: $modelName ($modelCode) - برای تغییر کلیک کنید"
          else
            "انتخاب محصول از لیست کالاها",
          color = AccentIndigo, fontSize = 12.sp, fontWeight = FontWeight.Bold
        )
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.4f)) {
        ExecutiveTextField(label = "مدل محصول", value = modelName, onValueChange = { modelName = it })
      }
      Box(modifier = Modifier.weight(0.6f)) {
        ExecutiveTextField(label = "کد", value = modelCode, onValueChange = { modelCode = it })
      }
    }

    // Customer Picker Dialog
    if (showCustomerPicker) {
      ItemSelectionPopupDialog(
        title = "انتخاب مشتری از لیست",
        items = customers,
        onDismiss = { showCustomerPicker = false },
        onAddNew = {
          customerName = ""
          customerPhone = ""
          showCustomerPicker = false
        },
        onItemSelected = { cust ->
          customerName = cust.name
          customerPhone = cust.phone
          showCustomerPicker = false
        },
        itemLabel = { it.name },
        itemCode = { it.phone },
        itemSecondary = { "شرکت: ${it.company} | بدهی: ${CurrencyHelper.formatToman(it.currentDebt)}" },
        itemPrice = { it.totalPurchases }
      )
    }

    // Product Picker Dialog
    if (showProductPicker) {
      ItemSelectionPopupDialog(
        title = "انتخاب محصول برای فروش",
        items = products,
        onDismiss = { showProductPicker = false },
        onAddNew = { showProductPicker = false },
        onItemSelected = { p ->
          modelName = p.name
          modelCode = p.code
          unitPriceText = p.effectiveSellingPrice.toString()
          showProductPicker = false
        },
        itemLabel = { it.name },
        itemCode = { it.code },
        itemSecondary = { "دسته: ${it.categoryName} | قیمت فروش: ${CurrencyHelper.formatToman(it.effectiveSellingPrice)}" },
        itemPrice = { it.effectiveSellingPrice }
      )
    }

    // Pricing Mode Toggle (Requirement 12: قیمت بر اساس متر / عدد vs کیلوگرم)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(customColors.secondaryBg)
        .padding(4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(6.dp))
          .background(if (pricingMode == "UNIT") AccentIndigo else Color.Transparent)
          .clickable { pricingMode = "UNIT" }
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text("قیمت بر اساس متر / عدد", fontSize = 11.sp, color = if (pricingMode == "UNIT") Color.White else customColors.textSecondary, fontWeight = FontWeight.Bold)
      }
      Box(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(6.dp))
          .background(if (pricingMode == "KG") AccentIndigo else Color.Transparent)
          .clickable { pricingMode = "KG" }
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text("قیمت بر اساس کیلوگرم (محاسبه خودکار متر)", fontSize = 11.sp, color = if (pricingMode == "KG") Color.White else customColors.textSecondary, fontWeight = FontWeight.Bold)
      }
    }

    if (pricingMode == "KG") {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.weight(1f)) {
          ExecutiveTextField(
            label = "قیمت هر کیلوگرم (تومان)",
            value = pricePerKgText,
            keyboardType = KeyboardType.Number,
            onValueChange = {
              pricePerKgText = it
              val pkg = it.toLongOrNull() ?: 0L
              val mpk = metersPerKgText.toDoubleOrNull() ?: 3.0
              val pm = FinancialCalculationService.calculatePricePerMeter(pkg, mpk)
              unitPriceText = pm.toString()
            }
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          ExecutiveTextField(
            label = "متراژ در هر کیلوگرم",
            value = metersPerKgText,
            keyboardType = KeyboardType.Decimal,
            onValueChange = {
              metersPerKgText = it
              val pkg = pricePerKgText.toLongOrNull() ?: 0L
              val mpk = it.toDoubleOrNull() ?: 3.0
              val pm = FinancialCalculationService.calculatePricePerMeter(pkg, mpk)
              unitPriceText = pm.toString()
            }
          )
        }
      }
      Text(
        text = "💡 قیمت محاسبه‌شده هر متر بر اساس وزن: ${CurrencyHelper.formatToman(effectiveUnitPrice)}",
        style = MaterialTheme.typography.labelSmall,
        color = StatusSuccess,
        fontWeight = FontWeight.Bold
      )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد / متراژ کل", value = quantityText, keyboardType = KeyboardType.Number, onValueChange = { quantityText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = if (pricingMode == "KG") "قیمت واحد محاسبه‌شده (تومان)" else "قیمت واحد (تومان)",
          value = unitPriceText,
          keyboardType = KeyboardType.Number,
          onValueChange = { unitPriceText = it }
        )
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تخفیف کل (تومان)", value = discountText, keyboardType = KeyboardType.Number, onValueChange = { discountText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "مبلغ بیعانه / دریافتی", value = prePaymentText, keyboardType = KeyboardType.Number, onValueChange = { prePaymentText = it })
      }
    }

    // Calculations Box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("مبلغ خالص فاکتور:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(netTotal), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("مانده بدهی مشتری:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(remainingDebt), style = MaterialTheme.typography.bodySmall, color = if (remainingDebt > 0) StatusWarning else StatusSuccess, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("سود پیش‌بینی شده:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(profit), style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }
    }

    Button(
      onClick = {
        onSubmit(customerName, customerPhone, modelCode, modelName, qty, unitPrice, discount, prePaid, totalCost)
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_sale_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
    ) {
      Text(if (isPreOrder) "تأیید و صدور سفارش" else "تأیید و صدور فاکتور نهایی", fontWeight = FontWeight.Bold)
    }
  }
}

data class ConsumableInputDraft(
  val id: String = java.util.UUID.randomUUID().toString(),
  var accessoryCode: String = "ZIP-01",
  var accessoryName: String = "زیپ دنده‌فلزی ۵۰ سانتی",
  var quantityUsedText: String = "100",
  var unit: String = "عدد",
  var unitCostPrice: Long = 18000L
)

/**
 * 6. Quick Production Form (ثبت تولید با مصرف طاقه، ملزومات واقعی، و هزینه خیاط‌کار)
 */
@Composable
fun QuickProductionForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val availableRolls by viewModel.availableFabricRolls.collectAsStateWithLifecycle()
  val inventoryItems by viewModel.inventory.collectAsStateWithLifecycle()
  val accessoriesList = remember(inventoryItems) {
    inventoryItems.filter { it.category == "ملزومات" || it.unitCostPrice > 0L }
  }

  var modelName by remember { mutableStateOf("هودی کلاه‌دار پاییزه") }
  var modelCode by remember { mutableStateOf("HD-204") }
  var quantityText by remember { mutableStateOf("100") }
  var selectedRollId by remember { mutableStateOf(availableRolls.firstOrNull()?.id ?: 0L) }
  val selectedRoll = availableRolls.find { it.id == selectedRollId } ?: availableRolls.firstOrNull()
  var metersText by remember { mutableStateOf("185.0") }
  var weightKgText by remember { mutableStateOf("72.0") }
  var wagePerItemText by remember { mutableStateOf("85000") } // هزینه خیاط‌کار
  var rollDropdownExpanded by remember { mutableStateOf(false) }

  // Consumables list (ملزوم + مقدار مصرف + واحد + محاسبه خودکار هزینه واقعی از سابقه خرید)
  val consumables = remember {
    mutableStateListOf(
      ConsumableInputDraft(
        accessoryCode = "ZIP-01",
        accessoryName = "زیپ دنده‌فلزی ۵۰ سانت",
        quantityUsedText = "100",
        unit = "عدد",
        unitCostPrice = 18000L
      ),
      ConsumableInputDraft(
        accessoryCode = "THR-40",
        accessoryName = "نخ دوک پلی‌استر ۴۰/۲",
        quantityUsedText = "4",
        unit = "دوک",
        unitCostPrice = 45000L
      )
    )
  }

  var pickerConsumableIndex by remember { mutableStateOf<Int?>(null) }

  if (pickerConsumableIndex != null) {
    val targetIdx = pickerConsumableIndex!!
    ItemSelectionPopupDialog(
      title = "انتخاب ملزوم از انبار و سابقه خرید",
      items = accessoriesList,
      onDismiss = { pickerConsumableIndex = null },
      onAddNew = { pickerConsumableIndex = null },
      onItemSelected = { selected ->
        if (targetIdx in consumables.indices) {
          consumables[targetIdx] = consumables[targetIdx].copy(
            accessoryCode = selected.code,
            accessoryName = selected.name,
            unit = if (selected.unitType.isNotBlank()) selected.unitType else "عدد",
            unitCostPrice = selected.unitCostPrice
          )
        }
        pickerConsumableIndex = null
      },
      itemLabel = { it.name },
      itemCode = { it.code },
      itemSecondary = { "موجودی: ${it.availableForSale} ${it.unitType} | قیمت خرید واقعی: ${CurrencyHelper.formatToman(it.unitCostPrice)}" },
      itemPrice = { it.unitCostPrice }
    )
  }

  val qty = quantityText.toIntOrNull() ?: 0
  val meters = metersText.toDoubleOrNull() ?: 0.0
  val weightKg = weightKgText.toDoubleOrNull() ?: 0.0
  val tailorCostPerItem = wagePerItemText.toLongOrNull() ?: 0L // هزینه خیاط‌کار
  val rollBuyPrice = selectedRoll?.buyPricePerMeter ?: 210000L

  val totalConsumablesCost = consumables.sumOf {
    val q = it.quantityUsedText.toDoubleOrNull() ?: 0.0
    (q * it.unitCostPrice).toLong()
  }
  val consumablesCostPerItem = if (qty > 0) totalConsumablesCost / qty else 0L

  val fabricCostTotal = (meters * rollBuyPrice).toLong()
  val fabricCostPerItem = if (qty > 0) fabricCostTotal / qty else 0L
  val detailedUnitCost = fabricCostPerItem + tailorCostPerItem + consumablesCostPerItem

  val rollRemaining = selectedRoll?.remainingMeters ?: 0.0
  val isRollCapacityValid = selectedRoll == null || FinancialCalculationService.validateRollCapacity(rollRemaining, meters)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "ثبت تولید بچ کارگاه (با مصرف ملزومات و خیاط‌کار)",
        style = MaterialTheme.typography.titleMedium,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )
      IconButton(onClick = onBack) {
        Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted)
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.4f)) {
        ExecutiveTextField(label = "نام مدل کارگاه", value = modelName, onValueChange = { modelName = it })
      }
      Box(modifier = Modifier.weight(0.7f)) {
        ExecutiveTextField(label = "کد مدل", value = modelCode, onValueChange = { modelCode = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد تولید (عدد)", value = quantityText, keyboardType = KeyboardType.Number, onValueChange = { quantityText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "وزن کل بچ (کیلوگرم)", value = weightKgText, keyboardType = KeyboardType.Decimal, onValueChange = { weightKgText = it })
      }
    }

    // Roll Selection ("از طاقه")
    if (availableRolls.isNotEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(customColors.secondaryBg)
          .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
          .padding(10.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("تخصیص از طاقه پارچه (فقط طاقه‌های موجود)", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(customColors.card)
              .clickable { rollDropdownExpanded = !rollDropdownExpanded }
              .padding(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "طاقه: ${selectedRoll?.rollCode ?: "بدون انتخاب"} (${selectedRoll?.fabricType ?: ""}) - موجودی: ${selectedRoll?.remainingMeters ?: 0.0} متر",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = customColors.textPrimary)
            }
          }

          if (rollDropdownExpanded) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              availableRolls.forEach { roll ->
                Text(
                  text = "${roll.rollCode} (${roll.fabricType}) - موجودی: ${roll.remainingMeters} متر",
                  style = MaterialTheme.typography.labelSmall,
                  color = AccentIndigo,
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      selectedRollId = roll.id
                      rollDropdownExpanded = false
                    }
                    .padding(4.dp)
                )
              }
            }
          }
        }
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "متراژ مصرفی پارچه (متر)",
          value = metersText,
          keyboardType = KeyboardType.Decimal,
          onValueChange = { metersText = it }
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        // Requirement 8: هزینه سربار -> هزینه خیاط‌کار
        ExecutiveTextField(
          label = "هزینه خیاط‌کار هر کار (تومان)",
          value = wagePerItemText,
          keyboardType = KeyboardType.Number,
          onValueChange = { wagePerItemText = it }
        )
      }
    }

    if (!isRollCapacityValid) {
      Text(
        text = "⚠️ متراژ مصرفی ($meters متر) از موجودی طاقه (${rollRemaining} متر) بیشتر است!",
        style = MaterialTheme.typography.labelSmall,
        color = StatusDanger
      )
    }

    // SECTION 4: CONSUMABLES LIST (مصرف ملزومات: ملزوم + مقدار مصرف + واحد + محاسبه خودکار هزینه)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "ملزومات و خرج‌کار مصرفی در تولید",
        style = MaterialTheme.typography.labelMedium,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      Button(
        onClick = {
          consumables.add(
            ConsumableInputDraft(
              accessoryCode = "ACC-${consumables.size + 1}",
              accessoryName = "ملزوم جدید ${consumables.size + 1}",
              quantityUsedText = "50",
              unit = "عدد",
              unitCostPrice = 25000L
            )
          )
        },
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentAmber),
        modifier = Modifier.height(30.dp)
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Add, contentDescription = "افزودن ملزوم", modifier = Modifier.size(14.dp))
          Text("افزودن ملزوم (+)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
      }
    }

    consumables.forEachIndexed { cIdx, cons ->
      val consQty = cons.quantityUsedText.toDoubleOrNull() ?: 0.0
      val lineCost = (consQty * cons.unitCostPrice).toLong()

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(customColors.secondaryBg)
          .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
          .padding(8.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${cIdx + 1}. ${cons.accessoryName} (${cons.accessoryCode})",
              style = MaterialTheme.typography.bodySmall,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
              Button(
                onClick = { pickerConsumableIndex = cIdx },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue.copy(alpha = 0.15f)),
                modifier = Modifier.height(24.dp)
              ) {
                Text("انتخاب از سابقه خرید", color = AccentBlue, fontSize = 9.sp)
              }
              if (consumables.size > 1) {
                IconButton(onClick = { consumables.removeAt(cIdx) }, modifier = Modifier.size(24.dp)) {
                  Icon(Icons.Default.Remove, contentDescription = "حذف", tint = StatusDanger, modifier = Modifier.size(14.dp))
                }
              }
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.weight(1f)) {
              ExecutiveTextField(
                label = "مقدار مصرف",
                value = cons.quantityUsedText,
                keyboardType = KeyboardType.Decimal,
                onValueChange = { cons.quantityUsedText = it }
              )
            }
            Box(modifier = Modifier.weight(0.7f)) {
              ExecutiveTextField(
                label = "واحد",
                value = cons.unit,
                onValueChange = { cons.unit = it }
              )
            }
            Box(modifier = Modifier.weight(1.3f)) {
              ExecutiveTextField(
                label = "قیمت خرید واحد (تومان)",
                value = cons.unitCostPrice.toString(),
                keyboardType = KeyboardType.Number,
                onValueChange = { cons.unitCostPrice = it.toLongOrNull() ?: 0L }
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("هزینه کل این قلم ملزوم:", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
            Text(CurrencyHelper.formatToman(lineCost), style = MaterialTheme.typography.labelSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // COST BREAKDOWN & CENTRAL CALCULATION CARD (Requirement 10 & 11)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("هزینه پارچه به ازای هر کار:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(fabricCostPerItem), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("هزینه خیاط‌کار هر کار:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(tailorCostPerItem), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("هزینه ملزومات هر کار:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(consumablesCostPerItem), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(color = customColors.border)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("بهای تمام‌شده هر کار (تومان):", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text(CurrencyHelper.formatToman(detailedUnitCost), style = MaterialTheme.typography.bodyMedium, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }
    }

    Button(
      onClick = {
        val inputConsumables = consumables.map { draft ->
          ProductionConsumableInputItem(
            accessoryCode = draft.accessoryCode,
            accessoryName = draft.accessoryName,
            quantityUsed = draft.quantityUsedText.toDoubleOrNull() ?: 0.0,
            unit = draft.unit,
            unitCostPrice = draft.unitCostPrice
          )
        }
        viewModel.submitProductionWithConsumables(
          modelCode = modelCode,
          modelName = modelName,
          quantity = qty,
          rollId = selectedRoll?.id,
          rollCode = selectedRoll?.rollCode ?: "",
          fabricMetersUsed = meters,
          sewingWagePerItem = tailorCostPerItem,
          consumables = inputConsumables,
          note = "تولید بچ با ${inputConsumables.size} قلم ملزومات"
        )
      },
      enabled = qty > 0 && isRollCapacityValid,
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_production_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
    ) {
      Text("تأیید تولید، کسر از طاقه و انبارش کالا", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 7. Cutting Operation Form
 */
@Composable
fun QuickCuttingForm(
  onSubmit: (String, String, String, Int, Int, Double, Double) -> Unit,
  onOpenMultiCut: () -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var modelName by remember { mutableStateOf("شلوار اسلش پنبه") }
  var modelCode by remember { mutableStateOf("SH-310") }
  var fabricCode by remember { mutableStateOf("M310") }
  var targetQuantityText by remember { mutableStateOf("300") }
  var cutQuantityText by remember { mutableStateOf("290") }
  var standardMetersText by remember { mutableStateOf("1.20") }
  var actualMetersText by remember { mutableStateOf("1.24") }

  val target = targetQuantityText.toIntOrNull() ?: 0
  val cut = cutQuantityText.toIntOrNull() ?: 0
  val std = standardMetersText.toDoubleOrNull() ?: 0.0
  val act = actualMetersText.toDoubleOrNull() ?: 0.0

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ثبت عملیات برشکاری", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    // Banner Button for Multi-Model Cutting from a single fabric roll
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(AccentIndigo.copy(alpha = 0.12f))
        .border(1.dp, AccentIndigo.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
        .clickable(onClick = onOpenMultiCut)
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(Icons.Default.ContentCut, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(24.dp))
          Column {
            Text("برش چند مدلی از یک طاقه پارچه", style = MaterialTheme.typography.labelLarge, color = AccentIndigo, fontWeight = FontWeight.Bold)
            Text("تعیین قد و تعداد کار برای چند مدل مختلف از یک طاقه", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted, fontSize = 11.sp)
          }
        }
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(18.dp))
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.5f)) {
        ExecutiveTextField(label = "مدل کار", value = modelName, onValueChange = { modelName = it })
      }
      Box(modifier = Modifier.weight(0.7f)) {
        ExecutiveTextField(label = "کد مدل", value = modelCode, onValueChange = { modelCode = it })
      }
    }

    ExecutiveTextField(label = "کد پارچه مصرفی", value = fabricCode, onValueChange = { fabricCode = it })

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد هدف سفارش", value = targetQuantityText, keyboardType = KeyboardType.Number, onValueChange = { targetQuantityText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد برش واقعی", value = cutQuantityText, keyboardType = KeyboardType.Number, onValueChange = { cutQuantityText = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "استاندارد مصرف (متر)", value = standardMetersText, keyboardType = KeyboardType.Decimal, onValueChange = { standardMetersText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "مصرف واقعی هر کار (متر)", value = actualMetersText, keyboardType = KeyboardType.Decimal, onValueChange = { actualMetersText = it })
      }
    }

    Button(
      onClick = { onSubmit(modelCode, modelName, fabricCode, target, cut, std, act) },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_cutting_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = StatusWarning)
    ) {
      Text("ثبت عملیات برش", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 8. Customer Form
 */
@Composable
fun QuickCustomerForm(
  onSubmit: (String, String, String, String, String) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var name by remember { mutableStateOf("") }
  var company by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("عمده‌فروش") }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ثبت مشتری جدید", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    ExecutiveTextField(label = "نام مسئول خرید", value = name, onValueChange = { name = it })
    ExecutiveTextField(label = "نام شرکت / فروشگاه", value = company, onValueChange = { company = it })
    ExecutiveTextField(label = "شماره تماس", value = phone, keyboardType = KeyboardType.Phone, onValueChange = { phone = it })
    ExecutiveTextField(label = "آدرس", value = address, onValueChange = { address = it })
    ExecutiveTextField(label = "رده خریدار (عمده‌فروش / آنلاین / بوتیک)", value = category, onValueChange = { category = it })

    Button(
      onClick = {
        if (name.isNotBlank()) {
          onSubmit(name, company, phone, address, category)
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_customer_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentPurple)
    ) {
      Text("ایجاد پرونده مشتری", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * 9. Edit Fabric Dialog (کد جوری بنویس که قابل ادیت کردن باشه)
 */
@Composable
fun EditFabricForm(
  fabric: FabricEntity,
  onUpdate: (FabricEntity) -> Unit,
  onDelete: (Long) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var name by remember { mutableStateOf(fabric.name) }
  var color by remember { mutableStateOf(fabric.color) }
  var rollsText by remember { mutableStateOf(fabric.rollCount.toString()) }
  var metersText by remember { mutableStateOf(fabric.totalMeters.toString()) }
  var weightKgText by remember { mutableStateOf(fabric.totalWeightKg.toString()) }
  var priceMeterText by remember { mutableStateOf(fabric.buyPricePerMeter.toString()) }
  var priceKgText by remember { mutableStateOf(fabric.buyPricePerKg.toString()) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ویرایش اطلاعات طاقه پارچه", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بستن", tint = customColors.textMuted) }
    }

    ExecutiveTextField(label = "نام پارچه", value = name, onValueChange = { name = it })
    ExecutiveTextField(label = "رنگ", value = color, onValueChange = { color = it })

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد طاقه", value = rollsText, keyboardType = KeyboardType.Number, onValueChange = { rollsText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "متراژ کل (متر)", value = metersText, keyboardType = KeyboardType.Decimal, onValueChange = { metersText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "وزن کل (کیلو)", value = weightKgText, keyboardType = KeyboardType.Decimal, onValueChange = { weightKgText = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت متر (تومان)", value = priceMeterText, keyboardType = KeyboardType.Number, onValueChange = { priceMeterText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت کیلو (تومان)", value = priceKgText, keyboardType = KeyboardType.Number, onValueChange = { priceKgText = it })
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = {
          val updated = fabric.copy(
            name = name,
            color = color,
            rollCount = rollsText.toIntOrNull() ?: fabric.rollCount,
            totalMeters = metersText.toDoubleOrNull() ?: fabric.totalMeters,
            totalWeightKg = weightKgText.toDoubleOrNull() ?: fabric.totalWeightKg,
            buyPricePerMeter = priceMeterText.toLongOrNull() ?: fabric.buyPricePerMeter,
            buyPricePerKg = priceKgText.toLongOrNull() ?: fabric.buyPricePerKg
          )
          onUpdate(updated)
        },
        modifier = Modifier.weight(1f).height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
      ) {
        Text("ذخیره تغییرات", fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = { onDelete(fabric.id) },
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
      ) {
        Icon(Icons.Default.Delete, contentDescription = "حذف")
      }
    }
  }
}

/**
 * 10. Edit Inventory Dialog
 */
@Composable
fun EditInventoryForm(
  item: InventoryEntity,
  onUpdate: (InventoryEntity) -> Unit,
  onDelete: (Long) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var name by remember { mutableStateOf(item.name) }
  var readyCountText by remember { mutableStateOf(item.readyForShipment.toString()) }
  var availableCountText by remember { mutableStateOf(item.availableForSale.toString()) }
  var salePriceText by remember { mutableStateOf(item.unitSalePrice.toString()) }
  var costPriceText by remember { mutableStateOf(item.unitCostPrice.toString()) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ویرایش قلم انبار (${item.code})", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بستن", tint = customColors.textMuted) }
    }

    ExecutiveTextField(label = "نام کالا", value = name, onValueChange = { name = it })

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "آماده ارسال", value = readyCountText, keyboardType = KeyboardType.Number, onValueChange = { readyCountText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قابل فروش", value = availableCountText, keyboardType = KeyboardType.Number, onValueChange = { availableCountText = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "قیمت فروش (تومان)", value = salePriceText, keyboardType = KeyboardType.Number, onValueChange = { salePriceText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "بهای تمام‌شده (تومان)", value = costPriceText, keyboardType = KeyboardType.Number, onValueChange = { costPriceText = it })
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = {
          val updated = item.copy(
            name = name,
            readyForShipment = readyCountText.toIntOrNull() ?: item.readyForShipment,
            availableForSale = availableCountText.toIntOrNull() ?: item.availableForSale,
            unitSalePrice = salePriceText.toLongOrNull() ?: item.unitSalePrice,
            unitCostPrice = costPriceText.toLongOrNull() ?: item.unitCostPrice
          )
          onUpdate(updated)
        },
        modifier = Modifier.weight(1f).height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
      ) {
        Text("ذخیره تغییرات", fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = { onDelete(item.id) },
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
      ) {
        Icon(Icons.Default.Delete, contentDescription = "حذف")
      }
    }
  }
}

/**
 * 11. Edit Order Form
 */
@Composable
fun EditOrderForm(
  order: SaleOrderEntity,
  onUpdate: (SaleOrderEntity) -> Unit,
  onDelete: (SaleOrderEntity) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var deliveryStatus by remember { mutableStateOf(order.deliveryStatus) }
  var paidAmountText by remember { mutableStateOf(order.paidAmount.toString()) }
  var discountText by remember { mutableStateOf(order.discountAmount.toString()) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ویرایش سفارش ${order.orderNumber}", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بستن", tint = customColors.textMuted) }
    }

    ExecutiveTextField(label = "وضعیت سفارش (ثبت شده / در حال دوخت / آماده ارسال / تحویل شده)", value = deliveryStatus, onValueChange = { deliveryStatus = it })
    ExecutiveTextField(label = "مبلغ پرداختی مشتری (تومان)", value = paidAmountText, keyboardType = KeyboardType.Number, onValueChange = { paidAmountText = it })
    ExecutiveTextField(label = "تخفیف (تومان)", value = discountText, keyboardType = KeyboardType.Number, onValueChange = { discountText = it })

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = {
          val paid = paidAmountText.toLongOrNull() ?: order.paidAmount
          val disc = discountText.toLongOrNull() ?: order.discountAmount
          val updated = order.copy(
            deliveryStatus = deliveryStatus,
            paidAmount = paid,
            discountAmount = disc
          )
          onUpdate(updated)
        },
        modifier = Modifier.weight(1f).height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
      ) {
        Text("ثبت اصلاحات فاکتور", fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = { onDelete(order) },
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
      ) {
        Icon(Icons.Default.Delete, contentDescription = "حذف")
      }
    }
  }
}

/**
 * 12. Edit Customer Form
 */
@Composable
fun EditCustomerForm(
  customer: CustomerEntity,
  onUpdate: (CustomerEntity) -> Unit,
  onDelete: (CustomerEntity) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var phone by remember { mutableStateOf(customer.phone) }
  var address by remember { mutableStateOf(customer.address) }
  var tier by remember { mutableStateOf(customer.tier) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("ویرایش پرونده ${customer.name}", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بستن", tint = customColors.textMuted) }
    }

    ExecutiveTextField(label = "شماره تماس", value = phone, keyboardType = KeyboardType.Phone, onValueChange = { phone = it })
    ExecutiveTextField(label = "آدرس", value = address, onValueChange = { address = it })
    ExecutiveTextField(label = "رده مشتری (خرید اول / نقدی / اعتباری / VIP)", value = tier, onValueChange = { tier = it })

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = {
          val updated = customer.copy(
            phone = phone,
            address = address,
            tier = tier
          )
          onUpdate(updated)
        },
        modifier = Modifier.weight(1f).height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
      ) {
        Text("ذخیره پرونده", fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = { onDelete(customer) },
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
      ) {
        Icon(Icons.Default.Delete, contentDescription = "حذف")
      }
    }
  }
}

@Composable
fun ExecutiveTextField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  keyboardType: KeyboardType = KeyboardType.Text,
  modifier: Modifier = Modifier,
  leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
  val customColors = LocalCustomColors.current

  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
    leadingIcon = if (leadingIcon != null) {
      { Icon(leadingIcon, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp)) }
    } else null,
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    singleLine = true,
    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    colors = OutlinedTextFieldDefaults.colors(
      focusedTextColor = customColors.textPrimary,
      unfocusedTextColor = customColors.textPrimary,
      focusedContainerColor = customColors.secondaryBg,
      unfocusedContainerColor = customColors.secondaryBg,
      focusedBorderColor = AccentBlue,
      unfocusedBorderColor = customColors.border,
      focusedLabelColor = AccentBlue,
      unfocusedLabelColor = customColors.textMuted
    )
  )
}

/**
 * Multi-Model Cutting Sheet (پاپ‌آپ بازشونده برش چندین مدل مختلف از یک طاقه پارچه)
 */
@Composable
fun MultiModelCuttingSheet(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit,
  onClose: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val fabrics by viewModel.fabrics.collectAsStateWithLifecycle()
  val context = androidx.compose.ui.platform.LocalContext.current

  // Selected fabric
  var selectedFabricId by remember(fabrics) {
    mutableStateOf(fabrics.firstOrNull()?.id ?: 0L)
  }
  val currentFabric = fabrics.find { it.id == selectedFabricId } ?: fabrics.firstOrNull()

  // Dynamic list of models to cut from this fabric roll
  data class CutItemState(
    var modelName: String,
    var modelCode: String,
    var heightCm: String,
    var meterPerItem: String,
    var count: String
  )

  var modelItems by remember {
    mutableStateOf(
      listOf(
        CutItemState("تیشرت لانگ اوورسایز", "TS-102", "75", "1.10", "40"),
        CutItemState("شلوارک ست راحتی", "SH-102", "50", "0.75", "40")
      )
    )
  }

  var keepRemainingInStock by remember { mutableStateOf(true) }

  // Total required meters calculation
  val totalRequiredMeters = modelItems.sumOf { item ->
    val cnt = item.count.toIntOrNull() ?: 0
    val m = item.meterPerItem.toDoubleOrNull() ?: 0.0
    cnt * m
  }

  val availableMeters = currentFabric?.totalMeters ?: 0.0
  val remainingMeters = availableMeters - totalRequiredMeters
  val isOverCapacity = remainingMeters < 0.0

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = "پاپ‌آپ برش چند مدلی از یک طاقه",
          style = MaterialTheme.typography.titleMedium,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "استخراج همزمان چندین مدل مختلف از یک طاقه پارچه با ثبت قد و تعداد",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textMuted,
          fontSize = 11.sp
        )
      }
      IconButton(onClick = onClose) {
        Icon(Icons.Default.Close, contentDescription = "بستن", tint = customColors.textMuted)
      }
    }

    // 1. Fabric Selection Picker
    Text(
      text = "انتخاب طاقه پارچه مبدا برش:",
      style = MaterialTheme.typography.labelMedium,
      color = AccentIndigo,
      fontWeight = FontWeight.Bold
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      fabrics.take(4).forEach { fab ->
        val isSelected = (fab.id == currentFabric?.id)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AccentIndigo.copy(alpha = 0.15f) else customColors.secondaryBg)
            .border(1.dp, if (isSelected) AccentIndigo else customColors.border, RoundedCornerShape(10.dp))
            .clickable { selectedFabricId = fab.id }
            .padding(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${fab.name} (${fab.color}) - کد: ${fab.code}",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "موجودی: ${fab.totalMeters.toInt()} متر | ${fab.totalWeightKg.toInt()} کیلو (${fab.rollCount} طاقه)",
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
            }
            if (isSelected) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(20.dp))
            }
          }
        }
      }
    }

    HorizontalDivider(color = customColors.border, thickness = 1.dp)

    // 2. Models cut from this roll
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "مدل‌های برش‌خورده از این طاقه:",
        style = MaterialTheme.typography.labelMedium,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )

      Button(
        onClick = {
          modelItems = modelItems + CutItemState("مدل جدید", "M-${modelItems.size + 1}", "70", "1.00", "20")
        },
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("+ افزودن مدل دیگر", style = MaterialTheme.typography.labelSmall)
      }
    }

    modelItems.forEachIndexed { index, item ->
      val itemMeters = (item.count.toIntOrNull() ?: 0) * (item.meterPerItem.toDoubleOrNull() ?: 0.0)

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.secondaryBg)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .padding(12.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "مدل شماره ${index + 1}: ${item.modelName}",
              style = MaterialTheme.typography.labelMedium,
              color = AccentIndigo,
              fontWeight = FontWeight.Bold
            )
            if (modelItems.size > 1) {
              ManagementDeleteButton(onClick = {                  modelItems = modelItems.toMutableList().also { it.removeAt(index) }                })
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1.4f)) {
              ExecutiveTextField(
                label = "نام مدل",
                value = item.modelName,
                onValueChange = { newV ->
                  modelItems = modelItems.toMutableList().also { it[index] = it[index].copy(modelName = newV) }
                }
              )
            }
            Box(modifier = Modifier.weight(0.8f)) {
              ExecutiveTextField(
                label = "کد مدل",
                value = item.modelCode,
                onValueChange = { newV ->
                  modelItems = modelItems.toMutableList().also { it[index] = it[index].copy(modelCode = newV) }
                }
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
              ExecutiveTextField(
                label = "قد کار (سانتی‌متر)",
                value = item.heightCm,
                keyboardType = KeyboardType.Number,
                onValueChange = { newV ->
                  modelItems = modelItems.toMutableList().also { it[index] = it[index].copy(heightCm = newV) }
                }
              )
            }
            Box(modifier = Modifier.weight(1f)) {
              ExecutiveTextField(
                label = "مصرف هر کار (متر)",
                value = item.meterPerItem,
                keyboardType = KeyboardType.Decimal,
                onValueChange = { newV ->
                  modelItems = modelItems.toMutableList().also { it[index] = it[index].copy(meterPerItem = newV) }
                }
              )
            }
            Box(modifier = Modifier.weight(1f)) {
              ExecutiveTextField(
                label = "تعداد کار (عدد)",
                value = item.count,
                keyboardType = KeyboardType.Number,
                onValueChange = { newV ->
                  modelItems = modelItems.toMutableList().also { it[index] = it[index].copy(count = newV) }
                }
              )
            }
          }

          Text(
            text = "متراژ پارچه مصرفی این مدل: ${String.format(java.util.Locale.US, "%.1f", itemMeters)} متر",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textMuted
          )
        }
      }
    }

    // 3. Live Balance & Remaining Fabric Calculator Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(if (isOverCapacity) StatusDanger.copy(alpha = 0.1f) else customColors.secondaryBg)
        .border(1.dp, if (isOverCapacity) StatusDanger else customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("متراژ کل موجود در طاقه:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text("${availableMeters.toInt()} متر", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("مجموع متراژ مورد نیاز مدل‌ها:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text("${String.format(java.util.Locale.US, "%.1f", totalRequiredMeters)} متر", style = MaterialTheme.typography.bodySmall, color = AccentIndigo, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("باقی‌مانده پارچه در طاقه:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(
            text = if (isOverCapacity) "کسری متراژ: ${String.format(java.util.Locale.US, "%.1f", -remainingMeters)} متر!" else "${UnitFormatter.shortMeters(remainingMeters)} متر موجودی باقی‌مانده",
            style = MaterialTheme.typography.bodySmall,
            color = if (isOverCapacity) StatusDanger else StatusSuccess,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // 4. Keep remaining fabric switch
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "نگهداری باقیمانده طاقه در انبار به عنوان پارچه خرد",
        style = MaterialTheme.typography.bodySmall,
        color = customColors.textSecondary
      )
      androidx.compose.material3.Switch(
        checked = keepRemainingInStock,
        onCheckedChange = { keepRemainingInStock = it }
      )
    }

    // Submit Button
    Button(
      onClick = {
        if (currentFabric != null && !isOverCapacity && modelItems.isNotEmpty()) {
          val cuts = modelItems.map { state ->
            com.example.data.model.MultiCutModelItem(
              modelName = state.modelName,
              modelCode = state.modelCode,
              heightCm = state.heightCm.toIntOrNull() ?: 70,
              metersPerItem = state.meterPerItem.toDoubleOrNull() ?: 1.0,
              cutQuantity = state.count.toIntOrNull() ?: 1
            )
          }
          viewModel.submitMultiModelCutting(
            fabricId = currentFabric.id,
            modelCuts = cuts,
            keepRemainingInStock = keepRemainingInStock,
            context = context
          )
        }
      },
      enabled = !isOverCapacity && currentFabric != null,
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("submit_multi_cut_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = if (isOverCapacity) customColors.textMuted else AccentIndigo
      )
    ) {
      Text(
        text = if (isOverCapacity) "کسری متراژ در طاقه - لطفاً اصلاح کنید" else "تأیید و اجرای برش چند مدلی طاقه",
        fontWeight = FontWeight.Bold
      )
    }
  }
}

// ==========================================================
// PHASE 2: FABRIC ROLL REGISTRATION FORM
// ==========================================================

@Composable
fun QuickRollForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val existingFabrics by viewModel.fabrics.collectAsStateWithLifecycle()
  val existingSuppliers by viewModel.suppliers.collectAsStateWithLifecycle()

  var showFabricPicker by remember { mutableStateOf(false) }
  var showSupplierPicker by remember { mutableStateOf(false) }
  var showSupplierRegisterDialog by remember { mutableStateOf(false) }

  var rollCode by remember { mutableStateOf("ROLL-${(100..999).random()}") }
  var fabricType by remember { mutableStateOf("دورس پنبه سه نخ خارخورده") }
  var fabricCode by remember { mutableStateOf("FAB-DRS-01") }
  var color by remember { mutableStateOf("مشکی زغالی") }
  var batchNumber by remember { mutableStateOf("PT-1403-91") }
  var supplierName by remember { mutableStateOf("نساجی بروجرد") }
  var inboundDate by remember { mutableStateOf(com.example.util.PersianDateHelper.getTodayPersianDate()) }

  var initialMetersText by remember { mutableStateOf("120.0") }
  var weightKgText by remember { mutableStateOf("40.0") }
  var metersPerKgText by remember { mutableStateOf("3.0") }
  var buyPricePerKgText by remember { mutableStateOf("420000") }
  var buyPricePerMeterText by remember { mutableStateOf("140000") }

  val initialMeters = initialMetersText.toDoubleOrNull() ?: 0.0
  val weightKg = weightKgText.toDoubleOrNull() ?: 0.0
  val metersPerKg = metersPerKgText.toDoubleOrNull() ?: (if (weightKg > 0) initialMeters / weightKg else 3.0)
  val buyPricePerMeter = buyPricePerMeterText.toLongOrNull() ?: 0L
  val buyPricePerKg = buyPricePerKgText.toLongOrNull() ?: 0L

  val totalFabricCost = (initialMeters * buyPricePerMeter).toLong()
  val totalRollValue = totalFabricCost

  if (showFabricPicker) {
    ItemSelectionPopupDialog(
      title = "انتخاب جنس پارچه یا طاقه قبلی",
      items = existingFabrics,
      onDismiss = { showFabricPicker = false },
      onAddNew = {
        fabricType = ""
        fabricCode = ""
        color = ""
      },
      onItemSelected = { fab ->
        fabricType = fab.name
        fabricCode = fab.code
        color = fab.color
        supplierName = fab.supplierName
        batchNumber = fab.batchNumber
        buyPricePerMeterText = fab.buyPricePerMeter.toString()
        buyPricePerKgText = fab.buyPricePerKg.toString()
      },
      itemLabel = { it.name },
      itemCode = { it.code },
      itemSecondary = { "موجودی: ${it.rollCount} طاقه (${it.totalMeters} متر)" },
      itemPrice = { it.buyPricePerMeter }
    )
  }

  if (showSupplierPicker) {
    SupplierSelectionPopupDialog(
      suppliers = existingSuppliers,
      onDismiss = { showSupplierPicker = false },
      onAddNewSupplier = {
        showSupplierPicker = false
        showSupplierRegisterDialog = true
      },
      onSupplierSelected = { sup ->
        supplierName = "${sup.name} (${sup.storeName.ifBlank { "دفتر مرکزی" }})"
      }
    )
  }

  if (showSupplierRegisterDialog) {
    SupplierRegistrationDialog(
      onDismiss = { showSupplierRegisterDialog = false },
      onSubmit = { name, storeName, mobile, phone, address, distributionCategory, description ->
        viewModel.submitSupplier(
          name = name,
          storeName = storeName,
          mobile = mobile,
          phone = phone,
          address = address,
          distributionCategory = distributionCategory,
          description = description
        )
        supplierName = "$name ($storeName)"
        showSupplierRegisterDialog = false
      }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("ثبت طاقه جدید پارچه (فاز ۲)", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        Text("شناسه مستقل، متراژ، وزن، قیمت خرید و هزینه باربری", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted, fontSize = 11.sp)
      }
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    // Top action buttons for quick selection
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = { showFabricPicker = true },
        modifier = Modifier.weight(1f).height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan.copy(alpha = 0.2f))
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Search, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
          Text("انتخاب نوع پارچه", color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Button(
        onClick = { showSupplierPicker = true },
        modifier = Modifier.weight(1f).height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo.copy(alpha = 0.2f))
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.PersonAdd, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(16.dp))
          Text("انتخاب تأمین‌کننده", color = AccentIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "شناسه / بارکد طاقه", value = rollCode, onValueChange = { rollCode = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "کد دسته پارچه", value = fabricCode, onValueChange = { fabricCode = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.4f)) {
        ExecutiveTextField(label = "نوع پارچه", value = fabricType, onValueChange = { fabricType = it })
      }
      Box(modifier = Modifier.weight(0.8f)) {
        ExecutiveTextField(label = "رنگ", value = color, onValueChange = { color = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "شماره پارت / لات", value = batchNumber, onValueChange = { batchNumber = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "نام تأمین‌کننده", value = supplierName, onValueChange = { supplierName = it })
      }
    }

    ExecutiveTextField(
      label = "تاریخ ورود به انبار (شمسی - پیش‌فرض خودکار و قابل ویرایش)",
      value = inboundDate,
      onValueChange = { inboundDate = it },
      leadingIcon = Icons.Default.CalendarToday
    )

    // Quantitative metrics: meters, weight, metersPerKg
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "وزن طاقه (کیلوگرم)",
          value = weightKgText,
          keyboardType = KeyboardType.Decimal,
          onValueChange = {
            weightKgText = it
            val w = it.toDoubleOrNull() ?: 0.0
            val mpk = metersPerKgText.toDoubleOrNull() ?: 0.0
            if (w > 0.0 && mpk > 0.0) {
              initialMetersText = String.format(java.util.Locale.US, "%.1f", w * mpk)
            }
          }
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "متراژ هر کیلو (متر/کیلو)",
          value = metersPerKgText,
          keyboardType = KeyboardType.Decimal,
          onValueChange = {
            metersPerKgText = it
            val mpk = it.toDoubleOrNull() ?: 0.0
            val w = weightKgText.toDoubleOrNull() ?: 0.0
            if (w > 0.0 && mpk > 0.0) {
              initialMetersText = String.format(java.util.Locale.US, "%.1f", w * mpk)
            }
          }
        )
      }
      Box(modifier = Modifier.weight(1.1f)) {
        ExecutiveTextField(
          label = "متراژ کل طاقه (متر)",
          value = initialMetersText,
          keyboardType = KeyboardType.Decimal,
          onValueChange = {
            initialMetersText = it
            val m = it.toDoubleOrNull() ?: 0.0
            val w = weightKgText.toDoubleOrNull() ?: 0.0
            if (m > 0.0 && w > 0.0) {
              metersPerKgText = String.format(java.util.Locale.US, "%.2f", m / w)
            }
          }
        )
      }
    }

    // Pricing: Meter and Kilogram Auto-Conversion
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "قیمت خرید هر کیلو (تومان)",
          value = buyPricePerKgText,
          keyboardType = KeyboardType.Number,
          onValueChange = {
            buyPricePerKgText = it
            val pkg = it.toLongOrNull() ?: 0L
            val mpk = metersPerKgText.toDoubleOrNull() ?: 3.0
            if (pkg > 0L && mpk > 0.0) {
              // فرمول خودکار: قیمت متر = قیمت کیلو / متر بر کیلو
              val pm = FinancialCalculationService.calculatePricePerMeter(pkg, mpk)
              buyPricePerMeterText = pm.toString()
            }
          }
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "قیمت خرید هر متر (تومان)",
          value = buyPricePerMeterText,
          keyboardType = KeyboardType.Number,
          onValueChange = {
            buyPricePerMeterText = it
            val pm = it.toLongOrNull() ?: 0L
            val mpk = metersPerKgText.toDoubleOrNull() ?: 3.0
            if (pm > 0L && mpk > 0.0) {
              val pkg = FinancialCalculationService.calculatePricePerKg(pm, mpk)
              buyPricePerKgText = pkg.toString()
            }
          }
        )
      }
    }

    // Informational notice regarding shipping allocation via waybills
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
        .padding(12.dp)
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
        Text(
          text = "هزینه باربری این طاقه در بخش «بارنامه و باربری» با انتخاب این طاقه به صورت خودکار محاسبه و تخصیص داده می‌شود.",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textSecondary,
          fontSize = 11.sp
        )
      }
    }

    // Calculation Summary Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.cardElevated)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("خلاصه مشخصات و ارزش اولیه طاقه", style = MaterialTheme.typography.labelMedium, color = AccentCyan, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("متراژ اولیه طاقه:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text("${String.format(java.util.Locale.US, "%.1f", initialMeters)} متر ($weightKg کیلوگرم)", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("بهای خرید پارچه:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(totalFabricCost), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
        }
        HorizontalDivider(color = customColors.border)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("ارزش اولیه طاقه در انبار:", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text(CurrencyHelper.formatToman(totalRollValue), style = MaterialTheme.typography.bodyMedium, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }
    }

    Button(
      onClick = {
        viewModel.submitFabricRoll(
          rollCode = rollCode,
          inboundDate = inboundDate,
          fabricType = fabricType,
          fabricCode = fabricCode,
          color = color,
          initialMeters = initialMeters,
          weightKg = weightKg,
          buyPricePerMeter = buyPricePerMeter,
          buyPricePerKg = buyPricePerKg,
          allocatedShippingCost = 0L,
          supplierName = supplierName,
          batchNumber = batchNumber
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_fabric_roll_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
    ) {
      Text("تأیید و ثبت طاقه در انبار پارچه", fontWeight = FontWeight.Bold, color = Color.Black)
    }
  }
}

// ==========================================================
// PHASE 2: ROLL CONSUMPTION FORM (MULTI-PRODUCT & STOCK CONTROL)
// ==========================================================

@Composable
fun QuickRollConsumeForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val availableRolls by viewModel.availableFabricRolls.collectAsStateWithLifecycle()
  val standards by viewModel.standards.collectAsStateWithLifecycle()

  var selectedRollId by remember { mutableStateOf<Long?>(null) }
  val selectedRoll = remember(availableRolls, selectedRollId) {
    availableRolls.firstOrNull { it.id == selectedRollId } ?: availableRolls.firstOrNull()
  }

  val products by viewModel.products.collectAsStateWithLifecycle()
  var showProductPicker by remember { mutableStateOf(false) }
  var modelName by remember { mutableStateOf("هودی بیسیک زمستانه") }
  var modelCode by remember { mutableStateOf("HD-204") }
  var consumptionUnit by remember { mutableStateOf(FabricConsumptionUnit.METERS) }
  var consumptionAmountText by remember { mutableStateOf("25.0") }
  var garmentCountText by remember { mutableStateOf("20") }
  var note by remember { mutableStateOf("تولید و برش پارت اول") }
  var tailorCostText by remember { mutableStateOf("85000") }
  var profitPercentText by remember { mutableStateOf("35") }
  var baseMaterialsText by remember { mutableStateOf("15000") }
  var otherDirectCostText by remember { mutableStateOf("0") }
  var selectedStatus by remember { mutableStateOf("برش خورده") }

  val productions by viewModel.productions.collectAsStateWithLifecycle()
  val relatedProductions = remember(productions, selectedRoll) {
    if (selectedRoll == null) emptyList()
    else productions.filter { it.rollId == selectedRoll.id && it.status != "تکمیل شده" && it.status != "آماده ارسال / تکمیل موجودی" }
  }

  val rollInitialMeters = selectedRoll?.initialMeters ?: 1.0
  val rollWeightKg = selectedRoll?.weightKg ?: 0.0
  val metersPerKg = if (rollWeightKg > 0.0 && rollInitialMeters > 0.0) rollInitialMeters / rollWeightKg else 3.0

  val enteredAmount = consumptionAmountText.toDoubleOrNull() ?: 0.0
  val metersUsed = if (consumptionUnit == FabricConsumptionUnit.METERS) enteredAmount else enteredAmount * metersPerKg
  val kgUsed = if (consumptionUnit == FabricConsumptionUnit.KILOGRAMS) enteredAmount else (if (metersPerKg > 0.0) enteredAmount / metersPerKg else 0.0)

  val garmentCount = garmentCountText.toIntOrNull() ?: 1
  val metersPerGarment = if (garmentCount > 0) metersUsed / garmentCount else metersUsed

  val remainingMeters = selectedRoll?.remainingMeters ?: 0.0
  val isOverCapacity = selectedRoll != null && metersUsed > (remainingMeters + 0.05)
  val newRemainingMeters = (remainingMeters - metersUsed).coerceAtLeast(0.0)
  val newRemainingKg = if (metersPerKg > 0.0) newRemainingMeters / metersPerKg else 0.0
  val untouchedPercent = if (rollInitialMeters > 0.0) ((newRemainingMeters / rollInitialMeters) * 100.0).coerceIn(0.0, 100.0) else 0.0

  val buyPriceMeter = selectedRoll?.buyPricePerMeter ?: 0L
  val allocatedFabricCost = (metersUsed * buyPriceMeter).toLong()
  val allocatedShipping = selectedRoll?.allocatedShippingCost ?: 0L
  val allocatedShippingCost = if (rollInitialMeters > 0.0) ((metersUsed / rollInitialMeters) * allocatedShipping).toLong() else 0L

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("مصرف طاقه پارچه (تخصیص به چند مدل)", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        Text("کنترل عدم مصرف مازاد و تفکیک بهای تمام شده", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted, fontSize = 11.sp)
      }
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    // Roll Selector Header
    Text("انتخاب طاقه پارچه مبدا:", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)

    if (availableRolls.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(StatusDanger.copy(alpha = 0.1f))
          .padding(14.dp)
      ) {
        Text("هیچ طاقه فعالی در انبار یافت نشد! لطفاً ابتدا طاقه جدید ثبت کنید.", color = StatusDanger, fontSize = 12.sp)
      }
    } else {
      // Horizontal list of roll pills
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        availableRolls.take(4).forEach { roll ->
          val isSel = roll.id == selectedRoll?.id
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSel) AccentIndigo.copy(alpha = 0.2f) else customColors.cardElevated)
              .border(1.dp, if (isSel) AccentIndigo else customColors.border, RoundedCornerShape(8.dp))
              .clickable { selectedRollId = roll.id }
              .padding(8.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(roll.rollCode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) AccentIndigo else customColors.textPrimary)
              Text("${UnitFormatter.shortMeters(roll.remainingMeters)}متر", fontSize = 10.sp, color = customColors.textMuted)
            }
          }
        }
      }
    }

    // Selected Roll Details Card
    selectedRoll?.let { roll ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.cardElevated)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .padding(12.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("${roll.fabricType} - ${roll.color}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(AccentCyan.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(roll.status, fontSize = 10.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
            }
          }
          Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("موجودی اولیه:", fontSize = 11.sp, color = customColors.textMuted)
            Text("${roll.initialMeters} متر (${roll.weightKg} کیلو)", fontSize = 11.sp, color = customColors.textSecondary)
          }
          Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("موجودی باقیمانده فعلی:", fontSize = 11.sp, color = customColors.textMuted)
            Text("${UnitFormatter.shortMeters(roll.remainingMeters)} متر", fontSize = 11.sp, color = StatusSuccess, fontWeight = FontWeight.Bold)
          }
          Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("قیمت هر متر / باربری تخصیص یافته:", fontSize = 11.sp, color = customColors.textMuted)
            Text("${CurrencyHelper.formatToman(roll.buyPricePerMeter)} | ${CurrencyHelper.formatToman(roll.allocatedShippingCost)}", fontSize = 11.sp, color = customColors.textSecondary)
          }
        }
      }
    }

    // Modular Unit Selector: Meter vs Kilogram
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(customColors.secondaryBg)
        .padding(4.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf(
        FabricConsumptionUnit.METERS to "📏 ثبت مصرف بر حسب متر",
        FabricConsumptionUnit.KILOGRAMS to "⚖️ ثبت مصرف بر حسب کیلوگرم"
      ).forEach { (unit, title) ->
        val isSelected = consumptionUnit == unit
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AccentIndigo else Color.Transparent)
            .clickable { consumptionUnit = unit }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color.White else customColors.textSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp
          )
        }
      }
    }

    // Amount & Garment Count Inputs
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.2f)) {
        ExecutiveTextField(
          label = "میزان مصرف کل (${consumptionUnit.shortUnit})",
          value = consumptionAmountText,
          keyboardType = KeyboardType.Decimal,
          onValueChange = { consumptionAmountText = it }
        )
      }
      Box(modifier = Modifier.weight(0.8f)) {
        ExecutiveTextField(
          label = "تیراژ کار (تعداد برش)",
          value = garmentCountText,
          keyboardType = KeyboardType.Number,
          onValueChange = { garmentCountText = it }
        )
      }
    }

    // Dynamic Live Conversion Tag
    if (enteredAmount > 0.0) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(AccentCyan.copy(alpha = 0.1f))
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (consumptionUnit == FabricConsumptionUnit.METERS)
              "معادل وزنی: ${UnitFormatter.shortKg(kgUsed)} کیلوگرم"
            else
              "معادل متراژ: ${UnitFormatter.shortMeters(metersUsed)} متر پارچه",
            fontSize = 11.sp,
            color = AccentCyan,
            fontWeight = FontWeight.Bold
          )
          if (garmentCount > 0) {
            Text(
              text = "مصرف سرانه هر عدد: ${UnitFormatter.shortMeters(metersPerGarment)} متر (${String.format(java.util.Locale.US, "%.0f", (kgUsed * 1000) / garmentCount)} گرم)",
              fontSize = 11.sp,
              color = customColors.textSecondary
            )
          }
        }
      }
    }

    // Product Picker
    Button(
      onClick = { showProductPicker = true },
      modifier = Modifier.fillMaxWidth().height(44.dp),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo.copy(alpha = 0.18f))
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Search, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(18.dp))
        Text(
          text = if (modelName.isNotBlank() && modelCode.isNotBlank())
            "مدل انتخابی: $modelName ($modelCode) - برای تغییر کلیک کنید"
          else
            "انتخاب مدل کار از لیست محصولات / ثبت مدل جدید",
          color = AccentIndigo, fontSize = 12.sp, fontWeight = FontWeight.Bold
        )
      }
    }

    // Model Inputs
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.4f)) {
        ExecutiveTextField(label = "نام مدل محصول مورد نظر", value = modelName, onValueChange = { modelName = it })
      }
      Box(modifier = Modifier.weight(0.8f)) {
        ExecutiveTextField(label = "کد مدل", value = modelCode, onValueChange = { modelCode = it })
      }
    }

    // Product Selection Dialog
    if (showProductPicker) {
      ItemSelectionPopupDialog(
        title = "انتخاب مدل از لیست محصولات",
        items = products,
        onDismiss = { showProductPicker = false },
        onAddNew = {
          modelName = ""
          modelCode = ""
          showProductPicker = false
        },
        onItemSelected = { p ->
          modelName = p.name
          modelCode = p.code
          showProductPicker = false
        },
        itemLabel = { it.name },
        itemCode = { it.code },
        itemSecondary = { "دسته: ${it.categoryName} | قیمت فروش: ${CurrencyHelper.formatToman(it.effectiveSellingPrice)}" },
        itemPrice = { it.currentCostPrice }
      )
    }

    ExecutiveTextField(
      label = "یادداشت / پارت دوخت",
      value = note,
      onValueChange = { note = it }
    )

    // ============================================
    // هزینه‌های تولید و قیمت‌گذاری (فاز ۱۰.۳)
    // ============================================
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.cardElevated)
        .border(1.dp, AccentIndigo.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          "💰 هزینه‌های تولید و قیمت‌گذاری محصول",
          style = MaterialTheme.typography.labelMedium,
          color = AccentIndigo,
          fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(modifier = Modifier.weight(1f)) {
            ExecutiveTextField(
              label = "هزینه خیاط هر کار",
              value = tailorCostText,
              keyboardType = KeyboardType.Number,
              onValueChange = { tailorCostText = it }
            )
          }
          Box(modifier = Modifier.weight(1f)) {
            ExecutiveTextField(
              label = "سود ثابت هدف (٪)",
              value = profitPercentText,
              keyboardType = KeyboardType.Decimal,
              onValueChange = { profitPercentText = it }
            )
          }
        }

        ExecutiveTextField(
          label = "هزینه ملزومات پایه هر کار (نخ، دوک، برق، کرایه)",
          value = baseMaterialsText,
          keyboardType = KeyboardType.Number,
          onValueChange = { baseMaterialsText = it }
        )

        ExecutiveTextField(
          label = "هزینه‌های اضافه (سایر)",
          value = otherDirectCostText,
          keyboardType = KeyboardType.Number,
          onValueChange = { otherDirectCostText = it }
        )

        // پیش‌نمایش محاسبه
        val fabricCostPerUnit = if (garmentCount > 0) ((metersUsed * buyPriceMeter) / garmentCount).toLong() else 0L
        val tailorVal = tailorCostText.toLongOrNull() ?: 0L
        val baseVal = baseMaterialsText.toLongOrNull() ?: 0L
        val profitPct = profitPercentText.toDoubleOrNull() ?: 0.0
        val subtotal = fabricCostPerUnit + tailorVal + baseVal
        val profitVal = (subtotal * profitPct / 100.0).toLong()
        val finalUnitPrice = subtotal + profitVal

        HorizontalDivider(color = customColors.border)

        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("هزینه پارچه هر کار:", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(fabricCostPerUnit), fontSize = 11.sp, color = customColors.textPrimary)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("جمع هزینه (بدون سود):", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(subtotal), fontSize = 11.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("سود (${profitPct.toInt()}٪):", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(profitVal), fontSize = 11.sp, color = StatusSuccess)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("قیمت فروش پیشنهادی هر کار:", fontSize = 12.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text(CurrencyHelper.formatToman(finalUnitPrice), fontSize = 13.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Detailed Consumption & Untouched Roll Metrics Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          "محاسبه متراژ و وزن مصرفی و باقیمانده طاقه:",
          style = MaterialTheme.typography.labelMedium,
          color = AccentCyan,
          fontWeight = FontWeight.Bold
        )
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("کل متراژ مصرف‌شده برای $garmentCount کار:", fontSize = 11.sp, color = customColors.textMuted)
          Text("${UnitFormatter.shortMeters(metersUsed)} متر", fontSize = 11.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("کل وزن مصرف‌شده از طاقه:", fontSize = 11.sp, color = customColors.textMuted)
          Text("${UnitFormatter.shortKg(kgUsed)} کیلوگرم", fontSize = 11.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(color = customColors.border)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("باقیمانده دست‌نخورده طاقه (متر و کیلو):", fontSize = 11.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text(
            "${UnitFormatter.shortMeters(newRemainingMeters)} متر (${UnitFormatter.shortKg(newRemainingKg)} کیلو)",
            fontSize = 11.sp,
            color = if (isOverCapacity) StatusDanger else StatusSuccess,
            fontWeight = FontWeight.Bold
          )
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("درصد دست‌نخورده از کل طاقه اولیه:", fontSize = 11.sp, color = customColors.textMuted)
          Text("${untouchedPercent.toInt()}٪ دست‌نخورده", fontSize = 11.sp, color = AccentIndigo, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Stock Control & Warning Box
    if (isOverCapacity) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(StatusDanger.copy(alpha = 0.15f))
          .border(1.dp, StatusDanger, RoundedCornerShape(10.dp))
          .padding(12.dp)
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Close, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(20.dp))
          Text(
            text = "خطای کسری موجودی: متراژ مصرفی (${String.format(java.util.Locale.US, "%.1f", metersUsed)}متر) بیشتر از باقیمانده طاقه (${UnitFormatter.shortMeters(remainingMeters)}متر) است!",
            color = StatusDanger,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Cost Breakdown for this consumption
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("هزینه پارچه مصرفی:", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(allocatedFabricCost), fontSize = 11.sp, color = customColors.textPrimary)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("سهم باربری مصرف شده:", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(allocatedShippingCost), fontSize = 11.sp, color = AccentBlue)
        }
        HorizontalDivider(color = customColors.border)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("بهای تمام شده ماده اولیه برای این مدل:", fontSize = 11.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text(CurrencyHelper.formatToman(allocatedFabricCost + allocatedShippingCost), fontSize = 12.sp, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Status Selector (Phase 15 Patch 3)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text("وضعیت کار پس از ثبت:", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf("برش خورده", "در حال دوخت", "آماده ارسال").forEach { st ->
          val sel = selectedStatus == st
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (sel) AccentIndigo else customColors.cardElevated)
              .border(1.dp, if (sel) AccentIndigo else customColors.border, RoundedCornerShape(8.dp))
              .clickable { selectedStatus = st }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              st,
              fontSize = 11.sp,
              fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
              color = if (sel) Color.White else customColors.textPrimary
            )
          }
        }
      }
    }

    // View History Shortcut Button
    selectedRoll?.let { roll ->
      Button(
        onClick = { viewModel.showRollHistory(roll) },
        modifier = Modifier.fillMaxWidth().height(40.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = customColors.cardElevated)
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.History, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(16.dp))
          Text("مشاهده سوابق مصارف گذشته طاقه ${roll.rollCode}", color = AccentIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Button(
      onClick = {
        selectedRoll?.let { roll ->
          viewModel.consumeFabricRoll(
            rollId = roll.id,
            modelCode = modelCode,
            modelName = modelName,
            metersUsed = metersUsed,
            note = note,
            garmentCount = garmentCount,
            metersPerGarment = metersPerGarment,
            createProductionOrder = true,
            tailorCostPerItem = tailorCostText.toLongOrNull() ?: 0L,
            accessoriesCostPerItem = baseMaterialsText.toLongOrNull() ?: 0L,
            otherDirectCost = otherDirectCostText.toLongOrNull() ?: 0L,
            initialStatus = selectedStatus
          )
        }
      },
      enabled = !isOverCapacity && selectedRoll != null && metersUsed > 0.0,
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_roll_consume_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = if (isOverCapacity) customColors.textMuted else StatusSuccess
      )
    ) {
      Text(
        text = if (isOverCapacity) "عدم امکان ثبت - کسری متراژ طاقه" else "تأیید و ثبت مصرف از طاقه و ارجاع به خط دوخت",
        fontWeight = FontWeight.Bold
      )
    }

    // List of in-progress productions created from this roll with direct "Ready Goods Registration"
    if (relatedProductions.isNotEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.cardElevated)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "کارهای برش‌خورده از این طاقه در خط تولید (${relatedProductions.size} مورد):",
          style = MaterialTheme.typography.titleSmall,
          color = AccentIndigo,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "پس از آماده شدن، با دکمه زیر مستقیماً محصول را به عنوان کار آماده در انبار ثبت کنید:",
          style = MaterialTheme.typography.labelSmall,
          color = customColors.textMuted
        )

        relatedProductions.forEach { prod ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(customColors.secondaryBg)
              .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(prod.modelName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
                  Text("کد مدل: ${prod.modelCode} | کد تولید: PROD-${prod.id}", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(AccentCyan.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(prod.status, fontSize = 10.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
                }
              }

              Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("تعداد کار در جریان:", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                Text("${prod.quantity} عدد (${String.format(java.util.Locale.US, "%.1f", prod.fabricMetersUsed)} متر پارچه)", style = MaterialTheme.typography.labelSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = {
                  viewModel.completeProductionToReadyGoods(prod)
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(38.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(6.dp))
                Text("ثبت کار آماده در انبار محصولات", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

// ==========================================================
// PHASE 2: SHIPPING EXPENSE REGISTRATION FORM
// ==========================================================

@Composable
fun QuickShippingExpenseForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val availableRolls by viewModel.availableFabricRolls.collectAsStateWithLifecycle()

  var trackingNumber by remember { mutableStateOf("BL-${(10000..99999).random()}") }
  var title by remember { mutableStateOf("کرایه حمل پارت پارچه و ملزومات") }
  var carrierName by remember { mutableStateOf("باربری تیزرو تهران - راننده صادقی") }
  var totalAmountText by remember { mutableStateOf("1850000") }
  var inboundType by remember { mutableStateOf("طاقه پارچه") }
  var allocationMethod by remember { mutableStateOf(ShippingAllocationMethod.PER_ITEM) }

  var itemCountText by remember { mutableStateOf("10") }
  var totalWeightKgText by remember { mutableStateOf("300.0") }
  var totalQuantityText by remember { mutableStateOf("1200.0") }
  var notes by remember { mutableStateOf("رسید بارنامه شماره ۷۱") }

  val totalAmount = totalAmountText.toLongOrNull() ?: 0L
  val itemCount = itemCountText.toIntOrNull() ?: 0
  val totalWeightKg = totalWeightKgText.toDoubleOrNull() ?: 0.0
  val totalQuantity = totalQuantityText.toDoubleOrNull() ?: 0.0

  val costPerUnit = FinancialCalculationService.calculateUnitShippingCost(
    totalAmount = totalAmount,
    method = allocationMethod,
    itemCount = itemCount,
    totalWeightKg = totalWeightKg,
    totalQuantity = totalQuantity
  )

  val selectedRollIds = remember { mutableStateOf(listOf<Long>()) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("ثبت بارنامه و هزینه باربری (فاز ۲)", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        Text("تخصیص هزینه به طاقه‌ها، وزن یا تعداد و گزارش سرانه", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted, fontSize = 11.sp)
      }
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "شماره بارنامه/رهگیری", value = trackingNumber, onValueChange = { trackingNumber = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "نوع محموله", value = inboundType, onValueChange = { inboundType = it })
      }
    }

    ExecutiveTextField(label = "شرح بارنامه", value = title, onValueChange = { title = it })
    ExecutiveTextField(label = "نام باربری / شرکت حمل", value = carrierName, onValueChange = { carrierName = it })

    ExecutiveTextField(
      label = "مبلغ کل هزینه باربری (تومان)",
      value = totalAmountText,
      keyboardType = KeyboardType.Number,
      onValueChange = { totalAmountText = it }
    )

    // Allocation Method Selector
    Text("روش تخصیص هزینه باربری:", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf(
        ShippingAllocationMethod.PER_ITEM to "بر اساس تعداد (طاقه/بسته)",
        ShippingAllocationMethod.PER_WEIGHT to "بر اساس وزن (کیلو)",
        ShippingAllocationMethod.PER_QUANTITY to "بر اساس مقدار (متر)"
      ).forEach { (method, label) ->
        val isSel = allocationMethod == method
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSel) AccentBlue.copy(alpha = 0.2f) else customColors.cardElevated)
            .border(1.dp, if (isSel) AccentBlue else customColors.border, RoundedCornerShape(8.dp))
            .clickable { allocationMethod = method }
            .padding(vertical = 8.dp, horizontal = 4.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(label, fontSize = 10.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = if (isSel) AccentBlue else customColors.textSecondary)
        }
      }
    }

    // Quantitative input parameters
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "تعداد طاقه/قلم", value = itemCountText, keyboardType = KeyboardType.Number, onValueChange = { itemCountText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "وزن کل محموله (کیلو)", value = totalWeightKgText, keyboardType = KeyboardType.Decimal, onValueChange = { totalWeightKgText = it })
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(label = "متراژ/مقدار کل", value = totalQuantityText, keyboardType = KeyboardType.Decimal, onValueChange = { totalQuantityText = it })
      }
    }

    // Calculated Unit Cost Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.secondaryBg)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("مبلغ کل کرایه حمل:", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(totalAmount), fontSize = 12.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("سهم باربری هر واحد بر اساس فرمول:", fontSize = 11.sp, color = customColors.textMuted)
          Text(
            text = when (allocationMethod) {
              ShippingAllocationMethod.PER_ITEM, ShippingAllocationMethod.EQUAL -> "${CurrencyHelper.formatToman(costPerUnit)} / هر طاقه یا قلم"
              ShippingAllocationMethod.PER_WEIGHT, ShippingAllocationMethod.BY_WEIGHT -> "${CurrencyHelper.formatToman(costPerUnit)} / هر کیلوگرم"
              ShippingAllocationMethod.PER_QUANTITY -> "${CurrencyHelper.formatToman(costPerUnit)} / هر متر"
              ShippingAllocationMethod.BY_VOLUME -> "${CurrencyHelper.formatToman(costPerUnit)} / متر مکعب"
              ShippingAllocationMethod.BY_PURCHASE_VALUE -> "${CurrencyHelper.formatToman(costPerUnit)} / ریالی"
              ShippingAllocationMethod.WEIGHTED, ShippingAllocationMethod.MANUAL -> "${CurrencyHelper.formatToman(costPerUnit)} / تخصیص وزنی-دستی"
            },
            fontSize = 12.sp,
            color = AccentBlue,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Option to assign to existing fabric rolls
    if (availableRolls.isNotEmpty()) {
      Text("تخصیص خودکار کرایه به طاقه‌های موجود در انبار:", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        availableRolls.take(5).forEach { roll ->
          val isChecked = selectedRollIds.value.contains(roll.id)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(customColors.cardElevated)
              .clickable {
                val current = selectedRollIds.value.toMutableList()
                if (isChecked) current.remove(roll.id) else current.add(roll.id)
                selectedRollIds.value = current
              }
              .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
              Checkbox(
                checked = isChecked,
                onCheckedChange = { chk ->
                  val current = selectedRollIds.value.toMutableList()
                  if (chk) current.add(roll.id) else current.remove(roll.id)
                  selectedRollIds.value = current
                },
                colors = CheckboxDefaults.colors(checkedColor = AccentBlue)
              )
              Text("${roll.rollCode} - ${roll.fabricType}", fontSize = 11.sp, color = customColors.textPrimary)
            }
            Text("${roll.remainingMeters}متر", fontSize = 10.sp, color = customColors.textMuted)
          }
        }
      }
    }

    ExecutiveTextField(label = "توضیحات و راننده", value = notes, onValueChange = { notes = it })

    Button(
      onClick = {
        viewModel.submitShippingExpense(
          trackingNumber = trackingNumber,
          title = title,
          totalAmount = totalAmount,
          inboundType = inboundType,
          itemCount = itemCount,
          totalWeightKg = totalWeightKg,
          totalQuantity = totalQuantity,
          unit = when (allocationMethod) {
            ShippingAllocationMethod.PER_ITEM, ShippingAllocationMethod.EQUAL -> "طاقه"
            ShippingAllocationMethod.PER_WEIGHT, ShippingAllocationMethod.BY_WEIGHT, ShippingAllocationMethod.WEIGHTED -> "کیلوگرم"
            ShippingAllocationMethod.PER_QUANTITY -> "متر"
            ShippingAllocationMethod.BY_VOLUME -> "متر مکعب"
            ShippingAllocationMethod.BY_PURCHASE_VALUE -> "ریال"
            ShippingAllocationMethod.MANUAL -> "قلم"
          },
          allocationMethod = allocationMethod,
          carrierName = carrierName,
          notes = notes,
          allocateToRollIds = selectedRollIds.value
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_shipping_expense_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
    ) {
      Text("تأیید و ثبت هزینه باربری و بارنامه", fontWeight = FontWeight.Bold)
    }
  }
}

// ==========================================================
// PHASE 2: ROLL HISTORY MODAL (SABEQEH MASRAF TAQE)
// ==========================================================

@Composable
fun RollHistoryModal(
  viewModel: ManufacturingViewModel,
  onClose: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val selectedRoll by viewModel.selectedRollForHistory.collectAsStateWithLifecycle()
  val allUsages by viewModel.rollUsages.collectAsStateWithLifecycle()

  val usages = remember(allUsages, selectedRoll) {
    if (selectedRoll != null) allUsages.filter { it.rollId == selectedRoll!!.id } else emptyList()
  }

  var editingUsage by remember { mutableStateOf<RollUsageEntity?>(null) }

  editingUsage?.let { usage ->
    RollUsageEditDialog(
      usage = usage,
      metersPerKg = selectedRoll?.metersPerKg ?: 0.0,
      onSave = { newMeters, newKg, newModel, newNote ->
        viewModel.updateRollUsageAction(usage.id, newMeters, newKg, newModel, newNote)
        editingUsage = null
      },
      onDelete = {
        viewModel.deleteRollUsageAction(usage.id)
        editingUsage = null
      },
      onDismiss = { editingUsage = null }
    )
  }

  val initialM = selectedRoll?.initialMeters ?: 0.0
  val remainingM = selectedRoll?.remainingMeters ?: 0.0
  val consumedM = (initialM - remainingM).coerceAtLeast(0.0)
  val consumedPercent = if (initialM > 0.0) ((consumedM / initialM) * 100).toInt() else 0

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("سوابق مصرف طاقه ${selectedRoll?.rollCode ?: ""}", style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        Text("${selectedRoll?.fabricType ?: ""} | رنگ: ${selectedRoll?.color ?: ""}", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted, fontSize = 11.sp)
      }
      IconButton(onClick = onClose) { Icon(Icons.Default.Close, "بستن", tint = customColors.textMuted) }
    }

    // Roll Info Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.cardElevated)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("تاریخ ورود به انبار:", fontSize = 11.sp, color = customColors.textMuted)
          Text(selectedRoll?.inboundDate ?: "امروز", fontSize = 11.sp, color = customColors.textPrimary)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("متراژ اولیه:", fontSize = 11.sp, color = customColors.textMuted)
          Text("${String.format(java.util.Locale.US, "%.1f", initialM)} متر (${selectedRoll?.weightKg ?: 0.0} کیلو)", fontSize = 11.sp, color = customColors.textPrimary)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("موجودی باقیمانده:", fontSize = 11.sp, color = customColors.textMuted)
          Text("${String.format(java.util.Locale.US, "%.1f", remainingM)} متر", fontSize = 12.sp, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("مجموع مصرف شده:", fontSize = 11.sp, color = customColors.textMuted)
          Text("${String.format(java.util.Locale.US, "%.1f", consumedM)} متر ($consumedPercent%)", fontSize = 11.sp, color = if (consumedPercent > 90) StatusDanger else AccentIndigo, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("کرایه باربری سرانه این طاقه:", fontSize = 11.sp, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(selectedRoll?.allocatedShippingCost ?: 0L), fontSize = 11.sp, color = AccentBlue)
        }
      }
    }

    Text("لیست مصارف ثبت شده برای این طاقه:", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary, fontWeight = FontWeight.Bold)

    if (usages.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(customColors.cardElevated)
          .padding(20.dp),
        contentAlignment = Alignment.Center
      ) {
        Text("تاکنون مصرفی برای این طاقه ثبت نشده است.", color = customColors.textMuted, fontSize = 12.sp)
      }
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        usages.forEach { usage ->
          var showEditDialog by remember(usage.id) { mutableStateOf(false) }
          var showDeleteConfirm by remember(usage.id) { mutableStateOf(false) }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(customColors.secondaryBg)
              .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("${usage.modelName} (${usage.modelCode})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
                Text(usage.usageDate, fontSize = 10.sp, color = customColors.textMuted)
              }
              Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("متراژ مصرفی:", fontSize = 11.sp, color = customColors.textMuted)
                Text("${usage.metersUsed} متر (${String.format(java.util.Locale.US, "%.1f", usage.weightKgUsed)} کیلو)", fontSize = 11.sp, color = customColors.textSecondary, fontWeight = FontWeight.Bold)
              }
              Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("سهم بهای تمام‌شده پارچه + باربری:", fontSize = 10.sp, color = customColors.textMuted)
                Text(CurrencyHelper.formatToman(usage.allocatedFabricCost + usage.allocatedShippingCost), fontSize = 11.sp, color = StatusSuccess, fontWeight = FontWeight.Bold)
              }
              if (usage.note.isNotBlank()) {
                Text("توضیحات: ${usage.note}", fontSize = 10.sp, color = customColors.textMuted)
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                ManagementEditButton(onClick = {                    editingUsage = usage                  })
              }
            }
          }
        }
      }
    }

    Button(
      onClick = onClose,
      modifier = Modifier.fillMaxWidth().height(44.dp),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
    ) {
      Text("بستن سوابق طاقه", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * Quick Fixed Cost Form (ثبت هزینه ثابت با تخصیص هوشمند: همه محصولات، محصولات خاص، دسته محصول، یک تولید مشخص)
 */
@Composable
fun QuickFixedCostForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  var title by remember { mutableStateOf("اجاره کارگاه تولیدی") }
  var amountText by remember { mutableStateOf("15000000") }
  var selectedScope by remember { mutableStateOf(FixedCostScope.ALL_PRODUCTS) }
  var targetProductsText by remember { mutableStateOf("HD-204, PR-102") }
  var targetCategoryText by remember { mutableStateOf("هودی و سویشرت") }
  var targetProductionIdText by remember { mutableStateOf("1") }
  var note by remember { mutableStateOf("هزینه ماهیانه کارگاه دوخت") }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "ثبت هزینه ثابت و تخصیص به بهای تمام‌شده",
        style = MaterialTheme.typography.titleMedium,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )
      IconButton(onClick = onBack) { Icon(Icons.Default.Close, "بازگشت", tint = customColors.textMuted) }
    }

    // Quick presets (اجاره، استهلاک، طراحی، سایر)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf("اجاره کارگاه", "استهلاک چرخ خیاطی", "طراحی الگو و شابلون", "حقوق سرپرست").forEach { preset ->
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (title == preset) AccentIndigo.copy(alpha = 0.2f) else customColors.card)
            .border(1.dp, if (title == preset) AccentIndigo else customColors.border, RoundedCornerShape(8.dp))
            .clickable { title = preset }
            .padding(6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = preset,
            style = MaterialTheme.typography.labelSmall,
            color = if (title == preset) AccentIndigo else customColors.textPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    ExecutiveTextField(
      label = "عنوان هزینه ثابت",
      value = title,
      onValueChange = { title = it }
    )

    ExecutiveTextField(
      label = "مبلغ کل هزینه (تومان)",
      value = amountText,
      keyboardType = KeyboardType.Number,
      onValueChange = { amountText = it }
    )

    // Scope selection cards
    Text(
      text = "دامنه تخصیص این هزینه به محصولات:",
      style = MaterialTheme.typography.labelMedium,
      color = customColors.textPrimary,
      fontWeight = FontWeight.Bold
    )

    val scopes = listOf(
      FixedCostScope.ALL_PRODUCTS to "همه محصولات (توزیع متناسب)",
      FixedCostScope.SELECTED_PRODUCTS to "محصولات خاص (کدهای مشخص)",
      FixedCostScope.PRODUCT_CATEGORY to "دسته محصول خاص",
      FixedCostScope.SPECIFIC_PRODUCTION to "یک تولید مشخص (بچ خاص)"
    )

    scopes.forEach { (scope, label) ->
      val isSelected = selectedScope == scope
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(if (isSelected) AccentIndigo.copy(alpha = 0.15f) else customColors.card)
          .border(1.dp, if (isSelected) AccentIndigo else customColors.border, RoundedCornerShape(8.dp))
          .clickable { selectedScope = scope }
          .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .border(2.dp, if (isSelected) AccentIndigo else customColors.border, CircleShape)
            .background(if (isSelected) AccentIndigo else Color.Transparent)
        )
        Text(
          text = label,
          style = MaterialTheme.typography.bodySmall,
          color = if (isSelected) AccentIndigo else customColors.textPrimary,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
      }
    }

    when (selectedScope) {
      FixedCostScope.SELECTED_PRODUCTS -> {
        ExecutiveTextField(
          label = "کدهای محصولات هدف (با کاما جدا کنید)",
          value = targetProductsText,
          onValueChange = { targetProductsText = it }
        )
      }
      FixedCostScope.PRODUCT_CATEGORY -> {
        ExecutiveTextField(
          label = "نام دسته محصول هدف",
          value = targetCategoryText,
          onValueChange = { targetCategoryText = it }
        )
      }
      FixedCostScope.SPECIFIC_PRODUCTION -> {
        ExecutiveTextField(
          label = "شناسه بچ تولید (Production ID)",
          value = targetProductionIdText,
          keyboardType = KeyboardType.Number,
          onValueChange = { targetProductionIdText = it }
        )
      }
      FixedCostScope.ALL_PRODUCTS -> {
        Text(
          text = "این هزینه به نسبت تعداد کل محصولات بین همه کالاهای فعال تقسیم خواهد شد.",
          style = MaterialTheme.typography.labelSmall,
          color = customColors.textMuted
        )
      }
    }

    ExecutiveTextField(
      label = "توضیحات و بابت هزینه",
      value = note,
      onValueChange = { note = it }
    )

    val amount = amountText.toLongOrNull() ?: 0L

    Button(
      onClick = {
        if (amount > 0L && title.isNotBlank()) {
          val codes = if (selectedScope == FixedCostScope.SELECTED_PRODUCTS) {
            targetProductsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
          } else emptyList()

          val cat = if (selectedScope == FixedCostScope.PRODUCT_CATEGORY) targetCategoryText.trim() else ""
          val prodId = if (selectedScope == FixedCostScope.SPECIFIC_PRODUCTION) targetProductionIdText.toLongOrNull() ?: 0L else 0L

          viewModel.submitFixedCost(
            title = title,
            amount = amount,
            scope = selectedScope.name,
            targetCategory = cat,
            targetProductCodes = if (selectedScope == FixedCostScope.SELECTED_PRODUCTS) targetProductsText else "",
            targetProductionId = prodId,
            notes = note
          )
        }
      },
      enabled = amount > 0L && title.isNotBlank(),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("submit_fixed_cost_action"),
      shape = RoundedCornerShape(10.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
    ) {
      Text("تأیید و اعمال در بهای تمام‌شده محصولات", fontWeight = FontWeight.Bold)
    }
  }
}

// =========================================================================
// Reusable Sheet Header
// =========================================================================
@Composable
private fun SheetHeader(title: String, onBack: () -> Unit) {
  val customColors = LocalCustomColors.current
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(title, style = MaterialTheme.typography.titleMedium, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
    IconButton(onClick = onBack) {
      Icon(Icons.Default.Close, contentDescription = "بازگشت", tint = customColors.textMuted)
    }
  }
}

// =========================================================================
// 1. QUICK SALE RETURN FORM (ثبت مرجوعی فروش و بازگشت به انبار)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSaleReturnForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val orders by viewModel.salesOrders.collectAsStateWithLifecycle()
  var selectedOrderId by remember { mutableStateOf<Long?>(orders.firstOrNull()?.id) }
  val selectedOrder = orders.find { it.id == selectedOrderId }

  var returnQtyText by remember { mutableStateOf("1") }
  var returnToStock by remember { mutableStateOf(true) }
  var refundAmountText by remember { mutableStateOf("0") }
  var reasonText by remember { mutableStateOf("انصراف خریدار / تغییر سایز") }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    SheetHeader(title = "ثبت مرجوعی فروش (برگشت به انبار)", onBack = onBack)

    if (orders.isEmpty()) {
      Text("هیچ سفارش ثبتی برای مرجوع کردن وجود ندارد.", color = customColors.textMuted)
      return
    }

    Text("انتخاب سفارش فروش:", style = MaterialTheme.typography.labelMedium, color = customColors.textPrimary)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(orders.take(15)) { ord ->
        val isSelected = ord.id == selectedOrderId
        Card(
          onClick = { selectedOrderId = ord.id },
          colors = CardDefaults.cardColors(containerColor = if (isSelected) AccentBlue.copy(alpha = 0.2f) else customColors.card),
          border = BorderStroke(1.dp, if (isSelected) AccentBlue else customColors.border),
          shape = RoundedCornerShape(8.dp)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text(ord.orderNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            Text("${ord.customerName} - ${ord.modelName}", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
            Text("تعداد: ${ord.quantity} عدد", style = MaterialTheme.typography.labelSmall, color = AccentIndigo)
          }
        }
      }
    }

    selectedOrder?.let { ord ->
      ExecutiveTextField(
        label = "تعداد مرجوعی (حداکثر ${ord.quantity} عدد)",
        value = returnQtyText,
        onValueChange = { returnQtyText = it },
        keyboardType = KeyboardType.Number
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("افزایش مجدد موجودی انبار کالا", style = MaterialTheme.typography.bodyMedium, color = customColors.textPrimary)
        Switch(checked = returnToStock, onCheckedChange = { returnToStock = it })
      }

      ExecutiveTextField(
        label = "مبلغ عودت نقدی به مشتری (تومان)",
        value = refundAmountText,
        onValueChange = { refundAmountText = it },
        keyboardType = KeyboardType.Number
      )

      ExecutiveTextField(
        label = "دلیل مرجوعی",
        value = reasonText,
        onValueChange = { reasonText = it }
      )

      val retQty = returnQtyText.toIntOrNull() ?: 0
      val refundAmt = refundAmountText.toLongOrNull() ?: 0L
      val valid = retQty in 1..ord.quantity

      Button(
        onClick = {
          if (valid) {
            viewModel.submitSaleReturn(
              orderId = ord.id,
              returnQuantity = retQty,
              reason = reasonText,
              returnToStock = returnToStock,
              refundAmount = refundAmt
            )
          }
        },
        enabled = valid,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusWarning),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("تأیید و ثبت نهایی مرجوعی", fontWeight = FontWeight.Bold)
      }
    }
  }
}

// =========================================================================
// 2. QUICK PURCHASE ORDER FORM (خرید از تأمین‌کننده با تحویل ناقص)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickPurchaseOrderForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
  val materials by viewModel.materials.collectAsStateWithLifecycle()

  var selectedSupplierId by remember { mutableStateOf<Long?>(suppliers.firstOrNull()?.id) }
  val selectedSupplier = suppliers.find { it.id == selectedSupplierId }

  var selectedMaterialId by remember { mutableStateOf<Long?>(materials.firstOrNull()?.id) }
  val selectedMaterial = materials.find { it.id == selectedMaterialId }

  var orderedQtyText by remember { mutableStateOf("100") }
  var deliveredQtyText by remember { mutableStateOf("100") }
  var unitPriceText by remember { mutableStateOf(selectedMaterial?.currentPrice?.toString() ?: "0") }
  var shippingCostText by remember { mutableStateOf("0") }
  var discountText by remember { mutableStateOf("0") }
  var paidAmountText by remember { mutableStateOf("0") }
  var notesText by remember { mutableStateOf("") }

  LaunchedEffect(selectedMaterialId) {
    selectedMaterial?.let {
      if (unitPriceText == "0" || unitPriceText.isBlank()) {
        unitPriceText = it.currentPrice.toString()
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    SheetHeader(title = "ثبت خرید از تأمین‌کننده (با کنترل تحویل)", onBack = onBack)

    Text("انتخاب تأمین‌کننده:", style = MaterialTheme.typography.labelMedium, color = customColors.textPrimary)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(suppliers) { sup ->
        val isSelected = sup.id == selectedSupplierId
        FilterChip(
          selected = isSelected,
          onClick = { selectedSupplierId = sup.id },
          label = { Text(sup.name) }
        )
      }
    }

    Text("انتخاب ماده اولیه یا ملزومات:", style = MaterialTheme.typography.labelMedium, color = customColors.textPrimary)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(materials) { mat ->
        val isSelected = mat.id == selectedMaterialId
        FilterChip(
          selected = isSelected,
          onClick = {
            selectedMaterialId = mat.id
            unitPriceText = mat.currentPrice.toString()
          },
          label = { Text("${mat.name} (${mat.unit})") }
        )
      }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "مقدار سفارش‌شده",
          value = orderedQtyText,
          onValueChange = { orderedQtyText = it },
          keyboardType = KeyboardType.Decimal
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "مقدار تحویل‌شده به انبار",
          value = deliveredQtyText,
          onValueChange = { deliveredQtyText = it },
          keyboardType = KeyboardType.Decimal
        )
      }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "نرخ واحد خرید (تومان)",
          value = unitPriceText,
          onValueChange = { unitPriceText = it },
          keyboardType = KeyboardType.Number
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "کرایه حمل / باربری (تومان)",
          value = shippingCostText,
          onValueChange = { shippingCostText = it },
          keyboardType = KeyboardType.Number
        )
      }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "تخفیف (تومان)",
          value = discountText,
          onValueChange = { discountText = it },
          keyboardType = KeyboardType.Number
        )
      }
      Box(modifier = Modifier.weight(1f)) {
        ExecutiveTextField(
          label = "مبلغ واریز نقدی (تومان)",
          value = paidAmountText,
          onValueChange = { paidAmountText = it },
          keyboardType = KeyboardType.Number
        )
      }
    }

    val orderedQty = orderedQtyText.toDoubleOrNull() ?: 0.0
    val deliveredQty = deliveredQtyText.toDoubleOrNull() ?: 0.0
    val unitPrice = unitPriceText.toLongOrNull() ?: 0L
    val shippingCost = shippingCostText.toLongOrNull() ?: 0L
    val discount = discountText.toLongOrNull() ?: 0L
    val paid = paidAmountText.toLongOrNull() ?: 0L

    val grossAmount = (deliveredQty * unitPrice).toLong()
    val totalBill = (grossAmount + shippingCost - discount).coerceAtLeast(0L)
    val remainingDebt = (totalBill - paid).coerceAtLeast(0L)

    Card(
      colors = CardDefaults.cardColors(containerColor = customColors.card),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("مبلغ کل فاکتور تحویلی: ${FinancialCalculationService.formatCurrency(totalBill)}", fontWeight = FontWeight.Bold, color = customColors.textPrimary)
        Text("مانده بدهی به تأمین‌کننده: ${FinancialCalculationService.formatCurrency(remainingDebt)}", color = if (remainingDebt > 0L) StatusDanger else StatusSuccess)
        if (orderedQty > deliveredQty) {
          Text("کسری تحویل (در انتظار): ${(orderedQty - deliveredQty).toInt()} واحد", color = StatusWarning, fontWeight = FontWeight.Bold)
        }
      }
    }

    Button(
      onClick = {
        if (selectedSupplier != null && selectedMaterial != null && orderedQty > 0.0 && unitPrice > 0L) {
          viewModel.submitPurchaseOrderWithDelivery(
            supplierId = selectedSupplier.id,
            supplierName = selectedSupplier.name,
            materialId = selectedMaterial.id,
            materialName = selectedMaterial.name,
            orderedQuantity = orderedQty,
            deliveredQuantity = deliveredQty,
            unitPrice = unitPrice,
            shippingCost = shippingCost,
            discountAmount = discount,
            paidAmount = paid,
            notes = notesText
          )
        }
      },
      enabled = selectedSupplier != null && selectedMaterial != null && orderedQty > 0.0 && unitPrice > 0L,
      modifier = Modifier.fillMaxWidth().height(48.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
      shape = RoundedCornerShape(8.dp)
    ) {
      Text("ثبت فاکتور خرید و ورود به انبار", fontWeight = FontWeight.Bold)
    }
  }
}

// =========================================================================
// 3. QUICK ATOMIC BOM PRODUCTION FORM (تولید اتمیک با اعتبارسنجی کسری مواد)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAtomicBOMProductionForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val products by viewModel.products.collectAsStateWithLifecycle()
  val boms by viewModel.boms.collectAsStateWithLifecycle()
  val materials by viewModel.materials.collectAsStateWithLifecycle()
  val colors by viewModel.colors.collectAsStateWithLifecycle()
  val sizes by viewModel.sizes.collectAsStateWithLifecycle()

  var selectedProductId by remember { mutableStateOf<Long?>(products.firstOrNull()?.id) }
  val selectedProduct = products.find { it.id == selectedProductId }

  var selectedColor by remember { mutableStateOf("مشکی") }
  var selectedSize by remember { mutableStateOf("L") }
  var quantityText by remember { mutableStateOf("50") }
  var wageText by remember { mutableStateOf("") }

  LaunchedEffect(selectedProductId) {
    selectedProduct?.let {
      wageText = it.sewingWage.toString()
    }
  }

  val quantity = quantityText.toIntOrNull() ?: 0
  val wage = wageText.toLongOrNull() ?: 0L

  // Calculate BOM requirements & shortages
  val productBoms = remember(selectedProductId, boms) {
    if (selectedProductId != null) boms.filter { it.productId == selectedProductId } else emptyList()
  }

  val shortageList = remember(productBoms, quantity, materials) {
    val list = mutableListOf<String>()
    if (quantity > 0) {
      for (bom in productBoms) {
        val mat = materials.find { it.id == bom.materialId }
        val needed = bom.standardQuantity * quantity
        if (mat == null) {
          list.add("ماده «${bom.materialName}» در سیستم یافت نشد.")
        } else if (mat.stockQuantity < needed) {
          val shortage = needed - mat.stockQuantity
          list.add("کسری «${mat.name}»: ${String.format(java.util.Locale.US, "%.1f", shortage)} ${mat.unit} کمبود دارید (موجودی فعلی: ${String.format(java.util.Locale.US, "%.1f", mat.stockQuantity)})")
        }
      }
    }
    list
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    SheetHeader(title = "تولید اتمیک بر اساس فرمول BOM", onBack = onBack)

    Text("انتخاب مدل محصول:", style = MaterialTheme.typography.labelMedium, color = customColors.textPrimary)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(products) { prod ->
        val isSelected = prod.id == selectedProductId
        FilterChip(
          selected = isSelected,
          onClick = { selectedProductId = prod.id },
          label = { Text("${prod.name} (${prod.code})") }
        )
      }
    }

    selectedProduct?.let { prod ->
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.weight(1f)) {
          ExecutiveTextField(
            label = "تعداد تولید (عدد)",
            value = quantityText,
            onValueChange = { quantityText = it },
            keyboardType = KeyboardType.Number
          )
        }
        Box(modifier = Modifier.weight(1f)) {
          ExecutiveTextField(
            label = "دستمزد خیاط هر عدد (تومان)",
            value = wageText,
            onValueChange = { wageText = it },
            keyboardType = KeyboardType.Number
          )
        }
      }

      // BOM Material Breakdown
      Card(
        colors = CardDefaults.cardColors(containerColor = customColors.card),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("فرمول مصرف استاندارد BOM برای $quantity عدد:", fontWeight = FontWeight.Bold, color = customColors.textPrimary)
          if (productBoms.isEmpty()) {
            Text("برای این محصول فرمول BOM تعریف نشده است. لطفاً ابتدا از بخش «اطلاعات پایه» فرمول را ثبت نمایید.", color = StatusWarning)
          } else {
            productBoms.forEach { b ->
              val totalNeeded = b.standardQuantity * quantity
              val mat = materials.find { it.id == b.materialId }
              val currentStock = mat?.stockQuantity ?: 0.0
              val isOk = currentStock >= totalNeeded
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("${b.materialName}: ${String.format(java.util.Locale.US, "%.2f", totalNeeded)} ${b.unit}", color = customColors.textPrimary)
                Text(
                  if (isOk) "موجودی کافی (${currentStock.toInt()})" else "کسری موجودی!",
                  color = if (isOk) StatusSuccess else StatusDanger,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      // Shortages error banner
      if (shortageList.isNotEmpty()) {
        Card(
          colors = CardDefaults.cardColors(containerColor = StatusDanger.copy(alpha = 0.15f)),
          border = BorderStroke(1.dp, StatusDanger),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("⛔ خطا در تولید اتمیک: عدم کفایت موجودی مواد اولیه", fontWeight = FontWeight.Bold, color = StatusDanger)
            shortageList.forEach { s ->
              Text("• $s", color = StatusDanger, style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }

      Button(
        onClick = {
          if (quantity > 0 && shortageList.isEmpty() && productBoms.isNotEmpty()) {
            viewModel.submitAtomicProduction(
              productId = prod.id,
              color = selectedColor,
              size = selectedSize,
              quantity = quantity,
              sewingWagePerItem = wage
            )
          }
        },
        enabled = quantity > 0 && shortageList.isEmpty() && productBoms.isNotEmpty(),
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("تولید، کسر خودکار مواد و ورود به انبار کالا", fontWeight = FontWeight.Bold)
      }
    }
  }
}

// =========================================================================
// 4. QUICK MARKET PRICE UPDATE FORM (تغییر نرخ بازار و بازسنجی محصولات)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickMarketPriceUpdateForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val materials by viewModel.materials.collectAsStateWithLifecycle()
  val products by viewModel.products.collectAsStateWithLifecycle()
  val boms by viewModel.boms.collectAsStateWithLifecycle()

  var selectedMaterialId by remember { mutableStateOf<Long?>(materials.firstOrNull()?.id) }
  val selectedMaterial = materials.find { it.id == selectedMaterialId }

  var newPriceText by remember { mutableStateOf(selectedMaterial?.currentPrice?.toString() ?: "0") }
  var reasonText by remember { mutableStateOf("افزایش نرخ بازار کارخانجات") }

  LaunchedEffect(selectedMaterialId) {
    selectedMaterial?.let {
      newPriceText = it.currentPrice.toString()
    }
  }

  val newPrice = newPriceText.toLongOrNull() ?: 0L

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    SheetHeader(title = "بروزرسانی نرخ بازار مواد و اثرسنجی بها", onBack = onBack)

    Text("انتخاب ماده اولیه:", style = MaterialTheme.typography.labelMedium, color = customColors.textPrimary)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(materials) { mat ->
        val isSelected = mat.id == selectedMaterialId
        FilterChip(
          selected = isSelected,
          onClick = { selectedMaterialId = mat.id },
          label = { Text("${mat.name} (${FinancialCalculationService.formatCurrency(mat.currentPrice)})") }
        )
      }
    }

    selectedMaterial?.let { mat ->
      Card(
        colors = CardDefaults.cardColors(containerColor = customColors.card),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("نام ماده: ${mat.name}", fontWeight = FontWeight.Bold, color = customColors.textPrimary)
          Text("قیمت جاری بازار: ${FinancialCalculationService.formatCurrency(mat.currentPrice)} به ازای هر ${mat.unit}", color = AccentIndigo)
          Text("موجودی فعلی در انبار: ${String.format(java.util.Locale.US, "%.1f", mat.stockQuantity)} ${mat.unit}", color = customColors.textMuted)
        }
      }

      ExecutiveTextField(
        label = "قیمت جدید بازار (تومان به ازای هر ${mat.unit})",
        value = newPriceText,
        onValueChange = { newPriceText = it },
        keyboardType = KeyboardType.Number
      )

      ExecutiveTextField(
        label = "علت یا سند تغییر نرخ",
        value = reasonText,
        onValueChange = { reasonText = it }
      )

      // Affected products preview
      val affectedBoms = boms.filter { it.materialId == mat.id }
      val affectedProducts = products.filter { prod -> affectedBoms.any { it.productId == prod.id } }

      if (affectedProducts.isNotEmpty()) {
        Text("محصولاتی که بهای تمام‌شده آنها با این تغییر بازسنجی می‌شود:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AccentIndigo)
        affectedProducts.forEach { p ->
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("• ${p.name}", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
            Text("بهای جاری: ${FinancialCalculationService.formatCurrency(p.currentCostPrice)}", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          }
        }
      }

      Button(
        onClick = {
          if (newPrice > 0L) {
            viewModel.submitMarketPriceUpdate(
              materialId = mat.id,
              newPrice = newPrice,
              reason = reasonText
            )
          }
        },
        enabled = newPrice > 0L && newPrice != mat.currentPrice,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentAmber),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("بروزرسانی نرخ بازار و محاسبه مجدد بهای تمام‌شده", fontWeight = FontWeight.Bold)
      }
    }
  }
}

// =========================================================================
// 5. QUICK CUSTOMER PAYMENT FORM (دریافت وجه از مشتری)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickCustomerPaymentForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val customers by viewModel.customers.collectAsStateWithLifecycle()
  var selectedCustomerId by remember { mutableStateOf<Long?>(customers.firstOrNull()?.id) }
  val selectedCust = customers.find { it.id == selectedCustomerId }

  var amountText by remember { mutableStateOf("") }
  var trackingCodeText by remember { mutableStateOf("") }
  var notesText by remember { mutableStateOf("") }

  LaunchedEffect(selectedCustomerId) {
    selectedCust?.let {
      if (it.currentDebt > 0L) {
        amountText = it.currentDebt.toString()
      }
    }
  }

  val amount = amountText.toLongOrNull() ?: 0L

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    SheetHeader(title = "دریافت وجه از مشتری (تسویه حساب)", onBack = onBack)

    Text("انتخاب مشتری:", style = MaterialTheme.typography.labelMedium, color = customColors.textPrimary)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(customers) { c ->
        val isSelected = c.id == selectedCustomerId
        FilterChip(
          selected = isSelected,
          onClick = { selectedCustomerId = c.id },
          label = { Text("${c.name} (${FinancialCalculationService.formatCurrency(c.currentDebt)})") }
        )
      }
    }

    selectedCust?.let { c ->
      Card(
        colors = CardDefaults.cardColors(containerColor = customColors.card),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("نام مشتری: ${c.name} - ${c.company}", fontWeight = FontWeight.Bold, color = customColors.textPrimary)
          Text("مانده بدهی فعلی: ${FinancialCalculationService.formatCurrency(c.currentDebt)}", color = if (c.currentDebt > 0) StatusDanger else StatusSuccess, fontWeight = FontWeight.Bold)
          Text("کل خریدهای قبلی: ${FinancialCalculationService.formatCurrency(c.totalPurchases)}", color = customColors.textMuted)
        }
      }

      ExecutiveTextField(
        label = "مبلغ دریافتی (تومان)",
        value = amountText,
        onValueChange = { amountText = it },
        keyboardType = KeyboardType.Number
      )

      ExecutiveTextField(
        label = "شماره پیگیری واریز / چک",
        value = trackingCodeText,
        onValueChange = { trackingCodeText = it }
      )

      ExecutiveTextField(
        label = "توضیحات و بابت",
        value = notesText,
        onValueChange = { notesText = it }
      )

      Button(
        onClick = {
          if (amount > 0L) {
            viewModel.submitCustomerPayment(
              customerId = c.id,
              amount = amount,
              trackingCode = trackingCodeText,
              notes = notesText
            )
          }
        },
        enabled = amount > 0L,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("ثبت واریزی و کسر از بدهی مشتری", fontWeight = FontWeight.Bold)
      }
    }
  }
}

// =========================================================================
// 6. QUICK SUPPLIER PAYMENT FORM (پرداخت به تأمین‌کننده)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSupplierPaymentForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
  var selectedSupplierId by remember { mutableStateOf<Long?>(suppliers.firstOrNull()?.id) }
  val selectedSup = suppliers.find { it.id == selectedSupplierId }

  var amountText by remember { mutableStateOf("") }
  var trackingCodeText by remember { mutableStateOf("") }
  var notesText by remember { mutableStateOf("") }

  LaunchedEffect(selectedSupplierId) {
    selectedSup?.let {
      if (it.currentDebt > 0L) {
        amountText = it.currentDebt.toString()
      }
    }
  }

  val amount = amountText.toLongOrNull() ?: 0L

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    SheetHeader(title = "پرداخت به تأمین‌کننده (تسویه بدهی)", onBack = onBack)

    Text("انتخاب تأمین‌کننده:", style = MaterialTheme.typography.labelMedium, color = customColors.textPrimary)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(suppliers) { s ->
        val isSelected = s.id == selectedSupplierId
        FilterChip(
          selected = isSelected,
          onClick = { selectedSupplierId = s.id },
          label = { Text("${s.name} (${FinancialCalculationService.formatCurrency(s.currentDebt)})") }
        )
      }
    }

    selectedSup?.let { s ->
      Card(
        colors = CardDefaults.cardColors(containerColor = customColors.card),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("تأمین‌کننده: ${s.name} - ${s.storeName}", fontWeight = FontWeight.Bold, color = customColors.textPrimary)
          Text("مانده بدهی به ایشان: ${FinancialCalculationService.formatCurrency(s.currentDebt)}", color = if (s.currentDebt > 0) StatusDanger else StatusSuccess, fontWeight = FontWeight.Bold)
          Text("مجموع پرداخت‌های گذشته: ${FinancialCalculationService.formatCurrency(s.paidAmount)}", color = customColors.textMuted)
        }
      }

      ExecutiveTextField(
        label = "مبلغ پرداختی (تومان)",
        value = amountText,
        onValueChange = { amountText = it },
        keyboardType = KeyboardType.Number
      )

      ExecutiveTextField(
        label = "شماره ارجاع پایا / ساتنا",
        value = trackingCodeText,
        onValueChange = { trackingCodeText = it }
      )

      ExecutiveTextField(
        label = "توضیحات و بابت",
        value = notesText,
        onValueChange = { notesText = it }
      )

      Button(
        onClick = {
          if (amount > 0L) {
            viewModel.submitSupplierPayment(
              supplierId = s.id,
              amount = amount,
              trackingCode = trackingCodeText,
              notes = notesText
            )
          }
        },
        enabled = amount > 0L,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentPurple),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("ثبت حواله و تسویه حساب تأمین‌کننده", fontWeight = FontWeight.Bold)
      }
    }
  }
}

// =========================================================================
// 7. QUICK INVENTORY AUDIT FORM (انبارگردانی و ثبت کسری یا فزونی)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickInventoryAuditForm(
  viewModel: ManufacturingViewModel,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val materials by viewModel.materials.collectAsStateWithLifecycle()
  val inventory by viewModel.inventory.collectAsStateWithLifecycle()

  var itemType by remember { mutableStateOf("MATERIAL") } // MATERIAL, FINISHED_GOOD
  var selectedMaterialId by remember { mutableStateOf<Long?>(materials.firstOrNull()?.id) }
  var selectedInvId by remember { mutableStateOf<Long?>(inventory.firstOrNull()?.id) }

  var adjustmentType by remember { mutableStateOf("INCREASE") } // INCREASE, DECREASE
  var quantityText by remember { mutableStateOf("5") }
  var reasonText by remember { mutableStateOf("انبارگردانی پایان دوره") }
  var notesText by remember { mutableStateOf("") }

  val qty = quantityText.toDoubleOrNull() ?: 0.0

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    SheetHeader(title = "انبارگردانی و تعدیل موجودی (دفتر کل انبار)", onBack = onBack)

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      FilterChip(
        selected = itemType == "MATERIAL",
        onClick = { itemType = "MATERIAL" },
        label = { Text("مواد اولیه و ملزومات") }
      )
      FilterChip(
        selected = itemType == "FINISHED_GOOD",
        onClick = { itemType = "FINISHED_GOOD" },
        label = { Text("محصولات آماده") }
      )
    }

    if (itemType == "MATERIAL") {
      Text("انتخاب ماده:", style = MaterialTheme.typography.labelMedium, color = customColors.textPrimary)
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(materials) { m ->
          val isSelected = m.id == selectedMaterialId
          FilterChip(
            selected = isSelected,
            onClick = { selectedMaterialId = m.id },
            label = { Text("${m.name} (${m.stockQuantity.toInt()} ${m.unit})") }
          )
        }
      }
    } else {
      Text("انتخاب کالا:", style = MaterialTheme.typography.labelMedium, color = customColors.textPrimary)
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(inventory) { inv ->
          val isSelected = inv.id == selectedInvId
          FilterChip(
            selected = isSelected,
            onClick = { selectedInvId = inv.id },
            label = { Text("${inv.name} (${inv.totalStock} عدد)") }
          )
        }
      }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      FilterChip(
        selected = adjustmentType == "INCREASE",
        onClick = { adjustmentType = "INCREASE" },
        label = { Text("افزایش موجودی (اضافه انبار +)") }
      )
      FilterChip(
        selected = adjustmentType == "DECREASE",
        onClick = { adjustmentType = "DECREASE" },
        label = { Text("کاهش موجودی (کسری انبار -)") }
      )
    }

    ExecutiveTextField(
      label = "مقدار تعدیل",
      value = quantityText,
      onValueChange = { quantityText = it },
      keyboardType = KeyboardType.Decimal
    )

    ExecutiveTextField(
      label = "دلیل تعدیل (مثلا مغایرت شمارش، آسیب‌دیدگی)",
      value = reasonText,
      onValueChange = { reasonText = it }
    )

    ExecutiveTextField(
      label = "توضیحات تکمیلی",
      value = notesText,
      onValueChange = { notesText = it }
    )

    Button(
      onClick = {
        if (qty > 0.0) {
          if (itemType == "MATERIAL") {
            val mat = materials.find { it.id == selectedMaterialId } ?: return@Button
            viewModel.submitInventoryAdjustment(
              itemType = "MATERIAL",
              itemId = mat.id,
              itemCode = mat.code,
              itemName = mat.name,
              adjustmentType = adjustmentType,
              quantity = qty,
              reason = reasonText,
              notes = notesText
            )
          } else {
            val inv = inventory.find { it.id == selectedInvId } ?: return@Button
            viewModel.submitInventoryAdjustment(
              itemType = "FINISHED_GOOD",
              itemId = inv.id,
              itemCode = inv.code,
              itemName = inv.name,
              adjustmentType = adjustmentType,
              quantity = qty,
              reason = reasonText,
              notes = notesText
            )
          }
        }
      },
      enabled = qty > 0.0,
      modifier = Modifier.fillMaxWidth().height(48.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
      shape = RoundedCornerShape(8.dp)
    ) {
      Text("تأیید انبارگردانی و ثبت در دفتر کل انبار", fontWeight = FontWeight.Bold)
    }
  }
}

/**
 * دیالوگ به‌روزرسانی قیمت روز (بدون ثبت خرید جدید)
 * بند ۳۹: کاربر می‌تواند قیمت روز را تغییر دهد بدون اینکه موجودی فیزیکی تغییر کند
 */
@Composable
fun PriceUpdateDialog(
  viewModel: ManufacturingViewModel,
  onDismiss: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val target by viewModel.priceUpdateTarget.collectAsStateWithLifecycle()

  if (target == null) return
  val t = target!!

  var pricePerMeterText by remember(t.id) { mutableStateOf(t.currentPricePerMeter.toString()) }
  var pricePerKgText by remember(t.id) { mutableStateOf(t.currentPricePerKg.toString()) }
  var reasonText by remember(t.id) { mutableStateOf("تغییر قیمت بازار") }

  val isFabricRoll = t.type == com.example.viewmodel.PriceUpdateType.FABRIC_ROLL

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("به‌روزرسانی قیمت روز", fontWeight = FontWeight.Bold, color = customColors.textPrimary)
        Text(t.title, fontSize = 11.sp, color = customColors.textMuted)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // اطلاعات فعلی
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(customColors.secondaryBg)
            .padding(10.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("قیمت قبلی:", fontSize = 11.sp, color = customColors.textMuted)
            if (isFabricRoll) {
              Text("متر: ${CurrencyHelper.formatToman(t.currentPricePerMeter)} • کیلو: ${CurrencyHelper.formatToman(t.currentPricePerKg)}",
                fontSize = 12.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            } else {
              Text("هر ${t.unit}: ${CurrencyHelper.formatToman(t.currentPricePerMeter)}",
                fontSize = 12.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            }
          }
        }

        // فیلدهای جدید
        if (isFabricRoll) {
          ExecutiveTextField(
            label = "قیمت جدید هر متر (تومان)",
            value = pricePerMeterText,
            keyboardType = KeyboardType.Number,
            onValueChange = {
              pricePerMeterText = it
              val pm = it.toLongOrNull() ?: 0L
              if (pm > 0L && t.metersPerKg > 0.0) {
                pricePerKgText = (pm * t.metersPerKg).toLong().toString()
              }
            }
          )
          ExecutiveTextField(
            label = "قیمت جدید هر کیلو (تومان)",
            value = pricePerKgText,
            keyboardType = KeyboardType.Number,
            onValueChange = {
              pricePerKgText = it
              val pk = it.toLongOrNull() ?: 0L
              if (pk > 0L && t.metersPerKg > 0.0) {
                pricePerMeterText = (pk / t.metersPerKg).toLong().toString()
              }
            }
          )
        } else {
          ExecutiveTextField(
            label = "قیمت جدید هر ${t.unit} (تومان)",
            value = pricePerMeterText,
            keyboardType = KeyboardType.Number,
            onValueChange = { pricePerMeterText = it }
          )
        }

        ExecutiveTextField(
          label = "دلیل تغییر قیمت",
          value = reasonText,
          onValueChange = { reasonText = it }
        )

        // هشدار
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(StatusWarning.copy(alpha = 0.12f))
            .padding(8.dp)
        ) {
          Text(
            "⚠ این عملیات موجودی فیزیکی را تغییر نمی‌دهد. فقط قیمت روز و بهای محاسباتی به‌روزرسانی می‌شود.",
            fontSize = 11.sp, color = StatusWarning
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val pm = pricePerMeterText.toLongOrNull() ?: 0L
          val pk = pricePerKgText.toLongOrNull() ?: 0L
          if (isFabricRoll) {
            viewModel.updateRollPrice(t.id, pm, pk, reasonText)
          } else {
            viewModel.updateMaterialPrice(t.id, pm, reasonText)
          }
          viewModel.closePriceUpdateDialog()
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
      ) {
        Text("ثبت قیمت روز", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = {
        viewModel.closePriceUpdateDialog()
        onDismiss()
      }) {
        Text("انصراف", color = customColors.textMuted)
      }
    },
    containerColor = customColors.cardElevated
  )
}
