package com.melodify.musicapp.domain.model

data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val profileImage: String,
    val bio: String,
    val followersCount: Int,
    val followingCount: Int,
    val playlistsCount: Int,
    val isPremium: Boolean,
    val isFollowing: Boolean
)