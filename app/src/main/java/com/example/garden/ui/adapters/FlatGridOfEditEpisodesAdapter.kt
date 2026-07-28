package com.example.garden.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.Layout
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.garden.R
import com.example.garden.baseDensity
import com.example.garden.ui.utils.convertToStringTime
import com.example.garden.database.ImageData
import com.example.garden.database.SizeType
import com.example.garden.episodeInfo
import com.example.garden.ui.utils.ChapterInfo
import com.example.garden.ui.utils.drawables.createOutlinedbackground
import com.example.garden.ui.utils.getAdaptiveRadius
import com.example.garden.ui.utils.getTextSizeByHeight
import com.example.garden.ui.utils.system.hideKeyboardd
import com.example.garden.ui.utils.viewExtensions.loadImage
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.round

private enum class AdapterType {
    EditEpisodes, EditChapters, EditChaptersPages
}

private data class createAdapterUiReturn(
    val container: ConstraintLayout,
    val imageAddIco: ImageView,
    val imageView: ImageView,
    val cardView: CardView,
    val imageContainer: ConstraintLayout,
    val hT: TextInputLayout,
    val hTI: TextInputEditText,
    val lengthTextView: TextView,
    val dragicoView: ImageView,
    val deleateIcoView: ImageView,
    val addBgDrawable: GradientDrawable,
    val editIcoView: ImageView,
    val numTextView: TextView,
)

