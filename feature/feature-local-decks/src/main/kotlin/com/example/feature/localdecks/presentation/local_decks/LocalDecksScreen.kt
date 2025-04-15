package com.example.feature.localdecks.presentation.local_decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.DeckCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalDecksScreen(
    paddingValues: PaddingValues,
    onDeckClick: (String) -> Unit,
    onDeckLongClickListener: (String) -> Unit,
    onAddClick: () -> Unit,
) {
    val viewModel: LocalDecksViewModel = hiltViewModel()
    val decks by viewModel.decks.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val context = LocalContext.current
    val refreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    val expandedFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    Scaffold(
        topBar = {
            CenteredTopAppBar(title = "Мои колоды")
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(paddingValues),
                onClick = onAddClick,
                expanded = expandedFab,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add deck",
                    )
                },
                text = {
                    Text(text = "Создать колоду")
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier.padding(innerPadding),
            state = refreshState,
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.startSync(context) }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                items(
                    items = decks,
                    key = { it.id }
                ) { deck ->
                    DeckCard(
                        deck = deck,
                        modifier = Modifier.animateItem(),
                        onDeckClickListener = { onDeckClick(deck.id) },
                        onDeckLongClickListener = onDeckLongClickListener
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(180.dp))
                }
            }
        }
    }
}