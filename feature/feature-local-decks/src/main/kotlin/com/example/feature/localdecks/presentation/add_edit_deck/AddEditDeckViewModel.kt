package com.example.feature.localdecks.presentation.add_edit_deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.domain.entity.Deck
import com.example.feature.localdecks.domain.usecase.GetDeckByIdUseCase
import com.example.feature.localdecks.domain.usecase.InsertDeckUseCase
import com.example.feature.localdecks.domain.usecase.UpdateDeckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class AddEditDeckViewModel @Inject constructor(
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val insertDeckUseCase: InsertDeckUseCase,
    private val updateDeckUseCase: UpdateDeckUseCase,
) : ViewModel() {

    var screenState = MutableStateFlow<AddEditDeckScreenState>(AddEditDeckScreenState())
        private set

    var currentDeckId: String? = null
    private var currentDeck: Deck? = null

    fun initDeck() {
        viewModelScope.launch {
            currentDeckId?.also {
                getDeckByIdUseCase(it)?.also { deck ->
                    currentDeck = deck
                    screenState.value = screenState.value.copy(
                        name = deck.name,
                        isPublic = deck.isPublic
                    )
                }
            }
        }
    }

    fun onNameChanged(name: String) {
        screenState.value = screenState.value.copy(name = name, nameError = null)
    }

    fun onPublicStateChanged() {
        screenState.value = screenState.value.copy(isPublic = !screenState.value.isPublic)
    }

    fun saveDeck(): Boolean {
        screenState.value = screenState.value.copy(isSaveButtonEnabled = false)
        val currentState = screenState.value
        if (currentState.name.isBlank()) {
            screenState.value = screenState.value.copy(
                nameError = "Название не может быть пустым",
                isSaveButtonEnabled = true
            )
            return false
        }

        viewModelScope.launch {
            val deck = Deck(
                id = currentDeckId ?: UUID.randomUUID().toString(),
                name = currentState.name.trim(),
                isPublic = currentState.isPublic,
                cards = currentDeck?.cards ?: emptyList()
            )
            if (currentDeckId == null) {
                insertDeckUseCase(deck)
            } else {
                updateDeckUseCase(deck)
            }
        }
        return true
    }
}