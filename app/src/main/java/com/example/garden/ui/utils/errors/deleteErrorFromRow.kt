package com.example.garden.ui.utils.errors

import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout

fun deleteErrorFromRow(row: ConstraintLayout, errorViewTag: String) {
    val errorView: ConstraintLayout? = row.findViewWithTag(errorViewTag)
    errorView ?: return
    val rowlp1 = row.layoutParams as? ConstraintLayout.LayoutParams ?: run {
        Log.d("deleteErrowFromRow", "Incorrect row layout params")
        return
    }
    for (i in 0 until row.childCount) {
        val child = row.getChildAt(i)
        val test = child.layoutParams as? ConstraintLayout.LayoutParams ?: run {
            Log.d("deleteErrowFromRow", "Incorrect row -> child $i layout params")
            return
        }
    }
    row.post {
        errorView.post {
            rowlp1.height = row.height - errorView.height
            row.layoutParams = rowlp1
            for (i in 0 until row.childCount) {
                val child = row.getChildAt(i)
                val childlp1 = child.layoutParams as? ConstraintLayout.LayoutParams
                if (childlp1 != null) {
                    if (childlp1.bottomToTop == errorView.id) {
                        childlp1.bottomToTop = ConstraintLayout.LayoutParams.UNSET
                        childlp1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        child.layoutParams = childlp1
                    }
                }
            }
            row.removeView(errorView)
        }
    }
}