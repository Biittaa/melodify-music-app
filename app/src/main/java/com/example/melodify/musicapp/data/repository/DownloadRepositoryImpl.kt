package com.melodify.musicapp.data.repository

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.data.local.dao.DownloadedSongDao
import com.melodify.musicapp.data.local.entity.DownloadedSongEntity
import com.melodify.musicapp.domain.model.Download
import com.melodify.musicapp.domain.model.DownloadStatus
import com.melodify.musicapp.domain.repository.DownloadRepository
import com.melodify.musicapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of DownloadRepository
 * Uses WorkManager for background downloads
 * DownloadWorker is implemented by Person #2 (Player Module)
 */
@Singleton
class DownloadRepositoryImpl @Inject constructor(
    private val workManager: WorkManager,
    private val downloadedSongDao: DownloadedSongDao,
    private val currentUserProvider: CurrentUserProvider
) : DownloadRepository {

    override suspend fun download(songId: String): Result<Unit> {
        // Check if user is premium
        val isPremium = settingsRepository.getPremium()
        if (!isPremium) {
            return Result.Error(Exception("Premium subscription required for downloads"))
        }

        val userId = currentUserProvider.getCurrentUser()?.id ?: return Result.Error(Exception("User not logged in"))

        // Create WorkManager request
        val workRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .setInputData(
                Data.Builder()
                    .putString("song_id", songId)
                    .putString("user_id", userId)
                    .build()
            )
            .addTag(songId)
            .build()
        workManager.enqueue(workRequest)
        return Result.Success(Unit)
    }
    override suspend fun cancel(songId: String) {
        // Cancel the corresponding WorkManager job
        workManager.cancelAllWorkByTag(songId)
    }

    override suspend fun delete(songId: String) {
        // Delete file and Room record
        downloadedSongDao.delete(DownloadedSongEntity(songId, "", 0))
    }

    override suspend fun getDownloads(): List<Download> {
        // Fetch from Room
        val entities = downloadedSongDao.getAll().firstOrNull() ?: emptyList()
        return entities.map { entity ->
            Download(
                songId = entity.songId,
                progress = 100,
                status = DownloadStatus.COMPLETED
            )
        }
    }

    override fun observeDownloads(): Flow<List<Download>> {
        return downloadedSongDao.getAll().map { entities ->
            entities.map { entity ->
                Download(
                    songId = entity.songId,
                    progress = 100,
                    status = DownloadStatus.COMPLETED
                )
            }
        }
    }
}

/**
 * DownloadWorker - Implemented by Person #2 (Player Module)
 * Handles actual file download in background
 */
class DownloadWorker : androidx.work.CoroutineWorker() {
    // Implementation by Person #2
}