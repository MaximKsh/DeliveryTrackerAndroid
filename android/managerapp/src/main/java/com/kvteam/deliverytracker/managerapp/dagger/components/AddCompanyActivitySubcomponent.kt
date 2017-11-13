package com.kvteam.deliverytracker.managerapp.dagger.components

import com.kvteam.deliverytracker.managerapp.ui.login.addcompany.AddCompanyActivity
import dagger.android.AndroidInjector
import dagger.Subcomponent


@Subcomponent
interface AddCompanyActivitySubcomponent : AndroidInjector<AddCompanyActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AddCompanyActivity>()
}