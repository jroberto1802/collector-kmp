package com.softcom.collector.data.security

import com.liftric.kvault.KVault

actual fun createSecureStorage(): SecureStorage {
    val vault = KVault(serviceName = "com.softcom.collector")
    return object : SecureStorage {
        override fun getString(key: String): String? = vault.string(forKey = key)

        override fun putString(key: String, value: String) {
            check(vault.set(key = key, stringValue = value)) { "Não foi possível salvar os dados protegidos." }
        }

        override fun remove(key: String) {
            vault.deleteObject(forKey = key)
        }
    }
}
