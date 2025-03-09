package com.example.network.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPictureResponseDto(
    @SerialName("message") val message: String
)