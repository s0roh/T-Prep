package com.example.feature.localdecks.presentation.add_edit_deck

data class AddEditDeckScreenState(
    val name: String = "",
    val isPublic: Boolean = false,
    val nameError: String? = null,
    val isSaveButtonEnabled: Boolean = true
)