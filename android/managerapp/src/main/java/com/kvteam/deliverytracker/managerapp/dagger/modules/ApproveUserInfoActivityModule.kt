package com.kvteam.deliverytracker.managerapp.dagger.modules

import android.app.Activity
import com.kvteam.deliverytracker.managerapp.ui.approveuserinfo.ApproveUserInfoActivity
import com.kvteam.deliverytracker.managerapp.dagger.components.ApproveUserInfoActivitySubcomponent
import dagger.android.AndroidInjector
import dagger.android.ActivityKey
import dagger.multibindings.IntoMap
import dagger.Binds
import dagger.Module


@Module(subcomponents = arrayOf(ApproveUserInfoActivitySubcomponent::class))
abstract class ApproveUserInfoActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(ApproveUserInfoActivity::class)
    internal abstract fun approveUserInfoActivityInjector(builder: ApproveUserInfoActivitySubcomponent.Builder):
            AndroidInjector.Factory<out Activity>

}

