package com.example.training.presentation.training_errors

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.NavigationIconType
import com.example.training.domain.entity.TrainingError
import com.example.training.presentation.util.getFormattedTime

@Composable
fun TrainingErrorsScreen(
    trainingSessionId: String,
    onBackClick: () -> Unit
) {
    val viewModel: TrainingErrorsViewModel = hiltViewModel()

    val errorsList by viewModel.errorsList.collectAsStateWithLifecycle()
    val trainingSessionTime by viewModel.trainingSessionTime.collectAsStateWithLifecycle()

    LaunchedEffect(trainingSessionId) {
        viewModel.loadErrorsData(trainingSessionId)
    }

    var currentErrorIndex by remember { mutableIntStateOf(0) }
    val currentError = errorsList.getOrNull(currentErrorIndex)
    var isNavigatingForward by remember { mutableStateOf(true) }

    val screenWidth = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }

    val onNextError = {
        if (currentErrorIndex < errorsList.size - 1) {
            isNavigatingForward = true
            currentErrorIndex++
        }
    }

    val onPreviousError = {
        if (currentErrorIndex > 0) {
            isNavigatingForward = false
            currentErrorIndex--
        }
    }

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = "Ошибки тренировки",
                subtitle = getFormattedTime(trainingSessionTime),
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        if (offset.x > screenWidth / 2) onNextError() else onPreviousError()
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedErrorContent(currentError, isNavigatingForward)
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun AnimatedErrorContent(currentError: TrainingError?, isNavigatingForward: Boolean) {
    AnimatedContent(
        targetState = currentError,
        transitionSpec = {
            if (isNavigatingForward) {
                slideInHorizontally { width -> width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width } + fadeOut()
            } else {
                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> width } + fadeOut()
            }.using(SizeTransform(clip = false))
        },
        label = "Error Animation"
    ) { error ->
        error?.let {
            ErrorItem(it)
        } ?: NoErrorMessage()
    }
}

@Composable
private fun ErrorItem(error: TrainingError) {
    Column(
        modifier = Modifier.padding(horizontal = 28.dp)
    ) {
        Text(text = error.question, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))

        AnswerSection(
            title = "Правильный ответ:",
            answer = error.correctAnswer,
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
        Spacer(modifier = Modifier.height(19.dp))

        val userAnswer = if (error.incorrectAnswer.isBlank()) "Вы не ответили на данный вопрос"
        else error.incorrectAnswer

        AnswerSection(
            title = "Ваш ответ:",
            answer = userAnswer,
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    }
}

@Composable
private fun AnswerSection(title: String, answer: String, containerColor: Color) {
    Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(7.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Text(
            text = answer,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 9.dp)
        )
    }
}

@Composable
private fun NoErrorMessage() {
    Text(
        text = "Нет данных",
        style = MaterialTheme.typography.bodyLarge
    )
}