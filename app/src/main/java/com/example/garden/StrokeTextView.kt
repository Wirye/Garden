package com.example.garden

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class StrokeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    var strokeColor: Int = Color.BLACK
    var strokeWidth: Float = 4f // Толщина контура

    override fun onDraw(canvas: Canvas) {
        val states = textColors

        // 1. Настраиваем кисть для контура
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeMiter = 10f
        this.setTextColor(strokeColor)
        paint.strokeWidth = strokeWidth

        // Рисуем контур
        super.onDraw(canvas)

        // 2. Возвращаем обычный стиль для заливки текста
        paint.style = Paint.Style.FILL
        this.setTextColor(states)

        // Рисуем основной текст поверх контура
        super.onDraw(canvas)
    }
}