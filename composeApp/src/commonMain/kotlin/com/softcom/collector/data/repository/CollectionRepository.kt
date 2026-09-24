package com.softcom.collector.data.repository

import com.softcom.collector.data.local.AppDatabase
import com.softcom.collector.data.local.CollectionEntity
import com.softcom.collector.data.local.toDomain
import com.softcom.collector.model.Collection
import com.softcom.collector.model.CollectionFilter
import com.softcom.collector.model.DEFAULT_COLLECTION_STATUS
import com.softcom.collector.model.SEVEN_DAYS_MS
import com.softcom.collector.platform.currentTimeMillis

class CollectionRepository(
    private val database: AppDatabase,
) {
    private val dao get() = database.collectionDao()

    suspend fun list(filter: CollectionFilter, search: String): List<Collection> {
        val query = search.trim()
        val entities = when (filter) {
            CollectionFilter.RECENTES -> dao.listRecent(
                since = currentTimeMillis() - SEVEN_DAYS_MS,
                query = query,
            )
            CollectionFilter.TODAS -> dao.listActive(query)
            CollectionFilter.ARQUIVADAS -> dao.listArchived(query)
        }
        return entities.map { it.toDomain() }
    }

    suspend fun create(name: String): Collection {
        val trimmed = name.trim()
        require(trimmed.isNotEmpty()) { "Informe o nome da coleta." }

        val id = dao.insert(
            CollectionEntity(
                name = trimmed,
                status = DEFAULT_COLLECTION_STATUS,
                archived = false,
                itemCount = 0,
                createdAt = currentTimeMillis(),
            ),
        )
        return dao.findById(id)?.toDomain()
            ?: error("Não foi possível criar a coleta.")
    }

    suspend fun archive(id: Long) {
        dao.archive(id)
    }

    suspend fun unarchive(id: Long) {
        dao.unarchive(id)
    }

    suspend fun delete(id: Long) {
        database.collectionItemDao().deleteByCollection(id)
        dao.deleteById(id)
    }
}
