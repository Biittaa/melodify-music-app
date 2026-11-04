package com.melodify.musicapp.data.repository

import com.melodify.musicapp.core.player.IPlayerController
import com.melodify.musicapp.domain.model.PlayerState
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PlayerRepository
 * Delegates all playback operations to IPlayerController
 */
@Singleton
class PlayerRepositoryImpl @Inject constructor(
    private val playerController: IPlayerController
) : PlayerRepository {

    override fun play(song: Song) {
        playerController.play(song)
    }

    override fun playPlaylist(songs: List<Song>, startIndex: Int) {
        playerController.playPlaylist(songs, startIndex)
    }

    override fun pause() {
        playerController.pause()
    }

    override fun stop() {
        playerController.stop()
    }

    override fun next() {
        playerController.next()
    }

    override fun previous() {
        playerController.previous()
    }

    override fun seekTo(position: Long) {
        playerController.seekTo(position)
    }

    override fun setSpeed(speed: Float) {
        playerController.setSpeed(speed)
    }

    override fun toggleShuffle(enable: Boolean?) {
        playerController.toggleShuffle(enable)
    }

    override fun setRepeatMode(mode: Int) {
        playerController.setRepeatMode(mode)
    }

    override fun setSleepTimer(minutes: Int) {
        playerController.setSleepTimer(minutes)
    }

    override fun playerState(): Flow<PlayerState> {
        return playerController.getPlayerState()
    }
}