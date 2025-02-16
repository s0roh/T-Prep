package com.example.training.presentation.training_mode_settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.NavigationIconType
import com.example.database.models.TrainingMode
import com.example.training.domain.entity.TrainingModes
import com.example.training.presentation.util.updateMode

@Composable
fun TrainingModeSettingsScreen(
    deckId: String,
    onBackClick: () -> Unit = {}
) {
    val viewModel: TrainingModeSettingsViewModel = hiltViewModel()
    var trainingModes by remember { mutableStateOf<TrainingModes?>(null) }

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
                title = "Настройки",
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
                    label = "Режим выбора ответов",
                    checked = modes.modes.contains(TrainingMode.MULTIPLE_CHOICE),
                    onCheckedChange = { isChecked ->
                        val updatedModes = updateMode(modes, TrainingMode.MULTIPLE_CHOICE, isChecked)
                        trainingModes = updatedModes
                        viewModel.updateModes(updatedModes)
                    }
                )

                TrainingModeSwitch(
                    label = "Режим истина / ложь",
                    checked = modes.modes.contains(TrainingMode.TRUE_FALSE),
                    onCheckedChange = { isChecked ->
                        val updatedModes = updateMode(modes, TrainingMode.TRUE_FALSE, isChecked)
                        trainingModes = updatedModes
                        viewModel.updateModes(updatedModes)
                    }
                )

                TrainingModeSwitch(
                    label = "Ввод части ответа",
                    checked = modes.modes.contains(TrainingMode.FILL_IN_THE_BLANK),
                    onCheckedChange = { isChecked ->
                        val updatedModes = updateMode(modes, TrainingMode.FILL_IN_THE_BLANK, isChecked)
                        trainingModes = updatedModes
                        viewModel.updateModes(updatedModes)
                    }
                )
            }
        }
    }
}

@Composable
fun TrainingModeSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
