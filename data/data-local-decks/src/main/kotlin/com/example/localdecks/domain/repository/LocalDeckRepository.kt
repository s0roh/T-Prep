package com.example.localdecks.domain.repository

import android.net.Uri
import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.common.ui.entity.DeckUiModel
import kotlinx.coroutines.flow.Flow

interface LocalDeckRepository {

    /**
     * Получает поток список всех локальных колод.
     *
     * @return [Flow] список [DeckUiModel].
     */
    fun getDecks(): Flow<List<DeckUiModel>>

    /**
     * Получает колоду по её идентификатору.
     *
     * @param deckId Идентификатор колоды.
     * @return Объект [Deck] или null, если не найден.
     */
    suspend fun getDeckById(deckId: String): Deck?


    /**
     * Вставляет новую колоду в базу данных.
     *
     * @param deck Объект [Deck] для вставки.
     */
    suspend fun insertDeck(deck: Deck)

    /**
     * Обновляет существующую колоду.
     *
     * @param deck Обновлённый объект [Deck].
     */
    suspend fun updateDeck(deck: Deck)

    /**
     * Удаляет колоду по её идентификатору.
     *
     * @param deckId Идентификатор колоды.
     */
    suspend fun deleteDeck(deckId: String)

    /**
     * Выполняет "мягкое" удаление колоды (помечает как удалённую).
     *
     * @param deckId Идентификатор колоды.
     */
    suspend fun softDeleteDeck(deckId: String)

    /**
     * Восстанавливает ранее "мягко" удалённую колоду.
     *
     * @param deckId Идентификатор колоды.
     */
    suspend fun restoreDeck(deckId: String)

    /**
     * Получает поток список всех карточек для указанной колоды.
     *
     * @param deckId Идентификатор колоды.
     * @return [Flow] список [Card].
     */
    fun getCardsForDeck(deckId: String): Flow<List<Card>>

    /**
     * Получает карточку по её идентификатору.
     *
     * @param cardId Идентификатор карточки.
     * @return Объект [Card] или null, если не найден.
     */
    suspend fun getCardById(cardId: Int): Card?

    /**
     * Получает URI изображения для заданной карточки.
     *
     * @param deckId Идентификатор колоды.
     * @param cardId Идентификатор карточки.
     * @return [Uri] изображения или null, если изображение отсутствует.
     */
    suspend fun getCardPicture(deckId: String, cardId: Int): Uri?

    /**
     * Вставляет новую карточку в указанную колоду.
     *
     * @param card Объект [Card] для вставки.
     * @param deckId Идентификатор колоды.
     * @return Идентификатор вставленной карточки.
     */
    suspend fun insertCard(card: Card, deckId: String): Int

    /**
     * Обновляет существующую карточку.
     *
     * @param card Обновлённый объект [Card].
     */
    suspend fun updateCard(card: Card)

    /**
     * Обновляет URI изображения карточки.
     *
     * @param deckId Идентификатор колоды.
     * @param cardId Идентификатор карточки.
     * @param pictureUri Новый [Uri] изображения.
     */
    suspend fun updateCardPicture(deckId: String, cardId: Int, pictureUri: Uri)

    /**
     * Удаляет карточку из базы данных.
     *
     * @param card Объект [Card] для удаления.
     */
    suspend fun deleteCard(card: Card)

    /**
     * Удаляет изображение, связанное с карточкой.
     *
     * @param deckId Идентификатор колоды.
     * @param cardId Идентификатор карточки.
     */
    suspend fun deleteCardPicture(deckId: String, cardId: Int)
}