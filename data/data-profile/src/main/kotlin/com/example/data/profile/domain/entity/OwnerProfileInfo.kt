package com.example.data.profile.domain.entity

import android.net.Uri
import com.example.common.ui.entity.DeckUiModel

data class OwnerProfileInfo(
    val ownerProfileName: String,
    val ownerProfileImage: Uri?,
    val ownerPublicDecks: List<DeckUiModel>,
    val ownerTotalTrainings: Int,
    val ownerMediumPercentage: Int,
)
