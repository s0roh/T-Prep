package com.example.network.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    @SerialName("id") val userId: String,
    @SerialName("username") val userName: String,
    @SerialName("email") val email: String? = null,
    @SerialName("has_picture") val hasPicture: Boolean,
    @SerialName("collections") val collectionsId: List<String>,
    @SerialName("statistics") val statistics: UserStatisticDto,
    @SerialName("favourite") val favourite: List<String>? = emptyList(),
)
