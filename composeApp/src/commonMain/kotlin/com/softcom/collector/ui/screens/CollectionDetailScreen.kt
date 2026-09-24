package com.softcom.collector.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softcom.collector.presentation.CollectionDetailUiState
import com.softcom.collector.presentation.CollectionDetailViewModel
import com.softcom.collector.resources.Res
import com.softcom.collector.resources.empty_state_itens_coleta
import com.softcom.collector.ui.components.BarcodeScannerDialog
import com.softcom.collector.ui.components.CollectionActionsDialog
import com.softcom.collector.ui.components.CollectionItemCard
import com.softcom.collector.ui.components.CollectionItemOptionsDialog
import com.softcom.collector.ui.components.CollectorCompactTextField
import com.softcom.collector.ui.components.EditCollectionItemDialog
import com.softcom.collector.ui.components.ProductSearchBottomSheet
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorSurface
import com.softcom.collector.ui.theme.CollectorText
import org.jetbrains.compose.resources.painterResource

@Composable
fun CollectionDetailScreen(
    viewModel: CollectionDetailViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val hasToast = state.toastMessage.isNotBlank()
    val fabBottom = if (hasToast) 72.dp else 28.dp
    val listBottomPadding = fabBottom + 72.dp

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 13.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(42.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(42.dp)
                        .background(Color(0xFFF4F4F4), CircleShape)
                        .clickable(onClick = onBack),
                ) {
                    Icon(
                        Icons.Outlined.ArrowBackIosNew,
                        contentDescription = "Voltar",
                        tint = Color(0xFF555555),
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(
                    text = state.collection?.name ?: "Coleta",
                    color = CollectorText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 54.dp).fillMaxWidth(),
                )
            }

            if (state.isQuickCollection) {
                QuantityRow(
                    state = state,
                    viewModel = viewModel,
                    topPadding = 18.dp,
                )
                SearchRow(
                    state = state,
                    viewModel = viewModel,
                    topPadding = 12.dp,
                )
            } else {
                SearchRow(
                    state = state,
                    viewModel = viewModel,
                    topPadding = 18.dp,
                )
                state.selectedProduct?.let { product ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .background(Color(0xFFFFF0E7), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                    ) {
                        Text(
                            product.name,
                            color = CollectorText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if (state.isLotSerial) {
                    CollectorCompactTextField(
                        value = state.lotText,
                        onValueChange = viewModel::setLotText,
                        placeholder = "Lote/Serial",
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    ) {
                        CollectorCompactTextField(
                            value = state.manufacturingDateText,
                            onValueChange = viewModel::setManufacturingDateText,
                            placeholder = "Fabricação",
                            modifier = Modifier.weight(1f),
                        )
                        CollectorCompactTextField(
                            value = state.expirationDateText,
                            onValueChange = viewModel::setExpirationDateText,
                            placeholder = "Validade",
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                QuantityRow(
                    state = state,
                    viewModel = viewModel,
                    topPadding = if (state.isLotSerial) 12.dp else 12.dp,
                )
            }

            if (state.feedback.isNotBlank()) {
                Text(
                    state.feedback,
                    color = Color(0xFFB3261E),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }

            HorizontalDivider(color = Color(0xFFE6E6E6), modifier = Modifier.padding(top = 16.dp))

            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CollectorOrange)
                    }
                }
                state.loadError.isNotBlank() -> {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            state.loadError,
                            color = CollectorText,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 14.dp),
                        contentPadding = PaddingValues(bottom = listBottomPadding),
                    ) {
                        if (state.items.isEmpty()) {
                            item {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 28.dp),
                                ) {
                                    Box(modifier = Modifier.background(Color.White)) {
                                        Image(
                                            painter = painterResource(Res.drawable.empty_state_itens_coleta),
                                            contentDescription = null,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .size(width = 240.dp, height = 220.dp)
                                                .background(Color.White),
                                        )
                                    }
                                    Text(
                                        "Nenhum produto adicionado",
                                        color = CollectorText,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 14.dp),
                                    )
                                    Text(
                                        emptyDescription(state),
                                        color = Color(0xFF999999),
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 12.dp),
                                    )
                                }
                            }
                        } else {
                            items(state.items, key = { it.id }) { item ->
                                CollectionItemCard(
                                    item = item,
                                    onOpenOptions = viewModel::openItemMenu,
                                    isGrade = state.isGrade,
                                    searchType = state.searchType,
                                    priceType = state.priceType,
                                )
                            }
                        }
                    }
                }
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = fabBottom)
                .size(56.dp)
                .background(CollectorOrange, CircleShape)
                .clickable(onClick = viewModel::openActions),
        ) {
            Icon(
                Icons.Outlined.Menu,
                contentDescription = "Ações",
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }

        if (hasToast) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(CollectorOrange)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
            ) {
                Text(
                    state.toastMessage,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }

    if (state.isScannerVisible) {
        BarcodeScannerDialog(
            onScanned = viewModel::applyBarcode,
            onDismiss = viewModel::closeScanner,
        )
    }
    if (state.isProductSearchVisible) {
        ProductSearchBottomSheet(
            query = state.searchQuery,
            results = state.searchResults,
            isSearching = state.isSearching,
            searchType = state.searchType,
            priceType = state.priceType,
            onQueryChange = viewModel::setSearchQuery,
            onSelect = viewModel::selectProductFromSearch,
            onDismiss = viewModel::closeProductSearch,
        )
    }
    if (state.isActionsVisible) {
        CollectionActionsDialog(
            onSave = onBack,
            onSync = viewModel::closeActions,
            onDismiss = viewModel::closeActions,
        )
    }
    state.menuItem?.let {
        CollectionItemOptionsDialog(
            onEdit = viewModel::requestEditItem,
            onRemove = viewModel::removeSelectedItem,
            onDismiss = viewModel::closeItemMenu,
        )
    }
    state.editingItem?.let { item ->
        EditCollectionItemDialog(
            item = item,
            quantity = state.editQuantityText,
            lot = state.editLotText,
            manufacturingDate = state.editManufacturingDateText,
            expirationDate = state.editExpirationDateText,
            errorMessage = state.editError,
            isSubmitting = state.isSavingEdit,
            isGrade = state.isGrade,
            isLotSerial = state.isLotSerial,
            onQuantityChange = viewModel::setEditQuantityText,
            onLotChange = viewModel::setEditLotText,
            onManufacturingDateChange = viewModel::setEditManufacturingDateText,
            onExpirationDateChange = viewModel::setEditExpirationDateText,
            onDismiss = viewModel::closeEditItem,
            onSubmit = viewModel::saveEditedItem,
        )
    }
}

