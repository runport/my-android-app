package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import com.example.data.model.CategoryEntity
import com.example.data.model.ColorEntity
import com.example.data.model.InventoryLedgerEntity
import com.example.data.model.MaterialEntity
import com.example.data.model.MaterialUnitEntity
import com.example.data.model.PriceChangeReasonEntity
import com.example.data.model.ProductBOMEntity
import com.example.data.model.ProductEntity
import com.example.data.model.PurchaseOrderEntity
import com.example.data.model.SalesChannelEntity
import com.example.data.model.SizeEntity
import com.example.data.service.FinancialCalculationService
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.model.DashboardChartType
import com.example.data.model.DashboardLayoutArrangement
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerEntity
import com.example.data.model.CuttingEntity
import com.example.data.model.ModelStandardEntity
import com.example.data.model.OrderStatusHistoryEntity
import com.example.data.model.ProductionEntity
import com.example.data.model.SaleOrderEntity
import com.example.data.model.SaleOrderStatus
import com.example.data.model.SupplierEntity
import com.example.ui.components.CurrencyHelper
import com.example.ui.components.FixedCostBenchmarkCard
import com.example.ui.components.StatusChip
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.dialogs.SupplierRegistrationDialog
import com.example.viewmodel.ManufacturingViewModel
import com.example.viewmodel.MoreSubSection
import com.example.viewmodel.QuickActionType
import com.example.ui.screens.ReadyGoodsScreen
import com.example.ui.dialogs.DeleteAllDataConfirmDialog
import com.example.ui.dialogs.RestoreFromFileDialog

