package com.example.garden_beta

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.example.garden_beta.MainActivity.listOfObjNeedToScaleOptimizateFormat
import kotlin.math.min
import kotlin.math.round

data class objectsSizeFormat (
    val obj: Int,
    val width: Int,
    val height: Int,
    val marginStart: Int,
    val marginEnd: Int,
    val marginTop: Int,
    val marginBottom: Int,
)
data class realMarginsFormat (
    val obj: Int,
    val marginStartAndEnd: Int,
    val marginTopAndBottom: Int,
)

data class realMarginsStateFormat(
    val state: String,
    val realMargins: MutableList<realMarginsFormat>
)

class workingWithView(activity: AppCompatActivity, hierarchy: hierarchy) {
    val activity = activity
    val resources = activity.resources
    val hierarchy = hierarchy(activity)
    val objHierarchy = hierarchy.initializeAllViewHierarchy(activity.findViewById<View>(R.id.main))
    val objectsSizeList = mutableListOf<objectsSizeFormat>()
    val realMarginsStates = mutableListOf<realMarginsStateFormat>()
    fun realMarginsCreateState(ids: MutableList<Int>, state: String) {
        // Get new x and y (margins from rootview)
        val realMargins = mutableListOf<realMarginsFormat>()
        for (i in 0 until ids.size) {
            val obj = ids[i]
            val x = activity.findViewById<View>(obj).x
            val y = activity.findViewById<View>(obj).y
            realMargins.add(realMarginsFormat(obj, x.toInt(), y.toInt()))
        }
        realMarginsStates.add(realMarginsStateFormat(state, realMargins))
    }

    fun getRealMarginsState(state: String): MutableList<realMarginsFormat> {
        var res = mutableListOf<realMarginsFormat>()
        for (i in 0 until realMarginsStates.size) {
            val obj = realMarginsStates[i]
            if (obj.state == state) {
                res = obj.realMargins
            }
        }
        return res
    }
    fun initObjectsSizeList(ids: MutableList<Int>) {
        for (i in 0 until ids.size) {
            if (findInObjectsSizeList(ids[i]).obj == 0) {
                val objWidth = activity.findViewById<View>(ids[i]).width
                val objHeigth = activity.findViewById<View>(ids[i]).height
                val objMarginStart = activity.findViewById<View>(ids[i]).marginStart
                val objMarginEnd = activity.findViewById<View>(ids[i]).marginEnd
                val objMarginTop = activity.findViewById<View>(ids[i]).marginTop
                val objMarginBottom = activity.findViewById<View>(ids[i]).marginBottom
                val res = objectsSizeFormat(ids[i], objWidth, objHeigth, objMarginStart, objMarginEnd, objMarginTop, objMarginBottom)
                objectsSizeList.add(res)
            }
        }
    }
    fun findInObjectsSizeList(obj: Int): objectsSizeFormat {
        var res = objectsSizeFormat(0,0,0,0,0,0,0)
        for (i in 0 until objectsSizeList.size) {
            val objj = objectsSizeList[i]
            if (objj.obj == obj) {
                res = objj
            }
        }
        return res
    }

