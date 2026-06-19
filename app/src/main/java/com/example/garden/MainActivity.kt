package com.example.garden

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View.LAYER_TYPE_SOFTWARE
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.drawable.toDrawable
import java.io.File
import kotlin.math.round
import androidx.core.graphics.toColorInt
import androidx.core.graphics.createBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlin.math.floor
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import coil.imageLoader
import coil.request.ImageRequest
import com.example.garden.database.ElementType
import com.example.garden.database.Genre
import com.example.garden.database.ImageData
import com.example.garden.database.ImageSource
import com.example.garden.database.LinkData
import kotlinx.coroutines.launch
import kotlin.math.ceil
import androidx.core.view.isEmpty
import androidx.core.widget.NestedScrollView
import com.example.garden.database.AppDatabase
import com.example.garden.database.groups.AppGroupsDataBase
import com.example.garden.database.groups.groupsData
import com.example.garden.database.groups.groupsDataDao
import com.example.garden.database.objectDataDao
import com.example.garden.viewmodel.MainViewModel
import com.example.garden.appsettings.AnimeSettingsState
import com.example.garden.appsettings.SettingsManager
import com.example.garden.database.SizeType
import com.example.garden.viewmodel.MultiViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.getValue
import kotlin.math.pow
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.text.LineBreaker
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.slider.Slider
import kotlin.math.min
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.math.max
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.garden.database.LinkType
import java.util.Collections
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import com.example.garden.customView.OptimizedTextView
import com.example.garden.database.MusicGenre
import com.example.garden.database.objectData
import com.example.garden.players.AnimeVideoPlayer
import com.example.garden.viewmodel.ResultSenderViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.isNotEmpty

var orientationOld = 0
var orientationNow = 0
var screenWidth = 0
var screenHeight = 0
var screenWidthDp = 0
var screenHeightDp = 0
var alreadyCreated = false
val delitRad = 3f
lateinit var baseBlob1: ShapeDrawable
lateinit var baseBlob2: ShapeDrawable
lateinit var baseBlob3: ShapeDrawable
lateinit var baseBlurEffectForBloobs: RenderEffect

val icoSizesRatio = 1.42f
var blob12OvalSize = 0
var blob12FullSize = 0
var blob3OvalSize = 0
var blob3FullSize = 0
var blob1MarginEnd = 0
var blob1MarginTop = 0
var blob2MarginStart = 0
var blob2MarginTop = 0
var blob3MarginEnd = 0
var blob3MarginTop = 0
var baseblob1Id = -1
var baseblob2Id = -1
var baseblob3Id = -1
var alreadyShowedAddBlock = false
var dotDrawables = mutableListOf<GradientDrawable>()
val blobsNeedToHideOnAlbomOrientationIdsList = mutableListOf<Int>()
val genreColors = mapOf(
    Pair(Genre.Drama, "#37619F"),
    Pair(Genre.Comedy, "#FFD600"),
    Pair(Genre.Romance, "#FF85A2"),
    Pair(Genre.EverydayLife, "#A0E4B0"),
    Pair(Genre.School, "#A0E4B0"),
    Pair(Genre.Psychological, "#9D5CFF"),
    Pair(Genre.Shonen, "#FF9100"),
    Pair(Genre.ActionMovie, "#FF4B4B"),
    Pair(Genre.MartialArts, "#FF4B4B"),
    Pair(Genre.Action, "#FF4B4B"),
    Pair(Genre.Adventures, "#4CAF50"),
    Pair(Genre.Shoujo, "#F48FB1"),
    Pair(Genre.Fantasy, "#1FA2FF"),
    Pair(Genre.Isekai, "#00E5FF"),
    Pair(Genre.ScienceFiction, "#2979FF"),
    Pair(Genre.Cyberpunk, "#2979FF"),
    Pair(Genre.Fantastic, "#2979FF"),
    Pair(Genre.Supernatural, "#2979FF"),
    Pair(Genre.PostApocalypse, "#8D6E63"),
    Pair(Genre.Detective, "#BDBDBD"),
    Pair(Genre.Thriller, "#D32F2F"),
    Pair(Genre.Horrors, "#D32F2F"),
    Pair(Genre.Mysticism, "#7E57C2"),
    Pair(Genre.Etty, "#F06292"),
    Pair(Genre.Harem, "#FFD54F"),
    Pair(Genre.Age, "#BFDFDFDF"),
    Pair(Genre.Year, "#BFDFDFDF"),
    Pair(Genre.Sezon, "#BFDFDFDF"),
    Pair(Genre.Episodes, "&")
)
val musicGenreColors = mapOf(
    Pair(MusicGenre.Rock, "#E53935"),
    Pair(MusicGenre.Jazz, "#FFC107"),
    Pair(MusicGenre.LoFi, "#B39DDB"),
    Pair(MusicGenre.Pop, "#FF4081"),
    Pair(MusicGenre.Classical, "#F5F5DC"),
    Pair(MusicGenre.Metal, "#212121"),
    Pair(MusicGenre.Electronic, "#00E5FF"),
    Pair(MusicGenre.HipHop, "#FB8C00"),
    Pair(MusicGenre.Country, "#8D6E63"),
    Pair(MusicGenre.Ambient, "#1A237E"),
)
var genreNames = mapOf<Genre, String>()
var musicGenreNames = mapOf<MusicGenre, String>()
var density = 0f
var baseDensity = 0f
var reversDensity = 0f
var statusBarHeight = 0
var layersList = mutableListOf<Layer>()
var lastElevation = 0
var animePageObjectId: Long = -1
var objectsList = listOf<objectData2>()
var groupsList = listOf<groupsData>()
var animeSezonsSettingsState = AnimeSettingsState()
var mainPageRecyclerId: Int = 0
var animePageRecyclerId: Int = 0
var steps = listOf<Float>()
var maxOverLayElevation = 100f

val _bsdFlow = MutableSharedFlow<List<BottomSheetDialogElement>>(extraBufferCapacity = 1)
val bsdFlow = _bsdFlow.asSharedFlow()

enum class BsdButtonsTags {
    animePage_extraButton_changeAllEpisodesWatchedMark,
}
enum class LayerMode {
    Full, Mini
}
sealed class Layer {
    data class AnimePage(
        val elevation: Int,
        var mainRecyclerScrollPositionInPx: Int,
        val layoutObjId: Long,
        var state: Int
    ) : Layer()
    data class MainPage(
        val elevation: Int,
        val pageId: Int, // Для MainPage
        val scrollPositionCarousels: MutableMap<Int, Int>,
        val activeDotPositionCarousels: MutableMap<Int, Int>,
        val scrollPositionsInPx: MutableMap<Int, Int>,
        var mainRecyclerScrollPositionInPx: Int,
        var mainRecyclerScrollPosition: Int,
        var state: Int // У страниц будут свои состояния, они будут описаны в коде самой страницы
    ) : Layer()
    data class OverLay(
        val elevation: Int,
        val tag: pageTags,
        val info: OverLayLayer
    ) : Layer()
    data class VideoPlayer(
        val elevation: Int,
        val id: Long,
        var isPlaying: Boolean,
        var playbackSpeed: Float = 1.0f,
        var isControllerVisible: Boolean = true
    ) : Layer()
}
sealed class OverLayLayer {
    data class CreateAnimePage(
        var name: String,
        var image: ImageData?,
        var description: String,
        var author: String,
        var genreList: List<Genre>,
        val episodesList: List<episodeInfo>,
        val type: ElementType
    ) : OverLayLayer()
    data class GenreChoice(
        var genreList: List<Pair<Boolean, Genre>>,
        val key: String
    ) : OverLayLayer()
    data class CreateCarouselPage(
        val name: String,
        val childsCornerRadius: SizeType?,
        val childsShowName: Boolean,
        val childsNamePosition: Int?,
        val childsShowAlreadyWatchedLine: Boolean,
        val layoutType: Int?,
        val showWatchAllButton: Boolean,
        val maxObjectsInOneLine: Int?,
        val maxLines: Int?,
        val dovodchik: Boolean,
        val showDovodchikDots: Boolean,
    ) : OverLayLayer()
}
data class episodeInfo(
    var image: ImageData?,
    var name: String,
    val link: LinkData,
    val length: Long,
)
data class listDot(
    val itemPositionInPx: Int,
)
data class listDot2(
    val dot: ImageView,
    val targetWidth: Int,
    val pageAnimOnThisDot: Boolean,
    val number: Int
)
data class createDovodchikDotsReturn(
    val layout: ConstraintLayout,
    val list: List<Pair<listDot2, listDot>>,
    val numTextView: TextView,
)
data class objectData2(
    val id: Long = 0,

    // Навигация
    var page: Int,              // 0 - home ; 1 - anime ; 2 - manga ; 3 - music ; 4 - download
    var position: Int,          // Позиция на странице/в родителе

    // Имя
    var name: String? = null,   // Название (для карусели или карточки)
    var showName: Boolean = false, // Показывать ли название
    var namePosition: Int? = 0, // 1 - Внутри карточки 0 - снаружи
    var showAlreadyWatchedLine: Boolean = true,
    var showAvatar: Boolean = false,  // Это для каруселей, чтобы показывать рядом с названием карусели ник и аву пользователя
    var childsShowName: Boolean = false, // Показывать ли название
    var childsNamePosition: Int? = 0, // 1 - Внутри карточки 0 - снаружи
    var childsShowAlreadyWatchedLine: Boolean = true,

    // Изображение (оно же превью)
    var image: ImageData? = null,

    // Доп. инфа
    var description: String? = null,
    var author: String? = null,
    var type: String? = null,  // Аниме, манга, музыка и т.д
    var alreadyWatched: Long, // Минуты и секунды до куда досмотрел пользователь
    var length: Long,  // Минуты и секунды всей длинны

    // Размеры (для карточек)
    var width: Int? = null,
    var height: Int? = null,
    var childsCornerRadius: SizeType? = null,

    var layoutType: Int? = null,  // 0 - сетка (т.е constraint layout с расположенными в виде сетки view вместо recycler view, 1 - с recycler view.
    // Распостраняется и на карточки (0 - карточка - это сетка из карточек (у такой карточки должны быть childs, именно они выступают в роли карточек в сетке, если их нету карточка считается обычной), 1 - обычная карточка)
    var dovodchik: Boolean = false,
    var showDovodchikDots: Boolean = false,

    // Для layout type 0
    var maxObjectsInOneLine: Int? = null,
    var maxLines: Int? = null,

    // Это для recycler view параметр (в основном нужно чтобы для удобства отодвинуть view от начала экрана)
    var paddingHorizontal: Int? = null,  // Используется, как marginStart у 1 карточки в recycler и как marginStart/End у constraint layout родителя в grid
    var paddingVertical: Int? = null,  // Используется, как marinTop в каруселей
    var marginBetweenElementsHorizontal: Int? = null,  // Используется, как marginStart у всех карточек, кроме 1 и также, как marginEnd у последней в recycler и как marginStart у всех карточек кроме 1 в grid
    var marginBetweenElementsVertical: Int? = null,  // Используется только в grid, как marginTop у 1 карточки линии, кроме 1 линии

    var childs: List<objectData2>,
    // Ссылка (куда ведет элемент)
    var link: LinkData? = null,

    // Тип элемента (для удобства фильтрации и только для неё, про макет с.м link -> template)
    var elementType: ElementType,
    var genre: List<Genre>? = null,
)
data class animePageSezonsAdapterListFormat(
    var obj: objectData2,
    var settingsState: AnimeSettingsState
)
data class createM3ButtonReturn(
    val container: ConstraintLayout,
    val childs: List<View>,
    val width: Int
)
data class createGridOfGenresReturn(
    val container: ConstraintLayout,
    val sumHeight: Int,
    val sumWidth: Int
)
sealed class BottomSheetDialogElement {
    data class Button (
        val tag: BsdButtonsTags,
        val icoId: Int,
        val text: String,
        val openThePage: Boolean,
    ) : BottomSheetDialogElement()

    data class SegmentedButton(
        val icoId: Int,
        val text: String,
        val sizeType: SizeType,
        val options: List<Pair<segmentedButtonOptions, BsdButtonsTags>>,
    ) : BottomSheetDialogElement()

    data class DropdownRow(
        val text: String,
        val icoId: Int,
        val buttonIcoId: Int?,
        val options: List<Pair<String, BsdButtonsTags>>,
    ) : BottomSheetDialogElement()

    data class Slider(
        val tag: BsdButtonsTags,
        val icoId: Int,
        val text: String,
        val stops: List<Float>,
        val createSteps: Boolean,
    ) : BottomSheetDialogElement()
}
data class segmentedButtonOptions(
    var text: String,
    var icoId: Int?,
    var isActive: Boolean,
)
enum class pageTags {
    animePage, createAnimePage, genreChoice, videoPlayer
}
sealed class infoOfPageToShow {
    data class infoOfAnimePage(
        val id: Long,
        val isItNewLayer: Boolean
    ) : infoOfPageToShow()
    data class infoOfOverlayLayer (
        val info: OverLayLayer,
        val isItNewLayer: Boolean
    ) : infoOfPageToShow()
    data class infoOfVideoPlayer (
        val id: Long,
        val isItNewLayer: Boolean
    ) : infoOfPageToShow()
}
sealed class createFlatGridInput {
    data class EditEpisodes (
        val info: List<episodeInfo>,
        val callback: (List<episodeInfo>) -> Unit
    ) : createFlatGridInput()
    data class Episodes (
        val info: List<objectData2>
    ) : createFlatGridInput()
    data class Music (
        val info: List<objectData2>
    ) : createFlatGridInput()
}
enum class fileType {
    IMAGE, VIDEO, AUDIO
}
data class spaceItemDecorationInput (
    val spaces: List<Int>,
    val firstObjectSpaces: List<Int>
)

class App: Application() {
    lateinit var settingsManager: SettingsManager
    lateinit var database: AppDatabase
    lateinit var dao: objectDataDao
    lateinit var groupsDataBase: AppGroupsDataBase
    lateinit var groupsDao: groupsDataDao

    override fun onCreate() {
        super.onCreate()
        settingsManager = SettingsManager(this)
        database = AppDatabase.getDatabase(this)
        dao = database.objectDataDao()
        groupsDataBase = AppGroupsDataBase.getDatabase(this)
        groupsDao = groupsDataBase.groupsDataDao()
    }
}

object ResultKeys {
    const val CREATE_CARD_GENRE_CHOICE = "CREATE_CARD_PAGE_GENRE_CHOICE"
    const val CREATE_CARD_ADD_EPISODES = "CREATE_CARD_PAGE_ADD_EPISODES"
    const val CREATE_CARD_CHANGE_IMAGE = "CREATE_CARD_PAGE_CHANGE_IMAGE"
    const val CREATE_CARD_APPLY = "CREATE_CARD_PAGE_APPLY"
    const val VIDEO_PLAYER_ANIME_EPISODE_INFORMATION = "VIDEO_PLAYER_ANIME_EPISODE_INFORMATION"
    const val VIDEO_PLAYER_IS_PLAYING = "VIDEO_PLAYER_IS_PLAYING"
    const val VIDEO_PLAYER_PLAYBACK_SPEED = "VIDEO_PLAYER_PLAYBACK_SPEED"
    const val VIDEO_PLAYER_IS_CONTROLLER_VISIBLE = "VIDEO_PLAYER_IS_CONTROLLER_VISIBLE"
    const val VIDEO_PLAYER_CLOSE_FORM_PLAYER = "VIDEO_PLAYER_CLOSE_FORM_PLAYER"
    const val VIDEO_PLAYER_CHANGE_PLAYBACK_SPEED = "VIDEO_PLAYER_CHANGE_PLAYBACK_SPEED"
    const val VIDEO_PLAYER_CHANGE_IS_CONTROLLER_VISIBLE = "VIDEO_PLAYER_CHANGE_IS_CONTROLLER_VISIBLE"
    const val VIDEO_PLAYER_CHANGE_IS_PLAYING = "VIDEO_PLAYER_CHANGE_IS_PLAYING"
    const val VIDEO_PLAYER_EDIT_EPISODE_ALREADY_WATCHED = "VIDEO_PLAYER_EDIT_EPISODE_ALREADY_WATCHED"
}

data class createCardApply(
    val name: String,
    val image: ImageData?,
    val description: String,
    val author: String,
    val genreList: List<Genre>,
    val episodesList: List<episodeInfo>
)


fun createCard(width: Int?, height: Int?, showName: Boolean, namePosition: Int?, image: ImageData?, name: String?, author: String?, alreadyWatched: Long, length: Long, showAlreadyWatchedLine: Boolean, context: Context, items: List<objectData2>, cornerRadius: SizeType?, optimizateCardSize: Boolean = true, gridMode: Boolean = false): Triple<List<View>, Int, Pair<Int, Int>> {
    val res = mutableListOf<View>()
    val font = ResourcesCompat.getFont(context, R.font.google_sans_medium)
    val textSizee = floor(40f*density) // в px
    var cardViewId = 0
    var alreadyWatchedLineId = 0
    var cardViewWidth = 0
    var size = cardScaleCalcFree(items, 50, 30, width, height, screenWidth)
    if (width != null && height != null && !optimizateCardSize) {
        size = Pair(width, height)
    }
    var cardHeight = 0
    var cardWidth = 0
    if (author != null && name != null && showName && namePosition == 0 && !gridMode) {
        cardHeight = size.second - optimizeText(name, size.first, textSizee, false, font).totalHeight*3
        cardWidth = (cardHeight.toFloat() * (size.first.toFloat() / size.second)).toInt()
    }
    else if (author == null && name != null && showName && namePosition == 0 && !gridMode) {
        cardHeight = size.second - optimizeText(name, size.first, textSizee, false, font).totalHeight*2
        cardWidth = (cardHeight.toFloat() * (size.first.toFloat() / size.second)).toInt()
    }
    else if ((!showName) || (namePosition == 1) || gridMode) {
        cardWidth = size.first
        cardHeight = size.second
    }
    var res2 = cardWidth
    if (showName && namePosition == 1) {
        res2 = size.first
    }
    val cardView = CardView(context).apply {
        cardViewWidth = cardWidth
        val layoutparams1 = ConstraintLayout.LayoutParams(
            cardWidth,
            cardHeight
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.setMargins(0,0,0, 0)
        layoutParams = layoutparams1
        cardElevation = 0f
        radius = getAdaptiveRadius(cardWidth, cornerRadius ?: SizeType.SMALL)
        val newId = View.generateViewId()
        id = newId
        cardViewId = newId
    }
    val constraintLayoutInsideCardView = ConstraintLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.MarginLayoutParams.MATCH_PARENT,
            ViewGroup.MarginLayoutParams.MATCH_PARENT
        )
    }
    val imageView = ImageView(context).apply {
        layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.MarginLayoutParams.MATCH_PARENT,
            ViewGroup.MarginLayoutParams.MATCH_PARENT
        )
        scaleType = ImageView.ScaleType.CENTER_CROP
        loadImage(image)
    }
    constraintLayoutInsideCardView.addView(imageView)
    if (showAlreadyWatchedLine) {
        val alreadyWatchedLine = ImageView(context).apply {
            var lineHeight = (cardHeight.toFloat()/50).toInt()
            var lineWidth = (cardWidth*((alreadyWatched.toFloat()/length))).toInt()
            if (cardWidth <= 0) {
                lineWidth = (size.first*((alreadyWatched.toFloat()/length))).toInt()
            }
            if (lineHeight == 0) {
                lineHeight = 10
            }
            val layoutparams1 = ConstraintLayout.LayoutParams(
                lineWidth,
                lineHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            setBackgroundColor("#da1d37".toColorInt())
            layoutParams = layoutparams1
            val newId = View.generateViewId()
            id = newId
            alreadyWatchedLineId = newId
        }
        constraintLayoutInsideCardView.addView(alreadyWatchedLine)
    }
    if (name != null && showName && namePosition == 1) {
        val textView1 = TextView(context).apply {
            val optimizatedText = optimizeText(name, res2, textSizee, false, font, 1)
            val layoutparams1 = ConstraintLayout.LayoutParams(
                res2,
                optimizatedText.totalHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            if (showAlreadyWatchedLine) {
                layoutparams1.bottomToTop = alreadyWatchedLineId
                layoutparams1.setMargins(15,0,0,0)
            }
            else {
                layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.setMargins(15,0,0,10)
            }
            layoutParams = layoutparams1
            text = optimizatedText.firstLine
            this.typeface = font
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
            includeFontPadding = false
            setShadowLayer(5f, 0f, 0f, Color.BLACK)
        }
        constraintLayoutInsideCardView.addView(textView1)
    }
    cardView.addView(constraintLayoutInsideCardView)
    res.add(cardView)
    if (name != null && showName && namePosition == 0) {

        var textView1Id = 0
        var textView2Id = 0
        val optimizatedText = optimizeText(name, cardViewWidth, textSizee, false, font)
        val textView1 = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                cardViewWidth,
                optimizatedText.totalHeight
            )
            layoutparams1.startToStart = cardViewId
            layoutparams1.topToBottom = cardViewId
            layoutparams1.setMargins(0,0,0,0)
            layoutParams = layoutparams1
            text = optimizatedText.firstLine
            this.typeface = font
            val newId = View.generateViewId()
            id = newId
            textView1Id = newId
            setTextColor(Color.WHITE)
            includeFontPadding = false
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
            setShadowLayer(5f, 0f, 0f, Color.BLACK)
        }
        res.add(textView1)
        val textView2 = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                cardViewWidth,
                optimizatedText.totalHeight
            )
            layoutparams1.startToStart = textView1Id
            layoutparams1.topToBottom = textView1Id
            layoutparams1.setMargins(0,0,0,0)
            layoutParams = layoutparams1
            text = optimizatedText.secondLine
            this.typeface = font
            setTextColor(Color.WHITE)
            val newId = View.generateViewId()
            id = newId
            textView2Id = newId
            includeFontPadding = false
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
            setShadowLayer(5f, 0f, 0f, Color.BLACK)
        }
        res.add(textView2)

        if (author != null) {
            val textView3 = TextView(context).apply {
                val optimizatedTextAuthor = optimizeText(author, cardViewWidth, textSizee, false, font, 1)
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    cardViewWidth,
                    optimizatedTextAuthor.totalHeight
                )
                layoutparams1.startToStart = textView1Id
                layoutparams1.topToBottom = textView2Id
                layoutParams = layoutparams1
                text = optimizatedTextAuthor.firstLine
                setTextColor(Color.WHITE)
                this.typeface = font
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
                setShadowLayer(5f, 0f, 0f, Color.BLACK)
            }
            res.add(textView3)
        }
    }
    else if (name != null && showName && namePosition == 1) {
        val gradientView = ImageView(context).apply {
            val gradientWidth = res2.coerceAtLeast(1)
            val gradientHeight = (size.second.toFloat() / 10).toInt().coerceAtLeast(2)
            val layoutparams1 = ConstraintLayout.LayoutParams(
                gradientWidth,
                gradientHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            val bitmap = createBitmap(gradientWidth, gradientHeight)
            val canvas = Canvas(bitmap)
            val colors = intArrayOf(
                "#393939".toColorInt(), // 0%
                "#393939".toColorInt(), // 30%
                "#00393939".toColorInt() // 100%
            )
            val positions = floatArrayOf(0f, 0.5f, 1f)

            val shader = LinearGradient(
                0f, gradientHeight.toFloat(), // низ
                0f, 0f,                       // верх
                colors,
                positions,
                Shader.TileMode.CLAMP
            )

            val paint = Paint().apply {
                this.shader = shader
            }

            canvas.drawRect(
                0f,
                0f,
                gradientWidth.toFloat(),
                gradientHeight.toFloat(),
                paint
            )

            val bitmapDrawable = bitmap.toDrawable(resources)
            background = bitmapDrawable
            layoutParams = layoutparams1
        }
        constraintLayoutInsideCardView.addView(gradientView)
    }
    return Triple(res,res2,size)
}
fun createGridOfChilds(objList: List<objectData2>, maxObjectsInOneLine: Int?, context: Context, lineWidth: Int?, parent: objectData2): ConstraintLayout {
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        id = View.generateViewId()
    }
    var size = Triple(0,0,0f)
    for (i in objList) {
        if (i.width != null && i.height != null) {
            var margin = 10
            if (parent.marginBetweenElementsHorizontal != null) {
                margin = parent.marginBetweenElementsHorizontal!!
            }
            size = if (lineWidth == null) {
                cardScaleCalcForGrid(i.width, i.height, screenWidth, margin, maxObjectsInOneLine)
            } else {
                cardScaleCalcForGrid(i.width, i.height, lineWidth, margin, maxObjectsInOneLine)
            }
            break
        }
    }
    var maxObjectsInOneLine2 = maxObjectsInOneLine
    if (maxObjectsInOneLine2 == null) {
        maxObjectsInOneLine2 = size.third.toInt()
    }
    var k = 0
    var lastFirstViewId = 0
    var lastViewId = 0
    var maxObjects: Int
    if (parent.maxLines != null) {
        maxObjects = maxObjectsInOneLine2 * parent.maxLines!!
        if (objList.size <= maxObjects) {
            maxObjects = objList.size
        }
    }
    else {
        maxObjects = objList.size
    }
    var marginH = 10
    if (parent.marginBetweenElementsHorizontal != null) {
        marginH = parent.marginBetweenElementsHorizontal!!
    }
    var marginV = 10
    if (parent.marginBetweenElementsVertical != null) {
        marginV = parent.marginBetweenElementsVertical!!
    }
    var height = 0
    for (i in 0 until maxObjects) {
        val objData = objList[i]
        val views = createCard(size.first, size.second, parent.childsShowName, parent.childsNamePosition, objData.image, objData.name, objData.author, objData.alreadyWatched, objData.length, parent.showAlreadyWatchedLine, context, objList, parent.childsCornerRadius,false, true)
        val cardContainer = ConstraintLayout(context).apply {
            val font = ResourcesCompat.getFont(context, R.font.google_sans_medium)
            val textSizee = floor(40f*density) // в px
            val cardConHeight = views.third.second + if (objData.name != null && parent.showName && parent.namePosition == 0) {optimizeText(objData.name!!, size.first, textSizee, false, font).totalHeight*2} else {0} + if (objData.name != null && parent.showName && parent.namePosition == 0) {if (objData.author != null) {optimizeText(objData.name!!, size.first, textSizee, false, font).totalHeight} else {0}} else {0}

            val layoutparams2 = ConstraintLayout.LayoutParams(
                if ((!parent.showName) || (parent.namePosition == 1)) {
                    views.third.first
                } else {
                    views.second
                },
                cardConHeight
            )
            val newId = View.generateViewId()
            id = newId
            var marginType: Int
            when (objData.position) {
                0 -> {
                    marginType = 1
                }
                else -> {
                    marginType = 2
                }
            }
            if (objData.position == k) {
                if (lastFirstViewId == 0) {
                    layoutparams2.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutparams2.setMargins(0,0,0,0)
                    height += cardConHeight
                }
                else {
                    layoutparams2.topToBottom = lastFirstViewId
                    when (marginType) {
                        1 -> {
                            layoutparams2.setMargins(0, 0, 0, 0)
                            height += cardConHeight
                        }
                        else -> {
                            layoutparams2.setMargins(0, marginV, 0, 0)
                            height += cardConHeight + marginV
                        }
                    }
                }
                layoutparams2.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                lastFirstViewId = newId
                lastViewId = 0
                k += maxObjectsInOneLine2
            }
            else {

                layoutparams2.setMargins(marginH,0,0,0)
                if (lastFirstViewId != 0 && lastViewId == 0) {
                    layoutparams2.startToEnd = lastFirstViewId
                    layoutparams2.topToTop = lastFirstViewId
                }
                else if (lastViewId != 0) {
                    layoutparams2.startToEnd = lastViewId
                    layoutparams2.topToTop = lastViewId
                }
                else {
                    layoutparams2.startToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutparams2.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                }
                lastViewId = newId
            }
            layoutParams = layoutparams2
        }

        for (i in views.first) {
            cardContainer.addView(i)
        }
        container.addView(cardContainer)
    }
    val layoutparams1 = container.layoutParams as ConstraintLayout.LayoutParams
    layoutparams1.height = height
    container.layoutParams = layoutparams1
    return container
}
fun createDovodchikDots(context: Context, items: List<objectData2>, paddingHorizontal: Int, marginBetweenElementsHorizontal: Int, showName: Boolean, namePosition: Int?): createDovodchikDotsReturn {
    val res = mutableListOf< Pair<listDot2, listDot>>()
    val dotsList = calculateAmountOfDots(items, paddingHorizontal, marginBetweenElementsHorizontal, context, showName, namePosition)
    val dotsAmount = dotsList.size
    val height1 = 35
    val margin = 0
    val maxDotsAmountOnScreen = floor((screenWidth -  300).toFloat() / (height1+margin)).toInt()
    var dotsOnScreen: Int
    var showDotsPageNum = false
    if (dotsAmount > maxDotsAmountOnScreen) {
        dotsOnScreen = maxDotsAmountOnScreen - 1
        showDotsPageNum = true
    }
    else {
        dotsOnScreen = dotsAmount
    }
    val firstDotMargin = if (showDotsPageNum) { 0 } else { ((screenWidth - dotsOnScreen * height1+margin).toFloat() / 2).toInt()}
    val lastDotMargin = if (showDotsPageNum) { 150 } else { ((screenWidth - dotsOnScreen * height1+margin).toFloat() / 2).toInt()}
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            height1
        )
    }
    var firstElementId = ConstraintLayout.LayoutParams.PARENT_ID
    var numId = 0
    var numRes = TextView(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            0,
            0
        )
    }
    if (showDotsPageNum) {
        val num = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                height1,
                height1
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(150,0,0,0)
            layoutParams = layoutparams1
            text = "1"
            setTextColor(resources.getColor(R.color.white))
            includeFontPadding = false
            typeface = ResourcesCompat.getFont(context, R.font.google_sans_medium)
            val paddings = calculateDigitParams(height1, '1', context)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, paddings.first)
            setPadding(paddings.second, paddings.third, 0,0)
            val newId = View.generateViewId()
            id = newId
            numId = newId
            firstElementId = newId
            setBackgroundResource(R.drawable.obvodka)
        }
        container.addView(num)
        numRes = num
    }
    var previousElementId = firstElementId
    val horizontalScrollView = HorizontalScrollView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            if (showDotsPageNum) {
                screenWidth - 150 - height1 - 5
            } else {
                ConstraintLayout.LayoutParams.MATCH_PARENT
            },
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        if (showDotsPageNum) {
            layoutparams1.startToEnd = firstElementId
            setPadding(0,0,lastDotMargin,0)
            layoutparams1.setMargins(5,0,0,0)
        }
        else {
            layoutparams1.startToStart = firstElementId
        }
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        setOnTouchListener { _, _ -> false }
        layoutParams = layoutparams1
    }
    var widthSum = firstDotMargin
    var lastSwitchPageDotWidthSum = firstDotMargin
    var lastNumer = 1
    val dotsContainer = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT)

    }
    for (i in 0 until dotsList.size) {
        val dot = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                height1,
                height1
            )
            if (previousElementId == firstElementId) {
                layoutparams1.startToStart = previousElementId
            }
            else {
                layoutparams1.startToEnd = previousElementId
            }
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(if (previousElementId == firstElementId) {firstDotMargin} else {margin},0,0,0)
            setPadding(5,5,5,5)
            setImageResource(if (previousElementId == firstElementId) {R.drawable.dot_active} else {R.drawable.dot_inactive})
            layoutParams = layoutparams1
            val newId = View.generateViewId()
            id = newId
            previousElementId = newId
        }
        dotsContainer.addView(dot)
        if ((i+1) % dotsOnScreen == 0) {
            lastSwitchPageDotWidthSum = widthSum
            lastNumer += 1
            res.add(Pair(listDot2(dot, lastSwitchPageDotWidthSum, true, lastNumer), dotsList[i]))
        }
        else {
            res.add(Pair(listDot2(dot, lastSwitchPageDotWidthSum, false, lastNumer), dotsList[i]))
        }
        widthSum += height1+margin
    }
    horizontalScrollView.addView(dotsContainer)
    container.addView(horizontalScrollView)
    return createDovodchikDotsReturn(container, res, numRes)
}
fun cardScaleCalcFree(objList: List<objectData2>, marginStartAndEnd: Int, margin: Int, width: Int?, height: Int?, lineWidth: Int): Pair<Int, Int> {
    var res = 0
    var res2 = 0
    if (objList.isNotEmpty() && width != null && height != null) {
        var widthsSum = 0
        for (i in objList) {
            if (i.width != null) {
                widthsSum += i.width!!
            }
        }
        val baseWidth = widthsSum / objList.size
        var baseHeight = 0
        if (objList[0].height != null) {
            baseHeight = objList[0].height as Int
        }

        var amountCards: Float
        var prres: Int

        if (baseWidth >= 840) {
            amountCards = round(((lineWidth.toFloat()-((marginStartAndEnd*3)+marginStartAndEnd-margin))/baseWidth))
            prres = round(((lineWidth.toFloat() - (marginStartAndEnd*3) - (margin * (amountCards-1))) / amountCards)).toInt()
        }
        // For small or normal cards (like activity_main_anime_homepage_carousel_scrolly_1 cards)
        else {
            amountCards = round((lineWidth.toFloat()-(marginStartAndEnd + (marginStartAndEnd - margin)))/baseWidth)
            prres = round(((lineWidth.toFloat() - (marginStartAndEnd*2) - (margin * (amountCards-1))) / amountCards)).toInt()
        }
        res2 = round((prres * (baseHeight.toFloat() / baseWidth))).toInt()
        res = round(res2 * (width.toFloat()/height)).toInt()
    }
    return Pair(res, res2)
}
fun cardScaleCalcForGrid(width: Int?, height: Int?, lineWidth: Int, margin: Int, maxObjectsInLine: Int?): Triple<Int, Int, Float> {
    var res = 0
    var res2 = 0
    var amountCards = 0f
    if (width != null && height != null) {
        if (maxObjectsInLine == null) {
            amountCards = round((lineWidth-(margin*2)).toFloat()/width)
        }
        else {
            amountCards = maxObjectsInLine.toFloat()
        }
        res = round(((lineWidth - (margin * (amountCards-1)))/amountCards)).toInt()
        res2 = round((res.toFloat() * (height.toFloat() / width.toFloat()))).toInt()
    }
    return Triple(res, res2, amountCards)
}
fun calcRecyclerViewHeight(items: List<objectData2>, position: Int): Int {
    val parent = items[position]
    val items = parent.childs
    var res = -1
    for (i in 0 until items.size) {
        var height: Int
        if (items[i].layoutType == null || items[i].layoutType == 1 || items[i].childs.isEmpty()) {
            val size = cardScaleCalcFree(items, 50, 30, items[i].width, items[i].height, screenWidth)
            height = size.second
        }
        else {
            var paddingHorizontal = items[i].paddingHorizontal
            if (paddingHorizontal == null) {
                paddingHorizontal = 50
            }
            var size = Triple(0,0,0f)
            val objList = items[i].childs
            val lineWidth = (screenWidth-paddingHorizontal*2)
            val maxObjectsInOneLine = items[i].maxObjectsInOneLine
            var margin = 10
            if (items[i].marginBetweenElementsHorizontal != null) {
                margin = items[i].marginBetweenElementsHorizontal!!
            }
            var marginV = 10
            if (items[i].marginBetweenElementsVertical != null) {
                marginV = items[i].marginBetweenElementsVertical!!
            }
            for (i in objList) {
                if (i.width != null && i.height != null) {
                    size = cardScaleCalcForGrid(i.width, i.height, lineWidth, margin, maxObjectsInOneLine)
                    break
                }
            }
            var maxObjectsInOneLine2 = maxObjectsInOneLine
            if (maxObjectsInOneLine2 == null) {
                maxObjectsInOneLine2 = size.third.toInt()
            }
            var maxObjects: Int
            if (items[i].maxLines != null) {
                maxObjects = maxObjectsInOneLine2 * items[i].maxLines!!
                if (objList.size <= maxObjects) {
                    maxObjects = objList.size
                }
            }
            else {
                maxObjects = objList.size
            }
            val lines = ceil(maxObjects.toFloat() / maxObjectsInOneLine2.toFloat()).toInt()
            height = ((size.second + marginV) * lines) - marginV
        }
        res = maxOf(res, height)
    }
    return res
}
fun calcItemPosInPxByPos(items: List<objectData2>, position: Int, itemPosition: Int, context: Context): Int {
    val parent = items[position]
    val items = parent.childs
    var paddingHorizontal = parent.paddingHorizontal
    if (paddingHorizontal == null) {
        paddingHorizontal = 50
    }
    var marginBetweenElementsHorizontal = parent.marginBetweenElementsHorizontal
    if (marginBetweenElementsHorizontal == null) {
        marginBetweenElementsHorizontal = 30
    }
    var res = paddingHorizontal
    val r = if (items.size < itemPosition) {items.size} else {itemPosition}
    for (i in 0 until r) {
        val obj = items[i]
        val width = obj.width
        val height = obj.height
        val author = obj.author
        val showName = obj.showName
        val namePosition = obj.namePosition
        val name = obj.name
        val font = ResourcesCompat.getFont(context, R.font.google_sans_medium)
        val textSizee = 40f // в px
        if (obj.layoutType == null || obj.layoutType == 1 || obj.childs.isEmpty()) {
            val size = cardScaleCalcFree(items, 50, 30, width, height, screenWidth)
            var cardHeight: Int
            var cardWidth = 0
            if (author != null && name != null && showName && namePosition == 0) {
                cardHeight = size.second - optimizeText(name, size.first, textSizee, false, font).totalHeight*3
                cardWidth = (cardHeight.toFloat() * (size.first.toFloat() / size.second)).toInt()
            }
            else if (author == null && name != null && showName && namePosition == 0) {
                cardHeight = size.second - optimizeText(name, size.first, textSizee, false, font).totalHeight*2
                cardWidth = (cardHeight.toFloat() * (size.first.toFloat() / size.second)).toInt()
            }
            else if ((!showName) || (namePosition == 1)) {
                cardWidth = size.first
                cardHeight = size.second
            }
            res += cardWidth + marginBetweenElementsHorizontal
        }
        else {
            val width = screenWidth
            res += width - if (i>0) {if (items[i-1].layoutType==1) {marginBetweenElementsHorizontal} else {0}} else {paddingHorizontal}
        }
    }

    return res
}
fun listOptimizate(list: List<objectData2>): List<objectData2> {
    val res = mutableListOf<objectData2>()
    var k = 0
    while (res.size != list.size) {
        for (i in 0 until list.size) {
            val obj = list[i]
            if (obj.position == k) {
                if (obj.childs.isNotEmpty()) {
                    obj.childs = listOptimizate(obj.childs)
                }
                res.add(obj)
                k+=1
            }
        }
    }
    return res
}
fun optimizateSizesOfEveryListObj(list: List<objectData2>): List<objectData2> {
    if (list.isEmpty()) {
        return listOf<objectData2>()
    }
    val res = list as MutableList<objectData2>
    for (i in 0 until res.size) {
        val obj = res[i]
        obj.width = if (obj.width == null) {null} else {round(obj.width!!.toFloat()*density).toInt()}
        obj.height = if (obj.height == null) {null} else {round(obj.height!!.toFloat()*density).toInt()}
        obj.paddingVertical = if (obj.paddingVertical == null) {null} else {round(obj.paddingVertical!!.toFloat()*density).toInt()}
        obj.paddingHorizontal = if (obj.paddingHorizontal == null) {null} else {round(obj.paddingHorizontal!!.toFloat()*density).toInt()}
        obj.marginBetweenElementsHorizontal = if (obj.marginBetweenElementsHorizontal == null) {null} else {round(obj.marginBetweenElementsHorizontal!!.toFloat()*density).toInt()}
        obj.marginBetweenElementsVertical = if (obj.marginBetweenElementsVertical == null) {null} else {round(obj.marginBetweenElementsVertical!!.toFloat()*density).toInt()}
        obj.childs = optimizateSizesOfEveryListObj(obj.childs)
    }
    return res
}
fun calculateAmountOfDots(items: List<objectData2>, paddingHorizontal: Int, marginBetweenElementsHorizontal: Int, context: Context, showName: Boolean, namePosition: Int?): List<listDot> {
    // Инициализируем список точек, первая точка всегда находится в позиции 0 (начало)
    val res = mutableListOf<listDot>(listDot(0))
    var sumWidth = paddingHorizontal
    var lastDotPosInPx = 0
    var lastElementPositionInPx = paddingHorizontal
    val font = ResourcesCompat.getFont(context, R.font.google_sans_medium)
    val textSizee = 40f // в px
    var lastType = 0
    var lastPaddingHorizontal = 0

    // ЭТАП 1: Расчет полной ширины всего контента (sumWidth)
    // Это необходимо для определения границ прокрутки и финальной точки.
    for (i in 0 until  items.size) {
        var width: Int
        width = calculateCardWidth(items, font, textSizee, showName, namePosition, i)
        // Если элемент — сетка (layoutType == 0), рассчитываем ширину всей группы
        if (items[i].layoutType == 0) {
            if (items[i].childs.isNotEmpty()) {
                var paddingHorizontal = items[i].paddingHorizontal
                if (paddingHorizontal == null) {
                    paddingHorizontal = 50
                }
                lastPaddingHorizontal = paddingHorizontal
                width = if (i > 0) { if (items[i-1].layoutType == 0) {screenWidth} else {screenWidth - (marginBetweenElementsHorizontal)}}  else {screenWidth-marginBetweenElementsHorizontal-paddingHorizontal}
            }
            lastType = 0
        }
        else {
            lastType = 1
        }
        // Накапливаем общую ширину с учетом горизонтальных отступов
        sumWidth += width + marginBetweenElementsHorizontal
    }

    // Корректируем sumWidth, убирая лишние отступы в конце
    sumWidth -= marginBetweenElementsHorizontal
    if (lastType == 0) {
        sumWidth -= lastPaddingHorizontal
    }

    // ЭТАП 2: Определение позиций точек
    var sumWidth2 = paddingHorizontal
    var lastElementPosition = 0
    while (true) {
        // Создаём виртуальное "окно", каждый новый срез начинается в позиции последней точки
        var srez = lastDotPosInPx..screenWidth+lastDotPosInPx
        if (srez.last > sumWidth) {
            srez = lastDotPosInPx..sumWidth
        }
        // Проходим по элементам, начиная с последней точки
        for (i in lastElementPosition until items.size) {
            var width: Int
            width = calculateCardWidth(items, font, textSizee, showName, namePosition, i)
            var size: Triple<Int, Int, Float>
            if (items[i].layoutType == 0) {
                if (items[i].childs.isNotEmpty()) {
                    var paddingHorizontal = items[i].paddingHorizontal
                    if (paddingHorizontal == null) {
                        paddingHorizontal = 50
                    }
                    width = if (i > 0) { if (items[i-1].layoutType == 0) {screenWidth} else {screenWidth - (marginBetweenElementsHorizontal)}}  else {screenWidth-marginBetweenElementsHorizontal-paddingHorizontal}
                }
            }

            // Рассчитываем конец текущего элемента
            var kon = sumWidth2 + width
            if (sumWidth2 != lastElementPositionInPx) {
                kon = lastElementPositionInPx + width
            }

            // Если элемент не влезает в текущее "окно" экрана
            if (kon > srez.last) {
                var k = false
                // Проверяем, нет ли уже точки в этой позиции
                for (o in 0 until res.size) {
                    if (res[o].itemPositionInPx == sumWidth2 - marginBetweenElementsHorizontal) {
                        k = true
                    }
                }

                // Если начало элемента попадает в текущий срез и точки еще нет
                if (sumWidth2 - marginBetweenElementsHorizontal in srez && !k) {
                    val itemPositionInPx = sumWidth2 - marginBetweenElementsHorizontal
                    // Проверяем, чтобы при прокрутке к этой точке мы не увидели "пустоту" за пределами контента
                    if ((sumWidth + paddingHorizontal) - itemPositionInPx >= screenWidth) {
                        res.add(listDot(itemPositionInPx))
                    }
                    else {
                        // Если контент заканчивается, ставим точку так, чтобы экран упирался в правый край
                        res.add(listDot(sumWidth + paddingHorizontal - screenWidth))
                    }
                    lastDotPosInPx = itemPositionInPx
                    lastElementPosition = i
                    lastElementPositionInPx = sumWidth2
                    break // Нашли новую точку — начинаем новый цикл
                }
                else {
                    // Иначе ставим точку по самому краю текущего среза
                    val itemPositionInPx = srez.last
                    if ((sumWidth + paddingHorizontal) - itemPositionInPx >= screenWidth) {
                        res.add(listDot(itemPositionInPx))
                    }
                    else {
                        res.add(listDot(sumWidth + paddingHorizontal - screenWidth))
                    }
                    lastDotPosInPx = itemPositionInPx
                    lastElementPosition = i
                    sumWidth2 = itemPositionInPx
                    break
                }
            }

            // Двигаем "курсор" текущей позиции ширины
            if (sumWidth2 != lastElementPositionInPx) {
                sumWidth2 = lastElementPositionInPx + width + marginBetweenElementsHorizontal
            }
            else {
                sumWidth2 += width + marginBetweenElementsHorizontal
            }
            lastElementPositionInPx = sumWidth2
        }

        // Если дошли до фактического конца контента — выходим
        if (srez.last == sumWidth) {
            break
        }
    }
    return res
}
fun createDotDrawables(): MutableList<GradientDrawable> {
    val activeColor = "#FFFFFF".toColorInt()  // Цвет активной точки
    val inactiveColor = "#BF424242".toColorInt()  // Цвет неактивной точки (50% прозрачности)
    val steps = 100  // Количество промежуточных состояний

    val drawables = mutableListOf<GradientDrawable>()

    for (step in 0..steps) {
        val progress = step.toFloat() / steps.toFloat()
        val interpolatedColor = calculateColorAsGradientStep(inactiveColor, activeColor, progress)

        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(interpolatedColor)
        drawable.setSize(35, 35)

        drawables.add(drawable)
    }

    return drawables
}
fun calculateColorAsGradientStep(startColor: Int, endColor: Int, progress: Float): Int {
    val startAlpha = Color.alpha(startColor)
    val startRed = Color.red(startColor)
    val startGreen = Color.green(startColor)
    val startBlue = Color.blue(startColor)

    val endAlpha = Color.alpha(endColor)
    val endRed = Color.red(endColor)
    val endGreen = Color.green(endColor)
    val endBlue = Color.blue(endColor)

    val newAlpha = (startAlpha + (endAlpha - startAlpha) * progress).toInt()
    val newRed = (startRed + (endRed - startRed) * progress).toInt()
    val newGreen = (startGreen + (endGreen - startGreen) * progress).toInt()
    val newBlue = (startBlue + (endBlue - startBlue) * progress).toInt()

    return Color.argb(newAlpha, newRed, newGreen, newBlue)
}
fun convertToStringTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return when {
        hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, secs)
        else -> String.format("%02d:%02d", minutes, secs)
    }
}
fun findObjectByIdInList(id: Long, list: List<objectData2>): objectData2? {
    for (i in list) {
        if (i.id == id) {
            return i
        }
        if (i.childs.isNotEmpty()) {
            val res = findObjectByIdInList(id, i.childs)
            if (res != null) {
                return res
            }
        }
    }
    return null
}
fun findMainPageLayerByPageId(pageId: Int): Layer? {
    return layersList.filterIsInstance<Layer.MainPage>().find { it.pageId == pageId }
}
fun findLayerByLayerObjectId(objId: Long): Pair<Layer?, Int> {
    val res: Pair<Layer?, Int> = Pair(null, -1)
    for (i in 0 until layersList.size) {
        val obj = layersList[layersList.lastIndex-i]
        when (obj) {
            is Layer.AnimePage -> {
                if (obj.layoutObjId == objId) {
                    return Pair(obj, layersList.size-1-i)
                }
            }
            else -> {}
        }
    }
    return res
}
fun findLayerByElevation(elevation: Int): Layer? {
    val res: Layer? = null
    for (i in layersList) {
        when (i) {
            is Layer.AnimePage -> {
                if (i.elevation == elevation) {
                    return i
                }
            }
            is Layer.MainPage -> {
                if (i.elevation == elevation) {
                    return i
                }
            }
            is Layer.OverLay -> {
                if (i.elevation == elevation) {
                    return i
                }
            }
            is Layer.VideoPlayer -> {
                if (i.elevation == elevation) {
                    return i
                }
            }
        }
    }
    return res
}
fun getAdaptiveRadius(widthPx: Int, type: SizeType): Float {
    // 1. Переводим ширину в DP, чтобы формула работала одинаково на разных экранах
    val widthDp = widthPx / baseDensity

    // 2. Базовый коэффициент для типа скругления
    val multiplier = when (type) {
        SizeType.SMALL -> 1.5f
        SizeType.MEDIUM -> 2.4f
        SizeType.LARGE -> 3.6f
        SizeType.XLARGE -> 5.2f
    }

    // 3. Вычисляем корень третьей степени (степень 0.33)
    // Это дает плавный рост: ширина выросла в 8 раз -> радиус вырос только в 2 раза
    val adaptiveFactor = widthDp.toDouble().pow(0.33).toFloat()

    // 4. Итоговый результат в PX
    val resultRadius = multiplier * adaptiveFactor * baseDensity

    // 5. Ограничители (Clamp), чтобы не было "овалов" на экстремально мелких объектах
    val minLimit = when(type) {
        SizeType.SMALL -> 2f * baseDensity
        else -> 4f * baseDensity
    }

    return resultRadius.coerceAtLeast(minLimit)
}
fun createBSDButton(textt: String, icoId: Int, showOpenPageArrow: Boolean, context: Context, width: Int, height: Int): ConstraintLayout {
    val font = context.resources.getFont(R.font.google_sans_medium)
    val icoSize = floor(height.toFloat() / 2f).toInt()
    val icoMarginLeft = round(width.toFloat() / 28.42f).toInt()
    val marginRight = round(icoMarginLeft.toFloat() * 1.42f).toInt()
    val arrowMarginRight = floor(marginRight.toFloat() / icoSizesRatio).toInt()
    val textViewWidth = width - icoMarginLeft*2 - marginRight - icoSize
    val textHeight = round(icoSize.toFloat() / 1f).toInt()
    val textSizee = getTextSizeByHeight(textHeight, font)
    var icoViewId = 0
    val arrowSize = floor(icoSize.toFloat() / 1.46f).toInt()
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            width,
            height
        )
    }
    val ico = ImageView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            icoSize,
            icoSize
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.setMargins(icoMarginLeft,0,0,0)
        layoutParams = layoutparams1
        setImageResource(icoId)
        scaleType = ImageView.ScaleType.CENTER_CROP
        val newId = View.generateViewId()
        id = newId
        icoViewId = newId
    }
    container.addView(ico)
    val textView = TextView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            textViewWidth,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        maxLines = 2
        text = textt
        includeFontPadding = false
        typeface = font
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
        setTextColor("#D9FFFFFF".toColorInt())
        measure(
            View.MeasureSpec.makeMeasureSpec(textViewWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        ellipsize = TextUtils.TruncateAt.END
        layoutparams1.topToTop = icoViewId
        layoutparams1.startToEnd = icoViewId
        val difference = icoSize - measuredHeight
        if (difference < 0) {
            layoutparams1.setMargins(icoMarginLeft,round(difference.toFloat() / 2f).toInt(),0,0)
        }
        else if (difference > 0){
            layoutparams1.setMargins(icoMarginLeft, (round(difference.toFloat() / 2f)).toInt(),0,0)
        }
        else {
            layoutparams1.setMargins(icoMarginLeft,0,0,0)
        }
        layoutParams = layoutparams1
    }
    container.addView(textView)
    if (showOpenPageArrow) {
        val arrow = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                arrowSize,
                arrowSize
            )
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,0,arrowMarginRight,0)
            setImageResource(R.drawable.chevron_forward)
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = layoutparams1
        }
        container.addView(arrow)
    }
    return container
}
fun createM3Button(context: Context, width: Int, height: Int, textt: String, name: String, nameColor: Int, isActive: Boolean, sizeType: SizeType, cornersMode: Int, icoId: Int? = null, pillMode: Boolean = false, dropDownMode: Boolean = false, wrapContentMode: Boolean = false, maxWidthh: Int? = null): createM3ButtonReturn {
    val font = context.resources.getFont(R.font.google_sans_medium)
    val viewList = mutableListOf<View>()
    var widthToReturn = width
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(width, height)
        tag = "button_container"
        id = View.generateViewId()
    }
    val textRatio = when (sizeType) {
        SizeType.XLARGE -> 3.4f
        SizeType.LARGE -> 3.0f
        SizeType.MEDIUM -> 2.6f
        SizeType.SMALL -> 2.2f
    }
    var buttonHeight = height
    var nameId = 0

    if (name != "") {
        val nameHeight = round(height.toFloat() / 3f).toInt()
        buttonHeight = (round(height.toFloat() / 3f) * 2f).toInt()
        val nameView = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                width,
                nameHeight
            )
            maxLines = 2
            this.text = name
            includeFontPadding = false
            typeface = font
            setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSizeByHeight(round(nameHeight.toFloat() / 2f).toInt(), font))
            setTextColor(nameColor)
            gravity = Gravity.CENTER

            measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            ellipsize = TextUtils.TruncateAt.END

            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1

            val newId = View.generateViewId()
            id = newId
            nameId = newId
            tag = "button_name"
        }
