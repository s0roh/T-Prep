package com.example.core_network.dto.global

import com.google.gson.annotations.SerializedName

data class DeckDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("is_public") val isPublic: Boolean,
    @SerializedName("cards") val cards: List<CardDto>
)