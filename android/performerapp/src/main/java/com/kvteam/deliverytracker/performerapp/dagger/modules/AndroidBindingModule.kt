package com.kvteam.deliverytracker.performerapp.dagger.modules

import com.kvteam.deliverytracker.core.dagger.modules.DeliveryTrackerFirebaseInstanceIdServiceModule
import com.kvteam.deliverytracker.core.dagger.modules.DeliveryTrackerFirebaseMessageServiceModule
import com.kvteam.deliverytracker.core.dagger.modules.GeopositionSenderModule
import com.kvteam.deliverytracker.core.dagger.modules.ServiceModules
import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.core.dagger.scopes.ServiceScope
import com.kvteam.deliverytracker.core.geoposition.GeopositionSender
import com.kvteam.deliverytracker.core.notifications.DeliveryTrackerFirebaseMessageService
import com.kvteam.deliverytracker.core.session.DeliveryTrackerInstanceIdService
import com.kvteam.deliverytracker.core.session.SessionService
import com.kvteam.deliverytracker.performerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.performerapp.ui.login.LoginActivity
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [(LoginActivityModule::class)])
    internal abstract fun loginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(ConfirmDataActivityModule::class)])
    internal abstract fun confirmDataActivity(): ConfirmDataActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(MainActivityModule::class)])
    internal abstract fun mainActivity(): MainActivity

    @ServiceScope
    @ContributesAndroidInjector(modules = [(ServiceModules::class)])
    internal abstract fun sessionService(): SessionService

    @ServiceScope
    @ContributesAndroidInjector(modules = [(DeliveryTrackerFirebaseInstanceIdServiceModule::class)])
    internal abstract fun firebaseInstanceIdService(): DeliveryTrackerInstanceIdService

    @ServiceScope
    @ContributesAndroidInjector(modules = [(DeliveryTrackerFirebaseMessageServiceModule::class)])
    internal abstract fun firebaseMessageService(): DeliveryTrackerFirebaseMessageService

    @ContributesAndroidInjector(modules = [(GeopositionSenderModule::class)])
    internal abstract fun geopositionSenderBroadcastReceiver(): GeopositionSender
}