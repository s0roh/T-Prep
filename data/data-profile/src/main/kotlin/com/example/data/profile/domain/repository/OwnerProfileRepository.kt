package com.example.data.profile.domain.repository

import com.example.data.profile.domain.entity.OwnerProfileInfo

interface OwnerProfileRepository {

    /**
     * Загружает информацию о владельце по указанному идентификатору.
     *
     * @param ownerId Уникальный идентификатор владельца.
     * @return [OwnerProfileInfo] данные профиля владельца.
     * @throws Exception в случае ошибки загрузки данных.
     */
    suspend fun loadOwnerProfileInfo(ownerId: String): OwnerProfileInfo
}