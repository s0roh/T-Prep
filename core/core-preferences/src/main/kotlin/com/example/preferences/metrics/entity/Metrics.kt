package com.example.preferences.metrics.entity

/**
 * Представляет собой набор пользовательских метрик.
 *
 * @property favouriteFilterButton Количество кликов по кнопке фильтра "Избранное".
 * @property favouriteProfileButton Количество кликов по кнопке "Открыть избранные колоды".
 * @property lastInAppTime Суммарное время, проведённое в приложении за последнюю сессию (в секундах).
 * @property sumTrainingsTime Суммарное время, проведённое в тренировках за последнюю сессию (в секундах).
 * @property trainingsCount Количество тренировок за последнюю сессию.
 */
data class Metrics(
    val favouriteFilterButton: Int,
    val favouriteProfileButton: Int,
    val lastInAppTime: Int,
    val sumTrainingsTime: Int,
    val trainingsCount: Int
)
