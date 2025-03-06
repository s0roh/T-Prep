package com.example.feature.profile.domain

import com.example.preferences.AuthPreferences
import javax.inject.Inject

class SaveUserProfileImageUseCase @Inject constructor(
    private val preferences: AuthPreferences,
) {

    operator fun invoke(uri: String) = preferences.saveUserProfileImage(uri)
}