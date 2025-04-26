package com.example.myapplication

class GameLogic {

    companion object {
        const val CAR_ROW = 4
    }

    val board = Array(7) { IntArray(3) }
    var carColumn = 1
    var lives = 3

    fun moveLeft(): Boolean {
        if (carColumn > 0) {
            if (board[CAR_ROW][carColumn - 1] == 1) {
                lives--
                carColumn--
                return true
            }
            carColumn--
        }
        return false
    }

    fun moveRight(): Boolean {
        if (carColumn < 2) {
            if (board[CAR_ROW][carColumn + 1] == 1) {
                lives--
                carColumn++
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
        for (row in board) {
            row.fill(0)
        }
    }

    fun tick(): Boolean {
        for (i in board.size - 1 downTo 1) {
            board[i] = board[i - 1].copyOf()
        }

        board[0] = IntArray(3).apply {
            val spawn = (1..100).random()
            if (spawn <= 60) {
                val obstacleCol = (0..2).random()
                this[obstacleCol] = 1
            }
        }

        if (board[CAR_ROW][carColumn] == 1) {
            lives--
            return true
        }

        return false
    }
}
