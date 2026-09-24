package com.softcom.collector.di

import com.softcom.collector.data.local.createAppDatabase
import com.softcom.collector.data.remote.CollectorApi
import com.softcom.collector.data.repository.AuthRepository
import com.softcom.collector.data.repository.CollectionDetailRepository
import com.softcom.collector.data.repository.CollectionRepository
import com.softcom.collector.data.repository.ProductRepository
import com.softcom.collector.data.repository.SettingsRepository
import com.softcom.collector.data.security.createSecureStorage

class AppContainer {
    private val api = CollectorApi()
    private val secureStorage = createSecureStorage()
    private val database = createAppDatabase()

    val authRepository = AuthRepository(api, secureStorage, api.json)
    val settingsRepository = SettingsRepository(secureStorage, api.json)
    val productRepository = ProductRepository(database, api, authRepository)
    val collectionRepository = CollectionRepository(database)
    val collectionDetailRepository = CollectionDetailRepository(database)
}
