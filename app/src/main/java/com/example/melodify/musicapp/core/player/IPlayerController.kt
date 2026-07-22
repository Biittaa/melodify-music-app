package com.melodify.musicapp.core.player

import com.melodify.musicapp.domain.model.PlayerState
import com.melodify.musicapp.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface IPlayerController {
    fun play(song: Song)
    fun playPlaylist(songs: List<Song>, startIndex: Int)
    fun pause()
    fun stop()
    fun next()
    fun previous()
    fun seekTo(position: Long)
    fun setSpeed(speed: Float)
    fun toggleShuffle(enable: Boolean? = null)
    fun setRepeatMode(mode: Int)
    fun setSleepTimer(minutes: Int)
    fun getPlayerState(): Flow<PlayerState>
}