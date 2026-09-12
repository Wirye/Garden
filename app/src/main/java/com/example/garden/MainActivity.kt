package com.example.garden

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.garden.appsettings.AuthManager
import com.example.garden.database.CardSize
import com.example.garden.database.CarouselType
import com.example.garden.database.CollectionType
import com.example.garden.database.ElementType
import com.example.garden.database.GridGenreItem
import com.example.garden.database.ImageData
import com.example.garden.database.LayoutType
import com.example.garden.database.LinkData
import com.example.garden.database.PageType
import com.example.garden.database.SizeType
import com.example.garden.ui.screens.ChapterInfo
import com.example.garden.ui.screens.EpisodeInfo
import com.example.garden.ui.screens.MainScreen
import com.example.garden.ui.screens.PageWithSearchInput
import com.example.garden.ui.theme.GardenTheme
import com.example.garden.utils.getValidLayerId
import com.example.garden.viewmodel.AuthViewModel
import com.example.garden.viewmodel.LayersViewModel
import com.example.garden.viewmodel.MainViewModel
import com.example.garden.viewmodel.ResultSenderViewModel
import com.example.garden.viewmodel.utils.viewModelFactory
import kotlinx.parcelize.Parcelize

sealed class Layer (
    val id: Long = getValidLayerId(),
    var firstElementPosition: Int = 0
) : Parcelable {

    @Parcelize
    data class AnimePage(
        var mainRecyclerScrollPositionInPx: Int,
        val layoutObjId: Long,
        var state: Int,
    ) : Layer()
    @Parcelize
    data class MainPage(
        val page: PageType,
        val scrollPositionCarousels: MutableMap<Long, Int>,
        var mainRecyclerScrollPosition: Int
    ) : Layer()
    @Parcelize
    data class OverLay(
        val info: OverLayLayer,
    ) : Layer()
    @Parcelize
    data class VideoPlayer(
        var isPlaying: Boolean,
        var playbackSpeed: Float = 1.0f,
        var isControllerVisible: Boolean = true,
    ) : Layer()
    @Parcelize
    data class CreateCardPage(
        val parentId: Long,
        val cardId: Long? = null,
        var name: String,
        var image: ImageData? = null,
        var description: String,
        var author: String,
        var genreList: List<GridGenreItem>,
        var episodesList: List<EpisodeInfo>,
        var cardType: ElementType,
        var chaptersList: List<ChapterInfo>,
        var cardsList: List<Long>,
        var horizontalVideo: LinkData? = null,
        var verticalVideo: LinkData? = null,
        var song: LinkData? = null,
        var carouselType: CarouselType,
    ) : Layer()
    @Parcelize
    data class CreateCarouselPage(
        val localLayer: MainPage = MainPage(PageType.Home, mutableMapOf(), 0),
        var name: String,
        val page: PageType,
        var childsCornerRadius: SizeType?,
        var childsShowName: Boolean,
        var childsNamePosition: Int?,
        var childsShowAlreadyWatchedLine: Boolean,
        var layoutType: LayoutType,
        var objectsInOneLine: Int?,
        var maxLines: Int?,
        var dovodchik: Boolean,
        var showDovodchikDots: Boolean,
        var carouselType: CarouselType,
        var carouselCollectionType: CollectionType? = null,
        var showIco: Boolean = false,
        var ico: ImageData? = null,
        var adaptiveGridSize: Boolean = false,
        var maxObjectsInOneLineForAdaptiveSize: Int? = null,
        var maxLinesForAdaptiveSize: Int? = null,
        var childsSize: CardSize = CardSize.MEDIUM,
        var childsShowAuthor: Boolean = false,
        val carouselId: Long? = null
    ) : Layer()
    @Parcelize
    data class PageWithSearch (
        var startsInfo: PageWithSearchInput,
        val key: String
    ): Layer()

    @Parcelize
    data class AppSettings(
        val nothing: Int = 0,
    ): Layer()

    @Parcelize
    data class AniLibertyLoginPage(
        val nothing: Int = 0,
    ) : Layer()
}

@Parcelize
sealed class OverLayLayer : Parcelable {
    @Parcelize
    data class GenreChoice(
        var genreList: List<Pair<Boolean, GridGenreItem>>,
        val key: String
    ) : OverLayLayer()
}
object ResultKeys {
    var lastPageWithSearchId = 0L
    fun getPageWithSearchKey(): String {
        val res = lastPageWithSearchId
        lastPageWithSearchId += 1L
        return "PAGE_WITH_SEARCH_${res}"
    }

    var lastPageWithSearchEditId = 0L
    fun getPageWithEditSearchKey(): String {
        val res = lastPageWithSearchEditId
        lastPageWithSearchEditId += 1L
        return "PAGE_WITH_SEARCH_EDIT_${res}"
    }
}

@Immutable
data class CustomColors(
    val closeButton: Color = Color(0xffdb4242),
    val onCloseButton: Color = Color(0xffffffff),
    val placeholder: Color = Color(0x59AFAFAF),
)

val LocalCustomColors = staticCompositionLocalOf { CustomColors() }

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        val app = application as App
        viewModelFactory {
            MainViewModel(app.dao, app.groupsDao)
        }
    }
    private val layersViewModel: LayersViewModel by viewModels()
    private val resultSenderViewModel: ResultSenderViewModel by viewModels()

    private val authViewModel: AuthViewModel by viewModels {
        viewModelFactory {
            AuthViewModel(AuthManager(applicationContext))
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            viewModel
            val windowSizeClass = calculateWindowSizeClass(this)
            GardenTheme(windowSizeClass = windowSizeClass) {
                val customColors = remember { CustomColors() }
                CompositionLocalProvider(LocalCustomColors provides customColors) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen(layersViewModel, resultSenderViewModel, authViewModel)
                    }
                }
            }
        }
    }
}
