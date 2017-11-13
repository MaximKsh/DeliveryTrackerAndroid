package com.kvteam.deliverytracker.managerapp

import android.content.res.Configuration
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.managerapp.dagger.components.DaggerAppComponent
import com.kvteam.deliverytracker.managerapp.ui.login.LoginActivity
import java.lang.reflect.Type

class ManagerApplication: DeliveryTrackerApplication() {
    override fun applicationInjector() = DaggerAppComponent.builder().create(this)!!
    override val loginActivityType: Type
        get() = LoginActivity::class.java

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate() {
        super.onCreate()
    }
}