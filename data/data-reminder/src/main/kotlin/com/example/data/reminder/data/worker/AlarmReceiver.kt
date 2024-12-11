package com.example.data.reminder.data.worker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.data.reminder.R
import com.example.data.reminder.data.util.RouteNavigator
import com.example.data.reminder.data.util.getEntryPoint
import com.example.database.models.TrainingReminderDBO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> restoreReminders(context)
            else -> handleReminderNotification(context, intent)
        }
    }
}

private fun restoreReminders(context: Context) {
    val entryPoint = getEntryPoint(context)
    val database = entryPoint.getTPrepDatabase()

    CoroutineScope(Dispatchers.IO).launch {
        val reminders = database.trainingReminderDao.getAllReminders()
        reminders.forEach { reminder ->
            val reminderScheduler = getEntryPoint(context).getReminderScheduler()
            reminderScheduler.scheduleReminder(reminder.id, reminder.reminderTime)
        }
    }
}

private fun handleReminderNotification(context: Context, intent: Intent) {
    val reminderId = intent.getLongExtra("reminderId", -1L)
    if (reminderId == -1L) return

    val entryPoint = getEntryPoint(context)
    val database = entryPoint.getTPrepDatabase()
    val routeNavigator = entryPoint.getRouteNavigator()

    CoroutineScope(Dispatchers.IO).launch {
        val reminder = database.trainingReminderDao.getReminderById(reminderId)
        if (reminder != null) {
            sendNotification(context, routeNavigator, reminder)
            database.trainingReminderDao.deleteReminder(reminder.deckId, reminder.source)
        }
    }
}

private fun sendNotification(
    context: Context,
    routeNavigator: RouteNavigator,
    reminder: TrainingReminderDBO
) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channelId = "training_channel"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel(notificationManager, channelId)
    }

    val route = routeNavigator.getDeckDetailsRoute(reminder.deckId, reminder.source)
    val intent = routeNavigator.createDeckIntent(route)

    val pendingIntent = PendingIntent.getActivity(
        context,
        reminder.deckId.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, "training_channel")
        .setContentTitle("Колода: ${reminder.name}")
        .setContentText("Пришло время для тренировки!")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(reminder.id.toInt(), notification)

    CoroutineScope(Dispatchers.IO).launch {
        val entryPoint =getEntryPoint(context)
        val database = entryPoint.getTPrepDatabase()

        database.trainingReminderDao.deleteReminder(
            deckId = reminder.deckId,
            source = reminder.source
        )
    }
}

@SuppressLint("NewApi")
private fun createNotificationChannel(
    notificationManager: NotificationManager,
    channelId: String
) {
    if (notificationManager.getNotificationChannel(channelId) == null) {
        val channel = NotificationChannel(
            channelId,
            "Training Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for training reminders"
        }
        notificationManager.createNotificationChannel(channel)
    }
}