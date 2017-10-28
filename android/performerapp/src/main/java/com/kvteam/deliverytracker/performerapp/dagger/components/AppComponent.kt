package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.PerformerApplication
import com.kvteam.deliverytracker.performerapp.dagger.modules.*
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AndroidSupportInjectionModule::class,
        AndroidBindingModule::class,
        SingletonModule::class,
        ViewModelModule::class))
internal interface AppComponent : AndroidInjector<PerformerApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<PerformerApplication>()

}
