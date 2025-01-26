package com.example.training.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun QuestionArea(question: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Вопрос:",
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = question,
            fontSize = 20.sp,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
    }
}