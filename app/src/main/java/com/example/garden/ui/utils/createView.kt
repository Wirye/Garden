package com.example.garden.ui.utils

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.garden.BsdButtonsTags
import com.example.garden.GenreChoiceOutput
import com.example.garden.Layer
import com.example.garden.R
import com.example.garden.ResultKeys
import com.example.garden.SelectFileInput
import com.example.garden.SelectFileOutput
import com.example.garden.SelectFilesInput
import com.example.garden.SelectFilesOutput
import com.example.garden.baseDensity
import com.example.garden.createCardApply
import com.example.garden.database.CarouselType
import com.example.garden.database.CollectionType
import com.example.garden.database.ElementType
import com.example.garden.database.Genre
import com.example.garden.database.ImageData
import com.example.garden.database.ImageSource
import com.example.garden.database.LinkData
import com.example.garden.database.LinkType
import com.example.garden.database.SizeType
import com.example.garden.episodeInfo
import com.example.garden.fileType
import com.example.garden.genreColors
import com.example.garden.genreNames
import com.example.garden.icoSizesRatio
import com.example.garden.ui.utils.viewExtensions.lifecycleOwner
import com.example.garden.listDot
import com.example.garden.listDot2
import com.example.garden.ui.utils.viewExtensions.loadImage
import com.example.garden.objectData2
import com.example.garden.scaledDensity
import com.example.garden.screenHeight
import com.example.garden.screenWidth
import com.example.garden.statusBarHeight
import com.example.garden.steps
import com.example.garden.ui.adapters.CarouselsAdapter
import com.example.garden.ui.adapters.FlatGridOfEditEpisodesAdapter
import com.example.garden.viewmodel.ResultSenderViewModel
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.Collections
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import com.example.garden.leftInsetWidth
import com.example.garden.navigationBarHeight
import com.example.garden.rightInsetWidth
import com.example.garden.ui.customView.OutlinedTextField
import com.example.garden.ui.utils.animations.generateAnimationId
import com.example.garden.ui.utils.animations.toggleExtensionAnimation
import com.example.garden.ui.utils.animations.toggleSwitchButtonAnimation
import com.example.garden.ui.utils.drawables.createOutlinedbackground
import com.example.garden.ui.utils.errors.addErrorToRow
import com.example.garden.ui.utils.errors.deleteErrorFromRow
import com.example.garden.ui.utils.mathExtensions.snapToStep
import com.example.garden.ui.utils.viewExtensions.changeStrokeColor
import com.example.garden.ui.utils.viewExtensions.findIco
import com.example.garden.ui.utils.viewExtensions.findTextInputEditText
import com.example.garden.utils.getVideoDuration
import com.example.garden.utils.search.searchInList
import kotlin.collections.isNotEmpty

fun createCard(width: Int?, height: Int?, showName: Boolean, namePosition: Int?, showAuthor: Boolean, image: ImageData?, name: String?, author: String?, alreadyWatched: Long, length: Long, showAlreadyWatchedLine: Boolean, context: Context, items: List<objectData2>, cornerRadius: SizeType?, optimizateCardSize: Boolean = true, gridMode: Boolean = false, lineWidth: Int? = null, paddingHorizontal: Int? = null, marginBetweenElementsHorizontal: Int? = null): Pair<List<View>, Pair<Int, Int>> {
    val res = mutableListOf<View>()
    val font = ResourcesCompat.getFont(context, R.font.google_sans_medium)
    val textSizee = floor(15f * baseDensity) // в px
    var cardViewId = 0
    var alreadyWatchedLineId = 0
    var cardViewWidth = 0
    var size = cardScaleCalcFree(items, (paddingHorizontal ?: (round(19f * baseDensity).toInt())), (marginBetweenElementsHorizontal ?: round(11f * baseDensity).toInt()), width, height, lineWidth ?: screenWidth)
    if (width != null && height != null && !optimizateCardSize) {
        size = Pair(width, height)
    }
    var cardHeight = 0
    var cardWidth = 0
    if (showAuthor && showName && namePosition == 0 && !gridMode) {
        cardHeight = size.second - optimizeText(context.getString(R.string.StringForMaxHeightCalculate), size.first, textSizee, false, font).totalHeight*3
        cardWidth = (cardHeight.toFloat() * (size.first.toFloat() / size.second)).toInt()
    }
    else if (!showAuthor && showName && namePosition == 0 && !gridMode) {
        cardHeight = size.second - optimizeText(context.getString(R.string.StringForMaxHeightCalculate), size.first, textSizee, false, font).totalHeight*2
        cardWidth = (cardHeight.toFloat() * (size.first.toFloat() / size.second)).toInt()
    }
    else if (showAuthor && showName && namePosition == 1 && !gridMode) {
        cardHeight = size.second - optimizeText(context.getString(R.string.StringForMaxHeightCalculate), size.first, textSizee, false, font).totalHeight*1
        cardWidth = (cardHeight.toFloat() * (size.first.toFloat() / size.second)).toInt()
    }
    else if ((!showName) || (namePosition == 1) || gridMode) {
        cardWidth = size.first
        cardHeight = size.second
    }
    val cardView = CardView(context).apply {
        cardViewWidth = cardWidth
        val layoutparams1 = ConstraintLayout.LayoutParams(
            cardViewWidth,
            cardHeight
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.setMargins(0,0,0, 0)
        layoutParams = layoutparams1
        cardElevation = 0f
        radius = if (cornerRadius != null) getAdaptiveRadius(cardWidth, cornerRadius) else 0f
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
            elevation = 1000f
        }
        constraintLayoutInsideCardView.addView(alreadyWatchedLine)
    }

    if (showName && namePosition == 1) {
        val textView1 = TextView(context).apply {
            val optimizatedText = optimizeText(name ?: "Без имени", cardViewWidth, textSizee, false, font, 1)
            val layoutparams1 = ConstraintLayout.LayoutParams(
                cardViewWidth,
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
    var textView1Id = 0
    var textView2Id = 0
    if (showName && namePosition == 0) {
        val optimizatedText = optimizeText(name ?: "Без имени", cardViewWidth, textSizee, false, font, maxLines = if (gridMode) 1 else 2)
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
        if (!gridMode) {res.add(textView2)}
    }
    if (showAuthor) {
        val textView3 = TextView(context).apply {
            val optimizatedTextAuthor = optimizeText(author ?: "Без автора", cardViewWidth, textSizee, false, font, 1)
            val layoutparams1 = ConstraintLayout.LayoutParams(
                cardViewWidth,
                optimizatedTextAuthor.totalHeight
            )
            layoutparams1.startToStart = if (textView1Id != 0) textView1Id else cardViewId
            layoutparams1.topToBottom = if (textView2Id != 0) textView2Id else if (textView1Id != 0) textView1Id else cardViewId
            layoutParams = layoutparams1
            text = optimizatedTextAuthor.firstLine
            setTextColor("#9C9C9C".toColorInt())
            this.typeface = font
            includeFontPadding = false
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
            setShadowLayer(5f, 0f, 0f, Color.BLACK)
        }
        res.add(textView3)
    }
    else if (name != null && showName && namePosition == 1) {
        val gradientView = ImageView(context).apply {
            val gradientWidth = cardViewWidth.coerceAtLeast(1)
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
                "#80393939".toColorInt(), // 30%
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
    val realCardHeight = if (!gridMode) size.second else {size.second + optimizeText(context.getString(R.string.StringForMaxHeightCalculate), size.first, textSizee, false, font).totalHeight * if (showName && showAuthor && namePosition == 0) 2 else if (showName && namePosition == 0) 1 else if (showAuthor) 1 else 0}
    size = Pair(cardViewWidth, realCardHeight)
    return Pair(res,size)
}
fun createGridOfChilds(objList: List<objectData2>, objectsInOneLine: Int?, context: Context, lineWidth: Int?, parent: objectData2): ConstraintLayout {

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
            val margin = parent.marginBetweenElementsHorizontal ?: round(4f*baseDensity).toInt()
            size = cardScaleCalcForGrid(i.width, i.height, lineWidth ?: screenWidth, margin, null)
            break
        }
    }
    var objectsInOneLine2 = size.third.toInt()
    var maxLines = parent.maxLines
    if (parent.adaptiveGridSize) {
        val res = calculateObjectsInOneLineAndMaxLinesForAdaptiveGridSize(parent, size.third.toInt())
        objectsInOneLine2 = res.first
        maxLines = res.second
    }
    else {
        objectsInOneLine2 = objectsInOneLine ?: size.third.toInt()
    }
    var k = 0
    var lastFirstViewId = 0
    var lastViewId = 0
    var maxObjects: Int
    if (maxLines != null) {
        maxObjects = objectsInOneLine2 * maxLines
        if (objList.size <= maxObjects) {
            maxObjects = objList.size
        }
    }
    else {
        maxObjects = objList.size
    }
    var marginH = round(4f * baseDensity).toInt()
    if (parent.marginBetweenElementsHorizontal != null) {
        marginH = parent.marginBetweenElementsHorizontal!!
    }
    var marginV = round(4f * baseDensity).toInt()
    if (parent.marginBetweenElementsVertical != null) {
        marginV = parent.marginBetweenElementsVertical!!
    }
    var height = 0
    for (i in objList) {
        if (i.width != null && i.height != null) {
            val margin = parent.marginBetweenElementsHorizontal ?: round(4f*baseDensity).toInt()
            size = cardScaleCalcForGrid(i.width, i.height, lineWidth ?: screenWidth, margin, objectsInOneLine2)
            break
        }
    }
    for (i in 0 until maxObjects) {
        val objData = objList[i]
        val views = createCard(size.first, size.second, parent.childsShowName, parent.childsNamePosition, parent.childsShowAuthor, objData.image, if (objData.name != null && objData.name != "") objData.name else "Без имени", objData.author, objData.alreadyWatched, objData.length, parent.showAlreadyWatchedLine, context, objList, parent.childsCornerRadius,false, true, marginBetweenElementsHorizontal = parent.marginBetweenElementsHorizontal, paddingHorizontal = parent.paddingHorizontal)
        val cardContainer = ConstraintLayout(context).apply {
            val font = ResourcesCompat.getFont(context, R.font.google_sans_medium)
            val textSizee = floor(15f * baseDensity) // в px
            val layoutparams2 = ConstraintLayout.LayoutParams(
                views.second.first,
                views.second.second
            )
            val cardConHeight = views.second.second
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
                k += objectsInOneLine2
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
data class CreateDovodchikDotsReturn(
    val layout: ConstraintLayout,
    val list: List<Pair<listDot2, listDot>>,
    val numTextView: TextView,
)
fun createDovodchikDots(context: Context, items: List<objectData2>, paddingHorizontal: Int, marginBetweenElementsHorizontal: Int, showName: Boolean, showAuthor: Boolean, namePosition: Int?, lineWidth: Int): CreateDovodchikDotsReturn {
    val res = mutableListOf< Pair<listDot2, listDot>>()
    val dotsList = calculateAmountOfDots(items, paddingHorizontal, marginBetweenElementsHorizontal, context, showName, showAuthor, namePosition, lineWidth)
    val dotsAmount = dotsList.size
    val height1 = 35
    val margin = 0
    val maxDotsAmountOnScreen = floor((lineWidth -  300).toFloat() / (height1+margin)).toInt()
    var dotsOnScreen: Int
    var showDotsPageNum = false
    if (dotsAmount > maxDotsAmountOnScreen) {
        dotsOnScreen = maxDotsAmountOnScreen - 1
        showDotsPageNum = true
    }
    else {
        dotsOnScreen = dotsAmount
    }
    val firstDotMargin = if (showDotsPageNum) { 0 } else { ((lineWidth - dotsOnScreen * height1+margin).toFloat() / 2).toInt()}
    val lastDotMargin = if (showDotsPageNum) { 150 } else { ((lineWidth - dotsOnScreen * height1+margin).toFloat() / 2).toInt()}
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            lineWidth,
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
                lineWidth - 150 - height1 - 5
            } else {
                lineWidth
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
        isHorizontalScrollBarEnabled = false
    }
    var widthSum = firstDotMargin
    var lastSwitchPageDotWidthSum = firstDotMargin
    var lastNumer = 1
    val dotsContainer = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            lineWidth,
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
    return CreateDovodchikDotsReturn(container, res, numRes)
}
fun createBSDButton(textt: String, icoId: Int?, showOpenPageArrow: Boolean, context: Context, width: Int, height: Int, overrideMarginLeft: Int? = null, overrideMarginRight: Int? = null): ConstraintLayout {
    val font = context.resources.getFont(R.font.google_sans_medium)
    val icoSize = floor(height.toFloat() / 2f).toInt()
    val margins = calculateLeftAndRightMarginForRows(width)
    val icoMarginLeft = overrideMarginLeft ?: margins.marginLeft
    val marginRight = overrideMarginRight ?: margins.marginRight
    val arrowMarginRight = round(marginRight.toFloat() / icoSizesRatio).toInt()
    val arrowSize = floor(icoSize.toFloat() / 1.46f).toInt()
    val textViewWidth = width - icoMarginLeft - if (icoId != null) {icoMarginLeft + icoSize} else {0}  - if (showOpenPageArrow) {(arrowSize+arrowMarginRight+icoMarginLeft)} else {marginRight}
    val textHeight = round(icoSize.toFloat() / 1f).toInt()
    val textSizee = getTextSizeByHeight(textHeight, font, context = context)
    var icoViewId = 0
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            width,
            height
        )
    }
    if (icoId != null) {
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
    }

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
        if (icoId != null) {
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
        }
        else {
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(icoMarginLeft,0,0,0)
        }
        layoutParams = layoutparams1
        tag = "textView"
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

data class CreateM3ButtonReturn(
    val container: ConstraintLayout,
    val childs: List<View>,
    val width: Int
)
fun createM3Button(context: Context, width: Int, height: Int, textt: String, name: String, nameColor: Int, isActive: Boolean, sizeType: SizeType, cornersMode: Int, icoId: Int? = null, pillMode: Boolean = false, dropDownMode: Boolean = false, wrapContentMode: Boolean = false, maxWidthh: Int? = null): CreateM3ButtonReturn {
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
    val nameHeight = round(height.toFloat() / 3f).toInt().coerceIn(0, round(12f*baseDensity).toInt())
    var buttonHeight = if (name == "") {height} else {height-nameHeight}
    val buttonWidth = round(width.toFloat() / height.toFloat() * buttonHeight.toFloat()).toInt()
    val textHeight = round(buttonHeight / textRatio).toInt()
    val textSizee = getTextSizeByHeight(textHeight, font, context = context)
    val icoSize = (textSizee * 1.5f).toInt().coerceIn(0, buttonHeight)
    val arrowMarginRight = round(1.4f*baseDensity*textRatio).toInt()
    val maxTextWidth = (if (wrapContentMode) {maxWidthh ?: 1000000} else {buttonWidth} - if (icoId != null) {(icoSize+arrowMarginRight)} else {0} - if (dropDownMode) {(icoSize+(arrowMarginRight*2))} else {0}).coerceIn(0,1000000000)
    val textView = TextView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams = layoutparams1
        maxWidth = maxTextWidth
        maxLines = 1
        text = textt
        includeFontPadding = false
        typeface = font
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
        setTextColor("#804A4459".toColorInt())
        tag = "button_text"
        id = View.generateViewId()
        ellipsize = TextUtils.TruncateAt.END
        measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
    }
    val wrapContentModeButtonWidth = if (icoId != null) {(icoSize+(arrowMarginRight*2))} else {0} + if (dropDownMode) {(icoSize+(arrowMarginRight*2))} else {0} + textView.measuredWidth + if (dropDownMode && (icoId == null)) {arrowMarginRight*3} else {0}
    if (wrapContentMode) {
        widthToReturn = wrapContentModeButtonWidth
        val containerlp1 = container.layoutParams as ConstraintLayout.LayoutParams
        containerlp1.width = wrapContentModeButtonWidth
        container.layoutParams = containerlp1
    }
    var nameId = 0
    if (name != "") {
        val nameView = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                width,
                nameHeight
            )
            maxLines = 2
            this.text = name
            includeFontPadding = false
            typeface = font
            setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSizeByHeight(round(nameHeight.toFloat() / 2f).toInt(), font, context = context))
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
        viewList.add(nameView)
    }

    val buttonBg = View(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            if (wrapContentMode) {wrapContentModeButtonWidth} else {buttonWidth},
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

        val radius = if (!pillMode) {getAdaptiveRadius(if (wrapContentMode) {wrapContentModeButtonWidth} else {buttonWidth}, sizeType)} else {10000f}
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
    viewList.add(buttonBg)

    var arrowId = 0
    if (dropDownMode) {
        val arrow = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                icoSize,
                icoSize
            )
            layoutparams1.endToEnd = buttonBg.id
            layoutparams1.topToTop = buttonBg.id
            layoutparams1.bottomToBottom = buttonBg.id
            layoutparams1.setMargins(0,0,arrowMarginRight,0)
            setImageResource(R.drawable.chevron_forward)
            imageTintList = ColorStateList.valueOf("#804A4459".toColorInt())
            rotation = 90f
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = layoutparams1
            val newId = View.generateViewId()
            id = newId
            arrowId = newId
            tag = "arrow"
        }
        viewList.add(arrow)
    }

    val icoAndTextContaier = ConstraintLayout(context).apply {
        val lp1 = ConstraintLayout.LayoutParams(
            (if (icoId != null) {(icoSize+arrowMarginRight)} else {0} + textView.measuredWidth),
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        lp1.startToStart = buttonBg.id
        lp1.topToTop = buttonBg.id
        lp1.bottomToBottom = buttonBg.id
        if (dropDownMode) {lp1.endToStart = arrowId} else {lp1.endToEnd = buttonBg.id}
        layoutParams = lp1
        tag = "icoAndTextContaier"
    }
    viewList.add(icoAndTextContaier)

    var icoViewId = 0
    if (icoId != null) {
        val icoView = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                icoSize,
                icoSize
            )
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            setImageResource(icoId)
            scaleType = ImageView.ScaleType.CENTER_CROP
            tag = "button_ico"
            val newId = View.generateViewId()
            id = newId
            icoViewId = newId
            layoutParams = layoutparams1
        }
        icoAndTextContaier.addView(icoView)
    }


    val textViewlp1 = textView.layoutParams as ConstraintLayout.LayoutParams
    textViewlp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
    if (icoViewId == 0) {
        textViewlp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
    }
    else {
        textViewlp1.startToEnd = icoViewId
        textViewlp1.setMargins(arrowMarginRight,0,0,0)
    }
    textView.layoutParams = textViewlp1
    icoAndTextContaier.addView(textView)


    return CreateM3ButtonReturn(container, viewList, widthToReturn)
}

