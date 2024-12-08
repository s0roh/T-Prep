package com.example.network.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the response containing the user data after successful creation.
 *
 * This DTO is used to transfer the user data returned by the server after the user is successfully created.
 *
 * @property id The unique identifier for the newly created user.
 * @property userName The username of the created user.
 * @property email The email address of the created user.
 */
@Serializable
data class CreateUserResponseDto(
    @SerialName("id") val id: Long,
    @SerialName("username") val userName: String,
    @SerialName("email") val email: String
)