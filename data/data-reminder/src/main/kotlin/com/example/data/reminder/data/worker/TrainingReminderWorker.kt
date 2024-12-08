package com.example.data.reminder.data.worker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.database.TPrepDatabase
import com.example.database.models.TrainingReminderDBO
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.data.reminder.R
import com.example.data.reminder.data.util.RouteNavigator

@HiltWorker
class TrainingReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: TPrepDatabase,
    private val routeNavigator: RouteNavigator
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val reminderId = inputData.getLong("reminderId", -1)

            if (reminderId == -1L) return Result.failure()

            val trainingReminder = getReminderFromDatabase(reminderId) ?:  return Result.failure()

            sendNotification(trainingReminder)

            return Result.success()
        } catch (e: Exception) {
            Log.e("!@#", "Error in doWork", e)
            return Result.failure()
        }
    }

    private suspend fun getReminderFromDatabase(reminderId: Long): TrainingReminderDBO? {
        return withContext(Dispatchers.IO) {
            database.trainingReminderDao.getReminderById(reminderId)
        }
    }

    @SuppressLint("NotificationPermission")
    private fun sendNotification(reminder: TrainingReminderDBO) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "training_channel"
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel == null) {
                val channelName = "Training Notifications"
                val channelDescription = "Notifications for training reminders"
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = channelDescription
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        val route = routeNavigator.getDeckDetailsRoute(reminder.deckId, reminder.source)
        val intent = routeNavigator.createDeckIntent(route)

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            reminder.deckId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "training_channel")
            .setContentTitle("Колода: ${reminder.name}")
            .setContentText("Пришло время для тренировки!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(reminder.id.toInt(), notification)
    }
}