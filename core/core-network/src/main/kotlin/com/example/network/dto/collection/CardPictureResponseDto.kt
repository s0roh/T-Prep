package com.example.network.dto.collection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardPictureResponseDto(
    @SerialName("object_name") val objectName: String
)