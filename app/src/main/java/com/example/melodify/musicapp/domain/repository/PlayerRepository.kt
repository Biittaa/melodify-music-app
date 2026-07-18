package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.PlayerState
import com.melodify.musicapp.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun play(song: Song)
    fun pause()
    fun stop()
    fun next()
    fun previous()
    fun seekTo(position: Long)
    fun setSpeed(speed: Float)       // 1.0x , 1.5x , 2x
    fun toggleShuffle()
    fun setRepeatMode(mode: Int)     // 0: Off, 1: Repeat All, 2: Repeat One
    fun playerState(): Flow<PlayerState>
}