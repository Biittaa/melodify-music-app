package com.melodify.musicapp.domain.model

import com.google.firebase.firestore.PropertyName

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
) {
    // فیلدهای اضافی برای Firebase (با @PropertyName)
    @get:PropertyName("usernameSearch")
    val usernameSearch: String = username.lowercase()

    // سازنده بدون پارامتر برای Firestore
    constructor() : this(
        id = "",
        username = "",
        fullName = "",
        email = "",
        profileImage = "",
        bio = "",
        followersCount = 0,
        followingCount = 0,
        playlistsCount = 0,
        isPremium = false,
        isFollowing = false
    )
}