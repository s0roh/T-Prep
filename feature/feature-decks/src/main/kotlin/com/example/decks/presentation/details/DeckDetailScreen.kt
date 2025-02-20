package com.example.decks.presentation.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.common.ui.AppButton
import com.example.common.ui.AppElevatedButton
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.ErrorState
import com.example.common.ui.LoadingState
import com.example.common.ui.NavigationIconType
import com.example.common.util.getCardWordForm
import com.example.common.util.getFormattedTime
import com.example.database.models.Source
import com.example.decks.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DeckDetailScreen(
    deckId: String,
    source: Source,
    onBackClick: () -> Unit,
    onStartTraining: (deckId: String) -> Unit,
    onAddCardClick: () -> Unit,
    onEditDeck: (deckId: String) -> Unit = {},
    onEditCard: (deckId: String, cardId: Int?) -> Unit,
    onRemindClick: (deckName: String) -> Unit,
    onTrainingModeSettingsClick: (String) -> Unit
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
                state = currentState,
                onBackClick = onBackClick,
                onStartTraining = onStartTraining,
                onAddCardClick = onAddCardClick,
                onEditDeckName = onEditDeck,
                onEditDeckPrivateState = {
                    viewModel.changeDeckPrivacy()
                },
                onDeleteDeck = {
                    viewModel.deleteDeck()
                    onBackClick()
                },
                onDeleteCard = { card ->
                    viewModel.deleteCard(card = card)
                },
                onEditCard = onEditCard,
                onRemindClick = onRemindClick,
                onTrainingModeSettingsClick = onTrainingModeSettingsClick

            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeckDetailContent(
    deck: Deck,
    source: Source,
    state: DeckDetailScreenState.Success,
    onBackClick: () -> Unit,
    onStartTraining: (deckId: String) -> Unit,
    onAddCardClick: () -> Unit,
    onEditDeckName: (deckId: String) -> Unit,
    onEditDeckPrivateState: () -> Unit,
    onDeleteDeck: () -> Unit,
    onEditCard: (deckId: String, cardId: Int?) -> Unit,
    onDeleteCard: (Card) -> Unit,
    onRemindClick: (deckName: String) -> Unit,
    onTrainingModeSettingsClick: (String) -> Unit
) {
    var isBottomSheetOpen by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    if (showDeleteDialog) {
        AppAlertDialog(
            title = "Удалить колоду?",
            message = "В случае удаления, колода не сможет быть восстановлена",
            onConfirm = {
                showDeleteDialog = false
                onDeleteDeck()
            },
            onCancel = { showDeleteDialog = false }
        )
    }

    if (showPrivacyDialog) {
        val (title, message) = if (deck.isPublic) {
            "Сделать колоду приватной?" to
                    "После изменения приватности только Вы сможете видеть и использовать эту колоду. Другие пользователи потеряют доступ к ней."
        } else {
            "Опубликовать колоду?" to
                    "После публикации все пользователи смогут видеть содержимое Вашей колоды и запланировать тренировку на ней."
        }
        AppAlertDialog(
            title = title,
            message = message,
            onConfirm = {
                showPrivacyDialog = false
                onEditDeckPrivateState()
            },
            onCancel = { showPrivacyDialog = false }
        )
    }

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = onBackClick,
                showActions = source == Source.LOCAL,
                onRenameDeck = { onEditDeckName(deck.id) },
                onChangePrivacy = { showPrivacyDialog = true },
                onTrainingSettings = { onTrainingModeSettingsClick(deck.id) },
                onDeleteDeck = { showDeleteDialog = true },
                isPublic = deck.isPublic,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            DeckTitle(deck = deck)

            Spacer(modifier = Modifier.height(35.dp))

            DeckInfoRow(deck = deck)

            Spacer(modifier = Modifier.height(66.dp))

            AppElevatedButton(
                title = if (state.nextTrainingTime == null) "Запланировать тренировку"
                else "Открыть план тренировок",
                shouldShowIcon = true,
                iconResId = if (state.nextTrainingTime == null) com.example.common.R.drawable.ic_calendar
                else com.example.common.R.drawable.ic_circleclose,
                onClick = { onRemindClick(deck.name) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(17.dp))

            AppElevatedButton(
                title = "Показать карточки",
                shouldShowIcon = true,
                iconResId = com.example.common.R.drawable.ic_bookmark,
                onClick = { isBottomSheetOpen = true },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            state.nextTrainingTime?.let { nextTrainingTime ->
                TrainingScheduledTime(nextTrainingTime = nextTrainingTime)
            }

            AppButton(
                title = "Начать тренировку",
                shouldShowIcon = true,
                iconResId = com.example.common.R.drawable.ic_play,
                onClick = { onStartTraining(deck.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
                    .height(52.dp)
            )
        }
    }

    val listState = rememberLazyListState()
    var expandedCardId = rememberSaveable { mutableStateOf<Int?>(null) }

    if (isBottomSheetOpen) {
        CardListBottomSheet(
            deck = deck,
            expandedCardId = expandedCardId,
            sheetState = sheetState,
            coroutineScope = coroutineScope,
            listState = listState,
            onEditCard = onEditCard,
            onDeleteCard = onDeleteCard,
            onAddCardClick = onAddCardClick,
            onDismiss = { isBottomSheetOpen = false }
        )
    }

    LaunchedEffect(isBottomSheetOpen) {
        if (isBottomSheetOpen) {
            sheetState.show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardListBottomSheet(
    deck: Deck,
    expandedCardId: MutableState<Int?>,
    sheetState: SheetState,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    onEditCard: (deckId: String, cardId: Int) -> Unit,
    onDeleteCard: (Card) -> Unit,
    onAddCardClick: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (deck.cards.isEmpty()) {
                NoCardsPlaceholder(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            text = "Коснитесь карточки, чтобы увидеть ответ",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(bottom = 7.dp)
                        )
                    }

                    items(deck.cards, key = { it.id }) { card ->
                        var isMenuExpanded = remember { mutableStateOf(false) }

                        ExpandableCardItem(
                            card = card,
                            deckId = deck.id,
                            expandedCardId = expandedCardId,
                            isMenuExpanded = isMenuExpanded,
                            sheetState = sheetState,
                            coroutineScope = coroutineScope,
                            onEditCard = onEditCard,
                            onDeleteCard = onDeleteCard,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }

            AppButton(
                title = "Добавить карточку",
                onClick = {
                    coroutineScope.launch {
                        sheetState.hide()
                        onAddCardClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandableCardItem(
    card: Card,
    deckId: String,
    expandedCardId: MutableState<Int?>,
    isMenuExpanded: MutableState<Boolean>,
    sheetState: SheetState,
    coroutineScope: CoroutineScope,
    onEditCard: (deckId: String, cardId: Int) -> Unit,
    onDeleteCard: (Card) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    expandedCardId.value =
                        if (expandedCardId.value == card.id) null else card.id
                })
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = card.question,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                Box {
                    IconButton(onClick = { isMenuExpanded.value = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Меню"
                        )
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded.value,
                        onDismissRequest = { isMenuExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Изменить") },
                            onClick = {
                                isMenuExpanded.value = false
                                coroutineScope.launch {
                                    sheetState.hide()
                                    onEditCard(deckId, card.id)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Удалить") },
                            onClick = { onDeleteCard(card) }
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expandedCardId.value == card.id) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Ответ:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = card.answer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppAlertDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Подтвердить")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun DeckTitle(deck: Deck) {
    Text(
        text = deck.name,
        style = MaterialTheme.typography.headlineLarge.copy(
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun DeckInfoRow(deck: Deck) {
    Row(
        modifier = Modifier.width(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (deck.isPublic) "Публичная" else "Приватная",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        VerticalDivider(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .height(44.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${deck.cards.size} ${getCardWordForm(deck.cards.size)}",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun TrainingScheduledTime(nextTrainingTime: Long) {
    Text(
        text = "Тренировка запланирована",
        style = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
    Text(
        text = getFormattedTime(nextTrainingTime),
        style = MaterialTheme.typography.titleSmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
    Spacer(modifier = Modifier.height(17.dp))
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