package com.kvteam.deliverytracker.performerapp

import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.performerapp.dagger.components.DaggerAppComponent
import com.kvteam.deliverytracker.performerapp.ui.login.LoginActivity
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import java.lang.reflect.Type


class PerformerApplication: DeliveryTrackerApplication() {
    override fun applicationInjector() = DaggerAppComponent.builder().create(this)!!
    override val loginActivityType: Type
        get() = LoginActivity::class.java

    override val mainActivityType: Type
        get() = MainActivity::class.java
}