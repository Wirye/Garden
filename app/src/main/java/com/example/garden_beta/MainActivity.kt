
package com.example.garden_beta

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.example.garden_beta.databinding.ActivityMainBinding
import kotlin.math.round
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var ids =  mutableListOf<Int>()
    private var obj_hierarchy = mutableListOf<view_hierarchy>()
    private var real_margins = mutableListOf<real_margins_format>()
    private var screen_width = 0
    private var screen_height = 0

    data class view_hierarchy (
        val obj: Int,
        val base_obj_list: MutableList<MutableList<view_hierarchy>>,
        val scrollx_obj_list: MutableList<MutableList<view_hierarchy>>,
        val scrolly_obj_list: MutableList<MutableList<view_hierarchy>>,
    )

    data class list_of_obj_need_to_scale_optimizate_format (
        val obj: Int,
        val what_change: String,
        val consider_others: Boolean,
    )

    data class real_margins_format (
        val obj: Int,
        val margin_start_and_end: Int,
        val margin_top_and_bottom: Int,
    )

    private var list_of_obj_need_to_scale_optimizate = mutableListOf<list_of_obj_need_to_scale_optimizate_format>()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val base_screen_width = 1200
        val base_screen_height = 2652
        var already_changed = false

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            screen_width = binding.main.width
            screen_height = binding.main.height
            // Ids and obj_hierarchy lists init and scale optimization
            if (already_changed == false) {
                ids = get_all_view_ids(binding.main, ids)
                obj_hierarchy = initialize_all_view_hierarchy(findViewById<View>(ids.get(0)))
                // Show needed information
                Log.d("all ids", ids.toString())
                Log.d("hierarchy", obj_hierarchy.toString())
                for (i in 0 until ids.size) {
                    Log.d("id", resources.getResourceName(ids.get(i)).toString())
                }

                // IMPORTANT! List of obj that need to scale optimization
                list_of_obj_need_to_scale_optimizate = mutableListOf<list_of_obj_need_to_scale_optimizate_format>(
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainHomeButton.id, "margin_start", false),
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainSettingsButton.id, "margin_start", false),
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainSearchButton.id, "margin_end", false),
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainDownloadButton.id, "margin_end",false),
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainAccountButton.id, "margin_end",false),
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainAnimeCarouselScrolly1.id,"margin_top", true),
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainAnimeCarouselCardScrollx1.id,"scale", true),
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainMusicCarouselCardScrollx1.id,"scale", true),
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainAnimeCarouselScrolly1.id,"viewgroup_scale", true),
                )

                // Scale optimization
                for (i in 0 until list_of_obj_need_to_scale_optimizate.size) {
                    val obj = list_of_obj_need_to_scale_optimizate.get(i).obj
                    val what_change = list_of_obj_need_to_scale_optimizate.get(i).what_change
                    val consider_others = list_of_obj_need_to_scale_optimizate.get(i).consider_others
                    if (consider_others == true) {
                        val list_of_obj = find_in_view_hierarchy_help(obj_hierarchy.get(0), obj).second
                        for (o in 0 until list_of_obj.size) {
                            when(what_change) {
                                "scale" ->findViewById<View>(list_of_obj.get(o)).layoutParams=findViewById<View>(list_of_obj.get(o)).layoutParams.apply { width=card_scale_calc(list_of_obj.get(o), screen_width.toFloat()).first; height= card_scale_calc(list_of_obj.get(o), screen_width.toFloat()).second}
                                "margin_start" -> findViewById<View>(list_of_obj.get(o)).layoutParams=set_margin(findViewById<View>(list_of_obj.get(o)).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(list_of_obj.get(o)).marginStart.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "start")
                                "margin_end" -> findViewById<View>(list_of_obj.get(o)).layoutParams=set_margin(findViewById<View>(list_of_obj.get(o)).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(list_of_obj.get(o)).marginEnd.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "end")
                                "margin_top" -> findViewById<View>(list_of_obj.get(o)).layoutParams=set_margin(findViewById<View>(list_of_obj.get(o)).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(list_of_obj.get(o)).marginTop.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "top")
                                "margin_bottom" -> findViewById<View>(list_of_obj.get(o)).layoutParams=set_margin(findViewById<View>(list_of_obj.get(o)).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(list_of_obj.get(o)).marginBottom.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "bottom")
                            }
                        }
                    }
                    else {
                        when(what_change) {
                            "scale" ->findViewById<View>(obj).layoutParams=findViewById<View>(obj).layoutParams.apply { width=card_scale_calc(obj, screen_width.toFloat()).first; height= card_scale_calc(obj, screen_width.toFloat()).second}
                            "margin_start" -> findViewById<View>(obj).layoutParams=set_margin(findViewById<View>(obj).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(obj).marginStart.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "start")
                            "margin_end" -> findViewById<View>(obj).layoutParams=set_margin(findViewById<View>(obj).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(obj).marginEnd.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "end")
                            "margin_top" -> findViewById<View>(obj).layoutParams=set_margin(findViewById<View>(obj).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(obj).marginTop.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "top")
                            "margin_bottom" -> findViewById<View>(obj).layoutParams=set_margin(findViewById<View>(obj).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(obj).marginBottom.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "bottom")
                        }
                    }
                }
                // Wait for changes
                binding.root.post {
                    // Get new x and y (margins from rootview)
                    for (i in 0 until ids.size) {
                        val obj = ids.get(i)
                        val x = findViewById<View>(obj).x
                        val y = findViewById<View>(obj).y
                        real_margins.add(real_margins_format(obj, x.toInt(), y.toInt()))
                    }
                    // Scale optimization for view groups
                    for (i in 0 until list_of_obj_need_to_scale_optimizate.size) {
                        val obj = list_of_obj_need_to_scale_optimizate.get(i).obj
                        val what_change = list_of_obj_need_to_scale_optimizate.get(i).what_change
                        val consider_others = list_of_obj_need_to_scale_optimizate.get(i).consider_others
                        if (consider_others == true) {
                            val list_of_obj = find_in_view_hierarchy_help(obj_hierarchy.get(0), obj).second
                            for (o in 0 until list_of_obj.size) {
                                when(what_change) {
                                    "viewgroup_scale" -> findViewById<View>(list_of_obj.get(o)).layoutParams=findViewById<View>(list_of_obj.get(o)).layoutParams.apply { height=viewgroup_scale_calc(list_of_obj.get(o)) }
                                }
                            }
                        }
                        else {
                            when(what_change) {
                                "viewgroup_scale" -> findViewById<View>(obj).layoutParams=findViewById<View>(obj).layoutParams.apply { height=viewgroup_scale_calc(obj) }
                            }
                        }
                    }
                }
                already_changed = true
            }
        }
    }

    private var touched = false
    private var x_start = 0.0f
    private var y_start = 0.0f
    private var is_touchable_obj_touched = false
    private var scroll_direction = ""
    private var changeable_obj_list = mutableListOf<Int>()
    private var changeable_obj_list_x = mutableListOf<Int>()
    private var changeable_obj_list_y = mutableListOf<Int>()
    private var obj_type = ""
    private var all_carusels_geted = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        // If event -> gesture
        if (event.action == 2) {
            if (touched == false) {
                x_start = x
                y_start = y
                is_touchable_obj_touched = true
                changeable_obj_list = what_element(x_start, y_start).first
                obj_type = what_element(x_start, y_start).second
                if (obj_type == "scrollx_obj") {
                    changeable_obj_list_x = changeable_obj_list
                }
                else if (obj_type == "scrolly_obj") {
                    changeable_obj_list_y = changeable_obj_list
                }
                else if (obj_type == "") {
                    for (i in 0 until obj_hierarchy.get(0).scrollx_obj_list.size) {
                        changeable_obj_list_x.add(obj_hierarchy.get(0).scrollx_obj_list.get(i).get(0).obj)
                    }
                    for (i in 0 until obj_hierarchy.get(0).scrolly_obj_list.size) {
                        changeable_obj_list_y.add(obj_hierarchy.get(0).scrolly_obj_list.get(i).get(0).obj)
                    }
                }
                touched = true
            }
            // Scroll
            var sensivity = 0.8f
            if (is_touchable_obj_touched == true) {
                scroll(x, y, sensivity, changeable_obj_list_x, changeable_obj_list_y)
            }
        }
        // If event -> Nothing
        if (event.action == 1) {
            scroll_direction=""
            touched = false
            x_start = 0.0f
            y_start = 0.0f
            is_touchable_obj_touched = false
            changeable_obj_list = mutableListOf<Int>()
            changeable_obj_list_x = mutableListOf<Int>()
            changeable_obj_list_y = mutableListOf<Int>()
            all_carusels_geted = false
            obj_type = ""
        }
        // If event -> Click
        if (event.action == 0) {

        }
        return super.onTouchEvent(event)
    }

    fun pr(object_value: Float, base_screen_value: Float, screen_value: Float): Float {
        val res = screen_value * (object_value/base_screen_value)
        return res
    }

    fun set_margin(obj: ConstraintLayout.LayoutParams, object_value: Float, base_screen_value: Float, screen_value: Float, where_margin: String): ConstraintLayout.LayoutParams {
        when(where_margin){
            "start" -> obj.marginStart=pr(object_value, base_screen_value, screen_value).toInt()
            "end" -> obj.marginEnd=pr(object_value, base_screen_value, screen_value).toInt()
            "top" -> obj.setMargins(0,pr(object_value, base_screen_value, screen_value).toInt(), 0, 0)
            "bottom" -> obj.setMargins(0,0, 0, pr(object_value, base_screen_value, screen_value).toInt())

        }
        return obj
    }

    fun card_scale_calc(obj: Int, screen_width: Float): Pair<Int, Int> {
        // Calculate margin
        var margin = findViewById<View>(obj).marginStart
        var q = find_in_view_hierarchy_help(obj_hierarchy.get(0), obj).second
        if (q.size > 1) {
            for (i in 0 until q.size) {
                val objj = q.get(i)
                val objj_name = resources.getResourceName(objj)
                if ("1" in objj_name) {
                    margin = findViewById<View>(objj).marginStart
                }
            }
        }
        // Calculate new width and height
        val base_card_width = findViewById<View>(obj).width
        val base_card_heigth = findViewById<View>(obj).height
        var amout_cards = 0.0f
        var res = 0
        if (base_card_width >= 840) {
            amout_cards = round(((screen_width-((margin*3)+margin-30))/base_card_width))
            res = (((screen_width-((margin*3)+margin-30))/amout_cards)-30).toInt()
        }
        else {
            amout_cards = round((screen_width-(margin + (margin - 30)))/base_card_width)
            res = (((screen_width-(margin + (margin - 30)))/amout_cards)-30).toInt()
        }
        val res2 = round((res.toFloat() * (base_card_heigth.toFloat() / base_card_width.toFloat()))).toInt()
        return Pair(res,res2)
    }

    fun viewgroup_scale_calc(obj: Int): Int {
        val viewgroup = findViewById<View>(obj)
        var res = -1
        if (viewgroup is ViewGroup) {
            var child_with_lowest_y = -1
            var lowest_y = 0
            for (i in 0 until viewgroup.childCount) {
                val child = viewgroup.getChildAt(i)
                val child_y = child.y
                if (child_y > lowest_y) {
                    child_with_lowest_y = child.id
                    lowest_y = child_y.toInt()
                }
            }
            if (child_with_lowest_y != -1) {
                val child_with_lowest_y_obj = findViewById<View>(child_with_lowest_y)
                res = lowest_y + child_with_lowest_y_obj.height
            }
        }
        return res
    }

    fun get_x_start(obj: Int, x_start: Float): Float {
        var res = x_start
        if (obj != ids.get(0)) {
            if (findViewById<View>(obj).parent != findViewById<View>(ids.get(0))) {
                val parent_view = findViewById<View>(obj).parent
                if (parent_view is View) {
                    res = get_x_start(parent_view.id, (parent_view.x + x_start))
                }
            }
        }
        return res
    }

    fun get_y_start(obj: Int, y_start: Float): Float {
        var res = y_start
        if (obj != ids.get(0)) {
            if (findViewById<View>(obj).parent != findViewById<View>(ids.get(0))) {
                val parent_view = findViewById<View>(obj).parent
                if (parent_view is View) {
                    res = get_y_start(parent_view.id, (parent_view.y + y_start))
                }
            }
        }
        return res
    }

    fun what_element(x: Float, y: Float): Pair<MutableList<Int>, String> {
        val touched_objects = mutableListOf<Int>(-1)
        // Create touched_objects list with touched objects
        for (i in 0 until ids.size) {
            val obj = ids.get(i)
            val obj_name = resources.getResourceName(obj).toString()
            var x_start = get_x_start(obj,findViewById<View>(obj).x)
            var y_start = get_y_start(obj,findViewById<View>(obj).y)
            var width = findViewById<View>(obj).width.toFloat()
            var height = findViewById<View>(obj).height.toFloat()
            // add touched object to touched_objects list
            if ((x in x_start..x_start+width) && (y in y_start..y_start+height) && (("image" in obj_name) == false)) {
                if (-1 in touched_objects) {
                    touched_objects.removeAt(touched_objects.indexOf(-1))
                }
                touched_objects.add(obj)
            }
        }
        val obj_need_to_remove = mutableListOf<Int>() // keep id of object that need to remove
        for (i in 0 until touched_objects.size) {
            val fun_return =  find_in_view_hierarchy_help(obj_hierarchy.get(0), touched_objects.get(i))
            val parent_obj_for_finding_obj = fun_return.first
            // Step by step add objects that no need for us in obj_need_to_remove list
            if (parent_obj_for_finding_obj != -1) {
                obj_need_to_remove.add(touched_objects.indexOf(parent_obj_for_finding_obj))
            }
        }
        // Remove objects contained in obj_need_to_remove list
        var k = 0 // need to get id correctly
        for (i in 0 until obj_need_to_remove.size) {
            if (obj_need_to_remove.get(i) - k >= 0) {
                touched_objects.removeAt((obj_need_to_remove.get(i)-k))
                k = k + 1
            }
        }
        // Recreate touched_objects list with correct objects
        for (i in 0 until touched_objects.size) {
            val objects = find_in_view_hierarchy_help(obj_hierarchy.get(0), touched_objects.get(i)).second
            for (o in 0 until objects.size) {
                if ((objects.get(o) in touched_objects) == false) {
                    touched_objects.add(objects.get(o))
                }
            }
        }
        var obj_with_positionNumber = 0
        val ids_of_obj_without_positionNumber = mutableListOf<Int>()
        for (i in 0 until touched_objects.size) {
            val obj = touched_objects.get(i)
            val obj_name = resources.getResourceName(obj)
            try {
                val k = obj_name.get(obj_name.lastIndex).toString().toInt()
                if (k != -1) {
                    obj_with_positionNumber = obj_with_positionNumber + 1
                }
            }
            catch (e: Exception) {
                   ids_of_obj_without_positionNumber.add(obj)
            }
        }
        if ((obj_with_positionNumber > 0) && (ids_of_obj_without_positionNumber.isNotEmpty())) {
            for (i in 0 until touched_objects.size) {
                if (i < touched_objects.size) {
                    val obj = touched_objects.get(i)
                    if (obj in ids_of_obj_without_positionNumber) {
                        touched_objects.removeAt(touched_objects.indexOf(obj))
                    }
                }
            }
        }
        val type = find_in_view_hierarchy_help(obj_hierarchy.get(0), touched_objects.get(0)).third
        val res = Pair(touched_objects, type)
        return res
    }

    fun get_all_view_ids(rootView: View, ids: MutableList<Int>): MutableList<Int> {
        if (rootView.id != View.NO_ID) {
            ids.add(rootView.id)
            if (rootView is ViewGroup) {
                for (i in 0 until rootView.childCount){
                    get_all_view_ids((rootView.getChildAt(i)), ids)
                }
            }
        }
        return ids
    }

    fun initialize_all_view_hierarchy(rootView: View): MutableList<view_hierarchy> {
        val base_obj_list = mutableListOf<MutableList<view_hierarchy>>()
        val scrollx_obj_list = mutableListOf<MutableList<view_hierarchy>>()
        val scrolly_obj_list = mutableListOf<MutableList<view_hierarchy>>()
        if (rootView.id != View.NO_ID) {
            if (rootView is ViewGroup) {
                for (i in 0 until rootView.childCount) {
                    val view_hierarchy = initialize_view_hierarchy(rootView.getChildAt(i))
                    if ("scrollx" in resources.getResourceName(rootView.getChildAt(i).id)) {
                        scrollx_obj_list.add(view_hierarchy)
                    }
                    else if ("scrolly" in resources.getResourceName(rootView.getChildAt(i).id)) {
                        scrolly_obj_list.add(view_hierarchy)
                    }
                    else {
                        base_obj_list.add(view_hierarchy)
                    }
                }
            }
        }
        val res = mutableListOf<view_hierarchy>(view_hierarchy(rootView.id, base_obj_list, scrollx_obj_list, scrolly_obj_list))
        return res
    }
    fun initialize_view_hierarchy(rootView: View): MutableList<view_hierarchy> {
        val base_obj_list = mutableListOf<MutableList<view_hierarchy>>()
        val scrollx_obj_list = mutableListOf<MutableList<view_hierarchy>>()
        val scrolly_obj_list = mutableListOf<MutableList<view_hierarchy>>()

        if (rootView is ViewGroup) {
            for (i in 0 until rootView.childCount) {
                val obj = rootView.getChildAt(i).id
                val obj_name = resources.getResourceName(obj)
                val child_view_hierarchy = initialize_view_hierarchy(rootView.getChildAt(i))

                if ("scrollx" in resources.getResourceName(rootView.getChildAt(i).id)) {
                    scrollx_obj_list.add(child_view_hierarchy)
                }
                else if ("scrolly" in resources.getResourceName(rootView.getChildAt(i).id)) {
                    scrolly_obj_list.add(child_view_hierarchy)
                }
                else {
                    base_obj_list.add(child_view_hierarchy)
                }
            }
        }
        val res = mutableListOf<view_hierarchy>(view_hierarchy(rootView.id, base_obj_list, scrollx_obj_list, scrolly_obj_list))
        return res
    }

    fun find_in_view_hierarchy_help(parent_obj: view_hierarchy, finding_obj: Int): Triple<Int, MutableList<Int>, String> {
        var parent_obj_id = -1
        var finding_obj_list = mutableListOf<Int>()
        var what_type = ""

        // Here checks for all obj attributes (NEEDS IN UPDATE, WHEN NEW ATTRIBUTE ADDED)

        // base obj (obj without attributes)
        if (parent_obj.base_obj_list.isNotEmpty()) {
            for (i in 0 until parent_obj.base_obj_list.size) {
                val a = parent_obj.base_obj_list.get(i).get(0)
                if (a.obj == finding_obj) {
                    parent_obj_id = parent_obj.obj
                    finding_obj_list = mutableListOf<Int>(a.obj)
                    what_type = ""
                }
                else {
                    if (find_in_view_hierarchy_help(a, finding_obj).second.isNotEmpty()) {
                        parent_obj_id = find_in_view_hierarchy_help(a, finding_obj).first
                        for (o in 0 until find_in_view_hierarchy_help(a, finding_obj).second.size) {
                            finding_obj_list.add(find_in_view_hierarchy_help(a, finding_obj).second.get(o))
                        }
                        what_type = find_in_view_hierarchy_help(a, finding_obj).third
                        break
                    }
                }
            }
        }

        // scrollx obj (may be scrolled by x)
        if (parent_obj.scrollx_obj_list.isNotEmpty()) {
            for (i in 0 until parent_obj.scrollx_obj_list.size) {
                val a = parent_obj.scrollx_obj_list.get(i).get(0)
                if (a.obj == finding_obj) {
                    parent_obj_id = parent_obj.obj
                    for (o in 0 until parent_obj.scrollx_obj_list.size) {
                        finding_obj_list.add(parent_obj.scrollx_obj_list.get(o).get(0).obj)
                    }
                    what_type = "scrollx_obj"
                    break
                }
                else {
                    if (find_in_view_hierarchy_help(a, finding_obj).second.isNotEmpty()) {
                        parent_obj_id = find_in_view_hierarchy_help(a, finding_obj).first
                        for (o in 0 until find_in_view_hierarchy_help(a, finding_obj).second.size) {
                            finding_obj_list.add(find_in_view_hierarchy_help(a, finding_obj).second.get(o))
                        }
                        what_type = find_in_view_hierarchy_help(a, finding_obj).third
                        break
                    }
                }
            }
        }
        // scrolly obj (may be scrolled by y)
        if (parent_obj.scrolly_obj_list.isNotEmpty()) {
            for (i in 0 until parent_obj.scrolly_obj_list.size) {
                val a = parent_obj.scrolly_obj_list.get(i).get(0)
                if (a.obj == finding_obj) {
                    parent_obj_id = parent_obj.obj
                    for (o in 0 until parent_obj.scrolly_obj_list.size) {
                        finding_obj_list.add(parent_obj.scrolly_obj_list.get(o).get(0).obj)
                    }
                    what_type = "scrolly_obj"
                    break
                }
                else {
                    if (find_in_view_hierarchy_help(a, finding_obj).second.isNotEmpty()) {
                        parent_obj_id = find_in_view_hierarchy_help(a, finding_obj).first
                        for (o in 0 until find_in_view_hierarchy_help(a, finding_obj).second.size) {
                            finding_obj_list.add(find_in_view_hierarchy_help(a, finding_obj).second.get(o))
                        }
                        what_type = find_in_view_hierarchy_help(a, finding_obj).third
                        break
                    }
                }
            }
        }

        val res = Triple(parent_obj_id, finding_obj_list, what_type)
        return res
    }

    fun get_nearest_scrolly_obj (obj: Int): MutableList<Int> {
        var res = mutableListOf<Int>()
        var parent_obj = find_in_view_hierarchy_help(obj_hierarchy.get(0),obj).first
        var parent_obj_name = resources.getResourceName(parent_obj)
        while ("scrolly" in parent_obj_name == false) {
            parent_obj = find_in_view_hierarchy_help(obj_hierarchy.get(0), parent_obj).first
            parent_obj_name = resources.getResourceName(parent_obj)
        }
        res = find_in_view_hierarchy_help(obj_hierarchy.get(0), parent_obj).second
        return res
    }

    fun scroll(x: Float, y: Float, sensivity: Float, changeable_obj_list_x: MutableList<Int>, changeable_obj_list_y: MutableList<Int>) {
        // Get scroll direction
        if ((scroll_direction == "") && ((x != x_start) || (y != y_start))==true) {
            if ((abs(x-x_start) / (abs(y-y_start))) < 1.8f) {
                scroll_direction = "y"
            }
            else {
                scroll_direction="x"
            }
        }
        // Calculate diffrent value and set changeable_obj_list
        var changeable_obj_list = mutableListOf<Int>()
        var diffrent = 0.0f
        var id_of_first_object = 0
        var id_of_last_object = 0
        var margin_start_and_end = 0
        var amout_cards = 0
        if (scroll_direction == "x") {
            diffrent = (x - x_start) * sensivity
            changeable_obj_list = changeable_obj_list_x
        }
        else if (scroll_direction == "y") {
            diffrent = (y-y_start)*sensivity
            if (changeable_obj_list_y.isNotEmpty()) {
                changeable_obj_list = changeable_obj_list_y
            }
            else if (changeable_obj_list_x.isNotEmpty()) {
                changeable_obj_list = get_nearest_scrolly_obj(changeable_obj_list_x.get(0))
            }
        }
        // Calculate id of first object and start, end margins
        for (i in 0 until changeable_obj_list.size) {
            val obj1 = changeable_obj_list.get(i)
            val obj_name1 = resources.getResourceName(obj1)
            if ("1" in obj_name1) {
                id_of_first_object = obj1
                for (o in 0 until real_margins.size) {
                    val obj2 = real_margins.get(o)
                    if (obj2.obj == obj1) {
                        if (scroll_direction == "x") {
                            margin_start_and_end = obj2.margin_start_and_end.toInt()
                        }
                        else if (scroll_direction == "y") {
                            margin_start_and_end = obj2.margin_top_and_bottom.toInt()
                        }
                    }
                }
            }
        }
        // Calculate amout of objects
        for (i in 0 until changeable_obj_list.size) {
            try {
                val k = resources.getResourceName(changeable_obj_list.get(i)).get(resources.getResourceName(changeable_obj_list.get(i)).lastIndex).toString().toInt()
                amout_cards = amout_cards + 1
            }
            catch (e: NumberFormatException) {
                Log.d("ERROR", "ITEM ${resources.getResourceName(changeable_obj_list.get(i))} DON'T HAVE positionNumber IN HIS ID")
            }
        }
        // Calculate id of last object
        for (i in 0 until changeable_obj_list.size) {
            val obj = changeable_obj_list.get(i)
            val obj_name = resources.getResourceName(obj)
            if ("${amout_cards}" in obj_name) {
                id_of_last_object = obj
            }
        }
        // Scroll logic
        for (i in 0 until changeable_obj_list.size) {
            val obj = changeable_obj_list.get(i)
            var parent_obj_size = 0
            // Calculate scroll direction
            if (scroll_direction == "x") {
                parent_obj_size = findViewById<View>(find_in_view_hierarchy_help(obj_hierarchy.get(0), obj).first).width
            }
            else if (scroll_direction == "y") {
                parent_obj_size = findViewById<View>(find_in_view_hierarchy_help(obj_hierarchy.get(0), obj).first).height
            }
            // Check condition - where we scroll to?
            var scroll_to_right_or_up = false
            if (scroll_direction == "x") {
                scroll_to_right_or_up = x > x_start
            }
            else if (scroll_direction == "y") {
                scroll_to_right_or_up = y > y_start
            }
            var scroll_to_left_or_bottom = false
            if (scroll_direction == "x") {
                scroll_to_left_or_bottom = x < x_start
            }
            else if (scroll_direction == "y") {
                scroll_to_left_or_bottom = y < y_start
            }
            // Calculate values needed to check condition - can we scroll further?
            var first_obj_coord = 0.0f
            var last_obj_coord = 0.0f
            var last_obj_size = 0
            if (scroll_direction == "x") {
                first_obj_coord = findViewById<View>(id_of_first_object).x
                last_obj_coord = findViewById<View>(id_of_last_object).x
                last_obj_size = findViewById<View>(id_of_last_object).width
            }
            else if (scroll_direction == "y") {
                first_obj_coord = findViewById<View>(id_of_first_object).y
                last_obj_coord = findViewById<View>(id_of_last_object).y
                last_obj_size = findViewById<View>(id_of_last_object).height
            }

            // Scroll to right or up
            if (scroll_to_right_or_up) {
                // If we can't scroll further
                if ((first_obj_coord+diffrent >= margin_start_and_end)) {
                    if (((last_obj_coord+diffrent) < (parent_obj_size - margin_start_and_end - last_obj_size)) == false) {
                        for (o in 0 until changeable_obj_list.size) {
                            val obj6 = changeable_obj_list.get(o)
                            val obj_name6 = resources.getResourceName(obj6)
                            // Change first object coord
                            if (scroll_direction == "x") {
                                findViewById<View>(id_of_first_object).x=margin_start_and_end.toFloat()
                            }
                            else if (scroll_direction == "y") {
                                findViewById<View>(id_of_first_object).y=margin_start_and_end.toFloat()
                            }
                            // Change other objects coord
                            if (("1" in obj_name6) == false) {
                                try {
                                    val k = obj_name6.get(obj_name6.lastIndex).toString().toInt()
                                    var q = 0.0f
                                    for (j in 0 until k-1) {
                                        // Calculate other objects sizes and margins (needed to calculate object position)
                                        for (p in 0 until changeable_obj_list.size) {
                                            val k3 = resources.getResourceName(changeable_obj_list.get(p)).get(resources.getResourceName(changeable_obj_list.get(p)).lastIndex).toString().toInt()
                                            if (k3 == j + 1) {
                                                if (scroll_direction == "x") {
                                                    q = q + findViewById<View>(changeable_obj_list.get(p)).width
                                                    if (k3 == 1) {
                                                        q = q + findViewById<View>(obj6).marginStart
                                                    }
                                                    else {
                                                        q = q + findViewById<View>(changeable_obj_list.get(p)).marginStart
                                                    }
                                                }
                                                else if (scroll_direction == "y") {
                                                    q = q + findViewById<View>(changeable_obj_list.get(p)).height
                                                    if (k3 == 1) {
                                                        q = q + findViewById<View>(obj6).marginTop
                                                    }
                                                    else {
                                                        q = q + findViewById<View>(changeable_obj_list.get(p)).marginTop
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    // Set object position
                                    if (scroll_direction == "x") {
                                        val objj = findViewById<View>(obj6)
                                        objj.x=(margin_start_and_end+q)
                                    }
                                    else if (scroll_direction == "y") {
                                        val objj = findViewById<View>(obj6)
                                        objj.y=(margin_start_and_end+q)
                                    }
                                }
                                catch (e: Exception) {
                                    Log.d("ERROR", "ITEM ${obj_name6} DON'T HAVE positionNumber IN HIS ID")
                                }
                            }
                        }
                        if (scroll_direction == "x") {
                            x_start = x
                        }
                        else if (scroll_direction == "y") {
                            y_start = y
                        }
                        break
                    }
                }
                // If we can scroll further
                else {
                    for (i in 0 until changeable_obj_list.size) {
                        val obj4 = findViewById<View>(changeable_obj_list.get(i))
                        // Set object position
                        if (scroll_direction == "x") {
                            obj4.x=obj4.x+diffrent
                        }
                        else if (scroll_direction == "y") {
                            obj4.y=obj4.y+diffrent
                        }
                    }
                    if (scroll_direction == "x") {
                        x_start = x
                    }
                    else if (scroll_direction == "y") {
                        y_start = y
                    }
                    break
                }
            }

            // Scroll to left or bottom
            else if (scroll_to_left_or_bottom) {
                // If we can't scroll further
                if ((last_obj_coord+diffrent) <= (parent_obj_size - margin_start_and_end - last_obj_size)) {
                    if ((first_obj_coord)+diffrent > margin_start_and_end) {
                        for (o in 0 until changeable_obj_list.size) {
                            val obj7 = changeable_obj_list.get(o)
                            val obj_name7 = resources.getResourceName(obj7)
                            // Change last object coord
                            if (scroll_direction == "x") {
                                findViewById<View>(id_of_last_object).x=(parent_obj_size - margin_start_and_end - last_obj_size).toFloat()
                            }
                            else if (scroll_direction =="y") {
                                findViewById<View>(id_of_last_object).y=(parent_obj_size - margin_start_and_end - last_obj_size).toFloat()
                            }
                            // Change other objects coord
                            if (("${amout_cards}" in obj_name7) == false) {
                                try {
                                    val k2 = obj_name7.get(obj_name7.lastIndex).toString().toInt()
                                    var q = 0.0f
                                    var q1 = 0.0f
                                    // Calculate other objects sizes (needed to calculate object position)
                                    for (j in 1 until amout_cards-k2+2) {
                                        for (p in 0 until changeable_obj_list.size) {
                                            val obj8 = changeable_obj_list.get(p)
                                            val k3 = resources.getResourceName(obj8).get(resources.getResourceName(obj8).lastIndex).toString().toInt()
                                            if (k3 == amout_cards-j+1-1+1) {
                                                if (scroll_direction == "x") {
                                                    q = q + findViewById<View>(obj8).width
                                                }
                                                else if (scroll_direction == "y") {
                                                    q = q + findViewById<View>(obj8).height
                                                }
                                            }
                                        }
                                    }
                                    // Calculate other objects margins (needed to calculate object position)
                                    for (j in 1 until amout_cards-k2+1) {
                                        for (p in 0 until changeable_obj_list.size) {
                                            val obj8 = changeable_obj_list.get(p)
                                            val k3 = resources.getResourceName(obj8).get(resources.getResourceName(obj8).lastIndex).toString().toInt()
                                            if (k3 == amout_cards-j+1) {
                                                if (scroll_direction == "x") {
                                                    q1 = q1 + findViewById<View>(obj8).marginStart
                                                }
                                                else if (scroll_direction == "y") {
                                                    q1 = q1 + findViewById<View>(obj8).marginTop
                                                }
                                            }
                                        }
                                    }
                                    // Set object position
                                    if (scroll_direction == "x") {
                                        val objj = findViewById<View>(obj7)
                                        objj.x=parent_obj_size - margin_start_and_end - q - q1
                                    }
                                    else if (scroll_direction == "y") {
                                        val objj = findViewById<View>(obj7)
                                        objj.y=parent_obj_size - margin_start_and_end - q - q1
                                    }
                                }
                                catch (e: IndexOutOfBoundsException) {
                                    Log.d("ERROR", "ITEM ${obj_name7} DON'T HAVE positionNumber IN HIS ID")
                                }
                            }
                        }
                    }
                    if (scroll_direction == "x") {
                        x_start = x
                    }
                    else if (scroll_direction == "y") {
                        y_start = y
                    }
                    break
                }
                // If we can scroll further
                else {
                    for (i in 0 until changeable_obj_list.size) {
                        val obj5 = findViewById<View>(changeable_obj_list.get(i))
                        // Set object position
                        if (scroll_direction == "x") {
                            obj5.x=obj5.x+diffrent
                        }
                        else if (scroll_direction == "y") {
                            obj5.y=obj5.y+diffrent
                        }
                    }
                    if (scroll_direction == "x") {
                        x_start = x
                    }
                    else if (scroll_direction == "y") {
                        y_start = y
                    }
                    break
                }
            }
        }
    }
}
