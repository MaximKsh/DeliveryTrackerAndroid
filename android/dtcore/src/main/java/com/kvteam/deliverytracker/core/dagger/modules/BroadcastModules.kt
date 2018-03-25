package com.kvteam.deliverytracker.core.dagger.modules

import android.content.BroadcastReceiver
import com.kvteam.deliverytracker.core.dagger.components.GeopositionSenderSubcomponent
import com.kvteam.deliverytracker.core.geoposition.GeopositionSender
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.BroadcastReceiverKey
import dagger.multibindings.IntoMap


@Module(subcomponents = [(GeopositionSenderSubcomponent::class)])
abstract class GeopositionSenderModule {
    @Binds
    @IntoMap
    @BroadcastReceiverKey(GeopositionSender::class)
    internal abstract fun geopositionSenderInjector(builder: GeopositionSenderSubcomponent.Builder):
            AndroidInjector.Factory<out BroadcastReceiver>
}