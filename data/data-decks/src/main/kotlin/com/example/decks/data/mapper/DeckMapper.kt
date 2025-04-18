package com.example.decks.data.mapper

import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.common.ui.entity.DeckUiModel
import com.example.database.models.CardDBO
import com.example.database.models.DeckDBO
import com.example.network.dto.global.CardDto
import com.example.network.dto.collection.DeckDto
import com.example.network.dto.collection.PublicDeckDto

internal fun DeckDto.toEntity(): Deck =
    Deck(
        id = id,
        name = name,
        isPublic = isPublic,
        authorId = authorId,
        cards = cards.map { it.toEntity() }
    )

internal fun PublicDeckDto.toEntity(isLiked: Boolean): DeckUiModel =
    DeckUiModel(
        id = id,
        name = name,
        isPublic = isPublic,
        cardsCount = cardsCount,
        likes = likes,
        trainings = trainings,
        isLiked = isLiked,
        shouldShowLikes = true
    )

internal fun CardDto.toEntity(): Card =
    Card(
        id = id,
        question = question,
        answer = answer,
        wrongAnswers = otherAnswers.items,
        attachment = attachment,
    )

internal fun CardDBO.toEntity(): Card =
    Card(
        id = id,
        question = question,
        answer = answer,
        wrongAnswers = listOfNotNull(wrongAnswer1, wrongAnswer2, wrongAnswer3),
        attachment =attachment,
        picturePath = picturePath
    )

internal fun DeckDBO.toEntity(cards: List<Card>): Deck =
    Deck(
        id = id,
        name = name,
        isPublic = isPublic,
        cards = cards
    )


