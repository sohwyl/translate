package com.example.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import com.example.R
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.LalezarFontFamily
import com.example.ui.theme.VazirmatnFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Full-screen animated brand moment shown for ~3 seconds on every cold start
 * of the app, before the real content underneath (already composing) is
 * revealed. Mirrors the "splash artwork + name/tagline card" pattern used by
 * most modern, polished apps.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    // Slow continuous "Ken Burns" zoom on the artwork for a living, cinematic feel.
    val imageScale = remember { Animatable(1f) }
    // Fade-in of the artwork itself.
    val imageAlpha = remember { Animatable(0f) }
    // Caption card: fades in and settles upward slightly after the artwork appears.
    val captionAlpha = remember { Animatable(0f) }
    val captionOffsetY = remember { Animatable(18f) }
    // Whole-screen fade-out used to cross-fade into the app content underneath.
    val exitAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch {
            imageAlpha.animateTo(1f, animationSpec = tween(650, easing = FastOutSlowInEasing))
        }
        launch {
            imageScale.animateTo(1.09f, animationSpec = tween(3200, easing = LinearOutSlowInEasing))
        }

        delay(500)

        launch {
            captionAlpha.animateTo(1f, animationSpec = tween(550, easing = FastOutSlowInEasing))
        }
        launch {
            captionOffsetY.animateTo(0f, animationSpec = tween(550, easing = FastOutSlowInEasing))
        }

        // Total time the splash stays fully visible before it starts dismissing.
        delay(2500)

        exitAlpha.animateTo(0f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = exitAlpha.value }
    ) {
        Image(
            painter = painterResource(R.drawable.img_splash_maseer_arbaeen),
            contentDescription = "مسیر عشق؛ نجف تا کربلاء",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = imageAlpha.value
                    scaleX = imageScale.value
                    scaleY = imageScale.value
                }
        )

        // Bottom scrim so the caption stays legible over any part of the artwork.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.55f to Color.Transparent,
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
