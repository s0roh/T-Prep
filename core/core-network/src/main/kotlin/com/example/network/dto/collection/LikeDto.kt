package com.example.network.dto.collection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikeDto(
    @SerialName("likes") val likes: Int,
)
