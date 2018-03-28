package com.kvteam.deliverytracker.core.dagger.components

import com.kvteam.deliverytracker.core.geoposition.GeopositionSender
import dagger.Subcomponent
import dagger.android.AndroidInjector


@Subcomponent
interface GeopositionSenderSubcomponent : AndroidInjector<GeopositionSender> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<GeopositionSender>()
}
