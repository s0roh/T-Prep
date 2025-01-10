package com.example.auth.util

import android.util.Patterns

internal fun isEmailValid(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}