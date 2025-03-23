package com.example.network.dto.collection.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryItemDto(
    @SerialName("collection_id") val collectionId: String,
    @SerialName("collection_name") val collectionName: String,
    @SerialName("time") val time: Int,
    @SerialName("correct_cards") val correctCards: List<Int>,
    @SerialName("incorrect_cards") val incorrectCards: List<Int>,
    @SerialName("all_cards_count") val allCardsCount: Int,
    @SerialName("errors") val errors: List<ErrorAnswerDto>,
    @SerialName("right_answers") val rightAnswers: List<CorrectAnswerDto>,
)
