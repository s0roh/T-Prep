package com.example.feature.training.presentation.training_mode_settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.NavigationIconType
import com.example.database.models.TrainingMode
import com.example.feature.training.R
import com.example.feature.training.presentation.components.PagerIndicator
import com.example.feature.training.presentation.components.TrainingModeSwitch
import com.example.feature.training.presentation.util.HintHelpItem
import com.example.feature.training.presentation.util.getHintHelpItemsBasedOnTheme
import com.example.feature.training.presentation.util.updateMode
import com.example.training.domain.entity.TrainingModes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingModeSettingsScreen(
    deckId: String,
    onBackClick: () -> Unit = {},
) {
    val viewModel: TrainingModeSettingsViewModel = hiltViewModel()
    var trainingModes by remember { mutableStateOf<TrainingModes?>(null) }

    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(deckId) {
        trainingModes = viewModel.loadModeSettings(deckId)
    }

    BackHandler {
        viewModel.saveModeSettings()
        onBackClick()
    }

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = stringResource(R.string.settings),
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = {
                    viewModel.saveModeSettings()
                    onBackClick()
                }
            )
        }
    ) { paddingValues ->
        trainingModes?.let { modes ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                TrainingModeSwitch(
                    label = stringResource(R.string.answer_selection_mode),
                    checked = modes.modes.contains(TrainingMode.MULTIPLE_CHOICE),
                    onCheckedChange = { isChecked ->
                        val updatedModes =
                            updateMode(modes, TrainingMode.MULTIPLE_CHOICE, isChecked)
                        trainingModes = updatedModes
                        viewModel.updateModes(updatedModes)
                    }
                )

                TrainingModeSwitch(
                    label = stringResource(R.string.true_false_mode),
                    checked = modes.modes.contains(TrainingMode.TRUE_FALSE),
                    onCheckedChange = { isChecked ->
                        val updatedModes = updateMode(modes, TrainingMode.TRUE_FALSE, isChecked)
                        trainingModes = updatedModes
                        viewModel.updateModes(updatedModes)
                    }
                )

                TrainingModeSwitch(
                    label = stringResource(R.string.part_of_answer_mode),
                    checked = modes.modes.contains(TrainingMode.FILL_IN_THE_BLANK),
                    onCheckedChange = { isChecked ->
                        val updatedModes =
                            updateMode(modes, TrainingMode.FILL_IN_THE_BLANK, isChecked)
                        trainingModes = updatedModes
                        viewModel.updateModes(updatedModes)
                    }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(MaterialTheme.shapes.large)
                        .clickable { isBottomSheetOpen = true }
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = stringResource(R.string.about_train_modes),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.about_train_modes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }
        }
    }

    if (isBottomSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isBottomSheetOpen = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 8.dp,
            dragHandle = {}
        ) {
            HintsCarousel(hints = getHintHelpItemsBasedOnTheme())
        }
    }
}

@Composable
fun HintsCarousel(hints: List<HintHelpItem>) {
    val pagerState = rememberPagerState(pageCount = { hints.size })
    val maxHintHeightDp = remember { mutableStateOf<Dp?>(null) }
    val density = LocalDensity.current

    Column {
        HorizontalPager(state = pagerState) { page ->
            val hint = hints[page]

            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        if (maxHintHeightDp.value == null) {
                            maxHintHeightDp.value = with(density) {
                                coordinates.size.height.toDp()
                            }
                        }
                    }
                    .height(maxHintHeightDp.value ?: Dp.Unspecified)
                    .fillMaxWidth()
            ) {
                HintCard(
                    imageResId = hint.imageResId,
                    titleResId = hint.titleResId,
                    descriptionResId = hint.descriptionResId
                )
            }
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }.collect { currentPage ->
                    pagerState.animateScrollToPage(currentPage)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PagerIndicator(
            pageCount = pagerState.pageCount,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
 private fun HintCard(
    imageResId: Int,
    titleResId: Int,
    descriptionResId: Int,
) {
    Column {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = stringResource(id = titleResId),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 35.dp)
        )
        Text(
            text = stringResource(id = titleResId),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.W600,
                fontSize = 18.sp
            ),
            modifier = Modifier.padding(horizontal = 28.dp)
        )
        Spacer(modifier = Modifier.height(11.dp))
        Text(
            text = stringResource(id = descriptionResId),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.W400,
                fontSize = 14.sp
            ),
            modifier = Modifier.padding(horizontal = 28.dp)
        )
    }
}