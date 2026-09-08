package com.example.garden.ui.utils

import com.example.garden.database.ObjectData2

fun findObjectByIdInList(id: Long, list: List<ObjectData2>): ObjectData2? {
    for (i in list) {
        if (i.id == id) {
            return i
        }
        if (i.childs.isNotEmpty()) {
            val res = findObjectByIdInList(id, i.childs)
            if (res != null) {
                return res
            }
        }
    }
    return null
}
