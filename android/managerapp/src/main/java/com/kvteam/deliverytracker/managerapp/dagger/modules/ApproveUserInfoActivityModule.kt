package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.app.Activity
import com.kvteam.deliverytracker.core.dagger.scopes.FragmentScope
import com.kvteam.deliverytracker.managerapp.AddCompanyActivity
import com.kvteam.deliverytracker.managerapp.ApproveUserInfoActivity
import com.kvteam.deliverytracker.managerapp.LocationFragment
import com.kvteam.deliverytracker.managerapp.dagger.components.AddCompanyActivitySubcomponent
import com.kvteam.deliverytracker.managerapp.dagger.components.ApproveUserInfoActivitySubcomponent
import dagger.android.AndroidInjector
import dagger.android.ActivityKey
import dagger.multibindings.IntoMap
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module(subcomponents = arrayOf(ApproveUserInfoActivitySubcomponent::class))
abstract class ApproveUserInfoActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(ApproveUserInfoActivity::class)
    internal abstract fun approveUserInfoActivityInjector(builder: ApproveUserInfoActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>

}

