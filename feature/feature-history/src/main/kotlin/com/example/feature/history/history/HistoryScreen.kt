package com.example.feature.history.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.CenteredTopAppBar
import com.example.feature.history.R
import com.example.feature.history.util.getFormattedDateForItem
import com.example.feature.history.util.groupHistoryByDate
import com.example.history.domain.entity.TrainingHistoryItem

@Composable
fun HistoryScreen(
    paddingValues: PaddingValues,
    onHistoryClick: (trainingSessionId: String) -> Unit,
) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val trainingHistory by viewModel.historyGroups.collectAsState()

    LaunchedEffect(trainingHistory) {
        viewModel.loadHistory()
    }

    Scaffold(
        topBar = { CenteredTopAppBar(title = stringResource(R.string.history)) }
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
            top = innerPadding.calculateTopPadding(),
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
            bottom = paddingValues.calculateBottomPadding(),
        )

        if (trainingHistory.isEmpty()) {
            EmptyHistoryMessage(modifier = Modifier.padding(combinedPadding))
        } else {
            val groupedHistory = groupHistoryByDate(trainingHistory)

            HistoryList(
                groupedHistory = groupedHistory,
                onHistoryClick = onHistoryClick,
                modifier = Modifier.padding(combinedPadding)
            )
        }
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
            text = stringResource(R.string.you_dont_have_any_training_history_yet),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HistoryList(
    groupedHistory: Map<String, List<TrainingHistoryItem>>,
    onHistoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical =  16.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        groupedHistory.forEach { (header, items) ->
            item(key = header) {
                TimePeriodHeader(header = header, modifier = Modifier.animateItem())
            }
            itemsIndexed(items) { index, trainingHistory ->
                TrainingHistoryCard(
                    modifier = Modifier.animateItem(),
                    trainingHistoryItem = trainingHistory,
                    onHistoryClick = onHistoryClick
                )
            }
        }
    }
}

@Composable
private fun TimePeriodHeader(header: String, modifier: Modifier = Modifier) {
    Text(
        text = header,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun TrainingHistoryCard(
    modifier: Modifier = Modifier,
    trainingHistoryItem: TrainingHistoryItem,
    onHistoryClick: (String) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                onHistoryClick(
                    trainingHistoryItem.trainingHistories.first().trainingSessionId
                )
            }
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
                    text = trainingHistoryItem.trainingHistories.first().deckName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = getFormattedDateForItem(trainingHistoryItem.timestamp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${trainingHistoryItem.percentOfCorrectAnswers}%",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = if (trainingHistoryItem.percentOfCorrectAnswers < 50) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                ),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}