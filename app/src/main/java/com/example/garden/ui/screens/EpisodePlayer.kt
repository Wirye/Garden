package com.example.garden.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.example.garden.AniLibriaClient
import com.example.garden.R
import com.example.garden.database.entities.EpisodeInfo
import com.example.garden.database.entities.LinkData
import com.example.garden.ui.components.CloseButton
import com.example.garden.ui.components.StrokeText
import com.example.garden.ui.components.icons.CheckIco
import com.example.garden.ui.components.icons.EpisodesMenuOpenIco
import com.example.garden.ui.components.icons.MaxsimizeIco
import com.example.garden.ui.components.icons.MinimizeIco
import com.example.garden.ui.components.icons.PauseIco
import com.example.garden.ui.components.icons.PlayArrowFilledIco
import com.example.garden.ui.components.icons.SearchIco
import com.example.garden.ui.components.icons.SettingsIco
import com.example.garden.ui.components.icons.SkipNextFilledIco
import com.example.garden.ui.components.icons.SkipPreviousFilledIco
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus
import com.example.garden.viewmodel.MainViewModel
import com.github.wirye.anilibriakt.model.Episode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

private const val UI_HIDE_DELAY_MS = 5_000L
private const val SEEK_STEP_MS = 10_000L
private const val SAVE_INTERVAL_MS = 5_000L
private const val WATCHED_THRESHOLD = 0.95f
private const val OP_ED_UNIT_MS = 1_000L

// ───────────────────────────── модели состояния ─────────────────────────────

private sealed interface PendingSeek {
    data class Ms(val value: Long) : PendingSeek
    data class Fraction(val value: Float) : PendingSeek
}

private sealed interface PlayerPhase {
    data object Loading : PlayerPhase
    data object Main : PlayerPhase
    data class Error(val message: String?, val unsupported: Boolean = false) : PlayerPhase
}

fun List<EpisodeInfo>.notWeb(): List<EpisodeInfo> {
    return this.map { episode ->
        episode.copy(
            variants = episode.variants.filter { variant -> variant.link !is LinkData.AniLibriaEpisode }
        )
    }.filter { episode ->
        episode.variants.isNotEmpty()
    }
}

// ───────────────────────────── экран плеера ─────────────────────────────

