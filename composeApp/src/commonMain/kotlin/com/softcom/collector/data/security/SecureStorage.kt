package com.softcom.collector.data.security

interface SecureStorage {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
    fun remove(key: String)
}

expect fun createSecureStorage(): SecureStorage
