package com.example.history.domain.usecase

import com.example.history.domain.entity.HistoryWithTimePeriod
import com.example.history.domain.repository.HistoryRepository
import javax.inject.Inject

internal class GetGroupedHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository
) {

    suspend operator fun invoke(): List<HistoryWithTimePeriod> = repository.getGroupedHistory()
}