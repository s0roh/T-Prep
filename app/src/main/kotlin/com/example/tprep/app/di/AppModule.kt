package com.example.tprep.app.di

import android.content.Context
import com.example.auth.data.repository.AuthRepositoryImpl
import com.example.auth.domain.repository.AuthRepository
import com.example.data.profile.data.OwnerProfileRepositoryImpl
import com.example.data.profile.data.ProfileRepositoryImpl
import com.example.data.profile.data.SettingsRepositoryImpl
import com.example.data.profile.domain.repository.OwnerProfileRepository
import com.example.data.profile.domain.repository.ProfileRepository
import com.example.data.profile.domain.repository.SettingsRepository
import com.example.data.reminder.data.repository.ReminderSchedulerImpl
import com.example.data.reminder.data.util.RouteNavigator
import com.example.data.reminder.domain.repository.ReminderScheduler
import com.example.database.TPrepDatabase
import com.example.decks.data.repository.DeckDetailsRepositoryImpl
import com.example.decks.data.repository.PublicDeckRepositoryImpl
import com.example.decks.domain.repository.DeckDetailsRepository
import com.example.decks.domain.repository.PublicDeckRepository
import com.example.history.data.repository.HistoryRepositoryImpl
import com.example.history.domain.repository.HistoryRepository
import com.example.localdecks.data.repository.LocalDeckRepositoryImpl
import com.example.localdecks.data.repository.SyncCardRepositoryImpl
import com.example.localdecks.data.repository.SyncDeckRepositoryImpl
import com.example.localdecks.domain.repository.LocalDeckRepository
import com.example.localdecks.domain.repository.SyncHelper
import com.example.localdecks.data.repository.SyncHelperImpl
import com.example.localdecks.data.repository.SyncUserDataRepositoryImpl
import com.example.localdecks.data.repository.SyncUserMetricsRepositoryImpl
import com.example.localdecks.domain.repository.SyncCardRepository
import com.example.localdecks.domain.repository.SyncDeckRepository
import com.example.localdecks.domain.repository.SyncUserDataRepository
import com.example.localdecks.domain.repository.SyncUserMetricsRepository
import com.example.network.api.ApiService
import com.example.preferences.auth.AuthPreferences
import com.example.preferences.auth.AuthPreferencesImpl
import com.example.preferences.auth.util.AuthRequestWrapper
import com.example.preferences.metrics.MetricsPreferences
import com.example.preferences.metrics.MetricsPreferencesImpl
import com.example.preferences.tooltip.TooltipPreferences
import com.example.preferences.tooltip.TooltipPreferencesImpl
import com.example.tprep.app.utils.reminder.AppRouteNavigator
import com.example.tprep.app.utils.getApiBaseUrl
import com.example.training.data.repository.TrainingRepositoryImpl
import com.example.training.domain.repository.TrainingRepository
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
    @Suppress("unused")
    fun bindPublicDeckRepository(publicDeckRepositoryImpl: PublicDeckRepositoryImpl): PublicDeckRepository

    @Binds
    @Singleton
    @Suppress("unused")
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    @Suppress("unused")
    fun bindHistoryRepository(historyRepositoryImpl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    @Singleton
    @Suppress("unused")
    fun bindSyncCardRepository(syncCardRepositoryImpl: SyncCardRepositoryImpl): SyncCardRepository

    @Binds
    @Singleton
    @Suppress("unused")
    fun bindSyncDeckRepository(syncDeckRepositoryImpl: SyncDeckRepositoryImpl): SyncDeckRepository

    @Binds
    @Singleton
    @Suppress("unused")
    fun bindSyncUserMetricsRepository(syncUserMetricsRepositoryImpl: SyncUserMetricsRepositoryImpl): SyncUserMetricsRepository

    @Binds
    @Singleton
    @Suppress("unused")
    fun bindDeckDetailsRepository(deckDetailsRepositoryImpl: DeckDetailsRepositoryImpl): DeckDetailsRepository

    @Binds
    @Singleton
    @Suppress("unused")
    fun bindSyncHelper(syncHelperImpl: SyncHelperImpl): SyncHelper

    @Binds
    @Singleton
    @Suppress("unused")
    fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    companion object {

        @Provides
        @Singleton
        fun provideTPrepDatabase(
            @ApplicationContext context: Context,
        ): TPrepDatabase {
            return TPrepDatabase(context)
        }

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
        fun provideApiService(
            @ApplicationContext context: Context,
            okHttpClient: OkHttpClient,
        ): ApiService {
            val apiBaseUrl = getApiBaseUrl(context)
            return ApiService(
                baseUrl = apiBaseUrl,
                okHttpClient = okHttpClient
            )
        }

        @Provides
        @Singleton
        fun provideAuthPreferences(@ApplicationContext context: Context): AuthPreferences {
            return AuthPreferencesImpl(context)
        }

        @Provides
        @Singleton
        fun provideTooltipPreferences(@ApplicationContext context: Context): TooltipPreferences {
            return TooltipPreferencesImpl(context)
        }

        @Provides
        @Singleton
        fun provideMetricsPreferences(@ApplicationContext context: Context): MetricsPreferences {
            return MetricsPreferencesImpl(context)
        }

        @Provides
        @Singleton
        fun provideScreenNavigator(@ApplicationContext context: Context): RouteNavigator {
            return AppRouteNavigator(context)
        }

        @Provides
        @Singleton
        fun provideReminderScheduler(
            @ApplicationContext context: Context,
            database: TPrepDatabase,
            apiService: ApiService,
            authRequestWrapper: AuthRequestWrapper,
        ): ReminderScheduler {
            return ReminderSchedulerImpl(context, database, apiService, authRequestWrapper)
        }

        @Provides
        @Singleton
        fun provideProfileRepository(
            @ApplicationContext context: Context,
            apiService: ApiService,
            preferences: AuthPreferences,
            authRequestWrapper: AuthRequestWrapper,
        ): ProfileRepository {
            return ProfileRepositoryImpl(context, apiService, preferences, authRequestWrapper)
        }

        @Provides
        @Singleton
        fun provideLocalDeckRepository(
            @ApplicationContext context: Context,
            database: TPrepDatabase,
            syncHelper: SyncHelper,
            preferences: AuthPreferences,
        ): LocalDeckRepository {
            return LocalDeckRepositoryImpl(
                context,
                database,
                syncHelper,
                preferences
            )
        }

        @Provides
        @Singleton
        fun provideSyncUserDataRepository(
            @ApplicationContext context: Context,
            database: TPrepDatabase,
            apiService: ApiService,
            authRequestWrapper: AuthRequestWrapper,
            preferences: AuthPreferences,
        ): SyncUserDataRepository {
            return SyncUserDataRepositoryImpl(
                context,
                database,
                apiService,
                authRequestWrapper,
                preferences
            )
        }

        @Provides
        @Singleton
        fun provideOwnerProfileRepository(
            @ApplicationContext context: Context,
            apiService: ApiService,
            authRequestWrapper: AuthRequestWrapper,
        ): OwnerProfileRepository {
            return OwnerProfileRepositoryImpl(
                context,
                apiService,
                authRequestWrapper
            )
        }

        @Provides
        @Singleton
        fun provideTrainingRepository(
            @ApplicationContext context: Context,
            database: TPrepDatabase,
            apiService: ApiService,
            preferences: AuthPreferences,
            authRequestWrapper: AuthRequestWrapper,
        ): TrainingRepository {
            return TrainingRepositoryImpl(
                context,
                database,
                apiService,
                preferences,
                authRequestWrapper
            )
        }
    }
}