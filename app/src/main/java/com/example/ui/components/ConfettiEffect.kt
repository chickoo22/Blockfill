package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val speedX: Float,
    val speedY: Float,
    val rotationSpeed: Float,
    val color: Color,
    val size: Float
)

@Composable
fun ConfettiEffect(
    trigger: Long,
    modifier: Modifier = Modifier
) {
    if (trigger == 0L) return

    val progress = remember(trigger) { Animatable(0f) }

    val particles = remember(trigger) {
        val colors = listOf(
            Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFFEF4444),
            Color(0xFF3B82F6), Color(0xFFEC4899), Color(0xFF8B5CF6)
        )
        val rng = Random(trigger)
        List(45) {
            ConfettiParticle(
                initialX = rng.nextFloat(),
                initialY = rng.nextFloat() * 0.3f - 0.1f,
                speedX = (rng.nextFloat() - 0.5f) * 0.4f,
                speedY = 0.6f + rng.nextFloat() * 0.7f,
                rotationSpeed = (rng.nextFloat() - 0.5f) * 10f,
                color = colors[rng.nextInt(colors.size)],
                size = 14f + rng.nextFloat() * 12f
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val t = progress.value

            for (p in particles) {
                val currentX = (p.initialX + p.speedX * t) * w
                val currentY = (p.initialY + p.speedY * t) * h
                val alpha = (1f - t).coerceIn(0f, 1f)

                drawRect(
                    color = p.color.copy(alpha = alpha),
                    topLeft = Offset(currentX, currentY),
                    size = Size(p.size, p.size * 0.6f)
                )
            }
        }
    }
}
