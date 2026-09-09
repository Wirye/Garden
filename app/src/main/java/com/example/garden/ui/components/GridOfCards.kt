package com.example.garden.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.example.garden.database.ObjectData2
import com.example.garden.database.SizeType
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.getAspectRatio

@Composable
fun GridOfCards(
    allCardsAmount: Int,
    gridInfo: ObjectData2,
    maxLines: Int,
    maxObjectsInOneLine: Int, // Card sizes will be calculated based on this parameter.
    // Please calculate this parameter in advance (for example, for layoutType = 2 or something similar)

    lineWidth: Dp, // Excluding indents, grid width will be lineWidth - (paddingStart + paddingEnd)
    paddingStart: Dp,
    paddingEnd: Dp,
    marginBetweenElements: Dp,
    onCardClick: (ObjectData2) -> Unit,
    onCardEdit: (ObjectData2) -> Unit,
    onCardDelete: (Long) -> Unit
) {
    val items = gridInfo.childs.sortedBy { it.position }
    if (items.isNotEmpty()) {
        val gridWidth = lineWidth - (paddingStart + paddingEnd)
        val totalSpacing = marginBetweenElements * (maxObjectsInOneLine - 1)
        val cardWidth = (gridWidth - totalSpacing) / maxObjectsInOneLine
        val aspectRatio = items.first().elementType.getAspectRatio()

        val cardsAmountOnFirstGrid = (maxObjectsInOneLine * maxLines).coerceAtMost(allCardsAmount)

        val extraCardsAmount = cardsAmountOnFirstGrid - items.size

        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(start = paddingStart, end = paddingEnd),
            horizontalArrangement = Arrangement.spacedBy(marginBetweenElements),
            verticalArrangement = Arrangement.spacedBy(marginBetweenElements),
            maxItemsInEachRow = maxObjectsInOneLine,
            maxLines = maxLines
        ) {
            items.forEach { item ->
                Card(
                    width = cardWidth,
                    name = item.name,
                    author = item.author,
                    aspectRatio = aspectRatio,
                    showName = gridInfo.childsShowName,
                    showAuthor = gridInfo.childsShowAuthor,
                    namePosition = gridInfo.childsNamePosition ?: 0,
                    length = item.length,
                    alreadyWatched = item.alreadyWatched,
                    showAlreadyWatchedLine = gridInfo.childsShowAlreadyWatchedLine,
                    image = item.image,
                    cornerRadius = gridInfo.childsCornerRadius ?: SizeType.ESMALL,
                    layoutType = item.layoutType,
                    onClick = { onCardClick(item) },
                    onEdit = { onCardEdit(item) },
                    onDelete = { onCardDelete(item.id) }
                )
            }

            repeat(extraCardsAmount) {
                val item = remember(items) { items.first() }
                Box(
                    modifier = Modifier.blockGestures().graphicsLayer {
                        this.alpha = 0f
                    }
                ) {
                    Card(
                        width = cardWidth,
                        name = item.name,
                        author = item.author,
                        aspectRatio = aspectRatio,
                        showName = gridInfo.childsShowName,
                        showAuthor = gridInfo.childsShowAuthor,
                        namePosition = gridInfo.childsNamePosition ?: 0,
                        length = item.length,
                        alreadyWatched = item.alreadyWatched,
                        showAlreadyWatchedLine = gridInfo.childsShowAlreadyWatchedLine,
                        image = item.image,
                        cornerRadius = gridInfo.childsCornerRadius ?: SizeType.ESMALL,
                        layoutType = item.layoutType,
                        onClick = { },
                        onEdit = { onCardEdit(item) },
                        onDelete = { onCardDelete(item.id) }
                    )
                }
            }
        }
    }
}