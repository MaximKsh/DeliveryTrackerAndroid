package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.app.Activity
import com.kvteam.deliverytracker.core.dagger.scopes.FragmentScope
import com.kvteam.deliverytracker.managerapp.LoginActivity
import com.kvteam.deliverytracker.managerapp.dagger.components.LoginActivitySubcomponent
import dagger.android.AndroidInjector
import dagger.android.ActivityKey
import dagger.multibindings.IntoMap
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module(subcomponents = arrayOf(LoginActivitySubcomponent::class))
abstract class LoginActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(LoginActivity::class)
    internal abstract fun loginActivityInjector(builder: LoginActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>

}

