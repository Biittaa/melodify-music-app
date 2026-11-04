package com.melodify.musicapp.core.common

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.melodify.musicapp.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocalMusicScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scanLocalMusic(): List<Song> {
        val songs = mutableListOf<Song>()
        val contentResolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        val cursor = contentResolver.query(uri, null, selection, null, sortOrder)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val albumId = it.getLong(albumIdColumn)
                val duration = it.getLong(durationColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                
                // Mock cover for local songs
                val coverUrl = "https://picsum.photos/seed/$albumId/400/400"

                songs.add(
                    Song(
                        id = "local_$id",
                        title = title,
                        artistId = artist,
                        albumId = albumId.toString(),
                        coverUrl = coverUrl,
                        audioUrl = contentUri.toString(),
                        duration = duration,
                        genre = "Local",
                        playCount = 0,
                        isLiked = false,
                        isDownloaded = true
                    )
                )
            }
        }
        return songs
    }
}
