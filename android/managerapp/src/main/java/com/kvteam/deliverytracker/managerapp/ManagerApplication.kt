package com.kvteam.deliverytracker.managerapp

import com.kvteam.deliverytracker.core.DeliveryTrackerApplication
import com.kvteam.deliverytracker.managerapp.dagger.components.DaggerAppComponent
import com.kvteam.deliverytracker.managerapp.ui.login.LoginActivity
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import java.lang.reflect.Type

class ManagerApplication: DeliveryTrackerApplication() {
    override fun applicationInjector() = DaggerAppComponent.builder().create(this)!!
    override val loginActivityType: Type
        get() = LoginActivity::class.java


    override val mainActivityType: Type
        get() = MainActivity::class.java

}