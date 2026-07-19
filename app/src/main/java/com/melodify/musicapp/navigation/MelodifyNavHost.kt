<<<<<<< Updated upstream
//package com.melodify.musicapp.navigation
//
//import androidx.compose.animation.*
//import androidx.compose.animation.core.tween
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.melodify.musicapp.feature.downloads.DownloadsScreen
//import com.melodify.musicapp.feature.home.HomeScreen
//import com.melodify.musicapp.feature.home.HomeViewModel
//import com.melodify.musicapp.feature.playlist.PlaylistScreen
//import com.melodify.musicapp.feature.profile.ProfileScreen
//import com.melodify.musicapp.feature.search.SearchScreen
//import com.melodify.musicapp.feature.player.PlayerScreen
//import com.melodify.musicapp.feature.chat.ChatScreen
//import com.melodify.musicapp.feature.settings.SettingsScreen
//
//@Composable
//fun MelodifyNavHost(
//    startDestination: String = Screen.Home.route
//) {
//    val navController = rememberNavController()
//    val bottomBarState = remember { mutableStateOf(true) }
//
//    NavHost(
//        navController = navController,
//        startDestination = startDestination,
//        enterTransition = {
//            fadeIn(animationSpec = tween(300)) +
//                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
//        },
//        exitTransition = {
//            fadeOut(animationSpec = tween(300)) +
//                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
//        }
//    ) {
//        composable(Screen.Home.route) {
//            val viewModel: HomeViewModel = hiltViewModel()
//            HomeScreen(navController = navController, viewModel = viewModel)
//        }
//
//        composable(Screen.Search.route) {
//            SearchScreen(navController = navController)
//        }
//
//        composable(Screen.Downloads.route) {
//            DownloadsScreen(navController = navController)
//        }
//
//        composable(Screen.Playlist.route) {
//            PlaylistScreen(navController = navController)
//        }
//
//        composable(Screen.Profile.route) {
//            ProfileScreen(navController = navController)
//        }
//
//        composable(
//            route = Screen.Player.route,
//            arguments = listOf(navArgument("songId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val songId = backStackEntry.arguments?.getString("songId") ?: ""
//            PlayerScreen(navController = navController, songId = songId)
//        }
//
//        composable(
//            route = Screen.Chat.route,
//            arguments = listOf(navArgument("userId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val userId = backStackEntry.arguments?.getString("userId") ?: ""
//            ChatScreen(navController = navController, userId = userId)
//        }
//
//        composable(Screen.Settings.route) {
//            SettingsScreen(navController = navController)
//        }
//
//        composable(Screen.LikedSongs.route) {
//            // LikedSongsScreen
//        }
//
//        composable(Screen.RecentlyPlayed.route) {
//            // RecentlyPlayedScreen
//        }
//
//        composable(Screen.Artists.route) {
//            // ArtistsScreen
//        }
//
//        composable(Screen.Following.route) {
//            // FollowingScreen
//        }
//    }
//}


=======
>>>>>>> Stashed changes
package com.melodify.musicapp.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.melodify.musicapp.feature.chat.ChatScreen
import com.melodify.musicapp.feature.downloads.DownloadsScreen
import com.melodify.musicapp.feature.home.HomeScreen
import com.melodify.musicapp.feature.home.HomeViewModel
import com.melodify.musicapp.feature.player.PlayerScreen
import com.melodify.musicapp.feature.playlist.PlaylistScreen
import com.melodify.musicapp.feature.profile.ProfileScreen
import com.melodify.musicapp.feature.search.SearchScreen
import com.melodify.musicapp.feature.settings.SettingsScreen

@Composable
fun MelodifyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route
) {
    val bottomBarState = remember { mutableStateOf(true) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) +
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) +
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(300)
                    )
        }
    ) {

        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }

        composable(Screen.Downloads.route) {
            DownloadsScreen(navController = navController)
        }

        composable(Screen.Playlist.route) {
            PlaylistScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("songId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val songId =
                backStackEntry.arguments?.getString("songId") ?: ""

            PlayerScreen(
                navController = navController,
                songId = songId
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val userId =
                backStackEntry.arguments?.getString("userId") ?: ""

            ChatScreen(
                navController = navController,
                userId = userId
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(Screen.LikedSongs.route) {
            // TODO
        }

        composable(Screen.RecentlyPlayed.route) {
            // TODO
        }

        composable(Screen.Artists.route) {
            // TODO
        }

        composable(Screen.Following.route) {
            // TODO
        }
    }
}