package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.core.common.Result
import com.melodify.musicapp.domain.model.Download
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for offline song downloads
 * Handles download requests, cancellation, deletion, and status observation
 */
interface DownloadRepository {

    /**
     * Start downloading a song for offline playback
     * @param songId ID of the song to download
     * @return Result indicating success or failure of the request initiation
     */
    suspend fun download(songId: String): Result<Unit>

    /**
     * Cancel an ongoing download
     * @param songId ID of the song being downloaded
     */
    suspend fun cancel(songId: String)

    /**
     * Delete a downloaded song from local storage
     * @param songId ID of the song to delete
     */
    suspend fun delete(songId: String)

    /**
     * Get all downloaded songs list (one-time snapshot)
     * @return List of Download objects
     */
    suspend fun getDownloads(): List<Download>

    /**
     * Observe download status changes in real-time
     * @return Flow emitting list of Download updates
     */
    fun observeDownloads(): Flow<List<Download>>

    fun observeActiveDownloads(): Flow<List<Download>>


}