package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DailyChallengeEntity
import com.example.data.GameRepository
import com.example.data.LevelProgressEntity
import com.example.data.UserSettingsEntity
import com.example.game.DailyChallengeProvider
import com.example.game.LevelProvider
import com.example.model.Cell
import com.example.model.DailyChallengeData
import com.example.model.Difficulty
import com.example.model.Level
import com.example.util.NetworkMonitor
import com.example.util.SoundHapticsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    MENU,
    PLAY
}

data class GameUiState(
    val currentScreen: AppScreen = AppScreen.MENU,
    val selectedDifficulty: Difficulty = Difficulty.MEDIUM,
    val currentLevelNumber: Int = 10,
    val level: Level = LevelProvider.getLevel(10),
    val currentPath: List<Cell> = listOf(LevelProvider.getLevel(10).startCell),
    val isCompleted: Boolean = false,
    val elapsedSeconds: Long = 0L,
    val hintsRemaining: Int = 5,
    val activeHintIndices: Set<Int> = emptySet(),
    val completedLevels: Map<Int, LevelProgressEntity> = emptyMap(),
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val isVipAdFree: Boolean = false,
    val isOnline: Boolean = false,
    val coins: Int = 50,
    val isDailyChallenge: Boolean = false,
    val dailyChallengeData: com.example.model.DailyChallengeData? = null,
    val showDailyDialog: Boolean = false,
    val showDailyWinDialog: Boolean = false,
    val dailyWinRank: Int = 0,
    val showTutorialOverlay: Boolean = false,
    val hasCompletedTutorial: Boolean = false,
    val dailyStreak: Int = 0,
    val showResetTutorial: Boolean = false,
    val showLevelSelectDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val showHelpDialog: Boolean = false,
    val showShopDialog: Boolean = false,
    val showRewardedAdDialog: Boolean = false,
    val showInterstitialAdDialog: Boolean = false,
    val showPrivacyPolicyDialog: Boolean = false,
    val celebrationTrigger: Long = 0L
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(AppDatabase.getDatabase(application).gameDao())
    val soundHaptics = SoundHapticsManager(application)
    private val networkMonitor = NetworkMonitor(application)

    private val _uiState = MutableStateFlow(
        GameUiState(isOnline = networkMonitor.isCurrentlyOnline())
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var progressJob: Job? = null
    private var levelsCompletedSinceAd = 0
    private var lastDailyDate: String = ""
    private var bestDailyStreak: Int = 0

    init {
        // Observe real-time network status (offline play is 100% smooth; ads only display when online)
        viewModelScope.launch {
            networkMonitor.isOnline.collectLatest { online ->
                _uiState.update { it.copy(isOnline = online) }
            }
        }

        // Load user settings
        viewModelScope.launch {
            repository.getUserSettings().collectLatest { settings ->
                if (settings != null) {
                    val diff = try {
                        Difficulty.valueOf(settings.selectedDifficulty)
                    } catch (_: Exception) {
                        Difficulty.MEDIUM
                    }
                    soundHaptics.hapticsEnabled = settings.hapticsEnabled
                    soundHaptics.soundEnabled = settings.soundEnabled
                    lastDailyDate = settings.lastDailyDate
                    bestDailyStreak = settings.bestDailyStreak

                    _uiState.update {
                        it.copy(
                            selectedDifficulty = diff,
                            hintsRemaining = settings.hintsRemaining,
                            soundEnabled = settings.soundEnabled,
                            hapticsEnabled = settings.hapticsEnabled,
                            isVipAdFree = settings.isVipAdFree,
                            coins = settings.coins,
                            dailyStreak = settings.dailyStreak,
                            hasCompletedTutorial = settings.hasCompletedTutorial
                        )
                    }
                    loadDailyChallengeData(settings.dailyStreak, settings.lastDailyDate)
                } else {
                    val defaultSettings = UserSettingsEntity()
                    repository.saveUserSettings(defaultSettings)
                    loadDailyChallengeData(0, "")
                }
            }
        }

        // Observe progress across all 300 levels
        observeAllProgress()
    }

    fun loadDailyChallengeData(currentStreak: Int? = null, lastDateStr: String? = null) {
        viewModelScope.launch {
            val today = DailyChallengeProvider.getDateString()
            val existing = repository.getDailyChallenge(today)
            val streakVal = currentStreak ?: _uiState.value.dailyStreak
            val lastDate = lastDateStr ?: lastDailyDate

            val (leaderboard, rank) = DailyChallengeProvider.getLeaderboard(
                playerTimeSec = if (existing != null && existing.completed) existing.bestTimeSec else null,
                playerStreak = maxOf(1, streakVal)
            )

            val dailyLevel = DailyChallengeProvider.generateDailyLevel()
            val data = DailyChallengeData(
                dateString = today,
                formattedDate = DailyChallengeProvider.getFormattedDate(),
                dayOfWeek = DailyChallengeProvider.getDayOfWeekName(),
                isCompleted = existing?.completed ?: false,
                bestTimeSec = existing?.bestTimeSec ?: 0L,
                playerRank = if (existing?.completed == true) (if (existing.rank > 0) existing.rank else rank) else 0,
                streak = streakVal,
                level = dailyLevel,
                leaderboard = leaderboard
            )

            _uiState.update { it.copy(dailyChallengeData = data) }
        }
    }

    fun openDailyChallengeHub() {
        soundHaptics.playButtonSound()
        loadDailyChallengeData()
        _uiState.update { it.copy(showDailyDialog = true) }
    }

    fun closeDailyChallengeHub() {
        soundHaptics.playButtonSound()
        _uiState.update { it.copy(showDailyDialog = false) }
    }

    fun startDailyChallenge() {
        soundHaptics.playButtonSound()
        stopTimer()
        val dailyLevel = DailyChallengeProvider.generateDailyLevel()
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.PLAY,
                isDailyChallenge = true,
                selectedDifficulty = Difficulty.DAILY,
                currentLevelNumber = 0,
                level = dailyLevel,
                currentPath = listOf(dailyLevel.startCell),
                isCompleted = false,
                elapsedSeconds = 0L,
                activeHintIndices = emptySet(),
                showTutorialOverlay = false,
                showResetTutorial = false,
                showDailyDialog = false,
                showDailyWinDialog = false
            )
        }
        startTimer()
    }

    fun dismissDailyWinDialog() {
        soundHaptics.playButtonSound()
        _uiState.update { it.copy(showDailyWinDialog = false) }
    }

    fun dismissTutorialOverlay() {
        _uiState.update { it.copy(showTutorialOverlay = false, hasCompletedTutorial = true) }
        saveSettings()
    }

    private fun observeAllProgress() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            repository.getAllProgress().collectLatest { list ->
                val map = list.associateBy { it.levelNumber }
                _uiState.update { current ->
                    current.copy(completedLevels = map)
                }
            }
        }
    }

    fun selectDifficulty(difficulty: Difficulty) {
        soundHaptics.playButtonSound()
        val range = difficulty.levelRange
        val state = _uiState.value

        // Find highest unlocked or completed level in this difficulty tier
        val completedInTier = state.completedLevels.values.filter { it.levelNumber in range && it.completed }
        val highestCompleted = completedInTier.maxOfOrNull { it.levelNumber }
        val targetLevel = when {
            highestCompleted != null && highestCompleted < range.last -> highestCompleted + 1
            highestCompleted == range.last -> range.last
            difficulty == Difficulty.MEDIUM -> 61
            difficulty == Difficulty.HARD -> 121
            difficulty == Difficulty.EXTRA_HARD -> 181
            difficulty == Difficulty.MASTER -> 241
            else -> range.first
        }

        val newLevel = LevelProvider.getLevel(targetLevel)
        _uiState.update {
            it.copy(
                selectedDifficulty = difficulty,
                currentLevelNumber = targetLevel,
                level = newLevel,
                currentPath = listOf(newLevel.startCell),
                isCompleted = false,
                activeHintIndices = emptySet()
            )
        }
        saveSettings()
    }

    fun startGame(levelNumber: Int? = null) {
        soundHaptics.playButtonSound()
        val targetLevelNum = (levelNumber ?: _uiState.value.currentLevelNumber).coerceIn(1, 300)
        loadLevel(targetLevelNum)
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.PLAY,
                showResetTutorial = (targetLevelNum == 10)
            )
        }
    }

    fun backToMenu() {
        soundHaptics.playButtonSound()
        stopTimer()
        _uiState.update { it.copy(currentScreen = AppScreen.MENU, showResetTutorial = false) }
    }

    fun loadLevel(levelNumber: Int) {
        stopTimer()
        val clamped = levelNumber.coerceIn(1, 300)
        val difficulty = Difficulty.forLevel(clamped)
        val level = LevelProvider.getLevel(clamped)
        val showTutorial = (clamped == 1 && !_uiState.value.hasCompletedTutorial)

        _uiState.update {
            it.copy(
                isDailyChallenge = false,
                selectedDifficulty = difficulty,
                currentLevelNumber = clamped,
                level = level,
                currentPath = listOf(level.startCell),
                isCompleted = false,
                elapsedSeconds = 0L,
                activeHintIndices = emptySet(),
                showResetTutorial = (clamped == 10),
                showTutorialOverlay = showTutorial
            )
        }
        startTimer()
        saveSettings()
    }

    fun nextLevel() {
        val nextNum = _uiState.value.currentLevelNumber + 1
        if (nextNum <= 300) {
            loadLevel(nextNum)
        }
    }

    /**
     * Reverts the player's last move on the current puzzle grid.
     */
    fun undoMove() {
        val state = _uiState.value
        if (state.isCompleted || state.currentPath.size <= 1) return

        val newPath = state.currentPath.dropLast(1)
        _uiState.update {
            it.copy(
                currentPath = newPath,
                showResetTutorial = false
            )
        }
        soundHaptics.playUndoSound()
        soundHaptics.playBacktrackHaptic()
    }

    fun resetCurrentLevel() {
        val start = _uiState.value.level.startCell
        _uiState.update {
            it.copy(
                currentPath = listOf(start),
                isCompleted = false,
                activeHintIndices = emptySet(),
                showResetTutorial = false
            )
        }
        soundHaptics.playResetSound()
        soundHaptics.playBacktrackHaptic()
    }

    fun dismissTutorial() {
        _uiState.update { it.copy(showResetTutorial = false) }
    }

    fun handleObstacleHit(cell: Cell) {
        soundHaptics.playBlockedHaptic()
        soundHaptics.playBlockedSound()
    }

    fun handleCellHover(cell: Cell) {
        val state = _uiState.value
        if (state.isCompleted) return

        val level = state.level
        if (cell in level.obstacles) {
            handleObstacleHit(cell)
            return
        }
        if (cell !in level.activeCells) return

        val path = state.currentPath
        val head = path.last()

        // 1. Touching head: nothing to do
        if (cell == head) return

        // 2. Backtrack 1 step if touching previous cell
        if (path.size > 1 && cell == path[path.size - 2]) {
            val newPath = path.dropLast(1)
            _uiState.update { it.copy(currentPath = newPath, showResetTutorial = false) }
            soundHaptics.playUndoSound()
            soundHaptics.playBacktrackHaptic()
            return
        }

        // 3. Step forward if orthogonal unvisited neighbor
        val isOrthogonal = (Math.abs(cell.row - head.row) + Math.abs(cell.col - head.col)) == 1
        if (isOrthogonal && cell !in path) {
            val newPath = path + cell
            val isWin = newPath.size == level.activeCells.size

            _uiState.update {
                it.copy(
                    currentPath = newPath,
                    isCompleted = isWin,
                    showResetTutorial = false,
                    celebrationTrigger = if (isWin) System.currentTimeMillis() else it.celebrationTrigger
                )
            }

            if (isWin) {
                onLevelWon()
            } else {
                soundHaptics.playStepHaptic()
                soundHaptics.playStepSound(newPath.size)
            }
        }
    }

    fun handleCellClick(cell: Cell) {
        val state = _uiState.value
        if (state.isCompleted) return

        val level = state.level
        if (cell in level.obstacles) {
            handleObstacleHit(cell)
            return
        }

        val path = state.currentPath
        val idx = path.indexOf(cell)

        // If clicking a cell already in path (before head), rewind back to it
        if (idx in 0 until path.size - 1) {
            val newPath = path.subList(0, idx + 1)
            _uiState.update { it.copy(currentPath = newPath, showResetTutorial = false) }
            soundHaptics.playUndoSound()
            soundHaptics.playBacktrackHaptic()
            return
        }

        // Otherwise handle normal neighbor stepping
        handleCellHover(cell)
    }

    fun useHint() {
        val state = _uiState.value
        if (state.isCompleted) return

        // If out of hints, open the shop or rewarded ad to get more
        if (state.hintsRemaining <= 0) {
            showShop(true)
            return
        }

        val solution = state.level.solution
        val currentPath = state.currentPath

        var matches = true
        for (i in currentPath.indices) {
            if (i >= solution.size || currentPath[i] != solution[i]) {
                matches = false
                break
            }
        }

        val newPath: List<Cell>
        if (!matches) {
            var validPrefixLen = 0
            while (validPrefixLen < currentPath.size &&
                validPrefixLen < solution.size &&
                currentPath[validPrefixLen] == solution[validPrefixLen]
            ) {
                validPrefixLen++
            }
            if (validPrefixLen == 0) validPrefixLen = 1
            newPath = solution.subList(0, validPrefixLen)
        } else {
            val nextLen = minOf(currentPath.size + 2, solution.size)
            newPath = solution.subList(0, nextLen)
        }

        val isWin = newPath.size == state.level.activeCells.size
        val newHints = state.hintsRemaining - 1

        _uiState.update {
            it.copy(
                currentPath = newPath,
                hintsRemaining = newHints,
                isCompleted = isWin,
                celebrationTrigger = if (isWin) System.currentTimeMillis() else it.celebrationTrigger
            )
        }

        soundHaptics.playHintSound()
        soundHaptics.playStepHaptic()
        saveSettings()

        if (isWin) {
            onLevelWon()
        }
    }

    private fun onLevelWon() {
        val state = _uiState.value
        stopTimer()
        soundHaptics.playWinHaptic()
        soundHaptics.playWinSound()

        if (state.isDailyChallenge) {
            val timeTaken = maxOf(1L, state.elapsedSeconds)
            val today = DailyChallengeProvider.getDateString()
            val wasAlreadyCompleted = state.dailyChallengeData?.isCompleted == true

            // Calculate streak
            val newStreak = if (wasAlreadyCompleted) {
                state.dailyStreak
            } else if (lastDailyDate.isNotBlank() && DailyChallengeProvider.isConsecutiveDay(lastDailyDate, today)) {
                state.dailyStreak + 1
            } else if (state.dailyStreak == 0) {
                1
            } else if (lastDailyDate == today) {
                state.dailyStreak
            } else {
                1
            }

            lastDailyDate = today
            bestDailyStreak = maxOf(bestDailyStreak, newStreak)

            val (leaderboard, rank) = DailyChallengeProvider.getLeaderboard(
                playerTimeSec = timeTaken,
                playerStreak = newStreak
            )

            // Rewards: First daily completion gets +100 coins and +2 hints
            val rewardCoins = if (!wasAlreadyCompleted) 100 else 25
            val rewardHints = if (!wasAlreadyCompleted) 2 else 0

            viewModelScope.launch {
                repository.saveDailyChallenge(
                    DailyChallengeEntity(
                        date = today,
                        completed = true,
                        bestTimeSec = if (state.dailyChallengeData?.bestTimeSec != null && state.dailyChallengeData.bestTimeSec > 0) {
                            minOf(state.dailyChallengeData.bestTimeSec, timeTaken)
                        } else timeTaken,
                        rank = rank,
                        streak = newStreak,
                        completedTimestamp = System.currentTimeMillis()
                    )
                )
            }

            _uiState.update { current ->
                val updatedDailyData = current.dailyChallengeData?.copy(
                    isCompleted = true,
                    bestTimeSec = timeTaken,
                    playerRank = rank,
                    streak = newStreak,
                    leaderboard = leaderboard
                )
                current.copy(
                    dailyChallengeData = updatedDailyData,
                    dailyStreak = newStreak,
                    coins = current.coins + rewardCoins,
                    hintsRemaining = current.hintsRemaining + rewardHints,
                    dailyWinRank = rank,
                    showDailyWinDialog = true
                )
            }
            saveSettings()
        } else {
            recordLevelCompletion(state.selectedDifficulty, state.currentLevelNumber, state.elapsedSeconds)
            if (state.currentLevelNumber == 1) {
                _uiState.update { it.copy(hasCompletedTutorial = true) }
            }
            saveSettings()

            // Trigger interstitial ad after completing levels if online and ad-supported
            levelsCompletedSinceAd++
            if (_uiState.value.isOnline && !_uiState.value.isVipAdFree && levelsCompletedSinceAd >= 2) {
                levelsCompletedSinceAd = 0
                _uiState.update { it.copy(showInterstitialAdDialog = true) }
            }
        }
    }

    // Revenue / Monetization Actions
    fun rewardFromAd() {
        val newHints = _uiState.value.hintsRemaining + 2
        _uiState.update {
            it.copy(
                hintsRemaining = newHints,
                showRewardedAdDialog = false
            )
        }
        soundHaptics.playPurchaseSound()
        saveSettings()
    }

    fun buyHintPack(count: Int) {
        val newHints = _uiState.value.hintsRemaining + count
        _uiState.update {
            it.copy(
                hintsRemaining = newHints,
                showShopDialog = false
            )
        }
        soundHaptics.playPurchaseSound()
        saveSettings()
    }

    fun buyVipPass() {
        val newHints = _uiState.value.hintsRemaining + 50
        _uiState.update {
            it.copy(
                isVipAdFree = true,
                hintsRemaining = newHints,
                showShopDialog = false
            )
        }
        soundHaptics.playPurchaseSound()
        saveSettings()
    }

    fun restorePurchases() {
        soundHaptics.playButtonSound()
        _uiState.update { it.copy(isVipAdFree = true) }
        saveSettings()
    }

    fun addExtraHints(count: Int = 3) {
        val newCount = _uiState.value.hintsRemaining + count
        _uiState.update { it.copy(hintsRemaining = newCount) }
        saveSettings()
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.clearAllProgress()
            loadLevel(1)
            _uiState.update {
                it.copy(
                    completedLevels = emptyMap(),
                    currentLevelNumber = 1,
                    selectedDifficulty = Difficulty.EASY,
                    hintsRemaining = 5
                )
            }
            saveSettings()
        }
    }

    private fun recordLevelCompletion(difficulty: Difficulty, levelNumber: Int, timeSec: Long) {
        viewModelScope.launch {
            repository.markLevelCompleted(
                difficulty = difficulty.name,
                levelNumber = levelNumber,
                stars = 3,
                timeSec = timeSec
            )
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun toggleSound() {
        val newSound = !_uiState.value.soundEnabled
        soundHaptics.soundEnabled = newSound
        _uiState.update { it.copy(soundEnabled = newSound) }
        if (newSound) soundHaptics.playButtonSound()
        saveSettings()
    }

    fun toggleHaptics() {
        soundHaptics.playButtonSound()
        val newHaptics = !_uiState.value.hapticsEnabled
        soundHaptics.hapticsEnabled = newHaptics
        _uiState.update { it.copy(hapticsEnabled = newHaptics) }
        saveSettings()
    }

    fun showLevelSelect(show: Boolean) {
        soundHaptics.playButtonSound()
        _uiState.update { it.copy(showLevelSelectDialog = show) }
    }

    fun showSettings(show: Boolean) {
        soundHaptics.playButtonSound()
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun showHelp(show: Boolean) {
        soundHaptics.playButtonSound()
        _uiState.update { it.copy(showHelpDialog = show) }
    }

    fun showShop(show: Boolean) {
        soundHaptics.playButtonSound()
        _uiState.update { it.copy(showShopDialog = show) }
    }

    fun showRewardedAd(show: Boolean) {
        soundHaptics.playButtonSound()
        _uiState.update { it.copy(showRewardedAdDialog = show) }
    }

    fun dismissInterstitialAd() {
        soundHaptics.playButtonSound()
        _uiState.update { it.copy(showInterstitialAdDialog = false) }
    }

    fun showPrivacyPolicy(show: Boolean) {
        soundHaptics.playButtonSound()
        _uiState.update { it.copy(showPrivacyPolicyDialog = show) }
    }

    private fun saveSettings() {
        viewModelScope.launch {
            val state = _uiState.value
            repository.saveUserSettings(
                UserSettingsEntity(
                    id = 1,
                    hintsRemaining = state.hintsRemaining,
                    hapticsEnabled = state.hapticsEnabled,
                    soundEnabled = state.soundEnabled,
                    selectedDifficulty = state.selectedDifficulty.name,
                    isVipAdFree = state.isVipAdFree,
                    coins = state.coins,
                    dailyStreak = state.dailyStreak,
                    bestDailyStreak = bestDailyStreak,
                    lastDailyDate = lastDailyDate,
                    hasCompletedTutorial = state.hasCompletedTutorial
                )
            )
        }
    }
}
