package com.melodify.musicapp.feature.playlists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.feature.player.PlayerViewModel
import com.melodify.musicapp.feature.search.SearchResultItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: String,
    onBackClick: () -> Unit,
    onSongClick: (Song) -> Unit,
    viewModel: PlaylistDetailViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSongSheet by remember { mutableStateOf(false) }

    LaunchedEffect(playlistId) {
        viewModel.loadPlaylistDetails(playlistId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.playlist?.title ?: "پلی‌لیست", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddSongSheet = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Song")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val listState = rememberLazyListState()
            
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    PlaylistHeader(
                        title = uiState.playlist?.title ?: "پلی‌لیست",
                        songsCount = uiState.songs.size,
                        isShuffle = uiState.isShuffle,
                        onShuffleToggle = { 
                            viewModel.toggleShuffle()
                            playerViewModel.toggleShuffle(true)
                        },
                        onShuffleOff = { 
                            viewModel.toggleShuffle(false)
                            playerViewModel.toggleShuffle(false)
                        }
                    )
                }
                
                itemsIndexed(uiState.songs, key = { _, song -> song.id }) { index, song ->
                    Box(modifier = Modifier.animateItemPlacement()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.DragHandle,
                                contentDescription = "Reorder",
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .size(24.dp)
                                    .pointerInput(Unit) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = { },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                val threshold = 50f
                                                if (dragAmount.y > threshold && index < uiState.songs.size - 1) {
                                                    viewModel.moveSong(index, index + 1)
                                                } else if (dragAmount.y < -threshold && index > 0) {
                                                    viewModel.moveSong(index, index - 1)
                                                }
                                            },
                                            onDragEnd = { }
                                        )
                                    }
                            )
                            
                            Box(modifier = Modifier.weight(1f)) {
                                SearchResultItem(
                                    song = song,
                                    onClick = { 
                                        playerViewModel.playPlaylist(uiState.songs, index)
                                        onSongClick(song) 
                                    }
                                )
                            }
                            
                            IconButton(onClick = { viewModel.removeSong(song.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSongSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSongSheet = false },
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            AddSongsSheetContent(
                uiState = uiState,
                onSearchQueryChange = viewModel::updateSearchQuery,
                onSongToggle = viewModel::toggleSongSelection,
                onAddClick = {
                    viewModel.addSelectedSongs()
                    showAddSongSheet = false
                }
            )
        }
    }
}

@Composable
fun AddSongsSheetContent(
    uiState: PlaylistDetailUiState,
    onSearchQueryChange: (String) -> Unit,
    onSongToggle: (String) -> Unit,
    onAddClick: () -> Unit
) {
    val filteredSongs = remember(uiState.allAvailableSongs, uiState.searchQuery) {
        uiState.allAvailableSongs.filter { 
            it.title.contains(uiState.searchQuery, ignoreCase = true) 
        }
    }

    Column(modifier = Modifier.fillMaxHeight(0.8f).padding(16.dp)) {
        Text(
            "افزودن آهنگ به پلی‌لیست", 
            style = MaterialTheme.typography.titleLarge, 
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("جستجو آهنگ...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredSongs) { song ->
                val isSelected = uiState.selectedSongIds.contains(song.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSongToggle(song.id) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onSongToggle(song.id) }
                    )
                    SearchResultItem(song = song, onClick = { onSongToggle(song.id) })
                }
            }
        }
        
        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            enabled = uiState.selectedSongIds.isNotEmpty()
        ) {
            Text("افزودن (${uiState.selectedSongIds.size})")
        }
    }
}

@Composable
fun PlaylistHeader(
    title: String,
    songsCount: Int,
    isShuffle: Boolean,
    onShuffleToggle: () -> Unit,
    onShuffleOff: () -> Unit
) {
    var lastClickTime by remember { mutableStateOf(0L) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "پلی‌لیست", 
            style = MaterialTheme.typography.labelLarge, 
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier.size(200.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.LibraryMusic, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = "$songsCount آهنگ", color = Color.Gray)
        
        Button(
            onClick = { 
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < 500) {
                    onShuffleOff()
                } else {
                    onShuffleToggle()
                }
                lastClickTime = currentTime
            },
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isShuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        ) {
            Icon(if (isShuffle) Icons.Default.ShuffleOn else Icons.Default.Shuffle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isShuffle) "پخش در حالت اتفاقی روشن" else "پخش اتفاقی")
        }
    }
}
