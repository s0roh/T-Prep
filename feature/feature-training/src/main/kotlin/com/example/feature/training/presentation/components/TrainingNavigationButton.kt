package com.example.feature.training.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.feature.training.R

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
        isAnswered -> stringResource(R.string.next)
        onSubmit != null && userInput.isNotBlank() -> stringResource(R.string.check_answer)
        else -> stringResource(R.string.skip)
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