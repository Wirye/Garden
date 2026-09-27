package com.example.garden.repository.auth

import com.example.garden.appsettings.AuthManager
import com.example.garden.appsettings.AuthState
import com.github.wirye.anilibriakt.AniLibriaClient
import com.github.wirye.anilibriakt.model.UserProfile
import com.github.wirye.anilibriakt.model.UserProfileFields
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(private val authManager: AuthManager) : AuthRepository {
    val authStateFlow: Flow<AuthState> = authManager.authStateFlow

    private val anilibriaApi = AniLibriaClient(tokenProvider = { authManager.getToken() })

    override suspend fun login(login: String, password: String): Result<String> = anilibriaApi.auth.login(login, password)

    override suspend fun getProfile(requestedData: List<UserProfileFields>): Result<UserProfile> = anilibriaApi.auth.getProfile(requestedData)

    override suspend fun clearAniLibriaSession() = authManager.clearAniLibriaSession()

    override suspend fun saveAniLibriaSession(
        token: String?,
        cookies: String?,
        nickName: String?,
        avatarUrl: String?
    ) = authManager.saveAniLibriaSession(token, cookies, nickName, avatarUrl)

    override suspend fun updateAniLibriaProfile(
        avatarUrl: String?,
        nickName: String?
    ) = authManager.updateAniLibriaProfile(avatarUrl, nickName)
}