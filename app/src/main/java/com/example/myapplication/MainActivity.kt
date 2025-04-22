package com.example.myapplication

import android.os.*
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.com.example.myapplication.GameLogic
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var logic: GameLogic
    private lateinit var grid: GridLayout
    private lateinit var restartBtn: FloatingActionButton
    private lateinit var startBtn: Button
    private lateinit var livesLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logic = GameLogic()
        grid = findViewById(R.id.grid)
        restartBtn = findViewById(R.id.restart_btn)
        startBtn = findViewById(R.id.start_btn)
        livesLayout = findViewById(R.id.lives_layout)

        findViewById<FloatingActionButton>(R.id.left_btn).setOnClickListener {
            val crashed = logic.moveLeft()
            if (crashed) vibrate()
            updateUI()
        }

        findViewById<FloatingActionButton>(R.id.right_btn).setOnClickListener {
            val crashed = logic.moveRight()
            if (crashed) vibrate()
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
                    Toast.makeText(this@MainActivity, "Crash!", Toast.LENGTH_SHORT).show()
                    vibrate()
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

    @Suppress("DEPRECATION")
    private fun vibrate() {
        val vibrator = getSystemService<Vibrator>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            vibrator?.vibrate(300)
        }
    }
}
