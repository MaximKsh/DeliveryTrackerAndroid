package com.kvteam.deliverytracker.core.common

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

class Configuration (
        private val gsonProvider: IDeliveryTrackerGsonProvider,
        private val context: Context) {

    private class MapWrapper : HashMap<String, Any>()

    private val configObj by lazy {
        val gson = gsonProvider.gson
        try {
            val configFile = context.assets.open("configuration.json")
            val reader = BufferedReader(InputStreamReader(configFile))
            return@lazy gson.fromJson<MapWrapper>(reader, MapWrapper::class.java)
        } catch (e: Exception) {
            Log.e("Config", e.message, e)
            return@lazy MapWrapper()
        }
    }

    fun getRaw(key: String) : Any? {
        return configObj[key]
    }

    inline fun <reified T> get(key: String): T {
        return getRaw(key) as T
    }

}