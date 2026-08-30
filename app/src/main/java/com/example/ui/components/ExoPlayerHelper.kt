package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.exoplayer.ExoPlayer

class ExoPlayerHelper private constructor(context: Context) {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context.applicationContext).build()
    private var activeListener: Player.Listener? = null

    companion object {
        private const val TAG = "ExoPlayerHelper"

        @Volatile
        private var INSTANCE: ExoPlayerHelper? = null

        fun getInstance(context: Context): ExoPlayerHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ExoPlayerHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun playAsset(
        assetPath: String,
        speed: Float = 1.0f,
        onStart: () -> Unit = {},
        onComplete: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            stop()

            exoPlayer.playbackParameters = PlaybackParameters(speed)

            val uri = Uri.parse("asset:///$assetPath")
            Log.d(TAG, "ExoPlayer resolving asset URI: $uri at ${speed}x")

            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer.setMediaItem(mediaItem)

            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            Log.i(TAG, "ExoPlayer STATE_READY: Asset URI $assetPath loaded successfully.")
                            onStart()
                        }
                        Player.STATE_ENDED -> {
                            Log.i(TAG, "ExoPlayer STATE_ENDED: Playback finished for $assetPath.")
                            onComplete()
                            removeCurrentListener()
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.w(TAG, "ExoPlayer Playback Error on $assetPath: ${error.message}")
                    onError(error)
                    removeCurrentListener()
                }
            }

            activeListener = listener
            exoPlayer.addListener(listener)
            exoPlayer.prepare()
            exoPlayer.play()
        } catch (e: Exception) {
            Log.e(TAG, "Exception preparing ExoPlayer for $assetPath: ${e.message}")
            onError(e)
        }
    }

    private fun removeCurrentListener() {
        activeListener?.let {
            exoPlayer.removeListener(it)
            activeListener = null
        }
    }

    fun stop() {
        try {
            removeCurrentListener()
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping ExoPlayer: ${e.message}")
        }
    }

    fun release() {
        try {
            exoPlayer.release()
            Log.i(TAG, "ExoPlayer Singleton released.")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing ExoPlayer: ${e.message}")
        }
    }
}
