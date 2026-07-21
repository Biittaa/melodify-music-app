package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.PlayerState
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for media playback control
 * Handles all player operations: play, pause, seek, speed, shuffle, repeat
 * This is primarily implemented by Player Team Member (Person #2)
 */
interface PlayerRepository {

    /**
     * Play a specific song
     * @param song Song to be played
     */
    fun play(song: Song)

    /**
     * Pause the current playback
     */
    fun pause()

    /**
     * Stop the current playback
     */
    fun stop()

    /**
     * Play the next song in the queue
     */
    fun next()

    /**
     * Play the previous song in the queue
     */
    fun previous()

    /**
     * Seek to a specific position in the current song
     * @param position Position in milliseconds
     */
    fun seekTo(position: Long)

    /**
     * Set playback speed
     * @param speed Speed multiplier (e.g., 1.0f, 1.5f, 2.0f)
     */
    fun setSpeed(speed: Float)

    /**
     * Toggle shuffle mode on/off
     */
    fun toggleShuffle()

    /**
     * Set repeat mode
     * @param mode 0 = Off, 1 = Repeat All, 2 = Repeat One
     */
    fun setRepeatMode(mode: Int)

    /**
     * Get the current player state as a reactive stream
     * @return Flow emitting PlayerState updates
     */
    fun playerState(): Flow<PlayerState>
}