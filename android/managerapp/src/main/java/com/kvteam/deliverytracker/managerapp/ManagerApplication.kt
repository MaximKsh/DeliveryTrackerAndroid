package com.kvteam.deliverytracker.managerapp

import android.content.res.Configuration
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.managerapp.dagger.components.DaggerAppComponent

class ManagerApplication: DeliveryTrackerApplication() {
    override fun applicationInjector() = DaggerAppComponent.builder().create(this)!!

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate() {
        super.onCreate()
    }
}