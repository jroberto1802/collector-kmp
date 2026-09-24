package com.softcom.collector.data.local

import androidx.room.Database
import androidx.room.ConstructedBy
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [ProductEntity::class, CollectionEntity::class, CollectionItemEntity::class],
    version = 4,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun collectionDao(): CollectionDao
    abstract fun collectionItemDao(): CollectionItemDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect fun createAppDatabase(): AppDatabase
