package com.example.feature.localdecks.presentation.add_edit_card

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.example.common.R
import com.example.common.ui.AppButton
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.NavigationIconType
import com.example.feature.localdecks.presentation.components.TextFieldWithError
import com.example.feature.localdecks.presentation.util.CropImageContract
import com.example.feature.localdecks.presentation.util.launchCrop

@Composable
fun AddEditCardScreen(
    cardId: Int?,
    deckId: String,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val viewModel: AddEditCardViewModel = hiltViewModel()
    val screenState by viewModel.screenState.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val themeColors = MaterialTheme.colorScheme

    LaunchedEffect(cardId) {
        viewModel.currentCardId = cardId
        viewModel.currentDeckId = deckId
        viewModel.initCard()
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AddEditCardEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.setCardPicture(result.uriContent.toString())
        }
    }

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { launchCrop(uri, cropImageLauncher::launch, themeColors) }
        }

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = if (cardId == null) "Создание карточки" else "Изменить карточку",
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = onBackClick
            )
        }
    ) { paddingValues ->
        AddEditCardForm(
            modifier = Modifier
                .padding(paddingValues)
                .imePadding(),
            cardId = cardId,
            screenState = screenState,
            onQuestionChange = { viewModel.onQuestionChanged(it) },
            onAnswerChange = { viewModel.onAnswerChanged(it) },
            onSave = {
                if (viewModel.saveCard()) {
                    onSaveClick()
                }
            },
            onAddPictureClick = { pickImageLauncher.launch("image/*") },
            focusManager = focusManager,
            viewModel = viewModel
        )
    }
}

@Composable
private fun AddEditCardForm(
    modifier: Modifier = Modifier,
    cardId: Int?,
    screenState: AddEditCardScreenState,
    onQuestionChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
    onSave: () -> Unit,
    onAddPictureClick: () -> Unit,
    focusManager: FocusManager,
    viewModel: AddEditCardViewModel,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 45.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextFieldWithError(
                value = screenState.question,
                onValueChange = onQuestionChange,
                label = "Вопрос",
                error = screenState.questionError,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedContent(
                targetState = screenState.cardPictureUri,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "CardPictureSwitcher"
            ) { targetImageUri ->
                if (targetImageUri == null) {
                    TextButton(
                        onClick = onAddPictureClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_image),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Добавить картинку")
                    }
                } else {
                    CardPicture(
                        imageUri = targetImageUri,
                        onClick = onAddPictureClick,
                        onSwipeToDelete = { viewModel.deleteCardPicture() }
                    )
                }
            }

            TextFieldWithError(
                value = screenState.answer,
                onValueChange = onAnswerChange,
                label = "Ответ",
                error = screenState.answerError,
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                    onSave()
                },
                modifier = Modifier.fillMaxWidth()
            )

            WrongAnswersSection(
                wrongAnswers = screenState.wrongAnswerList,
                onAddWrongAnswer = { viewModel.addWrongAnswerField() },
                onWrongAnswerChanged = { index, value ->
                    viewModel.updateWrongAnswer(
                        index,
                        value
                    )
                },
                onRemoveWrongAnswer = { index -> viewModel.removeWrongAnswer(index) }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        AppButton(
            title = if (cardId == null) "Добавить карточку" else "Изменить карточку",
            onClick = onSave,
            enabled = screenState.isSaveButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun WrongAnswersSection(
    wrongAnswers: List<String>,
    onAddWrongAnswer: () -> Unit,
    onWrongAnswerChanged: (Int, String) -> Unit,
    onRemoveWrongAnswer: (Int) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        wrongAnswers.forEachIndexed { index, answer ->
            WrongAnswerField(
                value = answer,
                onValueChange = { onWrongAnswerChanged(index, it) },
                onRemove = { onRemoveWrongAnswer(index) }
            )
        }

        if (wrongAnswers.size < 3) {
            TextButton(
                onClick = onAddWrongAnswer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить неправильный ответ")
            }
        }
    }
}

@Composable
private fun WrongAnswerField(
    value: String,
    onValueChange: (String) -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Неправильный ответ") },
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_trash),
                contentDescription = "Удалить",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CardPicture(
    imageUri: Uri,
    onClick: () -> Unit,
    onSwipeToDelete: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * 0.7f }
    )

    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
        onSwipeToDelete()
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Удалить",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.W500)
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build(),
            contentDescription = "Картинка карточки",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() }
        )
    }
}
