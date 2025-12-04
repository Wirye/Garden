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
import androidx.core.view.marginEnd
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var ids =  mutableListOf<Int>()
    private var obj_hierarchy = mutableListOf<view_hierarchy>()
    private var list_of_scrollable_obj = mutableListOf<Int>()


    private var screen_width = 0
    private var screen_height = 0

    data class view_hierarchy (
        val obj: Int,
        val base_obj_list: MutableList<MutableList<view_hierarchy>>,
        val scrollx_obj_list: MutableList<MutableList<view_hierarchy>>,
        val scrolly_obj_list: MutableList<MutableList<view_hierarchy>>,
    )


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

            // scale optimization
            if (already_changed == false) {

                ids = get_all_view_ids(binding.main, ids)
                obj_hierarchy = initialize_all_view_hierarchy(findViewById<View>(ids.get(0)))

                Log.d("all ids", ids.toString())
                Log.d("hierarchy", obj_hierarchy.toString())
                for (i in 0 until ids.size) {
                    Log.d("id", resources.getResourceName(ids.get(i)).toString())
                }
                Log.d("status bar", binding.main.y.toString())
//                for (i in 0 until ids.size) {
                Log.d("CHECK FUN FIND_IN_HIERARCHY", find_in_view_hierarchy_help(obj_hierarchy.get(0), 2131296797).first.toString() +"     " + find_in_view_hierarchy_help(obj_hierarchy.get(0), 2131296797).second.toString() +"     " + find_in_view_hierarchy_help(obj_hierarchy.get(0), 2131296797).third.toString())
//                }

                for (i in 0 until ids.size){
                    val obj = ids.get(i)
                    val obj_name = resources.getResourceName(obj).toString()
                    if ("button" in obj_name) {
                        var white_list = mutableListOf<String>("home", "settings")
                        var white_list_2 = mutableListOf<String>("search", "download", "account")
                        for (o in 0 until white_list.size) {
                            if (white_list.get(o) in obj_name) {
                                findViewById<View>(obj).layoutParams=set_margin(findViewById<View>(obj).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(obj).marginStart.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "start")
                                white_list.set(o, " ")
                            }
                        }

                        for (j in 0 until white_list_2.size) {
                            if (white_list_2.get(j) in obj_name) {
                                findViewById<View>(obj).layoutParams=set_margin(findViewById<View>(obj).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(obj).marginEnd.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "end")
                                white_list_2.set(j," ")
                            }
                        }

                    }
                    if ("carousel" in obj_name) {
                        if ((("text" in obj_name) == false) && (("card" in obj_name) == false)) {
                            findViewById<View>(obj).layoutParams=set_margin(findViewById<View>(obj).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(obj).marginTop.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "top")
                        }
                        if ("text" in obj_name) {
                            findViewById<View>(obj).layoutParams=set_margin(findViewById<View>(obj).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(obj).marginStart.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "start")
                        }
                        if ("card" in obj_name) {

                            if (("anime" in obj_name) && (("image" in obj_name) == false)){
                                if ("1" in obj_name) {
                                    findViewById<View>(obj).layoutParams=set_margin(findViewById<View>(obj).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(obj).marginStart.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "start")
                                }
                                findViewById<View>(obj).layoutParams=findViewById<View>(obj).layoutParams.apply { width=anime_carousel_card_scale_calc(findViewById<View>(obj).width.toFloat(), screen_width.toFloat()).first; height=anime_carousel_card_scale_calc(findViewById<View>(obj).width.toFloat(), screen_width.toFloat()).second }
                            }

                            if (("music" in obj_name) && (("image" in obj_name) == false)){
                                if ("1" in obj_name) {
                                    findViewById<View>(obj).layoutParams=set_margin(findViewById<View>(obj).layoutParams as ConstraintLayout.LayoutParams, findViewById<View>(obj).marginStart.toFloat(), base_screen_width.toFloat(), screen_width.toFloat(), "start")
                                }
                                findViewById<View>(obj).layoutParams=findViewById<View>(obj).layoutParams.apply { width=music_carousel_card_scale_calc(findViewById<View>(obj).width.toFloat(),screen_width.toFloat()).first; height=music_carousel_card_scale_calc(findViewById<View>(obj).width.toFloat(),screen_width.toFloat()).second }
                            }
                        }
                    }
                    // Make list of scrollable objects
                    if ((("carousel" in obj_name) && ("card" in obj_name) && (("image") in obj_name) == false) || (("carousel" in obj_name) && (("card" in obj_name) == false) && (("text" in obj_name) == false))) {
                        list_of_scrollable_obj.add(obj)
                    }
                }
                for (i in 0 until list_of_scrollable_obj.size) {
                    val obj = list_of_scrollable_obj.get(i)
                    val obj_name = resources.getResourceName(obj)
                    Log.d("scrollable_objects", obj_name)
                }
            }
            already_changed = true
        }
    }

    private var touched = false
    private var cursor_cords = mutableListOf<Pair<Float, Float>>()
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
                touched = true
            }
            // Scroll
            if (is_touchable_obj_touched == true) {

                // Get scroll direction
                if ((scroll_direction == "") && ((x != x_start) || (y != y_start))==true) {
                    if ((abs(x-x_start) / (abs(y-y_start))) < 1.8f) {
                        Log.d("y", (abs(x-x_start).toString() + "   " +(abs(y-y_start).toString() + "  x  " + x.toString() + "  y  " + y.toString())))
                        scroll_direction = "y"
                    }
                    else {
                        Log.d("x", (abs((x-x_start)).toString() + "   " +(abs((y-y_start)).toString() + "  x  " + x.toString() + "  y  " + y.toString())))
                        scroll_direction="x"
                    }
                }

                // Horizontal scroll
                if (scroll_direction == "x") {
                    var sensivity = 0.8f
                    val diffrent = (x-x_start)*sensivity
                    var id_of_first_object = 0
                    var id_of_last_object = 0
                    var margin_start_and_end = 0
                    var amout_cards = 0

                    Log.d("changeable_obj_list_x", changeable_obj_list_x.toString())
                    for (i in 0 until changeable_obj_list_x.size) {
                        Log.d("changeable_obj_list_x all obj names", resources.getResourceName(changeable_obj_list_x.get(i)).toString())
                    }
                    // Calculate id of first object and start, end margins
                    for (i in 0 until changeable_obj_list_x.size) {
                        val obj1 = changeable_obj_list_x.get(i)
                        val obj_name1 = resources.getResourceName(obj1)
                        if (("1" in obj_name1) || (changeable_obj_list_x.size == 1)) {
                            id_of_first_object = obj1
                            margin_start_and_end = findViewById<View>(obj1).marginStart
                        }
                    }

                    // Calculate amout of objects
                    for (i in 0 until changeable_obj_list_x.size) {
                        amout_cards = amout_cards + 1
                    }

                    // Calculate id of last object
                    for (i in 0 until changeable_obj_list_x.size) {
                        val obj = changeable_obj_list_x.get(i)
                        val obj_name = resources.getResourceName(obj)
                        if (("${amout_cards}" in obj_name) || (changeable_obj_list_x.size == 1)) {
                            id_of_last_object = obj
                        }
                    }

                    // Scroll logic
                    for (i in 0 until changeable_obj_list_x.size) {
                        val obj = changeable_obj_list_x.get(i)
                        val obj_name = resources.getResourceName(obj)
                        // Scroll to right
                        if (x > x_start) {
                            if ((findViewById<View>(id_of_first_object).x+diffrent >= margin_start_and_end)) {
                                if (((findViewById<View>(id_of_last_object).x+diffrent) >= (screen_width - margin_start_and_end - findViewById<View>(id_of_last_object).width)) == false) {
                                    for (i in 0 until changeable_obj_list_x.size) {
                                        val obj6 = changeable_obj_list_x.get(i)
                                        val obj_name6 = resources.getResourceName(obj6)
                                        findViewById<View>(id_of_first_object).x=margin_start_and_end.toFloat()
                                        if ((("1" in obj_name6) == false) && (changeable_obj_list_x.size > 1)) {
                                            try {
                                                val k = obj_name6.get(obj_name6.lastIndex).toString().toInt()
                                                findViewById<View>(obj6).x=(margin_start_and_end+((findViewById<View>(obj6).width + findViewById<View>(obj6).marginStart)*(k-1))).toFloat()
                                            }
                                            catch (e: NumberFormatException) {
                                                Log.d("ERROR", "ITEM DON'T HAVE positionNumber IN HIS ID")
                                            }
                                        }
                                    }
                                    x_start = x
                                    break
                                }
                            }
                            else {
                                for (i in 0 until changeable_obj_list_x.size) {
                                    val obj4 = changeable_obj_list_x.get(i)
                                    val obj_name4 = resources.getResourceName(obj4)
                                    findViewById<View>(obj4).x=findViewById<View>(obj4).x+diffrent
                                }
                                x_start = x
                                break
                            }
                        }
                        // Scroll to left
                        else if (x < x_start) {
                            if ((findViewById<View>(id_of_last_object).x+diffrent) <= (screen_width - margin_start_and_end - findViewById<View>(id_of_last_object).width)) {
                                if (((findViewById<View>(id_of_first_object).x+diffrent <= margin_start_and_end)) == false) {
                                    for (i in 0 until changeable_obj_list_x.size) {
                                        val obj7 = changeable_obj_list_x.get(i)
                                        val obj_name7 = resources.getResourceName(obj7)
                                        findViewById<View>(id_of_last_object).x=(screen_width - margin_start_and_end - findViewById<View>(id_of_last_object).width).toFloat()
                                        if (((resources.getResourceName(id_of_last_object)) in obj_name7 == false) && (changeable_obj_list_x.size > 1)) {
                                            try {
                                                val k2 = obj_name7.get(obj_name7.lastIndex).toString().toInt()
                                                findViewById<View>(obj7).x=(screen_width - margin_start_and_end - ((findViewById<View>(id_of_last_object).width)*(amout_cards-k2+1)) - ((findViewById<View>(id_of_last_object).marginStart)*(amout_cards-k2))).toFloat()
                                            }
                                            catch (e: NumberFormatException) {
                                                Log.d("ERROR", "ITEM DON'T HAVE positionNumber IN HIS ID")
                                            }
                                        }
                                    }
                                    x_start = x
                                    break
                                }
                            }
                            else {
                                for (i in 0 until changeable_obj_list_x.size) {
                                    val obj5 = changeable_obj_list_x.get(i)
                                    val obj_name5 = resources.getResourceName(obj5)
                                    findViewById<View>(obj5).x=findViewById<View>(obj5).x+diffrent
                                }
                                x_start = x
                                break
                            }
                        }
                    }
                }

                // Vertical scroll
                if (scroll_direction == "y") {
                    var margin_start_and_end = 0

                    if (changeable_obj_list_y.size != 0) {
                        margin_start_and_end = findViewById<View>(changeable_obj_list_y.get(0)).marginTop
                    }

                    if (y > y_start) {
                        Log.d("up", margin_start_and_end.toString())
                    }
                    else if (y < y_start) {
                        Log.d("down", "")
                    }

                }
            }
        }

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

    fun anime_carousel_card_scale_calc(base_card_width: Float, screen_width: Float): Pair<Int, Int> {
        val margin = binding.activityMainAnimeCarouselCardScrollx1.marginStart
        val amout_cards = round((screen_width-(margin + (margin - 30)))/base_card_width)
        val res = (((screen_width-(margin + (margin - 30)))/amout_cards)-30).toInt()
        val res2 = round((res * 1.4288)).toInt()
        return Pair(res,res2)
    }

    fun music_carousel_card_scale_calc(base_card_width: Float, screen_width: Float): Pair<Int, Int> {
        val margin = binding.activityMainMusicCarouselCardScrollx1.marginStart
        val amout_cards = round(((screen_width-((margin*3)+margin-30))/base_card_width)).toInt()
        val res = (((screen_width-((margin*3)+margin-30))/amout_cards)-30).toInt()
        val res2 = res
        return Pair(res,res2)
    }

    // It's just exists
    fun element_size_calc(base_value: Float, base_screen_value: Float, screen_value: Float): Float {
        val res = pr(base_value,base_screen_value,screen_value) * base_value
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
            touched_objects.removeAt((obj_need_to_remove.get(i)-k))
            k = k + 1
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
        val type = find_in_view_hierarchy_help(obj_hierarchy.get(0), touched_objects.get(0)).third
        val res = Pair(touched_objects, type)
        Log.d("RES", res.toString())
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

}