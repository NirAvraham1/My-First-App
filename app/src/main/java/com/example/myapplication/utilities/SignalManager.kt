package com.example.myapplication.utilities

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import java.lang.ref.WeakReference

class SignalManager private constructor(context: Context) {

    private val contextRef: WeakReference<Context> = WeakReference(context.applicationContext)

    fun toast(text: String) {
        contextRef.get()?.let { context ->
            Toast
                .makeText(
                    context,
                    text,
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }

    fun vibrate() {
        contextRef.get()?.let { context ->
            val vibrator: Vibrator =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager =
                        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val SOSPattern = longArrayOf(
                    0, 200, 100, 200, 100, 200,
                    300, 500, 100, 500, 100, 500,
                    300, 200, 100, 200, 100, 200
                )

                val waveFormVibrationEffect =
                    VibrationEffect.createWaveform(SOSPattern, -1)

                vibrator.vibrate(waveFormVibrationEffect)
            } else {
                vibrator.vibrate(500)
            }
        }
    }

    companion object {
        private var instance: SignalManager? = null

        fun init(context: Context) {
            if (instance == null) {
                instance = SignalManager(context)
            }
        }

        fun getInstance(): SignalManager {
            return instance
                ?: throw IllegalStateException("SignalManager must be initialized with init(context) before use")
        }
    }
}
