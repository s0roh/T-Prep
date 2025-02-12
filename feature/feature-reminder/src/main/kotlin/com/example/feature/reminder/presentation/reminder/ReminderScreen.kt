package com.example.feature.reminder.presentation.reminder

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.AppButton
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.NavigationIconType
import com.example.data.reminder.domain.entity.Reminder
import com.example.database.models.Source
import com.example.feature.reminder.R
import com.example.feature.reminder.presentation.util.formatReminderTime

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ReminderScreen(
    deckId: String,
    deckName: String,
    source: Source,
    onAddClick: (String, String, Source, Boolean) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: ReminderViewModel = hiltViewModel()

    val reminders by viewModel.reminders.collectAsState()

    LaunchedEffect(deckId, source) {
        viewModel.loadReminders(deckId, source)
    }

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = stringResource(R.string.training_plan),
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = onBackClick
            )
        }
    ) { paddingValues ->

        Crossfade(targetState = reminders.isNotEmpty()) { isNotEmpty ->
            if (isNotEmpty) {
                ReminderList(
                    reminders = reminders,
                    modifier = Modifier.padding(paddingValues),
                    onAddClick = { onAddClick(deckId, deckName, source, false) },
                    onDeleteRemindClick = { reminderId, deckId, source, reminderTime ->
                        viewModel.cancelReminder(reminderId = reminderId)
                        viewModel.deleteReminder(
                            deckId = deckId,
                            source = source,
                            reminderTime = reminderTime
                        )
                    }
                )
            } else {
                ReminderCreationMethod(
                    modifier = Modifier.padding(paddingValues),
                    onAutoClick = { onAddClick(deckId, deckName, source, true) },
                    onManualClick = { onAddClick(deckId, deckName, source, false) }
                )
            }
        }
    }
}

@Composable
private fun ReminderCreationMethod(
    onAutoClick: () -> Unit,
    onManualClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        SelectionWithDescription(
            title = stringResource(R.string.auto_title),
            description = stringResource(R.string.auto_description),
            onClick = onAutoClick
        )
        SelectionWithDescription(
            title = stringResource(R.string.manual_title),
            description = stringResource(R.string.manual_description),
            onClick = onManualClick
        )
    }

}

@Composable
private fun SelectionWithDescription(title: String, description: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 30.dp, vertical = 12.dp)

    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = 10.dp)
                .fillMaxWidth()

        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow),
                contentDescription = "Выбор метода"
            )
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}


@Composable
private fun ReminderList(
    reminders: List<Reminder>,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
    onDeleteRemindClick: (Long, String, Source, Long) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            items(items = reminders, key = { it.id }) { reminder ->
                ReminderItem(
                    reminderTime = reminder.reminderTime,
                    modifier = Modifier.animateItem(),
                    onDeleteClick = {
                        onDeleteRemindClick(
                            reminder.id,
                            reminder.deckId,
                            reminder.source,
                            reminder.reminderTime
                        )
                    }
                )
            }
            item(key = "footer") {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .animateItem(),
                    text = stringResource(R.string.reminder_list_description),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }

        AppButton(
            title = stringResource(R.string.add_reminder),
            shouldShowIcon = true,
            iconResId = R.drawable.ic_plus,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(18.dp),
            onClick = onAddClick
        )
    }
}


@Composable
private fun ReminderItem(
    reminderTime: Long,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatReminderTime(reminderTime),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )

        IconButton(onClick = { onDeleteClick() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_trash),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "Удалить"
            )
        }
    }
}