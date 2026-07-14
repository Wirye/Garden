package com.example.garden.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.text.LineBreaker
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.garden.Layer
import com.example.garden.ui.adapters.objectDiffCallbacks.ObjectDiffCallback
import com.example.garden.ui.utils.PageFlingListener
import com.example.garden.R
import com.example.garden.baseDensity
import com.example.garden.ui.utils.calcItemPosInPxByPos
import com.example.garden.ui.utils.calcRecyclerViewHeight
import com.example.garden.ui.utils.calculateDigitParams
import com.example.garden.ui.utils.createDovodchikDots
import com.example.garden.ui.utils.createDovodchikDotsReturn
import com.example.garden.ui.utils.createGridOfChilds
import com.example.garden.dotDrawables
import com.example.garden.ui.utils.findMainPageLayerByPageId
import com.example.garden.layersList
import com.example.garden.objectData2
import com.example.garden.orientationNow
import com.example.garden.orientationOld
import com.example.garden.screenWidth
import com.example.garden.ui.customView.OptimizedTextView
import com.example.garden.ui.utils.spaceItemDecoration
import com.example.garden.ui.utils.spaceItemDecorationInput
import kotlin.collections.set
import kotlin.math.round

class CarouselsAdapter(private val context: Context, val addCardToCarousel: (Long) -> Unit, val clickOnItem: (objectData2) -> Unit, val previewMode: Boolean = false, val layersListForPreviewMode: List<Layer>? = null, val customLineWidth: Int? = null) : ListAdapter<objectData2, CarouselsAdapter.ViewHolder>(ObjectDiffCallback()) {

    class ViewHolder(val constraintLayout: ConstraintLayout)  : RecyclerView.ViewHolder(constraintLayout) {
        var totalScrolledRecyclerView = 0
        var totalScrolledHorizontalScrollView = 0
        var activeDotPosition = 0
        var nameTextView: TextView? = null
        var editButton: ImageButton? = null
        var addButton: ImageButton? = null
        var watchAllButton: Button? = null
        var recyclerView: RecyclerView? = null
        var constraintLayoutGrid: ConstraintLayout? = null
        var dotsLayout: ViewGroup? = null
    }

