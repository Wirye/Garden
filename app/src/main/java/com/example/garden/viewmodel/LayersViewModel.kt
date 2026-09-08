package com.example.garden.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.garden.Layer
import com.example.garden.database.PageType

class LayersViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_BACK_STACK = "key_layers_back_stack"
        private const val KEY_MAIN_PAGES_STACK = "key_main_pages_stack"
    }

    val mainPagesStack: SnapshotStateList<PageType> = mutableStateListOf<PageType>().apply {
        val restoredStack = savedStateHandle.get<ArrayList<PageType>>(KEY_MAIN_PAGES_STACK)
        if (!restoredStack.isNullOrEmpty()) {
            addAll(restoredStack)
        } else {
            add(PageType.Home)
        }
    }

    val backStack: SnapshotStateList<Layer> = mutableStateListOf<Layer>().apply {
        val restoredStack = savedStateHandle.get<ArrayList<Layer>>(KEY_BACK_STACK)
        if (!restoredStack.isNullOrEmpty()) {
            addAll(restoredStack)
        } else {
            add(Layer.MainPage(PageType.Home, mutableMapOf(), 0))
        }
    }

    private fun persistStack() {
        savedStateHandle[KEY_BACK_STACK] = ArrayList(backStack)
    }
    private fun persistMainPagesStack() {
        savedStateHandle[KEY_MAIN_PAGES_STACK] = ArrayList(mainPagesStack)
    }


    fun openLayer(layer: Layer) {
        if (layer is Layer.MainPage) {
            val pageId = layer.pageId
            val isLayerAlreadyExist = backStack.any { it is Layer.MainPage && it.pageId == pageId }
            if (isLayerAlreadyExist) {
                val index = backStack.indexOfFirst { it is Layer.MainPage && it.pageId == pageId }
                val layer = backStack[index]
                backStack.add(layer)
                backStack.removeAt(index)
            } else {
                backStack.add(layer)
            }
            mainPagesStack.add(pageId)
            persistMainPagesStack()
        }
        else {
            backStack.add(layer)
        }
        persistStack()
    }

    fun removeLayerById(layerId: Long) {
        val index = backStack.indexOfFirst { it.id == layerId }
        if (index != -1) {
            backStack.removeAt(index)
            persistStack()
        }
    }

    fun popLayer(): Boolean {
        if ((backStack.firstOrNull { it !is Layer.MainPage } != null) || (mainPagesStack.size > 1)) {
            val isMainPage = backStack.last() is Layer.MainPage
            if (isMainPage) {
                mainPagesStack.removeAt(mainPagesStack.lastIndex)
                val lastMainPage = mainPagesStack.last()
                val isLastMainPageAlreadyExist = backStack.any { it is Layer.MainPage && it.pageId == lastMainPage }
                if (isLastMainPageAlreadyExist) {
                    val lastMainPageLayer = backStack.first { it is Layer.MainPage && it.pageId == lastMainPage }
                    val mainPageLayer = backStack.last()
                    val index = backStack.indexOf(lastMainPageLayer)
                    backStack[index] = mainPageLayer
                    backStack[backStack.lastIndex] = lastMainPageLayer
                } else {
                    val lastMainPageLayer = Layer.MainPage(lastMainPage, mutableMapOf(), 0)
                    backStack.add(lastMainPageLayer)
                }
                persistMainPagesStack()
            } else {
                backStack.removeAt(backStack.lastIndex)
            }
            persistStack()
            return true
        }
        return false
    }

    fun updateLayer(updatedLayer: Layer) {
        val index = backStack.indexOfFirst { it.id == updatedLayer.id }
        if (index != -1) {
            backStack[index] = updatedLayer
        }
        persistStack()
    }

    fun updateLayerFirstElementPosition(layerId: Long, position: Int) {
        val index = backStack.indexOfFirst { it.id == layerId }
        if (index != -1) {
            val bs = backStack[index]
            bs.firstElementPosition = position
        }
        persistStack()
    }
}