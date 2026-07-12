package com.example.garden.ui.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView

data class spaceItemDecorationInput (
    val spaces: List<Int>,
    val firstObjectSpaces: List<Int>,
    val lastObjectSpaces: List<Int>,
)
class spaceItemDecoration(private val info: spaceItemDecorationInput) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: android.graphics.Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemsAmount = state.itemCount
        if (position == 0) {
            outRect.left = info.firstObjectSpaces[0]
            outRect.top = info.firstObjectSpaces[1]
            outRect.right = info.firstObjectSpaces[2]
            outRect.bottom = info.firstObjectSpaces[3]
        }
        else if (position == itemsAmount-1) {
            outRect.left = info.lastObjectSpaces[0]
            outRect.top = info.lastObjectSpaces[1]
            outRect.right = info.lastObjectSpaces[2]
            outRect.bottom = info.lastObjectSpaces[3]
        }
        else {
            outRect.left = info.spaces[0]
            outRect.top = info.spaces[1]
            outRect.right = info.spaces[2]
            outRect.bottom = info.spaces[3]
        }

    }
}