package com.example.garden_beta

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
data class viewHierarchy (
    val obj: Int,
    val baseObjList: MutableList<MutableList<viewHierarchy>>,
    val scrollxObjList: MutableList<MutableList<viewHierarchy>>,
    val scrollyObjList: MutableList<MutableList<viewHierarchy>>,
)
class hierarchy(activity: AppCompatActivity) {
    val resources = activity.resources
    val activity = activity
    fun initializeAllViewHierarchy(rootView: View): MutableList<viewHierarchy> {
        val baseObjList = mutableListOf<MutableList<viewHierarchy>>()
        val scrollxObjList = mutableListOf<MutableList<viewHierarchy>>()
        val scrollyObjList = mutableListOf<MutableList<viewHierarchy>>()
        if (rootView.id != View.NO_ID) {
            if (rootView is ViewGroup) {
                for (i in 0 until rootView.childCount) {
                    val viewHierarchy = initializeViewHierarchy(rootView.getChildAt(i))
                    if ("scrollx" in resources.getResourceName(rootView.getChildAt(i).id)) {
                        scrollxObjList.add(viewHierarchy)
                    }
                    else if ("scrolly" in resources.getResourceName(rootView.getChildAt(i).id)) {
                        scrollyObjList.add(viewHierarchy)
                    }
                    else {
                        baseObjList.add(viewHierarchy)
                    }
                }
            }
        }
        val res = mutableListOf<viewHierarchy>(viewHierarchy(rootView.id, baseObjList, scrollxObjList, scrollyObjList))
        return res
    }
    fun initializeViewHierarchy(rootView: View): MutableList<viewHierarchy> {
        val baseObjList = mutableListOf<MutableList<viewHierarchy>>()
        val scrollxObjList = mutableListOf<MutableList<viewHierarchy>>()
        val scrollyObjList = mutableListOf<MutableList<viewHierarchy>>()

        if (rootView is ViewGroup) {
            for (i in 0 until rootView.childCount) {
                val childViewHierarchy = initializeViewHierarchy(rootView.getChildAt(i))

                if ("scrollx" in resources.getResourceName(rootView.getChildAt(i).id)) {
                    scrollxObjList.add(childViewHierarchy)
                }
                else if ("scrolly" in resources.getResourceName(rootView.getChildAt(i).id)) {
                    scrollyObjList.add(childViewHierarchy)
                }
                else {
                    baseObjList.add(childViewHierarchy)
                }
            }
        }
        val res = mutableListOf<viewHierarchy>(viewHierarchy(rootView.id, baseObjList, scrollxObjList, scrollyObjList))
        return res
    }

    fun findInViewHierarchyHelp(parentObj: viewHierarchy, findingObj: Int): Triple<Int, MutableList<Int>, String> {
        var parentObjId = -1
        var findingObjList = mutableListOf<Int>()
        var whatType = ""

        // Here checks for all obj attributes (NEEDS IN UPDATE, WHEN NEW ATTRIBUTE ADDED)

        // base obj (obj without attributes)
        if (parentObj.baseObjList.isNotEmpty()) {
            for (i in 0 until parentObj.baseObjList.size) {
                val a = parentObj.baseObjList[i][0]
                if (a.obj == findingObj) {
                    parentObjId = parentObj.obj
                    findingObjList = mutableListOf<Int>(a.obj)
                    whatType = ""
                }
                else {
                    if (findInViewHierarchyHelp(a, findingObj).second.isNotEmpty()) {
                        parentObjId = findInViewHierarchyHelp(a, findingObj).first
                        for (o in 0 until findInViewHierarchyHelp(a, findingObj).second.size) {
                            findingObjList.add(findInViewHierarchyHelp(a, findingObj).second[o])
                        }
                        whatType = findInViewHierarchyHelp(a, findingObj).third
                        break
                    }
                }
            }
        }

        // scrollx obj (may be scrolled by x)
        if (parentObj.scrollxObjList.isNotEmpty()) {
            for (i in 0 until parentObj.scrollxObjList.size) {
                val a = parentObj.scrollxObjList[i][0]
                if (a.obj == findingObj) {
                    parentObjId = parentObj.obj
                    for (o in 0 until parentObj.scrollxObjList.size) {
                        findingObjList.add(parentObj.scrollxObjList[o][0].obj)
                    }
                    whatType = "scrollx_obj"
                    break
                }
                else {
                    if (findInViewHierarchyHelp(a, findingObj).second.isNotEmpty()) {
                        parentObjId = findInViewHierarchyHelp(a, findingObj).first
                        for (o in 0 until findInViewHierarchyHelp(a, findingObj).second.size) {
                            findingObjList.add(findInViewHierarchyHelp(a, findingObj).second[o])
                        }
                        whatType = findInViewHierarchyHelp(a, findingObj).third
                        break
                    }
                }
            }
        }
        // scrolly obj (may be scrolled by y)
        if (parentObj.scrollyObjList.isNotEmpty()) {
            for (i in 0 until parentObj.scrollyObjList.size) {
                val a = parentObj.scrollyObjList[i][0]
                if (a.obj == findingObj) {
                    parentObjId = parentObj.obj
                    for (o in 0 until parentObj.scrollyObjList.size) {
                        findingObjList.add(parentObj.scrollyObjList[o][0].obj)
                    }
                    whatType = "scrolly_obj"
                    break
                }
                else {
                    if (findInViewHierarchyHelp(a, findingObj).second.isNotEmpty()) {
                        parentObjId = findInViewHierarchyHelp(a, findingObj).first
                        for (o in 0 until findInViewHierarchyHelp(a, findingObj).second.size) {
                            findingObjList.add(findInViewHierarchyHelp(a, findingObj).second[o])
                        }
                        whatType = findInViewHierarchyHelp(a, findingObj).third
                        break
                    }
                }
            }
        }

        val res = Triple(parentObjId, findingObjList, whatType)
        return res
    }
}