data class SegmentedButtonOptions(
    var text: String,
    var icoId: Int?,
    var isActive: Boolean,
)
fun createSegmentedButton(context: Context, width: Int, height: Int, options: List<SegmentedButtonOptions>): ConstraintLayout {
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
            textt = options[i].text,
            icoId = options[i].icoId,
            name = "",
            sizeType = SizeType.MEDIUM,
            cornersMode = cornersMode,
            nameColor = "#FFFFFF".toColorInt(),
            isActive = options[i].isActive
        )

        val button = rt.container
        for (i in rt.childs.indices) {
            button.addView(rt.childs[i])
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
            setStroke(round(1f * baseDensity).toInt(), "#000000".toColorInt())
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
fun createSlider(context: Context, widthh: Int, stopsList: List<Pair<Float, String>>, heightt: Int, createSteps: Boolean = false, alreadyValue: Float, createTextInputView: Boolean = false): ConstraintLayout {
    val font = context.resources.getFont(R.font.google_sans_regular)
    val mediumFont = context.resources.getFont(R.font.google_sans_medium)
    val textSizee = round(7f*baseDensity)
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(widthh, heightt)
        tag = "slider_container"
        id = View.generateViewId()
    }
    val maxValue = stopsList[stopsList.lastIndex].first
    val tempTextView = TextView(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        includeFontPadding = false
        typeface = font
        maxLines = 1
        text = "0.00"
    }
    tempTextView.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    val margin = round(4f*baseDensity).toInt()
    val textInputTextSizee = round(getTextSizeByHeight(heightt, font, context = context) / 1.25f)
    val textInputViewWidth = tempTextView.measuredWidth + (3*ceil(textInputTextSizee).toInt())
    val textInput = createOutlinedTextField(context, textInputViewWidth, SizeType.SMALL, heightt, "", Gravity.CENTER, textInputTextSizee, 1, "%.2f".format(alreadyValue).trim().replace(",","."), (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL), round(textInputViewWidth.toFloat() / 4f).toInt(), true)

    val containerrWidth = if (!createTextInputView) {widthh} else {widthh - margin - textInputViewWidth}
    val containerr = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(containerrWidth, heightt)
        tag = "slider_containerr"
        id = View.generateViewId()
    }
    val textViewsList = mutableListOf<Triple<Int, Int, TextView>>()
    for (i in stopsList) {
        val textView = TextView(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            includeFontPadding = false
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
            typeface = mediumFont
            text = i.second
            setTextColor("#AFAFAF".toColorInt())
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
        }
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        textView.updateLayoutParams<ConstraintLayout.LayoutParams> { width = textView.measuredWidth ; height = textView.measuredHeight }
        textViewsList.add(Triple(textView.measuredWidth, textView.measuredHeight, textView))
    }
    val firstTextViewWith = textViewsList[0].first
    val firstTextViewHeight = textViewsList[0].second
    val lastTextViewWith = textViewsList[textViewsList.lastIndex].first
    var containerrrWidth = containerrWidth - ceil(firstTextViewWith.toFloat() / 2f).toInt() - ceil(lastTextViewWith.toFloat() / 2f).toInt()
    val containerrrHeight = heightt - firstTextViewHeight
    var containerrrWidthForCalcs = containerrrWidth - round(2f * baseDensity).toInt() - round(2f*baseDensity).toInt()
    var widthBetweenDots = round(containerrrWidthForCalcs.toFloat() / (stopsList.size-1).toFloat()).toInt()
    val txtMaxWidth = round(widthBetweenDots.toFloat() / 2f).toInt()
    for (i in textViewsList) {
        i.third.maxWidth = txtMaxWidth
        if (i.third.measuredWidth > txtMaxWidth) {
            i.third.updateLayoutParams<ConstraintLayout.LayoutParams> { width = txtMaxWidth }
        }
    }
    containerrrWidth = containerrWidth - ceil(min(firstTextViewWith, txtMaxWidth).toFloat() / 2f).toInt() - ceil(min(lastTextViewWith, txtMaxWidth).toFloat() / 2f).toInt()
    containerrrWidthForCalcs = containerrrWidth - round(2f * baseDensity).toInt() - round(2f * baseDensity).toInt()
    widthBetweenDots = round(containerrrWidthForCalcs.toFloat() / (stopsList.size-1).toFloat()).toInt()
    val containerrr = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(containerrrWidth, containerrrHeight).apply {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        }
        tag = "slider_containerrr"
        id = View.generateViewId()
    }

    val minSliderHeight = round(48f*baseDensity).toInt()
    val sliderHeight = max(containerrrHeight, minSliderHeight)
    val marginTop = if (containerrrHeight < minSliderHeight) {round((containerrrHeight - round(0.25f*baseDensity).toInt() - minSliderHeight).toFloat() / 2f).toInt()} else {0}
    val gapSize = round(5f * baseDensity).toInt()
    val slider = Slider(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            containerrrWidth+trackSidePadding*2-round(5f*baseDensity).toInt(),
            sliderHeight
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.setMargins(-trackSidePadding+round(2.5f*baseDensity).toInt(),marginTop,0,0)
        val thumbWidth = round(2f * baseDensity).toInt()
        val thumbDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 1000f
            setColor("#6750A4".toColorInt())
            setSize(thumbWidth, round(containerrrHeight.toFloat() / 2f).toInt())
        }
        setCustomThumbDrawable(thumbDrawable)
        labelBehavior = LabelFormatter.LABEL_GONE
        haloRadius = 0
        thumbRadius = round(containerrrHeight.toFloat() / 2f).toInt() - round(1f*baseDensity).toInt()
        thumbTrackGapSize = gapSize
        trackInsideCornerSize = round(2f * baseDensity).toInt()
        trackHeight = round(containerrrHeight.toFloat() / 2.75f).toInt()
        layoutParams = layoutparams1
        trackActiveTintList = ColorStateList.valueOf("#E8DEF8".toColorInt())
        trackInactiveTintList = ColorStateList.valueOf("#80EADDFF".toColorInt())
        thumbTintList = ColorStateList.valueOf("#E8DEF8".toColorInt())
        tickActiveTintList = ColorStateList.valueOf("#4A4459".toColorInt())
        tickInactiveTintList = ColorStateList.valueOf("#4A4459".toColorInt())

        if (stopsList.isNotEmpty()) {
            valueFrom = stopsList[0].first
            valueTo = stopsList[stopsList.lastIndex].first
            if (!createSteps) {
                stepSize = 0f
            }
            else {
                stepSize = if(stopsList.size > 2) {(valueTo - valueFrom) / (stopsList.size.toFloat() - 1f)} else {valueTo - valueFrom}
            }
        }
        value = if (alreadyValue > valueTo) {valueTo} else {alreadyValue}
        id = View.generateViewId()
        tag = "slider_control"
    }
    slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
        @SuppressLint("RestrictedApi")
        override fun onStartTrackingTouch(slider: Slider) {
            slider.thumbTrackGapSize = gapSize
        }

        override fun onStopTrackingTouch(slider: Slider) {
            slider.thumbTrackGapSize = gapSize
        }
    })
    containerrr.addView(slider)
    containerr.addView(containerrr)
    for (i in 0 until textViewsList.size) {
        val obj = textViewsList[i]
        if (i == 0) {
            val margin = round((containerrWidth-containerrrWidthForCalcs).toFloat() / 2f).toInt() + round(2.5f*baseDensity).toInt() - round(obj.first.toFloat() / 2f).toInt()
            obj.third.updateLayoutParams<ConstraintLayout.LayoutParams> {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                setMargins(margin,0,0,0)
            }
        }
        else if (i == textViewsList.size-1) {
            val margin = round((containerrWidth-containerrrWidthForCalcs).toFloat() / 2f).toInt() + round(2.5f*baseDensity).toInt() - round(obj.first.toFloat() / 2f).toInt()
            obj.third.updateLayoutParams<ConstraintLayout.LayoutParams> {
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                setMargins(0,0,margin,0)
            }
        }
        else {
            val dotCoord = round((containerrWidth - containerrrWidthForCalcs).toFloat() / 2f).toInt() + (widthBetweenDots*(i))
            val margin = dotCoord - if (obj.first > txtMaxWidth) {round(txtMaxWidth.toFloat()/2f).toInt()} else {round(obj.first.toFloat()/2f).toInt()} + round(0.5f*baseDensity).toInt()
            obj.third.updateLayoutParams<ConstraintLayout.LayoutParams> {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                setMargins(margin,0,0,0)
            }
        }
        containerr.addView(obj.third)
    }
    container.addView(containerr)
    if (createTextInputView) {
        val lp1 = textInput.layoutParams as ConstraintLayout.LayoutParams
        lp1.startToEnd = containerr.id
        lp1.topToTop = containerr.id
        lp1.setMargins(margin,0,0,0)
        textInput.layoutParams = lp1
        container.addView(textInput)
        val textInputEditText = textInput.findTextInputEditText()
        textInputEditText?.addTextChangedListener (object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val rawText = textInputEditText.text.toString().trim().replace(',', '.')
                var newSliderValue = rawText.toFloatOrNull() ?: 0f
                val minSliderValue = stopsList[0].first
                val maxSliderValue = stopsList[stopsList.lastIndex].first
                if (createSteps) {
                    val stepSize = if(stopsList.size > 2) {(maxSliderValue - minSliderValue) / (stopsList.size.toFloat() - 1f)} else {maxSliderValue - minSliderValue}
                    newSliderValue = newSliderValue.snapToStep(minSliderValue, stepSize)
                }
                newSliderValue = newSliderValue.coerceIn(minSliderValue, maxSliderValue)
                slider.value = "%.2f".format(newSliderValue).trim().replace(",", ".").toFloat()
            }
            override fun afterTextChanged(s: Editable?) {
                val minSliderValue = stopsList[0].first
                val maxSliderValue = stopsList[stopsList.lastIndex].first
                val text = textInputEditText.text.toString().trim().replace(',', '.')
                val textValue = text.toFloatOrNull() ?: return
                if (textValue > maxSliderValue) {
                    textInputEditText.setText("%.2f".format(maxSliderValue).trim().replace(",", "."))
                }
                else if (textValue < minSliderValue) {
                    textInputEditText.setText("%.2f".format(minSliderValue).trim().replace(",", "."))
                }
                else if (createSteps) {
                    val stepSize = if(stopsList.size > 2) {(maxSliderValue - minSliderValue) / (stopsList.size.toFloat() - 1f)} else {maxSliderValue - minSliderValue}
                    val snappedTextValue = textValue.snapToStep(minSliderValue, stepSize)
                    if (snappedTextValue != textValue) {
                        textInputEditText.setText("%.2f".format(snappedTextValue).trim().replace(",", "."))
                    }
                }
            }
        })
        slider.addOnChangeListener { _, value, _ ->
            textInputEditText?.setText("%.2f".format(slider.value).trim().replace(",","."))
        }
    }
    return container
}
fun createSliderRow(context: Context, width: Int, name: String, icoId: Int?, stopsList: List<Pair<Float, String>>, heightt: Int, createSteps: Boolean = false, alreadyValue: Float, createTextInputView: Boolean = false): List<View> {
    val font = context.resources.getFont(R.font.google_sans_medium)

    val margins = calculateLeftAndRightMarginForRows(width)
    val icoMarginLeft = margins.marginLeft
    val marginRight = margins.marginRight
    val icoSize = floor(heightt.toFloat() / 2f).toInt()
    val titleWidth = width - icoMarginLeft - marginRight - if (icoId != null) {(icoSize + icoMarginLeft)} else {0}
    val titleId = View.generateViewId()
    val textHeight = round(icoSize.toFloat() / 1f).toInt()
    val textSizee = getTextSizeByHeight(textHeight, font, context = context)
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
    if (icoId != null) {
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
    }


    val textView = TextView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            titleWidth,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

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
        if (icoId != null) {
            layoutparams1.startToEnd = icoViewId
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            val diff2 = heightt - measuredHeight - textHeight
            layoutparams1.setMargins(icoMarginLeft, round(diff2.toFloat() / 2f).toInt(),0,0)
        }
        else {
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(icoMarginLeft,0,0,0)
        }
        id = titleId
        tag = "slider_title"
        layoutParams = layoutparams1
    }
    container.addView(textView)
    viewsToReturn.add(textView)

    val slider = createSlider(context, titleWidth, stopsList, textHeight, createSteps, alreadyValue, createTextInputView)
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
fun createDropdownRow(context: Context, width: Int, height: Int, titleText: String, icoId: Int?, options: List<Pair<String, BsdButtonsTags>>, onItemSelected: (String, BsdButtonsTags) -> Unit, buttonIcoId: Int? = null, alreadyValue: Int? = null): ConstraintLayout {
    val font = context.resources.getFont(R.font.google_sans_medium)
    val margins = calculateLeftAndRightMarginForRows(width)
    val icoMarginLeft = margins.marginLeft
    val marginRight = margins.marginRight
    val icoSize = floor(height.toFloat() / 2f).toInt()
    val textViewWidth = round((width - icoMarginLeft - marginRight - if (icoId != null) {(icoSize + icoMarginLeft)} else {0}).toFloat() / 2.5f).toInt()
    val buttonHeight = round(height.toFloat() / 1.5f).toInt()
    val buttonWidth = width - icoMarginLeft - icoMarginLeft - icoSize - textViewWidth
    val textHeight = round(icoSize.toFloat() / 1f).toInt()
    val textSizee = getTextSizeByHeight(textHeight, font, context = context)
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(width, height)
        tag = "dropdown_container"
        id = View.generateViewId()
    }

    val icoViewId = View.generateViewId()
    if (icoId != null) {
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
    }


    val rt = createM3Button(context = context, width = buttonWidth, height = buttonHeight, textt = options[if (alreadyValue == null || alreadyValue > options.indices.last) 0 else alreadyValue].first, name = "", sizeType = SizeType.SMALL, cornersMode = 0, icoId = buttonIcoId, pillMode = true, dropDownMode = true, wrapContentMode = true, maxWidthh = buttonWidth, isActive = true, nameColor = "#FFFFFF".toColorInt())
    val dropdownButton = rt.container
    for (i in rt.childs.indices) {
        dropdownButton.addView(rt.childs[i])
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
        if (icoId != null) {layoutparams1.startToEnd = icoViewId} else {layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID}
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
        if (icoId != null) {
            val difference = icoSize - measuredHeight
            if (difference < 0) {
                layoutparams1.setMargins(icoMarginLeft,round(difference.toFloat() / 2f).toInt(),0,0)
            }
            else if (difference > 0){
                layoutparams1.setMargins(icoMarginLeft, (round(difference.toFloat() / 2f)).toInt(),0,0)
            }
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
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, newOptions)
        setAdapter(adapter)
        anchorView = dropdownButton
        isModal = true
        setWidth(calculateContentWidthForListPopupWindow(context, newOptions, 16f* scaledDensity) + round(32f*baseDensity).toInt())

        setOnItemClickListener { _, _, position, _ ->
            val selected = options[position]
            val newChilds = createM3Button(context = context, width = buttonWidth, height = buttonHeight, textt = selected.first, name = "", sizeType = SizeType.SMALL, cornersMode = 0, icoId = buttonIcoId, pillMode = true, dropDownMode = true, wrapContentMode = true, maxWidthh = buttonWidth, isActive = true, nameColor = "#FFFFFF".toColorInt())
            dropdownButton.removeAllViews()
            val lp1 = dropdownButton.layoutParams as ConstraintLayout.LayoutParams
            lp1.width = newChilds.width
            dropdownButton.layoutParams = lp1
            for (i in newChilds.childs) {
                dropdownButton.addView(i)
            }
            dropdownButton.invalidate()
            dropdownButton.requestLayout()
            onItemSelected(selected.first,selected.second)
            dismiss()
        }
    }
    dropdownButton.setOnClickListener {
        dropdownButton.requestFocus()
        listPopupWindow.show()
    }

    return container
}
fun createSegmentedButtonRow(context: Context, width: Int, height: Int, options: List<SegmentedButtonOptions>, icoId: Int?, textt: String, callback: (Float) -> Unit): ConstraintLayout {
    val font = context.resources.getFont(R.font.google_sans_medium)
    val icoSize = floor(height.toFloat() / 2f).toInt()
    val margins = calculateLeftAndRightMarginForRows(width)
    val icoMarginLeft = margins.marginLeft
    val marginRight = margins.marginRight
    val segmentedButtonWidth = calculateIdealButtonWidthByHeight(context,
        CalculateIdealButtonWidthByHeightInput.SegmentedButtonInput(height, options, font), round(32f*baseDensity).toInt()).coerceIn(0, round(width.toFloat() / 2.15f).toInt())
    val textViewWidthMaxWidth = width - icoMarginLeft - marginRight - segmentedButtonWidth - if (icoId != null) {(icoSize + icoMarginLeft)} else {0}
    val textHeight = round(icoSize.toFloat() / 1f).toInt()
    val textSizee = getTextSizeByHeight(textHeight, font, context = context)
    val marginBetweenTextAndButton = round(icoMarginLeft.toFloat() / 2f).toInt()
    var icoViewId = 0
    var textViewId = 0
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            width,
            height
        )
    }

    if (icoId != null) {
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
    }

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
        if (icoId != null) {
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
        }
        else {
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(icoMarginLeft, 0,0,0)
        }
        layoutParams = layoutparams1
        val newId = View.generateViewId()
        id = newId
        textViewId = newId
    }
    container.addView(textView)

    val notIdealButtonWidth = width - if (icoId != null) {(icoMarginLeft + icoSize)} else {0} - icoMarginLeft - textView.measuredWidth - marginRight
    val idealButtonWidth = calculateIdealButtonWidthByHeight(context,CalculateIdealButtonWidthByHeightInput.SegmentedButtonInput(height, options, font), round(16f*baseDensity).toInt())
    val buttonWidth = min(notIdealButtonWidth, idealButtonWidth)
    val buttonHeight = round(height.toFloat() * 0.8f).toInt()
    val segmentedButton = createSegmentedButton(context, buttonWidth, buttonHeight, options)
    val lp1 = segmentedButton.layoutParams as ConstraintLayout.LayoutParams
    lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
    lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
    lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
    lp1.setMargins(0,0,marginRight,0)
    segmentedButton.layoutParams = lp1
    container.addView(segmentedButton)
    for (i in options.indices) {
        val currentValue = i.toFloat()
        val segment = segmentedButton.findViewWithTag<View>("button_$i")
        segment?.setOnClickListener { clickedSegment ->
            segment.requestFocus()
            callback(currentValue)
            for (o in options.indices) {
                val segment1 = segmentedButton.findViewWithTag<View>("button_$o") ?: continue
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
    return container
}
fun createSwitchButton(context: Context, isChecked: Boolean, width: Int, height: Int, callback: (Boolean) -> Unit, animIdCallback: ((Long) -> Unit)? = null): ConstraintLayout {

    val padding = round(height * 0.15f).toInt()
    val thumbSize = height - padding * 2
    val colorOn = "#EADDFF".toColorInt()
    val colorOff = "#80EADDFF".toColorInt()
    var currentState = isChecked

    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(width, height)
    }

    val track = View(context).apply {
        id = View.generateViewId()
        layoutParams = ConstraintLayout.LayoutParams(0, 0)
        background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = height / 2f
            setColor(if (currentState) colorOn else colorOff)
        }
        tag = "track"
    }

    val thumb = View(context).apply {
        id = View.generateViewId()
        layoutParams = ConstraintLayout.LayoutParams(thumbSize, thumbSize)
        background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.WHITE)
        }
        elevation = 4f
        tag = "thumb"
    }

    container.addView(track)
    container.addView(thumb)

    track.updateLayoutParams<ConstraintLayout.LayoutParams> {
        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
    }

    thumb.updateLayoutParams<ConstraintLayout.LayoutParams> {
        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        setMargins(padding, 0, padding, 0)
        horizontalBias = if (currentState) 1f else 0f
    }

    val animId = generateAnimationId()
    if (animIdCallback != null) {
        animIdCallback(animId)
    }
    container.setOnClickListener {
        container.requestFocus()
        currentState = !currentState
        toggleSwitchButtonAnimation(container,currentState, animId)
        callback(currentState)
    }

    return container
}
fun createSwitchButtonRow(context: Context, isChecked: Boolean, width: Int, height: Int, name: String, icoId: Int?, callback1: (Boolean) -> Unit, animIdCallback: ((Long) -> Unit)? = null): ConstraintLayout {
    val font = context.resources.getFont(R.font.google_sans_medium)
    val icoSize = floor(height.toFloat() / 2f).toInt()
    val margins = calculateLeftAndRightMarginForRows(width)
    val icoMarginLeft = margins.marginLeft
    val marginRight = margins.marginRight
    val switchButtonHeight = round(height.toFloat() / 2.3f).toInt()
    val switchButtonWidth = calculateIdealButtonWidthByHeight(context,CalculateIdealButtonWidthByHeightInput.SwitchButtonInput(switchButtonHeight),0)
    val textViewWidth = width - icoMarginLeft*2 - marginRight - if (icoId != null) {(icoSize + icoMarginLeft)} else {0} - switchButtonWidth
    val textHeight = round(icoSize.toFloat() / 1f).toInt()
    val textSizee = getTextSizeByHeight(textHeight, font, context = context)
    var icoViewId = 0
    val container = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            width,
            height
        )
    }
    if (icoId != null) {
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
    }

    val textView = TextView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            textViewWidth,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        maxLines = 2
        text = name
        includeFontPadding = false
        typeface = font
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
        setTextColor("#D9FFFFFF".toColorInt())
        measure(
            View.MeasureSpec.makeMeasureSpec(textViewWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        ellipsize = TextUtils.TruncateAt.END
        if (icoId != null) {
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
        }
        else {
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(icoMarginLeft,0,0,0)
        }

        layoutParams = layoutparams1
    }
    container.addView(textView)

    val switchButton = createSwitchButton(context, isChecked, switchButtonWidth, switchButtonHeight, callback = {value -> callback1(value)}, animIdCallback)
    val lp1 = switchButton.layoutParams as ConstraintLayout.LayoutParams
    lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
    lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
    lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
    lp1.setMargins(0,0,marginRight,0)
    switchButton.layoutParams = lp1
    switchButton.tag = "switch_button"
    container.addView(switchButton)

    return container
}
fun createOutlinedTextField(context: Context, width: Int, radius: SizeType, heightt: Int, hintText: String, gravityy: Int = Gravity.CENTER, textSizee: Float, maxLiness: Int? = null, alreadyEnteredText: String? = null, inputTypee: Int? = null, paddingHorizontal: Int? = null, twoSidePadding: Boolean = false, ico: ImageData? = null, applyPaddingHorizontalToIco: Boolean = false, overrideIcoColor: Int? = null, overrideIcoSize: Pair<Int, Int>? = null, overrideIcoPaddings: Pair<Int, Int>? = null, imeOptionss: Int? = null): OutlinedTextField {
    val font = context.resources.getFont(R.font.google_sans_regular)
    val strokeWidth = round(1f*baseDensity).toInt()
    val inputLayout = OutlinedTextField(context).apply {
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
            intArrayOf(android.R.attr.state_focused),
            intArrayOf()
        )
        val colors = intArrayOf(strokeColor, strokeColor)
        setBoxStrokeColorStateList(ColorStateList(states, colors))

        setBoxCornerRadii(radius1, radius1, radius1, radius1)

        typeface = font
        isHintEnabled = false
        gravity = gravityy
    }
    val widthh = width
    val containerInsideInputLayout = ConstraintLayout(context).apply {
        val layoutParams1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            heightt
        )
        layoutParams = layoutParams1
        background = createOutlinedbackground(radius, widthh, round(1f*baseDensity).toInt(), 0.5f)
    }

    val editText = TextInputEditText(inputLayout.context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            heightt
        ).apply {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        }

        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
        setTextColor("#AFAFAF".toColorInt())

        if (inputTypee != null) {
            inputType = inputTypee
        }
        if (imeOptionss != null) {
            imeOptions = imeOptionss
        }
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
        setPadding(paddingHorizontal ?: textSizee.toInt(),if (gravityy == Gravity.TOP) textSizee.toInt() else 0,if (twoSidePadding && (paddingHorizontal != null)) {paddingHorizontal * if (ico != null && applyPaddingHorizontalToIco) 2 else 1} else {0} + if (ico != null) overrideIcoSize?.first ?: heightt else 0,0)
        includeFontPadding = false
        setText(alreadyEnteredText)
        tag = "edit_text"
        id = View.generateViewId()
    }
    containerInsideInputLayout.addView(editText)
    if (ico != null) {
        val icoView = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                overrideIcoSize?.first ?: heightt,
                overrideIcoSize?.second ?: heightt
            )
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,0,if (applyPaddingHorizontalToIco && paddingHorizontal != null) paddingHorizontal else 0,0)
            layoutParams = layoutparams1
            scaleType = ImageView.ScaleType.CENTER_CROP
            loadImage(ico)
            tag = "ico"
            if (overrideIcoColor != null) {
                imageTintList = ColorStateList.valueOf(overrideIcoColor)
            }
            if (overrideIcoPaddings != null) {
                setPadding(overrideIcoPaddings.first,overrideIcoPaddings.second,overrideIcoPaddings.first,overrideIcoPaddings.second)
            }
        }
        containerInsideInputLayout.addView(icoView)
    }
    inputLayout.addView(containerInsideInputLayout)
    return inputLayout
}
data class CreateGridOfGenresReturn(
    val container: ConstraintLayout,
    val sumHeight: Int,
    val sumWidth: Int
)
fun createGridOfGenres(context: Context, infoContainerHeight: Int, genreList: List<Pair<Boolean, Genre>>, length: Long, alreadyWatched: Long, widthh: Int, heightt: Int, marginBetweenInfoElements: Int, considerSelectedState: Boolean = false, addShowAllButton: Boolean = false, showAllButtonWidth: Int? = null, addClickListeners: Boolean = false, onClick: (Genre) -> Unit, ageText: String? = null, episodesText: String? = null, sezonText: String? = null, yearText: String? = null): CreateGridOfGenresReturn {
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
        val res = optimizeText(context.getString(R.string.StringForMaxHeightCalculate), 1000, steps[i], false, infoTextFont, 1)
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
                infoContainer.requestFocus()
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
    return CreateGridOfGenresReturn(container, sumHeight, sumWidth)
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

            val shader = LinearGradient(
                0f, 0f,
                w, 0f,
                intArrayOf(startColor, endColor),
                positions,
                Shader.TileMode.CLAMP
            )

            paint.shader = shader

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

sealed class CreateFlatGridInput {
    data class EditEpisodes (
        val info: List<episodeInfo>,
        val callback: (List<episodeInfo>) -> Unit
    ) : CreateFlatGridInput()
    data class Episodes (
        val info: List<objectData2>
    ) : CreateFlatGridInput()
    data class Music (
        val info: List<objectData2>
    ) : CreateFlatGridInput()
}
sealed class CreateFlatGridOutput {
    data class EditEpisodes (
        val container: ConstraintLayout,
        val adapter: FlatGridOfEditEpisodesAdapter
    ) : CreateFlatGridOutput()
    data class Basic (
        val container: ConstraintLayout
    ) : CreateFlatGridOutput()
}
fun createFlatGrid(context: Context, startsInfo: CreateFlatGridInput, widthh: Int, heightt: Int, changeImage: (Int) -> Unit): CreateFlatGridOutput {
    val containerToReturn = ConstraintLayout(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(
            widthh,
            heightt
        )
    }
    var res: CreateFlatGridOutput = CreateFlatGridOutput.Basic(containerToReturn)
    val elementHeight = round(46f * baseDensity).toInt()
    when (startsInfo) {
        is CreateFlatGridInput.EditEpisodes -> {
            var list = startsInfo.info as MutableList<episodeInfo>
            val adapterr = FlatGridOfEditEpisodesAdapter(context, list, widthh, elementHeight, changes = {list = it
                startsInfo.callback(it)}, changeImage = {changeImage(it)})
            res = CreateFlatGridOutput.EditEpisodes(containerToReturn, adapterr)
            val recyclerView = RecyclerView(context).apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    widthh,
                    heightt
                )
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = adapterr
                addItemDecoration(spaceItemDecoration(spaceItemDecorationInput(listOf(0,round(11f * baseDensity).toInt(),0,0), listOf(0,0,0,0), listOf(0,round(11f * baseDensity).toInt(),0,0))))
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
        is CreateFlatGridInput.Episodes -> {}
        is CreateFlatGridInput.Music -> {}
    }
    return res
}

sealed class OverLayLayer {
    data class CreateAnimePage(
        var name: String,
        var image: ImageData?,
        var description: String,
        var author: String,
        var genreList: List<Genre>,
        val episodesList: List<episodeInfo>,
        val type: ElementType,
        val parentId: Long
    ) : OverLayLayer()
    data class GenreChoice(
        var genreList: List<Pair<Boolean, Genre>>,
        val key: String
    ) : OverLayLayer()
    data class CreateCarouselPage(
        val localLayersList: MutableList<Layer> = mutableListOf<Layer>(),
        var name: String,
        var page: Int,
        var childsCornerRadius: SizeType?,
        var childsShowName: Boolean,
        var childsNamePosition: Int?,
        var childsShowAlreadyWatchedLine: Boolean,
        var layoutType: Int?,
        val showWatchAllButton: Boolean,
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
        var childsBaseWidth: Int? = null,
        var childsBaseHeight: Int? = null,
        var childsShowAuthor: Boolean = false,
    ) : OverLayLayer()
    data class PageWithSearch (
        var startsInfo: PageWithSearchInput,
        val key: String
    ): OverLayLayer()
}
sealed class PageWithSearchInput {
    data class EditAnimeCardEpisodes(
        var alreadyEnteredSearchText: String?,
        val list: MutableList<episodeInfo>,
    ) : PageWithSearchInput()
}
object CreateOvDialog {
    fun createCarouselPage(context: Context, startsInfo: OverLayLayer.CreateCarouselPage, layer: Layer, resultSenderViewModel: ResultSenderViewModel) : ConstraintLayout {
        val localLayersList = startsInfo.localLayersList
        localLayersList.add(Layer.MainPage(
            elevation = 1,
            pageId = 0,
            scrollPositionCarousels = mutableMapOf<Int,Int>(),
            activeDotPositionCarousels = mutableMapOf<Int,Int>(),
            scrollPositionsInPx = mutableMapOf<Int,Int>(),
            mainRecyclerScrollPositionInPx = 0,
            mainRecyclerScrollPosition = 0,
            state = 0
        ))

        val info = startsInfo

        val font = context.resources.getFont(R.font.google_sans_regular)
        val boldFont = context.resources.getFont(R.font.google_sans_bold)
        val maxPageWidth = round(1000f * baseDensity).toInt()
        val actualWidth = min(screenWidth, maxPageWidth)
        val marginLeft = round(16f * baseDensity).toInt()
        val marginTop = round(12f * baseDensity).toInt()
        val previewContainerMarginTop = marginTop * 2
        var hTextSizee = round(24f*baseDensity)
        val hTextText = "Создание карусели"
        val hTextMaxWidth = actualWidth - marginLeft*2
        val elementHeight = round(57f * baseDensity).toInt()
        val hBtn = round(32f * baseDensity).toInt()
        for (i in steps) {
            val opT = optimizeText(hTextText, hTextMaxWidth, i, false, boldFont, 1)
            if (opT.firstLine[opT.firstLine.lastIndex].toString() != "." && i <= hTextSizee) {
                hTextSizee = i
                break
            }
        }
        val container = ConstraintLayout(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                screenWidth + leftInsetWidth + rightInsetWidth,
                screenHeight + statusBarHeight + navigationBarHeight
            ).apply {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            }
            setPadding(0, statusBarHeight, 0, navigationBarHeight)
            clipToPadding = false
            setBackgroundColor("#181619".toColorInt())
            setOnClickListener {
            }
            setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                    }
                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                    }
                }
                true
            }
        }

        val hText = TextView(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.setMargins(0,marginTop,0,0)
            setTextColor("#FFFFFF".toColorInt())
            setTextSize(TypedValue.COMPLEX_UNIT_PX, hTextSizee)
            typeface = boldFont
            text = hTextText
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            id = View.generateViewId()
            layoutParams = lp1
            includeFontPadding = false
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
        }
        container.addView(hText)

        val scrollContainerr = ScrollView(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                screenHeight - previewContainerMarginTop - marginTop - hText.measuredHeight
            )
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToBottom = hText.id
            lp1.setMargins(0,previewContainerMarginTop,0,0)
            layoutParams = lp1
            val paddingHorizontal = if (screenWidth > maxPageWidth) round((screenWidth - maxPageWidth).toFloat() / 2f).toInt() else 0
            setPadding(leftInsetWidth + paddingHorizontal,0,rightInsetWidth + paddingHorizontal,hBtn + (marginTop*2))
            isVerticalScrollBarEnabled = false
        }
        val constraintLayoutInsideScrolConainerr = ConstraintLayout(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                actualWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = lp1
        }

        val previewContainerWidth = actualWidth - marginLeft*2
        val previewContainerHeight = round(previewContainerWidth / 1.258f).toInt() + (marginTop * 2).coerceIn(0, round(350f*baseDensity).toInt())

        val previewContainerForeground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(previewContainerWidth, SizeType.MEDIUM)
            setStroke(round(2f*baseDensity).toInt(), "#9C9C9C".toColorInt())
        }
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
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,0,0,0)
            layoutParams = layoutparams1
            background = previewContainerBackground
            foreground = previewContainerForeground
            id = View.generateViewId()
        }
        val previewAdapter = CarouselsAdapter(context, addCardToCarousel = {}, clickOnItem = {}, true, localLayersList, previewContainerWidth)
        val recyclerView = RecyclerView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                previewContainerWidth,
                screenHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,round(marginTop*1.5f).toInt(),0,0)
            layoutParams = layoutparams1
            layoutManager = object : LinearLayoutManager(context, VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            adapter = previewAdapter
            val newId = View.generateViewId()
            id = newId
        }
        previewContainer.addView(recyclerView)
        val previewErrorTextView = TextView(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                previewContainerWidth - (round(19f*baseDensity).toInt()*2),
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            setTextColor("#BF9C9C9C".toColorInt())
            setTextSize(TypedValue.COMPLEX_UNIT_PX, hTextSizee)
            typeface = boldFont
            text = "Превью недоступно из-за ошибки в настройках карусели"
            layoutParams = lp1
            visibility = View.GONE
            gravity = Gravity.CENTER_HORIZONTAL
        }
        previewContainer.addView(previewErrorTextView)
        constraintLayoutInsideScrolConainerr.addView(previewContainer)

        previewContainer.post {
            previewAdapter.firstHolderHeightCallback = {height -> run {
                previewContainer.post {
                    scrollContainerr.post {
                        val scroll = scrollContainerr.scrollY
                        val previewConHeight = previewContainer.height
                        val newHeight = height.coerceIn(0, round(450f * baseDensity).toInt())
                        val newPreviewConHeight = newHeight + (marginTop * 2)
                        val diff = newPreviewConHeight - previewConHeight
                        val previewContainerlp1 = previewContainer.layoutParams as ConstraintLayout.LayoutParams
                        previewContainerlp1.height = newPreviewConHeight
                        previewContainer.layoutParams = previewContainerlp1
                        val recyclerViewlp1 = recyclerView.layoutParams as ConstraintLayout.LayoutParams
                        recyclerViewlp1.height = newHeight
                        recyclerView.layoutParams = recyclerViewlp1
                        scrollContainerr.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                            override fun onPreDraw(): Boolean {
                                if (scrollContainerr.viewTreeObserver.isAlive) {
                                    scrollContainerr.viewTreeObserver.removeOnPreDrawListener(this)
                                } else {
                                    scrollContainerr.viewTreeObserver.removeOnPreDrawListener(this)
                                }
                                if (diff != 0 && scroll > round(previewConHeight.toFloat() / 2f).toInt()) {
                                    scrollContainerr.scrollY = scroll + diff
                                }
                                return true
                            }
                        })
                        previewContainer.invalidate()
                        recyclerView.invalidate()
                        scrollContainerr.invalidate()
                        previewContainer.requestLayout()
                        recyclerView.requestLayout()
                        scrollContainerr.requestLayout()
                    }
                }
            }
            }
        }


        val scrollContainerBackgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(previewContainerWidth, SizeType.MEDIUM)
            setColor("#29262C".toColorInt())
        }

        val scrollContainerForegroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(previewContainerWidth, SizeType.MEDIUM)
            setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
        }

        val containerInsideScrollContainerForeground = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                previewContainerWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToBottom = previewContainer.id
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,previewContainerMarginTop,0,0)
            layoutParams = layoutparams1
            id = View.generateViewId()
            foreground = scrollContainerForegroundDrawable
        }
        val containerInsideScrollContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                previewContainerWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            id = View.generateViewId()
            background = scrollContainerBackgroundDrawable
        }
        containerInsideScrollContainerForeground.addView(containerInsideScrollContainer)
        constraintLayoutInsideScrolConainerr.addView(containerInsideScrollContainerForeground)

        var choiceIcoRow: ConstraintLayout? = null
        var presetsRow: ConstraintLayout? = null
        var presetsRowlp1: ConstraintLayout.LayoutParams? = null
        var choiceIcoRowlp1: ConstraintLayout.LayoutParams? = null
        var showIcoRow: ConstraintLayout? = null
        var showIcoRowlp1: ConstraintLayout.LayoutParams? = null
        var carouselType: ConstraintLayout? = null
        var carouselTypelp1: ConstraintLayout.LayoutParams? = null
        var carouselMaketType: ConstraintLayout? = null
        var carouselMaketTypelp1: ConstraintLayout.LayoutParams? = null
        var gridSizes: ConstraintLayout? = null
        var gridSizeslp1: ConstraintLayout.LayoutParams? = null
        var turnOnAdaptiveGridSizesConstraintLayout: ConstraintLayout? = null
        var turnOnAdaptiveGridSizesConstraintLayoutlp1: ConstraintLayout.LayoutParams? = null
        var turnOnAdaptiveGridSizes: ConstraintLayout? = null
        var turnOnAdaptiveGridSizeslp1: ConstraintLayout.LayoutParams? = null
        var gridSizes2: ConstraintLayout? = null
        var gridSizes2lp1: ConstraintLayout.LayoutParams? = null
        var showCardsName: ConstraintLayout? = null
        var showCardsNamelp1: ConstraintLayout.LayoutParams? = null
        var cardsNamePosition: ConstraintLayout? = null
        var cardsNamePositionlp1: ConstraintLayout.LayoutParams? = null
        var showCardsAuthor: ConstraintLayout? = null
        var showCardsAuthorlp1: ConstraintLayout.LayoutParams? = null
        var showAlreadyWatchedLine: ConstraintLayout? = null
        var showAlreadyWatchedLinelp1: ConstraintLayout.LayoutParams? = null
        var cardsCornerRadius: ConstraintLayout? = null
        var cardsCornerRadiuslp1: ConstraintLayout.LayoutParams? = null
        var cardsBaseSize: ConstraintLayout? = null
        var cardsBaseSizelp1: ConstraintLayout.LayoutParams? = null
        var turnOnDovodchik: ConstraintLayout? = null
        var turnOnDovodchiklp1: ConstraintLayout.LayoutParams? = null
        var showDovodchikDots: ConstraintLayout? = null
        var showDovodchikDotslp1: ConstraintLayout.LayoutParams? = null
        var turnOnDovodchikBlockView: View? = null
        var turnOnDovodchikConstraintLayout: ConstraintLayout? = null
        var addButtonContainerBlockView: View? = null
        var turnOnDovodchikSwitchAnimId = -1L
        var addButtonContainer: ConstraintLayout? = null


        val objectsInOneLineErrorTag = "CreateCarouselPage_objectsInOneLine_is_greater_then_maxObjectsInOneLineForAdaptiveSize"
        val turnOnDovodchikAnimId = generateAnimationId()

        fun createChildWrapper(startsInfo1: OverLayLayer.CreateCarouselPage, positionn: Int): objectData2 {
            val elType = if (startsInfo1.carouselType == CarouselType.Music || startsInfo1.carouselType == CarouselType.PlaylistNMusic) {
                ElementType.Music} else if (startsInfo1.carouselType == CarouselType.Manga) {
                ElementType.Manga} else if (startsInfo1.carouselType == CarouselType.Playlist) {
                ElementType.Playlist} else {ElementType.Anime}
            val baseCardWidth = startsInfo1.childsBaseWidth ?: calculateBaseCardSize(elType, SizeType.MEDIUM).first
            val baseCardHeight = startsInfo1.childsBaseHeight ?: calculateBaseCardSize(elType, SizeType.MEDIUM).second
            val childWrapper = objectData2(
                page = 0,
                position = positionn,
                name = "Карточка ${positionn+1}",
                length = 1L,
                alreadyWatched = 1L,
                image = null,
                childs = emptyList(),
                elementType = elType,
                width = baseCardWidth,
                height = baseCardHeight
            )
            return childWrapper
        }

        fun createCarouselPreview(startsInfo1: OverLayLayer.CreateCarouselPage) {
            val listOfAllExceptions = listOf(objectsInOneLineErrorTag)
            val listOfExceptionsThatDidNotWorked = mutableListOf(objectsInOneLineErrorTag)
            try {
                val objectsInOneLine = startsInfo1.objectsInOneLine
                val maxObjectsInOneLineForAdaptiveSize = startsInfo1.maxObjectsInOneLineForAdaptiveSize
                val adaptiveGridSize = startsInfo1.adaptiveGridSize
                if (objectsInOneLine != null && maxObjectsInOneLineForAdaptiveSize != null) {
                    if (objectsInOneLine > maxObjectsInOneLineForAdaptiveSize && adaptiveGridSize) {
                        throw Exception(objectsInOneLineErrorTag)
                    }
                }

                val childss = mutableListOf<objectData2>()
                for (i in 0 until 50) {
                    val child = createChildWrapper(startsInfo1, i)
                    childss.add(child)
                }

                val carouselWrapper = objectData2(
                    id = 0L,
                    page = 0,
                    position = 0,
                    name = startsInfo1.name,
                    length = 1L,
                    alreadyWatched = 0,
                    elementType = ElementType.Carousel,
                    childs = childss,
                    childsCornerRadius = startsInfo1.childsCornerRadius,
                    childsShowName = startsInfo1.childsShowName,
                    childsNamePosition = startsInfo1.childsNamePosition,
                    childsShowAlreadyWatchedLine = startsInfo1.childsShowAlreadyWatchedLine,
                    layoutType = startsInfo1.layoutType,
                    objectsInOneLine = startsInfo1.objectsInOneLine,
                    maxLines = startsInfo1.maxLines,
                    maxObjectsInOneLineForAdaptiveSize = startsInfo1.maxObjectsInOneLineForAdaptiveSize,
                    maxLinesForAdaptiveSize = startsInfo1.maxLinesForAdaptiveSize,
                    adaptiveGridSize = startsInfo1.adaptiveGridSize,
                    dovodchik = if (startsInfo1.layoutType == 2) true else startsInfo1.dovodchik,
                    showDovodchikDots = startsInfo.showDovodchikDots,
                    carouselType = startsInfo1.carouselType,
                    carouselCollectionType = startsInfo1.carouselCollectionType,
                    childsShowAuthor = startsInfo1.childsShowAuthor
                )
                previewAdapter.submitList(listOf<objectData2>(carouselWrapper))
            }
            catch (e: Exception) {
                when (e.message) {
                    objectsInOneLineErrorTag -> run {
                        listOfExceptionsThatDidNotWorked.remove(objectsInOneLineErrorTag)
                        val gridSizes2 = gridSizes2
                        if (gridSizes2 != null) {
                            deleteErrorFromRow(gridSizes2, objectsInOneLineErrorTag)
                            addErrorToRow(gridSizes2, context.getString(R.string.CreateCarouselPage_objectsInOneLineErrorText), context, objectsInOneLineErrorTag)
                            val til: OutlinedTextField? = gridSizes2.findViewWithTag("columnsInput")
                            til?.changeStrokeColor("#db4242".toColorInt())
                        }
                    }
                }
            }
            finally {
                listOfExceptionsThatDidNotWorked.forEach { exception ->
                    run {
                        when (exception) {
                            objectsInOneLineErrorTag -> run {
                                val gridSizes2 = gridSizes2
                                if (gridSizes2 != null) {
                                    deleteErrorFromRow(gridSizes2, objectsInOneLineErrorTag)
                                    val til: OutlinedTextField? = gridSizes2.findViewWithTag("columnsInput")
                                    til?.changeStrokeColor("#809C9C9C".toColorInt())
                                }
                            }
                        }
                    }
                }
                if (listOfExceptionsThatDidNotWorked.size != listOfAllExceptions.size) {
                    addButtonContainerBlockView?.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    previewErrorTextView.visibility = View.VISIBLE
                    addButtonContainer?.alpha = 0.6f
                }
                else {
                    recyclerView.visibility = View.VISIBLE
                    previewErrorTextView.visibility = View.GONE
                    addButtonContainerBlockView?.visibility = View.GONE
                    addButtonContainer?.alpha = 1f
                }
                val turnOnDovodchik = turnOnDovodchik
                if (turnOnDovodchik != null) {
                    val switchButton: ViewGroup? = turnOnDovodchik.findViewWithTag<ViewGroup>("switch_button")
                    if (switchButton != null && turnOnDovodchikSwitchAnimId != -1L) {
                        toggleSwitchButtonAnimation(switchButton, if (startsInfo1.layoutType == 2) true else startsInfo1.dovodchik, turnOnDovodchikSwitchAnimId, false)
                    }
                }
                val showDovodchikDots = showDovodchikDots
                val turnOnDovodchikConstraintLayout = turnOnDovodchikConstraintLayout
                if (showDovodchikDots != null && turnOnDovodchik != null&& turnOnDovodchikConstraintLayout != null) {
                    toggleExtensionAnimation(turnOnDovodchikConstraintLayout, listOf(showDovodchikDots), turnOnDovodchik, if (startsInfo1.layoutType == 2) true else startsInfo1.dovodchik, true, turnOnDovodchikAnimId)
                }
                if (startsInfo1.layoutType == 2) {
                    turnOnDovodchikBlockView?.visibility = View.VISIBLE
                    turnOnDovodchik?.alpha = 0.6f
                }
                else {
                    turnOnDovodchikBlockView?.visibility = View.GONE
                    turnOnDovodchik?.alpha = 1f
                }
            }
        }

        val elType = if (startsInfo.carouselType == CarouselType.Music || startsInfo.carouselType == CarouselType.PlaylistNMusic) {
            ElementType.Music} else if (startsInfo.carouselType == CarouselType.Manga) {
            ElementType.Manga} else if (startsInfo.carouselType == CarouselType.Playlist) {
            ElementType.Playlist} else {ElementType.Anime}
        var cardsBaseSizeValue = if (startsInfo.childsBaseWidth == null) SizeType.MEDIUM else if (startsInfo.childsBaseWidth == calculateBaseCardSize(elType,
                SizeType.SMALL).first) SizeType.SMALL else if (startsInfo.childsBaseWidth == calculateBaseCardSize(elType,
                SizeType.MEDIUM).first) SizeType.MEDIUM else if (startsInfo.childsBaseWidth == calculateBaseCardSize(elType,
                SizeType.LARGE).first) SizeType.LARGE else SizeType.MEDIUM

        val ml = round(previewContainerWidth.toFloat() / 28.42f).toInt().coerceIn(0, round(32f*baseDensity).toInt())
        val nameInputTextSizee = getTextSizeByHeight(round(hBtn.toFloat() / 2f).toInt(), font, context = context)
        val nameInput = createOutlinedTextField(context, (previewContainerWidth - (ml * 2)), SizeType.SMALL, hBtn, "Название", maxLiness = 1, alreadyEnteredText = if (startsInfo.name != "") { startsInfo.name } else { null }, textSizee = nameInputTextSizee, gravityy = Gravity.CENTER_VERTICAL)
        val nameInputlp1 = nameInput.layoutParams as ConstraintLayout.LayoutParams
        nameInputlp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        nameInputlp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        nameInputlp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        nameInputlp1.setMargins(0, marginLeft,0,0)
        nameInput.layoutParams = nameInputlp1
        nameInput.id = View.generateViewId()
        containerInsideScrollContainer.addView(nameInput)
        val nameInputt = nameInput.findTextInputEditText()
        nameInputt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                info.name = nameInputt.text.toString().trim()
                createCarouselPreview(info)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        presetsRow = createBSDButton("Пресеты", null, true, context, previewContainerWidth, elementHeight)
        presetsRowlp1 = presetsRow.layoutParams as ConstraintLayout.LayoutParams
        presetsRowlp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        presetsRowlp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        presetsRowlp1.topToBottom = nameInput.id
        presetsRow.layoutParams = presetsRowlp1
        presetsRow.id = View.generateViewId()
        containerInsideScrollContainer.addView(presetsRow)

        val showIcoRowAnimId = generateAnimationId()
        val showIcoRowConstraintLayout = ConstraintLayout(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                previewContainerWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToBottom = presetsRow.id
            layoutParams = lp1
            id = View.generateViewId()
        }
        containerInsideScrollContainer.addView(showIcoRowConstraintLayout)
        showIcoRow = createSwitchButtonRow(context, startsInfo.showIco, previewContainerWidth, elementHeight, "Показывать иконку", null,
            callback1 = {
                value -> run {
                    info.showIco = value
                    createCarouselPreview(info)
                    val choiceIcoRow = choiceIcoRow
                    val showIcoRow = showIcoRow
                    if (choiceIcoRow != null && showIcoRow != null) {
                        toggleExtensionAnimation(rootContainer = showIcoRowConstraintLayout, rows = listOf(choiceIcoRow), animSourceRow = showIcoRow, targetState = value, animate = true, animationId = showIcoRowAnimId)
                    }
                }
            })

        showIcoRowlp1 = showIcoRow.layoutParams as ConstraintLayout.LayoutParams
        showIcoRowlp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        showIcoRowlp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        showIcoRowlp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        showIcoRow.id = View.generateViewId()
        showIcoRow.layoutParams = showIcoRowlp1
        showIcoRow.elevation = 10f
        showIcoRowConstraintLayout.addView(showIcoRow)

        choiceIcoRow = createBSDButton("Иконка", null, false, context, previewContainerWidth, elementHeight)
        choiceIcoRowlp1 = choiceIcoRow.layoutParams as ConstraintLayout.LayoutParams
        choiceIcoRowlp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        choiceIcoRowlp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        choiceIcoRowlp1.topToTop = showIcoRow.id
        choiceIcoRow.id = View.generateViewId()
        val choiceIcoButtonSize = round(elementHeight.toFloat() / 1.786f).toInt()
        val choiceIcoButtonBackgroundDrawable = createOutlinedbackground(SizeType.SMALL, choiceIcoButtonSize, round(1f*baseDensity).toInt(), 0.5f)
        val choiceIcoButtonContainer = ConstraintLayout(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                choiceIcoButtonSize,
                choiceIcoButtonSize
            )
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.setMargins(0,0,calculateLeftAndRightMarginForRows(previewContainerWidth).marginRight,0)
            layoutParams = lp1
            background = choiceIcoButtonBackgroundDrawable
            tag = "choiceIcoButton"
        }
        val choiceIcoButton = ImageView(context).apply {
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
            if (startsInfo.ico != null) {
                visibility = View.GONE
            }
        }
        val choiceIcoButton2 = CardView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            radius = 10000f
            if (startsInfo.ico == null) {
                visibility = View.GONE
            }
        }
        val choiceIcoButton2Image = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            scaleType = ImageView.ScaleType.CENTER_CROP
            loadImage(startsInfo.ico)
        }
        choiceIcoButton2.addView(choiceIcoButton2Image)
        choiceIcoButtonContainer.addView(choiceIcoButton2)
        choiceIcoButtonContainer.addView(choiceIcoButton)
        choiceIcoRow.addView(choiceIcoButtonContainer)
        showIcoRowConstraintLayout.addView(choiceIcoRow)
        choiceIcoButtonContainer.setOnClickListener {
            choiceIcoButtonContainer.requestFocus()
            resultSenderViewModel.sendResult(ResultKeys.SELECT_FILE, SelectFileInput(fileType.IMAGE, ResultKeys.CREATE_CAROUSEL_PAGE_CHANGE_ICO))
        }
        toggleExtensionAnimation(rootContainer = showIcoRowConstraintLayout, rows = listOf(choiceIcoRow), animSourceRow = showIcoRow, targetState = startsInfo.showIco, animate = false, animationId = showIcoRowAnimId)

        carouselType = createDropdownRow(context, previewContainerWidth, elementHeight, "Тип карусели", null, listOf(Pair("Аниме",
            BsdButtonsTags.none), Pair("Музыка",
            BsdButtonsTags.none), Pair("Манга",
            BsdButtonsTags.none), Pair("Плейлисты",
            BsdButtonsTags.none), Pair("Аниме и манга",
            BsdButtonsTags.none), Pair("Плейлисты и музыка",
            BsdButtonsTags.none)), onItemSelected = {name, tag -> run {
                    info.carouselType = when (name) {
                        "Аниме" -> { CarouselType.Anime }
                        "Музыка" -> { CarouselType.Music }
                        "Манга" -> { CarouselType.Manga }
                        "Плейлисты" -> { CarouselType.Playlist }
                        "Аниме и манга" -> { CarouselType.AnimeNManga }
                        else -> { CarouselType.PlaylistNMusic }
                    }
                    val elType = if (info.carouselType == CarouselType.Music || info.carouselType == CarouselType.PlaylistNMusic) {
                        ElementType.Music} else if (info.carouselType == CarouselType.Manga) {
                        ElementType.Manga} else if (info.carouselType == CarouselType.Playlist) {
                        ElementType.Playlist} else {ElementType.Anime}
                    val cardsBaseSize = calculateBaseCardSize(elType, cardsBaseSizeValue)
                    info.childsBaseWidth = cardsBaseSize.first
                    info.childsBaseHeight = cardsBaseSize.second
                    createCarouselPreview(info)
                }
            }, alreadyValue = when (startsInfo.carouselType) {
                CarouselType.Anime -> 0
                CarouselType.Music -> 1
                CarouselType.Manga -> 2
                CarouselType.Playlist -> 3
                CarouselType.AnimeNManga -> 4
                CarouselType.PlaylistNMusic -> 5
            })

        carouselTypelp1 = carouselType.layoutParams as ConstraintLayout.LayoutParams
        carouselTypelp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        carouselTypelp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        carouselTypelp1.topToBottom = showIcoRowConstraintLayout.id
        carouselType.layoutParams = carouselTypelp1
        carouselType.id = View.generateViewId()
        containerInsideScrollContainer.addView(carouselType)

        val carouselMaketTypeConstraintLayout = ConstraintLayout(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                previewContainerWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToBottom = carouselType.id
            layoutParams = lp1
            id = View.generateViewId()
        }
        val carouselMaketTypeAnimId = generateAnimationId()
        carouselMaketType = createSegmentedButtonRow(context, previewContainerWidth, elementHeight, listOf(
            SegmentedButtonOptions("Обычный", null, (startsInfo.layoutType == 1 || startsInfo.layoutType == null)),
            SegmentedButtonOptions("Из сеток", null, (startsInfo.layoutType == 2)),
            SegmentedButtonOptions("Сетка", null, startsInfo.layoutType == 0)
        ), null, "Тип макета карусели", callback = {
            value -> run {
                info.layoutType = if (value == 2f) 0 else if (value == 0f) 1 else 2
                createCarouselPreview(info)
                val turnOnAdaptiveGridSizesConstraintLayout = turnOnAdaptiveGridSizesConstraintLayout
                val carouselMaketType = carouselMaketType
                val gridSizes = gridSizes
                if (turnOnAdaptiveGridSizesConstraintLayout != null && carouselMaketType != null && gridSizes != null) {
                    toggleExtensionAnimation(
                        carouselMaketTypeConstraintLayout,
                        listOf(gridSizes, turnOnAdaptiveGridSizesConstraintLayout),
                        carouselMaketType,
                        (value == 1f || value == 2f),
                        true,
                        carouselMaketTypeAnimId
                    )
                }

            }
        })

        carouselMaketTypelp1 = carouselMaketType.layoutParams as ConstraintLayout.LayoutParams
        carouselMaketTypelp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        carouselMaketTypelp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        carouselMaketTypelp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        carouselMaketType.layoutParams = carouselMaketTypelp1
        carouselMaketType.id = View.generateViewId()
        containerInsideScrollContainer.addView(carouselMaketTypeConstraintLayout)
        carouselMaketTypeConstraintLayout.addView(carouselMaketType)

        gridSizes = createBSDButton("Размеры сетки", null, false, context, previewContainerWidth, elementHeight)
        gridSizeslp1 = gridSizes.layoutParams as ConstraintLayout.LayoutParams
        gridSizeslp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        gridSizeslp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        gridSizeslp1.topToTop = carouselMaketType.id
        gridSizes.id = View.generateViewId()
        gridSizes.layoutParams = gridSizeslp1
        val inputsTextSizee = getTextSizeByHeight(round(choiceIcoButtonSize.toFloat() / 2f).toInt(), font, context = context)
        val gridSizesTextView: TextView? = gridSizes.findViewWithTag("textView")
        val inputsColumsWidth = calculateIdealWidthForOutlinedTextFieldForOvDialog(inputsTextSizee, "Столбцов", context, paddingHorizontal = round(8f*baseDensity).toInt(), twoSidePadding = true, gravityy = Gravity.CENTER_VERTICAL)
        val inputsLinesWidth = calculateIdealWidthForOutlinedTextFieldForOvDialog(inputsTextSizee, "Строк", context, paddingHorizontal = round(8f*baseDensity).toInt(), twoSidePadding = true, gravityy = Gravity.CENTER_VERTICAL)
        val gridSizesTextViewlp1 = gridSizesTextView?.layoutParams as? ConstraintLayout.LayoutParams
        gridSizesTextViewlp1?.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        gridSizesTextView?.layoutParams = gridSizesTextViewlp1
        gridSizesTextView?.maxWidth = previewContainerWidth - (calculateLeftAndRightMarginForRows(previewContainerWidth).marginLeft * 2) - (calculateLeftAndRightMarginForRows(previewContainerWidth).marginRight * 2) - inputsLinesWidth - inputsColumsWidth
        carouselMaketTypeConstraintLayout.addView(gridSizes)

        val inputsWidth = max(inputsColumsWidth, inputsLinesWidth)
        val columnsInput = createOutlinedTextField(context, inputsWidth, SizeType.SMALL, choiceIcoButtonSize, "Столбцов", maxLiness = 1, alreadyEnteredText = startsInfo.objectsInOneLine?.toString(), textSizee = inputsTextSizee, gravityy = Gravity.CENTER, inputTypee = InputType.TYPE_CLASS_NUMBER, paddingHorizontal = 0)
        val linesInput = createOutlinedTextField(context, inputsWidth, SizeType.SMALL, choiceIcoButtonSize, "Строк", maxLiness = 1, alreadyEnteredText = startsInfo.objectsInOneLine?.toString(), textSizee = inputsTextSizee, gravityy = Gravity.CENTER, inputTypee = InputType.TYPE_CLASS_NUMBER, paddingHorizontal = 0)
        val linesInputlp1 = linesInput.layoutParams as ConstraintLayout.LayoutParams
        linesInputlp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        linesInputlp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        linesInputlp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        linesInputlp1.setMargins(0,0,calculateLeftAndRightMarginForRows(previewContainerWidth).marginRight,0)
        linesInput.layoutParams = linesInputlp1
        linesInput.id = View.generateViewId()
        linesInput.tag = "linesInput"

        val columnsInputlp1 = columnsInput.layoutParams as ConstraintLayout.LayoutParams
        columnsInputlp1.endToStart = linesInput.id
        columnsInputlp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        columnsInputlp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        columnsInputlp1.setMargins(0,0,calculateLeftAndRightMarginForRows(previewContainerWidth).marginRight,0)
        columnsInput.layoutParams = columnsInputlp1
        columnsInput.id = View.generateViewId()
        columnsInput.tag = "columnsInput"

        val linesInputt = linesInput.findTextInputEditText()
        linesInputt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val maxLinesStr = linesInputt.text?.toString() ?: ""
                info.maxLines = if (maxLinesStr == "") null else maxLinesStr.toInt().coerceIn(1, Int.MAX_VALUE)
                createCarouselPreview(info)
            }
            override fun afterTextChanged(s: Editable?) {
                if ((linesInputt.text?.toString() ?: "") != (info.maxLines?.toString() ?: "")) {
                    linesInputt.setText(info.maxLines?.toString() ?: "")
                }
                linesInputt.setSelection(linesInputt.text?.length ?: 0)
            }
        })

        val columnsInputt = columnsInput.findTextInputEditText()
        columnsInputt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val objectsInOneLineStr = columnsInputt.text?.toString() ?: ""
                info.objectsInOneLine = if (objectsInOneLineStr == "") null else objectsInOneLineStr.toInt().coerceIn(1, Int.MAX_VALUE)
                createCarouselPreview(info)
            }
            override fun afterTextChanged(s: Editable?) {
                if ((columnsInputt.text?.toString() ?: "") != (info.objectsInOneLine?.toString() ?: "")) {
                    columnsInputt.setText(info.objectsInOneLine?.toString() ?: "")
                }
                columnsInputt.setSelection(columnsInputt.text?.length ?: 0)
            }
        })

        gridSizes.addView(linesInput)
        gridSizes.addView(columnsInput)

        val turnOnAdaptiveGridSizesAnimId = generateAnimationId()
        turnOnAdaptiveGridSizesConstraintLayout = ConstraintLayout(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                previewContainerWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = gridSizes.id
            }
            id = View.generateViewId()
        }
        turnOnAdaptiveGridSizesConstraintLayoutlp1 = turnOnAdaptiveGridSizesConstraintLayout.layoutParams as ConstraintLayout.LayoutParams
        carouselMaketTypeConstraintLayout.addView(turnOnAdaptiveGridSizesConstraintLayout)

        turnOnAdaptiveGridSizes = createSwitchButtonRow(context, startsInfo.adaptiveGridSize, previewContainerWidth, elementHeight, "Адаптивный режим", null, callback1 = { value ->
            info.adaptiveGridSize = value
            val gridSizes2 = gridSizes2
            val turnOnAdaptiveGridSizes = turnOnAdaptiveGridSizes
            createCarouselPreview(info)
            if (gridSizes2 != null && turnOnAdaptiveGridSizes != null) {
                toggleExtensionAnimation(turnOnAdaptiveGridSizesConstraintLayout, listOf(gridSizes2), turnOnAdaptiveGridSizes, value, true, turnOnAdaptiveGridSizesAnimId)
            }
        })
        turnOnAdaptiveGridSizeslp1 = turnOnAdaptiveGridSizes.layoutParams as ConstraintLayout.LayoutParams
        turnOnAdaptiveGridSizeslp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        turnOnAdaptiveGridSizeslp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        turnOnAdaptiveGridSizeslp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        turnOnAdaptiveGridSizes.layoutParams = turnOnAdaptiveGridSizeslp1
        turnOnAdaptiveGridSizes.id = View.generateViewId()
        turnOnAdaptiveGridSizesConstraintLayout.addView(turnOnAdaptiveGridSizes)

        gridSizes2 = createBSDButton("Максимальные размеры сетки", null, false, context, previewContainerWidth, elementHeight)
        gridSizes2lp1 = gridSizes2.layoutParams as ConstraintLayout.LayoutParams
        gridSizes2lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        gridSizes2lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        gridSizes2lp1.topToTop = turnOnAdaptiveGridSizes.id
        gridSizes2.id = View.generateViewId()
        gridSizes2.layoutParams = gridSizes2lp1
        val gridSizes2TextView: TextView? = gridSizes2.findViewWithTag("textView")
        val inputsColums2Width = calculateIdealWidthForOutlinedTextFieldForOvDialog(inputsTextSizee, "Столбцов", context, paddingHorizontal = round(8f*baseDensity).toInt(), twoSidePadding = true, gravityy = Gravity.CENTER_VERTICAL)
        val inputsLines2Width = calculateIdealWidthForOutlinedTextFieldForOvDialog(inputsTextSizee, "Строк", context, paddingHorizontal = round(8f*baseDensity).toInt(), twoSidePadding = true, gravityy = Gravity.CENTER_VERTICAL)
        val gridSizes2TextViewlp1 = gridSizes2TextView?.layoutParams as? ConstraintLayout.LayoutParams
        gridSizes2TextViewlp1?.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        gridSizes2TextView?.layoutParams = gridSizes2TextViewlp1
        gridSizes2TextView?.maxWidth = previewContainerWidth - (calculateLeftAndRightMarginForRows(previewContainerWidth).marginLeft * 2) - (calculateLeftAndRightMarginForRows(previewContainerWidth).marginRight * 2) - inputsLines2Width - inputsColums2Width
        gridSizes2TextView?.invalidate()
        gridSizes2TextView?.requestLayout()
        turnOnAdaptiveGridSizesConstraintLayout.addView(gridSizes2)

        val inputsTextSizee2 = getTextSizeByHeight(round(choiceIcoButtonSize.toFloat() / 2f).toInt(), font, context = context)
        val inputs2Width = max(inputsColums2Width, inputsLines2Width)
        val columnsInput2 = createOutlinedTextField(context, inputs2Width, SizeType.SMALL, choiceIcoButtonSize, "Столбцов", maxLiness = 1, alreadyEnteredText = startsInfo.maxObjectsInOneLineForAdaptiveSize?.toString(), textSizee = inputsTextSizee2, gravityy = Gravity.CENTER, inputTypee = InputType.TYPE_CLASS_NUMBER, paddingHorizontal = 0)
        val linesInput2 = createOutlinedTextField(context, inputs2Width, SizeType.SMALL, choiceIcoButtonSize, "Строк", maxLiness = 1, alreadyEnteredText = startsInfo.maxLinesForAdaptiveSize?.toString(), textSizee = inputsTextSizee2, gravityy = Gravity.CENTER, inputTypee = InputType.TYPE_CLASS_NUMBER, paddingHorizontal = 0)
        val linesInput2lp1 = linesInput2.layoutParams as ConstraintLayout.LayoutParams
        linesInput2lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        linesInput2lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        linesInput2lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        linesInput2lp1.setMargins(0,0,calculateLeftAndRightMarginForRows(previewContainerWidth).marginRight,0)
        linesInput2.layoutParams = linesInput2lp1
        linesInput2.id = View.generateViewId()
        linesInput2.tag = "linesInput"

        val columnsInput2lp1 = columnsInput2.layoutParams as ConstraintLayout.LayoutParams
        columnsInput2lp1.endToStart = linesInput2.id
        columnsInput2lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        columnsInput2lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        columnsInput2lp1.setMargins(0,0,calculateLeftAndRightMarginForRows(previewContainerWidth).marginRight,0)
        columnsInput2.layoutParams = columnsInput2lp1
        columnsInput2.id = View.generateViewId()
        columnsInput2.tag = "columnsInput"

        val linesInputt2 = linesInput2.findTextInputEditText()
        linesInputt2?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val maxLinesStr = linesInputt2.text?.toString() ?: ""
                info.maxLinesForAdaptiveSize = if (maxLinesStr == "") null else maxLinesStr.toInt().coerceIn(1, Int.MAX_VALUE)
                createCarouselPreview(info)
            }
            override fun afterTextChanged(s: Editable?) {
                if ((linesInputt2.text?.toString() ?: "") != (info.maxLinesForAdaptiveSize?.toString() ?: "")) {
                    linesInputt2.setText(info.maxLinesForAdaptiveSize?.toString() ?: "")
                }
                linesInputt2.setSelection(linesInputt2.text?.length ?: 0)
            }
        })

        val columnsInputt2 = columnsInput2.findTextInputEditText()
        columnsInputt2?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val maxObjectsInOneLineForAdaptiveSizeStr = columnsInputt2.text?.toString() ?: ""
                info.maxObjectsInOneLineForAdaptiveSize = if (maxObjectsInOneLineForAdaptiveSizeStr == "") null else maxObjectsInOneLineForAdaptiveSizeStr.toInt().coerceIn(1, Int.MAX_VALUE)
                createCarouselPreview(info)
            }
            override fun afterTextChanged(s: Editable?) {
                if ((columnsInputt2.text?.toString() ?: "") != (info.maxObjectsInOneLineForAdaptiveSize?.toString() ?: "")) {
                    columnsInputt2.setText(info.maxObjectsInOneLineForAdaptiveSize.toString())
                }
                columnsInputt2.setSelection(columnsInputt2.text?.length ?: 0)
            }
        })

        gridSizes2.addView(linesInput2)
        gridSizes2.addView(columnsInput2)

        toggleExtensionAnimation(turnOnAdaptiveGridSizesConstraintLayout, listOf(gridSizes2), turnOnAdaptiveGridSizes, startsInfo.adaptiveGridSize, false, turnOnAdaptiveGridSizesAnimId)
        toggleExtensionAnimation(carouselMaketTypeConstraintLayout, listOf(gridSizes, turnOnAdaptiveGridSizesConstraintLayout), carouselMaketType, (startsInfo.layoutType == 0 || startsInfo.layoutType == 2), false, carouselMaketTypeAnimId)


        val alreadyValuee = when (cardsBaseSizeValue) {
            SizeType.SMALL -> 0f
            SizeType.MEDIUM -> 1f
            SizeType.LARGE -> 2f
            else -> 1f
        }
        val cardsBaseSizeSliderRow = createSliderRow(context, previewContainerWidth, "Базовый размер карточек", null, listOf(Pair(0f, "SMALL"), Pair(1f, "MEDIUM"), Pair(2f, "LARGE")), elementHeight, true, alreadyValuee, false)
        cardsBaseSize = cardsBaseSizeSliderRow[0] as ConstraintLayout
        cardsBaseSizelp1 = cardsBaseSize.layoutParams as ConstraintLayout.LayoutParams
        cardsBaseSizelp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        cardsBaseSizelp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        cardsBaseSizelp1.topToBottom = carouselMaketTypeConstraintLayout.id
        cardsBaseSize.id = View.generateViewId()
        cardsBaseSize.layoutParams = cardsBaseSizelp1
        containerInsideScrollContainer.addView(cardsBaseSize)
        val cardsBaseSizeSlider = cardsBaseSizeSliderRow.last() as ConstraintLayout
        val cardsBaseSizeSlider1 = cardsBaseSizeSlider.getChildAt(0) as ConstraintLayout
        val cardsBaseSizeSlider2 = cardsBaseSizeSlider1.getChildAt(0) as ConstraintLayout
        val cardsBaseSizeSliderr = cardsBaseSizeSlider2.getChildAt(0) as Slider
        cardsBaseSizeSliderr.addOnChangeListener { _, value, _ ->
            cardsBaseSizeValue = when (value) {
                0f -> SizeType.SMALL
                1f -> SizeType.MEDIUM
                2f -> SizeType.LARGE
                else -> SizeType.MEDIUM
            }
            val elType = if (info.carouselType == CarouselType.Music || info.carouselType == CarouselType.PlaylistNMusic) {
                ElementType.Music} else if (info.carouselType == CarouselType.Manga) {
                ElementType.Manga} else if (info.carouselType == CarouselType.Playlist) {
                ElementType.Playlist} else {ElementType.Anime}
            info.childsBaseWidth = when(value) {
                0f -> calculateBaseCardSize(elType, SizeType.SMALL).first
                1f -> calculateBaseCardSize(elType, SizeType.MEDIUM).first
                2f -> calculateBaseCardSize(elType, SizeType.LARGE).first
                else -> calculateBaseCardSize(elType, SizeType.SMALL).first
            }
            info.childsBaseHeight = when(value) {
                0f -> calculateBaseCardSize(elType, SizeType.SMALL).second
                1f -> calculateBaseCardSize(elType, SizeType.MEDIUM).second
                2f -> calculateBaseCardSize(elType, SizeType.LARGE).second
                else -> calculateBaseCardSize(elType, SizeType.SMALL).second
            }
            createCarouselPreview(info)
        }

        val showCardsNameAnimId = generateAnimationId()
        val showCardsNameConstraintLayout = ConstraintLayout(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                previewContainerWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToBottom = cardsBaseSize.id
            layoutParams = lp1
            id = View.generateViewId()
        }
        showCardsName = createSwitchButtonRow(context, startsInfo.childsShowName, previewContainerWidth, elementHeight, "Показывать имя карточек", null, callback1 = {
            value -> run {
                info.childsShowName = value
                createCarouselPreview(info)
                val cardsNamePosition = cardsNamePosition
                val showCardsName = showCardsName
                if (showCardsName != null && cardsNamePosition != null) {
                    toggleExtensionAnimation(showCardsNameConstraintLayout, listOf(cardsNamePosition), showCardsName, value, true, showCardsNameAnimId)
                }
            }
        })
        showCardsNamelp1 = showCardsName.layoutParams as ConstraintLayout.LayoutParams
        showCardsNamelp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        showCardsNamelp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        showCardsNamelp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        showCardsName.id = View.generateViewId()
        showCardsName.layoutParams = showCardsNamelp1
        containerInsideScrollContainer.addView(showCardsNameConstraintLayout)
        showCardsNameConstraintLayout.addView(showCardsName)

        cardsNamePosition = createSegmentedButtonRow(context, previewContainerWidth, elementHeight, listOf(
            SegmentedButtonOptions("Внутри", null, startsInfo.childsNamePosition == 1 || startsInfo.childsNamePosition == null),
            SegmentedButtonOptions("Снаружи", null, startsInfo.childsNamePosition == 0)
        ), null, "Расположение имени карточек", callback = {
            value -> run {
                info.childsNamePosition = if (value == 0f) 1 else 0
                createCarouselPreview(info)
            }
        })
        cardsNamePositionlp1 = cardsNamePosition.layoutParams as ConstraintLayout.LayoutParams
        cardsNamePositionlp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        cardsNamePositionlp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        cardsNamePositionlp1.topToTop = showCardsName.id
        cardsNamePosition.layoutParams = cardsNamePositionlp1
        cardsNamePosition.id = View.generateViewId()
        showCardsNameConstraintLayout.addView(cardsNamePosition)
        toggleExtensionAnimation(showCardsNameConstraintLayout, listOf(cardsNamePosition), showCardsName, startsInfo.childsShowName, false, showCardsNameAnimId)

        showCardsAuthor = createSwitchButtonRow(context, startsInfo.childsShowAuthor, previewContainerWidth, elementHeight, "Показывать автора у карточек", null, callback1 = {
                value -> run {
            info.childsShowAuthor = value
            createCarouselPreview(info)
            }
        })
        showCardsAuthorlp1 = showCardsAuthor.layoutParams as ConstraintLayout.LayoutParams
        showCardsAuthorlp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        showCardsAuthorlp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        showCardsAuthorlp1.topToBottom = showCardsNameConstraintLayout.id
        showCardsAuthor.layoutParams = showCardsAuthorlp1
        showCardsAuthor.id = View.generateViewId()
        containerInsideScrollContainer.addView(showCardsAuthor)

        showAlreadyWatchedLine = createSwitchButtonRow(context, startsInfo.childsShowAlreadyWatchedLine, previewContainerWidth, elementHeight, "Показывать линию просмотра у карточек", null, callback1 = {
            value -> run {
                info.childsShowAlreadyWatchedLine = value
                createCarouselPreview(info)
            }
        })
        showAlreadyWatchedLinelp1 = showAlreadyWatchedLine.layoutParams as ConstraintLayout.LayoutParams
        showAlreadyWatchedLinelp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        showAlreadyWatchedLinelp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        showAlreadyWatchedLinelp1.topToBottom = showCardsAuthor.id
        showAlreadyWatchedLine.layoutParams = showAlreadyWatchedLinelp1
        showAlreadyWatchedLine.id = View.generateViewId()
        containerInsideScrollContainer.addView(showAlreadyWatchedLine)

        val alreadyValue = if (startsInfo.childsCornerRadius == null) 0f
        else if (startsInfo.childsCornerRadius == SizeType.SMALL) 1f
        else if (startsInfo.childsCornerRadius == SizeType.MEDIUM) 2f else if (startsInfo.childsCornerRadius == SizeType.LARGE) 3f
        else if (startsInfo.childsCornerRadius == SizeType.XLARGE) 4f else 0f
        val cardsCornerRadiusSliderRow = createSliderRow(context, previewContainerWidth, "Радиус закругления карточек", null, listOf(Pair(0f, "NONE"), Pair(1f, "SMALL"), Pair(2f, "MEDIUM"), Pair(3f, "LARGE"), Pair(4f, "XLARGE")), elementHeight, true, alreadyValue, false)
        cardsCornerRadius = cardsCornerRadiusSliderRow[0] as ConstraintLayout
        cardsCornerRadiuslp1 = cardsCornerRadius.layoutParams as ConstraintLayout.LayoutParams
        cardsCornerRadiuslp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        cardsCornerRadiuslp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        cardsCornerRadiuslp1.topToBottom = showAlreadyWatchedLine.id
        cardsCornerRadius.id = View.generateViewId()
        cardsCornerRadius.layoutParams = cardsCornerRadiuslp1
        containerInsideScrollContainer.addView(cardsCornerRadius)
        val slider = cardsCornerRadiusSliderRow.last() as ConstraintLayout
        val slider1 = slider.getChildAt(0) as ConstraintLayout
        val slider2 = slider1.getChildAt(0) as ConstraintLayout
        val sliderr = slider2.getChildAt(0) as Slider
        sliderr.addOnChangeListener { _, value, _ ->
            info.childsCornerRadius = when(value) {
                0f -> null
                1f -> SizeType.SMALL
                2f -> SizeType.MEDIUM
                3f -> SizeType.LARGE
                4f -> SizeType.XLARGE
                else -> SizeType.SMALL
            }
            createCarouselPreview(info)
        }

        turnOnDovodchikConstraintLayout = ConstraintLayout(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                previewContainerWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToBottom = cardsCornerRadius.id
            layoutParams = lp1
            id = View.generateViewId()
        }
        turnOnDovodchik = createSwitchButtonRow(context, startsInfo.dovodchik, previewContainerWidth, elementHeight, "Доводчик", null,
            callback1 = {
                value -> run {
                    info.dovodchik = value
                    createCarouselPreview(info)
                }
            },
            animIdCallback = {it -> turnOnDovodchikSwitchAnimId = it}
        )
        turnOnDovodchiklp1 = turnOnDovodchik.layoutParams as ConstraintLayout.LayoutParams
        turnOnDovodchiklp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        turnOnDovodchiklp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        turnOnDovodchiklp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        turnOnDovodchik.id = View.generateViewId()
        turnOnDovodchik.layoutParams = turnOnDovodchiklp1
        containerInsideScrollContainer.addView(turnOnDovodchikConstraintLayout)
        turnOnDovodchikConstraintLayout.addView(turnOnDovodchik)

        turnOnDovodchikBlockView = View(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            ).apply {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }
            visibility = if (startsInfo.layoutType == 2) View.VISIBLE else View.GONE
            setOnClickListener {
                Toast.makeText(
                    context,
                    "Доводчик принудительно включен параметром \"Тип макета карусели\"",
                    Toast.LENGTH_LONG
                ).show()
            }
            setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    view.performClick()
                }
                true
            }
            elevation = 1000f
        }

        turnOnDovodchik.addView(turnOnDovodchikBlockView)

        showDovodchikDots = createSwitchButtonRow(context, startsInfo.dovodchik, previewContainerWidth, elementHeight, "Показывать точки доводчика", null,
            callback1 = {
                value -> run {
                    info.showDovodchikDots = value
                    createCarouselPreview(info)
                }
            }
        )
        showDovodchikDotslp1 = showDovodchikDots.layoutParams as ConstraintLayout.LayoutParams
        showDovodchikDotslp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        showDovodchikDotslp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        showDovodchikDotslp1.topToTop = turnOnDovodchik.id
        showDovodchikDots.id = View.generateViewId()
        showDovodchikDots.layoutParams = showDovodchikDotslp1
        turnOnDovodchikConstraintLayout.addView(showDovodchikDots)
        showDovodchikDots.post {
            toggleExtensionAnimation(turnOnDovodchikConstraintLayout, listOf(showDovodchikDots), turnOnDovodchik,startsInfo.showDovodchikDots, false, turnOnDovodchikAnimId)
        }

        scrollContainerr.addView(constraintLayoutInsideScrolConainerr)
        container.addView(scrollContainerr)
        val addButtonBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(previewContainerWidth, SizeType.SMALL)
            setColor("#805EFF56".toColorInt())
        }

        addButtonContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                previewContainerWidth,
                hBtn
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,0,0,marginTop)
            layoutParams = layoutparams1
            background = addButtonBg
            tag = "add_card_button"
            elevation = 50f
            id = View.generateViewId()
        }

        addButtonContainerBlockView = View(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                previewContainerWidth,
                elementHeight
            ).apply {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = addButtonContainer.id
            }
            visibility = View.VISIBLE
            setOnClickListener {
                Toast.makeText(
                    context,
                    "Добавление недоступно из-за ошибки в настройках карусели",
                    Toast.LENGTH_LONG
                ).show()
            }
            setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    view.performClick()
                }
                true
            }
            elevation = 150f
        }

        val addButtonTextHeight = round(hBtn.toFloat() / 1.82f).toInt()
        val addButtonTextSize = getTextSizeByHeight(addButtonTextHeight, font, context = context)

        val addButtonText = TextView(context).apply {
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
            setTextSize(TypedValue.COMPLEX_UNIT_PX, addButtonTextSize)
            text = "Добавить"
            setTextColor("#FFFFFF".toColorInt())
        }

        addButtonContainer.addView(addButtonText)
        container.addView(addButtonContainer)
        container.addView(addButtonContainerBlockView)
        addButtonContainer.setOnClickListener {
            addButtonContainer.requestFocus()
            resultSenderViewModel.sendResult(ResultKeys.CREATE_CAROUSEL_APPLY, info)
        }

        val job = context.lifecycleOwner?.lifecycleScope?.launch {
            resultSenderViewModel.results.collect { (key, data) -> run {
                when (key) {
                    ResultKeys.CREATE_CAROUSEL_PAGE_CHANGE_ICO -> {
                        val dataa = data as ImageData
                        choiceIcoButton2.visibility = View.VISIBLE
                        choiceIcoButton.visibility = View.GONE
                        choiceIcoButtonContainer.background = null
                        choiceIcoButton2Image.loadImage(dataa)
                        info.ico = dataa
                        createCarouselPreview(info)
                    }
                }
            }
            }
        }
        if (layer is Layer.OverLay && job != null) {
            layer.activeJobs.add(job)
        }
        createCarouselPreview(startsInfo)

        constraintLayoutInsideScrolConainerr.requestLayout()
        constraintLayoutInsideScrolConainerr.invalidate()
        return container
    }
    @SuppressLint("ClickableViewAccessibility")
    fun createAnimePage(context: Context, startsInfo: OverLayLayer.CreateAnimePage, resultSenderViewModel: ResultSenderViewModel, openGenreChoice: () -> Unit, openEditEpisodesPage: (List<episodeInfo>, String?) -> Unit, layer: Layer) : ConstraintLayout {
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
        val containerWidth = actualWidth
        val containerHeight = screenHeight
        val bannerMarginTop = marginTop * 2
        val bannerAddIcoSize = round(bannerW.toFloat() / 3f).toInt()
        val nameInputWidth = min(round(320f * baseDensity).toInt(), (containerWidth - marginLeft - bannerW - marginLeft - marginTop))
        val nameInputHeight = hBtn
        val scrollContainerWidth = nameInputWidth + marginLeft + bannerW + marginLeft + marginTop
        var editEpisodesPageSearchText: String? = null
        val containerr = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                screenWidth + leftInsetWidth + rightInsetWidth,
                screenHeight + statusBarHeight + navigationBarHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setPadding(0, statusBarHeight, 0, navigationBarHeight)
            clipToPadding = false
            elevation = 100f
            setBackgroundColor("#181619".toColorInt())
        }
        val hTextId = View.generateViewId()
        val hText = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0, marginTop, 0, 0)
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
        val extraButton = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                hBtn,
                hBtn
            )
            layoutparams1.topToTop = hTextId
            layoutparams1.bottomToBottom = hTextId
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,0,marginTop,if(actualWidth > screenWidth) {(screenWidth - actualWidth)} else {0})
            layoutParams = layoutparams1
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
            imageTintList = ColorStateList.valueOf("#FFFFFF".toColorInt())
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
            layoutparams1.setMargins(0,bannerMarginTop,0,0)
            layoutParams = layoutparams1
            val paddingHorizontal = ((screenWidth - scrollContainerWidth).toFloat() / 2f).toInt()
            setPadding(paddingHorizontal + leftInsetWidth,0,paddingHorizontal + rightInsetWidth,(bannerMarginTop+hBtn))
            tag = "scroll_container"
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
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
        val bannerBgDrawable = createOutlinedbackground(SizeType.MEDIUM, bannerW, round(1f*baseDensity).toInt(), 0.5f)
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
            banner.requestFocus()
            resultSenderViewModel.sendResult(ResultKeys.SELECT_FILE, SelectFileInput(fileType.IMAGE, ResultKeys.CREATE_CARD_CHANGE_IMAGE))
        }
