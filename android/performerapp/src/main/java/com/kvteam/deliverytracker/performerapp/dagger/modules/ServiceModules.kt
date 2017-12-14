package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.app.Service
import com.kvteam.deliverytracker.performerapp.dagger.components.PerformerFirebaseInstanceIdServiceSubcomponent
import com.kvteam.deliverytracker.performerapp.dagger.components.PerformerFirebaseMessageServiceSubcomponent
import com.kvteam.deliverytracker.performerapp.notification.PerformerFirebaseInstanceIdService
import com.kvteam.deliverytracker.performerapp.notification.PerformerFirebaseMessageService
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ServiceKey
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(PerformerFirebaseInstanceIdServiceSubcomponent::class))
abstract class PerformerFirebaseInstanceIdServiceModule {
    @Binds
    @IntoMap
    @ServiceKey(PerformerFirebaseInstanceIdService::class)
    internal abstract fun performerFirebaseInstanceIdServiceInjector(
            builder: PerformerFirebaseInstanceIdServiceSubcomponent.Builder):
            AndroidInjector.Factory<out Service>
}

@Module(subcomponents = arrayOf(PerformerFirebaseMessageServiceSubcomponent::class))
abstract class PerformerFirebaseMessageServiceModule {
    @Binds
    @IntoMap
    @ServiceKey(PerformerFirebaseMessageService::class)
    internal abstract fun PerformerFirebaseMessageServiceInjector(
            builder: PerformerFirebaseMessageServiceSubcomponent.Builder):
            AndroidInjector.Factory<out Service>
}
