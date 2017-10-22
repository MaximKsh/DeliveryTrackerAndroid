package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.PerformerApplication
import com.kvteam.deliverytracker.performerapp.dagger.modules.AndroidBindingModule
import com.kvteam.deliverytracker.performerapp.dagger.modules.CoreModule
import com.kvteam.deliverytracker.performerapp.dagger.modules.SingletonCoreModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AndroidSupportInjectionModule::class,
        AndroidBindingModule::class,
        SingletonCoreModule::class,
        CoreModule::class))
internal interface AppComponent : AndroidInjector<PerformerApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<PerformerApplication>()

}
