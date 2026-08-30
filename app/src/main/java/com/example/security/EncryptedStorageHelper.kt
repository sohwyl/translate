package com.example.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedStorageHelper(context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "secure_arbaeen_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            context.getSharedPreferences("secure_arbaeen_fallback_prefs", Context.MODE_PRIVATE)
        }
    }

    fun savePurchaseToken(token: String) {
        sharedPreferences.edit().putString("purchase_token", token).apply()
    }

    fun getPurchaseToken(): String? {
        return sharedPreferences.getString("purchase_token", null)
    }

    fun saveJwtToken(jwt: String) {
        sharedPreferences.edit().putString("jwt_token", jwt).apply()
    }

    fun getJwtToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    fun saveDeviceId(deviceId: String) {
        sharedPreferences.edit().putString("device_id", deviceId).apply()
    }

    fun getDeviceId(): String {
        return sharedPreferences.getString("device_id", null) ?: "dev_${System.currentTimeMillis()}"
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
