package com.softcom.collector

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softcom.collector.di.AppContainer
import com.softcom.collector.presentation.AppRoute
import com.softcom.collector.presentation.AppViewModel
import com.softcom.collector.presentation.CollectionDetailViewModel
import com.softcom.collector.presentation.CollectionsViewModel
import com.softcom.collector.presentation.LoginViewModel
import com.softcom.collector.presentation.ProductSyncViewModel
import com.softcom.collector.presentation.ProductsViewModel
import com.softcom.collector.presentation.SettingsViewModel
import com.softcom.collector.ui.screens.CollectionDetailScreen
import com.softcom.collector.ui.screens.HomeScreen
import com.softcom.collector.ui.screens.LoginScreen
import com.softcom.collector.ui.screens.ProductsScreen
import com.softcom.collector.ui.screens.SettingsScreen
import com.softcom.collector.ui.theme.CollectorTheme

@Composable
fun App() {
    val container = remember { AppContainer() }
    val appViewModel = remember { AppViewModel(container.authRepository) }
    val loginViewModel = remember { LoginViewModel(container.authRepository) }
    val syncViewModel = remember { ProductSyncViewModel(container.productRepository) }
    val productsViewModel = remember {
        ProductsViewModel(container.productRepository, container.settingsRepository)
    }
    val collectionsViewModel = remember { CollectionsViewModel(container.collectionRepository) }
    val state by appViewModel.uiState.collectAsStateWithLifecycle()

    CollectorTheme {
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            when (state.route) {
                AppRoute.LOGIN -> LoginScreen(loginViewModel, appViewModel::onLoginSuccess)
                AppRoute.HOME -> {
                    LaunchedEffect(state.route) {
                        collectionsViewModel.refresh()
                    }
                    HomeScreen(
                        initialSyncRequired = state.initialSyncRequired,
                        syncViewModel = syncViewModel,
                        collectionsViewModel = collectionsViewModel,
                        onInitialSyncCompleted = appViewModel::onInitialSyncCompleted,
                        onOpenProducts = appViewModel::openProducts,
                        onOpenSettings = appViewModel::openSettings,
                        onOpenCollection = appViewModel::openCollectionDetail,
                        onLogout = appViewModel::logout,
                    )
                }
                AppRoute.SETTINGS -> {
                    val settingsViewModel = remember {
                        SettingsViewModel(container.settingsRepository)
                    }
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onBack = appViewModel::openHome,
                    )
                }
                AppRoute.PRODUCTS -> ProductsScreen(
                    viewModel = productsViewModel,
                    syncViewModel = syncViewModel,
                    settingsRepository = container.settingsRepository,
                    onBack = appViewModel::openHome,
                )
                AppRoute.COLLECTION_DETAIL -> {
                    val collectionId = state.selectedCollectionId
                    if (collectionId == null) {
                        LaunchedEffect(Unit) { appViewModel.openHome() }
                    } else {
                        val detailViewModel = remember(collectionId) {
                            CollectionDetailViewModel(
                                collectionId = collectionId,
                                repository = container.collectionDetailRepository,
                                settingsRepository = container.settingsRepository,
                            )
                        }
                        CollectionDetailScreen(
                            viewModel = detailViewModel,
                            onBack = appViewModel::openHome,
                        )
                    }
                }
            }
        }
    }
}
