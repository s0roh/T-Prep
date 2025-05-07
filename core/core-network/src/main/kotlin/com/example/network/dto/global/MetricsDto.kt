package com.example.network.dto.global

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO, содержащий пользовательские метрики активности.
 *
 * @property favouriteFilterButton Количество кликов по кнопке фильтра "Избранное".
 * @property favouriteProfileButton Количество кликов по кнопке "Открыть избранные колоды".
 * @property lastInAppTime Суммарное время, проведённое в приложении за последнюю сессию (в секундах).
 * @property sumTrainingsTime Суммарное время, проведённое в тренировках за последнюю сессию (в секундах).
 * @property trainingsCount Количество тренировок за последнюю сессию.
 */
@Serializable
data class MetricsDto(
    @SerialName("favourite_filter_button") val favouriteFilterButton: Int,
    @SerialName("favourite_profile_button") val favouriteProfileButton: Int,
    @SerialName("last_in_app_time") val lastInAppTime: Int,
    @SerialName("sum_trainings_time") val sumTrainingsTime: Int,
    @SerialName("trainings_count") val trainingsCount: Int,
)
