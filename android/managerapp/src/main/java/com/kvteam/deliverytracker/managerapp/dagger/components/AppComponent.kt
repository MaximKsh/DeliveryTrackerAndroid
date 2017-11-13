package com.kvteam.deliverytracker.managerapp.dagger.components


import com.kvteam.deliverytracker.core.instance.IInstanceManager
import com.kvteam.deliverytracker.core.webservice.IWebservice
import com.kvteam.deliverytracker.managerapp.ManagerApplication
import com.kvteam.deliverytracker.managerapp.dagger.modules.AndroidBindingModule
import com.kvteam.deliverytracker.managerapp.dagger.modules.CoreModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(
        AndroidSupportInjectionModule::class,
        AndroidBindingModule::class,
        CoreModule::class))
internal interface AppComponent : AndroidInjector<ManagerApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<ManagerApplication>()

    fun webservice(): IWebservice
    fun instanceManager(): IInstanceManager
}
