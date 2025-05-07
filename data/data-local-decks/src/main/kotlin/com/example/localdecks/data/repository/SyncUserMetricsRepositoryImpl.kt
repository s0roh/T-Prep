package com.example.localdecks.data.repository

import android.util.Log
import com.example.localdecks.data.mapper.toDto
import com.example.localdecks.domain.repository.SyncUserMetricsRepository
import com.example.network.api.ApiService
import com.example.preferences.auth.util.AuthRequestWrapper
import com.example.preferences.metrics.MetricsPreferences
import javax.inject.Inject

class SyncUserMetricsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
    private val preferences: MetricsPreferences,
) : SyncUserMetricsRepository {

    override suspend fun syncUserMetrics() {
        val localMetrics = preferences.getMetrics()

        if (with(localMetrics) {
                favouriteFilterButton == 0 &&
                        favouriteProfileButton == 0 &&
                        lastInAppTime == 0 &&
                        sumTrainingsTime == 0 &&
                        trainingsCount == 0
            }
        ) {
            Log.d("SyncWorker", "Метрики не требуют синхронизации: все значения равны 0")
            return
        }

        val metricsDto = localMetrics.toDto()

        authRequestWrapper.executeWithAuth { token ->
            val result = apiService.addMetrics(
                metricsDto = metricsDto,
                authHeader = token
            )

            if (result.isSuccessful) {
                preferences.resetMetrics()
            } else {
                Log.e("SyncWorker", "Ошибка синхронизации: ${result.errorBody()?.string()}")
            }
        }
    }
}