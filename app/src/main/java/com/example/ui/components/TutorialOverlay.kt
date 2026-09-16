package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Cell
import com.example.model.Level
import kotlin.math.min

/**
 * Interactive step-by-step tutorial overlay for Level 1.
 * Guides new players on how to touch the start block, drag into adjacent blocks,
 * and fill every block on the board to understand the core game mechanics.
 */
@Composable
fun TutorialOverlay(
    level: Level,
    currentPath: List<Cell>,
    isCompleted: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Current step determined dynamically by player's path progress
    val currentStep = when {
        isCompleted -> 4
        currentPath.size <= 1 -> 1
        currentPath.size in 2..3 -> 2
        else -> 3
    }

    val targetCell = when (currentStep) {
        1 -> level.startCell
        2, 3 -> {
            val idx = currentPath.size
            if (idx < level.solution.size) level.solution[idx] else level.solution.last()
        }
        else -> null
    }

    val prevCell = if (currentPath.isNotEmpty()) currentPath.last() else level.startCell

    val infiniteTransition = rememberInfiniteTransition(label = "tutorial_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val handProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hand_slide"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("tutorial_overlay_container")
    ) {
        // --- 1. INTERACTIVE BOARD VISUAL GUIDE (Pointer events pass through so user can drag!) ---
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val availableW = maxWidth.value
            val availableH = maxHeight.value
            val rows = level.rows
            val cols = level.cols

            val cellSize = min(availableW / cols, availableH / rows) * 0.92f
            val boardW = cellSize * cols
            val boardH = cellSize * rows

            Box(
                modifier = Modifier.size(boardW.dp, boardH.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cellPixelSize = size.width / cols
                    val cellPadding = cellPixelSize * 0.07f
                    val innerSize = cellPixelSize - (cellPadding * 2)

                    if (!isCompleted && targetCell != null) {
                        val targetCenter = Offset(
                            x = targetCell.col * cellPixelSize + cellPixelSize / 2f,
                            y = targetCell.row * cellPixelSize + cellPixelSize / 2f
                        )

                        // Draw pulsing glow halo around target block
                        val haloRadius = (innerSize * 0.55f) * pulseScale
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8).copy(alpha = 0.5f),
                                    Color(0xFF0284C7).copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                center = targetCenter,
                                radius = haloRadius
                            ),
                            radius = haloRadius,
                            center = targetCenter
                        )

                        // Draw dashed neon target border on the cell
                        val topLeft = Offset(
                            x = targetCell.col * cellPixelSize + cellPadding,
                            y = targetCell.row * cellPixelSize + cellPadding
                        )
                        drawRoundRect(
                            color = Color(0xFF38BDF8),
                            topLeft = topLeft,
                            size = Size(innerSize, innerSize),
                            cornerRadius = CornerRadius(cellPixelSize * 0.18f, cellPixelSize * 0.18f),
                            style = Stroke(
                                width = cellPixelSize * 0.06f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                            )
                        )

                        // For Step 2 & 3: Draw animated motion guide from previous cell to target cell
                        if (currentStep in 2..3 && prevCell != targetCell) {
                            val startCenter = Offset(
                                x = prevCell.col * cellPixelSize + cellPixelSize / 2f,
                                y = prevCell.row * cellPixelSize + cellPixelSize / 2f
                            )

                            // Dotted motion path
                            val motionPath = Path().apply {
                                moveTo(startCenter.x, startCenter.y)
                                lineTo(targetCenter.x, targetCenter.y)
                            }

                            drawPath(
                                path = motionPath,
                                color = Color(0xFF38BDF8).copy(alpha = 0.8f),
                                style = Stroke(
                                    width = cellPixelSize * 0.08f,
                                    cap = StrokeCap.Round,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                                )
                            )

                            // Animated gliding touch point along the vector
                            val handPos = Offset(
                                x = startCenter.x + (targetCenter.x - startCenter.x) * handProgress,
                                y = startCenter.y + (targetCenter.y - startCenter.y) * handProgress
                            )

                            // Touch ripple
                            drawCircle(
                                color = Color.White.copy(alpha = 0.7f * (1f - handProgress)),
                                radius = cellPixelSize * 0.28f * (0.5f + handProgress * 0.5f),
                                center = handPos
                            )
                            drawCircle(
                                color = Color(0xFF38BDF8),
                                radius = cellPixelSize * 0.12f,
                                center = handPos
                            )
                            drawCircle(
                                color = Color.White,
                                radius = cellPixelSize * 0.06f,
                                center = handPos
                            )
                        } else if (currentStep == 1) {
                            // Step 1: Animated pulsing touch circle directly on start block
                            drawCircle(
                                color = Color.White.copy(alpha = 0.6f),
                                radius = cellPixelSize * 0.20f * pulseScale,
                                center = targetCenter,
                                style = Stroke(width = cellPixelSize * 0.05f)
                            )
                        }
                    }
                }
            }
        }

        // --- 2. STEP-BY-STEP INSTRUCTION CARD BANNER (Pinned cleanly at Top) ---
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.96f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = when (currentStep) {
                        1 -> Color(0xFF10B981) // Emerald Start
                        2 -> Color(0xFF38BDF8) // Sky Blue Drag
                        3 -> Color(0xFFF59E0B) // Amber Fill
                        else -> Color(0xFF8B5CF6) // Purple Win
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // Header Row: Step Badge + Title + Skip Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Step pill badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when (currentStep) {
                                            1 -> Color(0xFF059669)
                                            2 -> Color(0xFF0284C7)
                                            3 -> Color(0xFFD97706)
                                            else -> Color(0xFF7C3AED)
                                        }
                                    )
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (currentStep == 4) "COMPLETE" else "STEP $currentStep OF 3",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                            }

                            // Dynamic Step Title
                            Text(
                                text = when (currentStep) {
                                    1 -> "Touch the Star"
                                    2 -> "Drag to Neighbor"
                                    3 -> "Fill Every Block"
                                    else -> "Level 1 Solved!"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        // Dismiss / Skip button
                        Surface(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.testTag("skip_tutorial_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "SKIP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8)
                                )
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Skip Tutorial",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Step Explanation Body
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    when (currentStep) {
                                        1 -> Color(0xFF10B981).copy(alpha = 0.2f)
                                        2 -> Color(0xFF38BDF8).copy(alpha = 0.2f)
                                        3 -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                        else -> Color(0xFF8B5CF6).copy(alpha = 0.2f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentStep == 4) Icons.Rounded.Lightbulb else Icons.Rounded.TouchApp,
                                contentDescription = null,
                                tint = when (currentStep) {
                                    1 -> Color(0xFF10B981)
                                    2 -> Color(0xFF38BDF8)
                                    3 -> Color(0xFFF59E0B)
                                    else -> Color(0xFFC084FC)
                                },
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (currentStep) {
                                    1 -> "Touch and hold the Star block to start drawing your path."
                                    2 -> "Drag into adjacent blocks! Each block fills with color as you move."
                                    3 -> "Cover all 5 blocks in one continuous line without crossing yourself!"
                                    else -> "Brilliant! You've learned how to draw the line and fill blocks!"
                                },
                                color = Color(0xFFF1F5F9),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = when (currentStep) {
                                    1 -> "💡 The star always marks your starting position."
                                    2 -> "💡 Follow the animated guide to the next cell."
                                    3 -> "💡 Swipe backward anytime to undo your steps."
                                    else -> "Tap Next to tackle progressively bigger grids!"
                                },
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Progress Dots
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..3) {
                            val isActive = i <= currentStep
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(width = if (i == currentStep) 24.dp else 8.dp, height = 8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (isActive) Color(0xFF38BDF8) else Color(0xFF334155)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
