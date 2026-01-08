package com.example.garden_beta

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

data class inerpFormat (
    val speedByX: Float,
    val speedByY: Float,
    val changeableObjListX: MutableList<Int>,
    val changeableObjListY: MutableList<Int>,
)
class scroll(rootView: Int, activity: AppCompatActivity, pages: pages, ids: MutableList<Int>) {
    val activity = activity
    val resources = activity.resources
    val hierarchy = hierarchy(activity)
    val pages = pages
    val workingWithView = workingWithView(activity, hierarchy)
    val objHierarchy = hierarchy.initializeAllViewHierarchy(activity.findViewById<View>(rootView))
    private var scrollInertiaJob: Job? = null
    private var animationJob: Job? = null
    val fonprocess = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Default +
                CoroutineName("FonProcess") +
                CoroutineExceptionHandler { _, expection ->
                    Log.d("ERROR", "Coroutine was stopped: ${expection.message}")
                }
    )
    val fonprocess2 = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Main +
                CoroutineName("FonProcess") +
                CoroutineExceptionHandler { _, expection ->
                    Log.d("ERROR", "Coroutine was stopped: ${expection.message}")
                }
    )
    var xStart = 0f
    var yStart = 0f
    var scrollDirection = ""
    var inerp = inerpFormat(0f,0f,mutableListOf<Int>(), mutableListOf<Int>())
    private var realMargins = workingWithView.realMarginsCreate(ids)
    fun realMarginsInScrollClassUpdate(realMarginsNew: MutableList<realMarginsFormat>) {
        realMargins = realMarginsNew
    }
    fun init(xStartNew: Float, yStartNew: Float) {
        xStart = xStartNew
        yStart = yStartNew
    }
    fun UpdateInerp(newInerp: inerpFormat) {
        inerp = newInerp
    }
    fun resetXYstart() {
        xStart = 0f
        yStart = 0f
    }
    fun scroll(x: Float, y: Float, changeableObjListX: MutableList<Int>, changeableObjListY: MutableList<Int>): String {
        // Get scroll direction
        if ((scrollDirection == "") && ((x != xStart) || (y != yStart))==true) {
            if ((abs(x-xStart) / (abs(y-yStart))) < 1.8f) {
                scrollDirection = "y"
            }
            else {
                scrollDirection="x"
            }
        }
        // Calculate diffrent value and set changeable_obj_list
        val sensivity = 0.5f
        var changeableObjList = mutableListOf<Int>()
        var diffrent = 0.0f
        var idOfFirstObject = 0
        var idOfLastObject = 0
        var marginStartAndEnd = 0
        var amountCards = 0
        if (scrollDirection == "x") {
            diffrent = (x - xStart) * sensivity
            for (i in 0 until changeableObjListX.size) {
                val obj = hierarchy.findInViewHierarchyHelp(objHierarchy[0], changeableObjListX[i])
                if (obj.third == "scrollx_obj") {
                    changeableObjList = obj.second
                }
            }
            if (changeableObjList.isEmpty()) {
                for (i in 0 until changeableObjListX.size) {
                    val objList = get_nearest_scrollx_obj(changeableObjListX[i])
                    if (objList.isNotEmpty()) {
                        changeableObjList = objList
                    }
                }
            }
        }
        else if (scrollDirection == "y") {
            diffrent = (y-yStart)*sensivity
            for (i in 0 until changeableObjListY.size) {
                val obj = hierarchy.findInViewHierarchyHelp(objHierarchy[0], changeableObjListY[i])
                if (obj.third == "scrolly_obj") {
                    changeableObjList = obj.second
                }
            }
            if (changeableObjList.isEmpty()) {
                changeableObjList = get_nearest_scrolly_obj(changeableObjListY[0])
            }
        }
        changeableObjList = pages.sortObjListByPage(changeableObjList)
        if (changeableObjList.isNotEmpty()) {
            // Calculate id of first object and start, end margins
            for (i in 0 until changeableObjList.size) {
                val obj1 = changeableObjList[i]
                val objName1 = activity.resources.getResourceName(obj1)
                if ("1" in objName1) {
                    idOfFirstObject = obj1
                    for (o in 0 until realMargins.size) {
                        val obj2 = realMargins[o]
                        if (obj2.obj == obj1) {
                            if (scrollDirection == "x") {
                                marginStartAndEnd = obj2.marginStartAndEnd
                            }
                            else if (scrollDirection == "y") {
                                marginStartAndEnd = obj2.marginTopAndBottom
                            }
                        }
                    }
                }
            }
            // Calculate amout of objects
            for (i in 0 until changeableObjList.size) {
                try {
                    val k = resources.getResourceName(changeableObjList[i])[resources.getResourceName(changeableObjList[i]).lastIndex].toString().toInt()
                    amountCards = amountCards + 1
                }
                catch (e: NumberFormatException) {
                    Log.d("ERROR", "ITEM ${resources.getResourceName(changeableObjList[i])} DON'T HAVE positionNumber IN HIS ID")
                }
            }
            // Calculate id of last object
            for (i in 0 until changeableObjList.size) {
                val obj = changeableObjList[i]
                val obj_name = resources.getResourceName(obj)
                if ("${amountCards}" in obj_name) {
                    idOfLastObject = obj
                }
            }
            if (idOfLastObject == 0) {
                Log.d("ERROR", "No last object, need to check ids!")
            }

            var parentObjSize = 0
            // Scroll logic
            for (i in 0 until changeableObjList.size) {
                val obj = changeableObjList[i]
                // Calculate scroll direction
                if (scrollDirection == "x") {
                    parentObjSize = activity.findViewById<View>(hierarchy.findInViewHierarchyHelp(objHierarchy[0], obj).first).width
                } else if (scrollDirection == "y") {
                    parentObjSize = activity.findViewById<View>(hierarchy.findInViewHierarchyHelp(objHierarchy[0], obj).first).height
                }
            }
            // Check condition - where we scroll to?
            var scrollToRightOrUp = false
            if (scrollDirection == "x") {
                scrollToRightOrUp = x > xStart
            }
            else if (scrollDirection == "y") {
                scrollToRightOrUp = y > yStart
            }
            var scrollToLeftOrBottom = false
            if (scrollDirection == "x") {
                scrollToLeftOrBottom = x < xStart
            }
            else if (scrollDirection == "y") {
                scrollToLeftOrBottom = y < yStart
            }
            // Calculate values needed to check condition - can we scroll further?
            var firstObjCoord = 0.0f
            var lastObjCoord = 0.0f
            var lastObjSize = 0
            if (scrollDirection == "x") {
                firstObjCoord = activity.findViewById<View>(idOfFirstObject).x
                lastObjCoord = activity.findViewById<View>(idOfLastObject).x
                lastObjSize = activity.findViewById<View>(idOfLastObject).width
            }
            else if (scrollDirection == "y") {
                firstObjCoord = activity.findViewById<View>(idOfFirstObject).y
                lastObjCoord = activity.findViewById<View>(idOfLastObject).y
                lastObjSize = activity.findViewById<View>(idOfLastObject).height
            }

            // Scroll to right or up
            if (scrollToRightOrUp) {
                // If we can't scroll further
                if ((firstObjCoord+diffrent >= marginStartAndEnd)) {
                    if (((lastObjCoord+diffrent) > (parentObjSize - marginStartAndEnd - lastObjSize)) == false) {
                        startState(changeableObjList, scrollDirection, marginStartAndEnd, idOfFirstObject)
                    }
                    if (scrollDirection == "x") {
                        xStart = x
                    }
                    else if (scrollDirection == "y") {
                        yStart = y
                    }
                }
                // If we can scroll further
                else {
                    for (i in 0 until changeableObjList.size) {
                        val obj4 = activity.findViewById<View>(changeableObjList[i])
                        // Set object position
                        if (scrollDirection == "x") {
                            obj4.x=obj4.x+diffrent
                        }
                        else if (scrollDirection == "y") {
                            obj4.y=obj4.y+diffrent
                        }
                    }
                    if (scrollDirection == "x") {
                        xStart = x
                    }
                    else if (scrollDirection == "y") {
                        yStart = y
                    }
                }
            }

            // Scroll to left or bottom
            else if (scrollToLeftOrBottom) {
                // If we can't scroll further
                if ((lastObjCoord+diffrent) <= (parentObjSize - marginStartAndEnd - lastObjSize)) {
                    if (((firstObjCoord)+diffrent < marginStartAndEnd) == false) {
                        endState(changeableObjList, scrollDirection, marginStartAndEnd, idOfLastObject, parentObjSize, lastObjSize, amountCards)
                    }
                    if (scrollDirection == "x") {
                        xStart = x
                    }
                    else if (scrollDirection == "y") {
                        yStart = y
                    }
                }
                // If we can scroll further
                else {
                    for (i in 0 until changeableObjList.size) {
                        val obj5 = activity.findViewById<View>(changeableObjList[i])
                        // Set object position
                        if (scrollDirection == "x") {
                            obj5.x=obj5.x+diffrent
                        }
                        else if (scrollDirection == "y") {
                            obj5.y=obj5.y+diffrent
                        }
                    }
                    if (scrollDirection == "x") {
                        xStart = x
                    }
                    else if (scrollDirection == "y") {
                        yStart = y
                    }
                }
            }
        }
        return scrollDirection
    }

    fun startState(changeableObjList: MutableList<Int>, scrollDirection: String, marginStartAndEnd: Int, idOfFirstObject: Int ) {
        for (o in 0 until changeableObjList.size) {
            val obj = changeableObjList[o]
            val objName = resources.getResourceName(obj)
            // Change first object coord
            if (scrollDirection == "x") {
                activity.findViewById<View>(idOfFirstObject).x=marginStartAndEnd.toFloat()
            }
            else if (scrollDirection == "y") {
                activity.findViewById<View>(idOfFirstObject).y=marginStartAndEnd.toFloat()
            }
            // Change other objects coord
            if (("1" in objName) == false) {
                try {
                    val positionId = objName[objName.lastIndex].toString().toInt()
                    var q = 0.0f
                    for (j in 0 until positionId-1) {
                        // Calculate other objects sizes and margins (needed to calculate object position)
                        for (p in 0 until changeableObjList.size) {
                            val positionId2 = resources.getResourceName(changeableObjList[p])[resources.getResourceName(changeableObjList[p]).lastIndex].toString().toInt()
                            if (positionId2 == j + 1) {
                                if (scrollDirection == "x") {
                                    q = q + activity.findViewById<View>(changeableObjList[p]).width
                                    if (positionId2 == 1) {
                                        q = q + activity.findViewById<View>(obj).marginStart
                                    }
                                    else {
                                        q = q + activity.findViewById<View>(changeableObjList[p]).marginStart
                                    }
                                }
                                else if (scrollDirection == "y") {
                                    q = q + activity.findViewById<View>(changeableObjList[p]).height
                                    if (positionId2 == 1) {
                                        q = q + activity.findViewById<View>(obj).marginTop
                                    }
                                    else {
                                        q = q + activity.findViewById<View>(changeableObjList[p]).marginTop
                                    }
                                }
                            }
                        }
                    }
                    // Set object position
                    if (scrollDirection == "x") {
                        val objj = activity.findViewById<View>(obj)
                        objj.x=(marginStartAndEnd+q)
                    }
                    else if (scrollDirection == "y") {
                        val objj = activity.findViewById<View>(obj)
                        objj.y=(marginStartAndEnd+q)
                    }
                }
                catch (e: Exception) {
                    Log.d("ERROR", "ITEM ${objName} DON'T HAVE positionNumber IN HIS ID")
                }
            }
        }
    }

    fun  endState(changeableObjList: MutableList<Int>, scrollDirection: String, marginStartAndEnd: Int, idOfLastObject: Int, parentObjSize: Int, lastObjSize: Int, amoutCards: Int ) {
        for (o in 0 until changeableObjList.size) {
            val obj = changeableObjList[o]
            val objName = resources.getResourceName(obj)
            // Change last object coord
            if (scrollDirection == "x") {
                activity.findViewById<View>(idOfLastObject).x=(parentObjSize - marginStartAndEnd - lastObjSize).toFloat()
            }
            else if (scrollDirection =="y") {
                activity.findViewById<View>(idOfLastObject).y=(parentObjSize - marginStartAndEnd - lastObjSize).toFloat()
            }
            // Change other objects coord
            if (("${amoutCards}" in objName) == false) {
                try {
                    val positionId = objName[objName.lastIndex].toString().toInt()
                    var q = 0.0f
                    var q1 = 0.0f
                    // Calculate other objects sizes (needed to calculate object position)
                    for (j in 1 until amoutCards-positionId+2) {
                        for (p in 0 until changeableObjList.size) {
                            val obj8 = changeableObjList[p]
                            val positionId2 = resources.getResourceName(obj8)[resources.getResourceName(obj8).lastIndex].toString().toInt()
                            if (positionId2 == amoutCards-j+1-1+1) {
                                if (scrollDirection == "x") {
                                    q = q + activity.findViewById<View>(obj8).width
                                }
                                else if (scrollDirection == "y") {
                                    q = q + activity.findViewById<View>(obj8).height
                                }
                            }
                        }
                    }
                    // Calculate other objects margins (needed to calculate object position)
                    for (j in 1 until amoutCards-positionId+1) {
                        for (p in 0 until changeableObjList.size) {
                            val obj8 = changeableObjList[p]
                            val positionId2 = resources.getResourceName(obj8)[resources.getResourceName(obj8).lastIndex].toString().toInt()
                            if (positionId2 == amoutCards-j+1) {
                                if (scrollDirection == "x") {
                                    q1 = q1 + activity.findViewById<View>(obj8).marginStart
                                }
                                else if (scrollDirection == "y") {
                                    q1 = q1 + activity.findViewById<View>(obj8).marginTop
                                }
                            }
                        }
                    }
                    // Set object position
                    if (scrollDirection == "x") {
                        val objj = activity.findViewById<View>(obj)
                        objj.x=parentObjSize - marginStartAndEnd - q - q1
                    }
                    else if (scrollDirection == "y") {
                        val objj = activity.findViewById<View>(obj)
                        objj.y=parentObjSize - marginStartAndEnd - q - q1
                    }
                }
                catch (e: IndexOutOfBoundsException) {
                    Log.d("ERROR", "ITEM ${objName} DON'T HAVE positionNumber IN HIS ID")
                }
            }
        }
    }

    fun get_nearest_scrolly_obj (obj: Int): MutableList<Int> {
        var res = mutableListOf<Int>()
        if (obj != R.id.main) {
            var parentObj = hierarchy.findInViewHierarchyHelp(objHierarchy[0],obj).first
            var parentObjName = resources.getResourceName(parentObj)
            while ("scrolly" in parentObjName == false) {
                parentObj = hierarchy.findInViewHierarchyHelp(objHierarchy[0], parentObj).first
                parentObjName = resources.getResourceName(parentObj)
            }
            res = hierarchy.findInViewHierarchyHelp(objHierarchy[0], parentObj).second
        }
        return res
    }

    fun get_nearest_scrollx_obj(obj: Int): MutableList<Int> {
        var res = mutableListOf<Int>()
        val objj = activity.findViewById<View>(obj)
        if ( objj is ViewGroup) {
            for (i in 0 until objj.childCount) {
                val childName = resources.getResourceName(objj.getChildAt(i).id)
                if ("scrollx" in childName) {
                    res = hierarchy.findInViewHierarchyHelp(objHierarchy[0], objj.getChildAt(i).id).second
                    break
                }
            }
        }
        return res
    }

    fun inerp_after_scroll_start() {
        scrollInertiaJob?.cancel()
        scrollInertiaJob = fonprocess2.launch {
            try {
                var velo = 0f
                val k = 0.98f // coefficient of friction
                if (inerp.speedByX != 0f) {
                    velo = inerp.speedByX
                }
                else {
                    velo = inerp.speedByY
                }
                while (velo > 5f || velo < -5f) {
                    if (inerp.changeableObjListX.isNotEmpty()) {
                        scroll(((xStart + velo)), yStart, inerp.changeableObjListX, inerp.changeableObjListY)
                    }
                    else if (inerp.changeableObjListY.isNotEmpty()) {
                        scroll(xStart, ((yStart + velo)), inerp.changeableObjListX, inerp.changeableObjListY)
                    }
                    velo = velo * k
                    delay(16)
                }
            } catch (e: CancellationException) {
                Log.d("STOPED", "STOPED")
                scrollDirection = ""
            }
            catch (e: Exception) {
                Log.d("ERROR", "${e}")
                scrollDirection = ""
            }
            finally {
                Log.d("STOPED", "FINNALY")
                scrollDirection = ""
            }
        }
    }

    fun inerp_after_scroll_stop() {
        scrollInertiaJob?.cancel()
    }
}

