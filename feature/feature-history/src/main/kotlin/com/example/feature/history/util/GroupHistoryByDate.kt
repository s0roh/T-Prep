package com.example.feature.history.util

import com.example.history.domain.entity.TrainingHistoryItem

internal fun groupHistoryByDate(historyItems: List<TrainingHistoryItem>): Map<String, List<TrainingHistoryItem>> {
    return historyItems.groupBy { getFormattedDateForHeader(it.timestamp) }
}