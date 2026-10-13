package com.example.garden.repository.auth

import com.example.garden.AniLibriaClient
import com.example.garden.appsettings.AuthManager
import com.example.garden.appsettings.AuthState
import com.github.wirye.anilibriakt.model.UserProfile
import com.github.wirye.anilibriakt.model.UserProfileFields
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(private val authManager: AuthManager) : AuthRepository {
    val authStateFlow: Flow<AuthState> = authManager.authStateFlow

    override suspend fun login(login: String, password: String): Result<String> = AniLibriaClient.account.auth.login(login, password)

    override suspend fun getProfile(requestedData: List<UserProfileFields>): Result<UserProfile> = AniLibriaClient.account.auth.getProfile(requestedData)

    override suspend fun clearAniLibriaSession() {
        AniLibriaClient.account.auth.logout()
        authManager.clearAniLibriaSession()
    }

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
