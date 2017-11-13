package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.ui.approveuserinfo.ApproveUserInfoActivity
import dagger.android.AndroidInjector
import dagger.Subcomponent


@Subcomponent
interface ApproveUserInfoActivitySubcomponent : AndroidInjector<ApproveUserInfoActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ApproveUserInfoActivity>()
}