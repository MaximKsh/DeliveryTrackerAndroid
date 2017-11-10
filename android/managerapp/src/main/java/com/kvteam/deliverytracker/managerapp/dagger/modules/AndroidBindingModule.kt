package com.kvteam.deliverytracker.managerapp.dagger.modules

import com.kvteam.deliverytracker.core.dagger.modules.ServiceModules
import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.core.dagger.scopes.ServiceScope
import com.kvteam.deliverytracker.core.session.SessionService
import com.kvteam.deliverytracker.managerapp.LoginActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
internal abstract class AndroidBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(LoginActivityModule::class))
    internal abstract fun loginActivity(): LoginActivity

    @ServiceScope
    @ContributesAndroidInjector(modules = arrayOf(ServiceModules::class))
    internal abstract fun sessionService(): SessionService
}