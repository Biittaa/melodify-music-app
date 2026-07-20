package com.melodify.musicapp.core.player

import android.content.Context
import androidx.work.*
import com.melodify.musicapp.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MelodifyDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun downloadSong(song: Song, isPremium: Boolean): String {
        if (!isPremium) {
            return "Premium required for downloads"
        }

        if (isDownloaded(song.id)) {
            return "Already downloaded"
        }

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workDataOf(
                "song_id" to song.id,
                "audio_url" to song.audioUrl
            ))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "download_${song.id}",
            ExistingWorkPolicy.KEEP,
            downloadRequest
        )
        
        return "Download started..."
    }

    fun isDownloaded(songId: String): Boolean {
        val file = File(context.filesDir, "songs/$songId.mp3")
        return file.exists()
    }

    fun getLocalFileUri(songId: String): String? {
        val file = File(context.filesDir, "songs/$songId.mp3")
        return if (file.exists()) file.absolutePath else null
    }

    fun deleteDownload(songId: String): Boolean {
        val file = File(context.filesDir, "songs/$songId.mp3")
        return if (file.exists()) file.delete() else false
    }
}