@OptIn(UnstableApi::class)
@Composable
fun EpisodePlayerScreen(
    episodes: List<EpisodeInfo>,
    title: String,
    id: Long,
    closeLayer: () -> Unit,
    mainViewModel: MainViewModel,
    startEpisodePosition: Int? = null, // какой эпизод запускать; по умолчанию первый непросмотренный
    lastVoiceName: String? = null,     // последняя выбранная озвучка карточки
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val activity = remember(context) { context.findActivity() }

    // ArrayList — чтобы список гарантированно сохранялся в Bundle
    var episodesList by rememberSaveable { mutableStateOf<List<EpisodeInfo>>(ArrayList(episodes)) }
    var currentPosition by rememberSaveable {
        mutableIntStateOf(
            startEpisodePosition
                ?: episodes.firstOrNull { !it.watched }?.position
                ?: episodes.firstOrNull()?.position
                ?: 0
        )
    }

    var voiceName by rememberSaveable { mutableStateOf(lastVoiceName) }
    var quality by rememberSaveable { mutableIntStateOf(0) } // 0 = лучшее из доступных
    var autoSkip by rememberSaveable { mutableStateOf(true) }
    var landscapeLocked by rememberSaveable { mutableStateOf(false) }

    var phase by remember { mutableStateOf<PlayerPhase>(PlayerPhase.Loading) }
    var webEpisode by remember { mutableStateOf<Episode?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    var positionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var pendingSeek by remember { mutableStateOf<PendingSeek?>(null) }
    val skippedMarks = remember { mutableSetOf<String>() }

    var uiVisible by remember { mutableStateOf(true) }
    var lastInteraction by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    var visibleAtDown by remember { mutableStateOf(true) }
    var sheetOpen by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableStateOf<Float?>(null) }
    var seekFlash by remember { mutableStateOf<Pair<Boolean, Int>?>(null) }

    val playerView = remember(context) {
        PlayerView(context).apply {
            useController = false
            keepScreenOn = true
        }
    }

    val mediaSourceFactory = DefaultMediaSourceFactory(context)

    val player = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
    }

    // ── вспомогательные функции ──
    fun persistProgress() {
        if (phase !is PlayerPhase.Main) return
        val dur = player.duration
        if (dur <= 0) return
        val idx = episodesList.indexOfFirst { it.position == currentPosition }
        if (idx < 0) return
        val pr = (player.currentPosition.toFloat() / dur).coerceIn(0f, 1f)
        val old = episodesList[idx]
        val newList = ArrayList(episodesList)
        newList[idx] = old.copy(positionPr = pr, watched = old.watched || pr >= WATCHED_THRESHOLD)
        episodesList = newList
        scope.launch {
            mainViewModel.updateAnimeCardEpisodesProgress(id, newList.toList().notWeb())
        }
    }

    fun neighbor(delta: Int): EpisodeInfo? {
        val idx = episodesList.indexOfFirst { it.position == currentPosition }
        return if (idx < 0) null else episodesList.getOrNull(idx + delta)
    }

    fun switchEpisode(position: Int) {
        if (position == currentPosition) return
        persistProgress()
        pendingSeek = null
        currentPosition = position
    }

    fun selectVoice(name: String) {
        if (name == voiceName) return
        persistProgress()
        pendingSeek = PendingSeek.Ms(player.currentPosition) // продолжаем с того же места
        voiceName = name
        scope.launch {
            mainViewModel.updateAnimeCardLastVoiceName(id, name)
        }
    }

    fun startMain(uri: String) {
        phase = PlayerPhase.Main
        val mediaItem =
            MediaItem.Builder()
                .setUri(uri.toUri())
                .build()
        player.setMediaItem(
            mediaItem
        )
        player.prepare()
        player.playWhenReady = true
    }

    fun selectQuality(height: Int) {
        val url = webEpisode?.qualityOptions()?.firstOrNull { it.first == height }?.second ?: return
        quality = height
        pendingSeek = PendingSeek.Ms(player.currentPosition)
        startMain(url)
    }

    fun togglePlay() {
        if (phase !is PlayerPhase.Main) return
        if (player.isPlaying) player.pause() else player.play()
    }

    fun seekBy(delta: Long) {
        val dur = player.duration.let { if (it == C.TIME_UNSET || it <= 0) Long.MAX_VALUE else it }
        player.seekTo((player.currentPosition + delta).coerceIn(0L, dur))
    }

    // ── ориентация ──
    LaunchedEffect(landscapeLocked) {
        activity?.requestedOrientation =
            if (landscapeLocked) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
    DisposableEffect(Unit) {
        onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }

    // ── в фоне не играем ──
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        player.pause()
        persistProgress()
    }

    DisposableEffect(Unit) {
        onDispose {
            persistProgress()
            player.release()
        }
    }

    DisposableEffect(activity) {
        val window = activity?.window ?: return@DisposableEffect onDispose {}
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        controller.hide(WindowInsetsCompat.Type.systemBars())

        onDispose {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    // ── слушатель плеера ──
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                if (!playing && phase is PlayerPhase.Main) persistProgress()
            }

            override fun onPlaybackStateChanged(state: Int) {
                isBuffering = state == Player.STATE_BUFFERING
                val ph = phase
                when (state) {
                    Player.STATE_READY -> if (ph is PlayerPhase.Main) {
                        val seek = pendingSeek
                        if (seek != null) {
                            pendingSeek = null
                            when (seek) {
                                is PendingSeek.Ms -> player.seekTo(seek.value)
                                is PendingSeek.Fraction -> {
                                    val d = player.duration
                                    if (d > 0) player.seekTo((d * seek.value).toLong())
                                }
                            }
                        }
                    }

                    Player.STATE_ENDED -> when (ph) {
                        is PlayerPhase.Main -> {
                            persistProgress()
                            neighbor(-1)?.let { switchEpisode(it.position) }
                        }

                        else -> Unit
                    }

                    else -> Unit
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                phase = PlayerPhase.Error(error.localizedMessage ?: error.errorCodeName)
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener) }

    }

    // ── загрузка источника при смене эпизода / озвучки ──
    LaunchedEffect(currentPosition, voiceName) {
        val episode = episodesList.firstOrNull { it.position == currentPosition }
        phase = PlayerPhase.Loading
        player.stop()
        player.clearMediaItems()
        webEpisode = null
        positionMs = 0L
        durationMs = 0L
        if (episode == null) {
            phase = PlayerPhase.Error(null)
            return@LaunchedEffect
        }
        if (pendingSeek == null && episode.positionPr in 0.01f..0.99f) {
            pendingSeek = PendingSeek.Fraction(if (episode.watched) 0f else episode.positionPr)
        }
        val variant = episode.variants.firstOrNull { it.voice.name == voiceName }
            ?: episode.variants.firstOrNull()

        when (val link = variant?.link) {
            is LinkData.Device -> startMain(deviceUri(link.path))

            is LinkData.AniLibriaEpisode -> {
                val web = AniLibriaClient.releases.episodes.getEpisode(link.id).getOrElse {
                    phase = PlayerPhase.Error(it.message)
                    return@LaunchedEffect
                }
                webEpisode = web
                val options = web.qualityOptions()
                val url =
                    (options.firstOrNull { it.first == quality } ?: options.firstOrNull())?.second
                if (url == null) {
                    phase = PlayerPhase.Error(null)
                    return@LaunchedEffect
                }

                startMain(url)
            }

            else -> phase = PlayerPhase.Error(null, unsupported = true)
        }
    }

    // ── опрос позиции, пропуск опенинга/эндинга, лимит рекламы ──
    LaunchedEffect(player) {
        while (true) {
            val p = player.currentPosition
            val d = player.duration
            if (d != C.TIME_UNSET && d > 0) durationMs = d
            positionMs = p
            when (phase) {

                is PlayerPhase.Main -> {
                    val web = webEpisode
                    if (autoSkip && web != null && pendingSeek == null && player.playbackState == Player.STATE_READY) {
                        web.opening?.let { o ->
                            val s = o.start
                            val e = o.end
                            if (s != null && e != null && p >= s * OP_ED_UNIT_MS && p < e * OP_ED_UNIT_MS &&
                                skippedMarks.add("o$currentPosition")
                            ) player.seekTo(e * OP_ED_UNIT_MS)
                        }
                        web.ending?.let { en ->
                            val s = en.start
                            val e = en.end
                            if (s != null && e != null && p >= s * OP_ED_UNIT_MS && p < e * OP_ED_UNIT_MS &&
                                skippedMarks.add("e$currentPosition")
                            ) {
                                val next = neighbor(-1)
                                if (next != null && e * OP_ED_UNIT_MS >= d - 1_500L) {
                                    persistProgress()
                                    switchEpisode(next.position)
                                } else player.seekTo(e * OP_ED_UNIT_MS)
                            }
                        }
                    }
                }

                else -> Unit
            }
            delay(250.milliseconds)
        }
    }

    // ── периодическое сохранение прогресса ──
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(SAVE_INTERVAL_MS.milliseconds)
            persistProgress()
        }
    }

    // ── автоскрытие интерфейса через 5 секунд бездействия ──
    LaunchedEffect(menuOpen, sheetOpen) { lastInteraction = SystemClock.elapsedRealtime() }
    LaunchedEffect(uiVisible, menuOpen, sheetOpen, dragFraction != null) {
        if (!uiVisible || menuOpen || sheetOpen || dragFraction != null) return@LaunchedEffect
        while (true) {
            if (SystemClock.elapsedRealtime() - lastInteraction >= UI_HIDE_DELAY_MS) {
                uiVisible = false
                break
            }
            delay(250.milliseconds)
        }
    }
    LaunchedEffect(seekFlash) {
        if (seekFlash != null) {
            delay(700.milliseconds)
            seekFlash = null
        }
    }

    BackHandler(enabled = sheetOpen) { sheetOpen = false }

    // ── производные значения для UI ──
    val currentEpisode = episodesList.firstOrNull { it.position == currentPosition }
    val prevEpisode = neighbor(1)
    val nextEpisode = neighbor(-1)
    val variants = currentEpisode?.variants.orEmpty()
    val selectedVoice =
        variants.firstOrNull { it.voice.name == voiceName } ?: variants.firstOrNull()
    val qualityOptions = webEpisode?.qualityOptions().orEmpty()
    val activeQuality = qualityOptions.firstOrNull { it.first == quality }?.first
        ?: qualityOptions.firstOrNull()?.first
    val progress = dragFraction ?: if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(
        0f,
        1f
    ) else 0f
    val shownPosition = dragFraction?.let { (it * durationMs).toLong() } ?: positionMs
    val withHours = durationMs >= 3_600_000L
    val timeText = "${formatTime(shownPosition, withHours)}/${formatTime(durationMs, withHours)}"

    val buttonColors = IconButtonDefaults.iconButtonColors(
        containerColor = Color.Transparent,
        contentColor = Color.White,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = Color.White.copy(alpha = 0.38f),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .blockGestures()
            .clearFocus(focusManager)
    ) {
        // ── видео ──
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                playerView.apply {
                    this.player = player
                }
            }
        )

        // ── слой жестов + интерфейс ──
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // любое касание показывает интерфейс и сбрасывает таймер
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (event.changes.any { it.changedToDown() }) {
                                visibleAtDown = uiVisible
                                uiVisible = true
                            }
                            if (event.changes.any { it.pressed }) {
                                lastInteraction = SystemClock.elapsedRealtime()
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { togglePlay() },
                        onDoubleTap = { offset ->
                            if (phase is PlayerPhase.Main) {
                                val forward = offset.x > size.width / 2f
                                seekBy(if (forward) SEEK_STEP_MS else -SEEK_STEP_MS)
                                seekFlash = forward to ((seekFlash?.second ?: 0) + 1)
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    // свайп: скрыть/показать интерфейс
                    var total = Offset.Zero
                    val threshold = 48.dp.toPx()
                    detectDragGestures(
                        onDragStart = { total = Offset.Zero },
                        onDrag = { _, delta -> total += delta },
                        onDragEnd = {
                            if (total.getDistance() > threshold) uiVisible = !visibleAtDown
                        }
                    )
                }
        ) {
            // центр: загрузка / ошибка / реклама / перемотка
            val ph = phase
            if (ph is PlayerPhase.Loading || ((ph is PlayerPhase.Main) && isBuffering)) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
            if (ph is PlayerPhase.Error) {
                Text(
                    text = if (ph.unsupported) stringResource(R.string.playerUnsupportedSource)
                    else ph.message ?: stringResource(R.string.playerUnknownError),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = MaterialTheme.spacing.large)
                )
            }
            seekFlash?.let { (forward, _) ->
                StrokeText(
                    text = (if (forward) "+" else "-") + (SEEK_STEP_MS / 1000).toString(),
                    modifier = Modifier
                        .align(if (forward) Alignment.CenterEnd else Alignment.CenterStart)
                        .padding(horizontal = 64.dp)
                )
            }

            // ── верхняя панель ──
            AnimatedVisibility(
                visible = uiVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.7f),
                                    Color.Transparent
                                )
                            )
                        )
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .padding(horizontal = MaterialTheme.spacing.screenHorizontal),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CloseButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            focusManager.clearFocus()
                            persistProgress()
                            closeLayer()
                        }
                    )
                    Text(
                        text = title,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── нижняя панель ──
            AnimatedVisibility(
                visible = uiVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .padding(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            StrokeText(
                                text = stringResource(
                                    R.string.Episode
                                ) + " $currentPosition"
                            )
                            currentEpisode?.name?.takeIf { it.isNotBlank() }
                                ?.let { StrokeText(text = it) }
                        }

                        StrokeText(
                            text = timeText,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 1
                        )
                    }

                    PlayerProgressBar(
                        progress = progress,
                        onScrub = { dragFraction = it },
                        onScrubEnd = { fraction ->
                            dragFraction = null
                            val d = player.duration
                            if (phase is PlayerPhase.Main && d > 0) {
                                player.seekTo((d * fraction).toLong())
                                positionMs = (d * fraction).toLong()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // слева: все эпизоды
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            IconButton(onClick = { sheetOpen = true }, colors = buttonColors) {
                                Icon(
                                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                                    imageVector = EpisodesMenuOpenIco,
                                    contentDescription = stringResource(R.string.playerEpisodesList)
                                )
                            }
                        }

                        // центр: предыдущий / пауза / следующий
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { prevEpisode?.let { switchEpisode(it.position) } },
                                enabled = prevEpisode != null,
                                colors = buttonColors,
                            ) {
                                Icon(
                                    SkipPreviousFilledIco,
                                    stringResource(R.string.playerPrevious),
                                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                                )
                            }
                            IconButton(
                                onClick = { togglePlay() },
                                colors = buttonColors,
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) PauseIco else PlayArrowFilledIco,
                                    contentDescription = stringResource(
                                        if (isPlaying) R.string.playerPause else R.string.playerPlay
                                    ),
                                    modifier = Modifier.size(MaterialTheme.dimens.minButtonHeight)
                                )
                            }

                            IconButton(
                                onClick = { nextEpisode?.let { switchEpisode(it.position) } },
                                enabled = nextEpisode != null,
                                colors = buttonColors,
                            ) {
                                Icon(
                                    SkipNextFilledIco,
                                    stringResource(R.string.playerNext),
                                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                                )
                            }
                        }

                        // справа: настройки + блокировка ориентации
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                                Box {
                                    IconButton(
                                        onClick = { menuOpen = true },
                                        colors = buttonColors
                                    ) {
                                        Icon(
                                            SettingsIco,
                                            stringResource(R.string.playerSettings),
                                            modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = menuOpen,
                                        onDismissRequest = { menuOpen = false },
                                        shape = MaterialTheme.shapes.extraLarge,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                                    ) {
                                        if (qualityOptions.isNotEmpty()) {
                                            MenuHeader(stringResource(R.string.playerQuality))
                                            qualityOptions.forEach { (height, _) ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            height.toString()
                                                        )
                                                    },
                                                    trailingIcon = {
                                                        if (height == activeQuality) Icon(
                                                            CheckIco,
                                                            null,
                                                            modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                                                        )
                                                    },
                                                    onClick = { selectQuality(height) }
                                                )
                                            }
                                            HorizontalDivider()
                                        }

                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.playerAutoSkip)) },
                                            trailingIcon = {
                                                Switch(
                                                    checked = autoSkip,
                                                    onCheckedChange = null
                                                )
                                            },
                                            onClick = { autoSkip = !autoSkip }
                                        )
                                        if (variants.size > 1) {
                                            HorizontalDivider()
                                            MenuHeader(stringResource(R.string.playerVoice))
                                            variants.forEach { variant ->
                                                DropdownMenuItem(
                                                    text = { Text(variant.voice.name) },
                                                    trailingIcon = {
                                                        if (variant.voice.name == selectedVoice?.voice?.name)
                                                            Icon(
                                                                CheckIco,
                                                                null,
                                                                modifier = Modifier.size(
                                                                    MaterialTheme.dimens.iconMedium
                                                                )
                                                            )
                                                    },
                                                    onClick = { selectVoice(variant.voice.name) }
                                                )
                                            }
                                        }
                                    }
                                }
                                IconButton(
                                    onClick = { landscapeLocked = !landscapeLocked },
                                ) {
                                    Icon(
                                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                                        imageVector = if (landscapeLocked) MinimizeIco
                                        else MaxsimizeIco,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── боковая шторка со списком эпизодов (поверх всего) ──
        EpisodesSideSheet(
            visible = sheetOpen,
            episodes = episodesList,
            currentPosition = currentPosition,
            onSelect = { position ->
                focusManager.clearFocus()
                sheetOpen = false
                switchEpisode(position)
            },
            onDismiss = {
                focusManager.clearFocus()
                sheetOpen = false
            }
        )
    }
}

