package com.melodify.musicapp.feature.downloads

import com.melodify.musicapp.domain.model.Download

data class DownloadsUiState(
    val isLoading: Boolean = false,
    val downloads: List<Download> = emptyList(),
    val error: String? = null
)

sealed class DownloadsEvent {
    data class OnDeleteDownload(val songId: String) : DownloadsEvent()
    data class OnSongClick(val songId: String) : DownloadsEvent()
    object OnRefresh : DownloadsEvent()
}