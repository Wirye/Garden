package com.example.garden.ui.factories

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import com.example.garden.BsdButtonsTags
import com.example.garden.baseDensity
import com.example.garden.ui.utils.createBSDButton
import com.example.garden.ui.utils.createDropdownRow
import com.example.garden.ui.utils.createSegmentedButtonRow
import com.example.garden.ui.utils.createSliderRow
import com.example.garden.database.SizeType
import com.example.garden.density
import com.example.garden.ui.utils.getAdaptiveRadius
import com.example.garden.screenHeight
import com.example.garden.screenWidth
import com.example.garden.ui.utils.mathExtensions.snapToStep
import com.example.garden.ui.utils.segmentedButtonOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.min
import kotlin.math.round

sealed class BottomSheetDialogElement {
    data class Button (
        val tag: BsdButtonsTags,
        val icoId: Int,
        val text: String,
        val openThePage: Boolean,
    ) : BottomSheetDialogElement()

    data class SegmentedButton(
        val tag: BsdButtonsTags,
        val icoId: Int,
        val text: String,
        val sizeType: SizeType,
        val options: List<segmentedButtonOptions>,
    ) : BottomSheetDialogElement()

    data class DropdownRow(
        val text: String,
        val icoId: Int,
        val buttonIcoId: Int?,
        val options: List<Pair<String, BsdButtonsTags>>,
    ) : BottomSheetDialogElement()

