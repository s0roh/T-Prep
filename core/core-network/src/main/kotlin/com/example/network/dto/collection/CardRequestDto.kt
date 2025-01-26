package com.example.network.dto.collection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardRequestDto(
    @SerialName("question") val question: String,
    @SerialName("answer") val answer: String,
)
