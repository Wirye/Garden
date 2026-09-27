package com.example.garden.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.example.garden.R
import com.example.garden.database.entities.PageType
import com.example.garden.ui.components.icons.AnimeIco
import com.example.garden.ui.components.icons.AnimeIcoFill
import com.example.garden.ui.components.icons.HomeIco
import com.example.garden.ui.components.icons.HomeIcoFill
import com.example.garden.ui.components.icons.MangaIco
import com.example.garden.ui.components.icons.MangaIcoFill
import com.example.garden.ui.components.icons.MusicIco
import com.example.garden.ui.components.icons.MusicIcoFill
import com.example.garden.ui.theme.dimens

private data class NavigationItem(
    val page: PageType,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelRes: Int,
    val onClick: () -> Unit
)

@Composable
fun MainPageBottomBar(
    active: Boolean = true,
    heightState: (Dp) -> Unit = {},
    onHomePage: () -> Unit,
    onAnimePage: () -> Unit,
    onMusicPage: () -> Unit,
    onMangaPage: () -> Unit,
    pageState: PageType
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    val items = listOf(
        NavigationItem(
            page = PageType.Home,
            selectedIcon = HomeIcoFill,
            unselectedIcon = HomeIco,
            labelRes = R.string.home,
            onClick = onHomePage
        ),
        NavigationItem(
            page = PageType.Anime,
            selectedIcon = AnimeIcoFill,
            unselectedIcon = AnimeIco,
            labelRes = R.string.anime,
            onClick = onAnimePage
        ),
        NavigationItem(
            page = PageType.Music,
            selectedIcon = MusicIcoFill,
            unselectedIcon = MusicIco,
            labelRes = R.string.music,
            onClick = onMusicPage
        ),
        NavigationItem(
            page = PageType.Manga,
            selectedIcon = MangaIcoFill,
            unselectedIcon = MangaIco,
            labelRes = R.string.manga,
            onClick = onMangaPage
        )
    )

    NavigationBar(
        modifier = Modifier.onGloballyPositioned {
            heightState(with(density) { it.size.height.toDp() })
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
    ) {
        items.forEach { item ->
            val selected = pageState == item.page
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (active) {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        item.onClick()
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(item.labelRes),
                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.labelRes),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
