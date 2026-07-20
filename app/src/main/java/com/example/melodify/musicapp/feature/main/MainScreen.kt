package com.melodify.musicapp.feature.main

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.melodify.musicapp.R
import com.melodify.musicapp.feature.home.HomeScreen
import com.melodify.musicapp.feature.search.SearchScreen
import com.melodify.musicapp.feature.downloads.DownloadsScreen
import com.melodify.musicapp.feature.playlists.PlaylistsScreen
import com.melodify.musicapp.feature.playlists.PlaylistDetailScreen
import com.melodify.musicapp.feature.profile.ProfileScreen
import com.melodify.musicapp.feature.player.MiniPlayer
import com.melodify.musicapp.feature.player.NowPlayingScreen
import com.melodify.musicapp.feature.settings.SettingsScreen
import com.melodify.musicapp.feature.chat.ConversationsScreen
import com.melodify.musicapp.feature.chat.ChatScreen
import com.melodify.musicapp.feature.liked_songs.LikedSongsScreen

sealed class Screen(val route: String, val labelRes: Int? = null, val icon: ImageVector? = null) {
    object Home : Screen("home", R.string.home, Icons.Default.Home)
    object Search : Screen("search", R.string.search, Icons.Default.Search)
    object Downloads : Screen("downloads", R.string.downloads, Icons.Default.Download)
    object Playlists : Screen("playlists", R.string.playlists, Icons.Default.LibraryMusic)
    object Profile : Screen("profile", R.string.profile, Icons.Default.Person)
    object Settings : Screen("settings", R.string.settings, Icons.Default.Settings)
    object ChatList : Screen("chat_list", R.string.app_name, Icons.Default.Notifications)
    object ChatDetail : Screen("chat_detail/{userId}")
    object PlaylistDetail : Screen("playlist_detail/{playlistId}")
    object LikedSongs : Screen("liked_songs", R.string.liked_songs)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavItems = listOf(Screen.Home, Screen.Search, Screen.Downloads, Screen.Playlists, Screen.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    var showNowPlaying by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                val currentRoute = currentDestination?.route ?: ""
                val shouldShowTopBar = bottomNavItems.any { it.route == currentRoute } || currentRoute == Screen.ChatList.route
                
                if (shouldShowTopBar) {
                    CenterAlignedTopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Melodify", fontWeight = FontWeight.ExtraBold)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                                Icon(Icons.Default.Settings, contentDescription = null)
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate(Screen.ChatList.route) }) {
                                BadgedBox(badge = { Badge { Text("3") } }) {
                                    Icon(Icons.Default.Notifications, contentDescription = null)
                                }
                            }
                            AsyncImage(
                                model = "https://www.w3schools.com/howto/img_avatar.png",
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    )
                }
            },
            bottomBar = {
                Column {
                    MiniPlayer(onClick = { showNowPlaying = true })
                    NavigationBar {
                        bottomNavItems.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon!!, contentDescription = null) },
                                label = { Text(stringResource(screen.labelRes!!)) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { 
                    HomeScreen(
                        onSongClick = { showNowPlaying = true },
                        onQuickActionClick = { action ->
                            when(action) {
                                "liked" -> navController.navigate(Screen.LikedSongs.route)
                                // other actions...
                            }
                        }
                    ) 
                }
                composable(Screen.Search.route) { SearchScreen(onSongClick = { showNowPlaying = true }) }
                composable(Screen.Downloads.route) { DownloadsScreen(onSongClick = { showNowPlaying = true }) }
                composable(Screen.Playlists.route) { 
                    PlaylistsScreen(onPlaylistClick = { playlist -> 
                        navController.navigate("playlist_detail/${playlist.id}")
                    }) 
                }
                composable(Screen.Profile.route) { 
                    ProfileScreen(
                        onSettingsClick = { navController.navigate(Screen.Settings.route) },
                        onLogoutClick = { /* Handle Logout */ }
                    ) 
                }
                composable(Screen.Settings.route) { SettingsScreen(onBackClick = { navController.popBackStack() }) }
                composable(Screen.ChatList.route) { 
                    ConversationsScreen(onConversationClick = { userId -> 
                        navController.navigate("chat_detail/$userId")
                    }) 
                }
                composable("chat_detail/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    ChatScreen(
                        otherUserId = userId,
                        onBackClick = { navController.popBackStack() },
                        onSongClick = { /* Play shared song */ }
                    )
                }
                composable(Screen.PlaylistDetail.route) { backStackEntry ->
                    val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
                    PlaylistDetailScreen(
                        playlistId = playlistId,
                        onBackClick = { navController.popBackStack() },
                        onSongClick = { showNowPlaying = true }
                    )
                }
                composable(Screen.LikedSongs.route) {
                    LikedSongsScreen(
                        onBackClick = { navController.popBackStack() },
                        onSongClick = { showNowPlaying = true }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showNowPlaying,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            NowPlayingScreen(onBackClick = { showNowPlaying = false })
        }
    }
}
