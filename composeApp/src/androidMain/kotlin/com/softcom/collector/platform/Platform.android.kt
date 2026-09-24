package com.softcom.collector.platform

import android.content.Intent
import android.net.Uri

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun openExternalUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    AndroidContext.applicationContext.startActivity(intent)
}