//        container.addView(nameView)
        viewList.add(nameView)
    }

    val buttonWidth = round(width.toFloat() / height.toFloat() * buttonHeight.toFloat()).toInt()
    val textHeight = round(buttonHeight / textRatio).toInt()
    val textSizee = getTextSizeByHeight(textHeight, font)
    val icoSize = textSizee.toInt()
    val icoMarginLeft = round((round((round((buttonHeight - icoSize).toFloat() / 2f)) * 1.3f)) / icoSizesRatio).toInt()
    val textViewMarginRight = round(icoMarginLeft.toFloat() * icoSizesRatio).toInt()

    val buttonBg = View(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            buttonWidth,
            buttonHeight
        )
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        if (nameId != 0) {
            layoutparams1.bottomToTop = nameId
        } else {
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        }
        layoutParams = layoutparams1

        val radius = if (!pillMode) {getAdaptiveRadius(width, sizeType)} else {10000f}
        val radii = FloatArray(8)
        when (cornersMode) {
            1 -> { // Только левые
                radii[0] = radius; radii[1] = radius
                radii[6] = radius; radii[7] = radius
            }
            2 -> { /* Без закруглений */ }
            3 -> { // Только правые
                radii[2] = radius; radii[3] = radius
                radii[4] = radius; radii[5] = radius
            }
            else -> {
                for (i in 0..7) radii[i] = radius
            }
        }

        background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = radii
            setColor(if (!isActive) {"#80EADDFF".toColorInt()} else {"#E8DEF8".toColorInt()})
        }

        val newId = View.generateViewId()
        id = newId
        tag = "button_bg"
    }
//    container.addView(buttonBg)
    viewList.add(buttonBg)

    var textVieww: TextView? = null
    if (textt != "") {
        val textView = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.topToTop = buttonBg.id
            layoutparams1.bottomToBottom = buttonBg.id
            layoutparams1.startToStart = buttonBg.id
            layoutparams1.endToEnd = buttonBg.id
            layoutParams = layoutparams1
            maxWidth = buttonWidth - textViewMarginRight - textViewMarginRight
            maxLines = 1
            text = textt
            includeFontPadding = false
            typeface = font
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
            setTextColor("#804A4459".toColorInt())
            tag = "button_text"
            id = View.generateViewId()
            ellipsize = TextUtils.TruncateAt.END
        }
        textVieww = textView
//        container.addView(textView)
        viewList.add(textView)
    }

    var icoViewMarginLeft = 0
    if (icoId != null) {
        val icoViewId = View.generateViewId()
        val icoView = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                icoSize,
                icoSize
            )
            layoutparams1.topToTop = buttonBg.id
            layoutparams1.bottomToBottom = buttonBg.id
            layoutparams1.startToStart = buttonBg.id
            layoutparams1.endToEnd = buttonBg.id
            layoutparams1.setMargins(0,0,0,0)
            setImageResource(icoId)
            scaleType = ImageView.ScaleType.CENTER_CROP
            tag = "button_ico"
            id = icoViewId
            layoutParams = layoutparams1
        }
//        container.addView(icoView)
        viewList.add(icoView)
        if (textVieww != null) {
            textVieww.maxWidth = buttonWidth- icoSize - textViewMarginRight
            textVieww.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val layoutparams1 = icoView.layoutParams as ConstraintLayout.LayoutParams
            icoViewMarginLeft = round((buttonWidth - icoSize - textVieww.measuredWidth).toFloat() / 2f).toInt()
            layoutparams1.setMargins(icoViewMarginLeft,0,0,0)
            icoView.layoutParams = layoutparams1

            val lp1 = textVieww.layoutParams as ConstraintLayout.LayoutParams
            lp1.startToEnd = icoViewId
            lp1.endToEnd = ConstraintLayout.LayoutParams.UNSET
            lp1.startToStart = ConstraintLayout.LayoutParams.UNSET
            textVieww.layoutParams = lp1
        }
    }
    if (dropDownMode) {
        val arrowViewId = View.generateViewId()
        val arrow = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                icoSize,
                icoSize
            )
            layoutparams1.endToEnd = buttonBg.id
            layoutparams1.topToTop = buttonBg.id
            layoutparams1.bottomToBottom = buttonBg.id
            layoutparams1.setMargins(0,0,icoMarginLeft,0)
            setImageResource(R.drawable.chevron_forward)
            rotation = 90f
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = layoutparams1
            id = arrowViewId
            tag = "arrow"
        }
        if (textVieww != null) {
            val lp1 = textVieww.layoutParams as ConstraintLayout.LayoutParams
            if (icoId == null) {
                lp1.startToStart = ConstraintLayout.LayoutParams.UNSET
                lp1.endToEnd = ConstraintLayout.LayoutParams.UNSET
                lp1.endToStart = arrowViewId
            }
            textVieww.maxWidth = buttonWidth - icoMarginLeft - if (icoId != null) {icoSize + icoViewMarginLeft} else {0} - icoSize
            textVieww.layoutParams = lp1
        }
