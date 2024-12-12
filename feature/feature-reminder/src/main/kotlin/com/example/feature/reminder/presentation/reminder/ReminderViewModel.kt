package com.example.feature.reminder.presentation.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.models.Source
import com.example.data.reminder.domain.entity.Reminder
import com.example.feature.reminder.domain.CancelReminderUseCase
import com.example.feature.reminder.domain.DeleteReminderUseCase
import com.example.feature.reminder.domain.GetReminderUseCase
import com.example.feature.reminder.domain.InsertReminderUseCase
import com.example.feature.reminder.domain.ScheduleReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    private val cancelReminderUseCase: CancelReminderUseCase,
    private val getReminderUseCase: GetReminderUseCase,
    private val insertReminderUseCase: InsertReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase
) : ViewModel() {

    fun scheduleReminder(reminderId: Long, timeMillis: Long) {
        scheduleReminderUseCase(reminderId, timeMillis)
    }

    fun cancelReminder(reminderId: Long) {
        cancelReminderUseCase(reminderId)
    }

    suspend fun getReminder(deckId: String, source: Source): Reminder? {
        return getReminderUseCase(deckId = deckId, source = source)
    }

    suspend fun insertReminder(reminder: Reminder): Long {
        return insertReminderUseCase(reminder = reminder)
    }

    fun deleteReminder(deckId: String, source: Source) {
        viewModelScope.launch {
            deleteReminderUseCase(deckId = deckId, source = source)
        }
    }
}