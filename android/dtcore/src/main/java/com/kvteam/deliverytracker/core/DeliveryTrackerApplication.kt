package com.kvteam.deliverytracker.core

import android.content.res.Configuration
import dagger.android.DaggerApplication
import android.content.Intent
import com.kvteam.deliverytracker.core.session.SessionService
import java.lang.reflect.Type


abstract class DeliveryTrackerApplication : DaggerApplication() {
    abstract val loginActivityType: Type

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate() {
        super.onCreate()

        val intent = Intent(this, SessionService::class.java)
        startService(intent)
    }

}