//        container.addView(arrow)
        viewList.add(arrow)
    }
    if (wrapContentMode) {
        var theIco: ImageView? = null
        var theText: TextView? = null
        var theName: TextView? = null
        var theArrow: ImageView? = null
        for (i in viewList) {
            when (i.tag) {
                "button_ico" -> theIco = i as ImageView
                "button_text" -> theText = i as TextView
                "button_name" -> theName = i as TextView
                "arrow" -> theArrow = i as ImageView
            }
        }


        if (theText != null) {
            theText.maxWidth = Int.MAX_VALUE
            theText.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
        }

        val actualTextWidth = theText?.measuredWidth ?: 0
        val actualIcoSize = if (icoId != null) icoSize else 0
        val actualArrowSize = if (dropDownMode) icoSize else 0

        var newWidth = icoMarginLeft + actualIcoSize + actualTextWidth + actualArrowSize + icoMarginLeft + textViewMarginRight
        widthToReturn = newWidth

        if (maxWidthh != null) {
            newWidth = min(newWidth, maxWidthh)
        }
        val textViewMaxWidthh = newWidth - icoMarginLeft - actualIcoSize - actualArrowSize - icoMarginLeft - textViewMarginRight
        if (theText != null) {
            theText.maxWidth = textViewMaxWidthh
            theText.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
        }
        val containerLp = container.layoutParams
        containerLp.width = newWidth
        container.layoutParams = containerLp

        val bgLp = buttonBg.layoutParams as ConstraintLayout.LayoutParams
        bgLp.width = newWidth
        buttonBg.layoutParams = bgLp

        if (theName != null) {
            val nLp = theName.layoutParams as ConstraintLayout.LayoutParams
            nLp.width = newWidth
            theName.layoutParams = nLp
        }

        val newRadius = if (!pillMode) { getAdaptiveRadius(newWidth, sizeType) } else { 10000f }
        val newRadii = FloatArray(8)
        when (cornersMode) {
            1 -> { newRadii[0] = newRadius; newRadii[1] = newRadius; newRadii[6] = newRadius; newRadii[7] = newRadius }
            2 -> { /* Без закруглений */ }
            3 -> { newRadii[2] = newRadius; newRadii[3] = newRadius; newRadii[4] = newRadius; newRadii[5] = newRadius }
            else -> { for (i in 0..7) newRadii[i] = newRadius }
        }
        (buttonBg.background as? GradientDrawable)?.cornerRadii = newRadii

        if (theIco != null) {
            val lp = theIco.layoutParams as ConstraintLayout.LayoutParams
            lp.startToStart = buttonBg.id
            lp.endToEnd = ConstraintLayout.LayoutParams.UNSET
            lp.setMargins(icoMarginLeft, 0, 0, 0)
            theIco.layoutParams = lp
        }

        if (theText != null) {
            val lp = theText.layoutParams as ConstraintLayout.LayoutParams
            lp.startToStart = ConstraintLayout.LayoutParams.UNSET
            lp.endToEnd = ConstraintLayout.LayoutParams.UNSET
            lp.startToEnd = ConstraintLayout.LayoutParams.UNSET
            lp.endToStart = ConstraintLayout.LayoutParams.UNSET

            if (theIco != null) {
                lp.startToEnd = theIco.id
                lp.setMargins(textViewMarginRight, 0, 0, 0)
            } else {
                lp.startToStart = buttonBg.id
                lp.setMargins(icoMarginLeft, 0, 0, 0)
            }
            theText.layoutParams = lp
        }

        if (theArrow != null) {
            val lp = theArrow.layoutParams as ConstraintLayout.LayoutParams
            lp.startToStart = ConstraintLayout.LayoutParams.UNSET
            lp.endToEnd = ConstraintLayout.LayoutParams.UNSET
            lp.startToEnd = ConstraintLayout.LayoutParams.UNSET
            lp.endToStart = ConstraintLayout.LayoutParams.UNSET

            if (theText != null) {
                lp.startToEnd = theText.id
                lp.setMargins(0, 0, 0, 0)
            } else if (theIco != null) {
                lp.startToEnd = theIco.id
                lp.setMargins(0, 0, 0, 0)
            } else {
                lp.startToStart = buttonBg.id
                lp.setMargins(icoMarginLeft, 0, 0, 0)
            }
            theArrow.layoutParams = lp
        }
    }

    return createM3ButtonReturn(container, viewList, widthToReturn)
}
fun createSegmentedButton(context: Context, width: Int, height: Int, options: List<Pair<segmentedButtonOptions, BsdButtonsTags>>): ConstraintLayout {
    val buttonWidth = width / options.size

    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(width, height)
        tag = "segmented_container"
        id = View.generateViewId()
    }

    var previousId = ConstraintLayout.LayoutParams.PARENT_ID

    for (i in 0 until options.size) {
        val cornersMode = when {
            i == 0 -> 1
            i == options.size - 1 -> 3
            else -> 2
        }

        val rt = createM3Button(
            context = context,
            width = buttonWidth,
            height = height,
            textt = options[i].first.text,
            icoId = options[i].first.icoId,
            name = "",
            sizeType = SizeType.MEDIUM,
            cornersMode = cornersMode,
            nameColor = "#FFFFFF".toColorInt(),
            isActive = options[i].first.isActive
        )

        val button = rt.container
        for (i in rt.childs) {
            button.addView(i)
        }
        val cr = getAdaptiveRadius(buttonWidth, SizeType.MEDIUM)
        val radii: FloatArray = if (cornersMode == 1) {
            floatArrayOf(cr,cr,0f,0f,0f,0f,cr,cr)
        }
        else if (cornersMode == 2) {
            floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f)
        }
        else {
            floatArrayOf(0f,0f,cr,cr,cr,cr,0f,0f)
        }
        val poloska = GradientDrawable().apply {
            setStroke(round(2f*density).toInt(), "#000000".toColorInt())
            cornerRadii = radii
        }
        button.background = poloska

        val layoutparams1 = ConstraintLayout.LayoutParams(buttonWidth, height)
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        if (previousId == ConstraintLayout.LayoutParams.PARENT_ID) {
            layoutparams1.startToStart = previousId
        } else {
            layoutparams1.startToEnd = previousId
        }

        button.layoutParams = layoutparams1
        button.tag = "button_$i"
        button.id = View.generateViewId()

        container.addView(button)
        previousId = button.id
    }

    return container
}
fun createSlider(context: Context, widthh: Int, stopsList: List<Float>, heightt: Int, createSteps: Boolean = false): ConstraintLayout {

    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(widthh, heightt)
        tag = "slider_container"
        id = View.generateViewId()
    }
    val minSliderHeight = round(48f*baseDensity).toInt()
    val sliderHeight = max(heightt, minSliderHeight)
    val marginTop = if (heightt < minSliderHeight) {round((heightt - minSliderHeight).toFloat() / 2f).toInt()} else {0}
    val gapSize = round(12f * density).toInt()
    val slider = Slider(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            widthh+trackSidePadding*2,
            sliderHeight
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.setMargins(-trackSidePadding,marginTop,0,0)
        val thumbWidth = round(3f * baseDensity).toInt()
        val thumbDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 1000f
            setColor("#6750A4".toColorInt())
            setSize(thumbWidth, heightt)
        }
        setCustomThumbDrawable(thumbDrawable)
        haloRadius = 0
        thumbTrackGapSize = gapSize
        trackInsideCornerSize = round(4f * density).toInt()
        trackHeight = round(heightt.toFloat() / 2.75f).toInt()
        layoutParams = layoutparams1
        trackActiveTintList = ColorStateList.valueOf("#E8DEF8".toColorInt())
        trackInactiveTintList = ColorStateList.valueOf("#80EADDFF".toColorInt())
        thumbTintList = ColorStateList.valueOf("#E8DEF8".toColorInt())
        tickActiveTintList = ColorStateList.valueOf("#4A4459".toColorInt())
        tickInactiveTintList = ColorStateList.valueOf("#4A4459".toColorInt())

        if (stopsList.isNotEmpty()) {
            valueFrom = stopsList[0]
            valueTo = stopsList[stopsList.lastIndex]
            if (!createSteps) {
                stepSize = 0f
            }
            else {
                stepSize = if(stopsList.size > 2) {(stopsList[stopsList.lastIndex] - stopsList[0]) / (stopsList.size.toFloat() - 1f)} else {stopsList[stopsList.lastIndex] - stopsList[0]}
            }
        }
        id = View.generateViewId()
        tag = "slider_control"
    }
    slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
        @SuppressLint("RestrictedApi")
        override fun onStartTrackingTouch(slider: Slider) {
            // Повторно назначаем gap при касании
            slider.thumbTrackGapSize = gapSize
        }

        override fun onStopTrackingTouch(slider: Slider) {
            // И при отпускании
            slider.thumbTrackGapSize = gapSize
        }
    })
    container.addView(slider)
    return container
}
fun createSliderRow(context: Context, width: Int, name: String, icoId: Int, stopsList: List<Float>, heightt: Int, createSteps: Boolean = false): List<View> {
    val font = context.resources.getFont(R.font.google_sans_medium)

    val icoMarginLeft = round(width.toFloat() / 28.42f).toInt()
    val marginRight = round(icoMarginLeft.toFloat() * 1.42f).toInt()
    val icoSize = floor(heightt.toFloat() / 2f).toInt()
    val titleWidth = width - icoMarginLeft - icoMarginLeft - marginRight - icoSize
    val titleId = View.generateViewId()
    val textHeight = round(icoSize.toFloat() / 1f).toInt()
    val textSizee = getTextSizeByHeight(textHeight, font)
    val viewsToReturn = mutableListOf<View>()

    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            width,
            heightt
        )
        tag = "slider_row_container"
        id = View.generateViewId()
    }
    viewsToReturn.add(container)

    val icoViewId = View.generateViewId()
    val ico = ImageView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(icoSize, icoSize)
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.setMargins(icoMarginLeft, 0, 0, 0)
        layoutParams = layoutparams1

        setImageResource(icoId)
        scaleType = ImageView.ScaleType.CENTER_CROP
        id = icoViewId
        tag = "slider_icon"
    }
    container.addView(ico)
    viewsToReturn.add(ico)

    val textView = TextView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            titleWidth,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutparams1.startToEnd = icoViewId
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        maxLines = 1
        text = name
        includeFontPadding = false
        typeface = font
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
        setTextColor("#D9FFFFFF".toColorInt())
        ellipsize = TextUtils.TruncateAt.END
        measure(
            View.MeasureSpec.makeMeasureSpec(titleWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val diff2 = heightt - measuredHeight - textHeight
        layoutparams1.setMargins(icoMarginLeft, round(diff2.toFloat() / 2f).toInt(),0,0)
        id = titleId
        tag = "slider_title"
        layoutParams = layoutparams1
    }
    container.addView(textView)
    viewsToReturn.add(textView)

    val slider = createSlider(context, titleWidth, stopsList, textHeight, createSteps)
    val layoutparams1 = slider.layoutParams as ConstraintLayout.LayoutParams
    layoutparams1.startToStart = titleId
    layoutparams1.height = textHeight
    if (name != "") {
        layoutparams1.topToBottom = titleId
    }
    else {
        layoutparams1.topToTop = icoViewId
        layoutparams1.bottomToBottom = icoViewId
    }
    slider.layoutParams = layoutparams1
    container.addView(slider)
    viewsToReturn.add(slider)

    return viewsToReturn
}
fun createDropdownRow(context: Context, width: Int, height: Int, titleText: String, icoId: Int, options: List<Pair<String, BsdButtonsTags>>, onItemSelected: (String, BsdButtonsTags) -> Unit, buttonIcoId: Int? = null): ConstraintLayout {
    val font = context.resources.getFont(R.font.google_sans_medium)
    val icoMarginLeft = round(width.toFloat() / 28.42f).toInt()
    val marginRight = round(icoMarginLeft.toFloat() * 1.42f).toInt()
    val icoSize = floor(height.toFloat() / 2f).toInt()
    val textViewWidth = round((width - icoMarginLeft - icoMarginLeft - marginRight - icoSize).toFloat() / 2.5f).toInt()
    val buttonHeight = round(height.toFloat() / 1.5f).toInt()
    val buttonWidth = width - icoMarginLeft - icoMarginLeft - icoSize - textViewWidth
    val textHeight = round(icoSize.toFloat() / 1f).toInt()
    val textSizee = getTextSizeByHeight(textHeight, font)
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(width, height)
        tag = "dropdown_container"
        id = View.generateViewId()
    }

    val icoViewId = View.generateViewId()
    val ico = ImageView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(icoSize, icoSize)
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.setMargins(icoMarginLeft, 0, 0, 0)
        layoutParams = layoutparams1

        setImageResource(icoId)
        scaleType = ImageView.ScaleType.CENTER_CROP
        id = icoViewId
        tag = "dropdown_icon"
    }
    container.addView(ico)

    val rt = createM3Button(context = context, width = buttonWidth, height = buttonHeight, textt = options[0].first, name = "", sizeType = SizeType.SMALL, cornersMode = 0, icoId = buttonIcoId, pillMode = true, dropDownMode = true, wrapContentMode = true, maxWidthh = buttonWidth, isActive = true, nameColor = "#FFFFFF".toColorInt())
    val dropdownButton = rt.container
    for (i in rt.childs) {
        dropdownButton.addView(i)
    }
    val layoutparams1 = dropdownButton.layoutParams as ConstraintLayout.LayoutParams
    layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
    layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
    layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
    layoutparams1.setMargins(0, 0, marginRight, 0)
    dropdownButton.layoutParams = layoutparams1

    val newId = View.generateViewId()
    dropdownButton.id = newId
    dropdownButton.tag = "dropdown_button"
    container.addView(dropdownButton)

    val textView = TextView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            textViewWidth,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutparams1.startToEnd = icoViewId
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.setMargins(icoMarginLeft, 0, 0, 0)
        maxLines = 2
        text = titleText
        includeFontPadding = false
        typeface = font
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
        setTextColor("#E6E0E9".toColorInt())
        ellipsize = TextUtils.TruncateAt.END
        measure(
            View.MeasureSpec.makeMeasureSpec(textViewWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val difference = icoSize - measuredHeight
        if (difference < 0) {
            layoutparams1.setMargins(icoMarginLeft,round(difference.toFloat() / 2f).toInt(),0,0)
        }
        else if (difference > 0){
            layoutparams1.setMargins(icoMarginLeft, (round(difference.toFloat() / 2f)).toInt(),0,0)
        }
        tag = "dropdown_title"
        layoutParams = layoutparams1
    }
    container.addView(textView)

    val listPopupWindow = ListPopupWindow(context).apply {
        val newOptions = mutableListOf<String>()
        for (i in options) {
            newOptions.add(i.first)
        }
        setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, newOptions))
        anchorView = dropdownButton // Привязываем список к созданной M3 кнопке
        isModal = true

        setOnItemClickListener { _, _, position, _ ->
            val selected = options[position]
            // Находим TextView внутри кнопки и меняем текст
            val btnText = dropdownButton.findViewWithTag<TextView>("button_text")
            btnText?.text = selected.first
            val newChilds = createM3Button(context = context, width = buttonWidth, height = buttonHeight, textt = selected.first, name = "", sizeType = SizeType.SMALL, cornersMode = 0, icoId = buttonIcoId, pillMode = true, dropDownMode = true, wrapContentMode = true, maxWidthh = buttonWidth, isActive = true, nameColor = "#FFFFFF".toColorInt())
            dropdownButton.removeAllViews()
            val lp1 = dropdownButton.layoutParams as ConstraintLayout.LayoutParams
            lp1.width = newChilds.width
            dropdownButton.layoutParams = lp1
            for (i in newChilds.childs) {
                dropdownButton.addView(i)
            }
            onItemSelected(selected.first,selected.second) // Вызываем callback
            dismiss()
        }
    }

    // Обрабатываем клик по кнопке (можно кликнуть и по фону, и по тексту)
    dropdownButton.setOnClickListener { listPopupWindow.show() }
    dropdownButton.findViewWithTag<View>("button_bg")?.setOnClickListener { listPopupWindow.show() }

    return container
}
fun createSegmentedButtonRow(context: Context, width: Int, height: Int, options: List<Pair<segmentedButtonOptions, BsdButtonsTags>>, icoId: Int, textt: String): ConstraintLayout {
    val font = context.resources.getFont(R.font.google_sans_medium)
    val icoSize = floor(height.toFloat() / 2f).toInt()
    val icoMarginLeft = round(width.toFloat() / 28.42f).toInt()
    val marginRight = round(icoMarginLeft.toFloat() * icoSizesRatio).toInt()
    val textViewWidthMaxWidth = round((width - icoMarginLeft*2 - marginRight - icoSize).toFloat() / 2.5f).toInt()
    val textHeight = round(icoSize.toFloat() / 1f).toInt()
    val textSizee = getTextSizeByHeight(textHeight, font)
    val marginBetweenTextAndButton = round(icoMarginLeft.toFloat() / 2f).toInt()
    var icoViewId = 0
    var textViewId = 0
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            width,
            height
        )
    }

    val ico = ImageView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            icoSize,
            icoSize
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.setMargins(icoMarginLeft,0,0,0)
        layoutParams = layoutparams1
        setImageResource(icoId)
        scaleType = ImageView.ScaleType.CENTER_CROP
        val newId = View.generateViewId()
        id = newId
        icoViewId = newId
    }
    container.addView(ico)
    val textView = TextView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        maxLines = 2
        maxWidth = textViewWidthMaxWidth
        text = textt
        includeFontPadding = false
        typeface = font
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
        setTextColor("#D9FFFFFF".toColorInt())
        measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        ellipsize = TextUtils.TruncateAt.END
        layoutparams1.topToTop = icoViewId
        layoutparams1.startToEnd = icoViewId
        val difference = icoSize - measuredHeight
        if (difference >= 0) {
            val marginTop = -round(measuredHeight.toFloat() / 2f).toInt()
            layoutparams1.setMargins(icoMarginLeft, -marginTop,0,0)
        }
        else {
            val marginTop = round(difference.toFloat() / 2f).toInt()
            layoutparams1.setMargins(icoMarginLeft, marginTop,0,0)
        }
        layoutParams = layoutparams1
        val newId = View.generateViewId()
        id = newId
        textViewId = newId
    }
    container.addView(textView)

    val buttonWidth = width - icoMarginLeft - icoSize - icoMarginLeft - textView.measuredWidth - marginRight
    val buttonHeight = round(height.toFloat() * 0.8f).toInt()
    val segmentedButton = createSegmentedButton(context, buttonWidth, buttonHeight, options)
    val lp1 = segmentedButton.layoutParams as ConstraintLayout.LayoutParams
    if (textt == "") {
        lp1.startToEnd = icoViewId
        lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        lp1.setMargins(icoMarginLeft,0,0,0)
    }
    else {
        lp1.startToEnd = textViewId
        lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        lp1.setMargins(icoMarginLeft,0,0,0)
    }
    segmentedButton.layoutParams = lp1
    container.addView(segmentedButton)
    return container
}
fun createSwitchButton(context: Context, isChecked: Boolean, width: Int, height: Int, onCheckedChange: ((Boolean) -> Unit)? = null): ConstraintLayout {

    // 1. Прописываем все размеры и цвета
    val padding = round(height * 0.15f).toInt() // Отступ кружка от краев
    val thumbSize = height - padding * 2
    val colorOn = "#EADDFF".toColorInt()
    val colorOff = "#80EADDFF".toColorInt()
    var currentState = isChecked

    // 2. Создаем container (ConstraintLayout)
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(width, height)
    }

    // 3. Создаем остальные view (фон и ползунок)
    val track = View(context).apply {
        id = View.generateViewId()
        // Размеры 0, 0, так как растянем по constraints
        layoutParams = ConstraintLayout.LayoutParams(0, 0)
        background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = height / 2f
            setColor(if (currentState) colorOn else colorOff)
        }
    }

    val thumb = View(context).apply {
        id = View.generateViewId()
        layoutParams = ConstraintLayout.LayoutParams(thumbSize, thumbSize)
        background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.WHITE)
        }
        elevation = 4f // Небольшая тень, чтобы кружок выделялся как на референсах
    }

    // 4. Добавляем view в контейнер и настраиваем привязки
    container.addView(track)
    container.addView(thumb)

    // Фон растягиваем на весь контейнер
    track.updateLayoutParams<ConstraintLayout.LayoutParams> {
        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
    }

    // Кружок привязываем ко всем краям, но двигаем через horizontalBias
    thumb.updateLayoutParams<ConstraintLayout.LayoutParams> {
        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        setMargins(padding, 0, padding, 0)
        horizontalBias = if (currentState) 1f else 0f
    }

    // 5. Добавляем ClickListener и анимацию
    container.setOnClickListener {
        val startBias = if (currentState) 1f else 0f
        val endBias = if (currentState) 0f else 1f
        val startColor = if (currentState) colorOn else colorOff
        val endColor = if (currentState) colorOff else colorOn

        // Меняем состояние
        currentState = !currentState
        onCheckedChange?.invoke(currentState)

        // Анимация передвижения ползунка
        val biasAnimator = ValueAnimator.ofFloat(startBias, endBias).apply {
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                thumb.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    horizontalBias = animator.animatedValue as Float
                }
            }
        }

        // Анимация смены цвета фона
        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor).apply {
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                (track.background as GradientDrawable).setColor(animator.animatedValue as Int)
            }
        }

        // Запускаем обе анимации одновременно
        biasAnimator.start()
        colorAnimator.start()
    }

    // 6. Возвращаем контейнер
    return container
}
fun getTextSizeByHeight(height: Int, font: Typeface? = null): Float {
    var textSizee = steps[7]
    for (i in steps) {
        val res = optimizeText("СъешьжеещёHj", 1000, i, false, font, 1)
        if (res.totalHeight <= height) {
            textSizee = i
            break
        }
    }
    return textSizee
}
object createOvDialog {
    fun createCarouselPage(context: Context, startsInfo: OverLayLayer.CreateCarouselPage) {
        val font = context.resources.getFont(R.font.google_sans_regular)
        val boldFont = context.resources.getFont(R.font.google_sans_bold)
        val isLandscape = screenWidth > screenHeight
        val maxPageWidth = round(1000f * baseDensity).toInt()
        val actualWidth = min(screenWidth, maxPageWidth)
        val marginLeft = round(16f * baseDensity).toInt()
        val marginTop = round(12f * baseDensity).toInt()
        var hTextSizee = round(24f*baseDensity)
        val hTextText = "Создание карусели"
        val hTextMaxWidth = actualWidth - marginLeft*2
        for (i in steps) {
            val opT = optimizeText(hTextText, hTextMaxWidth, i, false, boldFont, 1)
            if (opT.firstLine[opT.firstLine.lastIndex].toString() != "." && i <= hTextSizee) {
                hTextSizee = i
                break
            }
        }
        val container = ConstraintLayout(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor("#181619".toColorInt())
        }

        val scrollContainer = ScrollView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
        }
        container.addView(scrollContainer)
        val hText = TextView(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.setMargins(0,marginTop+statusBarHeight,0,0)
            setTextColor("#FFFFFF".toColorInt())
            setTextSize(TypedValue.COMPLEX_UNIT_PX, hTextSizee)
            typeface = boldFont
            text = hTextText
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            id = View.generateViewId()
            layoutParams = lp1
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
        }
        container.addView(hText)
        val containerInsideScrollContainer = ConstraintLayout(context).apply {
            val layoutparams1 = LinearLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams = layoutparams1
        }
        scrollContainer.addView(containerInsideScrollContainer)
        val previewContainerWidth = actualWidth - marginLeft*2
        val previewContainerHeight = round(previewContainerWidth / 1.258f).toInt()
        val previewContainerBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(previewContainerWidth, SizeType.MEDIUM)
            setColor("#08040D".toColorInt())
        }
        val previewContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                previewContainerWidth,
                previewContainerHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            background = previewContainerBackground
        }

        fun createCarouselPreview(startsInfo1: OverLayLayer.CreateCarouselPage) {

        }
        containerInsideScrollContainer.addView(previewContainer)

    }
    @SuppressLint("ClickableViewAccessibility")
    fun CreateAnimePage(context: Context, startsInfo: OverLayLayer.CreateAnimePage, resultSenderViewModel: ResultSenderViewModel, addCard: (List<episodeInfo>) -> Unit, changeImage: (Int) -> Unit, openGenreChoice: (String) -> Unit) : ConstraintLayout {
        val font = context.resources.getFont(R.font.google_sans_regular)
        val boldFont = context.resources.getFont(R.font.google_sans_bold)
        val isLandscape = screenWidth > screenHeight
        val maxPageWidth = round(1000f * baseDensity).toInt()
        val actualWidth = min(screenWidth, maxPageWidth)
        val bannerW = if (isLandscape) (screenHeight * 0.5f).toInt() else (actualWidth / 2.5f).toInt()
        val marginLeft = round(16f * baseDensity).toInt()
        val marginTop = round(12f * baseDensity).toInt()
        val bannerH = if (startsInfo.type == ElementType.Music) bannerW else (bannerW * 1.415f).toInt()
        val hBtn = round(32f * baseDensity).toInt()
        val marginBetweenInfoElements = round(6f * baseDensity).toInt()
        var hTextSize = round(24f*baseDensity)
        val hTextText = when (startsInfo.type) {
            ElementType.Anime -> "Создание аниме карточки"
            ElementType.Manga -> "Создание карточки манги"
            ElementType.Music -> "Создание карточки музыки"
            else -> {""}
        }
        val hTextMaxWidth = actualWidth - hBtn*2 - marginTop*4
        for (i in steps) {
            val opT = optimizeText(hTextText, hTextMaxWidth, i, false, boldFont, 1)
            if (opT.firstLine[opT.firstLine.lastIndex].toString() != "." && i <= hTextSize) {
                hTextSize = i
                break
            }
        }
        val inputLayoutsList = mutableListOf<View>()
        val buttonsList = mutableListOf<View>()
        val containerWidth = actualWidth
        val containerHeight = screenHeight - statusBarHeight
        val containerBackgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor("#08040D".toColorInt())
        }
        val bannerMarginTop = marginTop * 2
        val bannerAddIcoSize = round(bannerW.toFloat() / 3f).toInt()
        val nameInputWidth = min(round(320f * baseDensity).toInt(), (containerWidth - marginLeft - bannerW - marginLeft - marginTop))
        val nameInputHeight = hBtn
        val scrollContainerWidth = nameInputWidth + marginLeft + bannerW + marginLeft + marginTop

        val containerr = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                screenWidth,
                containerHeight + statusBarHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            elevation = 100f
            background = containerBackgroundDrawable
        }
        if (screenWidth > maxPageWidth) {
            val paddingHorizontal = round((screenWidth-maxPageWidth).toFloat() / 2f).toInt()

            containerr.setPadding(paddingHorizontal,0,paddingHorizontal,0)
        }
        val blobSize = containerWidth * 2
        val blobDrawable = blobInit(blobSize, "#C2A6FF", floatArrayOf(0f,1f), 0.6f)
        val blobMargin = round(containerWidth.toFloat() / 2f).toInt()
        val blob = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                blobSize,
                blobSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(-blobMargin,-blobMargin,0,0)
            layoutParams = layoutparams1
            background = blobDrawable
        }
        containerr.addView(blob)
        val hTextId = View.generateViewId()
        val hText = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0, marginTop+statusBarHeight, 0, 0)
            layoutParams = layoutparams1
            maxLines = 1
            setTextSize(TypedValue.COMPLEX_UNIT_PX, hTextSize)
            typeface = boldFont
            ellipsize = TextUtils.TruncateAt.END
            text = hTextText
            setTextColor("#FFFFFF".toColorInt())
            id = hTextId
            includeFontPadding = false
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
        }
        containerr.addView(hText)
        val extraButtonBgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            cornerRadius = 10000f
            setColor("#26AFAFAF".toColorInt())
        }
        val extraButton = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                hBtn,
                hBtn
            )
            layoutparams1.topToTop = hTextId
            layoutparams1.bottomToBottom = hTextId
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,0,marginTop,0)
            layoutParams = layoutparams1
            background = extraButtonBgDrawable
        }
        val extraButtonIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                round(hBtn.toFloat() / 1.2f).toInt(),
                round(hBtn.toFloat() / 1.2f).toInt()
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setImageResource(R.drawable.more_vert_add_block_ico)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        extraButton.addView(extraButtonIco)
        containerr.addView(extraButton)
        val scrollContainer = ScrollView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                containerWidth,
                containerHeight - marginTop - hText.measuredHeight - bannerMarginTop
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToBottom = hTextId
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,bannerMarginTop,0,0)
            layoutParams = layoutparams1
            val paddingHorizontal = ((containerWidth - scrollContainerWidth).toFloat() / 2f).toInt()
            setPadding(paddingHorizontal,0,paddingHorizontal,(marginTop*2+hBtn))
            tag = "scroll_container"
        }
        val container = ConstraintLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                scrollContainerWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            tag = "container"
        }
        scrollContainer.addView(container)
        containerr.addView(scrollContainer)

        val bannerId = View.generateViewId()
        val bannerBgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(bannerW, SizeType.MEDIUM)
            setColor("#BF1B1B1B".toColorInt())
            setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
        }
        val banner = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                bannerW,
                bannerH
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToBottom = hTextId
            layoutparams1.setMargins(marginLeft, 0,0,0)
            layoutParams = layoutparams1
            background = bannerBgDrawable
            id = bannerId
            tag = "banner_container"
        }
        val bannerCardView = CardView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                bannerW,
                bannerH
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            radius = getAdaptiveRadius(bannerW, SizeType.MEDIUM)
            id = View.generateViewId()
            tag = "banner_card_view"
            alpha = if (startsInfo.image == null) 0f else 1f
        }
        val bannerImageView = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                bannerW,
                bannerH
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            alpha = if (startsInfo.image == null) 0f else 1f
            tag = "image_container_image_view"
            scaleType = ImageView.ScaleType.CENTER_CROP
            if (startsInfo.image != null) {
                loadImage(startsInfo.image)
            }
        }
        val bannerAddIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                bannerAddIcoSize,
                bannerAddIcoSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setImageResource(R.drawable.add_ico)
            imageTintList = ColorStateList.valueOf("#B0B0B0".toColorInt())
            scaleType = ImageView.ScaleType.CENTER_CROP
            tag = "image_container_add_ico"
        }
        bannerCardView.addView(bannerImageView)
        banner.addView(bannerCardView)
        banner.addView(bannerAddIco)
        container.addView(banner)
        banner.setOnClickListener {
            changeImage(-1)
        }
