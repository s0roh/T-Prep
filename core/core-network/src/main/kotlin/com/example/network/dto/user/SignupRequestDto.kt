package com.example.network.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the request payload for creating a new user.
 *
 * This DTO is used to transfer the data necessary to create a user on the server, including username, email, and password.
 *
 * @property userName The username chosen by the user.
 * @property email The email address of the user.
 * @property password The password chosen by the user.
 */
@Serializable
data class SignupRequestDto(
    @SerialName("username") val userName: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)