package com.kvteam.deliverytracker.performerapp

import android.content.res.Configuration
import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.performerapp.dagger.components.DaggerAppComponent
import com.kvteam.deliverytracker.performerapp.ui.login.LoginActivity
import java.lang.reflect.Type


class PerformerApplication: DeliveryTrackerApplication() {
    override fun applicationInjector() = DaggerAppComponent.builder().create(this)!!
    override val loginActivityType: Type
        get() = LoginActivity::class.java

}