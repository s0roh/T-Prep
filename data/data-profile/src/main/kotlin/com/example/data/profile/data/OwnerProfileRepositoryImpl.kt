package com.example.data.profile.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.common.ui.entity.DeckUiModel
import com.example.data.profile.domain.entity.OwnerProfileInfo
import com.example.data.profile.domain.repository.OwnerProfileRepository
import com.example.network.api.ApiService
import com.example.preferences.AuthRequestWrapper
import java.io.File
import javax.inject.Inject

class OwnerProfileRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
) : OwnerProfileRepository {

    override suspend fun loadOwnerProfileInfo(ownerId: String): OwnerProfileInfo {
        return authRequestWrapper.executeWithAuth { token ->
            val userResponse = apiService.getUserInfo(userId = ownerId, authHeader = token)
            if (!userResponse.isSuccessful) {
                throw Exception("Failed to load owner profile: ${userResponse.code()}")
            }

            val userInfo = userResponse.body() ?: throw Exception("User info is null")
            val ownerImage = getOwnerProfileImage(ownerId)

            val favouriteDecksIds =
                userInfo.favourite?.toSet() ?: emptySet()

            val ownerPublicDecks = userInfo.collectionsId.map { deckId ->
                val deckDto = apiService.getDeckById(deckId, token)
                DeckUiModel(
                    id = deckDto.id,
                    name = deckDto.name,
                    isPublic = deckDto.isPublic,
                    cardsCount = deckDto.cards.size,
                    likes = deckDto.likes,
                    trainings = deckDto.trainings,
                    shouldShowLikes = true,
                    isLiked = favouriteDecksIds.contains(deckDto.id)
                )
            }
            return@executeWithAuth OwnerProfileInfo(
                ownerProfileName = userInfo.userName,
                ownerProfileImage = ownerImage,
                ownerPublicDecks = ownerPublicDecks,
                ownerTotalTrainings = userInfo.statistics.totalTrainings,
                ownerMediumPercentage = userInfo.statistics.mediumPercentage,
            )
        }
    }

    private suspend fun getOwnerProfileImage(ownerId: String): Uri? {
        return try {
            authRequestWrapper.executeWithAuth { token ->
                val response = apiService.getUserPicture(authHeader = token, userId = ownerId)
                if (response.isSuccessful) {
                    response.body()?.byteStream()?.let { inputStream ->
                        val tempFile = File(context.filesDir, "owner_profile_pic.jpg")
                        tempFile.outputStream().use { output ->
                            inputStream.copyTo(output)
                        }
                        Uri.fromFile(tempFile)
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(
                "OwnerProfileRepositoryImpl",
                "getOwnerProfileImage: Error fetching image: ${e.message}"
            )
            null
        }
    }
}