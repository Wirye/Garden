package com.example.garden.ui.utils.viewExtensions

import android.view.ViewGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.findTextInputEditText(): TextInputEditText? {
    var res: TextInputEditText? = null
    fun find(viewGroup: ViewGroup): TextInputEditText? {
        for (k in 0 until viewGroup.childCount) {
            val obj = viewGroup.getChildAt(k)
            if (obj is TextInputEditText) {
                return obj
            }
            else if (obj is ViewGroup) {
                val ress = find(obj)
                if (ress != null) {
                    return ress
                }
            }
        }
        return null
    }
    res = find(this)
    return res
}