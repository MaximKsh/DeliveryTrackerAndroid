package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.ui.addcompany.AddCompanyActivity
import com.kvteam.deliverytracker.managerapp.ui.approveuserinfo.ApproveUserInfoActivity
import com.kvteam.deliverytracker.managerapp.ui.login.LoginActivity
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface AddCompanyActivitySubcomponent : AndroidInjector<AddCompanyActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AddCompanyActivity>()
}

@Subcomponent
interface ApproveUserInfoActivitySubcomponent : AndroidInjector<ApproveUserInfoActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ApproveUserInfoActivity>()
}

@Subcomponent
interface LoginActivitySubcomponent : AndroidInjector<LoginActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<LoginActivity>()
}

@Subcomponent
interface MainActivitySubcomponent: AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}