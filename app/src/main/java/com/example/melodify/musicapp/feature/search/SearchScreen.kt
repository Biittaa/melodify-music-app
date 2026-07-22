package com.melodify.musicapp.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.melodify.musicapp.domain.model.SearchResult
import com.melodify.musicapp.R
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.ui.components.shimmerEffect
import com.melodify.musicapp.domain.model.SearchFilter
import com.melodify.musicapp.domain.model.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onSongClick: (Song) -> Unit,
    onUserClick: (User) -> Unit
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (uiState.history.isNotEmpty()) {
                    item {
                        SearchHistorySection(
                            history = uiState.history,
                            onHistoryClick = viewModel::onQueryChange,
                            onClearHistory = viewModel::clearHistory
                        )
                    }
                }

                if (uiState.localSongs.isNotEmpty()) {
                    item {
                        Text(
                            text = "My Local Music",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(uiState.localSongs) { song ->
                        SearchResultItem(song = song, onClick = { onSongClick(song) })
                    }
                }
            }
        } else {
            if (uiState.totalCount > 0) {
                Text(
                    text = "Results (${uiState.totalCount}):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            SearchResultsList(searchResults, onSongClick, onUserClick = onUserClick)
        }
    }
}

@Composable
fun SearchResultsList(
    results: LazyPagingItems<SearchResult>,
    onSongClick: (Song) -> Unit,
    onUserClick: (User) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(
            count = results.itemCount
        ) { index ->
            when(val item = results[index]) {
                is SearchResult.SongResult -> {
                    SearchResultItem(song = item.song, onClick = { onSongClick(item.song) })
                }
                is SearchResult.UserResult -> {
                    UserSearchItem(user = item.user, onUserClick = onUserClick)
                }
                is SearchResult.Header -> {
                    val style = if (item.title == "Songs" || item.title == "Users") {
                        MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    } else {
                        MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                    Text(
                        text = item.title,
                        style = style,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                null -> {}
            }
        }

        // Only show shimmer if loading AND we don't have enough results yet
        if (results.loadState.refresh is LoadState.Loading && results.itemCount == 0) {
            items(5) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}


@Composable
fun UserSearchItem(
    user: User,
    onUserClick: (User) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserClick(user)}
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        AsyncImage(
            model = user.profileImage.ifEmpty { "https://www.w3schools.com/howto/img_avatar.png" },
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = user.username,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "@${user.username}",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
            if (user.isFollowing) {
                Text(
                    text = "Following",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
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
            FilterChip(selected = selectedFilter == filter, onClick = { onFilterSelected(filter) }, label = {
                Text(stringResource(filter.titleRes))
            })
        }
    }
}

@Composable
fun SearchHistorySection(
    history: List<com.melodify.musicapp.domain.model.SearchHistory>,
    onHistoryClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.search_history), fontWeight = FontWeight.Bold)
            TextButton(onClick = onClearHistory) {  Text(text = stringResource(R.string.clear_all)) }
        }

        Column {
            history.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHistoryClick(item.keyword) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.History, null, tint = Color.Gray)
                    Spacer(Modifier.width(16.dp))
                    Text(item.keyword)
                }
            }
        }
    }
}
