@file:JvmName("MainScreenKt")
package com.melodify.musicapp.feature.main // Unified Package

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import com.melodify.musicapp.R

// Domain models
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.Playlist

// Feature screens
import com.melodify.musicapp.feature.auth.LoginScreen
import com.melodify.musicapp.feature.auth.RegisterScreen
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
import com.melodify.musicapp.feature.profile.ProfileViewModel
import com.melodify.musicapp.feature.social.SocialScreen

sealed class Screen(val route: String, val labelRes: Int? = null, val icon: ImageVector? = null) {
    object Home : Screen("home", R.string.home, Icons.Default.Home)
    object Search : Screen("search", R.string.search, Icons.Default.Search)
    object Downloads : Screen("downloads", R.string.downloads, Icons.Default.Download)
    object Playlists : Screen("playlists", R.string.playlists, Icons.Default.LibraryMusic)
    object Profile : Screen("profile", R.string.profile, Icons.Default.Person)
    object Settings : Screen("settings", R.string.settings, Icons.Default.Settings)
    object ChatList : Screen("chat_list", R.string.app_name, Icons.AutoMirrored.Filled.Chat)
    object Login : Screen("login")
    object Register : Screen("register")
    object PlaylistDetail : Screen("playlist_detail/{playlistId}")
    object LikedSongs : Screen("liked_songs", R.string.liked_songs)
    object Social : Screen("social", R.string.top_artists, Icons.Default.People)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(profileViewModel: ProfileViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(Screen.Home, Screen.Search, Screen.Downloads, Screen.Playlists, Screen.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val profileUiState by profileViewModel.uiState.collectAsState()
    var showNowPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(profileUiState.user, profileUiState.isLoading) {
        if (!profileUiState.isLoading && profileUiState.user == null) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                val currentRoute = currentDestination?.route ?: ""
                val isMainTab = bottomNavItems.any { it.route == currentRoute }

                if (isMainTab) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Melodify", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate(Screen.ChatList.route) }) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Live Chat")
                            }
                            IconButton(onClick = { /* Simulated notification panel */ }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                            }
                            IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                            profileUiState.user?.let { user ->
                                AsyncImage(
                                    model = user.profileImage.ifEmpty { "https://www.w3schools.com/howto/img_avatar.png" },
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .clickable { navController.navigate(Screen.Profile.route) },
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    )
                }
            },
            bottomBar = {
                val currentRoute = currentDestination?.route ?: ""
                val isAuthScreen = currentRoute == Screen.Login.route || currentRoute == Screen.Register.route
                if (!isAuthScreen && profileUiState.user != null) {
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
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
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
                composable(Screen.Login.route) {
                    LoginScreen(
                        onLoginSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                        onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                    )
                }
                composable(Screen.Register.route) {
                    RegisterScreen(
                        onRegisterSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                        onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                    )
                }
                composable(Screen.Home.route) {
                    HomeScreen(
                        onSongClick = { _: Song -> showNowPlaying = true },
                        onQuickActionClick = { action: String ->
                            when(action) {
                                "liked" -> navController.navigate(Screen.LikedSongs.route)
                                "playlists" -> navController.navigate(Screen.Playlists.route)
                                "artists" -> navController.navigate(Screen.Social.route)
                            }
                        }
                    )
                }
                composable(Screen.Search.route) { SearchScreen(onSongClick = { _: Song -> showNowPlaying = true }) }
                composable(Screen.Downloads.route) { DownloadsScreen(onSongClick = { _: Song -> showNowPlaying = true }) }
                composable(Screen.Playlists.route) {
                    PlaylistsScreen(onPlaylistClick = { playlist ->
                        navController.navigate("playlist_detail/${playlist.id}")
                    })
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onSettingsClick = { navController.navigate(Screen.Settings.route) },
                        onLogoutClick = { profileViewModel.logout() }
                    )
                }
                composable(Screen.Settings.route) { SettingsScreen(onBackClick = { navController.popBackStack() }) }
                composable(Screen.ChatList.route) {
                    ConversationsScreen(
                        onBackClick = { navController.popBackStack() },
                        onConversationClick = { userId -> navController.navigate("chat_detail/$userId") }
                    )
                }
                composable("chat_detail/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    ChatScreen(
                        otherUserId = userId,
                        onBackClick = { navController.popBackStack() },
                        onSongClick = { _: String -> showNowPlaying = true }
                    )
                }
                composable(Screen.PlaylistDetail.route) { backStackEntry ->
                    val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
                    PlaylistDetailScreen(
                        playlistId = playlistId,
                        onBackClick = { navController.popBackStack() },
                        onSongClick = { _: Song -> showNowPlaying = true }
                    )
                }
                composable(Screen.LikedSongs.route) {
                    LikedSongsScreen(onBackClick = { navController.popBackStack() }, onSongClick = { _: Song -> showNowPlaying = true })
                }
                composable(Screen.Social.route) {
                    SocialScreen(onBackClick = { navController.popBackStack() })
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