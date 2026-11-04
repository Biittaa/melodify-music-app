package com.melodify.musicapp.feature.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.melodify.musicapp.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartChatScreen(
    onDismiss: () -> Unit,
    onUserSelected: (User) -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "New Message",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.followedUsers.isEmpty()) {
                Text(
                    text = "You are not following anyone yet.",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(uiState.followedUsers) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUserSelected(user) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = user.profileImage.ifEmpty { "https://www.w3schools.com/howto/img_avatar.png" },
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = user.fullName,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "@${user.username}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}