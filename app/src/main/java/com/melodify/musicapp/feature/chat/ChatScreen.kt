package com.melodify.musicapp.feature.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.melodify.musicapp.R
import com.melodify.musicapp.domain.model.Message
import com.melodify.musicapp.core.ui.components.MelodifyTopBar
import com.melodify.musicapp.core.ui.theme.MelodifyColors
import com.melodify.musicapp.core.ui.theme.MelodifyGreen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    navController: NavController,
    userId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = userId) {
        viewModel.loadChat(userId)
    }

    LaunchedEffect(key1 = uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            MelodifyTopBar(
                title = uiState.otherUser?.fullName ?: "Chat",
                showActions = false,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = MelodifyColors().background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                reverseLayout = false,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    items = uiState.messages,
                    key = { it.id }
                ) { message ->
                    val isOwnMessage = message.senderId == "current_user"
                    MessageBubble(
                        message = message,
                        isOwnMessage = isOwnMessage
                    )
                }

                if (uiState.isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            MessageInput(
                onSendMessage = { text ->
                    viewModel.onEvent(ChatEvent.OnSendMessage(text))
                },
                onSendSong = { song ->
                    viewModel.onEvent(ChatEvent.OnSendSong(song))
                },
                isSending = uiState.isSending
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isOwnMessage: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(horizontal = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isOwnMessage)
                    MelodifyGreen
                else
                    MelodifyColors().surface
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isOwnMessage) 16.dp else 4.dp,
                bottomEnd = if (isOwnMessage) 4.dp else 16.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                message.songId?.let { songId ->
                    SharedSongCard(
                        songId = songId,
                        isOwnMessage = isOwnMessage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                if (message.text.isNotEmpty()) {
                    Text(
                        text = message.text,
                        color = if (isOwnMessage) Color.White else MelodifyColors().onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = formatTime(message.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isOwnMessage)
                            Color.White.copy(alpha = 0.7f)
                        else
                            MelodifyColors().onSurface.copy(alpha = 0.5f)
                    )

                    if (isOwnMessage) {
                        Spacer(modifier = Modifier.width(4.dp))
                        if (message.isSeen) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SharedSongCard(
    songId: String,
    isOwnMessage: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://picsum.photos/seed/$songId/50/50")
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = "Song Title",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOwnMessage) Color.White else MelodifyColors().onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Artist Name",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOwnMessage)
                        Color.White.copy(alpha = 0.7f)
                    else
                        MelodifyColors().onSurface.copy(alpha = 0.5f)
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = stringResource(R.string.play),
                tint = if (isOwnMessage) Color.White else MelodifyGreen
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MelodifyColors().surface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    val delay = index * 200L
                    val animatedAlpha by animateFloatAsState(
                        targetValue = if (System.currentTimeMillis() % 1200L > delay) 1f else 0.3f,
                        animationSpec = tween(300)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = animatedAlpha))
                    )
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    onSendSong: (Song) -> Unit,
    isSending: Boolean,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MelodifyColors().surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* Show song picker */ }
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Share Song",
                tint = MelodifyGreen
            )
        }

        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .background(
                    MelodifyColors().surface.copy(alpha = 0.3f),
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = "Type a message...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MelodifyColors().onSurface.copy(alpha = 0.5f)
                    )
                }
                innerTextField()
            }
        )

        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSendMessage(text)
                    text = ""
                }
            },
            enabled = text.isNotBlank() && !isSending
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = if (text.isNotBlank()) MelodifyGreen else MelodifyColors().onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}