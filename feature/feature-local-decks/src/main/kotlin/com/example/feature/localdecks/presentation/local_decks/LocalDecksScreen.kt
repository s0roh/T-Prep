package com.example.feature.localdecks.presentation.local_decks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.DeckCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalDecksScreen(
    onDeckClick: (String) -> Unit,
    onAddClick: () -> Unit,
) {
    val viewModel: LocalDecksViewModel = hiltViewModel()
    val decks by viewModel.decks.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val context = LocalContext.current
    val refreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои колоды") },
                actions = {
                    IconButton(
                        onClick = onAddClick
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Deck")
                    }
                }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            state = refreshState,
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.startSync(context) }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = decks,
                    key = { it.id }
                ) { deck ->
                    DeckCard(
                        deck = deck,
                        modifier = Modifier.animateItem(),
                        onDeckClickListener = { onDeckClick(deck.id) })
                }
            }
        }
    }
}