package com.example.localdecks.domain.usecase

import android.util.Log
import com.example.database.TPrepDatabase
import com.example.database.models.CardDBO
import com.example.database.models.DeckDBO
import com.example.network.api.ApiService
import com.example.preferences.AuthRequestWrapper
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val database: TPrepDatabase,
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
) {

    suspend operator fun invoke() {
        authRequestWrapper.executeWithAuth { token ->
            val response = apiService.getUserInfo(authHeader = token)

            if (response.isSuccessful) {
                Log.d("SyncWorker", "=====================================================")
                Log.d(
                    "SyncWorker",
                    "Информация о пользователе успешно получена: ${response.body()}"
                )

                val serverDeckIds = response.body()?.collectionsId ?: emptyList()

                // Удаляем локальные колоды, которых нет на сервере
                val localDecks = database.deckDao.getDecks().first()
                localDecks.forEach { localDeck ->
                    if (localDeck.serverDeckId !in serverDeckIds) {
                        Log.d("SyncWorker", "Удаляем локальную колоду: $localDeck")
                        database.deckDao.deleteDeck(localDeck)
                    }
                }

                serverDeckIds.forEach { deckId ->
                    val deckDto = apiService.getDeckById(deckId = deckId, authHeader = token)
                    // Проверяем, существует ли колода в локальной базе
                    val existingDeck = database.deckDao.getDeckByServerId(deckId)
                    val deckLocalId = if (existingDeck != null) {
                        // Если колода существует, обновляем ее, если данные изменились
                        if (existingDeck.name != deckDto.name || existingDeck.isPublic != deckDto.isPublic) {
                            Log.d(
                                "SyncWorker",
                                "Обновление локальной колоды. Старое значение: $existingDeck, новое: $deckDto"
                            )
                            database.deckDao.updateDeck(
                                DeckDBO(
                                    id = existingDeck.id,
                                    serverDeckId = deckId,
                                    name = deckDto.name,
                                    isPublic = deckDto.isPublic
                                )
                            )
                        }
                        existingDeck.id
                    } else {
                        // Если колода не существует, добавляем новую
                        Log.d("SyncWorker", "Добавление новой колоды: $deckDto")
                        database.deckDao.insertDeck(
                            DeckDBO(
                                serverDeckId = deckId,
                                name = deckDto.name,
                                isPublic = deckDto.isPublic
                            )
                        ).toString()
                    }

                    // Синхронизация карточек для колоды
                    deckDto.cards.forEach { cardDto ->
                        val existingCard = database.cardDao.getCardByServerId(
                            serverCardId = cardDto.id,
                            deckId = deckLocalId
                        )
                        if (existingCard != null) {
                            // Если карточка существует, проверяем, актуальна ли информация
                            if (existingCard.question != cardDto.question || existingCard.answer != cardDto.answer) {
                                Log.d(
                                    "SyncWorker",
                                    "Обновление карточки. Старое значение: $existingCard, новое: $cardDto"
                                )
                                database.cardDao.updateCard(
                                    CardDBO(
                                        id = existingCard.id,
                                        serverCardId = cardDto.id,
                                        deckId = deckLocalId.toString(),
                                        question = cardDto.question,
                                        answer = cardDto.answer
                                    )
                                )
                            }
                        } else {
                            // Если карточка не существует, вставляем новую
                            Log.d("SyncWorker", "Добавление новой карточки: $cardDto")
                            database.cardDao.insertCard(
                                CardDBO(
                                    id = 0,
                                    serverCardId = cardDto.id,
                                    deckId = deckLocalId.toString(),
                                    question = cardDto.question,
                                    answer = cardDto.answer
                                )
                            )
                        }


                        // Удаляем карточки, которых нет на сервере
                        val localCards = database.cardDao.getCardsForDeck(deckLocalId.toString())
                            .first() // Получаем список локальных карточек
                        val serverCardIds = deckDto.cards.map { it.id }

                        localCards.forEach { localCard ->
                            if (localCard.serverCardId != null && localCard.serverCardId !in serverCardIds) {
                                Log.d(
                                    "SyncWorker",
                                    "Удаляем карточку, отсутствующую на сервере: $localCard"
                                )
                                database.cardDao.deleteCard(localCard)
                            }
                        }
                    }
                }
                Log.d("SyncWorker", "=====================================================")
            } else {
                Log.e(
                    "SyncWorker",
                    "Ошибка при получении информации о пользователе: ${
                        response.errorBody()?.string()
                    }"
                )
                Log.e("SyncWorker", "=====================================================")
            }
        }
    }
}