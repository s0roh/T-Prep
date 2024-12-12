package com.example.network.api


import com.example.network.dto.collection.DeckDto
import com.example.network.dto.global.PublicDecksDto
import com.example.network.dto.user.RefreshRequestDto
import com.example.network.dto.user.SignupRequestDto
import com.example.network.dto.user.AuthResponseDto
import com.example.network.dto.user.LoginRequestDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("public/signup")
    suspend fun signup(
        @Body signupRequestDto: SignupRequestDto
    ): Response<AuthResponseDto>

    @POST("public/login")
    suspend fun login(
        @Body loginRequestDto: LoginRequestDto
    ): Response<AuthResponseDto>

    @POST("public/refreshToken")
    suspend fun refreshToken(
        @Body refreshRequestDto: RefreshRequestDto
    ): Response<AuthResponseDto>

    @GET("global/getPublicCollections")
    suspend fun getPublicDecks(
        @Query("count") count: Int = 10,
        @Query("offset") nextFrom: Int = 0
    ): PublicDecksDto

    @GET("collection/{id}")
    suspend fun getDeckById(
        @Path("id") deckId: String
    ): DeckDto
}

fun ApiService(
    baseUrl: String,
    okHttpClient: OkHttpClient,
    json: Json = Json {
        ignoreUnknownKeys = true
    }
): ApiService {
    return retrofit(baseUrl, okHttpClient, json).create()
}

private fun retrofit(
    baseUrl: String,
    okHttpClient: OkHttpClient,
    json: Json
): Retrofit {
    val jsonConverterFactory = json.asConverterFactory("application/json".toMediaType())

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(jsonConverterFactory)
        .client(okHttpClient)
        .build()
}