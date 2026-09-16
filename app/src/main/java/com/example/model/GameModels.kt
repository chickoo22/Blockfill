package com.example.model

import androidx.compose.ui.graphics.Color

data class Cell(val row: Int, val col: Int) {
    fun isAdjacentTo(other: Cell): Boolean {
        val dr = kotlin.math.abs(row - other.row)
        val dc = kotlin.math.abs(col - other.col)
        return (dr == 1 && dc == 0) || (dr == 0 && dc == 1)
    }
}

enum class Difficulty(
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val pathFillColor: Color,
    val pathStrokeColor: Color,
    val levelRange: IntRange,
    val trackProgress: Float
) {
    EASY(
        title = "EASY",
        subtitle = "Levels 1 - 60",
        primaryColor = Color(0xFF10B981), // Emerald
        pathFillColor = Color(0xFFA7F3D0),
        pathStrokeColor = Color(0xFF059669),
        levelRange = 1..60,
        trackProgress = 0.10f
    ),
    MEDIUM(
        title = "MEDIUM",
        subtitle = "Levels 61 - 120",
        primaryColor = Color(0xFFF59E0B), // Vibrant Amber / Orange
        pathFillColor = Color(0xFFFED7AA),
        pathStrokeColor = Color(0xFFD97706),
        levelRange = 61..120,
        trackProgress = 0.32f
    ),
    HARD(
        title = "HARD",
        subtitle = "Levels 121 - 180",
        primaryColor = Color(0xFFEF4444), // Coral Red
        pathFillColor = Color(0xFFFECACA),
        pathStrokeColor = Color(0xFFDC2626),
        levelRange = 121..180,
        trackProgress = 0.55f
    ),
    EXTRA_HARD(
        title = "EXTRA HARD",
        subtitle = "Levels 181 - 240",
        primaryColor = Color(0xFF6366F1), // Deep Violet / Indigo
        pathFillColor = Color(0xFFC7D2FE),
        pathStrokeColor = Color(0xFF4F46E5),
        levelRange = 181..240,
        trackProgress = 0.78f
    ),
    MASTER(
        title = "MASTER",
        subtitle = "Levels 241 - 300",
        primaryColor = Color(0xFFD946EF), // Fuchsia / Magenta Grandmaster
        pathFillColor = Color(0xFFF5D0FE),
        pathStrokeColor = Color(0xFFC026D3),
        levelRange = 241..300,
        trackProgress = 1.0f
    ),
    DAILY(
        title = "DAILY",
        subtitle = "Daily Challenge",
        primaryColor = Color(0xFFF59E0B), // Amber / Golden Sun
        pathFillColor = Color(0xFFFEF3C7),
        pathStrokeColor = Color(0xFFD97706),
        levelRange = 0..0,
        trackProgress = 1.0f
    );

    companion object {
        fun forLevel(levelNumber: Int): Difficulty = when {
            levelNumber == 0 -> DAILY
            levelNumber <= 60 -> EASY
            levelNumber <= 120 -> MEDIUM
            levelNumber <= 180 -> HARD
            levelNumber <= 240 -> EXTRA_HARD
            else -> MASTER
        }
    }
}

data class Level(
    val levelNumber: Int,
    val difficulty: Difficulty,
    val rows: Int,
    val cols: Int,
    val activeCells: Set<Cell>,
    val obstacles: Set<Cell> = emptySet(),
    val startCell: Cell,
    val solution: List<Cell>
)

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val avatarEmoji: String,
    val timeSec: Long,
    val isPlayer: Boolean = false,
    val countryCode: String = "GLOBAL",
    val streak: Int = 1
)

data class DailyChallengeData(
    val dateString: String,
    val formattedDate: String,
    val dayOfWeek: String,
    val isCompleted: Boolean = false,
    val bestTimeSec: Long = 0L,
    val playerRank: Int = 0,
    val streak: Int = 0,
    val level: Level,
    val leaderboard: List<LeaderboardEntry> = emptyList()
)
