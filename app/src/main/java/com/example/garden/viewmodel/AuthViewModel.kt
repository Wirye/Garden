package com.example.garden.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.garden.appsettings.AuthManager
import com.example.garden.appsettings.AuthState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.text.ifEmpty

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

    data class UserProfile(
        val id: Long,
        val username: String,
        val avatarUrl: String?
    )

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
}