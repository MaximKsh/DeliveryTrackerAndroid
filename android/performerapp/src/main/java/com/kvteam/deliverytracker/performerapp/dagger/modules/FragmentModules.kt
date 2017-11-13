package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.performerapp.dagger.components.*
import com.kvteam.deliverytracker.performerapp.ui.main.task.TaskFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.MyTasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.UndistributedTasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.ManagersListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.PerformersListFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(ManagersListFragmentSubcomponent::class))
abstract class ManagersListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(ManagersListFragment::class)
    internal abstract fun managersListFragmentInjector(builder: ManagersListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(MyTasksListFragmentSubcomponent::class))
abstract class MyTasksListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(MyTasksListFragment::class)
    internal abstract fun myTasksListFragmentInjector(builder: MyTasksListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(TaskFragmentSubcomponent::class))
abstract class TaskFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TaskFragment::class)
    internal abstract fun taskFragmentInjector(builder: TaskFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(UndistributedTasksListFragmentSubcomponent::class))
abstract class UndistributedTasksListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(UndistributedTasksListFragment::class)
    internal abstract fun undistributedTasksListFragmentInjector(builder: UndistributedTasksListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}

@Module(subcomponents = arrayOf(PerformersListFragmentSubcomponent::class))
abstract class PerformersListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(PerformersListFragment::class)
    internal abstract fun performersListFragmentInjector(builder: PerformersListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}