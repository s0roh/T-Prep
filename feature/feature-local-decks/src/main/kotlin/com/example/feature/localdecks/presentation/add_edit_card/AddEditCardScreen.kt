package com.example.feature.localdecks.presentation.add_edit_card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.AppButton
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.NavigationIconType
import com.example.feature.localdecks.presentation.components.TextFieldWithError

@Composable
fun AddEditCardScreen(
    cardId: Int?,
    deckId: String,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val viewModel: AddEditCardViewModel = hiltViewModel()
    val screenState by viewModel.screenState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(cardId) {
        viewModel.currentCardId = cardId
        viewModel.currentDeckId = deckId
        viewModel.initCard()
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
        Column(modifier = Modifier.padding(paddingValues).imePadding()) {
            AddEditCardForm(
                cardId = cardId,
                screenState = screenState,
                onQuestionChange = { viewModel.onQuestionChanged(it) },
                onAnswerChange = { viewModel.onAnswerChanged(it) },
                onSave = {
                    if (viewModel.saveCard()) {
                        onSaveClick()
                    }
                },
                focusManager = focusManager
            )
        }
    }
}

@Composable
private fun AddEditCardForm(
    cardId: Int?,
    screenState: AddEditCardScreenState,
    onQuestionChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
    onSave: () -> Unit,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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

        Spacer(modifier = Modifier.weight(1f))

        AppButton(
            title = if (cardId == null) "Добавить карточку" else "Изменить карточку",
            onClick = onSave,
            enabled = screenState.isSaveButtonEnabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}