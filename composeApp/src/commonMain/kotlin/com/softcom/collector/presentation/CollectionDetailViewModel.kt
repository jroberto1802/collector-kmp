package com.softcom.collector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softcom.collector.core.AppConstants
import com.softcom.collector.core.PriceType
import com.softcom.collector.core.SearchType
import com.softcom.collector.core.formatCollectionItemQuantity
import com.softcom.collector.core.formatCollectionQuantity
import com.softcom.collector.core.formatIsoToBrDate
import com.softcom.collector.core.isManufacturingAfterExpiration
import com.softcom.collector.core.maskBrDateInput
import com.softcom.collector.core.parseBrDateToIso
import com.softcom.collector.core.parseCollectionQuantity
import com.softcom.collector.data.repository.CollectionDetailRepository
import com.softcom.collector.data.repository.LotFields
import com.softcom.collector.data.repository.SettingsRepository
import com.softcom.collector.model.Collection
import com.softcom.collector.model.CollectionItem
import com.softcom.collector.model.Product
import com.softcom.collector.model.selectionCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val DEFAULT_QUANTITY = "1,000"
private const val LOOKUP_DEBOUNCE_MS = 220L
private const val TOAST_DURATION_MS = 2500L

data class CollectionDetailUiState(
    val collection: Collection? = null,
    val items: List<CollectionItem> = emptyList(),
    val isLoading: Boolean = true,
    val loadError: String = "",
    val productQuery: String = "",
    val selectedProduct: Product? = null,
    val quantityText: String = DEFAULT_QUANTITY,
    val lotText: String = "",
    val manufacturingDateText: String = "",
    val expirationDateText: String = "",
    val feedback: String = "",
    val toastMessage: String = "",
    val isAdding: Boolean = false,
    val isScannerVisible: Boolean = false,
    val isProductSearchVisible: Boolean = false,
    val isActionsVisible: Boolean = false,
    val menuItem: CollectionItem? = null,
    val editingItem: CollectionItem? = null,
    val editQuantityText: String = "",
    val editLotText: String = "",
    val editManufacturingDateText: String = "",
    val editExpirationDateText: String = "",
    val editError: String = "",
    val isSavingEdit: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<Product> = emptyList(),
    val isSearching: Boolean = false,
    val isQuickCollection: Boolean = false,
    val isLotSerial: Boolean = false,
    val isGrade: Boolean = false,
    val searchType: SearchType = SearchType.BARCODE,
    val priceType: PriceType = PriceType.PURCHASE,
)

