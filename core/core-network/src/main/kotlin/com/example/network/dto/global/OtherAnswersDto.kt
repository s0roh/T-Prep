package com.example.network.dto.global

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtherAnswersDto(
    @SerialName("count") val count: Int = 0,
    @SerialName("items") val items: List<String> = emptyList(),
)
