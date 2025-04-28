package com.example.data.profile.domain.entity

import android.net.Uri

/**
 * Модель данных для информации о профиле пользователя.
 *
 * @property profileName Имя пользователя.
 * @property profileEmail Электронная почта пользователя.
 * @property profileImage URI изображения профиля пользователя (может быть null, если нет изображения).
 */
data class ProfileInfo(
    val profileName: String,
    val profileEmail: String,
    val profileImage: Uri?,
)
