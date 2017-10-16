package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.managerapp.FirstFragment
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.android.AndroidInjector


@Subcomponent
interface FirstFragmentSubcomponent : AndroidInjector<FirstFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<FirstFragment>()
}