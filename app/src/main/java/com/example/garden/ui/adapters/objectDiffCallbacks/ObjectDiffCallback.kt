package com.example.garden.ui.adapters.objectDiffCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.garden.objectData2

class ObjectDiffCallback : DiffUtil.ItemCallback<objectData2>() {
    override fun areItemsTheSame(oldItem: objectData2, newItem: objectData2): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: objectData2, newItem: objectData2): Boolean {
        return oldItem == newItem
    }
}
