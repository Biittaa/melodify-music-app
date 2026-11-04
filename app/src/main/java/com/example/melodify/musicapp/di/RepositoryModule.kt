package com.melodify.musicapp.di

import com.melodify.musicapp.data.repository.AlbumRepositoryImpl
import com.melodify.musicapp.data.repository.ArtistRepositoryImpl
import com.melodify.musicapp.data.repository.AuthRepositoryImpl
import com.melodify.musicapp.data.repository.ChatRepositoryImpl
import com.melodify.musicapp.data.repository.DownloadRepositoryImpl
import com.melodify.musicapp.data.repository.PlayerRepositoryImpl
import com.melodify.musicapp.data.repository.PlaylistRepositoryImpl
import com.melodify.musicapp.data.repository.SearchRepositoryImpl
import com.melodify.musicapp.data.repository.SettingsRepositoryImpl
import com.melodify.musicapp.data.repository.SongRepositoryImpl
import com.melodify.musicapp.data.repository.UserRepositoryImpl
import com.melodify.musicapp.domain.repository.AlbumRepository
import com.melodify.musicapp.domain.repository.ArtistRepository
import com.melodify.musicapp.domain.repository.AuthRepository
import com.melodify.musicapp.domain.repository.ChatRepository
import com.melodify.musicapp.domain.repository.DownloadRepository
import com.melodify.musicapp.domain.repository.PlayerRepository
import com.melodify.musicapp.domain.repository.PlaylistRepository
import com.melodify.musicapp.domain.repository.SearchRepository
import com.melodify.musicapp.domain.repository.SettingsRepository
import com.melodify.musicapp.domain.repository.SongRepository
import com.melodify.musicapp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for binding repository interfaces to their implementations
 * All repositories are bound as singletons
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindSongRepository(impl: SongRepositoryImpl): SongRepository

    @Binds
    abstract fun bindAlbumRepository(impl: AlbumRepositoryImpl): AlbumRepository

    @Binds
    abstract fun bindArtistRepository(impl: ArtistRepositoryImpl): ArtistRepository

    @Binds
    abstract fun bindPlaylistRepository(impl: PlaylistRepositoryImpl): PlaylistRepository

    @Binds
    abstract fun bindPlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository

    @Binds
    abstract fun bindDownloadRepository(impl: DownloadRepositoryImpl): DownloadRepository

    @Binds
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}