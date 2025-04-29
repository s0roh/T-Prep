package com.example.feature.training.presentation.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.example.feature.training.R

data class HintHelpItem(
    val imageResId: Int,
    val titleResId: Int,
    val descriptionResId: Int,
)

@Composable
internal fun getHintHelpItemsBasedOnTheme(): List<HintHelpItem> {
    return if (isSystemInDarkTheme()) {
        listOf(
            HintHelpItem(
                R.drawable.choice_help_dark,
                R.string.choice_help_title,
                R.string.choice_help_description
            ),
            HintHelpItem(
                R.drawable.true_false_help_dark,
                R.string.true_false_help_title,
                R.string.true_false_help_description
            ),
            HintHelpItem(
                R.drawable.input_help_dark,
                R.string.input_help_title,
                R.string.input_help_description
            )
        )
    } else {
        listOf(
            HintHelpItem(
                R.drawable.choice_help_light,
                R.string.choice_help_title,
                R.string.choice_help_description
            ),
            HintHelpItem(
                R.drawable.true_false_help_light,
                R.string.true_false_help_title,
                R.string.true_false_help_description
            ),
            HintHelpItem(
                R.drawable.input_help_light,
                R.string.input_help_title,
                R.string.input_help_description
            )
        )
    }
}