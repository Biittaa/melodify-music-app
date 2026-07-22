package com.melodify.musicapp.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.melodify.musicapp.R
import com.melodify.musicapp.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherUserProfileScreen(
    userId: String,
    onBackClick: () -> Unit,
    viewModel: OtherUserProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                AsyncImage(
                    model = user.profileImage.ifEmpty { "https://www.w3schools.com/howto/img_avatar.png" },
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Name & Username
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@${user.username}",
                    color = Color.Gray
                )

                // Bio
                if (user.bio.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat(label = "Followers", value = user.followersCount.toString())
                    ProfileStat(label = "Following", value = user.followingCount.toString())
                    ProfileStat(label = "Playlists", value = user.playlistsCount.toString())
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Follow/Unfollow Button
                Button(
                    onClick = {
                        if (user.isFollowing) {
                            viewModel.unfollowUser(userId)
                        } else {
                            viewModel.followUser(userId)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (user.isFollowing)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (user.isFollowing) "Unfollow" else "Follow",
                        color = if (user.isFollowing)
                            MaterialTheme.colorScheme.onSecondary
                        else
                            MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}