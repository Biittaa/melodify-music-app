package com.melodify.musicapp.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.domain.model.Conversation
import com.melodify.musicapp.domain.model.Message
import com.melodify.musicapp.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val conversations: List<Conversation> = emptyList(),
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val isTyping: Boolean = false
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val convs = chatRepository.getConversations()
            _uiState.update { it.copy(conversations = convs, isLoading = false) }
        }
    }

    fun observeMessages(otherUserId: String) {
        viewModelScope.launch {
            chatRepository.observeMessages(otherUserId).collectLatest { msgs ->
                _uiState.update { it.copy(messages = msgs) }
            }
        }
    }

    fun sendMessage(receiverId: String, text: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(receiverId, text)
        }
    }
    
    fun sendSong(receiverId: String, songId: String) {
        viewModelScope.launch {
            chatRepository.sendSong(receiverId, songId)
        }
    }
}
