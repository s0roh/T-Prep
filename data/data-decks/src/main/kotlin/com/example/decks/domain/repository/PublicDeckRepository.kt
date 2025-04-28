package com.example.decks.domain.repository

import androidx.paging.PagingData
import com.example.common.domain.entity.Deck
import com.example.common.ui.entity.DeckUiModel
import com.example.database.models.Source
import kotlinx.coroutines.flow.Flow

interface PublicDeckRepository {

    /**
     * Получает поток постраничных данных (paging) с публичными колодами.
     *
     * @param query Поисковый запрос для фильтрации колод по имени или описанию.
     * @param sortBy Поле, по которому производится сортировка (например, "likes", "trainings").
     * @param category Категория, по которой фильтруются колоды (например, "favourite", "null").
     * @return Поток с постраничными данными [DeckUiModel], соответствующими фильтрам.
     */
    fun getPublicDecks(
        query: String? = null,
        sortBy: String? = null,
        category: String? = null,
    ): Flow<PagingData<DeckUiModel>>

    /**
     * Получает конкретную публичную колоду по её ID.
     *
     * @param id Идентификатор колоды.
     * @return Пара [Deck, Source], где `Source` указывает источник данных (Local/Network).
     */
    suspend fun getDeckById(id: String): Pair<Deck, Source>

    /**
     * Ставит или убирает лайк у колоды, в зависимости от текущего состояния.
     *
     * @param deckId Идентификатор колоды.
     * @param isLiked Флаг: true — если колода уже лайкнута и нужно убрать лайк, false — если нужно поставить лайк.
     * @return Актуальное количество лайков после выполнения действия.
     */
    suspend fun likeOrUnlikeDeck(deckId: String, isLiked: Boolean): Int


    /**
     * Получает список идентификаторов избранных колод пользователя.
     *
     * @return Список ID колод, находящихся в избранном.
     */
    suspend fun getFavouriteDeckIds(): List<String>
}