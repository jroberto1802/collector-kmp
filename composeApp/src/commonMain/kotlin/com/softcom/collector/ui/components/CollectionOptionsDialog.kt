package com.softcom.collector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.softcom.collector.model.Collection
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText

@Composable
fun CollectionOptionsDialog(
    collection: Collection,
    onArchive: () -> Unit,
    onUnarchive: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x47000000))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 48.dp)
                    .widthIn(max = 230.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .clickable(enabled = false, onClick = {})
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                OptionItem(Icons.Outlined.Edit, "Editar coleta", onDismiss)
                OptionItem(
                    icon = if (collection.archived) Icons.Outlined.Unarchive else Icons.Outlined.Archive,
                    label = if (collection.archived) "Desarquivar coleta" else "Arquivar coleta",
                    onClick = if (collection.archived) onUnarchive else onArchive,
                )
                OptionItem(Icons.Outlined.ContentCopy, "Clonar coleta", onDismiss)
                OptionItem(Icons.Outlined.DeleteOutline, "Deletar Coleta", onDelete)
            }
        }
    }
}

@Composable
private fun OptionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clickable(onClick = onClick),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .background(CollectorOrange, CircleShape),
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(14.dp))
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
