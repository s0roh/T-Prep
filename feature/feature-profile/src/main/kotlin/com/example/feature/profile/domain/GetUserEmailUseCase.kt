package com.example.feature.profile.domain

import com.example.preferences.AuthPreferences
import javax.inject.Inject

class GetUserEmailUseCase @Inject constructor(
    private val preferences: AuthPreferences
) {

    operator fun invoke(): String? = preferences.getUserEmail()
}