package com.softcom.collector.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.softcom.collector.model.Collection
import com.softcom.collector.model.DEFAULT_COLLECTION_STATUS

@Entity(
    tableName = "collections",
    indices = [
        Index(value = ["created_at"]),
        Index(value = ["archived"]),
        Index(value = ["name"]),
    ],
)
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val status: String = DEFAULT_COLLECTION_STATUS,
    val archived: Boolean = false,
    @ColumnInfo(name = "item_count") val itemCount: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)

fun CollectionEntity.toDomain() = Collection(
    id = id,
    name = name,
    status = status,
    archived = archived,
    itemCount = itemCount,
    createdAt = createdAt,
)
