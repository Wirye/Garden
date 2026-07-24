package com.example.garden.ui.utils.viewExtensions

import android.view.ViewGroup
import android.widget.ImageView
import com.example.garden.ui.customView.OutlinedTextField

fun OutlinedTextField.findIco(): ImageView? {
    var ico: ImageView? = null
    fun find(viewGroup: ViewGroup): ImageView? {
        for (k in 0 until viewGroup.childCount) {
            val obj = viewGroup.getChildAt(k)
            if (obj is ImageView && obj.tag == "ico") {
                return obj
            }
            else if (obj is ViewGroup) {
                val res = find(obj)
                if (res != null) {
                    return res
                }
            }
        }
        return null
    }
    ico = find(this)
    return ico
}