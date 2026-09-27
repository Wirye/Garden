package com.example.garden.repository.auth

import com.github.wirye.anilibriakt.model.UserProfile
import com.github.wirye.anilibriakt.model.UserProfileFields

interface AuthRepository {
    suspend fun clearAniLibriaSession()
    suspend fun saveAniLibriaSession(
        token: String?,
        cookies: String? = null,
        nickName: String? = null,
        avatarUrl: String? = null
    )
    suspend fun updateAniLibriaProfile(
        avatarUrl: String?,
        nickName: String?
    )
    suspend fun login(login: String, password: String): Result<String>
    suspend fun getProfile(
        requestedData: List<UserProfileFields>
    ): Result<UserProfile>
}