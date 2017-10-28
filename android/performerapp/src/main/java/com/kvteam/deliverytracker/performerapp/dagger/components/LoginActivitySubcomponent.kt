package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.ui.login.LoginActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface LoginActivitySubcomponent : AndroidInjector<LoginActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<LoginActivity>()
}