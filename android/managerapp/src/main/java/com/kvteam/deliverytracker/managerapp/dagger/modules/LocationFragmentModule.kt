package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.managerapp.L
import com.kvteam.deliverytracker.managerapp.dagger.components.FirstFragmentSubcomponent
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(FirstFragmentSubcomponent::class))
abstract class FirstFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(FirstFragment::class)
    internal abstract fun firstFragmentInjector(builder: FirstFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

}