package com.example.feature.reminder.presentation.componets

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimePickerDialogComposable(
    onTimeSelected: (Long) -> Unit,
    onDismissRequest: () -> Unit
) {
    val state = rememberTimePickerState()
    val showPicker = remember { mutableStateOf(true) }
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current

    Box {
        TimePickerDialog(
            title = if (showPicker.value) {
                "Select Time"
            } else {
                "Enter Time"
            },
            onCancel = onDismissRequest,
            onConfirm = {
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, state.hour)
                    set(Calendar.MINUTE, state.minute)
                }
                snackScope.launch {
                    snackState.showSnackbar("Entered time: ${formatter.format(cal.time)}")
                }
                onTimeSelected(cal.timeInMillis)
            },
            toggle = {
                if (configuration.screenHeightDp > 400) {
                    IconButton(onClick = { showPicker.value = !showPicker.value }) {
                        val icon =
                            if (showPicker.value) {
                                Icons.Outlined.Keyboard
                            } else {
                                Icons.Outlined.Schedule
                            }
                        Icon(
                            icon,
                            contentDescription =
                            if (showPicker.value) {
                                "Switch to Text Input"
                            } else {
                                "Switch to Touch Input"
                            }
                        )
                    }
                }
            }
        ) {
            if (showPicker.value && configuration.screenHeightDp > 400) {
                TimePicker(state = state)
            } else {
                TimeInput(state = state)
            }
        }
    }
}