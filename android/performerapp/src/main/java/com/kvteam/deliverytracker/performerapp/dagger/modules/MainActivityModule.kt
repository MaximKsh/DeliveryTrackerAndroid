package com.kvteam.deliverytracker.performerapp.dagger.modules


import android.app.Activity
import com.kvteam.deliverytracker.core.dagger.scopes.FragmentScope
import com.kvteam.deliverytracker.performerapp.dagger.components.MainActivitySubcomponent
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.PerformersListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.task.TaskFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.MyTasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.UndistributedTasksListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.ManagersListFragment
import dagger.android.AndroidInjector
import dagger.android.ActivityKey
import dagger.multibindings.IntoMap
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module(subcomponents = arrayOf(MainActivitySubcomponent::class),
        includes = arrayOf(MainActivityNavigationControllerModule::class))
abstract class MainActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity::class)
    internal abstract fun mainActivityInjector(builder: MainActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(TaskFragmentModule::class))
    internal abstract fun taskFragment(): TaskFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(MyTasksListFragmentModule::class))
    internal abstract fun myTasksListFragment(): MyTasksListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(UndistributedTasksListFragmentModule::class))
    internal abstract fun undistributedTasksListFragment(): UndistributedTasksListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(PerformersListFragmentModule::class))
    internal abstract fun performersListFragment(): PerformersListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(ManagersListFragmentModule::class))
    internal abstract fun managersListFragment(): ManagersListFragment
}