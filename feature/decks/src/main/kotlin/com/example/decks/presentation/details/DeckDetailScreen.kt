package com.example.decks.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.decks.domain.entity.Card
import com.example.decks.domain.entity.Deck
import com.example.decks.presentation.components.DeckCard
import com.example.decks.presentation.components.ErrorState
import com.example.decks.presentation.components.LoadingState
import com.example.decks.R

@Composable
fun DeckDetailScreen(
    deckId: Long,
    paddingValues: PaddingValues
) {
    val viewModel: DeckDetailViewModel = hiltViewModel()
    val screenState = viewModel.screenState.collectAsState()

    LaunchedEffect(deckId) {
        viewModel.loadDeckById(deckId)
    }

    when (val currentState = screenState.value) {
        is DeckDetailScreenState.Loading -> {
            LoadingState()
        }

        is DeckDetailScreenState.Error -> {
            ErrorState(message = stringResource(R.string.error_loading_deck))
        }

        is DeckDetailScreenState.Success -> {
            DeckDetailContent(
                deck = currentState.deck,
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
private fun DeckDetailContent(
    deck: Deck,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)

    ) {
        DeckCard(
            deck = deck,
            onDeckClickListener = {},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.cards_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(
                items = deck.cards,
                key = { it.id }
            ) { card ->
                CardItem(card = card)
            }
        }
    }
}

@Composable
private fun CardItem(
    card: Card,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.card_question, card.question),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.card_answer, card.answer),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

