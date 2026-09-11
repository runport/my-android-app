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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

/**
 * دیالوگ تایید چندمرحله‌ای حذف تمام داده‌ها (بند ۲۷)
 * مرحله ۱: هشدار اولیه
 * مرحله ۲: تایپ عبارت «حذف تمام داده‌ها»
 */
@Composable
fun DeleteAllDataConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val customColors = LocalCustomColors.current
    var step by remember { mutableStateOf(1) }
    var confirmText by remember { mutableStateOf("") }

    val expectedPhrase = "حذف تمام داده‌ها"
    val canDelete = confirmText.trim() == expectedPhrase

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = StatusDanger)
                Text(
                    if (step == 1) "هشدار جدی" else "تایید نهایی",
                    fontWeight = FontWeight.Bold,
                    color = StatusDanger
                )
            }
        },
        text = {
            when (step) {
                1 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(StatusDanger.copy(alpha = 0.1f))
                                .border(1.dp, StatusDanger.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    "⚠ این عملیات غیرقابل بازگشت است!",
                                    fontWeight = FontWeight.Bold,
                                    color = StatusDanger,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "تمام داده‌های زیر پاک خواهند شد:",
                                    color = customColors.textPrimary,
                                    fontSize = 12.sp
                                )
                                Text("• طاقه‌ها و پارچه‌ها", color = customColors.textSecondary, fontSize = 11.sp)
                                Text("• ملزومات و خرج‌کار", color = customColors.textSecondary, fontSize = 11.sp)
                                Text("• محصولات و موجودی", color = customColors.textSecondary, fontSize = 11.sp)
                                Text("• سفارشات و مشتریان", color = customColors.textSecondary, fontSize = 11.sp)
                                Text("• تأمین‌کنندگان و باربری‌ها", color = customColors.textSecondary, fontSize = 11.sp)
                                Text("• تمام سوابق مالی و تاریخی", color = customColors.textSecondary, fontSize = 11.sp)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(StatusWarning.copy(alpha = 0.12f))
                                .padding(10.dp)
                        ) {
                            Text(
                                "💡 توصیه می‌شود قبل از حذف، از داده‌ها پشتیبان بگیرید.",
                                color = StatusWarning,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                2 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "برای تایید نهایی، عبارت زیر را دقیقاً تایپ کنید:",
                            color = customColors.textPrimary,
                            fontSize = 12.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(customColors.secondaryBg)
                                .padding(10.dp)
                        ) {
                            Text(
                                "حذف تمام داده‌ها",
                                color = StatusDanger,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        OutlinedTextField(
                            value = confirmText,
                            onValueChange = { confirmText = it },
                            label = { Text("عبارت تایید") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = customColors.textPrimary,
                                unfocusedTextColor = customColors.textPrimary,
                                focusedContainerColor = customColors.secondaryBg,
                                unfocusedContainerColor = customColors.secondaryBg,
                                focusedBorderColor = StatusDanger,
                                unfocusedBorderColor = customColors.border
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            when (step) {
                1 -> {
                    Button(
                        onClick = { step = 2 },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusWarning)
                    ) {
                        Text("ادامه می‌دهم")
                    }
                }
                2 -> {
                    Button(
                        onClick = {
                            onConfirm()
                            onDismiss()
                        },
                        enabled = canDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canDelete) StatusDanger else customColors.textMuted
                        )
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("حذف همه داده‌ها")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف", color = customColors.textMuted)
            }
        },
        containerColor = customColors.cardElevated
    )
}

/**
 * دیالوگ بازیابی از فایل JSON
 */
@Composable
fun RestoreFromFileDialog(
    onConfirm: (jsonContent: String) -> Unit,
    onDismiss: () -> Unit
) {
    val customColors = LocalCustomColors.current
    var jsonText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("بازیابی از فایل پشتیبان", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StatusSuccess.copy(alpha = 0.1f))
                        .padding(10.dp)
                ) {
                    Text(
                        "متن فایل JSON پشتیبان را در کادر زیر جای‌گذاری کنید (Paste).",
                        color = StatusSuccess,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedTextField(
                    value = jsonText,
                    onValueChange = { jsonText = it },
                    label = { Text("محتوای JSON") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = customColors.textPrimary,
                        unfocusedTextColor = customColors.textPrimary,
                        focusedContainerColor = customColors.secondaryBg,
                        unfocusedContainerColor = customColors.secondaryBg,
                        focusedBorderColor = StatusSuccess,
                        unfocusedBorderColor = customColors.border
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(jsonText)
                    onDismiss()
                },
                enabled = jsonText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
            ) {
                Text("شروع بازیابی")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف", color = customColors.textMuted)
            }
        },
        containerColor = customColors.cardElevated
    )
}
