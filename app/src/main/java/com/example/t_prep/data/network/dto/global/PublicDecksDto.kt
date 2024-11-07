package com.example.t_prep.data.network.dto.global

import com.google.gson.annotations.SerializedName

data class PublicDecksDto(
    @SerializedName("count") val count: Int,
    @SerializedName("items") val decks: List<DeckDto>
)
