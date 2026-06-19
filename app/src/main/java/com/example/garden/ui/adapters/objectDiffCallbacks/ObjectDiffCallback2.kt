package com.example.garden.ui.adapters.objectDiffCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.garden.ui.adapters.animePageSezonsAdapterListFormat

class ObjectDiffCallback2 : DiffUtil.ItemCallback<animePageSezonsAdapterListFormat>() {
    override fun areItemsTheSame(p0: animePageSezonsAdapterListFormat, p1: animePageSezonsAdapterListFormat): Boolean {
        return p0.obj.id == p1.obj.id
    }

    override fun areContentsTheSame(p0: animePageSezonsAdapterListFormat, p1: animePageSezonsAdapterListFormat): Boolean {
        return p0 == p1
    }
}