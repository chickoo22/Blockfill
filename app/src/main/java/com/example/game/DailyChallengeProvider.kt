package com.example.game

import com.example.model.Cell
import com.example.model.Difficulty
import com.example.model.LeaderboardEntry
import com.example.model.Level
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

object DailyChallengeProvider {

    fun getCurrentCalendar(): Calendar = Calendar.getInstance()

    fun getDateString(calendar: Calendar = getCurrentCalendar()): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(calendar.time)
    }

    fun getFormattedDate(calendar: Calendar = getCurrentCalendar()): String {
        val sdf = SimpleDateFormat("EEEE, MMM d", Locale.US)
        return sdf.format(calendar.time)
    }

    fun getShortDate(calendar: Calendar = getCurrentCalendar()): String {
        val sdf = SimpleDateFormat("MMM d", Locale.US)
        return sdf.format(calendar.time)
    }

    fun getDayOfWeekName(calendar: Calendar = getCurrentCalendar()): String {
        val sdf = SimpleDateFormat("EEEE", Locale.US)
        return sdf.format(calendar.time)
    }

    /**
     * Checks if two dates are consecutive days (e.g. dateA is immediately before dateB)
     */
    fun isConsecutiveDay(prevDateStr: String, currentDateStr: String): Boolean {
        if (prevDateStr.isBlank()) return false
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return try {
            val prev = sdf.parse(prevDateStr) ?: return false
            val curr = sdf.parse(currentDateStr) ?: return false
            val diffMs = curr.time - prev.time
            val diffDays = diffMs / (1000 * 60 * 60 * 24)
            diffDays == 1L
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Generates a unique, deterministic daily challenge puzzle based on the date.
     * Guaranteed solvable with Hamiltonian path backtracking.
     */
    fun generateDailyLevel(calendar: Calendar = getCurrentCalendar()): Level {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=Sun, 7=Sat

        val seed = year * 1000003L + month * 10007L + day * 997L
        val rng = Random(seed)

        // Difficulty & dimensions vary by day of week
        val (rows, cols, targetLength, targetObstacles) = when (dayOfWeek) {
            Calendar.SATURDAY, Calendar.SUNDAY -> {
                // Weekend Grand Challenge: 7x7 grid, 34-38 blocks, 6-7 obstacles
                val len = 34 + rng.nextInt(5)
                listOf(7, 7, len, 6)
            }
            Calendar.FRIDAY -> {
                // Friday Sprint: 6x6 grid, 28-30 blocks, 5 obstacles
                val len = 28 + rng.nextInt(3)
                listOf(6, 6, len, 5)
            }
            Calendar.WEDNESDAY, Calendar.THURSDAY -> {
                // Mid-week: 6x6 grid, 24-27 blocks, 4 obstacles
                val len = 24 + rng.nextInt(4)
                listOf(6, 6, len, 4)
            }
            else -> {
                // Monday / Tuesday: 5x5 grid, 18-21 blocks, 2-3 obstacles
                val len = 18 + rng.nextInt(4)
                listOf(5, 5, len, 3)
            }
        }

        val path = LevelProvider.generatePath(rows, cols, targetLength, rng)
        val activeSet = path.toSet()

        // Obstacles around path
        val remainingCells = mutableListOf<Cell>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cell = Cell(r, c)
                if (cell !in activeSet) remainingCells.add(cell)
            }
        }

        val sortedCandidates = remainingCells.sortedWith(
            compareByDescending<Cell> { cell ->
                LevelProvider.getNeighbors8(cell, rows, cols).count { it in activeSet }
            }.thenBy { rng.nextInt() }
        )

        val obstacles = sortedCandidates.take(minOf(targetObstacles, sortedCandidates.size)).toSet()

        return Level(
            levelNumber = 0, // 0 indicates Daily Challenge
            difficulty = Difficulty.DAILY,
            rows = rows,
            cols = cols,
            activeCells = activeSet,
            obstacles = obstacles,
            startCell = path.first(),
            solution = path
        )
    }

    /**
     * Deterministically generates today's global competitive leaderboard.
     * Incorporates player's record dynamically if completed.
     */
    fun getLeaderboard(
        calendar: Calendar = getCurrentCalendar(),
        playerTimeSec: Long? = null,
        playerStreak: Int = 1
    ): Pair<List<LeaderboardEntry>, Int> {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val seed = (year * 31337L + month * 7919L + day * 1009L + 42L)
        val rng = Random(seed)

        // Baseline fastest time based on board size
        val baseFastest = when (dayOfWeek) {
            Calendar.SATURDAY, Calendar.SUNDAY -> 24L
            Calendar.FRIDAY -> 19L
            Calendar.WEDNESDAY, Calendar.THURSDAY -> 16L
            else -> 12L
        }

        val simulatedPlayers = listOf(
            Triple("Kaito_Tokyo", "⚡", "JP"),
            Triple("Elena_Berlin", "👑", "DE"),
            Triple("SwiftLine99", "🦊", "US"),
            Triple("Maya_SF", "🎯", "US"),
            Triple("Lucas_Rio", "🦁", "BR"),
            Triple("NovaSolver", "🚀", "GB"),
            Triple("ZenMaster", "💎", "AU"),
            Triple("PuzzlePro", "🧩", "FR"),
            Triple("BrainWave", "🌟", "KR"),
            Triple("Aarav_Mumbai", "🔥", "IN"),
            Triple("Chloe_Paris", "✨", "FR"),
            Triple("Leo_Seoul", "🐼", "KR"),
            Triple("Marco_Rome", "🍕", "IT"),
            Triple("Astrid_Oslo", "❄️", "NO"),
            Triple("Sam_Sydney", "🐨", "AU")
        ).shuffled(rng)

        val baseEntries = mutableListOf<LeaderboardEntry>()
        var currTime = baseFastest + rng.nextInt(3)

        for (i in simulatedPlayers.indices) {
            val (name, avatar, country) = simulatedPlayers[i]
            val streak = (3..25).random(rng)
            baseEntries.add(
                LeaderboardEntry(
                    rank = i + 1,
                    name = name,
                    avatarEmoji = avatar,
                    timeSec = currTime,
                    isPlayer = false,
                    countryCode = country,
                    streak = streak
                )
            )
            // Stagger times realistically
            currTime += (1 + rng.nextInt(3))
        }

        // If player has solved today's challenge, rank player realistically
        if (playerTimeSec != null && playerTimeSec > 0) {
            val playerEntry = LeaderboardEntry(
                rank = 0,
                name = "YOU",
                avatarEmoji = "⭐",
                timeSec = playerTimeSec,
                isPlayer = true,
                countryCode = "YOU",
                streak = playerStreak
            )

            // Insert player and re-rank
            val allWithPlayer = (baseEntries + playerEntry).sortedBy { it.timeSec }
            var playerRank = 0
            val rankedList = allWithPlayer.mapIndexed { index, entry ->
                val newRank = index + 1
                if (entry.isPlayer) {
                    playerRank = newRank
                }
                entry.copy(rank = newRank)
            }

            return Pair(rankedList.take(15), playerRank)
        } else {
            return Pair(baseEntries.take(10), 0)
        }
    }
}
