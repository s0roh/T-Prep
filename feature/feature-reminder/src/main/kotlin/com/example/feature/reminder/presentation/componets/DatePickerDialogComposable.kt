package com.example.feature.reminder.presentation.componets

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DatePickerDialogComposable(
    onDateSelected: (Long) -> Unit,
    onDismissRequest: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis!!)
                },
                enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Отмена") }
        }
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}