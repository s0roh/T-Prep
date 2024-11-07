package com.example.t_prep.data.mapper

import com.example.t_prep.data.network.dto.global.CardDto
import com.example.t_prep.data.network.dto.global.DeckDto
import com.example.t_prep.domain.entity.Card
import com.example.t_prep.domain.entity.Deck

fun DeckDto.toEntity(): Deck = Deck(
    id = id,
    name = name,
    isPublic = isPublic,
    cards = cards.map { it.toEntity() }
)

fun CardDto.toEntity(): Card = Card(
    id = this.id,
    question = this.question,
    answer = this.answer
)