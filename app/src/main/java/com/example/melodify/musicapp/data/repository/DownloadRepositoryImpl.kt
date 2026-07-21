package com.melodify.musicapp.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.core.common.Result
import com.melodify.musicapp.data.local.dao.DownloadedSongDao
import com.melodify.musicapp.data.local.entity.DownloadedSongEntity
import com.melodify.musicapp.domain.model.Download
import com.melodify.musicapp.domain.model.DownloadStatus
import com.melodify.musicapp.domain.repository.DownloadRepository
import com.melodify.musicapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    private val workManager: WorkManager,
    private val downloadedSongDao: DownloadedSongDao,
    private val currentUserProvider: CurrentUserProvider,
    private val settingsRepository: SettingsRepository
) : DownloadRepository {

    override suspend fun download(songId: String): Result<Unit> {
        val isPremium = settingsRepository.getPremium()
        if (!isPremium) {
            return Result.Error(Exception("Premium subscription required for downloads"))
        }

        val userId = currentUserProvider.getCurrentUser()?.id ?: return Result.Error(Exception("User not logged in"))

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
        workManager.cancelAllWorkByTag(songId)
    }

    override suspend fun delete(songId: String) {
        downloadedSongDao.delete(DownloadedSongEntity(songId, "", 0))
    }

    override suspend fun getDownloads(): List<Download> {
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

class DownloadWorker(appContext: Context, params: WorkerParameters) : androidx.work.CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        // Implementation logic
        return Result.success()
    }
}