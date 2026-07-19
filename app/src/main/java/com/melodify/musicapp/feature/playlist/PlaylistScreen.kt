//package com.melodify.musicapp.feature.playlist
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.navigation.NavController
//import coil.compose.AsyncImage
//import coil.request.ImageRequest
//import com.melodify.musicapp.ui.components.EmptyState
//import com.melodify.musicapp.ui.components.MelodifyTopBar
//import com.melodify.musicapp.ui.theme.MelodifyColors
//import com.melodify.musicapp.ui.theme.Space12
//import com.melodify.musicapp.ui.theme.Space16
//import com.melodify.musicapp.R
//import com.melodify.musicapp.domain.model.Playlist
//
//@Composable
//fun PlaylistScreen(
//    navController: NavController,
//    viewModel: PlaylistViewModel = hiltViewModel()
//) {
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    Scaffold(
//        topBar = {
//            MelodifyTopBar(
//                title = stringResource(R.string.nav_playlists),
//                showActions = false
//            )
//        },
//        containerColor = MelodifyColors().background
//    ) { paddingValues ->
//        PlaylistContent(
//            modifier = Modifier.padding(paddingValues),
//            uiState = uiState,
//            onEvent = viewModel::onEvent
//        )
//    }
//}
//
//@Composable
//fun PlaylistContent(
//    modifier: Modifier = Modifier,
//    uiState: PlaylistUiState,
//    onEvent: (PlaylistEvent) -> Unit
//) {
//    when {
//        uiState.isLoading -> {
//            Box(
//                modifier = modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator(color = MelodifyColors().primary)
//            }
//        }
//
//        uiState.playlists.isEmpty() -> {
//            EmptyState(
//                icon = Icons.Default.PlaylistAdd,
//                title = stringResource(R.string.no_playlists),
//                description = stringResource(R.string.create_first_playlist),
//                buttonText = stringResource(R.string.create_playlist),
//                onButtonClick = { /* Show create playlist dialog */ },
//                modifier = modifier
//            )
//        }
//
//        else -> {
//            LazyVerticalGrid(
//                modifier = modifier.fillMaxSize(),
//                columns = GridCells.Fixed(2),
//                horizontalArrangement = Arrangement.spacedBy(Space12),
//                verticalArrangement = Arrangement.spacedBy(Space12),
//                contentPadding = PaddingValues(Space16)
//            ) {
//                items(
//                    items = uiState.playlists,
//                    key = { it.id }
//                ) { playlist ->
//                    PlaylistGridItem(
//                        playlist = playlist,
//                        onClick = { onEvent(PlaylistEvent.OnPlaylistClick(playlist.id)) }
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun PlaylistGridItem(
//    playlist: Playlist,
//    onClick: () -> Unit
//) {
//    val colors = listOf(
//        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF3F51B5),
//        Color(0xFF2196F3), Color(0xFF009688), Color(0xFF4CAF50),
//        Color(0xFFFF9800), Color(0xFFFF5722), Color(0xFF795548)
//    )
//    val randomColor = colors.random()
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .aspectRatio(1f)
//            .clickable { onClick() },
//        colors = CardDefaults.cardColors(
//            containerColor = randomColor
//        ),
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(Space16),
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//            // Icon or cover
//            if (playlist.coverUrl.isNotEmpty()) {
//                AsyncImage(
//                    model = ImageRequest.Builder(LocalContext.current)
//                        .data(playlist.coverUrl)
//                        .crossfade(true)
//                        .build(),
//                    contentDescription = playlist.title,
//                    modifier = Modifier
//                        .size(48.dp)
//                        .clip(RoundedCornerShape(8.dp)),
//                    contentScale = ContentScale.Crop
//                )
//            } else {
//                Icon(
//                    imageVector = Icons.Default.PlaylistPlay,
//                    contentDescription = null,
//                    tint = Color.White.copy(alpha = 0.5f),
//                    modifier = Modifier.size(48.dp)
//                )
//            }
//
//            Column {
//                Text(
//                    text = playlist.title,
//                    style = MaterialTheme.typography.titleSmall,
//                    color = Color.White,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//                Text(
//                    text = "${playlist.songsCount} ${stringResource(R.string.songs)}",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.White.copy(alpha = 0.8f)
//                )
//            }
//        }
//    }
//}


package com.melodify.musicapp.feature.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.melodify.musicapp.ui.components.EmptyState
import com.melodify.musicapp.ui.components.MelodifyTopBar
import com.melodify.musicapp.ui.theme.MelodifyColors
import com.melodify.musicapp.ui.theme.Space12
import com.melodify.musicapp.ui.theme.Space16

// ✅ این خط رو به این شکل اضافه کن
import com.melodify.musicapp.R
import com.melodify.musicapp.domain.model.Playlist

@Composable
fun PlaylistScreen(
    navController: NavController,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MelodifyTopBar(
                title = stringResource(R.string.nav_playlists),
                showActions = false
            )
        },
        containerColor = MelodifyColors().background
    ) { paddingValues ->
        PlaylistContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
fun PlaylistContent(
    modifier: Modifier = Modifier,
    uiState: PlaylistUiState,
    onEvent: (PlaylistEvent) -> Unit
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MelodifyColors().primary)
            }
        }

        uiState.playlists.isEmpty() -> {
            EmptyState(
                icon = Icons.Default.PlaylistAdd,
                title = stringResource(R.string.no_playlists),
                description = stringResource(R.string.create_first_playlist),
                buttonText = stringResource(R.string.create_playlist),
                onButtonClick = { /* Show create playlist dialog */ },
                modifier = modifier
            )
        }

        else -> {
            LazyVerticalGrid(
                modifier = modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(Space12),
                verticalArrangement = Arrangement.spacedBy(Space12),
                contentPadding = PaddingValues(Space16)
            ) {
                items(
                    items = uiState.playlists,
                    key = { it.id }
                ) { playlist ->
                    PlaylistGridItem(
                        playlist = playlist,
                        onClick = { onEvent(PlaylistEvent.OnPlaylistClick(playlist.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistGridItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    val colors = listOf(
        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF3F51B5),
        Color(0xFF2196F3), Color(0xFF009688), Color(0xFF4CAF50),
        Color(0xFFFF9800), Color(0xFFFF5722), Color(0xFF795548)
    )
    val randomColor = colors.random()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = randomColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Space16),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (playlist.coverUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(playlist.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = playlist.title,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PlaylistPlay,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
            }

            Column {
                Text(
                    text = playlist.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${playlist.songsCount} ${stringResource(R.string.songs)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}