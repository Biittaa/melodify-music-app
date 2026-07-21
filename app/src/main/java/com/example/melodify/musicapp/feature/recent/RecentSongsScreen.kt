package com.melodify.musicapp.feature.recent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.feature.search.SearchResultItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentSongsScreen(
    onBackClick: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    // For now, we'll use an empty list or mock data.
    // You should create a RecentSongsViewModel later to fetch from the DB.
    val recentSongs = remember { emptyList<Song>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recently Played", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            items(recentSongs) { song ->
                SearchResultItem(song = song, onClick = { onSongClick(song) })
            }
        }
    }
}