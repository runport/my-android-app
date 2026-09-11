package com.example.ui.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DashboardChartType
import com.example.data.model.DashboardLayoutArrangement
import com.example.data.model.SectionSettingsTarget
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.PersianFont
import com.example.ui.theme.StatusSuccess
import com.example.viewmodel.ManufacturingViewModel

@Composable
fun SectionSettingsDialog(
  target: SectionSettingsTarget,
  viewModel: ManufacturingViewModel,
  onDismiss: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val factorySettings by viewModel.factorySettings.collectAsState()
  val isDarkTheme by viewModel.isDarkTheme.collectAsState()
  val selectedFont by viewModel.selectedFont.collectAsState()
  val context = LocalContext.current

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .fillMaxHeight(0.85f)
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, customColors.border, RoundedCornerShape(24.dp)),
      color = customColors.card
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // 1. Header Bar with pencil icon and close
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AccentIndigo.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = AccentIndigo,
                modifier = Modifier.size(18.dp)
              )
            }
            Column {
              Text(
                text = target.title,
                style = MaterialTheme.typography.titleMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "تنظیمات اختصاصی بخش انتخابی",
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textMuted
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(customColors.card)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "بستن",
              tint = customColors.textSecondary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Content for this section
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
          when (target) {
            SectionSettingsTarget.HEADER -> {
              HeaderSectionSettings(
                currentName = factorySettings.companyName,
                isDark = isDarkTheme,
                onToggleTheme = { viewModel.toggleTheme() },
                onSaveName = { name ->
                  viewModel.saveFactorySettings(
                    fixedShippingPerOrder = factorySettings.fixedShippingCostPerOrder,
                    fixedShippingPerRoll = factorySettings.fixedShippingCostPerRoll,
                    targetMargin = factorySettings.targetProfitMarginPercent,
                    overheadCost = factorySettings.overheadCostPerItem,
                    defaultAccCost = factorySettings.defaultAccessoriesCost,
                    companyName = name
                  )
                  onDismiss()
                }
              )
            }

            SectionSettingsTarget.LAYOUT -> {
              LayoutSettingsContent(
                currentLayout = factorySettings.dashboardLayout,
                onSelectLayout = { layout ->
                  viewModel.updateDashboardLayout(layout)
                  onDismiss()
                }
              )
            }

            SectionSettingsTarget.KPIS -> {
              KpisSettingsContent(
                currentMargin = factorySettings.targetProfitMarginPercent,
                onSave = { margin ->
                  viewModel.saveFactorySettings(
                    fixedShippingPerOrder = factorySettings.fixedShippingCostPerOrder,
                    fixedShippingPerRoll = factorySettings.fixedShippingCostPerRoll,
                    targetMargin = margin,
                    overheadCost = factorySettings.overheadCostPerItem,
                    defaultAccCost = factorySettings.defaultAccessoriesCost,
                    companyName = factorySettings.companyName
                  )
                  onDismiss()
                }
              )
            }

            SectionSettingsTarget.CHART -> {
              ChartSettingsContent(
                currentChartType = factorySettings.dashboardChartType,
                onSelectType = { chartType ->
                  viewModel.updateDashboardChartType(chartType)
                  onDismiss()
                }
              )
            }

            SectionSettingsTarget.ALERTS -> {
              AlertsSettingsContent(
                currentRolls = factorySettings.minFabricRollsThreshold,
                currentFabricKg = factorySettings.minFabricWeightKgThreshold,
                currentReadyGoods = factorySettings.minReadyGoodsCountThreshold,
                currentAccessoriesKg = factorySettings.minAccessoriesWeightKgThreshold,
                onSave = { r, fKg, rg, aKg ->
                  viewModel.saveFactorySettings(
                    fixedShippingPerOrder = factorySettings.fixedShippingCostPerOrder,
                    fixedShippingPerRoll = factorySettings.fixedShippingCostPerRoll,
                    targetMargin = factorySettings.targetProfitMarginPercent,
                    overheadCost = factorySettings.overheadCostPerItem,
                    defaultAccCost = factorySettings.defaultAccessoriesCost,
                    companyName = factorySettings.companyName,
                    minFabricRolls = r,
                    minFabricWeightKg = fKg,
                    minReadyGoodsCount = rg,
                    minAccessoriesWeightKg = aKg
                  )
                  onDismiss()
                }
              )
            }

            SectionSettingsTarget.ORDERS -> {
              OrdersSettingsContent(
                currentOrderShipping = factorySettings.fixedShippingCostPerOrder,
                currentRollShipping = factorySettings.fixedShippingCostPerRoll,
                onSave = { ordShip, rollShip ->
                  viewModel.saveFactorySettings(
                    fixedShippingPerOrder = ordShip,
                    fixedShippingPerRoll = rollShip,
                    targetMargin = factorySettings.targetProfitMarginPercent,
                    overheadCost = factorySettings.overheadCostPerItem,
                    defaultAccCost = factorySettings.defaultAccessoriesCost,
                    companyName = factorySettings.companyName
                  )
                  onDismiss()
                }
              )
            }

            SectionSettingsTarget.FONT -> {
              FontSettingsContent(
                currentFont = selectedFont,
                context = context,
                onSelectFont = { font ->
                  viewModel.setPersianFont(font)
                  onDismiss()
                },
                onPickCustomFont = { uri ->
                  viewModel.selectCustomFontFile(context, uri)
                  onDismiss()
                }
              )
            }

            SectionSettingsTarget.COSTS -> {
              CostsSettingsContent(
                overhead = factorySettings.overheadCostPerItem,
                accCost = factorySettings.defaultAccessoriesCost,
                onSave = { ov, acc ->
                  viewModel.saveFactorySettings(
                    fixedShippingPerOrder = factorySettings.fixedShippingCostPerOrder,
                    fixedShippingPerRoll = factorySettings.fixedShippingCostPerRoll,
                    targetMargin = factorySettings.targetProfitMarginPercent,
                    overheadCost = ov,
                    defaultAccCost = acc,
                    companyName = factorySettings.companyName
                  )
                  onDismiss()
                }
              )
            }
          }
        }
      }
    }
  }
}

