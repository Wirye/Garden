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
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import android.widget.ScrollView
import android.widget.TextView
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
import com.example.garden.R
import com.example.garden.ResultKeys
import com.example.garden.baseDensity
import com.example.garden.ui.utils.drawables.blobInit
import com.example.garden.database.ElementType
import com.example.garden.database.Genre
import com.example.garden.database.ImageData
import com.example.garden.database.SizeType
import com.example.garden.density
import com.example.garden.episodeInfo
import com.example.garden.genreColors
import com.example.garden.genreNames
import com.example.garden.icoSizesRatio
import com.example.garden.ui.utils.viewExtensions.lifecycleOwner
import com.example.garden.listDot
import com.example.garden.listDot2
import com.example.garden.ui.utils.viewExtensions.loadImage
import com.example.garden.objectData2
import com.example.garden.screenHeight
import com.example.garden.screenWidth
import com.example.garden.statusBarHeight
import com.example.garden.steps
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
data class createDovodchikDotsReturn(
    val layout: ConstraintLayout,
    val list: List<Pair<listDot2, listDot>>,
    val numTextView: TextView,
)
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

data class createM3ButtonReturn(
    val container: ConstraintLayout,
    val childs: List<View>,
    val width: Int
)
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

data class segmentedButtonOptions(
    var text: String,
    var icoId: Int?,
    var isActive: Boolean,
)
fun createSegmentedButton(context: Context, width: Int, height: Int, options: List<segmentedButtonOptions>): ConstraintLayout {
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
    val textInputTextSizee = round(getTextSizeByHeight(heightt, font) / 1.25f)
    val textInputViewWidth = tempTextView.measuredWidth + (3*ceil(textInputTextSizee).toInt())
    val textInput = createOutlinedTextFieldForOvDialog(context, textInputViewWidth, SizeType.SMALL, heightt, "", Gravity.CENTER, textInputTextSizee, 1, "%.2f".format(alreadyValue).trim().replace(",","."), (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL), round(textInputViewWidth.toFloat() / 4f).toInt(), true)

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
    }
    return container
}
fun createSliderRow(context: Context, width: Int, name: String, icoId: Int, stopsList: List<Pair<Float, String>>, heightt: Int, createSteps: Boolean = false, alreadyValue: Float, createTextInputView: Boolean = false): List<View> {
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
fun createSegmentedButtonRow(context: Context, width: Int, height: Int, options: List<segmentedButtonOptions>, icoId: Int, textt: String): ConstraintLayout {
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
fun createOutlinedTextFieldForOvDialog(context: Context, width: Int, radius: SizeType, heightt: Int, hintText: String, gravityy: Int = Gravity.CENTER, textSizee: Float, maxLiness: Int? = null, alreadyEnteredText: String? = null, inputTypee: Int? = null, paddingHorizontal: Int? = null, twoSidePadding: Boolean = false): TextInputLayout {
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

        if (inputTypee != null) {
            inputType = inputTypee
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
        setPadding(paddingHorizontal ?: textSizee.toInt(),if (gravityy == Gravity.TOP) textSizee.toInt() else 0,if (twoSidePadding && (paddingHorizontal != null)) {paddingHorizontal} else {0},0)
        includeFontPadding = false
        setText(alreadyEnteredText)
        tag = "edit_text"
    }

    inputLayout.addView(editText)
    return inputLayout
}

data class createGridOfGenresReturn(
    val container: ConstraintLayout,
    val sumHeight: Int,
    val sumWidth: Int
)
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
            val adapterr = FlatGridOfEditEpisodesAdapter(context, list, widthh, elementHeight, changes = {list = it
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