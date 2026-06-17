package com.example.garden.customView

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class OptimizedTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (layout != null && layout.lineCount > 1) {
            var maxLineWidth = 0f
            for (i in 0 until layout.lineCount) {
                if (layout.getLineWidth(i) > maxLineWidth) {
                    maxLineWidth = layout.getLineWidth(i)
                }
            }
            val realWidth = Math.ceil(maxLineWidth.toDouble()).toInt() + paddingLeft + paddingRight
            if (realWidth < measuredWidth) {
                setMeasuredDimension(realWidth, measuredHeight)
            }
        }
    }
}