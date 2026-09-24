package com.softcom.collector.data.repository

import com.softcom.collector.core.AppConstants
import com.softcom.collector.data.local.AppDatabase
import com.softcom.collector.data.local.toEntity
import com.softcom.collector.data.local.toListItem
import com.softcom.collector.data.remote.CollectorApi
import com.softcom.collector.model.ProductPage
import com.softcom.collector.model.ProductSyncProgress
import com.softcom.collector.platform.currentTimeMillis
import kotlin.math.ceil

class ProductRepository(
    private val database: AppDatabase,
    private val api: CollectorApi,
    private val authRepository: AuthRepository,
) {
    suspend fun synchronize(onProgress: (ProductSyncProgress) -> Unit): Int {
        val dao = database.productDao()
        dao.clear()

        val tokenData = api.requestIntegrationToken()
        AppConstants.setProductsToken(tokenData.token, tokenData.expiresIn, currentTimeMillis())
        authRepository.persistProductsToken(tokenData.token)

        var pageNumber = 1
        var lastPage = 1
        var downloadedProducts = 0
        val syncedAt = currentTimeMillis()

        do {
            val page = api.requestProductsPage(tokenData.token, pageNumber)
            val entities = page.data.map { it.toEntity(api.json, syncedAt) }
            dao.insertAll(entities)
            downloadedProducts += entities.size
            lastPage = page.lastPage.coerceAtLeast(1)
            onProgress(ProductSyncProgress(page.currentPage, lastPage, downloadedProducts))
            pageNumber = page.currentPage + 1
        } while (pageNumber <= lastPage)

        return downloadedProducts
    }

    suspend fun getLocalPage(page: Int, pageSize: Int, query: String): ProductPage {
        val normalizedQuery = query.trim()
        val total = database.productDao().count(normalizedQuery)
        val totalPages = ceil(total.toDouble() / pageSize).toInt().coerceAtLeast(1)
        val safePage = page.coerceIn(1, totalPages)
        val items = database.productDao()
            .getPage(normalizedQuery, pageSize, (safePage - 1) * pageSize)
            .map { it.toListItem() }

        return ProductPage(items, safePage, pageSize, total, totalPages)
    }
}
