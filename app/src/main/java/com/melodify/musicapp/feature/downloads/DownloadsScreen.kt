package com.melodify.musicapp.feature.downloads

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.melodify.musicapp.R
import com.melodify.musicapp.navigation.Screen
<<<<<<< Updated upstream
import com.melodify.musicapp.ui.components.EmptyState
import com.melodify.musicapp.ui.components.MelodifyTopBar
import com.melodify.musicapp.ui.theme.MelodifyColors
import com.melodify.musicapp.ui.theme.Space12
import com.melodify.musicapp.ui.theme.Space16
=======
import com.melodify.musicapp.core.ui.components.EmptyState
import com.melodify.musicapp.core.ui.components.MelodifyTopBar
import com.melodify.musicapp.core.ui.theme.MelodifyColors
import com.melodify.musicapp.core.ui.theme.Space12
import com.melodify.musicapp.core.ui.theme.Space16
>>>>>>> Stashed changes
import com.melodify.musicapp.domain.model.Download
import com.melodify.musicapp.domain.model.DownloadStatus

@Composable
fun DownloadsScreen(
    navController: NavController,
    viewModel: DownloadsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MelodifyTopBar(
                title = stringResource(R.string.nav_downloads),
                showActions = false
            )
        },
        containerColor = MelodifyColors().background
    ) { paddingValues ->
        DownloadsContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onSongClick = { songId ->
                navController.navigate(Screen.Player.passSongId(songId))
            }
        )
    }
}

@Composable
fun DownloadsContent(
    modifier: Modifier = Modifier,
    uiState: DownloadsUiState,
    onEvent: (DownloadsEvent) -> Unit,
    onSongClick: (String) -> Unit
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

        uiState.downloads.isEmpty() -> {
            EmptyState(
                icon = Icons.Default.Download,
                title = stringResource(R.string.no_downloads),
                description = stringResource(R.string.download_songs_offline),
                modifier = modifier
            )
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = uiState.downloads,
                    key = { it.songId }
                ) { download ->
                    DownloadItem(
                        download = download,
                        onItemClick = { onSongClick(download.songId) },
                        onDelete = {
                            onEvent(
                                DownloadsEvent.OnDeleteDownload(
                                    download.songId
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadItem(
    download: Download,
    onItemClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Space16, vertical = 4.dp)
            .clickable { onItemClick() },
        colors = CardDefaults.cardColors(
            containerColor = MelodifyColors().surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Space16),
            horizontalArrangement = Arrangement.spacedBy(Space12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ استفاده از coverUrl از download (اگه نداره، یه جایگزین بذار)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(download.coverUrl ?: "https://picsum.photos/seed/${download.songId}/200/200")
                    .crossfade(true)
                    .build(),
                contentDescription = download.title,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = download.title ?: "Unknown Title",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MelodifyColors().onSurface
                )
                Text(
                    text = download.artistName ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodySmall,
                    color = MelodifyColors().onSurface.copy(alpha = 0.7f)
                )

                // ✅ when کامل با همه حالت‌ها
                when (download.status) {
                    DownloadStatus.COMPLETED -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MelodifyColors().primary
                            )
                            Text(
                                text = stringResource(R.string.downloaded),
                                style = MaterialTheme.typography.labelSmall,
                                color = MelodifyColors().primary
                            )
                        }
                    }
                    DownloadStatus.DOWNLOADING -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MelodifyColors().primary
                            )
                            Text(
                                text = "${download.progress}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MelodifyColors().onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                    DownloadStatus.PENDING -> {
                        Text(
                            text = "Pending...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MelodifyColors().onSurface.copy(alpha = 0.5f)
                        )
                    }
                    DownloadStatus.PAUSED -> {
                        Text(
                            text = stringResource(R.string.paused),
                            style = MaterialTheme.typography.labelSmall,
                            color = MelodifyColors().onSurface.copy(alpha = 0.5f)
                        )
                    }
                    DownloadStatus.FAILED -> {
                        Text(
                            text = stringResource(R.string.failed),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (download.status == DownloadStatus.COMPLETED) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MelodifyColors().onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}