package com.softcom.collector.model

data class CollectionItem(
    val id: Long,
    val collectionId: Long,
    val productId: Long,
    val productLocalId: Long,
    val barcode: String,
    val reference: String = "",
    val sku: String,
    val name: String,
    val purchasePrice: Double,
    val salePrice: Double = 0.0,
    val quantity: Double,
    val lot: String = "",
    /** AAAA-MM-DD */
    val manufacturingDate: String = "",
    /** AAAA-MM-DD */
    val expirationDate: String = "",
    val size: String = "",
    val color: String = "",
    val createdAt: Long,
    val updatedAt: Long,
)

data class Product(
    val id: Long,
    val productId: Long,
    val barcode: String,
    val reference: String,
    val sku: String,
    val skuAttributesJson: String = "",
    val name: String,
    val purchasePrice: Double,
    val salePrice: Double,
    val stock: Double,
    val unit: String,
    val group: String,
    val manufacturer: String,
    val supplier: String,
)

fun Product.selectionCode(searchType: com.softcom.collector.core.SearchType): String =
    when (searchType) {
        com.softcom.collector.core.SearchType.REFERENCE ->
            reference.trim().ifBlank { productId.toString() }
        com.softcom.collector.core.SearchType.BARCODE ->
            barcode.trim().ifBlank { productId.toString() }
    }

@Deprecated("Use selectionCode(searchType)")
fun Product.selectionCode(): String =
    barcode.trim().ifBlank { reference.trim() }.ifBlank { productId.toString() }
