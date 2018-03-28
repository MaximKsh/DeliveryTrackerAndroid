package com.kvteam.deliverytracker.core.dagger.components

import com.kvteam.deliverytracker.core.notifications.DeliveryTrackerFirebaseMessageService
import com.kvteam.deliverytracker.core.session.DeliveryTrackerInstanceIdService
import com.kvteam.deliverytracker.core.session.SessionService
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface ServiceSubcomponents : AndroidInjector<SessionService> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SessionService>()
}


@Subcomponent
interface DeliveryTrackerFirebaseInstanceIdServiceSubcomponent
    : AndroidInjector<DeliveryTrackerInstanceIdService> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DeliveryTrackerInstanceIdService>()
}

@Subcomponent
interface PDeliveryTrackerFirebaseMessageServiceSubcomponent
    : AndroidInjector<DeliveryTrackerFirebaseMessageService> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DeliveryTrackerFirebaseMessageService>()
}