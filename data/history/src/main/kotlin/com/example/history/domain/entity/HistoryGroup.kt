package com.example.history.domain.entity

data class HistoryGroup(
    val timePeriod: TimePeriod,
    val decks: List<DeckHistory>
) {
    data class DeckHistory(
        val deckId: Long,
        val deckName: String,
        val cardsCount: Int
    )
}