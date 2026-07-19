package com.melodify.musicapp.domain.model

data class SearchHistory(
    val id: String,
    val keyword: String,
    val searchedAt: Long
)