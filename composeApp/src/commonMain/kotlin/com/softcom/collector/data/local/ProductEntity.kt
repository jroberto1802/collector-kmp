package com.softcom.collector.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.softcom.collector.model.Product
import com.softcom.collector.model.ProductDto
import com.softcom.collector.model.ProductListItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["product_id"]),
        Index(value = ["barcode"]),
        Index(value = ["sku"]),
        Index(value = ["name"]),
    ],
)
data class ProductEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "product_id") val productId: Long,
    val barcode: String,
    val reference: String,
    val sku: String,
    @ColumnInfo(name = "sku_attributes") val skuAttributes: String,
    val name: String,
    @ColumnInfo(name = "purchase_price") val purchasePrice: Double,
    @ColumnInfo(name = "sale_price") val salePrice: Double,
    val stock: Double,
    val unit: String,
    @ColumnInfo(name = "group_name") val group: String,
    val manufacturer: String,
    val supplier: String,
    @ColumnInfo(name = "synced_at") val syncedAt: Long,
)

fun ProductDto.toEntity(json: Json, syncedAt: Long) = ProductEntity(
    id = id,
    productId = productId,
    barcode = barcode,
    reference = referencia,
    sku = sku,
    skuAttributes = json.encodeToString(skuAttributes),
    name = nome,
    purchasePrice = purchasePrice,
    salePrice = salePrice,
    stock = estoque,
    unit = unit,
    group = grupo,
    manufacturer = fabricante,
    supplier = fornecedor,
    syncedAt = syncedAt,
)

fun ProductEntity.toDomain() = Product(
    id = id,
    productId = productId,
    barcode = barcode,
    reference = reference,
    sku = sku,
    skuAttributesJson = skuAttributes,
    name = name,
    purchasePrice = purchasePrice,
    salePrice = salePrice,
    stock = stock,
    unit = unit,
    group = group,
    manufacturer = manufacturer,
    supplier = supplier,
)

fun ProductEntity.toListItem() = ProductListItem(
    id = id,
    code = productId.takeIf { it > 0 }?.toString() ?: barcode.ifBlank { sku },
    name = name,
    size = com.softcom.collector.core.extractGradeAttributes(skuAttributes).size,
    color = com.softcom.collector.core.extractGradeAttributes(skuAttributes).color,
)
