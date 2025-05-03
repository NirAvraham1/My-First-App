package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val logic = GameLogic()
    private var grid: GridLayout? = null
    private var restartBtn: ExtendedFloatingActionButton? = null
    private var startBtn: Button? = null
    private var livesLayout: LinearLayout? = null

    private var gameRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SignalManager.init(applicationContext)

        grid = findViewById(R.id.grid)
        restartBtn = findViewById(R.id.restart_btn)
        startBtn = findViewById(R.id.start_btn)
        livesLayout = findViewById(R.id.lives_layout)

        findViewById<ExtendedFloatingActionButton>(R.id.left_btn).setOnClickListener {
            if (!gameRunning) return@setOnClickListener
            val crashed = logic.moveLeft()
            if (crashed) {
                SignalManager.getInstance().vibrate()
                SignalManager.getInstance().toast("Crash!")
            }
            updateUI()
        }

        findViewById<ExtendedFloatingActionButton>(R.id.right_btn).setOnClickListener {
            if (!gameRunning) return@setOnClickListener
            val crashed = logic.moveRight()
            if (crashed) {
                SignalManager.getInstance().vibrate()
                SignalManager.getInstance().toast("Crash!")
            }
            updateUI()
        }

        restartBtn?.setOnClickListener {
            restartBtn?.visibility = View.GONE
            logic.resetGame()
            gameRunning = true
            updateUI()
            startGameLoop()
        }

        startBtn?.setOnClickListener {
            startBtn?.visibility = View.GONE
            logic.resetGame()
            gameRunning = true
            updateUI()
            startGameLoop()
        }

        updateUI()
    }

    private fun startGameLoop() {
        lifecycleScope.launch {
            while (gameRunning && !logic.isGameOver()) {
                delay(1000L)
                val crashed = logic.tick()
                if (crashed) {
                    SignalManager.getInstance().vibrate()
                    SignalManager.getInstance().toast("Crash!")
                }
                updateUI()
            }
            if (logic.isGameOver()) {
                gameRunning = false
                restartBtn?.visibility = View.VISIBLE
            }
        }
    }

    private fun updateUI() {
        grid?.removeAllViews()
        val cellSize = grid?.width?.div(GameLogic.COLS) ?: 0

        for (i in 0 until GameLogic.ROWS) {
            for (j in 0 until GameLogic.COLS) {
                val layout = FrameLayout(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = cellSize
                        height = cellSize
                    }
                }

                val obstacle = ImageView(this).apply {
                    setImageResource(R.drawable.ic_obstacle)
                    visibility = if (logic.board[i][j] == 1) View.VISIBLE else View.INVISIBLE
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    adjustViewBounds = true
                }

                val player = ImageView(this).apply {
                    setImageResource(R.drawable.car)
                    visibility = if (i == GameLogic.CAR_ROW && j == logic.carColumn) View.VISIBLE else View.INVISIBLE
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    adjustViewBounds = true
                }

                layout.addView(obstacle)
                layout.addView(player)
                grid?.addView(layout)
            }
        }

        livesLayout?.removeAllViews()
        repeat(logic.lives) {
            val heart = ImageView(this)
            heart.setImageResource(R.drawable.ic_heart)
            heart.layoutParams = LinearLayout.LayoutParams(100, 100)
            livesLayout?.addView(heart)
        }
    }
}
