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
INV = BASE / "ui/screens/InventoryScreen.kt"
NEW_DLG = BASE / "ui/dialogs/WaybillDetailsDialog.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# ============================================
# ۱. ساخت WaybillDetailsDialog.kt
# ============================================
info("۱. ساخت WaybillDetailsDialog.kt")

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WaybillItemEntity
import com.example.data.repository.ManufacturingRepository
import com.example.ui.components.CurrencyHelper
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

/**
 * دیالوگ نمایش سهم کرایه یک طاقه از بارنامه‌های ثبت‌شده
 */
@Composable
fun WaybillDetailsDialog(
    repository: ManufacturingRepository,
    rollId: Long,
    rollCode: String,
    allocatedShipping: Long,
    onDismiss: () -> Unit
) {
    val customColors = LocalCustomColors.current
    var items by remember { mutableStateOf<List<WaybillItemEntity>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(rollId) {
        try {
            // جستجو در تمام بارنامه‌ها برای این طاقه
            val all = repository.getAllWaybillItemsForItem("FABRIC_ROLL", rollId)
            items = all
        } catch (_: Exception) {
            items = emptyList()
        }
        loading = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AccentCyan)
                    Column {
                        Text("سهم کرایه طاقه", fontWeight = FontWeight.Bold)
                        Text(rollCode, fontSize = 10.sp, color = customColors.textMuted)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = customColors.textMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 450.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // کارت جمع کل
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(StatusSuccess.copy(alpha = 0.12f))
                        .border(1.dp, StatusSuccess.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "مجموع کرایه تخصیص‌یافته به این طاقه:",
                            fontSize = 11.sp,
                            color = customColors.textMuted
                        )
                        Text(
                            CurrencyHelper.formatToman(allocatedShipping),
                            fontSize = 18.sp,
                            color = StatusSuccess,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (loading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("در حال بارگذاری...", color = customColors.textMuted, fontSize = 12.sp)
                    }
                } else if (items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(StatusWarning.copy(alpha = 0.1f))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "این طاقه هنوز از هیچ بارنامه‌ای کرایه دریافت نکرده",
                                color = StatusWarning,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "برای تخصیص، از بخش «ثبت بارنامه چندقلمی» استفاده کنید",
                                color = customColors.textMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                } else {
                    Text(
                        "لیست بارنامه‌ها (${items.size} ردیف):",
                        fontSize = 12.sp,
                        color = customColors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(items) { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(customColors.card)
                                    .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "بارنامه #${item.waybillId}",
                                            fontSize = 11.sp,
                                            color = AccentIndigo,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            CurrencyHelper.formatToman(item.shippingAllocation),
                                            fontSize = 12.sp,
                                            color = StatusSuccess,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        "مقدار: ${item.quantity} ${item.unit}",
                                        fontSize = 10.sp,
                                        color = customColors.textMuted
                                    )
                                    if (item.supplierName.isNotBlank()) {
                                        Text(
                                            "تأمین‌کننده: ${item.supplierName}",
                                            fontSize = 10.sp,
                                            color = customColors.textSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
            ) { Text("بستن") }
        },
        containerColor = customColors.cardElevated
    )
}
'''
write(NEW_DLG, code)
log("WaybillDetailsDialog.kt ساخته شد")

# ============================================
# ۲. افزودن متد به Repository
# ============================================
info("۲. افزودن متد getAllWaybillItemsForItem به Repository")

REPO = BASE / "data/repository/ManufacturingRepository.kt"
c = read(REPO)
if "getAllWaybillItemsForItem" in c:
    warn("متد از قبل هست")
else:
    idx = c.rstrip().rfind("}")
    block = '''
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
'''
    c = c[:idx] + block + c[idx:]
    write(REPO, c)
    log("متد Repository اضافه شد")

# ============================================
# ۳. افزودن کوئری به WaybillItemDao
# ============================================
info("۳. افزودن کوئری به WaybillItemDao")

DAO_FILE = BASE / "data/dao/AppDao.kt"
c = read(DAO_FILE)
if "getItemsForItem" in c:
    warn("کوئری از قبل هست")
else:
    # پیدا کردن interface WaybillItemDao
    m = re.search(r'interface\s+WaybillItemDao\s*\{', c)
    if m:
        open_idx = m.end() - 1
        depth = 0
        i = open_idx
        while i < len(c):
            if c[i] == '{': depth += 1
            elif c[i] == '}':
                depth -= 1
                if depth == 0: break
            i += 1
        close_idx = i
        block = '''
  @Query("SELECT * FROM waybill_items WHERE itemType = :itemType AND itemId = :itemId")
  suspend fun getItemsForItem(itemType: String, itemId: Long): List<WaybillItemEntity>
'''
        c = c[:close_idx] + block + c[close_idx:]
        write(DAO_FILE, c)
        log("کوئری اضافه شد")
    else:
        err("interface WaybillItemDao پیدا نشد")

# ============================================
# ۴. افزودن دکمه به کارت طاقه
# ============================================
info("۴. افزودن دکمه «جزئیات کرایه» به کارت طاقه")

c = read(INV)
if "onViewWaybill" in c:
    warn("قبلاً اعمال شده")
else:
    # ۴.۱ - تغییر signature
    c = c.replace(
        """fun FabricRollInventoryCard(
  roll: FabricRollEntity,
  onConsume: () -> Unit,
  onHistory: () -> Unit,
  onEdit: () -> Unit = {},
  onUpdatePrice: () -> Unit = {}
) {""",
        """fun FabricRollInventoryCard(
  roll: FabricRollEntity,
  onConsume: () -> Unit,
  onHistory: () -> Unit,
  onEdit: () -> Unit = {},
  onUpdatePrice: () -> Unit = {},
  onViewWaybill: () -> Unit = {}
) {""",
        1
    )
    log("signature به‌روز شد")

    # ۴.۲ - افزودن دکمه در ردیف دکمه‌ها
    anchor = """          OutlinedButton(
            onClick = onHistory,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.size(4.dp))
            Text("سوابق برش", style = MaterialTheme.typography.labelSmall)
          }"""
    if anchor in c:
        replacement = """          OutlinedButton(
            onClick = onHistory,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.size(4.dp))
            Text("سوابق برش", style = MaterialTheme.typography.labelSmall)
          }

          OutlinedButton(
            onClick = onViewWaybill,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.size(4.dp))
            Text("سهم کرایه", style = MaterialTheme.typography.labelSmall)
          }"""
        c = c.replace(anchor, replacement, 1)
        log("دکمه «سهم کرایه» اضافه شد")
    else:
        err("anchor OutlinedButton سوابق برش پیدا نشد")

    # ۴.۳ - تغییر call site
    old_call = """              onUpdatePrice = {
                viewModel.openPriceUpdateDialog(
                  com.example.viewmodel.PriceUpdateTarget(
                    type = com.example.viewmodel.PriceUpdateType.FABRIC_ROLL,
                    id = roll.id,
                    title = "طاقه ${roll.rollCode} - ${roll.fabricType}",
                    currentPricePerMeter = if (roll.currentPricePerMeter > 0L) roll.currentPricePerMeter else roll.buyPricePerMeter,
                    currentPricePerKg = if (roll.currentPricePerKg > 0L) roll.currentPricePerKg else roll.buyPricePerKg,
                    metersPerKg = roll.metersPerKg,
                    unit = "متر"
                  )
                )
              }
            )
          }"""
    new_call = """              onUpdatePrice = {
                viewModel.openPriceUpdateDialog(
                  com.example.viewmodel.PriceUpdateTarget(
                    type = com.example.viewmodel.PriceUpdateType.FABRIC_ROLL,
                    id = roll.id,
                    title = "طاقه ${roll.rollCode} - ${roll.fabricType}",
                    currentPricePerMeter = if (roll.currentPricePerMeter > 0L) roll.currentPricePerMeter else roll.buyPricePerMeter,
                    currentPricePerKg = if (roll.currentPricePerKg > 0L) roll.currentPricePerKg else roll.buyPricePerKg,
                    metersPerKg = roll.metersPerKg,
                    unit = "متر"
                  )
                )
              },
              onViewWaybill = {
                viewModel.openWaybillDetails(roll)
              }
            )
          }"""
    if old_call in c:
        c = c.replace(old_call, new_call, 1)
        log("call site به‌روز شد")
    else:
        err("call site onUpdatePrice پیدا نشد")

    write(INV, c)

# ============================================
# ۵. افزودن state به ViewModel
# ============================================
info("۵. افزودن state و متد به ViewModel")

VM = BASE / "viewmodel/ManufacturingViewModel.kt"
c = read(VM)
if "openWaybillDetails" in c:
    warn("متد از قبل هست")
else:
    idx = c.rstrip().rfind("}")
    block = '''
  // ==========================================
  // WAYBILL DETAILS
  // ==========================================

  private val _waybillDetailsTarget = MutableStateFlow<com.example.data.model.FabricRollEntity?>(null)
  val waybillDetailsTarget: StateFlow<com.example.data.model.FabricRollEntity?> = _waybillDetailsTarget.asStateFlow()

  fun openWaybillDetails(roll: com.example.data.model.FabricRollEntity) {
    _waybillDetailsTarget.value = roll
  }

  fun closeWaybillDetails() {
    _waybillDetailsTarget.value = null
  }

  fun getRepository(): com.example.data.repository.ManufacturingRepository = repository
'''
    c = c[:idx] + block + c[idx:]
    write(VM, c)
    log("state و متد اضافه شدند")

# ============================================
# ۶. نمایش دیالوگ در InventoryScreen
# ============================================
info("۶. نمایش دیالوگ در InventoryScreen")

c = read(INV)

if "WaybillDetailsDialog(" in c:
    warn("نمایش دیالوگ از قبل هست")
else:
    # در انتهای InventoryScreen قبل از آخرین }
    # باید موقعیت مناسب پیدا کنیم - جایی که LazyColumn بسته می‌شود و بعد dialog ها

    # پیدا کردن آخرین } تابع InventoryScreen
    # دنبال marker: بعد از LazyColumn آخر
    marker_pattern = re.compile(
        r'(\n\s*item\s*\{\s*\n\s*Spacer\(modifier\s*=\s*Modifier\.height\(80\.dp\)\)\s*\n\s*\}\s*\n\s*\}\s*\n\})',
        re.MULTILINE
    )
    m = marker_pattern.search(c)
    if m:
        insert_pos = m.end() - 1  # قبل از } پایانی تابع
        dialog_code = '''
  // دیالوگ جزئیات سهم کرایه طاقه
  val waybillTarget by viewModel.waybillDetailsTarget.collectAsState()
  waybillTarget?.let { roll ->
    WaybillDetailsDialog(
      repository = viewModel.getRepository(),
      rollId = roll.id,
      rollCode = roll.rollCode,
      allocatedShipping = roll.allocatedShippingCost,
      onDismiss = { viewModel.closeWaybillDetails() }
    )
  }
'''
        c = c[:insert_pos] + dialog_code + c[insert_pos:]
        log("نمایش دیالوگ اضافه شد")
    else:
        warn("الگوی انتهای LazyColumn پیدا نشد — دیالوگ باید دستی اضافه شود")

    write(INV, c)

# ============================================
# ۷. اعتبارسنجی
# ============================================
print()
info("۷. بررسی آکولاد:")
for path, label in [(INV, "InventoryScreen"), (VM, "ViewModel"),
                    (NEW_DLG, "WaybillDetailsDialog"), (DAO_FILE, "AppDao")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

print()
log("زیرفاز 2.3 اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase2.3): waybill details on fabric roll card' && git push origin feature/cutting-parts-workflow{RST}")
