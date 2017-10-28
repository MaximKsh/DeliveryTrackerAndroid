package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.performerapp.dagger.components.MyTasksListFragmentSubcomponent
import com.kvteam.deliverytracker.performerapp.dagger.components.UndistributedTasksListFragmentSubcomponent
import com.kvteam.deliverytracker.performerapp.ui.main.taskslist.MyTasksListFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(UndistributedTasksListFragmentSubcomponent::class))
abstract class UndistributedTasksListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(MyTasksListFragment::class)
    internal abstract fun undistributedTasksListFragmentInjector(builder: MyTasksListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}