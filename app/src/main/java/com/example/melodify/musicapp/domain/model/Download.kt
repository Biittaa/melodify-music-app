package com.melodify.musicapp.domain.model

enum class DownloadStatus { PENDING, DOWNLOADING, COMPLETED, FAILED }

data class Download(
    val songId: String,
    val progress: Int,
    val status: DownloadStatus
)