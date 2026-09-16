package com.example.game

import com.example.model.Cell
import com.example.model.Difficulty
import com.example.model.Level
import kotlin.random.Random

object LevelProvider {

    // Handcrafted Level 1 (Perfect 3x3 intro tutorial board)
    private val level1Handcrafted: Level by lazy {
        val solution = listOf(
            Cell(0, 0),
            Cell(0, 1),
            Cell(0, 2),
            Cell(1, 2),
            Cell(1, 1),
            Cell(1, 0),
            Cell(2, 0),
            Cell(2, 1),
            Cell(2, 2)
        )
        Level(
            levelNumber = 1,
            difficulty = Difficulty.EASY,
            rows = 3,
            cols = 3,
            activeCells = solution.toSet(),
            obstacles = emptySet(),
            startCell = solution.first(),
            solution = solution
        )
    }

    // Handcrafted Level 11 (matching screenshot 4)
    private val level11Handcrafted: Level by lazy {
        val solution = listOf(
            Cell(0, 4),
            Cell(1, 4),
            Cell(1, 3),
            Cell(1, 2),
            Cell(1, 1),
            Cell(1, 0),
            Cell(2, 0),
            Cell(2, 1),
            Cell(2, 2),
            Cell(2, 3),
            Cell(2, 4),
            Cell(3, 4),
            Cell(4, 4),
            Cell(4, 3),
            Cell(3, 3)
        )
        val obstacles = setOf(Cell(0, 3), Cell(3, 2), Cell(4, 2))
        Level(
            levelNumber = 11,
            difficulty = Difficulty.forLevel(11),
            rows = 5,
            cols = 5,
            activeCells = solution.toSet(),
            obstacles = obstacles,
            startCell = solution.first(),
            solution = solution
        )
    }

    // Handcrafted Level 10 (matching screenshot 5)
    private val level10Handcrafted: Level by lazy {
        val solution = listOf(
            Cell(0, 5), Cell(0, 4), Cell(0, 3), Cell(0, 2),
            Cell(1, 2), Cell(2, 2), Cell(3, 2), Cell(3, 3), Cell(3, 4), Cell(3, 5),
            Cell(4, 5), Cell(5, 5), Cell(5, 4), Cell(5, 3), Cell(5, 2), Cell(6, 2),
            Cell(6, 1), Cell(6, 0), Cell(5, 0), Cell(4, 0), Cell(3, 0), Cell(2, 0),
            Cell(1, 0), Cell(1, 1), Cell(2, 1)
        )
        val obstacles = setOf(Cell(0, 1), Cell(2, 3), Cell(4, 1), Cell(5, 1))
        Level(
            levelNumber = 10,
            difficulty = Difficulty.forLevel(10),
            rows = 7,
            cols = 6,
            activeCells = solution.toSet(),
            obstacles = obstacles,
            startCell = solution.first(),
            solution = solution
        )
    }

    // Handcrafted Level 12 (matching screenshot 3)
    private val level12Handcrafted: Level by lazy {
        val solution = listOf(
            Cell(0, 4), Cell(0, 5), Cell(1, 5), Cell(1, 4), Cell(2, 4),
            Cell(3, 4), Cell(3, 5), Cell(4, 5), Cell(4, 4), Cell(4, 3),
            Cell(5, 3), Cell(5, 2), Cell(5, 1), Cell(5, 0), Cell(4, 0),
            Cell(3, 0), Cell(3, 1), Cell(2, 1), Cell(2, 2), Cell(1, 2),
            Cell(0, 2), Cell(0, 1), Cell(0, 0), Cell(1, 0)
        )
        val obstacles = setOf(Cell(1, 1), Cell(2, 3), Cell(3, 2), Cell(4, 1))
        Level(
            levelNumber = 12,
            difficulty = Difficulty.forLevel(12),
            rows = 6,
            cols = 6,
            activeCells = solution.toSet(),
            obstacles = obstacles,
            startCell = solution.first(),
            solution = solution
        )
    }

    fun getLevel(levelNumber: Int): Level {
        val clamped = levelNumber.coerceIn(1, 300)
        return when (clamped) {
            1 -> level1Handcrafted
            10 -> level10Handcrafted
            11 -> level11Handcrafted
            12 -> level12Handcrafted
            else -> generateDeterministicLevel(clamped)
        }
    }

    fun getLevel(difficulty: Difficulty, levelNumber: Int): Level {
        val targetLevel = if (levelNumber in difficulty.levelRange) {
            levelNumber
        } else {
            difficulty.levelRange.first + (levelNumber - 1).coerceIn(0, 59)
        }
        return getLevel(targetLevel)
    }

