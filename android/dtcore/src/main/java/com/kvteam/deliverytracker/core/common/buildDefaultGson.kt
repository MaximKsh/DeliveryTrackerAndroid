package com.kvteam.deliverytracker.core.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.joda.time.DateTime

fun buildDefaultGson() : Gson {
    return GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, DateTimeTypeAdapter())
            .create()
}