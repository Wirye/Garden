package com.example.garden.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.garden.appsettings.AuthState
import com.example.garden.repository.auth.AuthRepositoryImpl
import com.github.wirye.anilibriakt.model.UserProfileFields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {

    val uiState: StateFlow<AuthState> = authRepository.authStateFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState()
        )

    fun logoutAniLiberty() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.clearAniLibriaSession()
        }
    }

    fun refreshAniLibertyUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.getProfile(UserProfileFields.entries.toList())
                .onSuccess { profile ->
                    authRepository.updateAniLibriaProfile(
                        nickName = profile.nickname,
                        avatarUrl = profile.avatar?.fullPreviewUrl
                    )
                }
                .onFailure { error ->
                    Log.e("AuthViewModel", "Не удалось обновить профиль AniLibria: ${error.message}")
                }
        }
    }

    suspend fun login(login: String, password: String) = authRepository.login(login, password).onSuccess { token ->
        authRepository.saveAniLibriaSession(token)
    }
}