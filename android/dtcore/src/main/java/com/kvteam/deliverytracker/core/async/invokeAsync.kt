package com.kvteam.deliverytracker.core.async

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch


fun <T> async(
        task: suspend CoroutineScope.() -> T,
        continueWith: (suspend CoroutineScope.(T) -> Unit)? = null) {
    if(continueWith != null) {
        val channel = Channel<T>()
        launch(CommonPool) {
            val res = task()
            channel.send(res)
        }.attachChild(launch(UI) {
            val res = channel.receive()
            continueWith(res)
        })
    } else {
        launch {
            task()
        }
    }
}