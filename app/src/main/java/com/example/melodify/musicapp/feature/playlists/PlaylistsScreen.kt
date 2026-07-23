package com.melodify.musicapp.feature.playlists

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.melodify.musicapp.R
import com.melodify.musicapp.domain.model.Playlist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    viewModel: PlaylistViewModel = hiltViewModel(),
    onPlaylistClick: (Playlist) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedPlaylistId by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Refresh playlist list when screen is shown
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    if (showCreateDialog) {
        PlaylistInputDialog(
            title = "Create New Playlist",
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                viewModel.createPlaylist(name) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) showCreateDialog = false
                }
            }
        )
    }

    if (showRenameDialog && selectedPlaylistId != null) {
        val currentName = uiState.userPlaylists.find { it.id == selectedPlaylistId }?.title ?: ""
        PlaylistInputDialog(
            title = "Rename Playlist",
            initialValue = currentName,
            onDismiss = { showRenameDialog = false },
            onConfirm = { newName ->
                viewModel.renamePlaylist(selectedPlaylistId!!, newName) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        selectedPlaylistId = null
                        showRenameDialog = false
                    }
                }
            }
        )
    }

    if (showDeleteConfirm && selectedPlaylistId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Playlist") },
            text = { Text("Are you sure you want to delete this playlist?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deletePlaylist(selectedPlaylistId!!)
                    selectedPlaylistId = null
                    showDeleteConfirm = false
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("No") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.playlists), fontWeight = FontWeight.Bold) },
                actions = {
                    if (selectedPlaylistId != null) {
                        IconButton(onClick = { showRenameDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Rename")
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                        IconButton(onClick = { selectedPlaylistId = null }) {
                            Icon(Icons.Default.Add, contentDescription = "Clear Selection", modifier = Modifier.graphicsLayer { rotationZ = 45f })
                        }
                    } else {
                        IconButton(onClick = { showCreateDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = padding.calculateTopPadding(), bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.userPlaylists.isNotEmpty()) {
                item(span = { GridItemSpan(2) }) { SectionHeader(title = stringResource(R.string.my_playlists)) }
                items(uiState.userPlaylists) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        isSelected = selectedPlaylistId == playlist.id,
                        onClick = {
                            if (selectedPlaylistId != null) {
                                selectedPlaylistId = if (selectedPlaylistId == playlist.id) null else playlist.id
                            } else {
                                onPlaylistClick(playlist)
                            }
                        },
                        onLongClick = {
                            selectedPlaylistId = playlist.id
                        }
                    )
                }
            }
            item(span = { GridItemSpan(2) }) { SectionHeader(title = "Internal Music") }
            items(uiState.internalMusic) { playlist ->
                PlaylistItem(playlist = playlist, isSelected = false, onClick = { onPlaylistClick(playlist) }, onLongClick = {})
            }
            item(span = { GridItemSpan(2) }) { SectionHeader(title = "Global Music") }
            items(uiState.globalMusic) { playlist ->
                PlaylistItem(playlist = playlist, isSelected = false, onClick = { onPlaylistClick(playlist) }, onLongClick = {})
            }
        }
    }
}

@Composable
fun PlaylistInputDialog(
    title: String,
    initialValue: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Playlist name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onConfirm(name) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistItem(
    playlist: Playlist,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = if (isSelected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors()
    ) {
        Column {
            Box(modifier = Modifier.aspectRatio(1f)) {
                if (playlist.coverUrl.isNotEmpty()) {
                    AsyncImage(model = playlist.coverUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.secondaryContainer) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.LibraryMusic, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = playlist.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = "${playlist.songsCount} songs", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}