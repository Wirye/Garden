
package com.example.garden_beta

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.garden_beta.databinding.ActivityMainBinding
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import androidx.core.view.VelocityTrackerCompat.addMovement
import androidx.core.view.VelocityTrackerCompat.computeCurrentVelocity
import kotlin.collections.mutableListOf
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var hierarchy: hierarchy
    private lateinit var scroll: scroll
    private lateinit var pages: pages
    private lateinit var workingWithView: workingWithView
    private var ids =  mutableListOf<Int>()
    private var objHierarchy = mutableListOf<viewHierarchy>()
    private var realMargins = mutableListOf<realMarginsFormat>()
    private var clickableObjectsList = mutableListOf<Int>()
    private var screenWidth = 0
    private var screenHeight = 0
    data class listOfObjNeedToScaleOptimizateFormat (
        val obj: Int,
        val whatChange: String,
        val considerOthers: Boolean,
    )
    private var homePageObjects = mutableListOf<Int>()
    private var animePageObjects = mutableListOf<Int>()
    private var mangaPageObjects = mutableListOf<Int>()
    private var musicPageObjects = mutableListOf<Int>()
    private var downloadPageObjects = mutableListOf<Int>()
    private var settingsPageObjects = mutableListOf<Int>()
    private var searchPageObjects = mutableListOf<Int>()
    private var accountPageObjects = mutableListOf<Int>()
    private var listOfObjNeedToScaleOptimizate = mutableListOf<listOfObjNeedToScaleOptimizateFormat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        hierarchy = hierarchy(this)
        setContentView(binding.root)
        val baseScreenWidth = 1200
        val baseScreenHeight = 2652
        var alreadyChanged = false
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            screenWidth = binding.main.width
            screenHeight = binding.main.height
            // Ids and obj_hierarchy lists init and scale optimization
            if (alreadyChanged == false) {
                ids = getAllViewIds(binding.main, ids)
                objHierarchy = hierarchy.initializeAllViewHierarchy(findViewById<View>(ids[0]))
                clickableObjectsList = createClickableObjectsList(ids)

                // Show needed information
                Log.d("all ids", ids.toString())
                Log.d("hierarchy", objHierarchy.toString())
                for (i in 0 until ids.size) {
                    Log.d("id", resources.getResourceName(ids[i]).toString())
                }

                // IMPORTANT! List of obj that need to scale optimization
                listOfObjNeedToScaleOptimizate = mutableListOf<listOfObjNeedToScaleOptimizateFormat>(
                    listOfObjNeedToScaleOptimizateFormat(binding.activityMainHomeButton.id, "margin_start", false),
                    listOfObjNeedToScaleOptimizateFormat(binding.activityMainSettingsButton.id, "margin_start", false),
                    listOfObjNeedToScaleOptimizateFormat(binding.activityMainSearchButton.id, "margin_end", false),
                    listOfObjNeedToScaleOptimizateFormat(binding.activityMainDownloadButton.id, "margin_end",false),
                    listOfObjNeedToScaleOptimizateFormat(binding.activityMainAccountButton.id, "margin_end",false),
                    listOfObjNeedToScaleOptimizateFormat(binding.activityMainAnimeHomepageCarouselScrolly1.id,"margin_top", true),
                    listOfObjNeedToScaleOptimizateFormat(binding.activityMainAnimeCarouselCardScrollx1.id,"scale", true),
                    listOfObjNeedToScaleOptimizateFormat(binding.activityMainMusicCarouselCardScrollx1.id,"scale", true),
                    listOfObjNeedToScaleOptimizateFormat(binding.activityMainMangaCarouselCardScrollx1.id,"scale", true),
                    listOfObjNeedToScaleOptimizateFormat(binding.activityMainContinuewatchingCarouselCardScrollx1.id,"scale", true),
                    listOfObjNeedToScaleOptimizateFormat(binding.activityMainAnimeHomepageCarouselScrolly1.id,"viewgroup_scale", true),
                )
                workingWithView = workingWithView(this, hierarchy)

                pages = pages(this)
                pages.createListsOfPagesObjects(ids)
                homePageObjects = pages.homePageObjects
                animePageObjects = pages.animePageObjects
                mangaPageObjects = pages.mangaPageObjects
                musicPageObjects = pages.musicPageObjects
                downloadPageObjects = pages.downloadPageObjects
                settingsPageObjects = pages.settingsPageObjects
                searchPageObjects = pages.searchPageObjects
                accountPageObjects = pages.accountPageObjects
                pages.switchPageTo("home")
                workingWithView.initObjectsSizeList(ids)
                scroll = scroll(binding.root.id, this, pages, ids)
                // Scale optimization
                workingWithView.scaleOptimization(listOfObjNeedToScaleOptimizate, baseScreenWidth, screenWidth)
                binding.root.post {
                    binding.main.requestLayout()
                    binding.main.post {
                        realMargins = workingWithView.getRealMarginsState("home")
                        if (realMargins.isEmpty()) {
                            workingWithView.realMarginsCreateState(ids, "home")
                        }
                        realMargins = workingWithView.getRealMarginsState("home")
                        scroll.realMarginsInScrollClassUpdate(realMargins)
                    }
                }
                alreadyChanged = true
            }
        }
        binding.activityMainAnimeButton.setOnClickListener {
            scroll.inerp_after_scroll_stop()
            pages.switchPageTo("anime")
            workingWithView.scaleOptimization(listOfObjNeedToScaleOptimizate, baseScreenWidth, screenWidth)
            binding.main.post {
                binding.main.requestLayout()
                binding.main.post {
                    realMargins = workingWithView.getRealMarginsState("anime")
                    if (realMargins.isEmpty()) {
                        workingWithView.realMarginsCreateState(ids, "anime")
                    }
                    realMargins = workingWithView.getRealMarginsState("anime")
                    scroll.realMarginsInScrollClassUpdate(realMargins)
                }
            }
        }
        binding.activityMainHomeButton.setOnClickListener {
            scroll.inerp_after_scroll_stop()
            pages.switchPageTo("home")
            workingWithView.scaleOptimization(listOfObjNeedToScaleOptimizate, baseScreenWidth, screenWidth)
            binding.main.post {
                binding.main.requestLayout()
                binding.main.post {
                    realMargins = workingWithView.getRealMarginsState("home")
                    if (realMargins.isEmpty()) {
                        workingWithView.realMarginsCreateState(ids, "home")
                    }
                    realMargins = workingWithView.getRealMarginsState("home")
                    scroll.realMarginsInScrollClassUpdate(realMargins)
                }
            }
        }
    }
    private var touched = false
    private var isTouchableObjTouched = false
    private var scrollDirection = ""
    private var changeableObjList = mutableListOf<Int>()
    private var changeableObjListX = mutableListOf<Int>()
    private var changeableObjListY = mutableListOf<Int>()
    private var objType = ""
    private var allCaruselsGeted = false
    private var inerp = inerpFormat(0f,0f, mutableListOf<Int>(), mutableListOf<Int>())
    private var mVelocityTracker: VelocityTracker? = null
    private var clickListener = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scroll.inerp_after_scroll_stop()
        val x = event.x
        val y = event.y
        var scrollDirection = ""
        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                clickListener = true
                scroll.inerp_after_scroll_stop()
                mVelocityTracker?.clear()
                mVelocityTracker = mVelocityTracker ?: VelocityTracker.obtain()
                mVelocityTracker?.addMovement(event)
                scroll.init(x, y)
                isTouchableObjTouched = true
                changeableObjList = whatElement(scroll.xStart, scroll.yStart).first
                for (i in 0 until changeableObjList.size) {
                    val obj = hierarchy.findInViewHierarchyHelp(objHierarchy[0], changeableObjList[i])
                    if (obj.third == "scrollx_obj") {
                        changeableObjListX = obj.second
                    }
                }
                if (changeableObjListX.isEmpty()) {
                    for (i in 0 until changeableObjList.size) {
                        val objList = scroll.get_nearest_scrollx_obj(changeableObjList[i])
                        if (objList.isNotEmpty()) {
                            changeableObjListX = objList
                        }
                    }
                }
                for (i in 0 until changeableObjList.size) {
                    val obj = hierarchy.findInViewHierarchyHelp(objHierarchy[0], changeableObjList[i])
                    if (obj.third == "scrolly_obj") {
                        changeableObjListY= obj.second
                    }
                }
                if (changeableObjListY.isEmpty()) {
                    changeableObjListY = scroll.get_nearest_scrolly_obj(changeableObjList[0])
                }
                changeableObjListX = pages.sortObjListByPage(changeableObjListX)
                changeableObjListY = pages.sortObjListByPage(changeableObjListY)
            }
            MotionEvent.ACTION_MOVE -> {
                clickListener = false
                scroll.inerp_after_scroll_stop()
                val pointerId = event.getPointerId(event.actionIndex)
                addMovement(mVelocityTracker!!, event)
                computeCurrentVelocity(mVelocityTracker!!,10)
                scrollDirection = scroll.scroll(x, y, changeableObjList, changeableObjList)

                if (scrollDirection == "x") {
                    inerp = inerpFormat((mVelocityTracker!!.getXVelocity(pointerId)), 0f, changeableObjListX, mutableListOf<Int>())
                }
                else if (scrollDirection == "y") {
                    if (changeableObjListY.isNotEmpty()) {
                        inerp = inerpFormat(0f, (mVelocityTracker!!.getYVelocity(pointerId)), mutableListOf<Int>(), changeableObjListY)
                    }
                    else if (changeableObjListX.isNotEmpty()) {
                        inerp = inerpFormat(0f, (mVelocityTracker!!.getYVelocity(pointerId)), mutableListOf<Int>(), scroll.get_nearest_scrolly_obj(changeableObjListX[0]))
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (clickListener == true) {
                    Log.d("ACTION_CLICK", "")
                    val clickedObjList = whatElement(event.x, event.y).first
                    for (i in 0 until clickedObjList.size) {
                        if (clickedObjList[i] in clickableObjectsList) {
                            Log.d("Clicked Objects", "${resources.getResourceName(clickedObjList[i])}")
                        }
                    }
                }
                scroll.UpdateInerp(inerp)
                scroll.inerp_after_scroll_start()
                inerp = inerpFormat(0f,0f,mutableListOf<Int>(), mutableListOf<Int>())
                mVelocityTracker?.recycle()
                mVelocityTracker = null
                scrollDirection=""
                touched = false
                scroll.resetXYstart()
                isTouchableObjTouched = false
                changeableObjList = mutableListOf<Int>()
                changeableObjListX = mutableListOf<Int>()
                changeableObjListY = mutableListOf<Int>()
                allCaruselsGeted = false
                objType = ""
            }
        }
        return true
    }


    fun getXStart(obj: Int, xStart: Float): Float {
        var res = xStart
        if (obj != ids[0]) {
            if (findViewById<View>(obj).parent != findViewById<View>(ids[0])) {
                val parentView = findViewById<View>(obj).parent
                if (parentView is View) {
                    res = getXStart(parentView.id, (parentView.x + xStart))
                }
            }
        }
        return res
    }

    fun getYStart(obj: Int, yStart: Float): Float {
        var res = yStart
        if (obj != ids[0]) {
            if (findViewById<View>(obj).parent != findViewById<View>(ids[0])) {
                val parentView = findViewById<View>(obj).parent
                if (parentView is View) {
                    res = getYStart(parentView.id, (parentView.y + yStart))
                }
            }
        }
        return res
    }

    fun whatElement(x: Float, y: Float): Pair<MutableList<Int>, String> {
        val touchedObjects = mutableListOf<Int>(-1)
        // Create touched_objects list with touched objects
        for (i in 0 until ids.size) {
            val obj = ids[i]
            val objName = resources.getResourceName(obj).toString()
            val xStart = getXStart(obj,findViewById<View>(obj).x)
            val yStart = getYStart(obj,findViewById<View>(obj).y)
            val width = findViewById<View>(obj).width.toFloat()
            val height = findViewById<View>(obj).height.toFloat()
            // add touched object to touched_objects list
            if ((x in xStart..xStart+width) && (y in yStart..yStart+height) && (("image" in objName) == false) && (findViewById<View>(obj).isVisible)) {
                if (-1 in touchedObjects) {
                    touchedObjects.removeAt(touchedObjects.indexOf(-1))
                }
                touchedObjects.add(obj)
            }
        }
        val type = hierarchy.findInViewHierarchyHelp(objHierarchy[0], touchedObjects[0]).third
        val res = Pair(touchedObjects, type)
        return res
    }

    fun getAllViewIds(rootView: View, ids: MutableList<Int>): MutableList<Int> {
        if (rootView.id != View.NO_ID) {
            ids.add(rootView.id)
            if (rootView is ViewGroup) {
                for (i in 0 until rootView.childCount){
                    getAllViewIds((rootView.getChildAt(i)), ids)
                }
            }
        }
        return ids
    }
    fun createClickableObjectsList(ids: MutableList<Int>): MutableList<Int> {
        val res = mutableListOf<Int>()
        for (i in 0 until ids.size) {
            val obj = ids[i]
            val objName = resources.getResourceName(obj)
            if (("card" in objName) && ("notButton" in objName) == false) {
                res.add(obj)
            }
            else if ("button" in objName) {
                res.add(obj)
            }
        }
        return res
    }

}
