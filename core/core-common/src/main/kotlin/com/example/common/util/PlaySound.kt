package com.example.common.util

import android.content.Context
import android.media.MediaPlayer

/**
 * Воспроизводит звук из ресурсов.
 *
 * @param context Контекст, используемый для создания [MediaPlayer].
 * @param soundResId Идентификатор звукового ресурса.
 */
fun playSound(context: Context, soundResId: Int) {
    MediaPlayer.create(context, soundResId)?.apply {
        setOnCompletionListener { it.release() }
        start()
    }
}