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
import javax.inject.Inject

class ReminderSchedulerImpl @Inject constructor(
    private val context: Context,
    private val database: TPrepDatabase
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

    override suspend fun getReminder(
        deckId: Long,
        source: Source
    ): Reminder? {
        return database.trainingReminderDao.getReminder(deckId = deckId, source = source)
            ?.toEntity()
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
       return database.trainingReminderDao.insertReminder(reminder.toDBO())
    }

    override suspend fun deleteReminder(
        deckId: Long,
        source: Source
    ) {
        database.trainingReminderDao.deleteReminder(deckId = deckId, source = source)
    }
}