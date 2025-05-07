package com.example.localdecks.domain.repository

interface SyncUserMetricsRepository {

    /**
     * Выполняет синхронизацию метрик пользователя.
     */
    suspend fun syncUserMetrics()
}