package com.example.feature.profile.domain

import com.example.decks.domain.repository.PublicDeckRepository
import javax.inject.Inject

internal class GetFavouriteDecksIdsUseCase @Inject constructor(
    private val repository: PublicDeckRepository,
) {

    suspend operator fun invoke(): List<String> = repository.getFavouriteDeckIds()
}
