package com.example.garden.ui.utils.animations

import android.animation.Animator
import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import kotlin.math.round


/**
 * Плавное переключение видимости любого количества строк (эффект выезжающей шторки).
 * Все анимированные строки должны быть прикреплены к topToTop сторки выше их
 * @param rootContainer Общий родитель, где лежат строки, которые нужно анимировать, и
 * строка, от которой должна начинаться анимация 1-й анимированной строки
 * @param rows Список строк, которые должны быть анимированы
 * @param targetState Явное указание состояния (true - выдвинуть, false - задвинуть, null - автоматическое переключение)
 * @param animate Нужно ли анимировать (false - это duration = 0)
 * @param onStateChanged Коллбэк, который возвращает состояние (выдвинуто/задвинуто)
 */

private val animationsList = mutableListOf<AnimationData>()
fun toggleExtensionAnimation(rootContainer: ConstraintLayout, rows: List<View>, animSourceRow: View, targetState: Boolean? = null, animate: Boolean = true, animationId: Long) {
    if (rows.isEmpty()) return

    val isCurrentlyExtend = rows.any { it.isVisible }
    val shouldBeExtended = targetState ?: !isCurrentlyExtend

    val sortedRowList = mutableListOf<View>()
    val lastRowId = animSourceRow.id

    fun sortRowsList(list: List<View>): List<View> {
        val result = mutableListOf<View>()
        val remaining = list.toMutableList()
        var currentSeekId = lastRowId

        while (remaining.isNotEmpty()) {
            val nextRow = remaining.find { view ->
                val lp = view.layoutParams as? ConstraintLayout.LayoutParams
                lp?.topToTop == currentSeekId
            }

            if (nextRow != null) {
                result.add(nextRow)
                currentSeekId = nextRow.id
                remaining.remove(nextRow)
            }
            else {
                Log.d("toggleExtensionAnimation (sortRowsList)", "The chain is broken")
                break
            }
        }
        return result
    }
    val sortedList = sortRowsList(rows)

    if (sortedList.size == rows.size) {
        sortedRowList.clear()
        sortedRowList.addAll(sortedList)
    }
    else {
        Log.d("toggleExtensionAnimation", "Something is incorrect")
    }

    val animationData = animationsList.find { it.animationId == animationId } ?: AnimationData(animationId, emptyList()).also {
        animationsList.add(it) }

    animationData.valueAnimators.forEach { animator ->
        animator.cancel()
    }

    if (shouldBeExtended) {
        if (!animate) {
            for (i in sortedRowList.indices) {
                val row = sortedRowList[i]
                val previousRow = if (i == 0) animSourceRow else sortedRowList[i-1]
                val previousRowHeight = previousRow.layoutParams.height
                val rowlp1 = row.layoutParams as ConstraintLayout.LayoutParams
                rowlp1.setMargins(row.marginLeft, previousRowHeight, row.marginRight, row.marginBottom)
                row.layoutParams = rowlp1
            }
        }
        else {
            val animatorsList = mutableListOf<Triple<ValueAnimator, ValueAnimator, Boolean>>()
            for (i in sortedRowList.indices) {
                val row = sortedRowList[i]
                val previousRow = if (i == 0) animSourceRow else sortedRowList[i-1]
                val previousRowHeight = previousRow.layoutParams.height
                val remainingHeight = previousRowHeight - row.marginTop
                val durationn = round(250f * round(round(remainingHeight.toFloat() / round(previousRowHeight.toFloat() / 100f)) / 100f)).toInt().toLong()
                val alreadyHeight = previousRowHeight - remainingHeight
                val rowlp1 = row.layoutParams as ConstraintLayout.LayoutParams
                row.layoutParams = rowlp1
                val heightAnimator = ValueAnimator.ofFloat(alreadyHeight.toFloat(), previousRowHeight.toFloat()).apply {
                    duration = durationn
                    addUpdateListener {
                        val av = it.animatedValue as Float
                        rowlp1.setMargins(row.marginLeft, av.toInt(), row.marginRight, row.marginBottom)
                        row.invalidate()
                        row.requestLayout()
                        rootContainer.invalidate()
                        rootContainer.requestLayout()
                    }
                }
                val alphaAnimator = ValueAnimator.ofFloat(row.alpha, 1f).apply {
                    doOnStart { row.visibility = View.VISIBLE }
                    duration = durationn
                    addUpdateListener {
                        row.alpha = it.animatedValue as Float
                    }
                }
                animatorsList.add(Triple(heightAnimator, alphaAnimator, false))
            }
            for (i in animatorsList.indices) {
                if (i < animatorsList.indices.last()) {
                    val animator = animatorsList[i]
                    val nextAnimator = animatorsList[i+1]
                    animator.second.addUpdateListener {
                        val av = it.animatedValue as Float
                        if (av >= 0.5f && !nextAnimator.third) {
                            animatorsList[i+1] = Triple(nextAnimator.first, nextAnimator.second, true)
                            animatorsList[i+1].first.start()
                            animatorsList[i+1].second.start()
                        }
                    }
                }
            }
            animatorsList.first().first.start()
            animatorsList.first().second.start()
            val animatorsListt = mutableListOf<Animator>()
            animatorsList.forEach {
                animatorsListt.add(it.first)
                animatorsListt.add(it.second)
            }
            animationData.valueAnimators = animatorsListt
        }
    }
    else {
        if (!animate) {
            sortedRowList.forEach { row ->
                val rowlp1 = row.layoutParams as ConstraintLayout.LayoutParams
                rowlp1.setMargins(row.marginLeft, 0, row.marginRight, row.marginBottom)
                row.layoutParams = rowlp1
                row.visibility = View.GONE
                row.alpha = 0f
            }
        }
        else {
            val animatorsList = mutableListOf<Triple<ValueAnimator, ValueAnimator, Boolean>>()
            for (i in sortedRowList.indices) {
                val row = sortedRowList[i]
                val previousRow = if (i == 0) animSourceRow else sortedRowList[i-1]
                val previousRowHeight = previousRow.layoutParams.height
                val alreadyHeight = previousRowHeight - row.marginTop
                val remainingHeight = previousRowHeight - alreadyHeight
                val durationn = round(250f * round(round(remainingHeight.toFloat() / round(previousRowHeight.toFloat() / 100f)) / 100f)).toInt().toLong()
                val rowlp1 = row.layoutParams as ConstraintLayout.LayoutParams
                row.layoutParams = rowlp1
                val heightAnimator = ValueAnimator.ofFloat(remainingHeight.toFloat(), 0f).apply {
                    duration = durationn
                    addUpdateListener {
                        val av = it.animatedValue as Float
                        rowlp1.setMargins(row.marginLeft, av.toInt(), row.marginRight, row.marginBottom)
                        row.invalidate()
                        row.requestLayout()
                        rootContainer.invalidate()
                        rootContainer.requestLayout()
                    }
                }
                val alphaAnimator = ValueAnimator.ofFloat(row.alpha, 0f).apply {
                    doOnStart { row.visibility = View.VISIBLE }
                    duration = durationn
                    addUpdateListener {
                        row.alpha = it.animatedValue as Float
                    }
                    doOnEnd { row.visibility = View.GONE }
                }
                animatorsList.add(Triple(heightAnimator, alphaAnimator, false))
            }
            for (i in animatorsList.indices) {
                if (i < animatorsList.indices.last) {
                    val animator = animatorsList[animatorsList.indices.last - i]
                    val nextAnimator = animatorsList[animatorsList.indices.last - (i+1)]
                    animator.second.addUpdateListener {
                        val av = it.animatedValue as Float
                        if (av <= 0.5f && !nextAnimator.third) {
                            animatorsList[animatorsList.indices.last - (i+1)] = Triple(nextAnimator.first, nextAnimator.second, true)
                            animatorsList[animatorsList.indices.last - (i+1)].first.start()
                            animatorsList[animatorsList.indices.last - (i+1)].second.start()
                        }
                    }
                }
            }
            animatorsList.last().first.start()
            animatorsList.last().second.start()
            val animatorsListt = mutableListOf<Animator>()
            animatorsList.forEach {
                animatorsListt.add(it.first)
                animatorsListt.add(it.second)
            }
            animationData.valueAnimators = animatorsListt
        }
    }
}