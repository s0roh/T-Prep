package com.example.feature.decks.presentation.deck_details_statistic

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.ErrorState
import com.example.common.ui.LoadingState
import com.example.common.ui.NavigationIconType
import com.example.feature.decks.R
import com.example.feature.decks.presentation.util.toLabel
import com.example.history.domain.entity.TrainingModeStats
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.StrokeStyle

@Composable
fun DeckDetailsStatisticScreen(
    deckId: String,
    deckName: String,
    onBackClick: () -> Unit,
) {
    val viewModel: DeckDetailStatisticViewModel = hiltViewModel()
    val screenState = viewModel.screenState.collectAsState()

    LaunchedEffect(deckId) {
        viewModel.loadStatistic(deckId = deckId)
    }

    when (val currentState = screenState.value) {
        is DeckDetailsStatisticScreenState.Error -> {
            ErrorState(message = stringResource(R.string.failed_to_load_data))
        }

        is DeckDetailsStatisticScreenState.Loading -> {
            LoadingState()
        }

        is DeckDetailsStatisticScreenState.Success -> {

            DeckDetailsStatisticScreenContent(
                state = currentState,
                deckName = deckName,
                onBackClick = onBackClick
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeckDetailsStatisticScreenContent(
    state: DeckDetailsStatisticScreenState.Success,
    deckName: String,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = stringResource(R.string.deck_statistic),
                subtitle = deckName,
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            if (state.deckTrainingStats.size < 2) {
                Text(
                    text = stringResource(R.string.statistics_warning),
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
                return@Column
            }
            ChartWithDialog(
                chart = { TrainingSuccessLineChart(state.deckTrainingStats) },
                title = stringResource(R.string.training_success_title),
                description = stringResource(R.string.training_success_line_chart_description),
                modifier = Modifier.height(200.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            ChartWithDialog(
                chart = { TrainingModesColumnChart(state.deckTrainingModeStats) },
                title = stringResource(R.string.training_modes_title),
                description = stringResource(R.string.training_modes_column_chart_description),
                modifier = Modifier.height(340.dp)
            )
        }
    }
}

@Composable
private fun ChartWithDialog(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    chart: @Composable () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.W400,
                    fontSize = 20.sp
                )
            )
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.graph_description),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            chart()
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            },
            title = { Text(stringResource(R.string.graph_description)) },
            text = { Text(description) }
        )
    }
}


@Composable
private fun TrainingSuccessLineChart(trainingData: List<Double>) {
    val statisticStartColor = MaterialTheme.colorScheme.primary
    val statisticEndColor = colorResource(id = R.color.color_correct_gradient_end)

    LineChart(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        maxValue = 100.00,
        dividerProperties = DividerProperties(enabled = false),
        gridProperties = GridProperties(
            xAxisProperties = GridProperties.AxisProperties(
                color = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                style = StrokeStyle.Dashed(
                    intervals = floatArrayOf(10f, 10f),
                    phase = 0f
                )
            ),
            yAxisProperties = GridProperties.AxisProperties(
                color = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                style = StrokeStyle.Dashed(
                    intervals = floatArrayOf(10f, 10f),
                    phase = 0f
                )
            )
        ),
        labelHelperProperties = LabelHelperProperties(enabled = false),
        indicatorProperties = HorizontalIndicatorProperties(
            textStyle = TextStyle.Default.copy(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ),
        data = remember {
            listOf(
                Line(
                    label = "Процент успеха",
                    values = trainingData,
                    color = SolidColor(statisticStartColor),
                    firstGradientFillColor = statisticEndColor.copy(alpha = .5f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                    drawStyle = DrawStyle.Stroke(width = 2.dp),
                )
            )
        }
    )
}

@Composable
private fun TrainingModesColumnChart(trainingData: List<TrainingModeStats>) {
    val totalGradientStart = colorResource(id = R.color.color_total_gradient_start)
    val totalGradientEnd = colorResource(id = R.color.color_total_gradient_end)
    val correctGradientStart = colorResource(id = R.color.color_correct_gradient_start)
    val correctGradientEnd = colorResource(id = R.color.color_correct_gradient_end)
    val incorrectGradientStart = colorResource(id = R.color.color_incorrect_gradient_start)
    val incorrectGradientEnd = colorResource(id = R.color.color_incorrect_gradient_end)

    ColumnChart(
        modifier = Modifier
            .height(300.dp)
            .padding(20.dp),
        labelProperties = LabelProperties(
            textStyle = TextStyle.Default.copy(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            enabled = true
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            textStyle = TextStyle.Default.copy(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ),
        dividerProperties = DividerProperties(enabled = false),
        gridProperties = GridProperties(
            xAxisProperties = GridProperties.AxisProperties(
                color = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                style = StrokeStyle.Dashed(
                    intervals = floatArrayOf(10f, 10f),
                    phase = 0f
                )
            ),
            yAxisProperties = GridProperties.AxisProperties(
                color = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                style = StrokeStyle.Dashed(
                    intervals = floatArrayOf(10f, 10f),
                    phase = 0f
                )
            )
        ),
        labelHelperProperties = LabelHelperProperties(
            textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        ),
        data = remember {
            trainingData.map { data ->
                Bars(
                    label = data.modeName.toLabel(),
                    values = listOf(
                        Bars.Data(
                            label = "Всего",
                            value = data.totalAttempts,
                            color = Brush.verticalGradient(
                                colors = listOf(totalGradientStart, totalGradientEnd)
                            )
                        ),
                        Bars.Data(
                            label = "Успешно",
                            value = data.correctAttempts,
                            color = Brush.verticalGradient(
                                colors = listOf(correctGradientStart, correctGradientEnd)
                            )
                        ),
                        Bars.Data(
                            label = "Провалено",
                            value = data.incorrectAttempts,
                            color = Brush.verticalGradient(
                                colors = listOf(incorrectGradientStart, incorrectGradientEnd)
                            )
                        )
                    ),
                )
            }
        },
        barProperties = BarProperties(
            thickness = 15.dp,
            spacing = 6.dp,
            cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
    )
}