package com.example.data.reminder.data.repository

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.data.reminder.data.worker.TrainingReminderWorker
import com.example.data.reminder.data.mapper.toDBO
import com.example.data.reminder.data.mapper.toEntity
import com.example.data.reminder.domain.entity.Reminder
import com.example.data.reminder.domain.repository.ReminderScheduler
import com.example.database.TPrepDatabase
import com.example.database.models.Source
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ReminderSchedulerImpl @Inject constructor(
    private val workManager: WorkManager,
    private val database: TPrepDatabase
) : ReminderScheduler {

    override fun scheduleReminder(reminderId: Long, timeMillis: Long) {
        val delay = timeMillis - System.currentTimeMillis()
        if (delay <= 0 ) throw IllegalStateException("Запланировать нужно на будущее")

        val workRequest = OneTimeWorkRequest.Builder(TrainingReminderWorker::class.java)
            .setInputData(workDataOf("reminderId" to reminderId))
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        val uniqueWorkName = "reminder_$reminderId"
        workManager.enqueueUniqueWork(
            uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    override fun cancelReminder(reminderId: Long) {
        val uniqueWorkName = "reminder_$reminderId"
        workManager.cancelUniqueWork(uniqueWorkName)
    }

    override suspend fun getReminder(
        deckId: Long,
        source: Source
    ): Reminder? {
        return database.trainingReminderDao.getReminder(deckId = deckId, source = source)
            ?.toEntity()
    }

    override suspend fun insertReminder(reminder: Reminder) {
        database.trainingReminderDao.insertReminder(reminder.toDBO())
    }

    override suspend fun deleteReminder(
        deckId: Long,
        source: Source
    ) {
        database.trainingReminderDao.deleteReminder(deckId = deckId, source = source)
    }
}