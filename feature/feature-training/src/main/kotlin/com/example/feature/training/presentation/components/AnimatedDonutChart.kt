package com.example.feature.training.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Данные для сегмента диаграммы.
 */
data class ChartSegment(
    val value: Int,    // Значение сегмента в процентах
    val color: Color,   // Цвет сегмента
)

/**
 * Данные для анимированного сегмента диаграммы.
 */
data class AnimatedSegment(
    val animation: Animatable<Float, AnimationVector1D>, // Анимируемый угол дуги
    val targetAngle: Float,                              // Целевой угол (пропорционален значению)
    val color: Color,                                     // Цвет сегмента
)

/**
 * Анимированная пончиковая диаграмма (donut chart).
 * @param modifier модификатор для настройки размера и расположения
 * @param segments список сегментов диаграммы
 */
@Composable
internal fun AnimatedDonutChart(
    chartSize: Dp = 220.dp,
    strokeWidth: Float = 60f,
    segments: List<ChartSegment>,
    modifier: Modifier = Modifier,
) {
    val totalValue = segments.sumOf { it.value }.toFloat()
    var accumulatedValue = 0f

    // Создание списка анимируемых сегментов
    val animatedSegments = segments.map { segment ->
        accumulatedValue += segment.value
        AnimatedSegment(
            targetAngle = (accumulatedValue / totalValue) * 360f,
            animation = Animatable(0f),
            color = segment.color
        )
    }

    // Запуск анимации
    LaunchedEffect(animatedSegments) {
        animatedSegments.forEach { segment ->
            launch {
                segment.animation.animateTo(
                    targetValue = segment.targetAngle,
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(chartSize)
    ) {
        Canvas(modifier = Modifier.size(chartSize)) {
            val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)

            // Отрисовка дуг диаграммы
            animatedSegments.reversed().forEach { segment ->
                drawArc(
                    color = segment.color,
                    startAngle = -90f, // Начинаем сверху
                    sweepAngle = segment.animation.value,
                    useCenter = false,
                    size = Size(size.width, size.height),
                    style = stroke
                )
            }
        }

        DonutChartContent(value = segments.first().value)
    }
}

/**
 * Функция для отображения текста внутри диаграммы.
 * @param value - значение сегмента, которое будет отображаться в центре диаграммы
 */
@Composable
private fun DonutChartContent(value: Int) {
    val animatedValue = remember { Animatable(0f) }

    LaunchedEffect(value) {
        animatedValue.animateTo(
            targetValue = value.toFloat(),
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Верно решено",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = "${animatedValue.value.toInt()}%",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            fontWeight = FontWeight.Bold
        )
    }
}