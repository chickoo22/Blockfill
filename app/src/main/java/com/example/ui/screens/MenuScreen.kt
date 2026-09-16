package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Difficulty
import com.example.ui.GameUiState
import com.example.ui.components.MascotVisual

@Composable
fun MenuScreen(
    uiState: GameUiState,
    onSelectDifficulty: (Difficulty) -> Unit,
    onPlayClicked: () -> Unit,
    onLevelSelectClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onHelpClicked: () -> Unit,
    onShopClicked: () -> Unit = {},
    onDailyChallengeClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentDiff = uiState.selectedDifficulty

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F5EC)) // Warm cream background
    ) {
        // --- TOP HEADER (Dark Navy / Slate) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.42f)
                .background(Color(0xFF1E2530))
        ) {
            // Subtle maze puzzle background decor behind header
            Canvas(modifier = Modifier.fillMaxSize()) {
                val blockColor = Color(0xFF283140)
                val strokeColor = Color(0xFF384355)
                // Draw some decorative faint blocks
                drawRoundRect(
                    color = blockColor,
                    topLeft = Offset(size.width * 0.15f, size.height * 0.45f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.22f, size.width * 0.22f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
                drawRoundRect(
                    color = blockColor,
                    topLeft = Offset(size.width * 0.42f, size.height * 0.45f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.22f, size.width * 0.22f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
                drawRoundRect(
                    color = blockColor,
                    topLeft = Offset(size.width * 0.68f, size.height * 0.45f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.22f, size.width * 0.22f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Settings button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { onSettingsClicked() }
                            .testTag("menu_settings_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFFE879F9),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Shop / Free Hints Button
                    Surface(
                        onClick = onShopClicked,
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF283141),
                        border = BorderStroke(1.5.dp, if (uiState.isVipAdFree) Color(0xFFF59E0B) else Color(0xFFF472B6)),
                        modifier = Modifier.testTag("menu_shop_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ShoppingBag,
                                contentDescription = "Shop",
                                tint = if (uiState.isVipAdFree) Color(0xFFF59E0B) else Color(0xFFF472B6),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (uiState.isVipAdFree) "VIP PASS" else "Shop (${uiState.hintsRemaining})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Level Selector Star / Grid button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { onLevelSelectClicked() }
                            .testTag("menu_levels_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GridOn,
                            contentDescription = "Levels",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title "BLOCK FILL"
                Text(
                    text = "BLOCK FILL",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle Instruction (matching screenshot)
                Text(
                    text = "Draw a line from the starting square that fills all of the squares on the board in order to complete each level!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE2E8F0),
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
            }
        }

        // --- BOTTOM SECTION (Mascot, Difficulty Slider, Play Button, Hills) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.58f)
        ) {
            // Rolling soft hills background at bottom (matching screenshot)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val hillPathBack = Path().apply {
                    moveTo(0f, h * 0.88f)
                    quadraticBezierTo(w * 0.35f, h * 0.83f, w * 0.65f, h * 0.89f)
                    quadraticBezierTo(w * 0.85f, h * 0.93f, w, h * 0.86f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(hillPathBack, Color(0xFFF3E7D3))

                val hillPathFront = Path().apply {
                    moveTo(0f, h * 0.94f)
                    quadraticBezierTo(w * 0.5f, h * 0.90f, w, h * 0.94f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(hillPathFront, Color(0xFFEBDCC4))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Centered Mascot Avatar (with negative offset to overlap the top dark section seamlessly!)
                Box(
                    modifier = Modifier
                        .offset(y = (-55).dp)
                        .testTag("mascot_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = currentDiff,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "mascot_anim"
                    ) { targetDiff ->
                        MascotVisual(difficulty = targetDiff, size = 135.dp)
                    }
                }

                Spacer(modifier = Modifier.height((-30).dp))

                // Difficulty Title (e.g. "MEDIUM", "HARD", "EXTRA HARD")
                Text(
                    text = currentDiff.title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = currentDiff.primaryColor,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = currentDiff.subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Difficulty Selector Bar / Slider (matching screenshots!)
                DifficultySliderBar(
                    currentDifficulty = currentDiff,
                    onSelectDifficulty = onSelectDifficulty,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Daily Challenge Banner Card (prominent retention feature with streak & leaderboard)
                Surface(
                    onClick = onDailyChallengeClicked,
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_daily_challenge_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.EmojiEvents,
                                    contentDescription = "Daily Challenge",
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "DAILY CHALLENGE",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1E2530),
                                        letterSpacing = 0.5.sp
                                    )
                                    if (uiState.dailyChallengeData?.isCompleted == true) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "✅ Done",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF059669)
                                        )
                                    }
                                }

                                Text(
                                    text = if (uiState.dailyChallengeData?.isCompleted == true) {
                                        "Rank #${uiState.dailyChallengeData.playerRank} • ${uiState.dailyStreak}d streak 🔥"
                                    } else {
                                        "Today's Puzzle • +100 Coins & Leaderboard"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (uiState.dailyChallengeData?.isCompleted == true) Color(0xFFFEF3C7) else Color(0xFFF59E0B)
                        ) {
                            Text(
                                text = if (uiState.dailyChallengeData?.isCompleted == true) "RANKINGS" else "PLAY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (uiState.dailyChallengeData?.isCompleted == true) Color(0xFFB45309) else Color(0xFF1E2530),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // PLAY BUTTON and HELP BUTTON ROW
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 36.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Big Play Button
                    Button(
                        onClick = onPlayClicked,
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                            .shadow(6.dp, RoundedCornerShape(20.dp))
                            .testTag("play_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = currentDiff.primaryColor),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "PLAY",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Level ${uiState.currentLevelNumber}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.95f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Purple Help Button (matching screenshot)
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .shadow(6.dp, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF8B5CF6))
                            .clickable { onHelpClicked() }
                            .testTag("help_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultySliderBar(
    currentDifficulty: Difficulty,
    onSelectDifficulty: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    val difficulties = Difficulty.values()
    val currentIndex = difficulties.indexOf(currentDifficulty)

    // Background track with rounded pill container (as seen in screenshots)
    Box(
        modifier = modifier
            .height(54.dp)
            .shadow(3.dp, RoundedCornerShape(27.dp))
            .clip(RoundedCornerShape(27.dp))
            .background(Color.White)
            .padding(5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Track gradient / active fill
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val ratio = (offset.x / size.width).coerceIn(0f, 1f)
                        val targetIndex = (ratio * difficulties.size).toInt().coerceIn(0, difficulties.size - 1)
                        onSelectDifficulty(difficulties[targetIndex])
                    }
                }
        ) {
            val trackHeight = size.height
            val trackWidth = size.width
            val progress = (currentIndex + 0.5f) / difficulties.size

            // Inactive track area
            drawRoundRect(
                color = Color(0xFFE2E8F0),
                size = androidx.compose.ui.geometry.Size(trackWidth, trackHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(trackHeight / 2f, trackHeight / 2f)
            )

            // Active track fill up to thumb
            val activeWidth = (trackWidth * progress).coerceIn(trackHeight, trackWidth)
            drawRoundRect(
                color = currentDifficulty.primaryColor,
                size = androidx.compose.ui.geometry.Size(activeWidth, trackHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(trackHeight / 2f, trackHeight / 2f)
            )

            // Thumb circle
            val thumbRadius = trackHeight * 0.42f
            val thumbCenterX = (trackWidth * progress).coerceIn(thumbRadius + 4f, trackWidth - thumbRadius - 4f)
            val thumbCenterY = trackHeight / 2f

            // White thumb border
            drawCircle(
                color = Color.White,
                radius = thumbRadius + 5f,
                center = Offset(thumbCenterX, thumbCenterY)
            )
            // Color thumb
            drawCircle(
                color = currentDifficulty.primaryColor,
                radius = thumbRadius,
                center = Offset(thumbCenterX, thumbCenterY)
            )
        }
    }
}
