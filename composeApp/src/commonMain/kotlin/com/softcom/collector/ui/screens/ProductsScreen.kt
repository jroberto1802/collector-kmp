package com.softcom.collector.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softcom.collector.data.repository.SettingsRepository
import com.softcom.collector.presentation.ProductSyncViewModel
import com.softcom.collector.presentation.ProductsViewModel
import com.softcom.collector.ui.components.CollectorCompactTextField
import com.softcom.collector.ui.components.ProductSyncDialog
import com.softcom.collector.ui.components.SoftcomLogo
import com.softcom.collector.ui.components.SyncConfirmationDialog
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText

@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel,
    syncViewModel: ProductSyncViewModel,
    settingsRepository: SettingsRepository,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showConfirmation by remember { mutableStateOf(false) }
    var showSync by remember { mutableStateOf(false) }

    // Garante que a flag de grade reflita as configurações atuais ao abrir a tela.
    LaunchedEffect(settingsRepository) {
        viewModel.refresh()
    }

    Box(Modifier.fillMaxSize().background(Color.White)) {
        Column(Modifier.fillMaxSize().padding(horizontal = 5.dp, vertical = 13.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(42.dp).background(Color(0xFFF4F4F4), CircleShape),
                ) {
                    Icon(
                        Icons.Outlined.ArrowBackIosNew,
                        "Voltar",
                        tint = Color(0xFF555555),
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text("Produtos", color = CollectorText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(Modifier.size(42.dp))
            }

            CollectorCompactTextField(
                value = state.search,
                onValueChange = viewModel::setSearch,
                placeholder = "Pesquise por nome ou código",
                fontSize = 13.sp,
                height = 48.dp,
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 29.dp),
            )
            Divider(Modifier.padding(top = 11.dp), color = Color(0xFFE5E5E5))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 22.dp),
            ) {
                Text("Código", color = Color(0xFF8B8B95), fontSize = 13.sp, modifier = Modifier.width(78.dp))
                Text("Produto", color = Color(0xFF8B8B95), fontSize = 13.sp)
            }

            Box(Modifier.weight(1f).fillMaxWidth()) {
                when {
                    state.isLoading -> CircularProgressIndicator(
                        color = CollectorOrange,
                        modifier = Modifier.align(Alignment.Center),
                    )
                    state.products.isEmpty() -> Text(
                        "Nenhum produto encontrado.",
                        color = Color(0xFF999999),
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 50.dp),
                    )
                    else -> LazyColumn(Modifier.fillMaxSize()) {
                        items(state.products, key = { it.id }) { product ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 74.dp)
                                    .padding(horizontal = 22.dp, vertical = 20.dp),
                            ) {
                                Text(
                                    product.code,
                                    color = Color(0xFF505050),
                                    fontSize = 13.sp,
                                    modifier = Modifier.width(78.dp),
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        product.name,
                                        color = Color(0xFF414141),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 18.sp,
                                    )
                                    if (state.isGrade) {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(SpanStyle(color = Color(0xFFA0A0A0))) {
                                                    append("Tamanho: ")
                                                }
                                                withStyle(SpanStyle(color = Color(0xFF414141))) {
                                                    append(product.size.ifBlank { "-" })
                                                }
                                                append("   ")
                                                withStyle(SpanStyle(color = Color(0xFFA0A0A0))) {
                                                    append("Cor: ")
                                                }
                                                withStyle(SpanStyle(color = Color(0xFF414141))) {
                                                    append(product.color.ifBlank { "-" })
                                                }
                                            },
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 6.dp),
                                        )
                                    }
                                }
                            }
                            Divider(color = Color(0xFFE2E2E2))
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().height(45.dp),
            ) {
                Text("Linhas:", color = Color(0xFF8B8B8B), fontSize = 10.sp)
                Text(
                    state.pageSize.toString(),
                    color = Color(0xFF777777),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 13.dp, end = 24.dp),
                )
                Text(
                    "${state.from} – ${state.to} de ${state.total}",
                    color = Color(0xFF777777),
                    fontSize = 10.sp,
                )
                IconButton(
                    onClick = viewModel::previousPage,
                    enabled = state.page > 1,
                    modifier = Modifier.padding(start = 12.dp),
                ) {
                    Icon(
                        Icons.Outlined.ChevronLeft,
                        "Página anterior",
                        tint = if (state.page > 1) Color(0xFF808080) else Color(0xFFC7C7C7),
                    )
                }
                IconButton(
                    onClick = viewModel::nextPage,
                    enabled = state.page < state.totalPages,
                ) {
                    Icon(
                        Icons.Outlined.ChevronRight,
                        "Próxima página",
                        tint = if (state.page < state.totalPages) Color(0xFF808080) else Color(0xFFC7C7C7),
                    )
                }
            }
            Box(Modifier.fillMaxWidth().height(70.dp), contentAlignment = Alignment.Center) {
                SoftcomLogo()
            }
        }

        FloatingActionButton(
            onClick = { showConfirmation = true },
            containerColor = CollectorOrange,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 18.dp)
                .size(54.dp),
        ) {
            Icon(Icons.Outlined.Sync, "Sincronizar", modifier = Modifier.size(27.dp))
        }
    }

    if (showConfirmation) {
        SyncConfirmationDialog(
            onCancel = { showConfirmation = false },
            onConfirm = {
                showConfirmation = false
                showSync = true
                syncViewModel.reset()
                syncViewModel.synchronize()
            },
        )
    }
    if (showSync) {
        ProductSyncDialog(syncViewModel) {
            showSync = false
            syncViewModel.reset()
            viewModel.refresh()
        }
    }
}
