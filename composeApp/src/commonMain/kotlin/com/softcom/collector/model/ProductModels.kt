package com.softcom.collector.model

import com.softcom.collector.core.FlexibleDoubleSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Long,
    @SerialName("produto_id") val productId: Long = 0,
    @SerialName("codigo_barras") val barcode: String = "",
    val referencia: String = "",
    val sku: String = "",
    @SerialName("sku_atributo") val skuAttributes: List<ProductSkuAttributeDto> = emptyList(),
    val nome: String = "",
    @SerialName("preco_compra")
    @Serializable(with = FlexibleDoubleSerializer::class)
    val purchasePrice: Double = 0.0,
    @SerialName("preco_venda")
    @Serializable(with = FlexibleDoubleSerializer::class)
    val salePrice: Double = 0.0,
    @Serializable(with = FlexibleDoubleSerializer::class)
    val estoque: Double = 0.0,
    @SerialName("unidade_medida") val unit: String = "",
    val grupo: String = "",
    val fabricante: String = "",
    val fornecedor: String = "",
)

@Serializable
data class ProductSkuAttributeDto(
    val id: Long = 0,
    val nome: String = "",
    @SerialName("item_id") val itemId: Long = 0,
    @SerialName("item_nome") val itemName: String = "",
)

@Serializable
data class ProductsPageDto(
    @SerialName("current_page") val currentPage: Int = 1,
    val data: List<ProductDto> = emptyList(),
    @SerialName("last_page") val lastPage: Int = 1,
    val total: Int = 0,
)

data class ProductListItem(
    val id: Long,
    val code: String,
    val name: String,
    val size: String = "",
    val color: String = "",
)

data class ProductPage(
    val items: List<ProductListItem>,
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val totalPages: Int,
)

data class ProductSyncProgress(
    val currentPage: Int,
    val lastPage: Int,
    val downloadedProducts: Int,
)
