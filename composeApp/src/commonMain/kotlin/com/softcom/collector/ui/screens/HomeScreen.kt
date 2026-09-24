package com.softcom.collector.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softcom.collector.core.AppConstants
import com.softcom.collector.core.formatCnpj
import com.softcom.collector.model.CollectionFilter
import com.softcom.collector.presentation.CollectionsViewModel
import com.softcom.collector.presentation.ProductSyncViewModel
import com.softcom.collector.presentation.SyncStatus
import com.softcom.collector.resources.Res
import com.softcom.collector.resources.empty_state_coleta
import com.softcom.collector.ui.components.CollectionCard
import com.softcom.collector.ui.components.CollectionOptionsDialog
import com.softcom.collector.ui.components.CollectorCompactTextField
import com.softcom.collector.ui.components.CollectorLogo
import com.softcom.collector.ui.components.DeleteCollectionDialog
import com.softcom.collector.ui.components.NewCollectionDialog
import com.softcom.collector.ui.components.ProductSyncDialog
import com.softcom.collector.ui.components.SoftcomLogo
import com.softcom.collector.ui.components.SyncConfirmationDialog
import com.softcom.collector.ui.components.VersionFooter
import com.softcom.collector.ui.theme.CollectorOrange
import com.softcom.collector.ui.theme.CollectorText
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(
    initialSyncRequired: Boolean,
    syncViewModel: ProductSyncViewModel,
    collectionsViewModel: CollectionsViewModel,
    onInitialSyncCompleted: () -> Unit,
    onOpenProducts: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCollection: (Long) -> Unit,
    onLogout: () -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showConfirmation by remember { mutableStateOf(false) }
    var showSync by remember(initialSyncRequired) { mutableStateOf(initialSyncRequired) }
    val collectionsState by collectionsViewModel.uiState.collectAsStateWithLifecycle()

    fun finishSync() {
        if (!showSync) return
        showSync = false
        syncViewModel.reset()
        if (initialSyncRequired) onInitialSyncCompleted()
    }

    LaunchedEffect(showSync) {
        if (!showSync) return@LaunchedEffect
        if (syncViewModel.uiState.value.status == SyncStatus.SYNCING) return@LaunchedEffect
        syncViewModel.reset()
        syncViewModel.synchronize()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.fillMaxHeight().fillMaxWidth(0.94f),
            ) {
                MenuContent(
                    onClose = { scope.launch { drawerState.close() } },
                    onSynchronize = {
                        scope.launch { drawerState.close() }
                        showConfirmation = true
                    },
                    onOpenProducts = {
                        scope.launch { drawerState.close() }
                        onOpenProducts()
                    },
                    onOpenSettings = {
                        scope.launch { drawerState.close() }
                        onOpenSettings()
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        syncViewModel.reset()
                        onLogout()
                    },
                )
            }
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.White).padding(horizontal = 16.dp, vertical = 13.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                CircleAction(background = Color(0xFFF4F4F4), onClick = { scope.launch { drawerState.open() } }) {
                    Icon(Icons.Outlined.Menu, "Menu", tint = Color(0xFF444444), modifier = Modifier.size(22.dp))
                }
                CollectorLogo(Modifier.width(169.dp).height(36.dp))
                CircleAction(
                    background = CollectorOrange,
                    onClick = collectionsViewModel::openNewCollection,
                ) {
                    Icon(Icons.Outlined.Add, "Nova coleta", tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }

            Text(
                "Minhas Coletas",
                color = CollectorText,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 25.dp),
            )
            CollectorCompactTextField(
                value = collectionsState.search,
                onValueChange = collectionsViewModel::setSearch,
                placeholder = "Pesquise uma coleta",
                fontSize = 13.sp,
                height = 48.dp,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 11.dp)) {
                HomeTab(
                    label = "Recentes",
                    active = collectionsState.filter == CollectionFilter.RECENTES,
                    onClick = { collectionsViewModel.setFilter(CollectionFilter.RECENTES) },
                )
                HomeTab(
                    label = "Todas",
                    active = collectionsState.filter == CollectionFilter.TODAS,
                    onClick = { collectionsViewModel.setFilter(CollectionFilter.TODAS) },
                )
                HomeTab(
                    label = "Arquivadas",
                    active = collectionsState.filter == CollectionFilter.ARQUIVADAS,
                    onClick = { collectionsViewModel.setFilter(CollectionFilter.ARQUIVADAS) },
                )
            }
            Divider(color = Color(0xFFE5E5E5), modifier = Modifier.padding(top = 11.dp))

            when {
                collectionsState.isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CollectorOrange)
                    }
                }
                collectionsState.collections.isEmpty() -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 28.dp),
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.empty_state_coleta),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(220.dp),
                        )
                        Text(
                            collectionsState.emptyCopy.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp),
                        )
                        Text(
                            collectionsState.emptyCopy.description,
                            color = Color(0xFFA0A0A0),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 17.dp),
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 14.dp),
                    ) {
                        items(collectionsState.collections, key = { it.id }) { collection ->
                            CollectionCard(
                                collection = collection,
                                onOpen = { onOpenCollection(it.id) },
                                onOpenOptions = collectionsViewModel::openCollectionMenu,
                            )
                        }
                    }
                }
            }

            SoftcomLogo(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp))
        }
    }

    if (showConfirmation) {
        SyncConfirmationDialog(
            onCancel = { showConfirmation = false },
            onConfirm = {
                showConfirmation = false
                showSync = true
            },
        )
    }
    if (showSync) {
        ProductSyncDialog(
            viewModel = syncViewModel,
            onCompleted = ::finishSync,
        )
    }
    if (collectionsState.isNewCollectionVisible) {
        NewCollectionDialog(
            name = collectionsState.newCollectionName,
            errorMessage = collectionsState.createError,
            isSubmitting = collectionsState.isCreating,
            onNameChange = collectionsViewModel::setNewCollectionName,
            onDismiss = collectionsViewModel::closeNewCollection,
            onSubmit = collectionsViewModel::createCollection,
        )
    }
    collectionsState.menuCollection?.let { collection ->
        CollectionOptionsDialog(
            collection = collection,
            onArchive = collectionsViewModel::archiveSelectedCollection,
            onUnarchive = collectionsViewModel::unarchiveSelectedCollection,
            onDelete = collectionsViewModel::requestDeleteCollection,
            onDismiss = collectionsViewModel::closeCollectionMenu,
        )
    }
    collectionsState.collectionPendingDelete?.let { collection ->
        DeleteCollectionDialog(
            collection = collection,
            isSubmitting = collectionsState.isDeleting,
            onCancel = collectionsViewModel::cancelDeleteCollection,
            onConfirm = collectionsViewModel::confirmDeleteCollection,
        )
    }
}

