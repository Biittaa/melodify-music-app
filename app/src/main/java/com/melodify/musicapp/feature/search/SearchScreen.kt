package com.melodify.musicapp.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip  // ✅ این رو اضافه کن
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
<<<<<<< Updated upstream
import androidx.compose.ui.text.style.TextOverflow
=======
>>>>>>> Stashed changes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

import com.melodify.musicapp.R
import com.melodify.musicapp.navigation.Screen
<<<<<<< Updated upstream
import com.melodify.musicapp.ui.components.MelodifyTopBar
import com.melodify.musicapp.ui.theme.MelodifyColors
import com.melodify.musicapp.ui.theme.Space16
import com.melodify.musicapp.ui.theme.Space8
=======
import com.melodify.musicapp.core.ui.theme.MelodifyColors
import com.melodify.musicapp.core.ui.theme.Space16
import com.melodify.musicapp.core.ui.theme.Space8
>>>>>>> Stashed changes
import com.melodify.musicapp.domain.model.Album
import com.melodify.musicapp.domain.model.Artist
import com.melodify.musicapp.domain.model.SearchHistory

// ✅ برای رفع خطاهای Experimental API
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        containerColor = MelodifyColors().background,
        topBar = {
            TopAppBar(
                title = {
                    SearchBar(
                        query = uiState.query,
                        onQueryChange = { viewModel.onEvent(SearchEvent.OnQueryChange(it)) },
                        onSearch = { viewModel.onEvent(SearchEvent.OnSearch(it)) },
                        focusRequester = focusRequester
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MelodifyColors().surface
                )
            )
        }
    ) { paddingValues ->
        SearchContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onSongClick = { songId ->
                navController.navigate(Screen.Player.passSongId(songId))
            },
            onArtistClick = { /* Navigate to artist detail */ },
            onAlbumClick = { /* Navigate to album detail */ }
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        placeholder = {
            Text(stringResource(R.string.search_music_artists))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear)
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch(query) }
        ),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MelodifyColors().primary,
            unfocusedBorderColor = MelodifyColors().onSurface.copy(alpha = 0.2f)
        )
    )
}

@Composable
fun SearchContent(
    modifier: Modifier = Modifier,
    uiState: SearchUiState,
    onEvent: (SearchEvent) -> Unit,
    onSongClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MelodifyColors().background)
    ) {
        FilterChips(
            selectedFilter = uiState.selectedFilter,
            onFilterChange = { onEvent(SearchEvent.OnFilterChange(it)) }
        )

        if (uiState.query.isEmpty()) {
            SearchHistorySection(
                history = uiState.history,
                onHistoryClick = { query -> onEvent(SearchEvent.OnHistoryClick(query)) },
                onHistoryDelete = { historyId -> onEvent(SearchEvent.OnHistoryDelete(historyId)) },
                onClearHistory = { onEvent(SearchEvent.OnClearHistory) }
            )
        } else {
            SearchResults(
                uiState = uiState,
                onSongClick = onSongClick,
                onArtistClick = onArtistClick,
                onAlbumClick = onAlbumClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    selectedFilter: SearchFilter,
    onFilterChange: (SearchFilter) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Space16, vertical = Space8),
        horizontalArrangement = Arrangement.spacedBy(Space8)
    ) {
        val filters = listOf(
            SearchFilter.ALL to R.string.all,
            SearchFilter.SONGS to R.string.songs,
            SearchFilter.ARTISTS to R.string.artists,
            SearchFilter.ALBUMS to R.string.albums
        )

        items(filters) { (filter, label) ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterChange(filter) },
                label = { Text(stringResource(label)) }
            )
        }
    }
}

@Composable
fun SearchHistorySection(
    history: List<SearchHistory>,
    onHistoryClick: (String) -> Unit,
    onHistoryDelete: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    if (history.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MelodifyColors().onSurface.copy(alpha = 0.3f)
                )
                Text(
                    text = stringResource(R.string.no_search_history),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MelodifyColors().onSurface.copy(alpha = 0.6f)
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Space16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.recent_searches),
                style = MaterialTheme.typography.titleMedium,
                color = MelodifyColors().onSurface
            )
            TextButton(onClick = onClearHistory) {
                Text(
                    text = stringResource(R.string.clear_all),
                    color = MelodifyColors().primary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(history) { item ->
                HistoryItem(
                    item = item,
                    onItemClick = { onHistoryClick(item.keyword) },
                    onDeleteClick = { onHistoryDelete(item.id) }
                )
            }
        }
    }
}

@Composable
fun HistoryItem(
    item: SearchHistory,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(horizontal = Space16, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = MelodifyColors().onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = item.keyword,
                style = MaterialTheme.typography.bodyMedium,
                color = MelodifyColors().onSurface
            )
        }

        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.delete),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SearchResults(
    uiState: SearchUiState,
    onSongClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MelodifyColors().primary
                )
            }
        }
        uiState.songs.isEmpty() && uiState.artists.isEmpty() && uiState.albums.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MelodifyColors().onSurface.copy(alpha = 0.3f)
                    )
                    Text(
                        text = stringResource(R.string.no_results_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MelodifyColors().onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(R.string.try_different_keywords),
                        style = MaterialTheme.typography.bodySmall,
                        color = MelodifyColors().onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (uiState.selectedFilter == SearchFilter.ALL || uiState.selectedFilter == SearchFilter.SONGS) {
                    items(uiState.songs) { song ->
                        SongResultItem(
                            song = song,
                            onClick = { onSongClick(song.id) }
                        )
                    }
                }

                if (uiState.selectedFilter == SearchFilter.ALL || uiState.selectedFilter == SearchFilter.ARTISTS) {
                    items(uiState.artists) { artist ->
                        ArtistResultItem(
                            artist = artist,
                            onClick = { onArtistClick(artist.id) }
                        )
                    }
                }

                if (uiState.selectedFilter == SearchFilter.ALL || uiState.selectedFilter == SearchFilter.ALBUMS) {
                    items(uiState.albums) { album ->
                        AlbumResultItem(
                            album = album,
                            onClick = { onAlbumClick(album.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SongResultItem(
    song: Song,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = Space16, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = song.title,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),  // ✅ clip درست شد
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MelodifyColors().onSurface
            )
            Text(
                text = song.artistName,
                style = MaterialTheme.typography.bodySmall,
                color = MelodifyColors().onSurface.copy(alpha = 0.7f)
            )
        }

        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = stringResource(R.string.play),
            tint = MelodifyColors().primary
        )
    }
}

@Composable
fun ArtistResultItem(
    artist: Artist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = Space16, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(artist.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = artist.name,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),  // ✅ clip درست شد
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = artist.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MelodifyColors().onSurface
            )
            Text(
                text = "${artist.followers} ${stringResource(R.string.followers)}",
                style = MaterialTheme.typography.bodySmall,
                color = MelodifyColors().onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AlbumResultItem(
    album: Album,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = Space16, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(album.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = album.title,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),  // ✅ clip درست شد
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = album.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MelodifyColors().onSurface
            )
            Text(
                text = album.releaseDate,
                style = MaterialTheme.typography.bodySmall,
                color = MelodifyColors().onSurface.copy(alpha = 0.7f)
            )
        }
    }
}