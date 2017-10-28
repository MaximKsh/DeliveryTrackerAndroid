package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.ui.main.userslist.ManagersListFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface ManagersListFragmentSubcomponent : AndroidInjector<ManagersListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ManagersListFragment>()
}