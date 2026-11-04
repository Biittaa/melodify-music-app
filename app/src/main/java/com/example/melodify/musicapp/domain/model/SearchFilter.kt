package com.melodify.musicapp.domain.model

import com.melodify.musicapp.R

enum class SearchFilter(
    val titleRes: Int
) {
    All(R.string.filter_all),
    Songs(R.string.filter_songs),
    Artists(R.string.filter_artists),
    Users(R.string.filter_users)
}
