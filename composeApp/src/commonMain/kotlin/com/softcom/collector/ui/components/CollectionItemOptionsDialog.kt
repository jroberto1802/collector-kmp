package com.softcom.collector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText

@Composable
fun CollectionItemOptionsDialog(
    onEdit: () -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.28f))
                .clickable(onClick = onDismiss)
                .padding(horizontal = 48.dp),
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 210.dp)
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .clickable(enabled = false) {}
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                OptionRow(icon = Icons.Outlined.Edit, label = "Editar item", onClick = onEdit)
                HorizontalDivider(color = Color(0xFFE8E8E8), modifier = Modifier.padding(vertical = 2.dp))
                OptionRow(icon = Icons.Outlined.DeleteOutline, label = "Remover item", onClick = onRemove)
            }
        }
    }
}

@Composable
fun CollectionActionsDialog(
    onSave: () -> Unit,
    onSync: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.28f))
                .clickable(onClick = onDismiss)
                .padding(start = 18.dp, end = 18.dp, bottom = 96.dp),
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 210.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .clickable(enabled = false) {}
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                OptionRow(
                    icon = Icons.Outlined.Save,
                    label = "Salvar Coleta",
                    onClick = {
                        onDismiss()
                        onSave()
                    },
                )
                HorizontalDivider(color = Color(0xFFE8E8E8), modifier = Modifier.padding(vertical = 2.dp))
                OptionRow(
                    icon = Icons.Outlined.Sync,
                    label = "Sincronizar Coleta",
                    onClick = onSync,
                )
            }
        }
    }
}

@Composable
private fun OptionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .background(CollectorOrange, CircleShape),
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
        Text(
            label,
            color = CollectorText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}
