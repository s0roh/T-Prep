package com.example.feature.profile.presentation.owner_profile

import android.net.Uri
import com.example.common.ui.entity.DeckUiModel

internal sealed interface OwnerProfileScreenState {

    object Loading : OwnerProfileScreenState

    data class Error(
        val message: String,
    ) : OwnerProfileScreenState

    data class Success(
        val userName: String,
        val profileImageUri: Uri? = null,
        val ownerPublicDecks: List<DeckUiModel>,
        val totalTrainings: Int = 115,
        val averageAccuracy: Int = 72,
    ) : OwnerProfileScreenState
}