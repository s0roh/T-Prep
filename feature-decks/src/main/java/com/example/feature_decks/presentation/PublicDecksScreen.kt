package com.example.feature_decks.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.data_decks.domain.entity.Deck
import com.example.feature_decks.R

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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
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

@Composable
private fun DeckCard(
    deck: Deck,
    onDeckClickListener: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = { onDeckClickListener(deck.id) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = deck.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = if (deck.isPublic) stringResource(R.string.decks_public)
                else stringResource(R.string.decks_private),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = stringResource(R.string.count_of_decks, deck.cards.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}