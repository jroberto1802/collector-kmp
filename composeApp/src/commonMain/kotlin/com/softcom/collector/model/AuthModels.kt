package com.softcom.collector.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class LoginDevice(
    val id: String,
    val merchantId: String,
    val baseUrl: String,
    val clientId: String,
    val clientSecret: String,
    val deviceId: String,
    val deviceName: String,
)

@Serializable
data class LoginResponse(
    val userName: String,
    val merchantCnpj: String,
    val merchantName: String,
    val merchantSoftcomCode: String,
    val device: LoginDevice,
    val token: String? = null,
)

@Serializable
data class IntegrationTokenEnvelope(
    val data: IntegrationTokenData,
)

@Serializable
data class IntegrationTokenData(
    val token: String,
    @SerialName("expires_in") val expiresIn: Long = 0,
    val type: String = "Bearer",
    val scope: String? = null,
)

@Serializable
data class SavedCredentials(
    val email: String,
    val password: String,
)
