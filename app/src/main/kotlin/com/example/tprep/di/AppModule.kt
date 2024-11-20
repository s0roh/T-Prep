package com.example.tprep.di

import android.content.Context
import com.example.auth.data.repository.AuthRepositoryImpl
import com.example.auth.domain.repository.AuthRepository
import com.example.decks.data.repository.DeckRepositoryImpl
import com.example.decks.domain.repository.DeckRepository
import com.example.network.api.ApiService
import com.example.preferences.AuthPreferences
import com.example.preferences.AuthPreferencesImpl
import com.example.tprep.BuildConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(okHttpClient: OkHttpClient): ApiService {
            return ApiService(
                baseUrl = BuildConfig.API_BASE_URL,
                okHttpClient = okHttpClient
            )
        }

        @Provides
        @Singleton
        fun provideAuthPreferences(@ApplicationContext context: Context): AuthPreferences {
            return AuthPreferencesImpl(context)
        }
    }
}