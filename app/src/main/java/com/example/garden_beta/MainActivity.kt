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
    private var list_of_scrollable_obj = mutableListOf<Int>()


    private var screen_width = 0
    private var screen_height = 0

    private var white_list_buttons_mg_start = mutableListOf<String>("home", "settings")
    private var white_list_buttons_mg_end = mutableListOf<String>("search", "download", "account")

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
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainAnimeCarouselCardScrollx1.id,"margin_start", false),
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainMusicCarouselCardScrollx1.id,"margin_start", false),
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainAnimeCarouselCardScrollx1.id,"scale", true),
                    list_of_obj_need_to_scale_optimizate_format(binding.activityMainMusicCarouselCardScrollx1.id,"scale", true),
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
                        if ("1" in obj_name1) {
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
                        if ("${amout_cards}" in obj_name) {
                            id_of_last_object = obj
                        }
                    }

                    // Scroll logic
                    for (i in 0 until changeable_obj_list_x.size) {
                        val obj = changeable_obj_list_x.get(i)
                        val obj_name = resources.getResourceName(obj)
                        val parent_obj_width = findViewById<View>(find_in_view_hierarchy_help(obj_hierarchy.get(0), obj).first).width
                        // Scroll to right
                        if (x > x_start) {
                            if ((findViewById<View>(id_of_first_object).x+diffrent >= margin_start_and_end)) {
                                if (((findViewById<View>(id_of_last_object).x+diffrent) >= (parent_obj_width - margin_start_and_end - findViewById<View>(id_of_last_object).width)) == false) {
                                    for (o in 0 until changeable_obj_list_x.size) {
                                        val obj6 = changeable_obj_list_x.get(o)
                                        val obj_name6 = resources.getResourceName(obj6)
                                        findViewById<View>(id_of_first_object).x=margin_start_and_end.toFloat()
                                        if (("1" in obj_name6) == false) {
                                            try {
                                                val k = obj_name6.get(obj_name6.lastIndex).toString().toInt()
                                                var q = 0.0f
                                                for (j in 0 until k-1) {
                                                    q = q + findViewById<View>(changeable_obj_list_x.get(j)).width
                                                    q = q + findViewById<View>(changeable_obj_list_x.get(j)).marginStart
                                                }
                                                findViewById<View>(obj6).x=(margin_start_and_end+q).toFloat()
                                            }
                                            catch (e: NumberFormatException) {
                                                Log.d("ERROR", "ITEM ${obj_name6} DON'T HAVE positionNumber IN HIS ID")
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
                            if ((findViewById<View>(id_of_last_object).x+diffrent) <= (parent_obj_width - margin_start_and_end - findViewById<View>(id_of_last_object).width)) {
                                if (((findViewById<View>(id_of_first_object).x+diffrent <= margin_start_and_end)) == false) {
                                    for (o in 0 until changeable_obj_list_x.size) {
                                        val obj7 = changeable_obj_list_x.get(o)
                                        val obj_name7 = resources.getResourceName(obj7)
                                        findViewById<View>(id_of_last_object).x=(parent_obj_width - margin_start_and_end - findViewById<View>(id_of_last_object).width).toFloat()
                                        if (("${amout_cards}" in obj_name7) == false) {
                                            try {
                                                val k2 = obj_name7.get(obj_name7.lastIndex).toString().toInt()
                                                var q = 0.0f
                                                var q1 = 0.0f
                                                for (j in 1 until amout_cards-k2+2) {
                                                    q = q + findViewById<View>(changeable_obj_list_x.get(amout_cards-j+1-1)).width
                                                }
                                                for (j in 0 until amout_cards-k2) {
                                                    q1 = q1 + findViewById<View>(changeable_obj_list_x.get(amout_cards-j)).marginStart
                                                }
                                                findViewById<View>(obj7).x=(parent_obj_width - margin_start_and_end - q - q1).toFloat()
                                            }
                                            catch (e: NumberFormatException) {
                                                Log.d("ERROR", "ITEM ${obj_name7} DON'T HAVE positionNumber IN HIS ID")
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
                    val diffrent = (y-y_start)*sensivity
                    var id_of_first_object = 0
                    var id_of_last_object = 0
                    var margin_start_and_end = 0
                    var amout_cards = 0

                    // Create or recreate changeable_obj_list_y list
                    if (changeable_obj_list_y.isEmpty() && changeable_obj_list_x.isNotEmpty()) {
                        changeable_obj_list_y = get_nearest_scrolly_obj(changeable_obj_list_x.get(0))
                    }

                    // Log.d all changeable_obj_list_y list
                    for (i in 0 until changeable_obj_list_y.size) {
                        Log.d("changeable_obj_list_y list", changeable_obj_list_y.get(i).toString() + "        " + resources.getResourceName(changeable_obj_list_y.get(i)))
                    }

                    // Calculate id of first obj and start, end margins
                    for (i in 0 until changeable_obj_list_y.size) {
                        val obj = changeable_obj_list_y.get(i)
                        val obj_name = resources.getResourceName(obj)
                        if ("1" in obj_name) {
                            id_of_first_object = obj
                            margin_start_and_end = findViewById<View>(obj).marginTop
                        }
                    }

                    // Calculate amout cards
                    for (i in 0 until changeable_obj_list_y.size) {
                        amout_cards = amout_cards + 1
                    }

                    // Calculate id of last obj
                    for (i in 0 until changeable_obj_list_y.size) {
                        val obj = changeable_obj_list_y.get(i)
                        val obj_name = resources.getResourceName(obj)
                        if ("${amout_cards}" in obj_name) {
                            id_of_last_object = obj
                        }
                    }


                    // Scroll logic
                    for (i in 0 until changeable_obj_list_y.size) {
                        val obj = changeable_obj_list_y.get(i)
                        val parent_obj_heigth = findViewById<View>(find_in_view_hierarchy_help(obj_hierarchy.get(0), obj).first).height
                        // Scroll up
                        if (y > y_start) {
                            Log.d("up", margin_start_and_end.toString())
                            if (findViewById<View>(id_of_first_object).y+diffrent >= margin_start_and_end) {
                                if ((findViewById<View>(id_of_last_object) .y+diffrent >= (parent_obj_heigth - margin_start_and_end - findViewById<View>(id_of_last_object).height)) == false) {
                                    for (o in 0 until changeable_obj_list_y.size) {
                                        val obj2 = changeable_obj_list_y.get(o)
                                        val obj2_name = resources.getResourceName(obj2)
                                        findViewById<View>(id_of_first_object).y=margin_start_and_end.toFloat()
                                        if (("1" in obj2_name) == false) {
                                            try {
                                                var q = 0
                                                val k = obj2_name.get(obj2_name.lastIndex).toString().toInt()
                                                for (j in 0 until k-1) {
                                                    q = q + findViewById<View>(changeable_obj_list_y.get(j)).height
                                                    q = q + findViewById<View>(changeable_obj_list_y.get(j)).marginTop
                                                }
                                                findViewById<View>(obj2).y = (margin_start_and_end + q).toFloat()
                                            }
                                            catch (e: Exception) {
                                                Log.d("ERROR", "ITEM ${obj2_name} DON'T HAVE positionNumber IN HIS ID")
                                            }
                                        }
                                    }
                                }
                                y_start = y
                                break
                            }
                            else {
                                for (u in 0 until changeable_obj_list_y.size) {
                                    val obj3 = changeable_obj_list_y.get(u)
                                    findViewById<View>(obj3).y=findViewById<View>(obj3).y+diffrent
                                }
                                y_start = y
                                break
                            }
                        }
                        // Scroll down
                        else if (y < y_start) {
                            Log.d("down", margin_start_and_end.toString())
                            if (findViewById<View>(id_of_last_object).y+diffrent <= parent_obj_heigth - margin_start_and_end - findViewById<View>(id_of_last_object).height) {
                                if ((findViewById<View>(id_of_first_object).y+diffrent <= margin_start_and_end) == false) {
                                    for (o in 0 until changeable_obj_list_y.size) {
                                        val obj4 = changeable_obj_list_y.get(o)
                                        val obj4_name = resources.getResourceName(obj4)
                                        findViewById<View>(id_of_last_object).y=parent_obj_heigth-margin_start_and_end-findViewById<View>(id_of_last_object).height.toFloat()
                                        if (("${amout_cards}" in obj4_name) == false) {
                                            try {
                                                val k = obj4_name.get(obj4_name.lastIndex).toString().toInt()
                                                var q = 0.0f
                                                var q1 = 0.0f
                                                for (j in 1 until amout_cards-k+2) {
                                                    q = q + findViewById<View>(changeable_obj_list_x.get(amout_cards-j+1-1)).height
                                                }
                                                for (j in 0 until amout_cards-k) {
                                                    q1 = q1 + findViewById<View>(changeable_obj_list_x.get(amout_cards-j)).marginTop
                                                }
                                                findViewById<View>(obj4).y=(parent_obj_heigth - margin_start_and_end - q - q1).toFloat()
                                            }
                                            catch (e: Exception) {
                                                Log.d("ERROR", "ITEM ${obj4_name} DON'T HAVE positionNumber IN HIS ID")
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                for (o in 0 until changeable_obj_list_y.size) {
                                    val obj5 = changeable_obj_list_y.get(o)
                                    findViewById<View>(obj5).y=findViewById<View>(obj5).y+diffrent
                                }
                            }
                        }
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

    fun get_nearest_scrolly_obj (obj: Int): MutableList<Int> {
        var res = mutableListOf<Int>()
        var parent_obj = find_in_view_hierarchy_help(obj_hierarchy.get(0),obj).first
        var parent_obj_name = resources.getResourceName(parent_obj)
        while ("scrolly" in parent_obj_name == false) {
            parent_obj = find_in_view_hierarchy_help(obj_hierarchy.get(0), parent_obj).first
            parent_obj_name = resources.getResourceName(parent_obj)
        }
        res = find_in_view_hierarchy_help(obj_hierarchy.get(0), parent_obj).second
        Log.d("FUUUUUUNNNNNNN", parent_obj.toString() + "     " + parent_obj_name.toString() + "    res    " + res.toString())
        return res
    }
}