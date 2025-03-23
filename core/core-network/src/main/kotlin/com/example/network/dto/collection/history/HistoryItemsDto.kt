package com.example.network.dto.collection.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryItemsDto(
    @SerialName("count") val count: Int,
    @SerialName("items") val items: List<HistoryItemDto>,
)