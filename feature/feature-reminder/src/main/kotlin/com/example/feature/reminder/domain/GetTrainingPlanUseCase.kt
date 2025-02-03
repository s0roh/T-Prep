package com.example.feature.reminder.domain

import com.example.data.reminder.domain.repository.ReminderScheduler
import javax.inject.Inject

class GetTrainingPlanUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler,
) {

    suspend operator fun invoke(
        startDate: Int,
        finishDate: Int,
        preferredTime: Int,
    ): List<Long> {
        return reminderScheduler.getTrainingPlan(
            startDate = startDate,
            finishDate = finishDate,
            preferredTime = preferredTime
        )
    }
}