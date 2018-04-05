package com.kvteam.deliverytracker.managerapp.ui.main.settings

import com.kvteam.deliverytracker.core.ui.settings.BaseChangePasswordFragment
import com.kvteam.deliverytracker.managerapp.ui.main.NavigationController
import javax.inject.Inject

class ChangePasswordFragment : BaseChangePasswordFragment() {
    @Inject
    lateinit var navigationController: NavigationController


    override fun onSuccessChangePassword() {
        navigationController.closeCurrentFragment()
    }

}