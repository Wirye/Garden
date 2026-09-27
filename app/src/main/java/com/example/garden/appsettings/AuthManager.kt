package com.example.garden.appsettings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.nio.charset.StandardCharsets

class CryptoManager(context: Context) {

    private val aead: Aead

    init {
        AeadConfig.register()
        aead = initAeadWithFallback(context)
    }

    private fun initAeadWithFallback(context: Context): Aead {
        return try {
            buildAead(context)
        } catch (_: Exception) {
            context.getSharedPreferences("tink_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            buildAead(context)
        }
    }

    private fun buildAead(context: Context): Aead {
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, "tink_keyset", "tink_prefs")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://_androidx_security_master_key_")
            .build()
            .keysetHandle

        return keysetHandle.getPrimitive(Aead::class.java)
    }

    fun encrypt(data: String): String {
        if (data.isBlank()) return ""
        val encryptedBytes = aead.encrypt(data.toByteArray(StandardCharsets.UTF_8), null)
        return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
    }

    fun decrypt(encryptedData: String): String {
        if (encryptedData.isBlank()) return ""
        return try {
            val decodedBytes =
                android.util.Base64.decode(encryptedData, android.util.Base64.DEFAULT)
            val decryptedBytes = aead.decrypt(decodedBytes, null)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (_: Exception) {
            ""
        }
    }
}

data class AuthState(
    val aniLibriaAvatarUrl: String? = null,
    val aniLibriaNickName: String? = null,
    val aniLibriaToken: String? = null
) {
    val isAniLibriaAuthorized: Boolean
        get() = !aniLibriaToken.isNullOrBlank()
}

private val Context.dataStore by preferencesDataStore(name = "auth_settings")

class AuthManager(private val context: Context, appScope: CoroutineScope) {

    private val cryptoManager = CryptoManager(context)

    companion object {
        private val KEY_ANILIBRIA_AVATAR = stringPreferencesKey("ani_avatar")
        private val KEY_ANILIBRIA_NICKNAME = stringPreferencesKey("ani_nickname")
        private val KEY_ANILIBRIA_TOKEN = stringPreferencesKey("ani_token")
        private val KEY_ANILIBRIA_COOKIES = stringPreferencesKey("ani_cookies")
    }

    val authStateFlow: Flow<AuthState> = context.dataStore.data.map { prefs ->
        AuthState(
            aniLibriaAvatarUrl = prefs[KEY_ANILIBRIA_AVATAR]?.let { cryptoManager.decrypt(it) },
            aniLibriaNickName = prefs[KEY_ANILIBRIA_NICKNAME]?.let { cryptoManager.decrypt(it) },
            aniLibriaToken = prefs[KEY_ANILIBRIA_TOKEN]?.let { cryptoManager.decrypt(it) }
        )
    }

    val tokenState: StateFlow<String> = authStateFlow
        .map { state -> state.aniLibriaToken ?: "" }
        .stateIn(
            scope = appScope,
            started = SharingStarted.Eagerly,
            initialValue = ""
        )

    fun getToken(): String = tokenState.value

    suspend fun updateAniLibriaProfile(
        avatarUrl: String?,
        nickName: String?
    ) {
        context.dataStore.edit { prefs ->
            avatarUrl?.let { prefs[KEY_ANILIBRIA_AVATAR] = cryptoManager.encrypt(it) }
            nickName?.let { prefs[KEY_ANILIBRIA_NICKNAME] = cryptoManager.encrypt(it) }
        }
    }

    suspend fun saveAniLibriaSession(
        token: String?,
        cookies: String? = null,
        nickName: String? = null,
        avatarUrl: String? = null
    ) {
        context.dataStore.edit { prefs ->
            token?.let { prefs[KEY_ANILIBRIA_TOKEN] = cryptoManager.encrypt(it) }
            cookies?.let { prefs[KEY_ANILIBRIA_COOKIES] = cryptoManager.encrypt(it) }
            nickName?.let { prefs[KEY_ANILIBRIA_NICKNAME] = cryptoManager.encrypt(it) }
            avatarUrl?.let { prefs[KEY_ANILIBRIA_AVATAR] = cryptoManager.encrypt(it) }
        }
    }

    suspend fun clearAniLibriaSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ANILIBRIA_TOKEN)
            prefs.remove(KEY_ANILIBRIA_COOKIES)
            prefs.remove(KEY_ANILIBRIA_NICKNAME)
            prefs.remove(KEY_ANILIBRIA_AVATAR)
        }
    }
}
