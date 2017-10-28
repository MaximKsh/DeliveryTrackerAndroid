package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.performerapp.dagger.components.ManagersListFragmentSubcomponent
import com.kvteam.deliverytracker.performerapp.ui.main.userslist.ManagersListFragment
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