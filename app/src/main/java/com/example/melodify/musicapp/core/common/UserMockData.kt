package com.melodify.musicapp.core.common

import com.melodify.musicapp.domain.model.User

object UserMockData {

    val users = listOf(
        User(
            id = "user_001",
            username = "armin_music",
            fullName = "Armin Rahimi",
            email = "armin@test.com",
            profileImage = "https://picsum.photos/200",
            bio = "Music lover 🎵",
            followersCount = 1250,
            followingCount = 340,
            playlistsCount = 15,
            isPremium = true,
            isFollowing = false
        ),

        User(
            id = "user_002",
            username = "sara_vibes",
            fullName = "Sara Ahmadi",
            email = "sara@test.com",
            profileImage = "https://picsum.photos/201",
            bio = "Creating playlists and discovering new sounds",
            followersCount = 870,
            followingCount = 120,
            playlistsCount = 8,
            isPremium = false,
            isFollowing = true
        ),

        User(
            id = "user_003",
            username = "dj_nima",
            fullName = "Nima Karimi",
            email = "nima@test.com",
            profileImage = "https://picsum.photos/202",
            bio = "DJ & producer 🎧",
            followersCount = 5400,
            followingCount = 500,
            playlistsCount = 42,
            isPremium = true,
            isFollowing = false
        )
    )
}