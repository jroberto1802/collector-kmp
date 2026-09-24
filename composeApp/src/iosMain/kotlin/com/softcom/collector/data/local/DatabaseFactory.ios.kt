package com.softcom.collector.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSURL

private const val DATABASE_NAME = "collector_v4.db"

@OptIn(ExperimentalForeignApi::class)
actual fun createAppDatabase(): AppDatabase {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )?.path ?: error("Não foi possível localizar o diretório de documentos do iOS.")

    val dbPath = "$documentDirectory/$DATABASE_NAME"

    fun build(): AppDatabase =
        Room.databaseBuilder<AppDatabase>(
            name = dbPath,
            factory = { AppDatabaseConstructor.initialize() },
        )
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .setQueryCoroutineContext(Dispatchers.Default)
            .build()

    var database = build()
    try {
        validateDatabase(database)
    } catch (_: Throwable) {
        runCatching { database.close() }
        deleteDatabaseFiles(dbPath)
        deleteDatabaseFiles("$documentDirectory/collector.db")

        database = build()
        validateDatabase(database)
    }
    return database
}

private fun validateDatabase(database: AppDatabase) {
    runBlocking(Dispatchers.Default) {
        database.collectionDao().findById(-1L)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun deleteDatabaseFiles(dbPath: String) {
    val fileManager = NSFileManager.defaultManager
    sequenceOf(
        dbPath,
        "$dbPath-wal",
        "$dbPath-shm",
        "$dbPath-journal",
    ).forEach { path ->
        runCatching {
            if (fileManager.fileExistsAtPath(path)) {
                fileManager.removeItemAtURL(NSURL.fileURLWithPath(path), null)
            }
        }
    }
}
