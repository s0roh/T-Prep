package com.example.feature.reminder.presentation.util

import android.content.Context
import android.widget.Toast

internal fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}