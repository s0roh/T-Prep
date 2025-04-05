package com.example.feature.profile.presentation.owner_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.profile.domain.GetFavouriteDecksIdsUseCase
import com.example.feature.profile.domain.LikeOrUnlikeUseCase
import com.example.feature.profile.domain.LoadOwnerProfileInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class OwnerProfileViewModel @Inject constructor(
    private val loadOwnerProfileInfoUseCase: LoadOwnerProfileInfoUseCase,
    private val likeOrUnlikeUseCase: LikeOrUnlikeUseCase,
    private val getFavouriteDecksIdsUseCase: GetFavouriteDecksIdsUseCase,
) : ViewModel() {

    var screenState = MutableStateFlow<OwnerProfileScreenState>(OwnerProfileScreenState.Loading)
        private set

    var eventFlow = MutableSharedFlow<OwnerProfileEvent>()
        private  set

    private val exceptionHandler = CoroutineExceptionHandler { _, message ->
        viewModelScope.launch {
            eventFlow.emit(OwnerProfileEvent.ShowError(message.message ?: "Unknown Error"))
        }
    }

    fun loadOwnerProfile(ownerId: String) {
        viewModelScope.launch(exceptionHandler) {
            val ownerProfileInfo = loadOwnerProfileInfoUseCase(ownerId = ownerId)

            val favouriteDeckIds = getFavouriteDecksIdsUseCase()
            val updatedDecks = ownerProfileInfo.ownerPublicDecks.map { deck ->
                val isLiked = favouriteDeckIds.contains(deck.id)
                deck.copy(isLiked = isLiked)
            }

            screenState.value = OwnerProfileScreenState.Success(
                userId = ownerId,
                userName = ownerProfileInfo.ownerProfileName,
                profileImageUri = ownerProfileInfo.ownerProfileImage,
                ownerPublicDecks = updatedDecks,
                totalTrainings = ownerProfileInfo.ownerTotalTrainings,
                averageAccuracy = ownerProfileInfo.ownerMediumPercentage
            )
        }
    }

    fun onLikeClick(deckId: String, isLiked: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            val updatedLikes = likeOrUnlikeUseCase(deckId = deckId, isLiked = isLiked)

            val favouriteDeckIds = getFavouriteDecksIdsUseCase()
            val isLiked = favouriteDeckIds.contains(deckId)

            screenState.value =
                (screenState.value as? OwnerProfileScreenState.Success)?.let { currentState ->
                    val updatedDecks = currentState.ownerPublicDecks.map { deck ->
                        if (deck.id == deckId) deck.copy(
                            likes = updatedLikes,
                            isLiked = isLiked
                        ) else deck
                    }

                    currentState.copy(ownerPublicDecks = updatedDecks)
                } ?: screenState.value
        }
    }
}