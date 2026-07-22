package com.example.garden.ui.utils.errors

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import com.example.garden.R
import com.example.garden.baseDensity
import com.example.garden.ui.customView.StrokeTextView
import kotlin.math.floor
import kotlin.math.round

fun addErrorToRow(row: ConstraintLayout, errorText: String, context: Context, errorViewTag: String) {
    val font = context.resources.getFont(R.font.google_sans_medium)
    val rowlp1 = row.layoutParams as? ConstraintLayout.LayoutParams ?: run {
        Log.d("addOrDelErrorToRow", "Incorrect row layout params")
        return
    }
    for (i in 0 until row.childCount) {
        val child = row.getChildAt(i)
        val test = child.layoutParams as? ConstraintLayout.LayoutParams ?: run {
            Log.d("addOrDelErrorToRow", "Incorrect row -> child $i layout params")
            return
        }
    }
    row.post {
        val textSizee = floor(15f * baseDensity)
        val errorTextView = StrokeTextView(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                row.width,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            setPadding(round(8f*baseDensity).toInt(),0,0,0)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizee)
            setTextColor("#FFFFFF".toColorInt())
            text = errorText
            includeFontPadding = false
            typeface = font
            layoutParams = layoutparams1
            val newId = View.generateViewId()
            id = newId
        }
        errorTextView.measure(
            View.MeasureSpec.makeMeasureSpec(row.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val errorContainerViewHeight = errorTextView.measuredHeight + round(8f*baseDensity).toInt()
        val errorContainerView = ConstraintLayout(context).apply {
            val layoutparams1 = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                errorContainerViewHeight
            )
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = layoutparams1
            setBackgroundColor("#db4242".toColorInt())
            id = View.generateViewId()
            tag = errorViewTag
        }

        errorContainerView.addView(errorTextView)
        rowlp1.height = row.height + errorContainerViewHeight
        row.layoutParams = rowlp1

        row.addView(errorContainerView)
        for (i in 0 until row.childCount) {
            val child = row.getChildAt(i)
            val childlp1 = child.layoutParams as? ConstraintLayout.LayoutParams ?: return@post
            if (childlp1.bottomToBottom == ConstraintLayout.LayoutParams.PARENT_ID && child.id != errorContainerView.id) {
                childlp1.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                childlp1.bottomToTop = errorContainerView.id
                child.layoutParams = childlp1
            }
        }
    }
}