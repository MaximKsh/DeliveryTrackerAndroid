package com.kvteam.deliverytracker.core.common

import android.content.res.Resources
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.core.R

class LocalizationManager(
        ext: ILocalizationManagerExtension,
        private val app: DeliveryTrackerApplication)
    : ILocalizationManager {
    private val nameIDMap = mutableMapOf<String, Int>()
    private val stringClasses = mutableSetOf<Class<out Any>>()

    init {
        stringClasses.add(R.string::class.java)
        stringClasses.addAll(ext.types)
    }

    override fun getString(resId: Int): String {
        return try {
            app.getString(resId)
        } catch (e: Resources.NotFoundException) {
            resId.toString()
        }
    }

    override fun getString(resName: String): String {
        if(nameIDMap.containsKey(resName)) {
            return getString(nameIDMap.getValue(resName))
        }
        for(source in stringClasses) {
            val field = source.fields.firstOrNull { it.name == resName }
            if(field != null) {
                val resId = field.getInt(null)
                return getString(resId)
            }
        }
        return resName
    }
}