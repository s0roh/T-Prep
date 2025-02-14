package com.example.training.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun QuestionArea(question: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.titleMedium
        )
    }
}