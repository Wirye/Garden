package com.example.garden

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import android.content.Intent
import android.content.res.Configuration
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.ScrollView
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.round
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlin.math.floor
import com.example.garden.database.ElementType
import com.example.garden.database.Genre
import com.example.garden.database.ImageData
import com.example.garden.database.ImageSource
import com.example.garden.database.LinkData
import kotlinx.coroutines.launch
import kotlin.math.ceil
import com.example.garden.database.groups.GroupsData
import com.example.garden.viewmodel.MainViewModel
import com.example.garden.appsettings.AnimeSettingsState
import com.example.garden.database.SizeType
import com.example.garden.viewmodel.MultiViewModelFactory
import kotlin.getValue
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.example.garden.database.LinkType
import androidx.core.net.toUri
import com.example.garden.database.MusicGenre
import com.example.garden.database.ObjectData
import com.example.garden.players.AnimeVideoPlayer
import com.example.garden.ui.adapters.AnimePageAdapter
import com.example.garden.ui.adapters.CarouselsAdapter
import com.example.garden.viewmodel.ResultSenderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.isNotEmpty
import com.example.garden.ui.adapters.AnimePageSezonsPageAdapter
import com.example.garden.ui.adapters.FlatGridOfEditEpisodesAdapter
import com.example.garden.ui.factories.BottomSheetDialogElement
import com.example.garden.ui.factories.bottomSheetDialogFactory
import com.example.garden.ui.utils.OverLayLayer
import com.example.garden.ui.utils.spaceItemDecoration
import com.example.garden.ui.utils.createOvDialog
import com.example.garden.ui.utils.createDotDrawables
import com.example.garden.ui.utils.findMainPageLayerByPageId
import com.example.garden.ui.utils.findLayerByLayerObjectId
import com.example.garden.ui.utils.findVideoPlayerLayer
import com.example.garden.ui.utils.findLayerByElevation
import com.example.garden.ui.utils.calcRecyclerViewHeight
import com.example.garden.ui.utils.optimizeText
import com.example.garden.ui.utils.system.changeOrientation
import com.example.garden.ui.utils.system.toggleSystemBars
import com.example.garden.ui.utils.viewExtensions.loadImage
import com.example.garden.ui.utils.viewExtensions.lifecycleOwner
import com.example.garden.ui.utils.getStatusBarHeight
import com.example.garden.ui.utils.drawables.blobInit
import com.example.garden.ui.utils.spaceItemDecorationInput
import com.example.garden.ui.adapters.animePageSezonsAdapterListFormat

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
var groupsList = listOf<GroupsData>()
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
enum class fileType {
    IMAGE, VIDEO, AUDIO
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
        val adapter1 = CarouselsAdapter(this, addCardToCarousel = { addCardToCarousel(it) }, clickOnItem = { showPage(infoOfPageToShow.infoOfAnimePage(it.id, true)) })
        val animePageSezonsPageAdapter = AnimePageSezonsPageAdapter(this, clickOnCard = { showPage(infoOfPageToShow.infoOfAnimePage(it.id, true)) } )
        val animePageAdapter = AnimePageAdapter(this, showShowAllText = { text, size, callback -> showShowAllText(text, size, callback) }, animePageSezonsPageAdapter, openVideo = { showPage(infoOfPageToShow.infoOfVideoPlayer(it, true)) })
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
                                    val textViewHeight = optimizeText(
                                        if (objectsList[i].name != null) {
                                            objectsList[i].name!!
                                        } else {
                                            "Молчаливая ведьма"
                                        }, 200, 50f, false, null, 1
                                    ).totalHeight
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
                                                val adapter = recycler.adapter as FlatGridOfEditEpisodesAdapter
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
                                                val adapter = recycler.adapter as FlatGridOfEditEpisodesAdapter
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
                            val cardData = ObjectData(
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
