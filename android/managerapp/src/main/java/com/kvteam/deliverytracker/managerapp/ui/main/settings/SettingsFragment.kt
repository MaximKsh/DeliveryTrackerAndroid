package com.kvteam.deliverytracker.managerapp.ui.main.settings

import com.kvteam.deliverytracker.core.ui.settings.BaseSettingsFragment
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import javax.inject.Inject

class SettingsFragment : BaseSettingsFragment() {
    @Inject
    lateinit var navigationController: NavigationController

    override fun onEditSettingsClicked() {
        navigationController.navigateToEditSettings()
    }

}