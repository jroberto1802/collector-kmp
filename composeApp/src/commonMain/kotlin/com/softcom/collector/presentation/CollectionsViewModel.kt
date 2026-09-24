package com.softcom.collector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softcom.collector.data.repository.CollectionRepository
import com.softcom.collector.model.Collection
import com.softcom.collector.model.CollectionFilter
import com.softcom.collector.model.CollectionsEmptyCopy
import com.softcom.collector.model.emptyCopyFor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CollectionsUiState(
    val filter: CollectionFilter = CollectionFilter.RECENTES,
    val search: String = "",
    val collections: List<Collection> = emptyList(),
    val isLoading: Boolean = true,
    val isNewCollectionVisible: Boolean = false,
    val newCollectionName: String = "",
    val isCreating: Boolean = false,
    val createError: String = "",
    val menuCollection: Collection? = null,
    val collectionPendingDelete: Collection? = null,
    val isDeleting: Boolean = false,
) {
    val emptyCopy: CollectionsEmptyCopy get() = emptyCopyFor(filter)
}

class CollectionsViewModel(
    private val collectionRepository: CollectionRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CollectionsUiState())
    val uiState: StateFlow<CollectionsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun setFilter(filter: CollectionFilter) {
        if (_uiState.value.filter == filter) return
        _uiState.update { it.copy(filter = filter) }
        refresh()
    }

    fun setSearch(value: String) {
        _uiState.update { it.copy(search = value) }
        refresh()
    }

    fun openNewCollection() {
        _uiState.update {
            it.copy(
                isNewCollectionVisible = true,
                newCollectionName = "",
                createError = "",
            )
        }
    }

    fun closeNewCollection() {
        if (_uiState.value.isCreating) return
        _uiState.update {
            it.copy(
                isNewCollectionVisible = false,
                newCollectionName = "",
                createError = "",
            )
        }
    }

    fun setNewCollectionName(value: String) {
        _uiState.update { it.copy(newCollectionName = value, createError = "") }
    }

    fun createCollection() {
        val name = _uiState.value.newCollectionName.trim()
        if (name.isEmpty()) {
            _uiState.update { it.copy(createError = "Informe o nome da coleta.") }
            return
        }
        if (_uiState.value.isCreating) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, createError = "") }
            runCatching { collectionRepository.create(name) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            isNewCollectionVisible = false,
                            newCollectionName = "",
                            createError = "",
                        )
                    }
                    refresh()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            createError = error.message ?: "Não foi possível criar a coleta.",
                        )
                    }
                }
        }
    }

    fun openCollectionMenu(collection: Collection) {
        _uiState.update { it.copy(menuCollection = collection) }
    }

    fun closeCollectionMenu() {
        _uiState.update { it.copy(menuCollection = null) }
    }

    fun archiveSelectedCollection() {
        val collection = _uiState.value.menuCollection ?: return
        _uiState.update { it.copy(menuCollection = null) }
        viewModelScope.launch {
            collectionRepository.archive(collection.id)
            refresh()
        }
    }

    fun unarchiveSelectedCollection() {
        val collection = _uiState.value.menuCollection ?: return
        _uiState.update { it.copy(menuCollection = null) }
        viewModelScope.launch {
            collectionRepository.unarchive(collection.id)
            refresh()
        }
    }

    fun requestDeleteCollection() {
        val collection = _uiState.value.menuCollection ?: return
        _uiState.update {
            it.copy(
                collectionPendingDelete = collection,
                menuCollection = null,
            )
        }
    }

    fun cancelDeleteCollection() {
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(collectionPendingDelete = null) }
    }

    fun confirmDeleteCollection() {
        val collection = _uiState.value.collectionPendingDelete ?: return
        if (_uiState.value.isDeleting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            runCatching { collectionRepository.delete(collection.id) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            collectionPendingDelete = null,
                        )
                    }
                    refresh()
                }
                .onFailure {
                    _uiState.update { it.copy(isDeleting = false) }
                }
        }
    }

    fun refresh() {
        val snapshot = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                collectionRepository.list(snapshot.filter, snapshot.search)
            }.onSuccess { items ->
                _uiState.update { it.copy(isLoading = false, collections = items) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, collections = emptyList()) }
            }
        }
    }
}
