package com.example.feature.profile.domain

import com.example.data.profile.domain.entity.ProfileInfo
import com.example.data.profile.domain.repository.ProfileRepository
import jakarta.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(): ProfileInfo = repository.getUserInfo()
}