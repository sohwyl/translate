package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkEmeraldBg
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.LightCreamBg
import com.example.ui.theme.SpiritualMotion
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-performance Artistic Spiritual Background:
 * 1. Deep Spiritual Atmospheric Gradient (Static GPU Shader)
 * 2. Delicate Non-intrusive Islamic Geometric Patterns & Girih Watermark
 * 3. Low-opacity 'Breathing' Animation with scale (0.97x -> 1.03x) and opacity oscillations
 *    adding visual depth with zero performance penalty.
 */
@Composable
fun ArbaeenAmbientBackground(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    enableBreathingOrnaments: Boolean = true,
    content: @Composable () -> Unit
) {
    // Ultra-smooth, slow breathing pulse for geometric patterns (5.5s serene cycle)
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_breathing")
    
    val breathingScale by if (enableBreathingOrnaments) {
        infiniteTransition.animateFloat(
            initialValue = 0.97f,
            targetValue = 1.03f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = SpiritualMotion.DURATION_BREATHING_CYCLE,
                    easing = SpiritualMotion.BreathingEase
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breathing_scale"
        )
    } else {
        remember { mutableFloatStateOf(1.0f) }
    }

    val breathingAlphaRatio by if (enableBreathingOrnaments) {
        infiniteTransition.animateFloat(
            initialValue = 0.0f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = SpiritualMotion.DURATION_BREATHING_CYCLE,
                    easing = SpiritualMotion.BreathingEase
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breathing_alpha"
        )
    } else {
        remember { mutableFloatStateOf(0.5f) }
    }

    val primaryBgGradient = if (isDarkTheme) {
        listOf(
            Color(0xFF0C241B),
            DarkEmeraldBg,
            Color(0xFF04120D)
        )
    } else {
        listOf(
            Color(0xFFFAF7F0),
            LightCreamBg,
            Color(0xFFECE5D6)
        )
    }

    // Low-opacity subtle colors for artistic Islamic lines
    val lineworkColor = if (isDarkTheme) {
        GoldenAmber.copy(alpha = 0.035f + breathingAlphaRatio * 0.035f)
    } else {
        Color(0xFF133E2B).copy(alpha = 0.025f + breathingAlphaRatio * 0.025f)
    }

    val archAccentColor = if (isDarkTheme) {
        GoldenAmber.copy(alpha = 0.05f + breathingAlphaRatio * 0.035f)
    } else {
        Color(0xFF133E2B).copy(alpha = 0.035f + breathingAlphaRatio * 0.025f)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w * 0.5f, h * 0.5f)

            // Layer 1: Atmospheric Spiritual Radial & Vertical Canvas
            drawRect(
                brush = Brush.radialGradient(
                    colors = primaryBgGradient,
                    center = Offset(w * 0.5f, h * 0.22f),
                    radius = w.coerceAtLeast(h) * 0.95f
                )
            )

            // Layer 2: Delicate Mihrab Arch at Top (طاق محراب اسلیمی)
            drawSpiritualMihrabArch(
                center = Offset(w * 0.5f, 0f),
                width = w * 0.88f,
                height = h * 0.24f,
                color = archAccentColor
            )

            // Layer 3: Islamic Geometric Patterns with Breathing Scale & Opacity Oscillations
            scale(scale = breathingScale, pivot = center) {
                // Corner Geometric Eslimi Motifs
                drawCornerEslimiShamseh(
                    origin = Offset(0f, 0f),
                    radius = w * 0.28f,
                    color = lineworkColor,
                    corner = CornerPosition.TOP_LEFT
                )
                drawCornerEslimiShamseh(
                    origin = Offset(w, 0f),
                    radius = w * 0.28f,
                    color = lineworkColor,
                    corner = CornerPosition.TOP_RIGHT
                )
                drawCornerEslimiShamseh(
                    origin = Offset(0f, h),
                    radius = w * 0.32f,
                    color = lineworkColor,
                    corner = CornerPosition.BOTTOM_LEFT
                )
                drawCornerEslimiShamseh(
                    origin = Offset(w, h),
                    radius = w * 0.32f,
                    color = lineworkColor,
                    corner = CornerPosition.BOTTOM_RIGHT
                )

                // Central Subtle 8-pointed Islamic Star Watermark
                drawIslamicGeometricStar(
                    center = Offset(w * 0.5f, h * 0.52f),
                    radius = w * 0.38f,
                    color = lineworkColor.copy(alpha = lineworkColor.alpha * 0.8f)
                )

                // Delicate Girih Lattice Grid lines (نقوش گره‌چینی مشبک ظریف)
                drawIslamicGirihLattice(
                    center = Offset(w * 0.5f, h * 0.52f),
                    radius = w * 0.38f,
                    color = lineworkColor.copy(alpha = lineworkColor.alpha * 0.5f)
                )
            }
        }

        // Foreground Content
        content()
    }
}

