package com.example.data.profile.domain.entity

import android.net.Uri
import com.example.common.ui.entity.DeckUiModel

/**
 * Модель данных для отображения профиля другого пользователя (владельца колод).
 *
 * @property ownerProfileName Имя владельца профиля.
 * @property ownerProfileImage URI изображения профиля владельца (может быть null, если нет изображения).
 * @property ownerPublicDecks Список публичных колод владельца.
 * @property ownerTotalTrainings Общее количество проведённых тренировок владельцем.
 * @property ownerMediumPercentage Средний процент правильных ответов владельца по всем тренировкам.
 */
data class OwnerProfileInfo(
    val ownerProfileName: String,
    val ownerProfileImage: Uri?,
    val ownerPublicDecks: List<DeckUiModel>,
    val ownerTotalTrainings: Int,
    val ownerMediumPercentage: Int,
)
