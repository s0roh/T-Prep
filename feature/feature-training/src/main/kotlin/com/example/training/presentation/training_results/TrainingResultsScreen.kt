package com.example.training.presentation.training_results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.R
import com.example.common.ui.AppButton
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.NavigationIconType
import com.example.database.models.Source
import com.example.training.domain.entity.TrainingError
import com.example.training.presentation.components.AnimatedDonutChart
import com.example.training.presentation.components.ChartSegment
import com.example.training.presentation.util.getCardWordForm
import com.example.training.presentation.util.getFormattedTime

@Composable
fun TrainingResultsScreen(
    trainingSessionId: String,
    cameFromHistoryScreen: Boolean = false,
    onBackClick: () -> Unit,
    onNavigateToDeck: (String, Source) -> Unit = { _, _ -> },
    onErrorsClick: (List<TrainingError>) -> Unit = {},
) {
    val viewModel: TrainingResultsViewModel = hiltViewModel()
    val screenState by viewModel.screenState.collectAsState()

    var deckId: String? by rememberSaveable { mutableStateOf(null) }
    var source: Source? by rememberSaveable { mutableStateOf(null) }

    LaunchedEffect(trainingSessionId) {
        viewModel.loadTrainingData(trainingSessionId)

        if (cameFromHistoryScreen) {
            viewModel.getInfoForNavigationToDeck(trainingSessionId) { result ->
                deckId = result.first
                source = result.second
            }
        }
    }

    when (val currentState = screenState) {
        is TrainingResultsScreenState.Loading -> LoadingScreen()

        is TrainingResultsScreenState.Success -> TrainingResultsContent(
            state = currentState,
            cameFromHistoryScreen = cameFromHistoryScreen,
            deckId = deckId,
            source = source,
            onBackClick = onBackClick,
            onNavigateToDeck = onNavigateToDeck,
            onErrorsClick = onErrorsClick
        )
    }
}

@Composable
private fun TrainingResultsContent(
    state: TrainingResultsScreenState.Success,
    cameFromHistoryScreen: Boolean,
    deckId: String?,
    source: Source?,
    onBackClick: () -> Unit,
    onNavigateToDeck: (String, Source) -> Unit = { _, _ -> },
    onErrorsClick: (List<TrainingError>) -> Unit,
) {
    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = state.deckName,
                subtitle = getFormattedTime(state.trainingSessionTime),
                navigationIconType = NavigationIconType.CLOSE,
                onNavigationClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(26.dp))
            StatisticRow(title = "Всего пройдено", count = state.totalAnswers)
            Spacer(modifier = Modifier.height(12.dp))
            StatisticRow(title = "Правильных", count = state.correctAnswers)
            Spacer(modifier = Modifier.height(60.dp))

            AnimatedDonutChart(
                segments = listOf(
                    ChartSegment(
                        state.correctPercentage,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    ChartSegment(
                        state.incorrectPercentage,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            Spacer(modifier = Modifier.height(45.dp))

            state.nextTrainingTime?.let { NextTrainingReminderInfo(it) }

            if (state.errorsList.isNotEmpty()) {
                AppButton(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Посмотреть ошибки",
                    shouldShowIcon = true,
                    iconResId = R.drawable.ic_loupe,
                    onClick = { onErrorsClick(state.errorsList) }
                )
            }

            if (cameFromHistoryScreen && deckId != null && source != null) {
                AppButton(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Перейти к колоде",
                    onClick = { onNavigateToDeck(deckId, source) }
                )
            }
        }
    }
}

@Composable
private fun NextTrainingReminderInfo(time: Long) {
    Text(
        text = "Следующая тренировка",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
        text = getFormattedTime(time),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 30.dp)
    )
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun StatisticRow(title: String, count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "$count ${getCardWordForm(count)}",
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            fontWeight = FontWeight.Bold
        )
    }
}
