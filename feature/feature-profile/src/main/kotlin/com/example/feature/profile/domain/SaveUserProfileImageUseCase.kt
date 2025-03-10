package com.example.feature.profile.domain

import androidx.core.net.toUri
import com.example.data.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class SaveUserProfileImageUseCase @Inject constructor(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(uri: String) = repository.updateUserProfileImage(uri.toUri())
}