private fun createAdapterUi(context: Context, adapterType: AdapterType, itemHeightt: Int, widthh: Int): createAdapterUiReturn {
    val boldFont = context.resources.getFont(R.font.google_sans_bold)
    val hTextHeight = round(itemHeightt.toFloat() / 2.55f).toInt()
    val textHeight = round(itemHeightt.toFloat() / 3.8f).toInt()
    val hTextSizee = getTextSizeByHeight(hTextHeight, boldFont, context = context)
    val textSizee = getTextSizeByHeight(textHeight, boldFont, context = context)
    val hTextColor = "#FFFFFF".toColorInt()
    val textColor = "#BFAFAFAF".toColorInt()
    val icoSize = round(itemHeightt.toFloat() / 1.5f).toInt()
    val margin = round(8f * baseDensity).toInt()
    val deleateIcoSize = round(icoSize.toFloat() / 1.6f).toInt()
    val container = ConstraintLayout(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            widthh,
            itemHeightt
        ).apply {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        }
        layoutParams = layoutparams1
    }
    val addBgDrawable = createOutlinedbackground(SizeType.SMALL, itemHeightt, round(1f*baseDensity).toInt(), 0.5f)
    val imageAddIco = ImageView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            itemHeightt,
            itemHeightt
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams = layoutparams1
        setImageResource(R.drawable.add_ico)
        imageTintList = ColorStateList.valueOf("#B0B0B0".toColorInt())
        scaleType = ImageView.ScaleType.CENTER_CROP
        elevation = 100f
    }
    val imageView = ImageView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            itemHeightt,
            itemHeightt
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        id = View.generateViewId()
        layoutParams = layoutparams1
        scaleType = ImageView.ScaleType.CENTER_CROP
    }
    val cardView = CardView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            itemHeightt,
            itemHeightt
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        id = View.generateViewId()
        layoutParams = layoutparams1
        radius = getAdaptiveRadius(itemHeightt, SizeType.SMALL)
    }
    cardView.addView(imageView)
    val imageContainer = ConstraintLayout(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            itemHeightt,
            itemHeightt
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        id = View.generateViewId()
        layoutParams = layoutparams1
    }
    imageContainer.addView(imageAddIco)
    imageContainer.addView(cardView)
    when (adapterType) {
        AdapterType.EditEpisodes -> {container.addView(imageContainer)}
        AdapterType.EditChapters -> {}
        AdapterType.EditChaptersPages -> {}
    }
    val numTextView = TextView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams = layoutparams1
        text = "TEST"
        setTextColor(hTextColor)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, hTextSizee)
        visibility = when (adapterType) {
            AdapterType.EditEpisodes -> View.GONE
            AdapterType.EditChapters -> View.VISIBLE
            AdapterType.EditChaptersPages -> View.VISIBLE
        }
        id = View.generateViewId()
        measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
    }
    container.addView(numTextView)
    val hT = TextInputLayout(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        setPadding(0,0,0,0)
        when(adapterType) {
            AdapterType.EditEpisodes -> layoutparams1.startToEnd = imageContainer.id
            AdapterType.EditChapters -> layoutparams1.startToEnd = numTextView.id
            AdapterType.EditChaptersPages -> layoutparams1.startToEnd = numTextView.id
        }
        layoutparams1.topToTop = when(adapterType) {
            AdapterType.EditEpisodes -> imageContainer.id
            AdapterType.EditChapters -> ConstraintLayout.LayoutParams.PARENT_ID
            AdapterType.EditChaptersPages -> ConstraintLayout.LayoutParams.PARENT_ID
        }
        isHintEnabled = false
        boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_NONE
        minimumHeight = 0
        layoutParams = layoutparams1
        id = View.generateViewId()
        maxWidth = widthh - icoSize - when(adapterType) {
            AdapterType.EditEpisodes -> itemHeightt
            AdapterType.EditChapters -> icoSize
            AdapterType.EditChaptersPages -> 0
        } - margin*3 - deleateIcoSize - when(adapterType) {
            AdapterType.EditEpisodes -> 0
            AdapterType.EditChapters -> numTextView.measuredWidth
            AdapterType.EditChaptersPages -> numTextView.measuredWidth
        }
    }
    val hTI = TextInputEditText(hT.context).apply {
        layoutParams = LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        setPadding(0,0,0,0)
        setTextColor(hTextColor)
        setTextSize(TypedValue.COMPLEX_UNIT_PX,hTextSizee)
        ellipsize = TextUtils.TruncateAt.END
        setHintTextColor(hTextColor)
        hint = "Без названия"
        setText("TEST")
        maxWidth = widthh - icoSize - itemHeightt - margin*3 - deleateIcoSize
        maxLines = 1
        isSingleLine = true
        imeOptions += EditorInfo.IME_ACTION_DONE or EditorInfo.IME_FLAG_NO_EXTRACT_UI
        setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                view.clearFocus()
                hideKeyboardd(this)
                true
            } else {
                false
            }
        }
    }
    hT.addView(hTI)
    hT.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    val lengthTextView = TextView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            when (adapterType) {
                AdapterType.EditEpisodes -> ConstraintLayout.LayoutParams.WRAP_CONTENT
                AdapterType.EditChapters -> 0
                AdapterType.EditChaptersPages -> 0
            }
        )
        layoutparams1.startToStart = hT.id
        layoutparams1.topToBottom = hT.id
        layoutParams = layoutparams1
        ellipsize = TextUtils.TruncateAt.END
        text = "TEST"
        setTextColor(textColor)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
        visibility = when (adapterType) {
            AdapterType.EditEpisodes -> View.VISIBLE
            AdapterType.EditChapters -> View.GONE
            AdapterType.EditChaptersPages -> View.GONE
        }
        measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        maxWidth = widthh - icoSize - itemHeightt - margin*2
        hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NORMAL
    }
    val sumTextsHeight = hT.measuredHeight + when (adapterType) {
        AdapterType.EditEpisodes -> lengthTextView.measuredHeight
        AdapterType.EditChapters -> 0
        AdapterType.EditChaptersPages -> 0
    }
    val textsMarginTop = when(adapterType) {
        AdapterType.EditEpisodes -> round((itemHeightt - sumTextsHeight).toFloat() / 6f).toInt() * 5
        else -> round((itemHeightt - sumTextsHeight).toFloat() / 2f).toInt()
    }
    val lp1 = hT.layoutParams as ConstraintLayout.LayoutParams
    lp1.setMargins(margin, textsMarginTop,0,0)
    hT.layoutParams = lp1
    val numTextViewLp1 = numTextView.layoutParams as ConstraintLayout.LayoutParams
    numTextViewLp1.setMargins(0,textsMarginTop,0,0)
    numTextView.layoutParams = numTextViewLp1
    when (adapterType) {
        AdapterType.EditEpisodes -> container.addView(hT)
        AdapterType.EditChapters -> container.addView(hT)
        AdapterType.EditChaptersPages -> {}
    }
    when (adapterType) {
        AdapterType.EditEpisodes -> container.addView(lengthTextView)
        AdapterType.EditChapters -> {}
        AdapterType.EditChaptersPages -> {}
    }
    val dragicoView = ImageView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            icoSize,
            icoSize
        )
        layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams = layoutparams1
        setImageResource(R.drawable.drag_handle_ico)
        imageTintList = ColorStateList.valueOf("#D9D9D9".toColorInt())
        scaleType = ImageView.ScaleType.CENTER_CROP
        id = View.generateViewId()
    }
    container.addView(dragicoView)

    val deleateIcoView = ImageView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            deleateIcoSize,
            deleateIcoSize
        )
        layoutparams1.endToStart = dragicoView.id
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.setMargins(0,0,margin,0)
        layoutParams = layoutparams1
        setImageResource(R.drawable.delete_ico)
        imageTintList = ColorStateList.valueOf("#852221".toColorInt())
        scaleType = ImageView.ScaleType.CENTER_CROP
        id = View.generateViewId()
    }
    container.addView(deleateIcoView)

    val editIcoView = ImageView(context).apply {
        val layoutparams1 = ConstraintLayout.LayoutParams(
            icoSize,
            icoSize
        )
        layoutparams1.endToStart = deleateIcoView.id
        layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams = layoutparams1
        val padding = round(icoSize.toFloat() / 1.25f).toInt()
        setPadding(padding,padding,padding,padding)
        rotation = 180f
        setImageResource(R.drawable.edit_ico)
        imageTintList = ColorStateList.valueOf("#B0B0B0".toColorInt())
        scaleType = ImageView.ScaleType.CENTER_CROP
        id = View.generateViewId()
    }
    when (adapterType) {
        AdapterType.EditEpisodes -> {}
        AdapterType.EditChapters -> container.addView(editIcoView)
        AdapterType.EditChaptersPages -> {}
    }

    return createAdapterUiReturn(
        container,
        imageAddIco,
        imageView,
        cardView,
        imageContainer,
        hT,
        hTI,
        lengthTextView,
        dragicoView,
        deleateIcoView,
        addBgDrawable,
        editIcoView,
        numTextView
    )
}

