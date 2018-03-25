package com.kvteam.deliverytracker.performerapp.ui.main.settings

import com.kvteam.deliverytracker.core.ui.settings.BaseEditSettingsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import javax.inject.Inject

class EditSettingsFragment : BaseEditSettingsFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override fun afterSuccessfulEdit() {
        navigationController.closeCurrentFragment()
    }

}