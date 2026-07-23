package com.melodify.musicapp.feature.chat

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.melodify.musicapp.domain.model.Conversation
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.*
import com.melodify.musicapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsScreen(
    onBackClick: () -> Unit,
    onConversationClick: (String) -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showStartChat by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadConversations()
    }

    if (showStartChat) {
        StartChatScreen(
            onDismiss = { showStartChat = false },
            onUserSelected = { user ->
                showStartChat = false
                onConversationClick(user.id)
            },
            viewModel = viewModel
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.loadFollowedUsers() // لیست کاربران فالوشده را لود میکند
                        showStartChat = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "New Chat")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.conversations.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No conversations yet", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(uiState.conversations) { conversation ->
                    ConversationItem(
                        conversation = conversation,
                        userRepository = viewModel.userRepository,
                        onClick = { onConversationClick(conversation.userId) }
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    userRepository: UserRepository, // تایپ دقیق ورودی اضافه شد
    onClick: () -> Unit
) {
    var otherUser by remember { mutableStateOf<User?>(null) }

    // دریافت مشخصات کاربر به صورت suspend (بدون collect)
    LaunchedEffect(conversation.userId) {
        try {
            otherUser = userRepository.getProfile(conversation.userId)
            Log.d("Chat", "conversation.userId = ${conversation.userId}")

            otherUser = userRepository.getProfile(conversation.userId)

            Log.d("Chat", "otherUser = $otherUser")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Log.d("Chat2", "otherUser = $otherUser")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = otherUser?.profileImage?.ifEmpty { "https://www.w3schools.com/howto/img_avatar.png" }
                ?: "https://www.w3schools.com/howto/img_avatar.png",
            contentDescription = null,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

//        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = otherUser?.fullName ?: stringResource(R.string.unknown_user),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = conversation.lastMessage,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(Date(conversation.lastMessageTime)),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            if (conversation.unreadCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Badge(
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Text(
                        text = conversation.unreadCount.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}