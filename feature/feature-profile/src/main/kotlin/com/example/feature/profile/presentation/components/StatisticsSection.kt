package com.example.feature.profile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.feature.profile.presentation.profile.ProfileScreenState

@Composable
internal fun StatisticsSection(currentState: ProfileScreenState.Success) {
    Text(
        text = "Общая статистика",
        style = MaterialTheme.typography.titleLarge,
    )
    Spacer(modifier = Modifier.height(15.dp))

    Column {
        StatisticRow(
            label = "Тренировок завершено",
            value = currentState.totalTrainings.toString()
        )

        Spacer(modifier = Modifier.height(9.dp))

        StatisticRow(
            label = "Процент правильных ответов",
            value = "${currentState.averageAccuracy}%"
        )
    }
}