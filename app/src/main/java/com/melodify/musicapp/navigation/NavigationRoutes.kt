package com.melodify.musicapp.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Downloads : Screen("downloads")
    object Playlist : Screen("playlist")
    object Profile : Screen("profile")
    object Player : Screen("player/{songId}") {
        fun passSongId(songId: String) = "player/$songId"
    }
    object Chat : Screen("chat/{userId}") {
        fun passUserId(userId: String) = "chat/$userId"
    }
    object Settings : Screen("settings")
    object LikedSongs : Screen("liked_songs")
    object RecentlyPlayed : Screen("recently_played")
    object Artists : Screen("artists")
    object Following : Screen("following")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Search,
    Screen.Downloads,
    Screen.Playlist,
    Screen.Profile
)