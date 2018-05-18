package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.ui.main.dayroute.DayRouteFragment
import com.kvteam.deliverytracker.performerapp.ui.main.settings.ChangePasswordFragment
import com.kvteam.deliverytracker.performerapp.ui.main.settings.EditSettingsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.settings.SettingsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.TaskDetailsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.TasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.UsersListFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector


@Subcomponent
interface UsersListFragmentSubcomponent : AndroidInjector<UsersListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<UsersListFragment>()
}

@Subcomponent
interface TasksListFragmentSubcomponent : AndroidInjector<TasksListFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TasksListFragment>()
}

@Subcomponent
interface DayRouteFragmentSubcomponent : AndroidInjector<DayRouteFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DayRouteFragment>()
}

@Subcomponent
interface SettingsFragmentSubcomponent : AndroidInjector<SettingsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SettingsFragment>()
}

@Subcomponent
interface EditSettingsFragmentSubcomponent : AndroidInjector<EditSettingsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<EditSettingsFragment>()
}


@Subcomponent
interface ChangePasswordFragmentSubcomponent : AndroidInjector<ChangePasswordFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ChangePasswordFragment>()
}

@Subcomponent
interface TaskDetailsFragmentSubcomponent : AndroidInjector<TaskDetailsFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskDetailsFragment>()
}