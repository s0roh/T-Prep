package com.example.t_prep.data.network.dto.user

import com.google.gson.annotations.SerializedName

data class CreateUserResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val userName: String,
   @SerializedName("email") val email: String
)
