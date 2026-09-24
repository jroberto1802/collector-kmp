package com.softcom.collector.core

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970

actual fun formatLocalDateTime(timestampMillis: Long): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = "dd/MM/yyyy 'às' HH:mm"
        locale = NSLocale(localeIdentifier = "pt_BR")
        timeZone = NSTimeZone.localTimeZone
    }
    val date = NSDate.dateWithTimeIntervalSince1970(timestampMillis / 1000.0)
    return formatter.stringFromDate(date)
}
