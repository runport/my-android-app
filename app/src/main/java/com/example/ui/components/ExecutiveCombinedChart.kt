package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppTimeRangeState
import com.example.data.model.ChartDisplayMetric
import com.example.data.model.CombinedChartPoint
import com.example.data.model.DashboardChartType
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusSuccess

@Composable
fun ExecutiveCombinedChart(
  points: List<CombinedChartPoint>,
  timeRange: AppTimeRangeState,
  chartType: DashboardChartType,
  displayMetric: ChartDisplayMetric = ChartDisplayMetric.COMBINED,
  showValues: Boolean = false,
  onOpenSettings: () -> Unit,
  onToggleShowValues: () -> Unit,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  var selectedIndex by remember { mutableStateOf<Int?>(null) }

  val salesColor = AccentIndigo
  val profitColor = StatusSuccess

  val showSales = displayMetric == ChartDisplayMetric.COMBINED || displayMetric == ChartDisplayMetric.SALES_ONLY
  val showProfit = displayMetric == ChartDisplayMetric.COMBINED || displayMetric == ChartDisplayMetric.PROFIT_ONLY

  val dynamicTitle = when (displayMetric) {
    ChartDisplayMetric.COMBINED -> timeRange.getChartTitle("فروش و سود خالص")
    ChartDisplayMetric.SALES_ONLY -> timeRange.getChartTitle("فروش")
    ChartDisplayMetric.PROFIT_ONLY -> timeRange.getChartTitle("سود خالص")
  }

  if (points.isEmpty()) {
    Box(
      modifier = modifier
        .fillMaxWidth()
        .height(220.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(customColors.card)
        .border(1.dp, customColors.border, RoundedCornerShape(20.dp)),
      contentAlignment = Alignment.Center
    ) {
      Text("داده‌ای برای این بازه زمانی یافت نشد", color = customColors.textMuted)
    }
    return
  }

  val activePoint = selectedIndex?.let { points.getOrNull(it) } ?: points.last()

  val maxSales = points.maxOfOrNull { it.sales } ?: 1L
  val maxProfit = points.maxOfOrNull { it.profit } ?: 1L
  val maxVal = maxOf(maxSales, maxProfit).toDouble().coerceAtLeast(100_000.0)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(customColors.card)
      .border(1.dp, customColors.border, RoundedCornerShape(20.dp))
      .padding(16.dp)
      .testTag("executive_combined_chart_card")
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // 1. Chart Header with Dynamic Title and Edit Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          Text(
            text = dynamicTitle,
            style = MaterialTheme.typography.titleMedium,
            color = customColors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (showSales) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(salesColor)
                )
                Text(
                  text = "فروش",
                  style = MaterialTheme.typography.labelSmall,
                  color = customColors.textSecondary,
                  fontSize = 10.sp
                )
              }
            }
            if (showProfit) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(profitColor)
                )
                Text(
                  text = "سود خالص",
                  style = MaterialTheme.typography.labelSmall,
                  color = customColors.textSecondary,
                  fontSize = 10.sp
                )
              }
            }
          }
        }

        // Action Icons: Toggle Numbers + Edit / Settings Button "✏️"
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Toggle Values Icon
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (showValues) AccentCyan.copy(alpha = 0.15f) else customColors.secondaryBg)
              .clickable { onToggleShowValues() }
              .testTag("chart_toggle_values_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (showValues) Icons.Default.Visibility else Icons.Default.VisibilityOff,
              contentDescription = "نمایش ارقام",
              tint = if (showValues) AccentCyan else customColors.textMuted,
              modifier = Modifier.size(18.dp)
            )
          }

          // Edit Settings Icon "✏️"
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(AccentIndigo.copy(alpha = 0.12f))
              .clickable { onOpenSettings() }
              .testTag("chart_settings_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "تنظیمات چارت",
              tint = AccentIndigo,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // 2. Interactive Tooltip Header (Shows active point numbers)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(customColors.secondaryBg.copy(alpha = 0.6f))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "بازه: ${activePoint.label}",
          style = MaterialTheme.typography.labelMedium,
          color = customColors.textSecondary,
          fontWeight = FontWeight.Bold
        )

        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (showSales) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = "فروش:",
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
              Text(
                text = activePoint.formattedSales,
                style = MaterialTheme.typography.labelMedium,
                color = salesColor,
                fontWeight = FontWeight.Bold
              )
            }
          }

          if (showProfit) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = "سود:",
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textMuted,
                fontSize = 11.sp
              )
              Text(
                text = activePoint.formattedProfit,
                style = MaterialTheme.typography.labelMedium,
                color = profitColor,
                fontWeight = FontWeight.Bold
              )
            }
          }

          if (displayMetric == ChartDisplayMetric.COMBINED) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(profitColor.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "${activePoint.profitMarginPercent}٪ حاشیه",
                style = MaterialTheme.typography.labelSmall,
                color = profitColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
          }
        }
      }

      // Optional direct value row above chart
      if (showValues) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          points.forEach { pt ->
            val vStr = if (displayMetric == ChartDisplayMetric.PROFIT_ONLY) pt.formattedProfit else pt.formattedSales
            Text(
              text = vStr,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 9.sp,
              color = customColors.textPrimary,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // 3. Canvas Drawing based on Chart Type
      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .pointerInput(points, chartType) {
            detectTapGestures(
              onPress = { offset ->
                val slotWidth = size.width / points.size.coerceAtLeast(1)
                val index = (offset.x / slotWidth).toInt().coerceIn(0, points.size - 1)
                selectedIndex = index
              }
            )
          }
      ) {
        val width = size.width
        val height = size.height
        val gridColor = customColors.borderSubtle.copy(alpha = 0.4f)

        // Draw 3 horizontal dashed guidelines
        val gridCount = 3
        for (i in 0..gridCount) {
          val y = height * (i.toFloat() / gridCount)
          drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
          )
        }

        val pointCount = points.size
        val slotWidth = width / pointCount.coerceAtLeast(1)
        val selectedIdx = selectedIndex ?: (pointCount - 1)

        when (chartType) {
          DashboardChartType.BAR, DashboardChartType.BAR_LINE -> {
            val barWidth = (slotWidth * 0.45f).coerceIn(10.dp.toPx(), 36.dp.toPx())
            val slotBg = customColors.secondaryBg.copy(alpha = 0.4f)

            points.forEachIndexed { index, pt ->
              val isSelected = index == selectedIdx
              val centerX = index * slotWidth + slotWidth / 2f

              // Bar background slot
              drawRoundRect(
                color = slotBg,
                topLeft = Offset(centerX - barWidth / 2f, 0f),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
              )

              if (showSales) {
                val normSales = (pt.sales.toDouble() / maxVal).coerceIn(0.05, 1.0)
                val barH = (normSales * (height - 20.dp.toPx())).toFloat()
                val top = height - barH

                drawRoundRect(
                  brush = Brush.verticalGradient(
                    colors = listOf(
                      if (isSelected) salesColor else salesColor.copy(alpha = 0.85f),
                      if (isSelected) salesColor.copy(alpha = 0.45f) else salesColor.copy(alpha = 0.2f)
                    ),
                    startY = top,
                    endY = height
                  ),
                  topLeft = Offset(centerX - barWidth / 2f, top),
                  size = Size(barWidth, barH),
                  cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
              }
            }

            // In BAR_LINE mode, draw Profit as an overlay smooth line with glowing dots!
            if (chartType == DashboardChartType.BAR_LINE && showProfit) {
              val profitCoords = points.mapIndexed { index, pt ->
                val x = index * slotWidth + slotWidth / 2f
                val normProfit = (pt.profit.toDouble() / maxVal).coerceIn(0.05, 1.0)
                val y = height - (normProfit * (height - 30.dp.toPx())).toFloat()
                Offset(x, y)
              }

              val profitPath = Path()
              profitCoords.forEachIndexed { idx, offset ->
                if (idx == 0) profitPath.moveTo(offset.x, offset.y)
                else {
                  val prev = profitCoords[idx - 1]
                  val cx = (prev.x + offset.x) / 2f
                  profitPath.cubicTo(cx, prev.y, cx, offset.y, offset.x, offset.y)
                }
              }

              drawPath(
                path = profitPath,
                color = profitColor,
                style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
              )

              // Draw dots on profit line
              profitCoords.forEachIndexed { idx, offset ->
                val isSelected = idx == selectedIdx
                if (isSelected) {
                  drawCircle(profitColor.copy(alpha = 0.35f), radius = 8.dp.toPx(), center = offset)
                }
                drawCircle(profitColor, radius = 4.5.dp.toPx(), center = offset)
                drawCircle(Color.White, radius = 2.dp.toPx(), center = offset)
              }
            }
          }

          DashboardChartType.LINE, DashboardChartType.AREA_LINE, DashboardChartType.DONUT_PRIMARY -> {
            // Draw Sales Line / Area
            if (showSales) {
              val salesCoords = points.mapIndexed { index, pt ->
                val x = if (pointCount > 1) index * (width / (pointCount - 1)) else width / 2f
                val norm = (pt.sales.toDouble() / maxVal).coerceIn(0.05, 1.0)
                val y = height - (norm * (height - 30.dp.toPx())).toFloat()
                Offset(x, y)
              }

              val salesPath = Path()
              salesCoords.forEachIndexed { idx, offset ->
                if (idx == 0) salesPath.moveTo(offset.x, offset.y)
                else {
                  val prev = salesCoords[idx - 1]
                  val cx = (prev.x + offset.x) / 2f
                  salesPath.cubicTo(cx, prev.y, cx, offset.y, offset.x, offset.y)
                }
              }

              if (chartType == DashboardChartType.AREA_LINE) {
                val fillPath = Path().apply {
                  addPath(salesPath)
                  lineTo(width, height)
                  lineTo(0f, height)
                  close()
                }
                drawPath(
                  path = fillPath,
                  brush = Brush.verticalGradient(
                    colors = listOf(salesColor.copy(alpha = 0.28f), Color.Transparent),
                    startY = 0f,
                    endY = height
                  )
                )
              }

              drawPath(
                path = salesPath,
                color = salesColor,
                style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
              )

              salesCoords.forEachIndexed { idx, offset ->
                val isSelected = idx == selectedIdx
                if (isSelected) {
                  drawCircle(salesColor.copy(alpha = 0.35f), radius = 9.dp.toPx(), center = offset)
                }
                drawCircle(salesColor, radius = 4.5.dp.toPx(), center = offset)
                drawCircle(Color.White, radius = 2.dp.toPx(), center = offset)
              }
            }

            // Draw Profit Line / Area
            if (showProfit) {
              val profitCoords = points.mapIndexed { index, pt ->
                val x = if (pointCount > 1) index * (width / (pointCount - 1)) else width / 2f
                val norm = (pt.profit.toDouble() / maxVal).coerceIn(0.05, 1.0)
                val y = height - (norm * (height - 30.dp.toPx())).toFloat()
                Offset(x, y)
              }

              val profitPath = Path()
              profitCoords.forEachIndexed { idx, offset ->
                if (idx == 0) profitPath.moveTo(offset.x, offset.y)
                else {
                  val prev = profitCoords[idx - 1]
                  val cx = (prev.x + offset.x) / 2f
                  profitPath.cubicTo(cx, prev.y, cx, offset.y, offset.x, offset.y)
                }
              }

              if (chartType == DashboardChartType.AREA_LINE) {
                val fillPath = Path().apply {
                  addPath(profitPath)
                  lineTo(width, height)
                  lineTo(0f, height)
                  close()
                }
                drawPath(
                  path = fillPath,
                  brush = Brush.verticalGradient(
                    colors = listOf(profitColor.copy(alpha = 0.22f), Color.Transparent),
                    startY = 0f,
                    endY = height
                  )
                )
              }

              drawPath(
                path = profitPath,
                color = profitColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
              )

              profitCoords.forEachIndexed { idx, offset ->
                val isSelected = idx == selectedIdx
                if (isSelected) {
                  drawCircle(profitColor.copy(alpha = 0.35f), radius = 9.dp.toPx(), center = offset)
                }
                drawCircle(profitColor, radius = 4.5.dp.toPx(), center = offset)
                drawCircle(Color.White, radius = 2.dp.toPx(), center = offset)
              }
            }
          }
        }
      }

      // 4. Bottom X-Axis Labels
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        points.forEachIndexed { idx, p ->
          val isSelected = (selectedIndex ?: (points.size - 1)) == idx
          Text(
            text = p.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) customColors.textPrimary else customColors.textMuted,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}
