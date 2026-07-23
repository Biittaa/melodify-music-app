package com.melodify.musicapp.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.core.common.CurrentUserProvider
import com.melodify.musicapp.domain.model.Conversation
import com.melodify.musicapp.domain.model.Message
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.ChatRepository
import com.melodify.musicapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val conversations: List<Conversation> = emptyList(),
    val messages: List<Message> = emptyList(),
    val followedUsers: List<User> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _otherUserTyping = MutableStateFlow(false)
    val otherUserTyping: StateFlow<Boolean> = _otherUserTyping.asStateFlow()

    init {
        loadFollowedUsers()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            chatRepository.getConversationsFlow().collectLatest { convs ->
                _uiState.update { it.copy(conversations = convs, isLoading = false) }
            }
        }
    }

    fun observeMessages(otherUserId: String) {
        viewModelScope.launch {
            chatRepository.observeMessages(otherUserId).collectLatest { msgs ->
                _uiState.update { it.copy(messages = msgs) }
                msgs.filter { !it.isSeen && it.senderId == otherUserId }
                    .forEach { message ->
                        chatRepository.markAsRead(message.id)
                    }
                chatRepository.markConversationAsRead(otherUserId)
            }
        }
        viewModelScope.launch {
            chatRepository.observeTypingStatus(otherUserId).collectLatest { isTyping ->
                _otherUserTyping.value = isTyping
            }
        }
    }

    fun sendMessage(receiverId: String, text: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(receiverId, text)
        }
    }

    // FIXED: changed viewModelScope.last → viewModelScope.launch
    fun setTypingStatus(receiverId: String, isTyping: Boolean) {
        viewModelScope.launch {
            chatRepository.setTypingStatus(receiverId, isTyping)
        }
    }

    fun sendSong(receiverId: String, songId: String) {
        viewModelScope.launch {
            chatRepository.sendSong(receiverId, songId)
        }
    }

    private fun loadFollowedUsers() {
        viewModelScope.launch {
            val currentUser = currentUserProvider.getCurrentUser()
            if (currentUser != null) {
                val following = userRepository.getUserFollowing(currentUser.id)
                _uiState.update { it.copy(followedUsers = following) }
            }
        }
    }
}