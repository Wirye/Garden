package com.example.garden.ui.factories

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
import com.example.garden.ui.utils.segmentedButtonOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.slider.Slider
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
        val icoId: Int,
        val text: String,
        val sizeType: SizeType,
        val options: List<Pair<segmentedButtonOptions, BsdButtonsTags>>,
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
        val stops: List<Float>,
        val createSteps: Boolean,
    ) : BottomSheetDialogElement()
}
class bottomSheetDialogFactory(private val activity: Activity) {
    @SuppressLint("PrivateResource")
    fun createDialog(list: List<BottomSheetDialogElement>, callback: (tag: BsdButtonsTags) -> Unit) {
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
                        callback(element.tag)
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
                        val currentTag = element.options[i].second
                        val segment = segmentedView.findViewWithTag<View>("button_$i")
                        segment?.setOnClickListener { clickedSegment ->
                            callback(currentTag)
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
                        createSteps = element.createSteps
                    )
                    val sliderContainer = sliderViews[0] as ConstraintLayout
                    val slider = sliderViews.last() as ConstraintLayout
                    val sliderr = slider.getChildAt(0) as Slider
                    sliderr.addOnChangeListener { _, value, _ ->
                        callback(element.tag)
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
                            callback(selectedTag)
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
