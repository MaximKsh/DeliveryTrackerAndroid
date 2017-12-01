package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.content.BroadcastReceiver
import com.kvteam.deliverytracker.performerapp.dagger.components.GeopositionSenderSubcomponent
import com.kvteam.deliverytracker.performerapp.geoposition.GeopositionSender
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.BroadcastReceiverKey
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(GeopositionSenderSubcomponent::class))
abstract class GeopositionSenderModule {
    @Binds
    @IntoMap
    @BroadcastReceiverKey(GeopositionSender::class)
    internal abstract fun geopositionSenderInjector(builder: GeopositionSenderSubcomponent.Builder):
            AndroidInjector.Factory<out BroadcastReceiver>
}