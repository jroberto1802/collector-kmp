package com.softcom.collector.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.softcom.collector.core.extractGradeAttributes
import com.softcom.collector.model.CollectionItem

@Entity(
    tableName = "collection_items",
    indices = [
        Index(value = ["collection_id"]),
        Index(value = ["barcode"]),
        Index(
            value = ["collection_id", "product_id", "lot", "manufacturing_date", "expiration_date"],
        ),
    ],
)
data class CollectionItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "collection_id") val collectionId: Long,
    @ColumnInfo(name = "product_id") val productId: Long,
    @ColumnInfo(name = "product_local_id") val productLocalId: Long = 0,
    val barcode: String = "",
    val sku: String = "",
    val name: String = "",
    @ColumnInfo(name = "purchase_price") val purchasePrice: Double = 0.0,
    val quantity: Double = 0.0,
    val lot: String = "",
    @ColumnInfo(name = "manufacturing_date") val manufacturingDate: String = "",
    @ColumnInfo(name = "expiration_date") val expirationDate: String = "",
    @ColumnInfo(name = "created_at") val createdAt: Long = 0,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = 0,
)

fun CollectionItemEntity.toDomain(
    reference: String = "",
    salePrice: Double = 0.0,
    skuAttributesJson: String = "",
): CollectionItem {
    val grade = extractGradeAttributes(skuAttributesJson)
    return CollectionItem(
        id = id,
        collectionId = collectionId,
        productId = productId,
        productLocalId = productLocalId,
        barcode = barcode,
        reference = reference,
        sku = sku,
        name = name,
        purchasePrice = purchasePrice,
        salePrice = salePrice,
        quantity = quantity,
        lot = lot,
        manufacturingDate = manufacturingDate,
        expirationDate = expirationDate,
        size = grade.size,
        color = grade.color,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