//        buttonsList.add(banner)

        val nameInputId = View.generateViewId()
        val nameInputTextSizee = getTextSizeByHeight(round(nameInputHeight.toFloat() / 2f).toInt(), font)
        val nameInput = createOutlinedTextFieldForOvDialog(context, nameInputWidth, SizeType.SMALL, nameInputHeight, "Введите название", Gravity.CENTER_VERTICAL,nameInputTextSizee,1, startsInfo.name)
        val lp1 = nameInput.layoutParams as ConstraintLayout.LayoutParams
        lp1.startToEnd = bannerId
        lp1.topToTop = bannerId
        lp1.setMargins(marginLeft,0,0,0)
        nameInput.layoutParams = lp1
        nameInput.id = nameInputId
        nameInput.tag = "name_input"
        container.addView(nameInput)
        inputLayoutsList.add(nameInput)
        var nameInputt: TextInputEditText? = null
        for (k in 0 until nameInput.childCount) {
            val obj = nameInput.getChildAt(k)
            if (obj is TextInputEditText) {
                nameInputt = obj
                break
            }
            else if (obj is FrameLayout) {
                for (h in 0 until obj.childCount) {
                    val objj = obj.getChildAt(h)
                    if (objj is TextInputEditText) {
                        nameInputt = objj
                        break
                    }
                }
            }
        }
        nameInputt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                startsInfo.name = nameInputt.text.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        val authorInputId = View.generateViewId()
        val authorInputContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                nameInputWidth,
                nameInputHeight
            )
            layoutparams1.startToStart = nameInputId
            layoutparams1.topToBottom = nameInputId
            layoutparams1.setMargins(0, marginLeft,0,0)
            layoutParams = layoutparams1
            id = authorInputId
            tag = "author_input"
        }
        val authorInput = createOutlinedTextFieldForOvDialog(context, nameInputWidth,SizeType.SMALL, nameInputHeight, "Автор", Gravity.CENTER_VERTICAL, nameInputTextSizee,1, startsInfo.author)
        val lp2 = authorInput.layoutParams as ConstraintLayout.LayoutParams
        lp2.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        lp2.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        authorInput.layoutParams = lp2
        authorInput.id = authorInputId
        authorInputContainer.addView(authorInput)
        val authorInputIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                nameInputHeight,
                nameInputHeight
            )
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            val padding = round(nameInputHeight.toFloat() / 5f).toInt()
            setPadding(padding, padding, padding, padding)
            setImageResource(R.drawable.search_ico)
            imageTintList = ColorStateList.valueOf("#D9D9D9".toColorInt())
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = layoutparams1
        }
        buttonsList.add(authorInputIco)
        authorInputContainer.addView(authorInputIco)
        container.addView(authorInputContainer)
        inputLayoutsList.add(authorInput)
        var authorInputt: TextInputEditText? = null
        for (k in 0 until authorInput.childCount) {
            val obj = authorInput.getChildAt(k)
            if (obj is TextInputEditText) {
                authorInputt = obj
                break
            }
            else if (obj is FrameLayout) {
                for (h in 0 until obj.childCount) {
                    val objj = obj.getChildAt(h)
                    if (objj is TextInputEditText) {
                        authorInputt = objj
                        break
                    }
                }
            }
        }
        authorInputt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                startsInfo.author = authorInputt.text.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val genreGridWidth = nameInputWidth - marginTop
        val genreContainerHeight = bannerH - ((nameInputHeight + marginLeft)*2)
        val genreGridHeight = genreContainerHeight - marginTop
        val newGenreList = mutableListOf<Pair<Boolean, Genre>>()
        for (i in startsInfo.genreList) {
            newGenreList.add(Pair(false, i))
        }
        val addGenreButtonSize = round(nameInputHeight.toFloat() / 1.25f).toInt()
        val genreGrid = createGridOfGenres(context, addGenreButtonSize, newGenreList, 1L, 0L, genreGridWidth, genreGridHeight, marginBetweenInfoElements,
            considerSelectedState = false,
            addShowAllButton = false,
            showAllButtonWidth = addGenreButtonSize,
            addClickListeners = false,
            onClick = {},
            ageText = null,
            episodesText = null,
            sezonText = null,
            yearText = null
        )

        val linesAmount = if (genreGrid.sumHeight == addGenreButtonSize) 1 else round(genreGrid.sumHeight.toFloat() / (addGenreButtonSize+marginBetweenInfoElements).toFloat()).toInt()
        val lastLineOstWidth = genreGridWidth - genreGrid.sumWidth
        val isNewLineNeeded = lastLineOstWidth < addGenreButtonSize || genreGrid.container.isEmpty()
        val addGenreButtonBgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(nameInputHeight, SizeType.SMALL)
            setColor("#BF1B1B1B".toColorInt())
            setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
        }
        val addGenreButtonContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                addGenreButtonSize,
                addGenreButtonSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0, 0, 0, 0)
            layoutParams = layoutparams1
            background = addGenreButtonBgDrawable
            tag = "add_genre_button"
        }
        val addGenerButtonIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setImageResource(R.drawable.add_ico)
            imageTintList = ColorStateList.valueOf("#B0B0B0".toColorInt())
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        addGenreButtonContainer.addView(addGenerButtonIco)
        val genreGridView = genreGrid.container
        val showAllButton: ViewGroup? = genreGridView.findViewWithTag("show_all_info_button")
        val sumHeight = genreGrid.sumHeight + if (isNewLineNeeded && showAllButton == null) (if (linesAmount != 0) { marginBetweenInfoElements } else {0} + addGenreButtonSize) else 0
        val lp11 = genreGridView.layoutParams as ConstraintLayout.LayoutParams
        lp11.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        lp11.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        lp11.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        lp11.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        lp11.height = sumHeight
        genreGridView.layoutParams = lp11
        genreGridView.tag = "genre_grid_view"
        if (showAllButton == null) {
            if (genreGridView.isNotEmpty()) {
                val lastView = genreGridView.getChildAt((genreGridView.childCount-1)) as ConstraintLayout
                val lp1 = addGenreButtonContainer.layoutParams as ConstraintLayout.LayoutParams
                lp1.startToStart = ConstraintLayout.LayoutParams.UNSET
                lp1.topToTop = ConstraintLayout.LayoutParams.UNSET
                if (!isNewLineNeeded) {
                    lp1.startToEnd = lastView.id
                    lp1.topToTop = lastView.id
                    lp1.setMargins(marginBetweenInfoElements,0,0,0)
                }
                else {
                    lp1.topToBottom = lastView.id
                    lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    lp1.setMargins(0,marginBetweenInfoElements,0,0)
                }
                addGenreButtonContainer.layoutParams = lp1
                genreGridView.addView(addGenreButtonContainer)
            }
            else {
                genreGridView.addView(addGenreButtonContainer)
            }
        }
        else {
            val lp1 = addGenreButtonContainer.layoutParams as ConstraintLayout.LayoutParams
            lp1.startToStart = ConstraintLayout.LayoutParams.UNSET
            lp1.topToTop = ConstraintLayout.LayoutParams.UNSET
            lp1.startToStart = showAllButton.id
            lp1.topToTop = showAllButton.id
            lp1.setMargins(0,0,0,0)
            addGenreButtonContainer.layoutParams = lp1
            genreGridView.addView(addGenreButtonContainer)
        }

        val genreContainerBackgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(nameInputWidth, SizeType.SMALL)
            setColor("#BF1B1B1B".toColorInt())
            setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
        }

        val newGenreContainerHeight = sumHeight + marginTop
        val genreContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                nameInputWidth,
                newGenreContainerHeight
            )
            layoutparams1.startToStart = nameInputId
            layoutparams1.topToBottom = authorInputId
            layoutparams1.setMargins(0,marginLeft,0,0)
            layoutParams = layoutparams1
            background = genreContainerBackgroundDrawable
            tag = "genre_container"
        }
        genreContainer.addView(genreGridView)
        container.addView(genreContainer)
        buttonsList.add(genreContainer)

        fun updateGenreList(newGenreList: List<Pair<Boolean, Genre>>) {
            val ls = mutableListOf<Genre>()
            for (i in newGenreList) {
                if (i.first) {
                    ls.add(i.second)
                }
            }
            startsInfo.genreList = ls
            val newGenreList = mutableListOf<Pair<Boolean, Genre>>()
            for (i in startsInfo.genreList) {
                newGenreList.add(Pair(false, i))
            }
            val addGenreButtonSize = round(nameInputHeight.toFloat() / 1.25f).toInt()
            val genreGrid = createGridOfGenres(context, addGenreButtonSize, newGenreList, 1L, 0L, genreGridWidth, genreGridHeight, marginBetweenInfoElements,
                considerSelectedState = false,
                addShowAllButton = false,
                showAllButtonWidth = addGenreButtonSize,
                addClickListeners = false,
                onClick = {},
                ageText = null,
                episodesText = null,
                sezonText = null,
                yearText = null
            )

            val linesAmount = if (genreGrid.sumHeight == addGenreButtonSize) 1 else round(genreGrid.sumHeight.toFloat() / (addGenreButtonSize+marginBetweenInfoElements).toFloat()).toInt()
            val lastLineOstWidth = genreGridWidth - genreGrid.sumWidth
            val isNewLineNeeded = lastLineOstWidth < addGenreButtonSize || genreGrid.container.isEmpty()
            val addGenreButtonBgDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = getAdaptiveRadius(nameInputHeight, SizeType.SMALL)
                setColor("#BF1B1B1B".toColorInt())
                setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
            }
            val addGenreButtonContainer = ConstraintLayout(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    addGenreButtonSize,
                    addGenreButtonSize
                )
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.setMargins(0, 0, 0, 0)
                layoutParams = layoutparams1
                background = addGenreButtonBgDrawable
                tag = "add_genre_button"
            }
            val addGenerButtonIco = ImageView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams = layoutparams1
                setImageResource(R.drawable.add_ico)
                imageTintList = ColorStateList.valueOf("#B0B0B0".toColorInt())
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            addGenreButtonContainer.addView(addGenerButtonIco)
            val genreGridView = genreGrid.container
            genreGridView.tag = "genre_grid_view"
            val showAllButton: ViewGroup? = genreGridView.findViewWithTag("show_all_info_button")
            val sumHeight = genreGrid.sumHeight + if (isNewLineNeeded && showAllButton == null) (if (linesAmount != 0) { marginBetweenInfoElements } else {0} + addGenreButtonSize) else 0
            val lp11 = genreGridView.layoutParams as ConstraintLayout.LayoutParams
            lp11.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp11.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp11.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp11.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            lp11.height = sumHeight
            genreGridView.layoutParams = lp11
            if (showAllButton == null) {
                if (genreGridView.isNotEmpty()) {
                    val lastView = genreGridView.getChildAt((genreGridView.childCount-1)) as ConstraintLayout
                    val lp1 = addGenreButtonContainer.layoutParams as ConstraintLayout.LayoutParams
                    lp1.startToStart = ConstraintLayout.LayoutParams.UNSET
                    lp1.topToTop = ConstraintLayout.LayoutParams.UNSET
                    if (!isNewLineNeeded) {
                        lp1.startToEnd = lastView.id
                        lp1.topToTop = lastView.id
                        lp1.setMargins(marginBetweenInfoElements,0,0,0)
                    }
                    else {
                        lp1.topToBottom = lastView.id
                        lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        lp1.setMargins(0,marginBetweenInfoElements,0,0)
                    }
                    addGenreButtonContainer.layoutParams = lp1
                    genreGridView.addView(addGenreButtonContainer)
                }
                else {
                    genreGridView.addView(addGenreButtonContainer)
                }
            }
            else {
                val lp1 = addGenreButtonContainer.layoutParams as ConstraintLayout.LayoutParams
                lp1.startToStart = ConstraintLayout.LayoutParams.UNSET
                lp1.topToTop = ConstraintLayout.LayoutParams.UNSET
                lp1.startToStart = showAllButton.id
                lp1.topToTop = showAllButton.id
                lp1.setMargins(0,0,0,0)
                addGenreButtonContainer.layoutParams = lp1
                genreGridView.addView(addGenreButtonContainer)
            }
            genreContainer.removeAllViews()
            val lp1 = genreContainer.layoutParams as ConstraintLayout.LayoutParams
            val newGenreContainerHeight = sumHeight + marginTop
            lp1.height = newGenreContainerHeight
            genreContainer.layoutParams = lp1
            genreContainer.addView(genreGridView)
            addGenreButtonContainer.setOnClickListener {
                container.requestFocus()
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                for (i in inputLayoutsList) {
                    i.clearFocus()
                    imm.hideSoftInputFromWindow(i.windowToken, 0)
                }
                val key = ResultKeys.CREATE_CARD_GENRE_CHOICE
                openGenreChoice(key)
            }
        }


        val editBannerButtonSize = hBtn
        val editBannerButtonIcoSize = round(editBannerButtonSize.toFloat() / 1.87f).toInt()
        val editBannerButtonId = View.generateViewId()
        val editBannerButtonBgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(editBannerButtonSize, SizeType.SMALL)
            setColor("#BF1B1B1B".toColorInt())
            setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
        }
        val editBannerButtonContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                editBannerButtonSize,
                editBannerButtonSize
            )
            layoutparams1.startToStart = bannerId
            layoutparams1.topToBottom = bannerId
            layoutparams1.setMargins(0,marginTop,0,0)
            layoutParams = layoutparams1
            background = editBannerButtonBgDrawable
            id = editBannerButtonId
        }
        val editBannerButtonIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                editBannerButtonIcoSize,
                editBannerButtonIcoSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setImageResource(R.drawable.edit_ico)
            imageTintList = ColorStateList.valueOf("#D9D9D9".toColorInt())
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        editBannerButtonContainer.addView(editBannerButtonIco)
        container.addView(editBannerButtonContainer)
        buttonsList.add(editBannerButtonContainer)

        val searchBannerContainerWidth = bannerW - editBannerButtonSize - marginBetweenInfoElements
        val searchButtonContainerDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(searchBannerContainerWidth, SizeType.SMALL)
            setColor("#BF1B1B1B".toColorInt())
            setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
        }
        val searchTextSize = getTextSizeByHeight(editBannerButtonIcoSize,font)
        val searchTextWidth = round(searchBannerContainerWidth.toFloat() / 1.831f).toInt()
        val searchButtonContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                searchBannerContainerWidth,
                editBannerButtonSize
            )
            layoutparams1.startToEnd = editBannerButtonId
            layoutparams1.topToTop = editBannerButtonId
            layoutparams1.setMargins(marginBetweenInfoElements,0,0,0)
            layoutParams = layoutparams1
            background = searchButtonContainerDrawable
        }
        val searchButtonTextId = View.generateViewId()
        val searchButtonText = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                searchTextWidth,
                editBannerButtonIcoSize
            )
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            text = "Пойск"
            setTextColor("#D9D9D9".toColorInt())
            setTextSize(TypedValue.COMPLEX_UNIT_PX, searchTextSize)
            includeFontPadding = false
            id = searchButtonTextId
            typeface = font
            gravity = Gravity.CENTER_VERTICAL
        }
        val searchButtonIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                editBannerButtonIcoSize,
                editBannerButtonIcoSize
            )
            layoutparams1.endToStart = searchButtonTextId
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,0,marginTop,0)
            layoutParams = layoutparams1
            setImageResource(R.drawable.search_ico)
            imageTintList = ColorStateList.valueOf("#D9D9D9".toColorInt())
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        searchButtonContainer.addView(searchButtonText)
        searchButtonContainer.addView(searchButtonIco)
        container.addView(searchButtonContainer)
        buttonsList.add(searchButtonContainer)


        var episodesList: MutableList<episodeInfo> = startsInfo.episodesList as MutableList<episodeInfo>
        if (startsInfo.type != ElementType.Music) {
            val descriptionInputWidth = bannerW + marginLeft + nameInputWidth
            val descriptionInputHeight = round(descriptionInputWidth.toFloat() / 2.83f).toInt()
            val descriptionInputMarginTop = round(marginTop.toFloat() * 1.8f).toInt()
            val descriptionInputId = View.generateViewId()
            val descriptionInput = createOutlinedTextFieldForOvDialog(context, descriptionInputWidth,SizeType.SMALL, descriptionInputHeight, "Введите описание", Gravity.TOP, nameInputTextSizee, null, startsInfo.description)
            val lp3 = descriptionInput.layoutParams as ConstraintLayout.LayoutParams
            lp3.startToStart = bannerId
            lp3.topToBottom = editBannerButtonId
            lp3.setMargins(0,descriptionInputMarginTop,0,0)
            descriptionInput.layoutParams = lp3
            descriptionInput.id = descriptionInputId
            descriptionInput.tag = "description_input"
            container.addView(descriptionInput)
            inputLayoutsList.add(descriptionInput)

            var descriptionInputt: TextInputEditText? = null
            for (k in 0 until descriptionInput.childCount) {
                val obj = descriptionInput.getChildAt(k)
                if (obj is TextInputEditText) {
                    descriptionInputt = obj
                    break
                }
                else if (obj is FrameLayout) {
                    for (h in 0 until obj.childCount) {
                        val objj = obj.getChildAt(h)
                        if (objj is TextInputEditText) {
                            descriptionInputt = objj
                            break
                        }
                    }
                }
            }
            descriptionInputt?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    startsInfo.description = descriptionInputt.text.toString()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            val episodesHTextHeight = round(hText.measuredHeight.toFloat() / 1.208f).toInt()
            val episodesHTextSize = getTextSizeByHeight(episodesHTextHeight, boldFont)
            val episodesHTextId = View.generateViewId()


            val episodesHText = TextView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    episodesHTextHeight
                )
                layoutparams1.startToStart = descriptionInputId
                layoutparams1.topToBottom = descriptionInputId
                layoutparams1.setMargins(0, descriptionInputMarginTop,0,0)
                layoutParams = layoutparams1
                includeFontPadding = false
                setTextSize(TypedValue.COMPLEX_UNIT_PX, episodesHTextSize)
                setTextColor("#FFFFFF".toColorInt())
                typeface = boldFont
                id = episodesHTextId
                text = "Эпизоды"
            }
            container.addView(episodesHText)

            val addEpisodeBgDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = getAdaptiveRadius(descriptionInputWidth, SizeType.SMALL)
                setColor("#1B1B1B".toColorInt())
                setStroke(round(1f*baseDensity).toInt(), "#9C9C9C".toColorInt())
            }
            val addEpisodesContainerBgDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = getAdaptiveRadius(descriptionInputWidth, SizeType.SMALL)
                setColor("#BF1B1B1B".toColorInt())
                setStroke(round(1f*baseDensity).toInt(), "#111111".toColorInt())
            }

            val addEpisodeButtonContainerWidth = descriptionInputWidth - marginTop
            val episodesBlockWidth = min(descriptionInputWidth, round(320f*baseDensity).toInt())
            val maxEpisodesBlockHeight = round(episodesBlockWidth.toFloat() / 1.2f).toInt()

            val maxEditEpisodesBlockHeight = maxEpisodesBlockHeight - nameInputHeight - marginTop - round(30f*density).toInt()
            var editEpisodesBlockHeight = ((round(120f*density).toInt() + round(30f*density).toInt())*episodesList.size) -  if (episodesList.isNotEmpty()) round(30f*density).toInt() else 0
            if (editEpisodesBlockHeight > maxEditEpisodesBlockHeight) {
                editEpisodesBlockHeight = maxEditEpisodesBlockHeight
            }
            val addEpisodesBlockContainer = ConstraintLayout(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    descriptionInputWidth,
                    (editEpisodesBlockHeight + marginTop + nameInputHeight + round(30f*density).toInt())
                )
                layoutparams1.startToStart = episodesHTextId
                layoutparams1.topToBottom = episodesHTextId
                layoutparams1.setMargins(0,marginTop,0,0)
                layoutParams = layoutparams1
                background = addEpisodesContainerBgDrawable
                id = View.generateViewId()
                tag = "add_episodes_block_container"
            }

            val addEpisodeButtonId = View.generateViewId()
            val addEpisodeButtonContainer = ConstraintLayout(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    addEpisodeButtonContainerWidth,
                    nameInputHeight
                )
                layoutParams = layoutparams1
                id = addEpisodeButtonId
                background = addEpisodeBgDrawable
                tag = "add_episode_button"
            }

            fun applyEpisodesListChanges(list: MutableList<episodeInfo>) {
                episodesList = list
                editEpisodesBlockHeight = ((round(120f*density).toInt() + round(30f*density).toInt())*episodesList.size) -  if (episodesList.isNotEmpty()) round(30f*density).toInt() else 0
                if (editEpisodesBlockHeight > maxEditEpisodesBlockHeight) {
                    editEpisodesBlockHeight = maxEditEpisodesBlockHeight
                }
                val lp1 = addEpisodesBlockContainer.layoutParams as ConstraintLayout.LayoutParams
                lp1.height = (editEpisodesBlockHeight + marginTop + nameInputHeight + if (list.isNotEmpty()) round(30f*density).toInt() else 0)
                addEpisodesBlockContainer.layoutParams = lp1
                if (list.isEmpty()) {
                    val lp2 = addEpisodeButtonContainer.layoutParams as ConstraintLayout.LayoutParams
                    lp2.setMargins(0,0,0,0)
                    addEpisodeButtonContainer.layoutParams = lp2
                }
                else {
                    val lp2 = addEpisodeButtonContainer.layoutParams as ConstraintLayout.LayoutParams
                    lp2.setMargins(0,round(30f*density).toInt(),0,0)
                    addEpisodeButtonContainer.layoutParams = lp2
                }
            }
            val editEpisodesContainer = createFlatGrid(context, createFlatGridInput.EditEpisodes(episodesList, callback = {applyEpisodesListChanges((it as MutableList<episodeInfo>))}), addEpisodeButtonContainerWidth, editEpisodesBlockHeight, changeImage = {changeImage(it)})
            val lpp = editEpisodesContainer.layoutParams as ConstraintLayout.LayoutParams
            lpp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lpp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lpp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lpp.setMargins(0,round(marginTop.toFloat() / 2f).toInt(),0,0)
            lpp.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            lpp.matchConstraintMinHeight = 0
            lpp.matchConstraintMaxHeight = maxEditEpisodesBlockHeight
            editEpisodesContainer.layoutParams = lpp
            editEpisodesContainer.tag = "edit_episodes_container"
            editEpisodesContainer.id = View.generateViewId()
            val editEpisodesRecyclerView = editEpisodesContainer.getChildAt(0) as RecyclerView
            val lpp1 = editEpisodesRecyclerView.layoutParams as ConstraintLayout.LayoutParams
            lpp1.constrainedHeight = true
            lpp1.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            editEpisodesRecyclerView.layoutParams = lpp1
            val lppp1 = addEpisodeButtonContainer.layoutParams as ConstraintLayout.LayoutParams
            lppp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lppp1.topToBottom = editEpisodesContainer.id
            lppp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lppp1.setMargins(0,round(30f*density).toInt(),0,0)
            addEpisodeButtonContainer.layoutParams = lppp1
            val addEpisodeButtonIco = ImageView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    nameInputHeight,
                    nameInputHeight
                )
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                setImageResource(R.drawable.add_ico)
                imageTintList = ColorStateList.valueOf("#B0B0B0".toColorInt())
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = layoutparams1
            }

            addEpisodeButtonContainer.addView(addEpisodeButtonIco)
            addEpisodesBlockContainer.addView(editEpisodesContainer)
            addEpisodesBlockContainer.addView(addEpisodeButtonContainer)
            container.addView(addEpisodesBlockContainer)
            applyEpisodesListChanges(startsInfo.episodesList)
        }
        else if (startsInfo.type == ElementType.Playlist) {}
        else {
            val trackText = TextView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                layoutparams1.startToStart = editBannerButtonContainer.id
                layoutparams1.topToBottom = editBannerButtonContainer.id
                layoutparams1.setMargins(0,marginTop,0,0)
                setTextSize(TypedValue.COMPLEX_UNIT_PX,hTextSize)
                setTextColor("#FFFFFF".toColorInt())
                maxLines = 1
                text = context.resources.getString(R.string.Track)
                layoutParams = layoutparams1
                includeFontPadding = false
                typeface = boldFont
                id = View.generateViewId()
                measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
            }
            val trackIco = ImageView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    hTextSize.toInt(),
                    hTextSize.toInt()
                )
                layoutparams1.startToEnd = trackText.id
                layoutparams1.topToTop = trackText.id
                layoutparams1.bottomToBottom = trackText.id
                layoutParams = layoutparams1
                setImageResource(R.drawable.music_note_ico)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            container.addView(trackText)
            container.addView(trackIco)

            val addTrackButtonBgDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = getAdaptiveRadius(nameInputHeight, SizeType.SMALL)
                setColor("#BF1B1B1B".toColorInt())
                setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
            }
            val addTrackButton = ConstraintLayout(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    nameInputHeight,
                    nameInputHeight
                )
                layoutparams1.startToStart = trackText.id
                layoutparams1.topToBottom = trackText.id
                layoutparams1.setMargins(0,marginBetweenInfoElements,0,0)
                layoutParams = layoutparams1
                background = addTrackButtonBgDrawable
                id = View.generateViewId()
            }
            val addTrackIco = ImageView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    nameInputHeight,
                    nameInputHeight
                )
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams = layoutparams1
                setImageResource(R.drawable.add_ico)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            addTrackButton.addView(addTrackIco)
            container.addView(addTrackButton)
            buttonsList.add(addTrackButton)

            val searchTrackButton = ConstraintLayout(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    nameInputHeight,
                    nameInputHeight
                )
                layoutparams1.startToStart = addTrackButton.id
                layoutparams1.topToBottom = addTrackButton.id
                layoutparams1.setMargins(marginBetweenInfoElements,0,0,0)
                layoutParams = layoutparams1
                background = addTrackButtonBgDrawable
            }
            val searchTrackIco = ImageView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    round(nameInputHeight.toFloat() / 1.25f).toInt(),
                    round(nameInputHeight.toFloat() / 1.25f).toInt()
                )
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams = layoutparams1
                setImageResource(R.drawable.search_ico)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            searchTrackButton.addView(searchTrackIco)
            container.addView(searchTrackButton)
            buttonsList.add(searchTrackButton)
            val addVerticalVideoBackgroundDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = getAdaptiveRadius(nameInputHeight*3, SizeType.SMALL)
                setColor("#BF1B1B1B".toColorInt())
                setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
            }
            val addVerticalVideoButton = ConstraintLayout(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    nameInputHeight*3,
                    nameInputHeight
                )
                layoutparams1.endToEnd = nameInputId
                layoutparams1.bottomToBottom = addTrackButton.id
                layoutParams = layoutparams1
                background = addVerticalVideoBackgroundDrawable
                id = View.generateViewId()
            }
            val addVerticalVideoIco = ImageView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    nameInputHeight,
                    nameInputHeight
                )
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams = layoutparams1
                setImageResource(R.drawable.add_ico)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }



        val addCardButtonBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(scrollContainerWidth, SizeType.SMALL)
            setColor("#805EFF56".toColorInt())
        }

        val addCardButtonContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                scrollContainerWidth,
                nameInputHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(marginLeft,0,0,marginTop)
            layoutParams = layoutparams1
            background = addCardButtonBg
            tag = "add_card_button"
            elevation = 101f
        }

        val addCardButtonTextHeight = round(nameInputHeight.toFloat() / 1.82f).toInt()
        val addCardButtonTextSize = getTextSizeByHeight(addCardButtonTextHeight, font)

        val addCardButtonText = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            includeFontPadding = false
            typeface = font
            setTextSize(TypedValue.COMPLEX_UNIT_PX, addCardButtonTextSize)
            text = "Добавить"
            setTextColor("#FFFFFF".toColorInt())
        }

        addCardButtonContainer.addView(addCardButtonText)
        containerr.addView(addCardButtonContainer)
        buttonsList.add(addCardButtonContainer)
        buttonsList.add(containerr)
        for (i in buttonsList) {
            i.setOnClickListener {
                i.requestFocus()
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                for (j in inputLayoutsList) {
                    j.clearFocus()
                    imm.hideSoftInputFromWindow(j.windowToken, 0)
                }
            }
        }


        with(container) {
            isFocusableInTouchMode = true
            isFocusable = true
        }
        container.setOnClickListener {
            container.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            for (i in inputLayoutsList) {
                i.clearFocus()
                imm.hideSoftInputFromWindow(i.windowToken, 0)
            }
        }
        container.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                }
                MotionEvent.ACTION_UP -> {
                    view.performClick()
                }
            }
            true
        }
        containerr.setOnClickListener {
            containerr.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            for (i in inputLayoutsList) {
                i.clearFocus()
                imm.hideSoftInputFromWindow(i.windowToken, 0)
            }
        }
        containerr.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                }
                MotionEvent.ACTION_UP -> {
                    view.performClick()
                }
            }
            true
        }

        addCardButtonContainer.setOnClickListener {
            addCard(episodesList)
        }
        val ls = mutableListOf<Pair<Boolean, Genre>>()
        for (i in startsInfo.genreList) {
            ls.add(Pair(true, i))
        }
        updateGenreList(ls)

        context.lifecycleOwner?.lifecycleScope?.launch {
            resultSenderViewModel.results.collect { (key, data) -> run {
                when (key) {
                    ResultKeys.CREATE_CARD_GENRE_CHOICE -> {
                        val newGenreList = data as List<Pair<Boolean, Genre>>
                        updateGenreList(newGenreList)
                    }
                }
            }
            }
        }
        return containerr
    }
    @SuppressLint("ClickableViewAccessibility")
    fun GenreChoice(context: Context, startsInfo: OverLayLayer.GenreChoice, resultSenderViewModel: ResultSenderViewModel, deny: () -> Unit) : ConstraintLayout {
        val font = context.resources.getFont(R.font.google_sans_regular)
        val boldFont = context.resources.getFont(R.font.google_sans_bold)
        val containerWidth = min(round(420f*baseDensity).toInt(), round(screenWidth.toFloat() / 1.25f).toInt())
        val textHeight = round(max(screenHeight,screenWidth).toFloat() / 30f).toInt()
        val hTextSizee = getTextSizeByHeight(textHeight,boldFont)
        val marginBetweenInfoElements = round(containerWidth.toFloat() / 57.6f).toInt()
        val gridWidth = containerWidth - marginBetweenInfoElements*4
        val infoContainerHeight = round((32f*baseDensity) / 1.25f).toInt()
        val buttonHeight = round(32f*baseDensity).toInt()
        val buttonWidth = round(containerWidth.toFloat() / 2f).toInt()
        val hTextViewId = View.generateViewId()
        val hTextView = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,marginBetweenInfoElements*2,0,0)
            includeFontPadding = false
            typeface = boldFont
            maxWidth = gridWidth
            setTextSize(TypedValue.COMPLEX_UNIT_PX, hTextSizee)
            text = "Выберите жанры"
            setTextColor("#FFFFFF".toColorInt())
            ellipsize = TextUtils.TruncateAt.END
            layoutParams = layoutparams1
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            id = hTextViewId
        }
        val textViewHeight = hTextView.measuredHeight
        val maxAllHeight = round(screenHeight.toFloat() / 1.5f).toInt()
        val maxGridContainerHeight = maxAllHeight - textViewHeight - marginBetweenInfoElements*6 - buttonHeight

        val fullGenreList = mutableListOf<Pair<Boolean, Genre>>()
        for (i in genreNames) {
            var isActive = false
            for (j in startsInfo.genreList) {
                if (j.second == i.key) {
                    isActive = j.first
                }
            }
            fullGenreList.add(Pair(isActive, i.key))
        }
        val gridContainer = ConstraintLayout(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                (gridWidth-(marginBetweenInfoElements*2)),
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }
        fun apply(genre: Genre) {
            for (i in 0 until fullGenreList.size) {
                val obj = fullGenreList[i]
                if (obj.second == genre) {
                    fullGenreList[i] = Pair(!obj.first, obj.second)
                }
            }
            val newGenreGrid = createGridOfGenres(
                context = context,
                infoContainerHeight = infoContainerHeight,
                genreList = fullGenreList,
                length = 1L,
                alreadyWatched = 0L,
                widthh = (gridWidth-(marginBetweenInfoElements*2)),
                heightt = 10000000,
                marginBetweenInfoElements = marginBetweenInfoElements,
                considerSelectedState = true,
                addShowAllButton = false,
                showAllButtonWidth = null,
                addClickListeners = true,
                onClick = {
                    apply(it)
                }
            )
            val newGenreGridView = newGenreGrid.container
            val lp1 = newGenreGridView.layoutParams as ConstraintLayout.LayoutParams
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            newGenreGridView.layoutParams = lp1
            gridContainer.removeAllViews()
            gridContainer.addView(newGenreGridView)
            startsInfo.genreList = fullGenreList
        }

        val genreGrid = createGridOfGenres(
            context = context,
            infoContainerHeight = infoContainerHeight,
            genreList = fullGenreList,
            length = 1L,
            alreadyWatched = 0L,
            widthh = (gridWidth-(marginBetweenInfoElements*2)),
            heightt = 10000000,
            marginBetweenInfoElements = marginBetweenInfoElements,
            considerSelectedState = true,
            addShowAllButton = false,
            showAllButtonWidth = null,
            addClickListeners = true,
            onClick = {
                apply(it)
            }
        )
        val genreGridMarginTop = round((marginBetweenInfoElements.toFloat()*2f) / 1.2f).toInt()
        val genreGridHeight = genreGrid.sumHeight
        var genreGridBlockContainerHeight = genreGridHeight + genreGridMarginTop*2
        if (genreGridBlockContainerHeight > maxGridContainerHeight) {
            genreGridBlockContainerHeight = maxGridContainerHeight
        }
        val containerHeight = textViewHeight + genreGridBlockContainerHeight + marginBetweenInfoElements*6 + buttonHeight

        val gridBlockContainerDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(gridWidth, SizeType.MEDIUM)
            setColor("#29262C".toColorInt())
            setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
        }
        val containerDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(containerWidth, SizeType.MEDIUM)
            setColor("#181619".toColorInt())
        }
        val containerStrokeDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
            cornerRadius = getAdaptiveRadius(containerWidth, SizeType.MEDIUM)
        }
        val applyButtonDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setStroke(round(1f*baseDensity).toInt(), "#000000".toColorInt())
            setColor("#805EFF56".toColorInt())
            val radius = getAdaptiveRadius(containerWidth, SizeType.MEDIUM)
            cornerRadii = floatArrayOf(0f,0f,0f,0f,radius,radius,0f,0f)
        }
        val denyButtonDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setStroke(round(1f*baseDensity).toInt(), "#000000".toColorInt())
            setColor("#80DE5B5B".toColorInt())
            val radius = getAdaptiveRadius(containerWidth, SizeType.MEDIUM)
            cornerRadii = floatArrayOf(0f,0f,0f,0f,0f,0f,radius,radius)
        }


        val container = ConstraintLayout(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                containerWidth,
                containerHeight
            )
            background = containerDrawable
        }
        val stroke = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                containerWidth,
                containerHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            background = containerStrokeDrawable
            elevation = 100f
        }
        container.addView(stroke)
        container.addView(hTextView)

        val gridBlockContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                gridWidth,
                genreGridBlockContainerHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToBottom = hTextViewId
            layoutparams1.setMargins(0,marginBetweenInfoElements*2,0,0)
            layoutParams = layoutparams1
            background = gridBlockContainerDrawable
        }
        val gridScrollContainer = ScrollView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                (gridWidth-(marginBetweenInfoElements*2)),
                (genreGridBlockContainerHeight - genreGridMarginTop*2)
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
        }

        val genreGridView = genreGrid.container
        val lp1 = genreGridView.layoutParams as ConstraintLayout.LayoutParams
        lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        genreGridView.layoutParams = lp1
        gridContainer.addView(genreGridView)
        gridScrollContainer.addView(gridContainer)
        gridBlockContainer.addView(gridScrollContainer)
        container.addView(gridBlockContainer)

        val buttonTextHeight = round(buttonHeight.toFloat() / 1.9f).toInt()
        val buttonTextSizee = getTextSizeByHeight(buttonTextHeight, font)
        val applyButtonContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonWidth,
                buttonHeight
            )
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            background = applyButtonDrawable
        }
        val applyButtonText = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                buttonTextHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            includeFontPadding = false
            typeface = font
            setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSizee)
            text = "Подтвердить"
            setTextColor("#FFFFFF".toColorInt())
            maxWidth = buttonWidth
            ellipsize = TextUtils.TruncateAt.END
        }
        applyButtonContainer.addView(applyButtonText)
        container.addView(applyButtonContainer)

        val denyButtonContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                buttonWidth,
                buttonHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            background = denyButtonDrawable
        }
        val denyButtonText = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                buttonTextHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            includeFontPadding = false
            typeface = font
            setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSizee)
            text = "Отменить"
            setTextColor("#FFFFFF".toColorInt())
            maxWidth = buttonWidth
            ellipsize = TextUtils.TruncateAt.END
        }

        denyButtonContainer.addView(denyButtonText)
        container.addView(denyButtonContainer)

        applyButtonContainer.setOnClickListener {
            resultSenderViewModel.sendResult(startsInfo.key, fullGenreList)
            deny()
        }
        denyButtonContainer.setOnClickListener {
            deny()
        }
        container.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                }
                MotionEvent.ACTION_UP -> {
                    view.performClick()
                }
            }
            true
        }

        return container
    }
}
fun blobInit(size: Int, color: String, positions: FloatArray = floatArrayOf(0f, 1f), alphaRatio: Float = 0.3f): ShapeDrawable {
    val res = ShapeDrawable(OvalShape()).apply {
        val color1 = color.toColorInt()
        val colors = intArrayOf(ColorUtils.setAlphaComponent(color1, (255 * alphaRatio).toInt()), ColorUtils.setAlphaComponent(getDeepDarkColor(color1), (255 * 0.0).toInt()))

        shaderFactory = object : ShapeDrawable.ShaderFactory() {
            override fun resize(p0: Int, p1: Int): Shader {
                return RadialGradient(
                    size / 2f, size / 2f, // Центр
                    size / delitRad,             // Радиус
                    colors,
                    positions,
                    Shader.TileMode.CLAMP
                )
            }
        }
    }
    return res
}
fun getDeepDarkColor(color: Int): Int {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color, hsl)
    hsl[1] = 0.85f
    hsl[2] = 0.08f
    return ColorUtils.HSLToColor(hsl)
}
fun createOutlinedTextFieldForOvDialog(context: Context, width: Int, radius: SizeType, heightt: Int, hintText: String, gravityy: Int = Gravity.CENTER, textSizee: Float, maxLiness: Int? = null, alreadyEnteredText: String? = null): TextInputLayout {
    val font = context.resources.getFont(R.font.google_sans_regular)
    val strokeWidth = round(1f*baseDensity).toInt()
    val inputLayout = TextInputLayout(context).apply {
        val layoutParams1 = ConstraintLayout.LayoutParams(
            width,
            heightt
        )
        layoutParams = layoutParams1
        val radius1 = getAdaptiveRadius(width, radius)
        boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        boxBackgroundColor = "#BF1B1B1B".toColorInt()

        // Устанавливаем одинаковую ширину для всех состояний
        boxStrokeWidth = strokeWidth
        boxStrokeWidthFocused = strokeWidth

        // Цветовая схема: всегда один цвет, чтобы не было визуальных "дерганий"
        val strokeColor = "#809C9C9C".toColorInt()
        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused), // Фокус
            intArrayOf()                             // Все остальные
        )
        val colors = intArrayOf(strokeColor, strokeColor)
        setBoxStrokeColorStateList(ColorStateList(states, colors))

        setBoxCornerRadii(radius1, radius1, radius1, radius1)

        typeface = font
        isHintEnabled = false
        gravity = gravityy
    }

    val editText = TextInputEditText(inputLayout.context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            heightt
        )

        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
        setTextColor("#AFAFAF".toColorInt())

        typeface = font
        background = null
        gravity = gravityy
        if (maxLiness != null) {
            maxLines = maxLiness
            if (maxLiness == 1) {
                isSingleLine = true
            }
        }

        hint = hintText
        setHintTextColor("#AFAFAF".toColorInt())
        setPadding(textSizee.toInt(),if (gravityy == Gravity.TOP) textSizee.toInt() else 0,0,0)
        includeFontPadding = false
        setText(alreadyEnteredText)
        tag = "edit_text"
    }

    inputLayout.addView(editText)
    return inputLayout
}
fun createGridOfGenres(context: Context, infoContainerHeight: Int, genreList: List<Pair<Boolean, Genre>>, length: Long, alreadyWatched: Long, widthh: Int, heightt: Int, marginBetweenInfoElements: Int, considerSelectedState: Boolean = false, addShowAllButton: Boolean = false, showAllButtonWidth: Int? = null, addClickListeners: Boolean = false, onClick: (Genre) -> Unit, ageText: String? = null, episodesText: String? = null, sezonText: String? = null, yearText: String? = null): createGridOfGenresReturn {
    val container = ConstraintLayout(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            widthh,
            heightt
        )
        layoutParams = layoutparams1
    }

    val infoTextHeight = round(infoContainerHeight.toFloat() / 1.46f).toInt()
    var infoTextSize = 0f
    val infoTextFont = ResourcesCompat.getFont(context,R.font.google_sans_regular)
    // Подбор размера шрифта
    for (i in 0 until steps.size) {
        val res = optimizeText("СъешьжеещёHj", 1000, steps[i], false, infoTextFont, 1)
        if (res.totalHeight <= infoTextHeight) {
            infoTextSize = steps[i]
            break
        }
    }

    val infoContainersList = mutableListOf<Triple<ConstraintLayout, Int, Pair<Boolean, Genre>>>()  // Список для хранения view жанров и инфы о них
    // Создание самих view жанров
    for (i in 0 until genreList.size) {
        val color = genreColors[genreList[i].second]
        var infoTextText = genreNames[genreList[i].second]
        when (genreList[i].second) {
            Genre.Age -> infoTextText = ageText
            Genre.Episodes -> infoTextText = episodesText
            Genre.Sezon -> infoTextText = sezonText
            Genre.Year -> infoTextText = yearText
            else -> {}
        }
        val positions = floatArrayOf(0f, if (length != 0.toLong()) {(alreadyWatched.toFloat() / (length.toFloat()/100f))} else {1f})

        val maxTextWidth = round((widthh - if (genreList[i].first) infoContainerHeight else 0).toFloat() / 1.65f).toInt()
        val infoText = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            text = infoTextText
            setTextSize(TypedValue.COMPLEX_UNIT_PX, infoTextSize)
            this.typeface = infoTextFont
            includeFontPadding = false
            setTextColor(if (color == "&") {"#DFDFDF".toColorInt()} else{color?.toColorInt() ?: "#FFFFFF".toColorInt()})
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            maxWidth = maxTextWidth
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            layoutParams = layoutparams1
        }
        infoText.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val textWidth = infoText.measuredWidth

        val infoContainerWidth = round(textWidth.toFloat() * 1.65f).toInt() + if (genreList[i].first) infoContainerHeight else 0
        val drawable = createGradientStrokeDrawable(if (color == "&") {"#FF4545".toColorInt()} else {color!!.toColorInt()}, if (color == "&") {"#BFDFDFDF".toColorInt()} else {color.toColorInt()}, 3, getAdaptiveRadius(infoContainerWidth, SizeType.SMALL), positions)
        val infoContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                infoContainerWidth,
                infoContainerHeight
            )
            layoutParams = layoutparams1
            background = drawable
            val newId = View.generateViewId()
            id = newId
        }
        if (genreList[i].first) {
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                val radius = getAdaptiveRadius(infoContainerWidth, SizeType.SMALL)
                cornerRadii = floatArrayOf(radius, radius, 0f, 0f, 0f, 0f, radius, radius)
                setColor(if (color == "&") "#BFDFDFDF".toColorInt() else {color.toColorInt()})
            }
            val checkIcoContainer = ConstraintLayout(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    infoContainerHeight,
                    infoContainerHeight
                )
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                background = drawable
                layoutParams = layoutparams1
                id = View.generateViewId()
            }
            val checkIcoView = ImageView(context).apply {
                val icoSize = round(infoContainerHeight.toFloat() / 1.46f).toInt()
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    icoSize,
                    icoSize
                )
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams = layoutparams1
                setImageResource(R.drawable.check_ico)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            checkIcoContainer.addView(checkIcoView)
            infoContainer.addView(checkIcoContainer)
            val lp1 = infoText.layoutParams as ConstraintLayout.LayoutParams
            lp1.startToEnd = checkIcoContainer.id
            lp1.startToStart = ConstraintLayout.LayoutParams.UNSET
            infoText.layoutParams = lp1
        }
        infoContainer.addView(infoText)
        if (addClickListeners) {
            infoContainer.setOnClickListener {
                onClick(genreList[i].second)
            }
        }
        infoContainersList.add(Triple(infoContainer, infoContainerWidth, genreList[i]))
    }

    // Нужно, чтобы поставить тех. инфу в самое начало списка жанров
    var genreAge = Triple(ConstraintLayout(context), 0, Pair(false, Genre.Detective))
    var genreYear = Triple(ConstraintLayout(context), 0, Pair(false, Genre.Detective))
    var genreSezon = Triple(ConstraintLayout(context), 0, Pair(false, Genre.Detective))
    var genreEpisodes = Triple(ConstraintLayout(context), 0, Pair(false, Genre.Detective))
    // Создаём список жанров (сначала самые маленькие по ширине)
    val icl: MutableList< Triple<ConstraintLayout, Int, Pair<Boolean, Genre>>> = infoContainersList
    var iclSorted = mutableListOf< Triple<ConstraintLayout, Int, Pair<Boolean, Genre>>>()  // Список, который будем использовать потом (сортированный список icl(он же infoContainersList))
    val infoGenresNeedToShowList = mutableListOf< Triple<ConstraintLayout, Int, Pair<Boolean, Genre>>>()

    // Наполняем список iclSorted
    while (icl.isNotEmpty()) {
        var minWidth = 10000000
        var objWithMinWidthIndex = -1
        val removeIndexs = mutableListOf<Int>()
        for (i in 0 until icl.size) {
            val obj = icl[i]
            if (obj.third.second == Genre.Age) {
                genreAge = obj
                removeIndexs.add(i)
            }
            else if (obj.third.second == Genre.Year) {
                genreYear = obj
                removeIndexs.add(i)
            }
            else if (obj.third.second == Genre.Episodes) {
                genreEpisodes = obj
                removeIndexs.add(i)
            }
            else if (obj.third.second == Genre.Sezon) {
                genreSezon = obj
                removeIndexs.add(i)
            }
            else if ((obj.second + if (obj.third.first) {0} else if (considerSelectedState && !obj.third.first) {infoContainerHeight} else {0}) < minWidth){
                objWithMinWidthIndex = i
                minWidth = (obj.second + if (obj.third.first) {0} else if (considerSelectedState && !obj.third.first) {infoContainerHeight} else {0})
            }
        }
        if (objWithMinWidthIndex != -1) {
            iclSorted.add(icl[objWithMinWidthIndex])
            removeIndexs.add(objWithMinWidthIndex)
        }
        removeIndexs.sortDescending()
        for (i in 0 until removeIndexs.size) {
            icl.removeAt(removeIndexs[i])
        }
    }

    // Добавляем тех. инфу в начало, если она есть
    if (genreAge.second != 0) {
        infoGenresNeedToShowList.add(genreAge)
    }
    if (genreYear.second != 0) {
        infoGenresNeedToShowList.add(genreYear)
    }
    if (genreSezon.second != 0) {
        infoGenresNeedToShowList.add(genreSezon)
    }
    if (genreEpisodes.second != 0) {
        infoGenresNeedToShowList.add(genreEpisodes)
    }
    iclSorted = (infoGenresNeedToShowList + iclSorted) as MutableList< Triple<ConstraintLayout, Int, Pair<Boolean, Genre>>>

    // Добавляем жанры (у нас уже есть view жанров, мы просто правильно их привязываем друг к другу и при необходимости добавляем кнопку "показать всё")
    var lastFirstViewId = 0
    var lastViewId = 0
    var sumHeight = 0
    var sumWidth = 0
    val showAllInfoViewText = TextView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        text = if (!addShowAllButton) "" else resources.getString(R.string.showAll)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, infoTextSize)
        this.typeface = infoTextFont
        includeFontPadding = false
        layoutParams = layoutparams1
        setTextColor("#DFDFDF".toColorInt())
        val maxTextWidth = round((widthh - if (considerSelectedState) infoContainerHeight else 0).toFloat() / 1.65f).toInt()
        maxWidth = maxTextWidth
    }
    showAllInfoViewText.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(infoTextHeight, View.MeasureSpec.EXACTLY)
    )
    val showAllInfoViewTextWidth = showAllInfoViewText.measuredWidth
    val showAllInfoViewWidth = showAllButtonWidth ?: round(showAllInfoViewTextWidth.toFloat() * 1.65f).toInt()
    val showAllInfoView = ConstraintLayout(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            showAllInfoViewWidth,
            infoContainerHeight
        )
        layoutParams = layoutparams1
        background = if (!addShowAllButton) {null} else {createGradientStrokeDrawable("#DFDFDF".toColorInt(), "#DFDFDF".toColorInt(), 4, getAdaptiveRadius(showAllInfoViewWidth, SizeType.SMALL), floatArrayOf(0f,1f))}
        tag = "show_all_info_button"
        id = View.generateViewId()
    }
    showAllInfoView.addView(showAllInfoViewText)

    var linesAmount = 1
    var shr = infoContainerHeight
    while (true) {
        if (shr + infoContainerHeight + marginBetweenInfoElements > heightt) {
            break
        }
        else {
            shr += infoContainerHeight + marginBetweenInfoElements
            linesAmount += 1
        }
    }

    val maxInfoContainersWidth = (widthh * linesAmount) - marginBetweenInfoElements - showAllInfoViewWidth
    var allSumWidth = 0

    for (i in iclSorted) {
        val newId = View.generateViewId()
        i.first.id = newId
        val layoutparams1 = ConstraintLayout.LayoutParams(
            i.second,
            infoContainerHeight
        )
        if (sumWidth + i.second + marginBetweenInfoElements + if (considerSelectedState && !i.third.first) {infoContainerHeight} else {0} <= widthh && allSumWidth + i.second + marginBetweenInfoElements + if (considerSelectedState && !i.third.first) {infoContainerHeight} else {0} <= maxInfoContainersWidth) {
            if (lastViewId == 0) {
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                if (lastFirstViewId == 0) {
                    layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                }
                else {
                    layoutparams1.topToBottom = lastFirstViewId
                    layoutparams1.setMargins(0,marginBetweenInfoElements,0,0)
                }
                lastFirstViewId = newId
                sumHeight += marginBetweenInfoElements + infoContainerHeight
            }
            else {
                layoutparams1.startToEnd = lastViewId
                layoutparams1.topToTop = lastViewId
                layoutparams1.setMargins(marginBetweenInfoElements,0,0,0)
            }
            sumWidth += i.second + marginBetweenInfoElements + if (considerSelectedState && !i.third.first) {infoContainerHeight} else {0}
            allSumWidth += i.second + marginBetweenInfoElements + if (considerSelectedState && !i.third.first) {infoContainerHeight} else {0}
            lastViewId = newId
        }
        else if (allSumWidth + i.second + marginBetweenInfoElements + if (considerSelectedState && !i.third.first) {infoContainerHeight} else {0} + (widthh-sumWidth) > maxInfoContainersWidth) {
            val lp1 = showAllInfoView.layoutParams as ConstraintLayout.LayoutParams
            if (sumWidth + marginBetweenInfoElements + showAllInfoViewWidth > widthh) {
                lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                if (lastFirstViewId == 0) {
                    lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                }
                else {
                    lp1.topToBottom = lastFirstViewId
                    lp1.setMargins(0,marginBetweenInfoElements,0,0)
                }
                lastFirstViewId = showAllInfoView.id
                sumWidth = showAllInfoViewWidth + marginBetweenInfoElements
                sumHeight += infoContainerHeight + marginBetweenInfoElements
            }
            else {
                if (lastViewId == 0) {
                    lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    if (lastFirstViewId == 0) {
                        lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    else {
                        lp1.topToBottom = lastFirstViewId
                        lp1.setMargins(0,marginBetweenInfoElements,0,0)
                    }
                    lastFirstViewId = showAllInfoView.id
                    sumHeight += marginBetweenInfoElements + infoContainerHeight
                    sumWidth = showAllInfoViewWidth + marginBetweenInfoElements
                }
                else {
                    lp1.startToEnd = lastViewId
                    lp1.topToTop = lastViewId
                    lp1.setMargins(marginBetweenInfoElements,0,0,0)
                    sumWidth += marginBetweenInfoElements + showAllInfoViewWidth
                }
            }
            lastViewId = showAllInfoView.id
            container.addView(showAllInfoView)
            break
        }
        else {
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            if (lastFirstViewId == 0) {
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }
            else {
                layoutparams1.topToBottom = lastFirstViewId
                layoutparams1.setMargins(0,marginBetweenInfoElements,0,0)
            }
            lastFirstViewId = newId
            lastViewId = newId
            allSumWidth += i.second + if (considerSelectedState && !i.third.first) {infoContainerHeight} else {0} + marginBetweenInfoElements + (widthh-sumWidth)
            sumWidth = i.second + if (considerSelectedState && !i.third.first) {infoContainerHeight} else {0} + marginBetweenInfoElements
            sumHeight += infoContainerHeight + marginBetweenInfoElements
        }
        i.first.layoutParams = layoutparams1
        container.addView(i.first)
    }
    sumHeight -= if (sumHeight != 0) marginBetweenInfoElements else 0
    val lp1 = container.layoutParams as ConstraintLayout.LayoutParams
    lp1.height = sumHeight
    container.layoutParams = lp1
    return createGridOfGenresReturn(container, sumHeight, sumWidth)
}
fun createGradientStrokeDrawable(startColor: Int, endColor: Int, strokeWidth1: Int, cornerRadius: Float = 0f, positions: FloatArray): Drawable {
    return object : Drawable() {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = strokeWidth1.toFloat()
        }
        private val rectF = RectF()

        override fun draw(canvas: Canvas) {
            val bounds = bounds
            val w = bounds.width().toFloat()

            // Создаем градиент для обводки (слева направо)
            val shader = LinearGradient(
                0f, 0f,           // начало (левый верх)
                w, 0f,            // конец (правый верх)
                intArrayOf(startColor, endColor),
                positions,
                Shader.TileMode.CLAMP
            )

            paint.shader = shader

            // Рисуем прямоугольник с обводкой
            rectF.set(
                bounds.left.toFloat() + strokeWidth1 / 2f,
                bounds.top.toFloat() + strokeWidth1 / 2f,
                bounds.right.toFloat() - strokeWidth1 / 2f,
                bounds.bottom.toFloat() - strokeWidth1 / 2f
            )

            if (cornerRadius > 0) {
                canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
            } else {
                canvas.drawRect(rectF, paint)
            }
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
            invalidateSelf()
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
            invalidateSelf()
        }

        override fun getOpacity() = PixelFormat.TRANSLUCENT
    }
}
fun createFlatGrid(context: Context, startsInfo: createFlatGridInput, widthh: Int, heightt: Int, changeImage: (Int) -> Unit): ConstraintLayout {
    val containerToReturn = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            widthh,
            heightt
        )
    }
    val scrollContainer = ScrollView(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            widthh,
            heightt
        )
    }
    val container = LinearLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            widthh,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        orientation = LinearLayout.VERTICAL
    }
    val elementHeight = round(120f*density).toInt()
    when (startsInfo) {
        is createFlatGridInput.EditEpisodes -> {
            var list = startsInfo.info as MutableList<episodeInfo>
            val adapterr = flatGridOfEditEpisodesAdapter(context, list, widthh, elementHeight, changes = {list = it
                startsInfo.callback(it)}, changeImage = {changeImage(it)})
            val recyclerView = RecyclerView(context).apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    widthh,
                    heightt
                )
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = adapterr
                addItemDecoration(spaceItemDecoration(spaceItemDecorationInput(listOf(0,round(30f*density).toInt(),0,0), listOf(0,0,0,0))))
            }
            val callback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, // Разрешаем движение вверх/вниз
                0 // Нам не нужно смахивание в сторону
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    Collections.swap(list, fromPos, toPos)
                    recyclerView.adapter?.notifyItemMoved(fromPos, toPos)
                    return true
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)
                    startsInfo.callback(list)
                    adapterr.notifyDataSetChanged()
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

                override fun isLongPressDragEnabled(): Boolean = false
            }

            val touchHelper = ItemTouchHelper(callback)
            adapterr.touchHelper = touchHelper
            touchHelper.attachToRecyclerView(recyclerView)
            containerToReturn.addView(recyclerView)
        }
        is createFlatGridInput.Episodes -> {}
        is createFlatGridInput.Music -> {}
    }
    return containerToReturn
}
fun hideKeyboardd(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
@SuppressLint("InternalInsetResource")
fun getStatusBarHeight(context: Context): Int {
    var result = 0
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = context.resources.getDimensionPixelSize(resourceId)
    }
    return result
}
fun calculateCardWidth(items: List<objectData2>, font: Typeface?, textSizee: Float, showName: Boolean, namePosition: Int?, position: Int): Int {
    var width: Int
    var cardHeight: Int
    var cardWidth = 0

    // Рассчитываем размеры карточки в зависимости от её типа и отображаемых элементов (имя, автор). Скрипт из createCard
    val size1 = cardScaleCalcFree(items, 50, 30, items[position].width, items[position].height, screenWidth)
    if (items[position].author != null && items[position].name != null && showName && namePosition == 0) {
        // Карточка с именем и автором под ней
        cardHeight = size1.second - optimizeText(items[position].name!!, size1.first, textSizee, false, font).totalHeight * 3
        cardWidth = (cardHeight.toFloat() * (size1.first.toFloat() / size1.second)).toInt()
    } else if (items[position].author == null && items[position].name != null && showName && namePosition == 0) {
        // Карточка только с именем под ней
        cardHeight = size1.second - optimizeText(items[position].name!!, size1.first, textSizee, false, font).totalHeight * 2
        cardWidth = (cardHeight.toFloat() * (size1.first.toFloat() / size1.second)).toInt()
    } else if ((!showName) || (showName && namePosition == 1)) {
        // Имя внутри или скрыто
        cardWidth = size1.first
        cardHeight = size1.second
    }
    var res2 = cardWidth
    if (showName && namePosition == 1) {
        res2 = size1.first
    }
    width = res2
    return width
}
fun findVideoPlayerLayer(): Int? {
    var res: Int? = null
    for (i in 0 until layersList.size) {
        if (layersList[i] is Layer.VideoPlayer) {
            res = i
        }
    }
    return res
}
fun changeOrientation(context: Context, isItShouldBeUnspecified: Boolean) {
    val activity = context as? Activity ?: return
    if (!isItShouldBeUnspecified) {
        if (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
    else {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}
fun toggleSystemBars(show: Boolean, context: Context) {
    val window = (context as? Activity)?.window ?: return
    val controller = WindowCompat.getInsetsController(window, window.decorView)

    if (show) {
        controller.show(WindowInsetsCompat.Type.systemBars())
    } else {
        // Прячем всё. BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE позволяет
        // временно вызвать бары свайпом, не ломая разметку.
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
}


var currentPendingKeyForFiles: String? = null
var currentPendingPositionForCreateCardChangeImage: Int = 0
class MainActivity : AppCompatActivity() {
    private lateinit var displayManager: DisplayManager
    private lateinit var recycler: RecyclerView
    private lateinit var animePage: RecyclerView
    private var lastRotation = -1
    private val viewModel: MainViewModel by viewModels {
        val app = application as App
        MultiViewModelFactory(app.dao, app.groupsDao, app.settingsManager, resources.displayMetrics.density)
    }
    private val resultSenderViewModel: ResultSenderViewModel by viewModels()
    val pickFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val uriString = it.toString()
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                // 2. Просим систему закрепить этот Uri за нами "пожизненно"
                contentResolver.takePersistableUriPermission(it, takeFlags)

            } catch (e: SecurityException) {
                Log.e("STORAGE", "Не удалось получить постоянный доступ", e)
            }
            val tag = currentPendingKeyForFiles
            val pos = currentPendingPositionForCreateCardChangeImage
            if (tag != null) {
                resultSenderViewModel.sendResult(tag, when(tag) {
                    ResultKeys.CREATE_CARD_CHANGE_IMAGE -> {Pair(uriString,pos)}
                    else -> {uriString}
                })
            }
        }
    }
    val pickFiles = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val resultList = mutableListOf<String>()

            uris.forEach { uri ->
                try {
                    contentResolver.takePersistableUriPermission(uri, takeFlags)
                    resultList.add(uri.toString())
                } catch (e: SecurityException) {
                    Log.e("STORAGE", "Ошибка доступа для одного из файлов", e)
                }
            }
            val tag = currentPendingKeyForFiles
            if (tag != null) {
                resultSenderViewModel.sendResult(tag, resultList)
            }
        }
    }
    @SuppressLint("ResourceAsColor", "ResourceType", "ClickableViewAccessibility",
        "UseCompatLoadingForDrawables"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.mainactivity)
        statusBarHeight = getStatusBarHeight(this)
        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels
        baseDensity = resources.displayMetrics.density
        screenWidthDp = round(screenWidth.toFloat() / baseDensity).toInt()
        screenHeightDp = round(screenHeight.toFloat() / baseDensity).toInt()
        density = baseDensity / 2.625f
        reversDensity = 2.625f / baseDensity

        steps = listOf(round(512f*density),round(256f*density),round(192f*density),round(128f*density),round(96f*density),round(64f*density), round(48f*density), round(40f*density), round(36f*density), round(32f*density), round(24f*density), round(20f*density), round(16f*density), round(12f*density))  // Это список возможных textSize
        val container = findViewById<ViewGroup>(R.id.main)
        val bsd = bottomSheetDialogFactory(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Для Android 13+ (API 33+)
            requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 101)
        }
        else {
            // Для Android 12 и ниже
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
        }
        if (!alreadyCreated) {
            // База данных
            viewModel.insert()
            viewModel.setChildsShowNameInSezonsRecycler(true)
            baseBlurEffectForBloobs = RenderEffect.createBlurEffect(
                150f, 150f,
                Shader.TileMode.MIRROR
            )
            dotDrawables = createDotDrawables()
            orientationOld = resources.configuration.orientation
            orientationNow = resources.configuration.orientation
            genreNames = mapOf(
                Pair(Genre.Drama, resources.getString(R.string.Drama)),
                Pair(Genre.Comedy, resources.getString(R.string.Comedy)),
                Pair(Genre.Romance, resources.getString(R.string.Romance)),
                Pair(Genre.EverydayLife, resources.getString(R.string.EverydayLife)),
                Pair(Genre.School, resources.getString(R.string.School)),
                Pair(Genre.Psychological, resources.getString(R.string.Psychological)),
                Pair(Genre.Shonen, resources.getString(R.string.Shonen)),
                Pair(Genre.ActionMovie, resources.getString(R.string.ActionMovie)),
                Pair(Genre.MartialArts, resources.getString(R.string.MartialArts)),
                Pair(Genre.Action, resources.getString(R.string.Action)),
                Pair(Genre.Adventures, resources.getString(R.string.Adventures)),
                Pair(Genre.Shoujo, resources.getString(R.string.Shoujo)),
                Pair(Genre.Fantasy, resources.getString(R.string.Fantasy)),
                Pair(Genre.Isekai, resources.getString(R.string.Isekai)),
                Pair(Genre.ScienceFiction, resources.getString(R.string.ScienceFiction)),
                Pair(Genre.Cyberpunk, resources.getString(R.string.Cyberpunk)),
                Pair(Genre.Fantastic, resources.getString(R.string.Fantastic)),
                Pair(Genre.Supernatural, resources.getString(R.string.Supernatural)),
                Pair(Genre.PostApocalypse, resources.getString(R.string.PostApocalypse)),
                Pair(Genre.Detective, resources.getString(R.string.Detective)),
                Pair(Genre.Thriller, resources.getString(R.string.Thriller)),
                Pair(Genre.Horrors, resources.getString(R.string.Horrors)),
                Pair(Genre.Mysticism, resources.getString(R.string.Mysticism)),
                Pair(Genre.Etty, resources.getString(R.string.Etty)),
                Pair(Genre.Harem, resources.getString(R.string.Harem)),
                Pair(Genre.Age, "Возраст"),
                Pair(Genre.Year, "Год"),
                Pair(Genre.Sezon, "Сезон года"),
                Pair(Genre.Episodes, "Кол-во эпизодов")

            )
            musicGenreNames = mapOf(
                Pair(MusicGenre.Rock, resources.getString(R.string.Rock)),
                Pair(MusicGenre.Pop, resources.getString(R.string.Pop)),
                Pair(MusicGenre.HipHop, resources.getString(R.string.HipHop)),
                Pair(MusicGenre.Electronic, resources.getString(R.string.Electronic)),
                Pair(MusicGenre.Metal, resources.getString(R.string.Metal)),
                Pair(MusicGenre.Country, resources.getString(R.string.Country)),
                Pair(MusicGenre.Jazz, resources.getString(R.string.Jazz)),
                Pair(MusicGenre.Classical, resources.getString(R.string.Classical)),
                Pair(MusicGenre.LoFi, resources.getString(R.string.LoFi)),
                Pair(MusicGenre.Ambient, resources.getString(R.string.Ambient))
            )
            alreadyCreated = true
        }

        // Base blobs init
        blob12OvalSize = round(screenWidth.toFloat() * 1.09f).toInt()
        blob12FullSize = round((blob12OvalSize.toFloat() * delitRad) / 2f).toInt()

        blob3OvalSize = round(screenWidth.toFloat() * 1.022f).toInt()
        blob3FullSize = round((blob3OvalSize.toFloat() * delitRad) / 2f).toInt()

        blob1MarginEnd = round(screenWidth.toFloat() / 3.303f).toInt() - ((blob12FullSize.toFloat() - blob12OvalSize.toFloat()) / 2f).toInt()
        blob1MarginTop = -(floor(blob12FullSize.toFloat() / 2f).toInt())

        blob2MarginStart = round(screenWidth.toFloat() / 3.54f).toInt() - ((blob12FullSize.toFloat() - blob12OvalSize.toFloat()) / 2f).toInt()
        blob2MarginTop = -(ceil(screenWidth.toFloat() / 3.07f).toInt()) - ((blob12FullSize.toFloat() - blob12OvalSize.toFloat()) / 2f).toInt()

        blob3MarginEnd = round(screenWidth.toFloat() / 4.84f).toInt() - ((blob12FullSize.toFloat() - blob12OvalSize.toFloat()) / 2f).toInt()
        blob3MarginTop = -round(screenWidth.toFloat() / 18.62f).toInt() - ((blob12FullSize.toFloat() - blob12OvalSize.toFloat()) / 2f).toInt()

        baseBlob1 = blobInit(blob12FullSize, "#B694FF")
        baseBlob2 = blobInit(blob12FullSize, "#97FF9A")
        baseBlob3 = blobInit(blob3FullSize, "#FFF374")
        // Base blobs init finish

        // Create base blobs
        val blob1 = ImageView(this).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                blob12FullSize,
                blob12FullSize
            )
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,blob1MarginTop, blob1MarginEnd,0)
            layoutParams = layoutparams1
            val newId = View.generateViewId()
            id = newId
            baseblob1Id = newId
            if (newId !in blobsNeedToHideOnAlbomOrientationIdsList) {
                blobsNeedToHideOnAlbomOrientationIdsList.add(newId)
            }
            background = baseBlob1
        }
        val blob2 = ImageView(this).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                blob12FullSize,
                blob12FullSize
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(blob2MarginStart, blob2MarginTop, 0,0)
            layoutParams = layoutparams1
            val newId = View.generateViewId()
            id = newId
            baseblob2Id = newId
            if (newId !in blobsNeedToHideOnAlbomOrientationIdsList) {
                blobsNeedToHideOnAlbomOrientationIdsList.add(newId)
            }
            background = baseBlob2
        }
        val blob3 = ImageView(this).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                blob3FullSize,
                blob3FullSize
            )
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0, blob3MarginTop, blob3MarginEnd,0)
            layoutParams = layoutparams1
            val newId = View.generateViewId()
            id = newId
            baseblob3Id = newId
            if (newId !in blobsNeedToHideOnAlbomOrientationIdsList) {
                blobsNeedToHideOnAlbomOrientationIdsList.add(newId)
            }
            background = baseBlob3
        }
        blob1.setRenderEffect(baseBlurEffectForBloobs)
        blob2.setRenderEffect(baseBlurEffectForBloobs)
        blob3.setRenderEffect(baseBlurEffectForBloobs)
        container.addView(blob2)
        container.addView(blob3)
        container.addView(blob1)

        // Регистрируем поворот экрана для функции onRotationChanged()
        displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
        val handler = Handler(Handler.Callback {
            val currentRotation = windowManager.defaultDisplay.rotation
            if (currentRotation != lastRotation) {
                lastRotation = currentRotation
                onRotationChanged()
            }
            true
        })
        displayManager.registerDisplayListener(
            object : DisplayManager.DisplayListener {
                override fun onDisplayAdded(displayId: Int) {}
                override fun onDisplayRemoved(displayId: Int) {}
                override fun onDisplayChanged(displayId: Int) {
                    // Это вызовется на ЛЮБОЙ поворот, включая 180°
                    handler.sendEmptyMessage(0)
                }
            },
            handler
        )
        onRotationChanged()

        // Адаптеры
        val adapter1 = carouselsAdapter(this, addCardToCarousel = {addCardToCarousel(it)}, clickOnItem = { showPage(infoOfPageToShow.infoOfAnimePage(it.id, true)) })
        val animePageSezonsPageAdapter = animePageSezonsPageAdapter(this, clickOnCard = { showPage(infoOfPageToShow.infoOfAnimePage(it.id, true)) } )
        val animePageAdapter = animePageAdapter(this, showShowAllText = { text, size, callback -> showShowAllText(text, size, callback)}, animePageSezonsPageAdapter, openVideo = {showPage(infoOfPageToShow.infoOfVideoPlayer(it,true))})
        val wrapper = objectData2(
            id = -1,
            page = 0,
            position = 0,
            name = null,
            author = null,
            showName = false,
            namePosition = 0,
            width = null,
            height = null,
            paddingVertical = null,
            paddingHorizontal = null,
            marginBetweenElementsHorizontal = null,
            marginBetweenElementsVertical = null,
            layoutType = 0,
            childs = listOf(),
            showDovodchikDots = false,
            dovodchik = false,
            maxObjectsInOneLine = null,
            maxLines = null,
            alreadyWatched = 0.toLong(),
            elementType = ElementType.Anime,
            length = 0.toLong(),
        )

        // Создаём главный recycler view
        recycler = RecyclerView(this).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                screenHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = adapter1
            val newId = View.generateViewId()
            id = newId
            mainPageRecyclerId = newId
            addItemDecoration(spaceItemDecoration(spaceItemDecorationInput(listOf(0,round(100f*density).toInt(),0,0), listOf(0, statusBarHeight,0,0))))
        }
        container.addView(recycler)

        var mainHomePageLayer: Layer.MainPage?
        if (findMainPageLayerByPageId(0) == null) {
            layersList.add(Layer.MainPage(lastElevation+1, 0, mutableMapOf<Int,Int>(), mutableMapOf<Int,Int>(), mutableMapOf<Int,Int>(), 0, 0, 0))
            lastElevation += 1
            mainHomePageLayer = layersList.findLast { it is Layer.MainPage } as? Layer.MainPage
        }
        else {
            mainHomePageLayer = layersList[layersList.indexOf(findMainPageLayerByPageId(0))] as? Layer.MainPage
        }

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recycler.layoutManager as LinearLayoutManager
                val firstVisible = layoutManager.findFirstVisibleItemPosition()
                if (mainHomePageLayer != null) {
                    mainHomePageLayer.mainRecyclerScrollPosition = firstVisible
                    mainHomePageLayer.mainRecyclerScrollPositionInPx += dy
                    if (mainHomePageLayer.mainRecyclerScrollPositionInPx in 0..(screenHeight.toFloat() / 7f).toInt()) {
                        val delitel = ((screenHeight.toFloat() / 7f).toInt()).toFloat() / 100f
                        val procenti = ((100 - (mainHomePageLayer.mainRecyclerScrollPositionInPx.toFloat() / delitel).toInt()).toFloat() / 100f)
                        changeBaseBlobsAlpha(procenti)
                    }
                    else {
                        changeBaseBlobsAlpha(0f)
                    }
                }
            }
        })
        animePage = RecyclerView(this).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = animePageAdapter
            visibility = View.GONE
            background = resources.getDrawable(R.drawable.activity_main_nav_header_background)
            val newId = View.generateViewId()
            id = newId
            animePageRecyclerId = newId
            itemAnimator = null
        }
        animePage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layer = findLayerByLayerObjectId(animePageObjectId).first
                if (layer != null) {
                    if (layer is Layer.AnimePage) {
                        layer.mainRecyclerScrollPositionInPx += dy
                    }
                }
            }
        })
        container.addView(animePage)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiDataFlow.collect {
                            newList -> run {
                        objectsList = newList
                        adapter1.submitList(newList)
                        if (newList.isNotEmpty()) {
                            val page = newList[0].page
                            var qq = 0
                            var mainHomePageLayer: Layer.MainPage? = null
                            if (findMainPageLayerByPageId(page) == null) {
                                layersList.add(Layer.MainPage(lastElevation+1, 0, mutableMapOf<Int,Int>(), mutableMapOf<Int,Int>(), mutableMapOf<Int,Int>(), 0, 0, 0))
                                lastElevation += 1
                                mainHomePageLayer = layersList.findLast { it is Layer.MainPage } as? Layer.MainPage
                            }
                            if (mainHomePageLayer != null) {
                                qq = mainHomePageLayer.mainRecyclerScrollPosition
                                mainHomePageLayer.mainRecyclerScrollPositionInPx = 0
                            }

                            recycler.post {
                                var scrollV = 0
                                val r = if (objectsList.size < qq) {objectsList.size} else {qq}   // qq - Это mainRecyclerScrollPosition с.м чуть выше от recycler.addOnScrollListener
                                for (i in 0 until r) {
                                    var pdV = objectsList[i].paddingVertical
                                    if (pdV == null) {
                                        pdV = 100
                                    }
                                    val textViewHeight = optimizeText(if (objectsList[i].name != null) {objectsList[i].name!!} else {"Молчаливая ведьма"}, 200, 50f, false, null, 1).totalHeight
                                    val itemHeight = calcRecyclerViewHeight(objectsList, i) + pdV + 15 + if (textViewHeight > 72) {textViewHeight} else {72} + if (objectsList[i].dovodchik && objectsList[i].showDovodchikDots && objectsList[i].childs.isNotEmpty() && (objectsList[i].layoutType == null || objectsList[i].layoutType == 1)) {45} else {0}  // 15 - Это marginTop у recycler view 72 - это высота кнопок add и edit  45 - это высота точек
                                    scrollV += itemHeight
                                }
                                recycler.scrollTo(0,scrollV)
                            }
                        }
                        viewModel.updateAnimePageAdapter(animePageObjectId, newList)
                    }
                    }
                }
                launch {
                    viewModel.animePageFlow.collect {
                        run {
                            val obj = it.animeData
                            groupsList = it.groups
                            animeSezonsSettingsState = it.settingsState

                            var ls = animePageSezonsPageAdapter.currentList as MutableList<animePageSezonsAdapterListFormat>
                            val objects = viewModel.getGroupObjectsByIdOfOne(animePageObjectId, objectsList, groupsList)
                            wrapper.childs = objects
                            if (ls.isNotEmpty()) {
                                ls[0].settingsState = animeSezonsSettingsState
                                ls[0].obj = wrapper
                            }
                            else {
                                ls = mutableListOf(animePageSezonsAdapterListFormat(wrapper, animeSezonsSettingsState))
                            }
                            animePageAdapter.submitList(if (obj != null) {listOf(obj)} else {null})
                            animePageSezonsPageAdapter.submitList(ls)
                            var layer: Layer.AnimePage? = null
                            var qq = 0
                            if (animePageObjectId != -1L && findLayerByLayerObjectId(animePageObjectId).first == null) {
                                layersList.add(Layer.AnimePage(lastElevation+1, 0, animePageObjectId, 0))
                                lastElevation += 1
                                layer = layersList.findLast { it is Layer.AnimePage } as? Layer.AnimePage
                            }
                            if (layer != null) {
                                qq = layer.mainRecyclerScrollPositionInPx
                            }
                            animePage.scrollTo(0, qq)
                        }
                    }
                }
                launch {
                    bsdFlow.collect {
                        bsd.createDialog(it, callback = { tag -> bsdButtonActions(tag)})
                    }
                }
                launch {
                    resultSenderViewModel.results.collect { (key, data) -> run {
                        when (key) {
                            ResultKeys.VIDEO_PLAYER_EDIT_EPISODE_ALREADY_WATCHED -> {
                                val dataa = data as Pair<Long, Long>
                                viewModel.editAlreadyWatched(dataa.first,dataa.second)
                            }
                        }
                    }
                    }
                }
            }
        }

        // При жесте назад
        onBackPressedDispatcher.addCallback(this) {
            hideLayer()
        }
        restoreLayer()
    }

    private var playerSubscriptionJob: kotlinx.coroutines.Job? = null
    fun openAnimeVideoPlayer(id: Long) {
        val contextt = this
        playerSubscriptionJob?.cancel()
        var isPlaying = true
        var playbackSpeed = 1f
        var isControllerVisible = true
        val vdpll = findVideoPlayerLayer()
        var l: Layer.VideoPlayer? = null
        if (vdpll != null) {
            l = layersList[vdpll] as Layer.VideoPlayer
            isPlaying = l.isPlaying
            playbackSpeed = l.playbackSpeed
            isControllerVisible = l.isControllerVisible
        }

        val mainContainer = findViewById<ViewGroup>(R.id.main)
        val playerLayer = AnimeVideoPlayer(this,resultSenderViewModel)
        playerLayer.tag = "video_player"
        val layoutparams1 = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        playerLayer.layoutParams = layoutparams1
        playerLayer.elevation = 10000f
        playerLayer.z = 10000f
        playerLayer.setBackgroundColor("#000000".toColorInt())
        mainContainer.addView(playerLayer)

        playerSubscriptionJob = this.lifecycleOwner?.lifecycleScope?.launch {
            val info = viewModel.getAllInfoByEpisodeId(id)
            withContext(Dispatchers.Main) {
                resultSenderViewModel.sendResult(ResultKeys.VIDEO_PLAYER_ANIME_EPISODE_INFORMATION, info)
                resultSenderViewModel.sendResult(ResultKeys.VIDEO_PLAYER_IS_PLAYING, isPlaying)
                resultSenderViewModel.sendResult(ResultKeys.VIDEO_PLAYER_PLAYBACK_SPEED, playbackSpeed)
                resultSenderViewModel.sendResult(ResultKeys.VIDEO_PLAYER_IS_CONTROLLER_VISIBLE, isControllerVisible)
                val uri = info.second[info.third].link?.contentPath
                if (uri != null) {
                    playerLayer.playEpisode(uri, info.second[info.third].alreadyWatched)
                }
            }
            resultSenderViewModel.results.collect { (key,data) -> run {
                when (key) {
                    ResultKeys.VIDEO_PLAYER_CLOSE_FORM_PLAYER -> run {hideLayer() }
                    ResultKeys.VIDEO_PLAYER_CHANGE_IS_PLAYING -> run { l?.isPlaying = data as Boolean}
                    ResultKeys.VIDEO_PLAYER_CHANGE_PLAYBACK_SPEED -> run {l?.playbackSpeed = data as Float}
                    ResultKeys.VIDEO_PLAYER_CHANGE_IS_CONTROLLER_VISIBLE -> run {l?.isControllerVisible = data as Boolean}
                }
            }
            }
        }
        // Не забываем повернуть экран в ландшафт!
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
    }
    fun closeVideoPlayer() {
        val mc = findViewById<ViewGroup>(R.id.main)
        val vp: View? = mc.findViewWithTag("video_player")
        if (vp != null) {
            mc.removeView(vp)
        }
    }
    fun getVideoDuration(uriString: String): Long {
        val retriever = MediaMetadataRetriever()
        try {
            // Устанавливаем источник данных через Uri
            retriever.setDataSource(this, uriString.toUri())

            // Извлекаем строку с длительностью
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            return floor((time?.toLong() ?: 0L) / 1000f).toLong()
        } catch (e: Exception) {
            Log.e("VIDEO_INFO", "Не удалось получить длительность: ${e.message}")
            return 0L
        } finally {
            // Обязательно освобождаем ресурсы!
            retriever.release()
        }
    }
    fun bsdButtonActions(tag: BsdButtonsTags) {
        when (tag) {
            BsdButtonsTags.animePage_extraButton_changeAllEpisodesWatchedMark -> {}
        }
    }
    fun restoreLayer() {
        for (i in layersList) {
            when (i) {
                is Layer.MainPage -> {}
                is Layer.AnimePage -> {showPage(infoOfPageToShow.infoOfAnimePage(i.layoutObjId, false))}
                is Layer.OverLay -> {showPage(infoOfPageToShow.infoOfOverlayLayer(i.info, false))}
                is Layer.VideoPlayer -> {showPage(infoOfPageToShow.infoOfVideoPlayer(i.id, false))}
            }
        }
    }
    fun hideLayer() {
        if (layersList.isNotEmpty()) {
            Log.d("LS", "$layersList")
            var layer = layersList.last()
            for (i in 0 until layersList.size) {
                val la = layersList[i]
                when (la) {
                    is Layer.MainPage -> {layer = la}
                    is Layer.AnimePage -> {layer = la}
                    is Layer.OverLay -> {layer = la}
                    is Layer.VideoPlayer -> {layer = la}
                }
//                if (la.mode != LayerMode.Mini) {
//                    layer = la
//                }
            }
            if (layer != layer) { // Проверка на то, может ли быть мини-режим у данного слоя
                // Переход в мини-режим
            }
            else {
                val layersElevationList = mutableListOf<Int>()
                for (i in layersList) {
                    when (i) {
                        is Layer.MainPage -> {layersElevationList.add(i.elevation)}
                        is Layer.AnimePage -> {layersElevationList.add(i.elevation)}
                        is Layer.OverLay -> {layersElevationList.add(i.elevation)}
                        is Layer.VideoPlayer -> {layersElevationList.add(i.elevation)}
                    }
                }
                layersElevationList.sortBy { it }
                var layerElevation: Int
                when (layer) {
                    is Layer.MainPage -> {layerElevation = layer.elevation}
                    is Layer.AnimePage -> {layerElevation = layer.elevation}
                    is Layer.OverLay -> {layerElevation = layer.elevation}
                    is Layer.VideoPlayer -> {layerElevation = layer.elevation}
                }
                val previousLayerElevation = layersElevationList[if (layersElevationList.indexOf(layerElevation) > 0) {layersElevationList.indexOf(layerElevation)-1} else {layersElevationList.indexOf(layerElevation)}]
                if (previousLayerElevation == layerElevation) {
                    finish()
                }
                val previousLayer = findLayerByElevation(previousLayerElevation)
                if (previousLayer == null) {
                    finish()
                }
                else {
                    when (layer) {
                        is Layer.MainPage -> {}
                        is Layer.AnimePage -> {hidePage(pageTags.animePage)}
                        is Layer.OverLay -> {hidePage(layer.tag)}
                        is Layer.VideoPlayer -> {hidePage(pageTags.videoPlayer)}
                    }
                    when (previousLayer) {
                        is Layer.MainPage -> {}
                        is Layer.AnimePage -> {showPage(infoOfPageToShow.infoOfAnimePage(previousLayer.layoutObjId, false))}
                        is Layer.OverLay -> {}
                        is Layer.VideoPlayer -> {}
                    }
                }
                layersList.remove(layer)
                Log.d("LLL", "$layer")
            }
        }
        else {
            finish()
        }
    }
    fun selectFile(fileTypee: fileType, key: String, positionForCreateCardChangeImage: Int?) {
        val fileTypeString = when (fileTypee) {
            fileType.IMAGE -> "image/*"
            fileType.AUDIO -> "audio/*"
            fileType.VIDEO -> "video/*"
        }
        currentPendingKeyForFiles = key
        currentPendingPositionForCreateCardChangeImage = positionForCreateCardChangeImage ?: 0
        pickFile.launch(fileTypeString)
    }
    fun selectFiles(fileTypes: List<fileType>, key: String) {
        currentPendingKeyForFiles = key
        var fileTypesString = arrayOf<String>()
        for (i in fileTypes) {
            when (i) {
                fileType.IMAGE -> fileTypesString = fileTypesString.plus("image/*")
                fileType.AUDIO -> fileTypesString = fileTypesString.plus("audio/*")
                fileType.VIDEO -> fileTypesString = fileTypesString.plus("video/*")
            }
        }
        pickFiles.launch(fileTypesString)
    }
    @SuppressLint("ClickableViewAccessibility")
    fun showPage(info: infoOfPageToShow) {
        when (info) {
            is infoOfPageToShow.infoOfAnimePage -> {
                animePageObjectId = info.id
                viewModel.updateAnimePageAdapter(info.id, objectsList)
                animePage.visibility = View.VISIBLE
                if (info.isItNewLayer) {
                    layersList.add(Layer.AnimePage(lastElevation+1, 0, info.id, 0))
                    lastElevation += 1
                }
            }
            is infoOfPageToShow.infoOfOverlayLayer -> {
                when (info.info) {
                    is OverLayLayer.CreateAnimePage -> {
                        if (info.isItNewLayer) {
                            layersList.add(Layer.OverLay(lastElevation+1, pageTags.createAnimePage, info.info))
                        }
                        val mainContainer = findViewById<ViewGroup>(R.id.main)
                        var bannerImageData: ImageData? = null
                        val createAnimePageContainerView = createOvDialog.CreateAnimePage(this, info.info, resultSenderViewModel,
                            addCard = {
                                val createAnimePageContainerView: ConstraintLayout? = mainContainer.findViewWithTag("create_anime_page_container")
                                if (createAnimePageContainerView != null) {
                                    val sc = createAnimePageContainerView.findViewWithTag<ScrollView>("scroll_container")
                                    val con = sc.findViewWithTag<ConstraintLayout>("container")
                                    val nameInput = con.findViewWithTag<TextInputLayout>("name_input").getChildAt(0).findViewWithTag<TextInputEditText>("edit_text")
                                    val authorInput = con.findViewWithTag<ConstraintLayout>("author_input").getChildAt(0) as TextInputLayout
                                    val authorInputt = authorInput.findViewWithTag<TextInputEditText>("edit_text")
                                    val descriptionInput = con.findViewWithTag<TextInputLayout>("description_input").findViewWithTag<TextInputEditText>("edit_text")
                                    Log.d("RES", "name = ${nameInput.text}  author = ${authorInputt.text}  description = ${descriptionInput.text} genres = ${info.info.genreList} bannerImage = $bannerImageData episodes = $it")
//                                info.info.apply()
                                    val data = createCardApply(nameInput.text.toString(), bannerImageData, authorInputt.text.toString(), descriptionInput.text.toString(), info.info.genreList, it)
                                    resultSenderViewModel.sendResult(ResultKeys.CREATE_CARD_APPLY,data)
                                }
                            },
                            changeImage = { pos -> selectFile(fileType.IMAGE, ResultKeys.CREATE_CARD_CHANGE_IMAGE, pos) },
                            openGenreChoice = {
                                val fullGenreList = mutableListOf<Pair<Boolean, Genre>>()
                                for (i in genreNames) {
                                    var isActive = false
                                    for (j in info.info.genreList) {
                                        if (j == i.key) {
                                            isActive = true
                                        }
                                    }
                                    fullGenreList.add(Pair(isActive, i.key))
                                }
                                showPage(infoOfPageToShow.infoOfOverlayLayer(OverLayLayer.GenreChoice(fullGenreList, ResultKeys.CREATE_CARD_GENRE_CHOICE),true))
                            })
                        createAnimePageContainerView.tag = "create_anime_page_container"
                        createAnimePageContainerView.isFocusable = true
                        createAnimePageContainerView.isFocusableInTouchMode = true

                        val sc = createAnimePageContainerView.findViewWithTag<ScrollView>("scroll_container")
                        val con = sc.findViewWithTag<ConstraintLayout>("container")

                        val addEpisodeBlockContainer = con.findViewWithTag<ConstraintLayout>("add_episodes_block_container")
                        val addEpisodeButton = addEpisodeBlockContainer.findViewWithTag<ConstraintLayout>("add_episode_button")
                        addEpisodeButton.setOnClickListener {
                            selectFiles(listOf(fileType.VIDEO), ResultKeys.CREATE_CARD_ADD_EPISODES)
                        }
                        lifecycleScope.launch {
                            resultSenderViewModel.results.collect { (key, data) -> run {
                                when (key) {
                                    ResultKeys.CREATE_CARD_CHANGE_IMAGE -> run {
                                        val dataa = data as Pair<String, Int>
                                        val imageData = ImageData(ImageSource.DEVICE, dataa.first)
                                        val capcv: ConstraintLayout? = mainContainer.findViewWithTag("create_anime_page_container")
                                        if (capcv != null) {
                                            val sc = capcv.findViewWithTag<ScrollView>("scroll_container")
                                            val con = sc.findViewWithTag<ConstraintLayout>("container")
                                            if (dataa.second == -1) {
                                                val banner: ConstraintLayout = con.findViewWithTag("banner_container")
                                                val cardView = banner.findViewWithTag<CardView>("banner_card_view")
                                                val imageView = cardView.getChildAt(0) as ImageView
                                                val addIco = banner.findViewWithTag<ImageView>("image_container_add_ico")
                                                imageView.alpha = 1f
                                                imageView.loadImage(imageData)
                                                cardView.alpha = 1f
                                                addIco.alpha = 0f
                                                info.info.image = imageData
                                                banner.background = null
                                                bannerImageData = imageData
                                            }
                                            else {
                                                val aebc = con.findViewWithTag<ConstraintLayout>("add_episodes_block_container")
                                                val eebc = aebc.findViewWithTag<ConstraintLayout>("edit_episodes_container")
                                                val recycler = eebc.getChildAt(0) as RecyclerView
                                                val adapter = recycler.adapter as flatGridOfEditEpisodesAdapter
                                                adapter.changeEpisodeBanner(imageData,dataa.second)
                                            }
                                        }
                                    }

                                    ResultKeys.CREATE_CARD_ADD_EPISODES -> run {
                                        val dataa = data as List<String>
                                        for (i in dataa) {
                                            val episodeInfo = episodeInfo(null, "", LinkData(LinkType.CONTENT, null, i),getVideoDuration(i))
                                            val capcv: ConstraintLayout? = mainContainer.findViewWithTag("create_anime_page_container")
                                            if (capcv != null) {
                                                val sc = capcv.findViewWithTag<ScrollView>("scroll_container")
                                                val con = sc.findViewWithTag<ConstraintLayout>("container")
                                                val aebc = con.findViewWithTag<ConstraintLayout>("add_episodes_block_container")
                                                val eebc = aebc.findViewWithTag<ConstraintLayout>("edit_episodes_container")
                                                val recycler = eebc.getChildAt(0) as RecyclerView
                                                val adapter = recycler.adapter as flatGridOfEditEpisodesAdapter
                                                adapter.addEpisode(episodeInfo)
                                            }
                                        }
                                    }
                                }
                            } }
                        }

                        val fullscreenview = createBlockBackgroundVieww()
                        fullscreenview.tag = "create_anime_page_fsv"
                        createAnimePageContainerView.elevation = maxOverLayElevation
                        maxOverLayElevation += 10f
                        mainContainer.addView(fullscreenview)
                        mainContainer.addView(createAnimePageContainerView)
                        var alreadyClosed = false
                        fullscreenview.setOnTouchListener { _, event ->
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {

                                }
                                MotionEvent.ACTION_UP -> {
                                    if (!alreadyClosed) {
                                        hideLayer()
                                        alreadyClosed = true
                                    }
                                }
                            }
                            true
                        }
                    }
                    is OverLayLayer.GenreChoice -> {
                        if (info.isItNewLayer) {
                            layersList.add(Layer.OverLay(lastElevation+1, pageTags.genreChoice, info.info))
                        }
                        val genreChoiceContainerView = createOvDialog.GenreChoice(this, info.info, resultSenderViewModel, deny = {hideLayer()})
                        genreChoiceContainerView.tag = "genre_choice_container"
                        val lp1 = genreChoiceContainerView.layoutParams as ConstraintLayout.LayoutParams
                        lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        genreChoiceContainerView.layoutParams = lp1
                        val fullscreenview = createBlockBackgroundVieww()
                        fullscreenview.tag = "genre_choice_fsv"
                        genreChoiceContainerView.elevation = maxOverLayElevation
                        maxOverLayElevation += 10f
                        val mainContainer = findViewById<ViewGroup>(R.id.main)
                        mainContainer.addView(fullscreenview)
                        mainContainer.addView(genreChoiceContainerView)
                        var alreadyClosed = false
                        fullscreenview.setOnTouchListener { _, event ->
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {}
                                MotionEvent.ACTION_UP -> {
                                    if (!alreadyClosed) {
                                        hideLayer()
                                        alreadyClosed = true
                                    }
                                }
                            }
                            true
                        }
                    }
                    is OverLayLayer.CreateCarouselPage -> {}
                }
            }
            is infoOfPageToShow.infoOfVideoPlayer -> {
                if (info.isItNewLayer) {
                    layersList.add(Layer.VideoPlayer(lastElevation+1, info.id, true))
                    lastElevation += 1
                }
                openAnimeVideoPlayer(info.id)
            }
        }
    }
    fun hidePage(tag: pageTags) {
        val mainContainer = findViewById<ViewGroup>(R.id.main)
        when(tag) {
            pageTags.animePage -> {
                animePageObjectId = -1
                viewModel.updateAnimePageAdapter(-1, objectsList)
                animePage.visibility = View.GONE
            }

            pageTags.createAnimePage -> {
                val createAnimePageContainerView: ViewGroup? = mainContainer.findViewWithTag<ViewGroup>("create_anime_page_container")
                val fullScreenView: View? = mainContainer.findViewWithTag<View>("create_anime_page_fsv")
                if (createAnimePageContainerView != null) {
                    mainContainer.removeView(createAnimePageContainerView)
                }
                if (fullScreenView != null) {
                    mainContainer.removeView(fullScreenView)
                }
            }

            pageTags.genreChoice -> {
                val genreChoiceContainerView: ViewGroup? = mainContainer.findViewWithTag<ViewGroup>("genre_choice_container")
                val fullScreenView: View? = mainContainer.findViewWithTag<View>("genre_choice_fsv")
                if (genreChoiceContainerView != null) {
                    mainContainer.removeView(genreChoiceContainerView)
                }
                if (fullScreenView != null) {
                    mainContainer.removeView(fullScreenView)
                }
            }
            pageTags.videoPlayer -> {
                changeOrientation(this,true)
                toggleSystemBars(true,this)
                closeVideoPlayer()
            }
        }

    }
    fun onRotationChanged() {
        val orientation = resources.configuration.orientation
        when {
            orientation == Configuration.ORIENTATION_PORTRAIT -> {
                for (i in 0 until blobsNeedToHideOnAlbomOrientationIdsList.size) {
                    val blob = findViewById<View>(blobsNeedToHideOnAlbomOrientationIdsList[i])
                    if (blob != null) {
                        blob.visibility = View.VISIBLE
                    }
                }
            }
            orientation == Configuration.ORIENTATION_LANDSCAPE -> {
                for (i in 0 until blobsNeedToHideOnAlbomOrientationIdsList.size) {
                    val blob = findViewById<View>(blobsNeedToHideOnAlbomOrientationIdsList[i])
                    if (blob != null) {
                        blob.visibility = View.GONE
                    }
                }
            }
        }
        orientationNow = orientation
    }
    fun changeBaseBlobsAlpha(newAlpha: Float) {
        val blob1 = findViewById<View>(baseblob1Id)
        val blob2 = findViewById<View>(baseblob2Id)
        val blob3 = findViewById<View>(baseblob3Id)
        if (blob1 != null) {
            blob1.alpha = newAlpha
        }
        if (blob2 != null) {
            blob2.alpha = newAlpha
        }
        if (blob3 != null) {
            blob3.alpha = newAlpha
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    fun addCardToCarousel(parentId: Long) {
        if (!alreadyShowedAddBlock) {
            showPage(infoOfPageToShow.infoOfOverlayLayer(
                OverLayLayer.CreateAnimePage(
                    "",
                    null,
                    "",
                    "",
                    mutableListOf(),
                    mutableListOf(),
                    ElementType.Anime,),
                true)
            )
            lifecycleScope.launch {
                resultSenderViewModel.results.collect { (key, data) -> run {
                    when (key) {
                        ResultKeys.CREATE_CARD_APPLY -> {
                            val dataa = data as createCardApply
                            var length = 0L
                            for (i in dataa.episodesList) {
                                length += i.length
                            }
                            val cardData = objectData(
                                0,
                                null,
                                0,
                                0,
                                dataa.name,
                                false,
                                null,
                                false,
                                false,
                                false,
                                null,
                                true,
                                dataa.image,
                                dataa.description,
                                dataa.author,
                                null,
                                0,
                                length,
                                520,
                                743,
                                null,
                                null,
                                false,
                                false,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                LinkData(LinkType.SELF,null,null),
                                ElementType.Anime,
                                dataa.genreList
                            )
                            viewModel.insertCardWithEpisodes(cardData,dataa.episodesList, parentId)
                            hideLayer()
                        }
                    }
                }
                }
            }
        }
    }
    fun createBlockBackgroundVieww(): ImageView {
        return ImageView(this).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            visibility = ImageView.VISIBLE
            setBackgroundResource(R.drawable.window_outofborders_background)
            elevation = maxOverLayElevation
            maxOverLayElevation += 10f
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    fun createBlockBackgroundView(extraView: View, callback: (alredyShowed: Boolean) -> Unit) {
        val fullScreenView = ImageView(this).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            visibility = ImageView.VISIBLE
            setBackgroundResource(R.drawable.window_outofborders_background)
            elevation = 10f
            alreadyShowedAddBlock = true
        }
        fullScreenView.alpha = 0f
        extraView.alpha = 0f
        fullScreenView.elevation = 99f
        fullScreenView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                }
                MotionEvent.ACTION_UP -> {
                    fullScreenView.animate().alpha(0f).setDuration(150).withEndAction {
                        fullScreenView.visibility = View.GONE
                        findViewById<ViewGroup>(R.id.main).removeView(fullScreenView)
                        findViewById<ViewGroup>(R.id.main).removeView(extraView)
                        callback(false)
                    }.start()
                    extraView.animate().alpha(0f).setDuration(140).start()
                }
            }
            true
        }
        findViewById<ViewGroup>(R.id.main).addView(extraView)
        findViewById<ViewGroup>(R.id.main).addView(fullScreenView)
        callback(true)
        fullScreenView.animate().alpha(1f).setDuration(150).start()
        extraView.animate().alpha(1f).setDuration(140).start()
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    fun showShowAllText(text1: String, textSizee: Float, callback: (alreadyShowed: Boolean) -> Unit) {
        val css = TextView(this).apply {
            val layoutparams2 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams2.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams2.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams2.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams2.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            text = text1
            includeFontPadding = false
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
            this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_regular)
            setTextColor("#FFFFFF".toColorInt())
            layoutParams = layoutparams2
        }

        css.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val cssWidth = css.measuredWidth
        var cssHeight = css.measuredHeight
        val nameOnFullScreenMargin = round(30f*density).toInt()
        val maxCssWidth = (screenWidth.toFloat() / 1.25f).toInt() - nameOnFullScreenMargin*2
        val maxCssHeight = (screenHeight.toFloat() / 1.5f).toInt() - nameOnFullScreenMargin
        if (cssWidth > maxCssWidth) {
            css.measure(
                View.MeasureSpec.makeMeasureSpec(maxCssWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            cssHeight = css.measuredHeight
        }
        val nameOnFullScreenViewContainer = ConstraintLayout(this).apply {
            val layoutparams3 = ConstraintLayout.LayoutParams(
                if (cssWidth > maxCssWidth) {maxCssWidth+nameOnFullScreenMargin*2} else {cssWidth+nameOnFullScreenMargin*2},
                if (cssHeight > maxCssHeight) {maxCssHeight+nameOnFullScreenMargin*2} else {cssHeight+nameOnFullScreenMargin*2}
            )
            if (cssWidth > maxCssWidth) {
                css.width = maxCssWidth
            }
            setPadding(nameOnFullScreenMargin-5,nameOnFullScreenMargin-5,nameOnFullScreenMargin-5,nameOnFullScreenMargin-5)
            background = resources.getDrawable(R.drawable.addbackground)
            layoutparams3.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams3.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams3.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams3.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams3
            elevation = 100f
            alpha = 0f
        }
        val nameOnFullScreenViewScrollContainer = ScrollView(this).apply {
            val layoutparams4 = ConstraintLayout.LayoutParams(
                if (cssWidth > maxCssWidth) {maxCssWidth} else {cssWidth},
                if (cssHeight > maxCssHeight) {maxCssHeight} else {cssHeight}
            )
            layoutparams4.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams4.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams4
        }
        nameOnFullScreenViewScrollContainer.addView(css)
        nameOnFullScreenViewContainer.addView(nameOnFullScreenViewScrollContainer)
        createBlockBackgroundView(nameOnFullScreenViewContainer) {
                alreadyShowed -> callback(alreadyShowed)
        }
    }
}

