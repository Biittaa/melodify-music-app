package com.melodify.musicapp.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.melodify.musicapp.R
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.ui.components.shimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onSongClick: (Song) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = uiState.query,
            onQueryChange = viewModel::onQueryChange,
            onSearch = {},
            active = false,
            onActiveChange = {},
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {}

        FilterChips(selectedFilter = uiState.selectedFilter, onFilterSelected = viewModel::onFilterChange)

        if (uiState.query.isEmpty()) {
            SearchHistorySection(
                history = uiState.history,
                onHistoryClick = viewModel::onQueryChange,
                onClearHistory = viewModel::clearHistory
            )
        } else {
            SearchResultsList(searchResults, onSongClick)
        }
    }
}

@Composable
fun SearchResultsList(songs: LazyPagingItems<Song>, onSongClick: (Song) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
        items(
            count = songs.itemCount,
            key = songs.itemKey { it.id }
        ) { index ->
            songs[index]?.let { song ->
                SearchResultItem(song = song, onClick = { onSongClick(song) })
            }
        }

        when (songs.loadState.refresh) {
            is LoadState.Loading -> {
                items(5) {
                    Box(modifier = Modifier.fillMaxWidth().height(72.dp).padding(16.dp).clip(RoundedCornerShape(8.dp)).shimmerEffect())
                }
            }
            is LoadState.Error -> { /* Handle Error */ }
            else -> {}
        }
    }
}

@Composable
fun SearchResultItem(song: Song, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.coverUrl,
            contentDescription = null,
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = song.title, fontWeight = FontWeight.SemiBold)
            Text(text = "Artist", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun FilterChips(selectedFilter: SearchFilter, onFilterSelected: (SearchFilter) -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(SearchFilter.entries.toTypedArray()) { filter ->
            FilterChip(selected = selectedFilter == filter, onClick = { onFilterSelected(filter) }, label = { Text(filter.name) })
        }
    }
}

@Composable
fun SearchHistorySection(history: List<com.melodify.musicapp.domain.model.SearchHistory>, onHistoryClick: (String) -> Unit, onClearHistory: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Recent Searches", fontWeight = FontWeight.Bold)
            TextButton(onClick = onClearHistory) { Text(text = "Clear All") }
        }
        LazyColumn {
            items(history) { item ->
                Row(modifier = Modifier.fillMaxWidth().clickable { onHistoryClick(item.keyword) }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, null, tint = Color.Gray); Spacer(Modifier.width(16.dp)); Text(item.keyword)
                }
            }
        }
    }
}
