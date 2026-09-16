package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.Difficulty

@Composable
fun MascotVisual(
    difficulty: Difficulty,
    size: Dp = 140.dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f

            when (difficulty) {
                Difficulty.EASY -> {
                    // Friendly Green Mascot with perky rounded ears
                    val whiteOutlineWidth = w * 0.07f
                    val headRadius = w * 0.36f

                    // Ears (White outline layer)
                    drawCircle(Color.White, headRadius * 0.45f, Offset(cx - headRadius * 0.75f, cy - headRadius * 0.75f))
                    drawCircle(Color.White, headRadius * 0.45f, Offset(cx + headRadius * 0.75f, cy - headRadius * 0.75f))
                    // Ears (Black layer)
                    drawCircle(Color.Black, headRadius * 0.38f, Offset(cx - headRadius * 0.75f, cy - headRadius * 0.75f))
                    drawCircle(Color.Black, headRadius * 0.38f, Offset(cx + headRadius * 0.75f, cy - headRadius * 0.75f))

                    // White outer halo
                    drawCircle(Color.White, headRadius + whiteOutlineWidth, Offset(cx, cy))
                    // Black head outline
                    drawCircle(Color.Black, headRadius, Offset(cx, cy))
                    // Green face fill
                    drawCircle(Color(0xFF22C55E), headRadius * 0.88f, Offset(cx, cy))

                    // Happy Eyes
                    val eyeRadius = headRadius * 0.22f
                    drawCircle(Color.Black, eyeRadius, Offset(cx - headRadius * 0.4f, cy - headRadius * 0.1f))
                    drawCircle(Color.Black, eyeRadius, Offset(cx + headRadius * 0.4f, cy - headRadius * 0.1f))
                    // Eye highlights (sparkles)
                    drawCircle(Color.White, eyeRadius * 0.4f, Offset(cx - headRadius * 0.45f, cy - headRadius * 0.18f))
                    drawCircle(Color.White, eyeRadius * 0.4f, Offset(cx + headRadius * 0.35f, cy - headRadius * 0.18f))

                    // Cute smiling mouth
                    val mouthPath = Path().apply {
                        moveTo(cx - headRadius * 0.3f, cy + headRadius * 0.25f)
                        quadraticBezierTo(cx, cy + headRadius * 0.55f, cx + headRadius * 0.3f, cy + headRadius * 0.25f)
                    }
                    drawPath(mouthPath, Color.Black, style = Stroke(width = w * 0.035f, cap = StrokeCap.Round))
                }

                Difficulty.MEDIUM -> {
                    // Screenshot 6: Sly Orange Mascot with black border and white halo
                    val whiteOutlineWidth = w * 0.065f
                    val headRadius = w * 0.38f

                    // White outer border
                    drawCircle(Color.White, headRadius + whiteOutlineWidth, Offset(cx, cy))
                    // Black outer edge
                    drawCircle(Color.Black, headRadius, Offset(cx, cy))
                    // Warm Orange Face
                    drawCircle(Color(0xFFF59E0B), headRadius * 0.88f, Offset(cx, cy))

                    // Sly Angled Eyes (Black wedge / rounded shape)
                    val leftEye = Path().apply {
                        moveTo(cx - headRadius * 0.55f, cy - headRadius * 0.22f)
                        lineTo(cx - headRadius * 0.18f, cy - headRadius * 0.05f)
                        quadraticBezierTo(cx - headRadius * 0.22f, cy + headRadius * 0.25f, cx - headRadius * 0.48f, cy + headRadius * 0.15f)
                        close()
                    }
                    val rightEye = Path().apply {
                        moveTo(cx + headRadius * 0.18f, cy - headRadius * 0.05f)
                        lineTo(cx + headRadius * 0.55f, cy - headRadius * 0.22f)
                        quadraticBezierTo(cx + headRadius * 0.48f, cy + headRadius * 0.15f, cx + headRadius * 0.22f, cy + headRadius * 0.25f)
                        close()
                    }
                    drawPath(leftEye, Color.Black)
                    drawPath(rightEye, Color.Black)

                    // Pupils / highlights (white triangular gleam)
                    drawCircle(Color.White, headRadius * 0.07f, Offset(cx - headRadius * 0.4f, cy - headRadius * 0.05f))
                    drawCircle(Color.White, headRadius * 0.07f, Offset(cx + headRadius * 0.32f, cy - headRadius * 0.05f))

                    // Smirking Mouth (sharp playful curve on the right side)
                    val smirkPath = Path().apply {
                        moveTo(cx - headRadius * 0.15f, cy + headRadius * 0.35f)
                        lineTo(cx + headRadius * 0.05f, cy + headRadius * 0.42f)
                        lineTo(cx + headRadius * 0.28f, cy + headRadius * 0.28f)
                    }
                    drawPath(smirkPath, Color.Black, style = Stroke(width = w * 0.04f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }

                Difficulty.HARD -> {
                    // Screenshot 2: Red Angry Mascot with Devil Horns
                    val headRadius = w * 0.34f

                    // Horns Path (White outline first, then black, then red)
                    val hornPath = Path().apply {
                        // Left horn
                        moveTo(cx - headRadius * 0.3f, cy - headRadius * 0.7f)
                        cubicTo(
                            cx - headRadius * 0.6f, cy - headRadius * 1.3f,
                            cx - headRadius * 1.1f, cy - headRadius * 0.9f,
                            cx - headRadius * 0.8f, cy - headRadius * 0.3f
                        )
                        // Right horn
                        moveTo(cx + headRadius * 0.3f, cy - headRadius * 0.7f)
                        cubicTo(
                            cx + headRadius * 0.6f, cy - headRadius * 1.3f,
                            cx + headRadius * 1.1f, cy - headRadius * 0.9f,
                            cx + headRadius * 0.8f, cy - headRadius * 0.3f
                        )
                    }
                    // Outer white contour
                    drawPath(hornPath, Color.White, style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    drawCircle(Color.White, headRadius * 1.2f, Offset(cx, cy))

                    // Black base for horns and head
                    drawPath(hornPath, Color.Black, style = Stroke(width = w * 0.07f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    drawCircle(Color.Black, headRadius * 1.05f, Offset(cx, cy))

                    // Red Face
                    drawCircle(Color(0xFFEF4444), headRadius * 0.92f, Offset(cx, cy))

                    // Angry Angled Eyes (Slanted downwards to center)
                    val eyeL = Path().apply {
                        moveTo(cx - headRadius * 0.55f, cy - headRadius * 0.28f)
                        lineTo(cx - headRadius * 0.15f, cy + headRadius * 0.05f)
                        lineTo(cx - headRadius * 0.45f, cy + headRadius * 0.18f)
                        close()
                    }
                    val eyeR = Path().apply {
                        moveTo(cx + headRadius * 0.55f, cy - headRadius * 0.28f)
                        lineTo(cx + headRadius * 0.15f, cy + headRadius * 0.05f)
                        lineTo(cx + headRadius * 0.45f, cy + headRadius * 0.18f)
                        close()
                    }
                    drawPath(eyeL, Color.Black)
                    drawPath(eyeR, Color.Black)

                    // Highlights
                    drawCircle(Color.White, headRadius * 0.06f, Offset(cx - headRadius * 0.38f, cy - headRadius * 0.06f))
                    drawCircle(Color.White, headRadius * 0.06f, Offset(cx + headRadius * 0.32f, cy - headRadius * 0.06f))

                    // Grumpy / sassy mouth
                    val mouth = Path().apply {
                        moveTo(cx - headRadius * 0.15f, cy + headRadius * 0.38f)
                        quadraticBezierTo(cx + headRadius * 0.05f, cy + headRadius * 0.25f, cx + headRadius * 0.28f, cy + headRadius * 0.42f)
                    }
                    drawPath(mouth, Color.Black, style = Stroke(width = w * 0.042f, cap = StrokeCap.Round))
                }

                Difficulty.EXTRA_HARD -> {
                    // Screenshot 1: Purple Horned Beast Mascot with sinister glowing eyes
                    val headRadius = w * 0.33f

                    // Beast Horns / Ears
                    val ears = Path().apply {
                        // Left tall ear/horn
                        moveTo(cx - headRadius * 0.4f, cy - headRadius * 0.6f)
                        cubicTo(
                            cx - headRadius * 0.9f, cy - headRadius * 1.35f,
                            cx - headRadius * 1.1f, cy - headRadius * 0.8f,
                            cx - headRadius * 0.8f, cy - headRadius * 0.1f
                        )
                        // Right tall ear/horn
                        moveTo(cx + headRadius * 0.4f, cy - headRadius * 0.6f)
                        cubicTo(
                            cx + headRadius * 0.9f, cy - headRadius * 1.35f,
                            cx + headRadius * 1.1f, cy - headRadius * 0.8f,
                            cx + headRadius * 0.8f, cy - headRadius * 0.1f
                        )
                    }
                    // Outer white contour
                    drawPath(ears, Color.White, style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    drawCircle(Color.White, headRadius * 1.22f, Offset(cx, cy))

                    // Black base
                    drawPath(ears, Color.Black, style = Stroke(width = w * 0.07f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    drawCircle(Color.Black, headRadius * 1.05f, Offset(cx, cy))

                    // Deep Purple Face
                    drawCircle(Color(0xFF5856D6), headRadius * 0.92f, Offset(cx, cy))

                    // Menacing Angled Slanted Eyes
                    val leftBeastEye = Path().apply {
                        moveTo(cx - headRadius * 0.58f, cy - headRadius * 0.3f)
                        lineTo(cx - headRadius * 0.18f, cy + headRadius * 0.02f)
                        lineTo(cx - headRadius * 0.48f, cy + headRadius * 0.18f)
                        close()
                    }
                    val rightBeastEye = Path().apply {
                        moveTo(cx + headRadius * 0.58f, cy - headRadius * 0.3f)
                        lineTo(cx + headRadius * 0.18f, cy + headRadius * 0.02f)
                        lineTo(cx + headRadius * 0.48f, cy + headRadius * 0.18f)
                        close()
                    }
                    drawPath(leftBeastEye, Color.Black)
                    drawPath(rightBeastEye, Color.Black)

                    // Glowing Sinister Pupils (Triangular / dot)
                    drawCircle(Color(0xFF2E2468), headRadius * 0.08f, Offset(cx - headRadius * 0.36f, cy - headRadius * 0.04f))
                    drawCircle(Color(0xFF2E2468), headRadius * 0.08f, Offset(cx + headRadius * 0.36f, cy - headRadius * 0.04f))

                    // Grimace / Evil smile
                    val grimace = Path().apply {
                        moveTo(cx - headRadius * 0.22f, cy + headRadius * 0.36f)
                        lineTo(cx, cy + headRadius * 0.28f)
                        lineTo(cx + headRadius * 0.22f, cy + headRadius * 0.36f)
                    }
                    drawPath(grimace, Color.Black, style = Stroke(width = w * 0.042f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }

                Difficulty.MASTER -> {
                    // Regal Master: Royal Fuchsia Mascot wearing an ornate 3-peak golden crown
                    val whiteOutlineWidth = w * 0.065f
                    val headRadius = w * 0.38f

                    // 1. Crown behind head
                    val crownBaseY = cy - headRadius * 0.4f
                    val crownTopLeft = Offset(cx - headRadius * 0.7f, cy - headRadius * 1.15f)
                    val crownTopMid = Offset(cx, cy - headRadius * 1.35f)
                    val crownTopRight = Offset(cx + headRadius * 0.7f, cy - headRadius * 1.15f)

                    val crownPath = Path().apply {
                        moveTo(cx - headRadius * 0.65f, crownBaseY)
                        lineTo(crownTopLeft.x, crownTopLeft.y)
                        lineTo(cx - headRadius * 0.3f, cy - headRadius * 0.7f)
                        lineTo(crownTopMid.x, crownTopMid.y)
                        lineTo(cx + headRadius * 0.3f, cy - headRadius * 0.7f)
                        lineTo(crownTopRight.x, crownTopRight.y)
                        lineTo(cx + headRadius * 0.65f, crownBaseY)
                        close()
                    }

                    // Crown outer white contour
                    drawPath(crownPath, Color.White, style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    // Head outer white
                    drawCircle(Color.White, headRadius + whiteOutlineWidth, Offset(cx, cy))

                    // Crown black contour
                    drawPath(crownPath, Color.Black, style = Stroke(width = w * 0.06f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    // Crown gold fill
                    drawPath(crownPath, Color(0xFFF59E0B))

                    // Crown Jewels
                    drawCircle(Color(0xFFEF4444), headRadius * 0.12f, crownTopMid)
                    drawCircle(Color(0xFF3B82F6), headRadius * 0.09f, crownTopLeft)
                    drawCircle(Color(0xFF10B981), headRadius * 0.09f, crownTopRight)

                    // Head black border & Fuchsia fill
                    drawCircle(Color.Black, headRadius, Offset(cx, cy))
                    drawCircle(Color(0xFFD946EF), headRadius * 0.90f, Offset(cx, cy))

                    // Confident Master Eyes (Slightly curved confident arch)
                    val eyeRadius = headRadius * 0.24f
                    drawCircle(Color.Black, eyeRadius, Offset(cx - headRadius * 0.38f, cy - headRadius * 0.05f))
                    drawCircle(Color.Black, eyeRadius, Offset(cx + headRadius * 0.38f, cy - headRadius * 0.05f))

                    // Golden Sparkle Highlights
                    drawCircle(Color(0xFFFDE047), eyeRadius * 0.38f, Offset(cx - headRadius * 0.42f, cy - headRadius * 0.12f))
                    drawCircle(Color(0xFFFDE047), eyeRadius * 0.38f, Offset(cx + headRadius * 0.34f, cy - headRadius * 0.12f))

                    // Confident sly smirk
                    val smirk = Path().apply {
                        moveTo(cx - headRadius * 0.28f, cy + headRadius * 0.32f)
                        quadraticBezierTo(cx + headRadius * 0.1f, cy + headRadius * 0.48f, cx + headRadius * 0.36f, cy + headRadius * 0.26f)
                    }
                    drawPath(smirk, Color.Black, style = Stroke(width = w * 0.045f, cap = StrokeCap.Round))
                }
                Difficulty.DAILY -> {
                    // Golden Trophy Champion Mascot for Daily Challenges
                    val whiteOutlineWidth = w * 0.07f
                    val headRadius = w * 0.36f

                    // White outer halo
                    drawCircle(Color.White, headRadius + whiteOutlineWidth, Offset(cx, cy))
                    // Head black outline
                    drawCircle(Color.Black, headRadius, Offset(cx, cy))
                    // Warm golden amber face
                    drawCircle(Color(0xFFF59E0B), headRadius * 0.90f, Offset(cx, cy))

                    // Big sparkling champion eyes
                    val eyeRadius = headRadius * 0.22f
                    drawCircle(Color.Black, eyeRadius, Offset(cx - headRadius * 0.38f, cy - headRadius * 0.1f))
                    drawCircle(Color.Black, eyeRadius, Offset(cx + headRadius * 0.38f, cy - headRadius * 0.1f))
                    // Sparkling white star highlights
                    drawCircle(Color.White, eyeRadius * 0.45f, Offset(cx - headRadius * 0.42f, cy - headRadius * 0.18f))
                    drawCircle(Color.White, eyeRadius * 0.45f, Offset(cx + headRadius * 0.34f, cy - headRadius * 0.18f))

                    // Big joyful open smile
                    val smile = Path().apply {
                        moveTo(cx - headRadius * 0.32f, cy + headRadius * 0.2f)
                        quadraticBezierTo(cx, cy + headRadius * 0.58f, cx + headRadius * 0.32f, cy + headRadius * 0.2f)
                    }
                    drawPath(smile, Color.Black, style = Stroke(width = w * 0.045f, cap = StrokeCap.Round))

                    // Rosy cheeks
                    drawCircle(Color(0xFFFDE68A), headRadius * 0.18f, Offset(cx - headRadius * 0.52f, cy + headRadius * 0.22f))
                    drawCircle(Color(0xFFFDE68A), headRadius * 0.18f, Offset(cx + headRadius * 0.52f, cy + headRadius * 0.22f))
                }
            }
        }
    }
}