class bottomSheetDialogFactory(private val activity: Activity) {
    @SuppressLint("PrivateResource")
    fun createDialog(list: List<BottomSheetDialogElement>, callback: (tag: BsdButtonsTags) -> Unit) {
        val dialog = BottomSheetDialog(activity)
        val googleMaxWidthDp = round(640f * baseDensity).toInt()
        val googleMaxWidthPx = round(googleMaxWidthDp.toFloat() * baseDensity).toInt()

        val elementWidth = min((min(screenWidth, screenHeight)), googleMaxWidthPx)
        val elementHeight = (150f * density).toInt()
        val pillRawHeight = (100f * density).toInt()
        val pillHeight = round(pillRawHeight.toFloat() / 4f).toInt()

        val container = LinearLayout(activity).apply {
            val layoutparams1 = LinearLayout.LayoutParams(
                elementWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL

            layoutParams = layoutparams1
            id = View.generateViewId()
        }

        val scrollContainer = NestedScrollView(activity).apply {
            val layoutparams1 = LinearLayout.LayoutParams(
                elementWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            addView(container)
            layoutparams1.setMargins(0,pillRawHeight,0,0)
            layoutParams = layoutparams1
        }
        dialog.setContentView(scrollContainer)
        val pillDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 10000f
            setColor("#BF4C4C4C".toColorInt())
        }

        val showPoloska = false
        fun createPoloska() : ImageView {
            val poloska = ImageView(activity).apply {
                val lp1 = ConstraintLayout.LayoutParams(
                    elementWidth,
                    round(3f*density).toInt()
                )
                lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                setBackgroundColor("#26D9D9D9".toColorInt())
                layoutParams = lp1
            }
            return poloska
        }

        for (element in list) {
            when (element) {
                is BottomSheetDialogElement.Button -> {
                    val buttonView = createBSDButton(
                        textt = element.text,
                        icoId = element.icoId,
                        showOpenPageArrow = element.openThePage,
                        context = activity,
                        width = elementWidth,
                        height = elementHeight
                    )
                    if (showPoloska) {
                        val poloska = createPoloska()
                        buttonView.addView(poloska)
                    }
                    buttonView.setOnClickListener {
                        callback(element.tag)
                    }
                    container.addView(buttonView)
                }

                is BottomSheetDialogElement.SegmentedButton -> {
                    val segmentedView = createSegmentedButtonRow(
                        context = activity,
                        width = elementWidth,
                        height = elementHeight,
                        options = element.options,
                        icoId = element.icoId,
                        textt = element.text
                    )
                    for (i in element.options.indices) {
                        val currentTag = element.options[i].second
                        val segment = segmentedView.findViewWithTag<View>("button_$i")
                        segment?.setOnClickListener { clickedSegment ->
                            callback(currentTag)
                            for (o in element.options.indices) {
                                val segment1 = segmentedView.findViewWithTag<View>("button_$o") ?: continue
                                val segment1Background = segment1.findViewWithTag<View>("button_bg").background as GradientDrawable
                                val colorNow = segment1Background.color?.defaultColor ?: "#80EADDFF".toColorInt()
                                val targetColor = if (segment1 == clickedSegment) {"#E8DEF8".toColorInt()} else {"#80EADDFF".toColorInt()}
                                if (colorNow != targetColor) {
                                    val animation = ValueAnimator.ofObject(ArgbEvaluator(), colorNow, targetColor).apply {
                                        duration = 100
                                        interpolator = AccelerateDecelerateInterpolator()
                                        addUpdateListener { animator ->
                                            segment1Background.setColor(animator.animatedValue as Int)
                                        }
                                    }
                                    animation.start()
                                }
                            }
                        }
                    }
                    if (showPoloska) {
                        val poloska = createPoloska()
                        segmentedView.addView(poloska)
                    }
                    container.addView(segmentedView)
                }

                is BottomSheetDialogElement.Slider -> {
                    val sliderHeight = elementHeight
                    val sliderViews = createSliderRow(
                        context = activity,
                        width = elementWidth,
                        name = element.text,
                        icoId = element.icoId,
                        stopsList = element.stops,
                        heightt = sliderHeight,
                        createSteps = element.createSteps
                    )
                    val sliderContainer = sliderViews[0] as ConstraintLayout
                    val slider = sliderViews.last() as ConstraintLayout
                    val sliderr = slider.getChildAt(0) as Slider
                    sliderr.addOnChangeListener { _, value, _ ->
                        callback(element.tag)
                    }
                    if (showPoloska) {
                        val poloska = createPoloska()
                        sliderContainer.addView(poloska)
                    }
                    container.addView(sliderContainer)
                }

                is BottomSheetDialogElement.DropdownRow -> {
                    val dropdownView = createDropdownRow(
                        context = activity,
                        width = elementWidth,
                        height = elementHeight,
                        titleText = element.text,
                        icoId = element.icoId,
                        options = element.options,
                        onItemSelected = { selectedText, selectedTag ->
                            callback(selectedTag)
                        },
                        buttonIcoId = element.buttonIcoId
                    )
                    if (showPoloska) {
                        val poloska = createPoloska()
                        dropdownView.addView(poloska)
                    }
                    container.addView(dropdownView)
                }
            }
        }
        val radius = getAdaptiveRadius(elementWidth, SizeType.XLARGE)
        val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

        dialog.setOnShowListener {
            bottomSheet?.let { sheet ->
                ViewCompat.setOnApplyWindowInsetsListener(sheet) { v, insets ->
                    v.setPadding(0, 0, 0, 0)
                    WindowInsetsCompat.CONSUMED
                }
                val behavior = BottomSheetBehavior.from(sheet)
                val shapeAppearance = ShapeAppearanceModel.builder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                    .setTopRightCorner(CornerFamily.ROUNDED, radius)
                    .build()

                sheet.background = MaterialShapeDrawable(shapeAppearance).apply {
                    fillColor = ColorStateList.valueOf("#181619".toColorInt())
                }
                sheet.backgroundTintList = null
                if (sheet.findViewWithTag<View>("handle") == null) {
                    val handle = View(activity).apply {
                        tag = "handle"
                        val lp = FrameLayout.LayoutParams(
                            round(elementWidth.toFloat() / 6f).toInt(),
                            pillHeight
                        ).apply {
                            gravity = Gravity.CENTER_HORIZONTAL
                            topMargin = round(12f * density).toInt()
                        }
                        layoutParams = lp
                        background = pillDrawable
                    }
                    sheet.addView(handle)
                }
                val lp1 = sheet.layoutParams
                lp1.width = elementWidth
                sheet.layoutParams = lp1
                behavior.skipCollapsed = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.maxHeight = (screenHeight.toFloat() * 0.7f).toInt()
            }
        }

        dialog.window?.let { window ->
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.setLayout(elementWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
            window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }

        dialog.show()
    }
}

class ObjectDiffCallback : DiffUtil.ItemCallback<objectData2>() {
    // Проверяем, один и тот же это элемент (по ID)
    override fun areItemsTheSame(oldItem: objectData2, newItem: objectData2): Boolean {
        return oldItem.id == newItem.id
    }

    // Проверяем, изменилось ли что-то внутри (Kotlin data class сделает это сам)
    override fun areContentsTheSame(oldItem: objectData2, newItem: objectData2): Boolean {
        return oldItem == newItem
    }
}

class ObjectDiffCallback2 : DiffUtil.ItemCallback<animePageSezonsAdapterListFormat>() {
    override fun areItemsTheSame(p0: animePageSezonsAdapterListFormat, p1: animePageSezonsAdapterListFormat): Boolean {
        return p0.obj.id == p1.obj.id
    }

    override fun areContentsTheSame(p0: animePageSezonsAdapterListFormat, p1: animePageSezonsAdapterListFormat): Boolean {
        return p0 == p1
    }
}

class carouselsAdapter(private val context: Context, val addCardToCarousel: (Long) -> Unit, val clickOnItem: (objectData2) -> Unit) : ListAdapter<objectData2, carouselsAdapter.ViewHolder>(ObjectDiffCallback()) {

    class ViewHolder(val constraintLayout: ConstraintLayout)  : RecyclerView.ViewHolder(constraintLayout) {
        var totalScrolledRecyclerView = 0
        var totalScrolledHorizontalScrollView = 0
        var activeDotPosition = 0
        var nameTextView: TextView? = null
        var editButton: ImageButton? = null
        var addButton: ImageButton? = null
        var watchAllButton: Button? = null
        var recyclerView: RecyclerView? = null
        var constraintLayoutGrid: ConstraintLayout? = null
        var dotsLayout: ViewGroup? = null
    }


    // Это база для элемента (остальные его модификации применяются в onBindViewHolder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ConstraintLayout(context).apply {
            val layoutParams1 = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams1.setMargins(0,0,0,0)
            layoutParams = layoutParams1
        })
    }

    // Это уже преобразования элемента (см. onCreateViewHolder) в зависимости от позиции (position)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.totalScrolledRecyclerView = 0
        holder.totalScrolledHorizontalScrollView = 0
        holder.activeDotPosition = 0
        val layer = layersList[layersList.indexOf(findMainPageLayerByPageId(getItem(position).page))]
        if (holder.constraintLayout.isEmpty() && layer is Layer.MainPage) {
            var paddingVertical = getItem(position).paddingVertical
            if (paddingVertical == null) {
                paddingVertical = round(100f*density).toInt()
            }
            else {
                paddingVertical = round(paddingVertical.toFloat()*density).toInt()
            }
            val layoutparams2 = holder.constraintLayout.layoutParams as RecyclerView.LayoutParams
            layoutparams2.setMargins(0,0,0,0)
            holder.constraintLayout.layoutParams = layoutparams2
            var idOfTextObj = 0
            var idOfEditButton = 0
            var idOfAddButton = 0
            val buttonsSize = round(25f*baseDensity).toInt()
            val addButtonSize = buttonsSize+round(0f*baseDensity).toInt()
            val watchButtonWidth = round(240f*density).toInt()
            val watchButtonHeight = (25f * baseDensity).toInt()
            val watchButtonMarginRight = (50f * density).toInt()
            var pdH = getItem(position).paddingHorizontal
            if (pdH == null) {
                pdH = round(50f*density).toInt()
            }
            else {
                pdH = round(pdH.toFloat()*density).toInt()
            }
            val textViewWidth = screenWidth - pdH - buttonsSize*2 - watchButtonWidth - watchButtonMarginRight
            val textView = OptimizedTextView(context).apply {
                if (getItem(position).name != null) {
                    text = getItem(position).name
                }
                else {
                    text = resources.getText(R.string.withoutName)
                }
                maxLines = 2
                breakStrategy = LineBreaker.BREAK_STRATEGY_HIGH_QUALITY
                includeFontPadding = false
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                maxWidth = textViewWidth
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.setMargins(pdH,0,0,0)
                val newId = View.generateViewId()
                id = newId
                idOfTextObj = newId
                setTextSize(TypedValue.COMPLEX_UNIT_PX, round(25f*baseDensity))
                setTextColor(Color.WHITE)
                layoutParams = layoutparams1
            }
            holder.constraintLayout.addView(textView)
            holder.nameTextView = textView
            val editButton = ImageButton(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    buttonsSize,
                    buttonsSize
                )
                layoutparams1.startToEnd = idOfTextObj
                layoutparams1.topToTop = idOfTextObj
                layoutparams1.bottomToBottom = idOfTextObj
                layoutparams1.setMargins(round(5f*baseDensity).toInt(),0,0,0)
                layoutParams = layoutparams1
                val newId = View.generateViewId()
                id = newId
                idOfEditButton = newId
                background = Color.TRANSPARENT.toDrawable()
                setImageResource(R.drawable.edit_ico)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            holder.constraintLayout.addView(editButton)
            holder.editButton = editButton
            val addButton = ImageButton(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    addButtonSize,
                    addButtonSize
                )
                layoutparams1.startToEnd = idOfEditButton
                layoutparams1.topToTop = idOfEditButton
                layoutparams1.bottomToBottom = idOfEditButton
                layoutParams = layoutparams1
                val newId = View.generateViewId()
                id = newId
                idOfAddButton = newId
                background = Color.TRANSPARENT.toDrawable()
                setImageResource(R.drawable.add_ico)
                scaleType = ImageView.ScaleType.CENTER_CROP
                setOnClickListener {
                    addCardToCarousel(currentList[holder.position].id)
                }
            }
            holder.constraintLayout.addView(addButton)
            holder.addButton = addButton
            val watchAllButton = Button(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    watchButtonWidth,
                    watchButtonHeight
                )
                layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = idOfAddButton
                layoutparams1.setMargins(0, 0, watchButtonMarginRight, 0)
                layoutParams = layoutparams1
                text = "Смотреть все"
                setPadding(0,0,0,0)
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, round(25f*density)) // converted to px value
                backgroundTintList = null
                stateListAnimator = null
                elevation = 0f
                setBackgroundResource(R.drawable.watch_all_button)
                focusable = View.FOCUSABLE
                isClickable = true
            }
            holder.constraintLayout.addView(watchAllButton)
            holder.watchAllButton = watchAllButton
            // Добавляем recycler view
            if (getItem(position).layoutType == null || getItem(position).layoutType == 1) {
                var recyclerViewId = 0
                val recycler = RecyclerView(context).apply {
                    val layoutParams1 = ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        calcRecyclerViewHeight(currentList,position)
                    )
                    layoutParams1.topToBottom = idOfTextObj
                    layoutParams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutParams1.setMargins(0, round(15f*density).toInt(), 0, 0)
                    layoutParams = layoutParams1
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = childAdapter(context, getItem(position), clickOnCard = {
                        clickOnItem(it)
                    })
                    val newId = View.generateViewId()
                    id = newId
                    recyclerViewId = newId
                }
                val ad = recycler.adapter as childAdapter
                ad.submitList(getItem(position).childs)
                holder.constraintLayout.addView(recycler)
                holder.recyclerView = recycler
                recycler.post {
                    recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            holder.totalScrolledRecyclerView += dx
                            layer.scrollPositionsInPx[getItem(position).position] = holder.totalScrolledRecyclerView
                            val layoutManager = recycler.layoutManager as LinearLayoutManager
                            val firstVisible = layoutManager.findFirstVisibleItemPosition()
                            layer.scrollPositionCarousels[getItem(position).position] = firstVisible
                        }
                    })
                    recycler.post {
                        if (!(getItem(position).dovodchik && getItem(position).showDovodchikDots)) {
                            var pdH = getItem(position).paddingHorizontal
                            if (pdH == null) {
                                pdH = round(50f*density).toInt()
                            }
                            else {
                                pdH = round(pdH.toFloat()*density).toInt()
                            }
                            layer.scrollPositionCarousels[getItem(position).position] = 0
                            val scrollH = calcItemPosInPxByPos(currentList,position, layer.scrollPositionCarousels[getItem(position).position]!!, context) - pdH
                            recycler.scrollBy(scrollH,0)

                        }
                    }
                }
                if (getItem(position).dovodchik && getItem(position).showDovodchikDots) {
                    var padding = round(50f*density).toInt()
                    if (getItem(position).paddingHorizontal != null) {
                        padding = round(getItem(position).paddingHorizontal!!.toFloat()*density).toInt()
                    }
                    var marginBetweenElementsHorizontal = getItem(position).marginBetweenElementsHorizontal
                    if (marginBetweenElementsHorizontal == null) {
                        marginBetweenElementsHorizontal = round(30f*density).toInt()
                    }
                    else {
                        marginBetweenElementsHorizontal = round(marginBetweenElementsHorizontal.toFloat()*density).toInt()
                    }
                    val dots = createDovodchikDots(context, getItem(position).childs, padding, marginBetweenElementsHorizontal, currentList[position].childsShowName, currentList[position].childsNamePosition)
                    val dotsLayout = dots.layout as ViewGroup
                    val layoutparams1 = dotsLayout.layoutParams as ConstraintLayout.LayoutParams
                    layoutparams1.topToBottom = recyclerViewId
                    layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutparams1.setMargins(0,round(10f*density).toInt(),0,0)
                    dotsLayout.layoutParams = layoutparams1
                    dotsLayout.setPadding(0,0,0,0)
                    holder.constraintLayout.addView(dotsLayout)
                    holder.dotsLayout = dotsLayout
                    var horizontalScrollView: HorizontalScrollView = HorizontalScrollView(context).apply {
                        layoutParams = ConstraintLayout.LayoutParams(
                            0,
                            0
                        )
                    }
                    for (o in 0 until dotsLayout.childCount) {
                        if (dotsLayout.getChildAt(o) is HorizontalScrollView) {
                            horizontalScrollView = dotsLayout.getChildAt(o) as HorizontalScrollView
                        }
                    }

                    horizontalScrollView.setOnScrollChangeListener { _, scrollX, _, oldScrollX, _ ->
                        holder.totalScrolledHorizontalScrollView = scrollX
                    }

                    val dotsContainer = horizontalScrollView.getChildAt(0) as ViewGroup
                    for (i in 0 until dots.list.size) {
                        val index = i
                        if (i < dotsContainer.childCount) {
                            dotsContainer.getChildAt(index).setOnClickListener {
                                updateActiveDot(index, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, recycler, horizontalScrollView, position, layer)
                                holder.activeDotPosition = index
                                layer.activeDotPositionCarousels[getItem(position).position] = index
                            }
                        }
                    }
                    var scrolled = holder.totalScrolledHorizontalScrollView
                    recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            scrolled += dx
                            var fdPos = 0
                            // Между какими он точками
                            for (i in 0 until dots.list.size) {
                                if (i+1 < dots.list.size) {
                                    val srez = dots.list[i].second.itemPositionInPx..dots.list[i+1].second.itemPositionInPx
                                    if (scrolled in srez) {
                                        val dot1 = dotsContainer.getChildAt(i) as ImageView
                                        val dot2 = dotsContainer.getChildAt(i+1) as ImageView
                                        val procFor2Dot = round((scrolled - srez.first).toFloat() / ((srez.last - srez.first).toFloat() / 100f)).toInt()
                                        val procFor1Dot = 100 - procFor2Dot
                                        dot1.setImageDrawable(dotDrawables[procFor1Dot])
                                        dot2.setImageDrawable(dotDrawables[procFor2Dot])
                                        fdPos = i
                                        break
                                    }
                                }
                            }
                            for (i in 0 until dots.list.size) {
                                if (i != fdPos && i != fdPos+1) {
                                    val dot = dotsContainer.getChildAt(i) as ImageView
                                    dot.setImageDrawable(dotDrawables[0])
                                }
                            }
                        }
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)

                            when (newState) {
                                RecyclerView.SCROLL_STATE_IDLE -> {
                                    // Пользователь отпустил и прокрутка остановилась
                                    val currentScroll = holder.totalScrolledRecyclerView

                                    // Находим ближайшую точку
                                    val targetDot = dots.list.minByOrNull {
                                        Math.abs(it.second.itemPositionInPx - currentScroll)
                                    }

                                    targetDot?.let {
                                        val targetIndex = dots.list.indexOf(it)
                                        val targetScroll = it.second.itemPositionInPx
                                        val distance = targetScroll - currentScroll

                                        // Если не на месте - доводим
                                        if (Math.abs(distance) > 0) {
                                            recyclerView.smoothScrollBy(distance, 0)
                                            updateActiveDot(targetIndex, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, recycler, horizontalScrollView, position, layer)
                                            holder.activeDotPosition = targetIndex
                                            layer.activeDotPositionCarousels[getItem(position).position] = targetIndex
                                        }
                                    }
                                }
                            }
                        }
                    })
                    recycler.setOnFlingListener(PageFlingListener(
                        dotsList = dots.list,
                        getCurrentScroll = { holder.totalScrolledRecyclerView },
                        updatePage = { dotIndex ->
                            updateActiveDot(dotIndex, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, recycler, horizontalScrollView, position, layer)
                            holder.activeDotPosition = dotIndex
                            layer.activeDotPositionCarousels[getItem(position).position] = dotIndex
                        },
                        recyclerView = recycler
                    ))
                    recycler.post {
                        dotsLayout.post {
                            if (layer.activeDotPositionCarousels[getItem(position).position] == null) {
                                layer.activeDotPositionCarousels[getItem(position).position] = 0
                            }
                            updateActiveDot(layer.activeDotPositionCarousels[getItem(position).position]!!, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView,
                                recycler, horizontalScrollView, position, layer, true, if (orientationOld != orientationNow) {true} else {false})
                            holder.activeDotPosition = layer.activeDotPositionCarousels[getItem(position).position]!!
                            orientationOld = orientationNow
                        }
                    }
                }
            }
            // Добавляем grid
            else {
                var maxObjectsInOneLine: Int?
                if (getItem(position).maxObjectsInOneLine == null) {
                    maxObjectsInOneLine = null
                }
                else {
                    maxObjectsInOneLine = getItem(position).maxObjectsInOneLine as Int
                }
                var paddingHorizontal = getItem(position).paddingHorizontal
                if (paddingHorizontal == null) {
                    paddingHorizontal = 0
                }
                else {
                    paddingHorizontal = round(paddingHorizontal.toFloat() * density).toInt()
                }
                val constraintLayout = createGridOfChilds(getItem(position).childs,  maxObjectsInOneLine, context, (screenWidth-paddingHorizontal*2), getItem(position))
                val layoutparams1 = constraintLayout.layoutParams as ConstraintLayout.LayoutParams
                layoutparams1.topToBottom = idOfTextObj
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.setMargins(paddingHorizontal, round(15f*density).toInt(),paddingHorizontal,0)
                constraintLayout.layoutParams = layoutparams1
                holder.constraintLayout.addView(constraintLayout)
                holder.constraintLayoutGrid = constraintLayout
            }
        }
        else if (layer is Layer.MainPage){
            if (holder.nameTextView != null) {
                holder.nameTextView!!.apply {
                    if (getItem(position).name != null) {
                        text = getItem(position).name
                    }
                    else {
                        text = resources.getText(R.string.withoutName)
                    }
                }
            }
            if (holder.addButton != null) {
                holder.addButton!!.setOnClickListener(null)
                holder.addButton!!.setOnClickListener {
                    addCardToCarousel(currentList[holder.position].id)
                }
            }
            if (holder.editButton != null) {
                holder.editButton!!.setOnClickListener(null)
                holder.editButton!!.setOnClickListener {
//                    editCarousel(position, currentList)
                }
            }
            if (holder.watchAllButton != null) {
                holder.watchAllButton!!.setOnClickListener(null)
                holder.watchAllButton!!.setOnClickListener {
//                    watchAll(position, currentList)
                }
            }
            if (holder.recyclerView != null) {
                val ad = holder.recyclerView!!.adapter as childAdapter
                ad.submitList(getItem(position).childs)
                holder.recyclerView!!.clearOnScrollListeners()
                holder.recyclerView!!.onFlingListener = null
                holder.recyclerView!!.scrollTo(0,0)
                holder.recyclerView!!.scrollBy(-1000000000,0)
                holder.recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        holder.totalScrolledRecyclerView += dx
                        layer.scrollPositionsInPx[getItem(position).position] = holder.totalScrolledRecyclerView
                        val layoutManager = holder.recyclerView!!.layoutManager as LinearLayoutManager
                        val firstVisible = layoutManager.findFirstVisibleItemPosition()
                        layer.scrollPositionCarousels[getItem(position).position] = firstVisible
                    }
                })
                holder.recyclerView!!.post {
                    if (!(getItem(position).dovodchik && getItem(position).showDovodchikDots)) {
                        if (layer.scrollPositionsInPx[getItem(position).position] != null) {
                            holder.recyclerView!!.scrollBy(layer.scrollPositionsInPx[getItem(position).position]!!,0)
                        }
                        else {
                            holder.recyclerView!!.scrollBy(0,0)
                        }
                    }
                }
            }
            if (holder.dotsLayout != null && holder.recyclerView != null) {
                holder.constraintLayout.removeView(holder.dotsLayout)
                holder.dotsLayout = null
                if (getItem(position).dovodchik && getItem(position).showDovodchikDots) {
                    var padding = round(50f*density).toInt()
                    if (getItem(position).paddingHorizontal != null) {
                        padding = round(getItem(position).paddingHorizontal!!.toFloat()*density).toInt()
                    }
                    var marginBetweenElementsHorizontal = getItem(position).marginBetweenElementsHorizontal
                    if (marginBetweenElementsHorizontal == null) {
                        marginBetweenElementsHorizontal = round(30f*density).toInt()
                    }
                    else {
                        marginBetweenElementsHorizontal = round(marginBetweenElementsHorizontal.toFloat()*density).toInt()
                    }
                    val dots = createDovodchikDots(context, getItem(position).childs, padding, marginBetweenElementsHorizontal, currentList[position].childsShowName, currentList[position].childsNamePosition)
                    val dotsLayout = dots.layout as ViewGroup
                    val layoutparams1 = dotsLayout.layoutParams as ConstraintLayout.LayoutParams
                    layoutparams1.topToBottom = holder.recyclerView!!.id
                    layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutparams1.setMargins(0,round(10f*density).toInt(),0,0)
                    dotsLayout.layoutParams = layoutparams1
                    dotsLayout.setPadding(0,0,0,0)
                    holder.constraintLayout.addView(dotsLayout)
                    holder.dotsLayout = dotsLayout
                    var horizontalScrollView: HorizontalScrollView = HorizontalScrollView(context).apply {
                        layoutParams = ConstraintLayout.LayoutParams(
                            0,
                            0
                        )
                    }
                    for (o in 0 until dotsLayout.childCount) {
                        if (dotsLayout.getChildAt(o) is HorizontalScrollView) {
                            horizontalScrollView = dotsLayout.getChildAt(o) as HorizontalScrollView
                        }
                    }

                    horizontalScrollView.setOnScrollChangeListener { _, scrollX, _, oldScrollX, _ ->
                        holder.totalScrolledHorizontalScrollView = scrollX
                    }

                    val dotsContainer = horizontalScrollView.getChildAt(0) as ViewGroup
                    for (i in 0 until dots.list.size) {
                        val index = i
                        if (i < dotsContainer.childCount) {
                            dotsContainer.getChildAt(index).setOnClickListener {
                                updateActiveDot(index, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, holder.recyclerView!!, horizontalScrollView, position, layer)
                                holder.activeDotPosition = index
                                layer.activeDotPositionCarousels[getItem(position).position] = index
                            }
                        }
                    }
                    var scrolled = holder.totalScrolledHorizontalScrollView
                    holder.recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            scrolled += dx
                            var fdPos = 0
                            // Между какими он точками
                            for (i in 0 until dots.list.size) {
                                if (i+1 < dots.list.size) {
                                    val srez = dots.list[i].second.itemPositionInPx..dots.list[i+1].second.itemPositionInPx
                                    if (scrolled in srez) {
                                        val dot1 = dotsContainer.getChildAt(i) as ImageView
                                        val dot2 = dotsContainer.getChildAt(i+1) as ImageView
                                        val procFor2Dot = round((scrolled - srez.first).toFloat() / ((srez.last - srez.first).toFloat() / 100f)).toInt()
                                        val procFor1Dot = 100 - procFor2Dot
                                        dot1.setImageDrawable(dotDrawables[procFor1Dot])
                                        dot2.setImageDrawable(dotDrawables[procFor2Dot])
                                        fdPos = i
                                        break
                                    }
                                }
                            }
                            for (i in 0 until dots.list.size) {
                                if (i != fdPos && i != fdPos+1) {
                                    val dot = dotsContainer.getChildAt(i) as ImageView
                                    dot.setImageDrawable(dotDrawables[0])
                                }
                            }
                        }
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)

                            when (newState) {
                                RecyclerView.SCROLL_STATE_IDLE -> {
                                    // Пользователь отпустил и прокрутка остановилась
                                    val currentScroll = holder.totalScrolledRecyclerView

                                    // Находим ближайшую точку
                                    val targetDot = dots.list.minByOrNull {
                                        Math.abs(it.second.itemPositionInPx - currentScroll)
                                    }

                                    targetDot?.let {
                                        val targetIndex = dots.list.indexOf(it)
                                        val targetScroll = it.second.itemPositionInPx
                                        val distance = targetScroll - currentScroll

                                        // Если не на месте - доводим
                                        if (Math.abs(distance) > 0) {
                                            recyclerView.smoothScrollBy(distance, 0)
                                            updateActiveDot(targetIndex, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, holder.recyclerView!!, horizontalScrollView, position, layer)
                                            holder.activeDotPosition = targetIndex
                                            layer.activeDotPositionCarousels[getItem(position).position] = targetIndex
                                        }
                                    }
                                }
                            }
                        }
                    })
                    holder.recyclerView!!.setOnFlingListener(PageFlingListener(
                        dotsList = dots.list,
                        getCurrentScroll = { holder.totalScrolledRecyclerView },
                        updatePage = { dotIndex ->
                            updateActiveDot(dotIndex, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, holder.recyclerView!!, horizontalScrollView, position, layer)
                            holder.activeDotPosition = dotIndex
                            layer.activeDotPositionCarousels[getItem(position).position] = dotIndex
                        },
                        recyclerView = holder.recyclerView!!
                    ))
                    holder.recyclerView!!.post {
                        dotsLayout.post {
                            if (layer.activeDotPositionCarousels[getItem(position).position] == null) {
                                layer.activeDotPositionCarousels[getItem(position).position] = 0
                            }
                            updateActiveDot(layer.activeDotPositionCarousels[getItem(position).position]!!, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, holder.recyclerView!!, horizontalScrollView, position, layer, true, if (orientationOld != orientationNow) {true} else {false})
                            holder.activeDotPosition = layer.activeDotPositionCarousels[getItem(position).position]!!
                            orientationOld = orientationNow
                        }
                    }
                }
            }
            if (holder.constraintLayoutGrid != null) {
                holder.constraintLayout.removeView(holder.constraintLayoutGrid)
                holder.constraintLayoutGrid = null
                var maxObjectsInOneLine: Int?
                if (getItem(position).maxObjectsInOneLine == null) {
                    maxObjectsInOneLine = null
                }
                else {
                    maxObjectsInOneLine = getItem(position).maxObjectsInOneLine as Int
                }
                var paddingHorizontal = getItem(position).paddingHorizontal
                if (paddingHorizontal == null) {
                    paddingHorizontal = 0
                }
                else {
                    paddingHorizontal = round(paddingHorizontal.toFloat() * density).toInt()
                }
                val constraintLayout = createGridOfChilds(getItem(position).childs,  maxObjectsInOneLine, context, (screenWidth-paddingHorizontal*2), getItem(position))
                val layoutparams1 = constraintLayout.layoutParams as ConstraintLayout.LayoutParams
                layoutparams1.topToBottom = holder.nameTextView!!.id
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.setMargins(paddingHorizontal, round(15f*density).toInt(),paddingHorizontal,0)
                constraintLayout.layoutParams = layoutparams1
                holder.constraintLayout.addView(constraintLayout)
                holder.constraintLayoutGrid = constraintLayout
            }
        }
    }
    var lastRecyclerViewPositionOnOrientationChanged: Pair<Int,Int>? = null
    fun updateActiveDot(newActiveDotPosition: Int, oldActiveDotPosition: Int, dotsAll: createDovodchikDotsReturn, totalScrolledRecyclerView: Int, totalScrolledHorizontalScrollView: Int, numberTextView: TextView, recycler: RecyclerView, horizontalScrollView: HorizontalScrollView, position: Int, layer: Layer.MainPage, force: Boolean = false, orientationChanged: Boolean = false) {
        val dots = dotsAll.list
        var recyclerScrollWidth: Int
        var number: Int
        var dotScrollWidth: Int
        var k = false
        if (lastRecyclerViewPositionOnOrientationChanged != null) {
            if (position > lastRecyclerViewPositionOnOrientationChanged!!.first && orientationNow == lastRecyclerViewPositionOnOrientationChanged!!.second) {
                k = true
            }
        }
        if (newActiveDotPosition < dots.size && !orientationChanged && !k) {
            recyclerScrollWidth = dots[newActiveDotPosition].second.itemPositionInPx - totalScrolledRecyclerView
            number = dots[newActiveDotPosition].first.number
            dotScrollWidth = dots[newActiveDotPosition].first.targetWidth - totalScrolledHorizontalScrollView
        }
        else {
            if (layer.scrollPositionCarousels[getItem(position).position] == null) {
                layer.scrollPositionCarousels[getItem(position).position] = 0
            }
            val itemPositionInPx = calcItemPosInPxByPos(currentList,position, layer.scrollPositionCarousels[getItem(position).position]!!,context)
            var dotPos = -1
            for (i in 0 until dots.size) {
                if (dots[i].second.itemPositionInPx <= itemPositionInPx && i > dotPos) {
                    dotPos = i
                }
                else if (dots[i].second.itemPositionInPx > itemPositionInPx) {
                    break
                }
            }
            recyclerScrollWidth = dots[dotPos].second.itemPositionInPx - totalScrolledRecyclerView
            number = dots[dotPos].first.number
            dotScrollWidth = dots[dotPos].first.targetWidth - totalScrolledHorizontalScrollView
            lastRecyclerViewPositionOnOrientationChanged = Pair(position, orientationNow)
        }
        numberTextView.text = number.toString()
        val paddings = calculateDigitParams(numberTextView.height, number.toString()[0], context)
        numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, paddings.first)
        numberTextView.setPadding(paddings.second, paddings.third, 0,0)
        if (force) {
            horizontalScrollView.scrollBy(dotScrollWidth,0)
            recycler.scrollBy(recyclerScrollWidth,0)
        }
        else {
            horizontalScrollView.smoothScrollBy(dotScrollWidth,0)
            recycler.smoothScrollBy(recyclerScrollWidth,0)
        }
    }
}

