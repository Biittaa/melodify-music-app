package com.melodify.musicapp.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
<<<<<<< Updated upstream
import androidx.compose.foundation.lazy.items
=======
>>>>>>> Stashed changes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
<<<<<<< Updated upstream
import com.example.melodify.R
import com.melodify.musicapp.navigation.Screen
import com.melodify.musicapp.ui.components.MelodifyTopBar
import com.melodify.musicapp.ui.components.ShimmerLoading
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.Playlist
import com.melodify.musicapp.ui.theme.MelodifyColors
import com.melodify.musicapp.ui.theme.Space12
import com.melodify.musicapp.ui.theme.Space16
import com.melodify.musicapp.ui.theme.Space4
import com.melodify.musicapp.ui.theme.Space8
=======
import com.melodify.musicapp.navigation.Screen
import com.melodify.musicapp.core.ui.components.MelodifyTopBar
import com.melodify.musicapp.core.ui.components.ShimmerLoading
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.Playlist
import com.melodify.musicapp.core.ui.theme.MelodifyColors
import com.melodify.musicapp.core.ui.theme.Space12
import com.melodify.musicapp.core.ui.theme.Space16
import com.melodify.musicapp.core.ui.theme.Space4
import com.melodify.musicapp.core.ui.theme.Space8
>>>>>>> Stashed changes

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MelodifyTopBar(
                title = stringResource(R.string.app_name),
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onNotificationClick = { /* Show notifications */ }
            )
        },
        containerColor = MelodifyColors().background
    ) { paddingValues ->
        HomeContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onSongClick = { songId ->
                navController.navigate(Screen.Player.passSongId(songId))
            },
            onPlaylistClick = { /* Navigate to playlist detail */ },
            onArtistClick = { /* Navigate to artist detail */ }
        )
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    onSongClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    onArtistClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MelodifyColors().background),
        verticalArrangement = Arrangement.spacedBy(Space16)
    ) {
        // Carousel
        item {
            if (uiState.carouselSongs.isNotEmpty()) {
                CarouselSection(
                    songs = uiState.carouselSongs,
                    onSongClick = onSongClick
                )
            }
        }

        // Quick Actions
        item {
            QuickActionsSection(
                onActionClick = { action ->
                    onEvent(HomeEvent.OnQuickActionClick(action))
                }
            )
        }

        // Trending Songs
        item {
            if (uiState.trendingSongs.isNotEmpty()) {
                SectionHeader(title = stringResource(R.string.trending_songs))
                SongRowSection(
                    songs = uiState.trendingSongs,
                    onSongClick = onSongClick
                )
            }
        }

        // Latest Songs
        item {
            if (uiState.latestSongs.isNotEmpty()) {
                SectionHeader(title = stringResource(R.string.latest_songs))
                SongRowSection(
                    songs = uiState.latestSongs,
                    onSongClick = onSongClick
                )
            }
        }

        // Playlists
        item {
            if (uiState.playlists.isNotEmpty()) {
                SectionHeader(title = stringResource(R.string.playlists))
                PlaylistRowSection(
                    playlists = uiState.playlists,
                    onPlaylistClick = onPlaylistClick
                )
            }
        }

        // Loading state
        if (uiState.isLoading) {
            item { LoadingSections() }
        }
    }
}

@Composable
fun CarouselSection(
    songs: List<Song>,
    onSongClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(Space8),
        contentPadding = PaddingValues(horizontal = Space16)
    ) {
        items(songs) { song ->
            CarouselItem(
                song = song,
                onClick = { onSongClick(song.id) }
            )
        }
    }
}

@Composable
fun CarouselItem(
    song: Song,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(320.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MelodifyColors().surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = song.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(Space16)
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = song.artistName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onActionClick: (QuickAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Space16),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickActionButton(
            icon = R.drawable.ic_favorite,
            label = stringResource(R.string.liked_songs),
            onClick = { onActionClick(QuickAction.LIKED_SONGS) }
        )
        QuickActionButton(
            icon = R.drawable.ic_history,
            label = stringResource(R.string.recently_played),
            onClick = { onActionClick(QuickAction.RECENTLY_PLAYED) }
        )
        QuickActionButton(
            icon = R.drawable.ic_playlist,
            label = stringResource(R.string.my_playlists),
            onClick = { onActionClick(QuickAction.MY_PLAYLISTS) }
        )
        QuickActionButton(
            icon = R.drawable.ic_artist,
            label = stringResource(R.string.top_artists),
            onClick = { onActionClick(QuickAction.TOP_ARTISTS) }
        )
    }
}

@Composable
fun QuickActionButton(
    icon: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = MelodifyColors().primary,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MelodifyColors().onSurface
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MelodifyColors().onSurface,
        modifier = Modifier
            .padding(horizontal = Space16)
            .padding(bottom = Space8)
    )
}

@Composable
fun SongRowSection(
    songs: List<Song>,
    onSongClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Space12),
        contentPadding = PaddingValues(horizontal = Space16)
    ) {
        items(songs) { song ->
            SongCard(
                song = song,
                onClick = { onSongClick(song.id) }
            )
        }
    }
}

@Composable
fun SongCard(
    song: Song,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = song.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = song.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MelodifyColors().onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = Space4)
        )

        Text(
            text = song.artistName,
            style = MaterialTheme.typography.bodySmall,
            color = MelodifyColors().onSurface.copy(alpha = 0.7f),
            maxLines = 1
        )
    }
}

@Composable
fun PlaylistRowSection(
    playlists: List<Playlist>,
    onPlaylistClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Space12),
        contentPadding = PaddingValues(horizontal = Space16)
    ) {
        items(playlists) { playlist ->
            PlaylistCard(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist.id) }
            )
        }
    }
}

@Composable
fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(playlist.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = playlist.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = playlist.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MelodifyColors().onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = Space4)
        )

        Text(
            text = "${playlist.songsCount} ${stringResource(R.string.songs)}",
            style = MaterialTheme.typography.bodySmall,
            color = MelodifyColors().onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun LoadingSections() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Space16)
    ) {
        repeat(3) {
            ShimmerLoading(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(horizontal = Space16)
            )
        }
    }
}