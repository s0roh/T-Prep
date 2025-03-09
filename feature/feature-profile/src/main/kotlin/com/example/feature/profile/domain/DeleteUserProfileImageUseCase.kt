package com.example.feature.profile.domain

import com.example.data.profile.domain.ProfileRepository
import jakarta.inject.Inject

class DeleteUserProfileImageUseCase @Inject constructor(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke() = repository.deleteUserProfileImage()
}