package com.example.auth.mapper

import com.example.network.dto.user.LoginRequestDto
import com.example.network.dto.user.RefreshRequestDto
import com.example.network.dto.user.SignupRequestDto

internal fun String.toDto(): RefreshRequestDto = RefreshRequestDto(refreshToken = this)

internal fun toLoginRequestDto(email: String, password: String): LoginRequestDto = LoginRequestDto(
    email = email,
    password = password
)

internal fun toSignupRequestDto(email: String, password: String, name: String): SignupRequestDto =
    SignupRequestDto(
        email = email,
        password = password,
        userName = name
    )