class childAdapter(private val context: Context, private val parent: objectData2, val clickOnCard: (objectData2) -> Unit) : ListAdapter<objectData2, childAdapter.ViewHolder>(ObjectDiffCallback()) {

    class ViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    // Это база для элемента (остальные его модификации применяются в onBindViewHolder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ConstraintLayout(context).apply {
            val layoutParams1 = RecyclerView.LayoutParams(
                0,
                0
            )
            layoutParams1.setMargins(round(30f*density).toInt(),0,0,0)
            layoutParams = layoutParams1
        })
    }

    // Это уже преобразования элемента (см. onCreateViewHolder) в зависимости от позиции (position)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.constraintLayout.removeAllViews()
        // Обычная карточка
        if (getItem(position).layoutType == null || getItem(position).layoutType == 1 || getItem(position).childs.isEmpty()) {
            val views = createCard(getItem(position).width, getItem(position).height, parent.childsShowName, parent.childsNamePosition, getItem(position).image, getItem(position).name, getItem(position).author, getItem(position).alreadyWatched, getItem(position).length, parent.childsShowAlreadyWatchedLine, context, currentList, parent.childsCornerRadius)
            val layoutparams2 = RecyclerView.LayoutParams(
                if (!getItem(position).showName || getItem(position).showName && getItem(position).namePosition == 1) {
                    views.third.first
                } else {
                    views.second
                },
                views.third.second
            )
            var padding = round(50f*density).toInt()
            if (parent.paddingHorizontal != null) {
                padding = (parent.paddingHorizontal!!.toFloat()*density).toInt()
            }
            var marginBetweenElementsHorizontal = parent.marginBetweenElementsHorizontal
            if (marginBetweenElementsHorizontal == null) {
                marginBetweenElementsHorizontal = round(30f*density).toInt()
            }
            else {
                marginBetweenElementsHorizontal = round(parent.marginBetweenElementsHorizontal!!.toFloat()*density).toInt()
            }
            when (getItem(position).position) {
                0 -> {
                    layoutparams2.setMargins(padding, 0, 0, 0)
                }
                currentList.size - 1 -> {
                    layoutparams2.setMargins(if (position > 0) {if (getItem(position).layoutType == 0) {0} else {marginBetweenElementsHorizontal}} else {marginBetweenElementsHorizontal}, 0, padding, 0)
                }
                else -> {
                    layoutparams2.setMargins(if (position > 0) {if (getItem(position).layoutType == 0) {0} else {marginBetweenElementsHorizontal}} else {marginBetweenElementsHorizontal}, 0, 0, 0)
                }
            }
            holder.constraintLayout.layoutParams = layoutparams2
            for (i in views.first) {
                holder.constraintLayout.addView(i)
            }
            holder.constraintLayout.setOnClickListener {
                clickOnCard(getItem(position))
            }
        }
        // Grid
        else {
            var paddingHorizontal = parent.paddingHorizontal
            if (paddingHorizontal == null) {
                paddingHorizontal = round(50f*density).toInt()
            }
            else {
                paddingHorizontal = round(parent.paddingHorizontal!!.toFloat()*density).toInt()
            }
            val constraintlayout1 = createGridOfChilds(getItem(position).childs, getItem(position).maxObjectsInOneLine, context, (screenWidth-paddingHorizontal*2), parent)
            val layoutparams1 = constraintlayout1.layoutParams as ConstraintLayout.LayoutParams
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(paddingHorizontal,0,0,0)
            constraintlayout1.layoutParams = layoutparams1
            for (i in 0 until constraintlayout1.childCount) {
                val objData = getItem(position).childs[i]
                val obj = constraintlayout1.getChildAt(i)
                obj.setOnClickListener {
                    clickOnCard(objData)
                }
            }
            val layoutparams2 = holder.constraintLayout.layoutParams as RecyclerView.LayoutParams
            layoutparams2.apply { width = screenWidth ; height = ConstraintLayout.LayoutParams.WRAP_CONTENT }
            layoutparams2.setMargins(0,0,0,0)
            holder.constraintLayout.layoutParams = layoutparams2
            holder.constraintLayout.addView(constraintlayout1)
        }

    }
}

