package com.example.t_prep.data.network.dto.global

import com.google.gson.annotations.SerializedName

data class CardDto(
    @SerializedName("local_id") val id: Long,
    @SerializedName("question") val question: String,
    @SerializedName("answer") val answer: String
)
