package com.weather.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FlowWrapper<T : Any>(
    private val flow: Flow<T>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    private var job: Job? = null
    
    fun subscribe(
        scope: CoroutineScope,
        onNext: (T) -> Unit
    ) {
        job?.cancel()
        job = flow
            .onEach { onNext(it) }
            .launchIn(scope)
    }
    
    fun unsubscribe() {
        job?.cancel()
        job = null
    }
}

// Extension function to easily wrap flows for iOS
fun <T : Any> Flow<T>.wrap(): FlowWrapper<T> = FlowWrapper(this)