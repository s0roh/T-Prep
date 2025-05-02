package com.example.feature.profile.presentation.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.profile.domain.DeleteUserProfileImageUseCase
import com.example.feature.profile.domain.GetTrainingStatsUseCase
import com.example.feature.profile.domain.GetUserInfoUseCase
import com.example.feature.profile.domain.SaveUserProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
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
            val profileInfo = getUserInfoUseCase()

            val (totalTrainings, averageAccuracy) = getTrainingStatsUseCase()

            screenState.value = ProfileScreenState.Success(
                userName = profileInfo.profileName,
                userEmail = profileInfo.profileEmail,
                profileImageUri = profileInfo.profileImage,
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

    fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "${UUID.randomUUID()}.jpg")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return Uri.fromFile(file)
    }
}