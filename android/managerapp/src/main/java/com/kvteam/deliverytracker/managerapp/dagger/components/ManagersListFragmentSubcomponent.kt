package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.ui.main.userslist.ManagersListFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector


@Subcomponent
interface ManagersListFragmentSubcomponent : AndroidInjector<ManagersListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ManagersListFragment>()
}