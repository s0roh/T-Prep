package com.example.t_prep.presentation.publicDecks

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.t_prep.domain.entity.Deck

@Composable
fun PublicDecksScreen(
    paddingValues: PaddingValues,
    token: String,
    onDeckClickListener: (Long) -> Unit
) {
    val viewModel: PublicDecksViewModel = viewModel()
    val screenState = viewModel.screenState.collectAsState(PublicDecksScreenState.Initial)

    PublicDecksScreenContent(
        token = token,
        paddingValues = paddingValues,
        screenState = screenState,
        onDeckClickListener = onDeckClickListener,
        viewModel = viewModel
    )
}

@Composable
private fun PublicDecksScreenContent(
    token: String,
    paddingValues: PaddingValues,
    screenState: State<PublicDecksScreenState>,
    onDeckClickListener: (Long) -> Unit,
    viewModel: PublicDecksViewModel
) {
    when (val currentState = screenState.value) {
        is PublicDecksScreenState.Decks -> {
            PublicDecks(
                token = token,
                paddingValues = paddingValues,
                decks = currentState.decks,
                nextDataIsLoading = currentState.nextDataIsLoading,
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
    token: String,
    paddingValues: PaddingValues,
    decks: List<Deck>,
    nextDataIsLoading: Boolean,
    onDeckClickListener: (Long) -> Unit,
    loadNextPublicDecks: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            horizontal = 8.dp,
            vertical = 16.dp
        )
    ) {
        item {
            Text(text = token)
        }
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
        item {
            if (nextDataIsLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
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
                text = if (deck.isPublic) "Public" else "Private",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Количество карточек: ${deck.cards.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}