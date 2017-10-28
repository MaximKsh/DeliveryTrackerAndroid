package com.kvteam.deliverytracker.performerapp.dagger.modules


import android.app.Activity
import com.kvteam.deliverytracker.core.dagger.scopes.FragmentScope
import com.kvteam.deliverytracker.performerapp.dagger.components.MainActivitySubcomponent
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import com.kvteam.deliverytracker.performerapp.ui.main.performerslist.PerformersListFragment
import com.kvteam.deliverytracker.performerapp.ui.main.task.TaskFragment
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
    @ContributesAndroidInjector(modules = arrayOf(PerformersListFragmentModule::class))
    internal abstract fun performersListFragment(): PerformersListFragment
}