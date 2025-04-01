package com.example.decks.data.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.decks.data.mapper.toEntity
import com.example.common.ui.entity.DeckUiModel
import com.example.network.api.ApiService
import com.example.preferences.AuthRequestWrapper

internal class PublicDecksPagingSource(
    private val apiService: ApiService,
    private val authRequestWrapper: AuthRequestWrapper,
    private val query: String? = null,
    private val sortBy: String? = null,
    private val category: String? = null,
) : PagingSource<Int, DeckUiModel>() {

    override fun getRefreshKey(state: PagingState<Int, DeckUiModel>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DeckUiModel> {
        val page = params.key ?: 0

        return try {
            authRequestWrapper.executeWithAuth { token ->
                Log.d("!@#", "Making API request with token: $token")

                val publicDecksResponse = apiService.getPublicDecksOrSearch(
                    name = query,
                    sortBy = sortBy,
                    category = category,
                    count = params.loadSize,
                    nextFrom = page * params.loadSize,
                    authHeader = token
                )

                val userInfoResponse = apiService.getUserInfo(authHeader = token)

                if (!userInfoResponse.isSuccessful) {
                    throw Exception("Failed to load profile info: ${userInfoResponse.code()}")
                }

                val favouriteDecksIds = userInfoResponse.body()?.favourite?.toSet()
                    ?: emptySet()

                val decks = publicDecksResponse.decks.orEmpty().map {deckDto ->
                    deckDto.toEntity(isLiked = favouriteDecksIds.contains(deckDto.id) )
                }

                LoadResult.Page(
                    data = decks,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (publicDecksResponse.count < 10 || decks.isEmpty()) null else page + 1
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}