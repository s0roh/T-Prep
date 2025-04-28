package com.example.feature.localdecks.presentation.add_edit_deck

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.AppButton
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.NavigationIconType
import com.example.feature.localdecks.R
import com.example.feature.localdecks.presentation.components.TextFieldWithError

@Composable
fun AddEditDeckScreen(
    deckId: String?,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val viewModel: AddEditDeckViewModel = hiltViewModel()
    val screenState by viewModel.screenState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(deckId) {
        viewModel.currentDeckId = deckId
        viewModel.initDeck()
    }

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = if (deckId == null) stringResource(R.string.deck_creation)
                else stringResource(R.string.edit_deck),
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .imePadding()
        ) {
            AddEditDeckForm(
                deckId = deckId,
                screenState = screenState,
                onNameChange = { viewModel.onNameChanged(it) },
                onSave = {
                    viewModel.saveDeck()
                    if (screenState.name.isNotBlank()) {
                        onSaveClick()
                    }
                },
                focusManager = focusManager
            )
        }
    }
}

@Composable
private fun AddEditDeckForm(
    deckId: String?,
    screenState: AddEditDeckScreenState,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    focusManager: FocusManager,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextFieldWithError(
            value = screenState.name,
            onValueChange = onNameChange,
            labelResId = R.string.name,
            error = screenState.nameError,
            imeAction = ImeAction.Done,
            onImeAction = {
                focusManager.clearFocus()
                keyboardController?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        Spacer(modifier = Modifier.weight(1f))

        AppButton(
            title = if (deckId == null) stringResource(R.string.add_deck)
            else stringResource(R.string.edit_deck),
            onClick = onSave,
            enabled = screenState.isSaveButtonEnabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}