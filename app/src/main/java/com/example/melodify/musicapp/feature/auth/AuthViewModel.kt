package com.melodify.musicapp.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodify.musicapp.core.common.Result
import com.melodify.musicapp.domain.model.User
import com.melodify.musicapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, user = result.data, isSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.exception.message) }
                }
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.register(username, email, password)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, user = result.data, isSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.exception.message) }
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
    }
}
