package com.example.garden.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.garden.appsettings.AuthManager
import com.example.garden.appsettings.AuthState
import com.example.garden.ui.screens.clearLoginGoogleWebViewCookies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import kotlin.text.ifEmpty

data class UserProfile(
    val id: Long,
    val username: String,
    val avatarUrl: String?
)

data class GoogleUserProfile(
    val name: String,
    val handleOrEmail: String?,
    val avatarUrl: String?
)

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
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            authManager.clearGoogleSession()
            withContext(Dispatchers.Main) {
                clearLoginGoogleWebViewCookies()
            }
        }
    }

    fun onAniLibertySignInSuccess(
        token: String,
        cookies: String,
        nickName: String?,
        avatarUrl: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            authManager.saveAniLibertySession(
                token = token,
                cookies = cookies,
                nickName = nickName,
                avatarUrl = avatarUrl
            )
        }
    }

    fun logoutAniLiberty() {
        viewModelScope.launch(Dispatchers.IO) {
            authManager.clearAniLibertySession()
        }
    }

    fun refreshGoogleUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val cookies = authManager.getGoogleCookies() ?: return@launch

            fetchGoogleUserProfile(cookies)
                .onSuccess { profile ->
                    authManager.updateGoogleProfile(
                        nickName = profile.name,
                        emailOrHandle = profile.handleOrEmail,
                        avatarUrl = profile.avatarUrl
                    )
                }
                .onFailure { error ->
                    Log.e("AuthViewModel", "Не удалось обновить профиль Google: ${error.message}")
                }
        }
    }

    fun refreshAniLibertyUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = authManager.getAniLibertyToken() ?: return@launch
            fetchAniLibertyUserProfile(token)
                .onSuccess { profile ->
                    authManager.updateAniLibertyProfile(
                        nickName = profile.username,
                        avatarUrl = profile.avatarUrl
                    )
                }
                .onFailure { error ->
                    Log.e("AuthViewModel", "Не удалось обновить профиль AniLiberty: ${error.message}")
                }
        }
    }

    fun fetchAniLibertyUserProfile(token: String): Result<UserProfile> {
        return runCatching {
            val url = URL("https://anilibria.top/api/v1/accounts/users/me/profile")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000

            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("Accept", "application/json")
            conn.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36"
            )

            val responseCode = conn.responseCode
            val inputStream = if (responseCode == 200) conn.inputStream else conn.errorStream
            val responseText = inputStream.bufferedReader().use { it.readText() }

            when (responseCode) {
                200 -> {
                    val json = JSONObject(responseText)

                    val id = json.getLong("id")
                    val name =
                        json.optString("nickname").ifEmpty { json.optString("login", "Пользователь") }

                    val avatarObj = json.optJSONObject("avatar")
                    val rawAvatarPath =
                        avatarObj?.optString("preview") ?: avatarObj?.optString("thumbnail")

                    val fullAvatarUrl = rawAvatarPath?.let {
                        if (it.startsWith("http")) it else "https://anilibria.top$it"
                    }

                    UserProfile(
                        id = id,
                        username = name,
                        avatarUrl = fullAvatarUrl
                    )
                }

                404 -> throw Exception("Токен недействителен или истёк (Код 404)")
                403 -> throw Exception("Необходимо авторизоваться (Код 403)")
                else -> throw Exception("Ошибка $responseCode: $responseText")
            }
        }
    }

    fun executeAniLibertyLogin(login: String, pass: String): Result<String> {
        return runCatching {
            val url = URL("https://anilibria.top/api/v1/accounts/users/auth/login")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000

            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")
            conn.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
            )

            val jsonBody = JSONObject().apply {
                put("login", login)
                put("password", pass)
            }.toString()

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(jsonBody)
                writer.flush()
            }

            val responseCode = conn.responseCode
            val inputStream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val responseText = inputStream.bufferedReader().use { it.readText() }

            if (responseText.trim().startsWith("<")) {
                Log.e("AniLibriaAuth", "Сервер вернул HTML: $responseText")
                throw Exception("Сервер вернул HTML-страницу (Код $responseCode). Возможно, заблокировано антиботом.")
            }

            when (responseCode) {
                200 -> {
                    val json = JSONObject(responseText)
                    json.getString("token")
                }

                401 -> throw Exception("Неправильный логин или пароль")
                422 -> throw Exception("Ошибка валидации (неверный формат данных)")
                else -> throw Exception("Ошибка сервера (код $responseCode)")
            }
        }
    }

    private fun getCookieValue(cookieString: String, cookieName: String): String? {
        return cookieString.split(";")
            .map { it.trim() }
            .firstOrNull { it.startsWith("$cookieName=") }
            ?.substringAfter("=")
    }

    private fun generateSapisidHash(sapisid: String, origin: String = "https://music.youtube.com"): String {
        val timestamp = System.currentTimeMillis() / 1000
        val input = "$timestamp $sapisid $origin"

        val md = MessageDigest.getInstance("SHA-1")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        val hash = digest.joinToString("") { "%02x".format(it) }

        return "SAPISIDHASH ${timestamp}_${hash}"
    }

    fun fetchGoogleUserProfile(cookies: String): Result<GoogleUserProfile> {
        return runCatching {
            val sapisid = getCookieValue(cookies, "SAPISID")
                ?: getCookieValue(cookies, "__Secure-3PAPISID")
                ?: throw Exception("SAPISID кука не найдена")

            val authHeader = generateSapisidHash(sapisid)

            val url = URL("https://music.youtube.com/youtubei/v1/account/account_menu?prettyPrint=false")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000

            conn.setRequestProperty("Cookie", cookies)
            conn.setRequestProperty("Authorization", authHeader)
            conn.setRequestProperty("X-Origin", "https://music.youtube.com")
            conn.setRequestProperty("Origin", "https://music.youtube.com")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")

            val jsonRequestBody = """
            {
                "context": {
                    "client": {
                        "clientName": "WEB_REMIX",
                        "clientVersion": "1.20240422.01.00"
                    }
                }
            }
        """.trimIndent()

            conn.outputStream.use { os ->
                os.write(jsonRequestBody.toByteArray(Charsets.UTF_8))
            }

            val responseCode = conn.responseCode
            val inputStream = if (responseCode == 200) conn.inputStream else conn.errorStream
            val responseText = inputStream.bufferedReader().use { it.readText() }

            if (responseCode != 200) {
                throw Exception("Ошибка API YouTube ($responseCode): $responseText")
            }

            val json = JSONObject(responseText)

            val header = json.optJSONArray("actions")
                ?.optJSONObject(0)
                ?.optJSONObject("openPopupAction")
                ?.optJSONObject("popup")
                ?.optJSONObject("multiPageMenuRenderer")
                ?.optJSONObject("header")
                ?.optJSONObject("activeAccountHeaderRenderer")
                ?: throw Exception("Не удалось распарсить структуру профиля Google")

            val name = header.optJSONObject("accountName")
                ?.optJSONArray("runs")
                ?.optJSONObject(0)
                ?.optString("text")
                ?: header.optJSONObject("accountName")?.optString("simpleText")
                ?: "Пользователь Google"

            val handleOrEmail = header.optJSONObject("email")
                ?.optJSONArray("runs")
                ?.optJSONObject(0)
                ?.optString("text")
                ?: header.optJSONObject("channelHandle")
                    ?.optJSONArray("runs")
                    ?.optJSONObject(0)
                    ?.optString("text")
                ?: ""

            val thumbnails = header.optJSONObject("accountPhoto")?.optJSONArray("thumbnails")
            val avatarUrl = if (thumbnails != null && thumbnails.length() > 0) {
                thumbnails.optJSONObject(thumbnails.length() - 1)?.optString("url")
            } else null

            GoogleUserProfile(
                name = name,
                handleOrEmail = handleOrEmail,
                avatarUrl = avatarUrl
            )
        }
    }
}