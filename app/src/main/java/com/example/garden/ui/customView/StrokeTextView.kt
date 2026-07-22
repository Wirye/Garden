package com.example.garden.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.garden.baseDensity
import kotlin.math.round

class StrokeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    var strokeColor: Int = Color.BLACK
    var strokeWidth: Float = round(1.3f*baseDensity)

    override fun onDraw(canvas: Canvas) {
        val states = textColors
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeMiter = round(3f* baseDensity)
        this.setTextColor(strokeColor)
        paint.strokeWidth = strokeWidth
        super.onDraw(canvas)
        paint.style = Paint.Style.FILL
        this.setTextColor(states)
        super.onDraw(canvas)
    }
}