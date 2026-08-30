package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Locale
import java.util.concurrent.Executors

/**
 * Singleton Audio Service & Player Helper utilizing Android Media3 (ExoPlayer)
 * for managing 1200 Phrase Audio Assets (600 phrases x Male & Female voices).
 *
 * Key Architecture & Resiliency Features:
 * 1. Singleton ExoPlayer Instance: Single reusable ExoPlayer managed on the Main Looper Thread.
 * 2. Strict Media Clearing: Calls player.stop() and player.clearMediaItems() before loading new audio items.
 * 3. Exact Assets Uri Formatting: Uses Uri format "asset:///$path" for zero-copy Android asset stream playing.
 * 4. Deep Logging (Player.Listener): Catches onPlayerError and logs PlaybackException error codes and causes under AUDIO_DEBUG.
 * 5. Dynamic Asset Verification & TTS Fallback: Verifies file presence in assets before loading, falling back smoothly to TTS if absent.
 * 6. Lifecycle Hygiene: Clean release() method to free ExoPlayer, TTS engine, and background threads safely.
 */
class AudioPlayerHelper private constructor(private val context: Context) : TextToSpeech.OnInitListener {

    private var exoPlayer: ExoPlayer? = null
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    private val mainHandler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    private val _speakingPhraseId = MutableStateFlow<Int?>(null)
    val speakingPhraseId: StateFlow<Int?> = _speakingPhraseId

    // Cached pending info for error fallback
    @Volatile
    private var pendingPhraseId: Int? = null
    @Volatile
    private var pendingText: String = ""
    @Volatile
    private var pendingIsFemale: Boolean = false
    @Volatile
    private var pendingSpeed: Float = 1.0f

    init {
        try {
            Log.d(DEBUG_TAG, "🔊 [AudioPlayerHelper] Initializing TextToSpeech engine and Media3 ExoPlayer...")
            tts = TextToSpeech(context.applicationContext, this)
            // Pre-initialize ExoPlayer on Main Thread
            mainHandler.post {
                getOrCreateExoPlayer()
            }
        } catch (e: Exception) {
            Log.e(DEBUG_TAG, "❌ [AudioPlayerHelper] Initialization exception: ${e.javaClass.simpleName} - ${e.message}", e)
        }
    }

