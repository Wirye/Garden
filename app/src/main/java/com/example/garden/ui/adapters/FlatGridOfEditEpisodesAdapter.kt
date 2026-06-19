package com.example.garden.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
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
import com.example.garden.density
import com.example.garden.episodeInfo
import com.example.garden.ui.utils.getAdaptiveRadius
import com.example.garden.ui.utils.getTextSizeByHeight
import com.example.garden.ui.utils.system.hideKeyboardd
import com.example.garden.ui.utils.viewExtensions.loadImage
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.round

class FlatGridOfEditEpisodesAdapter(private val context: Context, private val items: MutableList<episodeInfo>, private val widthh: Int, private val itemHeightt: Int, private val changes: (MutableList<episodeInfo>) -> Unit, private val changeImage: (Int) -> Unit): RecyclerView.Adapter<FlatGridOfEditEpisodesAdapter.ViewHolder>() {
    class ViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    lateinit var touchHelper: ItemTouchHelper

    override fun onCreateViewHolder(p0: ViewGroup, position: Int): FlatGridOfEditEpisodesAdapter.ViewHolder {
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
        val boldFont = context.resources.getFont(R.font.google_sans_bold)
        val hTextHeight = round(itemHeightt.toFloat() / 2.55f).toInt()
        val textHeight = round(itemHeightt.toFloat() / 3.8f).toInt()
        val hTextSizee = getTextSizeByHeight(hTextHeight, boldFont)
        val textSizee = getTextSizeByHeight(textHeight, boldFont)
        val hTextColor = "#FFFFFF".toColorInt()
        val textColor = "#BFAFAFAF".toColorInt()
        val icoSize = round(itemHeightt.toFloat() / 1.5f).toInt()
        val margin = round(20f*density).toInt()
        val deleateIcoSize = round(icoSize.toFloat() / 1.6f).toInt()
        val container = holder.constraintLayout
        val addBgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = getAdaptiveRadius(itemHeightt, SizeType.SMALL)
            setColor("#BF1B1B1B".toColorInt())
            setStroke(round(1f*baseDensity).toInt(), "#809C9C9C".toColorInt())
        }
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
        fun newImage(image: ImageData?) {
            if (image != null) {
                imageView.loadImage(image)
                imageAddIco.alpha = 0f
                cardView.alpha = 1f
            }
            else {
                cardView.alpha = 0f
                imageAddIco.alpha = 1f
                imageContainer.background = addBgDrawable
            }
        }
        newImage(items[position].image)
        imageContainer.addView(imageAddIco)
        imageContainer.addView(cardView)
        container.addView(imageContainer)
        val hT = TextInputLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0,0,0,0)
            layoutparams1.startToEnd = imageContainer.id
            layoutparams1.topToTop = imageContainer.id
            isHintEnabled = false
            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_NONE
            minimumHeight = 0
            layoutParams = layoutparams1
            id = View.generateViewId()
            maxWidth = widthh - icoSize - itemHeightt - margin*3 - deleateIcoSize
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
            maxWidth = widthh - icoSize - itemHeightt - margin*3 - deleateIcoSize
            maxLines = 1
            isSingleLine = true
            if (items[position].name != "") {
                setText(items[position].name)
            }
        }

        fun clearHTIFocus() {
            val actualPosition = holder.adapterPosition
            if (actualPosition != RecyclerView.NO_POSITION) {
                hTI.clearFocus()
                hideKeyboardd(hTI)
                changeEpisodeName(hTI.text.toString(), actualPosition)
            }
        }
        hTI.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clearHTIFocus()
                true
            } else {
                false
            }
        }
        hTI.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Проверяем, что мы всё еще редактируем ту же позицию
                val actualPosition = holder.adapterPosition
                if (actualPosition != RecyclerView.NO_POSITION) {
                    changeEpisodeName(hTI.text.toString(), actualPosition)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        hT.addView(hTI)
        hT.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val textView = TextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToStart = hT.id
            layoutparams1.topToBottom = hT.id
            layoutParams = layoutparams1
            ellipsize = TextUtils.TruncateAt.END
            text = convertToStringTime(items[position].length)
            setTextColor(textColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            maxWidth = widthh - icoSize - itemHeightt - margin*2
        }
        val sumTextsHeight = hT.measuredHeight + textView.measuredHeight
        val textsMarginTop = round((itemHeightt - sumTextsHeight).toFloat() / 6f).toInt() * 5
        val lp1 = hT.layoutParams as ConstraintLayout.LayoutParams
        lp1.setMargins(margin, textsMarginTop,0,0)
        hT.layoutParams = lp1
        container.addView(hT)
        container.addView(textView)
        val dragicoView = ImageView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                icoSize,
                icoSize
            )
            layoutparams1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = imageContainer.id
            layoutparams1.bottomToBottom = imageContainer.id
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
            layoutparams1.topToTop = dragicoView.id
            layoutparams1.bottomToBottom = dragicoView.id
            layoutparams1.setMargins(0,0,margin,0)
            layoutParams = layoutparams1
            setImageResource(R.drawable.delete_ico)
            imageTintList = ColorStateList.valueOf("#852221".toColorInt())
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        container.addView(deleateIcoView)
        deleateIcoView.setOnClickListener {
            val actualPosition = holder.adapterPosition
            if (actualPosition != RecyclerView.NO_POSITION) {
                removeEpisode(actualPosition)
            }
        }
        dragicoView.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper.startDrag(holder)
                if (hTI.hasFocus()) {
                    clearHTIFocus()
                }
            }
            false
        }
        holder.constraintLayout.isFocusable = true
        holder.constraintLayout.isFocusableInTouchMode = true
        holder.constraintLayout.setOnClickListener {
        }
        imageContainer.setOnClickListener {
            val actualPosition = holder.adapterPosition
            if (actualPosition != RecyclerView.NO_POSITION) {
                changeImage(actualPosition)
            }
            if (hTI.hasFocus()) {
                clearHTIFocus()
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
