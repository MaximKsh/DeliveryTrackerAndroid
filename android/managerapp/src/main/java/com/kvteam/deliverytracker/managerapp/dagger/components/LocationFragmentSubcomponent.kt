package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.ui.addcompany.LocationFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector


@Subcomponent
interface LocationFragmentSubcomponent : AndroidInjector<LocationFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<LocationFragment>()
}