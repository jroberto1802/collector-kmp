package com.softcom.collector.core

enum class BackendType {
    SOFTCOMSHOP,
    SOFTSHOP,
}

private val SOFTCOMSHOP_MARKERS = listOf(
    ".meusoftcom.com.br/softauth",
    ".softcomtecnologia.com.br/softauth",
)

fun detectBackendType(baseUrl: String): BackendType {
    val normalized = baseUrl.trim().lowercase()
    val isSoftcomshop = SOFTCOMSHOP_MARKERS.any { marker -> normalized.contains(marker) }
    return if (isSoftcomshop) BackendType.SOFTCOMSHOP else BackendType.SOFTSHOP
}