@Composable
private fun SearchRow(
    state: CollectionDetailUiState,
    viewModel: CollectionDetailViewModel,
    topPadding: androidx.compose.ui.unit.Dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(top = topPadding),
    ) {
        CollectorCompactTextField(
            value = state.productQuery,
            onValueChange = viewModel::setProductQuery,
            placeholder = "Produto",
            modifier = Modifier.weight(1f),
        )
        OrangeIconButton(onClick = viewModel::openScanner) {
            Icon(
                Icons.Outlined.QrCodeScanner,
                contentDescription = "Scanner",
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
        OrangeIconButton(onClick = viewModel::openProductSearch) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = "Busca",
                tint = Color.White,
                modifier = Modifier.size(21.dp),
            )
        }
    }
}

@Composable
private fun QuantityRow(
    state: CollectionDetailUiState,
    viewModel: CollectionDetailViewModel,
    topPadding: androidx.compose.ui.unit.Dp,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(top = topPadding),
    ) {
        StepButton(label = "-", onClick = viewModel::decreaseQuantity)
        Box(
            modifier = Modifier
                .width(78.dp)
                .height(46.dp)
                .background(CollectorSurface, RoundedCornerShape(8.dp)),
        ) {
            Text(
                "Qtde",
                color = Color(0xFF9A9A9A),
                fontSize = 9.sp,
                modifier = Modifier.padding(start = 10.dp, top = 4.dp),
            )
            CollectorCompactTextField(
                value = state.quantityText,
                onValueChange = viewModel::setQuantityText,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                height = 46.dp,
                horizontalPadding = 8.dp,
                background = Color.Transparent,
                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            )
        }
        StepButton(label = "+", onClick = viewModel::increaseQuantity)
        if (!state.isQuickCollection) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .background(
                        CollectorOrange.copy(alpha = if (state.isAdding) 0.7f else 1f),
                        RoundedCornerShape(8.dp),
                    )
                    .clickable(enabled = !state.isAdding, onClick = viewModel::addItem),
            ) {
                Text(
                    if (state.isAdding) "Adicionando..." else "Adicionar",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun emptyDescription(state: CollectionDetailUiState): String = when {
    state.isQuickCollection ->
        "Informe a quantidade e selecione o produto para lançar automaticamente!"
    state.isLotSerial ->
        "Informe produto, lote/serial, datas e quantidade para começar!"
    else ->
        "Informe a quantidade e digite o código de barras do produto para começar!"
}

@Composable
private fun OrangeIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(46.dp)
            .background(CollectorOrange, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        content = { content() },
    )
}

@Composable
private fun StepButton(label: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(42.dp)
            .height(46.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
    ) {
        Text(label, color = CollectorText, fontSize = 22.sp, fontWeight = FontWeight.Medium)
    }
}
