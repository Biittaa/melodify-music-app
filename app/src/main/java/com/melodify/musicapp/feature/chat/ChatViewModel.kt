package com.melodify.musicapp.feature.chat


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.melodify.musicapp.domain.model.Message
import com.melodify.musicapp.domain.model.Song
import com.melodify.musicapp.domain.model.User
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    // TODO: بعداً Repositoryها رو اضافه کنید
    // private val chatRepository: ChatRepository,
    // private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ChatEvent>()

    init {
        collectEvents()
    }

    private fun collectEvents() {
        viewModelScope.launch {
            _events.collect { event ->
                when (event) {
                    is ChatEvent.OnSendMessage -> sendMessage(event.text)
                    is ChatEvent.OnSendSong -> sendSong(event.song)
                    ChatEvent.OnRefresh -> refreshChat()
                }
            }
        }
    }

    fun onEvent(event: ChatEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    fun loadChat(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // TODO: جایگزین با کد واقعی از Repository
                // val messages = chatRepository.getMessages(userId)
                // val user = userRepository.getUser(userId)

                // داده‌های تستی موقت
                val mockMessages = listOf(
                    Message(
                        id = "1",
                        senderId = userId,
                        receiverId = "current_user",
                        text = "سلام! چطوری؟",
                        createdAt = System.currentTimeMillis() - 60000,
                        isSeen = true
                    ),
                    Message(
                        id = "2",
                        senderId = "current_user",
                        receiverId = userId,
                        text = "سلام! خوبم ممنون، تو؟",
                        createdAt = System.currentTimeMillis() - 30000,
                        isSeen = true
                    ),
                    Message(
                        id = "3",
                        senderId = userId,
                        receiverId = "current_user",
                        text = "خوبم! آهنگ جدید شنیدی؟",
                        createdAt = System.currentTimeMillis() - 10000,
                        isSeen = false
                    )
                )

                val mockUser = User(
                    id = userId,
                    username = "testuser",
                    fullName = "کاربر تست",
                    email = "test@example.com",
                    profileImage = "https://picsum.photos/seed/user$userId/200/200",
                    bio = "عاشق موسیقی 🎵",
                    followersCount = 120,
                    followingCount = 85,
                    playlistsCount = 5,
                    isPremium = true,
                    isFollowing = false
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        messages = mockMessages,
                        otherUser = mockUser,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "خطا در بارگذاری چت"
                    )
                }
            }
        }
    }

    private fun sendMessage(text: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }

            try {
                // TODO: جایگزین با کد واقعی از Repository
                // chatRepository.sendMessage(receiverId, text)

                val newMessage = Message(
                    id = System.currentTimeMillis().toString(),
                    senderId = "current_user",
                    receiverId = _uiState.value.otherUser?.id ?: "",
                    text = text,
                    createdAt = System.currentTimeMillis(),
                    isSeen = false
                )

                _uiState.update {
                    it.copy(
                        messages = it.messages + newMessage,
                        isSending = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSending = false,
                        error = e.message ?: "خطا در ارسال پیام"
                    )
                }
            }
        }
    }

    private fun sendSong(song: Song) {
        viewModelScope.launch {
            try {
                // TODO: جایگزین با کد واقعی از Repository
                // chatRepository.sendSong(receiverId, song.id)

                val newMessage = Message(
                    id = System.currentTimeMillis().toString(),
                    senderId = "current_user",
                    receiverId = _uiState.value.otherUser?.id ?: "",
                    text = "🎵 ${song.title} - ${song.artistId}",
                    songId = song.id,
                    createdAt = System.currentTimeMillis(),
                    isSeen = false
                )

                _uiState.update {
                    it.copy(
                        messages = it.messages + newMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "خطا در اشتراک آهنگ"
                    )
                }
            }
        }
    }

    private fun refreshChat() {
        val userId = _uiState.value.otherUser?.id
        if (userId != null) {
            loadChat(userId)
        }
    }

    // تابع برای شبیه‌سازی تایپ کردن (برای دمو)
    fun simulateTyping(isTyping: Boolean) {
        _uiState.update { it.copy(isTyping = isTyping) }
    }
}