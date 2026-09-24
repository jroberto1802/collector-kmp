package com.softcom.collector.data.remote

import com.softcom.collector.core.AppConstants
import com.softcom.collector.model.IntegrationTokenData
import com.softcom.collector.model.IntegrationTokenEnvelope
import com.softcom.collector.model.LoginRequest
import com.softcom.collector.model.LoginResponse
import com.softcom.collector.model.ProductsPageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class CollectorApi(
    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        coerceInputValues = true
    },
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) { json(json) }
        install(HttpTimeout) {
            requestTimeoutMillis = AppConstants.SYNCHRONIZATION_TIMEOUT_MS
            connectTimeoutMillis = AppConstants.REQUEST_TIMEOUT_MS
            socketTimeoutMillis = AppConstants.SYNCHRONIZATION_TIMEOUT_MS
        }
        install(Logging) { level = LogLevel.INFO }
        expectSuccess = false
    },
) {
    suspend fun login(email: String, password: String): LoginResponse {
        val response = client.post(AppConstants.AUTHENTICATION_URL) {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(LoginRequest(email, password)))
        }
        val body = response.body<String>()
        ensureSuccess(response.status.value, body, "Não foi possível realizar o login.")
        return decode(body)
    }

    suspend fun requestIntegrationToken(): IntegrationTokenData {
        require(AppConstants.deviceBaseUrl.isNotBlank()) { "A URL da retaguarda não foi informada pelo login." }
        require(AppConstants.deviceClientId.isNotBlank() && AppConstants.deviceClientSecret.isNotBlank()) {
            "As credenciais da retaguarda não foram informadas."
        }

        val response = client.post("${AppConstants.deviceBaseUrl}/authentication/token") {
            accept(ContentType.Application.Json)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("grant_type", "client_credentials")
                        append("client_id", AppConstants.deviceClientId)
                        append("client_secret", AppConstants.deviceClientSecret)
                    },
                ),
            )
        }
        val body = response.body<String>()
        ensureSuccess(response.status.value, body, "Não foi possível autenticar na retaguarda.")
        val element = json.parseToJsonElement(body).unwrapFirst()
        return json.decodeFromJsonElement(IntegrationTokenEnvelope.serializer(), element).data
    }

    suspend fun requestProductsPage(token: String, page: Int): ProductsPageDto {
        val response = client.get("${AppConstants.deviceBaseUrl}/api/v2/produtos/simplificado") {
            accept(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            header("Api-Version", "v2")
            url {
                parameters.append("page", page.toString())
                parameters.append("per_page", AppConstants.PRODUCTS_SYNC_PAGE_SIZE.toString())
            }
        }
        val body = response.body<String>()
        ensureSuccess(response.status.value, body, "Não foi possível baixar a página $page dos produtos.")
        return json.decodeFromJsonElement(ProductsPageDto.serializer(), json.parseToJsonElement(body).unwrapFirst())
    }

    private inline fun <reified T> decode(body: String): T = try {
        json.decodeFromString(body)
    } catch (error: SerializationException) {
        throw IllegalStateException("A API retornou dados em um formato inesperado.", error)
    }

    private fun ensureSuccess(status: Int, body: String, fallback: String) {
        if (status in 200..299) return
        val readable = runCatching {
            val candidate = json.parseToJsonElement(body).unwrapFirst().jsonObject
            listOf("human", "message", "error", "detail")
                .firstNotNullOfOrNull { candidate[it]?.toString()?.trim('"')?.takeIf(String::isNotBlank) }
        }.getOrNull()
        throw IllegalStateException(readable ?: "$fallback HTTP $status.")
    }
}

private fun JsonElement.unwrapFirst(): JsonElement =
    if (this is JsonArray) firstOrNull() ?: JsonObject(emptyMap()) else this
