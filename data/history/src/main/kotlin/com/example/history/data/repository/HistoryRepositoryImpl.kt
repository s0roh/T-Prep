package com.example.history.data.repository

import android.annotation.SuppressLint
import com.example.database.TPrepDatabase
import com.example.database.models.Source
import com.example.history.data.mapper.toDBO
import com.example.history.data.mapper.toEntity
import com.example.history.domain.entity.HistoryGroup
import com.example.history.domain.entity.HistoryGroup.DeckHistory
import com.example.history.domain.entity.TimePeriod
import com.example.history.domain.entity.TrainingHistory
import com.example.history.domain.repository.HistoryRepository
import com.example.network.api.ApiService
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class HistoryRepositoryImpl @Inject internal constructor(
    private val database: TPrepDatabase,
    private val apiService: ApiService
) : HistoryRepository {

    override suspend fun getLastTrainingPerDeck(): List<TrainingHistory> {
        return database.historyDao.getLastTrainingPerDeck().map { it.toEntity() }
    }

    override suspend fun insertHistory(
        history: TrainingHistory,
        source: Source
    ) {
        return database.historyDao.insertHistory(history.toDBO())
    }

    override suspend fun getGroupedHistory(): List<HistoryGroup> {
        val lastTrainings = getLastTrainingPerDeck()
        val now = System.currentTimeMillis()

        return lastTrainings
            .groupBy { determineTimePeriod(it.timestamp, now) }
            .map { (timePeriod, records) ->
                HistoryGroup(
                    timePeriod = timePeriod,
                    decks = records.map { record ->
                        getDeckHistory(record.deckId, record.source)
                    }
                )
            }
    }

    private suspend fun getDeckHistory(deckId: Long, source: Source): DeckHistory {
        return when (source) {
            Source.LOCAL -> {
                //TODO
                throw NotImplementedError("Fetching local deck details is not implemented.")
            }

            Source.NETWORK -> {
                val deck = apiService.getDeckById(deckId)
                return DeckHistory(
                    deckId = deck.id,
                    deckName = deck.name,
                    cardsCount = deck.cards.size
                )
            }
        }
    }

    @SuppressLint("NewApi")
    private fun determineTimePeriod(timestamp: Long, now: Long): TimePeriod {
        val currentDateTime = Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toLocalDate()
        val targetDateTime =
            Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()

        return when {
            currentDateTime == targetDateTime -> TimePeriod.TODAY
            currentDateTime.minusDays(1) == targetDateTime -> TimePeriod.YESTERDAY
            currentDateTime.with(DayOfWeek.MONDAY) <= targetDateTime -> TimePeriod.THIS_WEEK
            currentDateTime.withDayOfMonth(1) <= targetDateTime -> TimePeriod.THIS_MONTH
            currentDateTime.withDayOfYear(1) <= targetDateTime -> TimePeriod.THIS_YEAR
            else -> TimePeriod.OLDER
        }
    }
}