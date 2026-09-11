package com.example.ui.dialogs

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
