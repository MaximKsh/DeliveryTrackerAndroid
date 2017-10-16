package com.kvteam.deliverytracker.core

import android.content.res.Configuration
import dagger.android.DaggerApplication


abstract class DeliveryTrackerApplication : DaggerApplication() {

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
