package com.melodify.musicapp.di

import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.domain.model.User
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provides the current user from the provider.
     * Note: This provides the user instance at the time of injection.
     */
    @Provides
    fun provideCurrentUser(currentUserProvider: CurrentUserProvider): User? {
        return currentUserProvider.getCurrentUser()
    }
}