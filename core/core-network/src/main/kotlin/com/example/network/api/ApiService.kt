package com.example.network.api


import com.example.network.dto.collection.CardPictureResponseDto
import com.example.network.dto.collection.CardRequestDto
import com.example.network.dto.collection.DeckDto
import com.example.network.dto.collection.DeckRequestDto
import com.example.network.dto.collection.LikeDto
import com.example.network.dto.collection.ResponseMessageDto
import com.example.network.dto.collection.history.HistoryItemDto
import com.example.network.dto.collection.history.HistoryItemsDto
import com.example.network.dto.global.AutoRemindersDto
import com.example.network.dto.global.CardDto
import com.example.network.dto.global.MetricsDto
import com.example.network.dto.global.PublicDecksDto
import com.example.network.dto.user.AuthResponseDto
import com.example.network.dto.user.LoginRequestDto
import com.example.network.dto.user.RefreshRequestDto
import com.example.network.dto.user.SignupRequestDto
import com.example.network.dto.user.UserInfoDto
import com.example.network.dto.user.UserPictureResponseDto
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @GET("collection/search")
    suspend fun getPublicDecksOrSearch(
        @Query("name") name: String? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("category") category: String? = null,
        @Query("count") count: Int = 10,
        @Query("offset") nextFrom: Int = 0,
        @Header("Authorization") authHeader: String? = null,
    ): PublicDecksDto

    @POST("collection/training")
    suspend fun addTrainingToHistory(
        @Body historyItemDto: HistoryItemDto,
        @Header("Authorization") authHeader: String? = null,
    ): Response<ResponseMessageDto>

    @GET("user/history")
    suspend fun getUserHistory(
        @Query("from_time") fromTime: Int = 0,
        @Header("Authorization") authHeader: String? = null,
    ): Response<HistoryItemsDto>

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

    @GET("collection/{id}/card/{cardID}/picture")
    suspend fun getCardPicture(
        @Path("id") deckId: String,
        @Path("cardID") cardId: Int,
        @Query("object_name") objectName: String,
        @Header("Authorization") authHeader: String? = null,
    ): Response<ResponseBody>

    @Multipart
    @PUT("collection/{id}/card/{cardID}/picture")
    suspend fun updateCardPicture(
        @Path("id") deckId: String,
        @Path("cardID") cardId: Int,
        @Header("Authorization") authHeader: String? = null,
        @Part image: MultipartBody.Part,
    ): Response<CardPictureResponseDto>

    @DELETE("collection/{id}/card/{cardID}/picture")
    suspend fun deleteCardPicture(
        @Path("id") deckId: String,
        @Path("cardID") cardId: Int,
        @Query("object_name") objectName: String,
        @Header("Authorization") authHeader: String? = null,
    ): Response<ResponseMessageDto>

    @PUT("collection/{id}/like")
    suspend fun like(
        @Path("id") deckId: String,
        @Header("Authorization") authHeader: String? = null,
    ): Response<LikeDto>

    @PUT("collection/{id}/unlike")
    suspend fun unlike(
        @Path("id") deckId: String,
        @Header("Authorization") authHeader: String? = null,
    ): Response<LikeDto>

    @GET("user")
    suspend fun getUserInfo(
        @Query("id") userId: String? = null,
        @Header("Authorization") authHeader: String? = null,
    ): Response<UserInfoDto>

    @GET("user/picture")
    suspend fun getUserPicture(
        @Query("id") userId: String? = null,
        @Header("Authorization") authHeader: String? = null,
    ): Response<ResponseBody>

    @Multipart
    @PUT("user/picture")
    suspend fun updateUserPicture(
        @Header("Authorization") authHeader: String? = null,
        @Part image: MultipartBody.Part,
    ): Response<UserPictureResponseDto>

    @DELETE("user/picture")
    suspend fun deleteUserPicture(
        @Header("Authorization") authHeader: String? = null,
    ): Response<UserPictureResponseDto>

    @GET("global/getTrainingPlan")
    suspend fun getTrainingPlan(
        @Query("start_date") startDate: Int,
        @Query("end_date") finishDate: Int,
        @Query("preferred_time") preferredTime: Int,
        @Header("Authorization") authHeader: String? = null,
    ): AutoRemindersDto

    @POST("global/addMetrics")
    suspend fun addMetrics(
        @Body metricsDto: MetricsDto,
        @Header("Authorization") authHeader: String? = null,
    ): Response<Unit>
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