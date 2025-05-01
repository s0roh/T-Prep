package com.example.feature.decks.presentation.public_decks

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.common.ui.DeckCard
import com.example.common.ui.entity.DeckUiModel
import com.example.database.models.Source
import com.example.feature.decks.R
import com.example.feature.decks.presentation.util.rememberPublicDecksBalloonBuilder
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonWindow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicDecksScreen(
    paddingValues: PaddingValues,
    onDeckClickListener: (String) -> Unit,
    onTrainClick: (String, Source) -> Unit,
    onScheduleClick: (String, String, Source) -> Unit,
) {
    val viewModel: PublicDecksViewModel = hiltViewModel()
    val screenState by viewModel.screenState.collectAsState()
    val query = rememberSaveable { mutableStateOf("") }
    val searchBarExpanded = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val decksFlow = remember(query.value) {
        if (query.value.isBlank()) viewModel.decksFlow
        else viewModel.searchPublicDecks(query.value)
    }.collectAsLazyPagingItems()

    val isScrollingDown = remember { derivedStateOf { listState.firstVisibleItemScrollOffset > 0 } }

    val animatedPadding by animateDpAsState(
        targetValue = if (searchBarExpanded.value) 0.dp else 24.dp,
        label = stringResource(R.string.searchbarpadding)
    )

    val showDeckCardTooltip = rememberSaveable { mutableStateOf(true) }
    var balloonWindow by rememberBalloonWindow(null)
    var canDismiss by remember { mutableStateOf(false) }
    var isBalloonShown by rememberSaveable { mutableStateOf(false) }

    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.toArgb()
    val overlayColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f).toArgb()
    val shouldShowTooltip = screenState.shouldShowTooltip

    LaunchedEffect(Unit) {
        if (shouldShowTooltip) {
            delay(2000L)
            canDismiss = true
        }
    }

    val builder = rememberPublicDecksBalloonBuilder(
        backgroundColor = backgroundColor,
        overlayColor = overlayColor,
        canDismissProvider = { canDismiss },
        onDismissRequest = { balloonWindow?.dismiss() }
    )

    PullToRefreshBox(
        isRefreshing = decksFlow.loadState.refresh is LoadState.Loading,
        onRefresh = { decksFlow.refresh() },
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SearchBarComponent(
                query = query,
                searchBarExpanded = searchBarExpanded,
                lazyPagingItems = decksFlow,
                onDeckClickListener = onDeckClickListener,
                onTrainClick = { deckId ->
                    coroutineScope.launch {
                        val (deck, source) = viewModel.getDeckById(deckId)
                        onTrainClick(deck.id, source)
                    }
                },
                onQueryChange = { newQuery -> viewModel.searchPublicDecks(newQuery) },
                onLikeClickListener = { deckId, newIsLiked, onUpdate ->
                    viewModel.onLikeClick(
                        deckId,
                        newIsLiked
                    ) { successIsLiked, updatedLikes ->
                        onUpdate(successIsLiked, updatedLikes)
                    }
                },
                onScheduleClick = { deckId ->
                    coroutineScope.launch {
                        val (deck, source) = viewModel.getDeckById(deckId)
                        onScheduleClick(deck.id, deck.name, source)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = animatedPadding)
            )

            AnimatedVisibility(visible = !isScrollingDown.value) {
                SortAndCategoryFilters(
                    screenState = screenState,
                    updateSortType = viewModel::updateSortType,
                    updateCategory = viewModel::updateCategory
                )
            }

            HandlePagingLoadState(
                loadState = decksFlow.loadState.refresh,
                isLikeCategorySelected = screenState.category == DeckCategory.LIKED,
                onRetry = { decksFlow.retry() }
            ) {
                LazyColumn(
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(decksFlow.itemCount) { index ->
                        decksFlow[index]?.let { deck ->
                            var isLiked by remember { mutableStateOf(deck.isLiked) }
                            var likes by remember { mutableIntStateOf(deck.likes) }

                            val cardModifier = if (index == 0 && showDeckCardTooltip.value) {
                                Modifier.animateItem()
                            } else {
                                Modifier
                                    .padding(horizontal = 24.dp)
                                    .animateItem()
                            }

                            val deckCard = @Composable {
                                DeckCard(
                                    deck = deck.copy(isLiked = isLiked, likes = likes),
                                    onDeckClickListener = onDeckClickListener,
                                    onTrainClick = { id ->
                                        coroutineScope.launch {
                                            val (deck, source) = viewModel.getDeckById(id)
                                            onTrainClick(deck.id, source)
                                        }
                                    },
                                    onLikeClickListener = { id, liked ->
                                        viewModel.onLikeClick(
                                            id,
                                            liked
                                        ) { updatedLiked, updatedLikes ->
                                            isLiked = updatedLiked
                                            likes = updatedLikes
                                        }
                                    },
                                    onScheduleClick = { id ->
                                        coroutineScope.launch {
                                            val (deck, source) = viewModel.getDeckById(id)
                                            onScheduleClick(deck.id, deck.name, source)
                                        }
                                    },
                                    balloonWindow = balloonWindow,
                                    modifier = cardModifier
                                )
                            }

                            if (index == 0 && showDeckCardTooltip.value) {
                                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                    Balloon(
                                        builder = builder,
                                        onBalloonWindowInitialized = { balloonWindow = it },
                                        onComposedAnchor = {
                                            if (shouldShowTooltip && !isBalloonShown) {
                                                balloonWindow?.showAlignBottom()
                                                isBalloonShown = true
                                            }
                                        },
                                        balloonContent = {
                                            Text(
                                                text = stringResource(R.string.deck_card_long_press_tooltip),
                                                modifier = Modifier.padding(8.dp),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        }
                                    ) {
                                        deckCard()
                                    }
                                }
                            } else {
                                deckCard()
                            }
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
private fun SortAndCategoryFilters(
    screenState: PublicDecksScreenState,
    updateSortType: (SortType) -> Unit,
    updateCategory: (DeckCategory) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val selectedSortType = screenState.sortType

            SortType.entries.forEach { sortType ->
                val backgroundColor by animateColorAsState(
                    targetValue = if (selectedSortType == sortType)
                        MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.surface,
                    label = stringResource(R.string.sorttypebackground)
                )

                AssistChip(
                    onClick = { if (selectedSortType != sortType) updateSortType(sortType) },
                    label = {
                        Crossfade(targetState = sortType) { targetSortType ->
                            Text(
                                text = when (targetSortType) {
                                    SortType.LIKES -> stringResource(R.string.by_likes)
                                    SortType.TRAININGS -> stringResource(R.string.by_train)
                                }
                            )
                        }
                    },
                    colors = AssistChipDefaults.assistChipColors(containerColor = backgroundColor),
                    border = if (selectedSortType == sortType) null else AssistChipDefaults.assistChipBorder(
                        true
                    ),
                    leadingIcon = {
                        if (selectedSortType == sortType) {
                            Crossfade(targetState = selectedSortType) { selected ->
                                if (selected == sortType) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = stringResource(R.string.selected_mark)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val selectedCategory = screenState.category
            AssistChip(
                onClick = {
                    if (selectedCategory == DeckCategory.LIKED) {
                        updateCategory(DeckCategory.ALL)
                    } else {
                        updateCategory(DeckCategory.LIKED)
                    }
                },
                label = { Text(stringResource(R.string.favorites)) },
                colors = AssistChipDefaults.assistChipColors(
                    if (selectedCategory == DeckCategory.LIKED)
                        MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.surface
                ),
                border = if (selectedCategory == DeckCategory.LIKED) null
                else AssistChipDefaults.assistChipBorder(true),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite_icon),
                        tint = if (selectedCategory == DeckCategory.LIKED) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun HandlePagingLoadState(
    loadState: LoadState,
    onRetry: () -> Unit,
    isLikeCategorySelected: Boolean,
    onContent: @Composable () -> Unit,
) {
    when (loadState) {
        is LoadState.Loading -> LoadingIndicator()
        is LoadState.Error -> {
            if (isLikeCategorySelected) {
                EmptyContent(modifier = Modifier.fillMaxSize())
            } else {
                ErrorContent(
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        else -> onContent()
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = stringResource(R.string.no_data),
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_results),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier,
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
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.WifiOff,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.wifi_error),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
            Spacer(modifier = Modifier.height(48.dp))

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarComponent(
    query: MutableState<String>,
    searchBarExpanded: MutableState<Boolean>,
    lazyPagingItems: LazyPagingItems<DeckUiModel>,
    onQueryChange: (String) -> Unit,
    onDeckClickListener: (String) -> Unit,
    onTrainClick: (String) -> Unit,
    onScheduleClick: (String) -> Unit,
    onLikeClickListener: (String, Boolean, (Boolean, Int) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val inputField = @Composable {
        InputField(
            query = query.value,
            onQueryChange = { newQuery ->
                query.value = newQuery
                onQueryChange(newQuery)
            },
            onSearch = { newQuery ->
                scope.launch {
                    query.value = newQuery
                    onQueryChange(newQuery)
                }
            },
            expanded = searchBarExpanded.value,
            onExpandedChange = { searchBarExpanded.value = it },
            placeholder = { Text(stringResource(R.string.search_decks)) },
            leadingIcon = {
                if (searchBarExpanded.value) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                searchBarExpanded.value = false
                                query.value = ""
                            }
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(
                                R.string.back
                            )
                        )
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
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.clear_search)
                        )
                    }
                }
            }
        )
    }

    SearchBar(
        inputField = inputField,
        expanded = searchBarExpanded.value,
        onExpandedChange = { expanded ->
            searchBarExpanded.value = expanded
            if (!expanded) {
                query.value = ""
            }
        },
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
                Text(text = stringResource(R.string.no_results), modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    item {
                        Spacer(modifier = Modifier)
                    }
                    items(decksList) { deck ->
                        var isLiked by remember(deck.id) { mutableStateOf(deck.isLiked) }
                        var likes by remember(deck.id) { mutableIntStateOf(deck.likes) }

                        DeckCard(
                            deck = deck.copy(isLiked = isLiked, likes = likes),
                            onDeckClickListener = onDeckClickListener,
                            onTrainClick = onTrainClick,
                            onLikeClickListener = { deckId, newIsLiked ->
                                onLikeClickListener(
                                    deckId,
                                    newIsLiked
                                ) { updatedIsLiked, updatedLikes ->
                                    isLiked = updatedIsLiked
                                    likes = updatedLikes

                                }
                            },
                            onScheduleClick = onScheduleClick,
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