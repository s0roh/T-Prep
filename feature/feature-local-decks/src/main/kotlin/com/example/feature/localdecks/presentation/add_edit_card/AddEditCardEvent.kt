package com.example.feature.localdecks.presentation.add_edit_card

sealed class AddEditCardEvent {
    data class ShowError(val message: String) : AddEditCardEvent()
}