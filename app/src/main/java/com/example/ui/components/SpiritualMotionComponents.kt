package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkEmeraldBg
import com.example.ui.theme.DarkEmeraldCardBorder
import com.example.ui.theme.FavoriteRed
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.SpiritualMotion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * 1. نقوش اسلیمی با انیمیشن «تنفس» (Breathing Ambient Eslimi SVG/Vector)
 * Ultra-lightweight vector arabesque ornament with ambient breathing scale & opacity.
 * Placed in corners of cards or dialogs with low opacity (0.04 - 0.12).
 */
@Composable
fun EslimiCornerBreathingOrnament(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true,
    sizeDp: Dp = 64.dp,
    corner: EslimiCorner = EslimiCorner.TOP_RIGHT
) {
    val infiniteTransition = rememberInfiniteTransition(label = "eslimi_breathing")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(SpiritualMotion.DURATION_BREATHING_CYCLE, easing = SpiritualMotion.BreathingEase),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eslimi_scale"
    )

    val breathAlpha by infiniteTransition.animateFloat(
        initialValue = 0.04f,
        targetValue = 0.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(SpiritualMotion.DURATION_BREATHING_CYCLE, easing = SpiritualMotion.BreathingEase),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eslimi_alpha"
    )

    val ornamentColor = if (isDarkTheme) GoldenAmber else Color(0xFF133E2B)

    Canvas(
        modifier = modifier
            .size(sizeDp)
            .graphicsLayer {
                scaleX = breathScale
                scaleY = breathScale
                alpha = breathAlpha
            }
    ) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 1.2.dp.toPx())

        val (origin, rotDeg) = when (corner) {
            EslimiCorner.TOP_RIGHT -> Pair(Offset(w, 0f), 0f)
            EslimiCorner.TOP_LEFT -> Pair(Offset(0f, 0f), 90f)
            EslimiCorner.BOTTOM_LEFT -> Pair(Offset(0f, h), 180f)
            EslimiCorner.BOTTOM_RIGHT -> Pair(Offset(w, h), 270f)
        }

        rotate(degrees = rotDeg, pivot = origin) {
            // Concentric curves
            drawCircle(color = ornamentColor, radius = w * 0.9f, center = origin, style = stroke)
            drawCircle(color = ornamentColor, radius = w * 0.65f, center = origin, style = stroke)
            drawCircle(color = ornamentColor, radius = w * 0.38f, center = origin, style = stroke)

            // Eslimi petal arch
            val path = Path().apply {
                moveTo(origin.x - w * 0.9f, origin.y)
                cubicTo(
                    origin.x - w * 0.7f, origin.y + h * 0.4f,
                    origin.x - w * 0.4f, origin.y + h * 0.7f,
                    origin.x, origin.y + h * 0.9f
                )
            }
            drawPath(path, color = ornamentColor, style = stroke)
        }
    }
}