class PageFlingListener(private val dotsList: List<Pair<listDot2, listDot>>, private val getCurrentScroll: () -> Int, private val updatePage: (Int) -> Unit, private val recyclerView: RecyclerView) : RecyclerView.OnFlingListener() {

    override fun onFling(velocityX: Int, velocityY: Int): Boolean {
        val currentScroll = getCurrentScroll()
        val targetDot = if (velocityX > round(500f*density).toInt()) {
            dotsList.find { it.second.itemPositionInPx > currentScroll }
        } else if (velocityX < -round(500f*density).toInt()) {
            dotsList.findLast { it.second.itemPositionInPx < currentScroll }
        } else {
            return false
        }

        targetDot?.let {
            val targetIndex = dotsList.indexOf(it)
            val targetScroll = it.second.itemPositionInPx
            val distance = targetScroll - currentScroll

            recyclerView.smoothScrollBy(distance, 0)
            updatePage(targetIndex)
            return true
        }

        return false
    }
}

class animePageAdapter(private val context: Context, private val showShowAllText: (text: String, size: Float, callback: (Boolean) -> Unit) -> Unit, private val sezonsAdapter: animePageSezonsPageAdapter, private val openVideo: (Long) -> Unit): ListAdapter<objectData2, animePageAdapter.ViewHolder>(ObjectDiffCallback()) {
    class ViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout) {
        var sezonsRecycler: RecyclerView? = null
        var episodesContainer: ConstraintLayout? = null
        var episodesTextView: TextView? = null
        var sezonsTextView: TextView? = null
        var altRazdelLine: ImageView? = null
        var razdelLineWidth: Int = 0
        var altRazdelLineWidth: Int = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ConstraintLayout(context).apply {
            val layoutParams1 = RecyclerView.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = layoutParams1
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.constraintLayout.removeAllViews()
        fun setState(newState: Int) {
            val activeRazdelTextColor = "#5A4293".toColorInt()
            val nonactiveRazdelTextColor = "#D9D9D9".toColorInt()
            if (holder.sezonsRecycler != null && holder.episodesContainer != null && holder.episodesTextView != null && holder.sezonsTextView != null && holder.altRazdelLine != null) {
                when (newState) {
                    0 -> {
                        holder.sezonsRecycler!!.visibility = View.GONE
                        holder.episodesContainer!!.visibility = View.VISIBLE
                        holder.episodesTextView!!.setTextColor(activeRazdelTextColor)
                        holder.sezonsTextView!!.setTextColor(nonactiveRazdelTextColor)
                        holder.altRazdelLine!!.animate().translationX(0f).duration = 0.toLong()
                    }
                    1 -> {
                        holder.sezonsRecycler!!.visibility = View.VISIBLE
                        holder.episodesContainer!!.visibility = View.GONE
                        holder.episodesTextView!!.setTextColor(nonactiveRazdelTextColor)
                        holder.sezonsTextView!!.setTextColor(activeRazdelTextColor)
                        holder.altRazdelLine!!.animate().translationX((holder.razdelLineWidth - holder.altRazdelLineWidth).toFloat()).duration = 0.toLong()
                    }
                }
                val layer = findLayerByLayerObjectId(getItem(position).id)
                if (layer.first is Layer.AnimePage) {
                    val q = layersList[layer.second] as Layer.AnimePage
                    q.state = newState
                }
            }
        }
        val activeRazdelTextColor = "#5A4293".toColorInt()
        val nonactiveRazdelTextColor = "#D9D9D9".toColorInt()
        val resources = context.resources
        val parentCard = getItem(position)
        val name: String = if (parentCard.name != null) { parentCard.name!! } else {resources.getString(R.string.withoutName)}
        val descriptionText: String = if (parentCard.description != null) { parentCard.description!! } else {""}

        val container = holder.constraintLayout

        var showALlButtonInNameView: TextView?

        // Круги на заднем фоне
        val blob1 = ImageView(context).apply {
            val layoutParams1 = ConstraintLayout.LayoutParams(
                blob12FullSize,
                blob12FullSize
            )
            layoutParams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams1.setMargins(0,blob1MarginTop,blob1MarginEnd,0)
            layoutParams = layoutParams1
        }
        val blob2 = ImageView(context).apply {
            val layoutParams1 = ConstraintLayout.LayoutParams(
                blob12FullSize,
                blob12FullSize
            )
            layoutParams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams1.setMargins(blob2MarginStart, blob2MarginTop,0,0)
            layoutParams = layoutParams1
        }
        val blob3 = ImageView(context).apply {

            val layoutParams1 = ConstraintLayout.LayoutParams(
                blob3FullSize,
                blob3FullSize
            )
            layoutParams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams1.setMargins(0,blob3MarginTop,blob3MarginEnd,0)
            layoutParams = layoutParams1
        }
        container.addView(blob2)
        container.addView(blob3)
        container.addView(blob1)


        val isLandscape = screenWidth > screenHeight
        val maxPageWidth = round(1000f * baseDensity).toInt() // Лимит для планшетов
        if (screenWidth > maxPageWidth) {
            val paddingHorizontal = round((screenWidth-maxPageWidth).toFloat() / 2f).toInt()
            container.setPadding(paddingHorizontal,0,paddingHorizontal,0)
        }
        val actualWidth = min(screenWidth, maxPageWidth)
        val marginLeft = round(16f * baseDensity).toInt()
        val marginRight = marginLeft
        val marginTop = round(24f * baseDensity).toInt()
        val spaceBetween = round(12f * baseDensity).toInt()
        val marginBetweenInfoElements = round(6f * baseDensity).toInt()
        val marginLeftButton = round(6f * baseDensity).toInt()
        val subMarginLeftText = round(6f * baseDensity).toInt()
        val hBtn = round(32f * baseDensity).toInt()
        var fontSizeGenres = round(12f * baseDensity)
        val bannerW = if (isLandscape) (screenHeight * 0.5f).toInt() else (actualWidth / 2.5f).toInt()
        val bannerH = (bannerW * 1.415f).toInt()
        val watchBtnW = min(round(240f * baseDensity).toInt(),(actualWidth - bannerW - 3 * marginLeft - marginLeftButton - hBtn))
        val favoriteBtnW = hBtn // Квадратная
        val totalRightBlockWidth = watchBtnW + marginLeftButton + favoriteBtnW
        val nameWidth = totalRightBlockWidth
        val genreContainerWidth = totalRightBlockWidth

        // Создание баннера
        var bannerContainerId = 0
        val bannerContainer = CardView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                bannerW,
                bannerH
            )
            layoutparams1.setMargins(marginLeft, marginTop+statusBarHeight,0,0)
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            val newId = View.generateViewId()
            id = newId
            bannerContainerId = newId
            radius = getAdaptiveRadius(bannerW, SizeType.MEDIUM)
            alpha = 0.3f
        }
        val banner = ImageView(context).apply {
            val layoutParams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams1.setMargins(0, 0,0,0)
            layoutParams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutParams1
            id = View.generateViewId()
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        val textWidth = watchBtnW - (subMarginLeftText.toFloat() * 1.5f).toInt()
        var textSize = 0f
        var textHeight = 0
        var textText = "Начать смотреть"
        for (i in 0 until parentCard.childs.size) {
            if (parentCard.childs[i].alreadyWatched != 0.toLong()) {
                textText = "Продолжить смотреть"
            }
        }
        val playIcoSize = round(hBtn / 2.7f).toInt()
        var playIcoId = 0
        // Подбор размера текста для кнопки "смотреть"
        for (i in 0 until steps.size) {
            val textSizee = steps[i]
            val res = optimizeText("Продолжить смотреть",  textWidth, textSizee, false, null, 1)
            val resStr = res.firstLine
            if ((resStr[resStr.lastIndex].toString() != ".") && res.totalHeight <= (playIcoSize.toFloat() * 1.3f).toInt()) {
                textHeight = res.totalHeight
                textSize = textSizee
                break
            }
            else if (i == steps.size-1 && textSize == 0f){
                textSize = round(12f*density)
            }
        }
        // Создание кнопок "смотреть" и "добавить в избранное"
        val watchButtonBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(if (textText == "Продолжить смотреть") {"#D54444".toColorInt()} else {"#59AFAFAF".toColorInt()})
            cornerRadius = getAdaptiveRadius(watchBtnW, SizeType.SMALL)
        }
        var watchButtonId = 0
        val watchButton = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                watchBtnW,
                hBtn
            )
            layoutparams1.startToEnd = bannerContainerId
            layoutparams1.bottomToBottom = bannerContainerId
            layoutparams1.setMargins(marginLeft,0,0,0)
            background = watchButtonBackground
            layoutParams = layoutparams1
            val newId = View.generateViewId()
            id = newId
            watchButtonId = newId
        }
        val playIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                playIcoSize,
                playIcoSize
            )
            layoutparams1.startToStart = watchButtonId
            layoutparams1.topToTop = watchButtonId
            layoutparams1.bottomToBottom = watchButtonId
            layoutparams1.setMargins(subMarginLeftText,0,0,0)
            setImageResource(R.drawable.play_ico)
            layoutParams = layoutparams1
            val newId = View.generateViewId()
            id = newId
            playIcoId = newId
        }
        val textWatchButton = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                textWidth,
                textHeight
            )
            layoutparams1.startToEnd = playIcoId
            layoutparams1.topToTop = watchButtonId
            layoutparams1.bottomToBottom = watchButtonId
            layoutparams1.setMargins(subMarginLeftText,0,0,0)
            layoutParams = layoutparams1
            includeFontPadding = false
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            text = textText
            setTextColor("#FFFFFF".toColorInt())
            this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_regular)
        }
        watchButton.addView(textWatchButton)
        watchButton.addView(playIco)
        val favourite = false
        val favouriteButtonBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(if (!favourite) {"#59AFAFAF".toColorInt()} else {"#D54444".toColorInt()})
            cornerRadius = getAdaptiveRadius(watchBtnW, SizeType.SMALL)
        }
        var favouriteButtonId = 0
        val favouriteButton = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                hBtn,
                hBtn
            )
            layoutparams1.startToEnd = watchButtonId
            layoutparams1.bottomToBottom = watchButtonId
            layoutparams1.setMargins(marginLeftButton,0,0,0)
            layoutParams = layoutparams1
            background = favouriteButtonBackground
            val newId = View.generateViewId()
            id = newId
            favouriteButtonId = newId
        }
        val favouriteIco = ImageView(context).apply {
            val size = (hBtn.toFloat() / 1.6f).toInt()
            val layoutparams1 = ConstraintLayout.LayoutParams(
                size,
                size
            )
            layoutparams1.startToStart = favouriteButtonId
            layoutparams1.endToEnd = favouriteButtonId
            layoutparams1.topToTop = favouriteButtonId
            layoutparams1.bottomToBottom = favouriteButtonId
            layoutParams = layoutparams1
            setImageResource(R.drawable.favourite_ico)
        }
        favouriteButton.addView(favouriteIco)

        // Создание названия
        var nameViewId = 0
        val nameView = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                nameWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.endToEnd = favouriteButtonId
            layoutparams1.topToTop = bannerContainerId
            layoutparams1.setMargins(marginLeft,0,0,0)
            text = name
            includeFontPadding = false
            setTextSize(TypedValue.COMPLEX_UNIT_PX, steps[4])
            layoutParams = layoutparams1
            setTextColor(resources.getColor(R.color.white))
            this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_bold)

            setShadowLayer(round(4f*density), round(4f*density), round(4f*density),
                "#80000000".toColorInt())
            val paint = Paint()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = round(1f*density)
            paint.color = Color.BLACK
            setLayerType(LAYER_TYPE_SOFTWARE, paint)

            val newId = View.generateViewId()
            id = newId
            nameViewId = newId
        }
        nameView.measure(
            View.MeasureSpec.makeMeasureSpec(nameWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val nameViewHeight = nameView.measuredHeight

        bannerContainer.addView(banner)
        container.addView(bannerContainer)
        container.addView(watchButton)
        container.addView(favouriteButton)
        container.addView(nameView)

        // Создание жанров и оптимизация названия (размера шрифта и возможно обрезание текста с добавлением кнопки "показать всё")
        val genreList = currentList[0].genre ?: emptyList()
        val infoContainerHeight = (hBtn / 1.5f).toInt()  // Было 1.25
        val infoTextFont = ResourcesCompat.getFont(context,R.font.google_sans_regular)
        for (i in 0 until steps.size) {
            val optimizatedText = optimizeText("Сj", nameWidth, steps[i], false, infoTextFont, 1)
            if (optimizatedText.totalHeight < (infoContainerHeight.toFloat() / 1.46f).toInt()) {
                fontSizeGenres = steps[i]
                break
            }
        }
        val newGenreList = mutableListOf<Pair<Boolean, Genre>>()
        val maxInfoSumHeight = round(bannerH.toFloat() / 2f).toInt() - hBtn - marginBetweenInfoElements
        for (i in genreList) {
            newGenreList.add(Pair(false, i))
        }
        val genreGrid = createGridOfGenres(context, infoContainerHeight, newGenreList, parentCard.length, parentCard.alreadyWatched, genreContainerWidth, maxInfoSumHeight, marginBetweenInfoElements, considerSelectedState = false, addShowAllButton = true, showAllButtonWidth = null, addClickListeners = false, onClick = {})

        val genreGridView = genreGrid.container
        for (i in 0 until genreGridView.childCount) {
            val cd = genreGridView.getChildAt(i)
            if (cd.tag == "show_all_info_button") {
                cd.setOnClickListener {
                    // Обработка клика
                }
            }
        }
        val lp1 = genreGridView.layoutParams as ConstraintLayout.LayoutParams
        lp1.topToBottom = nameViewId
        lp1.startToStart = nameViewId
        lp1.setMargins(0, marginBetweenInfoElements,0,0)
        genreGridView.layoutParams = lp1
        container.addView(genreGridView)
        val sumHeight = genreGrid.sumHeight

        // Подгоняем название (шрифт и возможно обрезание текста, с добавлением кнопки "показать всё")
        val maxSumHeight2 = bannerH - hBtn - marginBetweenInfoElements
        var sumHeight2 = nameViewHeight + sumHeight + marginBetweenInfoElements

        for (i in 0 until steps.size) {
            if (sumHeight2 > maxSumHeight2 && steps[i] >= fontSizeGenres) {
                nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, steps[i])
                nameView.measure(
                    View.MeasureSpec.makeMeasureSpec(nameWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                sumHeight2 = nameView.measuredHeight + sumHeight + marginBetweenInfoElements
            }
            else if (sumHeight2 <= maxSumHeight2) {
                break
            }
            else if (steps[i] < fontSizeGenres) {
                val tempTextView = TextView(context).apply {
                    layoutParams = ConstraintLayout.LayoutParams(
                        nameWidth,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = "Н"
                    includeFontPadding = false
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizeGenres)
                    setTextColor(resources.getColor(R.color.white))
                    this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_bold)

                    setShadowLayer(round(4f*density),
                        round(4f * density),
                        round(4f*density), "#80000000".toColorInt())
                    val paint = Paint()
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = round(1f*density)
                    paint.color = Color.BLACK
                    setLayerType(LAYER_TYPE_SOFTWARE, paint)
                }
                tempTextView.measure(
                    View.MeasureSpec.makeMeasureSpec(nameWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                val maxNameViewHeight = bannerH - hBtn - marginBetweenInfoElements*2 - sumHeight
                val tempTextViewSimvolWidth = tempTextView.measuredWidth
                val tempViewLineHeight = tempTextView.measuredHeight
                val tempViewSimvolsInLine = floor(nameWidth.toFloat() / tempTextViewSimvolWidth.toFloat()).toInt()
                val tempViewLines = floor(maxNameViewHeight.toFloat() / tempViewLineHeight.toFloat()).toInt()
                val tempViewPredictSimvols = tempViewSimvolsInLine * tempViewLines
                var tempTextViewNewText = ""
                for (i in 0 until tempViewPredictSimvols) {
                    tempTextViewNewText += name[i]
                }
                tempTextView.text = tempTextViewNewText
                tempTextView.measure(
                    View.MeasureSpec.makeMeasureSpec(nameWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                var tempTextViewHeight = tempTextView.measuredHeight
                if (tempTextViewHeight > maxNameViewHeight) {
                    while (tempTextViewHeight > maxNameViewHeight) {
                        tempTextViewNewText = tempTextViewNewText.removeRange(tempTextViewNewText.lastIndex .. tempTextViewNewText.lastIndex)
                        tempTextView.text = tempTextViewNewText
                        tempTextView.measure(
                            View.MeasureSpec.makeMeasureSpec(nameWidth, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        )
                        tempTextViewHeight = tempTextView.measuredHeight
                    }
                }
                else {
                    while (tempTextViewHeight < maxNameViewHeight) {
                        tempTextViewNewText += name[tempTextViewNewText.lastIndex + 1]
                        tempTextView.text = tempTextViewNewText
                        tempTextView.measure(
                            View.MeasureSpec.makeMeasureSpec(nameWidth, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        )
                        tempTextViewHeight = tempTextView.measuredHeight
                    }
                    if (tempTextViewHeight > maxNameViewHeight) {
                        while (tempTextViewHeight > maxNameViewHeight) {
                            tempTextViewNewText = tempTextViewNewText.removeRange(tempTextViewNewText.lastIndex .. tempTextViewNewText.lastIndex)
                            tempTextView.text = tempTextViewNewText
                            tempTextView.measure(
                                View.MeasureSpec.makeMeasureSpec(nameWidth, View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                            )
                            tempTextViewHeight = tempTextView.measuredHeight
                        }
                    }
                }
                nameView.text = tempTextViewNewText
                val fontt = ResourcesCompat.getFont(context, R.font.google_sans_bold)
                var simvols = resources.getString(R.string.showAll).length
                val paint1 = Paint().apply {
                    this.textSize = steps[steps.indexOf(fontSizeGenres) + 1]
                    this.typeface = fontt
                }
                val nmT = nameView.text.removeRange(nameView.text.lastIndex - simvols - 1 .. nameView.text.lastIndex).toString()
                val width1 = paint1.measureText("Показать всё")
                val paint2 = Paint().apply {
                    this.textSize = fontSizeGenres
                    this.typeface = fontt
                }

                var width2 = paint2.measureText(nmT)
                for (i in 0 until simvols) {
                    if (width2 > width1) {
                        width2 = paint2.measureText(resources.getString(R.string.showAll).removeRange(simvols-1..resources.getString(R.string.showAll).lastIndex))
                        simvols -= 1
                    }
                }
                nameView.text = nameView.text.toString().removeRange( if (nameView.text.toString().length < simvols) {0} else {nameView.text.toString().lastIndex-simvols-1} .. nameView.text.toString().lastIndex)
                showALlButtonInNameView = TextView(context).apply {
                    val layoutparams1 = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutparams1.endToEnd = nameViewId
                    layoutparams1.bottomToBottom = nameViewId
                    val text1 = SpannableString(resources.getString(R.string.showAll))
                    text1.setSpan(UnderlineSpan(), 0, text1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    text = text1
                    includeFontPadding = false
                    layoutparams1.setMargins(0,0,0,7)
                    setPadding(round(10f*density).toInt(),round(10f*density).toInt(),round(10f*density).toInt(),0)
                    val sizee = steps[steps.indexOf(fontSizeGenres) + 1]
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, sizee)
                    this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_bold)
                    setTextColor("#BF9C9C9C".toColorInt())
                    layoutParams = layoutparams1
                }
                var alreadyShowed1 = false
                var lastClickTime = System.currentTimeMillis()
                showALlButtonInNameView.setOnClickListener {
                    if (!alreadyShowed1 && System.currentTimeMillis() - lastClickTime > 100) {
                        showShowAllText(name, steps[steps.indexOf(fontSizeGenres)]) {
                                alreadyShowed ->
                            alreadyShowed1 = alreadyShowed
                        }
                    }
                    lastClickTime = System.currentTimeMillis()
                }
                container.addView(showALlButtonInNameView)
                break
            }
        }

        nameView.measure(
            View.MeasureSpec.makeMeasureSpec(nameWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        sumHeight2 = nameView.measuredHeight + sumHeight + marginBetweenInfoElements

        // Устанавливаем отступ названия от верха баннера
        val nameViewMarginTop = maxSumHeight2 - sumHeight2
        val layoutparams1 = nameView.layoutParams as ConstraintLayout.LayoutParams
        layoutparams1.setMargins(marginLeft, nameViewMarginTop,0,0)
        nameView.layoutParams = layoutparams1

        // Создаём и подгоняем описание (возможно обрезание текста, с добавлением кнопки "показать всё")
        val descriptionWidth = screenWidth - marginLeft - marginRight
        val maxDescriptionHeight = round(descriptionWidth.toFloat() / 2f).toInt()
        var descriptionViewId = 0
        val description = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                descriptionWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            text = descriptionText
            includeFontPadding = false
            this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_regular)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizeGenres)
            setTextColor("#BF9C9C9C".toColorInt())
            layoutparams1.startToStart = bannerContainerId
            layoutparams1.topToBottom = bannerContainerId
            layoutparams1.setMargins(0, marginBetweenInfoElements,0,0)
            layoutParams = layoutparams1
            val newId = View.generateViewId()
            id = newId
            descriptionViewId = newId
        }
        description.measure(
            View.MeasureSpec.makeMeasureSpec(descriptionWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val descriptionHeight = description.measuredHeight
        container.addView(description)
        if (descriptionHeight > maxDescriptionHeight) {
            val tempTextView = TextView(context).apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    descriptionWidth,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                text = "Н"
                includeFontPadding = false
                this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_regular)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizeGenres)
                setTextColor("#BF9C9C9C".toColorInt())
            }

            tempTextView.measure(
                View.MeasureSpec.makeMeasureSpec(descriptionWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            val tempTextViewSimvolWidth = tempTextView.measuredWidth
            val tempViewLineHeight = tempTextView.measuredHeight
            val tempViewSimvolsInLine = floor(descriptionWidth.toFloat() / tempTextViewSimvolWidth.toFloat()).toInt()
            val tempViewLines = floor(maxDescriptionHeight.toFloat() / tempViewLineHeight.toFloat()).toInt()
            val tempViewPredictSimvols = tempViewSimvolsInLine * tempViewLines
            var tempTextViewNewText = ""
            for (i in 0 until tempViewPredictSimvols) {
                tempTextViewNewText += descriptionText[i]
            }
            tempTextView.text = tempTextViewNewText
            tempTextView.measure(
                View.MeasureSpec.makeMeasureSpec(descriptionWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            var tempTextViewHeight = tempTextView.measuredHeight
            if (tempTextViewHeight > maxDescriptionHeight) {
                while (tempTextViewHeight > maxDescriptionHeight) {
                    tempTextViewNewText = tempTextViewNewText.removeRange(tempTextViewNewText.lastIndex .. tempTextViewNewText.lastIndex)
                    tempTextView.text = tempTextViewNewText
                    tempTextView.measure(
                        View.MeasureSpec.makeMeasureSpec(descriptionWidth, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    tempTextViewHeight = tempTextView.measuredHeight
                }
            }
            else {
                while (tempTextViewHeight < maxDescriptionHeight) {
                    tempTextViewNewText += descriptionText[tempTextViewNewText.lastIndex + 1]
                    tempTextView.text = tempTextViewNewText
                    tempTextView.measure(
                        View.MeasureSpec.makeMeasureSpec(descriptionWidth, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    tempTextViewHeight = tempTextView.measuredHeight
                }
                if (tempTextViewHeight > maxDescriptionHeight) {
                    while (tempTextViewHeight > maxDescriptionHeight) {
                        tempTextViewNewText = tempTextViewNewText.removeRange(tempTextViewNewText.lastIndex .. tempTextViewNewText.lastIndex)
                        tempTextView.text = tempTextViewNewText
                        tempTextView.measure(
                            View.MeasureSpec.makeMeasureSpec(descriptionWidth, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        )
                        tempTextViewHeight = tempTextView.measuredHeight
                    }
                }
            }
            description.text = tempTextViewNewText
            val showAllButtonInDescription = TextView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                layoutparams1.endToEnd = descriptionViewId
                layoutparams1.bottomToBottom = descriptionViewId
                val text1 = SpannableString(resources.getString(R.string.showAll))
                text1.setSpan(UnderlineSpan(), 0, text1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                text = text1
                includeFontPadding = false
                layoutparams1.setMargins(0,0,0,7)
                setPadding(round(10f*density).toInt(),round(10f*density).toInt(),round(10f*density).toInt(),0)
                val sizee = steps[steps.indexOf(fontSizeGenres) + 1]
                setTextSize(TypedValue.COMPLEX_UNIT_PX, sizee)
                this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_bold)
                setTextColor("#BF9C9C9C".toColorInt())
                layoutParams = layoutparams1
            }
            showAllButtonInDescription.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val fontt = ResourcesCompat.getFont(context, R.font.google_sans_bold)
            var simvols = resources.getString(R.string.showAll).length
            val paint1 = Paint().apply {
                this.textSize = steps[steps.indexOf(fontSizeGenres) + 1]
                this.typeface = fontt
            }
            val nmT = description.text.removeRange(description.text.lastIndex - simvols-1 .. description.text.lastIndex).toString()
            val width1 = paint1.measureText("Показать всё")
            val paint2 = Paint().apply {
                this.textSize = fontSizeGenres
                this.typeface = fontt
            }

            var width2 = paint2.measureText(nmT)
            for (i in 0 until simvols) {
                if (width2 > width1) {
                    width2 = paint2.measureText(nmT.removeRange(simvols-1..nmT.lastIndex))
                    simvols -= 1
                }
            }
            description.text = description.text.removeRange(if (description.text.toString().length < simvols) {0} else {description.text.toString().lastIndex-simvols-1} .. description.text.toString().lastIndex)
            var alredyShowed1 = false
            var lastClickTime = 0.toLong()
            showAllButtonInDescription.setOnClickListener {
                if (!alredyShowed1 && System.currentTimeMillis() - lastClickTime > 100) {
                    showShowAllText(descriptionText, fontSizeGenres) {
                            alreadyShowed ->
                        alredyShowed1 = alreadyShowed
                    }
                }
                lastClickTime = System.currentTimeMillis()
            }
            container.addView(showAllButtonInDescription)
        }

        // Создаём "разделительный" раздел (разделительную линию, названия разделов (эпизоды и сезоны) и кнопки (пойск и дополнительное)
        val razdelLineWidth = screenWidth - marginLeft - marginRight - marginLeftButton*2 - hBtn*2
        val razdelLineHeight = (hBtn.toFloat() / 7.2f).toInt()
        val maxRazdelsNamesHeight = (hBtn.toFloat() / 1.3f).toInt()
        val razdelsNamesMarginFromRazdelLineLeftRight = round(razdelLineWidth.toFloat() / 12f).toInt()
        val razdelsNamesMarginFromRazdelLineBottom = marginBetweenInfoElements
        val altRazdelLineWidth = round(razdelLineWidth.toFloat() / 2.5f).toInt()
        var razdelLineId = 0
        var altrazdelLineId = 0
        var searchButtonOnRazdelLineId = 0
        var razdelsNameTextSize = steps[0]

        val razdelLineBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor("#59AFAFAF".toColorInt())
            cornerRadius = getAdaptiveRadius(razdelLineWidth, SizeType.XLARGE)
        }
        val altRazdelLineBaclground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor("#4D290088".toColorInt())
            cornerRadius = getAdaptiveRadius(razdelLineWidth, SizeType.XLARGE)
        }
        val razdelLine = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                razdelLineWidth,
                razdelLineHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToBottom = descriptionViewId
            layoutparams1.setMargins(marginLeft, marginTop,0,0)
            layoutParams = layoutparams1
            setImageDrawable(razdelLineBackground)
            val newId = View.generateViewId()
            id = newId
            razdelLineId = newId
        }
        container.addView(razdelLine)
        val altRazdelLine = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                altRazdelLineWidth,
                razdelLineHeight
            )
            layoutparams1.startToStart = razdelLineId
            layoutparams1.topToTop = razdelLineId
            layoutParams = layoutparams1
            val newId = View.generateViewId()
            id = newId
            altrazdelLineId = newId
            setImageDrawable(altRazdelLineBaclground)
        }
        container.addView(altRazdelLine)
        val episodesTextView = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToStart = razdelLineId
            layoutparams1.bottomToBottom = razdelLineId
            layoutparams1.setMargins(razdelsNamesMarginFromRazdelLineLeftRight, 0,0,razdelsNamesMarginFromRazdelLineBottom)
            text = resources.getString(R.string.episodes)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, razdelsNameTextSize)
            setTextColor(activeRazdelTextColor)
            includeFontPadding = false
            this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_bold)
            layoutParams = layoutparams1
        }
        episodesTextView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        var episodesTextViewHeight = episodesTextView.measuredHeight
        for (i in 0 until steps.size) {
            if (episodesTextViewHeight > maxRazdelsNamesHeight) {
                episodesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, steps[i])
                razdelsNameTextSize = steps[i]
                episodesTextView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                episodesTextViewHeight = episodesTextView.measuredHeight
            }
        }

        val lp2 = razdelLine.layoutParams as ConstraintLayout.LayoutParams
        lp2.setMargins(marginLeft, marginTop + round(15f*density).toInt() + episodesTextViewHeight,0,0)
        razdelLine.layoutParams = lp2
        container.addView(episodesTextView)
        val sezonsTextView = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.endToEnd = razdelLineId
            layoutparams1.bottomToBottom = razdelLineId
            layoutparams1.setMargins(0,0,razdelsNamesMarginFromRazdelLineLeftRight,razdelsNamesMarginFromRazdelLineBottom)
            layoutParams = layoutparams1
            text = resources.getString(R.string.sezons)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, razdelsNameTextSize)
            includeFontPadding = false
            setTextColor(nonactiveRazdelTextColor)
            this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_bold)
        }
        container.addView(sezonsTextView)

        val buttonContainersDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor("#59AFAFAF".toColorInt())
            cornerRadius = getAdaptiveRadius(hBtn, SizeType.SMALL)
        }
        val searchButtonContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                hBtn,
                hBtn
            )
            layoutparams1.startToEnd = razdelLineId
            layoutparams1.topToTop = razdelLineId
            val marginTop = ((hBtn - razdelLineHeight).toFloat() / 2f).toInt()
            layoutparams1.setMargins(marginLeftButton, -marginTop,0,0)
            layoutParams = layoutparams1
            background = buttonContainersDrawable
            val newId = View.generateViewId()
            id = newId
            searchButtonOnRazdelLineId = newId
        }
        val searchButtonIco = ImageView(context).apply {
            val size = round(hBtn / 1.39f).toInt()
            val layoutparams1 = ConstraintLayout.LayoutParams(
                size,
                size
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            val margins = ((hBtn - size).toFloat() / 2f).toInt()
            layoutparams1.setMargins(margins,margins,margins,margins)
            layoutParams = layoutparams1
            setImageResource(R.drawable.search_ico)
            imageTintList = ColorStateList.valueOf("#D9D9D9".toColorInt())
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        searchButtonContainer.addView(searchButtonIco)
        container.addView(searchButtonContainer)
        val extraButtonContainerOnRazdelLine = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                hBtn,
                hBtn
            )
            layoutparams1.startToEnd = searchButtonOnRazdelLineId
            layoutparams1.topToTop = searchButtonOnRazdelLineId
            layoutparams1.setMargins(marginLeftButton,0,0,0)
            layoutParams = layoutparams1
            background = buttonContainersDrawable
        }
        val extraButtonIcoOnRazdelLine = ImageView(context).apply {
            val size = round(hBtn / 1.2f).toInt() // БЫЛО 1.39
            val layoutparams1 = ConstraintLayout.LayoutParams(
                size,
                size
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setImageResource(R.drawable.vert_dots)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        extraButtonContainerOnRazdelLine.addView(extraButtonIcoOnRazdelLine)
        extraButtonContainerOnRazdelLine.setOnClickListener {
            var allEpisodesAreWatched = true
            for (i in currentList[0].childs) {
                if (i.alreadyWatched != i.length) {
                    allEpisodesAreWatched = false
                    break
                }
            }
            val bsdDialogExtraButton = listOf<BottomSheetDialogElement>(
                BottomSheetDialogElement.Button(
                    tag = BsdButtonsTags.animePage_extraButton_changeAllEpisodesWatchedMark,
                    icoId = if (allEpisodesAreWatched) R.drawable.close_ico else R.drawable.check_ico,
                    text = if (allEpisodesAreWatched) "Пометить все эпизоды как непросмотренные" else "Пометить все эпизоды как просмотренные",
                    openThePage = false
                ),
                BottomSheetDialogElement.Button(
                    tag = BsdButtonsTags.animePage_extraButton_changeAllEpisodesWatchedMark,
                    icoId = if (allEpisodesAreWatched) R.drawable.close_ico else R.drawable.check_ico,
                    text = "Удалить",
                    openThePage = false
                ),
                BottomSheetDialogElement.Slider(
                    tag = BsdButtonsTags.animePage_extraButton_changeAllEpisodesWatchedMark,
                    icoId = R.drawable.close_ico,
                    text = "Пометить все эпизоды как непросмотренные",
                    stops = listOf(0f, 1f),
                    createSteps = false
                ),
                BottomSheetDialogElement.Slider(
                    tag = BsdButtonsTags.animePage_extraButton_changeAllEpisodesWatchedMark,
                    icoId = R.drawable.close_ico,
                    text = "Удалить",
                    stops = listOf(0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f),
                    createSteps = true
                ),
                BottomSheetDialogElement.SegmentedButton(
                    icoId = R.drawable.close_ico,
                    text = "Пометить все эпизоды как непросмотренные",
                    sizeType = SizeType.SMALL,
                    options = listOf(Pair(segmentedButtonOptions("", R.drawable.check_ico, true), BsdButtonsTags.animePage_extraButton_changeAllEpisodesWatchedMark), Pair(segmentedButtonOptions("", R.drawable.check_ico, false), BsdButtonsTags.animePage_extraButton_changeAllEpisodesWatchedMark), Pair(segmentedButtonOptions("", R.drawable.check_ico, false), BsdButtonsTags.animePage_extraButton_changeAllEpisodesWatchedMark))
                ),
                BottomSheetDialogElement.DropdownRow(
                    icoId = R.drawable.close_ico,
                    text = "Удалить",
                    buttonIcoId = R.drawable.check_ico,
                    options = listOf(Pair("Русский", BsdButtonsTags.animePage_extraButton_changeAllEpisodesWatchedMark), Pair("Английский", BsdButtonsTags.animePage_extraButton_changeAllEpisodesWatchedMark), Pair("Китайский",BsdButtonsTags.animePage_extraButton_changeAllEpisodesWatchedMark))
                )
            )
            _bsdFlow.tryEmit(bsdDialogExtraButton)
        }
        container.addView(extraButtonContainerOnRazdelLine)

        val episodesAmount = parentCard.childs.size
        val episodesInOneLine = if (isLandscape) 3 else 2
        val episodeWidth = (screenWidth - (marginLeft * (episodesInOneLine + 1))) / episodesInOneLine
        val episodeHeight = (episodeWidth / 1.78f).toInt()
        var episodeNumTextSize = fontSizeGenres
        val episodesContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                screenWidth - marginLeft*2,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.topToBottom = searchButtonOnRazdelLineId
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(marginLeft, marginLeft,0,marginLeft)
            layoutParams = layoutparams1
        }
        var k = 0
        var lastFirstViewId2 = 0
        var lastViewId2 = 0
        for (i in 0 until episodesAmount) {
            val obj = parentCard.childs[episodesAmount-i-1]
            val episodeContainer = ConstraintLayout(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    episodeWidth,
                    episodeHeight
                )
                val newId = View.generateViewId()
                id = newId
                if (k == 0) {
                    layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    k += episodesInOneLine
                    lastFirstViewId2 = newId
                    lastViewId2 = newId
                }
                else if (i != k) {
                    layoutparams1.startToEnd = lastViewId2
                    layoutparams1.topToTop = lastFirstViewId2
                    layoutparams1.setMargins(marginLeft, 0,0,0)
                    lastViewId2 = newId
                }
                else {
                    layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutparams1.topToBottom = lastFirstViewId2
                    layoutparams1.setMargins(0,marginLeft,0,0)
                    lastFirstViewId2 = newId
                    lastViewId2 = newId
                    k += episodesInOneLine
                }
                layoutParams = layoutparams1
            }
            episodeContainer.setOnClickListener {
                val link = obj.link
                if (link != null) {
                    if (link.type == LinkType.CONTENT) {
                        val path = link.contentPath
                        if (path != null) {
                            openVideo(obj.id)
                        }
                    }
                }
            }


            val cardView = CardView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams = layoutparams1
                radius = round(25f*density)
            }
            val blurEffect = RenderEffect.createBlurEffect(
                20f,20f, Shader.TileMode.MIRROR
            )
            val watched = if (obj.alreadyWatched != 0.toLong()) {true} else if (obj.length == 0.toLong()) {true} else {false}
            if (!watched) {
                cardView.setRenderEffect(blurEffect)
            }
            if (obj.alreadyWatched == obj.length){
                val watchedText = TextView(context).apply {
                    val layoutparams1 = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutparams1.setMargins(spaceBetween,round(spaceBetween.toFloat() / 2f).toInt(),0,0)
                    layoutParams = layoutparams1
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, episodeNumTextSize)
                    this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_regular)
                    text = "Просмотрен"
                    setTextColor("#FFFFFF".toColorInt())
                    setShadowLayer(4f, 4f, 4f, "#80000000".toColorInt())
                    val paint = Paint()
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 1f
                    paint.color = Color.BLACK
                    setLayerType(LAYER_TYPE_SOFTWARE, paint)
                    elevation = 10f
                }
                episodeContainer.addView(watchedText)
            }



            val image = ImageView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams = layoutparams1
                scaleType = ImageView.ScaleType.CENTER_CROP
                loadImage(obj.image)
            }

            cardView.addView(image)
            episodeContainer.addView(cardView)
            episodesContainer.addView(episodeContainer)
            val lengthText = TextView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams = layoutparams1
                text = convertToStringTime(obj.length)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, if (steps.indexOf(episodeNumTextSize) < steps.lastIndex) { steps[steps.indexOf(episodeNumTextSize)+1] } else {episodeNumTextSize})
                setTextColor("#99FFFFFF".toColorInt())
                this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_regular)
                includeFontPadding = false

            }
            lengthText.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val lengthDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor("#99000000".toColorInt())
                cornerRadius = round(10f*density)
            }
            val lengthContainer = ConstraintLayout(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    lengthText.measuredWidth+round(20f*density).toInt(),
                    lengthText.measuredHeight+round(10f*density).toInt()
                )
                layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.setMargins(0,0,spaceBetween, spaceBetween)
                layoutParams = layoutparams1
                background = lengthDrawable
                elevation = 10f
            }
            lengthContainer.addView(lengthText)
            episodeContainer.addView(lengthContainer)
            var episodeNumId = 0
            val episodeNum = "Эпизод ${episodesAmount-i}"
            val episodeNumView = TextView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.setMargins(spaceBetween, 0,0,spaceBetween)
                layoutParams = layoutparams1
                text = episodeNum
                setTextColor(resources.getColor(R.color.white))
                setTextSize(TypedValue.COMPLEX_UNIT_PX, episodeNumTextSize)
                this.typeface = ResourcesCompat.getFont(context, R.font.google_sans_medium)

                includeFontPadding = false
                setShadowLayer(4f, 4f, 4f, "#80000000".toColorInt())
                val paint = Paint()
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 1f
                paint.color = Color.BLACK
                setLayerType(LAYER_TYPE_SOFTWARE, paint)
                elevation = 10f
                val newId = View.generateViewId()
                id = newId
                episodeNumId = newId
            }
            episodeContainer.addView(episodeNumView)
            val episodeName = TextView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                layoutparams1.startToStart = episodeNumId
                layoutparams1.bottomToTop = episodeNumId
                includeFontPadding = false
                layoutParams = layoutparams1
                setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizeGenres)
                this.typeface = infoTextFont
                text = optimizeText(if (obj.name == null) {""} else {obj.name!!}, episodeWidth-spaceBetween*2, if (steps.indexOf(episodeNumTextSize) < steps.lastIndex) { steps[steps.indexOf(episodeNumTextSize)+1] } else {episodeNumTextSize}, false, infoTextFont, 1).firstLine
                setTextColor("#BF9C9C9C".toColorInt())
                setShadowLayer(4f, 4f, 4f, "#80000000".toColorInt())
                val paint = Paint()
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 1f
                paint.color = Color.BLACK
                setLayerType(LAYER_TYPE_SOFTWARE, paint)
                elevation = 10f
            }
            episodeContainer.addView(episodeName)


            val extraButton = ImageView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    (hBtn.toFloat() / 2f).toInt(),
                    (hBtn.toFloat() / 2f).toInt()
                )
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.setMargins(0, spaceBetween,spaceBetween,0)
                layoutParams = layoutparams1
                elevation = 10f
                setImageResource(R.drawable.vert_dots)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            episodeContainer.addView(extraButton)
            val alreadyWatchedLineDrawableNotAllWatched = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor("#da1d37".toColorInt())
                cornerRadii = floatArrayOf(
                    0f, 0f,  // top-left
                    0f, 0f,  // top-right
                    0f, 0f,    // bottom-right
                    round(25f*density), round(25f*density)     // bottom-left
                )
            }
            val alreadyWatchedLineDrawableAllWatched = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor("#da1d37".toColorInt())
                cornerRadii = floatArrayOf(
                    0f, 0f,  // top-left
                    0f, 0f,  // top-right
                    round(25f*density), round(25f*density),    // bottom-right
                    round(25f*density), round(25f*density)     // bottom-left
                )
            }
            Log.d("ERQWRERWREWREW", "${obj.length}   ${obj.alreadyWatched}")
            val alreadyWatchedLine = ImageView(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    if (obj.alreadyWatched != obj.length) {(episodeWidth.toFloat() * (obj.alreadyWatched.toFloat() / obj.length.toFloat())).toInt()} else {episodeWidth},
                    (episodeHeight.toFloat() / 50f).toInt()
                )
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams = layoutparams1
                background = if (obj.alreadyWatched == obj.length) {alreadyWatchedLineDrawableAllWatched} else {alreadyWatchedLineDrawableNotAllWatched}
                elevation = 11f
            }
            episodeContainer.addView(alreadyWatchedLine)
        }

        val noScrollLinearManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        val sezonsRecycler = RecyclerView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.topToBottom = searchButtonOnRazdelLineId
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,marginLeft,0,0)
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
            layoutManager = noScrollLinearManager
            layoutParams = layoutparams1
            adapter = sezonsAdapter
        }
        container.addView(sezonsRecycler)
        container.addView(episodesContainer)
        holder.sezonsRecycler = sezonsRecycler
        holder.episodesContainer = episodesContainer
        holder.sezonsTextView = sezonsTextView
        holder.episodesTextView = episodesTextView
        holder.altRazdelLine = altRazdelLine
        holder.razdelLineWidth = razdelLineWidth
        holder.altRazdelLineWidth = altRazdelLineWidth
        episodesTextView.setOnClickListener {
            setState(0)
        }
        sezonsTextView.setOnClickListener {
            setState(1)
        }
        val layer = findLayerByLayerObjectId(getItem(position).id)
        if (layer.first is Layer.AnimePage) {
            setState((layer.first as Layer.AnimePage).state)
        }

        val drawBackgroundBlobs = ImageRequest.Builder(context)
            .data(if (parentCard.image != null) { if (parentCard.image!!.source == ImageSource.URL) {parentCard.image!!.value}  else if (parentCard.image!!.source == ImageSource.DEVICE)  {if (parentCard.image!!.value.startsWith("content://")) parentCard.image!!.value.toUri() else File(parentCard.image!!.value)} else {parentCard.image!!.value.toInt()} } else {R.drawable.anime_1})
            .allowHardware(false)
            .target { drawable ->
                // Картинка загрузилась, превращаем в Bitmap
                val bitmap = (drawable as BitmapDrawable).bitmap
                // Генерируем палитру...
                Palette.from(bitmap).generate { palette ->
                    val color1 = palette?.getVibrantColor(Color.MAGENTA) ?: Color.MAGENTA
                    val color2 = palette?.getDominantColor(Color.BLUE) ?: Color.BLUE
                    val color3 = palette?.getLightVibrantColor(Color.YELLOW) ?: Color.YELLOW

                    for (i in 0 until 3) {
                        var view = ImageView(context)
                        var color = 0
                        when (i) {
                            0 -> {
                                view = blob1
                                color = color1
                            }

                            1 -> {
                                view = blob2
                                color = color2
                            }

                            2 -> {
                                view = blob3
                                color = color3
                            }
                        }
                        color = ColorUtils.setAlphaComponent(color, (255 * 0.3).toInt())
                        val gradientDrawable = ShapeDrawable(OvalShape()).apply {
                            val colors = intArrayOf(color, ColorUtils.setAlphaComponent(getDeepDarkColor(color), (255 * 0.0).toInt()))
                            val positions = floatArrayOf(0.0f, 1f)

                            shaderFactory = object : ShapeDrawable.ShaderFactory() {
                                override fun resize(p0: Int, p1: Int): Shader {
                                    return RadialGradient(
                                        view.width / 2f, view.height / 2f, // Центр
                                        view.width / delitRad,             // Радиус
                                        colors,
                                        positions,
                                        Shader.TileMode.CLAMP
                                    )
                                }
                            }
                        }
                        view.apply {
                            background = gradientDrawable
                        }
                        view.setRenderEffect(baseBlurEffectForBloobs)
                    }
                }


                banner.setBackgroundColor(resources.getColor(android.R.color.transparent))
                bannerContainer.alpha = 1f
                banner.setImageBitmap(bitmap)
            }
            .listener(
                onError = { _, result ->
                    Log.e("CoilError", "Ошибка загрузки: ${result.throwable}")
//                    bannerContainer.setBackgroundColor(Color.parseColor("#59AFAFAF"))
                }
            )
            .listener(
                onStart = {
                    banner.setBackgroundColor("#AFAFAF".toColorInt())
                }
            )
            .build()
        context.imageLoader.enqueue(drawBackgroundBlobs)
    }

}

