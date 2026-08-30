package com.example.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

/**
 * Spiritual & Serene Motion Design Specs for Arbaeen App
 * Archetype: معنوی و آرام (Spiritual & Serene)
 * Featuring soft easing, subtle overshoot, and deliberate contemplative timing.
 */
object SpiritualMotion {
    // Soft, peaceful spiritual easing curve
    val SereneEaseInOut: Easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)
    
    // Contemplative deceleration curve for entering elements
    val SereneDecelerate: Easing = CubicBezierEasing(0.16f, 1.0f, 0.3f, 1.0f)
    
    // Disney Anticipation & Overshoot curve for lively micro-interactions
    val OvershootSpring: Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1.0f)
    
    // Natural sinusoidal breathing curve for ambient background motifs (Slow 5.5s cycle)
    val BreathingEase: Easing = CubicBezierEasing(0.45f, 0.05f, 0.55f, 0.95f)

    // Staggered choreography easing curve (Natural deceleration cubic-bezier)
    val StaggerEase: Easing = CubicBezierEasing(0.2f, 0.9f, 0.3f, 1.0f)

    // Standard durations (ms)
    const val DURATION_CARD_ENTRANCE = 480
    const val DURATION_MICRO_INTERACTION = 320
    const val DURATION_SHRINE_GATE_OPEN = 1100
    const val DURATION_BREATHING_CYCLE = 5500
    const val STAGGER_DELAY_PER_ITEM = 50
}
