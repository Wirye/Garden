package com.example.garden.ui.utils

import androidx.recyclerview.widget.RecyclerView
import com.example.garden.density
import com.example.garden.listDot
import com.example.garden.listDot2
import kotlin.math.round

class PageFlingListener(private val dotsList: List<Pair<listDot2, listDot>>, private val getCurrentScroll: () -> Int, private val updatePage: (Int) -> Unit, private val recyclerView: RecyclerView) : RecyclerView.OnFlingListener() {

    override fun onFling(velocityX: Int, velocityY: Int): Boolean {
        val currentScroll = getCurrentScroll()
        val targetDot = if (velocityX > round(500f*density).toInt()) {
            dotsList.find { it.second.itemPositionInPx > currentScroll }
        } else if (velocityX < -round(500f*density).toInt()) {
            dotsList.findLast { it.second.itemPositionInPx < currentScroll }
        } else {
            return false
        }

        targetDot?.let {
            val targetIndex = dotsList.indexOf(it)
            val targetScroll = it.second.itemPositionInPx
            val distance = targetScroll - currentScroll

            recyclerView.smoothScrollBy(distance, 0)
            updatePage(targetIndex)
            return true
        }

        return false
    }
}
