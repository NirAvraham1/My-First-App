package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.utilities.SignalManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.myapplication.GameLogic

class MainActivity : AppCompatActivity() {

    private lateinit var logic: GameLogic
    private lateinit var grid: GridLayout
    private lateinit var restartBtn: FloatingActionButton
    private lateinit var startBtn: Button
    private lateinit var livesLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SignalManager.init(applicationContext)

        logic = GameLogic()
        grid = findViewById(R.id.grid)
        restartBtn = findViewById(R.id.restart_btn)
        startBtn = findViewById(R.id.start_btn)
        livesLayout = findViewById(R.id.lives_layout)

        findViewById<FloatingActionButton>(R.id.left_btn).setOnClickListener {
            val crashed = logic.moveLeft()
            if (crashed) SignalManager.getInstance().vibrate()
            updateUI()
        }

        findViewById<FloatingActionButton>(R.id.right_btn).setOnClickListener {
            val crashed = logic.moveRight()
            if (crashed) SignalManager.getInstance().vibrate()
            updateUI()
        }

        restartBtn.setOnClickListener {
            restartBtn.visibility = View.GONE
            logic.resetGame()
            updateUI()
            startGameLoop()
        }

        startBtn.setOnClickListener {
            startBtn.visibility = View.GONE
            logic.resetGame()
            updateUI()
            startGameLoop()
        }

        updateUI()
    }

    private fun startGameLoop() {
        lifecycleScope.launch {
            while (!logic.isGameOver()) {
                delay(1000L)
                val crashed = logic.tick()
                if (crashed) {
                    SignalManager.getInstance().toast("Crash!")
                    SignalManager.getInstance().vibrate()
                }
                updateUI()
            }
            restartBtn.visibility = View.VISIBLE
        }
    }

    private fun updateUI() {
        grid.post {
            grid.removeAllViews()
            val cellSize = grid.width / 3

            for (i in 0 until 7) {
                for (j in 0 until 3) {
                    val iv = ImageView(this).apply {
                        layoutParams = GridLayout.LayoutParams().apply {
                            width = cellSize
                            height = cellSize
                        }
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        adjustViewBounds = true
                    }

                    iv.setImageResource(
                        when {
                            i == GameLogic.CAR_ROW && j == logic.carColumn -> R.drawable.car
                            logic.board[i][j] == 1 -> R.drawable.ic_obstacle
                            else -> android.R.color.transparent
                        }
                    )

                    grid.addView(iv)
                }
            }

            livesLayout.removeAllViews()
            repeat(logic.lives) {
                val heart = ImageView(this)
                heart.setImageResource(R.drawable.ic_heart)
                heart.layoutParams = LinearLayout.LayoutParams(100, 100)
                livesLayout.addView(heart)
            }
        }
    }
}
