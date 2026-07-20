package com.melodify.musicapp.core.common

import com.melodify.musicapp.domain.model.Song

object MockData {
    val songs = (1..50).map { i ->
        Song(
            id = "song_$i",
            title = "Track $i",
            artistId = "artist_${i % 5}",
            albumId = "album_${i % 10}",
            coverUrl = "https://picsum.photos/seed/$i/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-${(i % 10) + 1}.mp3",
            duration = 180000L + (i * 1000),
            genre = listOf("Pop", "Rock", "Jazz", "Hip Hop")[i % 4],
            playCount = (1000..50000).random().toLong(),
            isLiked = i % 3 == 0,
            isDownloaded = false
        )
    }
}
