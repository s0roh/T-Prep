package com.example.core_network.api

import com.example.core_network.dto.global.PublicDecksDto
import com.example.core_network.dto.user.CreateUserRequestDto
import com.example.core_network.dto.user.CreateUserResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Query

interface ApiService {

    @retrofit2.http.POST("user")
    suspend fun createUser(
        @Body createUserRequestDto: CreateUserRequestDto
    ): CreateUserResponseDto

    @retrofit2.http.GET("user/login")
    suspend fun loginUser(
        @Query("username") userName: String,
        @Query("password") password: String
    ): Response<String>

    @retrofit2.http.GET("global/getPublicCollections")
    suspend fun getPublicDecks(
        @Query("count") count: Int = 10,
        @Query("offset") nextFrom: Int = 0
    ): PublicDecksDto
}