package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.SecondFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector


@Subcomponent
interface SecondFragmentSubcomponent : AndroidInjector<SecondFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SecondFragment>()
}