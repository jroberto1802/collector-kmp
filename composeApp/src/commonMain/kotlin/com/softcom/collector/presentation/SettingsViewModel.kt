package com.softcom.collector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softcom.collector.core.AppConstants
import com.softcom.collector.core.AppSettings
import com.softcom.collector.core.PriceType
import com.softcom.collector.core.SearchType
import com.softcom.collector.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = true,
    val settings: AppSettings = AppSettings(),
    val isSoftcomshop: Boolean = false,
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val loaded = settingsRepository.load()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    settings = loaded,
                    isSoftcomshop = AppConstants.isSoftcomshop,
                )
            }
        }
    }

    fun setQuickCollection(value: Boolean) {
        val current = _uiState.value.settings
        val next = if (value) {
            current.copy(quickCollection = true, lotSerial = false, grade = false)
        } else {
            current.copy(quickCollection = false)
        }
        persist(next)
    }

    fun setLotSerial(value: Boolean) {
        if (!AppConstants.isSoftcomshop) return
        val current = _uiState.value.settings
        val next = if (value) {
            current.copy(lotSerial = true, quickCollection = false)
        } else {
            current.copy(lotSerial = false)
        }
        persist(next)
    }

    fun setGrade(value: Boolean) {
        val current = _uiState.value.settings
        val next = if (value) {
            current.copy(grade = true, quickCollection = false)
        } else {
            current.copy(grade = false)
        }
        persist(next)
    }

    fun setSearchType(value: SearchType) {
        persist(_uiState.value.settings.copy(searchType = value))
    }

    fun setPriceType(value: PriceType) {
        persist(_uiState.value.settings.copy(priceType = value))
    }

    private fun persist(settings: AppSettings) {
        settingsRepository.save(settings)
        _uiState.update { it.copy(settings = settings) }
    }
}
