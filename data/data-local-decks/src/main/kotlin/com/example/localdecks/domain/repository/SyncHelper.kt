package com.example.localdecks.domain.repository

import com.example.database.models.EntityType

/**
 * Вспомогательный интерфейс для пометки сущностей (колод и карточек) в процессе синхронизации.
 * Используется для отслеживания новых, обновлённых и удалённых объектов.
 */
interface SyncHelper {

    /**
     * Помечает сущность как новую для последующей синхронизации.
     *
     * @param deckId Идентификатор колоды, к которой относится сущность.
     * @param entityType Тип сущности [EntityType] (колода или карточка).
     * @param cardId Идентификатор карточки (если применимо), иначе null.
     */
    suspend fun markAsNew(deckId: String, entityType: EntityType, cardId: Int? = null)

    /**
     * Помечает сущность как обновлённую для последующей синхронизации.
     *
     * @param deckId Идентификатор колоды, к которой относится сущность.
     * @param entityType Тип сущности [EntityType] (колода или карточка).
     * @param cardId Идентификатор карточки (если применимо), иначе null.
     */
    suspend fun markAsUpdated(deckId: String, entityType: EntityType, cardId: Int? = null)

    /**
     * Помечает сущность как удалённую для последующей синхронизации.
     *
     * @param deckId Идентификатор колоды, к которой относится сущность.
     * @param entityType Тип сущности [EntityType] (колода или карточка).
     * @param cardId Идентификатор карточки (если применимо), иначе null.
     */
    suspend fun markAsDeleted(deckId: String, entityType: EntityType, cardId: Int? = null)
}