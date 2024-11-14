package com.example.feature_decks.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

internal fun <T> Flow<T>.mergeWith(another: Flow<T>): Flow<T> {
    return merge(this, another)
}