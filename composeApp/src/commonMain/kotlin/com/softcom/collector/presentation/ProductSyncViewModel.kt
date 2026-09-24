package com.softcom.collector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softcom.collector.data.repository.ProductRepository
import com.softcom.collector.model.ProductSyncProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SyncStatus { IDLE, SYNCING, SUCCESS, ERROR }

data class ProductSyncUiState(
    val status: SyncStatus = SyncStatus.IDLE,
    val progress: ProductSyncProgress? = null,
    val productCount: Int = 0,
    val errorMessage: String = "",
)

class ProductSyncViewModel(private val productRepository: ProductRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductSyncUiState())
    val uiState: StateFlow<ProductSyncUiState> = _uiState.asStateFlow()

    fun synchronize() {
        if (_uiState.value.status == SyncStatus.SYNCING) return
        viewModelScope.launch {
            _uiState.value = ProductSyncUiState(status = SyncStatus.SYNCING)
            runCatching {
                productRepository.synchronize { progress ->
                    _uiState.update { it.copy(progress = progress) }
                }
            }.onSuccess { count ->
                _uiState.update { it.copy(status = SyncStatus.SUCCESS, productCount = count) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        status = SyncStatus.ERROR,
                        errorMessage = error.message ?: "Não foi possível sincronizar os produtos.",
                    )
                }
            }
        }
    }

    fun reset() {
        _uiState.value = ProductSyncUiState()
    }
}
