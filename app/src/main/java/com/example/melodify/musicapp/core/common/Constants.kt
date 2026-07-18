package com.melodify.musicapp.core.common

object Constants {
    // -------- Firebase --------
    const val FIREBASE_USERS_COLLECTION = "users"
    const val FIREBASE_SONGS_COLLECTION = "songs"
    const val FIREBASE_ARTISTS_COLLECTION = "artists"
    const val FIREBASE_ALBUMS_COLLECTION = "albums"
    const val FIREBASE_PLAYLISTS_COLLECTION = "playlists"
    const val FIREBASE_PLAYLIST_SONGS_COLLECTION = "playlistSongs"
    const val FIREBASE_MESSAGES_COLLECTION = "messages"
    const val FIREBASE_FOLLOWS_COLLECTION = "follows"
    const val FIREBASE_LIKES_COLLECTION = "likes"
    const val FIREBASE_NOTIFICATIONS_COLLECTION = "notifications"

    // -------- Storage Paths --------
    const val STORAGE_PROFILE_IMAGES = "profile_images"
    const val STORAGE_SONGS = "songs"
    const val STORAGE_ALBUM_COVERS = "album_covers"
    const val STORAGE_PLAYLIST_COVERS = "playlist_covers"

    // -------- Room Database --------
    const val ROOM_DATABASE_NAME = "melodify.db"

    // -------- DataStore Preferences Keys --------
    const val DATASTORE_NAME = "settings"
    const val PREF_DARK_MODE = "dark_mode"
    const val PREF_LANGUAGE = "language"
    const val PREF_FONT_SCALE = "font_scale"
    const val PREF_NOTIFICATION_ENABLED = "notification_enabled"

    // -------- Player / ExoPlayer --------
    const val PLAYER_NOTIFICATION_ID = 1001
    const val PLAYER_CHANNEL_ID = "melodify_player_channel"
    const val PLAYER_CHANNEL_NAME = "Melodify Player"
    const val PLAYER_DEFAULT_SPEED = 1.0f

    // -------- Download / WorkManager --------
    const val DOWNLOAD_WORK_TAG = "download_song"
    const val DOWNLOAD_INPUT_SONG_ID = "song_id"
    const val DOWNLOAD_INPUT_USER_ID = "user_id"

    // -------- Paging --------
    const val PAGE_SIZE = 20
    const val SEARCH_DEBOUNCE_MILLIS = 500L

    // -------- Sleep Timer --------
    const val SLEEP_TIMER_DEFAULT_MINUTES = 15

    // -------- Chat --------
    const val MESSAGE_SONG_PLACEHOLDER = "🎵 A music is shared"
}