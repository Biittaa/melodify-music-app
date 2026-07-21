package com.melodify.musicapp.feature.chat

import com.melodify.musicapp.domain.model.Message
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.model.Song

data class ChatUiState(
    val isLoading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val otherUser: User? = null,
    val isTyping: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null
)

sealed class ChatEvent {
    data class OnSendMessage(val text: String) : ChatEvent()
    data class OnSendSong(val song: Song) : ChatEvent()
    object OnRefresh : ChatEvent()
}