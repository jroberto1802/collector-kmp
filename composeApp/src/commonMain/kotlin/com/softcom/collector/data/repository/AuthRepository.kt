package com.softcom.collector.data.repository

import com.softcom.collector.core.AppConstants
import com.softcom.collector.data.remote.CollectorApi
import com.softcom.collector.data.security.SecureStorage
import com.softcom.collector.model.LoginResponse
import com.softcom.collector.model.SavedCredentials
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AuthRepository(
    private val api: CollectorApi,
    private val secureStorage: SecureStorage,
    private val json: Json,
) {
    suspend fun login(email: String, password: String, rememberPassword: Boolean): LoginResponse {
        val loginData = api.login(email.trim(), password)
        AppConstants.setLoginData(loginData)
        secureStorage.putString(LOGIN_DATA_KEY, json.encodeToString(loginData))

        if (rememberPassword) {
            secureStorage.putString(CREDENTIALS_KEY, json.encodeToString(SavedCredentials(email.trim(), password)))
        } else {
            secureStorage.remove(CREDENTIALS_KEY)
        }
        return loginData
    }

    fun loadSavedCredentials(): SavedCredentials? = secureStorage.getString(CREDENTIALS_KEY)?.let {
        runCatching { json.decodeFromString<SavedCredentials>(it) }.getOrNull()
    }

    fun restoreLastLoginData() {
        secureStorage.getString(LOGIN_DATA_KEY)?.let {
            runCatching { json.decodeFromString<LoginResponse>(it) }
                .onSuccess(AppConstants::setLoginData)
                .onFailure { AppConstants.clearSession() }
        }
    }

    fun logout() {
        secureStorage.remove(LOGIN_DATA_KEY)
        secureStorage.remove(PRODUCT_TOKEN_KEY)
        AppConstants.clearSession()
    }

    fun persistProductsToken(token: String) {
        secureStorage.putString(PRODUCT_TOKEN_KEY, token)
    }

    private companion object {
        const val LOGIN_DATA_KEY = "login_data"
        const val CREDENTIALS_KEY = "saved_credentials"
        const val PRODUCT_TOKEN_KEY = "products_access_token"
    }
}
