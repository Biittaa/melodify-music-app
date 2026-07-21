package com.melodify.musicapp.feature.artist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.melodify.musicapp.domain.model.Artist

@Composable
fun ArtistScreen(
    onArtistClick: (Artist) -> Unit,
    viewModel: ArtistViewModel = hiltViewModel()
) {
    val artists by viewModel.artists.collectAsState(emptyList())
    val isLoading by viewModel.isLoading.collectAsState(false)

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(artists) { artist ->  // ← now types are correct
                ArtistItem(artist, onClick = { onArtistClick(artist) })
            }
        }
    }
}

@Composable
fun ArtistItem(artist: Artist, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = artist.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "${artist.followers} followers")
        }
    }
}