package com.example.garden.ui.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.garden.ui.adapters.objectDiffCallbacks.ObjectDiffCallback
import com.example.garden.ui.utils.createCard
import com.example.garden.ui.utils.createGridOfChilds
import com.example.garden.density
import com.example.garden.objectData2
import com.example.garden.screenWidth
import kotlin.math.round

class ChildAdapter(private val context: Context, private val parent: objectData2, val clickOnCard: (objectData2) -> Unit) : ListAdapter<objectData2, ChildAdapter.ViewHolder>(ObjectDiffCallback()) {

    class ViewHolder(val constraintLayout: ConstraintLayout) : RecyclerView.ViewHolder(constraintLayout)

    // Это база для элемента (остальные его модификации применяются в onBindViewHolder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ConstraintLayout(context).apply {
            val layoutParams1 = RecyclerView.LayoutParams(
                0,
                0
            )
            layoutParams1.setMargins(round(30f*density).toInt(),0,0,0)
            layoutParams = layoutParams1
        })
    }

    // Это уже преобразования элемента (см. onCreateViewHolder) в зависимости от позиции (position)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.constraintLayout.removeAllViews()
        // Обычная карточка
        if (getItem(position).layoutType == null || getItem(position).layoutType == 1 || getItem(position).childs.isEmpty()) {
            val views = createCard(getItem(position).width, getItem(position).height, parent.childsShowName, parent.childsNamePosition, getItem(position).image, getItem(position).name, getItem(position).author, getItem(position).alreadyWatched, getItem(position).length, parent.childsShowAlreadyWatchedLine, context, currentList, parent.childsCornerRadius)
            val layoutparams2 = RecyclerView.LayoutParams(
                if (!getItem(position).showName || getItem(position).showName && getItem(position).namePosition == 1) {
                    views.third.first
                } else {
                    views.second
                },
                views.third.second
            )
            var padding = round(50f*density).toInt()
            if (parent.paddingHorizontal != null) {
                padding = (parent.paddingHorizontal!!.toFloat()*density).toInt()
            }
            var marginBetweenElementsHorizontal = parent.marginBetweenElementsHorizontal
            if (marginBetweenElementsHorizontal == null) {
                marginBetweenElementsHorizontal = round(30f*density).toInt()
            }
            else {
                marginBetweenElementsHorizontal = round(parent.marginBetweenElementsHorizontal!!.toFloat()*density).toInt()
            }
            when (getItem(position).position) {
                0 -> {
                    layoutparams2.setMargins(padding, 0, 0, 0)
                }
                currentList.size - 1 -> {
                    layoutparams2.setMargins(if (position > 0) {if (getItem(position).layoutType == 0) {0} else {marginBetweenElementsHorizontal}} else {marginBetweenElementsHorizontal}, 0, padding, 0)
                }
                else -> {
                    layoutparams2.setMargins(if (position > 0) {if (getItem(position).layoutType == 0) {0} else {marginBetweenElementsHorizontal}} else {marginBetweenElementsHorizontal}, 0, 0, 0)
                }
            }
            holder.constraintLayout.layoutParams = layoutparams2
            for (i in views.first) {
                holder.constraintLayout.addView(i)
            }
            holder.constraintLayout.setOnClickListener {
                clickOnCard(getItem(position))
            }
        }
        // Grid
        else {
            var paddingHorizontal = parent.paddingHorizontal
            if (paddingHorizontal == null) {
                paddingHorizontal = round(50f*density).toInt()
            }
            else {
                paddingHorizontal = round(parent.paddingHorizontal!!.toFloat()*density).toInt()
            }
            val constraintlayout1 = createGridOfChilds(getItem(position).childs, getItem(position).maxObjectsInOneLine, context, (screenWidth-paddingHorizontal*2), parent)
            val layoutparams1 = constraintlayout1.layoutParams as ConstraintLayout.LayoutParams
            layoutparams1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutparams1.setMargins(paddingHorizontal,0,0,0)
            constraintlayout1.layoutParams = layoutparams1
            for (i in 0 until constraintlayout1.childCount) {
                val objData = getItem(position).childs[i]
                val obj = constraintlayout1.getChildAt(i)
                obj.setOnClickListener {
                    clickOnCard(objData)
                }
            }
            val layoutparams2 = holder.constraintLayout.layoutParams as RecyclerView.LayoutParams
            layoutparams2.apply { width = screenWidth ; height = ConstraintLayout.LayoutParams.WRAP_CONTENT }
            layoutparams2.setMargins(0,0,0,0)
            holder.constraintLayout.layoutParams = layoutparams2
            holder.constraintLayout.addView(constraintlayout1)
        }

    }
}
