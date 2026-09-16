package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM level_progress ORDER BY levelNumber ASC")
    fun getAllProgress(): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE difficulty = :difficulty ORDER BY levelNumber ASC")
    fun getProgressForDifficulty(difficulty: String): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE key = :key LIMIT 1")
    suspend fun getProgressByKey(key: String): LevelProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: LevelProgressEntity)

    @Query("DELETE FROM level_progress")
    suspend fun clearAllProgress()

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getUserSettings(): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSettings(settings: UserSettingsEntity)

    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    suspend fun getDailyChallenge(date: String): DailyChallengeEntity?

    @Query("SELECT * FROM daily_challenges ORDER BY date DESC")
    fun getAllDailyChallenges(): Flow<List<DailyChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDailyChallenge(daily: DailyChallengeEntity)
}
