package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.ui.main.MainActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface MainActivitySubcomponent : AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}