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
 * onboarding step. Kept vivid/high-quality on purpose — legibility for text
 * and cards on top is handled by their own glass panels (see glassPanel /
 * glassTextBackdrop in OnboardingScreen.kt), not by dimming the footage.
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

        // Only a very faint vignette at the very top/bottom edges — just
        // enough for the top status-bar row and bottom CTA to sit on
        // something, without ever dimming the video itself. The video should
        // read as full-quality, vivid footage; every other piece of UI
        // carries its own glass backdrop for legibility.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.22f),
                        0.12f to Color.Transparent,
                        0.85f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.30f)
                    )
                )
        )

        content()
    }
}
