package com.kvteam.deliverytracker.core.async

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.async

fun launchUI(task: suspend CoroutineScope.() -> Unit) {
    async (UI, CoroutineStart.UNDISPATCHED) {
        task()
    }
}

fun <T> launchUI(task: suspend CoroutineScope.() -> Unit,
                 defaultReturnValueFunc: () -> T) : T {
    async (UI, CoroutineStart.UNDISPATCHED) {
        task()
    }
    return defaultReturnValueFunc()
}