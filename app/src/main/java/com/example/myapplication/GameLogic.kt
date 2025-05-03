package com.example.myapplication

class GameLogic {

    companion object {
        const val ROWS = 4
        const val COLS = 3
        const val CAR_ROW = ROWS - 1
    }

    val board = Array(ROWS) { IntArray(COLS) }
    var carColumn = 1
    var lives = 3
    private var tickCount = 0

    fun moveLeft(): Boolean {
        if (carColumn > 0) {
            if (board[CAR_ROW][carColumn - 1] == 1) {
                carColumn--
                lives--
                return true
            }
            carColumn--
        }
        return false
    }

    fun moveRight(): Boolean {
        if (carColumn < COLS - 1) {
            if (board[CAR_ROW][carColumn + 1] == 1) {
                carColumn++
                lives--
                return true
            }
            carColumn++
        }
        return false
    }

    fun isGameOver(): Boolean {
        return lives <= 0
    }

    fun resetGame() {
        carColumn = 1
        lives = 3
        tickCount = 0
        for (row in board) {
            row.fill(0)
        }
    }

    fun tick(): Boolean {
        tickCount++

        for (i in ROWS - 1 downTo 1) {
            board[i] = board[i - 1].copyOf()
        }

        board[0] = IntArray(COLS)
        if (tickCount % 2 == 1) {
            val obstacleCol = (0 until COLS).random()
            board[0][obstacleCol] = 1
        }

        if (board[CAR_ROW][carColumn] == 1) {
            lives--
            return true
        }

        return false
    }
}
