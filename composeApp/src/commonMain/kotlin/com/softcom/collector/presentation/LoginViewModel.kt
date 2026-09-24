package com.softcom.collector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softcom.collector.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberPassword: Boolean = false,
    val passwordVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val feedback: String = "",
)

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        authRepository.loadSavedCredentials()?.let { credentials ->
            _uiState.value = LoginUiState(
                email = credentials.email,
                password = credentials.password,
                rememberPassword = true,
            )
        }
    }

    fun setEmail(value: String) = _uiState.update { it.copy(email = value) }
    fun setPassword(value: String) = _uiState.update { it.copy(password = value) }
    fun toggleRememberPassword() = _uiState.update { it.copy(rememberPassword = !it.rememberPassword) }
    fun togglePasswordVisibility() = _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }

    fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(feedback = "Informe o e-mail e a senha para continuar.") }
            return
        }
        if (state.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, feedback = "") }
            runCatching {
                authRepository.login(state.email, state.password, state.rememberPassword)
            }.onSuccess {
                _uiState.update { it.copy(isSubmitting = false, feedback = "Login realizado com sucesso.") }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        feedback = error.message ?: "Não foi possível realizar o login.",
                    )
                }
            }
        }
    }
}
