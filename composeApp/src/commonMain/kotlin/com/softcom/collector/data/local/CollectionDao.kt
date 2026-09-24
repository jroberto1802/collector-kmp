package com.softcom.collector.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CollectionDao {
    @Insert
    suspend fun insert(collection: CollectionEntity): Long

    @Query(
        """
        SELECT * FROM collections
        WHERE archived = 0
          AND created_at >= :since
          AND (
            :query = ''
            OR LOWER(name) LIKE '%' || LOWER(:query) || '%'
            OR CAST(id AS TEXT) LIKE '%' || :query || '%'
          )
        ORDER BY created_at DESC, id DESC
        """,
    )
    suspend fun listRecent(since: Long, query: String): List<CollectionEntity>

    @Query(
        """
        SELECT * FROM collections
        WHERE archived = 0
          AND (
            :query = ''
            OR LOWER(name) LIKE '%' || LOWER(:query) || '%'
            OR CAST(id AS TEXT) LIKE '%' || :query || '%'
          )
        ORDER BY created_at DESC, id DESC
        """,
    )
    suspend fun listActive(query: String): List<CollectionEntity>

    @Query(
        """
        SELECT * FROM collections
        WHERE archived = 1
          AND (
            :query = ''
            OR LOWER(name) LIKE '%' || LOWER(:query) || '%'
            OR CAST(id AS TEXT) LIKE '%' || :query || '%'
          )
        ORDER BY created_at DESC, id DESC
        """,
    )
    suspend fun listArchived(query: String): List<CollectionEntity>

    @Query("SELECT * FROM collections WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): CollectionEntity?

    @Query("UPDATE collections SET archived = 1 WHERE id = :id")
    suspend fun archive(id: Long)

    @Query("UPDATE collections SET archived = 0 WHERE id = :id")
    suspend fun unarchive(id: Long)

    @Query("UPDATE collections SET item_count = :itemCount WHERE id = :id")
    suspend fun updateItemCount(id: Long, itemCount: Int)

    @Query("DELETE FROM collections WHERE id = :id")
    suspend fun deleteById(id: Long)
}