private enum class CornerPosition {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

/**
 * Draws a traditional Islamic Mihrab Arch outline
 */
private fun DrawScope.drawSpiritualMihrabArch(
    center: Offset,
    width: Float,
    height: Float,
    color: Color
) {
    val stroke = Stroke(width = 1.2.dp.toPx())
    val path = Path().apply {
        val halfW = width * 0.5f
        moveTo(center.x - halfW, center.y)
        lineTo(center.x - halfW, center.y + height * 0.5f)
        
        // Pointed Arch Bezier curve
        cubicTo(
            center.x - halfW * 0.8f, center.y + height * 0.85f,
            center.x - halfW * 0.3f, center.y + height,
            center.x, center.y + height * 0.95f
        )
        cubicTo(
            center.x + halfW * 0.3f, center.y + height,
            center.x + halfW * 0.8f, center.y + height * 0.85f,
            center.x + halfW, center.y + height * 0.5f
        )
        lineTo(center.x + halfW, center.y)
    }
    drawPath(path, color = color, style = stroke)

    // Inner arch line
    val innerPath = Path().apply {
        val halfW = width * 0.42f
        moveTo(center.x - halfW, center.y)
        lineTo(center.x - halfW, center.y + height * 0.4f)
        cubicTo(
            center.x - halfW * 0.7f, center.y + height * 0.7f,
            center.x - halfW * 0.25f, center.y + height * 0.82f,
            center.x, center.y + height * 0.78f
        )
        cubicTo(
            center.x + halfW * 0.25f, center.y + height * 0.82f,
            center.x + halfW * 0.7f, center.y + height * 0.7f,
            center.x + halfW, center.y + height * 0.4f
        )
        lineTo(center.x + halfW, center.y)
    }
    drawPath(innerPath, color = color.copy(alpha = color.alpha * 0.6f), style = stroke)
}

/**
 * Draws an artistic Islamic Shamseh quarter in corners
 */
private fun DrawScope.drawCornerEslimiShamseh(
    origin: Offset,
    radius: Float,
    color: Color,
    corner: CornerPosition
) {
    val stroke = Stroke(width = 1.dp.toPx())

    // Concentric corner circles
    drawCircle(color = color, radius = radius, center = origin, style = stroke)
    drawCircle(color = color, radius = radius * 0.72f, center = origin, style = stroke)
    drawCircle(color = color, radius = radius * 0.45f, center = origin, style = stroke)
    drawCircle(color = color, radius = radius * 0.22f, center = origin, style = stroke)

    // Geometric radial lines in the corner quadrant
    val (startAngle, endAngle) = when (corner) {
        CornerPosition.TOP_LEFT -> Pair(0.0, Math.PI / 2)
        CornerPosition.TOP_RIGHT -> Pair(Math.PI / 2, Math.PI)
        CornerPosition.BOTTOM_RIGHT -> Pair(Math.PI, 3 * Math.PI / 2)
        CornerPosition.BOTTOM_LEFT -> Pair(3 * Math.PI / 2, 2 * Math.PI)
    }

    val steps = 6
    val angleStep = (endAngle - startAngle) / steps
    for (i in 0..steps) {
        val angle = startAngle + i * angleStep
        val cosA = cos(angle).toFloat()
        val sinA = sin(angle).toFloat()
        drawLine(
            color = color,
            start = Offset(origin.x + cosA * (radius * 0.22f), origin.y + sinA * (radius * 0.22f)),
            end = Offset(origin.x + cosA * radius, origin.y + sinA * radius),
            strokeWidth = 1.dp.toPx()
        )
    }
}

/**
 * Draws an intricate 8-pointed Islamic Star & Girih geometric pattern
 */
private fun DrawScope.drawIslamicGeometricStar(
    center: Offset,
    radius: Float,
    color: Color
) {
    val stroke = Stroke(width = 1.dp.toPx())

    // Concentric geometric circles
    drawCircle(color = color, radius = radius, center = center, style = stroke)
    drawCircle(color = color, radius = radius * 0.707f, center = center, style = stroke)
    drawCircle(color = color, radius = radius * 0.382f, center = center, style = stroke)

    // Square 1 (0 deg)
    val pathSq1 = Path().apply {
        val r = radius * 0.9f
        moveTo(center.x + r, center.y)
        lineTo(center.x, center.y + r)
        lineTo(center.x - r, center.y)
        lineTo(center.x, center.y - r)
        close()
    }
    drawPath(pathSq1, color, style = stroke)

    // Square 2 (45 deg)
    val r45 = radius * 0.9f * 0.7071f
    val pathSq2 = Path().apply {
        moveTo(center.x + r45, center.y + r45)
        lineTo(center.x - r45, center.y + r45)
        lineTo(center.x - r45, center.y - r45)
        lineTo(center.x + r45, center.y - r45)
        close()
    }
    drawPath(pathSq2, color, style = stroke)

    // 8 radial lines
    for (i in 0 until 8) {
        val angle = (i * Math.PI / 4).toFloat()
        val cosA = cos(angle.toDouble()).toFloat()
        val sinA = sin(angle.toDouble()).toFloat()
        drawLine(
            color = color,
            start = Offset(center.x + cosA * (radius * 0.382f), center.y + sinA * (radius * 0.382f)),
            end = Offset(center.x + cosA * radius, center.y + sinA * radius),
            strokeWidth = 1.dp.toPx()
        )
    }
}

/**
 * Draws delicate interlocking Girih lattice lines around the central motif
 */
private fun DrawScope.drawIslamicGirihLattice(
    center: Offset,
    radius: Float,
    color: Color
) {
    val stroke = Stroke(width = 0.8.dp.toPx())
    val outerR = radius * 1.35f
    
    // Outer secondary geometric ring
    drawCircle(color = color, radius = outerR, center = center, style = stroke)

    // Diagonal lattice diagonals
    val step = outerR * 0.35f
    for (i in -3..3) {
        val offset = i * step
        drawLine(
            color = color,
            start = Offset(center.x - outerR, center.y + offset),
            end = Offset(center.x + outerR, center.y + offset),
            strokeWidth = 0.6.dp.toPx()
        )
        drawLine(
            color = color,
            start = Offset(center.x + offset, center.y - outerR),
            end = Offset(center.x + offset, center.y + outerR),
            strokeWidth = 0.6.dp.toPx()
        )
    }
}
