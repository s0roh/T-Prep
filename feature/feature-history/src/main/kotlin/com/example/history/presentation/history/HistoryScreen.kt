package com.example.history.presentation.history

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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.database.models.Source
import com.example.history.domain.entity.HistoryWithTimePeriod
import com.example.history.domain.entity.TimePeriod
import com.example.history.domain.entity.TrainingHistory
import com.example.history.util.toLocalizedString

@Composable
fun HistoryScreen(
    paddingValues: PaddingValues,
    onHistoryClick: (deckId: String, source: Source) -> Unit
) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val historyGroups by viewModel.historyGroups.collectAsState()

    LaunchedEffect(historyGroups) {
        viewModel.loadHistory()
    }

    if (historyGroups.isEmpty()) {
        EmptyHistoryMessage(modifier = Modifier.padding(paddingValues))
    } else {
        HistoryList(
            historyWithTimePeriods = historyGroups,
            onHistoryClick = onHistoryClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun EmptyHistoryMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "У вас пока нет истории тренировок",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HistoryList(
    historyWithTimePeriods: List<HistoryWithTimePeriod>,
    onHistoryClick: (String, Source) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        historyWithTimePeriods.forEach { group ->
            item {
                TimePeriodHeader(group.timePeriod)
            }
            items(items = group.trainingHistories) { trainingHistory ->
                DeckHistoryItem(
                    trainingHistory = trainingHistory,
                    onHistoryClick = onHistoryClick
                )
            }
        }
    }
}

@Composable
private fun TimePeriodHeader(timePeriod: TimePeriod) {
    Text(
        text = timePeriod.toLocalizedString(),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
private fun DeckHistoryItem(
    trainingHistory: TrainingHistory,
    onHistoryClick: (String, Source) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onHistoryClick(trainingHistory.deckId, trainingHistory.source) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trainingHistory.deckName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Количество карточек: ${trainingHistory.cardsCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}