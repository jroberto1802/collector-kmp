package com.softcom.collector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText

@Composable
fun NewCollectionDialog(
    name: String,
    errorMessage: String,
    isSubmitting: Boolean,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
) {
    Dialog(onDismissRequest = { if (!isSubmitting) onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().height(28.dp),
            ) {
                Text(
                    "Nova Coleta",
                    color = CollectorText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(
                    onClick = onDismiss,
                    enabled = !isSubmitting,
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    Icon(Icons.Outlined.Close, "Fechar", tint = Color(0xFF8A8A8A))
                }
            }

            HorizontalDivider(color = Color(0xFFE8E8E8), modifier = Modifier.padding(top = 14.dp))

            CollectorCompactTextField(
                value = name,
                onValueChange = onNameChange,
                enabled = !isSubmitting,
                placeholder = "Nome da Coleta",
                height = 48.dp,
                background = Color.White,
                borderColor = Color(0xFFD8D8D8),
                borderWidth = 1.dp,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            )

            if (errorMessage.isNotBlank()) {
                Text(
                    errorMessage,
                    color = Color(0xFFB3261E),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Button(
                onClick = onSubmit,
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = CollectorOrange),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(46.dp),
            ) {
                Text(
                    if (isSubmitting) "Criando..." else "Criar Coleta",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
