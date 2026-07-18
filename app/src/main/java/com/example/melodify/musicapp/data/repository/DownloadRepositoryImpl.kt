package com.melodify.musicapp.data.repository

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.data.local.dao.DownloadedSongDao
import com.melodify.musicapp.data.local.entity.DownloadedSongEntity
import com.melodify.musicapp.domain.model.Download
import com.melodify.musicapp.domain.model.DownloadStatus
import com.melodify.musicapp.domain.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    private val workManager: WorkManager,
    private val downloadedSongDao: DownloadedSongDao,
    private val currentUserProvider: CurrentUserProvider
) : DownloadRepository {

    override suspend fun download(songId: String) {
        val userId = currentUserProvider.getCurrentUser()?.id ?: return
        val workRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .setInputData(
                androidx.work.Data.Builder()
                    .putString("song_id", songId)
                    .putString("user_id", userId)
                    .build()
            )
            .build()
        workManager.enqueue(workRequest)
    }

    override suspend fun cancel(songId: String) {
        // cancel related WorkerManager
        workManager.cancelAllWorkByTag(songId)
    }

    override suspend fun delete(songId: String) {
        // remove file and record from Room
        downloadedSongDao.delete(DownloadedSongEntity(songId, "", 0))
    }

    override suspend fun getDownloads(): List<Download> {
        // recieve from Room
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

class DownloadWorker : androidx.work.CoroutineWorker() {
}