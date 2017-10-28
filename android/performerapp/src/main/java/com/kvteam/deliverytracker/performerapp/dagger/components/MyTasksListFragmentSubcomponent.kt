package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.MyTasksListFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface MyTasksListFragmentSubcomponent : AndroidInjector<MyTasksListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MyTasksListFragment>()
}