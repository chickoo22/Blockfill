package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Cell
import com.example.ui.GameUiState
import com.example.ui.components.AdBannerView
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.GameBoard
import com.example.ui.components.LevelCompleteDialog
import com.example.ui.components.TutorialOverlay

@Composable
fun PlayScreen(
    uiState: GameUiState,
    onCellHover: (Cell) -> Unit,
    onCellClick: (Cell) -> Unit,
    onObstacleHit: (Cell) -> Unit = {},
    onUndoClicked: () -> Unit = {},
    onResetClicked: () -> Unit,
    onHintClicked: () -> Unit,
    onShopClicked: () -> Unit = {},
    onBackClicked: () -> Unit,
    onNextLevel: () -> Unit,
    onDismissTutorial: () -> Unit,
    onDismissTutorialOverlay: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val level = uiState.level
    val difficulty = uiState.selectedDifficulty

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1E2530)) // Slate/Navy background matching screenshots
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // --- TOP BAR (matching screenshots) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Back Button (matching screenshot)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onBackClicked() }
                        .testTag("play_back_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFF472B6), // Pinkish tint matching screenshot
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Center Title & Level Badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Difficulty pill badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (uiState.isDailyChallenge) Color(0xFFF59E0B) else Color(0xFF283141))
                                .padding(horizontal = 14.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (uiState.isDailyChallenge) "DAILY CHALLENGE" else difficulty.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (uiState.isDailyChallenge) Color(0xFF1E2530) else Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                        }

                        if (uiState.isVipAdFree && !uiState.isDailyChallenge) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF59E0B))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "VIP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (uiState.isDailyChallenge) {
                            "${uiState.dailyChallengeData?.dayOfWeek ?: "Daily"} Puzzle"
                        } else {
                            "Level ${uiState.currentLevelNumber}"
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    val infoText = if (uiState.isDailyChallenge) {
                        "${uiState.dailyChallengeData?.formattedDate ?: "Today"} • Streak: ${uiState.dailyStreak} 🔥"
                    } else if (level.obstacles.isNotEmpty()) {
                        "${level.rows}×${level.cols} • ${level.obstacles.size} Obstacles"
                    } else {
                        "${level.rows}×${level.cols} Grid"
                    }
                    Text(
                        text = infoText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Circular Reset Button (matching screenshot)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onResetClicked() }
                        .testTag("play_reset_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Reset Level",
                        tint = Color(0xFFF472B6), // Pinkish tint matching screenshot
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Tutorial Tooltip (matching Screenshot 5!)
            AnimatedVisibility(
                visible = uiState.showResetTutorial,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clickable { onDismissTutorial() }
                        .testTag("reset_tutorial_banner")
                ) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF472B6).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.TouchApp,
                                    contentDescription = null,
                                    tint = Color(0xFFF472B6),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Tap the Reset or Undo button to try again!",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Swipe backwards along your path to undo steps anytime.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // --- GAME BOARD AREA ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                GameBoard(
                    level = level,
                    currentPath = uiState.currentPath,
                    isCompleted = uiState.isCompleted,
                    onCellHover = onCellHover,
                    onCellClick = onCellClick,
                    onObstacleHit = onObstacleHit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // --- BOTTOM CONTROLS (Undo, Free Hints Shop, and Hint Lightbulb) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Undo Button
                val canUndo = uiState.currentPath.size > 1 && !uiState.isCompleted
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(if (canUndo) 6.dp else 1.dp, CircleShape)
                        .clip(CircleShape)
                        .background(if (canUndo) Color(0xFF334155) else Color(0xFF222B38))
                        .clickable(enabled = canUndo) { onUndoClicked() }
                        .testTag("play_undo_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Undo,
                        contentDescription = "Undo Last Move",
                        tint = if (canUndo) Color.White else Color(0xFF475569),
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Middle: Free Hints / Shop Pill
                Surface(
                    onClick = onShopClicked,
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF283141),
                    border = BorderStroke(1.dp, Color(0xFF475569)),
                    modifier = Modifier.testTag("play_shop_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircleOutline,
                            contentDescription = "Get Hints",
                            tint = Color(0xFFF472B6),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "+Free Hints",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // --- ENHANCED HINT BUTTON WITH HIGH-POLISH GLOW & UNCLIPPED DYNAMIC BADGE ---
                val hasHints = uiState.hintsRemaining > 0 || uiState.isVipAdFree
                Box(
                    modifier = Modifier.wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Circular clickable hint button
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .shadow(
                                elevation = if (hasHints) 8.dp else 2.dp,
                                shape = CircleShape,
                                spotColor = if (hasHints) Color(0xFFF43F5E) else Color(0xFF10B981)
                            )
                            .clip(CircleShape)
                            .background(
                                brush = if (hasHints) {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFF5283),
                                            Color(0xFFF43F5E),
                                            Color(0xFFBE123C)
                                        )
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF334155),
                                            Color(0xFF1E293B)
                                        )
                                    )
                                }
                            )
                            .border(
                                width = 2.dp,
                                brush = if (hasHints) {
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFFC0D3), Color(0xFFFB7185).copy(alpha = 0.5f))
                                    )
                                } else {
                                    Brush.linearGradient(
                                        listOf(Color(0xFF10B981), Color(0xFF059669))
                                    )
                                },
                                shape = CircleShape
                            )
                            .clickable(enabled = !uiState.isCompleted) {
                                if (hasHints) {
                                    onHintClicked()
                                } else {
                                    onShopClicked()
                                }
                            }
                            .testTag("play_hint_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner specular soft glow
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.25f),
                                            Color.Transparent
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lightbulb,
                                contentDescription = "Hint",
                                tint = if (hasHints) Color(0xFFFFFBEB) else Color(0xFF94A3B8),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Dynamic Badge at Top Right - Unclipped and completely visible
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .shadow(4.dp, CircleShape)
                            .background(
                                color = when {
                                    uiState.isVipAdFree -> Color(0xFFF59E0B)
                                    uiState.hintsRemaining > 0 -> Color(0xFFF59E0B)
                                    else -> Color(0xFF10B981)
                                },
                                shape = CircleShape
                            )
                            .border(2.dp, Color(0xFF1E2530), CircleShape)
                            .defaultMinSize(minWidth = 24.dp, minHeight = 24.dp)
                            .padding(
                                horizontal = if (!hasHints) 6.dp else 5.dp,
                                vertical = 2.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            uiState.isVipAdFree -> {
                                Text(
                                    text = "∞",
                                    color = Color(0xFF1E2530),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            uiState.hintsRemaining > 0 -> {
                                Text(
                                    text = "${uiState.hintsRemaining}",
                                    color = Color(0xFF1E2530),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            else -> {
                                Text(
                                    text = "+FREE",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // --- BOTTOM AD BANNER OR OFFLINE INDICATOR ---
            if (uiState.isOnline && !uiState.isVipAdFree) {
                AdBannerView(
                    onRemoveAdsClicked = onShopClicked,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            } else if (!uiState.isOnline) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Offline Mode • 100% Playable Offline",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Confetti effect on level completion
        ConfettiEffect(trigger = uiState.celebrationTrigger)

        // Level Complete Dialog (Standard Levels)
        if (uiState.isCompleted && !uiState.isDailyChallenge) {
            LevelCompleteDialog(
                difficulty = difficulty,
                levelNumber = uiState.currentLevelNumber,
                elapsedSeconds = uiState.elapsedSeconds,
                onNextLevel = onNextLevel,
                onReplay = onResetClicked,
                onMenu = onBackClicked
            )
        }

        // Interactive Step-by-Step Tutorial Overlay (Guided first-level introduction)
        if (uiState.showTutorialOverlay && !uiState.isDailyChallenge) {
            TutorialOverlay(
                level = level,
                currentPath = uiState.currentPath,
                isCompleted = uiState.isCompleted,
                onDismiss = onDismissTutorialOverlay
            )
        }
    }
}
