package com.melodify.musicapp.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.melodify.musicapp.R
import com.melodify.musicapp.domain.model.Message
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    otherUserId: String,
    onBackClick: () -> Unit,
    onSongClick: (String) -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val otherUserTyping by viewModel.otherUserTyping.collectAsState()
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(otherUserId) {
        viewModel.observeMessages(otherUserId)
    }

    LaunchedEffect(messageText) {
        viewModel.setTypingStatus(otherUserId, messageText.isNotEmpty())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(R.string.profile) + " $otherUserId", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        if (otherUserTyping) {
                            Text(text = "is typing...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            ChatInput(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(otherUserId, messageText)
                        messageText = ""
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            reverseLayout = true,
            contentPadding = PaddingValues(16.dp)
        ) {
            items(uiState.messages.reversed()) { message ->
                MessageBubble(
                    message = message,
                    isMine = message.senderId != otherUserId,
                    onSongClick = onSongClick
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isMine: Boolean, onSongClick: (String) -> Unit) {
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isMine) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Surface(
                color = color,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMine) 16.dp else 0.dp,
                    bottomEnd = if (isMine) 0.dp else 16.dp
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (message.songId != null) {
                        SongShareCard(songId = message.songId, onClick = { onSongClick(message.songId) })
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (message.text.isNotBlank()) {
                        Text(text = message.text, color = contentColor)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                if (isMine) {
                    Spacer(modifier = Modifier.width(4.dp))
                    val statusIcon = when {
                        !message.isSent -> Icons.Default.AccessTime  // Sending
                        message.isSeen -> Icons.Default.DoneAll       // Read / Seen
                        else -> Icons.Default.Done                   // Sent
                    }
                    val statusColor = if (message.isSeen) Color.Cyan else Color.Gray

                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = statusColor
                    )
                }
            }
        }
    }
}

@Composable
fun SongShareCard(songId: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = Color.Black.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = stringResource(R.string.trending), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "Play Song", color = Color.LightGray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun ChatInput(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.imePadding()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = onMessageChange,
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            IconButton(onClick = onSendClick) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}