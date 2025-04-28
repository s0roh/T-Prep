package com.example.feature.profile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.feature.profile.R
import com.example.feature.profile.presentation.owner_profile.OwnerProfileScreenState
import com.example.feature.profile.presentation.profile.ProfileScreenState

@Composable
internal fun StatisticsSection(
    modifier: Modifier = Modifier,
    state: ProfileScreenState.Success,
) {
    StatisticsSectionCommon(
        modifier = modifier,
        totalTrainings = state.totalTrainings,
        averageAccuracy = state.averageAccuracy
    )
}

@Composable
internal fun StatisticsSection(
    modifier: Modifier = Modifier,
    state: OwnerProfileScreenState.Success,
) {
    StatisticsSectionCommon(
        modifier = modifier,
        totalTrainings = state.totalTrainings,
        averageAccuracy = state.averageAccuracy
    )
}

@Composable
private fun StatisticsSectionCommon(
    modifier: Modifier = Modifier,
    totalTrainings: Int,
    averageAccuracy: Int,
) {
    Text(
        text = stringResource(R.string.general_statistics),
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
    )
    Spacer(modifier = Modifier.height(15.dp))

    Column(modifier = modifier) {
        StatisticRow(
            label = stringResource(R.string.training_completed),
            value = totalTrainings.toString()
        )

        Spacer(modifier = Modifier.height(9.dp))

        StatisticRow(
            label = stringResource(R.string.percentage_of_correct_answers),
            value = "${averageAccuracy}%"
        )
    }
}
