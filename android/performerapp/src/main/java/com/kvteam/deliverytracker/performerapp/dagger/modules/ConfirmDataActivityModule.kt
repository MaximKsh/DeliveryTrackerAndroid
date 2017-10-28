package com.kvteam.deliverytracker.performerapp.dagger.modules

import android.app.Activity
import com.kvteam.deliverytracker.performerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.performerapp.dagger.components.ConfirmDataActivitySubcomponent
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap


@Module(subcomponents = arrayOf(ConfirmDataActivitySubcomponent::class))
abstract class ConfirmDataActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(ConfirmDataActivity::class)
    internal abstract fun confirmDataActivity(builder: ConfirmDataActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>
}
