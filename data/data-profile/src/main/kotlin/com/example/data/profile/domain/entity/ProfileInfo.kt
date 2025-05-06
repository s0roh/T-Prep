package com.example.data.profile.domain.entity

/**
 * Модель данных для информации о профиле пользователя.
 *
 * @property profileName Имя пользователя.
 * @property profileEmail Электронная почта пользователя.
 */
data class ProfileInfo(
    val profileName: String,
    val profileEmail: String,
)
