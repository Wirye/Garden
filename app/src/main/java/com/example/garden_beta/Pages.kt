package com.example.garden_beta

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

class pages(activity: AppCompatActivity) {
    val activity = activity
    val resources = activity.resources
    var currentPage = ""
    val homePageObjects = mutableListOf<Int>()
    val animePageObjects = mutableListOf<Int>()
    val mangaPageObjects = mutableListOf<Int>()
    val musicPageObjects = mutableListOf<Int>()
    val downloadPageObjects = mutableListOf<Int>()
    val settingsPageObjects = mutableListOf<Int>()
    val searchPageObjects = mutableListOf<Int>()
    val accountPageObjects = mutableListOf<Int>()
    fun createListsOfPagesObjects (ids: MutableList<Int>) {
        for (i in 0 until ids.size) {
            val obj = ids[i]
            val objName = resources.getResourceName(obj)
            if ("homepage" in objName) {
                homePageObjects.add(obj)
                val objView = activity.findViewById<View>(obj)
                if (objView is ViewGroup) {
                    for (o in 0 until objView.childCount) {
                        homePageObjects.add(objView.getChildAt(o).id)
                    }
                }
            }
            if ("animepage" in objName) {
                animePageObjects.add(obj)
                val objView = activity.findViewById<View>(obj)
                if (objView is ViewGroup) {
                    for (o in 0 until objView.childCount) {
                        animePageObjects.add(objView.getChildAt(o).id)
                    }
                }
            }
            if ("mangapage" in objName) {
                mangaPageObjects.add(obj)
                val objView = activity.findViewById<View>(obj)
                if (objView is ViewGroup) {
                    for (o in 0 until objView.childCount) {
                        mangaPageObjects.add(objView.getChildAt(o).id)
                    }
                }
            }
            if ("downloadpage" in objName) {
                downloadPageObjects.add(obj)
                val objView = activity.findViewById<View>(obj)
                if (objView is ViewGroup) {
                    for (o in 0 until objView.childCount) {
                        downloadPageObjects.add(objView.getChildAt(o).id)
                    }
                }
            }
            if ("settingspage" in objName) {
                settingsPageObjects.add(obj)
                val objView = activity.findViewById<View>(obj)
                if (objView is ViewGroup) {
                    for (o in 0 until objView.childCount) {
                        settingsPageObjects.add(objView.getChildAt(o).id)
                    }
                }
            }
            if ("searchpage" in objName) {
                searchPageObjects.add(obj)
                val objView = activity.findViewById<View>(obj)
                if (objView is ViewGroup) {
                    for (o in 0 until objView.childCount) {
                        searchPageObjects.add(objView.getChildAt(o).id)
                    }
                }
            }
            if ("accountpage" in objName) {
                accountPageObjects.add(obj)
                val objView = activity.findViewById<View>(obj)
                if (objView is ViewGroup) {
                    for (o in 0 until objView.childCount) {
                        accountPageObjects.add(objView.getChildAt(o).id)
                    }
                }
            }
        }
    }

    fun switchPageTo(page: String) {
        var listOfObjectsNeededToShow = mutableListOf<Int>()
        val listOfObjectsNeededToHide = (homePageObjects + animePageObjects + mangaPageObjects + musicPageObjects + downloadPageObjects + settingsPageObjects + searchPageObjects + accountPageObjects) as MutableList<Int>
        when (page) {
            "home" -> { listOfObjectsNeededToShow = homePageObjects }
            "anime" -> { listOfObjectsNeededToShow = animePageObjects }
            "manga" -> { listOfObjectsNeededToShow = mangaPageObjects }
            "music" -> { listOfObjectsNeededToShow = musicPageObjects }
            "download" -> { listOfObjectsNeededToShow = downloadPageObjects }
            "settings" -> { listOfObjectsNeededToShow = settingsPageObjects }
            "search" -> { listOfObjectsNeededToShow = searchPageObjects }
            "account" -> { listOfObjectsNeededToShow = accountPageObjects }
        }
        activity.findViewById<View>(R.id.main).post {
            for (obj in listOfObjectsNeededToHide) {
                activity.findViewById<View>(obj).visibility = View.GONE
            }
            activity.findViewById<View>(R.id.main).requestLayout()
        }
        activity.findViewById<View>(R.id.main).post {
            for (obj in listOfObjectsNeededToShow) {
                activity.findViewById<View>(obj).visibility = View.VISIBLE
            }
            activity.findViewById<View>(R.id.main).requestLayout()
        }
        currentPage = page
    }
    fun whatPage(obj: Int): String {
        var res = ""
        if (obj in homePageObjects) {
            res = "home"
        }
        else if (obj in animePageObjects) {
            res = "anime"
        }
        else if (obj in mangaPageObjects) {
            res = "manga"
        }
        else if (obj in musicPageObjects) {
            res = "music"
        }
        else if (obj in downloadPageObjects) {
            res = "download"
        }
        return res
    }
    fun sortObjListByPage(objList: MutableList<Int>): MutableList<Int> {
        val res = mutableListOf<Int>()
        val currentPage =currentPage
        for (i in 0 until objList.size) {
            val obj = objList[i]
            val objPage = whatPage(obj)
            if (objPage == currentPage || obj == R.id.main) {
                res.add(obj)
            }
        }
        return res
    }
}