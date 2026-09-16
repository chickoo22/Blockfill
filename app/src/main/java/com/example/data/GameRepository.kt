package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {

    fun getAllProgress(): Flow<List<LevelProgressEntity>> {
        return gameDao.getAllProgress()
    }

    fun getProgressForDifficulty(difficulty: String): Flow<List<LevelProgressEntity>> {
        return gameDao.getProgressForDifficulty(difficulty)
    }

    suspend fun getProgress(difficulty: String, levelNumber: Int): LevelProgressEntity? {
        return gameDao.getProgressByKey("${difficulty}_$levelNumber")
    }

    suspend fun markLevelCompleted(difficulty: String, levelNumber: Int, stars: Int, timeSec: Long) {
        val existing = gameDao.getProgressByKey("${difficulty}_$levelNumber")
        val bestTime = if (existing != null && existing.bestTimeSec > 0) {
            minOf(existing.bestTimeSec, timeSec)
        } else {
            timeSec
        }
        val bestStars = if (existing != null) maxOf(existing.stars, stars) else stars
        gameDao.saveProgress(
            LevelProgressEntity(
                key = "${difficulty}_$levelNumber",
                difficulty = difficulty,
                levelNumber = levelNumber,
                completed = true,
                stars = bestStars,
                bestTimeSec = bestTime
            )
        )
    }

    suspend fun clearAllProgress() {
        gameDao.clearAllProgress()
    }

    fun getUserSettings(): Flow<UserSettingsEntity?> {
        return gameDao.getUserSettings()
    }

    suspend fun saveUserSettings(settings: UserSettingsEntity) {
        gameDao.saveUserSettings(settings)
    }

    suspend fun getDailyChallenge(date: String): DailyChallengeEntity? {
        return gameDao.getDailyChallenge(date)
    }

    fun getAllDailyChallenges(): Flow<List<DailyChallengeEntity>> {
        return gameDao.getAllDailyChallenges()
    }

    suspend fun saveDailyChallenge(daily: DailyChallengeEntity) {
        gameDao.saveDailyChallenge(daily)
    }
}
