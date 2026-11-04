package com.melodify.musicapp.data.local.scanner

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.melodify.musicapp.domain.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaStoreScanner @Inject constructor(
    private val context: Context
) {
    suspend fun scanLocalSongs(): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.IS_MUSIC
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = it.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val dataColumn = it.getColumnIndex(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val album = it.getString(albumColumn)
                val albumId = it.getLong(albumIdColumn)
                val duration = it.getLong(durationColumn)
                val filePath = it.getString(dataColumn)

                // Build cover art URI
                val coverUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )

                val song = Song(
                    id = id.toString(),
                    title = title ?: "Unknown",
                    artistId = artist ?: "Unknown",
                    albumId = albumId.toString(),
                    coverUrl = coverUri.toString(),
                    audioUrl = filePath ?: "",
                    duration = duration,
                    genre = "",
                    playCount = 0,
                    isLiked = false,
                    isDownloaded = false
                )
                songs.add(song)
            }
        }
        songs
    }
}