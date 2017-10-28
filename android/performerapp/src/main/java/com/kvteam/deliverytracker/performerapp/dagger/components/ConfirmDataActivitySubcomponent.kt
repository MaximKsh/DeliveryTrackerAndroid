package com.kvteam.deliverytracker.performerapp.dagger.components

import com.kvteam.deliverytracker.performerapp.ui.confirm.ConfirmDataActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface ConfirmDataActivitySubcomponent : AndroidInjector<ConfirmDataActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ConfirmDataActivity>()
}