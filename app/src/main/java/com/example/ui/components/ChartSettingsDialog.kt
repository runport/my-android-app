package com.example.ui.components

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppTimeRangeState
import com.example.data.model.ChartDisplayMetric
import com.example.data.model.DashboardChartType
import com.example.data.model.TimeRangeMode
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusSuccess
import com.example.util.PersianDateHelper

@Composable
fun ChartSettingsDialog(
  currentTimeRange: AppTimeRangeState,
  currentChartType: DashboardChartType,
  currentMetric: ChartDisplayMetric,
  showValues: Boolean,
  onDismiss: () -> Unit,
  onSave: (
    timeRangeMode: TimeRangeMode,
    customFromDate: String,
    customToDate: String,
    chartType: DashboardChartType,
    metric: ChartDisplayMetric,
    showValues: Boolean
  ) -> Unit
) {
  val customColors = LocalCustomColors.current

  var selectedMode by remember { mutableStateOf(currentTimeRange.mode) }
  var selectedChartType by remember { mutableStateOf(currentChartType) }
  var selectedMetric by remember { mutableStateOf(currentMetric) }
  var selectedShowValues by remember { mutableStateOf(showValues) }

  // Custom date range state
  val (curY, curM, curD) = PersianDateHelper.getCurrentJalaliDate()
  val mName = PersianDateHelper.persianMonths.getOrElse(curM - 1) { "اسفند" }

  var fromDay by remember {
    mutableStateOf(
      if (currentTimeRange.customFromPersian.isNotEmpty()) {
        PersianDateHelper.toEnglishDigits(currentTimeRange.customFromPersian).filter { it.isDigit() }.toIntOrNull() ?: 1
      } else 1
    )
  }
  var toDay by remember {
    mutableStateOf(
      if (currentTimeRange.customToPersian.isNotEmpty()) {
        PersianDateHelper.toEnglishDigits(currentTimeRange.customToPersian).filter { it.isDigit() }.toIntOrNull() ?: curD
      } else curD
    )
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, customColors.border, RoundedCornerShape(24.dp))
        .testTag("chart_settings_dialog"),
      color = customColors.card
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "تنظیمات اختصاصی نمودار",
            style = MaterialTheme.typography.titleMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "بستن",
              tint = customColors.textMuted
            )
          }
        }

        // 1. Time Range Section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "بازه زمانی محاسبات (Time Range):",
            style = MaterialTheme.typography.labelMedium,
            color = customColors.textSecondary,
            fontWeight = FontWeight.Bold
          )

          val timeModes = listOf(
            TimeRangeMode.TODAY_24H to "امروز (۲۴ ساعت)",
            TimeRangeMode.LAST_MONTH to "ماه اخیر (۳۰ روز)",
            TimeRangeMode.LAST_YEAR to "سال اخیر (۱۲ ماه)",
            TimeRangeMode.CUSTOM to "بازه انتخابی (Custom)"
          )

          timeModes.forEach { (mode, label) ->
            val isSelected = selectedMode == mode
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) AccentIndigo.copy(alpha = 0.14f) else customColors.secondaryBg)
                .border(1.dp, if (isSelected) AccentIndigo else customColors.border, RoundedCornerShape(12.dp))
                .clickable { selectedMode = mode }
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) AccentIndigo else customColors.textPrimary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
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

          // Custom Date Range Selector (From Date to To Date)
          if (selectedMode == TimeRangeMode.CUSTOM) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(customColors.secondaryBg)
                .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Text(
                text = "تعیین بازه از تاریخ تا تاریخ ($mName $curY):",
                style = MaterialTheme.typography.labelSmall,
                color = AccentCyan,
                fontWeight = FontWeight.Bold
              )

              // From Date Day Selector
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "از روز: ${PersianDateHelper.toPersianDigits(fromDay)} $mName",
                  style = MaterialTheme.typography.bodySmall,
                  color = customColors.textPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  listOf(1, 5, 10, 15).forEach { d ->
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (fromDay == d) AccentCyan else customColors.card)
                        .clickable { fromDay = d }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Text(
                        text = "$d",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (fromDay == d) Color.White else customColors.textSecondary
                      )
                    }
                  }
                }
              }

              // To Date Day Selector
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "تا روز: ${PersianDateHelper.toPersianDigits(toDay)} $mName",
                  style = MaterialTheme.typography.bodySmall,
                  color = customColors.textPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  listOf(15, 20, 25, 29).forEach { d ->
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (toDay == d) AccentCyan else customColors.card)
                        .clickable { toDay = d }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Text(
                        text = "$d",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (toDay == d) Color.White else customColors.textSecondary
                      )
                    }
                  }
                }
              }
            }
          }
        }

        // 2. Chart Type Section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "نوع نمودار (Chart Type):",
            style = MaterialTheme.typography.labelMedium,
            color = customColors.textSecondary,
            fontWeight = FontWeight.Bold
          )

          val chartTypes = listOf(
            DashboardChartType.BAR_LINE to ("ترکیبی (Bar + Line)" to "ستون برای فروش و خط برای سود خالص"),
            DashboardChartType.LINE to ("خطی پیوسته (Line)" to "روند هموار هر دو شاخص با نقاط عطف"),
            DashboardChartType.BAR to ("میله‌ای آماری (Bar)" to "مقایسه ستونی فروش و سود"),
            DashboardChartType.AREA_LINE to ("مساحتی گرادیانت (Area)" to "منحنی با سایه گرادیانت زیر نمودار"),
            DashboardChartType.DONUT_PRIMARY to ("چارت دونات پرتفوی" to "سهم ریالی دارایی‌های کارخانه")
          )

          chartTypes.forEach { (type, meta) ->
            val isSelected = selectedChartType == type
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) AccentIndigo.copy(alpha = 0.14f) else customColors.secondaryBg)
                .border(1.dp, if (isSelected) AccentIndigo else customColors.border, RoundedCornerShape(12.dp))
                .clickable { selectedChartType = type }
                .padding(horizontal = 14.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
              ) {
                Text(
                  text = meta.first,
                  style = MaterialTheme.typography.bodyMedium,
                  color = if (isSelected) AccentIndigo else customColors.textPrimary,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 12.sp
                )
                Text(
                  text = meta.second,
                  style = MaterialTheme.typography.labelSmall,
                  color = customColors.textMuted,
                  fontSize = 10.sp
                )
              }
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

        // 3. Display Metric Section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "شاخص‌های قابل نمایش:",
            style = MaterialTheme.typography.labelMedium,
            color = customColors.textSecondary,
            fontWeight = FontWeight.Bold
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(
              ChartDisplayMetric.COMBINED to "فروش + سود",
              ChartDisplayMetric.SALES_ONLY to "فقط فروش",
              ChartDisplayMetric.PROFIT_ONLY to "فقط سود"
            ).forEach { (m, label) ->
              val isSelected = selectedMetric == m
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) AccentIndigo else customColors.secondaryBg)
                  .clickable { selectedMetric = m }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isSelected) Color.White else customColors.textSecondary,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 10.sp
                )
              }
            }
          }
        }

        // 4. Show Values Toggle
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(customColors.secondaryBg)
            .padding(horizontal = 14.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
              text = "نمایش ارقام و مقادیر روی چارت",
              style = MaterialTheme.typography.bodyMedium,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
            Text(
              text = "برچسب مبالغ ریالی بر روی نقاط و ستون‌ها",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.textMuted,
              fontSize = 10.sp
            )
          }

          Switch(
            checked = selectedShowValues,
            onCheckedChange = { selectedShowValues = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = AccentIndigo
            )
          )
        }

        // 5. Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              val fromP = "${PersianDateHelper.toPersianDigits(fromDay)} $mName"
              val toP = "${PersianDateHelper.toPersianDigits(toDay)} $mName"
              onSave(selectedMode, fromP, toP, selectedChartType, selectedMetric, selectedShowValues)
            },
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("save_chart_settings_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
          ) {
            Text("ذخیره و اعمال تنظیمات", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }

          Button(
            onClick = onDismiss,
            modifier = Modifier
              .weight(0.5f)
              .height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = customColors.secondaryBg)
          ) {
            Text("انصراف", color = customColors.textSecondary, fontSize = 12.sp)
          }
        }
      }
    }
  }
}
