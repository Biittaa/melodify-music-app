package com.melodify.musicapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.melodify.musicapp.navigation.Screen
import com.melodify.musicapp.ui.theme.MelodifyColors
import com.melodify.musicapp.R
import androidx.compose.runtime.getValue

@Composable
fun MelodifyBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        NavigationItem(Screen.Home.route, R.drawable.ic_home, R.string.nav_home),
        NavigationItem(Screen.Search.route, R.drawable.ic_search, R.string.nav_search),
        NavigationItem(Screen.Downloads.route, R.drawable.ic_download, R.string.nav_downloads),
        NavigationItem(Screen.Playlist.route, R.drawable.ic_playlist, R.string.nav_playlists),
        NavigationItem(Screen.Profile.route, R.drawable.ic_profile, R.string.nav_profile)
    )

    NavigationBar(
        modifier = modifier.shadow(elevation = 8.dp),
        containerColor = MelodifyColors().surface,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = stringResource(id = item.label)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = item.label),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MelodifyColors().primary,
                    selectedTextColor = MelodifyColors().primary,
                    unselectedIconColor = MelodifyColors().onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MelodifyColors().onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}

data class NavigationItem(val route: String, val icon: Int, val label: Int)