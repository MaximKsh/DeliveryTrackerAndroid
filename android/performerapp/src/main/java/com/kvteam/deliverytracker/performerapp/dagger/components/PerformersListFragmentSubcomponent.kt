package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.ui.main.userslist.PerformersListFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface PerformersListFragmentSubcomponent : AndroidInjector<PerformersListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PerformersListFragment>()
}