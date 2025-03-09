package com.example.feature.profile.presentation.profile

import android.net.Uri

internal sealed interface ProfileScreenState {

    object Loading : ProfileScreenState

    data class Error(
        val message: String,
    ) : ProfileScreenState

    data class Success(
        val userName: String,
        val userEmail: String,
        val profileImageUri: Uri? = null,
        val totalTrainings: Int,
        val averageAccuracy: Int,
    ) : ProfileScreenState
}
