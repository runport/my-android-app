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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.util.PersianDateHelper

@Composable
fun CustomDateRangeDialog(
  initialFromPersian: String = "",
  initialToPersian: String = "",
  onDismiss: () -> Unit,
  onConfirm: (fromPersian: String, toPersian: String, fromTime: Long, toTime: Long) -> Unit
) {
  val customColors = LocalCustomColors.current
  val (curY, curM, curD) = PersianDateHelper.getCurrentJalaliDate()
  val mName = PersianDateHelper.persianMonths.getOrElse(curM - 1) { "اسفند" }

  var fromDay by remember {
    mutableStateOf(
      if (initialFromPersian.isNotEmpty()) {
        PersianDateHelper.toEnglishDigits(initialFromPersian).filter { it.isDigit() }.toIntOrNull() ?: 1
      } else 1
    )
  }
  var toDay by remember {
    mutableStateOf(
      if (initialToPersian.isNotEmpty()) {
        PersianDateHelper.toEnglishDigits(initialToPersian).filter { it.isDigit() }.toIntOrNull() ?: curD
      } else curD
    )
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, customColors.border, RoundedCornerShape(24.dp))
        .testTag("custom_date_range_dialog"),
      color = customColors.card
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.DateRange,
              contentDescription = null,
              tint = AccentIndigo,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = "انتخاب بازه زمانی سفارشی",
              style = MaterialTheme.typography.titleMedium,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "بستن",
              tint = customColors.textMuted
            )
          }
        }

        // Quick Preset Ranges
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "انتخاب سریع بازه:",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textMuted
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              Triple("۷ روز اخیر", (curD - 7).coerceAtLeast(1), curD),
              Triple("۱۵ روز اخیر", (curD - 15).coerceAtLeast(1), curD),
              Triple("کل این ماه", 1, 29)
            ).forEach { (lbl, s, e) ->
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(customColors.secondaryBg)
                  .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
                  .clickable {
                    fromDay = s
                    toDay = e
                  }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = lbl,
                  style = MaterialTheme.typography.labelSmall,
                  color = customColors.textSecondary,
                  fontSize = 10.sp
                )
              }
            }
          }
        }

        // From Date Day Picker
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(customColors.secondaryBg)
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "از تاریخ: ${PersianDateHelper.toPersianDigits(fromDay)} $mName $curY",
            style = MaterialTheme.typography.bodyMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(1, 5, 10, 15, 20).forEach { d ->
              val isSelected = fromDay == d
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) AccentIndigo else customColors.card)
                  .clickable { fromDay = d }
                  .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "$d",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isSelected) Color.White else customColors.textSecondary,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }

        // To Date Day Picker
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(customColors.secondaryBg)
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "تا تاریخ: ${PersianDateHelper.toPersianDigits(toDay)} $mName $curY",
            style = MaterialTheme.typography.bodyMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(15, 20, 25, 28, 29).forEach { d ->
              val isSelected = toDay == d
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) AccentCyan else customColors.card)
                  .clickable { toDay = d }
                  .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "$d",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isSelected) Color.White else customColors.textSecondary,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              val sDay = fromDay.coerceAtMost(toDay)
              val eDay = toDay.coerceAtLeast(fromDay)
              val fromP = "${PersianDateHelper.toPersianDigits(sDay)} $mName"
              val toP = "${PersianDateHelper.toPersianDigits(eDay)} $mName"
              val fromTime = PersianDateHelper.jalaliToTimestamp(curY, curM, sDay)
              val toTime = PersianDateHelper.jalaliToTimestamp(curY, curM, eDay) + 24 * 3600 * 1000L - 1000L
              onConfirm(fromP, toP, fromTime, toTime)
            },
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("confirm_custom_date_range_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
          ) {
            Text("اعمال بازه زمانی", fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = onDismiss,
            modifier = Modifier
              .weight(0.5f)
              .height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = customColors.secondaryBg)
          ) {
            Text("انصراف", color = customColors.textSecondary)
          }
        }
      }
    }
  }
}
