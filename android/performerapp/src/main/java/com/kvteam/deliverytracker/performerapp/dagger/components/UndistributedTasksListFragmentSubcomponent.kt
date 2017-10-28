package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.UndistributedTasksListFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface UndistributedTasksListFragmentSubcomponent : AndroidInjector<UndistributedTasksListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<UndistributedTasksListFragment>()
}