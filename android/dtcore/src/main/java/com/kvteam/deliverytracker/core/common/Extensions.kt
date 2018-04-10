package com.kvteam.deliverytracker.core.common

import java.util.*

fun <K, V> Map<K, V>.getInt(key: K) : Int {
    return this[key] as Int
}

fun <K, V> Map<K, V>.getString(key: K) : String {
    return this[key] as String
}

fun <K, V> Map<K, V>.getUUID(key: K) : UUID {
    return this[key] as UUID
}

fun <K, V, T> Map<K, V>.getVal(key: K) : T{
    @Suppress("UNCHECKED_CAST")
    return this[key] as T
}

fun Any.notify() {
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    (this as java.lang.Object).notify()
}


@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
fun Any.wait(ms: Long? = null) {
    if (ms != null) {
        (this as java.lang.Object).wait(ms)
    } else {
        (this as java.lang.Object).wait()
    }
}