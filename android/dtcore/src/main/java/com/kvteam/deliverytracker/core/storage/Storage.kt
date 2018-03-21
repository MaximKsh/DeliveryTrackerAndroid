package com.kvteam.deliverytracker.core.storage

import android.content.Context
import com.kvteam.deliverytracker.core.common.EMPTY_STRING


class Storage(private val context: Context): IStorage {
    private val spName = "DeliveryTrackerGeneralSharedPreferences"

    override fun getString(key: String) : String {
        val sharedPref = context.getSharedPreferences(spName, Context.MODE_PRIVATE)
        return sharedPref.getString(key, EMPTY_STRING)
    }

    override fun set(key: String, value: Any) {
        if (value !is String) {
            throw IllegalArgumentException("Only strings allowed")
        }

        val sharedPref = context.getSharedPreferences(spName, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }


}