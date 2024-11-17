package com.example.t_prep.di

import android.content.Context
import com.example.core_network.api.ApiFactory
import com.example.core_network.api.ApiService
import com.example.core_preferences.AuthPreferences
import com.example.core_preferences.AuthPreferencesImpl
import com.example.data_decks.data.repository.DeckRepositoryImpl
import com.example.data_decks.domain.repository.DeckRepository
import com.example.feature_auth.data.repository.AuthRepositoryImpl
import com.example.feature_auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    @Singleton
    fun bindDeckRepository(deckRepositoryImpl: DeckRepositoryImpl): DeckRepository

    @Binds
    @Singleton
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    companion object {

        @Provides
        @Singleton
        fun provideApiService(): ApiService {
            return ApiFactory.apiService
        }

        @Provides
        @Singleton
        fun provideAuthPreferences(@ApplicationContext context: Context): AuthPreferences {
            return AuthPreferencesImpl(context)
        }
    }
}