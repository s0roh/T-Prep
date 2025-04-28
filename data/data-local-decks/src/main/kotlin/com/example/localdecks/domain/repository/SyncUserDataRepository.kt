package com.example.localdecks.domain.repository

interface SyncUserDataRepository {

    /**
     * Выполняет синхронизацию пользовательских данных с сервером.
     */
    suspend fun syncUserData()
}