class CollectionDetailViewModel(
    private val collectionId: Long,
    private val repository: CollectionDetailRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CollectionDetailUiState())
    val uiState: StateFlow<CollectionDetailUiState> = _uiState.asStateFlow()

    private var productLookupJob: Job? = null
    private var searchJob: Job? = null
    private var toastJob: Job? = null
    private var isAddingGuard = false

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = "") }
            try {
                val settings = settingsRepository.load()
                val isLotSerial = settings.lotSerial && AppConstants.isSoftcomshop

                val collection = repository.getCollection(collectionId)
                if (collection == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadError = "Coleta não encontrada.",
                            collection = null,
                            items = emptyList(),
                            isQuickCollection = settings.quickCollection,
                            isLotSerial = isLotSerial,
                            isGrade = settings.grade,
                            searchType = settings.searchType,
                            priceType = settings.priceType,
                        )
                    }
                    return@launch
                }
                val items = repository.listItems(collectionId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        collection = collection,
                        items = items,
                        isQuickCollection = settings.quickCollection,
                        isLotSerial = isLotSerial,
                        isGrade = settings.grade,
                        searchType = settings.searchType,
                        priceType = settings.priceType,
                    )
                }
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loadError = error.message ?: "Não foi possível carregar a coleta.",
                    )
                }
            }
        }
    }

    fun setProductQuery(value: String) {
        _uiState.update { it.copy(productQuery = value) }
        productLookupJob?.cancel()
        val query = value.trim()
        if (query.isEmpty()) {
            _uiState.update { it.copy(selectedProduct = null) }
            return
        }
        productLookupJob = viewModelScope.launch {
            delay(LOOKUP_DEBOUNCE_MS)
            val searchType = _uiState.value.searchType
            val product = repository.findBySearchCode(query, searchType)
            _uiState.update {
                it.copy(
                    selectedProduct = product,
                    feedback = if (product != null) "" else it.feedback,
                )
            }
            if (product != null && _uiState.value.isQuickCollection) {
                addProduct(product)
            }
        }
    }

    fun setQuantityText(value: String) {
        _uiState.update { it.copy(quantityText = value) }
    }

    fun setLotText(value: String) {
        _uiState.update { it.copy(lotText = value) }
    }

    fun setManufacturingDateText(value: String) {
        _uiState.update { it.copy(manufacturingDateText = maskBrDateInput(value)) }
    }

    fun setExpirationDateText(value: String) {
        _uiState.update { it.copy(expirationDateText = maskBrDateInput(value)) }
    }

    fun increaseQuantity() {
        val current = parseCollectionQuantity(_uiState.value.quantityText) ?: 1.0
        _uiState.update { it.copy(quantityText = formatCollectionQuantity(current + 1.0)) }
    }

    fun decreaseQuantity() {
        val current = parseCollectionQuantity(_uiState.value.quantityText) ?: 1.0
        val next = maxOf(0.001, current - 1.0)
        _uiState.update { it.copy(quantityText = formatCollectionQuantity(next)) }
    }

    fun openScanner() = _uiState.update { it.copy(isScannerVisible = true) }

    fun closeScanner() = _uiState.update { it.copy(isScannerVisible = false) }

    fun applyBarcode(barcode: String) {
        val normalized = barcode.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(isScannerVisible = false) }
            val product = repository.findByBarcode(normalized)
            if (product == null) {
                _uiState.update {
                    it.copy(
                        productQuery = normalized,
                        selectedProduct = null,
                        feedback = "Nenhum produto encontrado com este código de barras.",
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(feedback = "") }
            if (_uiState.value.isQuickCollection) {
                _uiState.update { it.copy(productQuery = "", selectedProduct = null) }
                addProduct(product)
                return@launch
            }

            _uiState.update {
                it.copy(
                    productQuery = normalized,
                    selectedProduct = product,
                )
            }
        }
    }

    fun openProductSearch() {
        _uiState.update {
            it.copy(
                isProductSearchVisible = true,
                searchQuery = "",
                searchResults = emptyList(),
                isSearching = false,
            )
        }
    }

    fun closeProductSearch() {
        _uiState.update {
            it.copy(
                isProductSearchVisible = false,
                searchQuery = "",
                searchResults = emptyList(),
                isSearching = false,
            )
        }
    }

    fun setSearchQuery(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
        searchJob?.cancel()
        val query = value.trim()
        if (query.isEmpty()) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            delay(LOOKUP_DEBOUNCE_MS)
            val results = repository.searchProducts(query, _uiState.value.searchType)
            _uiState.update { it.copy(searchResults = results, isSearching = false) }
        }
    }

    fun selectProductFromSearch(product: Product) {
        val state = _uiState.value
        if (state.isQuickCollection) {
            _uiState.update {
                it.copy(
                    productQuery = "",
                    selectedProduct = null,
                    feedback = "",
                    isProductSearchVisible = false,
                    searchQuery = "",
                    searchResults = emptyList(),
                    isSearching = false,
                )
            }
            viewModelScope.launch { addProduct(product) }
            return
        }

        _uiState.update {
            it.copy(
                selectedProduct = product,
                productQuery = product.selectionCode(it.searchType),
                feedback = "",
                isProductSearchVisible = false,
                searchQuery = "",
                searchResults = emptyList(),
                isSearching = false,
            )
        }
    }

    fun addItem() {
        val product = _uiState.value.selectedProduct
        if (product == null) {
            _uiState.update {
                it.copy(feedback = "Informe um código de barras válido para localizar o produto.")
            }
            return
        }
        viewModelScope.launch { addProduct(product) }
    }

    private suspend fun addProduct(product: Product) {
        if (isAddingGuard) return

        val state = _uiState.value
        val quantity = parseCollectionQuantity(state.quantityText)
        if (quantity == null || quantity <= 0) {
            _uiState.update { it.copy(feedback = "Informe uma quantidade válida.") }
            return
        }

        val lotFields = try {
            resolveLotFieldsForSave(
                isLotSerial = state.isLotSerial,
                lotText = state.lotText,
                manufacturingDateText = state.manufacturingDateText,
                expirationDateText = state.expirationDateText,
            )
        } catch (error: IllegalArgumentException) {
            _uiState.update { it.copy(feedback = error.message.orEmpty()) }
            return
        }

        isAddingGuard = true
        _uiState.update { it.copy(isAdding = true, feedback = "") }
        try {
            repository.addItem(collectionId, product, quantity, lotFields)
            _uiState.update {
                it.copy(
                    isAdding = false,
                    productQuery = "",
                    selectedProduct = null,
                    quantityText = DEFAULT_QUANTITY,
                    lotText = "",
                    manufacturingDateText = "",
                    expirationDateText = "",
                )
            }
            if (state.isQuickCollection) {
                showToast("Produto Adicionado!")
            }
            load()
        } catch (error: Throwable) {
            _uiState.update {
                it.copy(
                    isAdding = false,
                    feedback = error.message ?: "Não foi possível adicionar o item.",
                )
            }
        } finally {
            isAddingGuard = false
        }
    }

    fun openActions() = _uiState.update { it.copy(isActionsVisible = true) }

    fun closeActions() = _uiState.update { it.copy(isActionsVisible = false) }

    fun openItemMenu(item: CollectionItem) = _uiState.update { it.copy(menuItem = item) }

    fun closeItemMenu() = _uiState.update { it.copy(menuItem = null) }

    fun requestEditItem() {
        val item = _uiState.value.menuItem ?: return
        _uiState.update {
            it.copy(
                editingItem = item,
                editQuantityText = formatCollectionItemQuantity(item.quantity),
                editLotText = item.lot,
                editManufacturingDateText = formatIsoToBrDate(item.manufacturingDate),
                editExpirationDateText = formatIsoToBrDate(item.expirationDate),
                editError = "",
                menuItem = null,
            )
        }
    }

    fun closeEditItem() {
        if (_uiState.value.isSavingEdit) return
        _uiState.update {
            it.copy(
                editingItem = null,
                editQuantityText = "",
                editLotText = "",
                editManufacturingDateText = "",
                editExpirationDateText = "",
                editError = "",
            )
        }
    }

    fun setEditQuantityText(value: String) {
        _uiState.update { it.copy(editQuantityText = value) }
    }

    fun setEditLotText(value: String) {
        _uiState.update { it.copy(editLotText = value) }
    }

    fun setEditManufacturingDateText(value: String) {
        _uiState.update { it.copy(editManufacturingDateText = maskBrDateInput(value)) }
    }

    fun setEditExpirationDateText(value: String) {
        _uiState.update { it.copy(editExpirationDateText = maskBrDateInput(value)) }
    }

    fun saveEditedItem() {
        val state = _uiState.value
        val item = state.editingItem ?: return
        val quantity = parseCollectionQuantity(state.editQuantityText)
        if (quantity == null || quantity <= 0) {
            _uiState.update { it.copy(editError = "Informe uma quantidade válida.") }
            return
        }

        var lot = item.lot
        var manufacturingDate = item.manufacturingDate
        var expirationDate = item.expirationDate

        if (state.isLotSerial) {
            val lotFields = try {
                resolveLotFieldsForSave(
                    isLotSerial = true,
                    lotText = state.editLotText,
                    manufacturingDateText = state.editManufacturingDateText,
                    expirationDateText = state.editExpirationDateText,
                )
            } catch (error: IllegalArgumentException) {
                _uiState.update { it.copy(editError = error.message.orEmpty()) }
                return
            }
            lot = lotFields.lot
            manufacturingDate = lotFields.manufacturingDate
            expirationDate = lotFields.expirationDate
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingEdit = true, editError = "") }
            try {
                repository.updateItem(
                    itemId = item.id,
                    quantity = quantity,
                    lotFields = LotFields(
                        lot = lot,
                        manufacturingDate = manufacturingDate,
                        expirationDate = expirationDate,
                    ),
                )
                _uiState.update {
                    it.copy(
                        isSavingEdit = false,
                        editingItem = null,
                        editQuantityText = "",
                        editLotText = "",
                        editManufacturingDateText = "",
                        editExpirationDateText = "",
                    )
                }
                load()
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(
                        isSavingEdit = false,
                        editError = error.message ?: "Não foi possível salvar o item.",
                    )
                }
            }
        }
    }

    fun removeSelectedItem() {
        val item = _uiState.value.menuItem ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(menuItem = null) }
            repository.removeItem(item.id)
            load()
        }
    }

    private fun showToast(message: String) {
        toastJob?.cancel()
        _uiState.update { it.copy(toastMessage = message) }
        toastJob = viewModelScope.launch {
            delay(TOAST_DURATION_MS)
            _uiState.update { it.copy(toastMessage = "") }
        }
    }

    private fun resolveLotFieldsForSave(
        isLotSerial: Boolean,
        lotText: String,
        manufacturingDateText: String,
        expirationDateText: String,
    ): LotFields {
        if (!isLotSerial) {
            return LotFields()
        }

        val manufacturingIso = parseBrDateToIso(manufacturingDateText)
            ?: throw IllegalArgumentException("Informe a data de fabricação (DD/MM/AAAA).")
        val expirationIso = parseBrDateToIso(expirationDateText)
            ?: throw IllegalArgumentException("Informe a data de validade (DD/MM/AAAA).")

        if (isManufacturingAfterExpiration(manufacturingIso, expirationIso)) {
            throw IllegalArgumentException(
                "A data de fabricação não pode ser maior que a data de validade.",
            )
        }

        return LotFields(
            lot = lotText.trim(),
            manufacturingDate = manufacturingIso,
            expirationDate = expirationIso,
        )
    }
}
