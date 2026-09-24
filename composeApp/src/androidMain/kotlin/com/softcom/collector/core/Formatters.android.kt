package com.softcom.collector.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatLocalDateTime(timestampMillis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))
    return formatter.format(Date(timestampMillis))
}
