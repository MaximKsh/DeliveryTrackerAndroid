package com.kvteam.deliverytracker.core.dagger.modules

import android.app.Service
import com.kvteam.deliverytracker.core.dagger.components.DeliveryTrackerFirebaseInstanceIdServiceSubcomponent
import com.kvteam.deliverytracker.core.dagger.components.PDeliveryTrackerFirebaseMessageServiceSubcomponent
import com.kvteam.deliverytracker.core.dagger.components.ServiceSubcomponents
import com.kvteam.deliverytracker.core.notifications.DeliveryTrackerFirebaseMessageService
import com.kvteam.deliverytracker.core.session.DeliveryTrackerInstanceIdService
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

@Module(subcomponents = arrayOf(DeliveryTrackerFirebaseInstanceIdServiceSubcomponent::class))
abstract class DeliveryTrackerFirebaseInstanceIdServiceModule {
    @Binds
    @IntoMap
    @ServiceKey(DeliveryTrackerInstanceIdService::class)
    internal abstract fun dtFirebaseInstanceIdServiceInjector(
            builder: DeliveryTrackerFirebaseInstanceIdServiceSubcomponent.Builder):
            AndroidInjector.Factory<out Service>
}

@Module(subcomponents = arrayOf(PDeliveryTrackerFirebaseMessageServiceSubcomponent::class))
abstract class DeliveryTrackerFirebaseMessageServiceModule {
    @Binds
    @IntoMap
    @ServiceKey(DeliveryTrackerFirebaseMessageService::class)
    internal abstract fun dtFirebaseMessageServiceInjector(
            builder: PDeliveryTrackerFirebaseMessageServiceSubcomponent.Builder):
            AndroidInjector.Factory<out Service>
}
