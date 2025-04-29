package com.example.feature.reminder.presentation.add_reminder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.AppButton
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.NavigationIconType
import com.example.database.models.Source
import com.example.feature.reminder.R
import com.example.feature.reminder.presentation.componets.DatePickerDialogComposable
import com.example.feature.reminder.presentation.componets.TimePickerDialogComposable
import com.example.feature.reminder.presentation.util.showToast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

@Composable
fun AddReminderScreen(
    deckId: String,
    deckName: String,
    source: Source,
    isAutoGeneration: Boolean,
    onBackClick: () -> Unit,
) {
    val viewModel: AddReminderViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                showToast(context, context.getString(R.string.no_permission_to_notify))
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

    var startDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var endDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var preferredTime by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedTime by rememberSaveable { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = if (isAutoGeneration) stringResource(R.string.auto) else stringResource(R.string.manual),
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isAutoGeneration) {
                AutoGeneration(
                    startDate, endDate, preferredTime,
                    onDateSelected = { startDate = it },
                    onEndDateSelected = { endDate = it },
                    onPreferredTimeSelected = { preferredTime = it }
                )
            } else {
                ManualGeneration(
                    selectedDate, selectedTime,
                    onDateSelected = { selectedDate = it },
                    onTimeSelected = { selectedTime = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            AppButton(
                title = if (isAutoGeneration) stringResource(R.string.create_plan)
                else stringResource(R.string.add_train),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                onClick = {
                    handleScheduleReminder(
                        deckId, deckName, source,
                        startDate, endDate, preferredTime,
                        selectedDate, selectedTime,
                        context, viewModel, coroutineScope,
                        hasNotificationPermission, launcher, isAutoGeneration,
                        onBackClick
                    )
                }
            )
        }
    }
}

@Composable
private fun ManualGeneration(
    selectedDate: Long?,
    selectedTime: Long?,
    onDateSelected: (Long) -> Unit,
    onTimeSelected: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        DateTimeSelectionRow(stringResource(R.string.date), selectedDate, onDateSelected)
        DateTimeSelectionRow(stringResource(R.string.time), selectedTime, onTimeSelected)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AutoGeneration(
    startDate: Long?,
    endDate: Long?,
    preferredTime: Long?,
    onDateSelected: (Long) -> Unit,
    onEndDateSelected: (Long) -> Unit,
    onPreferredTimeSelected: (Long) -> Unit,
) {
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val imageRes = if (isSystemInDarkTheme()) {
        R.drawable.auto_planing_dark
    } else {
        R.drawable.auto_planing_light
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            DateTimeSelectionRow(
                stringResource(R.string.beginning_of_training),
                startDate,
                onDateSelected
            )
            DateTimeSelectionRow(
                stringResource(R.string.end_of_training),
                endDate,
                onEndDateSelected
            )
            DateTimeSelectionRow(
                stringResource(R.string.preferred_time),
                preferredTime,
                onPreferredTimeSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(MaterialTheme.shapes.large)
                    .clickable { isBottomSheetOpen = true }
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = stringResource(R.string.how_it_works),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(20.dp)
                )
                Text(
                    text = stringResource(R.string.how_it_works),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 16.dp)
                )

            }
        }
    }

    if (isBottomSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isBottomSheetOpen = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 8.dp,
            dragHandle = {}
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = stringResource(id = R.string.auto_generation_image_description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 35.dp)
                )
                Text(
                    text = stringResource(id = R.string.auto_generation_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(horizontal = 28.dp)
                )
                Spacer(modifier = Modifier.height(11.dp))
                Text(
                    text = stringResource(id = R.string.auto_generation_description),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.padding(horizontal = 28.dp)

                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DateTimeSelectionRow(
    label: String,
    selectedTimeMillis: Long?,
    onTimeSelected: (Long) -> Unit,
) {
    val dateFormatter = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }
    val timeFormatter = remember { DateFormat.getTimeInstance(DateFormat.SHORT) }
    val openPicker = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        )
        Text(
            text = selectedTimeMillis?.let {
                if (label.contains(
                        stringResource(R.string.time),
                        ignoreCase = true
                    )
                ) timeFormatter.format(Date(it))
                else dateFormatter.format(Date(it))
            } ?: stringResource(R.string.select),
            modifier = Modifier.clickable { openPicker.value = true },
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        )
    }

    if (openPicker.value) {
        if (label.contains(stringResource(R.string.time), ignoreCase = true)) {
            TimePickerDialogComposable(
                onTimeSelected = {
                    onTimeSelected(it)
                    openPicker.value = false
                },
                onDismissRequest = { openPicker.value = false }
            )
        } else {
            DatePickerDialogComposable(
                onDateSelected = {
                    onTimeSelected(it)
                    openPicker.value = false
                },
                onDismissRequest = { openPicker.value = false }
            )
        }
    }
}

private fun handleScheduleReminder(
    deckId: String,
    deckName: String,
    source: Source,
    startDate: Long?,
    endDate: Long?,
    preferredTime: Long?,
    selectedDate: Long?,
    selectedTime: Long?,
    context: Context,
    viewModel: AddReminderViewModel,
    coroutineScope: CoroutineScope,
    hasNotificationPermission: Boolean,
    launcher: ActivityResultLauncher<String>,
    isAutoGeneration: Boolean,
    onBackClick: () -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    val exceptionHandler = CoroutineExceptionHandler { _, e ->
        showToast(context, "Ошибка: ${e.message}")
    }

    coroutineScope.launch(exceptionHandler) {
        val message = if (isAutoGeneration) {
            viewModel.createAutoReminders(
                startDate,
                endDate,
                preferredTime,
                deckId,
                deckName,
                source
            )
        } else {
            viewModel.createManualReminder(selectedDate, selectedTime, deckId, deckName, source)
        }

        message?.let { showToast(context, it) }

        if (message == null && isAutoGeneration) {
            onBackClick()
        }
    }
}