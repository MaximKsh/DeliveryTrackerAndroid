package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.MainActivity
import dagger.android.AndroidInjector
import dagger.Subcomponent


@Subcomponent
interface MainActivitySubcomponent: AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}