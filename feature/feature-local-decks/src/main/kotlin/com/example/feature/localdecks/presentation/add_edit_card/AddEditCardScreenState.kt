package com.example.feature.localdecks.presentation.add_edit_card

data class AddEditCardScreenState(
    val question: String = "",
    val answer: String = "",
    val questionError: String? = null,
    val answerError: String? = null,
    val isSaveButtonEnabled: Boolean = true
)