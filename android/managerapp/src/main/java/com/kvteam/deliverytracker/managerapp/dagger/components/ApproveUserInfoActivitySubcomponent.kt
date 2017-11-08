package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.AddCompanyActivity
import com.kvteam.deliverytracker.managerapp.ApproveUserInfoActivity
import dagger.android.AndroidInjector
import dagger.Subcomponent


@Subcomponent
interface ApproveUserInfoActivitySubcomponent : AndroidInjector<ApproveUserInfoActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ApproveUserInfoActivity>()
}