package com.example.network.api


import com.example.network.dto.global.DeckDto
import com.example.network.dto.global.PublicDecksDto
import com.example.network.dto.user.CreateUserRequestDto
import com.example.network.dto.user.CreateUserResponseDto
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

    @POST("user")
    suspend fun createUser(
        @Body createUserRequestDto: CreateUserRequestDto
    ): CreateUserResponseDto

    @GET("user/login")
    suspend fun loginUser(
        @Query("username") userName: String,
        @Query("password") password: String
    ): Response<String>

    @GET("global/getPublicCollections")
    suspend fun getPublicDecks(
        @Query("count") count: Int = 10,
        @Query("offset") nextFrom: Int = 0
    ): PublicDecksDto

    @GET("getCollection/{id}")
    suspend fun getDeckById(
        @Path("id") deckId: Long
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