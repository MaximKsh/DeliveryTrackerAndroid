package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.managerapp.ui.addcompany.LocationFragment
import com.kvteam.deliverytracker.managerapp.dagger.components.LocationFragmentSubcomponent
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(LocationFragmentSubcomponent::class))
abstract class LocationFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(LocationFragment::class)
    internal abstract fun locationFragmentInjector(builder: LocationFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

}