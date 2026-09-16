package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.Cell
import com.example.model.Level
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun GameBoard(
    level: Level,
    currentPath: List<Cell>,
    isCompleted: Boolean,
    onCellHover: (Cell) -> Unit,
    onCellClick: (Cell) -> Unit,
    onObstacleHit: (Cell) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "headPulse"
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val availableW = maxWidth.value
        val availableH = maxHeight.value

        val rows = level.rows
        val cols = level.cols

        val cellSize = min(availableW / cols, availableH / rows) * 0.92f
        val boardW = cellSize * cols
        val boardH = cellSize * rows

        val activeSet = level.activeCells
        val obstacleSet = level.obstacles
        val visitedSet = currentPath.toSet()

        Box(
            modifier = Modifier
                .size(boardW.dp, boardH.dp)
                .testTag("game_board_canvas")
                .pointerInput(level, currentPath, isCompleted) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            if (!isCompleted) {
                                val c = (offset.x / (cellSize * density)).toInt()
                                val r = (offset.y / (cellSize * density)).toInt()
                                if (r in 0 until rows && c in 0 until cols) {
                                    val cell = Cell(r, c)
                                    if (cell in activeSet) {
                                        onCellHover(cell)
                                    } else if (cell in obstacleSet) {
                                        onObstacleHit(cell)
                                    }
                                }
                            }
                        },
                        onDrag = { change, _ ->
                            if (!isCompleted) {
                                change.consume()
                                val c = (change.position.x / (cellSize * density)).toInt()
                                val r = (change.position.y / (cellSize * density)).toInt()
                                if (r in 0 until rows && c in 0 until cols) {
                                    val cell = Cell(r, c)
                                    if (cell in activeSet) {
                                        onCellHover(cell)
                                    } else if (cell in obstacleSet) {
                                        onObstacleHit(cell)
                                    }
                                }
                            }
                        }
                    )
                }
                .pointerInput(level, currentPath, isCompleted) {
                    detectTapGestures(
                        onTap = { offset ->
                            if (!isCompleted) {
                                val c = (offset.x / (cellSize * density)).toInt()
                                val r = (offset.y / (cellSize * density)).toInt()
                                if (r in 0 until rows && c in 0 until cols) {
                                    val cell = Cell(r, c)
                                    if (cell in activeSet) {
                                        onCellClick(cell)
                                    } else if (cell in obstacleSet) {
                                        onObstacleHit(cell)
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellPixelSize = size.width / cols
                val cellPadding = cellPixelSize * 0.07f
                val innerSize = cellPixelSize - (cellPadding * 2)
                val cornerRadius = CornerRadius(cellPixelSize * 0.18f, cellPixelSize * 0.18f)

                // 1. Draw obstacle blocks first
                for (obs in obstacleSet) {
                    val topLeft = Offset(
                        x = obs.col * cellPixelSize + cellPadding,
                        y = obs.row * cellPixelSize + cellPadding
                    )
                    // Dark metallic barrier tile
                    drawRoundRect(
                        color = Color(0xFF1E2430),
                        topLeft = topLeft,
                        size = Size(innerSize, innerSize),
                        cornerRadius = cornerRadius
                    )
                    // Dark bevel frame
                    drawRoundRect(
                        color = Color(0xFF131822),
                        topLeft = topLeft,
                        size = Size(innerSize, innerSize),
                        cornerRadius = cornerRadius,
                        style = Stroke(width = cellPixelSize * 0.04f)
                    )
                    // Obstacle barrier graphic: embossed diagonal cross
                    val center = Offset(topLeft.x + innerSize / 2f, topLeft.y + innerSize / 2f)
                    val arm = innerSize * 0.22f
                    val strokeW = cellPixelSize * 0.07f
                    drawLine(
                        color = Color(0xFF475569),
                        start = Offset(center.x - arm, center.y - arm),
                        end = Offset(center.x + arm, center.y + arm),
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color(0xFF475569),
                        start = Offset(center.x - arm, center.y + arm),
                        end = Offset(center.x + arm, center.y - arm),
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                    // Steel rivet dots
                    val dotR = innerSize * 0.04f
                    val offsetD = innerSize * 0.16f
                    drawCircle(Color(0xFF334155), radius = dotR, center = Offset(topLeft.x + offsetD, topLeft.y + offsetD))
                    drawCircle(Color(0xFF334155), radius = dotR, center = Offset(topLeft.x + innerSize - offsetD, topLeft.y + offsetD))
                    drawCircle(Color(0xFF334155), radius = dotR, center = Offset(topLeft.x + offsetD, topLeft.y + innerSize - offsetD))
                    drawCircle(Color(0xFF334155), radius = dotR, center = Offset(topLeft.x + innerSize - offsetD, topLeft.y + innerSize - offsetD))
                }

                // 2. Draw active playable cells
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val cell = Cell(r, c)
                        if (cell !in activeSet) continue

                        val topLeft = Offset(
                            x = c * cellPixelSize + cellPadding,
                            y = r * cellPixelSize + cellPadding
                        )
                        val isVisited = cell in visitedSet
                        val isStart = cell == level.startCell

                        if (isVisited) {
                            // Visited cell background fill (bright pastel)
                            drawRoundRect(
                                color = level.difficulty.pathFillColor,
                                topLeft = topLeft,
                                size = Size(innerSize, innerSize),
                                cornerRadius = cornerRadius
                            )
                            // Soft inner border
                            drawRoundRect(
                                color = level.difficulty.primaryColor.copy(alpha = 0.5f),
                                topLeft = topLeft,
                                size = Size(innerSize, innerSize),
                                cornerRadius = cornerRadius,
                                style = Stroke(width = cellPixelSize * 0.04f)
                            )
                        } else {
                            // Unvisited cell background (sleek dark slate block with rounded 3D bevel)
                            drawRoundRect(
                                color = Color(0xFF323B49),
                                topLeft = topLeft,
                                size = Size(innerSize, innerSize),
                                cornerRadius = cornerRadius
                            )
                            // Subtle 3D top/left highlight
                            drawRoundRect(
                                color = Color(0xFF3D4757),
                                topLeft = topLeft,
                                size = Size(innerSize, innerSize),
                                cornerRadius = cornerRadius,
                                style = Stroke(width = cellPixelSize * 0.03f)
                            )
                        }

                        // If it's the start cell and not yet moved from:
                        if (isStart && currentPath.size == 1) {
                            // Draw starting indicator (vibrant circular fill)
                            val center = Offset(
                                x = c * cellPixelSize + cellPixelSize / 2f,
                                y = r * cellPixelSize + cellPixelSize / 2f
                            )
                            drawCircle(
                                color = level.difficulty.primaryColor,
                                radius = innerSize * 0.38f,
                                center = center
                            )
                            // Draw Star icon inside start cell
                            drawStar(
                                center = center,
                                radius = innerSize * 0.22f,
                                color = Color.White
                            )
                        } else if (isStart && currentPath.size > 1) {
                            // Draw star in start cell as visited landmark
                            val center = Offset(
                                x = c * cellPixelSize + cellPixelSize / 2f,
                                y = r * cellPixelSize + cellPixelSize / 2f
                            )
                            drawStar(
                                center = center,
                                radius = innerSize * 0.22f,
                                color = Color(0xFF6B451A)
                            )
                        }
                    }
                }

                // 3. Draw connecting path ribbon
                if (currentPath.size > 1) {
                    val pathRibbon = Path()
                    val firstCenter = Offset(
                        x = currentPath[0].col * cellPixelSize + cellPixelSize / 2f,
                        y = currentPath[0].row * cellPixelSize + cellPixelSize / 2f
                    )
                    pathRibbon.moveTo(firstCenter.x, firstCenter.y)

                    for (i in 1 until currentPath.size) {
                        val pt = Offset(
                            x = currentPath[i].col * cellPixelSize + cellPixelSize / 2f,
                            y = currentPath[i].row * cellPixelSize + cellPixelSize / 2f
                        )
                        pathRibbon.lineTo(pt.x, pt.y)
                    }

                    // Stroke width for the path line
                    val strokeWidth = cellPixelSize * 0.24f

                    // Draw outer subtle glow / shadow
                    drawPath(
                        path = pathRibbon,
                        color = level.difficulty.primaryColor.copy(alpha = 0.25f),
                        style = Stroke(
                            width = strokeWidth * 1.5f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Draw main path line
                    drawPath(
                        path = pathRibbon,
                        color = level.difficulty.pathStrokeColor,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // 4. Draw Head Marker (Animated Mascot Face on active head)
                if (currentPath.isNotEmpty() && !isCompleted) {
                    val head = currentPath.last()
                    val headCenter = Offset(
                        x = head.col * cellPixelSize + cellPixelSize / 2f,
                        y = head.row * cellPixelSize + cellPixelSize / 2f
                    )

                    val headRadius = (innerSize * 0.44f) * pulseScale

                    // Draw head circle background
                    drawCircle(
                        color = level.difficulty.primaryColor,
                        radius = headRadius,
                        center = headCenter
                    )

                    // Subtle white outline rim
                    drawCircle(
                        color = Color.White,
                        radius = headRadius,
                        center = headCenter,
                        style = Stroke(width = cellPixelSize * 0.04f)
                    )

                    // Mascot face details on head marker
                    val eyeOffsetX = headRadius * 0.35f
                    val eyeOffsetY = -headRadius * 0.12f
                    val eyeRadius = headRadius * 0.14f

                    // Left eye (white)
                    drawCircle(
                        color = Color.White,
                        radius = eyeRadius,
                        center = Offset(headCenter.x - eyeOffsetX, headCenter.y + eyeOffsetY)
                    )
                    // Left pupil (dark)
                    drawCircle(
                        color = Color(0xFF1E2530),
                        radius = eyeRadius * 0.55f,
                        center = Offset(headCenter.x - eyeOffsetX, headCenter.y + eyeOffsetY)
                    )

                    // Right eye (white)
                    drawCircle(
                        color = Color.White,
                        radius = eyeRadius,
                        center = Offset(headCenter.x + eyeOffsetX, headCenter.y + eyeOffsetY)
                    )
                    // Right pupil (dark)
                    drawCircle(
                        color = Color(0xFF1E2530),
                        radius = eyeRadius * 0.55f,
                        center = Offset(headCenter.x + eyeOffsetX, headCenter.y + eyeOffsetY)
                    )

                    // Happy smile
                    val mouthWidth = headRadius * 0.45f
                    val mouthY = headCenter.y + headRadius * 0.25f
                    val smilePath = Path().apply {
                        moveTo(headCenter.x - mouthWidth / 2f, mouthY)
                        quadraticBezierTo(
                            headCenter.x,
                            mouthY + headRadius * 0.25f,
                            headCenter.x + mouthWidth / 2f,
                            mouthY
                        )
                    }
                    drawPath(
                        path = smilePath,
                        color = Color(0xFF1E2530),
                        style = Stroke(
                            width = cellPixelSize * 0.035f,
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawStar(
    center: Offset,
    radius: Float,
    color: Color,
    points: Int = 5
) {
    val path = Path()
    val innerRadius = radius * 0.45f
    val step = PI / points

    for (i in 0 until 2 * points) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angle = i * step - PI / 2
        val x = (center.x + r * cos(angle)).toFloat()
        val y = (center.y + r * sin(angle)).toFloat()
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    drawPath(path = path, color = color, style = Fill)
}
