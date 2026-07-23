package com.melodify.musicapp.data.repository

import android.content.Context
import androidx.work.*
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.core.common.Result
import com.melodify.musicapp.core.player.DownloadWorker
import com.melodify.musicapp.data.local.dao.DownloadedSongDao
import com.melodify.musicapp.data.local.entity.DownloadedSongEntity
import com.melodify.musicapp.domain.model.Download
import com.melodify.musicapp.domain.model.DownloadStatus
import com.melodify.musicapp.domain.repository.DownloadRepository
import com.melodify.musicapp.domain.repository.SettingsRepository
import com.melodify.musicapp.domain.repository.SongRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    private val workManager: WorkManager,
    private val downloadedSongDao: DownloadedSongDao,
    private val currentUserProvider: CurrentUserProvider,
    private val settingsRepository: SettingsRepository,
    private val songRepository: SongRepository,   // <-- اضافه شد
    @ApplicationContext private val context: Context
) : DownloadRepository {

    override suspend fun download(songId: String): Result<Unit> {
        val isPremium = settingsRepository.getPremium()
        if (!isPremium) {
            return Result.Error(Exception("Premium subscription required for downloads"))
        }

        val userId = currentUserProvider.getCurrentUser()?.id
            ?: return Result.Error(Exception("User not logged in"))

        // دریافت اطلاعات آهنگ برای دسترسی به audioUrl
        val song = try {
            songRepository.getSong(songId)
        } catch (e: Exception) {
            return Result.Error(Exception("Song not found"))
        }

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(
                workDataOf(
                    "song_id" to songId,
                    "audio_url" to song.audioUrl  // ارسال URL به Worker
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .addTag(songId)
            .build()

        workManager.enqueueUniqueWork(
            "download_$songId",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
        workManager.getWorkInfoByIdLiveData(workRequest.id).observeForever { info ->
            if (info?.state == WorkInfo.State.SUCCEEDED) {
                val path = "${context.filesDir}/songs/$songId.mp3"
                GlobalScope.launch(Dispatchers.IO) {
                    downloadedSongDao.insert(
                        DownloadedSongEntity(
                            songId,
                            path,
                            System.currentTimeMillis()
                        )
                    )
                }
            }
        }

        return Result.Success(Unit)
    }

//    override suspend fun cancel(songId: String) {
//        workManager.cancelAllWorkByTag(songId)
//    }

    override suspend fun cancel(songId: String) {
        workManager.cancelUniqueWork("dl_$songId")
    }

    override suspend fun delete(songId: String) {
        // حذف از دیتابیس و فایل
        downloadedSongDao.delete(DownloadedSongEntity(songId, "", 0))
        // حذف فایل فیزیکی (در صورت وجود)
        val file = java.io.File(context.filesDir, "songs/$songId.mp3")
        if (file.exists()) file.delete()
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
    override fun observeActiveDownloads(): Flow<List<Download>> {
        return workManager.getWorkInfosByTagFlow("download_tag").map { infoList ->
            infoList.filter { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED }
                .map { info ->
                    val progress = info.progress.getInt("progress", 0)
                    val title = info.tags.find { it.startsWith("title_") }?.removePrefix("title_") ?: "Music"
                    val id = info.tags.find { it.startsWith("id_") }?.removePrefix("id_") ?: ""
                    Download(id,  progress, DownloadStatus.DOWNLOADING)
                }
        }
    }

}