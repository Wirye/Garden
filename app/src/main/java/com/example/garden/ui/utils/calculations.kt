package com.example.garden.ui.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.example.garden.R
import com.example.garden.baseDensity
import com.example.garden.database.ElementType
import com.example.garden.database.SizeType
import com.example.garden.listDot
import com.example.garden.objectData2
import com.example.garden.screenWidth
import com.example.garden.steps
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

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

        if (baseWidth >= round(320f * baseDensity).toInt()) {
            amountCards = round(((lineWidth.toFloat()-((marginStartAndEnd*3)+marginStartAndEnd-margin))/baseWidth))
            prres = round(((lineWidth.toFloat() - (marginStartAndEnd*3) - (margin * (amountCards-1))) / amountCards)).toInt()
        }
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
fun calcRecyclerViewHeight(context: Context, items: List<objectData2>, position: Int, lineWidth: Int? = null): Int {
    val lineW = lineWidth ?: screenWidth
    val parent = items[position]
    val items = uploadLayoutTypeToCarouselChilds(parent.childs, parent, lineW)
    var res = -1
    val font = ResourcesCompat.getFont(context, R.font.google_sans_medium)
    val textSizee = floor(15f * baseDensity)
    for (i in 0 until items.size) {
        var height = 0
        if (items[i].layoutType == null || items[i].layoutType == 1 || items[i].childs.isEmpty()) {
            val size = cardScaleCalcFree(items, round(19f * baseDensity).toInt(), round(11f * baseDensity).toInt(), items[i].width, items[i].height, lineW)
            height = size.second
        }
        else {
            var paddingHorizontal = items[i].paddingHorizontal
            if (paddingHorizontal == null) {
                paddingHorizontal = round(19f * baseDensity).toInt()
            }
            var size = Triple(0,0,0f)
            val objList = items[i].childs
            val lineWidth = (lineW-paddingHorizontal*2)
            val maxObjectsInOneLine = items[i].objectsInOneLine
            var margin = round(4f * baseDensity).toInt()
            if (items[i].marginBetweenElementsHorizontal != null) {
                margin = items[i].marginBetweenElementsHorizontal!!
            }
            var marginV = round(4f * baseDensity).toInt()
            if (items[i].marginBetweenElementsVertical != null) {
                marginV = items[i].marginBetweenElementsVertical!!
            }
            for (i in objList) {
                if (i.width != null && i.height != null) {
                    size = cardScaleCalcForGrid(i.width, i.height, lineWidth, margin, maxObjectsInOneLine)
                    val extraHeightSize = 0 + (optimizeText(context.getString(R.string.StringForMaxHeightCalculate), size.first, textSizee, false, font).totalHeight) * (0 + if (parent.childsShowName) 1 else 0 + if (parent.childsShowAuthor) 1 else 0)
                    size = Triple(size.first, size.second + extraHeightSize, size.third)
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
fun calcItemPosInPxByPos(items: List<objectData2>, position: Int, itemPosition: Int, context: Context, lineWidth: Int? = null): Int {
    val parent = items[position]
    val lineW = lineWidth ?: screenWidth
    val items = uploadLayoutTypeToCarouselChilds(parent.childs, parent, lineW)
    var paddingHorizontal = parent.paddingHorizontal
    if (paddingHorizontal == null) {
        paddingHorizontal = round(19f * baseDensity).toInt()
    }
    var marginBetweenElementsHorizontal = parent.marginBetweenElementsHorizontal
    if (marginBetweenElementsHorizontal == null) {
        marginBetweenElementsHorizontal = round(11f * baseDensity).toInt()
    }
    var res = paddingHorizontal
    val r = if (items.size < itemPosition) {items.size} else {itemPosition}
    for (i in 0 until r) {
        val obj = items[i]
        val width = obj.width
        val height = obj.height
        val author = obj.author
        val showName = parent.childsShowName
        val showAuthor = parent.childsShowAuthor
        val namePosition = parent.childsNamePosition
        val name = obj.name
        val font = ResourcesCompat.getFont(context, R.font.google_sans_medium)
        val textSizee = round(25f*baseDensity)
        if (obj.layoutType == null || obj.layoutType == 1 || obj.childs.isEmpty()) {
            val cardWidth = calculateCardWidth(items, font, showName, showAuthor, namePosition, i, context, lineW, paddingHorizontal, marginBetweenElementsHorizontal)
            res += cardWidth + marginBetweenElementsHorizontal
        }
        else {
            val width = lineW
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
fun calculateAmountOfDots(items: List<objectData2>, paddingHorizontal: Int, marginBetweenElementsHorizontal: Int, context: Context, showName: Boolean, showAuthor: Boolean, namePosition: Int?, lineWidth: Int): List<listDot> {
    // Инициализируем список точек, первая точка всегда находится в позиции 0 (начало)
    val res = mutableListOf<listDot>(listDot(0))
    var sumWidth = paddingHorizontal
    var lastDotPosInPx = 0
    var lastElementPositionInPx: Int
    val font = ResourcesCompat.getFont(context, R.font.google_sans_medium)

    // ЭТАП 1: Расчет полной ширины всего контента (sumWidth)
    // Это необходимо для определения границ прокрутки и финальной точки.
    var layoutType = 1
    for (i in 0 until  items.size) {
        val width = calculateCardWidth(items, font, showName, showAuthor, namePosition, i, context,lineWidth)
        // Если элемент — сетка (layoutType == 0), рассчитываем ширину всей группы
        if (items[i].layoutType == 0) { layoutType = 0 }
        // Накапливаем общую ширину с учетом горизонтальных отступов
        sumWidth += width + if (items[i].layoutType != 0) marginBetweenElementsHorizontal else 0
    }

    // Корректируем sumWidth, убирая лишние отступы в конце
    sumWidth -= if (layoutType != 0) marginBetweenElementsHorizontal else paddingHorizontal
    lastElementPositionInPx = if (layoutType != 0) paddingHorizontal else 0

    // ЭТАП 2: Определение позиций точек
    var sumWidth2 = if (layoutType != 0) paddingHorizontal else 0
    var lastElementPosition = 0
    while (true) {
        // Создаём виртуальное "окно", каждый новый срез начинается в позиции последней точки
        var srez = lastDotPosInPx..lineWidth+lastDotPosInPx
        if (srez.last > sumWidth) {
            srez = lastDotPosInPx..sumWidth
        }
        // Проходим по элементам, начиная с последней точки
        for (i in lastElementPosition until items.size) {
            val width = calculateCardWidth(items, font, showName, showAuthor, namePosition, i, context,lineWidth)

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
                    if (res[o].itemPositionInPx == sumWidth2 - if (layoutType != 0) marginBetweenElementsHorizontal else 0) {
                        k = true
                    }
                }

                // Если начало элемента попадает в текущий срез и точки еще нет
                if (sumWidth2 - (if (layoutType != 0) marginBetweenElementsHorizontal else 0) in srez && !k) {
                    val itemPositionInPx = sumWidth2 - if (layoutType != 0) marginBetweenElementsHorizontal else 0
                    // Проверяем, чтобы при прокрутке к этой точке мы не увидели "пустоту" за пределами контента
                    if ((sumWidth + ( if (layoutType != 0) paddingHorizontal else 0)) - itemPositionInPx >= lineWidth) {
                        res.add(listDot(itemPositionInPx))
                    }
                    else {
                        // Если контент заканчивается, ставим точку так, чтобы экран упирался в правый край
                        res.add(listDot(sumWidth + (if (layoutType != 0) paddingHorizontal else 0) - lineWidth))
                    }
                    lastDotPosInPx = itemPositionInPx
                    lastElementPosition = i
                    lastElementPositionInPx = sumWidth2
                    break // Нашли новую точку — начинаем новый цикл
                }
                else {
                    // Иначе ставим точку по самому краю текущего среза
                    val itemPositionInPx = srez.last
                    if ((sumWidth + (if (layoutType != 0) paddingHorizontal else 0)) - itemPositionInPx >= lineWidth) {
                        res.add(listDot(itemPositionInPx))
                    }
                    else {
                        res.add(listDot(sumWidth + (if (layoutType != 0) paddingHorizontal else 0) - lineWidth))
                    }
                    lastDotPosInPx = itemPositionInPx
                    lastElementPosition = i
                    sumWidth2 = itemPositionInPx
                    break
                }
            }

            // Двигаем "курсор" текущей позиции ширины
            if (sumWidth2 != lastElementPositionInPx) {
                sumWidth2 = lastElementPositionInPx + width + if (layoutType != 0) marginBetweenElementsHorizontal else 0
            }
            else {
                sumWidth2 += width + if (layoutType != 0) marginBetweenElementsHorizontal else 0
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
    val widthDp = widthPx / baseDensity
    val multiplier = when (type) {
        SizeType.SMALL -> 1.5f
        SizeType.MEDIUM -> 2.4f
        SizeType.LARGE -> 3.6f
        SizeType.XLARGE -> 5.2f
    }
    val adaptiveFactor = widthDp.toDouble().pow(0.33).toFloat()
    val resultRadius = multiplier * adaptiveFactor * baseDensity
    val minLimit = when(type) {
        SizeType.SMALL -> 2f * baseDensity
        else -> 4f * baseDensity
    }
    return resultRadius.coerceAtLeast(minLimit)
}
fun getTextSizeByHeight(height: Int, font: Typeface? = null, context: Context): Float {
    var textSizee = steps[7]
    for (i in steps) {
        val res = optimizeText(context.getString(R.string.StringForMaxHeightCalculate), 1000, i, false, font, 1)
        if (res.totalHeight <= height) {
            textSizee = i
            break
        }
    }
    return textSizee
}
fun calculateCardWidth(items: List<objectData2>, font: Typeface?, showName: Boolean, showAuthor: Boolean, namePosition: Int?, position: Int, context: Context, lineWidth: Int? = null, paddingHorizontal: Int? = null, marginBetweenElementsHorizontal: Int? = null): Int {
    if (items[position].layoutType == 0) return if (items[position].childs.isNotEmpty()) lineWidth ?: screenWidth else 0
    val lineW = lineWidth ?: screenWidth
    var width: Int
    var cardHeight: Int
    var cardWidth = 0

    val textSizee = floor(15f * baseDensity)
    // Рассчитываем размеры карточки в зависимости от её типа и отображаемых элементов (имя, автор). Скрипт из createCard
    val size1 = cardScaleCalcFree(items, (paddingHorizontal ?: (round(19f * baseDensity).toInt())), (marginBetweenElementsHorizontal ?: round(11f * baseDensity).toInt()), items[position].width, items[position].height, lineW)
    if (showAuthor && showName && namePosition == 0) {
        // Карточка с именем и автором под ней
        cardHeight = size1.second - optimizeText(
            context.getString(R.string.StringForMaxHeightCalculate),
            size1.first,
            textSizee,
            false,
            font
        ).totalHeight * 3
        cardWidth = (cardHeight.toFloat() * (size1.first.toFloat() / size1.second)).toInt()
    } else if (!showAuthor && showName && namePosition == 0) {
        // Карточка только с именем под ней
        cardHeight = size1.second - optimizeText(
            context.getString(R.string.StringForMaxHeightCalculate),
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
    return cardWidth
}

sealed class CalculateIdealButtonWidthByHeightInput {
    data class M3ButtonInput(
        val height: Int,
        val text: String,
        val name: String,
        val sizeType: SizeType,
        val icoId: Int? = null,
        val dropDownMode: Boolean = false,
        val font: Typeface? = null,
    ) : CalculateIdealButtonWidthByHeightInput()

    data class SegmentedButtonInput(
        val height: Int,
        val options: List<segmentedButtonOptions>,
        val font: Typeface? = null
    ) : CalculateIdealButtonWidthByHeightInput()

    data class SwitchButtonInput(
        val height: Int
    ) : CalculateIdealButtonWidthByHeightInput()
}
fun calculateIdealButtonWidthByHeight(context: Context, buttonInfo: CalculateIdealButtonWidthByHeightInput, paddingHorizontal: Int): Int {

    return when (buttonInfo) {
        is CalculateIdealButtonWidthByHeightInput.M3ButtonInput -> {
            val nameHeight = if (buttonInfo.name.isEmpty()) {0} else {(buttonInfo.height.toFloat() / 3f).roundToInt().coerceIn(0, (12f * baseDensity).roundToInt())}
            val buttonHeight = buttonInfo.height - nameHeight

            val textRatio = when (buttonInfo.sizeType) {
                SizeType.XLARGE -> 3.4f
                SizeType.LARGE -> 3.0f
                SizeType.MEDIUM -> 2.6f
                SizeType.SMALL -> 2.2f
            }

            val textHeight = (buttonHeight / textRatio).roundToInt()
            val textSize = getTextSizeByHeight(textHeight, buttonInfo.font, context = context)
            val icoSize = (textSize * 1.5f).roundToInt().coerceIn(0, buttonHeight)
            val arrowMarginRight = (1.4f * baseDensity * textRatio).roundToInt()

            val tempTextView = TextView(context).apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                text = buttonInfo.text
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                typeface = buttonInfo.font
                measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
            }
            val textWidth = tempTextView.measuredWidth

            val iconPartWidth = if (buttonInfo.icoId != null) {(icoSize+(arrowMarginRight*2))} else {0}
            val dropDownPartWidth = if (buttonInfo.dropDownMode) {(icoSize+(arrowMarginRight*2))} else {0}
            val extraWidth = if (buttonInfo.dropDownMode && buttonInfo.icoId == null) {arrowMarginRight * 3} else {0}
            val contentWidth = iconPartWidth + textWidth + extraWidth + dropDownPartWidth
            val idealWidth = paddingHorizontal * 2 + contentWidth

            idealWidth
        }

        is CalculateIdealButtonWidthByHeightInput.SegmentedButtonInput -> {
            var totalWidth = 0
            var maxTextByLength = ""
            var availableIcoId: Int? = null
            for (option in buttonInfo.options) {
                if (option.text.length > maxTextByLength.length) {
                    maxTextByLength = option.text
                }
                if (option.icoId != null && availableIcoId == null) {
                    availableIcoId = option.icoId
                }
            }
            val segmentInput = CalculateIdealButtonWidthByHeightInput.M3ButtonInput(
                height = buttonInfo.height,
                text = maxTextByLength,
                name = "",
                sizeType = SizeType.MEDIUM,
                icoId = availableIcoId,
                dropDownMode = false,
                font = buttonInfo.font
            )

            val segmentWidth = calculateIdealButtonWidthByHeight(context, segmentInput, paddingHorizontal)
            totalWidth += (segmentWidth * buttonInfo.options.size)
            totalWidth
        }

        is CalculateIdealButtonWidthByHeightInput.SwitchButtonInput -> {
            (buttonInfo.height.toFloat() * 1.625f).roundToInt()
        }
    }
}

fun calculateContentWidthForListPopupWindow(context: Context, list: List<String>, textSizee: Float): Int {
    var maxWidth = 0
    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

    for (i in list.indices) {
        val tempTextView = TextView(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            text = list[i]
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
            measure(widthMeasureSpec, heightMeasureSpec)
        }
        if (tempTextView.measuredWidth > maxWidth) {
            maxWidth = tempTextView.measuredWidth
        }
    }
    return maxWidth
}

data class CalculateLeftAndRightMarginForRowsReturn(
    val marginLeft: Int,
    val marginRight: Int
)
fun calculateLeftAndRightMarginForRows(width: Int): CalculateLeftAndRightMarginForRowsReturn {
    val marginLeft = round(width.toFloat() / 28.42f).toInt().coerceIn(0, round(32f*baseDensity).toInt())
    val marginRight = round(marginLeft.toFloat() * 1.42f).toInt()
    return CalculateLeftAndRightMarginForRowsReturn(marginLeft, marginRight)
}
fun calculateIdealWidthForOutlinedTextFieldForOvDialog(textSizee: Float, hintText: String, context: Context, paddingHorizontal: Int? = null, twoSidePadding: Boolean = false, gravityy: Int = Gravity.CENTER): Int {
    val font = context.resources.getFont(R.font.google_sans_regular)
    val paddingLeft = paddingHorizontal ?: textSizee.toInt()
    val paddingRight = if (twoSidePadding && (paddingHorizontal != null)) {paddingHorizontal} else {textSizee.toInt()}
    val tempTextView = TextView(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        text = hintText
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
        includeFontPadding = false
        typeface = font
        measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
    }
    val textWidth = tempTextView.measuredWidth
    return paddingLeft + paddingRight + textWidth
}
fun calculateObjectsInOneLineAndMaxLinesForAdaptiveGridSize(parent: objectData2, objectsInOneLine: Int): Pair<Int, Int?> {
    val objectsInOneLine2 = objectsInOneLine.coerceIn(parent.objectsInOneLine ?: 1, parent.maxObjectsInOneLineForAdaptiveSize ?: Int.MAX_VALUE)
    var maxLines = parent.maxLines
    if (objectsInOneLine2 == parent.maxObjectsInOneLineForAdaptiveSize) {
        maxLines = parent.maxLinesForAdaptiveSize
    }
    else if (objectsInOneLine2 == parent.objectsInOneLine) {
        maxLines = parent.maxLines
    }
    else if (parent.maxObjectsInOneLineForAdaptiveSize == null && parent.objectsInOneLine == null) {
        maxLines = parent.maxLinesForAdaptiveSize
    }
    else if (parent.maxObjectsInOneLineForAdaptiveSize != null && parent.objectsInOneLine == null) {
        maxLines = parent.maxLines
    }
    else if (parent.maxObjectsInOneLineForAdaptiveSize == null && parent.objectsInOneLine != null) {
        maxLines = parent.maxLinesForAdaptiveSize
    }
    else {
        val maxObjectsInOneLineForAdaptiveSize = parent.maxObjectsInOneLineForAdaptiveSize!!
        val objectsInOneLinee = parent.objectsInOneLine!!
        val maxLinesForAdaptiveSize = parent.maxLinesForAdaptiveSize!!
        val maxLiness = parent.maxLines!!
        val steps = maxObjectsInOneLineForAdaptiveSize - objectsInOneLinee
        val currentStep = objectsInOneLine2 - objectsInOneLinee
        val pr = currentStep.toFloat() / steps.toFloat()
        val linesSteps = maxLinesForAdaptiveSize - maxLiness
        val interpolatedLines = linesSteps.toFloat() * pr
        maxLines = round(maxLiness.toFloat() + interpolatedLines).toInt()
    }
    return Pair(objectsInOneLine2, maxLines)
}
fun calculateBaseCardSize(elementType: ElementType, sizeType: SizeType): Pair<Int?, Int?> {
    return when (elementType) {
        ElementType.Anime, ElementType.Manga -> {
            when (sizeType) {
                SizeType.SMALL -> Pair(round(116f * baseDensity).toInt(), round(166f * baseDensity).toInt())
                SizeType.MEDIUM -> Pair(round(160f * baseDensity).toInt(), round(229f * baseDensity).toInt())
                SizeType.LARGE -> Pair(round(239f * baseDensity).toInt(), round(342 * baseDensity).toInt())
                SizeType.XLARGE -> Pair(round(239f * baseDensity).toInt(), round(342f * baseDensity).toInt())
            }
        }
        ElementType.Playlist, ElementType.Music -> {
            when (sizeType) {
                SizeType.SMALL -> Pair(round(166f * baseDensity).toInt(), round(166f * baseDensity).toInt())
                SizeType.MEDIUM -> Pair(round(229f * baseDensity).toInt(), round(229f * baseDensity).toInt())
                SizeType.LARGE -> Pair(round(308f * baseDensity).toInt(), round(308f * baseDensity).toInt())
                SizeType.XLARGE -> Pair(round(308f * baseDensity).toInt(), round(308f * baseDensity).toInt())
            }
        }
        ElementType.Episode -> {
            when (sizeType) {
                SizeType.SMALL -> Pair(round(162f * baseDensity).toInt(), round(91f * baseDensity).toInt())
                SizeType.MEDIUM -> Pair(round(228f * baseDensity).toInt(), round(128f * baseDensity).toInt())
                SizeType.LARGE -> Pair(round(308f * baseDensity).toInt(), round(173f * baseDensity).toInt())
                SizeType.XLARGE -> Pair(round(308f * baseDensity).toInt(), round(173f * baseDensity).toInt())
            }
        }
        else -> {
            Pair(null, null)
        }
    }
}