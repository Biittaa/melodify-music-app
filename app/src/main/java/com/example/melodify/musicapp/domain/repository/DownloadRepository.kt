package com.melodify.musicapp.domain.repository

import com.melodify.musicapp.domain.model.Download
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    suspend fun download(songId: String)
    suspend fun cancel(songId: String)
    suspend fun delete(songId: String)
    suspend fun getDownloads(): List<Download>
    fun observeDownloads(): Flow<List<Download>>
}