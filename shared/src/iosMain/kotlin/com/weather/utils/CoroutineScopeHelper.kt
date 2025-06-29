package com.weather.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class CoroutineScopeHelper {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    fun dispose() {
        scope.cancel()
    }
}