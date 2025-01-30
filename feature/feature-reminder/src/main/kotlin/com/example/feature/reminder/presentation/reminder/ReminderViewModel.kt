package com.example.feature.reminder.presentation.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.reminder.domain.entity.Reminder
import com.example.database.models.Source
import com.example.feature.reminder.domain.CancelReminderUseCase
import com.example.feature.reminder.domain.DeleteReminderUseCase
import com.example.feature.reminder.domain.GetRemindersForDeckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ReminderViewModel @Inject constructor(
    private val cancelReminderUseCase: CancelReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val getRemindersForDeckUseCase: GetRemindersForDeckUseCase,
) : ViewModel() {

    var reminders = MutableStateFlow<List<Reminder>>(emptyList())
        private set

    fun cancelReminder(reminderId: Long) {
        cancelReminderUseCase(reminderId)
    }
    fun loadReminders(deckId: String, source: Source) {
        viewModelScope.launch {
            reminders.value = getRemindersForDeckUseCase(deckId = deckId, source = source)
        }
    }

    fun deleteReminder(deckId: String, source: Source, reminderTime: Long) {
        viewModelScope.launch {
            deleteReminderUseCase(deckId = deckId, source = source, reminderTime = reminderTime)
            loadReminders(deckId, source)
        }
    }
}