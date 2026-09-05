package com.example.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
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
import kotlin.math.floor
import kotlin.math.sin

/**
 * Specialized Artistic Spiritual Background for the 3 Onboarding Steps.
 * Each step tells a visual story tied directly to the app's purpose:
 *
 * Step 1 — Welcome: A glowing shrine skyline (dome, minarets, crescent) under a
 *          starlit sky, framed by a golden 12-point Shamseh — sets the pilgrimage tone.
 * Step 2 — Role: The Tariq Al-Hussein pilgrimage road converging toward the shrine,
 *          lined with Mokeb (service tent) silhouettes — mirrors the "Pilgrim vs.
 *          Mokeb host" choice on screen.
 * Step 3 — Personalize: A Sun/Crescent-Moon duo sharing one orbit — a direct visual
 *          echo of the Day/Night theme switch being configured on this very screen.
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

    // A slower, continuously advancing 0..1 ramp (not reversing) used to twinkle
    // stars and walk a glowing dot along the pilgrimage road — motion that
    // doesn't just breathe in place.
    val driftProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "onboarding_drift"
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

    // Faint texture linework (kept very subtle, for background depth only)
    val lineworkColor = if (isDarkTheme) {
        GoldenAmber.copy(alpha = 0.05f + breathingAlphaRatio * 0.04f)
    } else {
        Color(0xFF133E2B).copy(alpha = 0.04f + breathingAlphaRatio * 0.03f)
    }

    // The primary motifs (shrine, road, orbits) — clearly visible so the art
    // actually reads as shrine / pilgrimage / theme imagery, not noise.
    val emphasisColor = if (isDarkTheme) {
        GoldenAmber.copy(alpha = 0.22f + breathingAlphaRatio * 0.10f)
    } else {
        Color(0xFF0F5C40).copy(alpha = 0.16f + breathingAlphaRatio * 0.08f)
    }

    val accentGlowColor = if (isDarkTheme) {
        GoldenAmber.copy(alpha = 0.14f + breathingAlphaRatio * 0.08f)
    } else {
        Color(0xFF133E2B).copy(alpha = 0.08f + breathingAlphaRatio * 0.05f)
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
                    1 -> drawStep1ShrineWelcomeBackground(
                        w = w,
                        h = h,
                        center = center,
                        lineworkColor = lineworkColor,
                        emphasisColor = emphasisColor,
                        accentColor = accentGlowColor,
                        isDarkTheme = isDarkTheme,
                        driftProgress = driftProgress
                    )
                    2 -> drawStep2PilgrimRoadBackground(
                        w = w,
                        h = h,
                        center = center,
                        lineworkColor = lineworkColor,
                        emphasisColor = emphasisColor,
                        accentColor = accentGlowColor,
                        driftProgress = driftProgress
                    )
                    3 -> drawStep3DayNightHarmonyBackground(
                        w = w,
                        h = h,
                        center = center,
                        lineworkColor = lineworkColor,
                        emphasisColor = emphasisColor,
                        accentColor = accentGlowColor,
                        breathingAlphaRatio = breathingAlphaRatio
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
 * Step 1 Art: Holy Welcome — Golden 12-point Shamseh medallion glowing over a
 * lit shrine skyline (dome + twin minarets + crescent), under a twinkling
 * night sky. Directly evokes the Arbaeen shrine pilgrimage this app serves.
 */