//        buttonsList.add(banner)

        val nameInputId = View.generateViewId()
        val nameInputTextSizee = getTextSizeByHeight(round(nameInputHeight.toFloat() / 2f).toInt(), font, context = context)
        val nameInput = createOutlinedTextField(context, nameInputWidth, SizeType.SMALL, nameInputHeight, "Введите название", Gravity.CENTER_VERTICAL,nameInputTextSizee,1, startsInfo.name)
        val lp1 = nameInput.layoutParams as ConstraintLayout.LayoutParams
        lp1.startToEnd = bannerId
        lp1.topToTop = bannerId
        lp1.setMargins(marginLeft,0,0,0)
        nameInput.layoutParams = lp1
        nameInput.id = nameInputId
        nameInput.tag = "name_input"
        container.addView(nameInput)
        val nameInputt = nameInput.findTextInputEditText()
        nameInputt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                startsInfo.name = nameInputt.text.toString().trim()
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
        val authorInputIcoPadding = round(nameInputHeight.toFloat() / 5f).toInt()
        val authorInput = createOutlinedTextField(context, nameInputWidth,SizeType.SMALL, nameInputHeight, "Автор", Gravity.CENTER_VERTICAL, nameInputTextSizee,1, startsInfo.author, ico = ImageData(
            ImageSource.SELF, R.drawable.search_ico.toString()), applyPaddingHorizontalToIco = false, overrideIcoColor = "#D9D9D9".toColorInt(),
            overrideIcoPaddings = Pair(authorInputIcoPadding,authorInputIcoPadding))
        val lp2 = authorInput.layoutParams as ConstraintLayout.LayoutParams
        lp2.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        lp2.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        authorInput.layoutParams = lp2
        authorInput.id = authorInputId
        authorInputContainer.addView(authorInput)
        container.addView(authorInputContainer)
        val authorInputt = authorInput.findTextInputEditText()
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
        val addGenreButtonBgDrawable = createOutlinedbackground(SizeType.SMALL, nameInputHeight, round(1f*baseDensity).toInt(), 0.5f)
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

        val genreContainerBackgroundDrawable = createOutlinedbackground(SizeType.SMALL, nameInputWidth, round(1f*baseDensity).toInt(), 0.5f)

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
            setOnClickListener {
                requestFocus()
            }
        }
        genreContainer.addView(genreGridView)
        container.addView(genreContainer)

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
            val addGenreButtonBgDrawable = createOutlinedbackground(SizeType.SMALL, nameInputHeight, round(1f*baseDensity).toInt(), 0.5f)
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
                openGenreChoice()
            }
        }


        val editBannerButtonSize = hBtn
        val editBannerButtonIcoSize = round(editBannerButtonSize.toFloat() / 1.87f).toInt()
        val editBannerButtonId = View.generateViewId()
        val editBannerButtonBgDrawable = createOutlinedbackground(SizeType.SMALL, editBannerButtonSize, round(1f*baseDensity).toInt(), 0.5f)
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

        val searchBannerContainerWidth = bannerW - editBannerButtonSize - marginBetweenInfoElements
        val searchButtonContainerDrawable = createOutlinedbackground(SizeType.SMALL, searchBannerContainerWidth,round(1f*baseDensity).toInt(), 0.5f)
        val searchTextSize = getTextSizeByHeight(editBannerButtonIcoSize,font, context = context)
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


        var episodesList: MutableList<episodeInfo> = startsInfo.episodesList as MutableList<episodeInfo>
        if (startsInfo.type != ElementType.Music) {
            val descriptionInputWidth = bannerW + marginLeft + nameInputWidth
            val descriptionInputHeight = round(descriptionInputWidth.toFloat() / 2.83f).toInt()
            val descriptionInputMarginTop = round(marginTop.toFloat() * 1.8f).toInt()
            val descriptionInputId = View.generateViewId()
            val descriptionInput = createOutlinedTextField(context, descriptionInputWidth,SizeType.SMALL, descriptionInputHeight, "Введите описание", Gravity.TOP, nameInputTextSizee, null, startsInfo.description)
            val lp3 = descriptionInput.layoutParams as ConstraintLayout.LayoutParams
            lp3.startToStart = bannerId
            lp3.topToBottom = editBannerButtonId
            lp3.setMargins(0,descriptionInputMarginTop,0,0)
            descriptionInput.layoutParams = lp3
            descriptionInput.id = descriptionInputId
            descriptionInput.tag = "description_input"
            container.addView(descriptionInput)

            val descriptionInputt = descriptionInput.findTextInputEditText()
            descriptionInputt?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    startsInfo.description = descriptionInputt.text.toString()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            val episodesHTextHeight = round(hText.measuredHeight.toFloat() / 1.208f).toInt()
            val episodesHTextSize = getTextSizeByHeight(episodesHTextHeight, boldFont, context = context)
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

            val openEditEpisodesPage = createBSDButton("Редактировать эпизоды", null, true, context, descriptionInputWidth, round(57f * baseDensity).toInt())
            val openEditEpisodesPagelp1 = openEditEpisodesPage.layoutParams as ConstraintLayout.LayoutParams
            openEditEpisodesPagelp1.startToStart = episodesHTextId
            openEditEpisodesPagelp1.topToBottom = episodesHTextId
            openEditEpisodesPagelp1.setMargins(0,marginTop,0,0)
            openEditEpisodesPage.layoutParams = openEditEpisodesPagelp1
            openEditEpisodesPage.tag = "open_edit_episodes_page"
            openEditEpisodesPage.id = View.generateViewId()
            openEditEpisodesPage.background = createOutlinedbackground(SizeType.SMALL, descriptionInputWidth, round(1f*baseDensity).toInt(), 0.5f)
            openEditEpisodesPage.setOnClickListener {
                openEditEpisodesPage(episodesList, editEpisodesPageSearchText)
            }
            container.addView(openEditEpisodesPage)
        }
        else if (startsInfo.type == ElementType.Playlist) {}
        else {}



        val addCardButtonBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(scrollContainerWidth, SizeType.SMALL)
            setColor("#805EFF56".toColorInt())
        }

        val addCardButtonContainer = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                actualWidth - (marginLeft*2),
                nameInputHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(0,0,0,marginTop)
            layoutParams = layoutparams1
            background = addCardButtonBg
            tag = "add_card_button"
            elevation = 101f
        }

        val addCardButtonTextHeight = round(nameInputHeight.toFloat() / 1.82f).toInt()
        val addCardButtonTextSize = getTextSizeByHeight(addCardButtonTextHeight, font, context = context)

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


        container.setOnClickListener {
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

        var clicked = false
        addCardButtonContainer.setOnClickListener {
            addCardButtonContainer.requestFocus()
            if (!clicked) {
                clicked = true
                val data = createCardApply(startsInfo.name, startsInfo.image, startsInfo.description, startsInfo.author, startsInfo.genreList, startsInfo.episodesList, startsInfo.parentId)
                resultSenderViewModel.sendResult(ResultKeys.CREATE_CARD_APPLY,data)
            }
        }
        val ls = mutableListOf<Pair<Boolean, Genre>>()
        for (i in startsInfo.genreList) {
            ls.add(Pair(true, i))
        }
        updateGenreList(ls)

        val job = context.lifecycleOwner?.lifecycleScope?.launch {
            resultSenderViewModel.results.collect { (key, data) -> run {
                when (key) {
                    ResultKeys.CREATE_CARD_GENRE_CHOICE -> {
                        val newGenreList = data as GenreChoiceOutput
                        updateGenreList(newGenreList.data)
                    }
                    ResultKeys.CREATE_CARD_CHANGE_IMAGE -> {
                        val dataa = data as? SelectFileOutput
                        if (dataa != null) {
                            bannerCardView.alpha = 1f
                            val newImage = ImageData(ImageSource.DEVICE, dataa.data)
                            bannerImageView.loadImage(newImage)
                            bannerAddIco.alpha = 0f
                            banner.background = null
                            startsInfo.image = newImage
                        }
                    }
                    ResultKeys.PAGE_WITH_SEARCH_EDIT_ANIME_CARD_EPISODES_CHANGE_SEARCH_INPUT_TEXT -> {
                        val dataa = data as? String
                        if (dataa != null) {
                            editEpisodesPageSearchText = dataa
                        }
                    }

                    ResultKeys.CREATE_CARD_APPLY_EPISODES_LIST -> {
                        val dataa = data as? MutableList<episodeInfo>
                        if (dataa != null) {
                            episodesList = dataa
                        }
                    }
                }
            }
            }
        }
        if (job != null) {
            if (layer is Layer.OverLay) {
                layer.activeJobs.add(job)
            }
        }
        return containerr
    }
    @SuppressLint("ClickableViewAccessibility")
    fun genreChoice(context: Context, startsInfo: OverLayLayer.GenreChoice, resultSenderViewModel: ResultSenderViewModel, close: () -> Unit) : ConstraintLayout {
        val font = context.resources.getFont(R.font.google_sans_regular)
        val boldFont = context.resources.getFont(R.font.google_sans_bold)
        val containerWidth = min(round(420f*baseDensity).toInt(), round(screenWidth.toFloat() / 1.25f).toInt())
        val textHeight = round(max(screenHeight,screenWidth).toFloat() / 30f).toInt().coerceIn(0, round(30f * baseDensity).toInt())
        val hTextSizee = getTextSizeByHeight(textHeight,boldFont, context = context)
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
            setOnClickListener {
                requestFocus()
            }
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
            setOnClickListener {
                requestFocus()
            }
            setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                    }
                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                    }
                }
                true
            }
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
            isVerticalScrollBarEnabled = false
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
        val buttonTextSizee = getTextSizeByHeight(buttonTextHeight, font, context = context)
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
            text = context.getString(R.string.Confirm)
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
            applyButtonContainer.requestFocus()
            resultSenderViewModel.sendResult(startsInfo.key, GenreChoiceOutput(fullGenreList))
            close()
        }
        denyButtonContainer.setOnClickListener {
            denyButtonContainer.requestFocus()
            close()
        }

        return container
    }
    fun pageWithSearch(startsInfo: PageWithSearchInput, context: Context, resultSenderViewModel: ResultSenderViewModel, key: String, layer: Layer, close: () -> Unit): ConstraintLayout {
        val font = context.resources.getFont(R.font.google_sans_regular)
        val boldFont = context.resources.getFont(R.font.google_sans_bold)
        val maxPageWidth = round(1000f * baseDensity).toInt()
        val actualWidth = min(screenWidth, maxPageWidth)
        val marginLeft = round(16f * baseDensity).toInt()
        val marginTop = round(12f * baseDensity).toInt()
        val contentContainerMarginTop = round(marginTop.toFloat() / 1f).toInt()
        var hTextSizee = round(24f*baseDensity)
        val hTextText = context.getString(R.string.EditingEpisodes)
        val hTextMaxWidth = actualWidth - marginLeft*2
        val hBtn = round(32f * baseDensity).toInt()
        for (i in steps) {
            val opT = optimizeText(hTextText, hTextMaxWidth, i, false, boldFont, 1)
            if (opT.firstLine[opT.firstLine.lastIndex].toString() != "." && i <= hTextSizee) {
                hTextSizee = i
                break
            }
        }

        val addExtraButton = when (startsInfo) {
            is PageWithSearchInput.EditAnimeCardEpisodes -> true
            else -> false
        }

        val alreadyEnteredSearchText = when (startsInfo) {
            is PageWithSearchInput.EditAnimeCardEpisodes -> startsInfo.alreadyEnteredSearchText
            else -> null
        }

        // 0 - Save button 1 - Confirm button
        val finalButtonType = when (startsInfo) {
            is PageWithSearchInput.EditAnimeCardEpisodes -> 0
            else -> 1
        }


        val container = ConstraintLayout(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                screenWidth + leftInsetWidth + rightInsetWidth,
                screenHeight + statusBarHeight + navigationBarHeight
            ).apply {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            }
            setPadding(0, statusBarHeight, 0, navigationBarHeight)
            clipToPadding = false
            setBackgroundColor("#181619".toColorInt())
            setOnClickListener {
            }
            setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                    }
                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                    }
                }
                true
            }
        }

        val hText = TextView(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.setMargins(0,marginTop,0,0)
            setTextColor("#FFFFFF".toColorInt())
            setTextSize(TypedValue.COMPLEX_UNIT_PX, hTextSizee)
            typeface = boldFont
            text = hTextText
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            id = View.generateViewId()
            layoutParams = lp1
            includeFontPadding = false
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
        }
        container.addView(hText)

        val searchInputWidth = actualWidth - marginLeft*2 - if (addExtraButton) ((marginLeft) + hBtn) else 0
        val searchInputConstraintLayout = ConstraintLayout(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                actualWidth - (marginLeft*2),
                hBtn
            )
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToBottom = hText.id
            lp1.setMargins(0,marginTop,0,0)
            layoutParams = lp1
            id = View.generateViewId()
        }
        val searchInputTextSizee = getTextSizeByHeight(round(hBtn.toFloat() / 2f).toInt(), font, context = context)
        val searchInputIcoPadding = round(hBtn.toFloat() / 6f).toInt()
        val searchInput = createOutlinedTextField(context = context, width = searchInputWidth,
            radius = SizeType.SMALL, heightt = hBtn, hintText = context.getString(R.string.Search),
            gravityy = Gravity.CENTER_VERTICAL, textSizee = searchInputTextSizee, maxLiness = 1,
            alreadyEnteredText = alreadyEnteredSearchText, inputTypee = InputType.TYPE_CLASS_TEXT,
            ico = ImageData(ImageSource.SELF, R.drawable.close_ico.toString()),
            applyPaddingHorizontalToIco = false, overrideIcoPaddings = Pair(searchInputIcoPadding, searchInputIcoPadding),
            imeOptionss = EditorInfo.IME_ACTION_SEARCH
        )
        val searchInputlp1 = searchInput.layoutParams as ConstraintLayout.LayoutParams
        searchInputlp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        searchInputlp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        searchInput.layoutParams = searchInputlp1
        searchInput.id = View.generateViewId()
        val searchInputt = searchInput.findTextInputEditText()
        val searchInputtClearIco = searchInput.findIco()
        val s = when (startsInfo) {
            is PageWithSearchInput.EditAnimeCardEpisodes -> startsInfo.alreadyEnteredSearchText ?: ""
            else -> ""
        }
        searchInputtClearIco?.visibility = if (s.isNotEmpty()) View.VISIBLE else View.GONE
        searchInputt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                when (startsInfo) {
                    is PageWithSearchInput.EditAnimeCardEpisodes -> {
                        startsInfo.alreadyEnteredSearchText = s.toString()
                    }
                    else -> {}
                }
                searchInputtClearIco?.visibility = if (s.toString().isNotEmpty()) View.VISIBLE else View.GONE
            }
        })
        searchInputtClearIco?.setOnClickListener {
            searchInputt?.setText("")
        }
        var extraButton: ImageView? = null
        if (addExtraButton) {
            extraButton = ImageView(context).apply {
                val lp1 = ConstraintLayout.LayoutParams(
                    hBtn,
                    hBtn
                )
                lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams = lp1
                setPadding(round(hBtn.toFloat() / 20f).toInt() + round(hBtn.toFloat() / 6f).toInt(), round(hBtn.toFloat() / 6f).toInt(),round(hBtn.toFloat() / 6f).toInt(),round(hBtn.toFloat() / 6f).toInt())
                background = createOutlinedbackground(SizeType.SMALL, hBtn, round(1f*baseDensity).toInt(), 0.5f)
                setImageResource(R.drawable.vert_dots)
                imageTintList = ColorStateList.valueOf("#D9D9D9".toColorInt())
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            searchInputConstraintLayout.addView(extraButton)
        }
        searchInputConstraintLayout.addView(searchInput)
        container.addView(searchInputConstraintLayout)

        val finalButton = ConstraintLayout(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                actualWidth - (marginLeft*2),
                hBtn
            )
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.setMargins(0,0,0,marginTop)
            layoutParams = lp1
            val color = when (finalButtonType) {
                0 -> "#4271db".toColorInt()
                1 -> "#805EFF56".toColorInt()
                else -> "#FFFFFF".toColorInt()
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(color)
                cornerRadius = getAdaptiveRadius((actualWidth-(marginLeft*2)), SizeType.SMALL)
            }
        }
        val finalButtonText = TextView(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = lp1
            includeFontPadding = false
            typeface = font
            setTextSize(TypedValue.COMPLEX_UNIT_PX, searchInputTextSizee)
            text = when (finalButtonType) {
                0 -> context.getString(R.string.Save)
                1 -> context.getString(R.string.Confirm)
                else -> ""
            }
            setTextColor("#FFFFFF".toColorInt())
            maxLines = 1
            maxWidth = actualWidth - (marginLeft*2)
            ellipsize = TextUtils.TruncateAt.END
        }
        finalButton.addView(finalButtonText)
        container.addView(finalButton)

        val contentContainerHeight = screenHeight - (marginTop*4) - hText.measuredHeight - (hBtn*2) - contentContainerMarginTop
        val contentContainer = ConstraintLayout(context).apply {
            val lp1 = ConstraintLayout.LayoutParams(
                actualWidth - (marginLeft*2),
                contentContainerHeight
            )
            lp1.topToBottom = searchInputConstraintLayout.id
            lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp1.setMargins(0,contentContainerMarginTop,0,0)
            layoutParams = lp1
        }

        container.addView(contentContainer)
        when (startsInfo) {
            is PageWithSearchInput.EditAnimeCardEpisodes -> {
                val width = actualWidth - (marginLeft*2)
                var episodesList = startsInfo.list
                val addEpisodeBgDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = getAdaptiveRadius(width, SizeType.SMALL)
                    setColor("#1B1B1B".toColorInt())
                    setStroke(round(1f*baseDensity).toInt(), "#9C9C9C".toColorInt())
                }
                val addEpisodesContainerBgDrawable = createOutlinedbackground(SizeType.SMALL, width, round(1f*baseDensity).toInt(), 0.5f)

                val addEpisodeButtonContainerWidth = width - marginTop
                val episodesBlockWidth = min(width, round(320f*baseDensity).toInt())
                val maxEpisodesBlockHeight = contentContainerHeight

                val maxEditEpisodesBlockHeight = maxEpisodesBlockHeight - hBtn - marginTop - round(11f * baseDensity).toInt()
                var editEpisodesBlockHeight = ((round(46f * baseDensity).toInt() + round(11f * baseDensity).toInt())*episodesList.size) -  if (episodesList.isNotEmpty()) round(11f * baseDensity).toInt() else 0
                if (editEpisodesBlockHeight > maxEditEpisodesBlockHeight) {
                    editEpisodesBlockHeight = maxEditEpisodesBlockHeight
                }
                val addEpisodesBlockContainer = ConstraintLayout(context).apply {
                    val layoutparams1 = ConstraintLayout.LayoutParams(
                        width,
                        (editEpisodesBlockHeight + marginTop + hBtn + round(11f * baseDensity).toInt())
                    )
                    layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutParams = layoutparams1
                    background = addEpisodesContainerBgDrawable
                    id = View.generateViewId()
                    tag = "add_episodes_block_container"
                    setOnClickListener {
                        requestFocus()
                    }
                }

                val addEpisodeButtonId = View.generateViewId()
                val addEpisodeButtonContainer = ConstraintLayout(context).apply {
                    val layoutparams1 = ConstraintLayout.LayoutParams(
                        addEpisodeButtonContainerWidth,
                        hBtn
                    )
                    layoutParams = layoutparams1
                    id = addEpisodeButtonId
                    background = addEpisodeBgDrawable
                    tag = "add_episode_button"
                }

                fun applyEpisodesListChanges(list: MutableList<episodeInfo>) {
                    episodesList = list
                    editEpisodesBlockHeight = ((round(46f * baseDensity).toInt() + round(11f * baseDensity).toInt())*episodesList.size) -  if (episodesList.isNotEmpty()) round(11f * baseDensity).toInt() else 0
                    if (editEpisodesBlockHeight > maxEditEpisodesBlockHeight) {
                        editEpisodesBlockHeight = maxEditEpisodesBlockHeight
                    }
                    val lp1 = addEpisodesBlockContainer.layoutParams as ConstraintLayout.LayoutParams
                    lp1.height = (editEpisodesBlockHeight + marginTop + hBtn + if (list.isNotEmpty()) round(11f * baseDensity).toInt() else 0)
                    addEpisodesBlockContainer.layoutParams = lp1
                    if (list.isEmpty()) {
                        val lp2 = addEpisodeButtonContainer.layoutParams as ConstraintLayout.LayoutParams
                        lp2.setMargins(0,0,0,0)
                        addEpisodeButtonContainer.layoutParams = lp2
                    }
                    else {
                        val lp2 = addEpisodeButtonContainer.layoutParams as ConstraintLayout.LayoutParams
                        lp2.setMargins(0,round(11f * baseDensity).toInt(),0,0)
                        addEpisodeButtonContainer.layoutParams = lp2
                    }
                }
                var currentPendingPositionToImageApply = -1
                val editEpisodes = createFlatGrid(context, CreateFlatGridInput.EditEpisodes(episodesList, callback = {applyEpisodesListChanges((it as MutableList<episodeInfo>))}), addEpisodeButtonContainerWidth, editEpisodesBlockHeight, changeImage = {
                    currentPendingPositionToImageApply = it
                    resultSenderViewModel.sendResult(ResultKeys.SELECT_FILE, SelectFileInput(fileType.IMAGE, ResultKeys.PAGE_WITH_SEARCH_EDIT_ANIME_CARD_EPISODES_CHANGE_EPISODE_IMAGE))
                })
                val editEpisodesContainer = when (editEpisodes) {
                    is CreateFlatGridOutput.EditEpisodes -> editEpisodes.container
                    is CreateFlatGridOutput.Basic -> editEpisodes.container
                }
                val editEpisodesAdapter: FlatGridOfEditEpisodesAdapter? = when (editEpisodes) {
                    is CreateFlatGridOutput.EditEpisodes -> editEpisodes.adapter
                    is CreateFlatGridOutput.Basic -> null
                }
                editEpisodesContainer.setOnClickListener {
                    editEpisodesContainer.requestFocus()
                }
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
                lppp1.setMargins(0,round(11f * baseDensity).toInt(),0,0)
                addEpisodeButtonContainer.layoutParams = lppp1
                val addEpisodeButtonIco = ImageView(context).apply {
                    val layoutparams1 = ConstraintLayout.LayoutParams(
                        hBtn,
                        hBtn
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

                addEpisodeButtonContainer.setOnClickListener {
                    resultSenderViewModel.sendResult(ResultKeys.SELECT_FILES, SelectFilesInput(listOf(fileType.VIDEO), ResultKeys.PAGE_WITH_SEARCH_EDIT_ANIME_CARD_EPISODES_ADD_EPISODES))
                }
                addEpisodeButtonContainer.addView(addEpisodeButtonIco)
                addEpisodesBlockContainer.addView(editEpisodesContainer)
                addEpisodesBlockContainer.addView(addEpisodeButtonContainer)
                applyEpisodesListChanges(episodesList)
                contentContainer.addView(addEpisodesBlockContainer)
                val job = context.lifecycleOwner?.lifecycleScope?.launch {
                    resultSenderViewModel.results.collect { (key, data) -> run {
                        when (key) {
                            ResultKeys.PAGE_WITH_SEARCH_EDIT_ANIME_CARD_EPISODES_ADD_EPISODES -> {
                                val dataa = data as? SelectFilesOutput
                                if (dataa != null) {
                                    for (i in dataa.data) {
                                        val episodeInfo = episodeInfo(null, "", LinkData(LinkType.CONTENT, null, i),getVideoDuration(i, context))
                                        editEpisodesAdapter?.addEpisode(episodeInfo)
                                    }
                                }
                            }
                            ResultKeys.PAGE_WITH_SEARCH_EDIT_ANIME_CARD_EPISODES_CHANGE_EPISODE_IMAGE -> {
                                val dataa = data as? SelectFileOutput
                                if (dataa != null) {
                                    val imageData = ImageData(ImageSource.DEVICE, dataa.data)
                                    editEpisodesAdapter?.changeEpisodeBanner(imageData, currentPendingPositionToImageApply)
                                }
                            }
                        }
                    }
                    }
                }
                if (layer is Layer.OverLay && job != null) {
                    layer.activeJobs.add(job)
                }
                finalButton.setOnClickListener {
                    resultSenderViewModel.sendResult(key, episodesList)
                    close()
                }
                fun smartSearch(s: String) {
                    resultSenderViewModel.sendResult(ResultKeys.PAGE_WITH_SEARCH_EDIT_ANIME_CARD_EPISODES_CHANGE_SEARCH_INPUT_TEXT, s)
                    val titlesList = episodesList.map { it.name }
                    val searchResult = searchInList(titlesList, s, 70).firstOrNull()
                    if (searchResult != null) {
                        val index = titlesList.indexOf(searchResult)
                        if (index != -1) {
                            if (editEpisodesAdapter != null) {
                                val firstVisibleItem = (editEpisodesRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                                val lastVisibleItem = (editEpisodesRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                                if (index !in firstVisibleItem..lastVisibleItem) {
                                    val scrollV = (index + (lastVisibleItem-firstVisibleItem)).coerceIn(0, editEpisodesAdapter.itemCount - 1)
                                    editEpisodesRecyclerView.smoothScrollToPosition(scrollV)
                                }
                            }
                        }
                    }
                }
                searchInputt?.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        smartSearch((s ?: "").toString())
                    }
                })
                smartSearch(startsInfo.alreadyEnteredSearchText ?: "")
            }
            else -> {}
        }

        return container
    }
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