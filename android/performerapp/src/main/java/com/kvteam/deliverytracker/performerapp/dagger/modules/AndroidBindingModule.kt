package com.kvteam.deliverytracker.performerapp.dagger.modules

import com.kvteam.deliverytracker.core.dagger.modules.ServiceModules
import com.kvteam.deliverytracker.core.dagger.scopes.ActivityScope
import com.kvteam.deliverytracker.core.dagger.scopes.ServiceScope
import com.kvteam.deliverytracker.core.session.SessionService
import com.kvteam.deliverytracker.performerapp.geoposition.GeopositionSender
import com.kvteam.deliverytracker.performerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.performerapp.ui.login.LoginActivity
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(LoginActivityModule::class))
    internal abstract fun loginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(ConfirmDataActivityModule::class))
    internal abstract fun confirmDataActivity(): ConfirmDataActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(MainActivityModule::class))
    internal abstract fun mainActivity(): MainActivity

    @ServiceScope
    @ContributesAndroidInjector(modules = arrayOf(ServiceModules::class))
    internal abstract fun sessionService(): SessionService

    @ContributesAndroidInjector(modules = arrayOf(GeopositionSenderModule::class))
    internal abstract fun geopositionSenderBroadcastReceiver(): GeopositionSender
}