@Composable
private fun MenuContent(
    onClose: () -> Unit,
    onSynchronize: () -> Unit,
    onOpenProducts: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 13.dp, vertical = 13.dp)) {
        CircleAction(background = Color(0xFFF4F4F4), onClick = onClose) {
            Icon(Icons.Outlined.ArrowBackIosNew, "Voltar", tint = Color(0xFF555555), modifier = Modifier.size(18.dp))
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 23.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 14.dp),
        ) {
            Text("Bem vindo ! 👋", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Empresa: ${AppConstants.merchantName}", color = Color(0xFF9A9A9A), fontSize = 16.sp, textAlign = TextAlign.Center)
            Text(formatCnpj(AppConstants.merchantCnpj), color = Color(0xFF9A9A9A), fontSize = 16.sp)
        }
        Divider(modifier = Modifier.padding(top = 14.dp))
        MenuItem(Icons.Outlined.Settings, "Configurações", "Habilitar ou Desabilitar recursos", onClick = onOpenSettings)
        MenuItem(Icons.Outlined.Sync, "Sincronizar", "Atualiza os dados de produtos cadastrados", onClick = onSynchronize)
        MenuItem(Icons.Outlined.Storefront, "Meus Produtos", "Visualizar os produtos sincronizados", onClick = onOpenProducts)
        MenuItem(Icons.Outlined.Logout, "Sair", "Realizar Logoff", onClick = onLogout)
        Spacer(Modifier.weight(1f))
        VersionFooter(showLogo = false, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun MenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(66.dp).clickable(onClick = onClick),
    ) {
        Icon(icon, null, tint = CollectorOrange, modifier = Modifier.size(22.dp))
        Column(modifier = Modifier.padding(start = 18.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(description, color = Color(0xFFAAAAAA), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
    Divider(color = Color(0xFFDADDE1))
}

@Composable
private fun HomeTab(label: String, active: Boolean = false, onClick: () -> Unit = {}) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(31.dp)
            .background(if (active) Color(0xFFFFEEE4) else Color(0xFFF4F4F5), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp),
    ) {
        Text(label, color = if (active) CollectorOrange else Color(0xFF999999), fontSize = 12.sp)
    }
}

@Composable
private fun CircleAction(background: Color, onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(42.dp).clip(CircleShape).background(background).clickable(onClick = onClick),
    ) { content() }
}
