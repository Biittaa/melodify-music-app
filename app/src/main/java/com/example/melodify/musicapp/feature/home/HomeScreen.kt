package com.melodify.musicapp.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.melodify.musicapp.R
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.ui.components.shimmerEffect

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onSongClick: (Song) -> Unit,
    onQuickActionClick: (String) -> Unit,
    onSeeAllClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        if (uiState.isLoading) {
            item { ShimmerCarousel() }
            item { QuickActionsSection(onQuickActionClick) }
            items(2) { ShimmerSection() }
        } else {
            item { HomeCarousel(songs = uiState.trendingSongs.take(5), onSongClick = onSongClick) }
            item { QuickActionsSection(onQuickActionClick) }
            item { 
                SongSection(
                    title = stringResource(R.string.new_releases), 
                    songs = uiState.newestSongs, 
                    onSongClick = onSongClick,
                    onSeeAllClick = { onSeeAllClick("New Releases") }
                ) 
            }
            item { 
                SongSection(
                    title = stringResource(R.string.trending), 
                    songs = uiState.trendingSongs, 
                    onSongClick = onSongClick,
                    onSeeAllClick = { onSeeAllClick("Trending") }
                ) 
            }

            // Local Music Section
            val localSongs = uiState.trendingSongs.filter { it.id.startsWith("local_") }
            if (localSongs.isNotEmpty()) {
                item {
                    SongSection(
                        title = "My Local Music", 
                        songs = localSongs, 
                        onSongClick = onSongClick,
                        onSeeAllClick = { onSeeAllClick("My Local Music") }
                    )
                }
            }
        }
    }
}

@Composable
fun ShimmerCarousel() {
    Box(modifier = Modifier.fillMaxWidth().height(220.dp).padding(16.dp).clip(RoundedCornerShape(16.dp)).shimmerEffect())
}

@Composable
fun ShimmerSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Box(modifier = Modifier.width(150.dp).height(24.dp).shimmerEffect())
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(4) {
                Box(modifier = Modifier.size(120.dp).clip(RoundedCornerShape(8.dp)).shimmerEffect())
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeCarousel(songs: List<Song>, onSongClick: (Song) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { songs.size })
    if (songs.isNotEmpty()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(220.dp).padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 16.dp
        ) { page ->
            val song = songs[page]
            Card(
                modifier = Modifier.fillMaxSize().clickable { onSongClick(song) },
                shape = RoundedCornerShape(16.dp)
            ) {
                AsyncImage(model = song.coverUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
        }
    }
}

@Composable
fun QuickActionsSection(onActionClick: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        QuickActionButton(Icons.Default.Favorite, stringResource(R.string.liked_songs)) { onActionClick("liked") }
        QuickActionButton(Icons.Default.History, stringResource(R.string.recently_played)) { onActionClick("recent") }
        QuickActionButton(Icons.Default.LibraryMusic, stringResource(R.string.my_playlists)) { onActionClick("playlists") }
        QuickActionButton(Icons.Default.Person, stringResource(R.string.top_artists)) { onActionClick("artists") }
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Surface(modifier = Modifier.size(56.dp), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.padding(16.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
    }
}

@Composable
fun SongSection(
    title: String, 
    songs: List<Song>, 
    onSongClick: (Song) -> Unit,
    onSeeAllClick: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { onSeeAllClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title, 
                style = MaterialTheme.typography.headlineSmall, 
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "See All",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(songs) { song -> SongItem(song = song, onClick = { onSongClick(song) }) }
        }
    }
}

@Composable
fun SongItem(song: Song, onClick: () -> Unit) {
    Column(modifier = Modifier.width(120.dp).clickable { onClick() }) {
        AsyncImage(model = song.coverUrl, contentDescription = null, modifier = Modifier.size(120.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = song.title, maxLines = 1, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(text = "Artist", maxLines = 1, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}
