package com.kvteam.deliverytracker.core.async

import android.util.Log
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.async

fun launchUI(task: suspend CoroutineScope.() -> Unit) {
    async (UI, CoroutineStart.UNDISPATCHED) {
        try {
            task()
        } catch (e: Exception) {
            Log.e("launchUI", e.message, e)
            throw e
        }
    }
}

fun <T> launchUI(task: suspend CoroutineScope.() -> Unit,
                 defaultReturnValueFunc: () -> T) : T {
    async (UI, CoroutineStart.UNDISPATCHED) {
        try {
            task()
        } catch (e: Exception) {
            Log.e("launchUI", e.message, e)
            throw e
        }
    }
    return defaultReturnValueFunc()
}