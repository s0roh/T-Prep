package com.example.decks.presentation.publicdecks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.common.domain.entity.Deck
import com.example.common.ui.DeckCard
import com.example.decks.R

@Composable
fun PublicDecksScreen(
    paddingValues: PaddingValues,
    onDeckClickListener: (String) -> Unit
) {
    val viewModel: PublicDecksViewModel = hiltViewModel()
    val lazyPagingItems = viewModel.publicDecks.collectAsLazyPagingItems()

    HandlePagingLoadState(
        loadState = lazyPagingItems.loadState.refresh,
        onRetry = { lazyPagingItems.retry() }
    ) {
        PublicDecksList(
            lazyPagingItems = lazyPagingItems,
            paddingValues = paddingValues,
            onDeckClickListener = onDeckClickListener
        )
    }
}

@Composable
private fun HandlePagingLoadState(
    loadState: LoadState,
    onRetry: () -> Unit,
    onContent: @Composable () -> Unit
) {
    when (loadState) {
        is LoadState.Loading -> LoadingIndicator()
        is LoadState.Error -> {
            val errorMessage =
                loadState.error.localizedMessage ?: stringResource(R.string.error_occurred)
            ErrorContent(message = errorMessage, onRetry = onRetry, isFullScreen = true)
        }

        else -> onContent()
    }
}

@Composable
private fun PublicDecksList(
    lazyPagingItems: LazyPagingItems<Deck>,
    paddingValues: PaddingValues,
    onDeckClickListener: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        items(
            count = lazyPagingItems.itemCount
        ) { index ->
            lazyPagingItems[index]?.let { deck ->
                DeckCard(
                    deck = deck,
                    onDeckClickListener = onDeckClickListener
                )
            }
        }

        when (val appendState = lazyPagingItems.loadState.append) {

            is LoadState.Loading -> {
                item {
                    LoadingIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }

            is LoadState.Error -> {
                val errorMessage =
                    appendState.error.localizedMessage
                        ?: "Ошибка подгрузки данных"
                item {
                    ErrorContent(
                        message = errorMessage,
                        onRetry = { lazyPagingItems.retry() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentSize(Alignment.Center),
                        isFullScreen = false
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
    isFullScreen: Boolean = true
) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isFullScreen) {
                Icon(
                    imageVector = Icons.Filled.WifiOff,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}