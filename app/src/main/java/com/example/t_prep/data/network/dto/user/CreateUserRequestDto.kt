package com.example.t_prep.data.network.dto.user

import com.google.gson.annotations.SerializedName

data class CreateUserRequestDto(
    @SerializedName("username") val userName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
