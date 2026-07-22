package com.melodify.musicapp.feature.profile  // <-- اصلاح: com.example به com.melodify

import androidx.compose.foundation.clickable
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
import com.example.melodify.musicapp.feature.profile.OtherUserProfileViewModel
import com.melodify.musicapp.R
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.ui.components.ImageViewerDialog  // <-- ایمپورت دیالوگ

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherUserProfileScreen(
    userId: String,
    onBackClick: () -> Unit,
    viewModel: OtherUserProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    // ============================================
    // ✅ متغیرهای مربوط به نمایش عکس بزرگ
    // ============================================
    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf("") }

    // ============================================
    // ✅ بارگذاری پروفایل
    // ============================================
    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
    }

    // ============================================
    // ✅ دیالوگ نمایش عکس بزرگ (قبل از Scaffold)
    // ============================================
    if (showImageDialog && selectedImageUrl.isNotEmpty()) {
        ImageViewerDialog(
            imageUrl = selectedImageUrl,
            onDismiss = { showImageDialog = false }
        )
    }

    // ============================================
    // ✅ Scaffold
    // ============================================
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
        when {
            // حالت لودینگ
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // حالت خطا
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "❌ Error loading profile",
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = uiState.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadUserProfile(userId) }) {
                            Text("Retry")
                        }
                    }
                }
            }

            // حالت موفق - نمایش پروفایل
            user != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ============================================
                    // ✅ Avatar - قابل کلیک برای بزرگنمایی
                    // ============================================
                    Box(
                        modifier = Modifier
                            .clickable {
                                user.profileImage.takeIf { it.isNotEmpty() }?.let {
                                    selectedImageUrl = it
                                    showImageDialog = true
                                }
                            }
                    ) {
                        AsyncImage(
                            model = user.profileImage.ifEmpty {
                                "https://www.w3schools.com/howto/img_avatar.png"
                            },
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

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

            // حالت پیش‌فرض - کاربر null
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("User not found")
                }
            }
        }
    }
}




//package com.example.melodify.musicapp.feature.profile
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import coil.compose.AsyncImage
//import com.melodify.musicapp.R
//import com.melodify.musicapp.domain.model.User
//import com.melodify.musicapp.feature.profile.ProfileStat
//import com.melodify.musicapp.ui.components.ImageViewerDialog
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun OtherUserProfileScreen(
//    userId: String,
//    onBackClick: () -> Unit,
//    viewModel: OtherUserProfileViewModel = hiltViewModel()
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    val user = uiState.user
//    var showImageDialog by remember { mutableStateOf(false) }
//    var selectedImageUrl by remember { mutableStateOf("") }
//
//    LaunchedEffect(userId) {
//        viewModel.loadUserProfile(userId)
//    }
//    if (showImageDialog && selectedImageUrl.isNotEmpty()) {
//        ImageViewerDialog(
//            imageUrl = selectedImageUrl,
//            onDismiss = { showImageDialog = false }
//        )
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Profile", fontWeight = FontWeight.Bold) },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        if (uiState.isLoading) {
//            Box(
//                modifier = Modifier.clickable {
//                    user?.profileImage.takeIf { it.isNotEmpty() }?.let {
//                        selectedImageUrl = it
//                        showImageDialog = true
//                    }
//                }
//            ) {
//                CircularProgressIndicator()
//            }
//        } else if (user != null) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding)
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                // Avatar
//                AsyncImage(
//                    model = user.profileImage.ifEmpty { "https://www.w3schools.com/howto/img_avatar.png" },
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(120.dp)
//                        .clip(CircleShape),
//                    contentScale = ContentScale.Crop
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Name & Username
//                Text(
//                    text = user.fullName,
//                    style = MaterialTheme.typography.headlineMedium,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = "@${user.username}",
//                    color = Color.Gray
//                )
//
//                // Bio
//                if (user.bio.isNotEmpty()) {
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        text = user.bio,
//                        style = MaterialTheme.typography.bodyMedium,
//                        modifier = Modifier.padding(horizontal = 16.dp)
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                // Stats
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    ProfileStat(label = "Followers", value = user.followersCount.toString())
//                    ProfileStat(label = "Following", value = user.followingCount.toString())
//                    ProfileStat(label = "Playlists", value = user.playlistsCount.toString())
//                }
//
//                Spacer(modifier = Modifier.height(32.dp))
//
//                // Follow/Unfollow Button
//                Button(
//                    onClick = {
//                        if (user.isFollowing) {
//                            viewModel.unfollowUser(userId)
//                        } else {
//                            viewModel.followUser(userId)
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = if (user.isFollowing)
//                            MaterialTheme.colorScheme.secondary
//                        else
//                            MaterialTheme.colorScheme.primary
//                    )
//                ) {
//                    Text(
//                        text = if (user.isFollowing) "Unfollow" else "Follow",
//                        color = if (user.isFollowing)
//                            MaterialTheme.colorScheme.onSecondary
//                        else
//                            MaterialTheme.colorScheme.onPrimary
//                    )
//                }
//            }
//        }
//    }
//}


