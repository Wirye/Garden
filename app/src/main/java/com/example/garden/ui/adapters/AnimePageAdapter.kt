package com.example.garden.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.View.LAYER_TYPE_SOFTWARE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.ImageRequest
import com.example.garden.ui.factories.BottomSheetDialogElement
import com.example.garden.BsdButtonsTags
import com.example.garden.Layer
import com.example.garden.R
import com.example.garden._bsdFlow
import com.example.garden.baseBlurEffectForBloobs
import com.example.garden.baseDensity
import com.example.garden.blob12FullSize
import com.example.garden.blob1MarginEnd
import com.example.garden.blob1MarginTop
import com.example.garden.blob2MarginStart
import com.example.garden.blob2MarginTop
import com.example.garden.blob3FullSize
import com.example.garden.blob3MarginEnd
import com.example.garden.blob3MarginTop
import com.example.garden.ui.utils.convertToStringTime
import com.example.garden.ui.utils.createGridOfGenres
import com.example.garden.database.Genre
import com.example.garden.database.ImageSource
import com.example.garden.database.LinkType
import com.example.garden.database.SizeType
import com.example.garden.delitRad
import com.example.garden.density
import com.example.garden.ui.utils.findLayerByLayerObjectId
import com.example.garden.ui.utils.getAdaptiveRadius
import com.example.garden.ui.utils.colors.getDeepDarkColor
import com.example.garden.layersList
import com.example.garden.ui.utils.viewExtensions.loadImage
import com.example.garden.objectData2
import com.example.garden.ui.utils.optimizeText
import com.example.garden.screenHeight
import com.example.garden.screenWidth
import com.example.garden.ui.utils.segmentedButtonOptions
import com.example.garden.statusBarHeight
import com.example.garden.steps
import com.example.garden.ui.adapters.objectDiffCallbacks.ObjectDiffCallback
import java.io.File
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round

class AnimePageAdapter(private val context: Context, private val showShowAllText: (text: String, size: Float, callback: (Boolean) -> Unit) -> Unit, private val sezonsAdapter: AnimePageSezonsPageAdapter, private val openVideo: (Long) -> Unit): ListAdapter<objectData2, AnimePageAdapter.ViewHolder>(ObjectDiffCallback()) {
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
