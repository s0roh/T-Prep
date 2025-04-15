package com.example.network.dto.collection

import com.example.network.dto.global.OtherAnswersDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardRequestDto(
    @SerialName("question") val question: String,
    @SerialName("answer") val answer: String,
    @SerialName("other_answers") val otherAnswers: OtherAnswersDto,
)
