package com.example.garden.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import com.example.garden.LocalCustomColors
import com.example.garden.ui.components.icons.CloseIco
import com.example.garden.ui.components.icons.HelpIco
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GoogleLoginPage(
    modifier: Modifier = Modifier,
    onSuccessAuth: (cookies: String) -> Unit,
    onClose: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    val density = LocalDensity.current
    val topInset = with(density) { WindowInsets.safeDrawing.getTop(density).toDp() }
    val rightInset = with(density) {
        WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current).toDp()
    }
    val leftInset = with(density) {
        WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current).toDp()
    }

    var isLoading by remember { mutableStateOf(true) }
    var webError by remember { mutableStateOf<String?>(null) }
    var authCaptured by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = topInset + MaterialTheme.spacing.screenHorizontal,
                start = leftInset + MaterialTheme.spacing.screenHorizontal,
                end = rightInset + MaterialTheme.spacing.screenHorizontal
            )
            .blockGestures()
            .clearFocus(focusManager),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FilledIconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    focusManager.clearFocus()
                    onClose()
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = LocalCustomColors.current.closeButton,
                    contentColor = LocalCustomColors.current.onCloseButton
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = CloseIco,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                )
            }

            Text(
                modifier = Modifier.weight(1f, fill = false),
                text = "Вход в Google",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                focusManager.clearFocus()
            }) {
                Icon(
                    imageVector = HelpIco,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        importantForAutofill = android.view.View.IMPORTANT_FOR_AUTOFILL_YES

                        settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true

                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setAcceptThirdPartyCookies(this, true)

                        fun checkAndSaveCookies(url: String) {
                            if (authCaptured) return
                            val cookies = cookieManager.getCookie("https://music.youtube.com")
                                ?: cookieManager.getCookie("https://youtube.com")
                                ?: cookieManager.getCookie(url)
                                ?: ""

                            if (cookies.contains("SAPISID") || cookies.contains("__Secure-3PAPISID") || cookies.contains("SID")) {
                                authCaptured = true
                                onSuccessAuth(cookies)
                            }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                                webError = null
                                url?.let { checkAndSaveCookies(it) }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                url?.let { checkAndSaveCookies(it) }
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                if (request?.isForMainFrame == true) {
                                    val errCode = error?.errorCode
                                    val errDesc = error?.description
                                    Log.e("GoogleAuthWeb", "Ошибка загрузки: $errCode | $errDesc | URL: ${request.url}")
                                    webError = "Ошибка соединения: $errDesc ($errCode)"
                                }
                            }
                        }

                        loadUrl("https://accounts.google.com/ServiceLogin?service=youtube&continue=https%3A%2F%2Fmusic.youtube.com%2F")
                    }
                }
            )

            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            if (webError != null) {
                Text(
                    text = webError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(MaterialTheme.spacing.medium)
                )
            }
        }
    }
}

fun clearLoginGoogleWebViewCookies(onCompleted: () -> Unit = {}) {
    val cookieManager = CookieManager.getInstance()
    cookieManager.removeAllCookies {
        cookieManager.flush()
        onCompleted()
    }
}
