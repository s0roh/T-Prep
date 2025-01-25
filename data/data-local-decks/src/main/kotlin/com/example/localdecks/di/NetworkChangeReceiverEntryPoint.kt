package com.example.localdecks.di

import com.example.database.TPrepDatabase
import com.example.network.api.ApiService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NetworkChangeReceiverEntryPoint {

    fun getTPrepDatabase(): TPrepDatabase

    fun getApiService(): ApiService
}

