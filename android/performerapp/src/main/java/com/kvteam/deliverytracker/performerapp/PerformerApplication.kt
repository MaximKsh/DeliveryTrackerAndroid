package com.kvteam.deliverytracker.performerapp

import android.app.Application
import android.content.res.Configuration
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication

/**
 * Created by maxim on 12.10.17.
 */

class PerformerApplication: Application() {

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate() {
        super.onCreate()
    }
}