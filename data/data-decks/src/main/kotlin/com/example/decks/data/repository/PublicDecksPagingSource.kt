package com.example.decks.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.common.domain.entity.Deck
import com.example.decks.data.mapper.toEntity
import com.example.decks.domain.entity.PublicDeck
import com.example.network.api.ApiService
import com.example.preferences.AuthRequestWrapper

internal class PublicDecksPagingSource(
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper
) : PagingSource<Int, PublicDeck>() {

    override fun getRefreshKey(state: PagingState<Int, PublicDeck>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PublicDeck> {
        val page = params.key ?: 0

        return try {
            authRequestWrapper.executeWithAuth { token ->
                val response = apiService.getPublicDecksOrSearch(
                    count = params.loadSize,
                    nextFrom = page * params.loadSize,
                    authHeader = token
                )

                val decks = response.decks.orEmpty().map { it.toEntity() }

                LoadResult.Page(
                    data = decks,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (response.count < 10 || decks.isEmpty()) null else page + 1
                )


            }
//            val response = apiService.getPublicDecks(
//                count = params.loadSize,
//                nextFrom = page * params.loadSize
//            )

//            val decks = response.decks.orEmpty().map { it.toEntity() }
//
//            LoadResult.Page(
//                data = decks,
//                prevKey = if (page == 0) null else page - 1,
//                nextKey = if (response.count < 10 || decks.isEmpty()) null else page + 1
//            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}