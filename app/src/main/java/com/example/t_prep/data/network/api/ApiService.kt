package com.example.t_prep.data.network.api

import com.example.t_prep.data.network.dto.CreateUserRequestDto
import com.example.t_prep.data.network.dto.CreateUserResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("user")
    suspend fun createUser(
        @Body createUserRequestDto: CreateUserRequestDto
    ): CreateUserResponseDto

    @GET("user/login")
    suspend fun loginUser(
        @Query("username") userName: String,
        @Query("password") password: String
    ): Response<String>
}