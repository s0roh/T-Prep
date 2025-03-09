package com.example.feature.profile.presentation.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.profile.domain.ClearTokensUseCase
import com.example.feature.profile.domain.GetTrainingStatsUseCase
import com.example.feature.profile.domain.GetUserEmailUseCase
import com.example.feature.profile.domain.GetUserNameUseCase
import com.example.feature.profile.domain.GetUserProfileImageUseCase
import com.example.feature.profile.domain.SaveUserProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import androidx.core.net.toUri
import com.example.feature.profile.domain.DeleteUserProfileImageUseCase

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val clearTokensUseCase: ClearTokensUseCase,
    private val getUserProfileImageUseCase: GetUserProfileImageUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val getUserEmailUseCase: GetUserEmailUseCase,
    private val saveUserProfileImageUseCase: SaveUserProfileImageUseCase,
    private val deleteUserProfileImageUseCase: DeleteUserProfileImageUseCase,
    private val getTrainingStatsUseCase: GetTrainingStatsUseCase,
) : ViewModel() {

    var screenState = MutableStateFlow<ProfileScreenState>(ProfileScreenState.Loading)
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, message ->
        screenState.value = ProfileScreenState.Error(message = message.toString())
    }

    init {
        loadProfile()
    }

    fun refreshProfile() {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch(exceptionHandler) {
            val userName = getUserNameUseCase()
                ?: throw IllegalArgumentException("User name is missing and cannot be null.")
            val userEmail = getUserEmailUseCase()
                ?: throw IllegalArgumentException("User email is missing and cannot be null.")
            val profileImageUri = getUserProfileImageUseCase()

            val (totalTrainings, averageAccuracy) = getTrainingStatsUseCase()

            screenState.value = ProfileScreenState.Success(
                userName = userName,
                userEmail = userEmail,
                profileImageUri = profileImageUri,
                totalTrainings = totalTrainings,
                averageAccuracy = averageAccuracy
            )
        }
    }

    fun setProfileImage(uri: String) {
        viewModelScope.launch(exceptionHandler) {
            val currentState = screenState.value
            if (currentState !is ProfileScreenState.Success) {
                throw IllegalStateException("setProfileImage called in an invalid state")
            }
            saveUserProfileImageUseCase(uri)
            screenState.value = currentState.copy(profileImageUri = uri.toUri())
        }
    }

    fun deleteProfileImage() {
        viewModelScope.launch(exceptionHandler) {
            val currentState = screenState.value
            if (currentState !is ProfileScreenState.Success) {
                throw IllegalStateException("deleteProfileImage called in an invalid state")
            }
            deleteUserProfileImageUseCase()
            screenState.value = currentState.copy(profileImageUri = null)
        }
    }

    fun logout() {
        clearTokensUseCase()
    }

    fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "${UUID.randomUUID()}.jpg")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return Uri.fromFile(file)
    }
}