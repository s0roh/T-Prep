package com.example.data.profile.domain.repository

import com.example.data.profile.domain.entity.OwnerProfileInfo

interface OwnerProfileRepository {

    suspend fun loadOwnerProfileInfo(ownerId: String): OwnerProfileInfo
}