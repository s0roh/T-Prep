package com.example.network.dto.global

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutoRemindersDto(
    @SerialName("count") val count: Int,
    @SerialName("items") val reminders: List<Int>,
)
