package com.softcom.collector.core

import com.softcom.collector.model.Collection
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

fun formatCnpj(value: String): String {
    val digits = value.filter(Char::isDigit).take(14)
    if (digits.length != 14) return value
    return "${digits.take(2)}.${digits.substring(2, 5)}.${digits.substring(5, 8)}/${digits.substring(8, 12)}-${digits.takeLast(2)}"
}

/**
 * Aceita números no formato BR ("1.234,56" / "0,00") ou EN ("1234.56" / "0.00").
 */
fun parseLocalizedDouble(raw: String): Double? {
    val value = raw.trim()
    if (value.isEmpty()) return 0.0

    val normalized = if (value.contains(',')) {
        value.replace(".", "").replace(',', '.')
    } else {
        value
    }

    return normalized.toDoubleOrNull()
}

expect fun formatLocalDateTime(timestampMillis: Long): String

fun formatCollectionHeader(collection: Collection): String =
    "Coleta nº ${collection.id} - ${formatLocalDateTime(collection.createdAt)}"

fun formatCollectionItemCount(itemCount: Int): String =
    "$itemCount ${if (itemCount == 1) "produto" else "produtos"}"

fun formatDecimalBr(value: Double, fractionDigits: Int): String {
    val factor = 10.0.pow(fractionDigits)
    var scaled = round(value * factor).toLong()
    val sign = if (scaled < 0) "-" else ""
    scaled = abs(scaled)
    val whole = scaled / factor.toLong()
    val fraction = (scaled % factor.toLong()).toString().padStart(fractionDigits, '0')
    val wholeFormatted = whole.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
    return if (fractionDigits > 0) {
        "$sign$wholeFormatted,$fraction"
    } else {
        "$sign$wholeFormatted"
    }
}

fun formatCollectionQuantity(value: Double): String = formatDecimalBr(value, 3)

fun formatCollectionItemQuantity(value: Double): String {
    val rounded = round(value * 1000.0) / 1000.0
    return if (rounded == rounded.toLong().toDouble()) {
        rounded.toLong().toString()
    } else {
        formatDecimalBr(rounded, 3).trimEnd('0').trimEnd(',')
    }
}

fun formatCurrencyBrl(value: Double): String = "R$ ${formatDecimalBr(value, 2)}"

fun parseCollectionQuantity(raw: String): Double? {
    val normalized = raw.trim().replace(".", "").replace(',', '.')
    if (normalized.isEmpty()) return null
    val parsed = normalized.toDoubleOrNull() ?: return null
    return if (parsed.isFinite()) parsed else null
}
