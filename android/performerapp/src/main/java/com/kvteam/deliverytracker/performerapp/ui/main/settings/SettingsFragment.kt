package com.kvteam.deliverytracker.performerapp.ui.main.settings

import com.kvteam.deliverytracker.core.ui.settings.BaseSettingsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import javax.inject.Inject

class SettingsFragment : BaseSettingsFragment() {


    @Inject
    lateinit var navigationController: NavigationController

    override fun onEditSettingsClicked() {
        navigationController.navigateToEditSettings()
    }

    override fun onChangePasswordClicked() {
        navigationController.navigateToChangePassword()
    }

}