package com.softcom.collector.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CollectionItemDao {
    @Query(
        """
        SELECT
          ci.id AS id,
          ci.collection_id AS collection_id,
          ci.product_id AS product_id,
          ci.product_local_id AS product_local_id,
          ci.barcode AS barcode,
          ci.sku AS sku,
          ci.name AS name,
          COALESCE(
            NULLIF(ci.purchase_price, 0),
            (
              SELECT p.purchase_price
              FROM products p
              WHERE p.product_id = ci.product_id
              LIMIT 1
            ),
            0
          ) AS purchase_price,
          ci.quantity AS quantity,
          ci.lot AS lot,
          ci.manufacturing_date AS manufacturing_date,
          ci.expiration_date AS expiration_date,
          ci.created_at AS created_at,
          ci.updated_at AS updated_at
        FROM collection_items ci
        WHERE ci.collection_id = :collectionId
        ORDER BY ci.updated_at DESC, ci.id DESC
        """,
    )
    suspend fun listByCollection(collectionId: Long): List<CollectionItemEntity>

    @Query(
        """
        SELECT * FROM collection_items
        WHERE collection_id = :collectionId
          AND product_id = :productId
          AND lot = :lot
          AND manufacturing_date = :manufacturingDate
          AND expiration_date = :expirationDate
        LIMIT 1
        """,
    )
    suspend fun findByCollectionProductAndLot(
        collectionId: Long,
        productId: Long,
        lot: String,
        manufacturingDate: String,
        expirationDate: String,
    ): CollectionItemEntity?

    @Query("SELECT * FROM collection_items WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): CollectionItemEntity?

    @Insert
    suspend fun insert(item: CollectionItemEntity): Long

    @Update
    suspend fun update(item: CollectionItemEntity)

    @Query(
        """
        UPDATE collection_items
        SET quantity = :quantity, updated_at = :updatedAt
        WHERE id = :id
        """,
    )
    suspend fun updateQuantity(id: Long, quantity: Double, updatedAt: Long)

    @Query(
        """
        UPDATE collection_items
        SET quantity = :quantity,
            lot = :lot,
            manufacturing_date = :manufacturingDate,
            expiration_date = :expirationDate,
            updated_at = :updatedAt
        WHERE id = :id
        """,
    )
    suspend fun updateItem(
        id: Long,
        quantity: Double,
        lot: String,
        manufacturingDate: String,
        expirationDate: String,
        updatedAt: Long,
    )

    @Query("DELETE FROM collection_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM collection_items WHERE collection_id = :collectionId")
    suspend fun deleteByCollection(collectionId: Long)

    @Query("SELECT COUNT(*) FROM collection_items WHERE collection_id = :collectionId")
    suspend fun countByCollection(collectionId: Long): Int
}
