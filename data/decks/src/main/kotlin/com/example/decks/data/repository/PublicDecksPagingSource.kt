package com.example.decks.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.common.domain.entity.Deck
import com.example.decks.data.mapper.toEntity
import com.example.network.api.ApiService

internal class PublicDecksPagingSource(
    private val apiService: ApiService
) : PagingSource<Int, Deck>() {

    override fun getRefreshKey(state: PagingState<Int, Deck>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Deck> {
        val page = params.key ?: 0

        return try {
            val response = apiService.getPublicDecks(
                count = params.loadSize,
                nextFrom = page * params.loadSize
            )
            LoadResult.Page(
                data = response.decks.map { it.toEntity() },
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.decks.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}