package com.softcom.collector.data.security

import com.liftric.kvault.KVault
import com.softcom.collector.platform.AndroidContext

actual fun createSecureStorage(): SecureStorage {
    val vault = KVault(AndroidContext.applicationContext, "collector_secure_storage")
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