    var firstHolderHeightCallback: ((Int) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ConstraintLayout(context).apply {
            val layoutParams1 = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams1.setMargins(0,0,0,0)
            layoutParams = layoutParams1
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val lineWidth = customLineWidth ?: screenWidth
        holder.totalScrolledRecyclerView = 0
        holder.totalScrolledHorizontalScrollView = 0
        holder.activeDotPosition = 0
        val layer = if (!previewMode) {layersList[layersList.indexOf(findMainPageLayerByPageId(getItem(position).page))]} else {layersListForPreviewMode?.get(0)}
        if (((holder.constraintLayout.isEmpty()) ||
                    (holder.constraintLayout.isNotEmpty() && (getItem(position).layoutType == 1 || getItem(position).layoutType == null) && holder.recyclerView == null) ||
                    (holder.constraintLayout.isNotEmpty() && getItem(position).layoutType == 0 && holder.constraintLayoutGrid == null))
            && layer is Layer.MainPage) {
            if (holder.constraintLayout.isNotEmpty()) {
                holder.constraintLayout.removeAllViews()
                holder.nameTextView = null
                holder.editButton = null
                holder.addButton = null
                holder.watchAllButton = null
                holder.recyclerView = null
                holder.constraintLayoutGrid = null
                holder.dotsLayout = null
            }
            val layoutparams2 = holder.constraintLayout.layoutParams as RecyclerView.LayoutParams
            layoutparams2.setMargins(0,0,0,0)
            holder.constraintLayout.layoutParams = layoutparams2
            var idOfTextObj = 0
            var idOfEditButton = 0
            var idOfAddButton = 0
            val buttonsSize = round(25f*baseDensity).toInt()
            val watchButtonWidth = round(91f * baseDensity).toInt()
            val watchButtonHeight = (25f * baseDensity).toInt()
            val watchButtonMarginRight = (19f * baseDensity).toInt()
            var pdH = getItem(position).paddingHorizontal
            if (pdH == null) {
                pdH = round(19f * baseDensity).toInt()
            }
            val textViewWidth = if (!previewMode) {lineWidth} else {
                lineWidth
            } - pdH - buttonsSize*2 - watchButtonWidth - watchButtonMarginRight
            val textView = OptimizedTextView(context).apply {
                if (getItem(position).name != null &&  getItem(position).name!!.isNotEmpty()) {
                    text = getItem(position).name
                }
                else {
                    text = resources.getText(R.string.withoutName)
                }
                maxLines = 2
                breakStrategy = LineBreaker.BREAK_STRATEGY_HIGH_QUALITY
                includeFontPadding = false
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                maxWidth = textViewWidth
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.setMargins(pdH,0,0,0)
                val newId = View.generateViewId()
                id = newId
                idOfTextObj = newId
                setTextSize(TypedValue.COMPLEX_UNIT_PX, round(25f*baseDensity))
                setTextColor(Color.WHITE)
                layoutParams = layoutparams1
            }
            holder.constraintLayout.addView(textView)
            holder.nameTextView = textView
            val editButton = ImageButton(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    buttonsSize,
                    buttonsSize
                )
                layoutparams1.startToEnd = idOfTextObj
                layoutparams1.topToTop = idOfTextObj
                layoutparams1.bottomToBottom = idOfTextObj
                layoutparams1.setMargins(round(5f*baseDensity).toInt(),0,0,0)
                layoutParams = layoutparams1
                val newId = View.generateViewId()
                id = newId
                idOfEditButton = newId
                background = Color.TRANSPARENT.toDrawable()
                setImageResource(R.drawable.edit_ico)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            holder.constraintLayout.addView(editButton)
            holder.editButton = editButton
            val addButton = ImageButton(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    buttonsSize,
                    buttonsSize
                )
                layoutparams1.startToEnd = idOfEditButton
                layoutparams1.topToTop = idOfEditButton
                layoutparams1.bottomToBottom = idOfEditButton
                layoutParams = layoutparams1
                val newId = View.generateViewId()
                id = newId
                idOfAddButton = newId
                background = Color.TRANSPARENT.toDrawable()
                setImageResource(R.drawable.add_ico)
                scaleType = ImageView.ScaleType.CENTER_CROP
                setOnClickListener {
                    requestFocus()
                    addCardToCarousel(currentList[holder.position].id)
                }
            }
            holder.constraintLayout.addView(addButton)
            holder.addButton = addButton
            val watchAllButton = Button(context).apply {
                val layoutparams1 = ConstraintLayout.LayoutParams(
                    watchButtonWidth,
                    watchButtonHeight
                )
                layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.topToTop = idOfAddButton
                layoutparams1.setMargins(0, 0, watchButtonMarginRight, 0)
                layoutParams = layoutparams1
                text = "Смотреть все"
                setPadding(0,0,0,0)
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, round(10f * baseDensity))
                backgroundTintList = null
                stateListAnimator = null
                elevation = 0f
                setBackgroundResource(R.drawable.watch_all_button)
                focusable = View.FOCUSABLE
                isClickable = true
            }
            holder.constraintLayout.addView(watchAllButton)
            holder.watchAllButton = watchAllButton
            // Добавляем recycler view
            if (getItem(position).layoutType == null || getItem(position).layoutType == 1) {
                var recyclerViewId = 0
                val recycler = RecyclerView(context).apply {
                    val layoutParams1 = ConstraintLayout.LayoutParams(
                        lineWidth,
                        calcRecyclerViewHeight(currentList,position, lineWidth)
                    )
                    layoutParams1.topToBottom = idOfTextObj
                    layoutParams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutParams1.setMargins(0, round(6f * baseDensity).toInt(), 0, 0)
                    layoutParams = layoutParams1
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = ChildAdapter(context, getItem(position), clickOnCard = {
                        clickOnItem(it)
                    }, lineWidth)
                    clipToPadding = false
                    val newId = View.generateViewId()
                    id = newId
                    recyclerViewId = newId
                    addItemDecoration(spaceItemDecoration(spaceItemDecorationInput(
                        listOf(getItem(position).marginBetweenElementsHorizontal ?: round(11f * baseDensity).toInt(),0,0,0),
                        listOf(getItem(position).paddingHorizontal ?: round(19f * baseDensity).toInt(),0,0,0),
                        listOf(getItem(position).marginBetweenElementsHorizontal ?: round(11f * baseDensity).toInt(),0,getItem(position).paddingHorizontal ?: round(19f * baseDensity).toInt(),0)
                    )))
                }
                val ad = recycler.adapter as ChildAdapter
                ad.submitList(getItem(position).childs) {
                    recycler.invalidateItemDecorations()
                }
                holder.constraintLayout.addView(recycler)
                holder.recyclerView = recycler
                recycler.post {
                    recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            holder.totalScrolledRecyclerView += dx
                            layer.scrollPositionsInPx[getItem(position).position] = holder.totalScrolledRecyclerView
                            val layoutManager = recycler.layoutManager as LinearLayoutManager
                            val firstVisible = layoutManager.findFirstVisibleItemPosition()
                            layer.scrollPositionCarousels[getItem(position).position] = firstVisible
                        }
                    })
                    recycler.post {
                        if (!(getItem(position).dovodchik && getItem(position).showDovodchikDots)) {
                            var pdH = getItem(position).paddingHorizontal
                            if (pdH == null) {
                                pdH = round(19f * baseDensity).toInt()
                            }
                            layer.scrollPositionCarousels[getItem(position).position] = 0
                            val scrollH = calcItemPosInPxByPos(currentList,position, layer.scrollPositionCarousels[getItem(position).position]!!, context, lineWidth) - pdH
                            recycler.scrollBy(scrollH,0)
                        }
                    }
                }
                if (getItem(position).dovodchik && getItem(position).showDovodchikDots) {
                    var padding = round(19f * baseDensity).toInt()
                    if (getItem(position).paddingHorizontal != null) {
                        padding = getItem(position).paddingHorizontal!!
                    }
                    var marginBetweenElementsHorizontal = getItem(position).marginBetweenElementsHorizontal
                    if (marginBetweenElementsHorizontal == null) {
                        marginBetweenElementsHorizontal = round(11f * baseDensity).toInt()
                    }
                    val dots = createDovodchikDots(context, getItem(position).childs, padding, marginBetweenElementsHorizontal, currentList[position].childsShowName, currentList[position].childsNamePosition, lineWidth)
                    val dotsLayout = dots.layout as ViewGroup
                    val layoutparams1 = dotsLayout.layoutParams as ConstraintLayout.LayoutParams
                    layoutparams1.topToBottom = recyclerViewId
                    layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutparams1.setMargins(0,round(4f * baseDensity).toInt(),0,0)
                    dotsLayout.layoutParams = layoutparams1
                    dotsLayout.setPadding(0,0,0,0)
                    holder.constraintLayout.addView(dotsLayout)
                    holder.dotsLayout = dotsLayout
                    var horizontalScrollView: HorizontalScrollView = HorizontalScrollView(context).apply {
                        layoutParams = ConstraintLayout.LayoutParams(
                            0,
                            0
                        )
                    }
                    for (o in 0 until dotsLayout.childCount) {
                        if (dotsLayout.getChildAt(o) is HorizontalScrollView) {
                            horizontalScrollView = dotsLayout.getChildAt(o) as HorizontalScrollView
                        }
                    }

                    horizontalScrollView.setOnScrollChangeListener { _, scrollX, _, oldScrollX, _ ->
                        holder.totalScrolledHorizontalScrollView = scrollX
                    }

                    val dotsContainer = horizontalScrollView.getChildAt(0) as ViewGroup
                    for (i in 0 until dots.list.size) {
                        val index = i
                        if (i < dotsContainer.childCount) {
                            dotsContainer.getChildAt(index).setOnClickListener {
                                dotsContainer.requestFocus()
                                updateActiveDot(index, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, recycler, horizontalScrollView, position, layer)
                                holder.activeDotPosition = index
                                layer.activeDotPositionCarousels[getItem(position).position] = index
                            }
                        }
                    }
                    var scrolled = holder.totalScrolledHorizontalScrollView
                    recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            scrolled += dx
                            var fdPos = 0
                            // Между какими он точками
                            for (i in 0 until dots.list.size) {
                                if (i+1 < dots.list.size) {
                                    val srez = dots.list[i].second.itemPositionInPx..dots.list[i+1].second.itemPositionInPx
                                    if (scrolled in srez) {
                                        val dot1 = dotsContainer.getChildAt(i) as ImageView
                                        val dot2 = dotsContainer.getChildAt(i+1) as ImageView
                                        val procFor2Dot = round((scrolled - srez.first).toFloat() / ((srez.last - srez.first).toFloat() / 100f)).toInt()
                                        val procFor1Dot = 100 - procFor2Dot
                                        dot1.setImageDrawable(dotDrawables[procFor1Dot])
                                        dot2.setImageDrawable(dotDrawables[procFor2Dot])
                                        fdPos = i
                                        break
                                    }
                                }
                            }
                            for (i in 0 until dots.list.size) {
                                if (i != fdPos && i != fdPos+1) {
                                    val dot = dotsContainer.getChildAt(i) as ImageView
                                    dot.setImageDrawable(dotDrawables[0])
                                }
                            }
                        }
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)

                            when (newState) {
                                RecyclerView.SCROLL_STATE_IDLE -> {
                                    // Пользователь отпустил и прокрутка остановилась
                                    val currentScroll = holder.totalScrolledRecyclerView

                                    // Находим ближайшую точку
                                    val targetDot = dots.list.minByOrNull {
                                        Math.abs(it.second.itemPositionInPx - currentScroll)
                                    }

                                    targetDot?.let {
                                        val targetIndex = dots.list.indexOf(it)
                                        val targetScroll = it.second.itemPositionInPx
                                        val distance = targetScroll - currentScroll

                                        // Если не на месте - доводим
                                        if (Math.abs(distance) > 0) {
                                            recyclerView.smoothScrollBy(distance, 0)
                                            updateActiveDot(targetIndex, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, recycler, horizontalScrollView, position, layer)
                                            holder.activeDotPosition = targetIndex
                                            layer.activeDotPositionCarousels[getItem(position).position] = targetIndex
                                        }
                                    }
                                }
                            }
                        }
                    })
                    recycler.setOnFlingListener(PageFlingListener(
                        dotsList = dots.list,
                        getCurrentScroll = { holder.totalScrolledRecyclerView },
                        updatePage = { dotIndex ->
                            updateActiveDot(dotIndex, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, recycler, horizontalScrollView, position, layer)
                            holder.activeDotPosition = dotIndex
                            layer.activeDotPositionCarousels[getItem(position).position] = dotIndex
                        },
                        recyclerView = recycler
                    ))
                    recycler.post {
                        dotsLayout.post {
                            if (layer.activeDotPositionCarousels[getItem(position).position] == null) {
                                layer.activeDotPositionCarousels[getItem(position).position] = 0
                            }
                            updateActiveDot(layer.activeDotPositionCarousels[getItem(position).position]!!, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView,
                                recycler, horizontalScrollView, position, layer, true, if (orientationOld != orientationNow) {true} else {false})
                            holder.activeDotPosition = layer.activeDotPositionCarousels[getItem(position).position]!!
                            orientationOld = orientationNow
                        }
                    }
                }
            }
            // Добавляем grid
            else {
                var maxObjectsInOneLine: Int?
                if (getItem(position).maxObjectsInOneLine == null) {
                    maxObjectsInOneLine = null
                }
                else {
                    maxObjectsInOneLine = getItem(position).maxObjectsInOneLine as Int
                }
                var paddingHorizontal = getItem(position).paddingHorizontal
                if (paddingHorizontal == null) {
                    paddingHorizontal = round(19f * baseDensity).toInt()
                }
                val constraintLayout = createGridOfChilds(getItem(position).childs,  maxObjectsInOneLine, context, (lineWidth-paddingHorizontal*2), getItem(position))
                val layoutparams1 = constraintLayout.layoutParams as ConstraintLayout.LayoutParams
                layoutparams1.topToBottom = idOfTextObj
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.setMargins(paddingHorizontal, round(6f * baseDensity).toInt(),paddingHorizontal,0)
                constraintLayout.layoutParams = layoutparams1
                holder.constraintLayout.addView(constraintLayout)
                holder.constraintLayoutGrid = constraintLayout
                constraintLayout.setOnClickListener {
                    constraintLayout.requestFocus()
                }
                for (i in 0 until constraintLayout.childCount) {
                    val obj = constraintLayout.getChildAt(i)
                    val objData = getItem(position).childs[i]
                    obj.setOnClickListener {
                        obj.requestFocus()
                        clickOnItem(objData)
                    }
                }
            }
        }
        else if (layer is Layer.MainPage) {
            if (holder.nameTextView != null) {
                holder.nameTextView!!.apply {
                    if (getItem(position).name != null && getItem(position).name!!.isNotEmpty()) {
                        text = getItem(position).name
                    }
                    else {
                        text = resources.getText(R.string.withoutName)
                    }
                }
            }
            if (holder.addButton != null) {
                holder.addButton!!.setOnClickListener(null)
                holder.addButton!!.setOnClickListener {
                    holder.addButton!!.requestFocus()
                    addCardToCarousel(currentList[holder.position].id)
                }
            }
            if (holder.editButton != null) {
                holder.editButton!!.setOnClickListener(null)
                holder.editButton!!.setOnClickListener {
                    holder.editButton!!.requestFocus()
//                    editCarousel(position, currentList)
                }
            }
            if (holder.watchAllButton != null) {
                holder.watchAllButton!!.setOnClickListener(null)
                holder.watchAllButton!!.setOnClickListener {
                    holder.watchAllButton!!.requestFocus()
//                    watchAll(position, currentList)
                }
            }
            if (holder.recyclerView != null && (getItem(position).layoutType == null || getItem(position).layoutType == 1)) {
                val recyclerViewlp1 = holder.recyclerView!!.layoutParams as ConstraintLayout.LayoutParams
                recyclerViewlp1.height = calcRecyclerViewHeight(currentList,position, lineWidth)
                holder.recyclerView!!.layoutParams = recyclerViewlp1
                val ad = holder.recyclerView!!.adapter as ChildAdapter
                ad.submitList(getItem(position).childs)
                ad.updateParent(getItem(position))
                holder.recyclerView!!.clearOnScrollListeners()
                holder.recyclerView!!.onFlingListener = null
                holder.recyclerView!!.scrollTo(0,0)
                holder.recyclerView!!.scrollBy(-1000000000,0)
                holder.recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        holder.totalScrolledRecyclerView += dx
                        layer.scrollPositionsInPx[getItem(position).position] = holder.totalScrolledRecyclerView
                        val layoutManager = holder.recyclerView!!.layoutManager as LinearLayoutManager
                        val firstVisible = layoutManager.findFirstVisibleItemPosition()
                        layer.scrollPositionCarousels[getItem(position).position] = firstVisible
                    }
                })
                holder.recyclerView!!.post {
                    if (!(getItem(position).dovodchik && getItem(position).showDovodchikDots)) {
                        if (layer.scrollPositionsInPx[getItem(position).position] != null) {
                            holder.recyclerView!!.scrollBy(layer.scrollPositionsInPx[getItem(position).position]!!,0)
                        }
                        else {
                            holder.recyclerView!!.scrollBy(0,0)
                        }
                    }
                }
            }
            else if (holder.recyclerView != null) {
                holder.constraintLayout.removeView(holder.recyclerView)
            }
            if (holder.dotsLayout != null && holder.recyclerView != null && getItem(position).dovodchik && getItem(position).showDovodchikDots) {
                holder.constraintLayout.removeView(holder.dotsLayout)
                holder.dotsLayout = null
                if (getItem(position).dovodchik && getItem(position).showDovodchikDots) {
                    var padding = round(19f * baseDensity).toInt()
                    if (getItem(position).paddingHorizontal != null) {
                        padding = getItem(position).paddingHorizontal!!
                    }
                    var marginBetweenElementsHorizontal = getItem(position).marginBetweenElementsHorizontal
                    if (marginBetweenElementsHorizontal == null) {
                        marginBetweenElementsHorizontal = round(11f * baseDensity).toInt()
                    }
                    val dots = createDovodchikDots(context, getItem(position).childs, padding, marginBetweenElementsHorizontal, currentList[position].childsShowName, currentList[position].childsNamePosition, lineWidth)
                    val dotsLayout = dots.layout as ViewGroup
                    val layoutparams1 = dotsLayout.layoutParams as ConstraintLayout.LayoutParams
                    layoutparams1.topToBottom = holder.recyclerView!!.id
                    layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutparams1.setMargins(0,round(4f * baseDensity).toInt(),0,0)
                    dotsLayout.layoutParams = layoutparams1
                    dotsLayout.setPadding(0,0,0,0)
                    holder.constraintLayout.addView(dotsLayout)
                    holder.dotsLayout = dotsLayout
                    var horizontalScrollView: HorizontalScrollView = HorizontalScrollView(context).apply {
                        layoutParams = ConstraintLayout.LayoutParams(
                            0,
                            0
                        )
                    }
                    for (o in 0 until dotsLayout.childCount) {
                        if (dotsLayout.getChildAt(o) is HorizontalScrollView) {
                            horizontalScrollView = dotsLayout.getChildAt(o) as HorizontalScrollView
                        }
                    }

                    horizontalScrollView.setOnScrollChangeListener { _, scrollX, _, oldScrollX, _ ->
                        holder.totalScrolledHorizontalScrollView = scrollX
                    }

                    val dotsContainer = horizontalScrollView.getChildAt(0) as ViewGroup
                    for (i in 0 until dots.list.size) {
                        val index = i
                        if (i < dotsContainer.childCount) {
                            dotsContainer.getChildAt(index).setOnClickListener {
                                dotsContainer.requestFocus()
                                updateActiveDot(index, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, holder.recyclerView!!, horizontalScrollView, position, layer)
                                holder.activeDotPosition = index
                                layer.activeDotPositionCarousels[getItem(position).position] = index
                            }
                        }
                    }
                    var scrolled = holder.totalScrolledHorizontalScrollView
                    holder.recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            scrolled += dx
                            var fdPos = 0
                            // Между какими он точками
                            for (i in 0 until dots.list.size) {
                                if (i+1 < dots.list.size) {
                                    val srez = dots.list[i].second.itemPositionInPx..dots.list[i+1].second.itemPositionInPx
                                    if (scrolled in srez) {
                                        val dot1 = dotsContainer.getChildAt(i) as ImageView
                                        val dot2 = dotsContainer.getChildAt(i+1) as ImageView
                                        val procFor2Dot = round((scrolled - srez.first).toFloat() / ((srez.last - srez.first).toFloat() / 100f)).toInt()
                                        val procFor1Dot = 100 - procFor2Dot
                                        dot1.setImageDrawable(dotDrawables[procFor1Dot])
                                        dot2.setImageDrawable(dotDrawables[procFor2Dot])
                                        fdPos = i
                                        break
                                    }
                                }
                            }
                            for (i in 0 until dots.list.size) {
                                if (i != fdPos && i != fdPos+1) {
                                    val dot = dotsContainer.getChildAt(i) as ImageView
                                    dot.setImageDrawable(dotDrawables[0])
                                }
                            }
                        }
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)

                            when (newState) {
                                RecyclerView.SCROLL_STATE_IDLE -> {
                                    val currentScroll = holder.totalScrolledRecyclerView
                                    val targetDot = dots.list.minByOrNull {
                                        Math.abs(it.second.itemPositionInPx - currentScroll)
                                    }

                                    targetDot?.let {
                                        val targetIndex = dots.list.indexOf(it)
                                        val targetScroll = it.second.itemPositionInPx
                                        val distance = targetScroll - currentScroll
                                        if (Math.abs(distance) > 0) {
                                            recyclerView.smoothScrollBy(distance, 0)
                                            updateActiveDot(targetIndex, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, holder.recyclerView!!, horizontalScrollView, position, layer)
                                            holder.activeDotPosition = targetIndex
                                            layer.activeDotPositionCarousels[getItem(position).position] = targetIndex
                                        }
                                    }
                                }
                            }
                        }
                    })
                    holder.recyclerView!!.setOnFlingListener(PageFlingListener(
                        dotsList = dots.list,
                        getCurrentScroll = { holder.totalScrolledRecyclerView },
                        updatePage = { dotIndex ->
                            updateActiveDot(dotIndex, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, holder.recyclerView!!, horizontalScrollView, position, layer)
                            holder.activeDotPosition = dotIndex
                            layer.activeDotPositionCarousels[getItem(position).position] = dotIndex
                        },
                        recyclerView = holder.recyclerView!!
                    ))
                    holder.recyclerView!!.post {
                        dotsLayout.post {
                            if (layer.activeDotPositionCarousels[getItem(position).position] == null) {
                                layer.activeDotPositionCarousels[getItem(position).position] = 0
                            }
                            updateActiveDot(layer.activeDotPositionCarousels[getItem(position).position]!!, holder.activeDotPosition, dots, holder.totalScrolledRecyclerView, holder.totalScrolledHorizontalScrollView, dots.numTextView, holder.recyclerView!!, horizontalScrollView, position, layer, true, if (orientationOld != orientationNow) {true} else {false})
                            holder.activeDotPosition = layer.activeDotPositionCarousels[getItem(position).position]!!
                            orientationOld = orientationNow
                        }
                    }
                }
            }
            else if (holder.dotsLayout != null) {
                holder.constraintLayout.removeView(holder.dotsLayout)
            }
            if (holder.constraintLayoutGrid != null && getItem(position).layoutType != null && getItem(position).layoutType != 1) {
                holder.constraintLayout.removeView(holder.constraintLayoutGrid)
                holder.constraintLayoutGrid = null
                var maxObjectsInOneLine: Int?
                if (getItem(position).maxObjectsInOneLine == null) {
                    maxObjectsInOneLine = null
                }
                else {
                    maxObjectsInOneLine = getItem(position).maxObjectsInOneLine as Int
                }
                var paddingHorizontal = getItem(position).paddingHorizontal
                if (paddingHorizontal == null) {
                    paddingHorizontal = round(19f * baseDensity).toInt()
                }
                val constraintLayout = createGridOfChilds(getItem(position).childs,  maxObjectsInOneLine, context, (lineWidth-paddingHorizontal*2), getItem(position))
                val layoutparams1 = constraintLayout.layoutParams as ConstraintLayout.LayoutParams
                layoutparams1.topToBottom = holder.nameTextView!!.id
                layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutparams1.setMargins(paddingHorizontal, round(6f * baseDensity).toInt(),paddingHorizontal,0)
                constraintLayout.layoutParams = layoutparams1
                holder.constraintLayout.addView(constraintLayout)
                holder.constraintLayoutGrid = constraintLayout
                constraintLayout.setOnClickListener {
                    constraintLayout.requestFocus()
                }
                for (i in 0 until constraintLayout.childCount) {
                    val obj = constraintLayout.getChildAt(i)
                    val objData = getItem(position).childs[i]
                    obj.setOnClickListener {
                        obj.requestFocus()
                        clickOnItem(objData)
                    }
                }
            }
            else if (holder.constraintLayoutGrid != null) {
                holder.constraintLayout.removeView(holder.constraintLayoutGrid)
            }
        }
        holder.constraintLayout.post {
            if (position == 0) {
                holder.constraintLayout.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                if (firstHolderHeightCallback != null) {
                    firstHolderHeightCallback!!(holder.constraintLayout.measuredHeight)
                }
            }
        }
    }
    var lastRecyclerViewPositionOnOrientationChanged: Pair<Int,Int>? = null
    fun updateActiveDot(newActiveDotPosition: Int, oldActiveDotPosition: Int, dotsAll: createDovodchikDotsReturn, totalScrolledRecyclerView: Int, totalScrolledHorizontalScrollView: Int, numberTextView: TextView, recycler: RecyclerView, horizontalScrollView: HorizontalScrollView, position: Int, layer: Layer.MainPage, force: Boolean = false, orientationChanged: Boolean = false) {
        val dots = dotsAll.list
        var recyclerScrollWidth: Int
        var number: Int
        var dotScrollWidth: Int
        var k = false
        if (lastRecyclerViewPositionOnOrientationChanged != null) {
            if (position > lastRecyclerViewPositionOnOrientationChanged!!.first && orientationNow == lastRecyclerViewPositionOnOrientationChanged!!.second) {
                k = true
            }
        }
        if (newActiveDotPosition < dots.size && !orientationChanged && !k) {
            recyclerScrollWidth = dots[newActiveDotPosition].second.itemPositionInPx - totalScrolledRecyclerView
            number = dots[newActiveDotPosition].first.number
            dotScrollWidth = dots[newActiveDotPosition].first.targetWidth - totalScrolledHorizontalScrollView
        }
        else {
            if (layer.scrollPositionCarousels[getItem(position).position] == null) {
                layer.scrollPositionCarousels[getItem(position).position] = 0
            }
            val itemPositionInPx = calcItemPosInPxByPos(currentList,position, layer.scrollPositionCarousels[getItem(position).position]!!,context, (customLineWidth ?: screenWidth))
            var dotPos = -1
            for (i in 0 until dots.size) {
                if (dots[i].second.itemPositionInPx <= itemPositionInPx && i > dotPos) {
                    dotPos = i
                }
                else if (dots[i].second.itemPositionInPx > itemPositionInPx) {
                    break
                }
            }
            recyclerScrollWidth = dots[dotPos].second.itemPositionInPx - totalScrolledRecyclerView
            number = dots[dotPos].first.number
            dotScrollWidth = dots[dotPos].first.targetWidth - totalScrolledHorizontalScrollView
            lastRecyclerViewPositionOnOrientationChanged = Pair(position, orientationNow)
        }
        numberTextView.text = number.toString()
        val paddings = calculateDigitParams(numberTextView.height, number.toString()[0], context)
        numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, paddings.first)
        numberTextView.setPadding(paddings.second, paddings.third, 0,0)
        if (force) {
            horizontalScrollView.scrollBy(dotScrollWidth,0)
            recycler.scrollBy(recyclerScrollWidth,0)
        }
        else {
            horizontalScrollView.smoothScrollBy(dotScrollWidth,0)
            recycler.smoothScrollBy(recyclerScrollWidth,0)
        }
    }
}
