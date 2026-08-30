package com.example.ui.components

import android.content.Context
import android.util.Log
import com.example.data.PhraseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

data class SyncResult(
    val totalChecked: Int,
    val validCount: Int,
    val missingCount: Int,
    val isComplete: Boolean,
    val logMessages: List<String>
)

object AudioSyncManager {

    private const val TAG = "AudioSyncManager"
    private const val TOTAL_PHRASES = 600

    private val _syncState = MutableStateFlow<SyncResult?>(null)
    val syncState: StateFlow<SyncResult?> = _syncState

    /**
     * Verifies the presence and integrity of all 1200 audio files (600 male + 600 female)
     * against the application's phrase manifest.
     */
    suspend fun verifyAndSyncAudioFiles(
        context: Context,
        phrases: List<PhraseEntity> = emptyList()
    ): SyncResult = withContext(Dispatchers.IO) {
        val assetManager = context.assets
        val logs = mutableListOf<String>()
        var validFiles = 0
        var missingFiles = 0

        Log.i(TAG, "🔄 Starting AudioSyncManager audit for 900 files...")

        val phraseCount = if (phrases.isNotEmpty()) phrases.size else TOTAL_PHRASES

        for (i in 1..phraseCount) {
            val phraseId = if (phrases.isNotEmpty()) phrases[i - 1].id else i
            val malePath = "audio/male/phrase_${phraseId}_male.mp3"
            val femalePath = "audio/female/phrase_${phraseId}_female.mp3"

            // Check Male Asset
            if (isAssetIntact(assetManager, malePath)) {
                validFiles++
            } else {
                missingFiles++
                logs.add("Missing: $malePath")
                Log.w(TAG, "⚠️ Missing audio asset: $malePath")
            }

            // Check Female Asset
            if (isAssetIntact(assetManager, femalePath)) {
                validFiles++
            } else {
                missingFiles++
                logs.add("Missing: $femalePath")
                Log.w(TAG, "⚠️ Missing audio asset: $femalePath")
            }
        }

        val totalExpected = phraseCount * 2
        val isComplete = (validFiles == totalExpected)

        val result = SyncResult(
            totalChecked = totalExpected,
            validCount = validFiles,
            missingCount = missingFiles,
            isComplete = isComplete,
            logMessages = logs
        )

        _syncState.value = result

        Log.i(TAG, "✅ AudioSyncManager Audit Finished: $validFiles/$totalExpected verified intact.")
        result
    }

    private fun isAssetIntact(assetManager: android.content.res.AssetManager, path: String): Boolean {
        return try {
            assetManager.openFd(path).use { fd ->
                fd.length > 0
            }
        } catch (e: Exception) {
            try {
                assetManager.open(path).use { stream ->
                    stream.available() > 0
                }
            } catch (ex: Exception) {
                false
            }
        }
    }
}