private fun DrawScope.drawStep1ShrineWelcomeBackground(
    w: Float,
    h: Float,
    center: Offset,
    lineworkColor: Color,
    emphasisColor: Color,
    accentColor: Color,
    isDarkTheme: Boolean,
    driftProgress: Float
) {
    val stroke = Stroke(width = 1.3.dp.toPx())
    val fineStroke = Stroke(width = 0.9.dp.toPx())

    // 0. Warm glow halo behind the medallion, so the motif reads clearly
    // instead of disappearing into the background gradient.
    val haloRadius = w * 0.5f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(accentColor, Color.Transparent),
            center = center,
            radius = haloRadius
        ),
        radius = haloRadius,
        center = center
    )

    // 1. Central 12-pointed Grand Islamic Shamseh
    val shamsehRadius = w * 0.36f
    drawCircle(color = emphasisColor, radius = shamsehRadius, center = center, style = stroke)
    drawCircle(color = emphasisColor, radius = shamsehRadius * 0.72f, center = center, style = fineStroke)
    drawCircle(color = lineworkColor, radius = shamsehRadius * 0.46f, center = center, style = fineStroke)

    // 12-pointed star lines
    for (i in 0 until 12) {
        val angle = (i * Math.PI / 6).toFloat()
        val cosA = cos(angle.toDouble()).toFloat()
        val sinA = sin(angle.toDouble()).toFloat()
        drawLine(
            color = emphasisColor,
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

    // 2. Twinkling night sky above the shrine skyline
    drawTwinkleField(
        w = w,
        topY = h * 0.06f,
        bottomY = h * 0.5f,
        count = 14,
        baseColor = if (isDarkTheme) GoldenAmber else Color(0xFF0F5C40),
        driftProgress = driftProgress
    )

    // 3. Shrine Skyline: Twin Minarets + Central Golden Dome + Crescent finial
    val horizonY = h * 0.80f
    val domeApexY = horizonY - 58.dp.toPx()

    val skylinePath = Path().apply {
        moveTo(0f, horizonY)
        lineTo(w * 0.24f, horizonY)

        // Left Minaret
        lineTo(w * 0.24f, horizonY - 50.dp.toPx())
        lineTo(w * 0.275f, horizonY - 58.dp.toPx())
        lineTo(w * 0.31f, horizonY - 50.dp.toPx())
        lineTo(w * 0.31f, horizonY)

        lineTo(w * 0.38f, horizonY)

        // Central Sacred Dome (bulbous shrine silhouette)
        cubicTo(
            w * 0.40f, horizonY - 30.dp.toPx(),
            w * 0.42f, horizonY - 62.dp.toPx(),
            w * 0.5f, domeApexY
        )
        cubicTo(
            w * 0.58f, horizonY - 62.dp.toPx(),
            w * 0.60f, horizonY - 30.dp.toPx(),
            w * 0.62f, horizonY
        )

        lineTo(w * 0.69f, horizonY)

        // Right Minaret
        lineTo(w * 0.69f, horizonY - 50.dp.toPx())
        lineTo(w * 0.725f, horizonY - 58.dp.toPx())
        lineTo(w * 0.76f, horizonY - 50.dp.toPx())
        lineTo(w * 0.76f, horizonY)

        lineTo(w, horizonY)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    // Soft filled silhouette so the shrine shape is unmistakable, plus a crisp outline
    drawPath(
        skylinePath,
        brush = Brush.verticalGradient(
            colors = listOf(accentColor, accentColor.copy(alpha = accentColor.alpha * 0.4f))
        )
    )
    drawPath(skylinePath, color = emphasisColor, style = Stroke(width = 1.3.dp.toPx()))

    // Crescent finial atop the dome
    drawCrescent(
        center = Offset(w * 0.5f, domeApexY - 12.dp.toPx()),
        radius = 7.dp.toPx(),
        color = emphasisColor
    )

    // 4. Corner Eslimi Quarters
    drawCornerSpirals(w, h, lineworkColor)
}

/**
 * Step 2 Art: Pilgrim & Mokeb Path — the Tariq Al-Hussein road converging toward
 * the shrine, lined by Mokeb (service tent) silhouettes with pennants, plus a
 * hexagonal Girih medallion. A soft glow "walks" up the road to suggest the
 * pilgrim's footsteps — echoing the Pilgrim / Mokeb-host choice on this screen.
 */
private fun DrawScope.drawStep2PilgrimRoadBackground(
    w: Float,
    h: Float,
    center: Offset,
    lineworkColor: Color,
    emphasisColor: Color,
    accentColor: Color,
    driftProgress: Float
) {
    val fineStroke = Stroke(width = 1.dp.toPx())

    // 1. Radiant Road vectors (طریق‌الحسین) converging towards top-center
    val vanishPoint = Offset(w * 0.5f, h * 0.1f)
    val roadCount = 7
    for (i in 0..roadCount) {
        val bottomX = w * (i.toFloat() / roadCount)
        drawLine(
            color = if (i == roadCount / 2) emphasisColor else lineworkColor,
            start = vanishPoint,
            end = Offset(bottomX, h),
            strokeWidth = if (i == roadCount / 2) 1.4.dp.toPx() else 0.9.dp.toPx()
        )
    }

    // Glowing dot "walking" up the central road, looping with driftProgress
    val centralRoadBottom = Offset(w * 0.5f, h)
    val walkPos = Offset(
        x = centralRoadBottom.x + (vanishPoint.x - centralRoadBottom.x) * driftProgress,
        y = centralRoadBottom.y + (vanishPoint.y - centralRoadBottom.y) * driftProgress
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(accentColor, Color.Transparent),
            center = walkPos,
            radius = 16.dp.toPx()
        ),
        radius = 16.dp.toPx(),
        center = walkPos
    )
    drawCircle(color = emphasisColor, radius = 3.dp.toPx(), center = walkPos)

    // 2. Mokeb (service tent) silhouettes lining the horizon
    val horizonY = h * 0.92f
    val tentCount = 5
    for (i in 0 until tentCount) {
        val tentCenterX = w * ((i + 0.5f) / tentCount)
        val tentWidth = w / tentCount * 0.62f
        val tentHeight = 30.dp.toPx() + (if (i % 2 == 0) 6.dp.toPx() else 0f)
        val tentPath = Path().apply {
            moveTo(tentCenterX - tentWidth / 2f, horizonY)
            lineTo(tentCenterX, horizonY - tentHeight)
            lineTo(tentCenterX + tentWidth / 2f, horizonY)
            close()
        }
        drawPath(tentPath, color = lineworkColor, style = fineStroke)
        // Small pennant flag at the tent's peak
        drawLine(
            color = emphasisColor,
            start = Offset(tentCenterX, horizonY - tentHeight),
            end = Offset(tentCenterX, horizonY - tentHeight - 10.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
        val flagPath = Path().apply {
            moveTo(tentCenterX, horizonY - tentHeight - 10.dp.toPx())
            lineTo(tentCenterX + 8.dp.toPx(), horizonY - tentHeight - 7.dp.toPx())
            lineTo(tentCenterX, horizonY - tentHeight - 4.dp.toPx())
            close()
        }
        drawPath(flagPath, color = emphasisColor)
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
    drawPath(hexPath, color = emphasisColor, style = Stroke(width = 1.3.dp.toPx()))

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
 * Step 3 Art: Personalize — a Sun (day) and Crescent Moon (night) sharing one
 * orbit, connected by a soft arc — a direct visual echo of the Day/Night theme
 * toggle configured on this screen. Gradient-stroked rings add a sense of depth.
 */
private fun DrawScope.drawStep3DayNightHarmonyBackground(
    w: Float,
    h: Float,
    center: Offset,
    lineworkColor: Color,
    emphasisColor: Color,
    accentColor: Color,
    breathingAlphaRatio: Float
) {
    val fineStroke = Stroke(width = 1.dp.toPx())

    // 1. Concentric Celestial Orbit Rings, gradient-stroked for a sense of depth
    val orbitR1 = w * 0.24f
    val orbitR2 = w * 0.42f
    val orbitR3 = w * 0.58f

    val ringBrush = Brush.sweepGradient(
        colors = listOf(emphasisColor, lineworkColor, emphasisColor),
        center = center
    )
    drawCircle(brush = ringBrush, radius = orbitR1, center = center, style = fineStroke)
    drawCircle(brush = ringBrush, radius = orbitR2, center = center, style = fineStroke)
    drawCircle(brush = ringBrush, radius = orbitR3, center = center, style = fineStroke)

    // 2. Sun disc (day) on one side of the shared orbit
    val sunCenter = Offset(center.x - orbitR2, center.y)
    val sunRadius = 20.dp.toPx()
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFE082), GoldenAmber.copy(alpha = 0.85f), Color.Transparent),
            center = sunCenter,
            radius = sunRadius * 2.2f
        ),
        radius = sunRadius * 2.2f,
        center = sunCenter
    )
    for (i in 0 until 8) {
        val angle = (i * Math.PI / 4).toFloat()
        val cosA = cos(angle.toDouble()).toFloat()
        val sinA = sin(angle.toDouble()).toFloat()
        drawLine(
            color = GoldenAmber.copy(alpha = 0.5f + breathingAlphaRatio * 0.3f),
            start = Offset(sunCenter.x + cosA * sunRadius, sunCenter.y + sinA * sunRadius),
            end = Offset(sunCenter.x + cosA * (sunRadius * 1.5f), sunCenter.y + sinA * (sunRadius * 1.5f)),
            strokeWidth = 1.4.dp.toPx()
        )
    }

    // 3. Crescent Moon (night) on the opposite side of the shared orbit
    val moonCenter = Offset(center.x + orbitR2, center.y)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFB9C4D6).copy(alpha = 0.35f), Color.Transparent),
            center = moonCenter,
            radius = sunRadius * 2f
        ),
        radius = sunRadius * 2f,
        center = moonCenter
    )
    drawCrescent(center = moonCenter, radius = sunRadius * 0.8f, color = Color(0xFFDCE3EC).copy(alpha = 0.9f))

    // Connecting arc between Sun and Moon, tracing their shared orbit
    drawArc(
        color = accentColor,
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(center.x - orbitR2, center.y - orbitR2),
        size = Size(orbitR2 * 2f, orbitR2 * 2f),
        style = Stroke(width = 1.6.dp.toPx())
    )

    // 4. 8-star interlocking diamond lattice (kept from original girih motif)
    val latticeR = w * 0.28f * 0.7f
    val pathSq1 = Path().apply {
        moveTo(center.x + latticeR, center.y)
        lineTo(center.x, center.y + latticeR)
        lineTo(center.x - latticeR, center.y)
        lineTo(center.x, center.y - latticeR)
        close()
    }
    drawPath(pathSq1, color = lineworkColor, style = fineStroke)

    val r45 = latticeR * 0.7071f
    val pathSq2 = Path().apply {
        moveTo(center.x + r45, center.y + r45)
        lineTo(center.x - r45, center.y + r45)
        lineTo(center.x - r45, center.y - r45)
        lineTo(center.x + r45, center.y - r45)
        close()
    }
    drawPath(pathSq2, color = lineworkColor, style = fineStroke)

    // 5. Orbital beads (تسبیح), each with a soft glow for a gentle 3D-bokeh feel
    val beadCount = 16
    for (i in 0 until beadCount) {
        val angle = (i * 2 * Math.PI / beadCount)
        val bx = center.x + cos(angle).toFloat() * orbitR3
        val by = center.y + sin(angle).toFloat() * orbitR3
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accentColor, Color.Transparent),
                center = Offset(bx, by),
                radius = 6.dp.toPx()
            ),
            radius = 6.dp.toPx(),
            center = Offset(bx, by)
        )
        drawCircle(color = emphasisColor, radius = 2.dp.toPx(), center = Offset(bx, by))
    }

    // Corner Ornaments
    drawCornerSpirals(w, h, lineworkColor)
}

