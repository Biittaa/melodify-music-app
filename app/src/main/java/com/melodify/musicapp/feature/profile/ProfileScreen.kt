package com.melodify.musicapp.feature.profile


import com.melodify.musicapp.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.melodify.musicapp.navigation.Screen
<<<<<<< Updated upstream
import com.melodify.musicapp.ui.components.MelodifyTopBar
import com.melodify.musicapp.ui.theme.MelodifyColors
import com.melodify.musicapp.ui.theme.Space12
import com.melodify.musicapp.ui.theme.Space16
import com.melodify.musicapp.ui.theme.Space8
=======
import com.melodify.musicapp.core.ui.components.MelodifyTopBar
import com.melodify.musicapp.core.ui.theme.MelodifyColors
import com.melodify.musicapp.core.ui.theme.Space12
import com.melodify.musicapp.core.ui.theme.Space16
import com.melodify.musicapp.core.ui.theme.Space8
>>>>>>> Stashed changes

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MelodifyTopBar(
                title = stringResource(R.string.nav_profile),
                showActions = false,
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        },
        containerColor = MelodifyColors().background
    ) { paddingValues ->
        ProfileContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onNavigateTo = { route ->
                navController.navigate(route)
            }
        )
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    uiState: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    onNavigateTo: (String) -> Unit
) {
    val user = uiState.user

    if (user == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MelodifyColors().primary)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MelodifyColors().background)
            .padding(Space16)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.profileImage)
                    .crossfade(true)
                    .build(),
                contentDescription = user.fullName,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { onEvent(ProfileEvent.OnEditProfile) },
                contentScale = ContentScale.Crop
            )

            // Premium Badge
            if (uiState.isPremium) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 24.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD700))
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.premium),
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.Companion.height(Space16))

        // Name
        Text(
            text = user.fullName,
            style = MaterialTheme.typography.titleLarge,
            color = MelodifyColors().onSurface,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "@${user.username}",
            style = MaterialTheme.typography.bodyMedium,
            color = MelodifyColors().onSurface.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (user.bio.isNotEmpty()) {
            Text(
                text = user.bio,
                style = MaterialTheme.typography.bodySmall,
                color = MelodifyColors().onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Space8)
            )
        }

        Spacer(modifier = Modifier.Companion.height(Space16))

        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = stringResource(R.string.followers),
                count = user.followersCount,
                onClick = { /* Navigate to followers */ }
            )
            StatItem(
                label = stringResource(R.string.following),
                count = user.followingCount,
                onClick = { /* Navigate to following */ }
            )
            StatItem(
                label = stringResource(R.string.playlists),
                count = user.playlistsCount,
                onClick = { /* Navigate to playlists */ }
            )
        }

        Spacer(modifier = Modifier.Companion.height(Space16))

        // Premium Button
        if (!uiState.isPremium) {
            Button(
                onClick = { onEvent(ProfileEvent.OnUpgradePremium) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MelodifyColors().primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.Companion.width(Space8))
                Text(text = stringResource(R.string.upgrade_to_premium))
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MelodifyColors().primary.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Space16),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MelodifyColors().primary
                    )
                    Spacer(modifier = Modifier.Companion.width(Space8))
                    Text(
                        text = stringResource(R.string.premium_active),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MelodifyColors().primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.Companion.height(Space16))

        // Menu Items
        ProfileMenuItem(
            icon = Icons.Default.Settings,
            title = stringResource(R.string.settings),
            onClick = { onEvent(ProfileEvent.OnSettings) }
        )

        ProfileMenuItem(
            icon = Icons.Default.Logout,
            title = stringResource(R.string.logout),
            onClick = { onEvent(ProfileEvent.OnLogout) },
            textColor = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun StatItem(
    label: String,
    count: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MelodifyColors().onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MelodifyColors().onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    textColor: Color = MelodifyColors().onSurface
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MelodifyColors().surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Space16),
            horizontalArrangement = Arrangement.spacedBy(Space12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = textColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
        }
    }
}