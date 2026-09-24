package com.softcom.collector.data.repository

import com.softcom.collector.core.SearchType
import com.softcom.collector.data.local.AppDatabase
import com.softcom.collector.data.local.CollectionItemEntity
import com.softcom.collector.data.local.toDomain
import com.softcom.collector.model.Collection
import com.softcom.collector.model.CollectionItem
import com.softcom.collector.model.Product
import com.softcom.collector.platform.currentTimeMillis

data class LotFields(
    val lot: String = "",
    val manufacturingDate: String = "",
    val expirationDate: String = "",
)

class CollectionDetailRepository(
    private val database: AppDatabase,
) {
    private val collectionDao get() = database.collectionDao()
    private val itemDao get() = database.collectionItemDao()
    private val productDao get() = database.productDao()

    suspend fun getCollection(id: Long): Collection? =
        collectionDao.findById(id)?.toDomain()

    suspend fun listItems(collectionId: Long): List<CollectionItem> =
        itemDao.listByCollection(collectionId).map { entity ->
            val product = productDao.findByProductId(entity.productId)
            entity.toDomain(
                reference = product?.reference.orEmpty(),
                salePrice = product?.salePrice ?: 0.0,
                skuAttributesJson = product?.skuAttributes.orEmpty(),
            )
        }

    suspend fun findByBarcode(barcode: String): Product? {
        val code = barcode.trim()
        if (code.isEmpty()) return null
        return productDao.findByBarcode(code)?.toDomain()
            ?: productDao.findByCode(code)?.toDomain()
    }

    suspend fun findBySearchCode(code: String, searchType: SearchType): Product? {
        val normalized = code.trim()
        if (normalized.isEmpty()) return null
        return when (searchType) {
            SearchType.REFERENCE -> productDao.findByReference(normalized)?.toDomain()
            SearchType.BARCODE -> productDao.findByBarcode(normalized)?.toDomain()
        }
    }

    suspend fun searchProducts(
        query: String,
        searchType: SearchType = SearchType.BARCODE,
        limit: Int = 40,
    ): List<Product> {
        val normalized = query.trim()
        if (normalized.isEmpty()) return emptyList()
        return when (searchType) {
            SearchType.REFERENCE -> productDao.searchByReference(normalized, limit)
            SearchType.BARCODE -> productDao.searchByBarcode(normalized, limit)
        }.map { it.toDomain() }
    }

    suspend fun addItem(
        collectionId: Long,
        product: Product,
        quantity: Double,
        lotFields: LotFields = LotFields(),
    ): CollectionItem {
        require(quantity > 0) { "Informe uma quantidade válida." }

        val now = currentTimeMillis()
        val lot = lotFields.lot.trim()
        val manufacturingDate = lotFields.manufacturingDate.trim()
        val expirationDate = lotFields.expirationDate.trim()

        val existing = itemDao.findByCollectionProductAndLot(
            collectionId = collectionId,
            productId = product.productId,
            lot = lot,
            manufacturingDate = manufacturingDate,
            expirationDate = expirationDate,
        )

        val itemId = if (existing != null) {
            itemDao.update(
                existing.copy(
                    quantity = existing.quantity + quantity,
                    updatedAt = now,
                    name = product.name,
                    barcode = product.barcode,
                    sku = product.sku,
                    purchasePrice = product.purchasePrice,
                    productLocalId = product.id,
                ),
            )
            existing.id
        } else {
            itemDao.insert(
                CollectionItemEntity(
                    collectionId = collectionId,
                    productId = product.productId,
                    productLocalId = product.id,
                    barcode = product.barcode,
                    sku = product.sku,
                    name = product.name,
                    purchasePrice = product.purchasePrice,
                    quantity = quantity,
                    lot = lot,
                    manufacturingDate = manufacturingDate,
                    expirationDate = expirationDate,
                    createdAt = now,
                    updatedAt = now,
                ),
            )
        }

        refreshItemCount(collectionId)
        val saved = itemDao.findById(itemId) ?: error("Não foi possível adicionar o item.")
        return saved.toDomain(
            reference = product.reference,
            salePrice = product.salePrice,
            skuAttributesJson = product.skuAttributesJson,
        )
    }

    suspend fun updateItemQuantity(itemId: Long, quantity: Double) {
        require(quantity > 0) { "Informe uma quantidade válida." }
        itemDao.updateQuantity(itemId, quantity, currentTimeMillis())
    }

    suspend fun updateItem(
        itemId: Long,
        quantity: Double,
        lotFields: LotFields = LotFields(),
    ) {
        require(quantity > 0) { "Informe uma quantidade válida." }
        itemDao.updateItem(
            id = itemId,
            quantity = quantity,
            lot = lotFields.lot.trim(),
            manufacturingDate = lotFields.manufacturingDate.trim(),
            expirationDate = lotFields.expirationDate.trim(),
            updatedAt = currentTimeMillis(),
        )
    }

    suspend fun removeItem(itemId: Long) {
        val item = itemDao.findById(itemId) ?: return
        itemDao.deleteById(itemId)
        refreshItemCount(item.collectionId)
    }

    private suspend fun refreshItemCount(collectionId: Long) {
        val count = itemDao.countByCollection(collectionId)
        collectionDao.updateItemCount(collectionId, count)
    }
}
