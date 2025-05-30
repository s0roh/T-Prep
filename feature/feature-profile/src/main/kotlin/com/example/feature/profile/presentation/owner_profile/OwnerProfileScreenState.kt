package com.example.feature.profile.presentation.owner_profile

import android.net.Uri
import com.example.common.ui.entity.DeckUiModel

internal sealed interface OwnerProfileScreenState {

    object Loading : OwnerProfileScreenState

    data class Success(
        val userId: String,
        val userName: String,
        val profileImageUri: Uri? = null,
        val ownerPublicDecks: List<DeckUiModel>,
        val totalTrainings: Int,
        val averageAccuracy: Int,
    ) : OwnerProfileScreenState
}