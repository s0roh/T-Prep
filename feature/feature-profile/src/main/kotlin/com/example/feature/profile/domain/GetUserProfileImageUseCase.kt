package com.example.feature.profile.domain

import android.net.Uri
import com.example.data.profile.domain.repository.ProfileRepository
import javax.inject.Inject

internal class GetUserProfileImageUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {

    suspend operator fun invoke(): Uri? = repository.getUserProfileImage()
}