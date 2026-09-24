package com.softcom.collector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.DialogProperties
import com.softcom.collector.core.formatCurrencyBrl
import com.softcom.collector.model.CollectionItem
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText

@Composable
fun EditCollectionItemDialog(
    item: CollectionItem,
    quantity: String,
    lot: String,
    manufacturingDate: String,
    expirationDate: String,
    errorMessage: String,
    isSubmitting: Boolean,
    isGrade: Boolean = false,
    isLotSerial: Boolean = false,
    onQuantityChange: (String) -> Unit,
    onLotChange: (String) -> Unit,
    onManufacturingDateChange: (String) -> Unit,
    onExpirationDateChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
) {
    Dialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(enabled = !isSubmitting, onClick = onDismiss)
                .padding(horizontal = 18.dp),
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 390.dp)
                    .fillMaxWidth()
                    .heightIn(max = 640.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .clickable(enabled = false) {}
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Editar item", color = CollectorText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSubmitting,
                        modifier = Modifier.align(Alignment.CenterEnd).size(28.dp),
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = "Fechar", tint = Color(0xFF8A8A8A))
                    }
                }
                HorizontalDivider(color = Color(0xFFE8E8E8), modifier = Modifier.padding(vertical = 14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                ) {
                    FieldLabel("Descrição")
                    Text(item.name, color = CollectorText, fontSize = 14.sp)

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            FieldLabel("Código de Barras")
                            Text(item.barcode.ifBlank { "-" }, color = CollectorText, fontSize = 14.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            FieldLabel("Referência")
                            Text(item.reference.ifBlank { "-" }, color = CollectorText, fontSize = 14.sp)
                        }
                    }

                    if (isGrade) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                FieldLabel("Tamanho")
                                Text(item.size.ifBlank { "-" }, color = CollectorText, fontSize = 14.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                FieldLabel("Cor")
                                Text(item.color.ifBlank { "-" }, color = CollectorText, fontSize = 14.sp)
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            FieldLabel("Preço de Compra")
                            Text(formatCurrencyBrl(item.purchasePrice), color = CollectorText, fontSize = 14.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            FieldLabel("Preço de Venda")
                            Text(formatCurrencyBrl(item.salePrice), color = CollectorText, fontSize = 14.sp)
                        }
                    }

                    FieldLabel("Quantidade")
                    CollectorCompactTextField(
                        value = quantity,
                        onValueChange = onQuantityChange,
                        enabled = !isSubmitting,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        height = 46.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                    )

                    if (isLotSerial) {
                        FieldLabel("Lote/Serial")
                        CollectorCompactTextField(
                            value = lot,
                            onValueChange = onLotChange,
                            enabled = !isSubmitting,
                            placeholder = "Lote/Serial",
                            height = 46.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                FieldLabel("Fabricação")
                                CollectorCompactTextField(
                                    value = manufacturingDate,
                                    onValueChange = onManufacturingDateChange,
                                    enabled = !isSubmitting,
                                    placeholder = "DD/MM/AAAA",
                                    height = 46.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp),
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                FieldLabel("Validade")
                                CollectorCompactTextField(
                                    value = expirationDate,
                                    onValueChange = onExpirationDateChange,
                                    enabled = !isSubmitting,
                                    placeholder = "DD/MM/AAAA",
                                    height = 46.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp),
                                )
                            }
                        }
                    }

                    if (errorMessage.isNotBlank()) {
                        Text(
                            errorMessage,
                            color = Color(0xFFB3261E),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                        .height(46.dp)
                        .background(
                            CollectorOrange.copy(alpha = if (isSubmitting) 0.7f else 1f),
                            RoundedCornerShape(8.dp),
                        )
                        .clickable(enabled = !isSubmitting, onClick = onSubmit),
                ) {
                    Text(
                        if (isSubmitting) "Salvando..." else "Salvar",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        color = CollectorText,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
    )
}
