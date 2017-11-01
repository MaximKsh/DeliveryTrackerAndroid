package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.app.Activity
import com.kvteam.deliverytracker.managerapp.AddCompanyActivity
import com.kvteam.deliverytracker.managerapp.dagger.components.AddCompanyActivitySubcomponent
import dagger.android.AndroidInjector
import dagger.android.ActivityKey
import dagger.multibindings.IntoMap
import dagger.Binds
import dagger.Module


@Module(subcomponents = arrayOf(AddCompanyActivitySubcomponent::class))
abstract class AddCompanyActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(AddCompanyActivity::class)
    internal abstract fun addCompanyActivityInjector(builder: AddCompanyActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>

}

