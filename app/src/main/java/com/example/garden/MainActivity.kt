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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.garden.appsettings.AuthManager
import com.example.garden.database.AppDatabase
import com.example.garden.database.entities.ArtistType
import com.example.garden.database.entities.CardSize
import com.example.garden.database.entities.CarouselType
import com.example.garden.database.entities.ChapterInfo
import com.example.garden.database.entities.CollectionType
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.EpisodeInfo
import com.example.garden.database.entities.GridGenreItem
import com.example.garden.database.entities.ImageData
import com.example.garden.database.entities.LayoutType
import com.example.garden.database.entities.LinkData
import com.example.garden.database.entities.PageType
import com.example.garden.database.entities.PlayListType
import com.example.garden.database.entities.SizeType
import com.example.garden.repository.aniLibriaSearch.AniLibriaSearchRepository
import com.example.garden.repository.aniLibriaSearch.AniLibriaSearchRepositoryImpl
import com.example.garden.repository.artists.ArtistsRepository
import com.example.garden.repository.artists.ArtistsRepositoryImpl
import com.example.garden.repository.auth.AuthRepositoryImpl
import com.example.garden.repository.objects.ObjectRepositoryImpl
import com.example.garden.repository.recentQueries.RecentQueriesRepositoryImpl
import com.example.garden.repository.uiRepository.UiRepositoryImpl
import com.example.garden.repository.webObjects.WebObjectsRepositoryImpl
import com.example.garden.ui.screens.MainScreen
import com.example.garden.ui.screens.PageWithSearchInput
import com.example.garden.ui.theme.GardenTheme
import com.example.garden.utils.getValidLayerId
import com.example.garden.viewmodel.AuthViewModel
import com.example.garden.viewmodel.LayersViewModel
import com.example.garden.viewmodel.MainViewModel
import com.example.garden.viewmodel.RecentQueriesViewModel
import com.example.garden.viewmodel.ResultSenderViewModel
import com.example.garden.viewmodel.SearchViewModel
import com.example.garden.viewmodel.utils.customViewModelFactory
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
        val scrollPositionCarousels: MutableMap<String, Int>,
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
        val cardId: String? = null,
        val cardPosition: Int = -1,
        var name: String,
        var image: ImageData? = null,
        var description: String,
        var authorsList: List<String>,
        var artistType: ArtistType? = null,
        var genreList: List<GridGenreItem>,
        var episodesList: List<EpisodeInfo>,
        var cardType: ElementType,
        var chaptersList: List<ChapterInfo>,
        var cardsList: List<Long>,
        var horizontalVideo: LinkData? = null,
        var song: LinkData? = null,
        var carouselType: CarouselType,
        var playlistType: PlayListType,
    ) : Layer()
    @Parcelize
    data class CreateCarouselPage(
        val localLayer: MainPage = MainPage(PageType.Home, mutableMapOf(), 0),
        var name: String,
        val page: PageType,
        var childsCornerRadius: SizeType,
        var childsShowName: Boolean,
        var childsNamePosition: Int,
        var childsShowAlreadyWatchedLine: Boolean,
        var layoutType: LayoutType,
        var objectsInOneLine: Int?,
        var maxLines: Int?,
        var dovodchik: Boolean,
        var showDovodchikDots: Boolean,
        var carouselType: CarouselType,
        var carouselCollectionType: CollectionType,
        var showIco: Boolean = false,
        var ico: ImageData? = null,
        var adaptiveGridSize: Boolean = false,
        var maxObjectsInOneLineForAdaptiveSize: Int? = null,
        var maxLinesForAdaptiveSize: Int? = null,
        var childsSize: CardSize = CardSize.MEDIUM,
        var childsShowAuthor: Boolean = false,
        val carouselId: String? = null,
        val carouselPosition: Int = -1
    ) : Layer()
    @Parcelize
    data class PageWithSearch (
        var startsInfo: PageWithSearchInput,
        val key: String
    ): Layer()

    @Parcelize
    data class UnspecifiedPage(
        val nothing: Int = 0
    ) : Layer()

    @Parcelize
    data class AppSettings(
        val nothing: Int = 0
    ): Layer()

    @Parcelize
    data class AniLibertyLoginPage(
        val nothing: Int = 0
    ) : Layer()
}
@Parcelize
enum class GlobalSearchState : Parcelable {
    Searching, Found
}

@Parcelize
sealed class OverLayLayer : Parcelable {
    @Parcelize
    data class GlobalSearch(
        var state: GlobalSearchState
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

    const val NO_DATA = "NO_DATA"

    const val GLOBAL_SEARCH_STATE_CHANGED_TO_SEARCHING = "GLOBAL_SEARCH_STATE_CHANGED_TO_SEARCHING"
}

@Immutable
data class CustomColors(
    val closeButton: Color = Color(0xffdb4242),
    val onCloseButton: Color = Color(0xffffffff),
    val placeholder: Color = Color(0x59AFAFAF),
)

val LocalCustomColors = staticCompositionLocalOf { CustomColors() }

class MainActivity : ComponentActivity() {
    private val db by lazy {
        AppDatabase.getDatabase(applicationContext)
    }

    private val repository by lazy {
        ObjectRepositoryImpl(db, db.objectDataDao())
    }

    private val recentQueriesRepository by lazy {
        RecentQueriesRepositoryImpl(db.resentQueriesDao())
    }

    private val authManager by lazy {
        AuthManager(context = applicationContext,
            appScope = (applicationContext as App).applicationScope
        )
    }

    private val authRepository by lazy {
        AuthRepositoryImpl(authManager = authManager)
    }

    private val webObjectsRepository by lazy {
        WebObjectsRepositoryImpl(db.webObjectDataDao())
    }

    private val uiRepository by lazy {
        UiRepositoryImpl(objectRepository = repository, webObjectsRepository = webObjectsRepository)
    }

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(repository = repository, uiRepository = uiRepository) as T
            }
        }
    }
    private val layersViewModel: LayersViewModel by viewModels()
    private val resultSenderViewModel: ResultSenderViewModel by viewModels()

    private val authViewModel: AuthViewModel by viewModels {
        customViewModelFactory {
            AuthViewModel(authRepository = authRepository)
        }
    }

    private val recentQueriesViewModel: RecentQueriesViewModel by viewModels {
        customViewModelFactory {
            RecentQueriesViewModel(repository = recentQueriesRepository)
        }
    }

    private val aniLibriaSearchRepository: AniLibriaSearchRepository by lazy {
        AniLibriaSearchRepositoryImpl(authManager = authManager)
    }

    private val artistRepository: ArtistsRepository by lazy {
        ArtistsRepositoryImpl(dao = db.objectDataDao())
    }

    private val searchViewModel: SearchViewModel by lazy {
        SearchViewModel(objectRepository = repository, aniLibriaSearchRepository = aniLibriaSearchRepository, artistsRepository = artistRepository)
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.insertSavedCarousel() // IT'S VERY IMPORTANT THING

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
            val windowSizeClass = calculateWindowSizeClass(this)
            GardenTheme(windowSizeClass = windowSizeClass) {
                val customColors = remember { CustomColors() }
                CompositionLocalProvider(LocalCustomColors provides customColors) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen(
                            layersViewModel = layersViewModel,
                            resultSenderViewModel = resultSenderViewModel,
                            authViewModel = authViewModel,
                            recentQueriesViewModel = recentQueriesViewModel,
                            searchViewModel = searchViewModel
                        )
                    }
                }
            }
        }
    }
}
