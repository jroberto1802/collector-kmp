package com.softcom.collector.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SkuAttribute(
    val id: Long = 0,
    @SerialName("nome") val name: String = "",
    @SerialName("item_id") val itemId: Long = 0,
    @SerialName("item_nome") val itemName: String = "",
)

data class GradeAttributes(
    val size: String = "",
    val color: String = "",
)

private val skuJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

fun normalizeSkuAttributes(raw: String?): List<SkuAttribute> {
    if (raw.isNullOrBlank()) return emptyList()
    return runCatching {
        // Tenta formato PT (API) e EN (persistido)
        val pt = skuJson.decodeFromString<List<SkuAttribute>>(raw)
        if (pt.isNotEmpty()) return@runCatching pt

        @Serializable
        data class EnAttr(
            val id: Long = 0,
            val name: String = "",
            val itemId: Long = 0,
            val itemName: String = "",
        )
        skuJson.decodeFromString<List<EnAttr>>(raw).map {
            SkuAttribute(id = it.id, name = it.name, itemId = it.itemId, itemName = it.itemName)
        }
    }.getOrElse {
        runCatching {
            @Serializable
            data class EnAttr(
                val id: Long = 0,
                val name: String = "",
                val itemId: Long = 0,
                val itemName: String = "",
            )
            skuJson.decodeFromString<List<EnAttr>>(raw).map {
                SkuAttribute(id = it.id, name = it.name, itemId = it.itemId, itemName = it.itemName)
            }
        }.getOrDefault(emptyList())
    }
}

fun extractGradeAttributes(raw: String?): GradeAttributes {
    val list = normalizeSkuAttributes(raw)
    var size = ""
    var color = ""
    for (attribute in list) {
        val name = attribute.name.trim().uppercase()
        val value = attribute.itemName.trim()
        when (name) {
            "TAMANHO", "SIZE" -> size = value
            "COR", "COLOR", "COLOUR" -> color = value
        }
    }
    return GradeAttributes(size = size, color = color)
}
