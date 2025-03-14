package com.example.feature.training.presentation.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.training.R

@Composable
internal fun NextOrSkipButton(
    isAnswered: Boolean,
    onNextCard: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isButtonEnabled by remember { mutableStateOf(true) }

    Button(
        onClick = {
            if (isAnswered) {
                isButtonEnabled = false
                onNextCard()
            } else {
                onSkip()
            }
        },
        modifier = modifier,
        enabled = isButtonEnabled
    ) {
        Text(text = stringResource(if (isAnswered) R.string.next else R.string.skip))
    }
}
