package com.example.history.data.repository

import android.annotation.SuppressLint
import com.example.database.TPrepDatabase
import com.example.history.data.mapper.toDBO
import com.example.history.data.mapper.toEntity
import com.example.history.domain.entity.HistoryWithTimePeriod
import com.example.history.domain.entity.TimePeriod
import com.example.history.domain.entity.TrainingHistory
import com.example.history.domain.repository.HistoryRepository
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class HistoryRepositoryImpl @Inject internal constructor(
    private val database: TPrepDatabase
) : HistoryRepository {

    override suspend fun getLastTrainingPerDeck(): List<TrainingHistory> {
        return database.historyDao.getLastTrainingPerDeck().map { it.toEntity() }
    }

    override suspend fun insertHistory(history: TrainingHistory) {
        return database.historyDao.insertOrUpdateHistory(history.toDBO())
    }

    override suspend fun getGroupedHistory(): List<HistoryWithTimePeriod> {
        val lastTrainings = getLastTrainingPerDeck()
        val currentTime = System.currentTimeMillis()

        return lastTrainings
            .groupBy { getTimePeriodForTimestamp(it.timestamp, currentTime) }
            .map { (timePeriod, decks) ->
                HistoryWithTimePeriod(
                    timePeriod = timePeriod,
                    trainingHistories = decks
                )
            }
    }

    @SuppressLint("NewApi")
    private fun getTimePeriodForTimestamp(recordTimestamp: Long, currentTime: Long): TimePeriod {
        val currentDate =
            Instant.ofEpochMilli(currentTime).atZone(ZoneId.systemDefault()).toLocalDate()
        val recordDate =
            Instant.ofEpochMilli(recordTimestamp).atZone(ZoneId.systemDefault()).toLocalDate()

        return when {
            currentDate == recordDate -> TimePeriod.TODAY
            currentDate.minusDays(1) == recordDate -> TimePeriod.YESTERDAY
            currentDate.with(DayOfWeek.MONDAY) <= recordDate -> TimePeriod.THIS_WEEK
            currentDate.withDayOfMonth(1) <= recordDate -> TimePeriod.THIS_MONTH
            currentDate.withDayOfYear(1) <= recordDate -> TimePeriod.THIS_YEAR
            else -> TimePeriod.OLDER
        }
    }
}