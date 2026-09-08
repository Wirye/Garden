package com.example.garden.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.garden.Layer
import com.example.garden.database.LayoutType

class CreateCarouselViewModel(
    initialLayer: Layer.CreateCarouselPage
) : ViewModel() {
    var onUpdate: (Layer.CreateCarouselPage) -> Unit = {}

    var state by mutableStateOf(initialLayer)
        private set

    var objectsInOneLineError by mutableStateOf(
        ((initialLayer.objectsInOneLine
            ?: Int.MAX_VALUE) > (initialLayer.maxObjectsInOneLineForAdaptiveSize
            ?: Int.MAX_VALUE)) && initialLayer.adaptiveGridSize
    )
        private set


    var maxLinesZeroError by mutableStateOf(
        initialLayer.maxLines == 0
    )
        private set

    var objectsInOneLineZeroError by mutableStateOf(
        initialLayer.objectsInOneLine == 0
    )
        private set

    var maxObjectsInOneLineForAdaptiveGridSizeZeroError by mutableStateOf(
        initialLayer.maxObjectsInOneLineForAdaptiveSize == 0 && initialLayer.adaptiveGridSize
    )
        private set

    var maxLinesForAdaptiveGridSizeZeroError by mutableStateOf(
        initialLayer.maxLinesForAdaptiveSize == 0 && initialLayer.adaptiveGridSize
    )
        private set

    var layoutTypeError by mutableStateOf(
        initialLayer.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID && (
                initialLayer.adaptiveGridSize || initialLayer.objectsInOneLine != 1 || initialLayer.maxObjectsInOneLineForAdaptiveSize != null || initialLayer.maxLinesForAdaptiveSize != null
                )
    )
        private set

    fun update(transform: Layer.CreateCarouselPage.() -> Layer.CreateCarouselPage) {
        state = state.transform()
        onUpdate(state)

        objectsInOneLineError =
            if ((state.objectsInOneLine != null && state.maxObjectsInOneLineForAdaptiveSize == null && state.adaptiveGridSize) || (state.objectsInOneLine == null && state.maxObjectsInOneLineForAdaptiveSize != null && state.adaptiveGridSize)) {
                false
            } else {
                ((state.objectsInOneLine
                    ?: 1) > (state.maxObjectsInOneLineForAdaptiveSize
                    ?: 1)) && state.adaptiveGridSize
            }

        maxLinesZeroError = state.maxLines == 0

        objectsInOneLineZeroError = state.objectsInOneLine == 0

        maxObjectsInOneLineForAdaptiveGridSizeZeroError =
            state.maxObjectsInOneLineForAdaptiveSize == 0 && state.adaptiveGridSize

        maxLinesForAdaptiveGridSizeZeroError =
            state.maxLinesForAdaptiveSize == 0 && state.adaptiveGridSize

        layoutTypeError = state.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID && (
                state.adaptiveGridSize || state.objectsInOneLine != 1 || state.maxObjectsInOneLineForAdaptiveSize != null || state.maxLinesForAdaptiveSize != null
                )
    }

    companion object {
        fun provideFactory(layer: Layer.CreateCarouselPage): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CreateCarouselViewModel(layer) as T
                }
            }
    }
}