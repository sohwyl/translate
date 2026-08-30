package com.example.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
 * Specialized Artistic Spiritual Background for the 3 Onboarding Steps:
 * Step 1: Celestial Islamic Shamseh, Dome & Minaret silhouettes, Top Mihrab Arch
 * Step 2: Sacred Road / Tariq Al-Hussein path vectors, Mokeb tent arches, Hexagonal Girih
 * Step 3: Day & Night Celestial Harmony (Sun & Crescent Moon starbursts, Concentric orbital rings)
 */
@Composable
fun OnboardingStepBackground(
    step: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "onboarding_breathing")

    // Slow 5.5s serene breathing cycle for background motifs
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = SpiritualMotion.DURATION_BREATHING_CYCLE,
                easing = SpiritualMotion.BreathingEase
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "onboarding_scale"
    )

    val breathingAlphaRatio by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = SpiritualMotion.DURATION_BREATHING_CYCLE,
                easing = SpiritualMotion.BreathingEase
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "onboarding_alpha"
    )

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
            Color(0xFFEBE4D5)
        )
    }

    val lineworkColor = if (isDarkTheme) {
        GoldenAmber.copy(alpha = 0.04f + breathingAlphaRatio * 0.04f)
    } else {
        Color(0xFF133E2B).copy(alpha = 0.03f + breathingAlphaRatio * 0.03f)
    }

    val accentGlowColor = if (isDarkTheme) {
        GoldenAmber.copy(alpha = 0.07f + breathingAlphaRatio * 0.05f)
    } else {
        Color(0xFF133E2B).copy(alpha = 0.04f + breathingAlphaRatio * 0.03f)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w * 0.5f, h * 0.42f)

            // Base atmospheric gradient
            drawRect(
                brush = Brush.radialGradient(
                    colors = primaryBgGradient,
                    center = Offset(w * 0.5f, h * 0.28f),
                    radius = w.coerceAtLeast(h) * 0.95f
                )
            )

            // Dynamic Step-Specific Geometric Art with Breathing Transformation
            scale(scale = breathingScale, pivot = center) {
                when (step) {
                    1 -> drawStep1CelestialWelcomeBackground(
                        w = w,
                        h = h,
                        center = center,
                        lineworkColor = lineworkColor,
                        accentColor = accentGlowColor
                    )
                    2 -> drawStep2PilgrimRoadBackground(
                        w = w,
                        h = h,
                        center = center,
                        lineworkColor = lineworkColor,
                        accentColor = accentGlowColor
                    )
                    3 -> drawStep3PersonalizationHarmonyBackground(
                        w = w,
                        h = h,
                        center = center,
                        lineworkColor = lineworkColor,
                        accentColor = accentGlowColor,
                        isDarkTheme = isDarkTheme
                    )
                }
            }

            // Universal Top Mihrab Arch
            drawTopMihrab(w, h, accentGlowColor)
        }

        // Foreground Content
        content()
    }
}

/**
 * Step 1 Art: Holy Welcome — Sacred 12-point Celestial Shamseh & Shrine Silhouette
 */
private fun DrawScope.drawStep1CelestialWelcomeBackground(
    w: Float,
    h: Float,
    center: Offset,
    lineworkColor: Color,
    accentColor: Color
) {
    val stroke = Stroke(width = 1.1.dp.toPx())
    val fineStroke = Stroke(width = 0.8.dp.toPx())

    // 1. Central 12-pointed Grand Islamic Shamseh
    val shamsehRadius = w * 0.36f
    drawCircle(color = lineworkColor, radius = shamsehRadius, center = center, style = stroke)
    drawCircle(color = lineworkColor, radius = shamsehRadius * 0.72f, center = center, style = fineStroke)
    drawCircle(color = lineworkColor, radius = shamsehRadius * 0.46f, center = center, style = fineStroke)

    // 12-pointed star lines
    for (i in 0 until 12) {
        val angle = (i * Math.PI / 6).toFloat()
        val cosA = cos(angle.toDouble()).toFloat()
        val sinA = sin(angle.toDouble()).toFloat()
        drawLine(
            color = lineworkColor,
            start = Offset(center.x + cosA * (shamsehRadius * 0.46f), center.y + sinA * (shamsehRadius * 0.46f)),
            end = Offset(center.x + cosA * shamsehRadius, center.y + sinA * shamsehRadius),
            strokeWidth = 1.dp.toPx()
        )
    }

    // 3 Interlocking Squares forming 12-star rosette
    for (step in 0 until 3) {
        val startAngle = step * (Math.PI / 6)
        val path = Path().apply {
            val r = shamsehRadius * 0.88f
            for (p in 0 until 4) {
                val a = startAngle + (p * Math.PI / 2)
                val px = center.x + cos(a).toFloat() * r
                val py = center.y + sin(a).toFloat() * r
                if (p == 0) moveTo(px, py) else lineTo(px, py)
            }
            close()
        }
        drawPath(path, color = lineworkColor, style = fineStroke)
    }

    // 2. Dome & Minaret Silhouette Outline at bottom horizon
    val horizonY = h * 0.78f
    val domePath = Path().apply {
        moveTo(0f, horizonY)
        lineTo(w * 0.25f, horizonY)
        
        // Left Minaret
        lineTo(w * 0.25f, horizonY - 45.dp.toPx())
        lineTo(w * 0.28f, horizonY - 45.dp.toPx())
        lineTo(w * 0.28f, horizonY)
        
        lineTo(w * 0.38f, horizonY)
        
        // Central Sacred Dome
        cubicTo(
            w * 0.42f, horizonY - 50.dp.toPx(),
            w * 0.58f, horizonY - 50.dp.toPx(),
            w * 0.62f, horizonY
        )
        
        lineTo(w * 0.72f, horizonY)
        
        // Right Minaret
        lineTo(w * 0.72f, horizonY - 45.dp.toPx())
        lineTo(w * 0.75f, horizonY - 45.dp.toPx())
        lineTo(w * 0.75f, horizonY)
        
        lineTo(w, horizonY)
    }
    drawPath(domePath, color = accentColor, style = Stroke(width = 1.2.dp.toPx()))

    // 3. Corner Eslimi Quarters
    drawCornerSpirals(w, h, lineworkColor)
}

