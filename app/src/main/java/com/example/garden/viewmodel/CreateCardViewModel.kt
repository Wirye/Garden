package com.example.garden.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.garden.Layer

class CreateCardViewModel(
    initialLayer: Layer.CreateCardPage
) : ViewModel() {
    var onUpdate: (Layer.CreateCardPage) -> Unit = {}

    var state by mutableStateOf(initialLayer)
        private set

    fun update(transform: Layer.CreateCardPage.() -> Layer.CreateCardPage) {
        state = state.transform()
        onUpdate(state)
    }

    companion object {
        fun provideFactory(layer: Layer.CreateCardPage): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CreateCardViewModel(layer) as T
                }
            }
    }
}