class animePageSezonsPageAdapter(private val context: Context, private val clickOnCard: (objectData2) -> Unit): ListAdapter<animePageSezonsAdapterListFormat, animePageSezonsPageAdapter.ViewHolder>(ObjectDiffCallback2()) {
    class ViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ConstraintLayout(context).apply {
            val layoutParams1 = RecyclerView.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = layoutParams1
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.constraintLayout.removeAllViews()
        val isLandscape = screenWidth > screenHeight
        val maxPageWidth = round(1000f * baseDensity).toInt() // Лимит для планшетов
        val actualWidth = min(screenWidth, maxPageWidth) // Чтобы не растягивалось в бесконечность
        val marginBetweenElementsHorizontal = round(16f * baseDensity).toInt()  // С.м onBindViewHolder у animePageAdapter -> marginLeft
        val amountOfCards = if (isLandscape) 4 else 2

        val item = getItem(position)
        val parentCard = getItem(position).obj
        parentCard.childsShowName = item.settingsState.showChildsName
        parentCard.childsNamePosition = item.settingsState.childsNamePosition
        parentCard.marginBetweenElementsHorizontal = marginBetweenElementsHorizontal

        val layout = createGridOfChilds(parentCard.childs, amountOfCards, context, round(actualWidth.toFloat() - marginBetweenElementsHorizontal*2).toInt(), parentCard)
        val layoutparams1 = layout.layoutParams as ConstraintLayout.LayoutParams
        layoutparams1.setMargins(marginBetweenElementsHorizontal, 0,0,0)
        layout.layoutParams = layoutparams1
        holder.constraintLayout.addView(layout)
        for (i in 0 until layout.childCount) {
            val obj = layout.getChildAt(i)
            val objData = parentCard.childs[i]
            obj.setOnClickListener {
                clickOnCard(objData)
            }
        }
    }
}

class flatGridOfEditEpisodesAdapter(private val context: Context, private val items: MutableList<episodeInfo>, private val widthh: Int, private val itemHeightt: Int, private val changes: (MutableList<episodeInfo>) -> Unit, private val changeImage: (Int) -> Unit): RecyclerView.Adapter<flatGridOfEditEpisodesAdapter.ViewHolder>() {
    class ViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    lateinit var touchHelper: ItemTouchHelper

    override fun onCreateViewHolder(p0: ViewGroup, position: Int): flatGridOfEditEpisodesAdapter.ViewHolder {
        return ViewHolder(ConstraintLayout(context).apply {
            val layoutParams1 = RecyclerView.LayoutParams(
                widthh,
                itemHeightt
            )
            layoutParams = layoutParams1
        })
    }

    fun addEpisode(newEpisode: episodeInfo) {
        items.add(newEpisode)
        notifyItemInserted(items.size - 1)
        changes(items)
    }
    fun removeEpisode(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size - position)
            changes(items)
        }
    }
    fun changeEpisodeName(newName: String, position: Int) {
        if (position >= 0 && position < items.size) {
            items[position].name = newName
            changes(items)
        }
    }
    fun changeEpisodeBanner(newImage: ImageData, position: Int) {
        if (position >= 0 && position < items.size) {
            items[position].image = newImage
            changes(items)
            notifyItemChanged(position)
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.constraintLayout.removeAllViews()
        val boldFont = context.resources.getFont(R.font.google_sans_bold)
        val hTextHeight = round(itemHeightt.toFloat() / 2.55f).toInt()
        val textHeight = round(itemHeightt.toFloat() / 3.8f).toInt()
        val hTextSizee = getTextSizeByHeight(hTextHeight, boldFont)
        val textSizee = getTextSizeByHeight(textHeight, boldFont)
        val hTextColor = "#FFFFFF".toColorInt()
        val textColor = "#BFAFAFAF".toColorInt()
        val icoSize = round(itemHeightt.toFloat() / 1.5f).toInt()
        val margin = round(20f*density).toInt()
        val deleateIcoSize = round(icoSize.toFloat() / 1.6f).toInt()
        val container = holder.constraintLayout
        val addBgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(itemHeightt, SizeType.SMALL)
            setColor("#BF1B1B1B".toColorInt())
            setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
        }
        val imageAddIco = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                itemHeightt,
                itemHeightt
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setImageResource(R.drawable.add_ico)
            imageTintList = ColorStateList.valueOf("#B0B0B0".toColorInt())
            scaleType = ImageView.ScaleType.CENTER_CROP
            elevation = 100f
        }
        val imageView = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                itemHeightt,
                itemHeightt
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            id = View.generateViewId()
            layoutParams = layoutparams1
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        val cardView = CardView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                itemHeightt,
                itemHeightt
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            id = View.generateViewId()
            layoutParams = layoutparams1
            radius = getAdaptiveRadius(itemHeightt, SizeType.SMALL)
        }
        cardView.addView(imageView)
        val imageContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                itemHeightt,
                itemHeightt
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            id = View.generateViewId()
            layoutParams = layoutparams1
        }
        fun newImage(image: ImageData?) {
            if (image != null) {
                imageView.loadImage(image)
                imageAddIco.alpha = 0f
                cardView.alpha = 1f
            }
            else {
                cardView.alpha = 0f
                imageAddIco.alpha = 1f
                imageContainer.background = addBgDrawable
            }
        }
        newImage(items[position].image)
        imageContainer.addView(imageAddIco)
        imageContainer.addView(cardView)
        container.addView(imageContainer)
        val hT = TextInputLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0,0,0,0)
            layoutparams1.startToEnd = imageContainer.id
            layoutparams1.topToTop = imageContainer.id
            isHintEnabled = false
            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_NONE
            minimumHeight = 0
            layoutParams = layoutparams1
            id = View.generateViewId()
            maxWidth = widthh - icoSize - itemHeightt - margin*3 - deleateIcoSize
        }
        val hTI = TextInputEditText(hT.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0,0,0,0)
            setTextColor(hTextColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX,hTextSizee)
            ellipsize = TextUtils.TruncateAt.END
            setHintTextColor(hTextColor)
            hint = "Без названия"
            maxWidth = widthh - icoSize - itemHeightt - margin*3 - deleateIcoSize
            maxLines = 1
            isSingleLine = true
            if (items[position].name != "") {
                setText(items[position].name)
            }
        }

        fun clearHTIFocus() {
            val actualPosition = holder.adapterPosition
            if (actualPosition != RecyclerView.NO_POSITION) {
                hTI.clearFocus()
                hideKeyboardd(hTI)
                changeEpisodeName(hTI.text.toString(), actualPosition)
            }
        }
        hTI.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clearHTIFocus()
                true
            } else {
                false
            }
        }
        hTI.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Проверяем, что мы всё еще редактируем ту же позицию
                val actualPosition = holder.adapterPosition
                if (actualPosition != RecyclerView.NO_POSITION) {
                    changeEpisodeName(hTI.text.toString(), actualPosition)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        hT.addView(hTI)
        hT.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val textView = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToStart = hT.id
            layoutparams1.topToBottom = hT.id
            layoutParams = layoutparams1
            ellipsize = TextUtils.TruncateAt.END
            text = convertToStringTime(items[position].length)
            setTextColor(textColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            maxWidth = widthh - icoSize - itemHeightt - margin*2
        }
        val sumTextsHeight = hT.measuredHeight + textView.measuredHeight
        val textsMarginTop = round((itemHeightt - sumTextsHeight).toFloat() / 6f).toInt() * 5
        val lp1 = hT.layoutParams as ConstraintLayout.LayoutParams
        lp1.setMargins(margin, textsMarginTop,0,0)
        hT.layoutParams = lp1
        container.addView(hT)
        container.addView(textView)
        val dragicoView = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                icoSize,
                icoSize
            )
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = imageContainer.id
            layoutparams1.bottomToBottom = imageContainer.id
            layoutParams = layoutparams1
            setImageResource(R.drawable.drag_handle_ico)
            imageTintList = ColorStateList.valueOf("#D9D9D9".toColorInt())
            scaleType = ImageView.ScaleType.CENTER_CROP
            id = View.generateViewId()
        }
        container.addView(dragicoView)

        val deleateIcoView = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                deleateIcoSize,
                deleateIcoSize
            )
            layoutparams1.endToStart = dragicoView.id
            layoutparams1.topToTop = dragicoView.id
            layoutparams1.bottomToBottom = dragicoView.id
            layoutparams1.setMargins(0,0,margin,0)
            layoutParams = layoutparams1
            setImageResource(R.drawable.delete_ico)
            imageTintList = ColorStateList.valueOf("#852221".toColorInt())
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        container.addView(deleateIcoView)
        deleateIcoView.setOnClickListener {
            val actualPosition = holder.adapterPosition
            if (actualPosition != RecyclerView.NO_POSITION) {
                removeEpisode(actualPosition)
            }
        }
        dragicoView.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper.startDrag(holder)
                if (hTI.hasFocus()) {
                    clearHTIFocus()
                }
            }
            false
        }
        holder.constraintLayout.isFocusable = true
        holder.constraintLayout.isFocusableInTouchMode = true
        holder.constraintLayout.setOnClickListener {
        }
        imageContainer.setOnClickListener {
            val actualPosition = holder.adapterPosition
            if (actualPosition != RecyclerView.NO_POSITION) {
                changeImage(actualPosition)
            }
            if (hTI.hasFocus()) {
                clearHTIFocus()
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class spaceItemDecoration(private val info: spaceItemDecorationInput) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: android.graphics.Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position != 0) {
            outRect.left = info.spaces[0]
            outRect.top = info.spaces[1]
            outRect.right = info.spaces[2]
            outRect.bottom = info.spaces[3]
        }
        else {
            outRect.left = info.firstObjectSpaces[0]
            outRect.top = info.firstObjectSpaces[1]
            outRect.right = info.firstObjectSpaces[2]
            outRect.bottom = info.firstObjectSpaces[3]
        }
    }
}
