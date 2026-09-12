package com.example.garden.appsettings

import android.content.Context
import android.webkit.CookieManager
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.nio.charset.StandardCharsets
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class CryptoManager(context: Context) {

    private val aead: Aead

    init {
        AeadConfig.register()

        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, "tink_keyset", "tink_prefs")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://_androidx_security_master_key_")
            .build()
            .keysetHandle

        aead = keysetHandle.getPrimitive(Aead::class.java)
    }

    fun encrypt(data: String): String {
        if (data.isBlank()) return ""
        val encryptedBytes = aead.encrypt(data.toByteArray(StandardCharsets.UTF_8), null)
        return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
    }

    fun decrypt(encryptedData: String): String {
        if (encryptedData.isBlank()) return ""
        val decodedBytes = android.util.Base64.decode(encryptedData, android.util.Base64.DEFAULT)
        val decryptedBytes = aead.decrypt(decodedBytes, null)
        return String(decryptedBytes, StandardCharsets.UTF_8)
    }
}

data class AuthState(
    val googleEmail: String? = null,
    val googleAvatarUrl: String? = null,
    val googleNickName: String? = null,
    val googleToken: String? = null,

    val aniLibertyAvatarUrl: String? = null,
    val aniLibertyNickName: String? = null,
    val aniLibertyToken: String? = null
) {
    val isGoogleAuthorized: Boolean
        get() = !googleToken.isNullOrBlank()

    val isAniLibertyAuthorized: Boolean
        get() = !aniLibertyToken.isNullOrBlank()
}

private val Context.dataStore by preferencesDataStore(name = "auth_settings")

class AuthManager(private val context: Context) {

    private val cryptoManager = CryptoManager(context)

    companion object {
        private val KEY_GOOGLE_EMAIL = stringPreferencesKey("g_email")
        private val KEY_GOOGLE_AVATAR = stringPreferencesKey("g_avatar")
        private val KEY_GOOGLE_NICKNAME = stringPreferencesKey("g_nickname")
        private val KEY_GOOGLE_TOKEN = stringPreferencesKey("g_token")
        private val KEY_GOOGLE_COOKIES = stringPreferencesKey("g_cookies")
        private val KEY_ANILIBERTY_AVATAR = stringPreferencesKey("ani_avatar")
        private val KEY_ANILIBERTY_NICKNAME = stringPreferencesKey("ani_nickname")
        private val KEY_ANILIBERTY_TOKEN = stringPreferencesKey("ani_token")
        private val KEY_ANILIBERTY_COOKIES = stringPreferencesKey("ani_cookies")
    }

    val authStateFlow: Flow<AuthState> = context.dataStore.data.map { prefs ->
        AuthState(
            // Google Data
            googleEmail = prefs[KEY_GOOGLE_EMAIL]?.let { cryptoManager.decrypt(it) },
            googleAvatarUrl = prefs[KEY_GOOGLE_AVATAR]?.let { cryptoManager.decrypt(it) },
            googleNickName = prefs[KEY_GOOGLE_NICKNAME]?.let { cryptoManager.decrypt(it) },
            googleToken = prefs[KEY_GOOGLE_TOKEN]?.let { cryptoManager.decrypt(it) },

            // AniLiberty Data
            aniLibertyAvatarUrl = prefs[KEY_ANILIBERTY_AVATAR]?.let { cryptoManager.decrypt(it) },
            aniLibertyNickName = prefs[KEY_ANILIBERTY_NICKNAME]?.let { cryptoManager.decrypt(it) },
            aniLibertyToken = prefs[KEY_ANILIBERTY_TOKEN]?.let { cryptoManager.decrypt(it) }
        )
    }

    suspend fun saveGoogleSession(
        email: String?,
        avatarUrl: String?,
        nickName: String?,
        token: String?,
        cookies: String? = null
    ) {
        context.dataStore.edit { prefs ->
            email?.let { prefs[KEY_GOOGLE_EMAIL] = cryptoManager.encrypt(it) }
            avatarUrl?.let { prefs[KEY_GOOGLE_AVATAR] = cryptoManager.encrypt(it) }
            nickName?.let { prefs[KEY_GOOGLE_NICKNAME] = cryptoManager.encrypt(it) }
            token?.let { prefs[KEY_GOOGLE_TOKEN] = cryptoManager.encrypt(it) }
            cookies?.let { prefs[KEY_GOOGLE_COOKIES] = cryptoManager.encrypt(it) }
        }
    }

    suspend fun clearGoogleSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_GOOGLE_EMAIL)
            prefs.remove(KEY_GOOGLE_AVATAR)
            prefs.remove(KEY_GOOGLE_NICKNAME)
            prefs.remove(KEY_GOOGLE_TOKEN)
            prefs.remove(KEY_GOOGLE_COOKIES)
        }
    }

    suspend fun saveAniLibertySession(
        token: String?,
        cookies: String? = null,
        nickName: String? = null,
        avatarUrl: String? = null
    ) {
        context.dataStore.edit { prefs ->
            token?.let { prefs[KEY_ANILIBERTY_TOKEN] = cryptoManager.encrypt(it) }
            cookies?.let { prefs[KEY_ANILIBERTY_COOKIES] = cryptoManager.encrypt(it) }
            nickName?.let { prefs[KEY_ANILIBERTY_NICKNAME] = cryptoManager.encrypt(it) }
            avatarUrl?.let { prefs[KEY_ANILIBERTY_AVATAR] = cryptoManager.encrypt(it) }
        }
    }

    suspend fun clearAniLibertySession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ANILIBERTY_TOKEN)
            prefs.remove(KEY_ANILIBERTY_COOKIES)
            prefs.remove(KEY_ANILIBERTY_NICKNAME)
            prefs.remove(KEY_ANILIBERTY_AVATAR)
        }
    }

    suspend fun getGoogleCookies(): String? {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_GOOGLE_COOKIES]?.let { cryptoManager.decrypt(it) }
        }.firstOrNull()
    }

    suspend fun getAniLibertyCookies(): String? {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_ANILIBERTY_COOKIES]?.let { cryptoManager.decrypt(it) }
        }.firstOrNull()
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
        CookieManager.getInstance().removeAllCookies(null)
    }
}
