package com.example.feature.profile.presentation.owner_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.profile.domain.LoadOwnerProfileInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class OwnerProfileViewModel @Inject constructor(
    private val loadOwnerProfileInfoUseCase: LoadOwnerProfileInfoUseCase,
) : ViewModel() {

    var screenState = MutableStateFlow<OwnerProfileScreenState>(OwnerProfileScreenState.Loading)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, message ->
        screenState.value = OwnerProfileScreenState.Error(message = message.toString())
    }

    fun loadOwnerProfile(ownerId: String) {
        viewModelScope.launch(exceptionHandler) {
            val ownerProfileInfo = loadOwnerProfileInfoUseCase(ownerId = ownerId)

            screenState.value = OwnerProfileScreenState.Success(
                userName = ownerProfileInfo.ownerProfileName,
                profileImageUri = ownerProfileInfo.ownerProfileImage,
                ownerPublicDecks = ownerProfileInfo.ownerPublicDecks
            )
        }
    }
}