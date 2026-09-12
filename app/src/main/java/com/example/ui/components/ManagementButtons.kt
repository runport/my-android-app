package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.LocalCustomColors
import com.example.ui.theme.StatusDanger

// ---------- Icon buttons (for cards) ----------

@Composable
fun ManagementEditButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val c = LocalCustomColors.current
  IconButton(onClick = onClick, modifier = modifier.size(32.dp)) {
    Icon(
      imageVector = Icons.Default.Edit,
      contentDescription = "ویرایش",
      tint = AccentCyan,
      modifier = Modifier.size(18.dp),
    )
  }
}

@Composable
fun ManagementDeleteButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  IconButton(onClick = onClick, modifier = modifier.size(32.dp)) {
    Icon(
      imageVector = Icons.Default.Delete,
      contentDescription = "حذف",
      tint = StatusDanger,
      modifier = Modifier.size(18.dp),
    )
  }
}

// ---------- Text buttons (headers) ----------

@Composable
fun ManagementAddButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  containerColor: Color = AccentIndigo,
  contentColor: Color = Color.White,
) {
  Button(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .height(44.dp),
    shape = RoundedCornerShape(10.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = containerColor,
      contentColor = contentColor,
    ),
  ) {
    Icon(
      imageVector = Icons.Default.Add,
      contentDescription = null,
      tint = contentColor,
      modifier = Modifier.size(18.dp),
    )
    Spacer(Modifier.width(6.dp))
    Text(
      text = text,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = contentColor,
    )
  }
}

@Composable
fun ManagementSaveButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  text: String = "ذخیره",
  enabled: Boolean = true,
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
  ) {
    Text(text, fontWeight = FontWeight.Bold)
  }
}

@Composable
fun ManagementCancelButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  text: String = "انصراف",
) {
  Button(
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger),
  ) {
    Text(text, fontWeight = FontWeight.Bold)
  }
}
