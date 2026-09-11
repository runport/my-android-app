package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DrillDownComponent
import com.example.data.model.DrillDownData
import com.example.data.model.DrillDownItem
import com.example.data.service.FinancialCalculationService
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@Composable
fun DrillDownDialog(
  data: DrillDownData,
  onDismiss: () -> Unit,
  onMetricClick: (String) -> Unit = {}
) {
  val customColors = LocalCustomColors.current

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.88f)
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, customColors.border, RoundedCornerShape(24.dp)),
      color = customColors.card
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // 1. Header Bar
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
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = AccentIndigo,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = data.title,
                style = MaterialTheme.typography.titleMedium,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "کاوش سلسله‌مراتبی داده‌ها (Drill-Down)",
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textMuted
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
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

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Hero Total Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(customColors.card)
            .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
            .padding(16.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "مقدار کل این شاخص",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.textMuted
            )
            Text(
              text = data.formattedTotal,
              style = MaterialTheme.typography.headlineMedium,
              color = AccentIndigo,
              fontWeight = FontWeight.ExtraBold
            )
            if (data.subtitle.isNotEmpty()) {
              Text(
                text = data.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = customColors.textSecondary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Scrollable Content (Components breakdown + Real constituent records)
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Components Breakdown
          if (data.components.isNotEmpty()) {
            item {
              Text(
                text = "تفکیک سازنده و اجزای مالی",
                style = MaterialTheme.typography.labelLarge,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
            }

            items(data.components) { comp ->
              DrillDownComponentRow(
                component = comp,
                onClick = {
                  if (comp.keyId.isNotEmpty()) {
                    onMetricClick(comp.keyId)
                  }
                }
              )
            }
          }

          // Real Items List
          if (data.items.isNotEmpty()) {
            item {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "اسناد و داده‌های ثبت‌شده در دیتابیس (${data.items.size} مورد)",
                style = MaterialTheme.typography.labelLarge,
                color = customColors.textPrimary,
                fontWeight = FontWeight.Bold
              )
            }

            items(data.items) { item ->
              DrillDownRecordRow(item = item)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Close button
        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
          colors = ButtonDefaults.buttonColors(containerColor = customColors.card),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = "بستن پنجره کاوش",
            color = customColors.textPrimary,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}

@Composable
private fun DrillDownComponentRow(
  component: DrillDownComponent,
  onClick: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val isClickable = component.keyId.isNotEmpty()

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(customColors.card.copy(alpha = 0.7f))
      .border(1.dp, customColors.border.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
      .clickable(enabled = isClickable, onClick = onClick)
      .padding(12.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
              .size(24.dp)
              .clip(CircleShape)
              .background(
                if (component.isPositive) StatusSuccess.copy(alpha = 0.15f) else StatusDanger.copy(alpha = 0.15f)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (component.isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
              contentDescription = null,
              tint = if (component.isPositive) StatusSuccess else StatusDanger,
              modifier = Modifier.size(14.dp)
            )
          }

          Text(
            text = component.label,
            style = MaterialTheme.typography.bodyMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Medium
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "${if (component.isPositive) "+" else "-"}${FinancialCalculationService.formatCurrency(component.amount)}",
            style = MaterialTheme.typography.labelLarge,
            color = if (component.isPositive) StatusSuccess else customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
          if (isClickable) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "کاوش",
              tint = customColors.textMuted,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      if (component.percentage > 0.0) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          LinearProgressIndicator(
            progress = { (component.percentage / 100.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
              .weight(1f)
              .height(5.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = if (component.isPositive) StatusSuccess else AccentIndigo,
            trackColor = customColors.border
          )
          Text(
            text = "${component.percentage.toInt()}٪",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textMuted,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}

@Composable
private fun DrillDownRecordRow(item: DrillDownItem) {
  val customColors = LocalCustomColors.current

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
      .padding(10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(2.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = item.id,
            style = MaterialTheme.typography.labelSmall,
            color = AccentIndigo,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
          Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.SemiBold
          )
        }
        Text(
          text = item.subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = customColors.textMuted,
          fontSize = 11.sp
        )
      }

      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(2.dp)
      ) {
        if (item.amount > 0L) {
          Text(
            text = FinancialCalculationService.formatCurrency(item.amount),
            style = MaterialTheme.typography.labelMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold
          )
        }
        item.badgeText?.let { badge ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(AccentCyan.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = badge,
              style = MaterialTheme.typography.labelSmall,
              color = AccentCyan,
              fontSize = 9.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }
}