/**
 * Step 2 Art: Pilgrim & Mokeb Path — Road lines, Tent arches & Hexagonal Girih
 */
private fun DrawScope.drawStep2PilgrimRoadBackground(
    w: Float,
    h: Float,
    center: Offset,
    lineworkColor: Color,
    accentColor: Color
) {
    val fineStroke = Stroke(width = 0.9.dp.toPx())

    // 1. Radiant Road vectors (طریق‌الحسین) converging towards top-center
    val vanishPoint = Offset(w * 0.5f, h * 0.12f)
    val roadCount = 7
    for (i in 0..roadCount) {
        val bottomX = w * (i.toFloat() / roadCount)
        drawLine(
            color = lineworkColor,
            start = vanishPoint,
            end = Offset(bottomX, h),
            strokeWidth = 0.8.dp.toPx()
        )
    }

    // 2. Concentric Distance Arches (نقوش موکب‌ها و عمودها)
    val archCenter = Offset(w * 0.5f, h * 0.95f)
    for (i in 1..4) {
        val r = w * (0.22f * i)
        drawCircle(
            color = lineworkColor,
            radius = r,
            center = archCenter,
            style = fineStroke
        )
    }

    // 3. Hexagonal Islamic Girih Medallion around center
    val hexRadius = w * 0.32f
    val hexPath = Path().apply {
        for (i in 0 until 6) {
            val angle = (i * Math.PI / 3).toFloat()
            val px = center.x + cos(angle.toDouble()).toFloat() * hexRadius
            val py = center.y + sin(angle.toDouble()).toFloat() * hexRadius
            if (i == 0) moveTo(px, py) else lineTo(px, py)
        }
        close()
    }
    drawPath(hexPath, color = accentColor, style = Stroke(width = 1.2.dp.toPx()))

    // Hexagonal inner stars
    for (i in 0 until 6) {
        val angle = (i * Math.PI / 3).toFloat()
        val px = center.x + cos(angle.toDouble()).toFloat() * hexRadius
        val py = center.y + sin(angle.toDouble()).toFloat() * hexRadius
        drawLine(color = lineworkColor, start = center, end = Offset(px, py), strokeWidth = 1.dp.toPx())
    }

    // 4. Side Arabesque Brackets (حاشیه‌های اسلیمی کناری)
    drawSideArabesqueBrackets(w, h, lineworkColor)
}

/**
 * Step 3 Art: Personalization & Themes — Celestial Sun & Moon Starbursts, Orbital Tasbih Rings
 */
