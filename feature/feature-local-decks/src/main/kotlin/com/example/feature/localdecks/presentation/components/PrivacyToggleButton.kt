package com.example.feature.localdecks.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun PrivacyToggleButton(
    isPublic: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isPublic) MaterialTheme.colorScheme.primary else Color(0xFFFFA500)
    val textColor = if (isPublic) MaterialTheme.colorScheme.primary else Color(0xFFFFA500)

    OutlinedButton(
        onClick = onToggle,
        modifier = modifier,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Text(
            text = if (isPublic) "Публичная" else "Приватная",
            color = textColor
        )
    }
}