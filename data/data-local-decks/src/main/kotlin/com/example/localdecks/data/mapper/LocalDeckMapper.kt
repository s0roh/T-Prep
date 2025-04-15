package com.example.localdecks.data.mapper

import com.example.common.domain.entity.Card
import com.example.common.domain.entity.Deck
import com.example.common.ui.entity.DeckUiModel
import com.example.database.models.CardDBO
import com.example.database.models.DeckDBO
import com.example.localdecks.domain.entity.CardRequest
import com.example.localdecks.domain.entity.DeckRequest
import com.example.network.dto.collection.CardRequestDto
import com.example.network.dto.collection.DeckRequestDto
import com.example.network.dto.global.OtherAnswersDto

internal fun DeckDBO.toEntity(cards: List<Card>): Deck = Deck(
    id = id,
    name = name,
    isPublic = isPublic,
    cards = cards
)

internal fun DeckDBO.toUiModel(cardsCount: Int): DeckUiModel = DeckUiModel(
    id = id,
    name = name,
    isPublic = isPublic,
    cardsCount = cardsCount,
    isLiked = false,
    likes = 0,
    trainings = 0,
    shouldShowLikes = false
)

internal fun CardDBO.toEntity(): Card = Card(
    id = id,
    question = question,
    answer = answer,
    wrongAnswers = listOfNotNull(wrongAnswer1, wrongAnswer2, wrongAnswer3),
    attachment = attachment,
    picturePath = picturePath
)

internal fun Deck.toDBO(serverDeckId: String?, userId: String): DeckDBO = DeckDBO(
    id = id,
    serverDeckId = serverDeckId,
    name = name,
    isPublic = isPublic,
    userId = userId
)

internal fun Card.toDBO(deckId: String, serverCardId: Int?): CardDBO = CardDBO(
    id = id,
    serverCardId = serverCardId,
    deckId = deckId,
    question = question,
    answer = answer,
    wrongAnswer1 = wrongAnswers.getOrNull(0),
    wrongAnswer2 = wrongAnswers.getOrNull(1),
    wrongAnswer3 = wrongAnswers.getOrNull(2),
    attachment = attachment,
    picturePath = picturePath
)

internal fun DeckRequest.toDTO() = DeckRequestDto(
    name = name,
    isPublic = isPublic
)

internal fun CardRequest.toDTO() = CardRequestDto(
    question = question,
    answer = answer,
    otherAnswers = OtherAnswersDto(
        count = wrongAnswers.size,
        items = wrongAnswers
    )
)
