package com.example.decks.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.common.ui.DeckCard
import com.example.common.ui.ErrorState
import com.example.common.ui.LoadingState
import com.example.database.models.Source
import com.example.decks.R

@Composable
fun DeckDetailScreen(
    deckId: Long,
    source: Source,
    paddingValues: PaddingValues,
    onStartTraining: (deckId: Long) -> Unit,
    onAddCardClick: () -> Unit,
    onDeleteDeck: () -> Unit,
    onEditDeck: (deckId: Long) -> Unit = {},
    onEditCard: (deckId: Long, cardId: Long?) -> Unit
) {
    val viewModel: DeckDetailViewModel = hiltViewModel()
    val screenState = viewModel.screenState.collectAsState()

    LaunchedEffect(deckId) {
        viewModel.loadDeckById(deckId = deckId, source = source)
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
                source = source,
                paddingValues = paddingValues,
                onStartTraining = onStartTraining,
                onAddCardClick = onAddCardClick,
                onEditDeck = onEditDeck,
                onDeleteDeck = { deck ->
                    viewModel.deleteDeck(deck)
                    onDeleteDeck()
                },
                onEditCard = onEditCard,
                onDeleteCard = { card ->
                    viewModel.deleteCard(card)
                },
            )
        }
    }
}

@Composable
private fun DeckDetailContent(
    deck: Deck,
    source: Source,
    paddingValues: PaddingValues,
    onStartTraining: (deckId: Long) -> Unit,
    onAddCardClick: () -> Unit,
    onEditDeck: (deckId: Long) -> Unit,
    onDeleteDeck: (Deck) -> Unit,
    onEditCard: (deckId: Long, cardId: Long?) -> Unit,
    onDeleteCard: (Card) -> Unit
) {
    var showDeleteDeckDialog by remember { mutableStateOf(false) }
    var showDeleteCardDialog by remember { mutableStateOf<Card?>(null) }

    if (showDeleteDeckDialog) {
        ConfirmDeleteDialog(
            message = stringResource(R.string.confirm_delete_deck),
            onConfirm = {
                showDeleteDeckDialog = false
                onDeleteDeck(deck)
            },
            onCancel = { showDeleteDeckDialog = false }
        )
    }

    showDeleteCardDialog?.let { card ->
        ConfirmDeleteDialog(
            message = stringResource(R.string.confirm_delete_card),
            onConfirm = {
                showDeleteCardDialog = null
                onDeleteCard(card)
            },
            onCancel = { showDeleteCardDialog = null }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 160.dp)
        ) {
            item {
                DeckCard(
                    deck = deck,
                    onDeckClickListener = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    showActions = source == Source.LOCAL,
                    onEdit = { onEditDeck(deck.id) },
                    onDelete = { showDeleteDeckDialog = true }
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
            if (deck.cards.isEmpty()) {
                item {
                    NoCardsPlaceholder()
                }
            } else {
                items(
                    items = deck.cards,
                    key = { it.id }
                ) { card ->
                    CardItem(
                        card = card,
                        showActions = source == Source.LOCAL,
                        onEdit = { onEditCard(deck.id, card.id) },
                        onDelete = { showDeleteCardDialog = card }
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            if (source == Source.LOCAL) {
                FloatingActionButton(
                    onClick = { onAddCardClick() },
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.End)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
            if (deck.cards.isNotEmpty()) {
                Button(
                    onClick = { onStartTraining(deck.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.start_training))
                }
            }
        }
    }
}

@Composable
private fun CardItem(
    card: Card,
    modifier: Modifier = Modifier,
    showActions: Boolean = false,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
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
            if (showActions) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun NoCardsPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_cards_placeholder),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
fun ConfirmDeleteDialog(
    message: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(text = stringResource(R.string.confirm_delete_title)) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}
