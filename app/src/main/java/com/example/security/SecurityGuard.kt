package com.example.security

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Debug
import com.example.data.UserPreferences
import java.io.File

object SecurityGuard {

    const val EXPECTED_SIGNATURE_HASH = "8F3A2B1C4D5E6F7A8B9C0D1E2F3A4B5C6D7E8F9A"

    fun performSecurityCheck(context: Context, userPreferences: UserPreferences): Boolean {
        var isTampered = false

        if (checkRoot()) {
            isTampered = true
        }

        if (checkFrida()) {
            isTampered = true
        }

        if (checkDebugger(context)) {
            isTampered = true
        }

        if (isTampered) {
            onTamperDetected(userPreferences)
            return false
        }

        return true
    }

    fun checkRoot(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )

        for (path in paths) {
            if (File(path).exists()) return true
        }

        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) return true

        return false
    }

    fun checkFrida(): Boolean {
        return try {
            val mapsFile = File("/proc/self/maps")
            if (mapsFile.exists()) {
                val content = mapsFile.readText()
                content.contains("frida") || content.contains("gadget") || content.contains("linjector")
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun checkDebugger(context: Context): Boolean {
        val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger() || isDebuggable
    }

    fun checkEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT)
    }

    private fun onTamperDetected(userPreferences: UserPreferences) {
        // Silent reaction: Quietly revoke VIP status without displaying obvious developer diagnostic messages to the attacker
        userPreferences.setGoldVersionActivated(false)
    }
}
