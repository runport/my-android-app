#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
patch_v4.py — ویرایش/حذف طاقه + انتخاب مشتری و محصول
"""
import sys
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
ok   = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

def replace_once(content, old, new, label):
    if new in content:
        warn(f"{label}: قبلاً اعمال شده")
        return content
    if old not in content:
        err(f"{label}: الگو پیدا نشد")
        return content
    ok(f"{label}")
    return content.replace(old, new, 1)

INV = Path("app/src/main/java/com/example/ui/screens/InventoryScreen.kt")
QA  = Path("app/src/main/java/com/example/ui/dialogs/QuickActionSheets.kt")

# =====================================================
# ۱. InventoryScreen.kt — ویرایش/حذف طاقه
# =====================================================
info("تغییر ۱: FabricRollInventoryCard — ویرایش/حذف طاقه")
c = read(INV)

c = replace_once(c,
"""fun FabricRollInventoryCard(
  roll: FabricRollEntity,
  onConsume: () -> Unit,
  onHistory: () -> Unit
) {""",
"""fun FabricRollInventoryCard(
  roll: FabricRollEntity,
  onConsume: () -> Unit,
  onHistory: () -> Unit,
  onEdit: () -> Unit = {}
) {""",
"signature FabricRollInventoryCard")

c = replace_once(c,
"""        IconButton(onClick = onHistory) {
          Icon(Icons.Default.History, contentDescription = "سوابق مصرف", tint = AccentCyan)
        }
      }""",
"""        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = AccentIndigo)
          }
          IconButton(onClick = onHistory) {
            Icon(Icons.Default.History, contentDescription = "سوابق مصرف", tint = AccentCyan)
          }
        }
      }""",
"icon buttons FabricRollInventoryCard")

c = replace_once(c,
"""          items(fabricRolls) { roll ->
            FabricRollInventoryCard(
              roll = roll,
              onConsume = {
                viewModel.openQuickAction(QuickActionType.ROLL_CONSUME)
              },
              onHistory = {
                viewModel.showRollHistory(roll)
              }
            )
          }""",
"""          items(fabricRolls) { roll ->
            FabricRollInventoryCard(
              roll = roll,
              onConsume = {
                viewModel.openQuickAction(QuickActionType.ROLL_CONSUME)
              },
              onHistory = {
                viewModel.showRollHistory(roll)
              },
              onEdit = {
                viewModel.startEditFabricRoll(roll)
              }
            )
          }""",
"call site FabricRollInventoryCard")

write(INV, c)

# =====================================================
# ۲. QuickActionSheets.kt — انتخاب محصول در مصرف طاقه
# =====================================================
info("تغییر ۲: انتخاب محصول در QuickRollConsumeForm")
c = read(QA)

c = replace_once(c,
"""  var selectedRollId by remember { mutableStateOf<Long?>(null) }
  val selectedRoll = remember(availableRolls, selectedRollId) {
    availableRolls.firstOrNull { it.id == selectedRollId } ?: availableRolls.firstOrNull()
  }

  var modelName by remember { mutableStateOf("هودی بیسیک زمستانه") }
  var modelCode by remember { mutableStateOf("HD-204") }""",
"""  var selectedRollId by remember { mutableStateOf<Long?>(null) }
  val selectedRoll = remember(availableRolls, selectedRollId) {
    availableRolls.firstOrNull { it.id == selectedRollId } ?: availableRolls.firstOrNull()
  }

  val products by viewModel.products.collectAsState()
  var showProductPicker by remember { mutableStateOf(false) }
  var modelName by remember { mutableStateOf("هودی بیسیک زمستانه") }
  var modelCode by remember { mutableStateOf("HD-204") }""",
"state QuickRollConsumeForm")

c = replace_once(c,
"""    // Model Inputs
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.4f)) {
        ExecutiveTextField(label = "نام مدل محصول مورد نظر", value = modelName, onValueChange = { modelName = it })
      }
      Box(modifier = Modifier.weight(0.8f)) {
        ExecutiveTextField(label = "کد مدل", value = modelCode, onValueChange = { modelCode = it })
      }
    }""",
"""    // Product Picker
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
    }""",
"product picker QuickRollConsumeForm")

write(QA, c)

# =====================================================
# ۳. QuickActionSheets.kt — انتخاب مشتری در QuickSaleForm
# =====================================================
info("تغییر ۳: انتخاب مشتری و محصول در QuickSaleForm")
c = read(QA)

c = replace_once(c,
"""fun QuickSaleForm(
  isPreOrder: Boolean,
  onSubmit: (String, String, String, String, Int, Long, Long, Long, Long) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current

  var customerName by remember { mutableStateOf("بوتیک آریا (احمدی)") }
  var customerPhone by remember { mutableStateOf("09121234567") }""",
"""fun QuickSaleForm(
  viewModel: ManufacturingViewModel,
  isPreOrder: Boolean,
  onSubmit: (String, String, String, String, Int, Long, Long, Long, Long) -> Unit,
  onBack: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val customers by viewModel.customers.collectAsState()
  val products by viewModel.products.collectAsState()
  var showCustomerPicker by remember { mutableStateOf(false) }
  var showProductPicker by remember { mutableStateOf(false) }

  var customerName by remember { mutableStateOf("بوتیک آریا (احمدی)") }
  var customerPhone by remember { mutableStateOf("09121234567") }""",
"signature QuickSaleForm")

c = replace_once(c,
"""    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.2f)) {
        ExecutiveTextField(label = "نام خریدار / فروشگاه", value = customerName, onValueChange = { customerName = it })
      }
      Box(modifier = Modifier.weight(0.8f)) {
        ExecutiveTextField(label = "تلفن", value = customerPhone, keyboardType = KeyboardType.Phone, onValueChange = { customerPhone = it })
      }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Box(modifier = Modifier.weight(1.4f)) {
        ExecutiveTextField(label = "مدل محصول", value = modelName, onValueChange = { modelName = it })
      }
      Box(modifier = Modifier.weight(0.6f)) {
        ExecutiveTextField(label = "کد", value = modelCode, onValueChange = { modelCode = it })
      }
    }""",
"""    // Customer Picker
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
    }""",
"customer + product picker QuickSaleForm")

# call site QuickSaleForm
c = replace_once(c,
"""        QuickActionType.SALE -> {
          QuickSaleForm(
            isPreOrder = false,
            onSubmit = { customer, phone, modelCode, modelName, qty, price, discount, paid, cost ->
              viewModel.submitSale(customer, phone, modelCode, modelName, qty, price, discount, paid, cost)
            },
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }""",
"""        QuickActionType.SALE -> {
          QuickSaleForm(
            viewModel = viewModel,
            isPreOrder = false,
            onSubmit = { customer, phone, modelCode, modelName, qty, price, discount, paid, cost ->
              viewModel.submitSale(customer, phone, modelCode, modelName, qty, price, discount, paid, cost)
            },
            onBack = { viewModel.openQuickAction(QuickActionType.WAREHOUSE_HUB) }
          )
        }""",
"call site QuickSaleForm")

write(QA, c)

# =====================================================
# ۴. بررسی آکولاد
# =====================================================
print()
info("بررسی نهایی:")
for path, label in [(INV, "InventoryScreen.kt"), (QA, "QuickActionSheets.kt")]:
    content = read(path)
    ob, cb = content.count("{"), content.count("}")
    if ob == cb:
        ok(f"{label}: {ob} آکولاد متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن")

print()
ok("تمام. حالا:")
print(f"  {B}git add . && git commit -m 'feat: roll edit/delete + customer/product pickers' && git push{RST}")
