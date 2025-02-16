package com.example.training.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun TrueFalseAnswerSection(displayedAnswer: String?, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Возможный ответ:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = displayedAnswer ?: "Ошибка отображения",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp)
        )
    }
}