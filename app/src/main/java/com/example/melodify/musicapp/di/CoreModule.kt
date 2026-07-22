package com.melodify.musicapp.di

import android.content.Context
import androidx.work.WorkManager
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.core.player.IPlayerController
import com.melodify.musicapp.core.player.PlayerControllerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.melodify.musicapp.core.common.InMemoryUserStore

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    @Binds
    @Singleton
    abstract fun bindPlayerController(impl: PlayerControllerImpl): IPlayerController

    companion object {
        @Provides
        @Singleton
        fun provideCurrentUserProvider(): CurrentUserProvider = CurrentUserProvider()

        @Provides
        @Singleton
        fun provideAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        @Provides
        @Singleton
        fun provideExoPlayer(
            @ApplicationContext context: Context,
            audioAttributes: AudioAttributes,
            cacheDataSourceFactory: CacheDataSource.Factory
        ): ExoPlayer {
            val mediaSourceFactory = DefaultMediaSourceFactory(context)
                .setDataSourceFactory(cacheDataSourceFactory)

            return ExoPlayer.Builder(context)
                .setAudioAttributes(audioAttributes, true)
                .setHandleAudioBecomingNoisy(true)
                .setMediaSourceFactory(mediaSourceFactory)
                .build()
        }

        @Provides
        @Singleton
        fun provideSimpleCache(@ApplicationContext context: Context): SimpleCache {
            val cacheDirectory = File(context.cacheDir, "music_cache")
            val evictor = LeastRecentlyUsedCacheEvictor(500 * 1024 * 1024) // 500MB
            val databaseProvider = StandaloneDatabaseProvider(context)
            return SimpleCache(cacheDirectory, evictor, databaseProvider)
        }

        @Provides
        @Singleton
        fun provideCacheDataSourceFactory(
            @ApplicationContext context: Context,
            cache: SimpleCache
        ): CacheDataSource.Factory {
            return CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        }

        @Provides
        @Singleton
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
            WorkManager.getInstance(context)


        @Provides
        @Singleton
        fun provideInMemoryUserStore(): InMemoryUserStore = InMemoryUserStore()
    }
}