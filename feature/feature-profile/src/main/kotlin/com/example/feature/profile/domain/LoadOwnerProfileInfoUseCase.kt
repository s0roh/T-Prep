package com.example.feature.profile.domain

import com.example.data.profile.domain.entity.OwnerProfileInfo
import com.example.data.profile.domain.repository.OwnerProfileRepository
import javax.inject.Inject

internal class LoadOwnerProfileInfoUseCase @Inject constructor(
    private val repository: OwnerProfileRepository,
) {

    suspend operator fun invoke(ownerId: String): OwnerProfileInfo =
        repository.loadOwnerProfileInfo(ownerId = ownerId)
}