    companion object {
        private const val DEBUG_TAG = "AUDIO_DEBUG"

        @Volatile
        private var INSTANCE: AudioPlayerHelper? = null

        fun getInstance(context: Context): AudioPlayerHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AudioPlayerHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    /**
     * Singleton / Reusable ExoPlayer accessor. MUST be called on Main Thread.
     */
    private fun getOrCreateExoPlayer(): ExoPlayer {
        if (exoPlayer == null) {
            Log.i(DEBUG_TAG, "🏗️ [Media3] Creating new Singleton ExoPlayer instance...")
            val player = ExoPlayer.Builder(context.applicationContext).build()
            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_ENDED -> {
                            Log.i(DEBUG_TAG, "⏹️ [ExoPlayer] Playback STATE_ENDED for Phrase #${pendingPhraseId}")
                            updateSpeakingPhraseId(null)
                        }
                        Player.STATE_READY -> {
                            Log.i(DEBUG_TAG, "▶️ [ExoPlayer] Playback STATE_READY. Playing now.")
                        }
                        Player.STATE_BUFFERING -> {
                            Log.d(DEBUG_TAG, "⏳ [ExoPlayer] Playback STATE_BUFFERING...")
                        }
                        Player.STATE_IDLE -> {
                            Log.d(DEBUG_TAG, "💤 [ExoPlayer] Playback STATE_IDLE")
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e(DEBUG_TAG, "❌ [ExoPlayer Error] CodeName: ${error.errorCodeName} (${error.errorCode}) | Message: ${error.message}", error)
                    updateSpeakingPhraseId(null)

                    val pId = pendingPhraseId
                    val pText = pendingText
                    if (pId != null && pText.isNotBlank()) {
                        Log.w(DEBUG_TAG, "⚠️ [ExoPlayer Error Fallback] Asset failed to play for Phrase #$pId. Triggering TTS fallback...")
                        playTtsFallback(pId, pText, pendingIsFemale, pendingSpeed)
                    }
                }
            })
            exoPlayer = player
        }
        return exoPlayer!!
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                val result = tts?.setLanguage(Locale("ar", "IQ"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale("ar"))
                }
                tts?.setSpeechRate(0.85f)
                tts?.setPitch(1.0f)

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d(DEBUG_TAG, "🗣️ [TTS] Utterance started: $utteranceId")
                    }
                    override fun onDone(utteranceId: String?) {
                        Log.d(DEBUG_TAG, "🗣️ [TTS] Utterance completed: $utteranceId")
                        updateSpeakingPhraseId(null)
                    }
                    override fun onError(utteranceId: String?) {
                        Log.e(DEBUG_TAG, "❌ [TTS Error] Utterance error for: $utteranceId")
                        updateSpeakingPhraseId(null)
                    }
                })
                isTtsInitialized = true
                Log.i(DEBUG_TAG, "✅ [TTS] Iraqi Arabic TextToSpeech Engine Initialized Successfully.")
            } catch (e: Exception) {
                Log.e(DEBUG_TAG, "❌ [TTS] Exception configuring TextToSpeech engine: ${e.javaClass.simpleName} - ${e.message}", e)
            }
        } else {
            Log.e(DEBUG_TAG, "❌ [TTS] Initialization failed with status code: $status")
        }
    }

    private fun updateSpeakingPhraseId(phraseId: Int?) {
        mainHandler.post {
            _speakingPhraseId.value = phraseId
        }
    }

    /**
     * Primary entry point to play phrase audio via Media3 ExoPlayer.
     * Verifies file presence in background, then schedules ExoPlayer playback on Main Thread.
     */
    fun speak(phraseId: Int, text: String, gender: String = UserPreferences.VOICE_MALE, speed: Float = 1.0f) {
        executor.execute {
            try {
                // Save context info for potential error fallbacks
                val isFemale = (gender == UserPreferences.VOICE_FEMALE || gender == "FEMALE" || gender == "زن" || gender.contains("زن"))
                pendingPhraseId = phraseId
                pendingText = text
                pendingIsFemale = isFemale
                pendingSpeed = speed

                val primaryFolder = if (isFemale) "female" else "male"
                val primarySuffix = if (isFemale) "female" else "male"
                val fallbackFolder = if (isFemale) "male" else "female"
                val fallbackSuffix = if (isFemale) "male" else "female"

                val assetCandidates = listOf(
                    "audio/$primaryFolder/phrase_${phraseId}_$primarySuffix.mp3",
                    "audio/$primaryFolder/phrase_$phraseId.mp3",
                    "audio/$fallbackFolder/phrase_${phraseId}_$fallbackSuffix.mp3",
                    "audio/$fallbackFolder/phrase_$phraseId.mp3",
                    "audio/phrase_$phraseId.mp3",
                    "audio/$phraseId.mp3"
                )

                Log.d(DEBUG_TAG, "🎧 [Audio Request] Phrase ID: #$phraseId | Voice Gender: ${if (isFemale) "FEMALE" else "MALE"} | Speed: ${speed}x")

                // Step 1: Verify file presence in assets before loading in ExoPlayer
                val validAssetPath = findValidAssetPath(assetCandidates)

                if (validAssetPath != null) {
                    val assetUriString = "asset:///$validAssetPath"
                    Log.i(DEBUG_TAG, "🎯 [Asset Verified] Found valid audio file at: $assetUriString")

                    // Step 2: Post ExoPlayer commands to Main Thread
                    mainHandler.post {
                        try {
                            // Stop any ongoing playback and clear current media items
                            stopTtsInternal()
                            val player = getOrCreateExoPlayer()
                            player.stop()
                            player.clearMediaItems()

                            // Set speed parameters
                            if (speed != 1.0f) {
                                player.playbackParameters = PlaybackParameters(speed)
                            } else {
                                player.playbackParameters = PlaybackParameters.DEFAULT
                            }

                            val mediaItem = MediaItem.fromUri(Uri.parse(assetUriString))
                            player.setMediaItem(mediaItem)
                            player.prepare()
                            player.playWhenReady = true

                            updateSpeakingPhraseId(phraseId)
                            Log.i(DEBUG_TAG, "▶️ [Media3 ExoPlayer] Playback initiated for Phrase #$phraseId ($assetUriString)")

                        } catch (e: Exception) {
                            Log.e(DEBUG_TAG, "❌ [ExoPlayer Setup Exception] for Phrase #$phraseId: ${e.javaClass.simpleName} - ${e.message}", e)
                            updateSpeakingPhraseId(null)
                            playTtsFallback(phraseId, text, isFemale, speed)
                        }
                    }
                } else {
                    Log.w(DEBUG_TAG, "⚠️ [Asset Missing] No valid asset file found for Phrase #$phraseId across candidates. Initiating TTS fallback...")
                    playTtsFallback(phraseId, text, isFemale, speed)
                }

            } catch (e: Exception) {
                Log.e(DEBUG_TAG, "❌ [Critical Error in speak()] Phrase #$phraseId: ${e.javaClass.simpleName} - ${e.message}", e)
                updateSpeakingPhraseId(null)
            }
        }
    }

    /**
     * Checks if asset candidate path exists in APK assets.
     */
    private fun findValidAssetPath(candidates: List<String>): String? {
        for (path in candidates) {
            try {
                val afd = context.assets.openFd(path)
                afd.close()
                return path
            } catch (e: FileNotFoundException) {
                // Try fallback stream check if openFd throws due to uncompressed assets
                try {
                    val stream = context.assets.open(path)
                    stream.close()
                    return path
                } catch (_: Exception) {}
            } catch (e: IOException) {
                // Continue checking other candidates
            } catch (e: Exception) {
                Log.d(DEBUG_TAG, "Asset check exception for $path: ${e.javaClass.simpleName} - ${e.message}")
            }
        }
        return null
    }

    private fun playTtsFallback(phraseId: Int, text: String, isFemale: Boolean, speed: Float) {
        mainHandler.post {
            try {
                exoPlayer?.stop()
                exoPlayer?.clearMediaItems()
            } catch (_: Exception) {}

            if (isTtsInitialized && tts != null) {
                try {
                    updateSpeakingPhraseId(phraseId)
                    val adjustedRate = (if (isFemale) 0.92f else 0.80f) * speed
                    tts?.setSpeechRate(adjustedRate)
                    tts?.setPitch(if (isFemale) 1.35f else 0.70f)

                    val status = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PHRASE_$phraseId")
                    Log.i(DEBUG_TAG, "🗣️ [TTS Fallback] Speak triggered for Phrase #$phraseId (Status code: $status)")
                } catch (e: Exception) {
                    Log.e(DEBUG_TAG, "❌ [TTS Exception] during speak() for Phrase #$phraseId: ${e.javaClass.simpleName} - ${e.message}", e)
                    updateSpeakingPhraseId(null)
                }
            } else {
                Log.e(DEBUG_TAG, "❌ [TTS Unavailable] Engine not initialized. Cannot play audio for Phrase #$phraseId")
                updateSpeakingPhraseId(null)
            }
        }
    }

    private fun stopTtsInternal() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e(DEBUG_TAG, "Error stopping TTS: ${e.message}")
        }
    }

    /**
     * Stops current ExoPlayer and TTS playback.
     */
    fun stop() {
        mainHandler.post {
            try {
                exoPlayer?.stop()
                exoPlayer?.clearMediaItems()
                stopTtsInternal()
                updateSpeakingPhraseId(null)
                Log.i(DEBUG_TAG, "⏹️ [AudioPlayerHelper] Playback stopped by user request.")
            } catch (e: Exception) {
                Log.e(DEBUG_TAG, "Error in stop(): ${e.javaClass.simpleName} - ${e.message}", e)
            }
        }
    }

    /**
     * Releases system resources (ExoPlayer, TTS, Worker Threads) on Activity/ViewModel destruction.
     */
    fun release() {
        mainHandler.post {
            try {
                exoPlayer?.stop()
                exoPlayer?.clearMediaItems()
                exoPlayer?.release()
                exoPlayer = null
                Log.i(DEBUG_TAG, "✅ [Media3 ExoPlayer] Single ExoPlayer released successfully.")
            } catch (e: Exception) {
                Log.e(DEBUG_TAG, "Error releasing ExoPlayer: ${e.message}", e)
            }

            try {
                tts?.stop()
                tts?.shutdown()
                tts = null
                isTtsInitialized = false
                Log.i(DEBUG_TAG, "✅ [TTS] TextToSpeech engine shut down successfully.")
            } catch (e: Exception) {
                Log.e(DEBUG_TAG, "Error shutting down TTS: ${e.message}", e)
            }

            updateSpeakingPhraseId(null)
        }

        try {
            executor.shutdown()
        } catch (e: Exception) {
            Log.e(DEBUG_TAG, "Error shutting down executor thread: ${e.message}", e)
        }

        synchronized(Companion) {
            if (INSTANCE === this) INSTANCE = null
        }
    }
}
