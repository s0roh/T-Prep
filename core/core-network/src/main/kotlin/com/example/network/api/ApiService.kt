package com.example.network.api


import com.example.network.dto.collection.CardRequestDto
import com.example.network.dto.collection.DeckDto
import com.example.network.dto.collection.DeckRequestDto
import com.example.network.dto.collection.ResponseMessageDto
import com.example.network.dto.global.AutoRemindersDto
import com.example.network.dto.global.CardDto
import com.example.network.dto.global.PublicDecksDto
import com.example.network.dto.user.RefreshRequestDto
import com.example.network.dto.user.SignupRequestDto
import com.example.network.dto.user.AuthResponseDto
import com.example.network.dto.user.LoginRequestDto
import com.example.network.dto.user.UserInfoDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("public/signup")
    suspend fun signup(
        @Body signupRequestDto: SignupRequestDto,
    ): Response<AuthResponseDto>

    @POST("public/login")
    suspend fun login(
        @Body loginRequestDto: LoginRequestDto,
    ): Response<AuthResponseDto>

    @POST("public/refreshToken")
    suspend fun refreshToken(
        @Body refreshRequestDto: RefreshRequestDto,
    ): Response<AuthResponseDto>

    @GET("global/getPublicCollections")
    suspend fun getPublicDecks(
        @Query("count") count: Int = 10,
        @Query("offset") nextFrom: Int = 0,
    ): PublicDecksDto

    @GET("collection/search")
    suspend fun getPublicDecksOrSearch(
        @Query("name") name: String? = null,
        @Query("count") count: Int = 10,
        @Query("offset") nextFrom: Int = 0,
        @Header("Authorization") authHeader: String? = null,
    ): PublicDecksDto

    @GET("collection/{id}")
    suspend fun getDeckById(
        @Path("id") deckId: String,
        @Header("Authorization") authHeader: String? = null,
    ): DeckDto

    @POST("collection")
    suspend fun createDeck(
        @Body deckRequestDto: DeckRequestDto,
        @Header("Authorization") authHeader: String? = null,
    ): Response<DeckDto>

    @PUT("collection/{id}")
    suspend fun updateDeck(
        @Path("id") deckId: String,
        @Body deckRequestDto: DeckRequestDto,
        @Header("Authorization") authHeader: String? = null,
    ): Response<ResponseMessageDto>

    @DELETE("collection/{id}")
    suspend fun deleteDeck(
        @Path("id") deckId: String,
        @Header("Authorization") authHeader: String? = null,
    ): Response<ResponseMessageDto>

    @POST("collection/{id}/card")
    suspend fun createCard(
        @Path("id") deckId: String,
        @Body cardRequestDto: CardRequestDto,
        @Header("Authorization") authHeader: String? = null,
    ): Response<CardDto>

    @PUT("collection/{id}/card/{cardID}")
    suspend fun updateCard(
        @Path("id") deckId: String,
        @Path("cardID") cardId: Int,
        @Body cardRequestDto: CardRequestDto,
        @Header("Authorization") authHeader: String? = null,
    ): Response<ResponseMessageDto>

    @DELETE("collection/{id}/card/{cardID}")
    suspend fun deleteCard(
        @Path("id") deckId: String,
        @Path("cardID") cardId: Int,
        @Header("Authorization") authHeader: String? = null,
    ): Response<ResponseMessageDto>

    @GET("user")
    suspend fun getUserInfo(
        @Header("Authorization") authHeader: String? = null
    ): Response<UserInfoDto>

    @GET("global/getTrainingPlan")
    suspend fun getTrainingPlan(
        @Query("start_date") startDate: Int,
        @Query("end_date") finishDate: Int,
        @Query("preferred_time") preferredTime: Int,
        @Header("Authorization") authHeader: String? = null
    ): AutoRemindersDto
}

fun ApiService(
    baseUrl: String,
    okHttpClient: OkHttpClient,
    json: Json = Json {
        ignoreUnknownKeys = true
    },
): ApiService {
    return retrofit(baseUrl, okHttpClient, json).create()
}

private fun retrofit(
    baseUrl: String,
    okHttpClient: OkHttpClient,
    json: Json,
): Retrofit {
    val jsonConverterFactory = json.asConverterFactory("application/json".toMediaType())

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(jsonConverterFactory)
        .client(okHttpClient)
        .build()
}