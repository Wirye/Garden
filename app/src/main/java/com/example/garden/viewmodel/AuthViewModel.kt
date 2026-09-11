package com.example.garden.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.garden.appsettings.AuthManager
import com.example.garden.appsettings.AuthState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authManager: AuthManager
) : ViewModel() {

    val uiState: StateFlow<AuthState> = authManager.authStateFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState()
        )

    fun onGoogleSignInSuccess(
        email: String,
        avatarUrl: String?,
        nickName: String?,
        token: String?,
        cookies: String?
    ) {
        viewModelScope.launch {
            authManager.saveGoogleSession(
                email = email,
                avatarUrl = avatarUrl,
                nickName = nickName,
                token = token,
                cookies = cookies
            )
        }
    }

    fun logoutGoogle() {
        viewModelScope.launch {
            authManager.clearGoogleSession()
        }
    }

    fun onAniLibertySignInSuccess(
        token: String,
        cookies: String,
        nickName: String?,
        avatarUrl: String?
    ) {
        viewModelScope.launch {
            authManager.saveAniLibertySession(
                token = token,
                cookies = cookies,
                nickName = nickName,
                avatarUrl = avatarUrl
            )
        }
    }

    fun logoutAniLiberty() {
        viewModelScope.launch {
            authManager.clearAniLibertySession()
        }
    }

    fun logoutAll() {
        viewModelScope.launch {
            authManager.clearAll()
        }
    }
}