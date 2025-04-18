package com.example.feature.profile.presentation.settings

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.CenteredTopAppBar
import com.example.common.ui.NavigationIconType
import com.example.feature.profile.R

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val screenState by viewModel.screenState.collectAsState()

    Scaffold(
        topBar = {
            CenteredTopAppBar(
                title = stringResource(R.string.settings),
                navigationIconType = NavigationIconType.BACK,
                onNavigationClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingItem(
                title = stringResource(R.string.vibration),
                checked = screenState.isVibrationEnabled,
                onCheckedChange = { viewModel.onVibrationToggle() }
            )

            SettingItem(
                title = stringResource(R.string.sound),
                checked = screenState.isSoundEnabled,
                onCheckedChange = { viewModel.onSoundToggle() }
            )
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange() }
        )
    }
}