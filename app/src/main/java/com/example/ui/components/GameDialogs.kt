package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.LevelProgressEntity
import com.example.model.Difficulty

@Composable
fun LevelCompleteDialog(
    difficulty: Difficulty,
    levelNumber: Int,
    elapsedSeconds: Long,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onMenu: () -> Unit
) {
    val isGameFinished = levelNumber >= 300

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF262F3D)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .testTag("level_complete_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isGameFinished) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = "Grand Champion",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "GRAND VICTORY!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD700),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "All 300 Levels Completed!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "LEVEL COMPLETE!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${difficulty.title} • Level $levelNumber of 300",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = difficulty.primaryColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mascot avatar celebrating!
                MascotVisual(difficulty = difficulty, size = 110.dp)

                Spacer(modifier = Modifier.height(16.dp))

                // Stars Row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Star",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Time: ${elapsedSeconds}s",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFCBD5E1)
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!isGameFinished) {
                    // Next Level Button
                    Button(
                        onClick = onNextLevel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("next_level_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = difficulty.primaryColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Next Level",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NEXT LEVEL (${levelNumber + 1})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReplay,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("replay_button"),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Replay",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Replay",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Button(
                        onClick = onMenu,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("dialog_menu_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF384353)),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "Menu",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LevelSelectDialog(
    currentLevel: Int,
    completedLevels: Map<Int, LevelProgressEntity>,
    onSelectLevel: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedLevel by remember { mutableIntStateOf(currentLevel.coerceIn(1, 300)) }
    val selectedTier = Difficulty.forLevel(selectedLevel)

    // A level is unlocked if:
    // 1. It is level 1, OR
    // 2. The previous level is completed, OR
    // 3. It's the first level of each difficulty tier (1, 61, 121, 181, 241), OR
    // 4. Pre-unlock first 12 levels
    val isSelectedUnlocked = selectedLevel == 1 ||
            selectedLevel == selectedTier.levelRange.first ||
            completedLevels[selectedLevel - 1]?.completed == true ||
            selectedLevel <= 12

    val isSelectedCompleted = completedLevels[selectedLevel]?.completed == true
    val isSelectedCurrent = selectedLevel == currentLevel

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF202734)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("level_select_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SELECT LEVEL",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "300 Levels • Offline & Online Ready",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Difficulty Tier Fast-Jump Chips
                val allTiers = Difficulty.values()
                ScrollableTabRow(
                    selectedTabIndex = allTiers.indexOf(selectedTier),
                    containerColor = Color(0xFF161C26),
                    contentColor = Color.White,
                    edgePadding = 4.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[allTiers.indexOf(selectedTier)]),
                            color = selectedTier.primaryColor,
                            height = 3.dp
                        )
                    }
                ) {
                    allTiers.forEach { tier ->
                        val isTierActive = tier == selectedTier
                        Tab(
                            selected = isTierActive,
                            onClick = { selectedLevel = tier.levelRange.first },
                            text = {
                                Text(
                                    text = "${tier.title}\n(${tier.levelRange.first}-${tier.levelRange.last})",
                                    fontSize = 11.sp,
                                    fontWeight = if (isTierActive) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isTierActive) tier.primaryColor else Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // --- LEVEL PREVIEW & JUMP CARD ---
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161C26)),
                    border = BorderStroke(1.5.dp, selectedTier.primaryColor.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "LEVEL $selectedLevel",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                val gridDescriptor = when {
                                    selectedLevel <= 5 -> "3x3 Grid • Intro"
                                    selectedLevel <= 18 -> "4x4 Grid • Light"
                                    selectedLevel <= 60 -> "5x5 Grid • Easy"
                                    selectedLevel <= 120 -> "6x6 Grid • Medium"
                                    selectedLevel <= 180 -> "7x7 Grid • Hard"
                                    selectedLevel <= 240 -> "8x8 Grid • Expert"
                                    else -> "9x9 Grid • Master"
                                }
                                Text(
                                    text = "${selectedTier.title} • $gridDescriptor",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = selectedTier.primaryColor
                                )
                            }

                            // Completion or Lock status pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when {
                                            isSelectedCompleted -> Color(0xFF10B981).copy(alpha = 0.2f)
                                            isSelectedCurrent -> selectedTier.primaryColor.copy(alpha = 0.2f)
                                            isSelectedUnlocked -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                                            else -> Color(0xFF64748B).copy(alpha = 0.2f)
                                        }
                                    )
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = when {
                                            isSelectedCompleted -> Icons.Rounded.Check
                                            isSelectedCurrent -> Icons.Rounded.PlayArrow
                                            isSelectedUnlocked -> Icons.Rounded.Star
                                            else -> Icons.Rounded.Lock
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            isSelectedCompleted -> Color(0xFF10B981)
                                            isSelectedCurrent -> selectedTier.primaryColor
                                            isSelectedUnlocked -> Color(0xFF3B82F6)
                                            else -> Color(0xFF94A3B8)
                                        },
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = when {
                                            isSelectedCompleted -> "Cleared"
                                            isSelectedCurrent -> "Current"
                                            isSelectedUnlocked -> "Ready"
                                            else -> "Locked"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isSelectedCompleted -> Color(0xFF10B981)
                                            isSelectedCurrent -> selectedTier.primaryColor
                                            isSelectedUnlocked -> Color(0xFF3B82F6)
                                            else -> Color(0xFF94A3B8)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // --- LEVEL SLIDER CONTROLS ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // -10 and -1 step buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                IconButton(
                                    onClick = { selectedLevel = (selectedLevel - 10).coerceAtLeast(1) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.FastRewind,
                                        contentDescription = "-10 Levels",
                                        tint = if (selectedLevel > 1) Color.White else Color(0xFF475569),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { selectedLevel = (selectedLevel - 1).coerceAtLeast(1) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronLeft,
                                        contentDescription = "-1 Level",
                                        tint = if (selectedLevel > 1) Color.White else Color(0xFF475569),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Slider (1 to 300)
                            Slider(
                                value = selectedLevel.toFloat(),
                                onValueChange = { selectedLevel = it.toInt().coerceIn(1, 300) },
                                valueRange = 1f..300f,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .testTag("level_select_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = selectedTier.primaryColor,
                                    activeTrackColor = selectedTier.primaryColor,
                                    inactiveTrackColor = Color(0xFF334155)
                                )
                            )

                            // +1 and +10 step buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                IconButton(
                                    onClick = { selectedLevel = (selectedLevel + 1).coerceAtMost(300) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronRight,
                                        contentDescription = "+1 Level",
                                        tint = if (selectedLevel < 300) Color.White else Color(0xFF475569),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { selectedLevel = (selectedLevel + 10).coerceAtMost(300) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.FastForward,
                                        contentDescription = "+10 Levels",
                                        tint = if (selectedLevel < 300) Color.White else Color(0xFF475569),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Play Level Button
                        Button(
                            onClick = {
                                onSelectLevel(selectedLevel)
                                onDismiss()
                            },
                            enabled = isSelectedUnlocked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("slider_play_level_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = selectedTier.primaryColor)
                        ) {
                            Icon(
                                imageVector = if (isSelectedUnlocked) Icons.Rounded.PlayArrow else Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSelectedUnlocked) "PLAY LEVEL $selectedLevel" else "LOCKED • CLEAR LEVEL ${selectedLevel - 1}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tier Grid of levels for direct tapping
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedTier.title} Levels (${selectedTier.levelRange.first}-${selectedTier.levelRange.last})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Tap block to preview",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val levelsInTier = selectedTier.levelRange.toList()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(levelsInTier) { lvl ->
                        val isCompleted = completedLevels[lvl]?.completed == true
                        val isUnlocked = lvl == 1 ||
                                lvl == selectedTier.levelRange.first ||
                                completedLevels[lvl - 1]?.completed == true ||
                                lvl <= 12

                        val isSelected = lvl == selectedLevel
                        val isCurrent = lvl == currentLevel

                        val bgColor = when {
                            isSelected -> selectedTier.primaryColor
                            isCompleted -> Color(0xFF1E293B)
                            isUnlocked -> Color(0xFF334155)
                            else -> Color(0xFF161C26)
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(bgColor)
                                .then(
                                    if (isSelected) Modifier.border(2.dp, Color.White, RoundedCornerShape(10.dp))
                                    else if (isCurrent) Modifier.border(1.5.dp, Color(0xFFF59E0B), RoundedCornerShape(10.dp))
                                    else Modifier
                                )
                                .clickable(enabled = isUnlocked) {
                                    selectedLevel = lvl
                                }
                                .testTag("level_item_$lvl"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!isUnlocked) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "Locked",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(14.dp)
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "$lvl",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else if (isCompleted) selectedTier.primaryColor else Color.White
                                    )
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = "Completed",
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    isVip: Boolean = false,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onOpenShop: () -> Unit = {},
    onRestorePurchases: () -> Unit = {},
    onOpenPrivacyPolicy: () -> Unit = {},
    onAddHints: () -> Unit,
    onResetAllProgress: () -> Unit,
    onDismiss: () -> Unit
) {
    var showResetConfirmation by remember { mutableStateOf(false) }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Rounded.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Progress?", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    "This will reset completion records for all 300 levels. Are you sure?",
                    color = Color(0xFFCBD5E1)
                )
            },
            containerColor = Color(0xFF1E2530),
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmation = false
                        onResetAllProgress()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Reset All", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF262F3D)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("settings_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SETTINGS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound Effects", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { onToggleSound() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFF59E0B))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Feedback", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { onToggleHaptics() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFF59E0B))
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Game Store & Free Hints Button
                Button(
                    onClick = {
                        onDismiss()
                        onOpenShop()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("settings_shop_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.ShoppingBag, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Game Store & Hints", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Privacy and Play Safety Button
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onOpenPrivacyPolicy()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("settings_privacy_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Security, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Privacy and Play Safety",
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Reset Progress
                OutlinedButton(
                    onClick = { showResetConfirmation = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reset_progress_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset Progress", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // About Block Fill Version
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E2430))
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Block Fill • v1.0.0",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun HelpDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF262F3D)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("help_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HOW TO PLAY",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Draw a continuous path from the starting square to fill every single block on the grid!",
                    fontSize = 15.sp,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E2430))
                        .padding(16.dp)
                ) {
                    Text("• Drag your finger across adjacent blocks to fill them.", color = Color(0xFFE2E8F0), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Retrace backward anytime to undo previous steps.", color = Color(0xFFE2E8F0), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Tap the Restart button to begin fresh.", color = Color(0xFFE2E8F0), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Stuck? Tap the Lightbulb for smart hints!", color = Color(0xFFE2E8F0), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Progress through 300 levels with growing complexity!", color = Color(0xFFE2E8F0), fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("GOT IT!", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
