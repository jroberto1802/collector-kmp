package com.softcom.collector.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.softcom.collector.core.PriceType
import com.softcom.collector.core.SearchType
import com.softcom.collector.core.formatCurrencyBrl
import com.softcom.collector.model.Product
import com.softcom.collector.resources.Res
import com.softcom.collector.resources.empty_state_product_search
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductSearchBottomSheet(
    query: String,
    results: List<Product>,
    isSearching: Boolean,
    onQueryChange: (String) -> Unit,
    onSelect: (Product) -> Unit,
    onDismiss: () -> Unit,
    searchType: SearchType = SearchType.BARCODE,
    priceType: PriceType = PriceType.PURCHASE,
) {
    val isReferenceSearch = searchType == SearchType.REFERENCE

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.18f)
                    .clickable(onClick = onDismiss),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.82f)
                    .background(Color.White, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp, bottom = 12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(42.dp)
                        .height(4.dp)
                        .background(Color(0xFFD0D0D0), RoundedCornerShape(2.dp)),
                )
                Text(
                    "Pesquise o produto desejado",
                    color = CollectorText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 14.dp, bottom = 14.dp),
                )
                CollectorCompactTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = "Digite o nome ou código de barras",
                    height = 46.dp,
                    background = Color.Transparent,
                    borderColor = CollectorOrange,
                    borderWidth = 1.5.dp,
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = null,
                            tint = Color(0xFF4C8BF5),
                            modifier = Modifier.size(18.dp),
                        )
                    },
                )
                HorizontalDivider(color = Color(0xFFE8E8E8), modifier = Modifier.padding(top = 14.dp))

                when {
                    query.trim().isEmpty() -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp, vertical = 28.dp),
                        ) {
                            Box(modifier = Modifier.background(Color.White)) {
                                Image(
                                    painter = painterResource(Res.drawable.empty_state_product_search),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(width = 220.dp, height = 180.dp).background(Color.White),
                                )
                            }
                            Text(
                                "Pesquise o produto desejado.",
                                color = CollectorText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp),
                            )
                            Text(
                                "Após localizar, selecione para lançar na sua coleta.",
                                color = Color(0xFF9A9A9A),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }
                    isSearching -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = CollectorOrange)
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(top = 12.dp),
                        ) {
                            if (results.isEmpty()) {
                                item {
                                    Text(
                                        "Nenhum produto encontrado.",
                                        color = Color(0xFF9A9A9A),
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                                    )
                                }
                            } else {
                                items(results, key = { it.id }) { product ->
                                    val price = if (priceType == PriceType.SALE) {
                                        product.salePrice
                                    } else {
                                        product.purchasePrice
                                    }
                                    val meta = if (isReferenceSearch) {
                                        "Referência: ${product.reference.ifBlank { "-" }}   Preço: ${formatCurrencyBrl(price)}"
                                    } else {
                                        "Cód.: ${product.barcode.ifBlank { "-" }}   Preço: ${formatCurrencyBrl(price)}"
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp)
                                            .border(1.dp, Color(0xFFE6E6E6), RoundedCornerShape(10.dp))
                                            .background(Color.White, RoundedCornerShape(10.dp))
                                            .clickable { onSelect(product) }
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                    ) {
                                        Text(
                                            product.name,
                                            color = CollectorText,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Text(
                                            meta,
                                            color = Color(0xFF9A9A9A),
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 6.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
