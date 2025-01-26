package com.example.localdecks.data.repository

import android.util.Log
import com.example.database.TPrepDatabase
import com.example.database.models.CardDBO
import com.example.database.models.DeckDBO
import com.example.localdecks.domain.repository.SyncUserDataRepository
import com.example.network.api.ApiService
import com.example.network.dto.global.CardDto
import com.example.preferences.AuthPreferences
import com.example.preferences.AuthRequestWrapper
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncUserDataRepositoryImpl @Inject constructor(
    private val database: TPrepDatabase,
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
    private val preferences: AuthPreferences
): SyncUserDataRepository {

    override suspend fun syncUserData() {
        authRequestWrapper.executeWithAuth { token ->
            if (token.isNullOrEmpty()) {
                Log.e("SyncWorker", "Токен отсутствует или пустой.")
                return@executeWithAuth
            }

            val response = apiService.getUserInfo(authHeader = token)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody == null) {
                    Log.e("SyncWorker", "Ответ от сервера пустой.")
                    return@executeWithAuth
                }

                val userId = responseBody.userId
                val serverDeckIds = responseBody.collectionsId

                preferences.saveUserId(userId)

                syncDecksWithServer(userId, serverDeckIds, token)
            } else {
                Log.e("SyncWorker", "Ошибка при получении информации о пользователе: ${response.errorBody()?.string()}")
            }
        }
    }

    private suspend fun syncDecksWithServer(userId: String, serverDeckIds: List<String>, token: String) {
        val allDecks = database.deckDao.getAllDecks().first()
        val serverDeckIdsSet = serverDeckIds.toSet()

        val userDecks = allDecks.filter { it.userId == userId }
        val nonUserDecks = allDecks.filter { it.userId != userId }

        handleUserDecks(userDecks, serverDeckIdsSet)
        handleNonUserDecks(nonUserDecks)

        serverDeckIds.forEach { deckId ->
            syncDeckWithServer(deckId, userId, token)
        }
    }

    private suspend fun handleUserDecks(userDecks: List<DeckDBO>, serverDeckIdsSet: Set<String>) {
        userDecks.forEach { localDeck ->
            if (localDeck.serverDeckId in serverDeckIdsSet) {
                if (localDeck.isDeleted) {
                    Log.d("SyncWorker", "Восстанавливаем колоду пользователя: $localDeck")
                    database.deckDao.updateDeck(localDeck.copy(isDeleted = false))
                }
            } else {
                Log.d("SyncWorker", "Удаляем локальную колоду: $localDeck")
                database.deckDao.deleteDeck(localDeck)
            }
        }
    }

    private suspend fun handleNonUserDecks(nonUserDecks: List<DeckDBO>) {
        nonUserDecks.forEach { localDeck ->
            if (!localDeck.isDeleted) {
                Log.d("SyncWorker", "Скрываем колоду: $localDeck")
                database.deckDao.updateDeck(localDeck.copy(isDeleted = true))
            }
        }
    }

    private suspend fun syncDeckWithServer(deckId: String, userId: String, token: String) {
        val deckDto = apiService.getDeckById(deckId = deckId, authHeader = token)
        val existingDeck = database.deckDao.getDeckByServerId(deckId)

        val deckLocalId = if (existingDeck != null) {
            if (existingDeck.name != deckDto.name || existingDeck.isPublic != deckDto.isPublic) {
                Log.d("SyncWorker", "Обновление локальной колоды. Старое: $existingDeck, новое: $deckDto")
                database.deckDao.updateDeck(
                    existingDeck.copy(
                        name = deckDto.name,
                        isPublic = deckDto.isPublic
                    )
                )
            }
            existingDeck.id
        } else {
            Log.d("SyncWorker", "Добавление новой колоды: $deckDto")
            database.deckDao.insertDeck(
                DeckDBO(
                    serverDeckId = deckId,
                    name = deckDto.name,
                    isPublic = deckDto.isPublic,
                    userId = userId
                )
            ).toString()
        }

        syncCardsWithServer(deckDto.cards, deckLocalId)
    }

    private suspend fun syncCardsWithServer(cards: List<CardDto>, deckLocalId: String) {
        val localCards = database.cardDao.getCardsForDeck(deckLocalId).first()
        val serverCardIds = cards.map { it.id }

        cards.forEach { cardDto ->
            val existingCard = database.cardDao.getCardByServerId(
                serverCardId = cardDto.id,
                deckId = deckLocalId
            )
            if (existingCard != null) {
                if (existingCard.question != cardDto.question || existingCard.answer != cardDto.answer) {
                    Log.d("SyncWorker", "Обновление карточки. Старое: $existingCard, новое: $cardDto")
                    database.cardDao.updateCard(
                        existingCard.copy(
                            question = cardDto.question,
                            answer = cardDto.answer
                        )
                    )
                }
            } else {
                Log.d("SyncWorker", "Добавление новой карточки: $cardDto")
                database.cardDao.insertCard(
                    CardDBO(
                        id = 0,
                        serverCardId = cardDto.id,
                        deckId = deckLocalId,
                        question = cardDto.question,
                        answer = cardDto.answer
                    )
                )
            }
        }

        localCards.forEach { localCard ->
            if (localCard.serverCardId != null && localCard.serverCardId !in serverCardIds) {
                Log.d("SyncWorker", "Удаляем карточку, отсутствующую на сервере: $localCard")
                database.cardDao.deleteCard(localCard)
            }
        }
    }
}