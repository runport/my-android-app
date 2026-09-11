#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
log  = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")

BASE = Path("app/src/main/java/com/example")
VM = BASE / "viewmodel/ManufacturingViewModel.kt"
QA = BASE / "ui/dialogs/QuickActionSheets.kt"
NEW_FILE = BASE / "ui/dialogs/ManagementDialogs.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# ============================================
# ۱. ساخت ManagementDialogs.kt
# ============================================
info("۱. ساخت ManagementDialogs.kt")

code = '''package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.FabricCategoryEntity
import com.example.data.model.ShippingCompanyEntity
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.viewmodel.ManufacturingViewModel

// ===========================================
// مدیریت شرکت‌های باربری (CRUD کامل)
// ===========================================
@Composable
fun ShippingCompanyManagerDialog(
    viewModel: ManufacturingViewModel,
    onDismiss: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val companies by viewModel.shippingCompanies.collectAsState()

    var editingCompany by remember { mutableStateOf<ShippingCompanyEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<ShippingCompanyEntity?>(null) }

    if (showEditor) {
        var name by remember(editingCompany?.id) { mutableStateOf(editingCompany?.name ?: "") }
        var phone by remember(editingCompany?.id) { mutableStateOf(editingCompany?.phone ?: "") }
        var address by remember(editingCompany?.id) { mutableStateOf(editingCompany?.address ?: "") }
        var notes by remember(editingCompany?.id) { mutableStateOf(editingCompany?.notes ?: "") }

        AlertDialog(
            onDismissRequest = { showEditor = false; editingCompany = null },
            title = { Text(if (editingCompany == null) "افزودن شرکت باربری" else "ویرایش شرکت باربری", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ManagerTextField(label = "نام شرکت", value = name, onValueChange = { name = it })
                    ManagerTextField(label = "تلفن", value = phone, onValueChange = { phone = it }, keyboardType = KeyboardType.Phone)
                    ManagerTextField(label = "آدرس", value = address, onValueChange = { address = it })
                    ManagerTextField(label = "یادداشت", value = notes, onValueChange = { notes = it })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val existing = editingCompany
                            if (existing == null) {
                                viewModel.submitShippingCompany(name, phone, address, notes)
                            } else {
                                viewModel.updateShippingCompany(
                                    existing.copy(name = name, phone = phone, address = address, notes = notes)
                                )
                            }
                            showEditor = false
                            editingCompany = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
                ) { Text("ذخیره") }
            },
            dismissButton = {
                TextButton(onClick = { showEditor = false; editingCompany = null }) { Text("انصراف") }
            },
            containerColor = customColors.cardElevated
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("حذف باربری", color = StatusDanger, fontWeight = FontWeight.Bold) },
            text = { Text("آیا از حذف «${target.name}» مطمئنی؟", color = customColors.textPrimary) },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteShippingCompany(target.id); deleteTarget = null },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) { Text("حذف") }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("انصراف") } },
            containerColor = customColors.cardElevated
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AccentCyan)
                    Text("مدیریت باربری‌ها", fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
            }
        },
        text = {
            Column(modifier = Modifier.heightIn(max = 420.dp)) {
                Button(
                    onClick = { editingCompany = null; showEditor = true },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text("افزودن باربری جدید")
                }
                Spacer(Modifier.height(10.dp))
                if (companies.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("هیچ باربری ثبت نشده", color = customColors.textMuted, fontSize = 12.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(companies) { company ->
                            Box(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(customColors.card)
                                    .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            company.name,
                                            color = customColors.textPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        if (company.phone.isNotBlank()) {
                                            Text("تلفن: ${company.phone}", color = customColors.textMuted, fontSize = 10.sp)
                                        }
                                    }
                                    Row {
                                        IconButton(
                                            onClick = { editingCompany = company; showEditor = true },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                                        }
                                        IconButton(
                                            onClick = { deleteTarget = company },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("بستن") } },
        containerColor = customColors.cardElevated
    )
}

// ===========================================
// مدیریت دسته‌بندی‌ها (پارچه/محصول/ملزومات)
// ===========================================
@Composable
fun CategoryManagerDialog(
    viewModel: ManufacturingViewModel,
    onDismiss: () -> Unit
) {
    val customColors = LocalCustomColors.current
    val fabricCats by viewModel.fabricCategories.collectAsState()
    val productCats by viewModel.categories.collectAsState()
    val materials by viewModel.materials.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val materialCategories = remember(materials) {
        materials.map { it.category }.filter { it.isNotBlank() }.distinct()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("مدیریت دسته‌بندی‌ها", fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
            }
        },
        text = {
            Column {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("پارچه", fontSize = 12.sp) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("محصول", fontSize = 12.sp) })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("ملزومات", fontSize = 12.sp) })
                }
                Spacer(Modifier.height(10.dp))
                when (selectedTab) {
                    0 -> FabricCategoryList(viewModel, fabricCats)
                    1 -> ProductCategoryList(viewModel, productCats)
                    else -> MaterialCategoryList(materialCategories)
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("بستن") } },
        containerColor = customColors.cardElevated
    )
}

@Composable
private fun FabricCategoryList(
    viewModel: ManufacturingViewModel,
    categories: List<FabricCategoryEntity>
) {
    val customColors = LocalCustomColors.current
    var showEditor by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<FabricCategoryEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<FabricCategoryEntity?>(null) }

    if (showEditor) {
        var name by remember(editing?.id) { mutableStateOf(editing?.name ?: "") }
        var code by remember(editing?.id) { mutableStateOf(editing?.code ?: "") }
        var desc by remember(editing?.id) { mutableStateOf(editing?.description ?: "") }

        AlertDialog(
            onDismissRequest = { showEditor = false; editing = null },
            title = { Text(if (editing == null) "افزودن دسته پارچه" else "ویرایش دسته", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ManagerTextField(label = "نام دسته", value = name, onValueChange = { name = it })
                    ManagerTextField(label = "کد (اختیاری)", value = code, onValueChange = { code = it })
                    ManagerTextField(label = "توضیحات", value = desc, onValueChange = { desc = it })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val existing = editing
                            if (existing == null) {
                                viewModel.submitFabricCategory(name, code, desc)
                            } else {
                                viewModel.updateFabricCategory(
                                    existing.copy(name = name, code = code, description = desc)
                                )
                            }
                            showEditor = false
                            editing = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
                ) { Text("ذخیره") }
            },
            dismissButton = { TextButton(onClick = { showEditor = false; editing = null }) { Text("انصراف") } },
            containerColor = customColors.cardElevated
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("حذف دسته", color = StatusDanger, fontWeight = FontWeight.Bold) },
            text = { Text("حذف «${target.name}»؟", color = customColors.textPrimary) },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteFabricCategory(target.id); deleteTarget = null },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) { Text("حذف") }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("انصراف") } },
            containerColor = customColors.cardElevated
        )
    }

    Column(modifier = Modifier.heightIn(max = 350.dp)) {
        Button(
            onClick = { editing = null; showEditor = true },
            modifier = Modifier.fillMaxWidth().height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.size(4.dp))
            Text("افزودن دسته پارچه", fontSize = 12.sp)
        }
        Spacer(Modifier.height(8.dp))
        if (categories.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                Text("دسته‌ای ثبت نشده", color = customColors.textMuted, fontSize = 12.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(categories) { cat ->
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(customColors.card)
                            .border(1.dp, customColors.border, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cat.name, color = customColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                if (cat.description.isNotBlank()) {
                                    Text(cat.description, color = customColors.textMuted, fontSize = 10.sp)
                                }
                            }
                            Row {
                                IconButton(onClick = { editing = cat; showEditor = true }, modifier = Modifier.size(26.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(14.dp))
                                }
                                IconButton(onClick = { deleteTarget = cat }, modifier = Modifier.size(26.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCategoryList(
    viewModel: ManufacturingViewModel,
    categories: List<CategoryEntity>
) {
    val customColors = LocalCustomColors.current
    var showEditor by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<CategoryEntity?>(null) }

    if (showEditor) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showEditor = false },
            title = { Text("افزودن دسته محصول", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ManagerTextField(label = "نام", value = name, onValueChange = { name = it })
                    ManagerTextField(label = "کد (اختیاری)", value = code, onValueChange = { code = it })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val catCode = code.ifBlank { "CAT-${System.currentTimeMillis() % 1000}" }
                            viewModel.saveCategory(catCode, name)
                            showEditor = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
                ) { Text("ذخیره") }
            },
            dismissButton = { TextButton(onClick = { showEditor = false }) { Text("انصراف") } },
            containerColor = customColors.cardElevated
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("حذف دسته", color = StatusDanger, fontWeight = FontWeight.Bold) },
            text = { Text("حذف «${target.name}»؟", color = customColors.textPrimary) },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteCategory(target.id); deleteTarget = null },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) { Text("حذف") }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("انصراف") } },
            containerColor = customColors.cardElevated
        )
    }

    Column(modifier = Modifier.heightIn(max = 350.dp)) {
        Button(
            onClick = { showEditor = true },
            modifier = Modifier.fillMaxWidth().height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.size(4.dp))
            Text("افزودن دسته محصول", fontSize = 12.sp)
        }
        Spacer(Modifier.height(8.dp))
        if (categories.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                Text("دسته‌ای ثبت نشده", color = customColors.textMuted, fontSize = 12.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(categories) { cat ->
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(customColors.card)
                            .border(1.dp, customColors.border, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(cat.name, color = customColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = { deleteTarget = cat }, modifier = Modifier.size(26.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MaterialCategoryList(categories: List<String>) {
    val customColors = LocalCustomColors.current
    Column(modifier = Modifier.heightIn(max = 350.dp)) {
        if (categories.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                Text(
                    "دسته ملزومات از فیلد Category هر ماده گرفته می‌شود",
                    color = customColors.textMuted, fontSize = 11.sp
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(categories) { cat ->
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(customColors.card)
                            .border(1.dp, customColors.border, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(cat, color = customColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ManagerTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val customColors = LocalCustomColors.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = customColors.textPrimary,
            unfocusedTextColor = customColors.textPrimary,
            focusedContainerColor = customColors.secondaryBg,
            unfocusedContainerColor = customColors.secondaryBg,
            focusedBorderColor = AccentIndigo,
            unfocusedBorderColor = customColors.border,
            focusedLabelColor = AccentIndigo,
            unfocusedLabelColor = customColors.textMuted
        )
    )
}
'''
write(NEW_FILE, code)
log("ManagementDialogs.kt ساخته شد")