@Composable
fun MoreHubScreen(
  viewModel: ManufacturingViewModel,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  val selectedSubSection by viewModel.selectedSubSection.collectAsState()
  val orders by viewModel.salesOrders.collectAsState()
  val productions by viewModel.productions.collectAsState()
  val cuttings by viewModel.cuttings.collectAsState()
  val customers by viewModel.customers.collectAsState()
  val suppliers by viewModel.suppliers.collectAsState()
  val standards by viewModel.standards.collectAsState()
  val isDarkTheme by viewModel.isDarkTheme.collectAsState()
  val selectedFont by viewModel.selectedFont.collectAsState()
  val factorySettings by viewModel.factorySettings.collectAsState()
  val statusHistory by viewModel.orderStatusHistory.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val products by viewModel.products.collectAsState()
  val materials by viewModel.materials.collectAsState()
  val colors by viewModel.colors.collectAsState()
  val sizes by viewModel.sizes.collectAsState()
  val boms by viewModel.boms.collectAsState()
  val materialUnits by viewModel.materialUnits.collectAsState()
  val salesChannels by viewModel.salesChannels.collectAsState()
  val purchaseOrders by viewModel.purchaseOrders.collectAsState()
  val inventoryLedger by viewModel.inventoryLedger.collectAsState()
  val context = LocalContext.current

  var showResetConfirmDialog by remember { mutableStateOf(false) }
  var showRestoreBackupDialog by remember { mutableStateOf(false) }
  var showQuickCalcDialog by remember { mutableStateOf(false) }
  var showAddSupplierDialog by remember { mutableStateOf(false) }
  var supplierToDelete by remember { mutableStateOf<SupplierEntity?>(null) }

  var masterDataTab by remember { mutableStateOf("PRODUCTS") }
  var showAddProductDialog by remember { mutableStateOf(false) }
  var showAddMaterialDialog by remember { mutableStateOf(false) }
  var showAddBomDialog by remember { mutableStateOf(false) }
  var showAddCategoryDialog by remember { mutableStateOf(false) }
  var showAddColorDialog by remember { mutableStateOf(false) }
  var showAddSizeDialog by remember { mutableStateOf(false) }

  // -------------------------------------------------------------
  // DIALOG: ADD PRODUCT
  // -------------------------------------------------------------
  if (showAddProductDialog) {
    var code by remember { mutableStateOf("PRD-${(100..999).random()}") }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("پوشاک") }
    var wageText by remember { mutableStateOf("85000") }
    var freightText by remember { mutableStateOf("15000") }
    var overheadText by remember { mutableStateOf("20000") }
    var marginText by remember { mutableStateOf("35") }
    var sellingPriceText by remember { mutableStateOf("0") }

    AlertDialog(
      onDismissRequest = { showAddProductDialog = false },
      title = { Text("تعریف محصول جدید", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("کد کالا") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام محصول") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("دسته‌بندی") }, modifier = Modifier.fillMaxWidth())
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(value = wageText, onValueChange = { wageText = it }, label = { Text("اجرت دوخت") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = freightText, onValueChange = { freightText = it }, label = { Text("کرایه حمل") }, modifier = Modifier.weight(1f))
          }
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(value = overheadText, onValueChange = { overheadText = it }, label = { Text("سربار ثابت") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = marginText, onValueChange = { marginText = it }, label = { Text("درصد سود") }, modifier = Modifier.weight(1f))
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (code.isNotBlank() && name.isNotBlank()) {
              viewModel.saveProduct(
                code = code,
                name = name,
                category = category,
                targetMarginPercent = marginText.toDoubleOrNull() ?: 35.0,
                overheadCost = overheadText.toLongOrNull() ?: 20000L,
                allocatedFreightCost = freightText.toLongOrNull() ?: 15000L,
                sellingPriceOverride = sellingPriceText.toLongOrNull() ?: 0L
              )
              showAddProductDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
        ) {
          Text("ذخیره کالا", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddProductDialog = false }) { Text("انصراف") }
      }
    )
  }

  // -------------------------------------------------------------
  // DIALOG: ADD MATERIAL
  // -------------------------------------------------------------
  if (showAddMaterialDialog) {
    var code by remember { mutableStateOf("MAT-${(100..999).random()}") }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("پارچه") }
    var unit by remember { mutableStateOf("متر") }
    var priceText by remember { mutableStateOf("150000") }
    var minStockText by remember { mutableStateOf("20") }

    AlertDialog(
      onDismissRequest = { showAddMaterialDialog = false },
      title = { Text("ثبت ماده اولیه / ملزومات جدید", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("کد ماده") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام ماده (مثلا پارچه گلکسی)") }, modifier = Modifier.fillMaxWidth())
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("دسته") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("واحد (متر/کیلو/عدد)") }, modifier = Modifier.weight(1f))
          }
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(value = priceText, onValueChange = { priceText = it }, label = { Text("قیمت پایه (تومان)") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = minStockText, onValueChange = { minStockText = it }, label = { Text("حداقل موجودی هشدار") }, modifier = Modifier.weight(1f))
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (code.isNotBlank() && name.isNotBlank()) {
              viewModel.saveMaterial(
                code = code,
                name = name,
                category = category,
                unit = unit,
                currentPrice = priceText.toLongOrNull() ?: 0L,
                minStock = minStockText.toDoubleOrNull() ?: 10.0
              )
              showAddMaterialDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
        ) {
          Text("ذخیره ماده اولیه", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddMaterialDialog = false }) { Text("انصراف") }
      }
    )
  }

  // -------------------------------------------------------------
  // DIALOG: ADD BOM ROW
  // -------------------------------------------------------------
  if (showAddBomDialog) {
    var selectedProdId by remember { mutableStateOf(products.firstOrNull()?.id ?: 0L) }
    var selectedMatId by remember { mutableStateOf(materials.firstOrNull()?.id ?: 0L) }
    var qtyText by remember { mutableStateOf("1.2") }

    val prod = products.find { it.id == selectedProdId }
    val mat = materials.find { it.id == selectedMatId }

    AlertDialog(
      onDismissRequest = { showAddBomDialog = false },
      title = { Text("افزودن ردیف به فرمول تولید (BOM)", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("انتخاب کالا:", style = MaterialTheme.typography.labelSmall)
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(products) { p ->
              FilterChip(
                selected = p.id == selectedProdId,
                onClick = { selectedProdId = p.id },
                label = { Text(p.name) }
              )
            }
          }

          Text("انتخاب ماده اولیه یا خرج‌کار:", style = MaterialTheme.typography.labelSmall)
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(materials) { m ->
              FilterChip(
                selected = m.id == selectedMatId,
                onClick = { selectedMatId = m.id },
                label = { Text("${m.name} (${m.unit})") }
              )
            }
          }

          OutlinedTextField(
            value = qtyText,
            onValueChange = { qtyText = it },
            label = { Text("مقدار مصرف برای ۱ عدد محصول (${mat?.unit ?: ""})") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (prod != null && mat != null) {
              val qty = qtyText.toDoubleOrNull() ?: 1.0
              viewModel.saveBOM(
                productId = prod.id,
                productCode = prod.code,
                materialId = mat.id,
                materialName = mat.name,
                unit = mat.unit,
                qty = qty,
                rate = mat.currentPrice
              )
              showAddBomDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
        ) {
          Text("افزودن به فرمول ساخت", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddBomDialog = false }) { Text("انصراف") }
      }
    )
  }

  // -------------------------------------------------------------
  // DIALOG: ADD CATEGORY
  // -------------------------------------------------------------
  if (showAddCategoryDialog) {
    var catCode by remember { mutableStateOf("CAT-${(10..99).random()}") }
    var catName by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddCategoryDialog = false },
      title = { Text("ثبت دسته‌بندی جدید", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = catCode, onValueChange = { catCode = it }, label = { Text("کد دسته‌بندی") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = catName, onValueChange = { catName = it }, label = { Text("نام دسته‌بندی (مثلاً اسلش، هودی، تیشرت)") }, modifier = Modifier.fillMaxWidth())
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (catName.isNotBlank()) {
              viewModel.saveCategory(catCode, catName)
              showAddCategoryDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
        ) {
          Text("ذخیره", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddCategoryDialog = false }) { Text("انصراف") }
      }
    )
  }

  // -------------------------------------------------------------
  // DIALOG: ADD COLOR
  // -------------------------------------------------------------
  if (showAddColorDialog) {
    var colorName by remember { mutableStateOf("") }
    var colorHex by remember { mutableStateOf("#1E293B") }

    AlertDialog(
      onDismissRequest = { showAddColorDialog = false },
      title = { Text("ثبت رنگ جدید", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = colorName, onValueChange = { colorName = it }, label = { Text("نام رنگ (مشکی، طوسی ملانژ، زیتونی)") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = colorHex, onValueChange = { colorHex = it }, label = { Text("کد هگزادسیمال رنگ (مثلاً #1E293B)") }, modifier = Modifier.fillMaxWidth())
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (colorName.isNotBlank()) {
              viewModel.saveColor(colorName, colorHex)
              showAddColorDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
        ) {
          Text("ذخیره", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddColorDialog = false }) { Text("انصراف") }
      }
    )
  }

  // -------------------------------------------------------------
  // DIALOG: ADD SIZE
  // -------------------------------------------------------------
  if (showAddSizeDialog) {
    var sizeName by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddSizeDialog = false },
      title = { Text("ثبت سایز جدید", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = sizeName, onValueChange = { sizeName = it }, label = { Text("نام سایز (M, L, XL, 2XL, فری سایز)") }, modifier = Modifier.fillMaxWidth())
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (sizeName.isNotBlank()) {
              viewModel.saveSize(sizeName)
              showAddSizeDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
        ) {
          Text("ذخیره", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddSizeDialog = false }) { Text("انصراف") }
      }
    )
  }

  if (showAddSupplierDialog) {
    SupplierRegistrationDialog(
      onDismiss = { showAddSupplierDialog = false },
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
        showAddSupplierDialog = false
      }
    )
  }

  if (supplierToDelete != null) {
    val sup = supplierToDelete!!
    AlertDialog(
      onDismissRequest = { supplierToDelete = null },
      title = {
        Text("حذف تأمین‌کننده", color = StatusDanger, fontWeight = FontWeight.Bold)
      },
      text = {
        Text(
          "آیا از حذف تأمین‌کننده «${sup.name} - ${sup.storeName}» اطمینان دارید؟ این عملیات قابل بازگشت نخواهد بود.",
          color = customColors.textSecondary,
          style = MaterialTheme.typography.bodyMedium
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteSupplier(sup)
            supplierToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
        ) {
          Text("حذف دائم", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { supplierToDelete = null }) {
          Text("انصراف", color = customColors.textMuted)
        }
      }
    )
  }

  if (showResetConfirmDialog) {
    DeleteAllDataConfirmDialog(
      onConfirm = {
        viewModel.resetToDemoData()
        showResetConfirmDialog = false
      },
      onDismiss = { showResetConfirmDialog = false }
    )
  }

  if (showRestoreBackupDialog) {
    RestoreFromFileDialog(
      onConfirm = { jsonText ->
        viewModel.restoreBackupData(jsonText)
        showRestoreBackupDialog = false
      },
      onDismiss = { showRestoreBackupDialog = false }
    )
  }

  if (showQuickCalcDialog) {
    var metersInput by remember { mutableStateOf("100") }
    var gsmInput by remember { mutableStateOf("300") }
    var widthCmInput by remember { mutableStateOf("150") }

    var fabricPricePerM by remember { mutableStateOf("195000") }
    var consumptionPerPiece by remember { mutableStateOf("1.2") }
    var sewingWageInput by remember { mutableStateOf("55000") }
    var accessoriesInput by remember { mutableStateOf("25000") }
    var overheadInput by remember { mutableStateOf("15000") }
    var profitPercentInput by remember { mutableStateOf("40") }

    val metersVal = metersInput.toDoubleOrNull() ?: 0.0
    val gsmVal = gsmInput.toDoubleOrNull() ?: 0.0
    val widthMeters = (widthCmInput.toDoubleOrNull() ?: 150.0) / 100.0
    val calculatedWeightKg = (metersVal * widthMeters * gsmVal) / 1000.0

    val fPrice = fabricPricePerM.toLongOrNull() ?: 0L
    val cons = consumptionPerPiece.toDoubleOrNull() ?: 1.0
    val wage = sewingWageInput.toLongOrNull() ?: 0L
    val acc = accessoriesInput.toLongOrNull() ?: 0L
    val overh = overheadInput.toLongOrNull() ?: 0L
    val profitPct = profitPercentInput.toDoubleOrNull() ?: 40.0

    val unitFabricCost = (cons * fPrice).toLong()
    val totalUnitCost = unitFabricCost + wage + acc + overh
    val suggestedSalePrice = (totalUnitCost * (1.0 + profitPct / 100.0)).toLong()

    AlertDialog(
      onDismissRequest = { showQuickCalcDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.Calculate, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(24.dp))
          Text("ماشین‌حساب سریع کارگاه دوزندگی", fontWeight = FontWeight.Bold, color = customColors.textPrimary)
        }
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("۱. تبدیل متراژ به کیلوگرم پارچه (بر اساس عرض و گرماژ):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AccentIndigo)
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
              value = metersInput,
              onValueChange = { metersInput = it },
              label = { Text("متراژ (m)", fontSize = 10.sp) },
              modifier = Modifier.weight(1f),
              singleLine = true
            )
            OutlinedTextField(
              value = gsmInput,
              onValueChange = { gsmInput = it },
              label = { Text("گرماژ (GSM)", fontSize = 10.sp) },
              modifier = Modifier.weight(1f),
              singleLine = true
            )
            OutlinedTextField(
              value = widthCmInput,
              onValueChange = { widthCmInput = it },
              label = { Text("عرض (cm)", fontSize = 10.sp) },
              modifier = Modifier.weight(1f),
              singleLine = true
            )
          }
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(AccentCyan.copy(alpha = 0.12f))
              .padding(8.dp)
          ) {
            Text(
              "وزن تخمینی: ${String.format(java.util.Locale.US, "%.2f", calculatedWeightKg)} کیلوگرم",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = AccentCyan
            )
          }

          Text("۲. محاسبه سرانگشتی بهای تمام‌شده و قیمت فروش هر دست:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AccentIndigo)
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
              value = fabricPricePerM,
              onValueChange = { fabricPricePerM = it },
              label = { Text("قیمت پارچه/متر", fontSize = 9.sp) },
              modifier = Modifier.weight(1f),
              singleLine = true
            )
            OutlinedTextField(
              value = consumptionPerPiece,
              onValueChange = { consumptionPerPiece = it },
              label = { Text("مصرف/دست (متر)", fontSize = 9.sp) },
              modifier = Modifier.weight(1f),
              singleLine = true
            )
            OutlinedTextField(
              value = sewingWageInput,
              onValueChange = { sewingWageInput = it },
              label = { Text("اجرت دوخت", fontSize = 9.sp) },
              modifier = Modifier.weight(1f),
              singleLine = true
            )
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
              value = accessoriesInput,
              onValueChange = { accessoriesInput = it },
              label = { Text("ملزومات (کش/زیپ)", fontSize = 9.sp) },
              modifier = Modifier.weight(1f),
              singleLine = true
            )
            OutlinedTextField(
              value = overheadInput,
              onValueChange = { overheadInput = it },
              label = { Text("سربار/واحد", fontSize = 9.sp) },
              modifier = Modifier.weight(1f),
              singleLine = true
            )
            OutlinedTextField(
              value = profitPercentInput,
              onValueChange = { profitPercentInput = it },
              label = { Text("درصد سود %", fontSize = 9.sp) },
              modifier = Modifier.weight(1f),
              singleLine = true
            )
          }
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(StatusSuccess.copy(alpha = 0.12f))
              .padding(8.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                "بهای تمام‌شده هر دست: ${CurrencyHelper.formatNumber(totalUnitCost)} تومان",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = customColors.textPrimary
              )
              Text(
                "قیمت فروش پیشنهادی: ${CurrencyHelper.formatNumber(suggestedSalePrice)} تومان (${profitPct.toInt()}% سود)",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = StatusSuccess
              )
            }
          }
        }
      },
      confirmButton = {
        Button(onClick = { showQuickCalcDialog = false }) {
          Text("بستن")
        }
      },
      containerColor = customColors.cardElevated
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "مدیریت ماژولار و تنظیمات کارخانه",
        style = MaterialTheme.typography.titleLarge,
        color = customColors.textPrimary,
        fontWeight = FontWeight.Bold
      )
    }

    // 2. Horizontal Sub-section Switcher
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MoreSubSection.values().forEach { sub ->
          val isSelected = sub == selectedSubSection
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(if (isSelected) customColors.cardElevated else customColors.secondaryBg)
              .border(1.dp, if (isSelected) AccentBlue else customColors.border, RoundedCornerShape(20.dp))
              .clickable { viewModel.setSubSection(sub) }
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .testTag("sub_tab_${sub.name.lowercase()}"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = sub.title,
              style = MaterialTheme.typography.labelSmall,
              color = if (isSelected) customColors.textPrimary else customColors.textMuted,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }
    }

    // 3. Sub-section Content
    when (selectedSubSection) {
      MoreSubSection.ORDERS -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("لیست سفارشات مشتریان و رهگیری", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = { viewModel.openReserveOrderDialog() },
                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("رزرو جدید", style = MaterialTheme.typography.labelSmall)
              }
              Button(
                onClick = { viewModel.openQuickAction(QuickActionType.SALE) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("فروش فوری", style = MaterialTheme.typography.labelSmall)
              }
            }
          }
        }
        items(orders) { order ->
          OrderTimelineCard(
            order = order,
            history = statusHistory.filter { it.orderId == order.id },
            onStatusChange = { targetStatus ->
              viewModel.updateOrderStatus(order.id, targetStatus, "تغییر وضعیت از رهگیری سفارشات")
            },
            onEdit = { viewModel.startEditOrder(order) }
          )
        }
      }

      MoreSubSection.PRODUCTION -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("بچ‌های فعال تولید در کارگاه", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            Button(
              onClick = { viewModel.openQuickAction(QuickActionType.PRODUCTION) },
              colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("ثبت تولید", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
        items(productions) { prod ->
          ProductionBatchCard(prod = prod)
        }
      }

      MoreSubSection.READY_GOODS -> {
        item {
          ReadyGoodsScreen(viewModel = viewModel)
        }
      }

      MoreSubSection.CUTTING -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("میز برش و پایش مصرف پارچه", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = { viewModel.openQuickAction(QuickActionType.MULTI_CUT) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("برش چند مدلی", style = MaterialTheme.typography.labelSmall)
              }
              Button(
                onClick = { viewModel.openQuickAction(QuickActionType.CUTTING) },
                colors = ButtonDefaults.buttonColors(containerColor = StatusWarning),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("برش ساده", style = MaterialTheme.typography.labelSmall)
              }
            }
          }
        }
        items(cuttings) { cut ->
          CuttingCard(cut = cut)
        }
      }

      MoreSubSection.CUSTOMERS -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("پرونده خریداران و بنکداران", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            Button(
              onClick = { viewModel.openQuickAction(QuickActionType.CUSTOMER) },
              colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.AccentPurple),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("مشتری جدید", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
        items(customers) { cust ->
          CustomerProfileCard(
            customer = cust,
            onEdit = { viewModel.startEditCustomer(cust) }
          )
        }
      }

      MoreSubSection.SUPPLIERS -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("تأمین‌کنندگان پارچه و نخ", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            Button(
              onClick = { showAddSupplierDialog = true },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
              modifier = Modifier.height(36.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
              Spacer(Modifier.size(4.dp))
              Text("افزودن تأمین‌کننده جدید", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
        if (suppliers.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(customColors.card)
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Text("هیچ تأمین‌کننده‌ای ثبت نشده است. با دکمه بالا تأمین‌کننده جدید اضافه کنید.", color = customColors.textMuted, fontSize = 12.sp)
            }
          }
        }
        items(suppliers) { sup ->
          SupplierCard(
            sup = sup,
            onDelete = { supplierToDelete = sup }
          )
        }
      }

      MoreSubSection.SETTINGS -> {
        // 0. User Role (RBAC) Switcher
        item {
          val activeRole by viewModel.currentUserRole.collectAsState()
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentBlue.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = AccentBlue
                  )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                  Text(
                    text = "نقش کاربری فعال (سطح دسترسی RBAC)",
                    style = MaterialTheme.typography.titleSmall,
                    color = customColors.textPrimary,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "نقش فعلی: ${activeRole.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textSecondary
                  )
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                com.example.data.model.UserRole.values().forEach { role ->
                  val isSelected = activeRole == role
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) AccentIndigo else customColors.cardElevated)
                      .clickable { viewModel.setCurrentUserRole(role) }
                      .padding(horizontal = 12.dp, vertical = 8.dp)
                  ) {
                    Text(
                      text = role.title,
                      style = MaterialTheme.typography.labelMedium,
                      color = if (isSelected) Color.White else customColors.textPrimary,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  }
                }
              }
            }
          }
        }

        // 1. Theme Configuration Setting (تم تاریک و روشن)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentIndigo.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = null,
                    tint = AccentIndigo
                  )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                  Text(
                    text = "ظاهر برنامه (تم تاریک / روشن)",
                    style = MaterialTheme.typography.titleSmall,
                    color = customColors.textPrimary,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = if (isDarkTheme) "حالت فعال: تم تاریک مدیریتی (Dark Executive)" else "حالت فعال: تم روشن ادیتوریال (Editorial Light)",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                  )
                }
              }

              Switch(
                checked = isDarkTheme,
                onCheckedChange = { viewModel.toggleTheme() },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = AccentIndigo,
                  uncheckedThumbColor = Color.White,
                  uncheckedTrackColor = customColors.border
                )
              )
            }
          }
        }

        // 1.1 Font Selection Setting (انتخاب قلم و فونت فارسی برنامه - یکان، وزیرمتن، شبنم، سیستم)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentIndigo.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Palette, contentDescription = null, tint = AccentIndigo)
                }
                Column {
                  Text(
                    text = "قلم و فونت فارسی برنامه",
                    style = MaterialTheme.typography.titleSmall,
                    color = customColors.textPrimary,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "قلم فعال: ${selectedFont.displayName} - استاندارد و شیک متناسب با گوشی",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                  )
                }
              }

              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                com.example.ui.theme.PersianFont.values().forEach { fontOption ->
                  val isSelected = (selectedFont == fontOption)
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(10.dp))
                      .background(if (isSelected) AccentIndigo.copy(alpha = 0.15f) else customColors.secondaryBg)
                      .border(1.dp, if (isSelected) AccentIndigo else customColors.border, RoundedCornerShape(10.dp))
                      .clickable { viewModel.setPersianFont(fontOption) }
                      .padding(horizontal = 14.dp, vertical = 10.dp)
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                          text = fontOption.displayName,
                          style = MaterialTheme.typography.bodyMedium,
                          color = if (isSelected) AccentIndigo else customColors.textPrimary,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                          text = fontOption.description,
                          style = MaterialTheme.typography.bodySmall,
                          color = customColors.textMuted,
                          fontSize = 11.sp
                        )
                      }
                      if (isSelected) {
                        Box(
                          modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentIndigo)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                          Text("فعال", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // 1.2 System Notification Settings (اعلان‌ها و هشدارهای استاتوس‌بار سیستم بالای گوشی)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(AccentBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AccentBlue)
                  }
                  Column {
                    Text(
                      text = "اعلان‌ها در بالای گوشی (Status Bar)",
                      style = MaterialTheme.typography.titleSmall,
                      color = customColors.textPrimary,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "نمایش مستقیم هشدارهای کسری انبار و رویدادها در نوار بالای گوشی",
                      style = MaterialTheme.typography.bodySmall,
                      color = customColors.textMuted
                    )
                  }
                }

                Switch(
                  checked = factorySettings.systemNotificationsEnabled,
                  onCheckedChange = { viewModel.toggleSystemNotifications(it) },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AccentBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = customColors.border
                  )
                )
              }

              // Test notification button
              OutlinedButton(
                onClick = { viewModel.sendTestNotification(context) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue)
              ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text("تست و ارسال هشدار نمونه به بالای گوشی", style = MaterialTheme.typography.labelMedium)
              }
            }
          }
        }

        // 2. Dashboard Chart Selection Setting (انتخاب نوع چارت آمارگیر صفحه اول)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentBlue.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.BarChart, contentDescription = null, tint = AccentBlue)
                }
                Column {
                  Text("نوع چارت آمارگیر صفحه اول", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  Text("انتخاب میان نمودار خطی مساحتی، ستونی میله‌ای، دایره‌ای یا ترکیبی", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                }
              }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DashboardChartType.values().forEach { cType ->
                  val isSelected = factorySettings.dashboardChartType == cType.name
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) AccentBlue.copy(alpha = 0.2f) else customColors.secondaryBg)
                      .border(1.dp, if (isSelected) AccentBlue else customColors.border, RoundedCornerShape(8.dp))
                      .clickable { viewModel.updateDashboardChartType(cType) }
                      .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = cType.title,
                      style = MaterialTheme.typography.labelSmall,
                      color = if (isSelected) AccentBlue else customColors.textMuted,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      maxLines = 1
                    )
                  }
                }
              }
            }
          }
        }

        // 3. Dashboard Layout Arrangement Setting (انتخاب نوع چیدمان بخش‌ها در صفحات اول)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentCyan.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.ViewQuilt, contentDescription = null, tint = AccentCyan)
                }
                Column {
                  Text("نوع چیدمان بخش‌ها در صفحه اصلی", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  Text("ترتیب اولویت کارت‌ها، شاخص‌ها، چارت‌ها و هشدارها در داشبورد", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                }
              }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DashboardLayoutArrangement.values().forEach { lType ->
                  val isSelected = factorySettings.dashboardLayout == lType.name
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) AccentCyan.copy(alpha = 0.2f) else customColors.secondaryBg)
                      .border(1.dp, if (isSelected) AccentCyan else customColors.border, RoundedCornerShape(8.dp))
                      .clickable { viewModel.updateDashboardLayout(lType) }
                      .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = lType.title,
                      style = MaterialTheme.typography.labelSmall,
                      color = if (isSelected) AccentCyan else customColors.textMuted,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      maxLines = 1
                    )
                  }
                }
              }
            }
          }
        }

        // 4. Stock Alerts Threshold Summary Setting (حد آستانه هشدار کسری بر اساس تعداد و وزن)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(StatusWarning.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = StatusWarning)
                  }
                  Column {
                    Text("حد آستانه هشدارها و نوتیفیکیشن کسری", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                    Text("بررسی خودکار طاقه (تعداد و وزن)، کار آماده و ملزومات", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                  }
                }

                IconButton(onClick = { viewModel.openQuickAction(QuickActionType.SETTINGS_EDIT) }) {
                  Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = AccentIndigo)
                }
              }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(customColors.secondaryBg)
                    .padding(8.dp)
                ) {
                  Column {
                    Text("حداقل طاقه", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                    Text("${factorySettings.minFabricRollsThreshold} طاقه (${factorySettings.minFabricWeightKgThreshold.toInt()} کیلو)", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  }
                }
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(customColors.secondaryBg)
                    .padding(8.dp)
                ) {
                  Column {
                    Text("حداقل کار آماده", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                    Text("${factorySettings.minReadyGoodsCountThreshold} عدد", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  }
                }
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(customColors.secondaryBg)
                    .padding(8.dp)
                ) {
                  Column {
                    Text("حداقل ملزومات", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                    Text("${factorySettings.minAccessoriesWeightKgThreshold.toInt()} کیلوگرم", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }

        // 5. Data Management, Cache & Reset Setting (ذخیره داده‌ها، پاک کردن کش و بازنشانی نمونه)
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(customColors.card)
              .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
              .padding(16.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentIndigo.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Settings, contentDescription = null, tint = AccentIndigo)
                }
                Column {
                  Text("مدیریت داده‌ها و نگهداری سیستم", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
                  Text("پشتیبان‌گیری، پاکسازی حافظه موقت و بازنشانی دیتابیس", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                }
              }

              // Action Buttons
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // A. Backup / Export all data
                Button(
                  onClick = { viewModel.exportAllDataJson(context) },
                  modifier = Modifier.fillMaxWidth().height(42.dp),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                ) {
                  Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(Modifier.size(8.dp))
                  Text("ذخیره تمام داده‌ها برای برنامه‌های دیگر (JSON / خروجی)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                // Restore / Import Backup Data
                OutlinedButton(
                  onClick = { showRestoreBackupDialog = true },
                  modifier = Modifier.fillMaxWidth().height(42.dp),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue)
                ) {
                  Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentBlue)
                  Spacer(Modifier.size(8.dp))
                  Text("بازیابی اطلاعات از فایل پشتیبان (JSON / Import)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                // Quick Workshop Calculator
                FilledTonalButton(
                  onClick = { showQuickCalcDialog = true },
                  modifier = Modifier.fillMaxWidth().height(42.dp),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.filledTonalButtonColors(containerColor = AccentCyan.copy(alpha = 0.15f), contentColor = AccentCyan)
                ) {
                  Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(Modifier.size(8.dp))
                  Text("ماشین‌حساب سریع کارگاه (تبدیل متراژ/وزن و بهای تمام‌شده)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                // B. Clear temporary cache
                OutlinedButton(
                  onClick = { viewModel.clearTemporaryCache(context) },
                  modifier = Modifier.fillMaxWidth().height(42.dp),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = customColors.textPrimary)
                ) {
                  Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentCyan)
                  Spacer(Modifier.size(8.dp))
                  Text("پاک کردن حافظه کش موقت برنامه", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                // C. Permanent reset to demo data
                Button(
                  onClick = { showResetConfirmDialog = true },
                  modifier = Modifier.fillMaxWidth().height(42.dp),
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = StatusDanger.copy(alpha = 0.85f))
                ) {
                  Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(Modifier.size(8.dp))
                  Text("پاک کردن دائم داده‌ها و بازنشانی داده‌های نمونه (دمو)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        // 6. Fixed Costs & Benchmarks Setting (مبالغ ثابت باربری، حاشیه سود ثابت و سربار)
        item {
          FixedCostBenchmarkCard(
            settings = factorySettings,
            onEditClick = { viewModel.openQuickAction(QuickActionType.SETTINGS_EDIT) }
          )
        }

        // 7. Standards List Header
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "استاندارد مدل‌ها و الگوهای مصرف",
              style = MaterialTheme.typography.titleSmall,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold
            )
            Button(
              onClick = { viewModel.openQuickAction(QuickActionType.SETTINGS_EDIT) },
              colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(Modifier.size(4.dp))
              Text("تنظیم کلی", style = MaterialTheme.typography.labelSmall)
            }
          }
        }

        items(standards) { standard ->
          ModelStandardCard(standard = standard)
        }
      }

      MoreSubSection.MASTER_DATA -> {
        item {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("تعریف اطلاعات پایه کارگاه", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              val tabs = listOf(
                "PRODUCTS" to "کالاها (${products.size})",
                "MATERIALS" to "مواد اولیه (${materials.size})",
                "BOM" to "فرمول ساخت BOM (${boms.size})",
                "CATEGORIES" to "دسته‌بندی‌ها (${categories.size})",
                "COLORS_SIZES" to "رنگ و سایز (${colors.size}/${sizes.size})"
              )
              items(tabs) { (key, title) ->
                FilterChip(
                  selected = masterDataTab == key,
                  onClick = { masterDataTab = key },
                  label = { Text(title, fontWeight = if (masterDataTab == key) FontWeight.Bold else FontWeight.Normal) }
                )
              }
            }
          }
        }

        when (masterDataTab) {
          "PRODUCTS" -> {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("لیست کالاهای تولیدی و بهای تمام‌شده پویا", style = MaterialTheme.typography.labelMedium, color = customColors.textMuted)
                Button(
                  onClick = { showAddProductDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(Modifier.size(4.dp))
                  Text("کالای جدید", style = MaterialTheme.typography.labelSmall)
                }
              }
            }

            items(products) { prod ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = customColors.card),
                border = BorderStroke(1.dp, customColors.border)
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                      Text(prod.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
                      Text("کد: ${prod.code} • دسته: ${prod.categoryName}", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                    }
                    if (!prod.isActive) {
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(StatusWarning.copy(alpha = 0.2f))
                          .padding(horizontal = 8.dp, vertical = 2.dp)
                      ) {
                        Text("بایگانی‌شده", color = StatusWarning, style = MaterialTheme.typography.labelSmall)
                      }
                    }
                  }

                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                      Text("بهای تمام‌شده پویا:", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                      Text("${CurrencyHelper.formatNumber(prod.currentCostPrice)} تومان", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AccentCyan)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                      Text("قیمت فروش پیشنهادی (${prod.targetProfitPercent.toInt()}% سود):", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                      Text("${CurrencyHelper.formatNumber(prod.effectiveSellingPrice)} تومان", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = StatusSuccess)
                    }
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      "اجرت دوخت: ${CurrencyHelper.formatNumber(prod.sewingWage)} | سربار: ${CurrencyHelper.formatNumber(prod.overheadCost)}",
                      style = MaterialTheme.typography.labelSmall,
                      color = customColors.textMuted
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      FilledTonalButton(
                        onClick = { viewModel.duplicateProduct(prod.id) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                          containerColor = AccentIndigo.copy(alpha = 0.15f),
                          contentColor = AccentIndigo
                        ),
                        shape = RoundedCornerShape(6.dp)
                      ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("تکثیر مدل", style = MaterialTheme.typography.labelSmall)
                      }

                      OutlinedButton(
                        onClick = { viewModel.submitSmartDeleteProduct(prod.id) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDanger),
                        shape = RoundedCornerShape(6.dp)
                      ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("حذف هوشمند", style = MaterialTheme.typography.labelSmall)
                      }
                    }
                  }
                }
              }
            }
          }

          "MATERIALS" -> {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Button(
                    onClick = { showAddMaterialDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text("ماده جدید", style = MaterialTheme.typography.labelSmall)
                  }
                  Button(
                    onClick = { viewModel.openQuickAction(QuickActionType.MARKET_PRICE_UPDATE) },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text("بروزرسانی نرخ بازار", style = MaterialTheme.typography.labelSmall)
                  }
                }
              }
            }

            items(materials) { mat ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = customColors.card),
                border = BorderStroke(1.dp, customColors.border)
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                      Text(mat.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
                      Text("کد: ${mat.code} • دسته: ${mat.category} • واحد: ${mat.unit}", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                    }
                    if (mat.isLowStock) {
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(StatusDanger.copy(alpha = 0.2f))
                          .padding(horizontal = 8.dp, vertical = 2.dp)
                      ) {
                        Text("کسری موجودی", color = StatusDanger, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                      }
                    }
                  }

                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                      Text("نرخ بازار فعلی:", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                      Text("${CurrencyHelper.formatNumber(mat.currentPrice)} تومان", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                      Text("موجودی در کارگاه:", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                      Text("${mat.stockQuantity} ${mat.unit} (حداقل: ${mat.minStockThreshold})", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (mat.isLowStock) StatusDanger else StatusSuccess)
                    }
                  }

                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(
                      onClick = { viewModel.submitSmartDeleteMaterial(mat.id) },
                      colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDanger),
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(14.dp))
                      Spacer(Modifier.size(4.dp))
                      Text("حذف هوشمند / بایگانی", style = MaterialTheme.typography.labelSmall)
                    }
                  }
                }
              }
            }
          }

          "BOM" -> {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("فرمول ساخت محصولات (مواد و متراژ استاندارد)", style = MaterialTheme.typography.labelMedium, color = customColors.textMuted)
                Button(
                  onClick = { showAddBomDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(Modifier.size(4.dp))
                  Text("افزودن ردیف فرمول", style = MaterialTheme.typography.labelSmall)
                }
              }
            }

            items(boms) { bom ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = customColors.card),
                border = BorderStroke(1.dp, customColors.border)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth().padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text("محصول: ${bom.productCode}", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                    Text("ماده: ${bom.materialName}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
                    Text("مصرف استاندارد: ${bom.standardQuantity} ${bom.unit} × ${CurrencyHelper.formatNumber(bom.unitRate)} = ${CurrencyHelper.formatNumber(bom.totalLineCost)} تومان", style = MaterialTheme.typography.labelSmall, color = AccentCyan)
                  }
                  IconButton(onClick = { viewModel.deleteBOM(bom.id, bom.productId) }) {
                    Icon(Icons.Default.DeleteForever, contentDescription = "حذف", tint = StatusDanger)
                  }
                }
              }
            }
          }

          "CATEGORIES" -> {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("دسته‌بندی‌های کالاها", style = MaterialTheme.typography.labelMedium, color = customColors.textMuted)
                Button(
                  onClick = { showAddCategoryDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("دسته جدید", style = MaterialTheme.typography.labelSmall)
                }
              }
            }

            items(categories) { cat ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = customColors.card),
                border = BorderStroke(1.dp, customColors.border)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth().padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(cat.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
                    Text("کد: ${cat.code}", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                  }
                  IconButton(onClick = { viewModel.deleteCategory(cat.id) }) {
                    Icon(Icons.Default.DeleteForever, contentDescription = "حذف", tint = StatusDanger)
                  }
                }
              }
            }
          }

          "COLORS_SIZES" -> {
            item {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("رنگ‌های تعریف شده", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                  Button(onClick = { showAddColorDialog = true }, shape = RoundedCornerShape(8.dp)) {
                    Text("رنگ جدید", style = MaterialTheme.typography.labelSmall)
                  }
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  items(colors) { c ->
                    Card(
                      shape = RoundedCornerShape(8.dp),
                      colors = CardDefaults.cardColors(containerColor = customColors.card),
                      border = BorderStroke(1.dp, customColors.border)
                    ) {
                      Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(c.name, style = MaterialTheme.typography.labelSmall)
                        Spacer(Modifier.size(6.dp))
                        IconButton(onClick = { viewModel.deleteColor(c.id) }, modifier = Modifier.size(20.dp)) {
                          Icon(Icons.Default.DeleteForever, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(14.dp))
                        }
                      }
                    }
                  }
                }

                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("سایزهای تعریف شده", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                  Button(onClick = { showAddSizeDialog = true }, shape = RoundedCornerShape(8.dp)) {
                    Text("سایز جدید", style = MaterialTheme.typography.labelSmall)
                  }
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  items(sizes) { s ->
                    Card(
                      shape = RoundedCornerShape(8.dp),
                      colors = CardDefaults.cardColors(containerColor = customColors.card),
                      border = BorderStroke(1.dp, customColors.border)
                    ) {
                      Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(s.name, style = MaterialTheme.typography.labelSmall)
                        Spacer(Modifier.size(6.dp))
                        IconButton(onClick = { viewModel.deleteSize(s.id) }, modifier = Modifier.size(20.dp)) {
                          Icon(Icons.Default.DeleteForever, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(14.dp))
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      MoreSubSection.PURCHASES -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("فاکتورهای خرید مواد اولیه", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = { viewModel.openQuickAction(QuickActionType.PURCHASE_IN) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("ثبت خرید", style = MaterialTheme.typography.labelSmall)
              }
              Button(
                onClick = { viewModel.openQuickAction(QuickActionType.SUPPLIER_PAYMENT) },
                colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("پرداخت به تأمین‌کننده", style = MaterialTheme.typography.labelSmall)
              }
            }
          }
        }

        items(purchaseOrders) { po ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = customColors.card),
            border = BorderStroke(1.dp, customColors.border)
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                  Text("شماره فاکتور: ${po.orderNumber}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
                  Text("تأمین‌کننده: ${po.supplierName} • تاریخ: ${po.orderDate}", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AccentBlue.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(po.status, color = AccentBlue, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
              }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                  Text("مبلغ کل فاکتور:", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                  Text("${CurrencyHelper.formatNumber(po.totalAmount)} تومان", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text("پرداخت شده / مانده بدهی:", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                  val remaining = po.totalAmount - po.paidAmount
                  Text(
                    "${CurrencyHelper.formatNumber(po.paidAmount)} / مانده: ${CurrencyHelper.formatNumber(remaining)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (remaining > 0) StatusDanger else StatusSuccess
                  )
                }
              }

              if (po.notes.isNotBlank()) {
                Text("یادداشت: ${po.notes}", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
              }
            }
          }
        }
      }

      MoreSubSection.LEDGER -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("دفتر کل انبار و رهگیری تراکنش‌ها", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
              Text("ردیابی لحظه‌ای ورود، خروج، مصرف تولید و انبارگردانی", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
            }
            Button(
              onClick = { viewModel.openQuickAction(QuickActionType.INVENTORY_AUDIT) },
              colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("انبارگردانی", style = MaterialTheme.typography.labelSmall)
            }
          }
        }

        items(inventoryLedger) { entry ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = customColors.card),
            border = BorderStroke(1.dp, customColors.border)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                  val badgeColor = when (entry.transactionType) {
                    "PURCHASE_INBOUND", "PRODUCTION_FINISH_INBOUND" -> StatusSuccess
                    "PRODUCTION_CONSUME", "SALE_SHIPMENT" -> StatusDanger
                    "RETURN" -> AccentBlue
                    "ADJUSTMENT" -> AccentIndigo
                    else -> customColors.textMuted
                  }
                  val typeTitle = when (entry.transactionType) {
                    "PURCHASE_INBOUND" -> "ورود خرید"
                    "PRODUCTION_FINISH_INBOUND" -> "تولید کالا"
                    "PRODUCTION_CONSUME" -> "مصرف تولید"
                    "SALE_SHIPMENT" -> "ارسال فروش"
                    "RETURN" -> "مرجوعی"
                    "ADJUSTMENT" -> "تعدیل انبار"
                    else -> entry.transactionType
                  }
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(badgeColor.copy(alpha = 0.15f))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(typeTitle, color = badgeColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                  }
                  Text(entry.itemName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = customColors.textPrimary)
                }
                Text("کد: ${entry.itemCode} • سند مرتبط: ${entry.relatedDocumentNumber.ifBlank { "-" }} • کاربر: ${entry.operator}", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                Text("تاریخ: ${entry.date}", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
              }

              Column(horizontalAlignment = Alignment.End) {
                val isPositive = entry.quantityChange >= 0
                Text(
                  "${if (isPositive) "+" else ""}${entry.quantityChange} ${entry.unit}",
                  style = MaterialTheme.typography.bodyLarge,
                  fontWeight = FontWeight.Bold,
                  color = if (isPositive) StatusSuccess else StatusDanger
                )
                Text("مانده: ${entry.balanceAfter} ${entry.unit}", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

/**
 * 1. Order Timeline Card with 3 Primary Statuses (سفارش داده شده، در حال دوخت، آماده ارسال)
 * and Status History Log (وضعیت قبلی، وضعیت جدید، تاریخ، ساعت)
 */
@Composable
fun OrderTimelineCard(
  order: SaleOrderEntity,
  history: List<OrderStatusHistoryEntity> = emptyList(),
  onStatusChange: (String) -> Unit = {},
  onEdit: () -> Unit
) {
  val customColors = LocalCustomColors.current
  // Requirement 6: 3 primary statuses
  val primaryStatuses = listOf(
    SaleOrderStatus.ORDER_PLACED,
    SaleOrderStatus.IN_SEWING,
    SaleOrderStatus.READY_FOR_SHIPPING
  )

  var showHistory by remember { mutableStateOf(false) }

  val currentIdx = when (order.deliveryStatus) {
    SaleOrderStatus.ORDER_PLACED, "ثبت شده", "سفارش ثبت شده" -> 0
    SaleOrderStatus.IN_SEWING, "در حال تولید", "در تولید", "در حال برش" -> 1
    SaleOrderStatus.READY_FOR_SHIPPING, "آماده تحویل", "ارسال شده", "تحویل شده" -> 2
    else -> 0
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, if (order.isDelayed) StatusDanger.copy(alpha = 0.4f) else customColors.border, RoundedCornerShape(14.dp))
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(order.orderNumber, style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text(order.customerName, style = MaterialTheme.typography.bodySmall, color = customColors.textSecondary)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          StatusChip(status = order.deliveryStatus)
          IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = customColors.textMuted, modifier = Modifier.size(16.dp))
          }
        }
      }

      // Requirement 6: 3 Canonical Timeline Steps Indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        primaryStatuses.forEachIndexed { idx, step ->
          val isDone = idx <= currentIdx
          val isCurrent = idx == currentIdx
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(if (isDone) (if (isCurrent) StatusSuccess else AccentBlue) else customColors.border),
              contentAlignment = Alignment.Center
            ) {
              if (isDone) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(Color.White))
              }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = step,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              color = if (isDone) customColors.textPrimary else customColors.textMuted,
              fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }

      // Quick Status Transition Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        primaryStatuses.forEach { targetStatus ->
          val isCurrent = order.deliveryStatus == targetStatus
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (isCurrent) AccentIndigo.copy(alpha = 0.2f) else customColors.secondaryBg)
              .border(1.dp, if (isCurrent) AccentIndigo else customColors.border, RoundedCornerShape(6.dp))
              .clickable { if (!isCurrent) onStatusChange(targetStatus) }
              .padding(vertical = 6.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = targetStatus,
              fontSize = 9.5.sp,
              color = if (isCurrent) AccentIndigo else customColors.textSecondary,
              fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }

      // Stock Check & Product Details
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(customColors.secondaryBg)
          .padding(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "تعداد: ${order.quantity} عدد • ${order.modelName}",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.textPrimary
          )
          if (order.isDelayed) {
            Text(
              text = "⚠ ۲ روز تأخیر",
              style = MaterialTheme.typography.labelSmall,
              color = StatusDanger,
              fontWeight = FontWeight.Bold
            )
          } else {
            Text(
              text = "✓ بررسی موجودی: تخصیص یافته",
              style = MaterialTheme.typography.labelSmall,
              color = StatusSuccess
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("مبلغ کل: ${CurrencyHelper.formatToman(order.netTotal)}", style = MaterialTheme.typography.bodySmall, color = customColors.textSecondary)
        Text("مانده: ${CurrencyHelper.formatToman(order.remainingDebt)}", style = MaterialTheme.typography.bodySmall, color = if (order.remainingDebt > 0) StatusWarning else StatusSuccess, fontWeight = FontWeight.Bold)
      }

      // Requirement 7: Status History Accordion (سابقه وضعیت)
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
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showHistory = !showHistory },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(Icons.Default.History, contentDescription = "سابقه وضعیت", tint = AccentBlue, modifier = Modifier.size(15.dp))
              Text(
                text = "سابقه تغییر وضعیت (${history.size} رکورد)",
                style = MaterialTheme.typography.labelSmall,
                color = AccentBlue,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = if (showHistory) "بستن ▲" else "مشاهده جزئیات ▼",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              color = customColors.textMuted
            )
          }

          if (showHistory) {
            if (history.isEmpty()) {
              Text(
                text = "هنوز سابقه تغییر وضعیتی برای این سفارش ثبت نشده است.",
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textMuted
              )
            } else {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                history.forEach { log ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(6.dp))
                      .background(customColors.card)
                      .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(log.oldStatus, fontSize = 10.sp, color = customColors.textMuted)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(10.dp))
                        Text(log.newStatus, fontSize = 10.sp, color = StatusSuccess, fontWeight = FontWeight.Bold)
                      }
                      if (log.note.isNotBlank()) {
                        Text(log.note, fontSize = 9.sp, color = customColors.textMuted)
                      }
                    }
                    Text(
                      text = "${log.date} ${log.time}",
                      fontSize = 9.sp,
                      color = customColors.textMuted
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * 2. Production Batch Card
 */
@Composable
fun ProductionBatchCard(prod: ProductionEntity) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text("کد مدل: ${prod.modelCode}", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text("${prod.modelName} • تاریخ: ${prod.date}", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        }
        StatusChip(status = prod.status)
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(customColors.secondaryBg)
          .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("تعداد کل", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${prod.quantity} عدد", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("طاقه مصرفی", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${prod.fabricRollsUsed} طاقه", style = MaterialTheme.typography.bodySmall, color = AccentCyan, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("متراژ مصرفی", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${prod.fabricMetersUsed.toInt()} متر", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("سود برآوردی", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(prod.totalProfit), style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * 3. Cutting Card
 */
@Composable
fun CuttingCard(cut: CuttingEntity) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, if (cut.isAbnormalConsumption) StatusWarning.copy(alpha = 0.4f) else customColors.border, RoundedCornerShape(14.dp))
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(cut.modelName, style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        StatusChip(status = cut.status)
      }

      // Progress Bar
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
          Text("پیشرفت برش: ${cut.progressPercent}٪", style = MaterialTheme.typography.labelSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text("نیاز: ${cut.targetQuantity} • برش: ${cut.cutQuantity} • کسری: ${cut.shortageQuantity}", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
        }
        LinearProgressIndicator(
          progress = { cut.progressPercent / 100f },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape),
          color = if (cut.progressPercent >= 100) StatusSuccess else StatusWarning,
          trackColor = customColors.border
        )
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(customColors.secondaryBg)
          .padding(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("مصرف استاندارد: ${cut.standardMetersPerItem} متر", style = MaterialTheme.typography.bodySmall, color = customColors.textSecondary)
          Text("مصرف واقعی: ${cut.actualMetersPerItem} متر", style = MaterialTheme.typography.bodySmall, color = if (cut.isAbnormalConsumption) StatusWarning else customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
      }

      if (cut.isAbnormalConsumption) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.Default.WarningAmber, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(16.dp))
          Text(
            text = "مصرف پارچه ${cut.fabricCode} ۱۸٪ بیشتر از استاندارد است",
            style = MaterialTheme.typography.labelSmall,
            color = StatusWarning,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}

/**
 * 4. Customer Profile Card with Edit Action
 */
@Composable
fun CustomerProfileCard(
  customer: CustomerEntity,
  onEdit: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .clickable(onClick = onEdit)
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(customer.name, style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          Text("${customer.company} • ${customer.phone}", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          StatusChip(status = customer.tier)
          IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = customColors.textMuted, modifier = Modifier.size(16.dp))
          }
        }
      }

      // Mini dashboard metrics
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(customColors.secondaryBg)
          .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("مجموع خرید", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(customer.totalPurchases), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("تعداد سفارش", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text("${customer.orderCount} فاکتور", style = MaterialTheme.typography.bodySmall, color = AccentBlue)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("بدهی جاری", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
          Text(CurrencyHelper.formatToman(customer.currentDebt), style = MaterialTheme.typography.bodySmall, color = if (customer.currentDebt > 0) StatusDanger else StatusSuccess, fontWeight = FontWeight.Bold)
        }
      }

      Text(
        text = "مدل‌های محبوب: ${customer.popularModels} • آخرین خرید: ${customer.lastOrderDate}",
        style = MaterialTheme.typography.labelSmall,
        color = customColors.textSecondary
      )
    }
  }
}