    fun scaleOptimization(listOfObjNeedToScaleOptimizate: MutableList<listOfObjNeedToScaleOptimizateFormat>, baseScreenWidth: Int, screenWidth: Int) {
        for (i in 0 until listOfObjNeedToScaleOptimizate.size) {
            val obj = listOfObjNeedToScaleOptimizate[i].obj
            val whatChange = listOfObjNeedToScaleOptimizate[i].whatChange
            val considerOthers = listOfObjNeedToScaleOptimizate[i].considerOthers
            val mode = listOfObjNeedToScaleOptimizate[i].mode
            if (considerOthers) {
                val listOfObj = hierarchy.findInViewHierarchyHelp(objHierarchy[0], obj).second
                for (o in 0 until listOfObj.size) {
                    when(whatChange) {
                        "scale" -> {
                            if (mode == "limited") {
                                activity.findViewById<View>(listOfObj[o]).layoutParams=activity.findViewById<View>(listOfObj[o]).layoutParams.apply { width=cardScaleCalc(listOfObj[o], screenWidth.toFloat(), "limited").first; height= cardScaleCalc(listOfObj[o], screenWidth.toFloat(), "limited").second}
                            }
                            else if (mode == "very limited") {
                                cardScaleCalc(listOfObj[o], screenWidth.toFloat(), "very limited")
                            }
                        }
                        "margin_start" -> activity.findViewById<View>(listOfObj[o]).layoutParams=setMargin(activity.findViewById<View>(listOfObj[o]).layoutParams as ViewGroup.MarginLayoutParams, findInObjectsSizeList(listOfObj[o]).marginStart.toFloat(), baseScreenWidth.toFloat(), screenWidth.toFloat(), "start")
                        "margin_end" -> activity.findViewById<View>(listOfObj[o]).layoutParams=setMargin(activity.findViewById<View>(listOfObj[o]).layoutParams as ViewGroup.MarginLayoutParams, findInObjectsSizeList(listOfObj[o]).marginEnd.toFloat(), baseScreenWidth.toFloat(), screenWidth.toFloat(), "end")
                        "margin_top" -> activity.findViewById<View>(listOfObj[o]).layoutParams=setMargin(activity.findViewById<View>(listOfObj[o]).layoutParams as ViewGroup.MarginLayoutParams, findInObjectsSizeList(listOfObj[o]).marginTop.toFloat(), baseScreenWidth.toFloat(), screenWidth.toFloat(), "top")
                        "margin_bottom" -> activity.findViewById<View>(listOfObj[o]).layoutParams=setMargin(activity.findViewById<View>(listOfObj[o]).layoutParams as ViewGroup.MarginLayoutParams, findInObjectsSizeList(listOfObj[o]).marginBottom.toFloat(), baseScreenWidth.toFloat(), screenWidth.toFloat(), "bottom")
                    }
                }
            }
            else {
                when(whatChange) {
                    "scale" -> {
                        if (mode == "limited") {
                            activity.findViewById<View>(obj).layoutParams=activity.findViewById<View>(obj).layoutParams.apply { width=cardScaleCalc(obj, screenWidth.toFloat(), "limited").first; height= cardScaleCalc(obj, screenWidth.toFloat(), "limited").second}
                        }
                        else if (mode == "very limited") {
                            cardScaleCalc(obj, screenWidth.toFloat(), "very limited")
                        }
                    }
                    "margin_start" -> activity.findViewById<View>(obj).layoutParams=setMargin(activity.findViewById<View>(obj).layoutParams as ViewGroup.MarginLayoutParams, findInObjectsSizeList(obj).marginStart.toFloat(), baseScreenWidth.toFloat(), screenWidth.toFloat(), "start")
                    "margin_end" -> activity.findViewById<View>(obj).layoutParams=setMargin(activity.findViewById<View>(obj).layoutParams as ViewGroup.MarginLayoutParams, findInObjectsSizeList(obj).marginEnd.toFloat(), baseScreenWidth.toFloat(), screenWidth.toFloat(), "end")
                    "margin_top" -> activity.findViewById<View>(obj).layoutParams=setMargin(activity.findViewById<View>(obj).layoutParams as ViewGroup.MarginLayoutParams, findInObjectsSizeList(obj).marginTop.toFloat(), baseScreenWidth.toFloat(), screenWidth.toFloat(), "top")
                    "margin_bottom" -> activity.findViewById<View>(obj).layoutParams=setMargin(activity.findViewById<View>(obj).layoutParams as ViewGroup.MarginLayoutParams, findInObjectsSizeList(obj).marginBottom.toFloat(), baseScreenWidth.toFloat(), screenWidth.toFloat(), "bottom")
                }
            }
        }
        // Wait for changes
        activity.findViewById<View>(R.id.main).post {

            // Scale optimization for view groups
            for (i in 0 until listOfObjNeedToScaleOptimizate.size) {
                val obj = listOfObjNeedToScaleOptimizate[i].obj
                val whatChange = listOfObjNeedToScaleOptimizate[i].whatChange
                val considerOthers = listOfObjNeedToScaleOptimizate[i].considerOthers
                if (considerOthers) {
                    val listOfObj = hierarchy.findInViewHierarchyHelp(objHierarchy[0], obj).second
                    for (o in 0 until listOfObj.size) {
                        when(whatChange) {
                            "viewgroup_scale" -> activity.findViewById<View>(listOfObj[o]).layoutParams=activity.findViewById<View>(listOfObj[o]).layoutParams.apply { height=viewgroupScaleCalc(listOfObj[o]) }
                        }
                    }
                }
                else {
                    when(whatChange) {
                        "viewgroup_scale" -> activity.findViewById<View>(obj).layoutParams=activity.findViewById<View>(obj).layoutParams.apply { height=viewgroupScaleCalc(obj) }
                    }
                }
            }
        }
    }

