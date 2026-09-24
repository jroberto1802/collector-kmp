package com.softcom.collector.platform

import platform.Foundation.NSDate
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1_000.0).toLong()

actual fun openExternalUrl(url: String) {
    NSURL.URLWithString(url)?.let { UIApplication.sharedApplication.openURL(it) }
}
