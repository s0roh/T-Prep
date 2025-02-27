package com.example.feature.decks.presentation.public_decks

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.common.ui.DeckCard
import com.example.common.ui.entity.DeckUiModel
import com.example.decks.R
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicDecksScreen(
    paddingValues: PaddingValues,
    onDeckClickListener: (String) -> Unit,
) {
    val viewModel: PublicDecksViewModel = hiltViewModel()
    val query = remember { mutableStateOf("") }
    val searchBarExpanded = remember { mutableStateOf(false) }
    val decksFlow = remember(query.value) {
        if (query.value.isBlank()) viewModel.publicDecks
        else viewModel.searchPublicDecks(query.value)
    }
    val lazyPagingItems = decksFlow.collectAsLazyPagingItems()

    val animatedPadding by animateDpAsState(
        targetValue = if (searchBarExpanded.value) 0.dp else 16.dp,
        label = "searchBarPadding"
    )

    PullToRefreshBox(
        isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading,
        onRefresh = { lazyPagingItems.refresh() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBarComponent(
                query = query,
                searchBarExpanded = searchBarExpanded,
                lazyPagingItems = lazyPagingItems,
                onDeckClickListener = onDeckClickListener,
                modifier = Modifier.padding(horizontal = animatedPadding)
            )
            HandlePagingLoadState(
                loadState = lazyPagingItems.loadState.refresh,
                onRetry = { lazyPagingItems.retry() }
            ) {
                LazyColumn(
                    modifier = Modifier.padding(
                        bottom = paddingValues.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    item {
                        Spacer(modifier = Modifier)
                    }
                    items(lazyPagingItems.itemCount) { index ->
                        lazyPagingItems[index]?.let { deck ->
                            DeckCard(
                                deck = deck,
                                onDeckClickListener = onDeckClickListener,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HandlePagingLoadState(
    loadState: LoadState,
    onRetry: () -> Unit,
    onContent: @Composable () -> Unit,
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
private fun LoadingIndicator(
    modifier: Modifier = Modifier.fillMaxSize(),
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
    isFullScreen: Boolean = true,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarComponent(
    query: MutableState<String>,
    searchBarExpanded: MutableState<Boolean>,
    lazyPagingItems: LazyPagingItems<DeckUiModel>,
    onDeckClickListener: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val inputField = @Composable {
        InputField(
            query = query.value,
            onQueryChange = { query.value = it },
            onSearch = {
                scope.launch { searchBarExpanded.value = false }
            },
            expanded = searchBarExpanded.value,
            onExpandedChange = { searchBarExpanded.value = it },
            placeholder = { Text("Поиск колод") },
            leadingIcon = {
                if (searchBarExpanded.value) {
                    IconButton(
                        onClick = { scope.launch { searchBarExpanded.value = false } }
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Назад")
                    }
                } else {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = query.value.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = { query.value = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Очистить поиск")
                    }
                }
            }
        )
    }

    SearchBar(
        inputField = inputField,
        expanded = searchBarExpanded.value,
        onExpandedChange = { searchBarExpanded.value = it },
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
        ) {
            val decksList = lazyPagingItems.itemSnapshotList.items
            if (decksList.isEmpty()) {
                Text("Нет результатов", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    item {
                        Spacer(modifier = Modifier)
                    }
                    items(decksList) { deck ->
                        DeckCard(
                            deck = deck,
                            onDeckClickListener = onDeckClickListener,
                            modifier = Modifier.animateItem()
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}