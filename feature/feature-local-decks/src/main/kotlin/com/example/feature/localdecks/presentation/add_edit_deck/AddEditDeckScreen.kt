package com.example.feature.localdecks.presentation.add_edit_deck

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.NavigationIconType
import com.example.feature.localdecks.presentation.components.PrivacyToggleButton
import com.example.feature.localdecks.presentation.components.TextFieldWithError

@Composable
fun AddEditDeckScreen(
    deckId: String?,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
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
                title = "Редактировать колоду",
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            AddEditDeckForm(
                screenState = screenState,
                onNameChange = { viewModel.onNameChanged(it) },
                onPublicToggle = { viewModel.onPublicStateChanged() },
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
    screenState: AddEditDeckScreenState,
    onNameChange: (String) -> Unit,
    onPublicToggle: () -> Unit,
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
            value = screenState.name,
            onValueChange = onNameChange,
            label = "Название колоды",
            error = screenState.nameError,
            imeAction = ImeAction.Done,
            onImeAction = { focusManager.clearFocus() },
            modifier = Modifier.fillMaxWidth()
        )

        PrivacyToggleButton(
            isPublic = screenState.isPublic,
            onToggle = onPublicToggle,
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.Start)
        )

        Button(
            onClick = onSave,
            enabled = screenState.isSaveButtonEnabled,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Сохранить")
        }
    }
}