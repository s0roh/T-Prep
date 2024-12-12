package com.example.localdecks.presentation.local_decks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.DeckCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalDecksScreen(
    paddingValues: PaddingValues,
    onDeckClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    val viewModel: LocalDecksViewModel = hiltViewModel()
    val decks by viewModel.decks.collectAsState()

    Column {
        TopAppBar(
            title = { Text("My Decks") },
            actions = {
                IconButton(
                    onClick = onAddClick
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Deck")
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = paddingValues.calculateBottomPadding()),
        ) {
            items(
                items = decks,
                key = { it.id }
            ) { deck ->
                DeckCard(deck = deck, onDeckClickListener = { onDeckClick(deck.id) })
            }
        }
    }
}