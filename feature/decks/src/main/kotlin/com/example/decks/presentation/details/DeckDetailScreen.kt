package com.example.decks.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.decks.presentation.components.DeckCard
import com.example.common.ui.ErrorState
import com.example.common.ui.LoadingState
import com.example.decks.R

@Composable
fun DeckDetailScreen(
    deckId: Long,
    paddingValues: PaddingValues,
    onStartTraining: (deckId: Long) -> Unit
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
                paddingValues = paddingValues,
                onStartTraining = onStartTraining
            )
        }
    }
}

@Composable
private fun DeckDetailContent(
    deck: Deck,
    paddingValues: PaddingValues,
    onStartTraining: (deckId: Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                DeckCard(
                    deck = deck,
                    onDeckClickListener = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.cards_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            items(
                items = deck.cards,
                key = { it.id }
            ) { card ->
                CardItem(card = card)
            }
        }
        Button(
            onClick = { onStartTraining(deck.id) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(text = stringResource(R.string.start_training))
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