class FlatGridOfEditEpisodesAdapter(private val context: Context, private val items: MutableList<episodeInfo>, private val widthh: Int, private val itemHeightt: Int, private val changes: (MutableList<episodeInfo>) -> Unit, private val changeImage: (Int) -> Unit): RecyclerView.Adapter<FlatGridOfEditEpisodesAdapter.ViewHolder>() {
    class ViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    lateinit var touchHelper: ItemTouchHelper

    override fun onCreateViewHolder(p0: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(ConstraintLayout(context).apply {
            val layoutParams1 = RecyclerView.LayoutParams(
                widthh,
                itemHeightt
            )
            layoutParams = layoutParams1
        })
    }

    fun addEpisode(newEpisode: episodeInfo) {
        items.add(newEpisode)
        notifyItemInserted(items.size - 1)
        changes(items)
    }
    fun removeEpisode(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size - position)
            changes(items)
        }
    }
    fun changeEpisodeName(newName: String, position: Int) {
        if (position >= 0 && position < items.size) {
            items[position].name = newName
            changes(items)
        }
    }
    fun changeEpisodeBanner(newImage: ImageData, position: Int) {
        if (position >= 0 && position < items.size) {
            items[position].image = newImage
            changes(items)
            notifyItemChanged(position)
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.constraintLayout.removeAllViews()
        val ui = createAdapterUi(context, AdapterType.EditEpisodes, itemHeightt, widthh)
        holder.constraintLayout.addView(ui.container)
        fun newImage(image: ImageData?) {
            if (image != null) {
                ui.imageView.loadImage(image)
                ui.imageAddIco.alpha = 0f
                ui.cardView.alpha = 1f
            }
            else {
                ui.cardView.alpha = 0f
                ui.imageAddIco.alpha = 1f
                ui.imageContainer.background = ui.addBgDrawable
            }
        }
        newImage(items[position].image)

        ui.hTI.setText(items[position].name)

        ui.hTI.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Проверяем, что мы всё еще редактируем ту же позицию
                val actualPosition = holder.adapterPosition
                if (actualPosition != RecyclerView.NO_POSITION) {
                    changeEpisodeName(ui.hTI.text.toString(), actualPosition)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        ui.lengthTextView.text = convertToStringTime(items[position].length)


        ui.deleateIcoView.setOnClickListener {
            val actualPosition = holder.adapterPosition
            if (actualPosition != RecyclerView.NO_POSITION) {
                removeEpisode(actualPosition)
            }
        }
        ui.dragicoView.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper.startDrag(holder)
            }
            false
        }
        holder.constraintLayout.isFocusable = true
        holder.constraintLayout.isFocusableInTouchMode = true
        holder.constraintLayout.setOnClickListener {
            holder.constraintLayout.requestFocus()
        }
        ui.imageContainer.setOnClickListener {
            ui.imageContainer.requestFocus()
            val actualPosition = holder.adapterPosition
            if (actualPosition != RecyclerView.NO_POSITION) {
                changeImage(actualPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class FlatGridOfEditChaptersAdapter(private val context: Context, private val items: MutableList<ChapterInfo>, private val widthh: Int, private val itemHeightt: Int, private val changes: (MutableList<ChapterInfo>) -> Unit, private val openEditChaptersPagesPage: (MutableList<ImageData>, Long) -> Unit): RecyclerView.Adapter<FlatGridOfEditChaptersAdapter.ViewHolder>() {
    class ViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    lateinit var touchHelper: ItemTouchHelper

    override fun onCreateViewHolder(p0: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(ConstraintLayout(context).apply {
            val layoutParams1 = RecyclerView.LayoutParams(
                widthh,
                itemHeightt
            )
            layoutParams = layoutParams1
        })
    }

    fun addChapter(newChapter: ChapterInfo) {
        items.add(newChapter)
        notifyItemInserted(items.size - 1)
        changes(items)
    }
    fun removeChapter(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size - position)
            changes(items)
        }
    }
    fun changeChapterName(newName: String, position: Int) {
        if (position >= 0 && position < items.size) {
            items[position].name = newName
            changes(items)
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.constraintLayout.removeAllViews()
        val ui = createAdapterUi(context, AdapterType.EditChapters, itemHeightt, widthh)
        holder.constraintLayout.addView(ui.container)
        ui.hTI.setText(items[position].name)


        val oldNumTextViewWidth = ui.numTextView.measuredWidth
        ui.numTextView.text = (position+1).toString()
        ui.numTextView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val newNumTextViewWidth = ui.numTextView.measuredWidth
        val diff = newNumTextViewWidth - oldNumTextViewWidth
        ui.hT.maxWidth += diff
        ui.hT.invalidate()
        ui.hT.requestLayout()
        ui.hTI.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Проверяем, что мы всё еще редактируем ту же позицию
                val actualPosition = holder.adapterPosition
                if (actualPosition != RecyclerView.NO_POSITION) {
                    changeChapterName(ui.hTI.text.toString(), actualPosition)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        ui.deleateIcoView.setOnClickListener {
            val actualPosition = holder.adapterPosition
            if (actualPosition != RecyclerView.NO_POSITION) {
                removeChapter(actualPosition)
            }
        }
        ui.dragicoView.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper.startDrag(holder)
            }
            false
        }
        holder.constraintLayout.isFocusable = true
        holder.constraintLayout.isFocusableInTouchMode = true
        holder.constraintLayout.setOnClickListener {
        }
        ui.editIcoView.setOnClickListener {
            openEditChaptersPagesPage(items[position].childs.toMutableList(), items[position].id)
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
}

class FlatGridOfEditChaptersPagesAdapter(private val context: Context, private val items: MutableList<ImageData>, private val widthh: Int, private val itemHeightt: Int, private val changes: (MutableList<ImageData>) -> Unit): RecyclerView.Adapter<FlatGridOfEditChaptersPagesAdapter.ViewHolder>() {
    class ViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    lateinit var touchHelper: ItemTouchHelper

    override fun onCreateViewHolder(p0: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(ConstraintLayout(context).apply {
            val layoutParams1 = RecyclerView.LayoutParams(
                widthh,
                itemHeightt
            )
            layoutParams = layoutParams1
        })
    }

    fun addChapterPage(newChapter: ImageData) {
        items.add(newChapter)
        notifyItemInserted(items.size - 1)
        changes(items)
    }
    fun removeChapterPage(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size - position)
            changes(items)
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.constraintLayout.removeAllViews()
        val ui = createAdapterUi(context, AdapterType.EditChaptersPages, itemHeightt, widthh)
        holder.constraintLayout.addView(ui.container)
        ui.numTextView.text = (position+1).toString()
        ui.deleateIcoView.setOnClickListener {
            val actualPosition = holder.adapterPosition
            if (actualPosition != RecyclerView.NO_POSITION) {
                removeChapterPage(actualPosition)
            }
        }
        ui.dragicoView.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper.startDrag(holder)
            }
            false
        }
        holder.constraintLayout.isFocusable = true
        holder.constraintLayout.isFocusableInTouchMode = true
        holder.constraintLayout.setOnClickListener {
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
}