    data class Slider(
        val tag: BsdButtonsTags,
        val icoId: Int,
        val text: String,
        val stops: List<Pair<Float, String>>,
        val createSteps: Boolean,
        val alreadyValue: Float,
        val createTextInputView: Boolean = false
    ) : BottomSheetDialogElement()
}
class bottomSheetDialogFactory(private val activity: Activity) {
    @SuppressLint("PrivateResource", "SetTextI18n")
    fun createDialog(list: List<BottomSheetDialogElement>, callback: (tag: BsdButtonsTags, value: Float) -> Unit) {
        val dialog = BottomSheetDialog(activity)
        val googleMaxWidthDp = round(640f * baseDensity).toInt()
        val googleMaxWidthPx = round(googleMaxWidthDp.toFloat() * baseDensity).toInt()

        val elementWidth = min((min(screenWidth, screenHeight)), googleMaxWidthPx)
        val elementHeight = (150f * density).toInt()
        val pillRawHeight = (100f * density).toInt()
        val pillHeight = round(pillRawHeight.toFloat() / 4f).toInt()

        val container = LinearLayout(activity).apply {
            val layoutparams1 = LinearLayout.LayoutParams(
                elementWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL

            layoutParams = layoutparams1
            id = View.generateViewId()
        }

        val scrollContainer = NestedScrollView(activity).apply {
            val layoutparams1 = LinearLayout.LayoutParams(
                elementWidth,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            addView(container)
            layoutparams1.setMargins(0,pillRawHeight,0,0)
            layoutParams = layoutparams1
        }
        dialog.setContentView(scrollContainer)
        val pillDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 10000f
            setColor("#BF4C4C4C".toColorInt())
        }

        val showPoloska = false
        fun createPoloska() : ImageView {
            val poloska = ImageView(activity).apply {
                val lp1 = ConstraintLayout.LayoutParams(
                    elementWidth,
                    round(3f*density).toInt()
                )
                lp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                lp1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                setBackgroundColor("#26D9D9D9".toColorInt())
                layoutParams = lp1
            }
            return poloska
        }

        for (element in list) {
            when (element) {
                is BottomSheetDialogElement.Button -> {
                    val buttonView = createBSDButton(
                        textt = element.text,
                        icoId = element.icoId,
                        showOpenPageArrow = element.openThePage,
                        context = activity,
                        width = elementWidth,
                        height = elementHeight
                    )
                    if (showPoloska) {
                        val poloska = createPoloska()
                        buttonView.addView(poloska)
                    }
                    buttonView.setOnClickListener {
                        callback(element.tag,0f)
                    }
                    container.addView(buttonView)
                }

                is BottomSheetDialogElement.SegmentedButton -> {
                    val segmentedView = createSegmentedButtonRow(
                        context = activity,
                        width = elementWidth,
                        height = elementHeight,
                        options = element.options,
                        icoId = element.icoId,
                        textt = element.text
                    )
                    for (i in element.options.indices) {
                        val currentValue = i.toFloat()
                        val segment = segmentedView.findViewWithTag<View>("button_$i")
                        segment?.setOnClickListener { clickedSegment ->
                            callback(element.tag,currentValue)
                            for (o in element.options.indices) {
                                val segment1 = segmentedView.findViewWithTag<View>("button_$o") ?: continue
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
                    if (showPoloska) {
                        val poloska = createPoloska()
                        segmentedView.addView(poloska)
                    }
                    container.addView(segmentedView)
                }

                is BottomSheetDialogElement.Slider -> {
                    val sliderHeight = elementHeight
                    val sliderViews = createSliderRow(
                        context = activity,
                        width = elementWidth,
                        name = element.text,
                        icoId = element.icoId,
                        stopsList = element.stops,
                        heightt = sliderHeight,
                        createSteps = element.createSteps,
                        alreadyValue = element.alreadyValue,
                        createTextInputView = element.createTextInputView
                    )
                    val sliderContainer = sliderViews[0] as ConstraintLayout
                    val slider = sliderViews.last() as ConstraintLayout
                    val slider1 = slider.getChildAt(0) as ConstraintLayout
                    val slider2 = slider1.getChildAt(0) as ConstraintLayout
                    val sliderr = slider2.getChildAt(0) as Slider
                    val textInputLayout = if (element.createTextInputView) {slider.getChildAt(1) as TextInputLayout} else {null}
                    var textInputEditText: TextInputEditText? = null
                    if (element.createTextInputView) {
                        for (k in 0 until textInputLayout!!.childCount) {
                            val obj = textInputLayout.getChildAt(k)
                            if (obj is TextInputEditText) {
                                textInputEditText = obj
                                break
                            }
                            else if (obj is FrameLayout) {
                                for (h in 0 until obj.childCount) {
                                    val objj = obj.getChildAt(h)
                                    if (objj is TextInputEditText) {
                                        textInputEditText = objj
                                        break
                                    }
                                }
                            }
                        }
                        textInputEditText?.addTextChangedListener (object : TextWatcher {
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                val rawText = textInputEditText.text.toString().trim().replace(',', '.')
                                var newSliderValue = rawText.toFloatOrNull() ?: 0f
                                val minSliderValue = element.stops[0].first
                                val maxSliderValue = element.stops[element.stops.lastIndex].first
                                if (element.createSteps) {
                                    val stepSize = if(element.stops.size > 2) {(maxSliderValue - minSliderValue) / (element.stops.size.toFloat() - 1f)} else {maxSliderValue - minSliderValue}
                                    newSliderValue = newSliderValue.snapToStep(minSliderValue, stepSize)
                                }
                                newSliderValue = newSliderValue.coerceIn(minSliderValue, maxSliderValue)
                                sliderr.value = "%.2f".format(newSliderValue).trim().replace(",", ".").toFloat()
                            }
                            override fun afterTextChanged(s: Editable?) {
                                val minSliderValue = element.stops[0].first
                                val maxSliderValue = element.stops[element.stops.lastIndex].first
                                val text = textInputEditText.text.toString().trim().replace(',', '.')
                                val textValue = text.toFloatOrNull() ?: return
                                if (textValue > maxSliderValue) {
                                    textInputEditText.setText("%.2f".format(maxSliderValue).trim().replace(",", "."))
                                }
                                else if (textValue < minSliderValue) {
                                    textInputEditText.setText("%.2f".format(minSliderValue).trim().replace(",", "."))
                                }
                                else if (element.createSteps) {
                                    val stepSize = if(element.stops.size > 2) {(maxSliderValue - minSliderValue) / (element.stops.size.toFloat() - 1f)} else {maxSliderValue - minSliderValue}
                                    val snappedTextValue = textValue.snapToStep(minSliderValue, stepSize)
                                    if (snappedTextValue != textValue) {
                                        textInputEditText.setText("%.2f".format(snappedTextValue).trim().replace(",", "."))
                                    }
                                }
                            }
                        })
                    }
                    sliderr.addOnChangeListener { _, value, _ ->
                        textInputEditText?.setText("%.2f".format(sliderr.value).trim().replace(",","."))
                        callback(element.tag,value)
                    }
                    if (showPoloska) {
                        val poloska = createPoloska()
                        sliderContainer.addView(poloska)
                    }
                    container.addView(sliderContainer)
                }

                is BottomSheetDialogElement.DropdownRow -> {
                    val dropdownView = createDropdownRow(
                        context = activity,
                        width = elementWidth,
                        height = elementHeight,
                        titleText = element.text,
                        icoId = element.icoId,
                        options = element.options,
                        onItemSelected = { selectedText, selectedTag ->
                            callback(selectedTag,0f)
                        },
                        buttonIcoId = element.buttonIcoId
                    )
                    if (showPoloska) {
                        val poloska = createPoloska()
                        dropdownView.addView(poloska)
                    }
                    container.addView(dropdownView)
                }
            }
        }
        val radius = getAdaptiveRadius(elementWidth, SizeType.XLARGE)
        val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

        dialog.setOnShowListener {
            bottomSheet?.let { sheet ->
                ViewCompat.setOnApplyWindowInsetsListener(sheet) { v, insets ->
                    v.setPadding(0, 0, 0, 0)
                    WindowInsetsCompat.CONSUMED
                }
                val behavior = BottomSheetBehavior.from(sheet)
                val shapeAppearance = ShapeAppearanceModel.builder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                    .setTopRightCorner(CornerFamily.ROUNDED, radius)
                    .build()

                sheet.background = MaterialShapeDrawable(shapeAppearance).apply {
                    fillColor = ColorStateList.valueOf("#181619".toColorInt())
                }
                sheet.backgroundTintList = null
                if (sheet.findViewWithTag<View>("handle") == null) {
                    val handle = View(activity).apply {
                        tag = "handle"
                        val lp = FrameLayout.LayoutParams(
                            round(elementWidth.toFloat() / 6f).toInt(),
                            pillHeight
                        ).apply {
                            gravity = Gravity.CENTER_HORIZONTAL
                            topMargin = round(12f * density).toInt()
                        }
                        layoutParams = lp
                        background = pillDrawable
                    }
                    sheet.addView(handle)
                }
                val lp1 = sheet.layoutParams
                lp1.width = elementWidth
                sheet.layoutParams = lp1
                behavior.skipCollapsed = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.maxHeight = (screenHeight.toFloat() * 0.7f).toInt()
            }
        }

        dialog.window?.let { window ->
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.setLayout(elementWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
            window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }

        dialog.show()
    }
}
