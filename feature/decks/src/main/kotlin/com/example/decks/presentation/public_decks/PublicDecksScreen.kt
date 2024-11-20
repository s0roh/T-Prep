package com.example.decks.presentation.public_decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.decks.domain.entity.Deck
import com.example.decks.presentation.components.DeckCard
import com.example.decks.presentation.components.LoadingState

@Composable
fun PublicDecksScreen(
    paddingValues: PaddingValues,
    onDeckClickListener: (Long) -> Unit
) {
    val viewModel: PublicDecksViewModel = hiltViewModel()
    val screenState = viewModel.screenState.collectAsState()

    PublicDecksScreenContent(
        paddingValues = paddingValues,
        screenState = screenState,
        onDeckClickListener = onDeckClickListener,
        viewModel = viewModel
    )
}

@Composable
private fun PublicDecksScreenContent(
    paddingValues: PaddingValues,
    screenState: State<PublicDecksScreenState>,
    onDeckClickListener: (Long) -> Unit,
    viewModel: PublicDecksViewModel
) {
    when (val currentState = screenState.value) {
        is PublicDecksScreenState.Decks -> {
            PublicDecks(
                paddingValues = paddingValues,
                decks = currentState.decks,
                nextDataIsLoading = currentState.nextDataIsLoading,
                hasMoreData = currentState.hasMoreData,
                onDeckClickListener = onDeckClickListener,
                loadNextPublicDecks = { viewModel.loadNextPublicDecks() }
            )
        }

        PublicDecksScreenState.Initial -> {}
        PublicDecksScreenState.Loading -> {
            LoadingState()
        }
    }
}

@Composable
private fun PublicDecks(
    paddingValues: PaddingValues,
    decks: List<Deck>,
    nextDataIsLoading: Boolean,
    hasMoreData: Boolean,
    onDeckClickListener: (Long) -> Unit,
    loadNextPublicDecks: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            horizontal = 8.dp,
            vertical = 16.dp
        )
    ) {
        items(
            items = decks,
            key = { it.id }
        ) { deck ->
            DeckCard(
                deck = deck,
                onDeckClickListener = onDeckClickListener,
                modifier = Modifier
            )
        }
        if (hasMoreData) {
            item {
                if (nextDataIsLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    SideEffect {
                        loadNextPublicDecks()
                    }
                }
            }
        }
    }
}