package com.softcom.collector.presentation

import androidx.lifecycle.ViewModel
import com.softcom.collector.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppRoute { LOGIN, HOME, PRODUCTS, SETTINGS, COLLECTION_DETAIL }

data class AppUiState(
    val route: AppRoute = AppRoute.LOGIN,
    val initialSyncRequired: Boolean = false,
    val selectedCollectionId: Long? = null,
)

class AppViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        authRepository.restoreLastLoginData()
    }

    fun onLoginSuccess() {
        _uiState.value = AppUiState(route = AppRoute.HOME, initialSyncRequired = true)
    }

    fun onInitialSyncCompleted() {
        _uiState.value = _uiState.value.copy(initialSyncRequired = false)
    }

    fun openProducts() {
        _uiState.value = _uiState.value.copy(route = AppRoute.PRODUCTS, selectedCollectionId = null)
    }

    fun openSettings() {
        _uiState.value = _uiState.value.copy(route = AppRoute.SETTINGS, selectedCollectionId = null)
    }

    fun openHome() {
        _uiState.value = _uiState.value.copy(route = AppRoute.HOME, selectedCollectionId = null)
    }

    fun openCollectionDetail(collectionId: Long) {
        _uiState.value = _uiState.value.copy(
            route = AppRoute.COLLECTION_DETAIL,
            selectedCollectionId = collectionId,
        )
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AppUiState()
    }
}