# ============================================
# ۲. افزودن QuickActionType جدید
# ============================================
info("۲. افزودن QuickActionType جدید")

c = read(VM)
if "SHIPPING_COMPANY_MANAGER" in c:
    warn("SHIPPING_COMPANY_MANAGER از قبل هست")
else:
    if "SHIPPING_MULTI," in c:
        c = c.replace(
            "  SHIPPING_MULTI,",
            "  SHIPPING_MULTI,\n  SHIPPING_COMPANY_MANAGER,\n  CATEGORY_MANAGER,",
            1
        )
        write(VM, c)
        log("SHIPPING_COMPANY_MANAGER و CATEGORY_MANAGER اضافه شدند")
    else:
        err("SHIPPING_MULTI در enum پیدا نشد")

# ============================================
# ۳. افزودن branches به Modal
# ============================================
info("۳. افزودن branches به QuickActionsModalBottomSheet")

c = read(QA)
if "ShippingCompanyManagerDialog(" in c:
    warn("branches از قبل هستند")
else:
    anchor = """        QuickActionType.SHIPPING_MULTI -> {"""
    if anchor in c:
        branches = """        QuickActionType.SHIPPING_COMPANY_MANAGER -> {
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
        QuickActionType.SHIPPING_MULTI -> {"""
        c = c.replace(anchor, branches, 1)
        write(QA, c)
        log("branches اضافه شدند")
    else:
        err("anchor SHIPPING_MULTI پیدا نشد")

