package com.example

import com.example.game.LevelProvider
import com.example.model.Difficulty
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testAll100LevelsSolvabilityProgressionAndObstacles() {
    var maxObsSoFar = 0
    var maxAreaSoFar = 0

    for (lvl in 1..100) {
      val level = LevelProvider.getLevel(lvl)

      assertEquals("Level number must match", lvl, level.levelNumber)
      assertEquals("Start cell must match first solution cell", level.solution.first(), level.startCell)
      assertEquals("Active cells set must match solution cells", level.solution.toSet(), level.activeCells)
      assertEquals("Solution size must equal activeCells size", level.solution.size, level.activeCells.size)
      assertTrue("Level must have at least 5 cells", level.activeCells.size >= 5)

      // Verify difficulty assignment matching 300 levels progression (60 levels per tier)
      val expectedDiff = when {
        lvl <= 60 -> Difficulty.EASY
        lvl <= 120 -> Difficulty.MEDIUM
        lvl <= 180 -> Difficulty.HARD
        lvl <= 240 -> Difficulty.EXTRA_HARD
        else -> Difficulty.MASTER
      }
      assertEquals("Difficulty must match tier", expectedDiff, level.difficulty)

      // Verify obstacles NEVER overlap with active cells
      val intersection = level.activeCells.intersect(level.obstacles)
      assertTrue("Obstacles must never overlap with active cells in level $lvl", intersection.isEmpty())

      // Verify solution path never touches an obstacle
      for (step in level.solution) {
        assertFalse("Solution path cannot touch an obstacle in level $lvl", step in level.obstacles)
      }

      // Verify consecutive steps are orthogonal adjacent
      for (i in 0 until level.solution.size - 1) {
        val c1 = level.solution[i]
        val c2 = level.solution[i + 1]
        val dist = Math.abs(c1.row - c2.row) + Math.abs(c1.col - c2.col)
        assertEquals("Consecutive cells must be orthogonal adjacent for level $lvl step $i", 1, dist)
      }

      // Check board boundaries for both active cells and obstacles
      for (cell in level.activeCells) {
        assertTrue("Active cell row in bounds", cell.row in 0 until level.rows)
        assertTrue("Active cell col in bounds", cell.col in 0 until level.cols)
      }
      for (cell in level.obstacles) {
        assertTrue("Obstacle cell row in bounds", cell.row in 0 until level.rows)
        assertTrue("Obstacle cell col in bounds", cell.col in 0 until level.cols)
      }

      val area = level.rows * level.cols
      if (area > maxAreaSoFar) maxAreaSoFar = area
      if (level.obstacles.size > maxObsSoFar) maxObsSoFar = level.obstacles.size
    }

    // Verify grid dimensions increase across progression
    val level1 = LevelProvider.getLevel(1)
    assertEquals(3, level1.rows)
    assertEquals(3, level1.cols)
    assertEquals(0, level1.obstacles.size) // Level 1 has 0 obstacles (tutorial)

    val level20 = LevelProvider.getLevel(20)
    assertTrue(level20.rows * level20.cols >= 16)
    assertTrue(level20.obstacles.size >= 1)

    val level50 = LevelProvider.getLevel(50)
    assertTrue(level50.rows * level50.cols >= 20)
    assertTrue(level50.obstacles.size >= 2)

    val level75 = LevelProvider.getLevel(75)
    assertTrue(level75.rows * level75.cols >= 25)
    assertTrue(level75.obstacles.size >= 3)

    val level100 = LevelProvider.getLevel(100)
    assertTrue("Level 100 has 6x6 grid", level100.rows * level100.cols >= 36)
    assertTrue("Level 100 has obstacles", level100.obstacles.size >= 4)
    assertTrue(maxAreaSoFar >= 36)
    assertTrue(maxObsSoFar >= 4)
  }

  @Test
  fun testPathUndoLogic() {
    val level = LevelProvider.getLevel(1) // 3x3 grid
    val path = mutableListOf(level.startCell)

    // Simulate moving to an adjacent cell
    val adjacent = level.activeCells.first { it != level.startCell && it.isAdjacentTo(level.startCell) }
    path.add(adjacent)
    assertEquals(2, path.size)

    // Undo step
    if (path.size > 1) {
      path.removeAt(path.size - 1)
    }
    assertEquals(1, path.size)
    assertEquals(level.startCell, path.first())

    // Attempting undo on starting cell should do nothing
    if (path.size > 1) {
      path.removeAt(path.size - 1)
    }
    assertEquals(1, path.size)
  }

  @Test
  fun testDailyChallengeGenerationAndLeaderboard() {
    val calA = java.util.Calendar.getInstance().apply {
      set(2026, java.util.Calendar.SEPTEMBER, 13)
    }
    val levelA1 = com.example.game.DailyChallengeProvider.generateDailyLevel(calA)
    val levelA2 = com.example.game.DailyChallengeProvider.generateDailyLevel(calA)

    // Deterministic: Same date produces identical puzzle
    assertEquals(levelA1.rows, levelA2.rows)
    assertEquals(levelA1.cols, levelA2.cols)
    assertEquals(levelA1.solution, levelA2.solution)
    assertEquals(levelA1.startCell, levelA2.startCell)
    assertEquals(levelA1.obstacles, levelA2.obstacles)

    // Solvability & integrity
    assertEquals(levelA1.solution.first(), levelA1.startCell)
    assertEquals(levelA1.solution.toSet(), levelA1.activeCells)
    assertTrue(levelA1.activeCells.intersect(levelA1.obstacles).isEmpty())

    for (i in 0 until levelA1.solution.size - 1) {
      val c1 = levelA1.solution[i]
      val c2 = levelA1.solution[i + 1]
      val dist = Math.abs(c1.row - c2.row) + Math.abs(c1.col - c2.col)
      assertEquals("Daily challenge steps must be orthogonal adjacent", 1, dist)
    }

    // Leaderboard logic
    val (leaderboard, rank) = com.example.game.DailyChallengeProvider.getLeaderboard(
      playerTimeSec = 45L,
      playerStreak = 3,
      calendar = calA
    )
    assertTrue("Leaderboard must not be empty", leaderboard.isNotEmpty())
    assertTrue("Player rank must be positive", rank > 0)
    assertTrue("Player must exist in leaderboard", leaderboard.any { it.isPlayer })

    // Times must be monotonically non-decreasing
    for (i in 0 until leaderboard.size - 1) {
      assertTrue(
        "Leaderboard times should be ordered",
        leaderboard[i].timeSec <= leaderboard[i + 1].timeSec
      )
    }
  }

  @Test
  fun testLevel1HandcraftedTutorial() {
    val lvl1 = LevelProvider.getLevel(1)
    assertEquals(3, lvl1.rows)
    assertEquals(3, lvl1.cols)
    assertEquals(9, lvl1.activeCells.size)
    assertEquals(0, lvl1.obstacles.size)
    assertEquals(lvl1.solution.first(), lvl1.startCell)
  }
}
