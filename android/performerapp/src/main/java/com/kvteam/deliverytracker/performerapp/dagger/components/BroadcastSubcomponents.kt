package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.geoposition.GeopositionSender
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface GeopositionSenderSubcomponent : AndroidInjector<GeopositionSender> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<GeopositionSender>()
}
