package com.kvteam.deliverytracker.managerapp.dagger.modules

import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.managerapp.LoginActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
internal abstract class AndroidBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(LoginActivityModule::class))
    internal abstract fun loginActivity(): LoginActivity
}