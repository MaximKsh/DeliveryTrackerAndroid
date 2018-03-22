package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.MyTasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.UndistributedTasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.ManagersListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.PerformersListFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector


@Subcomponent
interface ManagersListFragmentSubcomponent : AndroidInjector<ManagersListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ManagersListFragment>()
}

@Subcomponent
interface PerformersListFragmentSubcomponent : AndroidInjector<PerformersListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PerformersListFragment>()
}

@Subcomponent
interface UndistributedTasksListFragmentSubcomponent : AndroidInjector<UndistributedTasksListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<UndistributedTasksListFragment>()
}

@Subcomponent
interface MyTasksListFragmentSubcomponent : AndroidInjector<MyTasksListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MyTasksListFragment>()
}