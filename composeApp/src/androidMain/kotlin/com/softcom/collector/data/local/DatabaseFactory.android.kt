package com.softcom.collector.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.softcom.collector.platform.AndroidContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File

private const val DATABASE_NAME = "collector_v4.db"

actual fun createAppDatabase(): AppDatabase {
    val context = AndroidContext.applicationContext
    val dbFile = context.getDatabasePath(DATABASE_NAME)

    fun build(): AppDatabase =
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath,
            factory = { AppDatabaseConstructor.initialize() },
        )
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

    var database = build()
    try {
        validateDatabase(database)
    } catch (_: Throwable) {
        runCatching { database.close() }
        deleteDatabaseFiles(dbFile)
        context.deleteDatabase(DATABASE_NAME)
        // Remove legado corrompido das versões anteriores
        context.deleteDatabase("collector.db")
        deleteDatabaseFiles(context.getDatabasePath("collector.db"))

        database = build()
        validateDatabase(database)
    }
    return database
}

private fun validateDatabase(database: AppDatabase) {
    runBlocking(Dispatchers.IO) {
        database.collectionDao().findById(-1L)
    }
}

private fun deleteDatabaseFiles(dbFile: File) {
    sequenceOf(
        dbFile,
        File("${dbFile.path}-wal"),
        File("${dbFile.path}-shm"),
        File("${dbFile.path}-journal"),
    ).forEach { file ->
        runCatching { if (file.exists()) file.delete() }
    }
}
