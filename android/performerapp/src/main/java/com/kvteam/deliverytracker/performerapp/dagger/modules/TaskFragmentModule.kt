package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.performerapp.dagger.components.TaskFragmentSubcomponent
import com.kvteam.deliverytracker.performerapp.ui.main.task.TaskFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(TaskFragmentSubcomponent::class))
abstract class TaskFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(TaskFragment::class)
    internal abstract fun taskFragmentInjector(builder: TaskFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}