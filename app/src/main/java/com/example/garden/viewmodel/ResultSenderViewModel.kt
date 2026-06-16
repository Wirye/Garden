package com.example.garden.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ResultSenderViewModel : ViewModel() {
    // Канал для передачи результатов.
    // String — это ключ (кто ждет), Any — это данные (что передаем)
    // В ResultSenderViewModel
    private val _results = MutableSharedFlow<Pair<String, Any>>(
        replay = 0, // Хранить последнее событие для новых подписчиков
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val results = _results.asSharedFlow()

    fun sendResult(requestKey: String, data: Any) {
        _results.tryEmit(requestKey to data)
    }
}