// ───────────────────────────── UI-компоненты ─────────────────────────────

@Composable
private fun MenuHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
private fun EpisodesSideSheet(
    visible: Boolean,
    episodes: List<EpisodeInfo>,
    currentPosition: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val episodes by remember(episodes) { mutableStateOf(episodes.asReversed()) }
    var query by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val filtered = remember(episodes, query) {
        val q = query.trim()
        if (q.isEmpty()) episodes else episodes.filter { it.position.toString().contains(q) }
    }

    LaunchedEffect(visible) {
        if (visible) {
            val idx = filtered.indexOfFirst { it.position == currentPosition }
            if (idx >= 0) listState.scrollToItem(idx)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )
        }
        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.8f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(Unit) { detectTapGestures { } } // не пропускаем касания под шторку
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            SearchIco,
                            null,
                            modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                        )
                    },
                    placeholder = {
                        Text(
                            stringResource(R.string.searchByEpisodeNumber),
                            maxLines = 1
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = CircleShape
                )

                if (filtered.isEmpty()) {
                    Text(
                        text = stringResource(R.string.nothingIsHere),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    items(filtered, key = { it.position }) { episode ->
                        val shape = MaterialTheme.shapes.medium
                        Box(
                            modifier = Modifier
                                .clip(shape)
                                .then(
                                    if (episode.position == currentPosition)
                                        Modifier.border(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary,
                                            shape
                                        )
                                    else Modifier
                                )
                                .clickable { onSelect(episode.position) }
                        ) {
                            EpisodePreview(episode)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerProgressBar(
    progress: Float,
    onScrub: (Float) -> Unit,
    onScrubEnd: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.secondaryContainer
    val currentOnScrub by rememberUpdatedState(onScrub)
    val currentOnScrubEnd by rememberUpdatedState(onScrubEnd)

    Canvas(
        modifier = modifier
            .height(36.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    currentOnScrubEnd((offset.x / size.width).coerceIn(0f, 1f))
                }
            }
            .pointerInput(Unit) {
                var last = 0f
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        last = (offset.x / size.width).coerceIn(0f, 1f)
                        currentOnScrub(last)
                    },
                    onHorizontalDrag = { change, _ ->
                        change.consume()
                        last = (change.position.x / size.width).coerceIn(0f, 1f)
                        currentOnScrub(last)
                    },
                    onDragEnd = { currentOnScrubEnd(last) },
                    onDragCancel = { currentOnScrubEnd(last) }
                )
            }
    ) {
        val w = size.width
        val cy = size.height / 2f
        val x = progress.coerceIn(0f, 1f) * w
        val dotRadius = 2.5.dp.toPx()

        val barH = 12.dp.toPx()
        val gap = 6.dp.toPx()
        val thumbW = 4.dp.toPx()
        val thumbH = 30.dp.toPx()

        val activeEnd = x - gap - thumbW / 2f
        if (activeEnd > 0f) {
            drawRoundRect(
                color = activeColor,
                topLeft = Offset(0f, cy - barH / 2f),
                size = Size(activeEnd, barH),
                cornerRadius = CornerRadius(barH / 2f)
            )
        }
        drawRoundRect(
            color = activeColor,
            topLeft = Offset(x - thumbW / 2f, cy - thumbH / 2f),
            size = Size(thumbW, thumbH),
            cornerRadius = CornerRadius(thumbW / 2f)
        )
        val trackStart = x + thumbW / 2f + gap
        if (trackStart < w) {
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(trackStart, cy - barH / 2f),
                size = Size(w - trackStart, barH),
                cornerRadius = CornerRadius(barH / 2f)
            )
        }
        drawCircle(activeColor, dotRadius, Offset(w - barH / 2f, cy))
    }
}

// ───────────────────────────── утилиты ─────────────────────────────

private fun formatTime(ms: Long, withHours: Boolean): String {
    val s = (ms / 1000L).coerceAtLeast(0L)
    return if (withHours) {
        String.format(Locale.ROOT, "%d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60)
    } else {
        String.format(Locale.ROOT, "%02d:%02d", s / 60, s % 60)
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun deviceUri(path: String): String =
    if (path.startsWith("/")) Uri.fromFile(File(path)).toString() else path

/** Доступные качества web-эпизода: (высота, ссылка), от лучшего к худшему. */
private fun Episode.qualityOptions(): List<Pair<Int, String>> = listOfNotNull(
    hls1080?.takeIf { it.isNotBlank() }?.let { 1080 to it },
    hls720?.takeIf { it.isNotBlank() }?.let { 720 to it },
    hls480?.takeIf { it.isNotBlank() }?.let { 480 to it },
)
