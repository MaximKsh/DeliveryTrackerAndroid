package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.support.v4.app.Fragment
import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.managerapp.LoginActivity
import com.kvteam.deliverytracker.managerapp.SecondFragment
import com.kvteam.deliverytracker.managerapp.dagger.ISimpleType
import com.kvteam.deliverytracker.managerapp.dagger.SimpleType
import com.kvteam.deliverytracker.managerapp.dagger.components.SecondFragmentSubcomponent
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap


@Module(subcomponents = arrayOf(SecondFragmentSubcomponent::class))
abstract class SecondFragmentModule {
    @Binds
    @IntoMap
    @FragmentKey(SecondFragment::class)
    internal abstract fun secondFragmentInjector(builder: SecondFragmentSubcomponent.Builder):
            AndroidInjector.Factory<out Fragment>

    @Module
    class SimpleTypeModule {
        @Provides
        @ActivityScope
        fun simpleType(activity: LoginActivity): ISimpleType {
            return SimpleType(activity)
        }

    }
}