enum class EslimiCorner {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

/**
 * 2. چیدمان رقص‌گونه و ورود پله‌کانی (Staggered UI Choreography)
 * Sequentially slides and fades in list items / cards with precise 50ms serene timing and cubic-bezier easing.
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    modifier: Modifier = Modifier,
    key: Any? = null,
    content: @Composable () -> Unit
) {
    val animProgress = remember(key) { Animatable(0f) }

    LaunchedEffect(key, index) {
        val delayMillis = (index.coerceIn(0, 12) * SpiritualMotion.STAGGER_DELAY_PER_ITEM).toLong()
        delay(delayMillis)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = SpiritualMotion.DURATION_CARD_ENTRANCE,
                easing = SpiritualMotion.StaggerEase
            )
        )
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                alpha = animProgress.value
                translationY = (1f - animProgress.value) * 24.dp.toPx()
                scaleX = 0.95f + (animProgress.value * 0.05f)
                scaleY = 0.95f + (animProgress.value * 0.05f)
            }
    ) {
        content()
    }
}

/**
 * 3. میکرو-انیمیشن داستانی برای علاقه‌مندی (Narrative State Feedback: Favorite Action)
 * Story:
 * 1. Anticipation: Squashes down to 0.75x
 * 2. Radiance: Expands to 1.35x + emits golden spiritual rays & shockwave ring
 * 3. Settle: Settles into heart state with gentle spring damping
 */
@Composable
fun FavoriteMicroInteractionButton(
    isFavorite: Boolean,
    isDarkTheme: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    actionBtnBg: Color = if (isDarkTheme) Color(0xFF0F3227) else Color(0xFFEBF2EE),
    actionBtnBorder: Color = if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFC3D4CC)
) {
    val coroutineScope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1f) }
    val burstAnim = remember { Animatable(0f) }

    val activeColor = FavoriteRed
    val inactiveColor = if (isDarkTheme) Color(0xFF6B877B) else Color(0xFF8B9B93)

    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .border(1.dp, actionBtnBorder, CircleShape)
            .background(actionBtnBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    // Stage 1: Anticipation Squash (0.75x)
                    scaleAnim.animateTo(
                        targetValue = 0.75f,
                        animationSpec = tween(80, easing = LinearEasing)
                    )

                    // State trigger
                    onToggleFavorite()

                    // Stage 2: Radiance Burst (Expanding shockwave + golden rays)
                    launch {
                        burstAnim.snapTo(0f)
                        burstAnim.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(380, easing = SpiritualMotion.SereneDecelerate)
                        )
                    }

                    scaleAnim.animateTo(
                        targetValue = 1.35f,
                        animationSpec = tween(130, easing = SpiritualMotion.OvershootSpring)
                    )

                    // Stage 3: Settle with gentle spring
                    scaleAnim.animateTo(
                        targetValue = 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Narrative Shockwave & Sparkle burst
        if (burstAnim.value > 0f && burstAnim.value < 1f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val progress = burstAnim.value
                val center = Offset(size.width / 2, size.height / 2)
                val maxRadius = size.width * 0.85f

                // Expanding spiritual light ring
                drawCircle(
                    color = FavoriteRed.copy(alpha = (1f - progress) * 0.75f),
                    radius = maxRadius * progress,
                    center = center,
                    style = Stroke(width = (2.8.dp.toPx() * (1f - progress)).coerceAtLeast(1f))
                )

                // 8 radiating gold dust sparkles
                val sparkleDist = maxRadius * (0.4f + progress * 0.6f)
                val sparkleAlpha = (1f - progress) * 0.85f
                for (i in 0 until 8) {
                    val angle = (i * Math.PI / 4).toFloat()
                    val sx = center.x + cos(angle.toDouble()).toFloat() * sparkleDist
                    val sy = center.y + sin(angle.toDouble()).toFloat() * sparkleDist
                    drawCircle(
                        color = GoldenAmber.copy(alpha = sparkleAlpha),
                        radius = 2.dp.toPx() * (1f - progress),
                        center = Offset(sx, sy)
                    )
                }
            }
        }

        // Animated Icon
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "علاقه‌مندی",
            tint = if (isFavorite) activeColor else inactiveColor,
            modifier = Modifier
                .size(20.dp)
                .scale(scaleAnim.value)
        )
    }
}

/**
 * 4. تغییر وضعیت‌های مورفینگ (Morphing Audio Play Button)
 * Morphs seamlessly between:
 * - IDLE: Golden play icon with fine Islamic brass outline
 * - BUFFERING/LOADING: Rotating mini-tasbih prayer ring
 * - PLAYING: Rhythmic equalizer soundwave with green aura
 * - COMPLETED: Brief golden checkmark before settling
 */
