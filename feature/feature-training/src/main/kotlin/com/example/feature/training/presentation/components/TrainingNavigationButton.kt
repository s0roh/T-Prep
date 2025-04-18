package com.example.feature.training.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun TrainingNavigationButton(
    modifier: Modifier = Modifier,
    isAnswered: Boolean,
    userInput: String = "",
    isButtonEnabled: Boolean = true,
    onNextCard: () -> Unit,
    onSkip: () -> Unit,
    onSubmit: (() -> Unit)? = null,
) {
    val buttonText = when {
        isAnswered -> "Далее"
        onSubmit != null && userInput.isNotBlank() -> "Проверить ответ"
        else -> "Пропустить"
    }

    Button(
        onClick = {
            when {
                isAnswered -> onNextCard()
                onSubmit != null && userInput.isNotBlank() -> onSubmit()
                else -> onSkip()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        enabled = isButtonEnabled
    ) {
        Text(text = buttonText)
    }
}