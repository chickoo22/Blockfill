package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import com.example.ui.components.DailyChallengeHubDialog
import com.example.ui.components.DailyChallengeWinDialog
import com.example.ui.components.HelpDialog
import com.example.ui.components.InterstitialAdDialog
import com.example.ui.components.LevelSelectDialog
import com.example.ui.components.PrivacyPolicyDialog
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.ShopDialog
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.PlayScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BlockFillApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun BlockFillApp(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState.currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            AppScreen.MENU -> {
                MenuScreen(
                    uiState = uiState,
                    onSelectDifficulty = { diff -> viewModel.selectDifficulty(diff) },
                    onPlayClicked = { viewModel.startGame() },
                    onLevelSelectClicked = { viewModel.showLevelSelect(true) },
                    onSettingsClicked = { viewModel.showSettings(true) },
                    onHelpClicked = { viewModel.showHelp(true) },
                    onShopClicked = { viewModel.showShop(true) },
                    onDailyChallengeClicked = { viewModel.openDailyChallengeHub() }
                )
            }
            AppScreen.PLAY -> {
                PlayScreen(
                    uiState = uiState,
                    onCellHover = { cell -> viewModel.handleCellHover(cell) },
                    onCellClick = { cell -> viewModel.handleCellClick(cell) },
                    onObstacleHit = { cell -> viewModel.handleObstacleHit(cell) },
                    onUndoClicked = { viewModel.undoMove() },
                    onResetClicked = { viewModel.resetCurrentLevel() },
                    onHintClicked = { viewModel.useHint() },
                    onShopClicked = { viewModel.showShop(true) },
                    onBackClicked = { viewModel.backToMenu() },
                    onNextLevel = { viewModel.nextLevel() },
                    onDismissTutorial = { viewModel.dismissTutorial() },
                    onDismissTutorialOverlay = { viewModel.dismissTutorialOverlay() }
                )
            }
        }
    }

    // Level Selector Dialog
    if (uiState.showLevelSelectDialog) {
        LevelSelectDialog(
            currentLevel = uiState.currentLevelNumber,
            completedLevels = uiState.completedLevels,
            onSelectLevel = { levelNum ->
                viewModel.startGame(levelNum)
            },
            onDismiss = { viewModel.showLevelSelect(false) }
        )
    }

    // Settings Dialog
    if (uiState.showSettingsDialog) {
        SettingsDialog(
            soundEnabled = uiState.soundEnabled,
            hapticsEnabled = uiState.hapticsEnabled,
            isVip = uiState.isVipAdFree,
            onToggleSound = { viewModel.toggleSound() },
            onToggleHaptics = { viewModel.toggleHaptics() },
            onOpenShop = { viewModel.showShop(true) },
            onOpenPrivacyPolicy = { viewModel.showPrivacyPolicy(true) },
            onRestorePurchases = { viewModel.restorePurchases() },
            onAddHints = { viewModel.addExtraHints(3) },
            onResetAllProgress = { viewModel.resetAllProgress() },
            onDismiss = { viewModel.showSettings(false) }
        )
    }

    // Help Dialog
    if (uiState.showHelpDialog) {
        HelpDialog(
            onDismiss = { viewModel.showHelp(false) }
        )
    }

    // Game Store / Shop Dialog
    if (uiState.showShopDialog) {
        ShopDialog(
            hintsRemaining = uiState.hintsRemaining,
            isVip = uiState.isVipAdFree,
            onWatchAdClicked = {
                viewModel.showShop(false)
                viewModel.showRewardedAd(true)
            },
            onBuyStarterPack = { viewModel.buyHintPack(5) },
            onBuyMasterPack = { viewModel.buyHintPack(20) },
            onBuyVipPass = { viewModel.buyVipPass() },
            onRestorePurchases = { viewModel.restorePurchases() },
            onDismiss = { viewModel.showShop(false) }
        )
    }

    // Rewarded Video Ad Dialog
    if (uiState.showRewardedAdDialog) {
        RewardedAdDialog(
            onRewardClaimed = { viewModel.rewardFromAd() },
            onDismiss = { viewModel.showRewardedAd(false) },
            isOnline = uiState.isOnline
        )
    }

    // Interstitial Ad Dialog (Online only, non-VIP, periodic after level clears)
    if (uiState.showInterstitialAdDialog) {
        InterstitialAdDialog(
            onDismiss = { viewModel.dismissInterstitialAd() },
            onUpgradeVip = { viewModel.buyVipPass() }
        )
    }

    // Google Play Policy Compliant Privacy Policy Dialog
    if (uiState.showPrivacyPolicyDialog) {
        PrivacyPolicyDialog(
            onDismiss = { viewModel.showPrivacyPolicy(false) }
        )
    }

    // Daily Challenge Hub Dialog (Leaderboard, streak, today's puzzle)
    if (uiState.showDailyDialog && uiState.dailyChallengeData != null) {
        DailyChallengeHubDialog(
            data = uiState.dailyChallengeData!!,
            onStartDailyChallenge = { viewModel.startDailyChallenge() },
            onDismiss = { viewModel.closeDailyChallengeHub() }
        )
    }

    // Daily Challenge Win Celebration Dialog
    if (uiState.showDailyWinDialog && uiState.dailyChallengeData != null) {
        DailyChallengeWinDialog(
            data = uiState.dailyChallengeData!!,
            timeTakenSec = uiState.elapsedSeconds,
            rankAchieved = uiState.dailyWinRank,
            onViewLeaderboard = {
                viewModel.dismissDailyWinDialog()
                viewModel.openDailyChallengeHub()
            },
            onDismiss = { viewModel.dismissDailyWinDialog() }
        )
    }
}
