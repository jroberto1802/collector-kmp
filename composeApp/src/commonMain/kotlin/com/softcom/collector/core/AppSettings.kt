package com.softcom.collector.core

enum class SearchType(val value: String, val label: String) {
    REFERENCE("reference", "Referência"),
    BARCODE("barcode", "Barras");

    companion object {
        fun fromValue(value: String?): SearchType =
            entries.firstOrNull { it.value == value } ?: BARCODE
    }
}

enum class PriceType(val value: String, val label: String) {
    PURCHASE("purchase", "Compra"),
    SALE("sale", "Venda");

    companion object {
        fun fromValue(value: String?): PriceType =
            entries.firstOrNull { it.value == value } ?: PURCHASE
    }
}

data class AppSettings(
    val quickCollection: Boolean = false,
    val lotSerial: Boolean = false,
    val grade: Boolean = false,
    val searchType: SearchType = SearchType.BARCODE,
    val priceType: PriceType = PriceType.PURCHASE,
)
