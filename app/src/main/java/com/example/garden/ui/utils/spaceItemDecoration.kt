package com.example.garden.ui.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView

data class spaceItemDecorationInput (
    val spaces: List<Int>,
    val firstObjectSpaces: List<Int>
)
class spaceItemDecoration(private val info: spaceItemDecorationInput) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: android.graphics.Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position != 0) {
            outRect.left = info.spaces[0]
            outRect.top = info.spaces[1]
            outRect.right = info.spaces[2]
            outRect.bottom = info.spaces[3]
        }
        else {
            outRect.left = info.firstObjectSpaces[0]
            outRect.top = info.firstObjectSpaces[1]
            outRect.right = info.firstObjectSpaces[2]
            outRect.bottom = info.firstObjectSpaces[3]
        }
    }
}