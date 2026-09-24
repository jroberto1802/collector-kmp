package com.softcom.collector.core

/** Aplica máscara DD/MM/AAAA enquanto o usuário digita. */
fun maskBrDateInput(raw: String): String {
    val digits = raw.filter { it.isDigit() }.take(8)
    return when {
        digits.length <= 2 -> digits
        digits.length <= 4 -> "${digits.take(2)}/${digits.drop(2)}"
        else -> "${digits.take(2)}/${digits.drop(2).take(2)}/${digits.drop(4)}"
    }
}

/** Converte DD/MM/AAAA → AAAA-MM-DD. Retorna null se inválida. */
fun parseBrDateToIso(raw: String): String? {
    val match = Regex("""^(\d{2})/(\d{2})/(\d{4})$""").matchEntire(raw.trim()) ?: return null
    val day = match.groupValues[1].toInt()
    val month = match.groupValues[2].toInt()
    val year = match.groupValues[3].toInt()
    if (month !in 1..12 || day !in 1..31 || year < 1900) return null

    // Validação simples de calendário
    val daysInMonth = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> return null
    }
    if (day > daysInMonth) return null

    return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
}

/** Converte AAAA-MM-DD → DD/MM/AAAA. */
fun formatIsoToBrDate(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    val match = Regex("""^(\d{4})-(\d{2})-(\d{2})$""").matchEntire(iso.trim()) ?: return ""
    return "${match.groupValues[3]}/${match.groupValues[2]}/${match.groupValues[1]}"
}

fun isManufacturingAfterExpiration(manufacturingIso: String, expirationIso: String): Boolean =
    manufacturingIso > expirationIso
