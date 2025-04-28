package com.example.common.util

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission

/**
 * Выполняет короткую вибрацию устройства.
 *
 * Функция автоматически выбирает подходящий способ вибрации в зависимости от версии Android:
 * - Для Android 12+ (API 31 и выше) используется [VibratorManager] и [VibrationEffect].
 * - Для Android 8.0+ (API 26 и выше) используется [VibrationEffect] через [Vibrator].
 * - Для старых версий (до API 26) используется устаревший метод вибрации [Vibrator.vibrate(duration)].
 *
 * Требуется разрешение [Manifest.permission.VIBRATE].
 *
 * @param context Контекст приложения для доступа к системным сервисам вибрации.
 *
 * @see Vibrator
 * @see VibratorManager
 * @see VibrationEffect
 */
@Suppress("DEPRECATION")
@RequiresPermission(Manifest.permission.VIBRATE)
fun vibrate(context: Context) {
    when {
        // Для Android 12+ (API 31 и выше)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator.run {
                cancel()
                if (hasAmplitudeControl()) {
                    vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                }
            }
        }

        // Для Android 8.0 (API 26 и выше)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.cancel()
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        }

        // Для старых версий Android (API < 26)
        else -> {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.cancel()
            vibrator.vibrate(500)
        }
    }
}