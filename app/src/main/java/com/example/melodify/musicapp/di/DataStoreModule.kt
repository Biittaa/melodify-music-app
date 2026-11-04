package com.melodify.musicapp.di

import android.content.Context
import com.melodify.musicapp.data.local.datastore.SettingsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing DataStore preferences
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * Provides SettingsDataStore instance
     */
    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): SettingsDataStore {
        return SettingsDataStore(context)
    }
}