    /**
     * Generates a deterministic, guaranteed solvable level from 1 to 300.
     * As the level count increases:
     * - Grid dimensions increase progressively from 3x3 up to 9x9.
     * - Obstacle blocks are added progressively (from 0 up to 16) around the active path,
     *   forming walls, chokepoints, and maze corridors.
     */
    fun generateDeterministicLevel(levelNumber: Int): Level {
        val difficulty = Difficulty.forLevel(levelNumber)
        val seed = levelNumber * 7919L + 31337L
        val rng = Random(seed)

        // 1. Grid dimensions (rows x cols) and active path target length scale with levelNumber (1..300)
        val (rows, cols, targetLength) = when {
            levelNumber <= 5 -> {
                // Intro: 3x3 grids (5 to 7 blocks)
                val len = 5 + (levelNumber - 1) / 2
                Triple(3, 3, len.coerceIn(5, 7))
            }
            levelNumber <= 18 -> {
                // 4x4 grids (9 to 13 blocks)
                val len = 9 + (levelNumber - 6) / 3
                Triple(4, 4, len.coerceIn(9, 13))
            }
            levelNumber <= 38 -> {
                // 4x5 or 5x4 grids (14 to 17 blocks)
                val len = 14 + (levelNumber - 19) / 5
                val r = if (levelNumber % 2 == 0) 4 else 5
                val c = if (levelNumber % 2 == 0) 5 else 4
                Triple(r, c, len.coerceIn(14, 17))
            }
            levelNumber <= 60 -> {
                // 5x5 grids (18 to 22 blocks) - End of EASY tier (60)
                val len = 18 + (levelNumber - 39) / 5
                Triple(5, 5, len.coerceIn(18, 22))
            }
            levelNumber <= 90 -> {
                // 5x6 or 6x5 grids (22 to 26 blocks) - MEDIUM tier
                val len = 22 + (levelNumber - 61) / 7
                val r = if (levelNumber % 2 == 0) 5 else 6
                val c = if (levelNumber % 2 == 0) 6 else 5
                Triple(r, c, len.coerceIn(22, 26))
            }
            levelNumber <= 120 -> {
                // 6x6 grids (26 to 30 blocks) - End of MEDIUM tier (120)
                val len = 26 + (levelNumber - 91) / 7
                Triple(6, 6, len.coerceIn(26, 30))
            }
            levelNumber <= 150 -> {
                // 6x7 or 7x6 grids (30 to 36 blocks) - HARD tier
                val len = 30 + (levelNumber - 121) / 5
                val r = if (levelNumber % 2 == 0) 6 else 7
                val c = if (levelNumber % 2 == 0) 7 else 6
                Triple(r, c, len.coerceIn(30, 36))
            }
            levelNumber <= 180 -> {
                // 7x7 grids (35 to 41 blocks) - End of HARD tier (180)
                val len = 35 + (levelNumber - 151) / 5
                Triple(7, 7, len.coerceIn(35, 41))
            }
            levelNumber <= 210 -> {
                // 7x8 or 8x7 grids (40 to 47 blocks) - EXTRA HARD tier
                val len = 40 + (levelNumber - 181) / 4
                val r = if (levelNumber % 2 == 0) 7 else 8
                val c = if (levelNumber % 2 == 0) 8 else 7
                Triple(r, c, len.coerceIn(40, 47))
            }
            levelNumber <= 240 -> {
                // 8x8 grids (46 to 53 blocks) - End of EXTRA HARD tier (240)
                val len = 46 + (levelNumber - 211) / 4
                Triple(8, 8, len.coerceIn(46, 53))
            }
            levelNumber <= 270 -> {
                // 8x9 or 9x8 grids (52 to 59 blocks) - MASTER tier
                val len = 52 + (levelNumber - 241) / 4
                val r = if (levelNumber % 2 == 0) 8 else 9
                val c = if (levelNumber % 2 == 0) 9 else 8
                Triple(r, c, len.coerceIn(52, 59))
            }
            else -> {
                // Grandmaster 9x9 grids (58 to 66 blocks) - Levels 271..300
                val len = 58 + (levelNumber - 271) / 4
                Triple(9, 9, len.coerceIn(58, 66))
            }
        }

        // 2. Obstacle count scales up as level count increases from 1 to 300
        val targetObstacles = when {
            levelNumber <= 5 -> 0 // Pure introductory onboarding (0 obstacles)
            levelNumber <= 18 -> 1
            levelNumber <= 38 -> 2
            levelNumber <= 60 -> 3
            levelNumber <= 90 -> 4
            levelNumber <= 120 -> 5
            levelNumber <= 150 -> 6
            levelNumber <= 180 -> 7
            levelNumber <= 210 -> 9
            levelNumber <= 240 -> 11
            levelNumber <= 270 -> 13
            else -> 14 + (levelNumber - 270) / 15 // 14 to 16 obstacles
        }

        // 3. Generate guaranteed solvable path of length targetLength
        val path = generatePath(rows, cols, targetLength, rng)
        val activeSet = path.toSet()

        // 4. Place obstacle blocks strategically bordering the active path
        val remainingCells = mutableListOf<Cell>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cell = Cell(r, c)
                if (cell !in activeSet) {
                    remainingCells.add(cell)
                }
            }
        }

        // Prioritize cells that border the active path so obstacles define the maze walls
        val sortedCandidates = remainingCells.sortedWith(
            compareByDescending<Cell> { cell ->
                getNeighbors8(cell, rows, cols).count { it in activeSet }
            }.thenBy { rng.nextInt() }
        )

        val obstacles = sortedCandidates.take(minOf(targetObstacles, sortedCandidates.size)).toSet()

        return Level(
            levelNumber = levelNumber,
            difficulty = difficulty,
            rows = rows,
            cols = cols,
            activeCells = activeSet,
            obstacles = obstacles,
            startCell = path.first(),
            solution = path
        )
    }

    internal fun getNeighbors8(cell: Cell, rows: Int, cols: Int): List<Cell> {
        val result = mutableListOf<Cell>()
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = cell.row + dr
                val nc = cell.col + dc
                if (nr in 0 until rows && nc in 0 until cols) {
                    result.add(Cell(nr, nc))
                }
            }
        }
        return result
    }

    internal fun generatePath(rows: Int, cols: Int, targetLength: Int, rng: Random): List<Cell> {
        // Try randomized DFS walk with backtracking
        for (attempt in 0..60) {
            val startR = rng.nextInt(rows)
            val startC = rng.nextInt(cols)
            val path = mutableListOf(Cell(startR, startC))
            val visited = mutableSetOf(Cell(startR, startC))

            if (dfsWalk(path, visited, rows, cols, targetLength, rng, 0)) {
                return path
            }
        }

        // Secondary fallback: Warnsdorff-guided walk (heuristic: pick neighbor with fewest exits)
        for (attempt in 0..20) {
            val startR = rng.nextInt(rows)
            val startC = rng.nextInt(cols)
            val path = mutableListOf(Cell(startR, startC))
            val visited = mutableSetOf(Cell(startR, startC))
            var curr = Cell(startR, startC)

            while (path.size < targetLength) {
                val neighbors = getNeighbors(curr, rows, cols).filter { it !in visited }
                if (neighbors.isEmpty()) break
                val best = neighbors.minByOrNull { n ->
                    getNeighbors(n, rows, cols).count { it !in visited }
                } ?: neighbors.first()

                path.add(best)
                visited.add(best)
                curr = best
            }

            if (path.size >= targetLength) {
                return path
            }
        }

        // Ultimate fallback: Serpentine path
        val fallback = mutableListOf<Cell>()
        var count = 0
        for (r in 0 until rows) {
            val colRange = if (r % 2 == 0) (0 until cols) else (cols - 1 downTo 0)
            for (c in colRange) {
                fallback.add(Cell(r, c))
                count++
                if (count >= targetLength) return fallback
            }
        }
        return fallback
    }

    private fun getNeighbors(cell: Cell, rows: Int, cols: Int): List<Cell> {
        val deltas = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        return deltas.mapNotNull { (dr, dc) ->
            val nr = cell.row + dr
            val nc = cell.col + dc
            if (nr in 0 until rows && nc in 0 until cols) Cell(nr, nc) else null
        }
    }

    private fun dfsWalk(
        path: MutableList<Cell>,
        visited: MutableSet<Cell>,
        rows: Int,
        cols: Int,
        targetLength: Int,
        rng: Random,
        depth: Int
    ): Boolean {
        if (path.size >= targetLength) return true
        if (depth > 500) return false

        val current = path.last()
        val directions = listOf(
            Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1)
        ).shuffled(rng)

        for ((dr, dc) in directions) {
            val nr = current.row + dr
            val nc = current.col + dc
            val nextCell = Cell(nr, nc)

            if (nr in 0 until rows && nc in 0 until cols && nextCell !in visited) {
                path.add(nextCell)
                visited.add(nextCell)

                if (dfsWalk(path, visited, rows, cols, targetLength, rng, depth + 1)) {
                    return true
                }

                path.removeAt(path.size - 1)
                visited.remove(nextCell)
            }
        }
        return false
    }
}
