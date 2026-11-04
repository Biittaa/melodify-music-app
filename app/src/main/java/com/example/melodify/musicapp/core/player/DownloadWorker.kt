package com.melodify.musicapp.core.player

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val songId = inputData.getString("song_id") ?: return Result.failure()
        val audioUrl = inputData.getString("audio_url") ?: return Result.failure()

        return try {
            val url = URL(audioUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return Result.failure()
            }

            val musicDir = File(applicationContext.filesDir, "songs")
            if (!musicDir.exists()) musicDir.mkdirs()

            val file = File(musicDir, "$songId.mp3")

            url.openStream().use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            Result.success(workDataOf("file_path" to file.absolutePath))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}


