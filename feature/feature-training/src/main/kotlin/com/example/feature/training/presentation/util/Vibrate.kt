package com.example.feature.training.presentation.util

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresPermission

@Suppress("DEPRECATION")
@RequiresPermission(Manifest.permission.VIBRATE)
fun vibrate(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.cancel()
    val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        VibrationEffect.createWaveform(
            longArrayOf(0, 50, 25, 50, 25, 50, 25, 50, 25, 50),
            intArrayOf(255, 0, 255, 0, 255, 0, 255, 0, 255, 0),
            -1
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        VibrationEffect.createWaveform(
            longArrayOf(0, 100, 50, 200, 50, 150),
            intArrayOf(0, 120, 0, 255, 0, 180),
            -1
        )
    } else {
        return vibrator.vibrate(longArrayOf(0, 100, 50, 150, 50, 100), -1)
    }
    vibrator.vibrate(effect)
}