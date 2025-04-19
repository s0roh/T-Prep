package com.example.feature.training.presentation.util

import android.content.Context
import android.media.MediaPlayer
import com.example.feature.training.R

internal fun playSound(context: Context, isCorrect: Boolean, isFinish: Boolean = false) {
    val soundRes = when {
        isFinish -> R.raw.finish_train
        isCorrect -> R.raw.correct
        else -> R.raw.incorrect
    }
    MediaPlayer.create(context, soundRes)?.apply {
        setOnCompletionListener { it.release() }
        start()
    }
}