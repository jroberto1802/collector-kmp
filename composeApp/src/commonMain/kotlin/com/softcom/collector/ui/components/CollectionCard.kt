package com.softcom.collector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softcom.collector.core.formatCollectionHeader
import com.softcom.collector.core.formatCollectionItemCount
import com.softcom.collector.model.Collection
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CollectionCard(
    collection: Collection,
    onOpen: (Collection) -> Unit,
    onOpenOptions: (Collection) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE6E6E6), RoundedCornerShape(10.dp))
            .background(Color.White, RoundedCornerShape(10.dp))
            .clickable { onOpen(collection) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = formatCollectionHeader(collection),
                    color = Color(0xFFA0A0A0),
                    fontSize = 12.sp,
                )
                Text(
                    text = formatCollectionItemCount(collection.itemCount),
                    color = Color(0xFFA0A0A0),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
                Text(
                    text = collection.name,
                    color = CollectorText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            IconButton(
                onClick = { onOpenOptions(collection) },
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    Icons.Outlined.MoreVert,
                    contentDescription = "Opções da coleta",
                    tint = CollectorOrange,
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 6.dp),
        ) {
            StatusBadge(text = collection.status, background = Color(0xFF5B5B5B))
            if (collection.archived) {
                StatusBadge(text = "Arquivada", background = CollectorOrange)
            }
        }
    }
}

@Composable
private fun StatusBadge(text: String, background: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(background, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