    fun pr(objectValue: Float, baseScreenValue: Float, screenValue: Float): Float {
        val res = screenValue * (objectValue/baseScreenValue)
        return res
    }

    fun setMargin(obj: ViewGroup.MarginLayoutParams, objectValue: Float, baseScreenValue: Float, screenValue: Float, whereMargin: String): ViewGroup.MarginLayoutParams {
        when(whereMargin){
            "start" -> obj.marginStart=pr(objectValue, baseScreenValue, screenValue).toInt()
            "end" -> obj.marginEnd=pr(objectValue, baseScreenValue, screenValue).toInt()
            "top" -> obj.setMargins(0,pr(objectValue, baseScreenValue, screenValue).toInt(), 0, 0)
            "bottom" -> obj.setMargins(0,0, 0, pr(objectValue, baseScreenValue, screenValue).toInt())
        }
        return obj
    }

    fun cardScaleCalc(obj: Int, screenWidth: Float, mode: String): Pair<Int, Int> {
        // Modes:
        // free - doesn't change objects relative (by other objects) size and doesn't change constraints
        // limited - changes objects relative (by other objects) width (to fit they within certain limits) doesn't change constraints
        // very limited changes objects relative (by other objects) width (to fit they within certain limits) and change constraints     (FOR "VERY LIMITED" MODE YOU SHOULDN'T CALL CONSIDER OTHERS!)
        // to fit they within certain limits - place the calculated number of cards in the area in the middle of the screen width and less than it by marginStartAndEnd * 2
        // change constraints - places objects as a table with amountCards columns and N rows (all margins start become = marginStartAndEnd (first card margin start))

        var changeableObjList = mutableListOf<Int>()
        var margin = activity.findViewById<View>(obj).marginStart
        // Calculate marginStartAndEnd
        var marginStartAndEnd = findInObjectsSizeList(obj).marginStart
        var baseFirstCardWidth = findInObjectsSizeList(obj).width
        val q = hierarchy.findInViewHierarchyHelp(objHierarchy[0], obj).second
        for (i in 0 until q.size) {
            val obj = q[i]
            val objName = resources.getResourceName(obj)
            if ("card" in objName) {
                changeableObjList.add(obj)
            }
        }
        val newChangeableObjList = mutableListOf<Int>()
        var a = 0
        for (i in 0 until changeableObjList.size) {
            a = a + 1
            for (i in 0 until changeableObjList.size) {
                val obj = changeableObjList[i]
                val objName = resources.getResourceName(obj)
                if ("${a}" in objName) {
                    newChangeableObjList.add(obj)
                }
            }
        }
        changeableObjList = newChangeableObjList
        if (changeableObjList.size > 1) {
            for (i in 0 until changeableObjList.size) {
                val objj = changeableObjList[i]
                val objjName = resources.getResourceName(objj)
                if ("1" in objjName) {
                    marginStartAndEnd = findInObjectsSizeList(objj).marginStart
                    baseFirstCardWidth = findInObjectsSizeList(objj).width
                }
                // If obj is first obj, we get margin of second obj
                if ("1" in resources.getResourceName(obj) && ("2" in objjName)) {
                    margin = findInObjectsSizeList(objj).marginStart
                }
            }
        }

        // Calculate new width and height
        // RES - width
        // RES2 - height
        val baseCardWidth = findInObjectsSizeList(obj).width
        val baseCardHeight = findInObjectsSizeList(obj).height
        var amountCards = 0.0f
        var res = 0
        var res2 = 0
        when(mode) {
            "free" -> {
                // Working in progress
            }
            "limited" -> {
                // For big cards (like activity_main_music_homepage_carousel_scrolly_2 cards)
                if (baseCardWidth >= 840) {
                    amountCards = round(((screenWidth-((marginStartAndEnd*3)+marginStartAndEnd-margin))/baseFirstCardWidth))
                    res = round(((screenWidth - (marginStartAndEnd*3) - (margin * (amountCards-1))) / amountCards)).toInt()
                }
                // For small or normal cards (like activity_main_anime_homepage_carousel_scrolly_1 cards)
                else {
                    amountCards = round((screenWidth-(marginStartAndEnd + (marginStartAndEnd - margin)))/baseFirstCardWidth)
                    res = round(((screenWidth - (marginStartAndEnd*2) - (margin * (amountCards-1))) / amountCards)).toInt()
                }
                res2 = round((res.toFloat() * (baseCardHeight.toFloat() / baseCardWidth.toFloat()))).toInt()
            }
            "very limited" -> {
                amountCards = round((screenWidth-(marginStartAndEnd))/baseFirstCardWidth)
                res = round(((screenWidth - (marginStartAndEnd*2) - (marginStartAndEnd * (amountCards-1))) / amountCards)).toInt()
                res2 = round((res.toFloat() * (baseCardHeight.toFloat() / baseCardWidth.toFloat()))).toInt()
                var k = 0
                for (i in 0 until changeableObjList.size) {
                    val obj = changeableObjList[i]
                    activity.findViewById<View>(obj).layoutParams = activity.findViewById<View>(obj).layoutParams.apply { width = res; height = res2 }
                    var previousObject = 0
                    if (i > 0) {
                        previousObject = changeableObjList[i-1]
                    }
                    else {
                        previousObject = hierarchy.findInViewHierarchyHelp(objHierarchy[0], obj).first
                    }
                    if (k < amountCards) {
                        val objView = activity.findViewById<View>(obj)
                        val params = objView.layoutParams as ConstraintLayout.LayoutParams

                        if (activity.findViewById<View>(previousObject) != objView.parent) {
                            params.startToStart = ConstraintLayout.LayoutParams.UNSET
                            params.startToEnd = previousObject
                            params.topToTop = previousObject
                            params.topToBottom = ConstraintLayout.LayoutParams.UNSET
                            params.setMargins(0, 0, 0 ,0)
                        }
                        params.marginStart = marginStartAndEnd
                        objView.layoutParams = params
                    }
                    if (k == amountCards.toInt()) {
                        val objView = activity.findViewById<View>(obj)
                        val params = objView.layoutParams as ConstraintLayout.LayoutParams
                        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        params.startToEnd = ConstraintLayout.LayoutParams.UNSET
                        params.topToTop = ConstraintLayout.LayoutParams.UNSET
                        params.topToBottom = previousObject
                        params.setMargins(0, marginStartAndEnd, 0 ,0)
                        params.marginStart = marginStartAndEnd
                        objView.layoutParams = params
                        k = 0
                    }
                    k = k + 1
                }
            }
        }
        return Pair(res,res2)
    }

    fun viewgroupScaleCalc(obj: Int): Int {
        val viewgroup = activity.findViewById<View>(obj)
        var res = -1
        if (viewgroup is ViewGroup) {
            var childWithLowestY = -1
            var lowestY = 0
            var objsWithLowestYHeight = 0
            for (i in 0 until viewgroup.childCount) {
                val child = viewgroup.getChildAt(i)
                val childY = child.y
                val childHeight = child.height
                if (childY+childHeight > lowestY+objsWithLowestYHeight) {
                    childWithLowestY = child.id
                    lowestY = childY.toInt()
                    objsWithLowestYHeight = childHeight
                }
            }
            if (childWithLowestY != -1) {
                res = lowestY + objsWithLowestYHeight
            }
        }
        return res
    }
}