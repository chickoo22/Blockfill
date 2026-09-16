package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey
    val key: String, // e.g. "MEDIUM_11"
    val difficulty: String,
    val levelNumber: Int,
    val completed: Boolean = false,
    val stars: Int = 0,
    val bestTimeSec: Long = 0L
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val hintsRemaining: Int = 5,
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val selectedDifficulty: String = "MEDIUM",
    val isVipAdFree: Boolean = false,
    val coins: Int = 50,
    val dailyStreak: Int = 0,
    val bestDailyStreak: Int = 0,
    val lastDailyDate: String = "",
    val hasCompletedTutorial: Boolean = false
)

@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey
    val date: String, // e.g. "2026-09-13"
    val completed: Boolean = false,
    val bestTimeSec: Long = 0L,
    val stars: Int = 3,
    val streak: Int = 1,
    val rank: Int = 0,
    val completedTimestamp: Long = 0L
)
