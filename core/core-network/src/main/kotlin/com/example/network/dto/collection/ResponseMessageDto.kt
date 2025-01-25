package com.example.network.dto.collection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMessageDto(
    @SerialName("message") val message: String,
)
