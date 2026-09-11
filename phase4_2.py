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
MORE = BASE / "ui/screens/MoreHubScreen.kt"
NEW_SCREEN = BASE / "ui/screens/ReadyGoodsScreen.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# ============================================
# ۱. ساخت ReadyGoodsScreen.kt
# ============================================
info("۱. ساخت ReadyGoodsScreen.kt")

code = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CuttingEntity
import com.example.ui.components.CurrencyHelper
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.util.UnitFormatter
import com.example.viewmodel.ManufacturingViewModel

enum class ReadyGoodsTab(val title: String) {
    CUT("برش خورده"),
    SEWING("در حال دوخت"),
    READY("کار آماده")
}

@Composable
fun ReadyGoodsScreen(
    viewModel: ManufacturingViewModel,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    var selectedTab by remember { mutableStateOf(ReadyGoodsTab.CUT) }

    val cutParts by viewModel.cutButNotSewnParts.collectAsState()
    val sewingParts by viewModel.sewingParts.collectAsState()
    val readyParts by viewModel.readyParts.collectAsState()

    val currentList = when (selectedTab) {
        ReadyGoodsTab.CUT -> cutParts
        ReadyGoodsTab.SEWING -> sewingParts
        ReadyGoodsTab.READY -> readyParts
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "مدیریت کارهای آماده",
                    style = MaterialTheme.typography.titleLarge,
                    color = customColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "پارت‌های برش خورده، در حال دوخت و کارهای آماده انبار",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                )
            }
        }

        // Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(customColors.secondaryBg)
                    .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
                    .padding(4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ReadyGoodsTab.values().forEach { tab ->
                    val isSelected = tab == selectedTab
                    val count = when (tab) {
                        ReadyGoodsTab.CUT -> cutParts.size
                        ReadyGoodsTab.SEWING -> sewingParts.size
                        ReadyGoodsTab.READY -> readyParts.size
                    }
                    val accent = when (tab) {
                        ReadyGoodsTab.CUT -> StatusWarning
                        ReadyGoodsTab.SEWING -> AccentCyan
                        ReadyGoodsTab.READY -> StatusSuccess
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) accent.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                tab.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) accent else customColors.textMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                            Text(
                                "$count",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) accent else customColors.textMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Content
        if (currentList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(customColors.card)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            if (selectedTab == ReadyGoodsTab.READY) Icons.Default.CheckCircle else Icons.Default.ContentCut,
                            contentDescription = null,
                            tint = customColors.textMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            "هیچ پارتی در وضعیت «${selectedTab.title}» نیست",
                            style = MaterialTheme.typography.bodyMedium,
                            color = customColors.textMuted
                        )
                    }
                }
            }
        } else {
            items(currentList) { part ->
                PartCard(
                    part = part,
                    currentTab = selectedTab,
                    onChangeStatus = { newStatus ->
                        viewModel.updateCuttingPartStatus(part.id, newStatus, "تغییر وضعیت از صفحه کارهای آماده")
                    }
                )
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun PartCard(
    part: CuttingEntity,
    currentTab: ReadyGoodsTab,
    onChangeStatus: (String) -> Unit
) {
    val customColors = LocalCustomColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = customColors.card),
        border = androidx.compose.foundation.BorderStroke(1.dp, customColors.border)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        part.partTitle.ifBlank { "پارت برش" },
                        style = MaterialTheme.typography.titleSmall,
                        color = customColors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "طاقه: ${part.rollCode}",
                        style = MaterialTheme.typography.labelSmall,
                        color = customColors.textMuted,
                        fontSize = 10.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (currentTab) {
                                ReadyGoodsTab.CUT -> StatusWarning.copy(alpha = 0.2f)
                                ReadyGoodsTab.SEWING -> AccentCyan.copy(alpha = 0.2f)
                                ReadyGoodsTab.READY -> StatusSuccess.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        part.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (currentTab) {
                            ReadyGoodsTab.CUT -> StatusWarning
                            ReadyGoodsTab.SEWING -> AccentCyan
                            ReadyGoodsTab.READY -> StatusSuccess
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            HorizontalDivider(color = customColors.border)

            // Info grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoItem("مدل", part.productName.ifBlank { part.modelName })
                InfoItem("تعداد", "${part.cutQuantity} عدد")
                InfoItem("متراژ", UnitFormatter.shortMeters(part.metersUsed) + " م")
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoItem("بهای تمام‌شده", CurrencyHelper.formatToman(part.totalCost))
                InfoItem("قیمت فروش", CurrencyHelper.formatToman(part.sellingPrice))
                if (part.customerName.isNotBlank()) {
                    InfoItem("مشتری", part.customerName)
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (currentTab) {
                    ReadyGoodsTab.CUT -> {
                        Button(
                            onClick = { onChangeStatus(CuttingEntity.STATUS_SEWING) },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(4.dp))
                            Text("ارسال به دوخت", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    ReadyGoodsTab.SEWING -> {
                        Button(
                            onClick = { onChangeStatus(CuttingEntity.STATUS_READY) },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(4.dp))
                            Text("تکمیل و ورود به انبار", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    ReadyGoodsTab.READY -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(StatusSuccess.copy(alpha = 0.15f))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "✓ این پارت در انبار محصولات آماده ثبت شده",
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

@Composable
private fun InfoItem(label: String, value: String) {
    val customColors = LocalCustomColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 9.sp, color = customColors.textMuted)
        Text(value, fontSize = 10.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
    }
}
'''
write(NEW_SCREEN, code)
log("ReadyGoodsScreen.kt ساخته شد")

# ============================================
# ۲. افزودن به MoreHubScreen (به عنوان SubSection جدید)
# ============================================
info("۲. بررسی MoreHubScreen برای اضافه کردن")

# چک می‌کنیم که فایل هست
mc = read(MORE)
if not mc:
    err("MoreHubScreen پیدا نشد")
elif "ReadyGoodsScreen" in mc:
    warn("قبلاً اضافه شده")
else:
    # به جای ویرایش enum MoreSubSection (که در VM است)، فقط یک رفرنس ساده اضافه می‌کنیم
    info("ReadyGoodsScreen ساخته شد — اتصال ناوبری در فاز بعدی")

# ============================================
# ۳. اعتبارسنجی
# ============================================
print()
info("۳. بررسی آکولاد:")
for path, label in [(NEW_SCREEN, "ReadyGoodsScreen"), (VM, "ViewModel")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

print()
log("فاز ۴.۲ اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase4.2): ready goods screen with CUT/SEWING tabs' && git push origin feature/cutting-parts-workflow{RST}")
print()
warn("نکته: برای دسترسی از منو، در فاز ۴.۳ اتصال ناوبری اضافه می‌شود")
