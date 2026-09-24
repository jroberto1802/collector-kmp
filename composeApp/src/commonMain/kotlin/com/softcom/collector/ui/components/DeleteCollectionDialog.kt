package com.softcom.collector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.softcom.collector.model.Collection
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText

@Composable
fun DeleteCollectionDialog(
    collection: Collection,
    isSubmitting: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = { if (!isSubmitting) onCancel() }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(7.dp))
                .padding(horizontal = 16.dp, vertical = 18.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onCancel,
                    enabled = !isSubmitting,
                    modifier = Modifier.align(Alignment.TopEnd),
                ) {
                    Icon(Icons.Outlined.Close, "Fechar", tint = Color(0xFF7E7E7E))
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .background(Color(0xFFFFF3EB), CircleShape),
            ) {
                Text("!", color = Color(0xFFFFDCC7), fontSize = 58.sp, fontWeight = FontWeight.Light)
            }
            Text(
                "Deseja deletar esta coleta?",
                color = CollectorText,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 18.dp),
            )
            Text(
                "A coleta \"${collection.name}\" será removida permanentemente.",
                color = Color(0xFFA0A0A0),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 14.dp),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 21.dp),
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(7.dp),
                    modifier = Modifier.weight(1f).height(43.dp),
                ) { Text("Não", color = CollectorOrange) }
                Button(
                    onClick = onConfirm,
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = CollectorOrange),
                    shape = RoundedCornerShape(7.dp),
                    modifier = Modifier.weight(1f).height(43.dp),
                ) {
                    Text(if (isSubmitting) "Excluindo..." else "Sim")
                }
            }
        }
    }
}
