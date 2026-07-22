package com.example.garden.ui.utils.viewExtensions

import android.view.ViewGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.findTextInputEditText(): TextInputEditText? {
    var res: TextInputEditText? = null
    for (k in 0 until this.childCount) {
        val obj = this.getChildAt(k)
        if (obj is TextInputEditText) {
            res = obj
            break
        }
        else if (obj is ViewGroup) {
            for (h in 0 until obj.childCount) {
                val objj = obj.getChildAt(h)
                if (objj is TextInputEditText) {
                    res = objj
                    break
                }
            }
        }
    }
    return res
}