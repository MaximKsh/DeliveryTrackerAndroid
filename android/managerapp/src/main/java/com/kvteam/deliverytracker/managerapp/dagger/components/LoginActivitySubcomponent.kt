package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.LoginActivity
import com.kvteam.deliverytracker.managerapp.dagger.SimpleActivityModule1
import dagger.android.AndroidInjector
import dagger.Subcomponent




@Subcomponent(modules = arrayOf(SimpleActivityModule1::class))
interface LoginActivitySubcomponent : AndroidInjector<LoginActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<LoginActivity>()
}