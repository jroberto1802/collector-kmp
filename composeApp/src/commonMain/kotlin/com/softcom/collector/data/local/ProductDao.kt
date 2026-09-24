package com.softcom.collector.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {
    @Query("DELETE FROM products")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query(
        """
        SELECT * FROM products
        WHERE :query = ''
           OR LOWER(name) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(barcode) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(sku) LIKE '%' || LOWER(:query) || '%'
           OR CAST(product_id AS TEXT) LIKE '%' || :query || '%'
        ORDER BY name COLLATE NOCASE, id
        LIMIT :limit OFFSET :offset
        """,
    )
    suspend fun getPage(query: String, limit: Int, offset: Int): List<ProductEntity>

    @Query(
        """
        SELECT COUNT(*) FROM products
        WHERE :query = ''
           OR LOWER(name) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(barcode) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(sku) LIKE '%' || LOWER(:query) || '%'
           OR CAST(product_id AS TEXT) LIKE '%' || :query || '%'
        """,
    )
    suspend fun count(query: String): Int

    @Query(
        """
        SELECT * FROM products
        WHERE barcode = :code
        LIMIT 1
        """,
    )
    suspend fun findByBarcode(code: String): ProductEntity?

    @Query(
        """
        SELECT * FROM products
        WHERE reference = :code COLLATE NOCASE
        LIMIT 1
        """,
    )
    suspend fun findByReference(code: String): ProductEntity?

    @Query(
        """
        SELECT * FROM products
        WHERE barcode = :code
           OR reference = :code
           OR CAST(product_id AS TEXT) = :code
        LIMIT 1
        """,
    )
    suspend fun findByCode(code: String): ProductEntity?

    @Query(
        """
        SELECT * FROM products
        WHERE product_id = :productId
        LIMIT 1
        """,
    )
    suspend fun findByProductId(productId: Long): ProductEntity?

    @Query(
        """
        SELECT * FROM products
        WHERE LOWER(barcode) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(name) LIKE '%' || LOWER(:query) || '%'
        ORDER BY
          CASE
            WHEN barcode = :query COLLATE NOCASE THEN 0
            WHEN LOWER(barcode) LIKE '%' || LOWER(:query) || '%' THEN 1
            ELSE 2
          END,
          name COLLATE NOCASE,
          id
        LIMIT :limit
        """,
    )
    suspend fun searchByBarcode(query: String, limit: Int): List<ProductEntity>

    @Query(
        """
        SELECT * FROM products
        WHERE LOWER(reference) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(name) LIKE '%' || LOWER(:query) || '%'
        ORDER BY
          CASE
            WHEN reference = :query COLLATE NOCASE THEN 0
            WHEN LOWER(reference) LIKE '%' || LOWER(:query) || '%' THEN 1
            ELSE 2
          END,
          name COLLATE NOCASE,
          id
        LIMIT :limit
        """,
    )
    suspend fun searchByReference(query: String, limit: Int): List<ProductEntity>

    @Query(
        """
        SELECT * FROM products
        WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(barcode) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(reference) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(sku) LIKE '%' || LOWER(:query) || '%'
           OR CAST(product_id AS TEXT) LIKE '%' || :query || '%'
        ORDER BY name COLLATE NOCASE, id
        LIMIT :limit
        """,
    )
    suspend fun search(query: String, limit: Int): List<ProductEntity>
}
