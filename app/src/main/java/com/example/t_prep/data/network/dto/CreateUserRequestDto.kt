package com.example.t_prep.data.network.dto

data class CreateUserRequestDto(
    val username: String? = null,
    val email: String? = null,
    val password: String
)
