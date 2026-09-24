package com.softcom.collector.core

import com.softcom.collector.model.LoginResponse

object AppConstants {
    const val APP_NAME = "Collector"
    const val APP_VERSION = "3.0.0"
    const val AUTHENTICATION_URL = "https://collector-func.azurewebsites.net/api/login"
    const val SOFTCOM_URL = "https://www.softcomtecnologia.com.br"
    const val REQUEST_TIMEOUT_MS = 15_000L
    const val SYNCHRONIZATION_TIMEOUT_MS = 120_000L
    const val PRODUCTS_SYNC_PAGE_SIZE = 500

    var userName: String = ""
        private set
    var merchantCnpj: String = ""
        private set
    var merchantName: String = ""
        private set
    var merchantSoftcomCode: String = ""
        private set
    var deviceId: String = ""
        private set
    var deviceMerchantId: String = ""
        private set
    var deviceBaseUrl: String = ""
        private set
    var deviceClientId: String = ""
        private set
    var deviceClientSecret: String = ""
        private set
    var deviceDeviceId: String = ""
        private set
    var deviceName: String = ""
        private set
    var authenticationToken: String = ""
        private set
    var productsAccessToken: String = ""
        private set
    var productsTokenExpiresAt: Long = 0L
        private set
    var backendType: BackendType = BackendType.SOFTSHOP
        private set

    val isSoftcomshop: Boolean
        get() = backendType == BackendType.SOFTCOMSHOP

    fun setLoginData(loginData: LoginResponse) {
        userName = loginData.userName
        merchantCnpj = loginData.merchantCnpj
        merchantName = loginData.merchantName
        merchantSoftcomCode = loginData.merchantSoftcomCode
        deviceId = loginData.device.id
        deviceMerchantId = loginData.device.merchantId
        deviceBaseUrl = loginData.device.baseUrl.trim().trimEnd('/')
        deviceClientId = loginData.device.clientId
        deviceClientSecret = loginData.device.clientSecret
        deviceDeviceId = loginData.device.deviceId
        deviceName = loginData.device.deviceName
        authenticationToken = loginData.token.orEmpty()
        productsAccessToken = ""
        productsTokenExpiresAt = 0L
        backendType = detectBackendType(deviceBaseUrl)
    }

    fun setProductsToken(token: String, expiresInSeconds: Long, nowMillis: Long) {
        productsAccessToken = token
        productsTokenExpiresAt = nowMillis + (expiresInSeconds - 60L).coerceAtLeast(0L) * 1_000L
    }

    fun clearSession() {
        userName = ""
        merchantCnpj = ""
        merchantName = ""
        merchantSoftcomCode = ""
        deviceId = ""
        deviceMerchantId = ""
        deviceBaseUrl = ""
        deviceClientId = ""
        deviceClientSecret = ""
        deviceDeviceId = ""
        deviceName = ""
        authenticationToken = ""
        productsAccessToken = ""
        productsTokenExpiresAt = 0L
        backendType = BackendType.SOFTSHOP
    }
}
