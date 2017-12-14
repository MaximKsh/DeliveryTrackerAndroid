package com.kvteam.deliverytracker.core.async

import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch


fun <T> invokeAsync(
        task: suspend CoroutineScope.() -> T,
        continueWith: (suspend CoroutineScope.(T) -> Unit)? = null) {
    if(continueWith != null) {
        val channel = Channel<T>()
        launch(CommonPool) {
            try {
                val res = task()
                channel.send(res)
            } catch (e: Exception) {
                Log.e("InvokeAsync", e.message, e)
            }

        }.attachChild(launch(UI) {
            try {
                val res = channel.receive()
                continueWith(res)
            } catch (e: Exception) {
                Log.e("InvokeAsync", e.message, e)
            }
        })
    } else {
        launch {
            try {
                task()
            } catch (e: Exception) {
                Log.e("InvokeAsync", e.message, e)
            }
        }
    }
}