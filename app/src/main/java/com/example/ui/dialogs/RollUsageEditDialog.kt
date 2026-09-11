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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.model.RollUsageEntity
import com.example.ui.components.CurrencyHelper
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.util.UnitFormatter

@Composable
fun RollUsageEditDialog(
    usage: RollUsageEntity,
    metersPerKg: Double,
    onSave: (newMeters: Double, newKg: Double, newModel: String, newNote: String) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val customColors = LocalCustomColors.current
    var metersText by remember { mutableStateOf(UnitFormatter.shortMeters(usage.metersUsed)) }
    var modelName by remember { mutableStateOf(usage.modelName) }
    var note by remember { mutableStateOf(usage.note) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val metersValue = metersText.toDoubleOrNull() ?: 0.0
    val kgValue = if (metersPerKg > 0.0) metersValue / metersPerKg else 0.0

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("تایید حذف مصرف", color = StatusDanger, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("آیا از حذف این مصرف مطمئن هستی؟", color = customColors.textPrimary)
                    Text(
                        "${UnitFormatter.formatMeters(usage.metersUsed)} به طاقه ${usage.rollCode} برمی‌گردد",
                        color = StatusSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) { Text("بله، حذف کن") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("انصراف") }
            },
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
                Column {
                    Text("ویرایش مصرف طاقه", fontWeight = FontWeight.Bold)
                    Text(usage.rollCode, fontSize = 10.sp, color = customColors.textMuted)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = customColors.textMuted)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(customColors.secondaryBg)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("مصرف فعلی:", fontSize = 11.sp, color = customColors.textMuted)
                        Text(
                            UnitFormatter.formatDual(usage.weightKgUsed, usage.metersUsed),
                            fontSize = 12.sp, color = customColors.textPrimary, fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedTextField(
                    value = metersText,
                    onValueChange = { metersText = it },
                    label = { Text("متراژ مصرفی جدید (متر)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = customColors.textPrimary,
                        unfocusedTextColor = customColors.textPrimary,
                        focusedContainerColor = customColors.secondaryBg,
                        unfocusedContainerColor = customColors.secondaryBg,
                        focusedBorderColor = AccentIndigo,
                        unfocusedBorderColor = customColors.border
                    )
                )

                // تبدیل خودکار
                if (metersPerKg > 0.0 && metersValue > 0.0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentCyan.copy(alpha = 0.12f))
                            .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("معادل وزنی:", fontSize = 11.sp, color = customColors.textMuted)
                            Text(
                                UnitFormatter.formatKg(kgValue),
                                fontSize = 12.sp, color = AccentCyan, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = modelName,
                    onValueChange = { modelName = it },
                    label = { Text("نام مدل") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = customColors.textPrimary,
                        unfocusedTextColor = customColors.textPrimary,
                        focusedContainerColor = customColors.secondaryBg,
                        unfocusedContainerColor = customColors.secondaryBg,
                        focusedBorderColor = AccentIndigo,
                        unfocusedBorderColor = customColors.border
                    )
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("یادداشت") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = customColors.textPrimary,
                        unfocusedTextColor = customColors.textPrimary,
                        focusedContainerColor = customColors.secondaryBg,
                        unfocusedContainerColor = customColors.secondaryBg,
                        focusedBorderColor = AccentIndigo,
                        unfocusedBorderColor = customColors.border
                    )
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger.copy(alpha = 0.8f))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text("حذف", fontSize = 12.sp)
                }
                Button(
                    onClick = {
                        if (metersValue > 0.0) {
                            onSave(metersValue, kgValue, modelName, note)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
                ) { Text("ذخیره") }
            }
        },
        containerColor = customColors.cardElevated
    )
}
