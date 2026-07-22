package com.example.garden.ui.utils.animations

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import androidx.core.view.updateLayoutParams
import kotlin.math.round

private val animationsList = mutableListOf<AnimationData>()

fun toggleSwitchButtonAnimation(switch: ViewGroup, targetState: Boolean, animationId: Long, animate: Boolean = true) {
    val colorOn = "#EADDFF".toColorInt()
    val colorOff = "#80EADDFF".toColorInt()
    val track: View? = switch.findViewWithTag("track")
    val thumb: View? = switch.findViewWithTag("thumb")
    track ?: run {
        Log.d("toggleSwitchButtonAnimation", "There is not track")
        return
    }
    thumb ?: run {
        Log.d("toggleSwitchButtonAnimation", "There is no thumb")
        return
    }
    val trackBackground = track.background as? GradientDrawable ?: run {
        Log.d("toggleSwitchButtonAnimation", "Track background is not GradientDrawable")
        return
    }
    val thumblp1 = thumb.layoutParams as? ConstraintLayout.LayoutParams ?: run {
        Log.d("toggleSwitchButtonAnimation", "Thumb layoutParams is not ConstraintLayout.LayoutParams")
        return
    }

    val animationData = animationsList.find { it.animationId == animationId } ?: AnimationData(animationId, emptyList()).also {
        animationsList.add(it) }

    animationData.valueAnimators.forEach { animator ->
        animator.cancel()
    }

    val currentTrackColor = trackBackground.color?.defaultColor ?: run {
        Log.d("toggleSwitchButtonAnimation", "currentTrackColor error")
        return
    }
    val currentThumbHorizontalBias = thumblp1.horizontalBias
    val targetColor = if (targetState) colorOn else colorOff
    val targetBias = if (targetState) 1f else 0f
    val remainingThumbHorizontalBias = if (targetState) 1f - currentThumbHorizontalBias else currentThumbHorizontalBias
    val durationn = if (animate) round(100f * round(round(remainingThumbHorizontalBias / 0.01f) / 100f)).toInt().toLong() else 0L
    val biasAnimator = ValueAnimator.ofFloat(currentThumbHorizontalBias, targetBias).apply {
        duration = durationn
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animator ->
            thumb.updateLayoutParams<ConstraintLayout.LayoutParams> {
                horizontalBias = animator.animatedValue as Float
            }
        }
    }
    val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), currentTrackColor, targetColor).apply {
        duration = durationn
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animator ->
            (track.background as GradientDrawable).setColor(animator.animatedValue as Int)
        }
    }
    animationData.valueAnimators = listOf(biasAnimator, colorAnimator)
    biasAnimator.start()
    colorAnimator.start()
}