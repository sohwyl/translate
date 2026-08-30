package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 3D Physical Narrative Envelope Letter Dialog (بخش توسعه‌دهنده)
 * Follows physical origami paper folding principles and narrative stages:
 * Stage 1: Golden Wax Seal shimmer & release
 * Stage 2: 3D perspective flap unfolding on X-axis (0° -> -180°)
 * Stage 3: Aged parchment letter extraction & smooth slide-up
 * Stage 4: Calligraphy fade-in with spiritual serenity
 */
@Composable
fun DeveloperLetter3DDialog(
    isDarkTheme: Boolean,
    onDismiss: () -> Unit
) {
    val density = LocalDensity.current

    // Narrative Stage Progress: 0f (Sealed) -> 1f (Fully Unfolded)
    val sealBreak = remember { Animatable(0f) }
    val flapAngle = remember { Animatable(0f) } // 0 deg to -180 deg
    val letterSlide = remember { Animatable(0f) } // 0 to 1
    val contentFade = remember { Animatable(0f) } // 0 to 1

    LaunchedEffect(Unit) {
        // Stage 1: Wax Seal Shimmer & break (300ms)
        sealBreak.animateTo(
            targetValue = 1f,
            animationSpec = tween(350, easing = SpiritualMotion.SereneEaseInOut)
        )
        delay(50)

        // Stage 2: Flap 3D rotation backward (600ms)
        flapAngle.animateTo(
            targetValue = -180f,
            animationSpec = tween(650, easing = SpiritualMotion.SereneEaseInOut)
        )

        // Stage 3: Letter slides up out of envelope pocket (700ms)
        letterSlide.animateTo(
            targetValue = 1f,
            animationSpec = tween(700, easing = SpiritualMotion.SereneEaseInOut)
        )

        // Stage 4: Content Calligraphy reveals
        contentFade.animateTo(
            targetValue = 1f,
            animationSpec = tween(450, easing = SpiritualMotion.SereneDecelerate)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.78f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {},
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Envelope Container with 3D layers
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // 1. Envelope Back Shell
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDarkTheme) Color(0xFF0F261C) else Color(0xFFC7BCA7))
                            .border(1.5.dp, GoldenAmber.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    )

                    // 2. Extracted Parchment Letter Card (Slides Up)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.96f)
                            .graphicsLayer {
                                translationY = (1f - letterSlide.value) * 80.dp.toPx()
                                scaleX = 0.94f + letterSlide.value * 0.06f
                                scaleY = 0.94f + letterSlide.value * 0.06f
                                alpha = 0.3f + letterSlide.value * 0.7f
                            }
                            .shadow(24.dp, RoundedCornerShape(22.dp))
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                if (isDarkTheme) {
                                    Brush.verticalGradient(listOf(Color(0xFF143024), Color(0xFF0C2018)))
                                } else {
                                    Brush.verticalGradient(listOf(Color(0xFFFFFDF8), Color(0xFFF9F5EC)))
                                }
                            )
                            .border(1.5.dp, GoldenAmber, RoundedCornerShape(22.dp))
                            .padding(22.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer { alpha = contentFade.value }
                        ) {
                            // Top Ornament
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Canvas(modifier = Modifier.size(width = 40.dp, height = 12.dp)) {
                                    drawLine(GoldenAmber, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), strokeWidth = 1.5.dp.toPx())
                                    drawCircle(GoldenAmber, 3.dp.toPx(), Offset(size.width, size.height / 2))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "۞ نامه خادم به زائران ۞",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenAmber,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Canvas(modifier = Modifier.size(width = 40.dp, height = 12.dp)) {
                                    drawLine(GoldenAmber, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), strokeWidth = 1.5.dp.toPx())
                                    drawCircle(GoldenAmber, 3.dp.toPx(), Offset(0f, size.height / 2))
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            HorizontalDivider(
                                color = GoldenAmber.copy(alpha = 0.35f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Sacred Letter Calligraphy Text
                            Text(
                                text = "بسم الله الرحمن الرحیم\n\nزائر گرامی و همسفر عزیز طریق الحسین،\nالتماس دعا در مسیر پیاده‌روی نورانی نجف تا کربلا.\n\nاین نرم‌افزار به نیت خدمت خالصانه و پیوند دلهای زائران ایرانی و برادران خادم عراقی ساخته شده است.\n\nامید است در ثواب قدم‌های پاکتان در این طریق بهشتی، این خادم کوچک را هم از دعای خیر محروم نفرمایید.\n\n«رَحِمَ اللهُ والِدِیکَ و زیارة مقبولة»\n\nخادم زائران - توسعه‌دهنده برنامه",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 13.5.sp,
                                lineHeight = 24.sp,
                                color = if (isDarkTheme) TextPrimaryDark else Color(0xFF2C2518),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Close Button with Sacred Gold styling
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = DarkEmeraldBg,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "قبول حق • التماس دعا",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkEmeraldBg
                                )
                            }
                        }
                    }

                    // 3. Top Flap 3D Origami Folding Layer
                    if (flapAngle.value > -175f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .graphicsLayer {
                                    rotationX = flapAngle.value
                                    cameraDistance = 16f * density.density
                                    transformOrigin = TransformOrigin(0.5f, 0f)
                                }
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val flapPath = Path().apply {
                                    moveTo(0f, 0f)
                                    lineTo(w, 0f)
                                    lineTo(w * 0.5f, h)
                                    close()
                                }
                                drawPath(
                                    flapPath,
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            if (isDarkTheme) Color(0xFF163829) else Color(0xFFD6CABA),
                                            if (isDarkTheme) Color(0xFF0F261C) else Color(0xFFC7BCA7)
                                        )
                                    )
                                )
                                drawPath(
                                    flapPath,
                                    color = GoldenAmber.copy(alpha = 0.8f),
                                    style = Stroke(width = 1.5.dp.toPx())
                                )
                            }

                            // Golden Wax Seal on Flap Tip
                            if (sealBreak.value < 0.9f) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .offset(y = 14.dp)
                                        .size(34.dp)
                                        .graphicsLayer {
                                            alpha = 1f - sealBreak.value
                                            scaleX = 1f + sealBreak.value * 0.3f
                                            scaleY = 1f + sealBreak.value * 0.3f
                                        }
                                        .clip(CircleShape)
                                        .background(Color(0xFF8B1E0F))
                                        .border(1.5.dp, GoldenAmber, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MarkEmailRead,
                                        contentDescription = null,
                                        tint = GoldenAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
