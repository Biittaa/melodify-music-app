package com.melodify.musicapp.di

import com.melodify.musicapp.core.common.CurrentUserProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing core/common utility classes
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    /**
     * Provides CurrentUserProvider as a singleton
     * Used to cache the currently logged-in user in memory
     */
    @Provides
    @Singleton
    fun provideCurrentUserProvider(): CurrentUserProvider {
        return CurrentUserProvider()
    }
}