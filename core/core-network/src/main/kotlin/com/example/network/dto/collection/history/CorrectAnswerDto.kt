package com.example.network.dto.collection.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CorrectAnswerDto(
    @SerialName("card_id") val cardId: Int,
    @SerialName("type") val type: String,
)
