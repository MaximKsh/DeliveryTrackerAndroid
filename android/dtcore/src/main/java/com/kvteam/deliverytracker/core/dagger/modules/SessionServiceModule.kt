package com.kvteam.deliverytracker.core.dagger.modules

import android.app.Service
import com.kvteam.deliverytracker.core.dagger.components.SessionServiceSubcomponent
import com.kvteam.deliverytracker.core.session.Session
import com.kvteam.deliverytracker.core.session.SessionService
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ServiceKey
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(SessionServiceSubcomponent::class))
abstract class SessionServiceModule {
    @Binds
    @IntoMap
    @ServiceKey(SessionService::class)
    internal abstract fun sessionServiceInjector(builder: SessionServiceSubcomponent.Builder):
            AndroidInjector.Factory<out Service>


}