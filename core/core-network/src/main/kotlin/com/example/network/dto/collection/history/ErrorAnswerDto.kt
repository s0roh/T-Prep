package com.example.network.dto.collection.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorAnswerDto(
    @SerialName("card_id") val cardId: Int,
    @SerialName("question") val question: String,
    @SerialName("answer") val answer: String,
    @SerialName("type") val type: String,
    @SerialName("user_answer") val userAnswer: String,
    @SerialName("blank_answer") val blankAnswer: String?,
)
