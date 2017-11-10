package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.ui.confirm.ConfirmDataActivity
import com.kvteam.deliverytracker.performerapp.ui.login.LoginActivity
import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface LoginActivitySubcomponent : AndroidInjector<LoginActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<LoginActivity>()
}

@Subcomponent
interface ConfirmDataActivitySubcomponent : AndroidInjector<ConfirmDataActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ConfirmDataActivity>()
}

@Subcomponent
interface MainActivitySubcomponent : AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}