package com.kvteam.deliverytracker.core.dagger.modules

import android.app.Service
import com.kvteam.deliverytracker.core.dagger.components.ServiceSubcomponents
import com.kvteam.deliverytracker.core.session.SessionService
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ServiceKey
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(ServiceSubcomponents::class))
abstract class ServiceModules {
    @Binds
    @IntoMap
    @ServiceKey(SessionService::class)
    internal abstract fun sessionServiceInjector(builder: ServiceSubcomponents.Builder):
            AndroidInjector.Factory<out Service>


}