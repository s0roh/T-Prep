package com.example.network.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserStatisticDto(
    @SerialName("total_trainings") val totalTrainings: Int,
    @SerialName("medium_percentage") val mediumPercentage: Int,
)
