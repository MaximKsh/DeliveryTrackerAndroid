package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.managerapp.ui.createinstance.CreateInstanceActivity
import com.kvteam.deliverytracker.managerapp.ui.login.LoginActivity
import com.kvteam.deliverytracker.managerapp.ui.main.MainActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface CreateInstanceActivitySubcomponent : AndroidInjector<CreateInstanceActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<CreateInstanceActivity>()
}

@Subcomponent
interface ConfirmDataActivitySubcomponent : AndroidInjector<ConfirmDataActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ConfirmDataActivity>()
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