package com.example.garden.viewmodel

import androidx.lifecycle.ViewModel
import com.example.garden.ui.screens.PageWithSearchItem
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ResultSenderViewModel : ViewModel() {
    private val _results = MutableSharedFlow<Pair<String, Any>>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val results = _results.asSharedFlow()

    fun sendResult(requestKey: String, data: Any) {
        _results.tryEmit(requestKey to data)
    }
}


data class PageWithSearchSaveOutput(
    val items: List<PageWithSearchItem>
)