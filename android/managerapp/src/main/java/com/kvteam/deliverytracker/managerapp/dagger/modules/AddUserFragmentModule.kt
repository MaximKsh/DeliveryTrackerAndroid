package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.managerapp.dagger.components.AddUserFragmentSubcomponent
import com.kvteam.deliverytracker.managerapp.ui.main.adduser.AddUserFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(AddUserFragmentSubcomponent::class))
abstract class AddUserFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(AddUserFragment::class)
    internal abstract fun addUserFragmentInjector(builder: AddUserFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

}