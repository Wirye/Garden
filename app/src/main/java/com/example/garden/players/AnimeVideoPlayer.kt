package com.example.garden.players

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.garden.R
import com.example.garden.ResultKeys
import com.example.garden.customView.StrokeTextView
import com.example.garden.baseDensity
import com.example.garden.convertToStringTime
import com.example.garden.database.objectData
import com.example.garden.getTextSizeByHeight
import com.example.garden.lifecycleOwner
import com.example.garden.optimizeText
import com.example.garden.screenHeight
import com.example.garden.screenWidth
import com.example.garden.screenWidthDp
import com.example.garden.viewmodel.ResultSenderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.round
import android.view.GestureDetector
import com.example.garden.changeOrientation
import com.example.garden.toggleSystemBars
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class AnimeVideoPlayer(context: Context, private val resultSenderViewModel: ResultSenderViewModel) : FrameLayout(context) {
    private var exoPlayer: ExoPlayer? = null
    private var isUiLoaded = false
    private var hideUiJob: kotlinx.coroutines.Job? = null
    private var uiContainer: ConstraintLayout? = null // Ссылка на наш UI
    private val uiFadeDuration = 300L
    private var isPlayingState = true
    // Создаем визуальную часть
    private val playerView: PlayerView = PlayerView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        useController = false // Мы сделаем свои кнопки позже!
        isClickable = true
        isFocusable = true
        // Чтобы наверняка не пропускал клики к нижним слоям
        setOnClickListener { /* Пусто, просто ловим клик */ }
        setOnTouchListener { _, _ -> true }
    }
    private var playerSubscriptionJob: kotlinx.coroutines.Job? = null
    init {
        addView(playerView)
        setupPlayer()
        val scope = context.lifecycleOwner?.lifecycleScope
        playerSubscriptionJob = scope?.launch {
            resultSenderViewModel.results.collect { (key, data) ->
                // Используй withContext(Dispatchers.Main) чтобы UI не лагал
                withContext(Dispatchers.Main) {
                    when (key) {
                        ResultKeys.VIDEO_PLAYER_ANIME_EPISODE_INFORMATION -> {
                            val info = data as Triple<objectData?, List<objectData>, Int>
                            val ui = createUI(info)
                            addView(ui)
                        }
                        ResultKeys.VIDEO_PLAYER_IS_PLAYING -> setPlaying(data as Boolean)
                        ResultKeys.VIDEO_PLAYER_PLAYBACK_SPEED -> setPlaybackSpeed(data as Float)
                        ResultKeys.VIDEO_PLAYER_IS_CONTROLLER_VISIBLE -> setControllerVisible(data as Boolean)
                    }
                }
            }
        }
        toggleSystemBars(false,context)
    }

    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(context).build().also { player ->
            playerView.player = player // Привязываем "мозг" к "глазу"

            // Настройки по умолчанию
            player.playWhenReady = true // Автозапуск после загрузки
        }
    }

    var currentEpisodeId: Long? = null
    private fun createUI(info: Triple<objectData?, List<objectData>, Int>): ConstraintLayout {
        val contrainer = ConstraintLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        uiContainer = contrainer
        val boldFont = context.resources.getFont(R.font.google_sans_bold)
        val mediumFont = context.resources.getFont(R.font.google_sans_medium)
        val marginHorizontal = if (screenWidthDp <= 400) round(15f*baseDensity).toInt() else if (screenWidthDp <= 1200) round(30f* baseDensity).toInt() else round(60f* baseDensity).toInt()
        val marginTop = round(22f*baseDensity).toInt()
        val marginBetweenElements = round(15f*baseDensity).toInt()
        val playButtonSize = round(48f*baseDensity).toInt()
        val buttonSize = round(36f*baseDensity).toInt()
        val watchedLineHeight = round(3f*baseDensity).toInt()
        val watchedLineWidth = screenWidth - marginHorizontal*2
        val closeAndPlayButtonsIcosSize = round(playButtonSize.toFloat() / 2f).toInt()
        val buttonsIcoSize = round(buttonSize.toFloat() / 2f).toInt()
        val buttonsBackgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 1000f
            setColor("#40000000".toColorInt())
        }
        val thisEpisode = info.second[info.third]
        currentEpisodeId = thisEpisode.id
        val nameText = info.first?.name ?: ""
        val episodeNameText = thisEpisode.name ?: ""
        val episodeNumText = "Эпизод ${info.third + 1}"
        var lengthText = "${convertToStringTime(thisEpisode.alreadyWatched)} / ${convertToStringTime(thisEpisode.length)}"
        var nameTextSize = round(20f*baseDensity)
        val maxNameTextHeight = round(screenHeight.toFloat() / 10f).toInt()
        if (optimizeText("j", screenWidth, nameTextSize, false, mediumFont).totalHeight > maxNameTextHeight) {
            nameTextSize = getTextSizeByHeight(maxNameTextHeight, boldFont)
        }
        val lengthTextTextSize = round(nameTextSize / 1.3f)
        val closeButton = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonSize,
                buttonSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(marginHorizontal, marginTop, 0, 0)
            layoutParams = layoutparams1
            background = buttonsBackgroundDrawable
            id = View.generateViewId()
        }
        val closeButtonIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                closeAndPlayButtonsIcosSize,
                closeAndPlayButtonsIcosSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setImageResource(R.drawable.close_ico)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        closeButton.addView(closeButtonIco)
        contrainer.addView(closeButton)
        val name = StrokeTextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToEnd = closeButton.id
            layoutparams1.topToTop = closeButton.id
            layoutParams = layoutparams1
            text = nameText
            maxLines = 2
            ellipsize = TextUtils.TruncateAt.END
            this.typeface = mediumFont
            maxWidth = screenWidth - marginHorizontal*2 - buttonSize - marginBetweenElements
            setTextSize(TypedValue.COMPLEX_UNIT_PX, nameTextSize)
            setTextColor("#FFFFFF".toColorInt())
            includeFontPadding = false
            measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            val difference = buttonSize - measuredHeight
            val lp1 = layoutParams as ConstraintLayout.LayoutParams
            lp1.setMargins(0,(round(difference.toFloat() / 2f).toInt()),0,0)
            layoutParams = lp1
        }
        contrainer.addView(name)
        val playButton = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                playButtonSize,
                playButtonSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,0,0,marginTop)
            layoutParams = layoutparams1
            background = buttonsBackgroundDrawable
            id = View.generateViewId()
        }
        val playButtonIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                closeAndPlayButtonsIcosSize,
                closeAndPlayButtonsIcosSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            tag = "play_ico"
            setImageResource(if (isPlayingState) R.drawable.pause_ico else R.drawable.play_arrow_filled_ico)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        playButton.addView(playButtonIco)
        contrainer.addView(playButton)
        val nextButton = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonSize,
                buttonSize
            )
            layoutparams1.topToTop = playButton.id
            layoutparams1.bottomToBottom = playButton.id
            layoutparams1.startToEnd = playButton.id
            layoutparams1.setMargins(marginBetweenElements,0,0,0)
            layoutParams = layoutparams1
            background = buttonsBackgroundDrawable
            id = View.generateViewId()
        }
        val nextButtonIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonsIcoSize,
                buttonsIcoSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setImageResource(R.drawable.skip_next_filled_ico)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        nextButton.addView(nextButtonIco)
        contrainer.addView(nextButton)
        val previousButton = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonSize,
                buttonSize
            )
            layoutparams1.topToTop = playButton.id
            layoutparams1.bottomToBottom = playButton.id
            layoutparams1.endToStart = playButton.id
            layoutparams1.setMargins(0,0,marginBetweenElements,0)
            layoutParams = layoutparams1
            background = buttonsBackgroundDrawable
            id = View.generateViewId()
        }
        val previousButtonIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonsIcoSize,
                buttonsIcoSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setImageResource(R.drawable.skip_previous_filled_ico)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        previousButton.addView(previousButtonIco)
        contrainer.addView(previousButton)
        val episodesMenuButton = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonSize,
                buttonSize
            )
            layoutparams1.topToTop = playButton.id
            layoutparams1.bottomToBottom = playButton.id
            layoutparams1.startToStart = closeButton.id
            layoutParams = layoutparams1
            background = buttonsBackgroundDrawable
            id = View.generateViewId()
        }
        val episodesMenuIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonsIcoSize,
                buttonsIcoSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setImageResource(R.drawable.episodes_menu_open_ico)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        episodesMenuButton.addView(episodesMenuIco)
        contrainer.addView(episodesMenuButton)

        val sizeButton = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonSize,
                buttonSize
            )
            layoutparams1.topToTop = playButton.id
            layoutparams1.bottomToBottom = playButton.id
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,0,marginHorizontal,0)
            layoutParams = layoutparams1
            background = buttonsBackgroundDrawable
            id = View.generateViewId()
        }
        val sizeButtonIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonsIcoSize,
                buttonsIcoSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            val activity = context as? Activity ?: null
            val or = activity?.requestedOrientation
            if (or == null || or == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) setImageResource(R.drawable.maxsimize_ico) else setImageResource(R.drawable.minimize_ico)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        sizeButton.addView(sizeButtonIco)
        contrainer.addView(sizeButton)

        val settingsButton = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonSize,
                buttonSize
            )
            layoutparams1.topToTop = playButton.id
            layoutparams1.bottomToBottom = playButton.id
            layoutparams1.endToStart = sizeButton.id
            layoutparams1.setMargins(0,0,marginBetweenElements,0)
            layoutParams = layoutparams1
            background = buttonsBackgroundDrawable
        }
        val settingsButtonIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonsIcoSize,
                buttonsIcoSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setImageResource(R.drawable.settings_ico)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        settingsButton.addView(settingsButtonIco)
        contrainer.addView(settingsButton)

        val watchedLine = ImageView(context).apply {
            val padding = round(7f*baseDensity).toInt()
            val layoutparams1 = ConstraintLayout.LayoutParams(
                watchedLineWidth,
                watchedLineHeight + padding*2
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToTop = playButton.id
            layoutparams1.setMargins(0,0,0,marginBetweenElements-padding)
            layoutParams = layoutparams1
            setPadding(0,padding,0,padding)
            setImageResource(R.drawable.watched_line_color)
            id = View.generateViewId()
        }
        contrainer.addView(watchedLine)
        val alreadyWachedLine = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                1,
                watchedLineHeight
            )
            layoutparams1.startToStart = watchedLine.id
            layoutparams1.topToTop = watchedLine.id
            layoutparams1.bottomToBottom = watchedLine.id
            layoutParams = layoutparams1
            setImageResource(R.drawable.already_watched_line_color)
            id = View.generateViewId()
        }
        contrainer.addView(alreadyWachedLine)

        val length = StrokeTextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            layoutparams1.bottomToTop = watchedLine.id
            layoutparams1.endToEnd = watchedLine.id
            layoutparams1.setMargins(0,0,0,marginBetweenElements)
            layoutParams = layoutparams1
            text = lengthText
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            this.typeface = mediumFont
            setTextSize(TypedValue.COMPLEX_UNIT_PX, lengthTextTextSize)
            setTextColor("#FFFFFF".toColorInt())
            includeFontPadding = false
            measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }
        contrainer.addView(length)

        val episodeName = StrokeTextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToStart = closeButton.id
            layoutparams1.bottomToTop = watchedLine.id
            layoutparams1.setMargins(0,0,0,marginBetweenElements)
            layoutParams = layoutparams1
            text = episodeNameText
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            this.typeface = boldFont
            setTextSize(TypedValue.COMPLEX_UNIT_PX, lengthTextTextSize)
            setTextColor("#BF9C9C9C".toColorInt())
            id = generateViewId()
            maxWidth = screenWidth - marginHorizontal*2 - marginBetweenElements - length.measuredWidth
        }
        contrainer.addView(episodeName)

        val episodeNum = StrokeTextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToStart = episodeName.id
            layoutparams1.bottomToTop = episodeName.id
            layoutParams = layoutparams1
            text = episodeNumText
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            this.typeface = boldFont
            setTextSize(TypedValue.COMPLEX_UNIT_PX, nameTextSize)
            setTextColor("#FFFFFF".toColorInt())
            includeFontPadding = false
            id = generateViewId()
            maxWidth = screenWidth - marginHorizontal*2 - marginBetweenElements - length.measuredWidth
        }
        contrainer.addView(episodeNum)
        val totalDuration = info.second[info.third].length

        // Функция для мгновенного обновления UI (вынесли, чтобы не дублировать)
        fun updateProgressUI(currentMs: Long, totalMs: Long) {
            val total = if (totalMs > 0) totalMs else totalDuration
            length.text = "${convertToStringTime(currentMs / 1000)} / ${convertToStringTime(total / 1000)}"

            val progress = if (total > 0) currentMs.toFloat() / total.toFloat() else 0f
            val newWidth = (watchedLineWidth * progress).toInt().coerceAtLeast(1)
            alreadyWachedLine.layoutParams = (alreadyWachedLine.layoutParams as ConstraintLayout.LayoutParams).apply {
                width = newWidth
            }
        }

        // Добавляем слушатель, чтобы поймать конец видео
        exoPlayer?.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == androidx.media3.common.Player.STATE_ENDED) {
                    // Когда видео кончилось, форсируем 100% прогресс
                    val total = exoPlayer?.duration ?: totalDuration
                    updateProgressUI(total, total)
                }
            }
        })

        var lastSavedPosition = 0L
        val saveIntervalMs = 2000L // 10 секунд
        var k = 0L
        // 1. ЛОГИКА ОБНОВЛЕНИЯ PROGRESS (Корутина)
        context.lifecycleOwner?.lifecycleScope?.launch {
            while (isActive) {
                // Добавляем проверку: если видео почти кончилось, тоже обновляем,
                // даже если isPlaying уже может быть false
                if (exoPlayer?.isPlaying == true) {
                    val current = exoPlayer?.currentPosition ?: 0L
                    val total = exoPlayer?.duration ?: totalDuration

                    withContext(Dispatchers.Main) {
                        updateProgressUI(current, total)
                    }
                    if (k == saveIntervalMs) {
                        saveProgressToDb(thisEpisode.id, round(current.toFloat() / 1000f).toLong()) // Вызываем сохранение
                        lastSavedPosition = current
                        k = 0
                    }
                    k += 50
                }
                delay(50) // Обновление ~20 раз в секунду
            }
        }

        // 2. ЛОГИКА PLAY / PAUSE
        fun togglePlayPause() {
            val isCurrentlyPlaying = exoPlayer?.isPlaying ?: false
            resultSenderViewModel.sendResult(ResultKeys.VIDEO_PLAYER_CHANGE_IS_PLAYING, !isCurrentlyPlaying)
            if (isCurrentlyPlaying) {
                exoPlayer?.pause()
                playButtonIco.setImageResource(R.drawable.play_arrow_filled_ico)
            } else {
                // Если видео было на конце, и нажали Play - перематываем в начало
                if (exoPlayer?.playbackState == androidx.media3.common.Player.STATE_ENDED) {
                    exoPlayer?.seekTo(0)
                }
                exoPlayer?.play()
                playButtonIco.setImageResource(R.drawable.pause_ico)
            }
        }

        playButton.setOnClickListener { togglePlayPause() }

        // 3. ЛОГИКА НАЖАТИЯ НА ПУСТОЕ МЕСТО
        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                togglePlayPause()
                resetHideTimer()
                showUi(true)
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (contrainer.alpha > 0.8f) {
                    showUi(false)
                }
                else {
                    showUi(true)
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })

        contrainer.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        // 4. ЛОГИКА ПЕРЕМОТКИ ЖЕСТОМ
        watchedLine.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                    resetHideTimer()
                    val x = event.x.coerceIn(0f, view.width.toFloat())
                    val progress = x / view.width.toFloat()
                    val total = exoPlayer?.duration ?: totalDuration
                    val seekTo = (total * progress).toLong()

                    exoPlayer?.seekTo(seekTo)

                    // ОБНОВЛЯЕМ ТЕКСТ И ПОЛОСКУ ПРЯМО ТУТ (решает баг №1)
                    updateProgressUI(seekTo, total)
                    true
                }
                else -> false
            }
        }


        // 5. ОСТАЛЬНЫЕ КНОПКИ
        var alreadyClosed = false
        closeButton.setOnClickListener {
            if (!alreadyClosed) {
                alreadyClosed = true
                Log.d("Player", "Close")
                // Здесь должна быть твоя логика закрытия слоя (например, через resultSender)
                resultSenderViewModel.sendResult(ResultKeys.VIDEO_PLAYER_CLOSE_FORM_PLAYER, true)
            }
        }

        settingsButton.setOnClickListener {
            // openSettings() - как ты и просил
            Log.d("Player", "Open Settings Overlay")
        }

        episodesMenuButton.setOnClickListener {
            // openEpisodesMenu()
            Log.d("Player", "Open Episodes Menu")
        }
        sizeButton.setOnClickListener {
            changeOrientation(context, false)
        }

        // Кнопки Next/Prev (пока заглушки)
        nextButton.setOnClickListener { Log.d("Player", "Next Episode") }
        previousButton.setOnClickListener { Log.d("Player", "Prev Episode") }
        resetHideTimer()
        return contrainer
    }

    private fun showUi(show: Boolean) {
        val container = uiContainer ?: return
        resultSenderViewModel.sendResult(ResultKeys.VIDEO_PLAYER_CHANGE_IS_CONTROLLER_VISIBLE, show)
        if (show) {
            if (container.alpha == 1f) {
                resetHideTimer()
                return
            }
            container.animate()
                .alpha(1f)
                .setDuration(uiFadeDuration)
                .withEndAction { resetHideTimer() }
                .start()
        } else {
            container.animate()
                .alpha(0f)
                .setDuration(uiFadeDuration)
                .withEndAction {  }
                .start()
        }
    }

    private fun resetHideTimer() {
        hideUiJob?.cancel()
        hideUiJob = context.lifecycleOwner?.lifecycleScope?.launch {
            delay(4000)
            withContext(Dispatchers.Main) {
                showUi(false)
            }
        }
    }

    // Метод для загрузки серии
    fun playEpisode(url: String, currentPos: Long) {
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.seekTo(currentPos*1000)
        exoPlayer?.prepare()
    }
    // Управление скоростью
    fun setPlaybackSpeed(speed: Float) {
        resultSenderViewModel.sendResult(ResultKeys.VIDEO_PLAYER_CHANGE_PLAYBACK_SPEED, speed)
        exoPlayer?.setPlaybackSpeed(speed)
    }

    // Управление состоянием Play/Pause (внешнее)
    fun setPlaying(play: Boolean) {
        if (play) exoPlayer?.play() else exoPlayer?.pause()
        updatePlayPauseIco(play)
    }

    // Обновление иконки (вынесено, чтобы не дублировать)
    private fun updatePlayPauseIco(playing: Boolean) {
        val ico = uiContainer?.findViewWithTag<ImageView>("play_ico")
        ico?.setImageResource(if (playing) R.drawable.pause_ico else R.drawable.play_arrow_filled_ico)
        isPlayingState = playing
    }

    // Управление видимостью контроллеров
    fun setControllerVisible(visible: Boolean) {
        showUi(visible)
    }

    // КРИТИЧЕСКИ ВАЖНО: Освобождение ресурсов
    fun release() {
        val current = exoPlayer?.currentPosition ?: 0L
        val idi = currentEpisodeId
        if (idi != null) {
            saveProgressToDb(idi, round(current.toFloat() / 1000f).toLong())
        }
        exoPlayer?.release()
        exoPlayer = null
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
        playerSubscriptionJob?.cancel()
    }

    private fun saveProgressToDb(episodeId: Long, positionSeconds: Long) {
        resultSenderViewModel.sendResult(ResultKeys.VIDEO_PLAYER_EDIT_EPISODE_ALREADY_WATCHED, Pair(episodeId,positionSeconds))
    }
}