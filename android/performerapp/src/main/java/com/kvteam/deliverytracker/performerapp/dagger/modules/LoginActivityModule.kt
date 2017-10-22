package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.app.Activity
import com.kvteam.deliverytracker.performerapp.LoginActivity
import com.kvteam.deliverytracker.performerapp.dagger.components.LoginActivitySubcomponent
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(LoginActivitySubcomponent::class))
abstract class LoginActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(LoginActivity::class)
    internal abstract fun loginActivityInjector(builder: LoginActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>
}

