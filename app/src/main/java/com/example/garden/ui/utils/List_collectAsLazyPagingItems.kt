package com.example.garden.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf

@Composable
fun <T : Any> List<T>.collectAsLazyPagingItems(): LazyPagingItems<T> {
    return remember(this) {
        flowOf(PagingData.from(this))
    }.collectAsLazyPagingItems()
}
