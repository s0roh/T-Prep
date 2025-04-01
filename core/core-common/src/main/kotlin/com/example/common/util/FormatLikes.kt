package com.example.common.util

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
internal fun formatLikes(likes: Int): String {
    return when {
        likes >= 1_000_000 -> String.format("%.1fM", likes / 1_000_000.0)
        likes >= 1_000 -> String.format("%.1fK", likes / 1_000.0)
        else -> likes.toString()
    }
}