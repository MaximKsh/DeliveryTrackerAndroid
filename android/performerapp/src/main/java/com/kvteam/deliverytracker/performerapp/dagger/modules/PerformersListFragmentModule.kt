package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.performerapp.dagger.components.PerformersListFragmentSubcomponent
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.PerformersListFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap


@Module(subcomponents = arrayOf(PerformersListFragmentSubcomponent::class))
abstract class PerformersListFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(PerformersListFragment::class)
    internal abstract fun performersListFragmentInjector(builder: PerformersListFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>
}