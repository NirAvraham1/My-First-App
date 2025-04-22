package com.example.myapplication.utilities

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import java.lang.ref.WeakReference

class SignalManager private constructor(context: Context) {

    private val contextRef: WeakReference<Context> = WeakReference(context.applicationContext)

    fun toast(msg: String) {
        contextRef.get()?.let {
            Toast.makeText(it, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun vibrate() {
        val vibrator = contextRef.get()?.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                it.vibrate(effect)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 100, 50, 300)
                it.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                it.vibrate(300)
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
