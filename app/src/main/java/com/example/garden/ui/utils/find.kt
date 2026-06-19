package com.example.garden.ui.utils

import com.example.garden.Layer
import com.example.garden.layersList
import com.example.garden.objectData2

fun findVideoPlayerLayer(): Int? {
    var res: Int? = null
    for (i in 0 until layersList.size) {
        if (layersList[i] is Layer.VideoPlayer) {
            res = i
        }
    }
    return res
}
fun findObjectByIdInList(id: Long, list: List<objectData2>): objectData2? {
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
fun findMainPageLayerByPageId(pageId: Int): Layer? {
    return layersList.filterIsInstance<Layer.MainPage>().find { it.pageId == pageId }
}
fun findLayerByLayerObjectId(objId: Long): Pair<Layer?, Int> {
    val res: Pair<Layer?, Int> = Pair(null, -1)
    for (i in 0 until layersList.size) {
        val obj = layersList[layersList.lastIndex-i]
        when (obj) {
            is Layer.AnimePage -> {
                if (obj.layoutObjId == objId) {
                    return Pair(obj, layersList.size-1-i)
                }
            }
            else -> {}
        }
    }
    return res
}
fun findLayerByElevation(elevation: Int): Layer? {
    val res: Layer? = null
    for (i in layersList) {
        when (i) {
            is Layer.AnimePage -> {
                if (i.elevation == elevation) {
                    return i
                }
            }
            is Layer.MainPage -> {
                if (i.elevation == elevation) {
                    return i
                }
            }
            is Layer.OverLay -> {
                if (i.elevation == elevation) {
                    return i
                }
            }
            is Layer.VideoPlayer -> {
                if (i.elevation == elevation) {
                    return i
                }
            }
        }
    }
    return res
}