package com.example.feature.reminder.presentation.reminder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.database.models.Source
import com.example.feature.reminder.presentation.componets.DatePickerDialogComposable
import com.example.feature.reminder.presentation.componets.TimePickerDialogComposable
import com.example.feature.reminder.presentation.util.combineDateAndTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    deckId: String,
    deckName: String,
    source: Source,
    onBackClick: () -> Unit,
) {
    val viewModel: ReminderViewModel = hiltViewModel()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf<Long?>(null) }

    val openDatePicker = remember { mutableStateOf(false) }
    val openTimePicker = remember { mutableStateOf(false) }

    val dateFormatter = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }
    val timeFormatter = remember { DateFormat.getTimeInstance(DateFormat.SHORT) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(
                    context, "Разрешение на уведомления не предоставлено.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    val hasNotificationPermission = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DisplaySelectedDateTime(
                selectedDate = selectedDate,
                selectedTime = selectedTime,
                dateFormatter = dateFormatter,
                timeFormatter = timeFormatter
            )
            ReminderButtons(
                openDatePicker = { openDatePicker.value = true },
                openTimePicker = { openTimePicker.value = true },
                onScheduleClick = {
                    handleScheduleReminder(
                        deckId,
                        deckName,
                        source,
                        selectedDate,
                        selectedTime,
                        context,
                        viewModel,
                        coroutineScope,
                        hasNotificationPermission,
                        launcher
                    )
                },
                onCancelClick = {
                    coroutineScope.launch {
                        viewModel.getReminder(deckId = deckId, source = source)?.also {
                            viewModel.cancelReminder(it.id)
                        }
                        viewModel.deleteReminder(deckId, source)
                    }
                    showToast(context = context, message = "Напоминание отменено")
                },
                onBackClick = onBackClick
            )
        }

    }

    if (openDatePicker.value) {
        DatePickerDialogComposable(
            onDateSelected = { selectedDateMillis ->
                selectedDate = selectedDateMillis
                openDatePicker.value = false
            },
            onDismissRequest = { openDatePicker.value = false }
        )
    }

    if (openTimePicker.value) {
        TimePickerDialogComposable(
            onTimeSelected = { selectedTimeMillis ->
                selectedTime = selectedTimeMillis
                openTimePicker.value = false
            },
            onDismissRequest = { openTimePicker.value = false }
        )
    }
}

@Composable
private fun DisplaySelectedDateTime(
    selectedDate: Long?,
    selectedTime: Long?,
    dateFormatter: DateFormat,
    timeFormatter: DateFormat,
) {
    Text(
        text = buildString {
            if (selectedDate != null) {
                append(dateFormatter.format(Date(selectedDate)))
            } else {
                append("Дата не выбрана")
            }
            append(" ")
            if (selectedTime != null) {
                append(timeFormatter.format(Date(selectedTime)))
            } else {
                append("Время не выбрано")
            }
        },
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
private fun ReminderButtons(
    openDatePicker: () -> Unit,
    openTimePicker: () -> Unit,
    onScheduleClick: () -> Unit,
    onCancelClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Column {
        Button(modifier = Modifier.fillMaxWidth(), onClick = openDatePicker) {
            Text("Выбрать дату")
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = openTimePicker) {
            Text("Выбрать время")
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = onScheduleClick) {
            Text("Запланировать тренировку")
        }
        Button(modifier = Modifier.fillMaxWidth(), onClick = onCancelClick) {
            Text("Отменить тренировку")
        }
        Button(onClick = onBackClick, modifier = Modifier.align(Alignment.End)) {
            Text("Назад")
        }
    }
}

private fun handleScheduleReminder(
    deckId: String,
    deckName: String,
    source: Source,
    selectedDate: Long?,
    selectedTime: Long?,
    context: Context,
    viewModel: ReminderViewModel,
    coroutineScope: CoroutineScope,
    hasNotificationPermission: Boolean,
    launcher: ActivityResultLauncher<String>,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    val dateTimeInMillis = combineDateAndTime(selectedDate, selectedTime)
    if (dateTimeInMillis == null) {
        showToast(context, "Пожалуйста, выберите дату и время.")
        return
    }

    coroutineScope.launch {
        try {
            val reminder = viewModel.createReminderIfValid(
                deckId = deckId,
                source = source,
                reminderTime = dateTimeInMillis,
                deckName = deckName
            )
            if (reminder == null) {
                showToast(context, "Напоминание с таким временем уже установлено.")
                return@launch
            }

            val reminderId = viewModel.insertReminder(reminder)
            viewModel.scheduleReminder(reminderId, dateTimeInMillis)
            showToast(context, "Тренировка успешно запланирована!")
        } catch (e: Exception) {
            showToast(context, "Ошибка при планировании тренировки: ${e.message}")
        }
    }
}

private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}