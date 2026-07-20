package com.melodify.musicapp.core.common

import com.melodify.musicapp.domain.model.Song

object MockData {
    val songs = (1..60).map { i ->
        Song(
            id = "mock_song_$i",
            title = "Melody #$i",
            artistId = "Artist ${i % 5 + 1}",
            albumId = "Album ${i % 10 + 1}",
            coverUrl = "https://picsum.photos/seed/$i/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-${(i % 10) + 1}.mp3",
            duration = 210000L + (i * 1000),
            genre = listOf("Pop", "Jazz", "Rock", "Classical", "Hip Hop")[i % 5],
            playCount = (1000..99999).random().toLong(),
            isLiked = i % 4 == 0,
            isDownloaded = false
        )
    }
}
