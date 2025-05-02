package com.example.data.reminder.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.data.reminder.data.mapper.toDBO
import com.example.data.reminder.data.mapper.toEntity
import com.example.data.reminder.data.worker.AlarmReceiver
import com.example.data.reminder.domain.entity.Reminder
import com.example.data.reminder.domain.repository.ReminderScheduler
import com.example.database.TPrepDatabase
import com.example.database.models.Source
import com.example.network.api.ApiService
import com.example.preferences.auth.util.AuthRequestWrapper
import javax.inject.Inject

class ReminderSchedulerImpl @Inject constructor(
    private val context: Context,
    private val database: TPrepDatabase,
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
) : ReminderScheduler {

    override fun scheduleReminder(reminderId: Long, timeMillis: Long) {
        //TODO val delay = max(timeMillis - System.currentTimeMillis(), 0)
        //if (delay <= 0) throw IllegalStateException("Запланировать нужно на будущее")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(reminderId)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeMillis,
            pendingIntent
        )
    }

    private fun createPendingIntent(reminderId: Long): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("reminderId", reminderId)
        }
        return PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }


    override fun cancelReminder(reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(reminderId)
        alarmManager.cancel(pendingIntent)
    }

    override suspend fun getRemindersForDeck(
        deckId: String,
        source: Source,
    ): List<Reminder> {
        return database.trainingReminderDao.getRemindersForDeck(deckId = deckId, source = source)
            .map { it.toEntity() }
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
        return database.trainingReminderDao.insertReminder(reminder.toDBO())
    }

    override suspend fun deleteReminder(
        deckId: String,
        source: Source,
        reminderTime: Long,
    ) {
        database.trainingReminderDao.deleteReminder(
            deckId = deckId,
            source = source,
            reminderTime = reminderTime
        )
    }

    override suspend fun getTrainingPlan(
        startDate: Int,
        finishDate: Int,
        preferredTime: Int,
    ): List<Long> {
        return authRequestWrapper.executeWithAuth { token ->
            val response = apiService.getTrainingPlan(
                startDate = startDate,
                finishDate = finishDate,
                preferredTime = preferredTime,
                authHeader = token
            )
            response.remindersTimeInSeconds.map { it.toLong() * MILLISECONDS_IN_SECOND }
        }
    }

    companion object {
        private const val MILLISECONDS_IN_SECOND = 1000L
    }
}