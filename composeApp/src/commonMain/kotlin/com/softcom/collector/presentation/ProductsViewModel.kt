package com.softcom.collector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softcom.collector.data.repository.ProductRepository
import com.softcom.collector.data.repository.SettingsRepository
import com.softcom.collector.model.ProductListItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductsUiState(
    val products: List<ProductListItem> = emptyList(),
    val search: String = "",
    val page: Int = 1,
    val pageSize: Int = 10,
    val total: Int = 0,
    val totalPages: Int = 1,
    val isLoading: Boolean = true,
    val isGrade: Boolean = false,
) {
    val from: Int get() = if (total == 0) 0 else (page - 1) * pageSize + 1
    val to: Int get() = if (total == 0) 0 else (from + products.size - 1).coerceAtMost(total)
}

class ProductsViewModel(
    private val productRepository: ProductRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()
    private var loadingJob: Job? = null

    init {
        refresh()
    }

    fun setSearch(value: String) {
        _uiState.update { it.copy(search = value, page = 1) }
        refresh(debounce = true)
    }

    fun previousPage() {
        _uiState.update { it.copy(page = (it.page - 1).coerceAtLeast(1)) }
        refresh()
    }

    fun nextPage() {
        _uiState.update { it.copy(page = (it.page + 1).coerceAtMost(it.totalPages)) }
        refresh()
    }

    fun refresh(debounce: Boolean = false) {
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            if (debounce) delay(180)
            val isGrade = settingsRepository.load().grade
            _uiState.update { it.copy(isLoading = true, isGrade = isGrade) }
            val state = _uiState.value
            runCatching { productRepository.getLocalPage(state.page, state.pageSize, state.search) }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            products = result.items,
                            page = result.page,
                            total = result.total,
                            totalPages = result.totalPages,
                            isLoading = false,
                            isGrade = isGrade,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoading = false, products = emptyList(), isGrade = isGrade)
                    }
                }
        }
    }
}
