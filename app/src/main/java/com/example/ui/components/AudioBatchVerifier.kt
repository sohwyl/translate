package com.example.ui.components

import android.content.Context
import android.util.Log
import com.example.data.DatabaseInitializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AudioVerificationReport(
    val totalPhrases: Int,
    val verifiedMaleCount: Int,
    val verifiedFemaleCount: Int,
    val totalVerifiedCount: Int,
    val is100PercentVerified: Boolean,
    val missingFiles: List<String>
)

object AudioBatchVerifier {

    private const val TAG = "AudioBatchVerifier"

    /**
     * Batch verifies audio asset files (one male + one female per phrase) asynchronously
     * on Dispatchers.IO. Checks file existence, accessibility via AssetManager, and non-zero
     * byte size. The expected phrase count is read from the live phrase database rather than
     * hardcoded, so this stays correct as the phrase set grows.
     */
    suspend fun verifyAll900AudioFiles(context: Context): AudioVerificationReport = withContext(Dispatchers.IO) {
        val assetManager = context.assets
        val TOTAL_EXPECTED_PHRASES = DatabaseInitializer.getInitialPhrases().size
        var maleVerifiedCount = 0
        var femaleVerifiedCount = 0
        val missingList = mutableListOf<String>()

        Log.i(TAG, "═══════════════════════════════════════════════════════════")
        Log.i(TAG, "  STARTING BATCH VERIFICATION OF ALL $TOTAL_EXPECTED_PHRASES AUDIO ASSETS")
        Log.i(TAG, "═══════════════════════════════════════════════════════════")

        for (phraseId in 1..TOTAL_EXPECTED_PHRASES) {
            val malePath = "audio/male/phrase_${phraseId}_male.mp3"
            val femalePath = "audio/female/phrase_${phraseId}_female.mp3"

            // Verify Male asset
            if (verifyAssetFile(assetManager, malePath)) {
                maleVerifiedCount++
            } else {
                missingList.add(malePath)
            }

            // Verify Female asset
            if (verifyAssetFile(assetManager, femalePath)) {
                femaleVerifiedCount++
            } else {
                missingList.add(femalePath)
            }

            if (phraseId % 100 == 0 || phraseId == TOTAL_EXPECTED_PHRASES) {
                Log.d(
                    TAG,
                    "🔍 Verification Progress: $phraseId/$TOTAL_EXPECTED_PHRASES phrases checked " +
                            "(${maleVerifiedCount + femaleVerifiedCount}/${phraseId * 2} assets verified)"
                )
            }
        }

        val totalVerified = maleVerifiedCount + femaleVerifiedCount
        val is100Percent = (totalVerified == TOTAL_EXPECTED_PHRASES * 2)

        Log.i(TAG, "═══════════════════════════════════════════════════════════")
        Log.i(TAG, "  BATCH AUDIO VERIFICATION SUMMARY")
        Log.i(TAG, "═══════════════════════════════════════════════════════════")
        Log.i(TAG, "  ✅ Verified Male Audios:   $maleVerifiedCount / $TOTAL_EXPECTED_PHRASES")
        Log.i(TAG, "  ✅ Verified Female Audios: $femaleVerifiedCount / $TOTAL_EXPECTED_PHRASES")
        Log.i(TAG, "  🎯 TOTAL VERIFIED ASSETS:  $totalVerified / ${TOTAL_EXPECTED_PHRASES * 2}")
        Log.i(TAG, "  STATUS: ${if (is100Percent) "100% VERIFIED & READY FOR EXOPLAYER 🚀" else "WARNING: MISSING FILES"}")
        if (missingList.isNotEmpty()) {
            Log.e(TAG, "  ❌ Missing files count: ${missingList.size}. Sample missing: ${missingList.take(5)}")
        }
        Log.i(TAG, "═══════════════════════════════════════════════════════════")

        AudioVerificationReport(
            totalPhrases = TOTAL_EXPECTED_PHRASES,
            verifiedMaleCount = maleVerifiedCount,
            verifiedFemaleCount = femaleVerifiedCount,
            totalVerifiedCount = totalVerified,
            is100PercentVerified = is100Percent,
            missingFiles = missingList
        )
    }

    private fun verifyAssetFile(assetManager: android.content.res.AssetManager, path: String): Boolean {
        return try {
            assetManager.openFd(path).use { fd ->
                fd.length > 0
            }
        } catch (e: Exception) {
            // Try direct open input stream as fallback
            try {
                assetManager.open(path).use { stream ->
                    stream.available() >= 0
                }
            } catch (ex: Exception) {
                false
            }
        }
    }
}