/**
 * 5. Supplier Card
 */
@Composable
fun SupplierCard(
  sup: SupplierEntity,
  onDelete: () -> Unit = {}
) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(sup.name, style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
          if (sup.storeName.isNotBlank()) {
            Text(sup.storeName, style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
          }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          StatusChip(status = sup.supplyType.ifEmpty { "پارچه و ملزومات" })
          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.DeleteForever, contentDescription = "حذف تأمین‌کننده", tint = StatusDanger, modifier = Modifier.size(18.dp))
          }
        }
      }

      if (sup.mobile.isNotBlank() || sup.phone.isNotBlank()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          if (sup.mobile.isNotBlank()) {
            Text("موبایل: ${sup.mobile}", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
          }
          if (sup.phone.isNotBlank()) {
            Text("تلفن: ${sup.phone}", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
          }
        }
      }

      if (sup.address.isNotBlank()) {
        Text("آدرس: ${sup.address}", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
      }

      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text("مجموع خرید تا کنون:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        Text(CurrencyHelper.formatToman(sup.totalPurchases), style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      }
      if (sup.priceHistoryNote.isNotBlank()) {
        Text("سابقه و روند قیمت: ${sup.priceHistoryNote}", style = MaterialTheme.typography.labelSmall, color = customColors.textSecondary)
      }
    }
  }
}

/**
 * 6. Model Standard Card
 */
@Composable
fun ModelStandardCard(standard: ModelStandardEntity) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
      .padding(16.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(standard.modelName, style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text("مصرف استاندارد پارچه:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        Text("${standard.standardFabricConsumptionMeters} متر", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
      }
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text("وزن استاندارد هر کار:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        Text("${standard.standardWeightGrams.toInt()} گرم", style = MaterialTheme.typography.bodySmall, color = customColors.textPrimary)
      }
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text("دستمزد پایه دوخت:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        Text(CurrencyHelper.formatToman(standard.sewingWage), style = MaterialTheme.typography.bodySmall, color = customColors.textSecondary)
      }
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text("قیمت فروش پیشنهادی:", style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        Text(CurrencyHelper.formatToman(standard.suggestedSalePrice), style = MaterialTheme.typography.bodySmall, color = StatusSuccess, fontWeight = FontWeight.Bold)
      }
    }
  }
}
