package com.example.localdecks.domain.repository

interface SyncUserDataRepository {

    suspend fun syncUserData()
}