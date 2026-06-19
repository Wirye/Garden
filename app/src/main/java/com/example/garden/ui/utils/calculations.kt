package com.example.garden.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.example.garden.R
import com.example.garden.baseDensity
import com.example.garden.database.SizeType
import com.example.garden.density
import com.example.garden.listDot
import com.example.garden.objectData2
import com.example.garden.screenWidth
import com.example.garden.steps
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.round

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
fun calculateCardWidth(items: List<objectData2>, font: Typeface?, textSizee: Float, showName: Boolean, namePosition: Int?, position: Int): Int {
    var width: Int
    var cardHeight: Int
    var cardWidth = 0

    // Рассчитываем размеры карточки в зависимости от её типа и отображаемых элементов (имя, автор). Скрипт из createCard
    val size1 = cardScaleCalcFree(items, 50, 30, items[position].width, items[position].height, screenWidth)
    if (items[position].author != null && items[position].name != null && showName && namePosition == 0) {
        // Карточка с именем и автором под ней
        cardHeight = size1.second - optimizeText(
            items[position].name!!,
            size1.first,
            textSizee,
            false,
            font
        ).totalHeight * 3
        cardWidth = (cardHeight.toFloat() * (size1.first.toFloat() / size1.second)).toInt()
    } else if (items[position].author == null && items[position].name != null && showName && namePosition == 0) {
        // Карточка только с именем под ней
        cardHeight = size1.second - optimizeText(
            items[position].name!!,
            size1.first,
            textSizee,
            false,
            font
        ).totalHeight * 2
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
@SuppressLint("InternalInsetResource")
fun getStatusBarHeight(context: Context): Int {
    var result = 0
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = context.resources.getDimensionPixelSize(resourceId)
    }
    return result
}