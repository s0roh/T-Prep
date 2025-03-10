package com.example.network.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    @SerialName("id") val userId: String,
    @SerialName("username") val userName: String,
    @SerialName("email") val email: String,
    @SerialName("has_picture") val hasPicture: Boolean,
    @SerialName("collections") val collectionsId: List<String>,
)
