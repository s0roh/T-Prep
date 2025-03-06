package com.example.feature.profile.presentation.profile

import android.net.Uri

internal sealed interface ProfileScreenState{

    object Loading : ProfileScreenState

    object Error : ProfileScreenState

    data class Success(
        val userName: String,
        val userEmail: String,
        val profileImageUri: Uri? = null,
        val totalTrainings: Int,
        val averageAccuracy: Int
    ) : ProfileScreenState
}
