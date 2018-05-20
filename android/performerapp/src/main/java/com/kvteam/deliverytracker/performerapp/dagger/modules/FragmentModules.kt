package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.performerapp.dagger.components.*
import com.kvteam.deliverytracker.performerapp.ui.main.dayroute.DayRouteFragment
import com.kvteam.deliverytracker.performerapp.ui.main.settings.ChangePasswordFragment
import com.kvteam.deliverytracker.performerapp.ui.main.settings.EditSettingsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.settings.SettingsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.TaskDetailsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.TasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.UsersListFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap


@Module(subcomponents = arrayOf(UsersListFragmentSubcomponent::class))
abstract class UsersListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(UsersListFragment::class)
    internal abstract fun usersListFragmentInjector(builder: UsersListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

}

@Module(subcomponents = arrayOf(TasksListFragmentSubcomponent::class))
abstract class TasksListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TasksListFragment::class)
    internal abstract fun tasksListFragmentInjector(builder: TasksListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(DayRouteFragmentSubcomponent::class))
abstract class DayRouteFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(DayRouteFragment::class)
    internal abstract fun dayRouteFragmentInjector(builder: DayRouteFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}


@Module(subcomponents = arrayOf(SettingsFragmentSubcomponent::class))
abstract class SettingsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(SettingsFragment::class)
    internal abstract fun settingsFragmentInjector(builder: SettingsFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}


@Module(subcomponents = arrayOf(EditSettingsFragmentSubcomponent::class))
abstract class EditSettingsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(EditSettingsFragment::class)
    internal abstract fun editSettingsFragmentInjector(builder: EditSettingsFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(ChangePasswordFragmentSubcomponent::class))
abstract class ChangePasswordSettingsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(ChangePasswordFragment::class)
    internal abstract fun injector(builder: ChangePasswordFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(TaskDetailsFragmentSubcomponent::class))
abstract class TaskDetailsFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TaskDetailsFragment::class)
    internal abstract fun editSettingsFragmentInjector(builder: TaskDetailsFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}