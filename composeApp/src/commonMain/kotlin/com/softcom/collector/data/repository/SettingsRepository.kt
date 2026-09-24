package com.softcom.collector.data.repository

import com.softcom.collector.core.AppSettings
import com.softcom.collector.core.AppConstants
import com.softcom.collector.core.PriceType
import com.softcom.collector.core.SearchType
import com.softcom.collector.data.security.SecureStorage
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val SETTINGS_KEY = "collector.app-settings"

@Serializable
private data class AppSettingsDto(
    val quickCollection: Boolean = false,
    val lotSerial: Boolean = false,
    val grade: Boolean = false,
    val searchType: String = SearchType.BARCODE.value,
    val priceType: String = PriceType.PURCHASE.value,
)

class SettingsRepository(
    private val secureStorage: SecureStorage,
    private val json: Json,
) {
    fun load(): AppSettings {
        val raw = secureStorage.getString(SETTINGS_KEY) ?: return AppSettings()
        val dto = runCatching { json.decodeFromString<AppSettingsDto>(raw) }.getOrNull()
            ?: return AppSettings()

        var settings = AppSettings(
            quickCollection = dto.quickCollection,
            lotSerial = dto.lotSerial,
            grade = dto.grade,
            searchType = SearchType.fromValue(dto.searchType),
            priceType = PriceType.fromValue(dto.priceType),
        )

        if (settings.lotSerial && !AppConstants.isSoftcomshop) {
            settings = settings.copy(lotSerial = false)
            save(settings)
        }

        return settings
    }

    fun save(settings: AppSettings) {
        val sanitized = if (!AppConstants.isSoftcomshop) {
            settings.copy(lotSerial = false)
        } else {
            settings
        }
        val dto = AppSettingsDto(
            quickCollection = sanitized.quickCollection,
            lotSerial = sanitized.lotSerial,
            grade = sanitized.grade,
            searchType = sanitized.searchType.value,
            priceType = sanitized.priceType.value,
        )
        secureStorage.putString(SETTINGS_KEY, json.encodeToString(dto))
    }
}
