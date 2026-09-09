package com.example.ui.onboarding

import android.net.Uri
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

/**
 * Full-screen, muted, seamlessly-looping video background used behind each
 * onboarding step, with a darkening scrim so the foreground text/cards on
 * top always stay clearly readable regardless of the footage underneath.
 *
 * A fresh [ExoPlayer] is created per [videoResId] (i.e. per step) and
 * released when that step is left or the screen is disposed. Playback is
 * automatically paused while the app is backgrounded and resumed when it
 * comes back to the foreground.
 */
@Composable
fun OnboardingVideoBackground(
    @RawRes videoResId: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val exoPlayer = remember(videoResId) {
        ExoPlayer.Builder(context).build().apply {
            val uri = Uri.parse("android.resource://${context.packageName}/$videoResId")
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f
            playWhenReady = true
            prepare()
        }
    }

    // Pause/resume with the host lifecycle so the background video doesn't
    // keep decoding frames (battery/CPU) while the app isn't visible.
    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    // Stretch to fill the screen exactly (no cropping) so the
                    // full frame is always visible on every device/aspect ratio.
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                }
            },
            update = { view -> view.player = exoPlayer }
        )

        // Darkening scrim, deliberately heavier than before across the whole
        // frame (not just edges) so foreground text stays readable no matter
        // how bright/busy the footage underneath is.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.56f),
                        0.45f to Color.Black.copy(alpha = 0.48f),
                        0.7f to Color.Black.copy(alpha = 0.50f),
                        1f to Color.Black.copy(alpha = 0.68f)
                    )
                )
        )

        content()
    }
}