private fun DrawScope.drawStep3PersonalizationHarmonyBackground(
    w: Float,
    h: Float,
    center: Offset,
    lineworkColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    val fineStroke = Stroke(width = 0.9.dp.toPx())

    // 1. Concentric Celestial Orbit Rings (حلقه‌های مدار آسمانی)
    val orbitR1 = w * 0.24f
    val orbitR2 = w * 0.40f
    val orbitR3 = w * 0.55f

    drawCircle(color = lineworkColor, radius = orbitR1, center = center, style = fineStroke)
    drawCircle(color = lineworkColor, radius = orbitR2, center = center, style = fineStroke)
    drawCircle(color = lineworkColor, radius = orbitR3, center = center, style = fineStroke)

    // 2. Celestial 8-pointed Sun Starburst / Crescent Star
    val sunRadius = w * 0.28f
    for (i in 0 until 8) {
        val angle = (i * Math.PI / 4).toFloat()
        val cosA = cos(angle.toDouble()).toFloat()
        val sinA = sin(angle.toDouble()).toFloat()
        drawLine(
            color = accentColor,
            start = Offset(center.x + cosA * (sunRadius * 0.35f), center.y + sinA * (sunRadius * 0.35f)),
            end = Offset(center.x + cosA * sunRadius, center.y + sinA * sunRadius),
            strokeWidth = 1.2.dp.toPx()
        )
    }

    // 8-star interlocking diamond lattice
    val pathSq1 = Path().apply {
        val r = sunRadius * 0.7f
        moveTo(center.x + r, center.y)
        lineTo(center.x, center.y + r)
        lineTo(center.x - r, center.y)
        lineTo(center.x, center.y - r)
        close()
    }
    drawPath(pathSq1, color = lineworkColor, style = fineStroke)

    val r45 = sunRadius * 0.7f * 0.7071f
    val pathSq2 = Path().apply {
        moveTo(center.x + r45, center.y + r45)
        lineTo(center.x - r45, center.y + r45)
        lineTo(center.x - r45, center.y - r45)
        lineTo(center.x + r45, center.y - r45)
        close()
    }
    drawPath(pathSq2, color = lineworkColor, style = fineStroke)

    // 3. Orbital beads (دانه‌های تسبیح مدار)
    val beadCount = 16
    for (i in 0 until beadCount) {
        val angle = (i * 2 * Math.PI / beadCount)
        val bx = center.x + cos(angle).toFloat() * orbitR2
        val by = center.y + sin(angle).toFloat() * orbitR2
        drawCircle(
            color = accentColor,
            radius = 1.8.dp.toPx(),
            center = Offset(bx, by)
        )
    }

    // Corner Ornaments
    drawCornerSpirals(w, h, lineworkColor)
}

/**
 * Top Mihrab Arch
 */
private fun DrawScope.drawTopMihrab(w: Float, h: Float, color: Color) {
    val stroke = Stroke(width = 1.dp.toPx())
    val halfW = w * 0.44f
    val archH = h * 0.18f
    val path = Path().apply {
        moveTo(w * 0.5f - halfW, 0f)
        lineTo(w * 0.5f - halfW, archH * 0.4f)
        cubicTo(
            w * 0.5f - halfW * 0.75f, archH * 0.85f,
            w * 0.5f - halfW * 0.25f, archH,
            w * 0.5f, archH * 0.95f
        )
        cubicTo(
            w * 0.5f + halfW * 0.25f, archH,
            w * 0.5f + halfW * 0.75f, archH * 0.85f,
            w * 0.5f + halfW, archH * 0.4f
        )
        lineTo(w * 0.5f + halfW, 0f)
    }
    drawPath(path, color = color, style = stroke)
}

/**
 * Draws corner spiral curves
 */
private fun DrawScope.drawCornerSpirals(w: Float, h: Float, color: Color) {
    val stroke = Stroke(width = 0.9.dp.toPx())
    val r = w * 0.24f

    // Top-Left
    drawCircle(color = color, radius = r, center = Offset(0f, 0f), style = stroke)
    drawCircle(color = color, radius = r * 0.5f, center = Offset(0f, 0f), style = stroke)

    // Top-Right
    drawCircle(color = color, radius = r, center = Offset(w, 0f), style = stroke)
    drawCircle(color = color, radius = r * 0.5f, center = Offset(w, 0f), style = stroke)

    // Bottom-Left
    drawCircle(color = color, radius = r * 0.8f, center = Offset(0f, h), style = stroke)

    // Bottom-Right
    drawCircle(color = color, radius = r * 0.8f, center = Offset(w, h), style = stroke)
}

/**
 * Draws side arabesque bracket lines for step 2
 */
private fun DrawScope.drawSideArabesqueBrackets(w: Float, h: Float, color: Color) {
    val stroke = Stroke(width = 0.9.dp.toPx())
    val bracketH = h * 0.22f
    val startY = h * 0.38f

    // Left Bracket
    val leftPath = Path().apply {
        moveTo(0f, startY)
        cubicTo(w * 0.08f, startY + bracketH * 0.3f, w * 0.08f, startY + bracketH * 0.7f, 0f, startY + bracketH)
    }
    drawPath(leftPath, color = color, style = stroke)

    // Right Bracket
    val rightPath = Path().apply {
        moveTo(w, startY)
        cubicTo(w * 0.92f, startY + bracketH * 0.3f, w * 0.92f, startY + bracketH * 0.7f, w, startY + bracketH)
    }
    drawPath(rightPath, color = color, style = stroke)
}
