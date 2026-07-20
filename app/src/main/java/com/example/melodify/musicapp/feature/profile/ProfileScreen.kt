package com.melodify.musicapp.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.melodify.musicapp.R
import com.melodify.musicapp.ui.theme.PremiumGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = user?.profileImage ?: "https://www.w3schools.com/howto/img_avatar.png",
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { /* Change Avatar */ },
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name & Premium Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user?.fullName ?: "Guest User",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                if (uiState.isPremium) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Premium",
                        tint = PremiumGold
                    )
                }
            }
            Text(text = "@${user?.username ?: "guest"}", color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(label = "Followers", value = user?.followersCount?.toString() ?: "0")
                ProfileStat(label = "Following", value = user?.followingCount?.toString() ?: "0")
                ProfileStat(label = "Playlists", value = user?.playlistsCount?.toString() ?: "0")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Premium Section
            if (!uiState.isPremium) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PremiumGold.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PremiumGold),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.upgrade_to_premium),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB8860B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Download songs for offline listening and enjoy ad-free music.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.upgradeToPremium() },
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Get Premium", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = PremiumGold.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFB8860B))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "You are a Premium Member",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB8860B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout
            TextButton(
                onClick = onLogoutClick,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.logout))
            }
        }
    }
}

@Composable
fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}
