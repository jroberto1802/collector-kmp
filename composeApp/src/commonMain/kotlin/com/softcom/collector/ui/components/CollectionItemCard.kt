package com.softcom.collector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softcom.collector.core.PriceType
import com.softcom.collector.core.SearchType
import com.softcom.collector.core.formatCollectionItemQuantity
import com.softcom.collector.core.formatCurrencyBrl
import com.softcom.collector.core.formatIsoToBrDate
import com.softcom.collector.model.CollectionItem
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText

@Composable
fun CollectionItemCard(
    item: CollectionItem,
    onOpenOptions: (CollectionItem) -> Unit,
    isGrade: Boolean = false,
    searchType: SearchType = SearchType.BARCODE,
    priceType: PriceType = PriceType.PURCHASE,
    modifier: Modifier = Modifier,
) {
    val manufacturingLabel = formatIsoToBrDate(item.manufacturingDate)
    val expirationLabel = formatIsoToBrDate(item.expirationDate)
    val hasLotDates = manufacturingLabel.isNotBlank() || expirationLabel.isNotBlank()
    val price = if (priceType == PriceType.SALE) item.salePrice else item.purchasePrice
    val codeMeta = if (searchType == SearchType.REFERENCE) {
        "Referência: ${item.reference.ifBlank { "-" }}"
    } else {
        "Cód: ${item.barcode.ifBlank { "-" }}"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE6E6E6), RoundedCornerShape(10.dp))
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(start = 14.dp, end = 8.dp, top = 12.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(
                text = "Qtde: ${formatCollectionItemQuantity(item.quantity)}   $codeMeta   Preço: ${formatCurrencyBrl(price)}",
                color = Color(0xFFA0A0A0),
                fontSize = 12.sp,
            )
            Text(
                text = item.name,
                color = CollectorText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp),
            )
            if (hasLotDates) {
                Text(
                    text = buildString {
                        if (expirationLabel.isNotBlank()) append("Validade: $expirationLabel")
                        if (expirationLabel.isNotBlank() && manufacturingLabel.isNotBlank()) append("   ")
                        if (manufacturingLabel.isNotBlank()) append("Fabricação: $manufacturingLabel")
                    },
                    color = Color(0xFFA0A0A0),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            if (isGrade) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFFA0A0A0))) { append("Tamanho: ") }
                        withStyle(SpanStyle(color = CollectorText)) {
                            append(item.size.ifBlank { "-" })
                        }
                        append("   ")
                        withStyle(SpanStyle(color = Color(0xFFA0A0A0))) { append("Cor: ") }
                        withStyle(SpanStyle(color = CollectorText)) {
                            append(item.color.ifBlank { "-" })
                        }
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        IconButton(
            onClick = { onOpenOptions(item) },
            modifier = Modifier.size(28.dp),
        ) {
            Icon(
                Icons.Outlined.MoreVert,
                contentDescription = "Opções do item",
                tint = CollectorOrange,
            )
        }
    }
}
