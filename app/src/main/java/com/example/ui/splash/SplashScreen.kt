package com.example.ui.splash

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.LalezarFontFamily
import com.example.ui.theme.VazirmatnFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A gentle "ease-out" reveal curve (fast start, long soft settle) — used for
 * the zoom-out / pan-in so the shot feels like a camera arriving and settling
 * on the scene, rather than a flat, static zoom.
 */
private val RevealEasing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

/**
 * Full-screen animated brand moment shown for ~5 seconds on every cold start
 * of the app, before the real content underneath is revealed (the caller is
 * responsible for not composing that content until [onFinished] fires — see
 * MainActivity's Crossfade — so there is never a frame where anything but
 * this splash is visible).
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    // Reveal: starts zoomed in + off-center on the shrine, then zooms out and
    // settles into the full framed shot — a proper "arrival" instead of a
    // static image that's already fully visible from frame one.
    val revealScale = remember { Animatable(1.42f) }
    val revealOffsetXDp = remember { Animatable(48f) }
    val revealOffsetYDp = remember { Animatable(-70f) }

    // Quick opacity fade so the very first frame isn't a hard pop-in.
    val imageAlpha = remember { Animatable(0f) }

    // Caption card: fades in and settles upward slightly once the scene has revealed itself.
    val captionAlpha = remember { Animatable(0f) }
    val captionOffsetY = remember { Animatable(18f) }

    LaunchedEffect(Unit) {
        launch { imageAlpha.animateTo(1f, animationSpec = tween(300, easing = LinearEasing)) }
        launch { revealScale.animateTo(1f, animationSpec = tween(3000, easing = RevealEasing)) }
        launch { revealOffsetXDp.animateTo(0f, animationSpec = tween(3000, easing = RevealEasing)) }
        launch { revealOffsetYDp.animateTo(0f, animationSpec = tween(3000, easing = RevealEasing)) }

        delay(1200)
        launch { captionAlpha.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing)) }
        launch { captionOffsetY.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing)) }

        delay(1800) // now ~3000ms elapsed: the reveal has fully settled

        // Ambient continued life while the caption holds — a slow, barely
        // perceptible push-in, so the frame never feels frozen.
        revealScale.animateTo(1.045f, animationSpec = tween(1500, easing = LinearOutSlowInEasing))

        // ~4500ms elapsed; the caller's own crossfade covers the final ~0.5s.
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Opaque backdrop from frame zero so nothing underneath can ever
            // show through while the artwork itself is still fading in.
            .background(Color(0xFF201A14))
    ) {
        Image(
            painter = painterResource(R.drawable.img_splash_maseer_arbaeen),
            contentDescription = "مسیر عشق؛ نجف تا کربلاء",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = imageAlpha.value
                    scaleX = revealScale.value
                    scaleY = revealScale.value
                    translationX = revealOffsetXDp.value.dp.toPx()
                    translationY = revealOffsetYDp.value.dp.toPx()
                }
        )

        // Darkening scrim across the whole frame so the caption (and the
        // artwork's own embedded text) stays legible, a bit heavier at the bottom.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.22f),
                        0.55f to Color.Black.copy(alpha = 0.12f),
                        1f to Color.Black.copy(alpha = 0.72f)
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 40.dp)
                .graphicsLayer {
                    alpha = captionAlpha.value
                    translationY = captionOffsetY.value.dp.toPx()
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "مترجم عربی عراقی",
                fontFamily = LalezarFontFamily,
                fontSize = 26.sp,
                color = GoldenAmber,
                textAlign = TextAlign.Center
            )
            Box(modifier = Modifier.padding(top = 6.dp)) {
                Text(
                    text = "همراه شما، قدم‌به‌قدم در مسیر عشق تا کربلا",
                    fontFamily = VazirmatnFontFamily,
                    fontSize = 13.5.sp,
                    color = Color(0xFFF3EFE4),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