// ---------------- Sub-panels for Each Section ----------------

@Composable
private fun HeaderSectionSettings(
  currentName: String,
  isDark: Boolean,
  onToggleTheme: () -> Unit,
  onSaveName: (String) -> Unit
) {
  val customColors = LocalCustomColors.current
  var nameText by remember { mutableStateOf(currentName) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "نام کارخانه یا برند تولیدی:",
      style = MaterialTheme.typography.bodyMedium,
      color = customColors.textPrimary,
      fontWeight = FontWeight.Medium
    )

    OutlinedTextField(
      value = nameText,
      onValueChange = { nameText = it },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = "حالت تم بصری:",
      style = MaterialTheme.typography.bodyMedium,
      color = customColors.textPrimary,
      fontWeight = FontWeight.Medium
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.card)
        .clickable(onClick = onToggleTheme)
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
          contentDescription = null,
          tint = AccentIndigo
        )
        Text(
          text = if (isDark) "تم تیره (Dark Mode)" else "تم روشن (Light Mode)",
          style = MaterialTheme.typography.bodyMedium,
          color = customColors.textPrimary
        )
      }
      Text(
        text = "تغییر حالت",
        style = MaterialTheme.typography.labelSmall,
        color = AccentCyan,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.weight(1f))

    Button(
      onClick = { onSaveName(nameText) },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
      shape = RoundedCornerShape(12.dp)
    ) {
      Text("ذخیره تغییرات نام کارخانه", fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun LayoutSettingsContent(
  currentLayout: String,
  onSelectLayout: (DashboardLayoutArrangement) -> Unit
) {
  val customColors = LocalCustomColors.current
  val layouts = DashboardLayoutArrangement.entries.filter {
    // Only show the 8 canonical layouts
    it in listOf(
      DashboardLayoutArrangement.CLASSIC,
      DashboardLayoutArrangement.COMPACT,
      DashboardLayoutArrangement.MANAGEMENT,
      DashboardLayoutArrangement.LARGE_CARDS,
      DashboardLayoutArrangement.TWO_COLUMN,
      DashboardLayoutArrangement.STATISTICS_FOCUSED,
      DashboardLayoutArrangement.CHARTS_FOCUSED,
      DashboardLayoutArrangement.MINIMAL
    )
  }

  LazyColumn(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    items(layouts) { l ->
      val isSelected = currentLayout == l.name
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(if (isSelected) AccentIndigo.copy(alpha = 0.15f) else customColors.card)
          .border(
            1.dp,
            if (isSelected) AccentIndigo else customColors.border,
            RoundedCornerShape(14.dp)
          )
          .clickable { onSelectLayout(l) }
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = l.title,
              style = MaterialTheme.typography.bodyLarge,
              color = if (isSelected) AccentIndigo else customColors.textPrimary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = l.desc,
              style = MaterialTheme.typography.bodySmall,
              color = customColors.textMuted,
              fontSize = 11.sp
            )
          }

          if (isSelected) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(AccentIndigo),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun KpisSettingsContent(
  currentMargin: Double,
  onSave: (Double) -> Unit
) {
  val customColors = LocalCustomColors.current
  var marginText by remember { mutableStateOf(currentMargin.toInt().toString()) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "حاشیه سود هدف کارخانه (درصد):",
      style = MaterialTheme.typography.bodyMedium,
      color = customColors.textPrimary,
      fontWeight = FontWeight.Medium
    )

    OutlinedTextField(
      value = marginText,
      onValueChange = { marginText = it },
      modifier = Modifier.fillMaxWidth(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      shape = RoundedCornerShape(12.dp),
      suffix = { Text("٪", color = customColors.textMuted) }
    )

    Text(
      text = "این حاشیه سود به عنوان شاخص بنچمارک مقایسه سود واقعی سفارشات و گزارشات مالی کارخانه استفاده خواهد شد.",
      style = MaterialTheme.typography.bodySmall,
      color = customColors.textMuted
    )

    Spacer(modifier = Modifier.weight(1f))

    Button(
      onClick = { onSave(marginText.toDoubleOrNull() ?: currentMargin) },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
      shape = RoundedCornerShape(12.dp)
    ) {
      Text("ذخیره حاشیه سود هدف", fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun ChartSettingsContent(
  currentChartType: String,
  onSelectType: (DashboardChartType) -> Unit
) {
  val customColors = LocalCustomColors.current
  val types = listOf(
    DashboardChartType.BAR_LINE to "ترکیبی ستونی و خطی (فروش و سود همزمان)",
    DashboardChartType.BAR to "تک‌نمودار میله‌ای فروش دوره‌ای",
    DashboardChartType.AREA_LINE to "منحنـی نرم و سایه‌دار خطی",
    DashboardChartType.DONUT_PRIMARY to "نمودار دوناتی ارزش دارایی‌های انبار"
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Text(
      text = "انتخاب استایل و نوع نمایش نمودار صفحه اصلی:",
      style = MaterialTheme.typography.bodyMedium,
      color = customColors.textPrimary,
      fontWeight = FontWeight.Medium
    )

    types.forEach { (type, label) ->
      val isSelected = currentChartType == type.name
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(if (isSelected) AccentIndigo.copy(alpha = 0.15f) else customColors.card)
          .border(
            1.dp,
            if (isSelected) AccentIndigo else customColors.border,
            RoundedCornerShape(12.dp)
          )
          .clickable { onSelectType(type) }
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) AccentIndigo else customColors.textPrimary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )
          if (isSelected) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = AccentIndigo,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun AlertsSettingsContent(
  currentRolls: Int,
  currentFabricKg: Double,
  currentReadyGoods: Int,
  currentAccessoriesKg: Double,
  onSave: (Int, Double, Int, Double) -> Unit
) {
  val customColors = LocalCustomColors.current
  var rollsText by remember { mutableStateOf(currentRolls.toString()) }
  var fabricKgText by remember { mutableStateOf(currentFabricKg.toInt().toString()) }
  var readyText by remember { mutableStateOf(currentReadyGoods.toString()) }
  var accKgText by remember { mutableStateOf(currentAccessoriesKg.toInt().toString()) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Text(
      text = "حداقل موجودی هشدار طاقه‌های پارچه (عدد طاقه):",
      style = MaterialTheme.typography.labelSmall,
      color = customColors.textMuted
    )
    OutlinedTextField(
      value = rollsText,
      onValueChange = { rollsText = it },
      modifier = Modifier.fillMaxWidth(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      shape = RoundedCornerShape(10.dp)
    )

    Text(
      text = "حداقل موجودی هشدار وزن پارچه (کیلوگرم):",
      style = MaterialTheme.typography.labelSmall,
      color = customColors.textMuted
    )
    OutlinedTextField(
      value = fabricKgText,
      onValueChange = { fabricKgText = it },
      modifier = Modifier.fillMaxWidth(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      shape = RoundedCornerShape(10.dp)
    )

    Text(
      text = "حداقل موجودی هشدار محصولات آماده (عدد لباس):",
      style = MaterialTheme.typography.labelSmall,
      color = customColors.textMuted
    )
    OutlinedTextField(
      value = readyText,
      onValueChange = { readyText = it },
      modifier = Modifier.fillMaxWidth(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      shape = RoundedCornerShape(10.dp)
    )

    Spacer(modifier = Modifier.weight(1f))

    Button(
      onClick = {
        onSave(
          rollsText.toIntOrNull() ?: currentRolls,
          fabricKgText.toDoubleOrNull() ?: currentFabricKg,
          readyText.toIntOrNull() ?: currentReadyGoods,
          accKgText.toDoubleOrNull() ?: currentAccessoriesKg
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
      shape = RoundedCornerShape(12.dp)
    ) {
      Text("ذخیره آستانه‌های کمبود", fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun OrdersSettingsContent(
  currentOrderShipping: Long,
  currentRollShipping: Long,
  onSave: (Long, Long) -> Unit
) {
  val customColors = LocalCustomColors.current
  var orderShipText by remember { mutableStateOf(currentOrderShipping.toString()) }
  var rollShipText by remember { mutableStateOf(currentRollShipping.toString()) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Text(
      text = "کرایه حمل پیش‌فرض هر سفارش (تومان):",
      style = MaterialTheme.typography.bodyMedium,
      color = customColors.textPrimary
    )
    OutlinedTextField(
      value = orderShipText,
      onValueChange = { orderShipText = it },
      modifier = Modifier.fillMaxWidth(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      shape = RoundedCornerShape(12.dp)
    )

    Text(
      text = "کرایه باربری تخصیصی هر طاقه پارچه (تومان):",
      style = MaterialTheme.typography.bodyMedium,
      color = customColors.textPrimary
    )
    OutlinedTextField(
      value = rollShipText,
      onValueChange = { rollShipText = it },
      modifier = Modifier.fillMaxWidth(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.weight(1f))

    Button(
      onClick = {
        onSave(
          orderShipText.toLongOrNull() ?: currentOrderShipping,
          rollShipText.toLongOrNull() ?: currentRollShipping
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
      shape = RoundedCornerShape(12.dp)
    ) {
      Text("ذخیره تنظیمات سفارش و باربری", fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
private fun FontSettingsContent(
  currentFont: PersianFont,
  context: Context,
  onSelectFont: (PersianFont) -> Unit,
  onPickCustomFont: (Uri) -> Unit
) {
  val customColors = LocalCustomColors.current

  // Document file picker for TTF/OTF custom fonts
  val filePicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri: Uri? ->
    uri?.let { onPickCustomFont(it) }
  }

  val fonts = listOf(
    PersianFont.YEKAN to "ایران‌یکان (استاندارد نرم‌افزارهای مدیریتی)",
    PersianFont.VAZIR to "وزیر متن (بسیار خوانا و شفاف)",
    PersianFont.SHABNAM to "شبنم (مدرن و تمیز)",
    PersianFont.SYSTEM to "فونت پیش‌فرض سیستم اندروید"
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Text(
      text = "انتخاب قلم فارسی برای تمامی بخش‌های نرم‌افزار:",
      style = MaterialTheme.typography.bodyMedium,
      color = customColors.textPrimary,
      fontWeight = FontWeight.Medium
    )

    fonts.forEach { (font, label) ->
      val isSelected = currentFont == font
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(if (isSelected) AccentIndigo.copy(alpha = 0.15f) else customColors.card)
          .border(
            1.dp,
            if (isSelected) AccentIndigo else customColors.border,
            RoundedCornerShape(12.dp)
          )
          .clickable { onSelectFont(font) }
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) AccentIndigo else customColors.textPrimary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )
          if (isSelected) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = AccentIndigo,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Custom Font File Picker Button
    Button(
      onClick = {
        filePicker.launch(arrayOf("*/*"))
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(46.dp),
      colors = ButtonDefaults.buttonColors(containerColor = customColors.card),
      shape = RoundedCornerShape(12.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.FileUpload,
          contentDescription = null,
          tint = AccentIndigo,
          modifier = Modifier.size(18.dp)
        )
        Text(
          text = "انتخاب فایل فونت از حافظه گوشی (TTF / OTF)",
          color = customColors.textPrimary,
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Live Preview Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = "پیش‌نمایش قلم فعلی:",
          style = MaterialTheme.typography.labelSmall,
          color = customColors.textMuted
        )
        Text(
          text = "۱۲۳۴۵۶۷۸۹۰ - کارخانه پوشاک، خط دوخت و انبار پارچه",
          style = MaterialTheme.typography.bodyMedium,
          color = customColors.textPrimary,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
private fun CostsSettingsContent(
  overhead: Long,
  accCost: Long,
  onSave: (Long, Long) -> Unit
) {
  val customColors = LocalCustomColors.current
  var overheadText by remember { mutableStateOf(overhead.toString()) }
  var accCostText by remember { mutableStateOf(accCost.toString()) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Text(
      text = "هزینه سربار پیش‌فرض هر عدد لباس (تومان):",
      style = MaterialTheme.typography.bodyMedium,
      color = customColors.textPrimary
    )
    OutlinedTextField(
      value = overheadText,
      onValueChange = { overheadText = it },
      modifier = Modifier.fillMaxWidth(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      shape = RoundedCornerShape(12.dp)
    )

    Text(
      text = "هزینه ملزومات پیش‌فرض هر عدد کار (تومان):",
      style = MaterialTheme.typography.bodyMedium,
      color = customColors.textPrimary
    )
    OutlinedTextField(
      value = accCostText,
      onValueChange = { accCostText = it },
      modifier = Modifier.fillMaxWidth(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.weight(1f))

    Button(
      onClick = {
        onSave(
          overheadText.toLongOrNull() ?: overhead,
          accCostText.toLongOrNull() ?: accCost
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
      shape = RoundedCornerShape(12.dp)
    ) {
      Text("ذخیره تنظیمات هزینه‌ها", fontWeight = FontWeight.Bold)
    }
  }
}
