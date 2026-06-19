package com.example.garden.ui.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.garden.appsettings.AnimeSettingsState
import com.example.garden.baseDensity
import com.example.garden.ui.utils.createGridOfChilds
import com.example.garden.objectData2
import com.example.garden.screenHeight
import com.example.garden.screenWidth
import com.example.garden.ui.adapters.objectDiffCallbacks.ObjectDiffCallback2
import kotlin.math.min
import kotlin.math.round

data class animePageSezonsAdapterListFormat(
    var obj: objectData2,
    var settingsState: AnimeSettingsState
)
class AnimePageSezonsPageAdapter(private val context: Context, private val clickOnCard: (objectData2) -> Unit): ListAdapter<animePageSezonsAdapterListFormat, AnimePageSezonsPageAdapter.ViewHolder>(ObjectDiffCallback2()) {
    class ViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ConstraintLayout(context).apply {
            val layoutParams1 = RecyclerView.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = layoutParams1
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.constraintLayout.removeAllViews()
        val isLandscape = screenWidth > screenHeight
        val maxPageWidth = round(1000f * baseDensity).toInt() // Лимит для планшетов
        val actualWidth = min(screenWidth, maxPageWidth) // Чтобы не растягивалось в бесконечность
        val marginBetweenElementsHorizontal = round(16f * baseDensity).toInt()  // С.м onBindViewHolder у animePageAdapter -> marginLeft
        val amountOfCards = if (isLandscape) 4 else 2

        val item = getItem(position)
        val parentCard = getItem(position).obj
        parentCard.childsShowName = item.settingsState.showChildsName
        parentCard.childsNamePosition = item.settingsState.childsNamePosition
        parentCard.marginBetweenElementsHorizontal = marginBetweenElementsHorizontal

        val layout = createGridOfChilds(parentCard.childs, amountOfCards, context, round(actualWidth.toFloat() - marginBetweenElementsHorizontal*2).toInt(), parentCard)
        val layoutparams1 = layout.layoutParams as ConstraintLayout.LayoutParams
        layoutparams1.setMargins(marginBetweenElementsHorizontal, 0,0,0)
        layout.layoutParams = layoutparams1
        holder.constraintLayout.addView(layout)
        for (i in 0 until layout.childCount) {
            val obj = layout.getChildAt(i)
            val objData = parentCard.childs[i]
            obj.setOnClickListener {
                clickOnCard(objData)
            }
        }
    }
}