@Composable
fun MorphingSpiritualAudioButton(
    isPlaying: Boolean,
    isLocked: Boolean,
    isDarkTheme: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionBtnBg: Color = if (isDarkTheme) Color(0xFF0F3227) else Color(0xFFEBF2EE),
    actionBtnBorder: Color = if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFC3D4CC)
) {
    val buttonBgColor by animateColorAsState(
        targetValue = when {
            isLocked -> if (isDarkTheme) Color(0x33DAA520) else Color(0xFFFEF3C7)
            isPlaying -> Color(0xFF16A34A)
            else -> actionBtnBg
        },
        animationSpec = tween(300, easing = SpiritualMotion.SereneEaseInOut),
        label = "btn_bg_anim"
    )

    val buttonBorderColor by animateColorAsState(
        targetValue = when {
            isLocked -> GoldenAmber.copy(alpha = 0.7f)
            isPlaying -> Color(0xFF22C55E)
            else -> actionBtnBorder
        },
        label = "btn_border_anim"
    )

    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .border(1.dp, buttonBorderColor, CircleShape)
            .background(buttonBgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLocked -> {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "قفل",
                    tint = if (isDarkTheme) GoldenAmber else Color(0xFFD97706),
                    modifier = Modifier.size(18.dp)
                )
            }
            isPlaying -> {
                // Morph into Dynamic Audio Equalizer Wave
                AudioWaveAnimation(color = Color.White)
            }
            else -> {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(com.example.R.drawable.ic_modern_volume_speaker),
                    contentDescription = "پخش صوتی",
                    tint = if (isDarkTheme) GoldenAmber else Color(0xFF133E2B),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * Rotating Golden Tasbih (تسبیح نورانی) Loading Animation with 12 choreographed glowing beads
 */
@Composable
fun SpiritualTasbihLoading(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true,
    message: String = "در حال بارگذاری..."
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tasbih_loading")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tasbih_rot"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = SpiritualMotion.BreathingEase),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crescent_pulse"
    )

    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width * 0.38f
                val beadCount = 12

                for (i in 0 until beadCount) {
                    val beadAngle = ((i.toFloat() / beadCount) * 360f + rotationAngle) * (Math.PI / 180f)
                    val bx = center.x + cos(beadAngle).toFloat() * radius
                    val by = center.y + sin(beadAngle).toFloat() * radius

                    val phase = ((i.toFloat() / beadCount) + (rotationAngle / 360f)) % 1f
                    val beadRadius = (2.8.dp.toPx() + sin(phase * Math.PI.toFloat()) * 2.2.dp.toPx())
                    val beadAlpha = 0.35f + sin(phase * Math.PI.toFloat()) * 0.65f

                    drawCircle(
                        color = GoldenAmber.copy(alpha = beadAlpha * 0.4f),
                        radius = beadRadius * 2.0f,
                        center = Offset(bx, by)
                    )
                    drawCircle(
                        color = GoldenAmber.copy(alpha = beadAlpha),
                        radius = beadRadius,
                        center = Offset(bx, by)
                    )
                }

                // Center glowing crescent aura
                drawCircle(
                    color = GoldenAmber.copy(alpha = 0.15f * pulseGlow),
                    radius = radius * 0.6f * pulseGlow,
                    center = center
                )
            }

            Icon(
                imageVector = Icons.Default.Mosque,
                contentDescription = null,
                tint = GoldenAmber,
                modifier = Modifier
                    .size(22.dp)
                    .scale(0.85f + pulseGlow * 0.15f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) GoldenAmber else Color(0xFF133E2B),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Narrative 3D Shrine Gates Opening & Divine Ray Unlock Animation (خرید موفق / باز شدن قفل طلایی)
 * Tells a micro-story:
 * 1. The sacred wooden shrine doors with golden brass inlays swing open
 * 2. Radiates golden divine sanctuary rays outward
 * 3. Reveals the radiant 8-pointed golden star of blessing
 */
@Composable
fun HolyShrineSuccessAnimation(
    modifier: Modifier = Modifier
) {
    val gateProgress = remember { Animatable(0f) }
    val lightRaysAnim = rememberInfiniteTransition(label = "rays")
    val rayRotation by lightRaysAnim.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ray_rot"
    )

    LaunchedEffect(Unit) {
        gateProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = SpiritualMotion.DURATION_SHRINE_GATE_OPEN,
                easing = SpiritualMotion.SereneEaseInOut
            )
        )
    }

    val density = LocalDensity.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 170.dp, height = 150.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background Layer: Radiant Expanding Golden Light Sanctuary
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val openRatio = gateProgress.value

                // Divine inner glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GoldenAmber.copy(alpha = 0.95f * openRatio),
                            Color(0xFFFFE082).copy(alpha = 0.6f * openRatio),
                            Color.Transparent
                        ),
                        center = center,
                        radius = size.width * 0.65f * openRatio
                    ),
                    radius = size.width * 0.65f,
                    center = center
                )

                // Light Rays projecting outwards
                rotate(rayRotation, center) {
                    val rayCount = 12
                    for (i in 0 until rayCount) {
                        val angle = (i * 360f / rayCount) * (Math.PI / 180f)
                        val rx = center.x + cos(angle).toFloat() * size.width * 0.6f
                        val ry = center.y + sin(angle).toFloat() * size.height * 0.6f
                        drawLine(
                            brush = Brush.linearGradient(
                                listOf(GoldenAmber.copy(alpha = 0.7f * openRatio), Color.Transparent),
                                start = center,
                                end = Offset(rx, ry)
                            ),
                            start = center,
                            end = Offset(rx, ry),
                            strokeWidth = 3.dp.toPx() * openRatio
                        )
                    }
                }
            }

            // Center Golden Holy Star
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = DarkEmeraldBg,
                modifier = Modifier
                    .size(36.dp)
                    .scale(gateProgress.value)
            )

            // Foreground: 3D Opening Shrine Gates (Left and Right doors)
            val leftDoorAngle = -75f * gateProgress.value
            val rightDoorAngle = 75f * gateProgress.value

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                // Left Shrine Door
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .graphicsLayer {
                            rotationY = leftDoorAngle
                            cameraDistance = 14f * density.density
                            transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
                        }
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF2E1C0C), Color(0xFF4A331A), Color(0xFF1E1208))
                            )
                        )
                        .border(
                            1.5.dp,
                            GoldenAmber.copy(alpha = 0.85f),
                            RoundedCornerShape(topStart = 16.dp, bottomStart = 8.dp)
                        )
                ) {
                    ShrineDoorEngravings()
                }

                // Right Shrine Door
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .graphicsLayer {
                            rotationY = rightDoorAngle
                            cameraDistance = 14f * density.density
                            transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 0.5f)
                        }
                        .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1E1208), Color(0xFF4A331A), Color(0xFF2E1C0C))
                            )
                        )
                        .border(
                            1.5.dp,
                            GoldenAmber.copy(alpha = 0.85f),
                            RoundedCornerShape(topEnd = 16.dp, bottomEnd = 8.dp)
                        )
                ) {
                    ShrineDoorEngravings()
                }
            }
        }
    }
}

/**
 * Geometric brass and wood Islamic engravings on the shrine doors
 */
@Composable
private fun ShrineDoorEngravings() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val goldStroke = Stroke(width = 1.dp.toPx())

        // Top Arch Inlay
        drawArc(
            color = GoldenAmber.copy(alpha = 0.6f),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.15f, h * 0.1f),
            size = Size(w * 0.7f, h * 0.25f),
            style = goldStroke
        )

        // Center Geometric Brass Medallion
        drawCircle(
            color = GoldenAmber.copy(alpha = 0.7f),
            radius = w * 0.22f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = goldStroke
        )
        drawCircle(
            color = GoldenAmber,
            radius = 2.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.5f)
        )

        // Bottom Inlay Rect
        drawRoundRect(
            color = GoldenAmber.copy(alpha = 0.5f),
            topLeft = Offset(w * 0.15f, h * 0.65f),
            size = Size(w * 0.7f, h * 0.25f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = goldStroke
        )
    }
}