# ============================================
# ۴. افزودن دو دکمه به QuickWarehouseHub
# ============================================
info("۴. افزودن دکمه‌ها به QuickWarehouseHub")

c = read(QA)
if "SHIPPING_COMPANY_MANAGER)" in c and "action_company_manager" in c:
    warn("دکمه‌ها از قبل هستند")
else:
    # پیدا کردن انتهای QuickWarehouseHub (قبل از بستن تابع)
    hub_start = c.find("fun QuickWarehouseHub(")
    if hub_start < 0:
        err("QuickWarehouseHub پیدا نشد")
    else:
        # پیدا کردن آخرین Tile در Hub
        marker = """    QuickActionTile(
      title = "ثبت بچ تولید و کارگاه","""
        if marker in c:
            new_tiles = """    Spacer(modifier = Modifier.height(6.dp))

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
      title = "ثبت بچ تولید و کارگاه","""
            c = c.replace(marker, new_tiles, 1)
            write(QA, c)
            log("دو دکمه اضافه شدند")
        else:
            err("marker آخرین Tile پیدا نشد")

# ============================================
# ۵. اعتبارسنجی
# ============================================
print()
info("۵. بررسی آکولاد:")
for path, label in [(VM, "ViewModel"), (QA, "QuickActionSheets"), (NEW_FILE, "ManagementDialogs")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

print()
log("زیرفاز 2.2 اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase2.2): CRUD for shipping companies and categories' && git push origin feature/cutting-parts-workflow{RST}")
