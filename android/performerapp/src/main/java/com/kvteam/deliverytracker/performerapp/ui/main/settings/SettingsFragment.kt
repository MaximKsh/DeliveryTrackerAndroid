package com.kvteam.deliverytracker.performerapp.ui.main.settings

import com.kvteam.deliverytracker.core.ui.settings.BaseSettingsFragment
import com.kvteam.deliverytracker.performerapp.ui.main.NavigationController
import javax.inject.Inject

class SettingsFragment : BaseSettingsFragment() {
    override fun onChangePasswordClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Inject
    lateinit var navigationController: NavigationController

    override fun onEditSettingsClicked() {
        navigationController.navigateToEditSettings()
    }

}