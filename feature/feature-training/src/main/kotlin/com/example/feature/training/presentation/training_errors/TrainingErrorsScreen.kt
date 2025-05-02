package com.example.feature.training.presentation.training_errors

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.NavigationIconType
import com.example.common.util.getFormattedTime
import com.example.database.models.TrainingMode
import com.example.feature.training.R
import com.example.training.domain.entity.TrainingError

@Composable
fun TrainingErrorsScreen(
    trainingSessionId: String,
    onBackClick: () -> Unit,
) {
    val viewModel: TrainingErrorsViewModel = hiltViewModel()

    val errorsList by viewModel.errorsList.collectAsState()
    val errorPictures by viewModel.errorPictures.collectAsState()
    val pictureErrors by viewModel.pictureErrors.collectAsState()
    val trainingSessionTime by viewModel.trainingSessionTime.collectAsState()

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
                title = stringResource(R.string.training_mistakes),
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
                AnimatedErrorContent(
                    currentError,
                    isNavigatingForward,
                    errorPictures,
                    pictureErrors
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .align(Alignment.BottomCenter),
            ) {
                AnimatedVisibility(
                    visible = currentErrorIndex > 0,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.previous),
                        modifier = Modifier.padding(start = 32.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(
                    visible = currentErrorIndex < errorsList.size - 1,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(R.string.next),
                        modifier = Modifier.padding(end = 32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedErrorContent(
    currentError: TrainingError?,
    isNavigatingForward: Boolean,
    errorPictures: Map<Int, Uri?>,
    pictureErrors: Map<Int, String>,
) {
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
        label = stringResource(R.string.error_animation)
    ) { error ->
        error?.let {
            val pictureUri = errorPictures[it.cardId]
            val pictureErrorMsg = pictureErrors[error.cardId]
            ErrorItem(it, pictureUri, pictureErrorMsg)
        } ?: NoErrorMessage()
    }
}

@Composable
private fun ErrorItem(error: TrainingError, pictureUri: Uri?, pictureErrorMessage: String? = null) {
    Column {
        when {
            pictureErrorMessage != null -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pictureErrorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            pictureUri == null && !error.attachment.isNullOrBlank() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(216.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            pictureUri != null -> {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(pictureUri)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .build(),
                    contentDescription = stringResource(R.string.image_of_card),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Column(modifier = Modifier.padding(horizontal = 28.dp)) {
            Text(text = error.question, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))

            if (error.trainingMode == TrainingMode.FILL_IN_THE_BLANK &&
                error.answer != error.blankAnswer
            ) {
                AnswerSection(
                    title = stringResource(R.string.full_answer),
                    answer = error.answer,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
                Spacer(modifier = Modifier.height(19.dp))
            }

            AnswerSection(
                title = stringResource(R.string.correct_answer),
                answer = error.blankAnswer?.takeIf { it.isNotBlank() } ?: error.answer,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(19.dp))

            val userAnswer = if (error.userAnswer.isBlank()) stringResource(R.string.dont_answered)
            else error.userAnswer

            when (error.trainingMode) {
                TrainingMode.MULTIPLE_CHOICE -> {
                    AnswerSection(
                        title = stringResource(R.string.your_answer),
                        answer = userAnswer,
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                }

                TrainingMode.TRUE_FALSE -> {
                    AnswerSection(
                        title = stringResource(R.string.your_answer),
                        answer = stringResource(R.string.your_answer_is_wrong),
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                }
                TrainingMode.FILL_IN_THE_BLANK -> {
                    AnswerSection(
                        title = stringResource(R.string.your_answer),
                        answer = userAnswer,
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                }
            }
        }
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
        text = stringResource(R.string.no_data),
        style = MaterialTheme.typography.bodyLarge
    )
}