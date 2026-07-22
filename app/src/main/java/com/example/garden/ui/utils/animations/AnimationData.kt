package com.example.garden.ui.utils.animations

import android.animation.Animator

data class AnimationData(
    val animationId: Long,
    var valueAnimators: List<Animator>
)