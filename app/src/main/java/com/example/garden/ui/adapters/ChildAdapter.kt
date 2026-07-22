package com.example.garden.ui.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.garden.baseDensity
import com.example.garden.ui.adapters.objectDiffCallbacks.ObjectDiffCallback
import com.example.garden.ui.utils.createCard
import com.example.garden.ui.utils.createGridOfChilds
import com.example.garden.objectData2
import com.example.garden.screenWidth
import kotlin.math.round

class ChildAdapter(private val context: Context, private val parentt: objectData2, val clickOnCard: (objectData2) -> Unit, val customLineWidth: Int? = null) : ListAdapter<objectData2, ChildAdapter.ViewHolder>(ObjectDiffCallback()) {

    class ViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    private var parent = parentt

    fun updateParent(newParent: objectData2) {
        parent = newParent
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ConstraintLayout(context).apply {
            val layoutParams1 = RecyclerView.LayoutParams(
                0,
                0
            )
            layoutParams1.setMargins(0,0,0,0)
            layoutParams = layoutParams1
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val elementWidth = customLineWidth ?: screenWidth
        holder.constraintLayout.removeAllViews()
        // Обычная карточка
        if (getItem(position).layoutType == null || getItem(position).layoutType == 1 || getItem(position).childs.isEmpty()) {
            val views = createCard(getItem(position).width, getItem(position).height, parent.childsShowName, parent.childsNamePosition, parent.childsShowAuthor, getItem(position).image, if (getItem(position).name != "" && getItem(position).name != null) getItem(position).name else "Без имени", getItem(position).author, getItem(position).alreadyWatched, getItem(position).length, parent.childsShowAlreadyWatchedLine, context, currentList, parent.childsCornerRadius, lineWidth = elementWidth, paddingHorizontal = parent.paddingHorizontal, marginBetweenElementsHorizontal = parent.marginBetweenElementsHorizontal)
            val layoutparams2 = RecyclerView.LayoutParams(
                views.second.first,
                views.second.second
            )
            holder.constraintLayout.layoutParams = layoutparams2
            for (i in views.first) {
                holder.constraintLayout.addView(i)
            }
            holder.constraintLayout.setOnClickListener {
                clickOnCard(getItem(position))
                holder.constraintLayout.requestFocus()
            }
        }
        // Grid
        else {
            var paddingHorizontal = parent.paddingHorizontal
            if (paddingHorizontal == null) {
                paddingHorizontal = round(19f * baseDensity).toInt()
            }
            val constraintlayout1 = createGridOfChilds(getItem(position).childs, getItem(position).objectsInOneLine, context, (elementWidth-paddingHorizontal*2), parent)
            val layoutparams1 = constraintlayout1.layoutParams as ConstraintLayout.LayoutParams
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(paddingHorizontal,0,0,0)
            constraintlayout1.layoutParams = layoutparams1
            constraintlayout1.setOnClickListener {
                constraintlayout1.requestFocus()
            }
            for (i in 0 until constraintlayout1.childCount) {
                val objData = getItem(position).childs[i]
                val obj = constraintlayout1.getChildAt(i)
                obj.setOnClickListener {
                    clickOnCard(objData)
                    obj.requestFocus()
                }
            }
            val layoutparams2 = holder.constraintLayout.layoutParams as RecyclerView.LayoutParams
            layoutparams2.apply { width = elementWidth; height = ConstraintLayout.LayoutParams.WRAP_CONTENT }
            layoutparams2.setMargins(0,0,0,0)
            holder.constraintLayout.layoutParams = layoutparams2
            holder.constraintLayout.addView(constraintlayout1)
        }
    }
}
