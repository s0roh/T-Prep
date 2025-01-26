package com.example.network.dto.collection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeckRequestDto (
    @SerialName("name") val name: String,
    @SerialName("is_public") val isPublic: Boolean,
)