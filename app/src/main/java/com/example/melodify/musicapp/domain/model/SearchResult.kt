package com.example.melodify.musicapp.domain.model

import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.User


sealed class SearchResult {

    data class SongResult(
        val song: Song
    ): SearchResult()

    data class UserResult(
        val user: User
    ): SearchResult()

    data class Header(
        val title: String
    ): SearchResult()
}