/**
 * Scattered twinkling stars across the given vertical band. Uses a single
 * driftProgress value (already animated elsewhere) with per-star phase
 * offsets so every star twinkles independently at no extra animation cost.
 */
private fun DrawScope.drawTwinkleField(
    w: Float,
    topY: Float,
    bottomY: Float,
    count: Int,
    baseColor: Color,
    driftProgress: Float
) {
    for (i in 0 until count) {
        // Deterministic pseudo-random scatter based on index only (stable across recompositions)
        val seed = i * 12.9898f
        val rawX = sin(seed) * 43758.5453f
        val fracX = rawX - floor(rawX)
        val rawY = sin(seed * 1.7f + 3.1f) * 24634.6345f
        val fracY = rawY - floor(rawY)
        val px = w * fracX
        val py = topY + (bottomY - topY) * fracY

        val phase = (driftProgress * 2 * Math.PI + i * 0.9f).toFloat()
        val twinkle = 0.35f + 0.65f * (0.5f + 0.5f * sin(phase))
        val starRadius = (1.dp.toPx()) + (i % 3) * 0.6.dp.toPx()

        drawCircle(
            color = baseColor.copy(alpha = (0.10f + 0.35f * twinkle).coerceIn(0f, 1f)),
            radius = starRadius,
            center = Offset(px, py)
        )
    }
}

/** Draws a simple crescent moon shape: a filled disc with a smaller cut-out circle cleared from one side. */
private fun DrawScope.drawCrescent(center: Offset, radius: Float, color: Color) {
    drawCircle(color = color, radius = radius, center = center)
    drawCircle(
        color = Color.Transparent,
        radius = radius * 0.82f,
        center = Offset(center.x + radius * 0.42f, center.y),
        blendMode = BlendMode.Clear
    )
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
